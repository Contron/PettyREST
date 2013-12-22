package com.connorhaigh.pettyrest.http;

public class ContentType 
{
	/**
	 * Create a new HTTP content type object.
	 * @param type The main type.
	 * @param subtype The sub type.
	 */
	public ContentType(String type, String subtype)
	{
		//init
		this.type = type;
		this.subtype = subtype;
	}
	
	/**
	 * Returns the friendly type.
	 * @return The friendly type.
	 */
	public String getType()
	{
		return (this.type + "/" + this.subtype);
	}
	
	//types
	public static final ContentType TEXT_PLAIN_TYPE = new ContentType("text", "plain");
	public static final ContentType TEXT_HTML_TYPE = new ContentType("text", "html");
	
	//vars
	private String type;
	private String subtype;
}
