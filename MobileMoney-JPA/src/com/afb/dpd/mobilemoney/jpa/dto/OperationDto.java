package com.afb.dpd.mobilemoney.jpa.dto;

import java.io.Serializable;

public class OperationDto implements Serializable{
	
	private String matricule;
    private String comptes;
    private String typeOperations;
    private String agences;
    private String debut;
    private String fin;
    private String devise;
    private String uti;
    private String sens;
    private Integer taille;
    
    public OperationDto() {
		super();
		// TODO Auto-generated constructor stub
	}



	/**
	 * @param matricule
	 * @param comptes
	 * @param typeOperations
	 * @param agences
	 * @param debut
	 * @param fin
	 * @param devise
	 * @param uti
	 * @param sens
	 */
	public OperationDto(String matricule, String comptes, String typeOperations, String agences, String debut, String fin,
			String devise, String uti, String sens) {
		super();
		this.matricule = matricule;
		this.comptes = comptes;
		this.typeOperations = typeOperations;
		this.agences = agences;
		this.debut = debut;
		this.fin = fin;
		this.devise = devise;
		this.uti = uti;
		this.sens = sens;
	}



	/**
	 * @return the matricule
	 */
	public String getMatricule() {
		return matricule;
	}



	/**
	 * @param matricule the matricule to set
	 */
	public void setMatricule(String matricule) {
		this.matricule = matricule;
	}



	/**
	 * @return the comptes
	 */
	public String getComptes() {
		return comptes;
	}



	/**
	 * @param comptes the comptes to set
	 */
	public void setComptes(String comptes) {
		this.comptes = comptes;
	}



	/**
	 * @return the typeOperations
	 */
	public String getTypeOperations() {
		return typeOperations;
	}



	/**
	 * @param typeOperations the typeOperations to set
	 */
	public void setTypeOperations(String typeOperations) {
		this.typeOperations = typeOperations;
	}



	/**
	 * @return the agences
	 */
	public String getAgences() {
		return agences;
	}



	/**
	 * @param agences the agences to set
	 */
	public void setAgences(String agences) {
		this.agences = agences;
	}



	/**
	 * @return the debut
	 */
	public String getDebut() {
		return debut;
	}



	/**
	 * @param debut the debut to set
	 */
	public void setDebut(String debut) {
		this.debut = debut;
	}



	/**
	 * @return the fin
	 */
	public String getFin() {
		return fin;
	}



	/**
	 * @param fin the fin to set
	 */
	public void setFin(String fin) {
		this.fin = fin;
	}



	/**
	 * @return the devise
	 */
	public String getDevise() {
		return devise;
	}



	/**
	 * @param devise the devise to set
	 */
	public void setDevise(String devise) {
		this.devise = devise;
	}



	/**
	 * @return the uti
	 */
	public String getUti() {
		return uti;
	}



	/**
	 * @param uti the uti to set
	 */
	public void setUti(String uti) {
		this.uti = uti;
	}



	/**
	 * @return the sens
	 */
	public String getSens() {
		return sens;
	}

	/**
	 * @param sens the sens to set
	 */
	public void setSens(String sens) {
		this.sens = sens;
	}

	/**
	 * @return the taille
	 */
	public Integer getTaille() {
		return taille;
	}



	/**
	 * @param taille the taille to set
	 */
	public void setTaille(Integer taille) {
		this.taille = taille;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OperationDto [matricule=" + matricule + ", comptes=" + comptes + ", typeOperations=" + typeOperations
				+ ", agences=" + agences + ", debut=" + debut + ", fin=" + fin + ", devise=" + devise + ", uti=" + uti
				+ ", sens=" + sens + ", taille=" + taille + "]";
	}
	
	
}
