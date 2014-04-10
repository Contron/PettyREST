package com.connorhaigh.pettyrest.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import com.connorhaigh.pettyrest.exceptions.RestException;
import com.connorhaigh.pettyrest.listeners.ErrorListener;
import com.connorhaigh.pettyrest.listeners.TransactionListener;

public class Server implements Runnable
{
	/**
	 * Create a new PettyREST server.
	 * @param port the initial port
	 */
	public Server(int port)
	{
		this.port = port;
		
		this.maxArguments = 8;
		this.maxHeaders = 32;
		this.maxPost = 16;
		
		this.running = false;
		this.runningThread = null;
		this.serverSocket = null;
		
		this.definitionMap = new HashMap<String, Definition>();
		
		this.transactionListeners = new ArrayList<TransactionListener>();
		this.errorListeners = new ArrayList<ErrorListener>();
	}
	
	/**
	 * Run the server thread.
	 */
	@Override
	public void run()
	{
		while (this.running)
		{
			try
			{
				//wait for socket
				Socket socket = this.serverSocket.accept();
				socket.setSoTimeout(10000);
				socket.setKeepAlive(false);
				
				//create transaction and process
				Transaction transaction = new Transaction(Server.this, socket);
				transaction.start();
				
				//notify
				this.notifyTransactionListeners(transaction);
			}
			catch (Exception ex)
			{
				//notify
				this.notifyErrorListeners(ex);
			}
		}
	}

	/**
	 * Start the REST server, opening it on the specified port and listening for definitions.
	 * @throws RestException if the server is already running
	 * @throws IOException if the server socket could not be created
	 */
	public void start() throws RestException, IOException
	{
		//check if running
		if (this.running)
			throw new RestException("Server already started");
		
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
	 * @throws RestException if the server is not running
	 * @throws IOException if the server socket could not be closed
	 * @throws InterruptedException if the thread was interrupted whilst waiting for it to die
	 */
	public void stop() throws RestException, IOException, InterruptedException
	{
		//check if not running
		if (!this.running)
			throw new RestException("Server already stopped");
		
		//close socket
		this.serverSocket.close();
		
		//stop thread
		this.running = false;
		this.runningThread.join();
	}
	
	
	/**
	 * Link an address to a definition handler.
	 * @param address the address to link
	 * @param definition the definition handler class
	 */
	public void link(String address, Definition definition)
	{
		this.definitionMap.put(address, definition);
	}
	
	/**
	 * Unlink an address from a definition handler.
	 * @param address the address to unlink
	 */
	public void unlink(String address)
	{
		this.definitionMap.remove(address);
	}
	
	/**
	 * Returns if this server has an appropriate link for the specified address.
	 * @param address the address to find
	 * @return if this server has an appropriate link
	 */
	public boolean contains(String address)
	{
		return this.definitionMap.containsKey(address);
	}
	
	/**
	 * Returns the matching definition class mapped to the specified address.
	 * @param address the address to find
	 * @return the matching definition class
	 */
	public Definition get(String address)
	{
		return this.definitionMap.get(address);
	}
	
	/**
	 * Sets the port for this server.
	 * This has no effect if the server is running.
	 * @param port the new port to set
	 */
	public void setPort(int port)
	{
		this.port = port;
	}
	
	/**
	 * Returns the port for this server.
	 * @return the port
	 */
	public int getPort()
	{
		return this.port;
	}
	
	/**
	 * Sets the maximum number of arguments allowed by this server.
	 * @param maxArguments the new maximum number of arguments
	 */
	public void setMaxArguments(int maxArguments)
	{
		this.maxArguments = maxArguments;
	}
	
	/**
	 * Returns the maximum number of arguments allowed by this server.
	 * @return the maximum number of arguments
	 */
	public int getMaxArguments()
	{
		return this.maxArguments;
	}
	
	/**
	 * Sets the maximum number of header fields allowed by this server.
	 * @param maxHeaders the new maximum number of header fields
	 */
	public void setMaxHeaders(int maxHeaders)
	{
		this.maxHeaders = maxHeaders;
	}
	
	/**
	 * Returns the maximum number of header fields allowed by this server.
	 * @return the maximum number of header fields 
	 */
	public int getMaxHeaders()
	{
		return this.maxHeaders;
	}
	
	/**
	 * Sets the maximum number of POST arguments allowed by this server.
	 * @param maxPost the new maximum number of POST arguments
	 */
	public void setMaxPost(int maxPost)
	{
		this.maxPost = maxPost;
	}
	
	/**
	 * Returns the maximum number of POST arguments allowed by this server.
	 * @return the maximum number of POST arguments
	 */
	public int getMaxPost()
	{
		return this.maxPost;
	}
	
	/**
	 * Add a transaction listener to this server to be notified when new transactions are created.
	 * @param transactionListener the transaction listener to add
	 */
	public void addTransactionListener(TransactionListener transactionListener)
	{
		this.transactionListeners.add(transactionListener);
	}
	
	/**
	 * Remove a transaction listener from this server from be notified when new transactions are created.
	 * @param transactionListener the transaction listener from remove
	 */
	public void removeTransactionListener(TransactionListener transactionListener)
	{
		this.transactionListeners.remove(transactionListener);
	}
	
	/**
	 * Add an error listener to this server to be notified when an error occurs.
	 * @param errorListener the error listener to add
	 */
	public void addErrorListener(ErrorListener errorListener)
	{
		this.errorListeners.add(errorListener);
	}
	
	/**
	 * Remove an error listener from this server from be notified when an error occurs.
	 * @param errorListener the error listener from remove
	 */
	public void removeErrorListener(ErrorListener errorListener)
	{
		this.errorListeners.remove(errorListener);
	}
	
	/**
	 * Notify all registered transaction listeners of a new transaction.
	 * @param transaction the new transaction
	 */
	protected void notifyTransactionListeners(Transaction transaction)
	{
		//loop
		for (TransactionListener transactionListener : this.transactionListeners)
			transactionListener.transactionCreated(transaction);
	}
	
	/**
	 * Notify all registered error listeners of a new error.
	 * @param exception the new exception
	 */
	protected void notifyErrorListeners(Exception exception)
	{
		//loop
		for (ErrorListener errorListener : this.errorListeners)
			errorListener.error(exception);
	}
	
	private int port;
	
	private int maxArguments;
	private int maxHeaders;
	private int maxPost;
	
	private boolean running;
	private Thread runningThread;
	private ServerSocket serverSocket;
	
	private HashMap<String, Definition> definitionMap;
	
	private ArrayList<TransactionListener> transactionListeners;
	private ArrayList<ErrorListener> errorListeners;
}
