package com.connorhaigh.pettyrest.listeners;

import com.connorhaigh.pettyrest.core.Transaction;

public interface TransactionListener
{
	/**
	 * Called when a new transaction is created.
	 * @param transaction the new transaction
	 */
	public abstract void transactionCreated(Transaction transaction);
}
