package com.connorhaigh.pettyrest.html;

import com.connorhaigh.pettyrest.PettyREST;

public class Page 
{
	/**
	 * Construct a barebones page.
	 * @param title the title of the page
	 * @param description the page description
	 * @return the page
	 */
	public static String construct(String title, String description)
	{
		//builder
		StringBuilder stringBuilder = new StringBuilder();
		
		//doc type
		stringBuilder.append("<!DOCTYPE html>" + Page.NEW_LINE);
		stringBuilder.append("<html>" + Page.NEW_LINE);
		
		//head
		stringBuilder.append("\t<head>" + Page.NEW_LINE);
		stringBuilder.append("\t\t<title>" + title + "</title>" + Page.NEW_LINE);
		stringBuilder.append("\t</head>" + Page.NEW_LINE);
		
		//body
		stringBuilder.append("\t<body>" + Page.NEW_LINE);
		stringBuilder.append("\t\t<h1>" + title + "</h1>" + Page.NEW_LINE);
		stringBuilder.append("\t\t<p>" + description + "</p>" + Page.NEW_LINE);
		stringBuilder.append("\t\t<hr />" + Page.NEW_LINE);
		stringBuilder.append("\t\t<i>" + PettyREST.SERVER_NAME + "</i>" + Page.NEW_LINE);
		stringBuilder.append("\t</body>" + Page.NEW_LINE);
		stringBuilder.append("</html>" + Page.NEW_LINE);
		
		return stringBuilder.toString();
	}
	
	public static final String NEW_LINE = "\n";
}
