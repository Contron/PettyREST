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

import com.connorhaigh.pettyrest.exception.RESTException;
import com.connorhaigh.pettyrest.http.Header;
import com.connorhaigh.pettyrest.http.Output;
import com.connorhaigh.pettyrest.http.Reply;

public class Transaction implements Runnable
{
	/**
	 * Create a new transaction between the server and a client.
	 * @param server The owning server.
	 * @param socket The client socket.
	 */
	public Transaction(Server server, Socket socket)
	{
		//init
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
		} catch (Exception ex) {
			//error
			this.server.notifyErrorListeners(ex);
		}
	}
	
	/**
	 * Start processing the request from the client.
	 * @throws RESTException If the transaction is already in progress.
	 * @throws IOException If the wrapper streams could not be created.
	 */
	public void start() throws RESTException, IOException
	{
		//check if running
		if (this.running)
			throw new RESTException("Transaction already in progress");
		
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
	 * @throws RESTException If the transaction is not in progress.
	 * @throws IOException If the streams could not be closed.
	 */
	public void stop() throws RESTException, IOException
	{
		//check if not running
		if (!this.running)
			throw new RESTException("Transaction not in progress");
		
		//wrapper streams
		this.outputStream.flush();
		this.outputStream.close();
		
		//close the input stream after
		this.inputStream.close();
		
		//stop thread
		this.running = false;
	}
	
	/**
	 * Returns the server for this transaction.
	 * @return The server.
	 */
	public Server getServer()
	{
		return this.server;
	}
	
	/**
	 * Returns the client socket for this transaction.
	 * @return The client socket.
	 */
	public Socket getSocket()
	{
		return this.socket;
	}
	
	/**
	 * Handle the request.
	 * @throws IOException If the sockets could not be read or written to.
	 */
	private void handle() throws IOException
	{
		//get request
		String requestLine = this.inputStream.readLine();
		String[] requestParts = requestLine.split(" ");
		
		//get info
		String type = requestParts[0];
		String resource = requestParts[1];
		String[] resourceParts = resource.split("/");
		String page = null;
		
		//convert arguments
		ArrayList<String> args = new ArrayList<String>(Arrays.asList(resourceParts));
		if (args.size() > 0)
			args.remove(0);
		
		//data maps
		HashMap<String, String> headerMap = new HashMap<String, String>();
		HashMap<String, String> postDataMap = new HashMap<String, String>();
		
		//read input headers and post data
		while (this.inputStream.ready())
		{
			//read line
			String line = this.inputStream.readLine();
			if (line.isEmpty())
				break;
			
			//split
			String[] lineData = line.split(":");
			String key = lineData[0].trim();
			String value = lineData[1].trim();
				
			//put
			headerMap.put(key, value);
		}
		
		try
		{
			//check if contains
			if (this.server.contains(resource))
			{
				//get definition
				Definition definition = this.server.get(resource);
				
				//check request type
				String requestType = definition.getRequestType().getType();
				if (type.equals(requestType))
				{
					//output
					StringBuilder output = new StringBuilder();
					
					//create request class and handle
					Class<? extends Request> requestClass = definition.getRequestClass();
					Request request = requestClass.newInstance();
					request.handle(headerMap, postDataMap, args, output);
					
					//output
					page = (Header.construct(Reply.OKAY_200_REPLY, definition.getContentType(), output.length()) + output.toString());
				} else {
					//bad request
					page = Output.constructAll(Reply.BAD_REQUEST_400_REPLY);
				}
			} else {
				//not found
				page = Output.constructAll(Reply.NOT_FOUND_404_REPLY);
			}
		} catch (Exception ex) {
			//internal error
			page = Output.constructAll(Reply.INTERNAL_SERVER_ERROR_500_REPLY);
		}
		
		//output
		this.outputStream.write(page);
	}
	
	//vars
	private Server server;
	private Socket socket;
	
	private BufferedReader inputStream;
	private BufferedWriter outputStream;
	
	private boolean running;
	private Thread runningThread;
}
