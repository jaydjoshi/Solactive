package com.solactive.app.aggregator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.solactive.app.model.Statistics;

public class AllTickersAggregator {
	
	private static Map<String,TickerAggregator> tickerToAggregateMap = new ConcurrentHashMap<>();
	
	private static Statistics rootStatistics = new Statistics();

	public static Map<String, TickerAggregator> getTickerToAggregateMap() {
		return tickerToAggregateMap;
	}

	public static Statistics getRootStatistics() {
		return rootStatistics;
	}
	
	
	public static void reCalculateRoot() {
		
		long count = 0l;
		double sum=0d;
		double min = Double.MAX_VALUE;
		double max = 0d;
		
		for(TickerAggregator ticker: tickerToAggregateMap.values()) {
			Statistics tickerStats = ticker.getStatistics();
			if(tickerStats == null) {
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
		
		
		rootStatistics.setAvg(sum/count);
		rootStatistics.setCount(count);
		rootStatistics.setMax(max);
		rootStatistics.setMin(min);
		
	}
	

}
