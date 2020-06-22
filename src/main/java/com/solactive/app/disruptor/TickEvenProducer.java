package com.solactive.app.disruptor;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.InsufficientCapacityException;
import com.lmax.disruptor.RingBuffer;
import com.solactive.app.exception.TickNotInsertedException;
import com.solactive.app.model.Tick;

public class TickEvenProducer {
	private final RingBuffer<Tick> ringBuffer;
	private static final Logger logger = LoggerFactory.getLogger(TickEvenProducer.class);

	public TickEvenProducer(RingBuffer<Tick> ringBuffer)
	{
		this.ringBuffer = ringBuffer;
	}

	public void onData(Tick tick)
	{
		
		AtomicInteger count = new AtomicInteger();
		
		try
		{
			long sequence = ringBuffer.tryNext();  // Grab the next sequence

			Tick event = ringBuffer.get(sequence); // Get the entry in the Disruptor for the sequence
			// Fill with data
			event.setInstrument(tick.getInstrument()); 
			event.setPrice(tick.getPrice());
			event.setTimestamp(tick.getTimestamp()); 
			
			ringBuffer.publish(sequence);
			return;

		} catch (InsufficientCapacityException e1) {
			
			// retry 5 times
			if(count.get() == 5) {
				// catch the error and log in loggers
				logger.error("Error in outer try TickEvenProducer for tick {}", tick);
				throw new TickNotInsertedException();
			
			}
			
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// e.printStackTrace();
			}
			
			count.getAndIncrement();
			onData(tick);
			
			
		}finally {
				
		}
	}
}
