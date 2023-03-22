package com.afb.dpd.mobilemoney.dao;

import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Local;

import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.enums.TransactionStatus;
import com.yashiro.persistence.utils.dao.IJPAGenericDAO;

/**
 * Service DAO Local de Gestion du MobileMoney
 * @author Francis DJIOMOU
 * @version 2.0
 */
@Local
public interface IMobileMoneyDAOLocal extends IJPAGenericDAO {

	/**
	 * Nom du Service DAO de gestion des utilisateurs
	 */
	public static final String SERVICE_NAME = "MobileMoneyDAOLocal";
	
	/**
	 * MAJ d'une liste d'abonnements
	 * @param subs liste d'abonnements
	 */
	@Asynchronous
	public void updateSubscriber(List<Subscriber> subs);
	
	/**
	 * MAJ d'une liste de transactions
	 * @param trans liste de transactions
	 */
	@Asynchronous
	public void updateTransaction(List<Transaction> trans);
	
	/**
	 * MAJ d'un abonnement
	 * @param s abonnement
	 */
	public void updateSubscriber(Subscriber s);
	
	/**
	 * MAJ d'une transaction
	 * @param t transaction
	 */
	public void updateTransaction(Transaction t);
	
	/**
	 * MAJ de l'evenement  
	 * 
	 * @param eta
	 * @param etap
	 * @param id
	 */
	public void updateBkeve(String eta, String etap, Long id);
	
	/**
	 * MAJ du statut de la transaction
	 * @param status
	 * @param trxId
	 */
	public void updateTransactionId(TransactionStatus status, String trxId);
	
}
