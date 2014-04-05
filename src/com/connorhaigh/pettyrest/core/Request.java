package com.connorhaigh.pettyrest.core;

import java.util.HashMap;

public abstract class Request 
{
	/**
	 * Handle a request.
	 * @param arguments the map of arguments, if any
	 * @param headers the map of header information
	 * @param post the map of post information, if any
	 * @param output the output builder which is returned to the request
	 */
	public abstract void handle(HashMap<String, String> arguments, HashMap<String, String> headers, HashMap<String, String> post, StringBuilder output);
}
