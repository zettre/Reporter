package com.tcs.reporter.exception;

public class ReporterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	
	public ReporterException(String message) {
		super();
		this.message=message;
	}

	public String getMessage()
	{
		return this.message;
	}
}
