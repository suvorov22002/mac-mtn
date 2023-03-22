/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.afb.dpd.mobilemoney.jpa.enums.ModeFacturation;
import com.afb.dpd.mobilemoney.jpa.enums.Periodicite;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jpa.enums.TypeValeurFrais;

/**
 * Classe de parametrage des commissions
 * @author Francis DJIOMOU
 * @version 1.0
 */
@Entity
@Table(name = "MoMo_COMMISSIONS")
public class Commissions implements Serializable {

	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Id auto genere
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID") 
	private Long id;
	
	/**
	 * Type d'operation
	 */
	@Column(name = "TYPE_OPERATION")
	@Enumerated(EnumType.STRING)
	private TypeOperation operation;
	
	/**
	 * Type de valeur
	 */
	@Column(name = "TYPE_VALEUR")
	@Enumerated(EnumType.STRING)
	private TypeValeurFrais typeValeur;
	
	/**
	 * Valeur de la commission
	 */
	@Column(name = "VALEUR")
	private Double valeur = 0d;

	/**
	 * Taux de TVA
	 */
	@Column(name = "TAUX_TVA")
	private Double tauxTVA = 19.25d;
	
	/**
	 * Mode de Facturation du type d'operation
	 */
	@Column(name = "MODE_FACTURATION")
	@Enumerated(EnumType.STRING)
	private ModeFacturation modeFacturation;
	
	/**
	 * Periodicite de facturation
	 */
	@Column(name = "PERIOD_FACTURATION")
	@Enumerated(EnumType.STRING)
	private Periodicite periodFacturation;
	
	/**
	 * Default Constructor
	 */
	public Commissions() {}


	/**
	 * @param operation
	 * @param typeValeur
	 * @param valeur
	 */
	public Commissions(TypeOperation operation, TypeValeurFrais typeValeur, Double valeur, Double tauxtva, ModeFacturation mode, Periodicite periodicite) {
		super();
		this.operation = operation;
		this.typeValeur = typeValeur;
		this.valeur = valeur;
		this.tauxTVA = tauxtva;
		this.modeFacturation = mode;
		this.periodFacturation = periodicite;
	}


	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}


	/**
	 * @return the operation
	 */
	public TypeOperation getOperation() {
		return operation;
	}


	/**
	 * @param operation the operation to set
	 */
	public void setOperation(TypeOperation operation) {
		this.operation = operation;
	}


	/**
	 * @return the typeValeur
	 */
	public TypeValeurFrais getTypeValeur() {
		return typeValeur;
	}


	/**
	 * @param typeValeur the typeValeur to set
	 */
	public void setTypeValeur(TypeValeurFrais typeValeur) {
		this.typeValeur = typeValeur;
	}


	/**
	 * @return the valeur
	 */
	public Double getValeur() {
		return valeur;
	}


	/**
	 * @param valeur the valeur to set
	 */
	public void setValeur(Double valeur) {
		this.valeur = valeur;
	}


	/**
	 * @return the tauxTVA
	 */
	public Double getTauxTVA() {
		return tauxTVA;
	}


	/**
	 * @param tauxTVA the tauxTVA to set
	 */
	public void setTauxTVA(Double tauxTVA) {
		this.tauxTVA = tauxTVA;
	}


	/**
	 * @return the modeFacturation
	 */
	public ModeFacturation getModeFacturation() {
		return modeFacturation;
	}


	/**
	 * @param modeFacturation the modeFacturation to set
	 */
	public void setModeFacturation(ModeFacturation modeFacturation) {
		
		this.modeFacturation = modeFacturation;
		
		if(this.modeFacturation != null && this.modeFacturation.equals(ModeFacturation.PERIODIQUE)){
			
			if(this.typeValeur == null || !this.typeValeur.equals(TypeValeurFrais.FIXE)) this.typeValeur = TypeValeurFrais.FIXE;
			if(this.periodFacturation == null ) this.periodFacturation = Periodicite.MOIS;
			
		} else if(this.modeFacturation != null && this.modeFacturation.equals(ModeFacturation.TRANSACTION)){
			this.periodFacturation = null;
		}
	}


	/**
	 * @return the periodFacturation
	 */
	public Periodicite getPeriodFacturation() {
		return periodFacturation;
	}


	/**
	 * @param periodFacturation the periodFacturation to set
	 */
	public void setPeriodFacturation(Periodicite periodFacturation) {
		this.periodFacturation = periodFacturation;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((operation == null) ? 0 : operation.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Commissions other = (Commissions) obj;
		if (operation != other.operation)
			return false;
		return true;
	}

	public boolean isModeFacturationEnabled(){
		return this.operation.equals(TypeOperation.PULL) || this.operation.equals(TypeOperation.PUSH) ? true : false;
	}

	public boolean isPeriodiciteFacturationEnabled(){
		return this.operation.equals(TypeOperation.PULL) || this.operation.equals(TypeOperation.PUSH) ? true : false;
	}
	
}
