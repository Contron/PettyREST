package com.connorhaigh.pettyrest.listener;

//imports
import com.connorhaigh.pettyrest.core.Transaction;

public interface TransactionListener
{
	/**
	 * Called when a new transaction is created.
	 * @param transaction The new transaction.
	 */
	public abstract void transactionCreated(Transaction transaction);
}
