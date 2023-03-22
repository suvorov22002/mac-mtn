package com.afb.dpd.mobilemoney.jpa.enums;

import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.INVALID_ACTIVITY;

/**
 * Type d'operations
 * @author Alex JAZA
 * @version 1.0
 */
public enum MTNErrorCodes {
		
	ACCOUNT_ALREADY_MEMBER_OF_GROUP("This account is already a member of this group"),
	ACCOUNT_COULD_NOT_BE_REMOVED_FROM_BANKDOMAIN("Account could not be removed from Bank domain"),
	ACCOUNT_HOLDER_ADDITIONAL_INFORMATION_NAME_ALREADY_EXIST("The specified account holder's additional information name already exist"),
	ACCOUNT_HOLDER_ADDITIONAL_INFORMATION_NAME_IN_USE("The specified account holder's additional information name cannot be deleted because it is in use"),
	ACCOUNT_HOLDER_ALREADY_MEMBER_OF_GROUP("This account holder is already a member of this group"),
	ACCOUNT_HOLDER_DOES_NOT_EXIST("AccountHolder does not exist"),
	ACCOUNT_HOLDER_NOT_LOGGED_IN("This operation requires the user to be logged in"),
	ACCOUNT_HOLDER_PROFILE_DOES_NOT_HAVE_REQUESTED_ACCOUNT_TYPE("AccountHolder profile does not have requested account"),
	ACCOUNT_HOLDER_STATUS_IS_NOT_VALID_FOR_ADD_ACCOUNT_OPERATION("AccountHolder status should be blocked or active"),
	ACCOUNT_NOT_FOUND("Account not found"),
	ACCOUNT_ROUTE_ALREADY_EXISTS("Could not create account route since Account route with provided account route reference and currency already exists"),
	ACCOUNT_ROUTE_NOT_FOUND("Could not delete account route since Account route with provided account route reference and currency does not exists"),
	ACCOUNT_TYPE_NOT_SUPPORTED("Account type is not supported"),
	ACCOUNTHOLDER_ACCOUNT_REFERENCE_ALREADY_EXISTS("The account holder already has a connection to the specified account"),
	ACCOUNTHOLDER_ACTIVATION_FAILED("Activation of account holder failed"),
	ACCOUNTHOLDER_ANY_ONE_MANDATORY_FIELD_REQUIRED("Could not perform operation since at least one of the mandatory field is required"),
	ACCOUNTHOLDER_CAN_NOT_REMOVE_EMAIL("Could not perform operation since at least one of the mandatory field is required"),
	ACCOUNTHOLDER_CAN_NOT_REMOVE_EXT_ID("Could not perform operation since at least one of the mandatory field is required"),
	ACCOUNTHOLDER_CAN_NOT_REMOVE_MSISDN("Could not perform operation since at least one of the mandatory field is required"),
	ACCOUNTHOLDER_CAN_NOT_REMOVE_USER_NAME("Could not perform operation since at least one of the mandatory field is required"),
	ACCOUNTHOLDER_EUI_NOT_FOUND("Account holder could not be found"),
	ACCOUNTHOLDER_MSISDN_NOT_FOUND("Operation requires account holder to have an MSISDN identity but it has none"),
	ACCOUNTHOLDER_NOT_ACTIVE("Account holder is inactive"),
	ACCOUNTHOLDER_NOT_CHILD("A specified account holder was not a child to another specified account holder"),
	ACCOUNTHOLDER_NOT_FOUND("Account holder could not be found"),
	ACCOUNTHOLDER_SETTINGS_ALREADY_EXISTS("The settings for this account holder already exists"),
	ACCOUNTHOLDER_SETTINGS_MANDATORY("Account holder settings are mandatory"),
	ACCOUNTHOLDER_SETTINGS_NOT_FOUND("Account holder settings not found"),
	ACCOUNTHOLDER_WITH_ALIAS_ALREADY_EXISTS("Account holder could not be registered because an account holder with submitted Alias already exists"),
	TRANSACTION_ID_OR_ORIGINAL_TRANSACTION_ID_MISSING("Neither transaction ID nor original transaction ID was given"),
	TRANSACTION_NOT_COMPLETED("Transaction has not been completed and its state is either Pending or Failed"),
	TRANSACTION_NOT_FOUND("Transaction not found"),
	TRANSACTION_TIMED_OUT("Transaction timed out"),
	VALIDATION_ERROR("The data provided in the request failed the validation check"),
	FRI_CAN_ONLY_HAVE_ONE_OWNER("Setting multiple owners of an FRI is not allowed"),
	FRI_INVALID("FRI string is incorrectly formatted"),
	FRI_REFERENCE_NOT_FOUND(""),
	IDENTITY_INVALID("Identity string is incorrectly formatted"),
	IMSI_INVALID("Format of IMSI is invalid"),
	INACTIVE_ACCOUNT("Inactive receiver account"),
	INTERNAL_ERROR("An internal error caused the operation to fail"),
	INVALID_ACCOUNT_FRI("The FRI must be of account type"),
	INVALID_CURRENCY("Currency is either not valid or used incorrectly"),
	INVALID_FIELD_VALUE("Element has invalid format"),
	INVALID_PROFILE("Profile validation"),
	INVALID_PROFILE_COUNTER("Profile counter validation"),
	INVALID_PROFILE_NAME("Profile name validation"),
	INVALID_PROFILE_THRESHOLD("Profile threshold validation"),
	INVALID_RECEIVER("Invalid receiving end user"),
	INVALID_SETTLEMENT_DEPOSIT_ID("Settlement deposit with given ID could not be found"),
	INVALID_SETTLEMENTID("Settlement with given ID could not be found"),
	INVALID_SIGNATURE("The provided signature is not correct"),
	INVALID_STATUS("Current status is not as expected"),
	INVALID_TRANSACTION_TYPE("Invalid Transaction type"),
	NO_ACCESS("User has no access permission"),
	NOT_AUTHORIZED("The logged in user does not have sufficient permissions to perform this operation"),
	TARGET_AUTHORIZATION_ERROR("Target financial resource has reached counter or balance limits, has insufficient funds or is missing permissions"),
	TARGET_NOT_FOUND("Target FRI not found"),
	SOURCE_AND_TARGET_ARE_THE_SAME("Source is the same as the target"),
	SOURCE_NOT_FOUND("Source FRI not found"),
	RESOURCE_NOT_ACTIVE("Required resource is not active"),
	RESOURCE_NOT_AVAILABLE("FRI not available"),
	RESOURCE_NOT_FOUND("FRI not found"),
	RESOURCE_TEMPORARY_LOCKED("Required resource is temporarily locked"),
	QUOTE_NOT_SUPPORTED("Quote is not supported with the combination of parameters"),
	QUOTE_PROCESSING("Quote is already processing"),
	REACTIVATE_ACCOUNT_FAILED("Account could not be re-activated"),
	RECONCILIATION_PROCESS_ERROR("Failed to process reconciliation file"),
	QUEUED_FOR_APPROVAL("Request is queued for approval"),
	QUOTE_ALREADY_USED("Quote has already been used"),
	QUOTE_EXPIRED("Quote has expired"),
	QUOTE_NOT_FOUND("Quote not found");
		
