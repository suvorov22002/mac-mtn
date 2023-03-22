/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.dto;


/**
 * @author AFB
 *
 */

public class Unlink {
	
	/**
	 * Mandatory
	 * The unlink status
	 */
	private Boolean valid;
		
	/**
	 * Mandatory
	 * The error
	 */
	private ErrorResponse error;
	
		
	/**
	 * 
	 */
	public Unlink() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param valid
	 * @param error
	 */
	public Unlink(Boolean valid, ErrorResponse error) {
		super();
		this.valid = valid;
		this.error = error;
	}


	/**
	 * @return the valid
	 */
	public Boolean getValid() {
		return valid;
	}


	/**
	 * @param valid the valid to set
	 */
	public void setValid(Boolean valid) {
		this.valid = valid;
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
		return "Link [valid=" + valid + ", error=" + error + "]";
	}
	

}
