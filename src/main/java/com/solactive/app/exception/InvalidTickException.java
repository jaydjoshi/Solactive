package com.solactive.app.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class InvalidTickException extends RuntimeException {


	private static final long serialVersionUID = 8339587788665030327L;

	public InvalidTickException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InvalidTickException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public InvalidTickException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	

}
