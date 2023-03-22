/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.dto;


/**
 * @author AFB
 *
 */

public class ErrorResponse {
	
	/**
	 * Mandatory
	 * The error code
	 */
	private String errorcode;
		
	/**
	 * Optional
	 * The error name
	 */
	private String errorname;
	
	/**
	 * Optional
	 * The error message
	 */
	private String errormessage;
	
	
	/**
	 * 
	 */
	public ErrorResponse() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param errorcode
	 * @param errorname
	 * @param errormessage
	 */
	public ErrorResponse(String errorcode, String errorname, String errormessage) {
		super();
		this.errorcode = errorcode;
		this.errorname = errorname;
		this.errormessage = errormessage;
	}


	/**
	 * @return the errorcode
	 */
	public String getErrorcode() {
		return errorcode;
	}


	/**
	 * @param errorcode the errorcode to set
	 */
	public void setErrorcode(String errorcode) {
		this.errorcode = errorcode;
	}


	/**
	 * @return the errorname
	 */
	public String getErrorname() {
		return errorname;
	}


	/**
	 * @param errorname the errorname to set
	 */
	public void setErrorname(String errorname) {
		this.errorname = errorname;
	}


	/**
	 * @return the errormessage
	 */
	public String getErrormessage() {
		return errormessage;
	}


	/**
	 * @param errormessage the errormessage to set
	 */
	public void setErrormessage(String errormessage) {
		this.errormessage = errormessage;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ErrorResponse [errorcode=" + errorcode + ", errorname=" + errorname + ", errormessage=" + errormessage
				+ "]";
	}
	

}
