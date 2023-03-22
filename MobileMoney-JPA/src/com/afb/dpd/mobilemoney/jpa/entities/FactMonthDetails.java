/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * FactMonthDetails
 * @author alex_jaza
 * @version 1.0
 */
@Entity(name = "MOMOFactMonthDetails")
@Table(name = "MOMO_FactMonthDetails")
public class FactMonthDetails implements Serializable , Comparable<FactMonthDetails>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID")
	@SequenceGenerator(name="Seq_MOMOFactMonthDetails", sequenceName="SEQ_MOMOFactMonthDetails", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_MOMOFactMonthDetails")
	private Long id;
	
	@Column
	private Integer age;
	
	@Column
	private String dev;
	
	@Column
	private String txtage;
	
	@Column
	private String libage;
	
	@Column
	private String compte;
	
	@Column
	private String intitule;
	
	@Column
	private String libelle;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date  dateAbon;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date dateDernfact;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date dateDebutFact;
	
	@Column
	private String sens;
	
	@Column
	private Double mnt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private FactMonth parent;
	
	/**
	 * 
	 */
	public FactMonthDetails() {
		super();
	}

	

	/**
	 * @param age
	 * @param compte
	 * @param intitule
	 * @param libelle
	 * @param dateAbon
	 * @param dateDernfact
	 * @param dateDebutFact
	 * @param sens
	 * @param mnt
	 */
	public FactMonthDetails(Integer age, String compte, String intitule,
			String libelle, Date dateAbon, Date dateDernfact,
			Date dateDebutFact, String sens, Double mnt) {
		super();
		this.age = age;
		this.compte = compte;
		this.intitule = intitule;
		this.libelle = libelle;
		this.dateAbon = dateAbon;
		this.dateDernfact = dateDernfact;
		this.dateDebutFact = dateDebutFact;
		this.sens = sens;
		this.mnt = mnt;
	}



	/**
	 * @return the txtage
	 */
	public String getTxtage() {
		return txtage;
	}



	public String getDev() {
		return dev;
	}



	public void setDev(String dev) {
		this.dev = dev;
	}



	/**
	 * @param txtage the txtage to set
	 */
	public void setTxtage(String txtage) {
		this.txtage = txtage;
	}



	/**
	 * @return the libage
	 */
	public String getLibage() {
		return libage;
	}



	/**
	 * @param libage the libage to set
	 */
	public void setLibage(String libage) {
		this.libage = libage;
	}



	/**
	 * @return the parent
	 */
	public FactMonth getParent() {
		return parent;
	}



	/**
	 * @param parent the parent to set
	 */
	public void setParent(FactMonth parent) {
		this.parent = parent;
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
	 * @return the compte
	 */
	public String getCompte() {
		return compte;
	}

	/**
	 * @param compte the compte to set
	 */
	public void setCompte(String compte) {
		this.compte = compte;
	}

	/**
	 * @return the intitule
	 */
	public String getIntitule() {
		return intitule;
	}

	/**
	 * @param intitule the intitule to set
	 */
	public void setIntitule(String intitule) {
		this.intitule = intitule;
	}

	/**
	 * @return the libelle
	 */
	public String getLibelle() {
		return libelle;
	}

	/**
	 * @param libelle the libelle to set
	 */
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	/**
	 * @return the dateAbon
	 */
	public Date getDateAbon() {
		return dateAbon;
	}

	/**
	 * @param dateAbon the dateAbon to set
	 */
	public void setDateAbon(Date dateAbon) {
		this.dateAbon = dateAbon;
	}

	/**
	 * @return the dateDernfact
	 */
	public Date getDateDernfact() {
		return dateDernfact;
	}

	/**
	 * @param dateDernfact the dateDernfact to set
	 */
	public void setDateDernfact(Date dateDernfact) {
		this.dateDernfact = dateDernfact;
	}

	/**
	 * @return the dateDebutFact
	 */
	public Date getDateDebutFact() {
		return dateDebutFact;
	}

	/**
	 * @param dateDebutFact the dateDebutFact to set
	 */
	public void setDateDebutFact(Date dateDebutFact) {
		this.dateDebutFact = dateDebutFact;
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
	 * @return the mnt
	 */
	public Double getMnt() {
		return mnt;
	}

	/**
	 * @param mnt the mnt to set
	 */
	public void setMnt(Double mnt) {
		this.mnt = mnt;
	}

	/**
	 * @return the age
	 */
	public Integer getAge() {
		return age;
	}

	/**
	 * @param age the age to set
	 */
	public void setAge(Integer age) {
		this.age = age;
	}
	

	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((age == null) ? 0 : age.hashCode());
		result = prime * result + ((compte == null) ? 0 : compte.hashCode());
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
		FactMonthDetails other = (FactMonthDetails) obj;
		if (age == null) {
			if (other.age != null)
				return false;
		} else if (!age.equals(other.age))
			return false;
		if (compte == null) {
			if (other.compte != null)
				return false;
		} else if (!compte.equals(other.compte))
			return false;
		return true;
	}



	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(FactMonthDetails arg0) {
		return this.age.compareTo(arg0.getAge()); 
	}
		

}
