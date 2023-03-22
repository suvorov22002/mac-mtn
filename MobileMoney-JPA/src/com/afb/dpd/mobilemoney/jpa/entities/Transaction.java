/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

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
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.afb.dpd.mobilemoney.jpa.enums.ExceptionCategory;
import com.afb.dpd.mobilemoney.jpa.enums.ExceptionCode;
import com.afb.dpd.mobilemoney.jpa.enums.TransactionStatus;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jpa.tools.MoMoHelper;

import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

/**
 * Classe representant une transaction MAC a executer dans le Core Banking
 * @author Francis DJIOMOU
 * @version 1.0
 */
@Entity
@NamedQueries({    
    @NamedQuery(name="Transaction.findAll", query="SELECT t FROM Transaction t"),
    @NamedQuery(name=Transaction.UPDATE_TRANSACTION,query="update Transaction t set t.status=:status, t.posted=:posted, t.dateTraitement=:dateTraitement   where t.id=:id"),
    @NamedQuery(name=Transaction.UPDATE_TRANSACTION_STATUS,query="update Transaction t set t.status=:status where t.mtnTrxId = :trxId"),
}) 
@Table(name = "MoMo_TRANSACTION")
public class Transaction implements Serializable {

	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String UPDATE_TRANSACTION = "Transaction.mergesPostingTransaction";
	public static final String UPDATE_TRANSACTION_STATUS = "Transaction.majtrx";

	/**
	 * Default Constructor
	 */
	public Transaction() {}

	/**
	 * Id auto genere
	 */
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/**
	 * Id de la transaction cote MTN
	 */
	@Column(name = "MTN_TRX_ID", unique=true , nullable = false)
	private String mtnTrxId;
	
	/**
	 * Operation
	 */
	@Column(name = "OP", nullable = false)
	@Enumerated(EnumType.STRING)
	private TypeOperation typeOperation;
	
	/**
	 * Montant
	 */
	@Column(name = "AMOUNT", nullable = false)
	private Double amount = 0d;
	
	/**
	 * Numero de compte
	 */
	@Column(name = "ACCOUNT", nullable = false)
	private String account;
	
	/**
	 * Numero de telephone
	 */
	@Column(name = "PHONE")
	private String phoneNumber;
	
	/**
	 * Date de traitement de l'operation
	 */
	@Column(name = "DATE_OP", nullable = false)
	private Date date = new Date();

	/**
	 * Etat de la transaction
	 */
	@Column(name = "STATUS", nullable = false)
	@Enumerated(EnumType.STRING)
	private TransactionStatus status = TransactionStatus.WAITING;

	/**
	 * Type d'exception levee
	 */
	@Column(name = "ERROR_TYPE", nullable = true)
	@Enumerated(EnumType.STRING)
	private ExceptionCategory exceptionCategory;
	
	/**
	 * Code de l'exception
	 */
	@Column(name = "ERROR_CODE", nullable = true)
	@Enumerated(EnumType.STRING)
	private ExceptionCode exceptionCode;

	/**
	 * Souscripteur
	 */
	// Ajoute fetch = FetchType.LAZY cascade = CascadeType.MERGE,
	@ManyToOne( fetch = FetchType.EAGER)
	@JoinColumn(name = "SUBS_ID")
	private Subscriber subscriber;
	
	/**
	 * Total des commissions de la transaction
	 */
	@Column(name = "COMMISSIONS", nullable = false)
	private Double commissions = 0d;

	/**
	 * Montant TTC
	 */
	@Column(name = "TTC", nullable = false)
	private Double ttc = 0d;
	
	/**
	 * Determine si la transaction a ete postee dans le Core Banking ou non
	 */
	@Column(name = "POSTED")
	private Boolean posted = Boolean.FALSE;

	@Column(name = "reconcilier")
	private Boolean reconcilier = Boolean.FALSE;
	
	@Column(name = "verifier")
	private Boolean verifier = Boolean.FALSE;
	
	@Column(name = "DATE_RECON", nullable = true)
	private Date datereconcilier;
	
	/**
	 * Date de comptabilisation/facturation
	 */
	@Column(name = "DATECOMPTA", nullable = true)
	@Temporal(TemporalType.DATE)
	private Date dateCompta;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_TRAITEMENT", nullable = true)
	private Date dateTraitement;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_CONTROLE", nullable = true)
	private Date dateControle;
	
	@Transient
	private boolean selected = true;
	
	/**
	 * Message obtenu de MTN lors de la reconciliation
	 */
	@Column(name = "MESSAGE")
	private String message;

	/**
	 * TFJO Portal lance???
	 */
	@Column(name = "TFJO_LANCE")
	private Boolean tfjoLance = Boolean.FALSE;

