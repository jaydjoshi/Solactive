package com.solactive.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author jay
 *
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class TickNotInsertedException extends RuntimeException {


	private static final long serialVersionUID = 8339587788665030327L;

	public TickNotInsertedException() {
		super();
		
	}

	public TickNotInsertedException(String message) {
		super(message);
		
	}

	public TickNotInsertedException(Throwable cause) {
		super(cause);
		
	}
	
	

}
