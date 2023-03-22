package com.afb.dpd.mobilemoney.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.yashiro.persistence.utils.dao.JPAGenericDAORulesBased;

/**
 * Session Bean implementation class MobileMoneyDAO
 */
@Stateless(name = IPullPushDAOLocal.SERVICE_NAME, mappedName = IPullPushDAOLocal.SERVICE_NAME, description = "")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class PullPushDAO extends JPAGenericDAORulesBased implements IPullPushDAOLocal {

    /**
     * Default constructor. 
     */
    public PullPushDAO() {}

	/**
	 * Le gestionnaire d'entites
	 */
	@PersistenceContext(unitName = "pullpush")
	private EntityManager manager;

	
	public EntityManager getEntityManager() {
		return manager;
	}

	
}