	@Column(name = "MODE_NUIT_AMPLITUDE_ACTIVE")
	private Boolean modeNuitAmplitudeActive = Boolean.FALSE;

	@Column(name = "A_RETRAITER")
	private Boolean aRetraiter = Boolean.FALSE;
	
	@Column(name = "MAJ_EC")
	private Boolean majEc;
	
	@Column(name = "DATE_MAJ_EC", nullable = true)
	private Date dateMajEc;
	
	@Column(name = "MSG_MAJ_EC")
	private String msgMajEc;
	
	@Version
	@Column(columnDefinition = "integer DEFAULT 0", nullable = false)  
	private Long version;
	
	
	/**
	 * MAJ du mtn trx id avant insertion en bd pour les trx autre que PULL et PUSH
	 */
	@PrePersist
	void preInsert() {
	   if (this.mtnTrxId == null || this.mtnTrxId.isEmpty())
	       this.mtnTrxId = this.phoneNumber.concat(String.valueOf(this.subscriber.getId())).concat(String.valueOf(this.date.getTime()));
	}
	
	
	/**
	 * @param typeOperation
	 * @param subscriber
	 * @param amount
	 * @param account
	 * @param phoneNumber
	 */
	public Transaction(TypeOperation typeOperation, Subscriber subscriber, Double amount, String account, String phoneNumber) {
		super();
		this.typeOperation = typeOperation;
		this.subscriber = subscriber;
		this.amount = amount;
		this.account = account;
		this.phoneNumber = phoneNumber;
	}

	
	/**
	 * @param typeOperation
	 * @param subscriber
	 * @param amount
	 * @param account
	 * @param phoneNumber
	 */
	public Transaction(TypeOperation typeOperation, Subscriber subscriber, Double amount, String account, String phoneNumber, String mtnTrxId) {
		super();
		this.typeOperation = typeOperation;
		this.subscriber = subscriber;
		this.amount = amount;
		this.account = account;
		this.phoneNumber = phoneNumber;
		this.mtnTrxId = mtnTrxId;
	}

	
	/**
	 * 
	 * @param typeOperation
	 * @param subscriber
	 * @param amount
	 * @param account
	 * @param phoneNumber
	 * @param status
	 */
	public Transaction(TypeOperation typeOperation, Subscriber subscriber, Double amount, String account, String phoneNumber, TransactionStatus status) {
		super();
		this.typeOperation = typeOperation;
		this.subscriber = subscriber;
		this.amount = amount;
		this.account = account;
		this.phoneNumber = phoneNumber;
		this.status = status;
	}

