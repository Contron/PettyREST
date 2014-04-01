package com.connorhaigh.pettyrest.http;

public class RequestType 
{
	/**
	 * Create a new HTTP request type object.
	 * @param type the request type
	 */
	public RequestType(String type)
	{
		this.type = type;
	}
	
	/**
	 * Returns the friendly type.
	 * @return the friendly type
	 */
	public String getType()
	{
		return this.type;
	}
	
	public static final RequestType GET_REQUEST = new RequestType("GET");
	public static final RequestType POST_REQUEST = new RequestType("POST");
	public static final RequestType DELETE_REQUEST = new RequestType("DELETE");
	
	private String type;
}
