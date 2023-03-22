package com.afb.dpd.mobilemoney.dao;

import javax.ejb.Local;

import com.yashiro.persistence.utils.dao.IJPAGenericDAO;

/**
 * Service DAO Local de Gestion du PullPush
 * @author Francis DJIOMOU
 * @version 2.0
 */
@Local
public interface IPullPushDAOLocal extends IJPAGenericDAO {

	/**
	 * Nom du Service DAO de gestion des utilisateurs
	 */
	public static final String SERVICE_NAME = "PullPushDAOLocal";
	
}
