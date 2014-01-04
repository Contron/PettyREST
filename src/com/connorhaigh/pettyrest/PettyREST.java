package com.connorhaigh.pettyrest;

import com.connorhaigh.pettyrest.core.Server;

public class PettyREST 
{
	public static void main(String[] args)
	{
		try
		{
			Server server = new Server(8080);
			server.start();
		} catch (Exception ex) {
			
		}
	}
	
	//vars
	public static final String SERVER_NAME = ("PettyREST Server");
}