	/**
	 * Valeur
	 */
	private String value;
	
	/**
	 * Constructeur
	 * @param value
	 */
	private MTNErrorCodes(String value){
		this.setValue(value);
	}
	
	/**
	 * Retourne la liste des valeus
	 * @return liste des status des transactions
	 */
	public static List<MTNErrorCodes> getValues() {
		
		// Initialisation de la collection a retourner
		List<MTNErrorCodes> ops = new ArrayList<MTNErrorCodes>();
		
		// Ajout des valeurs
		ops.add(ACCOUNT_ALREADY_MEMBER_OF_GROUP);
		ops.add(ACCOUNT_COULD_NOT_BE_REMOVED_FROM_BANKDOMAIN);
		ops.add(ACCOUNT_HOLDER_ADDITIONAL_INFORMATION_NAME_ALREADY_EXIST);
		ops.add(ACCOUNT_HOLDER_PROFILE_DOES_NOT_HAVE_REQUESTED_ACCOUNT_TYPE);
		ops.add(ACCOUNT_HOLDER_NOT_LOGGED_IN);
		ops.add(ACCOUNT_ROUTE_NOT_FOUND);
		ops.add(ACCOUNT_NOT_FOUND);
		ops.add(ACCOUNT_HOLDER_STATUS_IS_NOT_VALID_FOR_ADD_ACCOUNT_OPERATION);
		ops.add(ACCOUNT_ROUTE_ALREADY_EXISTS);
		ops.add(ACCOUNT_HOLDER_ADDITIONAL_INFORMATION_NAME_IN_USE);
		ops.add(ACCOUNT_HOLDER_DOES_NOT_EXIST);
		ops.add(ACCOUNT_HOLDER_ALREADY_MEMBER_OF_GROUP);
		ops.add(ACCOUNT_TYPE_NOT_SUPPORTED);
		ops.add(ACCOUNTHOLDER_ACTIVATION_FAILED);
		ops.add(ACCOUNTHOLDER_ANY_ONE_MANDATORY_FIELD_REQUIRED);
		ops.add(ACCOUNTHOLDER_ACCOUNT_REFERENCE_ALREADY_EXISTS);
		ops.add(ACCOUNTHOLDER_CAN_NOT_REMOVE_USER_NAME);
		ops.add(ACCOUNTHOLDER_CAN_NOT_REMOVE_EMAIL);
		ops.add(ACCOUNTHOLDER_CAN_NOT_REMOVE_MSISDN);
		ops.add(ACCOUNTHOLDER_EUI_NOT_FOUND);
		ops.add(ACCOUNTHOLDER_MSISDN_NOT_FOUND);
		ops.add(ACCOUNTHOLDER_CAN_NOT_REMOVE_EXT_ID);
		ops.add(ACCOUNTHOLDER_NOT_CHILD);
		ops.add(ACCOUNTHOLDER_NOT_ACTIVE);
		ops.add(ACCOUNTHOLDER_SETTINGS_ALREADY_EXISTS);
		ops.add(ACCOUNTHOLDER_SETTINGS_NOT_FOUND);
		ops.add(ACCOUNTHOLDER_MSISDN_NOT_FOUND);
		ops.add(ACCOUNTHOLDER_ACTIVATION_FAILED);
		ops.add(ACCOUNTHOLDER_WITH_ALIAS_ALREADY_EXISTS);
		ops.add(INACTIVE_ACCOUNT);
		ops.add(INTERNAL_ERROR);
		ops.add(INVALID_PROFILE);
		ops.add(IDENTITY_INVALID);
		ops.add(INVALID_ACCOUNT_FRI);
		ops.add(INVALID_PROFILE_COUNTER);
		ops.add(IMSI_INVALID);
		ops.add(INVALID_CURRENCY);
		ops.add(INVALID_ACCOUNT_FRI);
		ops.add(INVALID_PROFILE_NAME);
		ops.add(INVALID_PROFILE_THRESHOLD);
		ops.add(INVALID_SETTLEMENTID);
		ops.add(INVALID_SETTLEMENT_DEPOSIT_ID);
		ops.add(INVALID_STATUS);
		ops.add(INVALID_FIELD_VALUE);
		ops.add(INVALID_SIGNATURE);
		ops.add(INVALID_TRANSACTION_TYPE);
		ops.add(INVALID_RECEIVER);
		ops.add(FRI_INVALID);
		ops.add(FRI_REFERENCE_NOT_FOUND);
		ops.add(FRI_CAN_ONLY_HAVE_ONE_OWNER);
		ops.add(NO_ACCESS);
		ops.add(NOT_AUTHORIZED);
		ops.add(VALIDATION_ERROR);
		ops.add(TARGET_AUTHORIZATION_ERROR);
		ops.add(TRANSACTION_ID_OR_ORIGINAL_TRANSACTION_ID_MISSING);
		ops.add(TARGET_NOT_FOUND);
		ops.add(TRANSACTION_NOT_FOUND);
		ops.add(TRANSACTION_NOT_COMPLETED);
		ops.add(TRANSACTION_TIMED_OUT);
		ops.add(SOURCE_NOT_FOUND);
		ops.add(SOURCE_AND_TARGET_ARE_THE_SAME);
		ops.add(RECONCILIATION_PROCESS_ERROR);
		ops.add(RESOURCE_NOT_ACTIVE);
		ops.add(RESOURCE_NOT_AVAILABLE);
		ops.add(RESOURCE_TEMPORARY_LOCKED);
		ops.add(REACTIVATE_ACCOUNT_FAILED);
		ops.add(RESOURCE_NOT_FOUND);
		ops.add(QUEUED_FOR_APPROVAL);
		ops.add(QUOTE_EXPIRED);
		ops.add(QUOTE_ALREADY_USED);
		ops.add(QUOTE_NOT_FOUND);
		ops.add(QUOTE_NOT_SUPPORTED);
		ops.add(QUOTE_PROCESSING);
		
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
