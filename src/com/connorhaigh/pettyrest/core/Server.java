package com.connorhaigh.pettyrest.core;

//imports
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import com.connorhaigh.pettyrest.exception.RESTException;

public class Server implements Runnable
{
	/**
	 * Create a new PettyREST server.
	 * @param port The initial port.
	 */
	public Server(int port)
	{
		//init
		this.port = port;
		
		this.running = false;
		this.runningThread = null;
		this.serverSocket = null;
		
		this.definitionMap = new HashMap<String, Definition>();
	}
	
	/**
	 * Run the server thread.
	 */
	@Override
	public void run()
	{
		//while running
		while (this.running)
		{
			try
			{
				//wait for socket
				Socket socket = this.serverSocket.accept();
				
				//create transaction and process
				Transaction transaction = new Transaction(Server.this, socket);
				transaction.start();
			} catch (Exception ex) {
				//error
			}
		}
	}

	/**
	 * Start the REST server, opening it on the specified port and listening for definitions.
	 * @throws RESTException If the server is already running.
	 * @throws IOException If the server socket could not be created.
	 */
	public void start() throws RESTException, IOException
	{
		//check if running
		if (this.running)
			throw new RESTException("Server already started");
		
		//create socket
		this.serverSocket = new ServerSocket(this.port);
		
		//start thread
		this.running = true;
		this.runningThread = new Thread(this);
		this.runningThread.setName("Server Thread");
		this.runningThread.start();
	}
	
	/**
	 * Stop the REST server, closing the server socket before waiting for it to completely stop.
	 * @throws RESTException If the server is not running.
	 * @throws IOException If the server socket could not be closed.
	 * @throws InterruptedException If the thread was interrupted whilst waiting for it to die.
	 */
	public void stop() throws RESTException, IOException, InterruptedException
	{
		//check if not running
		if (!this.running)
			throw new RESTException("Server already stopped");
		
		//close socket
		this.serverSocket.close();
		
		//stop thread
		this.running = false;
		this.runningThread.join();
	}
	
	
	/**
	 * Link an address to a definition handler.
	 * @param address The address to link.
	 * @param definition The definition handler class.
	 */
	public void link(String address, Definition definition)
	{
		this.definitionMap.put(address, definition);
	}
	
	/**
	 * Unlink an address from a definition handler.
	 * @param address The address to unlink.
	 */
	public void unlink(String address)
	{
		this.definitionMap.remove(address);
	}
	
	/**
	 * Returns if this server has an appropriate link for the specified address.
	 * @param address The address to find.
	 * @return If this server has an appropriate link.
	 */
	public boolean contains(String address)
	{
		return this.definitionMap.containsKey(address);
	}
	
	/**
	 * Returns the matching definition class mapped to the specified address.
	 * @param address The address to find.
	 * @return The matching definition class.
	 */
	public Definition get(String address)
	{
		return this.definitionMap.get(address);
	}
	
	/**
	 * Set the port for this server.
	 * This has no effect if the server is running.
	 * @param port The new port to set.
	 */
	public void setPort(int port)
	{
		this.port = port;
	}
	
	/**
	 * Returns the port for this server.
	 * @return The port.
	 */
	public int getPort()
	{
		return this.port;
	}
	
	//vars
	private int port;
	
	private boolean running;
	private Thread runningThread;
	private ServerSocket serverSocket;
	
	private HashMap<String, Definition> definitionMap;
}
