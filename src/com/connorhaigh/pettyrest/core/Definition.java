package com.connorhaigh.pettyrest.core;

//imports
import com.connorhaigh.pettyrest.http.ContentType;
import com.connorhaigh.pettyrest.http.RequestType;

public class Definition 
{
	/**
	 * Create a new request class definition.
	 * @param request The request class.
	 * @param requestType The type of the request (i.e. GET).
	 * @param contentType The content type output of the request (i.e. HTML).
	 */
	public Definition(Class<? extends Request> request, RequestType requestType, ContentType contentType)
	{
		//init
		this.request = request;
		
		this.requestType = requestType;
		this.contentType = contentType;
	}
	
	/**
	 * Returns the request class for this definition.
	 * @return The request class.
	 */
	public Class<? extends Request> getRequest()
	{
		return this.request;
	}
	
	/**
	 * Returns the request type for this definition.
	 * @return The request type.
	 */
	public RequestType getRequestType()
	{
		return this.requestType;
	}
	
	/**
	 * Returns the content type for this definition.
	 * @return The content type.
	 */
	public ContentType getContentType()
	{
		return this.contentType;
	}
	
	//vars
	private Class<? extends Request> request;
	
	private RequestType requestType;
	private ContentType contentType;
}
