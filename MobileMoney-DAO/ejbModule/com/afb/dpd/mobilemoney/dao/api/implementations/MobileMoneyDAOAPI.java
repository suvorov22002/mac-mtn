package com.afb.dpd.mobilemoney.dao.api.implementations;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.afb.dpd.mobilemoney.dao.api.ClientProduitDAO;
import com.afb.dpd.mobilemoney.dao.api.SendEventToCbsDAO;
import com.afb.dpd.mobilemoney.dao.api.interfaces.IMobileMoneyDAOAPILocal;

@Stateless(name=IMobileMoneyDAOAPILocal.SERVICE_NAME,mappedName=IMobileMoneyDAOAPILocal.SERVICE_NAME)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MobileMoneyDAOAPI implements IMobileMoneyDAOAPILocal{
	
	private ClientProduitDAO clientProduitDAO;
	private SendEventToCbsDAO sendEventToCbsDAO;
	
	public MobileMoneyDAOAPI() {
		init();
	}
	
	@PostConstruct
	public void init() {
		this.clientProduitDAO = new ClientProduitDAO();
		this.sendEventToCbsDAO = new SendEventToCbsDAO();
	}
	
	public void reload(){
		this.init();
	}
	
	@Override
	public ClientProduitDAO getClientProduitDAO() {
		// TODO Auto-generated method stub
		return this.clientProduitDAO;
	}

	@Override
	public SendEventToCbsDAO getSendEventToCbsDAO() {
		// TODO Auto-generated method stub
		return sendEventToCbsDAO;
	}
}
