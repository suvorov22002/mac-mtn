package com.afb.dpd.mobilemoney.dao;

import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.enums.TransactionStatus;
import com.afb.dpd.mobilemoney.jpa.tools.bkeve;
import com.yashiro.persistence.utils.dao.JPAGenericDAORulesBased;

/**
 * Session Bean implementation class MobileMoneyDAO
 */
@Stateless(name = IMobileMoneyDAOLocal.SERVICE_NAME, mappedName = IMobileMoneyDAOLocal.SERVICE_NAME, description = "")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MobileMoneyDAO extends JPAGenericDAORulesBased implements IMobileMoneyDAOLocal {

	private static Log logger = LogFactory.getLog(MobileMoneyDAO.class);
	
	/**
	 * Default constructor. 
	 */
	public MobileMoneyDAO() {}

	/**
	 * Le gestionnaire d'entites
	 */
	@PersistenceContext(unitName = "MoMo")
	private EntityManager manager;


	public EntityManager getEntityManager() {
		return manager;
	}

	@Asynchronous
	public void updateSubscriber(List<Subscriber> subs){
		//for(Subscriber s : subs) updateSubscriber(s);
		saveList(subs,true);
	}
	
	@Asynchronous
	public void updateTransaction(List<Transaction> trans){
		//for(Transaction s : trans) updateTransaction(s);
		logger.info("MAJ des trx envoyes en regul");
		saveList(trans,true);
	}
	
	@Asynchronous
	public void updateSubscriber(Subscriber s){
		Query q = manager.createNamedQuery(Subscriber.UPDATE_SUBSCRIBER);
		q.setParameter("dateSaveDernCompta",s.getDateDernCompta());
		q.setParameter("dateDernCompta",s.getDateSaveDernCompta());
		q.setParameter("id",s.getId());
		q.executeUpdate();
	}

	@Asynchronous
	public void updateTransaction(Transaction t){
		Query q = manager.createNamedQuery(Transaction.UPDATE_TRANSACTION);
		q.setParameter("status",t.getStatus());
		q.setParameter("posted",t.getPosted());
		q.setParameter("dateTraitement",t.getDateTraitement());
		q.setParameter("id",t.getId());
		q.executeUpdate();
	}
	
	@Asynchronous
	public void updateBkeve(String eta, String etap, Long id){
		Query q = manager.createNamedQuery(bkeve.UPDATE_EVENT);
		q.setParameter("eta", eta);
		q.setParameter("etap", etap);
		q.setParameter("id",id);
		q.executeUpdate();
	}
	
	@Asynchronous
	public void updateTransactionId(TransactionStatus status, String trxId){
		Query q = manager.createNamedQuery(Transaction.UPDATE_TRANSACTION_STATUS);
		q.setParameter("status",status);
		q.setParameter("trxId",trxId);
		q.executeUpdate();
	}

}
