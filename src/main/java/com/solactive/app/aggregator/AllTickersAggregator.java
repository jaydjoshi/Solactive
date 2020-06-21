package com.solactive.app.aggregator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.solactive.app.model.Statistics;

/**
 * 
 * @author jay
 *
 */
public class AllTickersAggregator {
	
	// setting initial capacity as 50000 and load factor as one, so that rehashing does not happen
	private static Map<String,TickerAggregator> tickerToAggregateMap = new ConcurrentHashMap<>(50000,1);
	
	private static volatile Statistics rootStatistics;

	public static Map<String, TickerAggregator> getTickerToAggregateMap() {
		return tickerToAggregateMap;
	}

	public static Statistics getRootStatistics(long currentTime) {
		reCalculateRoot(currentTime);
		return rootStatistics;
	}
	
	/**
	 *  recalculates stats at all instruments level
	 *  
	 * @param currentTime
	 */
	public static void reCalculateRoot(long currentTime) {
		
		long count = 0l;
		double sum=0d;
		double min = Double.MAX_VALUE;
		double max = 0d;
		
		for(TickerAggregator ticker: tickerToAggregateMap.values()) {
			Statistics tickerStats = ticker.getStatistics(currentTime);
			if(tickerStats == null || isEmpty(tickerStats)) {
				continue;
			}
			
			long tickerCount = tickerStats.getCount();
			double tickerMinimum = tickerStats.getMin();
			double tickerMaximum = tickerStats.getMax();
			
			// (avg * tickerCount) to get actual avg on aggregation level
			sum = sum+ (tickerStats.getAvg()* tickerCount);
			min = Math.min(min, tickerMinimum);
			max = Math.max(max, tickerMaximum);
			count = count+tickerCount;
		}
		
		
		// if no data available
		if(sum == 0d && min == Double.MAX_VALUE && max==0d && count==0l) {
			rootStatistics = new Statistics(0d, 0d, 0d, 0l);
			return;
		}
		
		rootStatistics = new Statistics(sum/count, max, min, count);
		
	}

	/**
	 * 
	 * @param tickerStats
	 * @return
	 */
	private static boolean isEmpty(Statistics tickerStats) {
		
		return (tickerStats.getAvg() == 0d && tickerStats.getMax()==0d && tickerStats.getMin()==0d && tickerStats.getCount()==0l);
	}

	

}