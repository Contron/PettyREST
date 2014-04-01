package com.connorhaigh.pettyrest.http;

public class ContentType 
{
	/**
	 * Create a new HTTP content type object.
	 * @param type the main type
	 * @param subtype the sub type
	 */
	public ContentType(String type, String subtype)
	{
		this.type = type;
		this.subtype = subtype;
	}
	
	/**
	 * Returns the friendly type.
	 * @return the friendly type
	 */
	public String getType()
	{
		return (this.type + "/" + this.subtype);
	}
	
	public static final ContentType TEXT_PLAIN_TYPE = new ContentType("text", "plain");
	public static final ContentType TEXT_HTML_TYPE = new ContentType("text", "html");
	
	private String type;
	private String subtype;
}
