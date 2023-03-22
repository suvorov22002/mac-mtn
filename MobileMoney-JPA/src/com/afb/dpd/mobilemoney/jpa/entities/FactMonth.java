/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

/**
 * FactMonth 
 * @author alex_jaza
 * @version 1.0
 */
@Entity(name = "MOMOFactMonth")
@Table(name = "MOMO_FactMonth")
public class FactMonth implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID")
	@SequenceGenerator(name="Seq_MOMOFactMonth", sequenceName="SEQ_MOMOFactMonth", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_MOMOFactMonth")
	private Long id;

	@Column
	@Temporal(TemporalType.DATE)
	private Date dateComtable;
	
	@Column
	private String sensD;
	
	@Column
	private String dev;
	
	@Column
	private String sensC;
	
	@Column
	private Double mntD;
	
	@Column
	private Double mntC;
	
	@Column
	private Integer nbrD;
	
	@Column
	private Integer nbrC;
	
	@Column
	private String uti;
	
	@Column
	@Temporal(TemporalType.DATE)
	private Date dateTrai;
		
	@Column
	private String mois;
	
	@OneToMany(mappedBy = "parent")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<FactMonthDetails> details = new ArrayList<FactMonthDetails>();
	
	/**
	 * 
	 */
	public FactMonth() {
		super();
	}
		
	
	/**
	 * @param dateComtable
	 * @param sensD
	 * @param sensC
	 * @param mntD
	 * @param mntC
	 * @param nbrD
	 * @param nbrC
	 * @param uti
	 * @param dateTrai
	 * @param mois
	 */
	public FactMonth(Date dateComtable, String sensD, String sensC,
			Double mntD, Double mntC, Integer nbrD, Integer nbrC, String uti,
			Date dateTrai, String mois) {
		super();
		this.dateComtable = dateComtable;
		this.sensD = sensD;
		this.sensC = sensC;
		this.mntD = mntD;
		this.mntC = mntC;
		this.nbrD = nbrD;
		this.nbrC = nbrC;
		this.uti = uti;
		this.dateTrai = dateTrai;
		this.mois = mois;
	}





	public String getDev() {
		return dev;
	}


	public void setDev(String dev) {
		this.dev = dev;
	}


	/**
	 * @return the details
	 */
	public List<FactMonthDetails> getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(List<FactMonthDetails> details) {
		this.details = details;
		for(FactMonthDetails det: this.details){
			det.setParent(this);
		}
	}

	
	
	/**
	 * @return the nbrD
	 */
	public Integer getNbrD() {
		return nbrD;
	}

	/**
	 * @param nbrD the nbrD to set
	 */
	public void setNbrD(Integer nbrD) {
		this.nbrD = nbrD;
	}

	/**
	 * @return the nbrC
	 */
	public Integer getNbrC() {
		return nbrC;
	}

	/**
	 * @param nbrC the nbrC to set
	 */
	public void setNbrC(Integer nbrC) {
		this.nbrC = nbrC;
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
	 * @return the dateTrai
	 */
	public Date getDateTrai() {
		return dateTrai;
	}

	/**
	 * @param dateTrai the dateTrai to set
	 */
	public void setDateTrai(Date dateTrai) {
		this.dateTrai = dateTrai;
	}

	/**
	 * @return the mois
	 */
	public String getMois() {
		return mois;
	}

	/**
	 * @param mois the mois to set
	 */
	public void setMois(String mois) {
		this.mois = mois;
	}

	/**
	 * @return the dateComtable
	 */
	public Date getDateComtable() {
		return dateComtable;
	}

	/**
	 * @param dateComtable the dateComtable to set
	 */
	public void setDateComtable(Date dateComtable) {
		this.dateComtable = dateComtable;
	}

	/**
	 * @return the sensD
	 */
	public String getSensD() {
		return sensD;
	}

	/**
	 * @param sensD the sensD to set
	 */
	public void setSensD(String sensD) {
		this.sensD = sensD;
	}

	/**
	 * @return the sensC
	 */
	public String getSensC() {
		return sensC;
	}

	/**
	 * @param sensC the sensC to set
	 */
	public void setSensC(String sensC) {
		this.sensC = sensC;
	}

	/**
	 * @return the mntD
	 */
	public Double getMntD() {
		return mntD;
	}

	/**
	 * @param mntD the mntD to set
	 */
	public void setMntD(Double mntD) {
		this.mntD = mntD;
	}

	/**
	 * @return the mntC
	 */
	public Double getMntC() {
		return mntC;
	}

	/**
	 * @param mntC the mntC to set
	 */
	public void setMntC(Double mntC) {
		this.mntC = mntC;
	}
	
}
