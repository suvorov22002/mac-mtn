/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;

import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;

/**
 * Classe representant un message transactionnel
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class RequestMessage implements Serializable {

	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor
	 */
	public RequestMessage() {}

	/**
	 * Type de l'operation (PULL/PUSH)
	 */
	private TypeOperation operation;
	
	/**
	 * PIN Banque
	 */
	private String bankPIN;
	
	/**
	 * Numero de Telephone
	 */
	private String phoneNumber = "237";
	
	/**
	 * Montant
	 */
	private Double amount;
	
	/**
	 * Numero de Compte
	 */
	private String account;
	
	/**
	 * Numero de la transaction
	 */
	private String trxId;

	/**
	 * @param operation
	 * @param bankPIN
	 * @param phoneNumber
	 * @param amount
	 * @param account
	 */
	public RequestMessage(TypeOperation operation, String bankPIN,
			String phoneNumber, Double amount, String account) {
		super();
		this.operation = operation;
		this.bankPIN = bankPIN;
		this.phoneNumber = phoneNumber;
		this.amount = amount;
		this.account = account;
	}
	
	
	/**
	 * @param operation
	 * @param phoneNumber
	 * @param amount
	 * @param account
	 */
	public RequestMessage(TypeOperation operation, String phoneNumber, Double amount, String account, String trxId) {
		super();
		this.operation = operation;
		this.phoneNumber = phoneNumber;
		this.amount = amount;
		this.account = account;
		this.trxId = trxId;
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
	 * @return the bankPIN
	 */
	public String getBankPIN() {
		return bankPIN;
	}

	/**
	 * @param bankPIN the bankPIN to set
	 */
	public void setBankPIN(String bankPIN) {
		this.bankPIN = bankPIN;
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
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}


	/**
	 * @return the trxId
	 */
	public String getTrxId() {
		return trxId;
	}


	/**
	 * @param trxId the trxId to set
	 */
	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}
	

}
