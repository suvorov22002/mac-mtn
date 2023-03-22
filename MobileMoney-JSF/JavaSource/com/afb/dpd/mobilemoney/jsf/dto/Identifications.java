/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.dto;

import java.util.List;

/**
 * @author AFB
 *
 */

public class Identifications {
	
	/**
	 * Mandatory
	 * The type of identification
	 */
	private Identification identification;
		
	/**
	 * Mandatory
	 * The id
	 */
	private ErrorResponse error;
	
		
	/**
	 * 
	 */
	public Identifications() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param identifications
	 * @param error
	 */
	public Identifications(Identification identification, ErrorResponse error) {
		super();
		this.identification = identification;
		this.error = error;
	}


	/**
	 * @return the identification
	 */
	public Identification getIdentification() {
		return identification;
	}


	/**
	 * @param identification the identification to set
	 */
	public void setIdentifications(Identification identification) {
		this.identification = identification;
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
		return "Identifications [identification=" + identification + ", error=" + error + "]";
	}
	

}
