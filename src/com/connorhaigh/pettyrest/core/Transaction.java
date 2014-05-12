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
			//notify
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
		
		//check length
		if (requestParts.length < 3)
		{
			//bad request
			this.outputStream.write(Output.constructAll(Reply.BAD_REQUEST_400_REPLY));
			
			return;
		}
		
		//get info
		String type = requestParts[0].trim();
		String resource = requestParts[1].trim();
		String version = requestParts[2].trim();
		
		//data maps
		HashMap<String, String> arguments = new HashMap<String, String>();
		HashMap<String, String> headers = new HashMap<String, String>();
		HashMap<String, String> post = new HashMap<String, String>();
		
		//check arguments
		int argsIndex = resource.indexOf(Transaction.ARGUMENT_INDICATOR);
		if (argsIndex != -1)
		{
			//read arguments
			this.readArguments(resource, argsIndex, arguments);
			resource = resource.substring(0, argsIndex);
		}
		
		//read headers and POST data
		this.readHeaders(headers);
		this.readPost(post);
		
		//output
		this.outputStream.write(this.process(version, type, arguments, headers, post, resource));
	}
	
	/**
	 * Reads the arguments from this transaction's resource identifier.
	 * @param resource the resource identifier
	 * @param argsIndex the index of the argument character
	 * @param arguments the arguments map to place entries in
	 * @throws IOException if the stream could not be read
	 */
	private void readArguments(String resource, int argsIndex, HashMap<String, String> arguments) throws IOException
	{	
		//read arguments
		String argumentsLine = resource.substring(argsIndex + 1);
		String[] argumentData = argumentsLine.split(Transaction.ARGUMENT_SEPARATOR);
		for (String field : argumentData)
		{
			//split again
			String[] fieldData = field.split(Transaction.KEY_VALUE_OPERATOR);
			if (fieldData.length < 2)
				continue;
			
			//put
			String key = fieldData[0].trim();
			String value = fieldData[1].trim();
			arguments.put(key, value);
		}
	}
	
	/**
	 * Reads the headers from this transaction's underlying input stream.
	 * @param headers the header map to place entries in
	 * @throws IOException if the stream could not be read
	 */
	private void readHeaders(HashMap<String, String> headers) throws IOException
	{
		//read input headers
		String headerLine = null;
		while ((headerLine = this.inputStream.readLine()) != null)
		{
			//check empty
			if (headerLine.isEmpty())
				break;
			
			//split
			String[] lineData = headerLine.split(Transaction.HEADER_SEPARATOR);
			if (lineData.length < 2)
				continue;
			
			//put
			String key = lineData[0].trim();
			String value = lineData[1].trim();
			headers.put(key, value);
		}
	}
	
	/**
	 * Reads the POST data from this transaction's underlying input stream.
	 * @param post the POST map to place entries in
	 * @throws IOException if the stream could not be read
	 */
	private void readPost(HashMap<String, String> post) throws IOException
	{
		//check if ready
		if (this.inputStream.ready())
		{
			//read char by char
			StringBuilder lineBuilder = new StringBuilder();
			while (this.inputStream.ready())
				lineBuilder.append((char) this.inputStream.read());
			
			//split
			String line = lineBuilder.toString();
			String[] lineData = line.split(Transaction.POST_SEPARATOR);
			for (String field : lineData)
			{
				//split again
				String[] fieldData = field.split(Transaction.KEY_VALUE_OPERATOR);
				if (fieldData.length < 2)
					continue;
				
				//put
				String key = fieldData[0].trim();
				String value = fieldData[1].trim();
				post.put(key, value);
			}
		}
	}
	
	/**
	 * Process a request with the appropriate parameters.
	 * @param version the HTTP version
	 * @param type the type of the request
	 * @param arguments the map containing arguments, if any
	 * @param headers the map containing the header data
	 * @param post the map containing the POST data
	 * @param resource the resource to look for
	 * @return the page.
	 */
	private String process(String version, String type, 
			HashMap<String, String> arguments, HashMap<String, String> headers, HashMap<String, String> post, 
			String resource)
	{
		//check size
		if (arguments.size() > this.server.getMaxArguments() || headers.size() > this.server.getMaxHeaders() || post.size() > this.server.getMaxPost())
			return Output.constructAll(Reply.REQUEST_TOO_LARGE_413_REPLY);
		
		//check version
		if (!version.equals(PettyREST.HTTP_VERSION))
			return Output.constructAll(Reply.HTTP_VERSION_NOT_SUPPORTED_505_REPLY);
		
		//check for match
		if (!this.server.contains(resource))
			return Output.constructAll(Reply.NOT_FOUND_404_REPLY);
		
		//get definition
		Definition definition = this.server.get(resource);
		
		//check request type
		String requestType = definition.getRequestType().getType();
		if (!requestType.equals(type))
			return Output.constructAll(Reply.METHOD_NOT_ALLOWED_405_REPLY);
		
		try
		{		
			//handle request
			String output = null;
			Handler request = definition.getHandler();
			output = request.handle(arguments, headers, post);
			
			//construct
			String header = Header.construct(Reply.OKAY_200_REPLY, definition.getContentType(), output.length());
			String page = (header + output);
		
			return page;
		}
		catch (Exception ex)
		{
			//notify
			this.server.notifyErrorListeners(ex);
			
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
