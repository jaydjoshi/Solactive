package com.solactive.app.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.solactive.app.constant.IndexConstant;
import com.solactive.app.exception.InvalidTickException;
import com.solactive.app.model.Tick;

@Component
public class IndexRequestValidator {
	
	/**
	 * validate the request
	 * @param tick
	 * @param currentTimeStamp
	 * @throws InvalidTickException
	 */
	public void validate(Tick tick, long currentTimeStamp) throws InvalidTickException {
	
		if(tick.getTimestamp() < (currentTimeStamp - IndexConstant.DEFAULT_SLIDING_WINDOW_MS)) {
			throw new InvalidTickException(IndexConstant.TICK_OLDER_THAN_60_SECONDS_MESSAGE);
		}
		if(StringUtils.isEmpty(tick.getInstrument())) {
			throw new InvalidTickException(IndexConstant.EMPTY_INSTRUMENT_MESSAGE);
		}
		if(tick.getPrice() < 0) {
			throw new InvalidTickException(IndexConstant.PRICE_LESS_THAN_ZERO_MESSAGE);
		}
		
	}

}
