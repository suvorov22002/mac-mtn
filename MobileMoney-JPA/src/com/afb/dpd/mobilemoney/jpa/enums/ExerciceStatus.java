package com.afb.dpd.mobilemoney.jpa.enums;

import java.util.ArrayList;
import java.util.List;


/**
 * Enumeration des statuts d un exercice
 * @author Francis konchou
 * @version 1.0
 */
public enum ExerciceStatus {
	
	/**
	 * En Attente
	 */
	WAITING("ExerciceStatus.Waiting"),
	
	/**
	 * Cloture
	 */
	FENCED("ExerciceStatus.Fenced"),
	
	/**
	 * Courant
	 */
	CURRENT("ExerciceStatus.Current");
	
	/**
	 * Valeur
	 */
	private final String value;
	
	
	/**
	 * Constructeur avec initialisation des parametres
	 * @param value	Valeur
	 */
	private ExerciceStatus(String value){
		// Initialisation de la valeur
		this.value = value;
	}

	/**
	 * Obtention de la valeur 
	 * @return	Valeur
	 */
	public String getValue() {
        return value;
    }
	
	/**
	 * Retourne la liste des valeurs
	 * @return	values
	 */
	public static List<ExerciceStatus> getValues(){
		// Initialisationd de la liste des valeurs
		List<ExerciceStatus> values = new ArrayList<ExerciceStatus>();
		values.add(WAITING);
		values.add(FENCED);
		values.add(CURRENT);
		return values;
	}
	
}