	/**
	 * @param typeOperation
	 * @param subscriber
	 * @param amount
	 * @param account
	 * @param phoneNumber
	 * @param date
	 * @param commissions
	 * @param ttc
	 */
	public Transaction(TypeOperation typeOperation, Subscriber subscriber,
			Double amount, String account, String phoneNumber, Date date,
			TransactionStatus status, Double commissions, Double ttc, Date dateCompta) {
		super();
		this.typeOperation = typeOperation;
		this.subscriber = subscriber;
		this.amount = amount;
		this.account = account;
		this.phoneNumber = phoneNumber;
		this.date = date;
		this.status = status;
		this.commissions = commissions;
		this.ttc = ttc;
		this.dateCompta = dateCompta;
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
	 * @return the mtnTrxId
	 */
	public String getMtnTrxId() {
		return mtnTrxId;
	}


	/**
	 * @param mtnTrxId the mtnTrxId to set
	 */
	public void setMtnTrxId(String mtnTrxId) {
		this.mtnTrxId = mtnTrxId;
	}


	public Date getDateTraitement() {
		return dateTraitement;
	}

	public void setDateTraitement(Date dateTraitement) {
		this.dateTraitement = dateTraitement;
	}

	/**
	 * @return the typeOperation
	 */
	public TypeOperation getTypeOperation() {
		return typeOperation;
	}

	/**
	 * @return the verifier
	 */
	public Boolean getVerifier() {
		return verifier;
	}

	/**
	 * @param verifier the verifier to set
	 */
	public void setVerifier(Boolean verifier) {
		this.verifier = verifier;
	}

	/**
	 * @param typeOperation the typeOperation to set
	 */
	public void setTypeOperation(TypeOperation typeOperation) {
		this.typeOperation = typeOperation;
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
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @return the reconcilier
	 */
	public Boolean getReconcilier() {
		return reconcilier;
	}

	/**
	 * @param reconcilier the reconcilier to set
	 */
	public void setReconcilier(Boolean reconcilier) {
		this.reconcilier = reconcilier;
	}

	/**
	 * @return the datereconcilier
	 */
	public Date getDatereconcilier() {
		return datereconcilier;
	}

	/**
	 * @param datereconcilier the datereconcilier to set
	 */
	public void setDatereconcilier(Date datereconcilier) {
		this.datereconcilier = datereconcilier;
	}

	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
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
	
	
	/**
	 * @return the dateControle
	 */
	public Date getDateControle() {
		return dateControle;
	}

	/**
	 * @param dateControle the dateControle to set
	 */
	public void setDateControle(Date dateControle) {
		this.dateControle = dateControle;
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
	 * @return the exceptionCategory
	 */
	public ExceptionCategory getExceptionCategory() {
		return exceptionCategory;
	}

	/**
	 * @param exceptionCategory the exceptionCategory to set
	 */
	public void setExceptionCategory(ExceptionCategory exceptionCategory) {
		this.exceptionCategory = exceptionCategory;
	}

	/**
	 * @return the exceptionCode
	 */
	public ExceptionCode getExceptionCode() {
		return exceptionCode;
	}

	/**
	 * @param exceptionCode the exceptionCode to set
	 */
	public void setExceptionCode(ExceptionCode exceptionCode) {
		this.exceptionCode = exceptionCode;
	}

	/**
	 * @return the commissions
	 */
	public Double getCommissions() {
		return commissions;
	}

	/**
	 * @param commissions the commissions to set
	 */
	public void setCommissions(Double commissions) {
		this.commissions = commissions;
	}

	/**
	 * @return the ttc
	 */
	public Double getTtc() {
		return ttc ;
	}

	/**
	 * @param ttc the ttc to set
	 */
	public void setTtc(Double ttc) {
		this.ttc = ttc;
	}
	
	public Double getTaxes() {
		return this.ttc - (this.amount + this.commissions);
	}
	
	public String getFormattedDate() {
		return PortalHelper.DEFAULT_DATE_FORMAT.format(date);
	}

	public String getFormattedMontant() {
		return MoMoHelper.espacement(amount);
	}

	public String getHour() {
		return MoMoHelper.sdf_HOUR.format(date);
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @return the posted
	 */
	public Boolean getPosted() {
		return posted;
	}

	/**
	 * @param posted the posted to set
	 */
	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}


	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	
	/**
	 * @return the tfjoLance
	 */
	public Boolean getTfjoLance() {
		return tfjoLance;
	}

	/**
	 * @param tfjoLance the tfjoLance to set
	 */
	public void setTfjoLance(Boolean tfjoLance) {
		this.tfjoLance = tfjoLance;
	}

	/**
	 * @return the modeNuitAmplitudeActive
	 */
	public Boolean getModeNuitAmplitudeActive() {
		return modeNuitAmplitudeActive;
	}

	/**
	 * @param modeNuitAmplitudeActive the modeNuitAmplitudeActive to set
	 */
	public void setModeNuitAmplitudeActive(Boolean modeNuitAmplitudeActive) {
		this.modeNuitAmplitudeActive = modeNuitAmplitudeActive;
	}

	/**
	 * @return the aRetraiter
	 */
	public Boolean getARetraiter() {
		return aRetraiter;
	}

	/**
	 * @param aRetraiter the aRetraiter to set
	 */
	public void setARetraiter(Boolean aRetraiter) {
		this.aRetraiter = aRetraiter;
	}
	
	
	/**
	 * @return the majEc
	 */
	public Boolean getMajEc() {
		return majEc;
	}


	/**
	 * @param majEc the majEc to set
	 */
	public void setMajEc(Boolean majEc) {
		this.majEc = majEc;
	}


	/**
	 * @return the dateMajEc
	 */
	public Date getDateMajEc() {
		return dateMajEc;
	}


	/**
	 * @param dateMajEc the dateMajEc to set
	 */
	public void setDateMajEc(Date dateMajEc) {
		this.dateMajEc = dateMajEc;
	}


	/**
	 * @return the msgMajEc
	 */
	public String getMsgMajEc() {
		return msgMajEc;
	}


	/**
	 * @param msgMajEc the msgMajEc to set
	 */
	public void setMsgMajEc(String msgMajEc) {
		this.msgMajEc = msgMajEc;
	}


	/**
	 * @return the version
	 */
	public Long getVersion() {
		return version;
	}
	

	public String getRoundedTaxes() {
		DecimalFormat df = new DecimalFormat("#.###");
		df.setRoundingMode(RoundingMode.HALF_UP);
		return df.format(getTaxes());
	}
	
	public boolean isSuccess() {
		return this.status.equals(TransactionStatus.SUCCESS);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return  id +"|" + amount +"|" + date +"|" + account +"|"+ phoneNumber +"|" + status +"|" + subscriber.getCustomerName() +"|" + typeOperation;
	}
	
	public String getFormattedTrxId() {
		return this.mtnTrxId.substring(11);
	}
	
	
	
}
