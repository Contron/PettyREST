package com.connorhaigh.pettyrest.listeners;

public interface ErrorListener
{
	/**
	 * Called when the server or a transaction fails.
	 * @param ex the exception
	 */
	public abstract void error(Exception ex);
}
