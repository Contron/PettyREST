package com.connorhaigh.pettyrest.http;

import com.connorhaigh.pettyrest.PettyREST;

public class Header 
{
	/**
	 * Construct a HTTP header.
	 * @param reply The reply code.
	 * @param type The content type.
	 * @param length The content length.
	 * @return The constructed header.
	 */
	public static String construct(Reply reply, ContentType type, int length)
	{
		//builder
		StringBuilder stringBuilder = new StringBuilder();
		
		//code
		stringBuilder.append("HTTP/1.1 " + reply.getReply());
		stringBuilder.append(Header.CARRIAGE_RETURN);
		
		//content type, length and connection
		stringBuilder.append("Content-Type: " + type.getType());
		stringBuilder.append(Header.CARRIAGE_RETURN);
		stringBuilder.append("Content-Length: " + length);
		stringBuilder.append(Header.CARRIAGE_RETURN);
		stringBuilder.append("Connection: close");
		stringBuilder.append(Header.CARRIAGE_RETURN);
		
		//server
		stringBuilder.append("Server: " + PettyREST.SERVER_NAME);
		stringBuilder.append(Header.CARRIAGE_RETURN);
		
		//carriage return
		//signals the end of the header
		stringBuilder.append(Header.CARRIAGE_RETURN);
		
		return stringBuilder.toString();
	}
	
	//constants
	public static final String CARRIAGE_RETURN = ("\r\n");
}
