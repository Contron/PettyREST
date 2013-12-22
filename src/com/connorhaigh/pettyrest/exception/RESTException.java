package com.connorhaigh.pettyrest.exception;

public class RESTException extends Exception
{
	/**
	 * Create a new REST related exception.
	 * @param message The exception message.
	 */
	public RESTException(String message)
	{
		super(message);
	}
	
	//vars
	public static final long serialVersionUID = 1;
}
