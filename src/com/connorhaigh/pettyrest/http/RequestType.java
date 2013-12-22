package com.connorhaigh.pettyrest.http;

public class RequestType 
{
	/**
	 * Create a new HTTP request type object.
	 * @param type The request type.
	 */
	public RequestType(String type)
	{
		//init
		this.type = type;
	}
	
	/**
	 * Returns the friendly type.
	 * @return The friendly type.
	 */
	public String getType()
	{
		return this.type;
	}
	
	//types
	public static final RequestType GET_REQUEST = new RequestType("GET");
	public static final RequestType POST_REQUEST = new RequestType("POST");
	public static final RequestType DELETE_REQUEST = new RequestType("DELETE");
	
	//vars
	private String type;
}
