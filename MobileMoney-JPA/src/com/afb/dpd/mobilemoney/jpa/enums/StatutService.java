package com.afb.dpd.mobilemoney.jpa.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Statuts des contrats de souscription
 * @author Alex JAZA
 * @version 1.0
 */
public enum StatutService {
	
	/**
	 * Service active
	 */
	ON("Actif"),
	
	/**
	 * Service desactive
	 */
	OFF("Inactif"),
	
	/**
	 * Service en maintenance (seul les numeros de tests configures sont autorises)
	 */
	TEST("Maintenance");
	
	/**
	 * Valeur
	 */
	private String value;
	
	/**
	 * Constructeur
	 * @param value
	 */
	private StatutService(String value){
		this.setValue(value);
	}
	
	/**
	 * Retourne la liste des valeus
	 * @return liste des status des contrats
	 */
	public static List<StatutService> getValues() {
		
		// Initialisation de la collection a retourner
		List<StatutService> ops = new ArrayList<StatutService>();
		
		// Ajout des valeurs
		ops.add(ON);
		ops.add(OFF);
		ops.add(TEST);
		
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
