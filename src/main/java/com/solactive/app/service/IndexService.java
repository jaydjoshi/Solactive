package com.solactive.app.service;

import com.solactive.app.model.Statistics;
import com.solactive.app.model.Tick;

public interface IndexService {
	
	/**
	 * 
	 * @param tick
	 * @return boolean value whether tick was inserted or not
	 */
	public boolean insertTicks(Tick tick);

	/**
	 * 
	 * 
	 * @return Ststistics of all tickers
	 */
	public Statistics getStatistics();

	/**
	 * 
	 * 
	 * @param instrument
	 * @return Statistics of single ticker
	 */
	public Statistics getStatistics(String instrument) ;

}
