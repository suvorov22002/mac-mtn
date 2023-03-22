package com.afb.dpd.mobilemoney.jpa.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Type d'operations
 * @author Alex JAZA
 * @version 1.0
 */
public enum MTNTransactionStatus {
	
	/**
	 * Transaction en cours de traitement
	 */
	PENDING("Pending transaction"),
	
	/**
	 * Echec de la transaction suite a une erreur
	 */
	FAILED("Failed transaction"),
	
	/**
	 * Transaction correctement executee de bout en bout
	 */
	SUCCESSFUL("Successful transaction");
	
	/**
	 * Valeur
	 */
	private String value;
	
	/**
	 * Constructeur
	 * @param value
	 */
	private MTNTransactionStatus(String value){
		this.setValue(value);
	}
	
	/**
	 * Retourne la liste des valeus
	 * @return liste des status des transactions
	 */
	public static List<MTNTransactionStatus> getValues() {
		
		// Initialisation de la collection a retourner
		List<MTNTransactionStatus> ops = new ArrayList<MTNTransactionStatus>();
		
		// Ajout des valeurs
		ops.add(PENDING);
		ops.add(FAILED);
		ops.add(SUCCESSFUL);
		
		// Retourne la collection
		return ops;
		
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
