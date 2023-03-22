package com.afb.dpd.mobilemoney.jpa.enums;

import java.util.ArrayList;
import java.util.List;

public enum StatusAbon {
	
	AUCUN("Aucun"),
	ENATTENTE("En attente"),
	FACTURE("Facturé"),
	NONFACTURE("Non facturé"),
	RESILIE("Résilié");


	/**
	 * Valeur de la constante (Code)
	 */
	private final String value;

	/**
	 * Liste des Types d'entrées
	 */
	private static final List<StatusAbon> status = new ArrayList<StatusAbon>();


	static {

		status.add(AUCUN);
		status.add(ENATTENTE);
		status.add(FACTURE);
		status.add(NONFACTURE);
		status.add(RESILIE);
	}


	/**
	 * Constructeur avec initialisation des parametres
	 * @param value	Valeur du type d'entrées
	 */
	private StatusAbon(String value) {

		// Initialisation de la valeur
		this.value = value;
	}

	/**
	 * Obtention de la valeur 
	 * @return	Valeur
	 */
	public String value() {
		return value;
	}

	/**
	 * Obtention de la valeur 
	 * @return	Valeur
	 */
	public String getValue() {
		return value;
	}
	/**
	 * Methode d'obtention des Types d'entrées
	 * @return	Types d'entéres
	 */
	public static List<StatusAbon> getValues () {

		// On retourne la liste
		return status;

	}
}
