/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.dto;


/**
 * @author AFB
 *
 */

public class TransactionsStatus {
	
	/**
	 * Mandatory
	 * The financial transaction id
	 */
	private String financialtransactionid;
		
	/**
	 * Mandatory
	 * The status
	 */
	private String status;
	
		
	/**
	 * 
	 */
	public TransactionsStatus() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param financialtransactionid
	 * @param status
	 */
	public TransactionsStatus(String financialtransactionid, String status) {
		super();
		this.financialtransactionid = financialtransactionid;
		this.status = status;
	}


	/**
	 * @return the financialtransactionid
	 */
	public String getFinancialtransactionid() {
		return financialtransactionid;
	}


	/**
	 * @param financialtransactionid the financialtransactionid to set
	 */
	public void setFinancialtransactionid(String financialtransactionid) {
		this.financialtransactionid = financialtransactionid;
	}


	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransactionStatus [financialtransactionid=" + financialtransactionid + ", status=" + status + "]";
	}

}
