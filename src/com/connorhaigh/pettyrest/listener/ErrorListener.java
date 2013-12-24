package com.connorhaigh.pettyrest.listener;

public interface ErrorListener
{
	/**
	 * Called when the server or a transaction fails.
	 * @param ex The exception.
	 */
	public abstract void error(Exception ex);
}
