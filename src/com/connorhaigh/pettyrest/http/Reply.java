package com.connorhaigh.pettyrest.http;

public class Reply 
{
	/**
	 * Create a new HTTP reply object.
	 * @param code the HTTP code
	 * @param message the HTTP message
	 * @param description a friendly description of the reply
	 */
	private Reply(String code, String message, String description)
	{
		this.code = code;
		this.message = message;
		
		this.description = description;
	}
	
	/**
	 * Returns the friendly reply.
	 * @return the friendly reply
	 */
	public String getReply()
	{
		return (this.code + " " + this.message);
	}
	
	/**
	 * Returns the friendly description.
	 * @return the friendly description
	 */
	public String getDescription()
	{
		return this.description;
	}

	public static final Reply OKAY_200 = new Reply("200", "OK", "The resource was found and processed successfully.");
	public static final Reply MOVED_PERMANENTLY_301 = new Reply("301", "Moved Permanently", "The resource has permanently been moved to a different location.");
	public static final Reply BAD_REQUEST_400 = new Reply("400", "Bad Request", "A malformed request type was sent for the specified resource.");
	public static final Reply METHOD_NOT_ALLOWED_405 = new Reply("405", "Method Not Allowed", "The method used for the specified resource is not allowed.");
	public static final Reply REQUEST_TOO_LARGE_413 = new Reply("413", "Request Entity Too Large", "The request is too large for this server to process.");
	public static final Reply ACCESS_DENIED_403 = new Reply("403", "Access Denied", "You do not have permission to view this resource.");
	public static final Reply NOT_FOUND_404 = new Reply("404", "Not Found", "The specified resource was not found on this server.");
	public static final Reply INTERNAL_SERVER_ERROR_500 = new Reply("500", "Internal Server Error", "The requested resource could not be processed successfully on this server.");
	public static final Reply HTTP_VERSION_NOT_SUPPORTED_505 = new Reply("505", "HTTP Version Not Supported", "The HTTP version used in the request is not supported on this server.");
	
	private String code;
	private String message;
	
	private String description;
}
