package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * PlageTransactions
 * @author Francis K 
 * @version 1.0
 */
@Entity
@Table(name = "MOMO_PLGTRANS")
public class PlageTransactions implements Serializable {

	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static final Long Default = 01l;

	/**
	 * PlageTransactions
	 */
	public PlageTransactions() {}
	
	
	public PlageTransactions(Long id) {
		super();
		this.id = id;
	}
	
	/**
	 * 
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/**
	 * profilName
	 */
	@Column
	private String profilName;
	
	
	/**
	 * Montant max du Pull
	 */
	@Column(name = "commissions")
	private Double commissions = 0d;
	
	
	/**
	 * Montant max du Pull
	 */
	@Column(name = "maxPullAmount")
	private Double maxPullAmount = 0d;
	
	/**
	 * Montant max du Push
	 */
	@Column(name = "maxPushAmount")
	private Double maxPushAmount = 0d;
	

	/**
	 * Montant max du Pull journalier
	 */
	@Column(name = "maxPullAmountDay")
	private Double maxPullAmountDay = 0d;
	
	/**
	 * Montant max du Push journalier
	 */
	@Column(name = "maxPushAmountDay")
	private Double maxPushAmountDay = 0d;
	
	/**
	 * Montant max du Pull hebdo
	 */
	@Column(name = "maxPullAmountWeek")
	private Double maxPullAmountWeek = 0d;
	
	/**
	 * Montant max du Push hebdo
	 */
	@Column(name = "maxPushAmountWeek")
	private Double maxPushAmountWeek = 0d;
	
	/**
	 * Montant max du Pull mensuel
	 */
	@Column(name = "maxPullAmountMonth")
	private Double maxPullAmountMonth = 0d;
	
	/**
	 * Montant max du Push mensuel
	 */
	@Column(name = "maxPushAmountMonth")
	private Double maxPushAmountMonth = 0d;
		
	/**
     * DAP des Operations de Push
     */
    @Column(name = "NCPDAPPUSH")
    private String ncpDAPPush = "00001-02523481001-82";

    /**
     * DAP des operations de Pull
     */
    @Column(name = "NCPDAPPULL")
    private String ncpDAPPull = "00001-02523481001-82";

    /**
     * Numero de compte de MTN
     */
    @Column(name = "ncpMTN")
    private String numCompteMTN = "00001-02523481001-82";

    /**
     * Numero de compte des Commissions
     */
    @Column(name = "numCompteCommissions")
    private String numCompteCommissions = "00001-72900090301-90";

    /**
     * Numero de compte TVA
     */
    @Column(name = "numCompteTVA")
    private String numCompteTVA = "00001-43400090035-17";

    /**
     * Numero de compte de liaison
     */
    @Column(name = "numCompteLiaison")
    private String numCompteLiaison = "45920090100";
    
    /**
     * Activation/Desactivation du Service
     */
    @Column(name = "ACTIF")
    private Boolean active = Boolean.TRUE;

    /**
     * Authorise les transactions pendant les TFJ
     */
    @Column(name = "ALLOW_TRANS_IN_TFJ")
    private Boolean allowTransDuringTFJO = Boolean.TRUE;


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
	 * @return the profilName
	 */
	public String getProfilName() {
		return profilName;
	}

	/**
	 * @param profilName the profilName to set
	 */
	public void setProfilName(String profilName) {
		this.profilName = profilName;
	}

	public Double getCommissions() {
		return commissions;
	}


	public void setCommissions(Double commissions) {
		this.commissions = commissions;
	}


	/**
	 * @return the maxPullAmount
	 */
	public Double getMaxPullAmount() {
		return maxPullAmount;
	}

	/**
	 * @param maxPullAmount the maxPullAmount to set
	 */
	public void setMaxPullAmount(Double maxPullAmount) {
		this.maxPullAmount = maxPullAmount;
	}

	/**
	 * @return the maxPushAmount
	 */
	public Double getMaxPushAmount() {
		return maxPushAmount;
	}

	/**
	 * @param maxPushAmount the maxPushAmount to set
	 */
	public void setMaxPushAmount(Double maxPushAmount) {
		this.maxPushAmount = maxPushAmount;
	}

	
	/**
	 * @return the maxPullAmountDay
	 */
	public Double getMaxPullAmountDay() {
		return null==maxPullAmountDay ? 0d : maxPullAmountDay;
	}

	/**
	 * @param maxPullAmountDay the maxPullAmountDay to set
	 */
	public void setMaxPullAmountDay(Double maxPullAmountDay) {
		this.maxPullAmountDay = maxPullAmountDay;
	}

	/**
	 * @return the maxPushAmountDay
	 */
	public Double getMaxPushAmountDay() {
		return null==maxPushAmountDay ? 0d : maxPushAmountDay;
	}

