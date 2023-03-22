package com.afb.dpd.mobilemoney.jpa.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Type d'operations
 * @author Francis DJIOMOU
 * @version 1.0
 */
public enum TransactionStatus {
	
	/**
	 * Transaction en cours de traitement
	 */
	WAITING("En Attente"),

	/**
	 * Transaction en cours de traitement
	 */
	PROCESSING("En Cours de Traitement"),

	/**
	 * En Attente de Regularisation
	 */
	REGUL("Attente de Regularisation"),
	
	/**
	 * Echec de la transaction suite a une erreur
	 */
	FAILED("Echec"),

	/**
	 * Transaction annulee
	 */
	CANCEL("Annulée"),
	
	/**
	 * Transaction annulee
	 */
	UNKNOW("Inconnue"),
	
	CLOSE("clotur"),
	
	/**
	 * Transaction correctement executee de bout en bout
	 */
	SUCCESS("Validée");
	
	/**
	 * Valeur
	 */
	private String value;
	
	/**
	 * Constructeur
	 * @param value
	 */
	private TransactionStatus(String value){
		this.setValue(value);
	}
	
	/**
	 * Retourne la liste des valeus
	 * @return liste des status des transactions
	 */
	public static List<TransactionStatus> getValues() {
		
		// Initialisation de la collection a retourner
		List<TransactionStatus> ops = new ArrayList<TransactionStatus>();
		
		// Ajout des valeurs
		ops.add(WAITING);
		ops.add(PROCESSING);
		ops.add(FAILED);
		ops.add(CANCEL);
		ops.add(SUCCESS);
		
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
