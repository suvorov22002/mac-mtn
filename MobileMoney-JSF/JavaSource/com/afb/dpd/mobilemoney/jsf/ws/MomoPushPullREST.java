package com.afb.dpd.mobilemoney.jsf.ws;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Startup;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.enums.ExceptionCode;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jpa.exception.MoMoException;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyTools;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;


@Stateless
@Startup
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@Path("/pushpull")
public class MomoPushPullREST {
	
	//@EJB
	//private IDaoMomoLocal dao;
	
	private static Log logger = LogFactory.getLog(MomoPushPullREST.class);
	
	/**
	 * Obtain the balance for a specific account
	 * 
	 * @param phoneNumber the subscriber phone number
	 * @return the balance of the account
	 */
	@GET
	@Path("/balance")
	@Consumes({"application/json","application/xml"})
	@Produces({"application/json","application/xml"})
	public long getBalance(@QueryParam("phoneNumber") String phoneNumber) {
		long balance = 0;
		// Traitement
		Map<String, String> map = new HashMap<String, String>();
		try {
			//map = MobileMoneyViewHelper.appManager.processBalanceTransaction(phoneNumber, Encrypter.getInstance().decryptText(bankPIN), "", "");
			map = MobileMoneyViewHelper.appManager.getBalanceECW(phoneNumber);
		} catch (MoMoException me) {
			
			// MoMo Exception
			logger.info(me.getCode() + " : " + me.getMessage());
			
		} catch (Exception e) {

			Subscriber subs = MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(phoneNumber);
			// Log de la trace
			if(subs != null) MobileMoneyViewHelper.appManager.logTraceTrxECW("", phoneNumber, 0d, subs, TypeOperation.BALANCE, ExceptionCode.BankException);
			
			// On relance l'exception
			throw new RuntimeException("Erreur lors de l'execution de la transaction Balance", e);
			
		}
		
		//logger.info("MAP = "+map.toString());
		
		if(map.containsKey("amount")) {
			balance = Long.parseLong(map.get("amount")); 
			//logger.info("Amount = "+map.get("amount"));
		}
		//if(map.containsKey("error")) returnMap.put("error", map.get("error"));
		
		//logger.info("Balance = "+balance);
		
		return balance;// MobileMoneyViewHelper.appManager.processBalanceTransaction(phoneNumber, bankPIN);
	}
	
	
	/**
	 * Make PUSH transaction
	 * Push money to bank account
	 * 
	 * @param transactionId
	 * @param phoneNumber
	 * @param amount
	 * @param currency
	 * @return
	 */
	@GET
	@Path("/push")
	@Consumes({"application/json","application/xml"})
	@Produces({"application/json","application/xml"})
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String pushTransaction(
			@QueryParam("transactionId") String transactionId, 
			@QueryParam("phoneNumber") String phoneNumber,
			@QueryParam("amount") Double amount) {
		// TODO Auto-generated method stub
		//logger.info("Montant PUSH : " + amount);
		//logger.info("PUSH TRX ID : "+transactionId);
		// Traitement
		Map<String, String> map = new HashMap<String, String>();
		try {
			//map = MobileMoneyViewHelper.appManager.processPushTransaction(phoneNumber, Encrypter.getInstance().decryptText(bankPIN), amount, "", "");
			map = MobileMoneyViewHelper.appManager.pushTransactionECW(transactionId, phoneNumber, amount);
		} catch (MoMoException me) {
			
			// MoMo Exception
			logger.info(me.getCode() + " : " + me.getMessage());
			
		} 
		catch (PersistenceException e){
//			System.err.println("ERREUR TRX DUPLIQUEE : " + e.getMessage());
//			Transaction transaction = MobileMoneyViewHelper.appManager.getMTNTrxID(transactionId);
//			System.err.println("TRX RETOURNEE : " + transaction);
//			// Operation executee avec succes
//			map.put("statusCode", "200");
//			map.put("remoteID", transaction.getId().toString());
//
//			// Solde du compte après operation
//			Double balance = 0.0;
//			try {
//				balance = MobileMoneyViewHelper.appManager.getCurrentSolde( MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(phoneNumber).getFirstAccount() );
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} //processBalanceTransaction2(msisdn);//getBalanceTransaction(msisdn, bankPin);
//			map.put("amount", String.valueOf(balance.longValue()));
//			
			// Annuler l'evenement poste dans le corebanking
		}
		catch (Exception e) {

			Subscriber subs = MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(phoneNumber);
			// Log de la trace
			if(subs != null) MobileMoneyViewHelper.appManager.logTraceTrxECW(transactionId, phoneNumber, amount, subs, TypeOperation.PUSH, ExceptionCode.BankException);
			
			// On relance l'exception
			throw new RuntimeException("Erreur lors de l'execution de la transaction Push from Account", e);
		}
		
