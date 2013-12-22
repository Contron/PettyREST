package com.connorhaigh.pettyrest.http;

public class Output 
{
	/**
	 * Construct a whole page.
	 * @param reply The reply to use.
	 * @return The page.
	 */
	public static String constructAll(Reply reply)
	{
		//get page and header
		String page = Page.construct(reply.getReply(), reply.getDescription());
		String header = Header.construct(reply, ContentType.TEXT_HTML_TYPE, page.length());
		
		return (header + page);
	}
}
