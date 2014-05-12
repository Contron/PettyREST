package com.connorhaigh.pettyrest.core;

import java.util.HashMap;

public interface Handler 
{
	/**
	 * Handle a request.
	 * @param arguments the arguments
	 * @param headers the HTTP header data
	 * @param post the HTTP post data
	 * @return the output
	 */
	public abstract String handle(HashMap<String, String> arguments, HashMap<String, String> headers, HashMap<String, String> post);
}
