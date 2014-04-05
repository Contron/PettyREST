package com.connorhaigh.pettyrest.core;

import com.connorhaigh.pettyrest.http.ContentType;
import com.connorhaigh.pettyrest.http.RequestType;

public class Definition 
{
	/**
	 * Create a new request class definition.
	 * @param request the request class
	 * @param requestType the type of the request (i.e. GET)
	 * @param contentType the content type output of the request (i.e. HTML)
	 */
	public Definition(Class<? extends Request> request, RequestType requestType, ContentType contentType)
	{
		this.request = request;
		
		this.requestType = requestType;
		this.contentType = contentType;
	}
	
	/**
	 * Returns the request class for this definition.
	 * @return the request class
	 */
	public Class<? extends Request> getRequestClass()
	{
		return this.request;
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
	
	private Class<? extends Request> request;
	
	private RequestType requestType;
	private ContentType contentType;
}
