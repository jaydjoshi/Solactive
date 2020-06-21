package com.solactive.app.model;

public final class Statistics {
	
	private final double avg;
	private final double max;
	private final double min;
	private final long count;
	
	/**
	 * 
	 * @param avg
	 * @param max
	 * @param min
	 * @param count
	 */
	public Statistics(double avg, double max, double min, long count) {
		super();
		this.avg = avg;
		this.max = max;
		this.min = min;
		this.count = count;
	}

	public double getAvg() {
		return avg;
	}
	
	public double getMax() {
		return max;
	}
	
	public double getMin() {
		return min;
	}
	
	public long getCount() {
		return count;
	}
	
	@Override
	public String toString() {
		return "Statistics [avg=" + avg + ", max=" + max + ", min=" + min + ", count=" + count + "]";
	}
	
	

}
