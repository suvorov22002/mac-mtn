/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.servlet;

import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.json.JSONObject;

import com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote;
import com.afb.dpd.mobilemoney.jpa.exception.MoMoException;

/**
 * @author hp
 *
 */
public class ClientMoMoBankAPI {

	/**
	 * Jndi Context
	 */
	private Context ctx = null;
	
	/**
	 * Bank service api
	 */
	private IMobileMoneyManagerRemote bankService = null;
	
	/**
	 * Bank serverName
	 */
	private String bankServerName = "172.21.10.103";
	
	/**
	 * Jndi port
	 */
	private String jndiPort = "1299";
	
	
	
	/**
	 * Customer phone number
	 */
	String phoneNumber = "237677159547";
	
	/**
	 * Customer MoMo bank PIN
	 */
	String bankPIN = "3766"; 
	
	/**
	 * Amount of the transaction
	 */
	Double amount = 1000d;
	
	
	/**
	 * Default constructor
	 */
	public ClientMoMoBankAPI() {}

	

	/**
	 * Initialize the jndi context for requesting jboss services
	 */
	private void initContext() {

		try {
			
			Properties props = System.getProperties();
			
			// Init jndi properties
			props.put("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
			props.put("java.naming.factory.url.pkgtial", "org.jnp.interfaces.NamingContextFactory");
			props.put("java.naming.factory.urls", "org.jboss.naming.client");
			props.put("java.naming.provider.url", "jnp://"+ bankServerName +":"+ jndiPort +"");
			
			// Initialisation du contexte JNDI
			ctx = new InitialContext( props );
			
			// Log
			//logger.info("Initialisation du Contexte OK !!!");
			
		} catch (Exception e) {
			
			// On relance l'exception
			throw new RuntimeException("Erreur lors de l'initialisation du Contexte JNDI", e);
			
		}
		
	}
	
	/**
	 * Start the bank service api
	 */
	private void startService() {

		try {
			
			bankService = (IMobileMoneyManagerRemote)ctx.lookup( "MobileMoney/" + IMobileMoneyManagerRemote.SERVICE_NAME + "/remote" );

		} catch (Exception e) {
			
			// On relance l'exception
			throw new RuntimeException("Erreur lors du demarrage du service MobileMoney", e);
			
		}


	}

	
	
	/**
	 * @param args
	 */
	public void main(String[] args) {

		// Init a connection to the server
		initContext();
		
		// Start bank MoMo API Interface
		startService();
		
		
		/****
		 * ************************
		 *   PULL FROM ACCOUNT
		 * ************************
		 */
		
		try {
			
			// Process a Pull Transaction
			bankService.processPullTransaction(phoneNumber, bankPIN, amount);
			
			// Log
			System.out.println("Transaction Processes successfully");
			
		} catch (MoMoException me) {
			
			// MoMo Exception
			System.out.println(me.getCode() + " : " + me.getMessage());
			
		} catch (Exception e) {
			
			// On relance l'exception
			throw new RuntimeException("Erreur lors de l'execution de la transaction Pull from Account", e);
			
		}
		
		
		/****
		 * ************************
		 *   PUSH FROM ACCOUNT
		 * ************************
		 */
		
		try {
			
			// Process a Push Transaction
			bankService.processPushTransaction(phoneNumber, bankPIN, amount);
			
			// Log
			System.out.println("Transaction Processes successfully");
			
		} catch (MoMoException me) {
			
			// MoMo Exception
			System.out.println(me.getCode() + " : " + me.getMessage());
			
		} catch (Exception e) {
			
			// On relance l'exception
			throw new RuntimeException("Erreur lors de l'execution de la transaction Push from Account", e);
			
		}
		
		

		/****
		 * ************************
		 *   BALANCE
		 * ************************
		 */
		
		try {
			
			// Process a Balance Transaction
			Double balance = bankService.processBalanceTransaction(phoneNumber, bankPIN);
			
			// Log
			System.out.println("Transaction Processes successfully! Balance = " + balance);
			
		} catch (MoMoException me) {
			
			// MoMo Exception
			System.out.println(me.getCode() + " : " + me.getMessage());
			
		} catch (Exception e) {
			
			// On relance l'exception
			throw new RuntimeException("Erreur lors de l'execution de la transaction Balance", e);
			
		}
		

		/****
		 * ************************
		 *   CHECK SUBSCRIBER
		 * ************************
		 */
		
		try {
			
			// Process a Balance Transaction
			Map<String, String> isSubscriber = bankService.checkSubscriber(phoneNumber);
			
		} catch (Exception e) {
			
			// On relance l'exception
			throw new RuntimeException("Erreur lors de l'execution de la transaction Balance", e);
			
		}
		
		

	}
	
	
}
