package com.afb.dpd.mobilemoney.jpa.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResponseBase implements Serializable {
	
	@JsonIgnore
	private static final long serialVersionUID = 1L;
	
	private String code; 
	private String error;
	private String message;
	
	public ResponseBase() {
		
	}
	
	public ResponseBase(String code, String error, String message) {
		this.code = code;
		this.error = error;
		this.message = message;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
}
