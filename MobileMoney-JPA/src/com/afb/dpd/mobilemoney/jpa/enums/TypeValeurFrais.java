package com.afb.dpd.mobilemoney.jpa.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Type de valeurs de commissions
 * @author Francis DJIOMOU
 * @version 1.0
 */
public enum TypeValeurFrais {
	
	/**
	 * Type de valeur Fixe
	 */
	FIXE("Frais Fixe"),
	
	/**
	 * Type valeur pourcentage
	 */
	POURCENTAGE("Taux (%)");
	
	/**
	 * Valeur
	 */
	private String value;
	
	/**
	 * Constructeur
	 * @param value
	 */
	private TypeValeurFrais(String value){
		this.setValue(value);
	}
	
	/**
	 * Retourne la liste des valeus
	 * @return liste des types de valeurs de frais
	 */
	public static List<TypeValeurFrais> getValues() {
		
		// Initialisation de la collection a retourner
		List<TypeValeurFrais> ops = new ArrayList<TypeValeurFrais>();
		
		// Ajout des valeurs
		ops.add(FIXE);
		ops.add(POURCENTAGE);
		
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
