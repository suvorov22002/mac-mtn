package com.afb.dpd.mobilemoney.jpa.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Type d'operations
 * @author Francis DJIOMOU
 * @version 1.0
 */
public enum TypeOperation {
	
	/**
	 * Pull from account
	 */
	PULL("Pull From Account"),
	
	/**
	 * Push from account
	 */
	PUSH("Push To Account"),

	/**
	 * Push from account
	 */
	BALANCE("Balance"),

	/**
	 * Push from account
	 */
	REVERSAL("Reversal"),

	/**
	 * Push from account
	 */
	SUBSCRIPTION("Souscription"),

	/**
	 * Push from account
	 */
	MODIFY("Modification de contrat"),

	/**
	 * Comptabilisation
	 */
	COMPTABILISATION("Comptabilisation"),
	
	/**
	 * Mini statement / Relevé des dernières transactions
	 */
	MINISTATEMENT("Mini-statement"),
	
		
	/**
	 * Annulation de contrat
	 */
	CANCEL("Annulation de contrat");
	
	/**
	 * Valeur
	 */
	private String value;
	
	/**
	 * Constructeur
	 * @param value
	 */
	private TypeOperation(String value){
		this.setValue(value);
	}
	
	/**
	 * Retourne la liste des valeus
	 * @return liste des types d'operations
	 */
	public static List<TypeOperation> getValues() {
		
		// Initialisation de la collection a retourner
		List<TypeOperation> ops = new ArrayList<TypeOperation>();
		
		// Ajout des valeurs
		ops.add(PULL);
		ops.add(PUSH);
		ops.add(SUBSCRIPTION);
		ops.add(MODIFY);
		ops.add(COMPTABILISATION);
		ops.add(MINISTATEMENT);
		ops.add(CANCEL);
		
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
