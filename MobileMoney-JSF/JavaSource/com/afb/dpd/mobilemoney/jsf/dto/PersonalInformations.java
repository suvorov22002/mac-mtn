/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.dto;


/**
 * @author AFB
 *
 */

public class PersonalInformations {
	
	/**
	 * Mandatory
	 * The personal informations
	 */
	private Information information;
		
	/**
	 * Optional
	 * The error response
	 */
	private ErrorResponse error;
	
	
	/**
	 * 
	 */
	public PersonalInformations() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param information
	 * @param error
	 */
	public PersonalInformations(Information information, ErrorResponse error) {
		super();
		this.information = information;
		this.error = error;
	}


	/**
	 * @return the information
	 */
	public Information getInformation() {
		return information;
	}


	/**
	 * @param information the information to set
	 */
	public void setInformation(Information information) {
		this.information = information;
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
		return "PersonalInformations [information=" + information + ", error=" + error + "]";
	}
	

}
