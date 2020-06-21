package com.solactive.app.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.solactive.app.aggregator.AllTickersAggregator;
import com.solactive.app.aggregator.TickerAggregator;
import com.solactive.app.exception.NoTickerAvailableException;
import com.solactive.app.exception.TickerNotAvailableException;
import com.solactive.app.model.Statistics;
import com.solactive.app.model.Tick;
import com.solactive.app.service.IndexService;

@Service
public class IndexServiceImpl implements IndexService {

	private Map<String, TickerAggregator> map = AllTickersAggregator.getTickerToAggregateMap();

	private static final Logger logger = LoggerFactory.getLogger(IndexServiceImpl.class);

	// TODO: updating stats is O(n) right now as we iterate over 60 s data.
	@Override
	public boolean insertTicks(Tick tick) {

		try {
			logger.debug("inside insertTicks");
			final long currentTime = System.currentTimeMillis();
			map.computeIfAbsent(tick.getInstrument(), k -> new TickerAggregator()).add(tick, currentTime);
		} finally {

		}
		// debug
		logger.debug("Map: \n" + map);

		return false;
	}

	// TODO: Actual sliding window is not implement, as we are storing stats only during inserts
	// there can be a gap between insert requests and statistics call
	@Override
	public Statistics getStatistics(long currentTimeStamp) {

		if (map.isEmpty()) {
			logger.error("No ticker data found in last 60s");
			throw new NoTickerAvailableException();
		} else {
			return AllTickersAggregator.getRootStatistics();
		}

	}

	@Override
	public Statistics getStatistics(long currentTimeStamp, String instrument) {

		if (map.containsKey(instrument)) {
			return map.get(instrument).getStatistics();
		} else {
			logger.error("ticker {} data is not found in last 60s", instrument);
			throw new TickerNotAvailableException();
		}

	}

}
