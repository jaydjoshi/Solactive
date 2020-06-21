package com.solactive.app.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.solactive.app.aggregator.AllTickersAggregator;
import com.solactive.app.aggregator.TickerAggregator;
import com.solactive.app.exception.InvalidTickException;
import com.solactive.app.exception.NoTickerAvailableException;
import com.solactive.app.exception.TickerNotAvailableException;
import com.solactive.app.model.ImmutableTick;
import com.solactive.app.model.Statistics;
import com.solactive.app.model.Tick;
import com.solactive.app.service.IndexService;

@Service
public class IndexServiceImpl implements IndexService {

	private Map<String, TickerAggregator> map = AllTickersAggregator.getTickerToAggregateMap();

	private static final Logger logger = LoggerFactory.getLogger(IndexServiceImpl.class);


	/**
	 * TODO: updating stats is O(n) right now as we iterate over 60 s data.
	 * @param tick
	 * @return boolean
	 */
	@Override
	public void insertTicks(Tick tick) {

		try {
			logger.debug("inside insertTicks");
			ImmutableTick immTick = convertToImmutableTick(tick);
			final long currentTime = System.currentTimeMillis();
			map.computeIfAbsent(immTick.getInstrument(), k -> new TickerAggregator()).addAndUpdateStatistics(immTick, currentTime);
		} catch(Exception e) {
			logger.error("Unable to insert ticker {} data", tick);
			throw new InvalidTickException();
		}
		// debug
		logger.debug("Map: \n" + map);

	}

	/**
	 * 
	 * @param tick
	 * @return immutable tick
	 */
	private ImmutableTick convertToImmutableTick(Tick tick) {
		return new ImmutableTick(tick.getInstrument(), tick.getPrice(), tick.getTimestamp());
	}

	/**
	 * two scenarios for time complexity,
	 * 1. best case O(1) : When all the ticker stats are within last 60 s i.e. no recalculation needed
	 * 2. worst case O(n*m) : When no ticks for any ticker is recieved in last 60 s window 
	 * where,
	 *  n = number of tickers
	 *  m = avg number of ticks in queue for all tickers
	 * worst case might degrade to O(n^2) for high number of tickers and high number of avg ticks
	 * 
	 * TODO : improve worst case
	 * 
	 * Space complexity : O(1)
	 * 
	 */
	@Override
	public Statistics getStatistics() {

		final long currentTime = System.currentTimeMillis();
		if (map.isEmpty()) {
			logger.error("No ticker data found in last 60s");
			throw new NoTickerAvailableException();
		} else {
			return AllTickersAggregator.getRootStatistics(currentTime);
		}

	}

	/**
	 * two scenarios for time complexity,
	 * 1. best case O(1) : When stats for the instrument are within last 60 s
	 * 2. worst case O(n) : When stats are outside last 60 s window i.e. 
	 * we need to remove ticks from queue and recalculate stats. We might have to remove all the elements in worst case.
	 * 
	 * Space complexity : O(1)
	 * 
	 */
	@Override
	public Statistics getStatistics( String instrument) {

		final long currentTime = System.currentTimeMillis();
		if (map.containsKey(instrument)) {
			return map.get(instrument).getStatistics(currentTime);
		} else {
			logger.error("ticker {} data is not found in last 60s", instrument);
			throw new TickerNotAvailableException();
		}

	}

}
