package com.afb.dpd.mobilemoney.dao.api.interfaces;

import com.afb.dpd.mobilemoney.dao.api.ClientProduitDAO;
import com.afb.dpd.mobilemoney.dao.api.SendEventToCbsDAO;

public interface IMobileMoneyDAOAPILocal {
	
	/**
	 * Nom du Service DAO
	 */
	public static final String SERVICE_NAME = "MobileMoneyDAOAPILocal";
	
	public void reload();
	
	public ClientProduitDAO getClientProduitDAO();
	public SendEventToCbsDAO getSendEventToCbsDAO();
}
