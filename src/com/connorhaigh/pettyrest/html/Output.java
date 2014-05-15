package com.connorhaigh.pettyrest.html;

import com.connorhaigh.pettyrest.http.ContentType;
import com.connorhaigh.pettyrest.http.Header;
import com.connorhaigh.pettyrest.http.Reply;

public class Output 
{
	/**
	 * Construct a whole page.
	 * @param reply the reply to use
	 * @return the page
	 */
	public static String constructAll(Reply reply)
	{
		//get page and header
		String page = Page.construct(reply.getReply(), reply.getDescription());
		String header = Header.construct(reply, ContentType.TEXT_HTML, page.length());
		
		return (header + page);
	}
}
