package com.connorhaigh.pettyrest.core;

import com.connorhaigh.pettyrest.http.ContentType;
import com.connorhaigh.pettyrest.http.RequestType;

public class Definition 
{
	/**
	 * Create a new request class definition.
	 * @param requestType the type of the request (i.e. GET)
	 * @param contentType the content type output of the request (i.e. HTML)
	 * @param handler the handler that will respond to the request
	 */
	public Definition(RequestType requestType, ContentType contentType, Handler handler)
	{
		this.requestType = requestType;
		this.contentType = contentType;
		
		this.handler = handler;
	}
	
	/**
	 * Returns the request type for this definition.
	 * @return the request type
	 */
	public RequestType getRequestType()
	{
		return this.requestType;
	}
	
	/**
	 * Returns the content type for this definition.
	 * @return the content type
	 */
	public ContentType getContentType()
	{
		return this.contentType;
	}
	
	/**
	 * Returns the handler for this definition.
	 * @return the handler
	 */
	public Handler getHandler()
	{
		return this.handler;
	}
	
	private RequestType requestType;
	private ContentType contentType;
	
	private Handler handler;
}
