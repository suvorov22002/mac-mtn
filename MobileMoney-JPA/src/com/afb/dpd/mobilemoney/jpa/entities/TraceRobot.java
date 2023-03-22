/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.afb.dpd.mobilemoney.jpa.enums.TransactionStatus;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;

import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

/**
 * Trace Robot
 *
 * @author AFB
 * @version 1.0
 */
@Entity
@Table(name = "MOMO_TRACE_ROBOT")
public class TraceRobot implements Serializable {

	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static final Long Default = 01l;
	
	
	/**
	 * Id auto genere
	 */
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(name = "DATETIME_TRACE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date datetimeTrace = new Date();
	
	@Column(name = "TYPE_OPERATION")
	@Enumerated(EnumType.STRING)
	private TypeOperation operation;
	
	@Column(name = "TRX_ID")
	private Long trxId;
	
	@Column(name = "TRX_ACCOUNT")
	private String account;
	
	@Column(name = "TRX_PHONE")
	private String phone;
	
	@Column(name = "TRX_AMOUNT")
	private Double amount;
	
	@Column(name = "STATUS_RECONCILIATION")
	@Enumerated(EnumType.STRING)
	private TransactionStatus status;	
	
	@Column(name = "COMMENT")
	private String commentaire;
	
	public TraceRobot() {}
	
	
	public TraceRobot(Long id) {
		super();
		this.id = id;
	}
	
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	

	public Date getDatetimeTrace() {
		return datetimeTrace;
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
	 * @return the trxId
	 */
	public Long getTrxId() {
		return trxId;
	}


	/**
	 * @param trxId the trxId to set
	 */
	public void setTrxId(Long trxId) {
		this.trxId = trxId;
	}


	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}


	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}


	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}


	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
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


	public void setDatetimeTrace(Date datetimeTrace) {
		this.datetimeTrace = datetimeTrace;
	}

	
	/**
	 * @return the commentaire
	 */
	public String getCommentaire() {
		return commentaire;
	}


	/**
	 * @param commentaire the commentaire to set
	 */
	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}


	public String getFormattedDatetimeTrace() {
		return datetimeTrace == null ? null : new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(datetimeTrace); 
	}
		
	
    
}
