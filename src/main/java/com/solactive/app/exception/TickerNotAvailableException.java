package com.solactive.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author jay
 *
 */
@ResponseStatus(HttpStatus.NO_CONTENT)
public class TickerNotAvailableException extends RuntimeException {


	private static final long serialVersionUID = 8339587788665030327L;

	public TickerNotAvailableException() {
		super();
		
	}

	public TickerNotAvailableException(String message) {
		super(message);
		
	}

	public TickerNotAvailableException(Throwable cause) {
		super(cause);
		
	}
	
	
}
