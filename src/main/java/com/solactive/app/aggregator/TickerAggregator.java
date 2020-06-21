package com.solactive.app.aggregator;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.solactive.app.constant.IndexConstant;
import com.solactive.app.model.Statistics;
import com.solactive.app.model.Tick;

public class TickerAggregator{

	private volatile PriorityBlockingQueue<Tick> tickPriorityBlockingQueue = new PriorityBlockingQueue<>(100, Tick.timestampComparator);
	// create immutable class
	private volatile Statistics statistics;
	private final Lock lock;
	
	public TickerAggregator(){
		
		statistics = new Statistics();
		lock = new ReentrantLock();
	}
	
	public PriorityBlockingQueue<Tick> getTickPriorityBlockingQueue() {
		return tickPriorityBlockingQueue;
	}
	
	
	/**
	 * 
	 * @return min timestamp in the queue
	 */
	public long getMinimumTimestamp() {
		return tickPriorityBlockingQueue.peek().getTimestamp();
	}

	/**
	 * two scenarios,
	 * 1. When stats for the instrument are within last 60 s, return statistics
	 * 2. When stats are outside last 60 s window (this will occur if ticks service was called before sometime 
	 * i.e stats are not updated. later, statistics is called so we need to update the stats before sending)
	 * 		2.1. remove old ticks
	 * 		2.2. recalculate and send statistics
	 */
	public Statistics getStatistics(final long currentTime) {
		
		if(currentTime - getMinimumTimestamp() > IndexConstant.DEFAULT_SLIDING_WINDOW_MS){
			try {
				//lock.lock();
				removeOldTicksFromHead(currentTime);
				this.reCalculate();
				// no need to recalulate root stats as reCalculateRoot() will call getStatistics of each ticker
				//AllTickersAggregator.reCalculateRoot(currentTime);
			}
			finally {
				//lock.unlock();
			}
		}
		return statistics;
	}
	

	@Override
	public String toString() {
		return "TickerQueue [tickPriorityBlockingQueue=" + tickPriorityBlockingQueue + ", statistics=" + statistics
				+ ", lock=" + lock + "]";
	}

	/**
	 * add method will do the following,
	 * 1. remove all the ticks older than 60 s
	 * 2. add tick in priority queue
	 * 3. recalculate statistics for the instrument
	 * 
	 * Using lock to avoid thread interference only for same ticker
	 * 
	 * @param e
	 * @param currentTime
	 * @return
	 */
	public boolean addAndUpdateStatistics(Tick e, final long currentTime) {
		
		boolean val = false;
		try {
			lock.lock();
			//System.out.println("Before removing old tick\n"+tickPriorityBlockingQueue.size());
			removeOldTicksFromHead(currentTime);
			//System.out.println("After removing old tick\n"+tickPriorityBlockingQueue.size());
			val = tickPriorityBlockingQueue.add(e);
			//System.out.println("After adding new tick\n"+tickPriorityBlockingQueue.size());
			//System.out.println("After adding new tick\n"+tickPriorityBlockingQueue.peek());
			
			this.reCalculate();
			//AllTickersAggregator.reCalculateRoot(currentTime);
		}finally {
			lock.unlock();
		}
		
		return val;
		
	}
	
	/**
	 * using comapareAndSet to set value of min timestamp
	 * @param tick
	 */
//	private void setMinTimestamp(Tick tick) {
//		
//		while(true) {
//			long existingVal = minimumTimestamp.get();
//			long newVal = tick.getTimestamp();
//			if(minimumTimestamp.compareAndSet(existingVal, newVal)) {
//				return;
//			}
//		}
//		
//	}

	/**
	 * remove using remove if
	 * @param currentTime
	 */
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
	 * TODO : this is bruteforce, see if calculations can be done while removings
	 */
	private void reCalculate() {
		
		long count = tickPriorityBlockingQueue.size();
		double sum=0d;
		double min = Double.MAX_VALUE;
		double max = 0d;
		
		for(Tick tick : tickPriorityBlockingQueue) {
			double price = tick.getPrice();
			sum = sum+price;
			min = Math.min(min, price);
			max = Math.max(max, price);
			
		}
		
		this.statistics.setAvg(sum/count);
		this.statistics.setCount(count);
		this.statistics.setMax(max);
		this.statistics.setMin(min);
		
	}

	

}
