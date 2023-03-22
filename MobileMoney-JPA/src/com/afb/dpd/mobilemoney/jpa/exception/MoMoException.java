/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.exception;

import com.afb.dpd.mobilemoney.jpa.enums.ExceptionCategory;
import com.afb.dpd.mobilemoney.jpa.enums.ExceptionCode;

/**
 * Classe d'exception du module
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class MoMoException extends Exception {

	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constuctor
	 */
	public MoMoException() {}
	
	/**
	 * Code du message
	 */
	protected ExceptionCode code;
	
	/**
	 * Message de l'exception
	 */
	protected String message;
	
	/**
	 * Categorie de l'exception
	 */
	protected ExceptionCategory category;

	/**
	 * @param code
	 * @param message
	 * @param category
	 */
	public MoMoException(ExceptionCode code, String message, ExceptionCategory category) {
		super();
		this.code = code;
		this.message = message;
		this.category = category;
	}

	/**
	 * @return the code
	 */
	public ExceptionCode getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(ExceptionCode code) {
		this.code = code;
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

	/**
	 * @return the category
	 */
	public ExceptionCategory getCategory() {
		return category;
	}

	/**
	 * @param category the category to set
	 */
	public void setCategory(ExceptionCategory category) {
		this.category = category;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + this.category + "] : " + (this.message != null ? this.message : this.code);
	}
	
}
