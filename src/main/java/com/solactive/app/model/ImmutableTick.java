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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((instrument == null) ? 0 : instrument.hashCode());
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImmutableTick other = (ImmutableTick) obj;
		if (instrument == null) {
			if (other.instrument != null)
				return false;
		} else if (!instrument.equals(other.instrument))
			return false;
		if (Double.doubleToLongBits(price) != Double.doubleToLongBits(other.price))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}



	public static Comparator<ImmutableTick> timestampComparator = (x,y) -> Long.compare(x.getTimestamp(), y.getTimestamp());

}
