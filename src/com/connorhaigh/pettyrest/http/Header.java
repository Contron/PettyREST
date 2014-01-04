package com.connorhaigh.pettyrest.http;

//imports
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
		stringBuilder.append("HTTP/1.1 " + reply.getReply() + Header.CARRIAGE_RETURN);
		
		//content type, length and connection
		stringBuilder.append("Content-Type: " + type.getType() + Header.CARRIAGE_RETURN);
		stringBuilder.append("Content-Length: " + length + Header.CARRIAGE_RETURN);
		stringBuilder.append("Cache-Control: no-cache" + Header.CARRIAGE_RETURN);
		stringBuilder.append("Connection: close" + Header.CARRIAGE_RETURN);
		
		//server
		stringBuilder.append("Server: " + PettyREST.SERVER_NAME + Header.CARRIAGE_RETURN);
		
		//carriage return
		stringBuilder.append(Header.CARRIAGE_RETURN);
		
		return stringBuilder.toString();
	}
	
	//constants
	public static final String CARRIAGE_RETURN = ("\r\n");
}
