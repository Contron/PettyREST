package com.connorhaigh.pettyrest.core;

//imports
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.connorhaigh.pettyrest.PettyREST;
import com.connorhaigh.pettyrest.exceptions.RestException;
import com.connorhaigh.pettyrest.html.Output;
import com.connorhaigh.pettyrest.http.Header;
import com.connorhaigh.pettyrest.http.Reply;

public class Transaction implements Runnable
{
	/**
	 * Create a new transaction between the client and the server.
	 * @param server the server object
	 * @param socket the client's socket
	 */
	public Transaction(Server server, Socket socket)
	{
		this.server = server;
		this.socket = socket;
		
		this.running = false;
		this.runningThread = null;
	}
	
	/**
	 * Run the transaction thread.
	 */
	@Override
	public void run()
	{
		try
		{
			//handle
			Transaction.this.handle();
			Transaction.this.stop();
		}
		catch (Exception ex)
		{
			//error
			this.server.notifyErrorListeners(ex);
		}
	}
	
	/**
	 * Start processing the request from the client.
	 * @throws RestException if the transaction is already in progress
	 * @throws IOException if the wrapper streams could not be created
	 */
	public void start() throws RestException, IOException
	{
		//check if running
		if (this.running)
			throw new RestException("Transaction already in progress");
		
		//wrapper streams
		this.inputStream = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		this.outputStream = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		
		//start thread
		this.running = true;
		this.runningThread = new Thread(this);
		this.runningThread.setName("Transaction Thread");
		this.runningThread.start();
	}
	
	/**
	 * Stop processing the request from the client.
	 * @throws RestException if the transaction is not in progress
	 * @throws IOException if the streams could not be closed
	 */
	public void stop() throws RestException, IOException
	{
		//check if not running
		if (!this.running)
			throw new RestException("Transaction not in progress");
		
		//wrapper streams
		this.outputStream.flush();
		this.outputStream.close();
		
		//close the input stream after
		this.inputStream.close();
		
		//stop thread
		this.running = false;
	}
	
	/**
	 * Handle the request.
	 * @throws IOException if the sockets could not be read or written to
	 */
	private void handle() throws IOException
	{
		//get request
		String requestLine = this.inputStream.readLine();
		String[] requestParts = requestLine.split(" ");
		
		//get info
		String type = requestParts[0].trim();
		String resource = requestParts[1].trim();
		String version = requestParts[2].trim();
		String[] resourceParts = resource.split("/");
		
		//convert arguments
		ArrayList<String> args = new ArrayList<String>(Arrays.asList(resourceParts));
		
		//data maps
		HashMap<String, String> headerMap = new HashMap<String, String>();
		HashMap<String, String> postMap = new HashMap<String, String>();
		
		//read input headers
		String headerLine = null;
		while ((headerLine = this.inputStream.readLine()) != null)
		{
			//break off if empty
			if (headerLine.isEmpty())
				break;
			
			//split
			String[] lineData = headerLine.split(":");
			String key = lineData[0].trim();
			String value = lineData[1].trim();
				
			//put
			headerMap.put(key, value);
		}
		
		//read POST data
		if (this.inputStream.ready())
		{
			//line builder
			StringBuilder lineBuilder = new StringBuilder();
			
			//read char by char
			while (this.inputStream.ready())
				lineBuilder.append((char) this.inputStream.read());
			
			//split
			String fieldLine = lineBuilder.toString();
			String[] lineData = fieldLine.split("&");
			for (String field : lineData)
			{
				//split again
				String[] fieldData = field.split("=");
				String key = fieldData[0].trim();
				String value = fieldData[1].trim();
				
				//put
				postMap.put(key, value);
			}
		}
		
		//output
		this.outputStream.write(this.process(version, type, headerMap, postMap, args, resource));
	}
	
	/**
	 * Process a request with the appropriate parameters.
	 * @param version the HTTP version
	 * @param type the type of the request
	 * @param headerMap the map containing the header data
	 * @param postMap the map containing the POST data
	 * @param args the list of arguments
	 * @param resource the resource to look for
	 * @return the page.
	 */
	private String process(String version, String type, 
			HashMap<String, String> headerMap, HashMap<String, String> postMap, ArrayList<String> args, 
			String resource)
	{
		//check size
		if (headerMap.size() > PettyREST.MAX_HEADERS || postMap.size() > PettyREST.MAX_POST)
			return Output.constructAll(Reply.REQUEST_TOO_LARGE_413_REPLY);
		
		//check version
		if (!version.equals(PettyREST.HTTP_VERSION))
			return Output.constructAll(Reply.HTTP_VERSION_NOT_SUPPORTED_505_REPLY);
		
		//check for match
		if (!this.server.contains(resource))
			return Output.constructAll(Reply.NOT_FOUND_404_REPLY);
		
		//get
		Definition definition = this.server.get(resource);
		String requestType = definition.getRequestType().getType();
		
		//check request type
		if (!requestType.equals(type))
			return Output.constructAll(Reply.BAD_REQUEST_400_REPLY);
		
		try
		{		
			//output
			StringBuilder output = new StringBuilder();
			
			//create request class and handle
			Class<? extends Request> requestClass = definition.getRequestClass();
			Request request = requestClass.newInstance();
			request.handle(headerMap, postMap, args, output);
			
			//construct
			String headers = Header.construct(Reply.OKAY_200_REPLY, definition.getContentType(), output.length());
			String page = (headers + output.toString());
		
			return page;
		}
		catch (Exception ex)
		{
			//error
			return Output.constructAll(Reply.INTERNAL_SERVER_ERROR_500_REPLY);
		}
	}
	
	/**
	 * Returns the server for this transaction.
	 * @return the server
	 */
	public Server getServer()
	{
		return this.server;
	}
	
	/**
	 * Returns the client socket for this transaction.
	 * @return the client socket
	 */
	public Socket getSocket()
	{
		return this.socket;
	}
	
	private Server server;
	private Socket socket;
	
	private BufferedReader inputStream;
	private BufferedWriter outputStream;
	
	private boolean running;
	private Thread runningThread;
}
