package com.solactive.app.disruptor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;
import com.solactive.app.aggregator.TickerAggregatorNonBlocking;
import com.solactive.app.model.Tick;

/**
 * 
 * @author jay
 *
 */
public class TickEventHandler implements EventHandler<Tick>{
	
	private final Map<String, TickerAggregatorNonBlocking> instrumentToTickerAggregatorMap;
	private static final Logger logger = LoggerFactory.getLogger(TickEventHandler.class);

	public TickEventHandler(Map<String, TickerAggregatorNonBlocking> instrumentToTickerAggregatorMap2) {
		this.instrumentToTickerAggregatorMap = instrumentToTickerAggregatorMap2;
	}

	/**
	 * 
	 */
	public void onEvent(Tick immTick, long sequence, boolean endOfBatch)
    {
		try {
			long currentTime = System.currentTimeMillis();
			instrumentToTickerAggregatorMap.computeIfAbsent(immTick.getInstrument(), k -> new TickerAggregatorNonBlocking()).addAndUpdateStatistics(immTick, currentTime);
		}
		catch(Exception e) {
			logger.error("Error in TickEventHandler for tick {}", immTick);
		}
    }
}