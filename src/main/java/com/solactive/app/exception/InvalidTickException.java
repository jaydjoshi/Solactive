package com.solactive.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 
 * @author jay
 *
 */
@ResponseStatus(HttpStatus.NO_CONTENT)
public class InvalidTickException extends RuntimeException {


	private static final long serialVersionUID = 8339587788665030327L;

	public InvalidTickException() {
		super();
		
	}

	public InvalidTickException(String message) {
		super(message);
		
	}

	public InvalidTickException(Throwable cause) {
		super(cause);
		
	}
	
	

}
