package com.afb.dpd.mobilemoney.jpa.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Type d'operations
 * @author Francis DJIOMOU
 * @version 1.0
 */
public enum ModeFacturation {
	
	/**
	 * Facturation par Transaction
	 */
	TRANSACTION("Par Transaction"),
	
	/**
	 * Facturation Periodique 
	 */
	PERIODIQUE("Periodique");
	
	/**
	 * Valeur
	 */
	private String value;
	
	/**
	 * Constructeur
	 * @param value
	 */
	private ModeFacturation(String value){
		this.setValue(value);
	}
	
	/**
	 * Retourne la liste des valeus
	 * @return liste des modes de facturation
	 */
	public static List<ModeFacturation> getValues() {
		
		// Initialisation de la collection a retourner
		List<ModeFacturation> ops = new ArrayList<ModeFacturation>();
		
		// Ajout des valeurs
		ops.add(TRANSACTION);
		ops.add(PERIODIQUE);
		
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
