package com.connorhaigh.pettyrest.http;

public class Reply 
{
	/**
	 * Create a new HTTP reply object.
	 * @param code The HTTP code.
	 * @param message The HTTP message.
	 * @param description A friendly description of the reply.
	 */
	private Reply(String code, String message, String description)
	{
		//init
		this.code = code;
		this.message = message;
		
		this.description = description;
	}
	
	/**
	 * Returns the friendly reply.
	 * @return The friendly reply.
	 */
	public String getReply()
	{
		return (this.code + " " + this.message);
	}
	
	/**
	 * Returns the friendly description.
	 * @return The friendly description.
	 */
	public String getDescription()
	{
		return this.description;
	}
	
	//replies
	public static final Reply OKAY_200_REPLY = new Reply("200", "OK", "The resource was found and processed successfully.");
	public static final Reply MOVED_PERMANENTLY_301_REPLY = new Reply("301", "Moved Permanently", "The resource has permanently been moved to a different location.");
	public static final Reply BAD_REQUEST_400_REPLY = new Reply("400", "Bad Request", "A malformed request type was sent for the specified resource.");
	public static final Reply ACCESS_DENIED_403_REPLY = new Reply("403", "Access Denied", "You do not have permission to view this resource.");
	public static final Reply NOT_FOUND_404_REPLY = new Reply("404", "Not Found", "The specified resource was not found on this server.");
	public static final Reply INTERNAL_SERVER_ERROR_500_REPLY = new Reply("500", "Internal Server Error", "The requested resource could not be processed successfully on this server.");
	
	//vars
	private String code;
	private String message;
	
	private String description;
}
