package com.solactive.app.service.impl;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import com.solactive.app.aggregator.AllTickersAggregator;
import com.solactive.app.aggregator.AllTickersAggregatorNonBlocking;
import com.solactive.app.aggregator.TickerAggregator;
import com.solactive.app.aggregator.TickerAggregatorNonBlocking;
import com.solactive.app.disruptor.TickEvenProducer;
import com.solactive.app.disruptor.TickEventFactory;
import com.solactive.app.disruptor.TickEventHandler;
import com.solactive.app.exception.InvalidTickException;
import com.solactive.app.exception.NoTickerAvailableException;
import com.solactive.app.exception.TickerNotAvailableException;
import com.solactive.app.model.ImmutableTick;
import com.solactive.app.model.Statistics;
import com.solactive.app.model.Tick;
import com.solactive.app.service.IndexService;

@Service
public class IndexNonBlockingServiceImpl implements IndexService {

	// map of instrument to TickerAggregatorNonBlocking
	private Map<String, TickerAggregatorNonBlocking> instrumentToTickerAggregatorMap = AllTickersAggregatorNonBlocking.getTickerToAggregateMap();

	private static final Logger logger = LoggerFactory.getLogger(IndexServiceImpl.class);
	
	// The factory for the event
    private TickEventFactory factory = new TickEventFactory();

    // Specify the size of the ring buffer, must be power of 2. setting to 2^24 -> 16777216 | 2^22 -> 4194304
    private static final int bufferSize = 16777216;
    
    private Disruptor<Tick> disruptor = new Disruptor<>(factory, bufferSize, DaemonThreadFactory.INSTANCE);
    
    private RingBuffer<Tick> ringBuffer;
    
    private TickEvenProducer producer;
	
	
    @PostConstruct
	public void setDisruptor() {
	 
      // Connect the handler
      disruptor.handleEventsWith(new TickEventHandler(instrumentToTickerAggregatorMap));

      // Start the Disruptor, starts all threads running
      disruptor.start();
      
      // Get the ring buffer from the Disruptor to be used for publishing.
      ringBuffer = disruptor.getRingBuffer();

      producer = new TickEvenProducer(ringBuffer);
	}


	/**
	 * Note: updating stats is O(n) right now as we iterate over 60 s data.
	 * @param tick
	 * @return boolean
	 */
	@Override
	public void insertTicks(Tick tick) {

		logger.debug("inside insertTicks");
		try {
			
			// Add data in queue and then add data to the map
			producer.onData(tick);
	        
		} catch(Exception e) {
			
			logger.error("Unable to insert ticker {} data", tick.getInstrument());
			throw new InvalidTickException();
		}
		logger.debug("Map: {}\n", instrumentToTickerAggregatorMap);
		logger.debug("Instrument {} inserted ", tick.getInstrument());

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
		if (instrumentToTickerAggregatorMap.isEmpty()) {
			logger.error("No ticker data found in last 60s");
			throw new NoTickerAvailableException();
		} else {
			return AllTickersAggregatorNonBlocking.getRootStatistics(currentTime);
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
		if (instrumentToTickerAggregatorMap.containsKey(instrument)) {
			return instrumentToTickerAggregatorMap.get(instrument).getStatistics(currentTime);
		} else {
			logger.error("ticker {} data is not found in last 60s", instrument);
			throw new TickerNotAvailableException();
		}

	}

}
