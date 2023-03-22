/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.Hibernate;

import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

import com.afb.dpd.mobilemoney.jpa.enums.Periodicite;
import com.afb.dpd.mobilemoney.jpa.enums.TransactionStatus;
import com.afb.dpd.mobilemoney.jpa.tools.bkeve;

/**
 * Classe de comptabilisation des abonnements Pull/Push
 * @author Francis DJIOMOU
 * @version 1.0
 */
@Entity
@Table(name = "MOMO_TFJO")
public class Comptabilisation implements Serializable {

	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor
	 */
	public Comptabilisation() {
		super();
	}

	/**
	 * Id (auto genere) de l'enregistrement
	 */
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/**
	 * Date de comptabilisation/facturation
	 */
	@Column(name = "DATECOMPTA", nullable = false)
	@Temporal(TemporalType.DATE)
	private Date dateCompta;

	/**
	 * Periodicite de la comptabilisation
	 */
	@Column(name = "PERIODICITE", nullable = false)
	@Enumerated(EnumType.STRING)
	private Periodicite periodicite;

	/**
	 * Etat de la comptabilisation
	 */
	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private TransactionStatus status = TransactionStatus.PROCESSING;

	/**
	 * Abonne
	 */
	// Change fetch = FetchType.EAGER
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBS_ID", nullable = false)
	private Subscriber subscriber;
	
	/**
	 * Evenement DELTA
	 */
	@ManyToOne(cascade = CascadeType.ALL , fetch = FetchType.EAGER)
	@JoinColumn(name = "EVE_ID", nullable = false)
	private bkeve eve;
	
	/**
	 * Date de traitement
	 */
	@Column(name = "DATE_TRAITEMENT", nullable = true)
	@Temporal(TemporalType.DATE)
	private Date dateTraitement;
	
	@Transient
	private boolean selected = true;
	
	/**
	 * @param id
	 * @param dateCompta
	 * @param subscriber
	 * @param periodicite
	 * @param eve
	 */
	public Comptabilisation(Long id, Date dateCompta, Date dateTraitement, Subscriber subscriber,
			Periodicite periodicite, bkeve eve) {
		super();
		this.id = id;
		this.dateCompta = dateCompta;
		this.dateTraitement = dateTraitement;
		this.subscriber = subscriber;
		this.periodicite = periodicite;
		this.eve = eve;
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
	 * @return the dateCompta
	 */
	public Date getDateCompta() {
		return dateCompta;
	}

	/**
	 * @param dateCompta the dateCompta to set
	 */
	public void setDateCompta(Date dateCompta) {
		this.dateCompta = dateCompta;
	}
	

	public Date getDateTraitement() {
		return dateTraitement;
	}

	public void setDateTraitement(Date dateTraitement) {
		this.dateTraitement = dateTraitement;
	}
	

	/**
	 * @return the subscriber
	 */
	public Subscriber getSubscriber() {
		return subscriber;
	}

	/**
	 * @param subscriber the subscriber to set
	 */
	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	/**
	 * @return the periodicite
	 */
	public Periodicite getPeriodicite() {
		return periodicite;
	}

	/**
	 * @param periodicite the periodicite to set
	 */
	public void setPeriodicite(Periodicite periodicite) {
		this.periodicite = periodicite;
	}

	/**
	 * @return the eve
	 */
	public bkeve getEve() {
		return eve;
	}

	/**
	 * @param eve the eve to set
	 */
	public void setEve(bkeve eve) {
		this.eve = eve;
	}

	/**
	 * @return the status
	 */
	public TransactionStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isPosted(){
		return this.status.equals(TransactionStatus.SUCCESS);
	}
	
	public String getFormattedDate(){
		return PortalHelper.DEFAULT_DATE_FORMAT.format(dateCompta);
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Comptabilisation [id=" + id + ", dateCompta=" + dateCompta + ", dateTraitement=" + dateTraitement
				+ ", subscriber=" + subscriber + ", periodicite=" + periodicite + ", eve=" + eve + ", status=" + status
				+ ", selected=" + selected + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dateCompta == null) ? 0 : dateCompta.hashCode());
		result = prime * result
				+ ((subscriber == null) ? 0 : subscriber.hashCode());
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
		Comptabilisation other = (Comptabilisation) obj;
		if (dateCompta == null) {
			if (other.dateCompta != null)
				return false;
		} else if (!dateCompta.equals(other.dateCompta))
			return false;
		if (subscriber == null) {
			if (other.subscriber != null)
				return false;
		} else if (!subscriber.equals(other.subscriber))
			return false;
		return true;
	}
	
}
