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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Classe de gestion des parametres d'envoie des alertes mails dans les differentes agences
 * @author Alex JAZA
 * @version 1.0
 */
@Entity
@Table(name = "MoMo_PRMTRS_ALERTE")
public class ParametreAlertes implements Serializable, Cloneable {

	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Id (auto genere) de l'enregistrement
	 */
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/**
	 * Code agence
	 */
	@Column(name = "CODE_AGENCE", nullable = false, unique=true)
	private String codeAgence;
	
	/**
	 * Nom agence
	 */
	@Column(name = "NOM_AGENCE", nullable = false)
	private String nomAgence;

	/**
	 * adresses mails de l'agence
	 */
	// Change fetch = FetchType.EAGER
	@CollectionOfElements(fetch = FetchType.EAGER)
	@JoinTable(
			name = "MoMo_ALERTE_EMAILS",
			joinColumns = {@JoinColumn(name = "AGE_ID")}
	)
	@Column(name = "EMAILS")
	@Fetch(FetchMode.SUBSELECT)
	//@LazyCollection(LazyCollectionOption.FALSE)
	private List<String> emails = new ArrayList<String>();
	
	/**
	 * Date de dernier envois de mails
	 */
	@Column(name = "LAST_SEND_MAIL", nullable = false)
	private Date lastSendMail = new Date();

	@Version
	@Column(columnDefinition = "integer DEFAULT 0", nullable = false)  
	private Long version;
	
		
	/**
	 * Default Constructor
	 */
	public ParametreAlertes() {
		super();
	}

 
	public ParametreAlertes(String codeAgence, String nomAgence, Date lastSendMail) {
		super();
		this.codeAgence = codeAgence;
		this.nomAgence = nomAgence;
		this.lastSendMail = lastSendMail;
	}


	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	

	/**
	 * @return the codeAgence
	 */
	public String getCodeAgence() {
		return codeAgence;
	}


	/**
	 * @param codeAgence the codeAgence to set
	 */
	public void setCodeAgence(String codeAgence) {
		this.codeAgence = codeAgence;
	}


	/**
	 * @return the nomAgence
	 */
	public String getNomAgence() {
		return nomAgence;
	}


	/**
	 * @param nomAgence the nomAgence to set
	 */
	public void setNomAgence(String nomAgence) {
		this.nomAgence = nomAgence;
	}

		
	/**
	 * @return the emails
	 */
	public List<String> getEmails() {
		return emails;
	}


	/**
	 * @param emails the emails to set
	 */
	public void setEmails(List<String> emails) {
		this.emails = emails;
	}
	

	/**
	 * @return the lastSendMail
	 */
	public Date getLastSendMail() {
		return lastSendMail;
	}


	/**
	 * @param lastSendMail the lastSendMail to set
	 */
	public void setLastSendMail(Date lastSendMail) {
		this.lastSendMail = lastSendMail;
	}
	
	
	/**
	 * @return the first email
	 */
	public String getFirstEmail(){
		return this.emails != null && !this.emails.isEmpty() ? this.emails.get(0) : null;
	}
	

	/**
	 * @return the all emails as String
	 */
	public String getAllEmails(){
		if(this.emails != null && !this.emails.isEmpty()){
			String ret = "";
			for(String email : emails){
				ret = StringUtils.isBlank(ret) ? email : ret + ", "+ email;
			}
			return ret;
		}
		
		return null;
	}
	
	/**
	 * @return the version
	 */
	public Long getVersion() {
		return version;
	}
		
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ParametreAlertes [id=" + id + ", codeAgence=" + codeAgence + ", nomAgence=" + nomAgence + ", emails="
				+ emails + ", lastSendMail=" + lastSendMail + "]";
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}
	
}
