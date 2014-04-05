package com.connorhaigh.pettyrest.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
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
	 * @throws Exception if a general exception occurs
	 */
	private void handle() throws IOException, Exception
	{
		//get request
		String requestLine = this.inputStream.readLine();
		String[] requestParts = requestLine.split(" ");
		
		//get info
		String type = requestParts[0].trim();
		String resource = requestParts[1].trim();
		String version = requestParts[2].trim();
		
		//data maps
		HashMap<String, String> arguments = new HashMap<String, String>();
		HashMap<String, String> header = new HashMap<String, String>();
		HashMap<String, String> post = new HashMap<String, String>();
		
		//check arguments
		int argsIndex = resource.indexOf(Transaction.ARGUMENT_INDICATOR);
		if (argsIndex != -1)
		{
			//extract arguments
			String argumentsLine = resource.substring(argsIndex + 1);
			String[] argumentData = argumentsLine.split(Transaction.ARGUMENT_SEPARATOR);
			for (String field : argumentData)
			{
				//split again
				String[] fieldData = field.split(Transaction.KEY_VALUE_OPERATOR);
				if (fieldData.length < 2)
					break;
				
				//put
				String key = fieldData[0].trim();
				String value = fieldData[1].trim();
				arguments.put(key, value);
			}
			
			//remove from request
			resource = resource.substring(0, argsIndex);
		}
		
		//read input headers
		String headerLine = null;
		while ((headerLine = this.inputStream.readLine()) != null)
		{
			//check empty
			if (headerLine.isEmpty())
				break;
			
			//split
			String[] lineData = headerLine.split(Transaction.HEADER_SEPARATOR);
			String key = lineData[0].trim();
			String value = lineData[1].trim();
				
			//put
			header.put(key, value);
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
			String[] lineData = fieldLine.split(Transaction.POST_SEPARATOR);
			for (String field : lineData)
			{
				//split again
				String[] fieldData = field.split(Transaction.KEY_VALUE_OPERATOR);
				if (fieldData.length < 2)
					break;
				
				//put
				String key = fieldData[0].trim();
				String value = fieldData[1].trim();
				post.put(key, value);
			}
		}
		
		//output
		this.outputStream.write(this.process(version, type, arguments, header, post, resource));
	}
	
	/**
	 * Process a request with the appropriate parameters.
	 * @param version the HTTP version
	 * @param type the type of the request
	 * @param arguments the map containing arguments, if any
	 * @param header the map containing the header data
	 * @param post the map containing the POST data
	 * @param resource the resource to look for
	 * @return the page.
	 */
	private String process(String version, String type, 
			HashMap<String, String> arguments, HashMap<String, String> header, HashMap<String, String> post, 
			String resource)
	{
		//check size
		if (arguments.size() > PettyREST.MAX_ARGUMENTS || header.size() > PettyREST.MAX_HEADERS || post.size() > PettyREST.MAX_POST)
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
			request.handle(arguments, header, post, output);
			
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
	
	public static final String KEY_VALUE_OPERATOR = "=";
	
	public static final String ARGUMENT_INDICATOR = "?";
	public static final String ARGUMENT_SEPARATOR = "&";
	public static final String HEADER_SEPARATOR = ":";
	public static final String POST_SEPARATOR = "&";
	
	private Server server;
	private Socket socket;
	
	private BufferedReader inputStream;
	private BufferedWriter outputStream;
	
	private boolean running;
	private Thread runningThread;
}
