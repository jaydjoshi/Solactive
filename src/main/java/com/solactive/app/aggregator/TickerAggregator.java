package com.solactive.app.aggregator;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.solactive.app.constant.IndexConstant;
import com.solactive.app.model.Statistics;
import com.solactive.app.model.Tick;

public class TickerAggregator{

	private PriorityBlockingQueue<Tick> tickPriorityBlockingQueue = new PriorityBlockingQueue<>();
	private volatile Statistics statistics;
	private final Lock lock;
	
	public TickerAggregator(){
		
		statistics = new Statistics();
		lock = new ReentrantLock();
	}
	
	public PriorityBlockingQueue<Tick> getTickPriorityBlockingQueue() {
		return tickPriorityBlockingQueue;
	}
	
	public Statistics getStatistics() {
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
	public boolean add(Tick e, final long currentTime) {
		
		boolean val = false;
		try {
			lock.lock();
			tickPriorityBlockingQueue.removeIf(t -> (currentTime - t.getTimestamp()) > IndexConstant.DEFAULT_SLIDING_WINDOW_MS);
			val = tickPriorityBlockingQueue.add(e);
			this.reCalculate();
			AllTickersAggregator.reCalculateRoot();
		}finally {
			lock.unlock();
		}
		
		return val;
		
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
