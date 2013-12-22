package com.connorhaigh.pettyrest.core;

//imports
import java.util.ArrayList;
import java.util.HashMap;

public abstract class Request 
{
	/**
	 * Handle a request.
	 * @param headers The map of header parameters.
	 * @param post The map of POST data, if available.
	 * @param args The array of arguments.
	 * @param output The output builder.
	 */
	public abstract void handle(HashMap<String, String> headers, HashMap<String, String> post, ArrayList<String> args, StringBuilder output);
}
