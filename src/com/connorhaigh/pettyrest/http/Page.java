package com.connorhaigh.pettyrest.http;

import com.connorhaigh.pettyrest.PettyREST;

public class Page 
{
	/**
	 * Construct a barebones page.
	 * @param title The title of the page.
	 * @param description The page description.
	 * @return
	 */
	public static String construct(String title, String description)
	{
		//builder
		StringBuilder stringBuilder = new StringBuilder();
		
		//doc type
		stringBuilder.append("<!DOCTYPE html>");
		stringBuilder.append(Page.NEW_LINE);
		stringBuilder.append("<html>");
		stringBuilder.append(Page.NEW_LINE);
		
		//head
		stringBuilder.append("\t<head>");
		stringBuilder.append(Page.NEW_LINE);
		stringBuilder.append("\t\t<title>" + title + "</title>");
		stringBuilder.append(Page.NEW_LINE);
		stringBuilder.append("\t</head>");
		stringBuilder.append(Page.NEW_LINE);
		
		//body
		stringBuilder.append("\t<body>");
		stringBuilder.append(Page.NEW_LINE);
		stringBuilder.append("\t\t<h1>" + title + "</h1>");
		stringBuilder.append(Page.NEW_LINE);
		stringBuilder.append("\t\t<p>" + description + "</p>");
		stringBuilder.append(Page.NEW_LINE);
		stringBuilder.append("\t\t<hr />");
		stringBuilder.append(Page.NEW_LINE);
		stringBuilder.append("\t\t" + "<i>" + PettyREST.SERVER_NAME + "</i>");
		stringBuilder.append(Page.NEW_LINE);
		stringBuilder.append("\t</body>");
		stringBuilder.append(Page.NEW_LINE);
		stringBuilder.append("</html>");
		stringBuilder.append(Page.NEW_LINE);
		
		return stringBuilder.toString();
	}
	
	//constants
	public static final String NEW_LINE = ("\n");
}