	/**
	 * @param maxPushAmountDay the maxPushAmountDay to set
	 */
	public void setMaxPushAmountDay(Double maxPushAmountDay) {
		this.maxPushAmountDay = maxPushAmountDay;
	}
		

	/**
	 * @return the maxPullAmountWeek
	 */
	public Double getMaxPullAmountWeek() {
		return null==maxPullAmountWeek ? 0d : maxPullAmountWeek;
	}


	/**
	 * @param maxPullAmountWeek the maxPullAmountWeek to set
	 */
	public void setMaxPullAmountWeek(Double maxPullAmountWeek) {
		this.maxPullAmountWeek = maxPullAmountWeek;
	}


	/**
	 * @return the maxPushAmountWeek
	 */
	public Double getMaxPushAmountWeek() {
		return null==maxPushAmountWeek ? 0d : maxPushAmountWeek;
	}


	/**
	 * @param maxPushAmountWeek the maxPushAmountWeek to set
	 */
	public void setMaxPushAmountWeek(Double maxPushAmountWeek) {
		this.maxPushAmountWeek = maxPushAmountWeek;
	}


	/**
	 * @return the maxPullAmountMonth
	 */
	public Double getMaxPullAmountMonth() {
		return null==maxPullAmountMonth ? 0d : maxPullAmountMonth;
	}


	/**
	 * @param maxPullAmountMonth the maxPullAmountMonth to set
	 */
	public void setMaxPullAmountMonth(Double maxPullAmountMonth) {
		this.maxPullAmountMonth = maxPullAmountMonth;
	}


	/**
	 * @return the maxPushAmountMonth
	 */
	public Double getMaxPushAmountMonth() {
		return null==maxPushAmountMonth ? 0d : maxPushAmountMonth;
	}


	/**
	 * @param maxPushAmountMonth the maxPushAmountMonth to set
	 */
	public void setMaxPushAmountMonth(Double maxPushAmountMonth) {
		this.maxPushAmountMonth = maxPushAmountMonth;
	}
	

	/**
	 * @return the ncpDAPPush
	 */
	public String getNcpDAPPush() {
		return ncpDAPPush;
	}

	/**
	 * @param ncpDAPPush the ncpDAPPush to set
	 */
	public void setNcpDAPPush(String ncpDAPPush) {
		this.ncpDAPPush = ncpDAPPush;
	}

	/**
	 * @return the ncpDAPPull
	 */
	public String getNcpDAPPull() {
		return ncpDAPPull;
	}

	/**
	 * @param ncpDAPPull the ncpDAPPull to set
	 */
	public void setNcpDAPPull(String ncpDAPPull) {
		this.ncpDAPPull = ncpDAPPull;
	}

	/**
	 * @return the numCompteMTN
	 */
	public String getNumCompteMTN() {
		return numCompteMTN;
	}

	/**
	 * @param numCompteMTN the numCompteMTN to set
	 */
	public void setNumCompteMTN(String numCompteMTN) {
		this.numCompteMTN = numCompteMTN;
	}

	/**
	 * @return the numCompteCommissions
	 */
	public String getNumCompteCommissions() {
		return numCompteCommissions;
	}

	/**
	 * @param numCompteCommissions the numCompteCommissions to set
	 */
	public void setNumCompteCommissions(String numCompteCommissions) {
		this.numCompteCommissions = numCompteCommissions;
	}

	/**
	 * @return the numCompteTVA
	 */
	public String getNumCompteTVA() {
		return numCompteTVA;
	}

	/**
	 * @param numCompteTVA the numCompteTVA to set
	 */
	public void setNumCompteTVA(String numCompteTVA) {
		this.numCompteTVA = numCompteTVA;
	}

	/**
	 * @return the numCompteLiaison
	 */
	public String getNumCompteLiaison() {
		return numCompteLiaison;
	}

	/**
	 * @param numCompteLiaison the numCompteLiaison to set
	 */
	public void setNumCompteLiaison(String numCompteLiaison) {
		this.numCompteLiaison = numCompteLiaison;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @return the allowTransDuringTFJO
	 */
	public Boolean getAllowTransDuringTFJO() {
		return allowTransDuringTFJO;
	}

	/**
	 * @param allowTransDuringTFJO the allowTransDuringTFJO to set
	 */
	public void setAllowTransDuringTFJO(Boolean allowTransDuringTFJO) {
		this.allowTransDuringTFJO = allowTransDuringTFJO;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PlageTransactions [maxPullAmount=" + maxPullAmount
				+ ", maxPullAmountDay=" + maxPullAmountDay + ", profilName="
				+ profilName + "]";
	}    
    
}
