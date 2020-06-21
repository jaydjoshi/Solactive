package com.solactive.app.aggregator;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.solactive.app.constant.IndexConstant;
import com.solactive.app.model.ImmutableTick;
import com.solactive.app.model.Statistics;

/**
 * trying non blocking algo using cas
 * @author jay
 *
 */
public class TickerAggregatorNonBlocking implements ITickerAggregator{

	// setting initial capacity to 100
	private volatile PriorityBlockingQueue<ImmutableTick> tickPriorityBlockingQueue = new PriorityBlockingQueue<>(100, ImmutableTick.timestampComparator);

	// create immutable class
	private  AtomicReference<Statistics>  statistics = new AtomicReference<Statistics>();
	//private volatile Statistics statistics;
	
	
	public TickerAggregatorNonBlocking(){
		
		
	}
	
	
	@Override
	public String toString() {
		return "TickerAggregatorNonBlocking [tickPriorityBlockingQueue=" + tickPriorityBlockingQueue + ", statistics="
				+ statistics + "]";
	}



	/**
	 * 
	 * @return min timestamp in the queue
	 */
	public long getMinimumTimestamp() {
		if(!tickPriorityBlockingQueue.isEmpty()) {
			return tickPriorityBlockingQueue.peek().getTimestamp();
		}
		return 0l;
	}

	/**
	 * two scenarios,
	 * 1. When stats for the instrument are within last 60 s, return statistics
	 * 2. When stats are outside last 60 s window (this will occur if ticks service was called before sometime 
	 * i.e stats are not updated. later, statistics is called so we need to update the stats before sending)
	 * 		2.1. remove old ticks
	 * 		2.2. recalculate and send statistics
	 * 
	 * Note - no need to recalulate root stats as reCalculateRoot() will call getStatistics of each ticker
	 */
	public Statistics getStatistics(final long currentTime) {
		
		// getMinimumTimestamp = 0 means queue is empty
		if(getMinimumTimestamp() > 0l && (currentTime - getMinimumTimestamp() > IndexConstant.DEFAULT_SLIDING_WINDOW_MS)){
			try {
				//lock.lock();
				removeOldTicksFromHead(currentTime);
				this.reCalculate();
				
			}
			finally {
				//lock.unlock();
			}
		}
		return statistics.get();
	}
	

	/**
	 * add method will do the following,
	 * 1. remove all the ticks older than 60 s
	 * 2. add tick in priority queue
	 * 3. recalculate statistics for the instrument
	 * 
	 * Note - no need to recalulate root stats as reCalculateRoot() will call getStatistics of each ticker
	 * 
	 * Using lock to avoid thread interference only for same ticker
	 * 
	 * @param e
	 * @param currentTime
	 * @return
	 */
	public int addAndUpdateStatistics(ImmutableTick e, final long currentTime) {
		
		try {
			//lock.lock();
			removeOldTicksFromHead(currentTime);
			tickPriorityBlockingQueue.add(e);
			this.reCalculate();
		}finally {
			//lock.unlock();
		}
		
		return tickPriorityBlockingQueue.size();
		
	}
	

	/**
	 * remove using remove if
	 * @param currentTime
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private void removeOldTicks(final long currentTime) {
		tickPriorityBlockingQueue.removeIf(t -> (currentTime - t.getTimestamp()) > IndexConstant.DEFAULT_SLIDING_WINDOW_MS);
	}
	
	/**
	 * remove from head, better performance
	 * @param currentTime
	 */
	private void removeOldTicksFromHead(final long currentTime) {
		
		while( !tickPriorityBlockingQueue.isEmpty() && ((currentTime - tickPriorityBlockingQueue.peek().getTimestamp()) > IndexConstant.DEFAULT_SLIDING_WINDOW_MS)) {
			tickPriorityBlockingQueue.poll();
		}
		
	}
	
	/**
	 * recalculate statistics of the ticker after each inserts
	 * TODO : this is bruteforce O(n), see if calculations can be done while removing
	 */
	private void reCalculate() {
			
		long count = tickPriorityBlockingQueue.size();
		double sum=0d;
		double min = Double.MAX_VALUE;
		double max = 0d;
		
		if(!tickPriorityBlockingQueue.isEmpty()) {
			for(ImmutableTick tick : tickPriorityBlockingQueue) {
				double price = tick.getPrice();
				sum = sum+price;
				min = Math.min(min, price);
				max = Math.max(max, price);
				
			}
		}
		
		// use CAS
		while(true) {
			
			Statistics existingValue = statistics.get();
			Statistics newValue;
			
			// if no data available
			if(sum == 0d && min == Double.MAX_VALUE && max==0d && count==0l) {
				newValue = new Statistics(0d, 0d, 0d, 0l);
			
			}else {
				newValue = new Statistics(sum/count, max, min, count);
			}
			

			if(statistics.compareAndSet(existingValue, newValue)) {
				return;
			}
		}
				
	}

	

}
