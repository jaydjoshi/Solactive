package com.solactive.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author jay
 *
 */
@ResponseStatus(HttpStatus.NO_CONTENT)
public class NoTickerAvailableException extends RuntimeException {


	private static final long serialVersionUID = 8339587788665030327L;

	public NoTickerAvailableException() {
		super();
		
	}

	public NoTickerAvailableException(String message) {
		super(message);
		
	}

	public NoTickerAvailableException(Throwable cause) {
		super(cause);
		
	}
	
	
}
