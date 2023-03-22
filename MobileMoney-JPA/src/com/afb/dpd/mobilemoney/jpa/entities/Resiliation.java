package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Classe representant une resiliation dans le CBS
 * @author 
 * @version 1.0
 */
@Entity
@Table(name = "MoMo_resil")
public class Resiliation implements Serializable {

	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default Constructor
	 */
	public Resiliation() {}

	/**
	 * Id auto genere
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "client")
	private String customerId;
    	
	@Column(name = "date_resi")
	private Date dateResiliation = new Date();
	
	@Column(name = "type_resil")
	private String typeResiliation;
	
	@Column(name = "uti")
	private String uti;
	


	/**
	 * @param id
	 * @param customerId
	 * @param dateResiliation
	 * @param typeResiliation
	 * @param uti
	 */
	public Resiliation(Long id, String customerId, Date dateResiliation, String typeResiliation, String uti) {
		super();
		this.id = id;
		this.customerId = customerId;
		this.dateResiliation = dateResiliation;
		this.typeResiliation = typeResiliation;
		this.uti = uti;
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
	 * @return the client
	 */
	public String getUti() {
		return uti;
	}


	/**
	 * @param client the client to set
	 */
	public void setUti(String uti) {
		this.uti = uti;
	}


	/**
	 * @return the submsisdn
	 */
	public String getCustomerId() {
		return customerId;
	}


	/**
	 * @param submsisdn the submsisdn to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}


	/**
	 * @return the dateResiliation
	 */
	public Date getDateResiliation() {
		return dateResiliation;
	}


	/**
	 * @param dateResiliation the dateResiliation to set
	 */
	public void setDateResiliation(Date dateResiliation) {
		this.dateResiliation = dateResiliation;
	}

	/**
	 * @return the typeResiliation
	 */
	public String getTypeResiliation() {
		return typeResiliation;
	}

	/**
	 * @param typeResiliation the typeResiliation to set
	 */
	public void setTypeResiliation(String typeResiliation) {
		this.typeResiliation = typeResiliation;
	}
	
}

