package com.solactive.app.aggregator;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.solactive.app.constant.IndexConstant;
import com.solactive.app.model.ImmutableTick;
import com.solactive.app.model.Statistics;

/**
 * using locks only for recalculate stats
 * @author jay
 *
 */
public class TickerAggregatorNonBlocking {

	// setting initial capacity to 60, with an assumption that we will receive 1 tick per sec. Even if we receive more than 1 tick, priority queu will resize. This is just initial value
	private PriorityBlockingQueue<ImmutableTick> tickPriorityBlockingQueue = new PriorityBlockingQueue<>(60, ImmutableTick.timestampComparator);

	// create immutable class
	private  AtomicReference<Statistics>  statistics = new AtomicReference<>();
	private final Lock lock = new ReentrantLock();
	private volatile double avgNum ;
	private volatile double maxNum ;
	private volatile double minNum ;
	
	
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
	 * Note - no need to recalculate root stats as reCalculateRoot() will call getStatistics of each ticker
	 */
	public Statistics getStatistics(final long currentTime) {
		
		// getMinimumTimestamp = 0 means queue is empty
		if(getMinimumTimestamp() > 0l && (currentTime - getMinimumTimestamp() > IndexConstant.DEFAULT_SLIDING_WINDOW_MS)){
			try {
				
				lock.lock();
				removeOldTicksFromHead(currentTime);
				this.reCalculate();
				
			}
			finally {
				lock.unlock();
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
			lock.lock();
			removeOldTicksFromHead(currentTime);
			addTick(e);
			this.reCalculate();
		}finally {
			lock.unlock();
		}
		
		return tickPriorityBlockingQueue.size();
		
	}
	
	/**
	 * ex: 1,2,3,4,5,6,7,8,9,10 
	 * total = 55
	 * avg = 5.5
	 * 
	 * add 11 from queue
	 * 1,2,3,4,5,6,7,8,9,10,11
	 * 
	 * avgNum = (5.5*10) +11 / 11 = 66/11 = 6
	 * 
	 * Also, set max and min by comparing
	 * 
	 * @param e
	 */
	private void addTick(ImmutableTick t) {
		tickPriorityBlockingQueue.add(t);
		if(statistics.get() != null) {
			avgNum = (((statistics.get().getAvg() * statistics.get().getCount()) + t.getPrice()) / tickPriorityBlockingQueue.size());
			maxNum = (Math.max(statistics.get().getMax(), t.getPrice()));
			minNum = (Math.min(statistics.get().getMin(), t.getPrice()));
		}else {
			// first tick
			avgNum = (t.getPrice());
			maxNum = (t.getPrice());
			minNum = (t.getPrice());
			
		}
		
	}
	
	
	/**
	 * remove from head, better performance
	 * 
	 * While setting avg,
	 * ex: 1,2,3,4,5,6,7,8,9,10 
	 * total = 55
	 * avg = 5.5
	 * 
	 * remove 1 from queue
	 * 2,3,4,5,6,7,8,9,10
	 * 
	 * avgNum = (5.5*10) -1 / 9 = 54/9 = 6
	 * 
	 * 
	 * While setting max,
	 * scenario 1: stats.max is greater than removed tick. set threadLocal max to stats.max
	 * ex: 1,2,3,4,5,6,7,8,9,10 
	 * remove :1
	 * stats.max = 10
	 * 10 >1 -> 10
	 * time complexity : O(1)
	 * 
	 * scenario 2: stats.max is equal to the removed tick price. we need to recalculate max
	 * ex: 11,2,3,4,5,6,7,8,9,10
	 * remove :11
	 * stats.max =11
	 * 11 = 11 -> set Double.MAX_VALUE will be recalculated later
	 * time complexity : O(n)
	 * worst case O(n) : set Double.MAX_VALUE when t.getPrice() was the max element, we need to recalculate max now
	 * set maxNum to Double.MAX_VALUE as we do not want to recalculate here within while loop, we will do it in reCalculate() method
	 * 
	 * While setting min,
	 * scenario 1: stats.min is less than removed tick. set threadLocal max to stats.min
	 * ex: 5,2,3,4,5,6,7,8,9,10 
	 * remove :5
	 * stats.min = 2
	 * 2 < 5 -> 2
	 * time complexity : O(1)
	 * 
	 * scenario 2: stats.min is equal to the removed tick price. we need to recalculate min
	 * ex: 1,2,3,4,5,6,7,8,9,10
	 * remove :1
	 * stats.min =1
	 * 1 = 1 -> set Double.MIN_VALUE will be recalculated later
	 * time complexity : O(n)
	 * worst case O(n) : set Double.MIN_VALUE when t.getPrice() was the min element, we need to recalculate min now
	 * set minNum to Double.MIN_VALUE as we do not want to recalculate here within while loop, we will do it in reCalculate() method
	 * 
	 * @param currentTime
	 */
	private void removeOldTicksFromHead(final long currentTime) {
		
		while( !tickPriorityBlockingQueue.isEmpty() && ((currentTime - tickPriorityBlockingQueue.peek().getTimestamp()) > IndexConstant.DEFAULT_SLIDING_WINDOW_MS)) {
			//if we are removing old ticks means stats is already prepared
			ImmutableTick t = tickPriorityBlockingQueue.poll();
			avgNum = (((statistics.get().getAvg() * statistics.get().getCount()) - t.getPrice()) / tickPriorityBlockingQueue.size());
			maxNum = (statistics.get().getMax() > t.getPrice() ? statistics.get().getMax() : Double.MAX_VALUE);
			minNum = (statistics.get().getMin() < t.getPrice() ? statistics.get().getMin() : Double.MIN_VALUE);
		}
		
	}
	
	/**
	 * recalculate statistics of the ticker after each inserts
	 * setting count and avg will be O(1)
	 * 
	 * min and max : 
	 * best case O(1)
	 * worst case O(n)
	 * 
	 */
	private void reCalculate() {

		long count = tickPriorityBlockingQueue.size();

		double min = minNum;
		double max = maxNum;

		if(!tickPriorityBlockingQueue.isEmpty() && (max == (Double.MAX_VALUE) || min == (Double.MIN_VALUE))) {
			// so new values can be calculated
			min=Double.MAX_VALUE;
			max=0d;

			for(ImmutableTick tick : tickPriorityBlockingQueue) {
				double price = tick.getPrice();
				min = Math.min(min, price);
				max = Math.max(max, price);

			}
		}

		// if no data available
		if(tickPriorityBlockingQueue.isEmpty()) {
			statistics.set(new Statistics(0d, 0d, 0d, 0l));
			return;
		}

		statistics.set(new Statistics(avgNum, max, min, count));

	}

	
	
				
}

	


