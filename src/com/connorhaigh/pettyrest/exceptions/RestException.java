package com.connorhaigh.pettyrest.exceptions;

public class RestException extends Exception
{
	/**
	 * Create a new REST related exception.
	 * @param message the exception message
	 */
	public RestException(String message)
	{
		super(message);
	}
	
	public static final long serialVersionUID = 1;
}
