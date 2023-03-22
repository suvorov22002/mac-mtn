/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.tools;

import java.io.Serializable;

/**
 * Classe representant un Type de compte dans Amplitude
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class TypeCompte implements Serializable {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default COnstructor
	 */
	public TypeCompte() {}

	/**
	 * Code
	 */
	private String code;
	
	/**
	 * Libelle
	 */
	private String nom;

	/**
	 * @param code
	 * @param nom
	 */
	public TypeCompte(String code, String nom) {
		super();
		this.code = code;
		this.nom = nom;
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
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}

	/**
	 * @param nom the nom to set
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	
}