		//logger.info("MAP = "+map.toString());
		
		Map<String, String> returnMap =  new HashMap<String, String>();
		if(map.containsKey("remoteID")) returnMap.put("remoteID", map.get("remoteID"));
		if(map.containsKey("scheduledTrxID")) returnMap.put("scheduledTrxID", map.get("scheduledTrxID"));
		if(map.containsKey("statusCode")) returnMap.put("statusCode", map.get("statusCode"));
		if(map.containsKey("amount")) returnMap.put("amount", map.get("amount"));
		if(map.containsKey("error")) returnMap.put("error", map.get("error"));
		//returnMap.put("currency", currency);
		
		return returnMap.toString();
	}
	
	
	/**
	 * Withdraw money from bank account
	 * 
	 * @param resource
	 * @param accountHolderId
	 * @param amount
	 * @param transactionId
	 * @param message
	 * @param extension
	 * @param version
	 * @return
	 */
	@GET
	@Path("/pull")
	@Consumes({"application/json","application/xml"})
	@Produces({"application/json","application/xml"})
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public String pullTransaction(
			@QueryParam("transactionId") String transactionId, 
			@QueryParam("phoneNumber") String phoneNumber,
			@QueryParam("amount") Double amount) {
		// TODO Auto-generated method stub

		//logger.info("PULL TRX ID : "+transactionId);
		// Traitement
		Map<String, String> map = new HashMap<String, String>();
		try {
			//map = MobileMoneyViewHelper.appManager.processPullTransaction(phoneNumber, Encrypter.getInstance().decryptText(bankPIN), amount, "", "");
			map = MobileMoneyViewHelper.appManager.pullTransactionECW(transactionId, phoneNumber, amount);
		} catch (MoMoException me) {
			
			// MoMo Exception
			logger.info(me.getCode() + " : " + me.getMessage());
			
		} 
		catch (PersistenceException e){
//			System.err.println("ERREUR TRX DUPLIQUEE : " + e.getMessage());
//			Transaction transaction = MobileMoneyViewHelper.appManager.getMTNTrxID(transactionId);
//			System.err.println("TRX RETOURNEE : " + transaction);
//			// Operation executee avec succes
//			map.put("statusCode", "200");
//			map.put("remoteID", transaction.getId().toString());
//
//			// Solde du compte après operation
//			Double balance = 0.0;
//			try {
//				balance = MobileMoneyViewHelper.appManager.getCurrentSolde( MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(phoneNumber).getFirstAccount() );
//			} catch (Exception e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			} //processBalanceTransaction2(msisdn);//getBalanceTransaction(msisdn, bankPin);
//			map.put("amount", String.valueOf(balance.longValue()));
//			
			// Annuler l'evenement poste dans le corebanking
		}
		catch (Exception e) {
			
			Subscriber subs = MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(phoneNumber);
			// Log de la trace
			if(subs != null) MobileMoneyViewHelper.appManager.logTraceTrxECW(transactionId, phoneNumber, amount, subs, TypeOperation.PULL, ExceptionCode.BankException);
			
			// On relance l'exception
			throw new RuntimeException("Erreur lors de l'execution de la transaction Pull from Account", e);
			
		}
		//logger.info("MAP = "+map.toString());
		
		Map<String, String> returnMap =  new HashMap<String, String>();
		if(map.containsKey("remoteID")) returnMap.put("remoteID", map.get("remoteID"));
		if(map.containsKey("statusCode")) returnMap.put("statusCode", map.get("statusCode"));
		if(map.containsKey("amount")) returnMap.put("amount", map.get("amount"));
		if(map.containsKey("error")) returnMap.put("error", map.get("error"));
		//returnMap.put("currency", currency);
		
		return returnMap.toString();
	}
	
	
	/**
	 * Obtain the last 4 transactions
	 * 
	 * @param phoneNumber
	 * @return The list of the last 4 transactions
	 */
	@GET
	@Path("/ministatement")
	@Consumes({"application/json","application/xml"})
	@Produces({"application/json","application/xml"})
	public List<Transaction> getMiniStatement(@QueryParam("phoneNumber") String phoneNumber) {
		// TODO Auto-generated method stub
		
		// Request MTN ECW
		//return MobileMoneyViewHelper.appDAOLocal.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("phoneNumber", phoneNumber)).add(Restrictions.gt("amount", 0d)), OrderContainer.getInstance().add(Order.desc("date")), null, 0, 5);
		try {
			List<Transaction> list = MobileMoneyViewHelper.appManager.getMinistatementECW(phoneNumber);
			//logger.info("MINI STATEMENT");
			for(Transaction trx : list) trx.setSubscriber(null);
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block

			Subscriber subs = MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(phoneNumber);
			// Log de la trace
			if(subs != null) MobileMoneyViewHelper.appManager.logTraceTrxECW("", phoneNumber, 0d, subs, TypeOperation.MINISTATEMENT, ExceptionCode.BankException);
						
			e.printStackTrace();
			// MoMo Exception
			logger.info(e.getMessage());
		}
		return null;
	}
	
	
	/**
	 * Verify if the phoneNumber exist in corabanking system
	 * 
	 * @param phoneNumber
	 * @return true if the phone number exist or false else
	 */
	@GET
	@Path("/verify")
	@Consumes({"application/json","application/xml"})
	@Produces({"application/json","application/xml"})
	public Boolean verifyAccountHolder(@QueryParam("phoneNumber") String phoneNumber) {
		// TODO Auto-generated method stub
		//logger.info("Starting Verify Account Holder");
		if(null == MobileMoneyViewHelper.appManager.verifySubscriberFromPhoneNumber(phoneNumber)) {
			//logger.info("Verify Account Holder : FALSE");
			return false;
		}
		logger.info("Verify Account Holder : TRUE");
		return true;
	}
	
	
	/**
	 * Get subscriber information from bank
	 * 
	 * @param phoneNumber
	 * @return subscriber : the subscriber information or null
	 */
	@GET
	@Path("/financialinfo")
	@Consumes({"application/json","application/xml"})
	@Produces({"application/json","application/xml"})
	public String getFinancialInformation(@QueryParam("phoneNumber") String phoneNumber) {
		// TODO Auto-generated method stub
		Map<String, String> map = new HashMap<String, String>();
		logger.info("PHONE TO LINK : "+phoneNumber);
		Subscriber sub = MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(phoneNumber);
		
		if(null != sub) {
			final String phone = phoneNumber;
			//wl
//			Thread th = 
			new Thread(new Runnable() {
			    @Override
			    public void run() {
//			    	logger.error("IN THREAD RUN LINKAGE METHOD : (Phone) "+phone);
			    	logger.info("IN THREAD RUN LINKAGE METHOD : (Phone) "+phone);
			    //	MobileMoneyTools.verifyAndExecuteLinkage(phone); ////comment 31-03-2021, linkage auto
			    }
			}).start();
			
//			th.start();
			//if(th.getState().equals(Thread.State.TERMINATED) && !th.isAlive()) th.stop();
			
			//logger.info("CUSTOMER NAME : "+sub.getCustomerName());
			map.put("accountNumber", MobileMoneyTools.getSubscriberFRI(sub));
			//map.put("accountNumber", sub.getFirstAccount().substring(13).replace("-", "")); // sub.getFirstPhone() 
			logger.info("CUSTOMER ACCOUNT : "+MobileMoneyTools.getSubscriberFRI(sub));
			map.put("accountName", (sub.getMtnSubFirstname()==null ? sub.getCustomerName() : sub.getMtnSubFirstname())+(sub.getMtnSubSurname()==null ? "" : " "+sub.getMtnSubSurname()));
			map.put("accountType", null);
			return  map.toString();
		}
		logger.info("SUBSCRIBER NOT FOUND ");
		return null;
	}
	
	
}
