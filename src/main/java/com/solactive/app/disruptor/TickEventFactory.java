package com.solactive.app.disruptor;

import com.lmax.disruptor.EventFactory;
import com.solactive.app.model.Tick;

/**
 * 
 * @author jay
 *
 */
public class TickEventFactory implements EventFactory<Tick>{
	 public Tick newInstance()
	    {
	        return new Tick();
	    }
}

