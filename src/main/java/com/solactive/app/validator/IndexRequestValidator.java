package com.solactive.app.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.solactive.app.constant.IndexConstant;
import com.solactive.app.exception.InvalidTickException;
import com.solactive.app.model.Tick;
import com.solactive.app.service.impl.IndexServiceImpl;

@Component
public class IndexRequestValidator {
	
	private static final Logger logger = LoggerFactory.getLogger(IndexServiceImpl.class);
	
	/**
	 * validate the request
	 * @param tick
	 * @param currentTimeStamp
	 * @throws InvalidTickException
	 */
	public void validate(Tick tick, long currentTimeStamp) throws InvalidTickException {
	
		if(tick.getTimestamp() < (currentTimeStamp - IndexConstant.DEFAULT_SLIDING_WINDOW_MS)) {
			logger.error(IndexConstant.TICK_OLDER_THAN_60_SECONDS_MESSAGE);
			throw new InvalidTickException(IndexConstant.TICK_OLDER_THAN_60_SECONDS_MESSAGE);
		}
		if(StringUtils.isEmpty(tick.getInstrument())) {
			logger.error(IndexConstant.EMPTY_INSTRUMENT_MESSAGE);
			throw new InvalidTickException(IndexConstant.EMPTY_INSTRUMENT_MESSAGE);
		}
		if(tick.getPrice() < 0) {
			logger.error(IndexConstant.PRICE_LESS_THAN_ZERO_MESSAGE);
			throw new InvalidTickException(IndexConstant.PRICE_LESS_THAN_ZERO_MESSAGE);
		}
		
	}

}
