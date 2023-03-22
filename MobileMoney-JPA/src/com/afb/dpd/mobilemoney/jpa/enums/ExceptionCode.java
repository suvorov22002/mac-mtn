package com.afb.dpd.mobilemoney.jpa.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * Type d'operations
 * @author Francis DJIOMOU
 * @version 1.0
 */
public enum ExceptionCode {
	
	/**
	 * Pull from account
	 */
	SubscriberInvalidPIN("Invalid Bank PIN"),
	SubscriberInvalidPhone("Invalid Phone Number"),
	SubscriberInvalidAccount("Invalid Account Number"),
	SubscriberInvalidAmount("Invalid Amount"),
	SubscriberAmountExceedMaxAllowed("Amount Exceed Max Allowed"),
	SubscriberWaitingValidation("Subscriber Waiting for Validation"),
	SubscriberSuspended("Suspended Contract"),
	
	// Plafond
	SubscriberLimitPullDayReached("Limit Pull Day Reached"),
	SubscriberLimitPushDayReached("Limit Push Day Reached"),
	SubscriberLimitPullWeekReached("Limit Pull Week Reached"),
	SubscriberLimitPushWeekReached("Limit Push Week Reached"),
	SubscriberLimitPullMonthReached("Limit Pull Month Reached"),
	SubscriberLimitPushMonthReached("Limit Push Month Reached"),
	
	BankClosingAccount("Close Or Closing Account Error"),
	BankBlockingDebitAccount("Debit Blocking Account Error"),
	BankBlockingCreditAccount("Credit Blocking Account Error"),
	BankInsufficientBalance("Insufficient Balance"),
	MobileMoneyExceededAmount("MoMo Ceiling Exceeded"),
	BankExceededAmount("Bank Ceiling Exceeded"),
	BankException("Bank Exception Error"),
	BankAPICbsException("API CBS Exception Error"),
	
	MobileMoneySDPServiceSuspended("MobileMoney SDP Service Suspended"),
	MobileMoneySDPServiceOnMaintenance("MobileMoney SDP Service On Maintenance"),
	MobileMoneySDPEndUserTrxSuspended("MobileMoney SDP End-User Transactions Suspended"),
	MobileMoneySDPMerchandTrxSuspended("MobileMoney SDP Merchand Transactions Suspended"),
	
	NetWorkSendingMoMoRequest("Sending MoMo Resquest Error"),
	NetWorkReceivingMoMoRequestResponse("Receiving MoMo Request Response Error"),
	NetWorkReceivingMoMoRequestConfirmation("Receiving MoMo Request Confirmation Error"),
	NetWorkSendingMoMoRequestConfirmation("Sending MoMo Request Confirmation Error"),
	
	SystemCoreBankingSystemAcces("Bank Core Banking System Error"),
	SystemCommittingTransaction("Bank Commit Transaction Error"),
	
	// Unicite MTN TRX ID
	TransactionIDAlreadyExist("Transaction Already Exit With This MTN Trx ID");
	
	/**
	 * Valeur
	 */
	private String value;
	
	/**
	 * Constructeur
	 * @param value
	 */
	private ExceptionCode(String value){
		this.setValue(value);
	}
	
	/**
	 * Retourne la liste des valeus
	 * @return liste des codes des execeptions
	 */
	public static List<ExceptionCode> getValues() {
		
		// Initialisation de la collection a retourner
		List<ExceptionCode> ops = new ArrayList<ExceptionCode>();
		
		// Ajout des valeurs
		ops.add(SubscriberInvalidPIN);
		ops.add(SubscriberInvalidPhone);
		ops.add(SubscriberInvalidAccount);
		ops.add(SubscriberInvalidAmount);
		ops.add(SubscriberAmountExceedMaxAllowed);
		ops.add(SubscriberWaitingValidation);
		ops.add(SubscriberSuspended);
		
		ops.add(BankClosingAccount);
		ops.add(BankBlockingDebitAccount);
		ops.add(BankBlockingCreditAccount);
		ops.add(BankInsufficientBalance);
		ops.add(MobileMoneyExceededAmount);
		ops.add(BankExceededAmount);

		
		ops.add(MobileMoneySDPServiceSuspended);
		ops.add(MobileMoneySDPServiceOnMaintenance);
		ops.add(MobileMoneySDPEndUserTrxSuspended);
		ops.add(MobileMoneySDPMerchandTrxSuspended);
		
		ops.add(NetWorkSendingMoMoRequest);
		ops.add(NetWorkReceivingMoMoRequestResponse);
		ops.add(NetWorkReceivingMoMoRequestConfirmation);
		ops.add(NetWorkSendingMoMoRequestConfirmation);
		
		ops.add(SystemCoreBankingSystemAcces);
		ops.add(SystemCommittingTransaction);
		
		ops.add(TransactionIDAlreadyExist);
		ops.add(BankAPICbsException);
		
		// Retourne la collection
		return ops;
		
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
}
