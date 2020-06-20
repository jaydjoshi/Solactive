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
	 * @param currentTimeStamp
	 * @return
	 */
	public Statistics getStatistics(long currentTimeStamp);

	/**
	 * 
	 * @param currentTimeStamp
	 * @param instrument
	 * @return
	 */
	public Statistics getStatistics(long currentTimeStamp, String instrument) ;

}
