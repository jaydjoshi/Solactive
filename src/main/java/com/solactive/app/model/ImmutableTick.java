/**
 * 
 */
package com.solactive.app.model;

import java.util.Comparator;

/**
 * @author jay
 *
 */
public final class ImmutableTick {
	
	private final String instrument;
	private final double price;
	private final long timestamp;
	
	
	public ImmutableTick(String instrument, double price, long timestamp) {
		super();
		this.instrument = instrument;
		this.price = price;
		this.timestamp = timestamp;
	}

	public String getInstrument() {
		return instrument;
	}

	public double getPrice() {
		return price;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	@Override
	public String toString() {
		return "Tick [instrument=" + instrument + ", price=" + price + ", timestamp=" + timestamp + "]";
	}
	
	public static Comparator<ImmutableTick> timestampComparator = (x,y) -> Long.compare(x.getTimestamp(), y.getTimestamp());

}
