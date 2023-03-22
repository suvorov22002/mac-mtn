package com.afb.dpd.mobilemoney.jpa.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Statuts des contrats de souscription
 * @author Francis DJIOMOU
 * @version 1.0
 */
public enum StatutContrat {
	
	/**
	 * Type de valeur Fixe
	 */
	ACTIF("Actif"),
	
	ACTIF_CBS("Actif cbs"),
	
	WAITING("En Attente"),
	
	/**
	 * Type valeur pourcentage
	 */
	SUSPENDU("Suspendu");
	
	/**
	 * Valeur
	 */
	private String value;
	
	/**
	 * Constructeur
	 * @param value
	 */
	private StatutContrat(String value){
		this.setValue(value);
	}
	
	/**
	 * Retourne la liste des valeus
	 * @return liste des status des contrats
	 */
	public static List<StatutContrat> getValues() {
		
		// Initialisation de la collection a retourner
		List<StatutContrat> ops = new ArrayList<StatutContrat>();
		
		// Ajout des valeurs
		ops.add(WAITING);
		ops.add(ACTIF);
		ops.add(SUSPENDU);
		ops.add(ACTIF_CBS);
		
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
