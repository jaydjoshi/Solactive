/**
 * 
 */
package com.solactive.app.model;

/**
 * @author jay
 * POJO class of Tick
 */
public class Tick implements Comparable<Tick>{
	
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
		Tick other = (Tick) obj;
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
	
	@Override
	public String toString() {
		return "Tick [instrument=" + instrument + ", price=" + price + ", timestamp=" + timestamp + "]";
	}
	
	@Override
	public int compareTo(Tick o) {
	
		int difference = (int) (this.timestamp - o.timestamp);
		return difference;
	}
	
	
	
}
