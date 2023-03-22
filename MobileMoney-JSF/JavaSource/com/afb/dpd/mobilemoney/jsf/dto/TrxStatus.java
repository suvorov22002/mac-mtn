/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.dto;

import java.util.List;

/**
 * @author AFB
 *
 */

public class TrxStatus {
	
	/**
	 * Mandatory
	 * The transaction status
	 */
	private TransactionsStatus transactionstatus;
		
	/**
	 * Mandatory
	 * The id
	 */
	private ErrorResponse error;
	
		
	/**
	 * 
	 */
	public TrxStatus() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param transctionstatus
	 * @param error
	 */
	public TrxStatus(TransactionsStatus transactionstatus, ErrorResponse error) {
		super();
		this.transactionstatus = transactionstatus;
		this.error = error;
	}


	/**
	 * @return the transctionstatus
	 */
	public TransactionsStatus getTransactionstatus() {
		return transactionstatus;
	}


	/**
	 * @param transctionstatus the transctionstatus to set
	 */
	public void setTransactionstatus(TransactionsStatus transactionstatus) {
		this.transactionstatus = transactionstatus;
	}


	/**
	 * @return the error
	 */
	public ErrorResponse getError() {
		return error;
	}


	/**
	 * @param error the error to set
	 */
	public void setError(ErrorResponse error) {
		this.error = error;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TrxStatus [transactionstatus=" + transactionstatus + ", error=" + error + "]";
	}
	

}
