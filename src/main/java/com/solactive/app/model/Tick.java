/**
 * 
 */
package com.solactive.app.model;

import java.util.Comparator;

/**
 * @author jay
 * POJO class of Tick
 */
public class Tick{
	
	private String instrument;
	private double price;
	private long timestamp;
	
	public String getInstrument() {
		return instrument;
	}
	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	@Override
	public String toString() {
		return "Tick [instrument=" + instrument + ", price=" + price + ", timestamp=" + timestamp + "]";
	}

	public static final Comparator<Tick> timestampComparator = (x,y) -> Long.compare(x.getTimestamp(), y.getTimestamp());
	
}
