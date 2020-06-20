package com.solactive.app.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.solactive.app.aggregator.AllTickersAggregator;
import com.solactive.app.aggregator.TickerAggregator;
import com.solactive.app.model.Statistics;
import com.solactive.app.model.Tick;
import com.solactive.app.service.IndexService;

@Service
public class IndexServiceImpl implements IndexService {
	
	
	Map<String,TickerAggregator> map = AllTickersAggregator.getTickerToAggregateMap();
	
	Logger logger = LoggerFactory.getLogger(IndexServiceImpl.class);

	@Override
	public boolean insertTicks(Tick tick) {
		
		try {
			
			logger.debug("inside insertTicks");
			final long currentTime = System.currentTimeMillis();
			map.computeIfAbsent(tick.getInstrument(), k -> new TickerAggregator()).add(tick, currentTime);
		}finally {
			
		}
		// debug
		logger.debug("Map: \n"+map);
		
		return false;
	}

	@Override
	public Statistics getStatistics(long currentTimeStamp) {
		
		if(map.isEmpty()) {
			// TODO: Create a global exception handler
			throw new RuntimeException();
		}else {
			return AllTickersAggregator.getRootStatistics();
		}
		
	}

	@Override
	public Statistics getStatistics(long currentTimeStamp, String instrument) {
		
		if(map.containsKey(instrument)) {
			return map.get(instrument).getStatistics();
		}
		else { // TODO: Create a global exception handler
			throw new RuntimeException();
		}
		
	}

}
