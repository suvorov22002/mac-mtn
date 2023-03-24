package com.afb.dpd.mobilemoney.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.naming.InitialContext;
import javax.persistence.EntityExistsException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.codehaus.jettison.json.JSONException;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.annotation.ejb.TransactionTimeout;

import com.afb.dpd.mobilemoney.dao.IMobileMoneyDAOLocal;
import com.afb.dpd.mobilemoney.dao.IPullPushDAOLocal;
import com.afb.dpd.mobilemoney.dao.api.exception.DAOAPIException;
import com.afb.dpd.mobilemoney.dao.api.interfaces.IMobileMoneyDAOAPILocal;
import com.afb.dpd.mobilemoney.jpa.entities.AccountInfos;
import com.afb.dpd.mobilemoney.jpa.entities.AccountUtils;
import com.afb.dpd.mobilemoney.jpa.entities.Commissions;
import com.afb.dpd.mobilemoney.jpa.entities.Comptabilisation;
import com.afb.dpd.mobilemoney.jpa.entities.FactMonth;
import com.afb.dpd.mobilemoney.jpa.entities.FactMonthDetails;
import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.ParametreAlertes;
import com.afb.dpd.mobilemoney.jpa.entities.PlageTransactions;
import com.afb.dpd.mobilemoney.jpa.entities.RequestMessage;
import com.afb.dpd.mobilemoney.jpa.entities.Resiliation;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.TraceRobot;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.entities.USSDTransaction;
import com.afb.dpd.mobilemoney.jpa.enums.ExceptionCategory;
import com.afb.dpd.mobilemoney.jpa.enums.ExceptionCode;
import com.afb.dpd.mobilemoney.jpa.enums.ModeFacturation;
import com.afb.dpd.mobilemoney.jpa.enums.Periodicite;
import com.afb.dpd.mobilemoney.jpa.enums.StatutContrat;
import com.afb.dpd.mobilemoney.jpa.enums.StatutService;
import com.afb.dpd.mobilemoney.jpa.enums.TransactionStatus;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jpa.enums.TypeValeurFrais;
import com.afb.dpd.mobilemoney.jpa.exception.MoMoException;
import com.afb.dpd.mobilemoney.jpa.tools.ClientProduit;
import com.afb.dpd.mobilemoney.jpa.tools.Doublon;
import com.afb.dpd.mobilemoney.jpa.tools.Equilibre;
import com.afb.dpd.mobilemoney.jpa.tools.EquilibreComptes;
import com.afb.dpd.mobilemoney.jpa.tools.MoMoHelper;
import com.afb.dpd.mobilemoney.jpa.tools.Queries;
import com.afb.dpd.mobilemoney.jpa.tools.TypeCompte;
import com.afb.dpd.mobilemoney.jpa.tools.bkeve;
import com.afb.dpd.mobilemoney.jpa.tools.bkmvti;
import com.afb.dsi.alertes.AfrilandSendMail;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
//import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.yashiro.persistence.utils.annotations.AllowedRole;
import com.yashiro.persistence.utils.annotations.ProtectedClass;
import com.yashiro.persistence.utils.collections.converters.ConverterUtil;
import com.yashiro.persistence.utils.dao.tools.AliasesContainer;
import com.yashiro.persistence.utils.dao.tools.LoaderModeContainer;
import com.yashiro.persistence.utils.dao.tools.OrderContainer;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;
import com.yashiro.persistence.utils.dao.tools.SaveListResult;
import com.yashiro.persistence.utils.dao.tools.encrypter.Encrypter;

import afb.dsi.dpd.portal.business.audit.shared.AuditModuleInterceptor;
import afb.dsi.dpd.portal.business.facade.IFacadeManagerRemote;
import afb.dsi.dpd.portal.jpa.entities.Branch;
import afb.dsi.dpd.portal.jpa.entities.DataSystem;
import afb.dsi.dpd.portal.jpa.entities.SMSWeb;
import afb.dsi.dpd.portal.jpa.entities.User;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;
import entreeRela.jpa.entities.ProcessUser;
import entreeRela.jpa.enums.Etape;
import entreeRela.jpa.enums.TypeClient;

/**
 * Implementation du service metier de Gestion du PersoCheque
 * @author Francis DJIOMOU
 * @version 2.0
 */
@ProtectedClass(system = "Mobile Money", methods = {} )
@Interceptors(value = {AuditModuleInterceptor.class})
@Stateless(name = IMobileMoneyManagerRemote.SERVICE_NAME, mappedName = IMobileMoneyManagerRemote.SERVICE_NAME, description = "")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MobileMoneyManager implements IMobileMoneyManagerRemote {

	/**
	 * Default constructor. 
	 */
	public MobileMoneyManager() {}

	/**
	 * Le Logger
	 */
	private static Log logger = LogFactory.getLog(MobileMoneyManager.class);

	/**
	 * Injection de l'EJB Facade du Portail
	 */
	@EJB
	private IMobileMoneyDAOLocal mobileMoneyDAO;
	
	/**
	 * Injection de l'EJB de l'API
	 */
	@EJB
	private IMobileMoneyDAOAPILocal  mobileMoneyAPI;
	
//	@EJB(lookup = "java:global/EntreeRelationCOM-Bussiness/EntreeRelaManager!EntreeRelaManager.IEntreeRelaManagerRemote")
//	@EJB(beanName = "../EntreeRelationCOM-Bussiness.jar#EntreeRelaManager")
//	@EJB(mappedName = "java:global/EntreeRelationCOM-Bussiness/EntreeRelaManager!entreeRela.metier.utils.IEntreeRelaManagerRemote")
//	private IEntreeRelaManagerRemote entreeRelaManager;
	
	/**
	 * Injection de l'EJB PullPush
	 */
	@EJB
	private IPullPushDAOLocal pullpushDAO;

	private Connection conCBS = null;
	private DataSystem dsCBS = null;
	private Parameters params = null;

	/**
	 * Nbre de fois qu'il faut tester (dans le Timer) la fermeture de l'agence centrale (Demarrage des TFJ)
	 */
	private int delaiCtrlTFJDelta = 1;

	/**
	 * Frequence d'execution du Timer (15min)
	 */
	private long nbMinutesCtrlFinTFJ = 900;
	
	// ADD
	private  Double mntD = 0d;
	private  Double mntC = 0d;
	private  Integer nbrC = 0;
	private  Integer nbrD = 0;
	private  String dev = "";
	private  List<FactMonthDetails> details = new ArrayList<FactMonthDetails>();
	// FIN ADD
	public final static String DATE_HOUR_FORMAT_TT="yyMMddHHmmssSSSS";
	
	public Long now(){
//		String _format = RandomStringUtils.randomNumeric(4);
//		_format = "";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_HOUR_FORMAT_TT);
		String format = simpleDateFormat.format(new Date());
		//logger.info("IDIDID "+format);
		return Long.valueOf(format) ;
	}

	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#activerSubscriptions()
	 */
	@Override
	@AllowedRole(name = "activerSubscriptions", displayName = "MoMo.ActiverSubscriptions")
	public void activerSubscriptions() {
		mobileMoneyDAO.filter(PlageTransactions.class,null,null,null,null,0,-1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#profilSubscriptions()
	 */
	@Override
	@AllowedRole(name = "profilSubscriptions", displayName = "MoMo.ProfilSubscriptions")
	public void profilSubscriptions() {
		mobileMoneyDAO.filter(PlageTransactions.class,null,null,null,null,0,-1);
	}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#saveParameters(com.afb.dpd.mobilemoney.jpa.entities.Parameters)
	 */
	@Override
	@AllowedRole(name = "saveParameters", displayName = "MoMo.SaveParameters")
	public Parameters saveParameters(Parameters param) {
		// TODO Auto-generated method stub
		params = mobileMoneyDAO.update(param);
		return params;
	}


	@Override
	public List<ParametreAlertes> consulterParametreAlertes(){
		return mobileMoneyDAO.filter(ParametreAlertes.class, null, null, OrderContainer.getInstance().add(Order.asc("codeAgence")), null, 0, -1);
	}


	@Override
	@AllowedRole(name = "saveParametreAlertes", displayName = "Momo.saveParametreAlertes")
	public ParametreAlertes saveParametreAlertes(ParametreAlertes pa) {
		// TODO Auto-generated method stub
		return mobileMoneyDAO.save(pa);
	}


	@Override
	@AllowedRole(name = "saveParametreAlertes", displayName = "Momo.saveParametreAlertes")
	public List<ParametreAlertes> saveListParametreAlertes(List<ParametreAlertes> list) {
		// TODO Auto-generated method stub
		SaveListResult<ParametreAlertes> slr = mobileMoneyDAO.saveList(list, true);
		return slr.getRegistered();
	}


	@Override
	@AllowedRole(name = "saveParametreAlertes", displayName = "Momo.saveParametreAlertes")
	public ParametreAlertes updateParametreAlertes(ParametreAlertes pa) {
		// TODO Auto-generated method stub
		return mobileMoneyDAO.update(pa);
	}


	public List<String> getEmailsAlerteAgence(String code){
		List<ParametreAlertes> listPA = new ArrayList<ParametreAlertes>();
		listPA = mobileMoneyDAO.filter(ParametreAlertes.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("codeAgence", code)), OrderContainer.getInstance().add(Order.asc("codeAgence")), null, 0, -1);
		if(listPA.isEmpty()) return new ArrayList<String>();
		return listPA.get(0).getEmails();
	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#savePlageTransactions(java.util.List)
	 */
	@Override
	@AllowedRole(name = "savePlageTransactions", displayName = "MoMo.savePlageTransactions")
	public void savePlageTransactions(List<PlageTransactions> plages){
		// TODO Auto-generated method stub
		for(PlageTransactions plg : plages) mobileMoneyDAO.update(plg);
	}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#saveSubscriber(com.afb.dpd.mobilemoney.jpa.entities.Subscriber)
	 */
	@Override
	@AllowedRole(name = "saveSubscriber", displayName = "MoMo.CreateSubscriber")
	public Subscriber saveSubscriber(Subscriber subscriber) throws Exception {

		// Lecture de la date de soscription
		subscriber.setDate(new Date());
		subscriber.setDateDernCompta(new Date());

		// Log
		logger.info("Generation du Code PIN du client");

		// Generation du PIN banque
		subscriber.setBankPIN( generateBankPIN() );

		// Log
		//logger.info("Recherche des parametres");

		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		// Log
		//logger.info("recherche des commissions sur la souscription");

		Map<TypeOperation, Commissions> mapComs = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation");

		// MAJ des infos de facturation
		subscriber.setCommissions( subscriber.getCommissions() + mapComs.get(TypeOperation.PULL).getValeur() );
		subscriber.setCommissions( subscriber.getCommissions() + mapComs.get(TypeOperation.PUSH).getValeur() );
		subscriber.setPeriod( mapComs.get(TypeOperation.PUSH).getPeriodFacturation() );
		Boolean facturer = isClientEmploye(subscriber.getCustomerId()) ? Boolean.FALSE : Boolean.TRUE;
		// Specifier s'il s'agit d'un employe ou non
		subscriber.setFacturer(facturer);
		//subscriber.setEmploye(!facturer);

		// Creation de la souscription
		subscriber = mobileMoneyDAO.save(subscriber);

		// Lecture de la commission du type operation
		Commissions coms = mapComs.get(TypeOperation.SUBSCRIPTION);

		// Si des commissions ont ete parametres sur l'operation
		if(coms != null && coms.getValeur() > 0) {

			// Log
			//logger.info("Il existe des commissions sur la souscription! On initialise la transaction a poster dans Delta");

			// Creation d'une transaction
			Transaction transaction = new Transaction(TypeOperation.SUBSCRIPTION, subscriber, 0d, subscriber.getAccounts().get(0), subscriber.getPhoneNumbers().get(0)) ;

			// Log
			//logger.info("Postage de l'evenement dans le CBS");

			// Postage de l'evenement dans le CBS
			posterEvenementDansCoreBanking(transaction);

		} else {
			//logger.info("Creation de la souscription");
			// Creation de la souscription
			subscriber = mobileMoneyDAO.save(subscriber);

			// Log
			//logger.info("Enregistrement du souscripteur");

		}

		// Log
		logger.info("Envoi du code PIN au client par SMS");

		// Envoi du code PIN Banque par SMS
		sendCodePINBySMS(subscriber);

		coms = null; 

		return subscriber;
	}


	/**
	 * 
	 * @param subscriber
	 * @return
	 * @throws Exception
	 */
	public Subscriber saveSubscriberNew(Subscriber subscriber) throws Exception {

		// Lecture de la date de soscription
		subscriber.setDate(new Date());

		// Generation du PIN banque
		subscriber.setBankPIN( generateBankPIN() );

//		checkGlobalConfig();
		params = findParameters();

		Map<TypeOperation, Commissions> mapComs = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation");

		// MAJ des infos de facturation
		subscriber.setCommissions( subscriber.getCommissions() + mapComs.get(TypeOperation.PULL).getValeur() );
		subscriber.setCommissions( subscriber.getCommissions() + mapComs.get(TypeOperation.PUSH).getValeur() );
		subscriber.setPeriod( mapComs.get(TypeOperation.PUSH).getPeriodFacturation() );
		subscriber.setFacturer( isClientEmploye(subscriber.getCustomerId()) ? Boolean.FALSE : Boolean.TRUE );
		subscriber.setDateDernCompta(new Date());

		// Creation de la souscription
		subscriber = mobileMoneyDAO.save(subscriber);

		return subscriber;
	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#saveSubscriber(com.afb.dpd.mobilemoney.jpa.entities.Subscriber)
	 */
	@Override
	@AllowedRole(name = "saveSubscriber", displayName = "MoMo.CreateSubscriber")
	public Subscriber saveSubscriberECW(Subscriber subscriber) throws Exception {

		// Lecture de la date de soscription
		subscriber.setDate(new Date());
		subscriber.setDateDernCompta(new Date());

		// Log
		logger.info("Generation du Code PIN du client");

		// Generation du PIN banque
		subscriber.setBankPIN( generateBankPIN() );

		// Log
		//logger.info("Recherche des parametres");

		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		// Log
		//logger.info("recherche des commissions sur la souscription");

		Map<TypeOperation, Commissions> mapComs = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation");

		// MAJ des infos de facturation
		subscriber.setCommissions( subscriber.getCommissions() + mapComs.get(TypeOperation.PULL).getValeur() );
		subscriber.setCommissions( subscriber.getCommissions() + mapComs.get(TypeOperation.PUSH).getValeur() );
		subscriber.setPeriod( mapComs.get(TypeOperation.PUSH).getPeriodFacturation() );
		
		// Specifier s'il s'agit d'un employe ou non
		Boolean facturer = isClientEmploye(subscriber.getCustomerId()) ? Boolean.FALSE : Boolean.TRUE;
		
		// Specifier s'il s'agit d'un employe ou non, ou s'il a souscrit à un package contenant le produit spécifique
	//	ClientProduit client = new ClientProduit();
	//	client.setMatricule(subscriber.getFirstAccount().split("-")[1].substring(0,7));
	//	client.setProduit("MoMo-06");
	//	String statut = statusAbon(client);
	//	System.out.println("STATUT: "+statut);
	//	statut = statut.replace('"', ' ').trim();
	//	client.setStatut(StatusAbon.valueOf(StatusAbon.class, String.valueOf(statut)));
		
	//	Boolean facturer = isClientEmploye(subscriber.getCustomerId()) ? Boolean.FALSE : client.getStatut().equals(StatusAbon.FACTURE) ? Boolean.FALSE : Boolean.TRUE;
		 
		subscriber.setFacturer(facturer);
		//subscriber.setEmploye(!facturer);

		// Creation de la souscription
		subscriber = mobileMoneyDAO.save(subscriber);
		// Log
		//logger.info("Enregistrement du souscripteur");

		// Log
		//		logger.info("Envoi du code PIN au client par SMS");

		// Envoi du code PIN Banque par SMS
		//		sendCodePINBySMS(subscriber);

		return subscriber;
	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#updateSubscriber(com.afb.dpd.mobilemoney.jpa.entities.Subscriber)
	 */
	@Override
	@AllowedRole(name = "updateSubscriber", displayName = "MoMo.UpdateSubscriber")
	public Subscriber updateSubscriber(Subscriber subscriber) throws Exception {

		// TODO Auto-generated method stub
		//return mobileMoneyDAO.update(subscriber);

		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		// Lecture de la commission du type operation
		Commissions coms = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation").get(TypeOperation.MODIFY);

		// Si des commissions ont ete parametres sur l'operation
		if(coms != null && coms.getValeur() > 0) {

			// Creation d'une transaction
			// Transaction transaction = new Transaction(TypeOperation.MODIFY, subscriber, 0d, subscriber.getAccounts().get(0), subscriber.getPhoneNumbers().get(0)) ;
			Transaction transaction = new Transaction(TypeOperation.MODIFY, subscriber, 0d, subscriber.getAccounts().get(0), subscriber.getPhoneNumbers().get(0), "") ;

			// Postage de l'evenement dans le CBS
			posterEvenementDansCoreBanking(transaction);

		}		

		return mobileMoneyDAO.update(subscriber);

	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#updateBankPIN(com.afb.dpd.mobilemoney.jpa.entities.Subscriber)
	 */
	@Override
	@AllowedRole(name = "updateBankPIN", displayName = "Momo.UpdateBankPIN")
	public Subscriber updateBankPIN(Subscriber subscriber) throws Exception {

		// Generation du PIN banque
		subscriber.setBankPIN( generateBankPIN() );

		// Creation de la souscription
		//subscriber = mobileMoneyDAO.update(subscriber);

		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		// Lecture de la commission du type operation
		Commissions coms = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation").get(TypeOperation.MODIFY);

		// Si des commissions ont ete parametres sur l'operation
		if(coms != null && coms.getValeur() > 0) {

			// Creation d'une transaction
			Transaction transaction = new Transaction(TypeOperation.MODIFY, subscriber, 0d, subscriber.getAccounts().get(0), subscriber.getPhoneNumbers().get(0), "") ;

			// Postage de l'evenement dans le CBS
			posterEvenementDansCoreBanking(transaction);

		}		

		subscriber = mobileMoneyDAO.update(subscriber);

		sendCodePINBySMS(subscriber);

		// Modification du souscripteur
		return subscriber;
	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#deleteSubscriber(java.lang.Long)
	 */
	@Override
	@AllowedRole(name = "deleteSubscriber", displayName = "MoMo.DeleteSubscriber")
	public void deleteSubscriber(Long subscriberId) {
		// TODO Auto-generated method stub
		mobileMoneyDAO.delete( mobileMoneyDAO.findByPrimaryKey(Subscriber.class, subscriberId, null) );
	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#filterSubscriptions(com.yashiro.persistence.utils.dao.tools.RestrictionsContainer, com.yashiro.persistence.utils.dao.tools.OrderContainer)
	 */
	@Override
	@AllowedRole(name = "filterSubscriptions", displayName = "MoMo.FilterSubscriptions")
	public List<Subscriber> filterSubscriptions(RestrictionsContainer rc, OrderContainer orders) {
		// TODO Auto-generated method stub
		return mobileMoneyDAO.filter(Subscriber.class, null, rc, orders, null, 0, -1);
		//return mobileMoneyDAO.filter(Subscriber.class, AliasesContainer.getInstance().add("user", "user"), rc, orders, null, 0, -1);
	}
	
	@Override
	public List<Subscriber> filterSubscription(RestrictionsContainer rc, OrderContainer orders) {
		// TODO Auto-generated method stub
		return mobileMoneyDAO.filter(Subscriber.class, null, rc, orders, null, 0, -1);

	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#filterSubscriptions(com.yashiro.persistence.utils.dao.tools.RestrictionsContainer, com.yashiro.persistence.utils.dao.tools.OrderContainer)
	 */
	@Override
	@AllowedRole(name = "filterSubscriptions", displayName = "MoMo.FilterSubscriptions")
	public List<Subscriber> filterSubscriptionsWithoutAlias(RestrictionsContainer rc, OrderContainer orders) {
		// TODO Auto-generated method stub
		return mobileMoneyDAO.filter(Subscriber.class, null, rc, orders, null, 0, -1);
	}


	@Override
	@AllowedRole(name = "executerBulkLinkage", displayName = "MoMo.ExecuterBulkLinkage")
	public void executerBulkLinkage(){

	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#saveSubscriber(com.afb.dpd.mobilemoney.jpa.entities.Subscriber)
	 */
	@Override
	public Subscriber facturerSouscription(Subscriber subscriber) throws Exception {

		// Log
		//logger.info("Recherche des parametres");

		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		// Log
		//logger.info("recherche des commissions sur la souscription");

		Map<TypeOperation, Commissions> mapComs = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation");
		
		// Lecture de la commission du type operation
		Commissions coms = mapComs.get(TypeOperation.SUBSCRIPTION);
		
		// Si des commissions ont ete parametres sur l'operation
		if(coms != null && coms.getValeur() > 0) {

			// Log
			//logger.info("Il existe des commissions sur la souscription! On initialise la transaction a poster dans Delta");
			String trxID = null;
			// Creation d'une transaction
			Transaction transaction = new Transaction(TypeOperation.SUBSCRIPTION, subscriber, 0d, subscriber.getAccounts().get(0), subscriber.getPhoneNumbers().get(0), trxID) ;

			// Log
			//logger.info("Postage de l'evenement dans le CBS");

			// Postage de l'evenement dans le CBS
			posterEvenementDansCoreBanking(transaction);

		} else {
			//logger.info("Creation de la souscription");
			// Creation de la souscription
			subscriber = mobileMoneyDAO.save(subscriber);

			// Log
			//logger.info("Enregistrement du souscripteur");

		}

		coms = null; 

		return subscriber;
	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#annulerSouscription(java.lang.Long)
	 */
	@Override
	@AllowedRole(name = "annulerSouscription", displayName = "MoMo.AnnulerSubscriptions")
	public void annulerSouscription(Long sousId, String login) throws Exception {

		// Recuperation du souscripteur
		Subscriber subscriber = mobileMoneyDAO.findByPrimaryKey(Subscriber.class, sousId, null);

		// Recherche des parametres
		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		// Lecture de la commission du type operation
		Commissions coms = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation").get(TypeOperation.CANCEL);

		// Si des commissions ont ete parametres sur l'operation
		if(coms != null && coms.getValeur() > 0) {

			// Creation d'une transaction
			// Transaction transaction = new Transaction(TypeOperation.SUBSCRIPTION, subscriber, 0d, subscriber.getAccounts().get(0), subscriber.getPhoneNumbers().get(0)) ;
			Transaction transaction = new Transaction(TypeOperation.SUBSCRIPTION, subscriber, 0d, subscriber.getAccounts().get(0), subscriber.getPhoneNumbers().get(0), "") ;

			// Postage de l'evenement dans le CBS
			posterEvenementDansCoreBanking(transaction);

		}

		// MAJ du statut de la souscription
		// Suspendre la souscription
		subscriber.setFacturer(false);
		subscriber.setStatus(StatutContrat.SUSPENDU);
		subscriber.setDateSuspendu(new Date());
		subscriber.setUtiSuspendu(login);
		subscriber.setActive(false);
		mobileMoneyDAO.save(subscriber);
		//mobileMoneyDAO.getEntityManager().createQuery("Update Subscriber s set s.status = :status where s.id = :id").setParameter("status", StatutContrat.SUSPENDU).setParameter("id", sousId).executeUpdate();


	}

	@Override
	@AllowedRole(name = "voidSusTemporaire", displayName = "MoMo.SuspensTemp")
	public void annulerSouscriptionTemp(Long sousId, String login) throws Exception {
		// Recuperation du souscripteur
		Subscriber subscriber = mobileMoneyDAO.findByPrimaryKey(Subscriber.class, sousId, null);

		// Recherche des parametres
		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		// Lecture de la commission du type operation
		Commissions coms = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation").get(TypeOperation.CANCEL);

		// Si des commissions ont ete parametres sur l'operation
		if(coms != null && coms.getValeur() > 0) {

			// Creation d'une transaction
			// Transaction transaction = new Transaction(TypeOperation.SUBSCRIPTION, subscriber, 0d, subscriber.getAccounts().get(0), subscriber.getPhoneNumbers().get(0)) ;
			Transaction transaction = new Transaction(TypeOperation.SUBSCRIPTION, subscriber, 0d, subscriber.getAccounts().get(0), subscriber.getPhoneNumbers().get(0), "") ;

			// Postage de l'evenement dans le CBS
			posterEvenementDansCoreBanking(transaction);

		}

		// MAJ du statut de la souscription
		// Suspendre la souscription
		subscriber.setFacturer(false);
		subscriber.setStatus(StatutContrat.SUSPENDU);
		subscriber.setDateSuspendu(new Date());
		subscriber.setUtiSuspendu(login);
		subscriber.setActive(false);
		subscriber.setSuspensTemp((subscriber.getSuspensTemp()!=null ? subscriber.getSuspensTemp()+"_":"")+login+"|"+now()+"|"+"S" );
		mobileMoneyDAO.save(subscriber);
		//mobileMoneyDAO.getEntityManager().createQuery("Update Subscriber s set s.status = :status where s.id = :id").setParameter("status", StatutContrat.SUSPENDU).setParameter("id", sousId).executeUpdate();
	}
	
	@Override
	@AllowedRole(name = "annulerSouscription", displayName = "MoMo.AnnulerSubscriptions")
	public void annulerFacturation(Long sousId, String login) throws Exception {
		
		// Recuperation du souscripteur
		Subscriber subscriber = mobileMoneyDAO.findByPrimaryKey(Subscriber.class, sousId, null);
		
		// MAJ du statut de la souscription
		// Suspendre la souscription
		subscriber.setFacturer(false);
		subscriber.setStatus(StatutContrat.ACTIF_CBS);
		//subscriber.setDateSuspendu(new Date());
		//subscriber.setUtiSuspendu(login);
		subscriber.setActive(false);
		mobileMoneyDAO.save(subscriber);
	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#annulerSouscription(java.lang.Long)
	 */
	@Override
	@AllowedRole(name = "annulerSouscription", displayName = "MoMo.AnnulerSubscriptions")
	public void annulerSouscriptionECW(Long sousId, String login) throws Exception {
		//logger.info("Annuler abonnement ECW");
		Subscriber subscriber = new Subscriber();
		RestrictionsContainer rc = RestrictionsContainer.getInstance();
		rc.add(Restrictions.eq("op", TypeOperation.SUBSCRIPTION ));
		OrderContainer ord = OrderContainer.getInstance().add(Order.desc("date_op"));
		// Recuperation de la transaction
		List<Transaction> trx = new ArrayList<Transaction>();
		trx = filterTransactions(rc, ord);

		if(!trx.isEmpty()) {
			//logger.info("Trx trouvee");
			processReversalTransactionECW(trx.get(0).getId().toString());
			//logger.info("Annulation effectuee avec succes");
			subscriber = trx.get(0).getSubscriber();
			// MAJ du statut de la souscription
			// Suspendre la souscription
			subscriber.setFacturer(false);
			subscriber.setStatus(StatutContrat.SUSPENDU);
			subscriber.setDateSuspendu(new Date());
			subscriber.setUtiSuspendu(login);
			subscriber.setActive(false);
			mobileMoneyDAO.save(subscriber);
			//logger.info("Mise a jour de l'abonne avec succes");
		}
		else{
			new Exception("Impossible d'annuler la souscription : Transaction introuvable");
		}

	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#deleteSouscription(java.lang.Long)
	 */
	@Override
	@AllowedRole(name = "deleteSouscription", displayName = "MoMo.SupprimerSubscriptions")
	public void deleteSouscription(Long sousId) {
		mobileMoneyDAO.delete( mobileMoneyDAO.findByPrimaryKey(Subscriber.class, sousId, null) );
	}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#subscriptionAlreadyExist(java.lang.String)
	 */
	public boolean subscriptionAlreadyExist(String customerId) {

		// Recherche des souscriptions du client 
		List<Subscriber> liste = mobileMoneyDAO.
				filter(Subscriber.class, null, RestrictionsContainer.getInstance().
						add(Restrictions.eq("customerId", customerId)).
						add(Restrictions.or(Restrictions.eq("status", StatutContrat.ACTIF), Restrictions.or(Restrictions.eq("status", StatutContrat.WAITING), Restrictions.eq("status", StatutContrat.ACTIF_CBS)))), null, null, 0, -1);

		// Calcul du resultat a retourner
		boolean resultat = liste != null && !liste.isEmpty();

		// Libere la variable
		liste.clear(); liste = null;

		// Retourne le resultat
		return resultat;
	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#subscriptionAlreadyExist(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public boolean subscriptionAlreadyExistECW(String customerId, String phoneNumber) {

		// Recherche du souscripteur possedant le code client et le numero de Tel du client
		List<Subscriber> liste = mobileMoneyDAO.getEntityManager().createQuery("From Subscriber s left join fetch s.phoneNumbers phones where s.customerId = '"+ customerId +"' and ( status = '"+StatutContrat.ACTIF+"' or status = '"+StatutContrat.ACTIF_CBS+"' ) and phones in ('"+ phoneNumber +"')").getResultList();

		//List<Subscriber> liste = mobileMoneyDAO.filter(Subscriber.class, AliasesContainer.getInstance().add("phoneNumbers", "phone"), RestrictionsContainer.getInstance().add(Restrictions.eq("customerId", customerId)).add(Restrictions.eq("status", StatutContrat.ACTIF)), null, null, 0, -1);

		// Calcul du resultat a retourner
		boolean resultat = liste != null && !liste.isEmpty();

		// Libere la variable
		liste.clear(); liste = null;

		// Retourne le resultat
		return resultat;
	}
	
	
	public Subscriber findSubscriber(String customerId) {

		// Recherche des souscriptions du client 
		List<Subscriber> liste = mobileMoneyDAO.filter(Subscriber.class, null, RestrictionsContainer.getInstance()
			.add(Restrictions.eq("customerId", customerId))
		    .add(Restrictions.or(Restrictions.eq("status", StatutContrat.ACTIF), Restrictions.eq("status", StatutContrat.ACTIF_CBS))), null, null, 0, -1);

		// Retourne le resultat
		return liste != null && !liste.isEmpty() ? liste.get(0) : null;
	}


	/**
	 * Genere le PIN Banque
	 * @return PIN Banque genere
	 */
	@SuppressWarnings("rawtypes")
	private String generateBankPIN() {

		String pin = "";

		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		List<String> pins = new ArrayList<String>();

		List<String> savedPins = new ArrayList<String>();

		for(int i=params.getMinPIN(); i<=params.getMaxPIN(); i++) pins.add( String.valueOf(i) );

		List saved = mobileMoneyDAO.getEntityManager().createQuery("Select s.bankPIN from Subscriber s").getResultList();

		if(saved != null) {

			for(Object o : saved) savedPins.add( o.toString()  );

		}

		pins.removeAll(savedPins);

		int rIndex = new Random().nextInt( pins.size() );

		pin = MoMoHelper.padText( pins.get(rIndex-1), MoMoHelper.LEFT, params.getBankPINLength(), "0");

		savedPins.clear(); pins.clear();

		return pin;

	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#findSubscriberFromPhoneNumber(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Subscriber verifySubscriberFromPhoneNumber(String phoneNumber) {

		// Recherche du souscripteur possedant le numero de Tel du message 
		List<Subscriber> subs = mobileMoneyDAO.getEntityManager().createQuery("From Subscriber s left join fetch s.phoneNumbers phones where phones in ('"+ phoneNumber +"') and (s.status=:statusWaiting or s.status=:statusActif or s.status=:statusCbs) ").setParameter("statusWaiting", StatutContrat.WAITING).setParameter("statusActif", StatutContrat.ACTIF).setParameter("statusCbs", StatutContrat.ACTIF_CBS).getResultList(); // mobileMoneyDAO.filter(Subscriber.class, AliasesContainer.getInstance().add("phoneNumbers", "phones"), RestrictionsContainer.getInstance().add(Restrictions.in("phones", new Object[]{phoneNumber}  )), null, null, 0, -1);

		if( subs != null && !subs.isEmpty() ) {
			return isCompteFerme(subs.get(0).getAccounts().get(0)) ? null : subs.get(0);
		}
		else return null;

		// Retourne le souscripteur trouve
		// return subs != null && !subs.isEmpty() && !isCompteFerme(subs.get(0).getAccounts().get(0)) ? subs.get(0) : null;
	}	


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#findSubscriberFromPhoneNumber(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Subscriber findSubscriberFromPhoneNumber(String phoneNumber) {

		// Recherche du souscripteur possedant le numero de Tel du message 
		List<Subscriber> subs = mobileMoneyDAO.getEntityManager().createQuery("From Subscriber s left join fetch s.phoneNumbers phones where phones in ('"+ phoneNumber +"') and ( s.status=:status or s.status=:_status )  ").setParameter("status", StatutContrat.ACTIF).setParameter("_status", StatutContrat.ACTIF_CBS).getResultList(); // mobileMoneyDAO.filter(Subscriber.class, AliasesContainer.getInstance().add("phoneNumbers", "phones"), RestrictionsContainer.getInstance().add(Restrictions.in("phones", new Object[]{phoneNumber}  )), null, null, 0, -1);

		if( subs != null && !subs.isEmpty() ) {
			return isCompteFerme(subs.get(0).getAccounts().get(0)) ? null : subs.get(0);
		}
		else return null;

		// Retourne le souscripteur trouve
		// return subs != null && !subs.isEmpty() && !isCompteFerme(subs.get(0).getAccounts().get(0)) ? subs.get(0) : null;
	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#findSubscriberFromPhoneNumber(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Subscriber findSubscriberFromPhoneNumber(String phoneNumber, Long subsID) {

		// Recherche du souscripteur possedant le numero de Tel du message 
		List<Subscriber> subs = mobileMoneyDAO.getEntityManager().createQuery("From Subscriber s left join fetch s.phoneNumbers phones where s.id <> "+ subsID +" and phones in ('"+ phoneNumber +"') and ( s.status=:status or s.status=:_status ) ").setParameter("status", StatutContrat.ACTIF).setParameter("_status", StatutContrat.ACTIF_CBS).getResultList();

		// Retourne le souscripteur trouve
		return subs != null && !subs.isEmpty() ? subs.get(0) : null;
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#generateTransactionFromMessage(com.afb.dpd.mobilemoney.jpa.entities.RequestMessage)
	 */
	@Override
	public Transaction generateTransactionFromMessage(RequestMessage message) {

		// Recherche du souscripteur possedant le numero de Tel du message 
		Subscriber sub = findSubscriberFromPhoneNumber( message.getPhoneNumber() );

		// Retourne la Transaction
		// return sub != null ? new Transaction(message.getOperation(), sub, message.getAmount(), (message.getAccount() != null ? message.getAccount() : sub.getAccounts().get(0) ), message.getPhoneNumber()) : null;
		return sub != null ? new Transaction(message.getOperation(), sub, message.getAmount(), (message.getAccount() != null ? message.getAccount() : sub.getAccounts().get(0) ), message.getPhoneNumber(), "") : null;

	}

	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#saveTransaction(com.afb.dpd.mobilemoney.jpa.entities.Transaction)
	 */
	@Override
	public Transaction saveTransaction(Transaction transaction) {
		// TODO Auto-generated method stub
		return mobileMoneyDAO.save(transaction);
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#updateTransaction(com.afb.dpd.mobilemoney.jpa.entities.Transaction)
	 */
	@Override
	public Transaction updateTransaction(Transaction transaction) {
		// TODO Auto-generated method stub
		return mobileMoneyDAO.update(transaction);
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#filterTransactions(com.yashiro.persistence.utils.dao.tools.RestrictionsContainer, com.yashiro.persistence.utils.dao.tools.OrderContainer)
	 */
	@Override
	@AllowedRole(name = "filterTransactions", displayName = "MoMo.FilterTransactions")
	public List<Transaction> filterTransactions(RestrictionsContainer rc, OrderContainer orders) {
		// TODO Auto-generated method stub
		return mobileMoneyDAO.filter(Transaction.class, AliasesContainer.getInstance().add("subscriber", "subscriber"), rc, orders, null, 0, -1);
	}

	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#purgeTransactions(java.util.List)
	 *
	@Override
	@AllowedRole(name = "purgeTransactions", displayName = "MoMo.PurgeTransactions")
	public void purgeTransactions(List<Transaction> transactions) {
		mobileMoneyDAO.delete(transactions);
	}*/

	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#generateAccountingEntries(java.util.List)
	 */
	@Override
	@AllowedRole(name = "generateAccountingEntries", displayName = "MoMo.GenerateAccountingEntries")
	public void generateAccountingEntries(List<Transaction> transactions) {
		// TODO Auto-generated method stub

	}

	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#initialisations()
	 */
	@Override
	public void initialisations() {
		
		// Recherche des parametres par defaut
		//		Parameters params = mobileMoneyDAO.findByPrimaryKey(Parameters.class, Encrypter.getInstance().hashText(Parameters.CODE_PARAM), null);
		params = mobileMoneyDAO.findByPrimaryKey(Parameters.class, Encrypter.getInstance().hashText(Parameters.CODE_PARAM), null);

		// Si les parametres n'existent pas
		if(params == null) params = mobileMoneyDAO.save( new Parameters() );

	}

	private TimerTask taskSMSFirst = null;

	private java.util.Timer timerSMSFirst = null;


	/**
	 * 
	 */
	public void processReconciliationAuto(){
		try{
			taskSMSFirst = new TimerTask(){
				@Override
				public void run(){
					try{
						processReconciliation();
					}catch(Exception e){
						e.printStackTrace();
					}
				}	
			};
			timerSMSFirst = new java.util.Timer(true);
			int sec = 60; int min = 20;
			timerSMSFirst.schedule(taskSMSFirst,DateUtils.addMinutes(new Date(),1),min*sec*1000);	
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public void StopReconciliationAuto(){
		try{
			if(taskSMSFirst != null )taskSMSFirst.cancel();
			if(timerSMSFirst != null )timerSMSFirst.cancel();
			logger.info("*********** StopReconciliationAuto **************");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public void processReconciliation(){
		try {

			logger.info("***********PROCESS ONE **************");
			Calendar cal = Calendar.getInstance();
			/**cal.set(Calendar.DAY_OF_MONTH, 25);
			cal.set(Calendar.MONTH, Calendar.FEBRUARY);
			cal.set(Calendar.YEAR, 2018);*/
			cal.set(Calendar.HOUR_OF_DAY,0);
			cal.set(Calendar.MINUTE,0);
			cal.set(Calendar.MILLISECOND,1);
			Date detedebut = cal.getTime();
			cal = Calendar.getInstance();
			/**cal.set(Calendar.DAY_OF_MONTH, 25);
			cal.set(Calendar.MONTH, Calendar.FEBRUARY);
			cal.set(Calendar.YEAR, 2018);*/
			/**cal.set(Calendar.HOUR_OF_DAY,23);
			cal.set(Calendar.MINUTE,59);
			cal.set(Calendar.MILLISECOND,59);
			Date detefin = cal.getTime();*/

			//Double amount = 0d;

			List<USSDTransaction> listRec = new ArrayList<USSDTransaction>();
			RestrictionsContainer rc = RestrictionsContainer.getInstance();
			rc.add(Restrictions.eq("status",TransactionStatus.SUCCESS));
			rc.add(Restrictions.eq("posted",Boolean.FALSE));
			rc.add(Restrictions.or(Restrictions.isNull("verifier"),Restrictions.eq("verifier",Boolean.FALSE)));
			rc.add(Restrictions.or(Restrictions.eq("typeOperation",TypeOperation.PULL),Restrictions.eq("typeOperation",TypeOperation.PUSH)));
			rc.add(Restrictions.eq("mtnTrxId",null)); // Ajoute
			rc.add(Restrictions.between("date", DateUtils.addDays(detedebut, -1), DateUtils.addMinutes(new Date(), -25)));
			OrderContainer orders = OrderContainer.getInstance();
			orders.add(Order.desc("date"));
			List<Transaction> trans = filterTransactions(rc, orders);

			logger.info("*********** COUNT **************"+trans.size());

			//String root = "D:\\migration\\";
			//String heur = new SimpleDateFormat("ddMMyyyyHHmmss").format(detefin);
			//File makeFile = new File(root);
			//if(!makeFile.exists()) makeFile.mkdirs();
			//DataOutputStream fluxSortieBinaire  = new DataOutputStream( new FileOutputStream(root+"MoMo_Regul_Auto"+heur+".txt"));
			//String fileWrite = "";
			for(Transaction tran : trans){
				rc = RestrictionsContainer.getInstance();
				rc.add(Restrictions.eq("lg_Remote_ID",tran.getId().toString()));
				List<USSDTransaction> list = filterUSSDTransactions(rc); 
				tran.setVerifier(Boolean.TRUE);
				//String txtContenu = "";
				if(!list.isEmpty()){
					//amount = amount + tran.getTtc();
					USSDTransaction ut = list.iterator().next();
					if( ut.opeOK() == false ){
						if(ut.getSubscriber() == null) ut.setSubscriber(tran.getSubscriber());
						listRec.add(ut);
						tran.setReconcilier(Boolean.TRUE);
						tran.setDatereconcilier(new Date());
						//txtContenu = txtContenu + ut.getStr_Phone() + "    "+ut.getInt_Amount()+"\n";
						logger.info("***111111***Reversal**"+tran.toString());
						//fileWrite = tran.toString() +"\n";
						//fluxSortieBinaire.writeBytes(fileWrite);*/
					}
				}else{
					// Annulation de la Transaction
					rc = RestrictionsContainer.getInstance().add(Restrictions.between("dt_Created",DateUtils.addMinutes(tran.getDate(),30),DateUtils.addMinutes(tran.getDate(),30)));
					rc.add(Restrictions.eq("int_Amount",tran.getAmount().intValue()));
					rc.add(Restrictions.eq("str_Phone",tran.getPhoneNumber()));
					List<USSDTransaction> listes = filterUSSDTransactions(rc);
					if(!listes.isEmpty()){
						USSDTransaction ut = listes.iterator().next();
						if(ut.opeOK() == false ){
							//amount = amount + tran.getTtc();
							logger.info("***22222****Tme Out**Reversal**"+tran.toString());
							//fileWrite = tran.toString() +"\n";
							//fluxSortieBinaire.writeBytes(fileWrite);
							tran.setReconcilier(Boolean.TRUE);
							tran.setDatereconcilier(new Date());
							processReversalTransaction(tran.getId().toString());
						}
					}else{
						/**amount = amount + tran.getTtc();
						logger.info("***33333****Tme Out**Reversal**"+tran.toString());
						fileWrite = tran.toString() +"\n";
						fluxSortieBinaire.writeBytes(fileWrite);*/
					}	
				}

				mobileMoneyDAO.update(tran);

			}

			//logger.info("***********TOTAL AMOUNT **************"+amount.intValue());
			//fluxSortieBinaire.close();
			//executerReconciliation(listRec,"AUTO " + new SimpleDateFormat("dd/MM/yyyy HH':'mm':'ss").format(new Date()));

			if(!listRec.isEmpty()){
				// recociliation de la transaction 
				for(USSDTransaction t : listRec){
					// S'il s'agit d'un PUSH 
					//if(t.getTypeOperation().equals(TypeOperation.PUSH))
					// Re-Execution de la transaction
					//processPullPushMessage(new RequestMessage(t.getTypeOperation(), Encrypter.getInstance().decryptText( t.getSubscriber().getBankPIN() ), t.getStr_Phone(), Double.valueOf(t.getInt_Amount()), t.getSubscriber().getAccounts().get(0)) ) ;
					// S'il s'agit d'un PULL
					//else 
					if(t.getTypeOperation().equals(TypeOperation.PULL)){
						// Annulation de la Transaction
						processReversalTransaction(t.getLg_Remote_ID());
						// MAJ des statuts de la transaction
						t.setStr_Status("valide"); t.setStr_Step(t.getTypeOperation().equals(TypeOperation.PUSH) ? "Reconciliee" : "Annulee"); t.setStr_Status_Description("Operation "+ (t.getTypeOperation().equals(TypeOperation.PUSH) ? "Reconciliee" : "Annulee") +" par " + "AUTO" + " le " + new SimpleDateFormat("dd/MM/yyyy HH':'mm").format(new Date()));
						// MAJ des statuts de la transaction dans l'api ussd
						pullpushDAO.update(t);
					}
				}
			}

			// Initialisation d'un conteneur de restrictions
			/**rc = RestrictionsContainer.getInstance().add(Restrictions.between("dt_Created",detedebut,detefin));
			List<USSDTransaction> listes = filterUSSDTransactions(rc); 
			listRec = new ArrayList<USSDTransaction>();
			for(USSDTransaction t : listes) {
				if(t.isAreconcilier()){
					if(t.getTypeOperation().equals(TypeOperation.PUSH))
						// Re-Execution de la transaction
						if(t.getSubscriber() == null) t.setSubscriber(findSubscriberFromPhoneNumber(t.getStr_Phone()) );
						processPullPushMessage( new RequestMessage(t.getTypeOperation(), Encrypter.getInstance().decryptText( t.getSubscriber().getBankPIN() ), t.getStr_Phone(), Double.valueOf(t.getInt_Amount()), t.getSubscriber().getAccounts().get(0)) ) ;
				}
			}*/

		} catch(Exception ex) {
			// Traitement de m'exception
			ex.printStackTrace();
		}

	}

	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#findParameters()
	 */
	@Override
	//@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Parameters findParameters() {
		// TODO Auto-generated method stub
		return mobileMoneyDAO.findByPrimaryKey(Parameters.class, Encrypter.getInstance().hashText(Parameters.CODE_PARAM), null);		
	}
	

	@Override
	@AllowedRole(name = "consulterConfiguration", displayName = "MoMo.Consulter.Configuration")
	public Parameters consulterConfiguration() {
		return findParameters();
	}


	@Override
	@AllowedRole(name = "filterPlageTransactions", displayName = "MoMo.filterPlageTransactions")
	public List<PlageTransactions> filterPlageTransactions() {
		return mobileMoneyDAO.filter(PlageTransactions.class,null,null,null,null,0,-1);
	}

	
	private void checkGlobalConfig(){
		if(params == null) params = findParameters();
	}
	

	private void findCBSDataSystem() {

		try {

			// Demarrage du service Facade du portail
			IFacadeManagerRemote portalFacadeManager = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );

			// Recuperation de la DS de cnx au CBS
			dsCBS = (DataSystem) portalFacadeManager.findByProperty(DataSystem.class, "code", "DELTA-V10");

		}catch(Exception e){}
	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#filterTypeCompteFromAmplitude()
	 */
	@Override
	public List<TypeCompte> filterTypeCompteFromAmplitude() {

		// Initialisation de la collection a retourner
		List<TypeCompte> types = new ArrayList<TypeCompte>();

		try{

			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();

			// Recherche de la liste des types de comptes dans le CBS
			ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(0).getQuery(), null);

			// Parcours du resultat
			while(rs != null && rs.next()){

				// Ajout de l'element trouve a la collection
				types.add( new TypeCompte(rs.getString("code").trim(), rs.getString("nom").trim()) );
			}

			// Fermeture des connexions
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
		} catch(Exception e){e.printStackTrace();}

		// Retourne les types de compte
		return types;
	}
	

	/*
	 * 
	 */
	@Override
	public List<String> filterCustomerAccountsFromCBS(String customerId) {

		// Initialisation de la liste de comptes a retourner
		List<String> accounts = new ArrayList<String>();

		// Recherche des parametres
		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		String in = "(";
		for(String typ : params.getAccountTypes()) in += "'"+ typ +"', ";
		if(in.length() > 1) in = in.substring(0, in.length()-2) + ")";


		try {

			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();

			// Recherche de la liste des types de comptes dans le CBS
			ResultSet rs = executeFilterSystemQuery(dsCBS, "select age, ncp, clc from bkcom where cli='"+ customerId +"' "+ (in.length() > 1 ? " and cpro in "+in+" " : "") , null);

			// S'il existe au moins un resultat
			while(rs != null && rs.next()) {
				accounts.add( rs.getString("age") + "-" + rs.getString("ncp") + "-" + rs.getString("clc") );
			}

			// Fermeture de cnx
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}

			// Suppression des variables
			//			params = null; 
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
		} catch(Exception e){}

		// Retourne la liste de comptes
		return accounts;

	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#findCustomerFromAmplitude(java.lang.String)
	 */
	@Override
	public Subscriber findCustomerFromAmplitude(String customerId) {

		// Initialisation
		Subscriber subscriber = null;
		String in = "";

		// Recherche des parametres generaux
		// checkGlobalConfig(); //Parameters params = findParameters();
		params = findParameters();

		// Si les types de cptes autorises ont ete parametres
		if( params.getAccountTypes() != null && !params.getAccountTypes().isEmpty()) {

			for(String s : params.getAccountTypes()) in += "'" + s + "'" + ", ";

		}

		if(!in.isEmpty()) in = "(".concat( in.substring(0, in.length()-2) ).concat(")");


		try {

			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();

			// Recherche de la liste des types de comptes dans le CBS
			//ResultSet rs = executeFilterSystemQuery(dsCBS, "select distinct bkcom.age, bkcom.ncp, bkcom.clc, bkcli.nom, nvl(bkcli.pre, ' ') as pre, bkadcli.adr1, bkadcli.ville from bkcom, bkcli LEFT JOIN bkadcli ON bkcli.cli = bkadcli.cli where bkcom.cli = bkcli.cli and bkadcli.cli = bkcli.cli and bkcom.cfe='N' and bkcom.ife='N' and bkcom.cli='"+ customerId +"'" + (in.isEmpty() ? "" : " and bkcom.cpro in " + in), null);
			ResultSet rs = executeFilterSystemQuery(dsCBS, "select distinct bkcom.age, bkcom.ncp, bkcom.clc, bkcli.nom, nvl(bkcli.pre, ' ') as pre, bkadcli.adr1, bkadcli.ville from bkcom, bkcli LEFT JOIN bkadcli ON bkcli.cli = bkadcli.cli where bkcom.cli = bkcli.cli and bkcom.cfe='N' and bkcom.ife='N' and bkcom.cli='"+ customerId +"'" + (in.isEmpty() ? "" : " and bkcom.cpro in " + in), null);

			// S'il existe au moins un resultat
			if(rs != null && rs.next()) {

				// Recuperation du nom et de l'adresse du souscripteur
				subscriber = new Subscriber(customerId, rs.getString("nom").trim().concat(" ").concat( rs.getString("pre").trim() ), rs.getString("adr1") + " " + rs.getString("ville"));

				// Ajout du premier compte
				subscriber.getAccounts().add( rs.getString("age").concat("-").concat(rs.getString("ncp")).concat("-").concat(rs.getString("clc")) );

				// Parcours des autres comptes
				while(rs != null && rs.next()) {

					// Ajout du compte trouve a la liste des comptes du clients
					if( !subscriber.getAccounts().contains( rs.getString("age").concat("-").concat(rs.getString("ncp")).concat("-").concat(rs.getString("clc")) )) subscriber.getAccounts().add( rs.getString("age").concat("-").concat(rs.getString("ncp")).concat("-").concat(rs.getString("clc")) );

				}
				// Recherche du Numero de CNI du client
				rs = executeFilterSystemQuery(dsCBS, "select num from bkpidcli where cli='"+ subscriber.getCustomerId() +"'", null);
				subscriber.setPid( rs != null && rs.next() ? rs.getString("num") : null );

				// Recherche des Numeros de telephone du Client
				rs = executeFilterSystemQuery(dsCBS, "SELECT num from bktelcli where cli='"+ subscriber.getCustomerId() +"' order by typ", null);
				if(rs != null) {
					while(rs.next()) subscriber.getPhoneNumbers().add(rs.getString("num"));
					rs.close();
				}

			}

			// Fermeture des connexions
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			} 
			//params = null;
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();

		} catch(Exception e){e.printStackTrace();}

		// Retourne le client trouve
		return subscriber;
	}
 
	
	public Transaction findTransactionBySubscriber(Long subsId) {

		List<Transaction> trans = mobileMoneyDAO.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("subscriber.id", subsId)), null, null, 0, -1);

		return trans != null && !trans.isEmpty() ? trans.get(0) : null;
	}
	
	
	public Transaction findTransactionByTypeAndSubscriber(TypeOperation typeOp, Long subsId) {

		List<Transaction> trans = mobileMoneyDAO.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("typeOp", typeOp)).add(Restrictions.eq("subscriber.id", subsId)), null, null, 0, -1);

		return trans != null && !trans.isEmpty() ? trans.get(0) : null;
	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#posterEvenementDansAmplitude(com.afb.dpd.mobilemoney.jpa.entities.Transaction)
	 */
	public void posterEvenementDansAmplitude(Transaction transaction) throws Exception {

		// S'il n'ya aucune transaction on sort
		if(transaction == null) return;

		// Lecture des parametres generaux
		// checkGlobalConfig(); //Parameters params = findParameters();
		params = findParameters();

		// Log
		//logger.info("");

		// Tests
		if(params == null) throw new Exception("Parametres inexistants");
		if(params.getNcpDAPPush() == null || params.getNcpDAPPull() == null) throw new Exception("Comptes de liaisons pour operations de Push & Pull non parametres !!!");

		// Initialisation du code de l'evenement a generer
		String codeOpe = params.getCodeOperation();  //.getNumCompteFloat().split("-")[0].equals(transaction.getAccount().split("-")[0]) ? MoMoHelper.CODE_OPE_VIREMENT_SIMPLE : MoMoHelper.CODE_OPE_VIREMENT_INTERAG;

		// Log
		//logger.info("Demarrage du service Facade du portail OK!");

		// Initialisation de DataStore d'Amplitude
		if(dsCBS == null) findCBSDataSystem();

		// Log
		//logger.info("Lecture de la DS du CBS OK!");

		// Recuperation du dernier numero evenement du type operation
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(2).getQuery(), new Object[]{codeOpe});

		// Log
		//logger.info("Lecture du dernier numero d'evenement genere OK!");

		// Calcul du numero d'evenement
		Long numEve = rs != null && rs.next() ? numEve = rs.getLong("num") + 1 : 0l;

		// Log
		//logger.info("Calcul du prochain numero d'evenement OK!");

		// Fermeture de cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}

		// Recuperation du cpte DAP a utiliser
		String ncpDAP = transaction.getTypeOperation().equals(TypeOperation.PULL) ? params.getNcpDAPPull() : params.getNcpDAPPush();

		// Recuperation du compte Float MTN
		ResultSet rsCpteMTN = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(6).getQuery(), new Object[]{ ncpDAP.split("-")[0], ncpDAP.split("-")[1], ncpDAP.split("-")[2] });

		// Log
		//logger.info("Recuperation des infos sur le cpte de MTN OK!");

		ResultSet rsCpteAbonne = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(6).getQuery(), new Object[]{ transaction.getAccount().split("-")[0], transaction.getAccount().split("-")[1], transaction.getAccount().split("-")[2] });

		// Log
		//logger.info("Recuperation des infos sur le compte de l'abonne OK!");

		// Initialisation de l'evenement a poster dans Amplitude
		bkeve eve = new bkeve();

		// Initialisation de l'evenement de la commission
		//bkevec evec = new bkevec(codeOpe, MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), transaction.getTypeOperation().equals(TypeOperation.PULL) ? params.getCommissionsPull() : params.getCommissionsPush() , "O");

		// Si on a trouve les 2 comptes a mouvementer
		if(rsCpteMTN != null && rsCpteMTN.next() && rsCpteAbonne != null && rsCpteAbonne.next()) {

			// Lecture des infos du cpte Debiteur
			eve.setDebiteur( (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("age") : rsCpteMTN.getString("age")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("dev") : rsCpteMTN.getString("dev")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("ncp") : rsCpteMTN.getString("ncp")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("suf") : rsCpteMTN.getString("suf")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("clc") : rsCpteMTN.getString("clc")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("cli") : rsCpteMTN.getString("cli")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("nom") : rsCpteMTN.getString("nom")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("ges") : rsCpteMTN.getString("ges")), transaction.getAmount(), transaction.getAmount(), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? getDvaDebit() : getDvaCredit() ), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getDouble("sde") : rsCpteMTN.getDouble("sde")));

			// Lecture des infos du cpte crediteur
			eve.setCrediteur( (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("age") : rsCpteMTN.getString("age")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("dev") : rsCpteMTN.getString("dev")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("ncp") : rsCpteMTN.getString("ncp")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("suf") : rsCpteMTN.getString("suf")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("clc") : rsCpteMTN.getString("clc")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("cli") : rsCpteMTN.getString("cli")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("nom") : rsCpteMTN.getString("nom")), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getString("ges") : rsCpteMTN.getString("ges")), transaction.getAmount(), transaction.getAmount(), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? getDvaDebit() : getDvaCredit()), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? rsCpteAbonne.getDouble("sde") : rsCpteMTN.getDouble("sde")));

			// Log
			//logger.info("Generation de l'evenement");

			// Fermeture des cnx
			if(rsCpteMTN != null) {
				rsCpteMTN.close(); 
				if(rsCpteMTN.getStatement() != null) {
					rsCpteMTN.getStatement().close();
				}
			}
			if(rsCpteAbonne != null) {
				rsCpteAbonne.close(); 
				if(rsCpteAbonne.getStatement() != null) {
					rsCpteAbonne.getStatement().close();
				}
			}
			
			
			// Enregistrement de l'evenement
			executeUpdateSystemQuery(dsCBS, eve.getSaveQuery(), eve.getQueryValues());

			// Enregistrement de la commission evenement
			//executeUpdateSystemQuery(dsCBS, evec.getSaveQuery(), evec.getQueryValues());

			// Log
			//logger.info("Creation de l'evenement OK!");

			// MAJ du solde indicatif debiteur
			executeUpdateSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(4).getQuery(), new Object[]{transaction.getAmount(), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? transaction.getAccount().split("-")[0] : ncpDAP.split("-")[0] ), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? transaction.getAccount().split("-")[1] : ncpDAP.split("-")[1] ), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? transaction.getAccount().split("-")[2] : ncpDAP.split("-")[2] )  });

			// Log
			//logger.info("MAJ du solde indicatif du compte debiteur OK!");

			// MAJ du solde indicatif crediteur
			executeUpdateSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(5).getQuery(), new Object[]{transaction.getAmount(), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? transaction.getAccount().split("-")[0] : ncpDAP.split("-")[0] ), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? transaction.getAccount().split("-")[1] : ncpDAP.split("-")[1] ), (transaction.getTypeOperation().equals(TypeOperation.PULL) ? transaction.getAccount().split("-")[2] : ncpDAP.split("-")[2] )  });

			// Log
			//logger.info("MAJ du solde indicatif du compte crediteur OK!");

			// MAJ du dernier numero d'evenement utilise pour le type operation
			executeUpdateSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(3).getQuery(), new Object[]{numEve, codeOpe});

			// Log
			//logger.info("MAJ du dernier numero d'evenement genere pour le type operation OK!");

		}
		// Fermeture des cnx
		// CBS_CNX_OPTI
		if(rsCpteMTN != null) {
			rsCpteMTN.close(); 
			if(rsCpteMTN.getStatement() != null) {
				rsCpteMTN.getStatement().close();
			}
		}
		if(rsCpteAbonne != null) {
			rsCpteAbonne.close(); 
			if(rsCpteAbonne.getStatement() != null) {
				rsCpteAbonne.getStatement().close();
			}
		}
		
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
	}

	
	/*
	private Date addDate(Date date, int nbJrs){
		Calendar cal = new GregorianCalendar();
		cal.setTime(date); cal.add(Calendar.DATE, nbJrs);
		return cal.getTime();
	}
	 */
	

	/**
	 * Construit l'evenement (accompagne des ecritures) a poster dans Delta 
	 * @param transaction
	 * @return evenement
	 */
	public bkeve buildEvenement(Transaction transaction) throws Exception {

		/***********************/
		/*** INITIALISATIONS ***/
		/***********************/
		logger.info("IN buildEvenement");
		// Lecture des parametres generaux
		// checkGlobalConfig(); //Parameters params = findParameters();
		params = findParameters();

		if(params == null) throw new Exception("Parametres non initialises");

		// Initialisation de DataStore d'Amplitude
		if(dsCBS == null) findCBSDataSystem();

		// Log
		//logger.info("Demarrage du service Facade du portail OK!");

		if(dsCBS == null) throw new Exception("Impossible de trouver la source de donnees au Core Banking");

		// Initialisations
		ResultSet rsCpteDAPMTN = null, rsCpteAbonne = null, rsCpteComs = null, rsCpteTVA = null, rsLiaisonAbonne = null; ResultSet rsLiaisonDAPMTN = null; ResultSet rsLiaisonComs = null; ResultSet rsLiaisonTva = null;
		Map<TypeOperation, Commissions> mapComs = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation");

		Long numEc = 1l;
		Date dco = getDateComptable(dsCBS);
		Double tva = 19.25d;
		Double frais = 0d;
		Double tauxCom = 0d;
		Double valeurCom = transaction.getCommissions();
		Double valeurTax = round(transaction.getTtc() - transaction.getCommissions(), 0);

		String natMag = "VIRMAC";
		String natIag = "VIRMAC";
		Date dvaDebit = getDvaDebit();
		Date dvaCredit = getDvaCredit();
		//String datop = new SimpleDateFormat("ddMMyyHHmm").format(new Date());
		String datop = new SimpleDateFormat("ddMMyyHHmm").format(transaction.getDate());
		// Date operation pour EC (ajout des secondes)
		//String datopec = new SimpleDateFormat("ddMMyyHHmmss").format(new Date());
		String datopec = new SimpleDateFormat("ddMMyyHHmmss").format(transaction.getDate());

		PlageTransactions plg = transaction.getSubscriber().getProfil();
		String ncpDAP = transaction.getTypeOperation().equals(TypeOperation.PULL) ? params.getNcpDAPPull() : (transaction.getTypeOperation().equals(TypeOperation.PUSH) ? params.getNcpDAPPush() : null);
		if(plg != null){
			ncpDAP = transaction.getTypeOperation().equals(TypeOperation.PULL) ? plg.getNcpDAPPull() : (transaction.getTypeOperation().equals(TypeOperation.PUSH) ? plg.getNcpDAPPush() : null);
		}		
		boolean modeNuit = isModeNuit();

		/***************************************************************/
		/*** LECTURE DU DERNIER NUMERO D'EVENEMENT DU TYPE OPERATION ***/
		/***************************************************************/

		// Recuperation du dernier numero evenement du type operation
		ResultSet rs = executeFilterSystemQuery(dsCBS, modeNuit ? MoMoHelper.getDefaultCBSQueries().get(2).getQuery().replaceAll("bkope", "syn_bkope") : MoMoHelper.getDefaultCBSQueries().get(2).getQuery(), new Object[]{ params.getCodeOperation() });

		// Log
		//logger.info("Lecture du dernier numero d'evenement genere OK!");

		// Calcul du numero d'evenement
		Long numEve = rs != null && rs.next() ? numEve = rs.getLong("num") + 1 : 1l;

		// Log
		//logger.info("Calcul du prochain numero d'evenement OK!");
		//logger.info("numero d'evenement "+numEve);

		// Fermeture de cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		
		/*********************************************************/
		/*** CALCUL DES COMMISSIONS ET TAXES DE LA TRANSACTION ***/
		/*********************************************************/

		// Recuperation de la commission correspondant a l'operation
		Commissions com = mapComs.get(transaction.getTypeOperation());

		if(com != null && (com.getModeFacturation() == null || !com.getModeFacturation().equals(ModeFacturation.PERIODIQUE) )  ){

			if(plg != null){
				frais = plg.getCommissions();  tauxCom = 0d;
			}else{
				// Calcul du taux et des frais
				if( com.getTypeValeur().equals(TypeValeurFrais.FIXE) ) frais = com.getValeur(); else tauxCom = com.getValeur();
			}

			valeurCom = tauxCom > 0 ? round(tauxCom * transaction.getAmount() / 100.0, 0) : frais;
			tva = com.getTauxTVA();
			valeurTax = round(valeurCom * tva / 100.0, 0);
		}

		Subscriber s = transaction.getSubscriber();
		
		// Si le client est un employe de la banque, on annule ses commissions ou s'il a deja ete facturé au package
		if(isClientEmploye(s.getCustomerId()) || s.getStatus().equals(StatutContrat.ACTIF_CBS)) {
			valeurCom = 0d; valeurTax = 0d;
		}

		// Si une commission a ete parametre pour l'operation
		if ( valeurCom > 0 ) {

			// Si le compte des commissions a ete parametre
			if(params.getNumCompteCommissions() != null && !params.getNumCompteCommissions().isEmpty()) 

				// Recuperation du numero de cpte des commissions
				rsCpteComs = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(7).getQuery(), new Object[]{ params.getNumCompteCommissions().split("-")[0], params.getNumCompteCommissions().split("-")[1], params.getNumCompteCommissions().split("-")[2] });

			if(rsCpteComs != null) rsCpteComs.next();

			// Si le numero de cpte TVA a ete parametre
			if(params.getNumCompteTVA() != null && !params.getNumCompteTVA().isEmpty())

				// Recuperation du numero de compte TVA
				rsCpteTVA = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(7).getQuery(), new Object[]{ params.getNumCompteTVA().split("-")[0], params.getNumCompteTVA().split("-")[1], params.getNumCompteTVA().split("-")[2] });

			if(rsCpteTVA != null) rsCpteTVA.next();
		}
		
		/************************************************/
		/*** RECHERCHE DU CPTE DEBITEUR (CPTE CLIENT) ***/
		/************************************************/

		// Recherche du cpte de l'abonne
		rsCpteAbonne = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(6).getQuery(), new Object[]{ transaction.getAccount().split("-")[0], transaction.getAccount().split("-")[1], transaction.getAccount().split("-")[2] });

		// Si on ne trouve le cpte du client on leve une exception
		if( rsCpteAbonne == null || !rsCpteAbonne.next() ) throw new Exception("Impossible de trouver le compte du client");
		
		/***********************************/
		/*** CONSTRUCTION DE L'EVENEMENT ***/
		/***********************************/

		transaction.setCommissions(round(valeurCom, 0));
		transaction.setTtc(round(transaction.getAmount() + valeurCom + valeurTax, 0));

		// Annulation de la Generation de l'evenement s'il nya aucun montant a debiter
		if(transaction.getTtc() == 0d) { 
			if(rsCpteAbonne != null) {
				rsCpteAbonne.close();
				if(rsCpteAbonne.getStatement() != null) {
					rsCpteAbonne.getStatement().close();
				}
			}
			if(rsCpteComs != null) {
				rsCpteComs.close();
				if(rsCpteComs.getStatement() != null) {
					rsCpteComs.getStatement().close();
				}
			}
			if(rsCpteTVA != null) {
				rsCpteTVA.close();
				if(rsCpteTVA.getStatement() != null) {
					rsCpteTVA.getStatement().close();
				}
			}
			transaction = null;
			return null;
		}

		// Initialisation de l'evenement a retourner
//		bkeve eve = new bkeve(transaction, params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), rsCpteAbonne.getString("dev"), round(transaction.getAmount() + valeurCom, 0), ncpDAP == null ? (rsCpteComs != null && !rsCpteComs.getString("age").equals(rsCpteAbonne.getString("age")) ? natIag : natMag) : (rsCpteAbonne.getString("age").equals(ncpDAP.split("-")[0]) ? natMag : natIag), dco, params.getCodeUtil(), tauxCom, frais, tva, round(transaction.getAmount() + valeurCom + valeurTax, 0));
		bkeve eve = new bkeve(transaction, params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), rsCpteAbonne.getString("dev"), round(transaction.getAmount() + valeurCom, 0), ncpDAP == null ? (rsCpteComs != null && !rsCpteComs.getString("age").equals(rsCpteAbonne.getString("age")) ? natIag : natMag) : (rsCpteAbonne.getString("age").equals(ncpDAP.split("-")[0]) ? natMag : natIag), dco, params.getCodeUtil(), tauxCom, frais, tva, round(transaction.getAmount() + valeurCom + valeurTax, 0), round(transaction.getAmount() + valeurCom + valeurTax, 0));

		// Si les TFJ sont en cours, 
		if(modeNuit && (transaction.getTypeOperation().equals(TypeOperation.PULL) || transaction.getTypeOperation().equals(TypeOperation.PUSH) ) ) {

			// S'il s'agit d'un PULL
			if(transaction.getTypeOperation().equals(TypeOperation.PULL)){

				// Si le total des Pull effectues par le client depasse le mnt max des Pull on annule tout
				//if(params.getMaxPullAmount() > 0d && transaction.getTtc() + getTotalAmountPullPendantTFJ(transaction.getPhoneNumber()) > params.getMaxPullAmount()) throw new Exception(ExceptionCode.MobileMoneyExceededAmount.getValue());
			}

			// on suspend le postage dans Amplitude
			eve.setSuspendInTFJ(Boolean.TRUE);

			// On augmente la periode de recyclage de l'evenement a J+1 (pour que Delta ne le Supprime pas)
			//eve.setDech( addDate(eve.getDco(), 1) );
		}

//		COMMENTE
// S'il s'agit d'une operation inter-agence
//		if(eve.getNat().equals(natIag)) {
//
//			// Recuperation du compte de liaison de l'agence du client
//			rsCpteLiaison = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='"+ rsCpteAbonne.getString("age") +"' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
//
//			// Recuperation du compte de liaison d'hippodrome
//			rsLiaisonHippo = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='00001' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
//
//			// Si le compte de liaison existe
//			if(rsCpteLiaison == null || !rsCpteLiaison.next()) throw new Exception("Impossible de trouver le compte de liaison de l'agence " + rsCpteAbonne.getString("age"));
//
//			// Si le compte de liaison d'hippodrome n'existe pas
//			if(rsLiaisonHippo == null || !rsLiaisonHippo.next()) throw new Exception("Impossible de trouver le compte de liaison de l'agence d'hippodrome " );
//
//		}
		
		
		// Recuperation des comptes de liaison

		logger.info("IN buildEvenement 2");
			// Recuperation du compte de liaison de l'agence du client
			rsLiaisonAbonne = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='"+ rsCpteAbonne.getString("age") +"' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
	
			// Recuperation du compte de liaison du DAP MTN (PULL/PUSH)
			if(StringUtils.isNotBlank(ncpDAP)) rsLiaisonDAPMTN = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='"+ ncpDAP.split("-")[0] +"' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
	
			// Recuperation du compte de liaison de l'agence du compte de commissions
			rsLiaisonComs = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='"+ params.getNumCompteCommissions().split("-")[0] +"' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
	
			// Recuperation du compte de liaison de l'agence du compte de TVA
			rsLiaisonTva = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='"+ params.getNumCompteTVA().split("-")[0] +"' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
			
			// Si le compte de liaison de l'agence du client n'existe pas
			if(rsLiaisonAbonne == null || !rsLiaisonAbonne.next()) throw new Exception("Impossible de trouver le compte de liaison de l'agence " + rsCpteAbonne.getString("age"));
	
			// Si le compte de liaison du DAP MTN n'existe pas
			if(StringUtils.isNotBlank(ncpDAP) && (rsLiaisonDAPMTN == null || !rsLiaisonDAPMTN.next())) throw new Exception("Impossible de trouver le compte de liaison de l'agence du DAP MTN " );
	
			// Si le compte de liaison des commissions n'existe pas
			if(rsLiaisonComs == null || !rsLiaisonComs.next()) throw new Exception("Impossible de trouver le compte de liaison de l'agence des commissions ");
	
			// Si le compte de liaison de la TVA n'existe pas
			if(rsLiaisonTva == null || !rsLiaisonTva.next()) throw new Exception("Impossible de trouver le compte de liaison de l'agence des taxes " );


		// S'il s'agit de la souscription
		if( transaction.getTypeOperation().equals(TypeOperation.SUBSCRIPTION) || transaction.getTypeOperation().equals(TypeOperation.MODIFY) || transaction.getTypeOperation().equals(TypeOperation.COMPTABILISATION) ) {

			// Si on a parametre les frais sur la souscription
			if ( valeurCom > 0 ) {

				// Ajout du debiteur
				eve.setDebiteur(rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), rsCpteAbonne.getString("ncp"), rsCpteAbonne.getString("suf"), rsCpteAbonne.getString("clc"), rsCpteAbonne.getString("cli"), rsCpteAbonne.getString("nom"), rsCpteAbonne.getString("ges"), round(transaction.getAmount() + valeurCom, 0), round(transaction.getAmount() + valeurCom, 0), dvaDebit, rsCpteAbonne.getDouble("sde"));

				// Ajout du debiteur
				eve.setCrediteur(rsCpteComs.getString("age"), rsCpteComs.getString("dev"), rsCpteComs.getString("ncp"), rsCpteComs.getString("suf"), rsCpteComs.getString("clc"), rsCpteComs.getString("cli"), rsCpteComs.getString("inti"), "   ", valeurCom, valeurCom, dvaDebit, rsCpteComs.getDouble("sde"));

				// Libelle de l'evenement
				eve.setLib1(datop + "/" + transaction.getTypeOperation().toString().substring(0, 5) + "/MAC/" + transaction.getPhoneNumber());

			}

			// S'il s'agit du Pull
		} else if( transaction.getTypeOperation().equals(TypeOperation.PULL) ) {
			logger.info("IN buildEvenement 3");
			// Recuperation du compte DAP PULL MTN
			rsCpteDAPMTN = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(8).getQuery(), new Object[]{ ncpDAP.split("-")[0], ncpDAP.split("-")[1], ncpDAP.split("-")[2] });

			// Ajout du debiteur
			eve.setDebiteur(rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), rsCpteAbonne.getString("ncp"), rsCpteAbonne.getString("suf"), rsCpteAbonne.getString("clc"), rsCpteAbonne.getString("cli"), rsCpteAbonne.getString("nom"), rsCpteAbonne.getString("ges"), transaction.getAmount(), transaction.getAmount(), dvaDebit, rsCpteAbonne.getDouble("sde"));

			// Ajout du crediteur
			if(rsCpteDAPMTN.next()) eve.setCrediteur(rsCpteDAPMTN.getString("age"), rsCpteDAPMTN.getString("dev"), rsCpteDAPMTN.getString("ncp"), rsCpteDAPMTN.getString("suf"), rsCpteDAPMTN.getString("clc"), rsCpteDAPMTN.getString("cli"), rsCpteDAPMTN.getString("inti"), rsCpteDAPMTN.getString("utic"), transaction.getAmount(), transaction.getAmount(), dvaDebit, rsCpteDAPMTN.getDouble("sde"));

			// Libelle de l'evenement
			eve.setLib1( datop + "/PULL/" + transaction.getPhoneNumber() + "/" + rsCpteAbonne.getString("ncp"));

			// Fermeture de la cnx
			//rsCpteMTN.close(); rsCpteMTN.getStatement().close();

			// S'il s'agit du Push
		} else if( transaction.getTypeOperation().equals(TypeOperation.PUSH) ) {
			logger.info("IN buildEvenement 4");
			// Recuperation du compte DAP PUSH MTN
			rsCpteDAPMTN = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(8).getQuery(), new Object[]{ ncpDAP.split("-")[0], ncpDAP.split("-")[1], ncpDAP.split("-")[2] });

			// Ajout du crediteur
			eve.setCrediteur(rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), rsCpteAbonne.getString("ncp"), rsCpteAbonne.getString("suf"), rsCpteAbonne.getString("clc"), rsCpteAbonne.getString("cli"), rsCpteAbonne.getString("nom"), rsCpteAbonne.getString("ges"), transaction.getAmount(), transaction.getAmount(), dvaCredit, rsCpteAbonne.getDouble("sde"));

			// Ajout du debiteur
			if(rsCpteDAPMTN.next()) eve.setDebiteur(rsCpteDAPMTN.getString("age"), rsCpteDAPMTN.getString("dev"), rsCpteDAPMTN.getString("ncp"), rsCpteDAPMTN.getString("suf"), rsCpteDAPMTN.getString("clc"), rsCpteDAPMTN.getString("cli"), rsCpteDAPMTN.getString("inti"), rsCpteDAPMTN.getString("utic"), transaction.getAmount(), transaction.getAmount(), dvaCredit, rsCpteDAPMTN.getDouble("sde"));

			// Libelle de l'evenement
			eve.setLib1(datop + "/PUSH/" + transaction.getPhoneNumber() + "/" + rsCpteAbonne.getString("ncp"));

		}
		
		/***********************************************************/
		/** GENERATION DES ECRITURES COMPTABLES DE LA TRANSACTION **/
		/***********************************************************/

		// Si c'est un PULL
		if(transaction.getTypeOperation().equals(TypeOperation.PULL) ) { // && mapComs.get(TypeOperation.PULL).getModeFacturation().equals(ModeFacturation.TRANSACTION) ) {

			// Debit du client du TTC
			eve.getEcritures().add( new bkmvti(rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), rsCpteAbonne.getString("cha"), rsCpteAbonne.getString("ncp"), rsCpteAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteAbonne.getString("clc"), dco, null, dvaDebit, round(transaction.getAmount() + valeurCom + valeurTax, 0), "D", "PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), round(transaction.getAmount() + valeurCom + valeurTax, 0), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

//			COMMENTE
//			if(!StringUtils.equalsIgnoreCase(rsCpteLiaison.getString("age"), rsLiaisonHippo.getString("age"))){
//				// Credit de la liaison du client du TTC
//				if(rsCpteLiaison != null) eve.getEcritures().add( new bkmvti(rsCpteLiaison.getString("age"), rsCpteLiaison.getString("dev"), rsCpteLiaison.getString("cha"), rsCpteLiaison.getString("ncp"), rsCpteLiaison.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteLiaison.getString("clc"), dco, null, dvaDebit, round(transaction.getAmount() + valeurCom + valeurTax, 0), "C", "PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteLiaison.getString("age"), rsCpteLiaison.getString("dev"), round(transaction.getAmount() + valeurCom + valeurTax, 0), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
//
//				// Debit de la liaison d'hippodromme du TTC
//				if(rsLiaisonHippo != null) eve.getEcritures().add( new bkmvti(rsLiaisonHippo.getString("age"), rsLiaisonHippo.getString("dev"), rsLiaisonHippo.getString("cha"), rsLiaisonHippo.getString("ncp"), rsLiaisonHippo.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonHippo.getString("clc"), dco, null, dvaDebit, round(transaction.getAmount() + valeurCom + valeurTax, 0), "D", "PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonHippo.getString("age"), rsLiaisonHippo.getString("dev"), round(transaction.getAmount() + valeurCom + valeurTax, 0), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
//			}
						
			// Comptes de debit (client) et credit (MTN) dans des agences differentes
			if(!StringUtils.equalsIgnoreCase(rsLiaisonAbonne.getString("age"), rsLiaisonDAPMTN.getString("age"))){
				
				// Credit de la liaison du client du montant HT
				if(rsLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), rsLiaisonAbonne.getString("cha"), rsLiaisonAbonne.getString("ncp"), rsLiaisonAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonAbonne.getString("clc"), dco, null, dvaCredit, transaction.getAmount(), "C", "PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), transaction.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
				// Debit de la liaison de MTN du montant HT
				if(rsLiaisonDAPMTN != null) eve.getEcritures().add( new bkmvti(rsLiaisonDAPMTN.getString("age"), rsLiaisonDAPMTN.getString("dev"), rsLiaisonDAPMTN.getString("cha"), rsLiaisonDAPMTN.getString("ncp"), rsLiaisonDAPMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonDAPMTN.getString("clc"), dco, null, dvaDebit, transaction.getAmount(), "D", "PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonDAPMTN.getString("age"), rsLiaisonDAPMTN.getString("dev"), transaction.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
				
			}
			// Comptes de debit (client) et credit (commissions) dans des agences differentes
			if( valeurCom > 0 && !StringUtils.equalsIgnoreCase(rsLiaisonAbonne.getString("age"), rsLiaisonComs.getString("age"))){
				
				// Credit de la liaison du client du montant des commissions
				if(rsLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), rsLiaisonAbonne.getString("cha"), rsLiaisonAbonne.getString("ncp"), rsLiaisonAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonAbonne.getString("clc"), dco, null, dvaCredit, valeurCom, "C", "FRAIS PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), valeurCom, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
				// Debit de la liaison des commissions
				if(rsLiaisonComs != null) eve.getEcritures().add( new bkmvti(rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), rsLiaisonComs.getString("cha"), rsLiaisonComs.getString("ncp"), rsLiaisonComs.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonComs.getString("clc"), dco, null, dvaDebit, valeurCom, "D", "FRAIS PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), valeurCom, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
				
			}
			// Comptes de debit (client) et credit (taxes) dans des agences differentes
			if( valeurCom > 0 && !StringUtils.equalsIgnoreCase(rsLiaisonAbonne.getString("age"), rsLiaisonTva.getString("age"))){
				
				// Debit de la liaison du client du montant de la taxe
				if(rsLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), rsLiaisonAbonne.getString("cha"), rsLiaisonAbonne.getString("ncp"), rsLiaisonAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonAbonne.getString("clc"), dco, null, dvaCredit, valeurTax, "C", "TAX PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), valeurTax, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
				// Credit de la liaison des taxes
				if(rsLiaisonTva != null) eve.getEcritures().add( new bkmvti(rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), rsLiaisonTva.getString("cha"), rsLiaisonTva.getString("ncp"), rsLiaisonTva.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonTva.getString("clc"), dco, null, dvaDebit, valeurTax, "D", "TAX PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), valeurTax, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
				
			}
			
			// Credit du cpte MTN du HT
			eve.getEcritures().add( new bkmvti(rsCpteDAPMTN.getString("age"), rsCpteDAPMTN.getString("dev"), rsCpteDAPMTN.getString("cha"), rsCpteDAPMTN.getString("ncp"), rsCpteDAPMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteDAPMTN.getString("clc"), dco, null, dvaCredit, transaction.getAmount(), "C", "PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteDAPMTN.getString("age"), rsCpteDAPMTN.getString("dev"), transaction.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

			// Credit du cpte Commissions de valeurCom
			if(rsCpteComs != null && valeurCom > 0) eve.getEcritures().add( new bkmvti(rsCpteComs.getString("age"), rsCpteComs.getString("dev"), rsCpteComs.getString("cha"), rsCpteComs.getString("ncp"), rsCpteComs.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteComs.getString("clc"), dco, null, dvaCredit, valeurCom, "C", "FRAIS PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteComs.getString("age"), rsCpteComs.getString("dev"), valeurCom, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

			// Credit du compte TVA de valeurTVA
			if(rsCpteTVA != null && valeurTax > 0) eve.getEcritures().add( new bkmvti(rsCpteTVA.getString("age"), rsCpteTVA.getString("dev"), rsCpteTVA.getString("cha"), rsCpteTVA.getString("ncp"), rsCpteTVA.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteTVA.getString("clc"), dco, null, dvaCredit, valeurTax, "C", "TAX PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteTVA.getString("age"), rsCpteTVA.getString("dev"), valeurTax, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;


			// Si c'est un PUSH
		} else if(transaction.getTypeOperation().equals(TypeOperation.PUSH) ) { // && mapComs.get(TypeOperation.PUSH).getModeFacturation().equals(ModeFacturation.TRANSACTION)) {

			// Debit du cpte MTN du montant HT
			eve.getEcritures().add( new bkmvti(rsCpteDAPMTN.getString("age"), rsCpteDAPMTN.getString("dev"), rsCpteDAPMTN.getString("cha"), rsCpteDAPMTN.getString("ncp"), rsCpteDAPMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteDAPMTN.getString("clc"), dco, null, dvaCredit, transaction.getAmount(), "D", "PUSH/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteDAPMTN.getString("age"), rsCpteDAPMTN.getString("dev"), transaction.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

			if(!StringUtils.equalsIgnoreCase(rsLiaisonAbonne.getString("age"), rsLiaisonDAPMTN.getString("age"))){

				// Credit de la liaison de MTN du montant HT
				if(rsLiaisonDAPMTN != null) eve.getEcritures().add( new bkmvti(rsLiaisonDAPMTN.getString("age"), rsLiaisonDAPMTN.getString("dev"), rsLiaisonDAPMTN.getString("cha"), rsLiaisonDAPMTN.getString("ncp"), rsLiaisonDAPMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonDAPMTN.getString("clc"), dco, null, dvaCredit, transaction.getAmount(), "C", "PUSH/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonDAPMTN.getString("age"), rsLiaisonDAPMTN.getString("dev"), transaction.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

				// Debit de la liaison du client du montant HT
				if(rsLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), rsLiaisonAbonne.getString("cha"), rsLiaisonAbonne.getString("ncp"), rsLiaisonAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonAbonne.getString("clc"), dco, null, dvaCredit, transaction.getAmount(), "D", "PUSH/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), transaction.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

			}

			// Credit du cpte client du HT
			eve.getEcritures().add( new bkmvti(rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), rsCpteAbonne.getString("cha"), rsCpteAbonne.getString("ncp"), rsCpteAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteAbonne.getString("clc"), dco, null, dvaCredit, transaction.getAmount(), "C", "PUSH/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), transaction.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

			// Debit du client de valeurCom + valeurTaxes
			if(valeurCom + valeurTax > 0) eve.getEcritures().add( new bkmvti(rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), rsCpteAbonne.getString("cha"), rsCpteAbonne.getString("ncp"), rsCpteAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteAbonne.getString("clc"), dco, null, dvaCredit, round(valeurCom + valeurTax, 0), "D", "FRAIS MAC/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), round(valeurCom + valeurTax, 0), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

//			COMMENTE
//			if(!StringUtils.equalsIgnoreCase(rsCpteLiaison.getString("age"), rsLiaisonHippo.getString("age"))){
//
//				// Credit Liaison Client de valeurCom + valeurTaxes
//				if(rsCpteLiaison != null && valeurCom + valeurTax > 0) eve.getEcritures().add( new bkmvti(rsCpteLiaison.getString("age"), rsCpteLiaison.getString("dev"), rsCpteLiaison.getString("cha"), rsCpteLiaison.getString("ncp"), rsCpteLiaison.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteLiaison.getString("clc"), dco, null, dvaCredit, round(valeurCom + valeurTax, 0), "C", "PUSH/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteLiaison.getString("age"), rsCpteLiaison.getString("dev"), round(valeurCom + valeurTax, 0), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
//
//				// Debit Liaison Hippodrome de valeurCom + valeurTaxes
//				if(rsLiaisonHippo != null && valeurCom + valeurTax > 0) eve.getEcritures().add( new bkmvti(rsLiaisonHippo.getString("age"), rsLiaisonHippo.getString("dev"), rsLiaisonHippo.getString("cha"), rsLiaisonHippo.getString("ncp"), rsLiaisonHippo.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonHippo.getString("clc"), dco, null, dvaCredit, round(valeurCom + valeurTax, 0), "D", "PUSH/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonHippo.getString("age"), rsLiaisonHippo.getString("dev"), round(valeurCom + valeurTax, 0), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
//
//			}
			
			
			// Comptes de debit (client) et credit (commissions) dans des agences differentes
			if( valeurCom > 0 && !StringUtils.equalsIgnoreCase(rsLiaisonAbonne.getString("age"), rsLiaisonComs.getString("age"))){
				
				// Credit de la liaison du client du montant des commissions
				if(rsLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), rsLiaisonAbonne.getString("cha"), rsLiaisonAbonne.getString("ncp"), rsLiaisonAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonAbonne.getString("clc"), dco, null, dvaCredit, valeurCom, "C", "FRAIS PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), valeurCom, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
				// Debit de la liaison des commissions
				if(rsLiaisonComs != null) eve.getEcritures().add( new bkmvti(rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), rsLiaisonComs.getString("cha"), rsLiaisonComs.getString("ncp"), rsLiaisonComs.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonComs.getString("clc"), dco, null, dvaDebit, valeurCom, "D", "FRAIS PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), valeurCom, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
				
			}
			// Comptes de debit (client) et credit (taxes) dans des agences differentes
			if( valeurTax > 0 && !StringUtils.equalsIgnoreCase(rsLiaisonAbonne.getString("age"), rsLiaisonTva.getString("age"))){
				
				// Credit de la liaison du client du montant de la taxe
				if(rsLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), rsLiaisonAbonne.getString("cha"), rsLiaisonAbonne.getString("ncp"), rsLiaisonAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonAbonne.getString("clc"), dco, null, dvaCredit, valeurTax, "C", "TAX PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), valeurTax, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
				// Debit de la liaison des taxes
				if(rsLiaisonTva != null) eve.getEcritures().add( new bkmvti(rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), rsLiaisonTva.getString("cha"), rsLiaisonTva.getString("ncp"), rsLiaisonTva.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonTva.getString("clc"), dco, null, dvaDebit, valeurTax, "D", "TAX PULL/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), valeurTax, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
				
			}
			

			// Credit des commissions de valeurCom
			if(rsCpteComs != null && valeurCom > 0) eve.getEcritures().add( new bkmvti(rsCpteComs.getString("age"), rsCpteComs.getString("dev"), rsCpteComs.getString("cha"), rsCpteComs.getString("ncp"), rsCpteComs.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteComs.getString("clc"), dco, null, dvaCredit, valeurCom, "C", "COM MAC/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteComs.getString("age"), rsCpteComs.getString("dev"), valeurCom, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

			// Credit du cpte TVA de valeurTaxes
			if(rsCpteTVA != null && valeurTax > 0) eve.getEcritures().add( new bkmvti(rsCpteTVA.getString("age"), rsCpteTVA.getString("dev"), rsCpteTVA.getString("cha"), rsCpteTVA.getString("ncp"), rsCpteTVA.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteTVA.getString("clc"), dco, null, dvaCredit, valeurTax, "C", "TAX MAC/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteTVA.getString("age"), rsCpteTVA.getString("dev"), valeurTax, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

			// Si c'est une autre operation
		} else {

			// Si les commissions existent
			if( valeurCom + valeurTax > 0) {
				// Debit du cpte client du montant valeurCom + valeurTVA
				eve.getEcritures().add( new bkmvti(rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), rsCpteAbonne.getString("cha"), rsCpteAbonne.getString("ncp"), rsCpteAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteAbonne.getString("clc"), dco, null, rsCpteAbonne.getDate("dva"), round(valeurCom + valeurTax, 0), "D", "FRAIS MAC/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), round(valeurCom + valeurTax, 0), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

				// Comptes de debit (client) et credit (commissions) dans des agences differentes
				if( valeurCom > 0 && !StringUtils.equalsIgnoreCase(rsLiaisonAbonne.getString("age"), rsLiaisonComs.getString("age"))){
					
					// Credit de la liaison du client du montant des commissions
					if(rsLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), rsLiaisonAbonne.getString("cha"), rsLiaisonAbonne.getString("ncp"), rsLiaisonAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonAbonne.getString("clc"), dco, null, dvaCredit, valeurCom, "C", "COM MAC/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), valeurCom, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
					// Debit de la liaison des commissions
					if(rsLiaisonComs != null) eve.getEcritures().add( new bkmvti(rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), rsLiaisonComs.getString("cha"), rsLiaisonComs.getString("ncp"), rsLiaisonComs.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonComs.getString("clc"), dco, null, dvaDebit, valeurCom, "D", "COM MAC/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), valeurCom, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
					
				}
				// Comptes de debit (client) et credit (taxes) dans des agences differentes
				if( valeurTax > 0 && !StringUtils.equalsIgnoreCase(rsLiaisonAbonne.getString("age"), rsLiaisonTva.getString("age"))){
					
					// Credit de la liaison du client du montant de la taxe
					if(rsLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), rsLiaisonAbonne.getString("cha"), rsLiaisonAbonne.getString("ncp"), rsLiaisonAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonAbonne.getString("clc"), dco, null, dvaCredit, valeurTax, "C", "TAX MAC/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), valeurTax, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
					// Debit de la liaison des taxes
					if(rsLiaisonTva != null) eve.getEcritures().add( new bkmvti(rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), rsLiaisonTva.getString("cha"), rsLiaisonTva.getString("ncp"), rsLiaisonTva.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonTva.getString("clc"), dco, null, dvaDebit, valeurTax, "D", "TAX MAC/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), valeurTax, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
					
				}
	
				// Credit cpte Comissions de valeurCom
				if(rsCpteComs != null && valeurCom > 0) eve.getEcritures().add( new bkmvti(rsCpteComs.getString("age"), rsCpteComs.getString("dev"), rsCpteComs.getString("cha"), rsCpteComs.getString("ncp"), rsCpteComs.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteComs.getString("clc"), dco, null, rsCpteComs.getDate("dva"), valeurCom, "C", "COM MAC/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteComs.getString("age"), rsCpteComs.getString("dev"), valeurCom, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
	
				// Credit cpte TVA de valeurTVA
				if(rsCpteTVA != null && valeurTax > 0) eve.getEcritures().add( new bkmvti(rsCpteTVA.getString("age"), rsCpteTVA.getString("dev"), rsCpteTVA.getString("cha"), rsCpteTVA.getString("ncp"), rsCpteTVA.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteTVA.getString("clc"), dco, null, rsCpteTVA.getDate("dva"), valeurTax, "C", "TAX MAC/" + datopec + "/" + transaction.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteTVA.getString("age"), rsCpteTVA.getString("dev"), valeurTax, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
			}
		}
		logger.info("IN buildEvenement end");
		// On libere les variables
		if(StringUtils.isNotBlank(ncpDAP)) ncpDAP = null;
		if(rsCpteAbonne != null) {
			rsCpteAbonne.close();
			if(rsCpteAbonne.getStatement() != null) {
				rsCpteAbonne.getStatement().close();
			}
			rsCpteAbonne = null;
		}
		if(rsCpteDAPMTN != null) {
			rsCpteDAPMTN.close();
			if(rsCpteDAPMTN.getStatement() != null) {
				rsCpteDAPMTN.getStatement().close();
			}
			rsCpteDAPMTN = null;
		}
		if(rsCpteComs != null) {
			rsCpteComs.close();
			if(rsCpteComs.getStatement() != null) {
				rsCpteComs.getStatement().close();
			}
			rsCpteComs = null;
		}
		if(rsCpteTVA != null) {
			rsCpteTVA.close();
			if(rsCpteTVA.getStatement() != null) {
				rsCpteTVA.getStatement().close();
			}
			rsCpteTVA = null;
		}
		if(rsLiaisonAbonne != null) {
			rsLiaisonAbonne.close();
			if(rsLiaisonAbonne.getStatement() != null) {
				rsLiaisonAbonne.getStatement().close();
			}
			rsLiaisonAbonne = null;
		}
		if(rsLiaisonComs != null) {
			rsLiaisonComs.close();
			if(rsLiaisonComs.getStatement() != null) {
				rsLiaisonComs.getStatement().close();
			}
			rsLiaisonComs = null;
		}
		if(rsLiaisonTva != null) {
			rsLiaisonTva.close();
			if(rsLiaisonTva.getStatement() != null) {
				rsLiaisonTva.getStatement().close();
			}
			rsLiaisonTva = null;
		}
		if(rsLiaisonDAPMTN != null) {
			rsLiaisonDAPMTN.close();
			if(rsLiaisonDAPMTN.getStatement() != null) {
				rsLiaisonDAPMTN.getStatement().close();
			}
			rsLiaisonDAPMTN = null;
		}
		logger.info("IN buildEvenement end+");
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
			rs = null;
		}
		logger.info("IN buildEvenement end++");
		mapComs.clear();
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		// Retourne l'evenement
		return eve;

	}


	private Long getLastEveNum(DataSystem dsCBS) throws Exception{

		boolean modeNuit = isModeNuit();

		// Recherche des parametres
		// checkGlobalConfig();
		params = findParameters();

		/***************************************************************/
		/*** LECTURE DU DERNIER NUMERO D'EVENEMENT DU TYPE OPERATION ***/
		/***************************************************************/

		// Recuperation du dernier numero evenement du type operation
		ResultSet rs = executeFilterSystemQuery(dsCBS, modeNuit ? MoMoHelper.getDefaultCBSQueries().get(2).getQuery().replaceAll("bkope", "syn_bkope") : MoMoHelper.getDefaultCBSQueries().get(2).getQuery(), new Object[]{ params.getCodeOperation() });

		// Log
		//logger.info("Lecture du dernier numero d'evenement genere OK!");

		// Calcul du numero d'evenement
		Long numEve = rs != null && rs.next() ? numEve = rs.getLong("num") + 1 : 1l;

		// Log
		//logger.info("Calcul du prochain numero d'evenement OK!");

		// Fermeture de cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		return numEve;
	}


	@Asynchronous
	private void reduitSolde(String numCompte, Double montant, boolean nuit) throws Exception {

		// Initialisation de DataStore d'Amplitude
		if(dsCBS == null) findCBSDataSystem();

		String req = "update "+ (nuit ? "syn_" : "") +"bkcom set sin = sin - "+ montant.longValue() +" where age=? and ncp=? and clc=?";
		executeUpdateSystemQuery(dsCBS, req, new Object[]{numCompte.split("-")[0], numCompte.split("-")[1], numCompte.split("-")[2]});
	}

	
	/**
	 * Retourne le solde du compte passe en parametre
	 * @param numCompte
	 * @return solde du compte
	 * @throws Exception
	 */
	private Double getSolde(String numCompte) throws Exception {
		params = findParameters();
		if(params.getCbsServices()) {
			return getSoldeInCoreBanking(numCompte);
		}
		else {
			// Initialisation de la valeur du solde a retourne
			Double solde = 0d;
	
			if(numCompte == null || numCompte.isEmpty()) return solde;
	
			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();
	
			// Marqueur du mode nuit
			boolean nuit = isModeNuit();
	
			// Requete de recuperation du solde indicatif du compte
			String req = "select a.sin-nvl(a.minds,0)+nvl(maut,0) as solde " +
					"from "+ (nuit ? "syn_" : "") +"bkcom a left join bkautc b on a.age=b.age and a.dev=b.dev and a.ncp=b.ncp " +
					"and eta in ('VA','VF','FO') and today between b.debut and b.fin " +
					"where a.cfe='N' and a.ife='N' and a.age='"+ numCompte.split("-")[0] +"' and a.clc='"+ numCompte.split("-")[2] +"' and a.ncp='"+ numCompte.split("-")[1] +"'";
	
			// Requete de recuperation du cumul des transactions effectuees pendant le mode nuit
			String req2 = "select nvl(sum(mon),0) as sin from bksin where age='"+ numCompte.split("-")[0] +"' and ncp='"+ numCompte.split("-")[1] +"'";
	
			// Execution de la requete de selection du solde indicatif du compte 
			ResultSet rs = executeFilterSystemQuery(dsCBS, req, null); //  nuit ? "select sin + (select nvl(sum(mon),0) as sin from bksin where age='"+ numCompte.split("-")[0] +"' and ncp='"+ numCompte.split("-")[1] +"') as sin from syn_bkcom where age='"+ numCompte.split("-")[0] +"' and ncp='"+ numCompte.split("-")[1] +"' and clc='"+ numCompte.split("-")[2] +"'  " : "select sin from bkcom where age = '"+ numCompte.split("-")[0] +"' and ncp = '"+ numCompte.split("-")[1] +"' and clc = '"+ numCompte.split("-")[2] +"' ", null) ;
	
			// Recuperation du solde du compte
			if(rs != null && rs.next()) solde = rs.getDouble("solde");
	
			// Si on est en mode nuit
			if(nuit){
				// MAJ du solde recupere
				rs = executeFilterSystemQuery(dsCBS, req2, null); // MoMoHelper.getDefaultCBSQueries().get(11).getQuery(), new Object[]{ numCompte.split("-")[1], new Date() });
				if(rs != null && rs.next()) solde += rs.getDouble("sin");
			}
			// Si le decouvert existe (on ajoute le montant de l'autorisation au solde du client)
			//if(rs != null && rs.next()) solde += rs.getDouble("maut");
	
			// Fermeture de la cnx
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
			
			// Retourne le solde du cpte
			return solde;
		}

	}

	private Double getSolde(String numCompte, boolean nuit) throws Exception {
		params = findParameters();
		if(params.getCbsServices()) {
			return getSoldeInCoreBanking(numCompte);
		}
		else {
			// Initialisation de la valeur du solde a retourne
			Double solde = 0d;
	
			if(numCompte == null || numCompte.isEmpty()) return solde;
	
			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();
	
			// Requete de recuperation du solde indicatif du compte
			String req = "select a.sin-nvl(a.minds,0)+nvl(maut,0) as solde " +
					"from "+ (nuit ? "syn_" : "") +"bkcom a left join bkautc b on a.age=b.age and a.dev=b.dev and a.ncp=b.ncp " +
					"and eta in ('VA','VF','FO') and today between b.debut and b.fin " +
					"where a.cfe='N' and a.ife='N' and a.age='"+ numCompte.split("-")[0] +"' and a.clc='"+ numCompte.split("-")[2] +"' and a.ncp='"+ numCompte.split("-")[1] +"'";
	
			// Requete de recuperation du cumul des transactions effectuees pendant le mode nuit
			String req2 = "select nvl(sum(mon),0) as sin from bksin where age='"+ numCompte.split("-")[0] +"' and ncp='"+ numCompte.split("-")[1] +"'";
	
			// Execution de la requete de selection du solde indicatif du compte 
			ResultSet rs = executeFilterSystemQuery(dsCBS, req, null); //  nuit ? "select sin + (select nvl(sum(mon),0) as sin from bksin where age='"+ numCompte.split("-")[0] +"' and ncp='"+ numCompte.split("-")[1] +"') as sin from syn_bkcom where age='"+ numCompte.split("-")[0] +"' and ncp='"+ numCompte.split("-")[1] +"' and clc='"+ numCompte.split("-")[2] +"'  " : "select sin from bkcom where age = '"+ numCompte.split("-")[0] +"' and ncp = '"+ numCompte.split("-")[1] +"' and clc = '"+ numCompte.split("-")[2] +"' ", null) ;
	
			// Recuperation du solde du compte
			if(rs != null && rs.next()) solde = rs.getDouble("solde");
	
			// Si on est en mode nuit
			if(nuit){
				// MAJ du solde recupere
				rs = executeFilterSystemQuery(dsCBS, req2, null); // MoMoHelper.getDefaultCBSQueries().get(11).getQuery(), new Object[]{ numCompte.split("-")[1], new Date() });
				if(rs != null && rs.next()) solde += rs.getDouble("sin");
			}
			// Si le decouvert existe (on ajoute le montant de l'autorisation au solde du client)
			//if(rs != null && rs.next()) solde += rs.getDouble("maut");
	
			// Fermeture de la cnx
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
			
			// Retourne le solde du cpte
			return solde;
		}

	}


	private List<AccountUtils> getMapSoldes(String sql) throws Exception {

		// Initialisation de la valeur a retourne
		List<AccountUtils> lau = new ArrayList<AccountUtils>();

		// Initialisation de DataStore d'Amplitude
		if(dsCBS == null) findCBSDataSystem();

		// Requete de recuperation du solde indicatif du compte
		String req = "select age,ncp,clc,cfe,ife,sin from bkcom where dev='001' and ("+sql+");";

		// Execution de la requete de selection du solde indicatif du compte 
		ResultSet rs = executeFilterSystemQuery(dsCBS, req, null); //  nuit ? "select sin + (select nvl(sum(mon),0) as sin from bksin where age='"+ numCompte.split("-")[0] +"' and ncp='"+ numCompte.split("-")[1] +"') as sin from syn_bkcom where age='"+ numCompte.split("-")[0] +"' and ncp='"+ numCompte.split("-")[1] +"' and clc='"+ numCompte.split("-")[2] +"'  " : "select sin from bkcom where age = '"+ numCompte.split("-")[0] +"' and ncp = '"+ numCompte.split("-")[1] +"' and clc = '"+ numCompte.split("-")[2] +"' ", null) ;

		// Recuperation du solde du compte
		if(rs != null){
			while(rs.next()){
				lau.add(new AccountUtils(rs.getString("age"), rs.getString("ncp"), rs.getString("clc"), rs.getString("ife"), rs.getString("cfe"), rs.getDouble("sin")));
			}
		}

		// Fermeture de la cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		//for(AccountUtils l : lau) logger.info(l);

		// Retourne le solde du cpte
		return lau;

	}


	/**
	 * Retourne la table des comptes a utiliser (Gestion du cas des TFJ)
	 * @return
	 * @throws Exception
	 *
	private String getTableComptesAUtiliser() throws Exception {

		// Retourne la table des comptes a utiliser
		return "bkcom"; //getStatutAgenceCentrale().equalsIgnoreCase("OU") ? "bkcom" : "bkcom";
	}*/

	
	/**
	 * Timer qui teste la fin des TFJ (toutes les heures)
	 */
	private void TFJScheduler() {

		// Initialisation du Timer
		Timer timer = new Timer();

		// Initialisation de la tache a executer
		timer.schedule(new TimerTask() {

			// Initialisation du compteur du delai de controle du lancement des tfj dans Amplitude
			int n = 0; boolean aEteFerme = false;

			@Override
			public void run() {
				try{

					//logger.info("[Traitement TFJ MAC]. Etape " + n);

					if(getStatutAgenceCentrale().equalsIgnoreCase("OU")) {

						//logger.info("[Traitement TFJ MAC]. Agence Ouverte");

						if( n >= delaiCtrlTFJDelta && aEteFerme ){

							//logger.info("[Traitement TFJ MAC]. Nbre de passage=" + n + " et Agence tjrs ouverte donc, execution de la fin des TFJ");

							// Suspension du Timer
							this.cancel();

							// Alors les TFJ sont finis et on execute les operations PST-TFJ
							endTFJ();

						} else if(n>=40){
							this.cancel(); endTFJ();
						}

					} else aEteFerme = true;

					// On incremente le compteur de controle de delai
					n++;

				}catch(Exception ex){logger.error("Erreur lors de la cloture des TFJ"); ex.printStackTrace();}

			}

			// Delai d'execution du Timer (1h)
		}, 0, nbMinutesCtrlFinTFJ * 1000);
	}

	
	public boolean isSoldeSuffisant(String numCompte, Double montant) throws Exception {

		return getSolde(numCompte) >= montant;
	}
	
	public boolean _isSoldeSuffisant(String numCompte, Double montant) throws Exception {
		return getSolde(numCompte) >= montant;
	}

	
	public boolean isCompteFerme(String numCompte) {

		boolean res = false;

		try {

			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();

			ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(7).getQuery(), new Object[]{ numCompte.split("-")[0], numCompte.split("-")[1], numCompte.split("-")[2] });

			if(rs == null || !rs.next()) res = true;

			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
			
		} catch(Exception e){}

		return res;

	}
	

	public boolean isClientEmploye(String numClient) {

		boolean res = false;

		try {

			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();

			ResultSet rs = executeFilterSystemQuery(dsCBS, "select pro from bkprocli where cli='"+ numClient +"' order by dpro desc", null);

			if(rs != null && rs.next()) res = rs.getString("pro").equals("110");

			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
			
		} catch(Exception e){}

		return res;

	}
	

//	public boolean isCompteEnOpposition(String age, String ncp){
//
//		boolean res = false;
//
//		try {
//
//			// Initialisation de DataStore d'Amplitude
//			if(dsCBS == null) findCBSDataSystem();
//
//			ResultSet rs = executeFilterSystemQuery(dsCBS, "select * from bkoppcom where age=? and ncp=? and ((dfin is null and eta='V') or (dfin>=today and eta='V') ) ", new Object[]{age, ncp});
//
//			res = rs != null && rs.next();
//
//			rs.close(); rs.getStatement().close();
//
//		} catch(Exception e){}
//
//		return res;
//
//	}
	
	
	/**
	 * Nouvelle version
	 * @param age
	 * @param ncp
	 * @return
	 */
	public boolean isCompteEnOpposition(String age, String ncp, String ope){
		
		try {
			
			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();

			ResultSet rs = executeFilterSystemQuery(dsCBS, "select opp from bkoppcom where age=? and ncp=? and eta='V' and (dfin>=today or dfin is null) ", new Object[]{age, ncp});

			List<String> opps = new ArrayList<String>();
			//res = rs != null && rs.next();
			// Recuperation du solde du compte
			if(rs != null){
				while(rs.next()){
					logger.info("OPP = "+rs.getString("opp"));
					opps.add(rs.getString("opp"));
				}
				for(String oppp : opps){
					rs = executeFilterSystemQuery(dsCBS, " select copp from bkopl where (age='00099' or age = ? ) and copp='O' and ope=? and opp= ?  ", new Object[]{age, ope, oppp});
					logger.info("AFTER RS OK");
					if(rs != null && rs.next()){
						logger.info("OK");
						// CBS_CNX_OPTI
						if(rs != null) {
							rs.close(); 
							if(rs.getStatement() != null) {
								rs.getStatement().close();
							}
						}
						if(conCBS != null ) conCBS.close();
						return Boolean.TRUE;
					}
				}

			}
			// CBS_CNX_OPTI
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
			if(conCBS != null ) conCBS.close();
			
		} catch(Exception e){}
		
		return Boolean.FALSE;
	}
	
	
	/**
	 * Nouvelle version
	 * @param age
	 * @param ncp
	 * @return
	 */
	public boolean isCompteEnOppositionCredit(String age, String ncp){
		boolean res = Boolean.FALSE;
		try {
			params = findParameters();
			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();
			logger.info("CHECKING OPPOSITION CREDIT !!! ");
			String sql = "select * from bkoppcom where eta='V' and (dfin>=today or dfin is null) and opp in LIST_CODE_OPP and age = ? and ncp = ? ";

			String in = "";
			if(params.getCodesOpp() != null && !params.getCodesOpp().trim().isEmpty()) {
//				logger.info("CODES OPPOSITION CONFIGURES : "+params.getCodesOpp());
				for(String code : params.getCodesOpp().split(",")) in += "'" + code + "'" + ", ";
			}
			if(!in.isEmpty()) in = "(".concat( in.substring(0, in.length()-2) ).concat(")");
			
			ResultSet rs = executeFilterSystemQuery(dsCBS, sql.replace("LIST_CODE_OPP", in), new Object[]{age, ncp});
//			ResultSet rs = executeFilterSystemQuery(dsCBS, "select opp from bkoppcom where age=? and ncp=? and eta='V' ", new Object[]{age, ncp});
			
			if(rs != null && rs.next()){
				logger.info("OPPOSITION CREDIT OK");
				// CBS_CNX_OPTI
				if(rs != null) {
					rs.close(); 
					if(rs.getStatement() != null) {
						rs.getStatement().close();
					}
				}
				if(conCBS != null ) conCBS.close();
				res = Boolean.TRUE;
			}
			
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
			
		} catch(Exception e){}
		
		return res;
	}
	
	
//	public boolean isBlocageDebit(String numCompte,String ope,String opp) throws Exception{
//
//		// Controle Opposition Debit sur le Compte    
//		ResultSet rs = executeFilterSystemQuery(dsCBS, CODE_OPE, new Object[]{numCompte.split("-")[0],numCompte.split("-")[1]});
//		List<String> opps = new ArrayList<String>();
//		while(rs.next()){
//			opps.add(rs.getString("opp"));
//		}
//		//System.out.println("********** getOppositionOperation opps.size() ***********" + opps.size());
//		//System.out.println("********** getOppositionOperation opps.toString() ***********" + opps.toString());
//
//		return getOppositionOperation(ope, numCompte.split("-")[0], opps);
//
//	}
//
//
//	public Boolean getOppositionOperation(String ope,String age,List<String> opps)throws Exception{
//		for(String oppp : opps){
//			ResultSet rs = executeFilterSystemQuery(dsCBS, " select copp from bkopl where (age='00099' or age = ? ) and copp='O' and ope=? and opp= ?  ", new Object[]{age,ope,oppp});
//			if(rs != null && rs.next()){
//				//System.out.println("********** getOppositionOperation Boolean.TRUE ***********");
//				return Boolean.TRUE;
//			}
//		}
//
//		//System.out.println("********** getOppositionOperation Boolean.FALSE ***********");
//		return Boolean.FALSE;
//	}


	private Boolean isTestPhoneNumber (String phoneNumber, List<String> listPhoneTest){

		for(String p : listPhoneTest){
			if(p.equals(phoneNumber)) return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}


	private Transaction getTransactionFromRequestMessage(RequestMessage message) throws Exception, MoMoException {

		Subscriber subs = findSubscriberFromPhoneNumber(message.getPhoneNumber());
		// checkGlobalConfig(); //
		params = findParameters();

		// Test de l'etat du service SDP
		if(params.getEtatServiceSDP().equals(StatutService.OFF)){
			//logger.info("SERVICE SDP OFF : Vous n'etes pas autorise a effectuer des transactions par le canal SDP");
			// Message d'avertissement
			throw new MoMoException(ExceptionCode.MobileMoneySDPServiceSuspended, ExceptionCode.MobileMoneySDPServiceSuspended.getValue(), ExceptionCategory.SYSTEM);
		}

		// Test de l'autorisation des trx Marchand et End-user
		if(Boolean.TRUE.equals(subs.isMerchant())){
			if(params.getAllowMerchandTrxSDP().equals(Boolean.FALSE)){
				//logger.info("SERVICE SDP MARCHAND OFF : Les transactions marchand ne sont pas autorises par le canal SDP");
				// Message d'avertissement
				throw new MoMoException(ExceptionCode.MobileMoneySDPMerchandTrxSuspended, ExceptionCode.MobileMoneySDPMerchandTrxSuspended.getValue(), ExceptionCategory.SYSTEM);
			}
		}
		else{
			if(params.getAllowEndUserTrxSDP().equals(Boolean.FALSE)){
				//logger.info("SERVICE SDP END-USER OFF : Les transactions end-user ne sont pas autorises par le canal SDP");
				// Message d'avertissement
				throw new MoMoException(ExceptionCode.MobileMoneySDPEndUserTrxSuspended, ExceptionCode.MobileMoneySDPEndUserTrxSuspended.getValue(), ExceptionCategory.SYSTEM);
			}
		}

		// Test de l'etat du service SDP
		if(params.getEtatServiceSDP().equals(StatutService.TEST)){
			if(!isTestPhoneNumber(subs.getFirstPhone(), params.getNumerosTest())){
				//logger.info("MAINTENANCE SDP ACTIVEE : Ce numero n'est pas un numero de test");
				// Message d'avertissement
				throw new MoMoException(ExceptionCode.MobileMoneySDPServiceOnMaintenance, ExceptionCode.MobileMoneySDPServiceOnMaintenance.getValue(), ExceptionCategory.SYSTEM);
			}
		}

		//		// Test de l'etat du service SDP
		//		if(params.getEtatServiceSDP().equals(StatutService.ON)){
		//			if(Boolean.TRUE.equals(subs.isMerchant())){
		//				if(params.getAllowMerchandTrxSDP().equals(Boolean.FALSE)){
		//					// Message d'avertissement
		//					throw new MoMoException(ExceptionCode.MobileMoneySDPMerchandTrxSuspended, ExceptionCode.MobileMoneySDPMerchandTrxSuspended.getValue(), ExceptionCategory.SYSTEM);
		//				}
		//			}
		//			else{
		//				if(params.getAllowEndUserTrxSDP().equals(Boolean.FALSE)){
		//					// Message d'avertissement
		//					throw new MoMoException(ExceptionCode.MobileMoneySDPEndUserTrxSuspended, ExceptionCode.MobileMoneySDPEndUserTrxSuspended.getValue(), ExceptionCategory.SYSTEM);
		//				}
		//			}
		//		}

		// Test du montant
		if( (message.getOperation().equals(TypeOperation.PULL) || message.getOperation().equals(TypeOperation.PUSH)) &&  ( message.getAmount() == null || message.getAmount() <= 0 ) ) throw new MoMoException(ExceptionCode.SubscriberInvalidAmount, ExceptionCode.SubscriberInvalidAmount.getValue(), ExceptionCategory.SUBSCRIBER);

		// test de l'abonne
		if( subs == null ) {

			// Message d'avertissement
			throw new MoMoException(ExceptionCode.SubscriberInvalidPhone, ExceptionCode.SubscriberInvalidPhone.getValue(), ExceptionCategory.SUBSCRIBER);

		}

		// test du PIN
		if( !subs.getBankPIN().equals( Encrypter.getInstance().encryptText( message.getBankPIN() ) ) ) {

			// Message d'avertissement
			throw new MoMoException(ExceptionCode.SubscriberInvalidPIN, ExceptionCode.SubscriberInvalidPIN.getValue(), ExceptionCategory.SUBSCRIBER);

		}

		// Test du numero de telephone
		if( subs == null ) throw new MoMoException(ExceptionCode.SubscriberInvalidPhone, ExceptionCode.SubscriberInvalidPhone.getValue(), ExceptionCategory.SUBSCRIBER);


		// Test du numero de telephone
		if( subs.getStatus().equals(StatutContrat.WAITING) ) {

			// Message d'avertissement
			throw new MoMoException(ExceptionCode.SubscriberSuspended, ExceptionCode.SubscriberSuspended.getValue(), ExceptionCategory.SUBSCRIBER);
		}

		// Test du numero de telephone
		if( subs.getStatus().equals(StatutContrat.SUSPENDU) ) {

			// Message d'avertissement
			throw new MoMoException(ExceptionCode.SubscriberSuspended, ExceptionCode.SubscriberSuspended.getValue(), ExceptionCategory.SUBSCRIBER);
		}

		// Lecture du compte
		if(message.getAccount() == null || message.getAccount().isEmpty()) message.setAccount( subs.getAccounts().get(0) );

		// Test de l'existence du compte 
		if( isCompteFerme(message.getAccount()) ) {

			// Message d'avertissement
			throw new MoMoException(ExceptionCode.BankClosingAccount, ExceptionCode.BankClosingAccount.getValue(), ExceptionCategory.METIER);

		}
		
		//logger.info("******************subs.isMerchant()*********************"+subs.isMerchant());

		// Si c'est un retrait (verification du solde)	
		if(message.getOperation().equals(TypeOperation.PULL)) {

			// Test du solde du compte 
			if( !isSoldeSuffisant(message.getAccount(), message.getAmount())) {

				// Message d'avertissement
				throw new MoMoException(ExceptionCode.BankInsufficientBalance, ExceptionCode.BankInsufficientBalance.getValue(), ExceptionCategory.METIER);

			}

			//  Test si le compte est en opposition
			if(isCompteEnOpposition(message.getAccount().split("-")[0], message.getAccount().split("-")[1], params.getCodeOperation()) ){

				// Message d'avertissement
				throw new MoMoException(ExceptionCode.BankBlockingDebitAccount, ExceptionCode.BankBlockingDebitAccount.getValue(), ExceptionCategory.METIER);

			}

			// Si le montant de la transaction est superieur au montant max on leve une exception
			// Authorisation d'une Transaction en Fonction du Profil 
			// plg.getMaxPullAmount() > 0 && 
			if(Boolean.TRUE.equals(subs.isMerchant())){
				PlageTransactions plg = subs.getProfil();
				//logger.info("*******PlageTransactions***********"+plg);
				if(plg != null){
					if(message.getAmount() > plg.getMaxPullAmount()) throw new MoMoException(ExceptionCode.SubscriberInvalidAmount, ExceptionCode.SubscriberInvalidAmount.getValue(), ExceptionCategory.SUBSCRIBER);

					// plafond Journalier
					if(Boolean.FALSE.equals(subs.islimit(message.getOperation(),message.getAmount(),plg.getMaxPullAmountDay()))) throw new MoMoException(ExceptionCode.SubscriberLimitPullDayReached, ExceptionCode.SubscriberLimitPullDayReached.getValue(), ExceptionCategory.SUBSCRIBER);

					// plafond Hebdo
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),plg.getMaxPullAmountWeek(), "W"))) throw new MoMoException(ExceptionCode.SubscriberLimitPullWeekReached, ExceptionCode.SubscriberLimitPullWeekReached.getValue(), ExceptionCategory.SUBSCRIBER);

					// plafond Mensuel
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),plg.getMaxPullAmountMonth(), "M"))) throw new MoMoException(ExceptionCode.SubscriberLimitPullMonthReached, ExceptionCode.SubscriberLimitPullMonthReached.getValue(), ExceptionCategory.SUBSCRIBER);

				}else{
					if(message.getAmount() > params.getMaxPullAmount()) throw new MoMoException(ExceptionCode.SubscriberInvalidAmount, ExceptionCode.SubscriberInvalidAmount.getValue(), ExceptionCategory.SUBSCRIBER);

					// plafond Journalier
					if(Boolean.FALSE.equals(subs.islimit(message.getOperation(),message.getAmount(),params.getMaxPullAmountDay()))) throw new MoMoException(ExceptionCode.SubscriberLimitPullDayReached, ExceptionCode.SubscriberLimitPullDayReached.getValue(), ExceptionCategory.SUBSCRIBER);

					// plafond Hebdo
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPullAmountWeek(), "W"))) throw new MoMoException(ExceptionCode.SubscriberLimitPullWeekReached, ExceptionCode.SubscriberLimitPullWeekReached.getValue(), ExceptionCategory.SUBSCRIBER);

					// plafond Mensuel
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPullAmountMonth(), "M"))) throw new MoMoException(ExceptionCode.SubscriberLimitPullMonthReached, ExceptionCode.SubscriberLimitPullMonthReached.getValue(), ExceptionCategory.SUBSCRIBER);

				}
			}else{
				if( message.getAmount() > params.getMaxPullAmount()) throw new MoMoException(ExceptionCode.SubscriberInvalidAmount, ExceptionCode.SubscriberInvalidAmount.getValue(), ExceptionCategory.SUBSCRIBER);

				// plafond Journalier
				if(Boolean.FALSE.equals(subs.islimit(message.getOperation(),message.getAmount(),params.getMaxPullAmountDay()))) throw new MoMoException(ExceptionCode.SubscriberLimitPullDayReached, ExceptionCode.SubscriberLimitPullDayReached.getValue(), ExceptionCategory.SUBSCRIBER);

				// plafond Hebdo
				if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPullAmountWeek(), "W"))) throw new MoMoException(ExceptionCode.SubscriberLimitPullWeekReached, ExceptionCode.SubscriberLimitPullWeekReached.getValue(), ExceptionCategory.SUBSCRIBER);

				// plafond Mensuel
				if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPullAmountMonth(), "M"))) throw new MoMoException(ExceptionCode.SubscriberLimitPullMonthReached, ExceptionCode.SubscriberLimitPullMonthReached.getValue(), ExceptionCategory.SUBSCRIBER);

			}

		} else if(message.getOperation().equals(TypeOperation.PUSH)) {

			// Si le montant de la transaction est superieur au montant max on leve une exception
			// plg.getMaxPushAmount() > 0 && params.getMaxPushAmount() > 0 &&
			if(Boolean.TRUE.equals(subs.isMerchant())){
				PlageTransactions plg = subs.getProfil();
				//logger.info("*******PlageTransactions***********"+plg);
				if(plg != null){
					if( message.getAmount() > plg.getMaxPushAmount()) throw new MoMoException(ExceptionCode.SubscriberInvalidAmount, ExceptionCode.SubscriberInvalidAmount.getValue(), ExceptionCategory.SUBSCRIBER);

					// plafond Journalier
					if(Boolean.FALSE.equals(subs.islimit(message.getOperation(),message.getAmount(),plg.getMaxPushAmountDay()))) throw new MoMoException(ExceptionCode.SubscriberLimitPushDayReached, ExceptionCode.SubscriberLimitPushDayReached.getValue(), ExceptionCategory.SUBSCRIBER);

					// plafond Hebdo
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),plg.getMaxPushAmountWeek(), "W"))) throw new MoMoException(ExceptionCode.SubscriberLimitPushWeekReached, ExceptionCode.SubscriberLimitPushWeekReached.getValue(), ExceptionCategory.SUBSCRIBER);

					// plafond Mensuel
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),plg.getMaxPushAmountMonth(), "M"))) throw new MoMoException(ExceptionCode.SubscriberLimitPushMonthReached, ExceptionCode.SubscriberLimitPushMonthReached.getValue(), ExceptionCategory.SUBSCRIBER);

				}else{
					if( message.getAmount() > params.getMaxPushAmount()) throw new MoMoException(ExceptionCode.SubscriberInvalidAmount, ExceptionCode.SubscriberInvalidAmount.getValue(), ExceptionCategory.SUBSCRIBER);

					// plafond Journalier
					if(Boolean.FALSE.equals(subs.islimit(message.getOperation(),message.getAmount(),params.getMaxPushAmountDay()))) throw new MoMoException(ExceptionCode.SubscriberLimitPushDayReached, ExceptionCode.SubscriberLimitPushDayReached.getValue(), ExceptionCategory.SUBSCRIBER);

					// plafond Hebdo
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPushAmountWeek(), "W"))) throw new MoMoException(ExceptionCode.SubscriberLimitPushWeekReached, ExceptionCode.SubscriberLimitPushWeekReached.getValue(), ExceptionCategory.SUBSCRIBER);

					// plafond Mensuel
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPushAmountMonth(), "M"))) throw new MoMoException(ExceptionCode.SubscriberLimitPushMonthReached, ExceptionCode.SubscriberLimitPushMonthReached.getValue(), ExceptionCategory.SUBSCRIBER);

				}
			}else{
				if(message.getAmount() > params.getMaxPushAmount()) throw new MoMoException(ExceptionCode.SubscriberInvalidAmount, ExceptionCode.SubscriberInvalidAmount.getValue(), ExceptionCategory.SUBSCRIBER);

				// plafond Journalier
				if(Boolean.FALSE.equals(subs.islimit(message.getOperation(),message.getAmount(),params.getMaxPushAmountDay()))) throw new MoMoException(ExceptionCode.SubscriberLimitPushDayReached, ExceptionCode.SubscriberLimitPushDayReached.getValue(), ExceptionCategory.SUBSCRIBER);

				// plafond Hebdo
				if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPushAmountWeek(), "W"))) throw new MoMoException(ExceptionCode.SubscriberLimitPushWeekReached, ExceptionCode.SubscriberLimitPushWeekReached.getValue(), ExceptionCategory.SUBSCRIBER);

				// plafond Mensuel
				if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPushAmountMonth(), "M"))) throw new MoMoException(ExceptionCode.SubscriberLimitPushMonthReached, ExceptionCode.SubscriberLimitPushMonthReached.getValue(), ExceptionCategory.SUBSCRIBER);

			}

		}
		//logger.info("[MoMo] : TEST PARAM POUR RETRAITEMENT");
		// Si le mode nuit est active et l'etat des tfjo active
		if(isTFJOPortalEnCours() && isModeNuit()) {
			//logger.info("[MoMo] : PARAM RETRAITEMENT OK!");
			// Desactivation du verrou des tfjo 
			setTFJOPortalEnCours(Boolean.FALSE);

			// Retraiter les transactions executes entre le lancement des TFJO Portal et l'activation du mode nuit Amplitude
			//RetraiterTransactionWorker.runChecking();
			processRetraiterTransactions();
		}

		String trxID = "";
		// Initialisation de la transaction
		Transaction trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), trxID);

		// Log
		//logger.info("NEW TRANSACTION ");

		// Sauvegarde du contexte de la transaction
		trx.setTfjoLance(isTFJOPortalEnCours());
		trx.setModeNuitAmplitudeActive(isModeNuit());

		// Marquer la transaction a retraiter si les TFJO Portal sont en cours et le mode nuit d'Amplitude non active
		if(isTFJOPortalEnCours() && !isModeNuit()) trx.setARetraiter(Boolean.TRUE);

		// Log
		//logger.info("Tfjo Lance : "+trx.getTfjoLance());
		// Log
		//logger.info("Mode Nuit Amplitude Active : "+trx.getModeNuitAmplitudeActive());
		// Log
		//logger.info("A Retraiter : "+trx.getARetraiter());

		// Tout est OK
		return trx;
	}


	private Transaction getTransactionFromRequestMessageECW(RequestMessage message) throws Exception, MoMoException {
		// checkGlobalConfig(); //
		params = findParameters();
		
		String msg = "";
		String subject = "";
		String title = "PLAFOND";
		
		
		
		// Verification de l'existence d'une trx avec le meme mtn trx id MAJ
		logger.info("Verification de l'existence d'une trx avec le meme mtn trx id : "+message.getTrxId());
		Transaction tx = getMTNTrxID(message.getTrxId());
		if((message.getOperation().equals(TypeOperation.PULL) || message.getOperation().equals(TypeOperation.PUSH)) && tx!=null){
			
			logger.info("TRX FOUND ");
			return tx;
			
		}
		
		logger.info("TRX NOT FOUND ");
		
		// Test du montant
		if( (message.getOperation().equals(TypeOperation.PULL) || message.getOperation().equals(TypeOperation.PUSH)) &&  ( message.getAmount() == null || message.getAmount() <= 0 ) ) throw new MoMoException(ExceptionCode.SubscriberInvalidAmount, ExceptionCode.SubscriberInvalidAmount.getValue(), ExceptionCategory.SUBSCRIBER);
		
		if(!StringUtils.isNotBlank(params.getTokenCbsApi())) {
			throw new MoMoException(ExceptionCode.BankAPICbsException, ExceptionCode.BankAPICbsException.getValue(), ExceptionCategory.NETWORK);
		}
		Subscriber subs = findSubscriberFromPhoneNumber(message.getPhoneNumber());
		
		// test de l'abonne
		if( subs == null ) {

			// Message d'avertissement
			throw new MoMoException(ExceptionCode.SubscriberInvalidPhone, ExceptionCode.SubscriberInvalidPhone.getValue(), ExceptionCategory.SUBSCRIBER);

		}

		// test du PIN
		/*if( !subs.getBankPIN().equals( Encrypter.getInstance().encryptText( message.getBankPIN() ) ) ) {

			// Message d'avertissement
			throw new MoMoException(ExceptionCode.SubscriberInvalidPIN, ExceptionCode.SubscriberInvalidPIN.getValue(), ExceptionCategory.SUBSCRIBER);

		}*/

		// Test du numero de telephone
		if( subs == null ) throw new MoMoException(ExceptionCode.SubscriberInvalidPhone, ExceptionCode.SubscriberInvalidPhone.getValue(), ExceptionCategory.SUBSCRIBER);


		// Test du numero de telephone
		if( subs.getStatus().equals(StatutContrat.WAITING) ) {

			// Message d'avertissement
			throw new MoMoException(ExceptionCode.SubscriberSuspended, ExceptionCode.SubscriberSuspended.getValue(), ExceptionCategory.SUBSCRIBER);
		}
		
		// Test du numero de telephone
		if( subs.getStatus().equals(StatutContrat.SUSPENDU) ) {
			msg = "Le propriétaire du n° "+message.getPhoneNumber()+" essaie de faire une opération. Cet abonnenement est suspendu. ";
			subject = "Alerte abonnement suspendu";
			sendSimpleMail(msg, subject, title);
			// Message d'avertissement
			throw new MoMoException(ExceptionCode.SubscriberSuspended, ExceptionCode.SubscriberSuspended.getValue(), ExceptionCategory.SUBSCRIBER);
		}

		// Lecture du compte
		if(message.getAccount() == null || message.getAccount().isEmpty()) message.setAccount( subs.getAccounts().get(0) );

		// Test de l'existence du compte 
		if( isCompteFerme(message.getAccount()) ) {

			// Message d'avertissement
			throw new MoMoException(ExceptionCode.BankClosingAccount, ExceptionCode.BankClosingAccount.getValue(), ExceptionCategory.METIER);

		}


		//logger.info("******************subs.isMerchant()*********************"+subs.isMerchant());

		// Si c'est un retrait (verification du solde)	
		if(message.getOperation().equals(TypeOperation.PULL)) {

			// Test du solde du compte 
			if( !_isSoldeSuffisant(message.getAccount(), message.getAmount())) {

				// Message d'avertissement
				throw new MoMoException(ExceptionCode.BankInsufficientBalance, ExceptionCode.BankInsufficientBalance.getValue(), ExceptionCategory.METIER);

			}

			//  Test si le compte est en opposition
			if(isCompteEnOpposition(message.getAccount().split("-")[0], message.getAccount().split("-")[1], params.getCodeOperation()) ){

				// Message d'avertissement
				throw new MoMoException(ExceptionCode.BankBlockingDebitAccount, ExceptionCode.BankBlockingDebitAccount.getValue(), ExceptionCategory.METIER);

			}

			// Si le montant de la transaction est superieur au montant max on leve une exception
			// Authorisation d'une Transaction en Fonction du Profil 
			// plg.getMaxPullAmount() > 0 && 
			if(Boolean.TRUE.equals(subs.isMerchant())){
				PlageTransactions plg = subs.getProfil();
				//logger.info("*******PlageTransactions***********"+plg);
				
				if(plg != null){
					if(message.getAmount() > plg.getMaxPullAmount()){
						msg = "Le marchand propriétaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PULL "
								+ "d'un montant de "+message.getAmount()+", supérieur à sa limite.";
						subject = "Marchand - Alerte dépassement plafond transaction";
						sendSimpleMail(msg, subject, title);
						throw new MoMoException(ExceptionCode.SubscriberAmountExceedMaxAllowed, ExceptionCode.SubscriberAmountExceedMaxAllowed.getValue(), ExceptionCategory.SUBSCRIBER);
					} 
 
					// plafond Journalier
					if(Boolean.FALSE.equals(subs.islimit(message.getOperation(),message.getAmount(),plg.getMaxPullAmountDay()))) {
						msg = "Le marchand proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PULL "
								+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite journalière.";
						subject = "Marchand - Alerte dépassement plafond journalier";
						sendSimpleMail(msg, subject, title);
						throw new MoMoException(ExceptionCode.SubscriberLimitPullDayReached, ExceptionCode.SubscriberLimitPullDayReached.getValue(), ExceptionCategory.SUBSCRIBER);
					}

					// plafond Hebdo
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),plg.getMaxPullAmountWeek(), "W"))) {
						msg = "Le marchand proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PULL "
								+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite hebdomadaire.";
						subject = "Marchand - Alerte dépassement plafond hebdomadaire";
						sendSimpleMail(msg, subject, title);
						throw new MoMoException(ExceptionCode.SubscriberLimitPullWeekReached, ExceptionCode.SubscriberLimitPullWeekReached.getValue(), ExceptionCategory.SUBSCRIBER);
					}

					// plafond Mensuel
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),plg.getMaxPullAmountMonth(), "M"))) {
						msg = "Le marchand proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PULL "
								+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite mensuelle.";
						subject = "Marchand - Alerte dépassement plafond mensuel";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberLimitPullMonthReached, ExceptionCode.SubscriberLimitPullMonthReached.getValue(), ExceptionCategory.SUBSCRIBER);
					}

				}else{
					if(message.getAmount() > params.getMaxPullAmount()) {
						msg = "Le marchand propriétaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PULL "
								+ "d'un montant de "+message.getAmount()+", supérieur à sa limite.";
						subject = "Marchand sans plage - Alerte dépassement plafond transaction";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberAmountExceedMaxAllowed, ExceptionCode.SubscriberAmountExceedMaxAllowed.getValue(), ExceptionCategory.SUBSCRIBER);
					}

					// plafond Journalier
					if(Boolean.FALSE.equals(subs.islimit(message.getOperation(),message.getAmount(),params.getMaxPullAmountDay()))) {
						msg = "Le marchand proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PULL "
								+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite journalière.";
						subject = "Marchand sans plage - Alerte dépassement plafond journalier";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberLimitPullDayReached, ExceptionCode.SubscriberLimitPullDayReached.getValue(), ExceptionCategory.SUBSCRIBER);
					}

					// plafond Hebdo
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPullAmountWeek(), "W"))) {
						msg = "Le marchand proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PULL "
								+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite hebdomadaire.";
						subject = "Marchand sans plage - Alerte dépassement plafond hebdomadaire";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberLimitPullWeekReached, ExceptionCode.SubscriberLimitPullWeekReached.getValue(), ExceptionCategory.SUBSCRIBER);
					}

					// plafond Mensuel
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPullAmountMonth(), "M"))) {
						msg = "Le marchand proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PULL "
								+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite mensuelle.";
						subject = "Marchand sans plage - Alerte dépassement plafond mensuel";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberLimitPullMonthReached, ExceptionCode.SubscriberLimitPullMonthReached.getValue(), ExceptionCategory.SUBSCRIBER);
					}

				}
			}else{
				
				
				
				if( message.getAmount() > params.getMaxPullAmount()) {
					msg = "Le proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PULL "
							+ "d'un montant de "+message.getAmount()+", supérieur à sa limite.";
					subject = "Alerte dépassement plafond transaction";
					sendSimpleMail(msg, subject,  title);
					throw new MoMoException(ExceptionCode.SubscriberAmountExceedMaxAllowed, ExceptionCode.SubscriberAmountExceedMaxAllowed.getValue(), ExceptionCategory.SUBSCRIBER);
				}

				// plafond Journalier
				if(Boolean.FALSE.equals(subs.islimit(message.getOperation(),message.getAmount(),params.getMaxPullAmountDay()))) {
					msg = "Le proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PULL "
							+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite journalière.";
					subject = "Alerte dépassement plafond journalier";
					sendSimpleMail(msg, subject,  title);
					throw new MoMoException(ExceptionCode.SubscriberLimitPullDayReached, ExceptionCode.SubscriberLimitPullDayReached.getValue(), ExceptionCategory.SUBSCRIBER);
				}

				// plafond Hebdo
				if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPullAmountWeek(), "W"))) {
					msg = "Le proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PULL "
							+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite hebdomadaire.";
					subject = "Alerte dépassement plafond hebdomadaire";
					sendSimpleMail(msg, subject,  title);
					throw new MoMoException(ExceptionCode.SubscriberLimitPullWeekReached, ExceptionCode.SubscriberLimitPullWeekReached.getValue(), ExceptionCategory.SUBSCRIBER);
				}

				// plafond Mensuel
				if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPullAmountMonth(), "M"))) {
					msg = "Le proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PULL "
							+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite mensuelle.";
					subject = "Alerte dépassement plafond mensuel";
					sendSimpleMail(msg, subject,  title);
					throw new MoMoException(ExceptionCode.SubscriberLimitPullMonthReached, ExceptionCode.SubscriberLimitPullMonthReached.getValue(), ExceptionCategory.SUBSCRIBER);
				}

			}

		} else if(message.getOperation().equals(TypeOperation.PUSH)) {

			//  Test si le compte est en opposition
			if(isCompteEnOppositionCredit(message.getAccount().split("-")[0], message.getAccount().split("-")[1]) ){

				// Message d'avertissement
				throw new MoMoException(ExceptionCode.BankBlockingCreditAccount, ExceptionCode.BankBlockingCreditAccount.getValue(), ExceptionCategory.METIER);

			}
			
			// Si le montant de la transaction est superieur au montant max on leve une exception
			// plg.getMaxPushAmount() > 0 && params.getMaxPushAmount() > 0 &&
			if(Boolean.TRUE.equals(subs.isMerchant())){
				PlageTransactions plg = subs.getProfil();
				//logger.info("*******PlageTransactions***********"+plg);
				if(plg != null){
					if( message.getAmount() > plg.getMaxPushAmount()) {
						msg = "Le marchand propriétaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PUSH "
								+ "d'un montant de "+message.getAmount()+", supérieur à sa limite.";
						subject = "Marchand - Alerte dépassement plafond transaction";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberAmountExceedMaxAllowed, ExceptionCode.SubscriberAmountExceedMaxAllowed.getValue(), ExceptionCategory.SUBSCRIBER);
					}

					// plafond Journalier
					if(Boolean.FALSE.equals(subs.islimit(message.getOperation(),message.getAmount(),plg.getMaxPushAmountDay()))) {
						msg = "Le marchand proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PUSH "
								+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite journalière.";
						subject = "Marchand - Alerte dépassement plafond journalier";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberLimitPushDayReached, ExceptionCode.SubscriberLimitPushDayReached.getValue(), ExceptionCategory.SUBSCRIBER);
					}

					// plafond Hebdo
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),plg.getMaxPushAmountWeek(), "W"))) {
						msg = "Le marchand proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PUSH "
								+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite hebdomadaire.";
						subject = "Marchand - Alerte dépassement plafond hebdomadaire";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberLimitPushWeekReached, ExceptionCode.SubscriberLimitPushWeekReached.getValue(), ExceptionCategory.SUBSCRIBER);
					}

					// plafond Mensuel
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),plg.getMaxPushAmountMonth(), "M"))) {
						msg = "Le marchand proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PUSH "
								+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite mensuelle.";
						subject = "Marchand - Alerte dépassement plafond mensuel";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberLimitPushMonthReached, ExceptionCode.SubscriberLimitPushMonthReached.getValue(), ExceptionCategory.SUBSCRIBER);
					}

				}else{
					if( message.getAmount() > params.getMaxPushAmount()) {
						msg = "Le marchand propriétaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PUSH "
								+ "d'un montant de "+message.getAmount()+", supérieur à sa limite.";
						subject = "Marchand sans plage - Alerte dépassement plafond transaction";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberAmountExceedMaxAllowed, ExceptionCode.SubscriberAmountExceedMaxAllowed.getValue(), ExceptionCategory.SUBSCRIBER);
					}

					// plafond Journalier
					if(Boolean.FALSE.equals(subs.islimit(message.getOperation(),message.getAmount(),params.getMaxPushAmountDay()))) {
						msg = "Le marchand proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PUSH "
								+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite journalière.";
						subject = "Marchand sans plage - Alerte dépassement plafond journalier";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberLimitPushDayReached, ExceptionCode.SubscriberLimitPushDayReached.getValue(), ExceptionCategory.SUBSCRIBER);
					}

					// plafond Hebdo
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPushAmountWeek(), "W"))) {
						msg = "Le marchand proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PUSH "
								+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite hebdomadaire.";
						subject = "Marchand sans plage - Alerte dépassement plafond hebdomadaire";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberLimitPushWeekReached, ExceptionCode.SubscriberLimitPushWeekReached.getValue(), ExceptionCategory.SUBSCRIBER);
					}

					// plafond Mensuel
					if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPushAmountMonth(), "M"))) {
						msg = "Le marchand proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PUSH "
								+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite mensuelle.";
						subject = "Marchand sans plage - Alerte dépassement plafond mensuel";
						sendSimpleMail(msg, subject,  title);
						throw new MoMoException(ExceptionCode.SubscriberLimitPushMonthReached, ExceptionCode.SubscriberLimitPushMonthReached.getValue(), ExceptionCategory.SUBSCRIBER);
					}

				}
			}else{
				if(message.getAmount() > params.getMaxPushAmount()) {
					msg = "Le proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PUSH "
							+ "d'un montant de "+message.getAmount()+", supérieur à sa limite.";
					subject = "Alerte dépassement plafond transaction";
					sendSimpleMail(msg, subject,  title);
					throw new MoMoException(ExceptionCode.SubscriberAmountExceedMaxAllowed, ExceptionCode.SubscriberAmountExceedMaxAllowed.getValue(), ExceptionCategory.SUBSCRIBER);
				}

				// plafond Journalier
				if(Boolean.FALSE.equals(subs.islimit(message.getOperation(),message.getAmount(),params.getMaxPushAmountDay()))) {
					msg = "Le proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PUSH "
							+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite journalière.";
					subject = "Alerte dépassement plafond journalier";
					sendSimpleMail(msg, subject,  title);
					throw new MoMoException(ExceptionCode.SubscriberLimitPushDayReached, ExceptionCode.SubscriberLimitPushDayReached.getValue(), ExceptionCategory.SUBSCRIBER);
				}

				// plafond Hebdo
				if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPushAmountWeek(), "W"))) {
					msg = "Le proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PUSH "
							+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite hebdomadaire.";
					subject = "Alerte dépassement plafond hebdomadaire";
					sendSimpleMail(msg, subject,  title);
					throw new MoMoException(ExceptionCode.SubscriberLimitPushWeekReached, ExceptionCode.SubscriberLimitPushWeekReached.getValue(), ExceptionCategory.SUBSCRIBER);
				}

				// plafond Mensuel
				if(Boolean.FALSE.equals(subs.islimitWeekHebd(message.getOperation(),message.getAmount(),params.getMaxPushAmountMonth(), "M"))) {
					msg = "Le proprietaire du n° "+message.getPhoneNumber()+" essaie de faire une opération de PUSH "
							+ "d'un montant de "+message.getAmount()+", entrainant ainsi le dépassement de sa limite mensuelle.";
					subject = "Alerte dépassement plafond mensuel";
					sendSimpleMail(msg, subject,  title);
					throw new MoMoException(ExceptionCode.SubscriberLimitPushMonthReached, ExceptionCode.SubscriberLimitPushMonthReached.getValue(), ExceptionCategory.SUBSCRIBER);
				}

			}

		}
		//logger.info("[MoMo] : TEST PARAM POUR RETRAITEMENT");
		// Si le mode nuit est active et l'etat des tfjo active
		if(isTFJOPortalEnCours() && isModeNuit()) {
			//logger.info("[MoMo] : PARAM RETRAITEMENT OK!");
			logger.error("MODE NUIT CBS ACTIVE");
			// Desactivation du verrou des tfjo 
			setTFJOPortalEnCours(Boolean.FALSE);
			logger.error("DESACTIVATION VERROU TFJO PORTAL");
			// Retraiter les transactions executes entre le lancement des TFJO Portal et l'activation du mode nuit Amplitude
			//RetraiterTransactionWorker.runChecking();
			processRetraiterTransactions();
		}

		// Initialisation de la transaction
		Transaction trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), message.getTrxId());

		// Log
		//logger.info("NEW TRANSACTION ");

		// Sauvegarde du contexte de la transaction
		trx.setTfjoLance(isTFJOPortalEnCours());
		trx.setModeNuitAmplitudeActive(isModeNuit());

		// Marquer la transaction a retraiter si les TFJO Portal sont en cours et le mode nuit d'Amplitude non active
		if(isTFJOPortalEnCours() && !isModeNuit()) {
			trx.setARetraiter(Boolean.TRUE);
			logger.error("TRANSACTION A RETRAITER [TFJOPortal="+trx.getTfjoLance()+" - ModeNuitCBS="+trx.getModeNuitAmplitudeActive()+" - Retraiter="+trx.getARetraiter()+"]");
		}

		// Log
		//logger.info("Tfjo Lance : "+trx.getTfjoLance());
		// Log
		//logger.info("Mode Nuit Amplitude Active : "+trx.getModeNuitAmplitudeActive());
		// Log
		//logger.info("A Retraiter : "+trx.getARetraiter());

		// Tout est OK
		return trx;
	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#processPullPushMessage(com.afb.dpd.mobilemoney.jpa.entities.RequestMessage)
	 */
	@Override
	public synchronized Transaction processPullPushMessage(RequestMessage message) throws Exception, MoMoException {

		// Traite le message de Pull/Pull from Account
		return posterEvenementDansCoreBanking( getTransactionFromRequestMessage(message) );

	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#posterEvenementDansCoreBanking(com.afb.dpd.mobilemoney.jpa.tools.bkeve)
	 */
	@Override
	public synchronized Transaction posterEvenementDansCoreBanking(Transaction transaction) throws Exception, ConstraintViolationException,EntityExistsException  {
		
		List<Queries> query = new ArrayList();
		logger.info("IN posterEvenementDansCoreBanking");
		if(transaction == null) return null;

		// Transaction avec un mtn trx id existant trouvee : retour
		if(transaction.getId() != null) return transaction;

		// Initialisation de DataStore d'Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();
		
		// Contruction de l'evenement liee a la transaction
		bkeve eve = buildEvenement(transaction);
		params = findParameters();
	    if(params.getCbsServices()) {	
		 if(eve != null) {
			logger.info("EVE NON NULL");
			
			//Envoi de l'evenement vers l'API
			bkeve _eve = registerEventToCoreBanking(eve);
			
			if(_eve != null) {
				logger.info("[MoMo - Register Event Response] : "+_eve.getEve()+" ID: "+_eve.getE_id());
				
				try {
					eve.setEve( MoMoHelper.padText(String.valueOf(_eve.getEve()), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0") );
					eve.setE_id(_eve.getE_id());
					eve.setId(Long.parseLong(eve.getE_id()));
					eve.getTransaction().setStatus(TransactionStatus.SUCCESS);
					eve = mobileMoneyDAO.save(eve);
					
				}catch (Exception e){
					logger.info("[MoMo CONTROLE RESERVATION ERROR] : SAVE TRX EXCEPTION --> CANCEL EVE & MAJ SOLDE");
					// MAJ de l'etat des evenements dans le Core Banking (annulation)
					// Annulation de la maj du solde indicatif
					logger.info("[MoMo - Register] : "+eve.getEve());
					bkeve ev = reversalEventsCoreBanking(""+eve.getId());
					e.printStackTrace();
				}
				
			}
			else {
				return transaction;
			}
			
		}
		else{
			// Transaction sans evenement (BALANCE/MINISTATEMENT)
			transaction.setStatus(TransactionStatus.SUCCESS);
		}
	}else {
		logger.info("CBS SERVICES NOT ENABLED");
		if(eve != null) {
			logger.info("EVE NON NULL");
			
		
			eve.setId(now());
			
			boolean evePosted = false;
			// Calcul de la table des evenements a utiliser
			//String tableEvt = eve.getSuspendInTFJ().booleanValue() ? " SYN_BKEVE " : " BKEVE ";
			String tableEvt = eve.getSuspendInTFJ().booleanValue() ? " BKEVE_EOD " : " BKEVE ";
		
			// Try catch global
			try{
				// requete d'enregistrement de l'evenement dans Amplitude
				query.add(new Queries(eve.getSaveQuery().replaceAll(" BKEVE ", tableEvt), eve.getQueryValues()));
				// MAJ du dernier numero d'evenement utilise pour le type operation
				query.add(new Queries(eve.getSuspendInTFJ().booleanValue() ? MoMoHelper.getDefaultCBSQueries().get(3).getQuery().replaceAll("bkope", "syn_bkope") : MoMoHelper.getDefaultCBSQueries().get(3).getQuery(), new Object[]{ Long.valueOf(eve.getEve()), eve.getOpe() }));
				
		/* commenté le 2020-08-27		
				try{
	
					// Enregistrement de l'evenement dans Amplitude
					executeUpdateSystemQuery(dsCBS, eve.getSaveQuery().replaceAll(" BKEVE ", tableEvt), eve.getQueryValues());
					
					logger.info("[MoMo CONTROLE RESERVATION] : SAVE EVE OK");
				}catch(java.sql.SQLException  ex){
			
	
					logger.info("[MoMo ERROR] : Erreur lors de l'insertion dans la table "+tableEvt+". Le Max eve ne correspond pas dans bkope! On regularise...");
	
					executeUpdateSystemQuery(dsCBS, "update "+ (eve.getSuspendInTFJ().booleanValue() ? "syn_" : "") +"bkope set num=(select max(eve) from "+ tableEvt +" where ope='"+ params.getCodeOperation() +"') where ope='"+ params.getCodeOperation() +"' ", null);
	
					// Recuperation du dernier numero evenement du type operation
					ResultSet rs = executeFilterSystemQuery(dsCBS, eve.getSuspendInTFJ().booleanValue() ? MoMoHelper.getDefaultCBSQueries().get(2).getQuery().replaceAll("bkope", "syn_bkope") : MoMoHelper.getDefaultCBSQueries().get(2).getQuery(), new Object[]{ params.getCodeOperation() });
	
					// Calcul du numero d'evenement
					Long numEve = rs != null && rs.next() ? numEve = rs.getLong("num") + 1 : 1l;
	
					// Fermeture de cnx
					rs.close(); rs.getStatement().close();
	
					eve.setEve( MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0") );
					for(bkmvti m : eve.getEcritures()) m.setEve(eve.getEve());
	
					// Enregistrement de l'evenement dans Amplitude
					executeUpdateSystemQuery(dsCBS, eve.getSaveQuery().replaceAll("BKEVE", tableEvt), eve.getQueryValues());
					logger.info("[MoMo CONTROLE RESERVATION] : SAVE EVE (2) OK");
//					try{
//						executeUpdateSystemQuery(dsCBS, eve.getSaveQuery().replaceAll("BKEVE", tableEvt), eve.getQueryValues());
//					}catch (Exception e){
//						// MAJ de l'etat des evenements dans le Core Banking (annulation)
//						executeUpdateSystemQuery(dsCBS, "update "+ tableEvt +" set eta='IG', etap='VA' where eve = "+ eve.getEve() +" and ope= "+eve.getOpe()+" ", null);
//						e.printStackTrace();
//					}
	
					logger.info("[MoMo ERROR] : Regularisation OK. On poursuit");
	
				}
	
				// MAJ du dernier numero d'evenement utilise pour le type operation
				executeUpdateSystemQuery(dsCBS, eve.getSuspendInTFJ().booleanValue() ? MoMoHelper.getDefaultCBSQueries().get(3).getQuery().replaceAll("bkope", "syn_bkope") : MoMoHelper.getDefaultCBSQueries().get(3).getQuery(), new Object[]{ Long.valueOf(eve.getEve()), eve.getOpe() });
	*/
				// Si on est dans le mode Nuit
				if(eve.getSuspendInTFJ().booleanValue()){
	
					// MAJ du solde indicatif du Compte Debiteur
			//		executeUpdateSystemQuery(dsCBS, "insert into bksin (age, dev, ncp, suf, mon, orig, flag) values (?, ?, ?, ?, ?, ?, ?) ", new Object[]{ eve.getAge1(), eve.getDev1(), eve.getNcp1(), eve.getSuf1(), -eve.getMon1(), "WEB", "O" });
					query.add(new Queries("insert into bksin (age, dev, ncp, suf, mon, orig, flag) values (?, ?, ?, ?, ?, ?, ?) ", new Object[]{ eve.getAge1(), eve.getDev1(), eve.getNcp1(), eve.getSuf1(), -eve.getMon1(), "WEB", "O" }));
					
					// MAJ du solde indicatif du compte Crediteur
			//		executeUpdateSystemQuery(dsCBS, "insert into bksin (age, dev, ncp, suf, mon, orig, flag) values (?, ?, ?, ?, ?, ?, ?) ", new Object[]{ eve.getAge2(), eve.getDev2(), eve.getNcp2(), eve.getSuf2(), eve.getMon2(), "WEB", "O" }); //executeUpdateSystemQuery(dsCBS, "update bksin set mon = mon + ? where age= ? and ncp = ?", new Object[]{ eve.getMon2(), eve.getAge2(), eve.getNcp2() });
					query.add(new Queries("insert into bksin (age, dev, ncp, suf, mon, orig, flag) values (?, ?, ?, ?, ?, ?, ?) ", new Object[]{ eve.getAge2(), eve.getDev2(), eve.getNcp2(), eve.getSuf2(), eve.getMon2(), "WEB", "O" }));
					// Si on est dans le mode Jour
				} else {
	
					// MAJ du solde indicatif du compte debiteur
				//	executeUpdateSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(4).getQuery(), new Object[]{ (transaction.getTypeOperation().equals(TypeOperation.PUSH) ? transaction.getAmount() : eve.getMnt1()) , eve.getAge1(), eve.getNcp1(), eve.getClc1() } );
					query.add(new Queries(MoMoHelper.getDefaultCBSQueries().get(4).getQuery(), new Object[]{ (transaction.getTypeOperation().equals(TypeOperation.PUSH) ? transaction.getAmount() : eve.getMnt1()) , eve.getAge1(), eve.getNcp1(), eve.getClc1() }));
					// MAJ du solde indicatif crediteur
					if(transaction.getTypeOperation().equals(TypeOperation.PUSH) || transaction.getTypeOperation().equals(TypeOperation.PULL)) 
					//	executeUpdateSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(5).getQuery(), new Object[]{transaction.getAmount(), eve.getAge2(), eve.getNcp2(), eve.getClc2()  });
					    query.add(new Queries(MoMoHelper.getDefaultCBSQueries().get(5).getQuery(), new Object[]{transaction.getAmount(), eve.getAge2(), eve.getNcp2(), eve.getClc2()  }));
				}
				
				executeUpdateSystemQuery(dsCBS, query);
				logger.info("[MoMo CONTROLE RESERVATION] : MAJ SOLDE OK");
			}catch (Exception e){
				// MAJ de l'etat des evenements dans le Core Banking (annulation)
				query = new ArrayList();
				//annulerEvenement(dsCBS,majEve.replaceAll("BKEVE", tableEvt));
		//		executeUpdateSystemQuery(dsCBS, "update "+ tableEvt +" set eta='IG', etap='VA' where eve = "+ eve.getEve() +" and ope= "+eve.getOpe()+" ", null);
				//query.add(new Queries("update "+ tableEvt +" set eta='IG', etap='VA' where eve = "+ eve.getEve() +" and ope= "+eve.getOpe()+" ", null));
				// Annulation de la maj du solde indicatif
		//		annulerMAJSoldeIndicatif(eve);
				//annulerMAJSoldeIndicatif(eve, query);
				logger.info("[MoMo CONTROLE RESERVATION ERROR] : CANCEL EVE & MAJ SOLDE");
				e.printStackTrace();
			}

			// Check Bkeve
			try{
				ResultSet rsa = executeFilterSystemQuery(dsCBS, eve.getCheckQuery().replaceAll("BKEVE", tableEvt), eve.getQueryCheckValues());
				if(rsa.next()){
					evePosted = true;
					logger.info("[MoMo CONTROLE RESERVATION] : CHECK EVE OK");
				}else{
					logger.info("[MoMo CONTROLE RESERVATION ERROR] : CHECK EVE EXCEPTION");
					throw new MoMoException(ExceptionCode.BankException, ExceptionCode.BankException.getValue(), ExceptionCategory.METIER);
				}		
				// CBS_CNX_OPTI
				if(rsa != null) {
					rsa.close(); 
					if(rsa.getStatement() != null) {
						rsa.getStatement().close();
					}
				}
			}catch (Exception e){
				logger.info("[MoMo CONTROLE RESERVATION ERROR] : CHECK EVE EXCEPTION");
				e.printStackTrace();
			//	if(evePosted)annulerEvenement(dsCBS,majEve.replaceAll("BKEVE", tableEvt));
				// MAJ de l'etat des evenements dans le Core Banking (annulation)
			//	annulerEvenement(dsCBS,majEve.replaceAll("BKEVE", tableEvt));
				if(evePosted)executeUpdateSystemQuery(dsCBS, "update "+ tableEvt +" set eta='IG', etap='VA' where eve = "+ eve.getEve() +" and ope= "+eve.getOpe()+" ", null);
				throw new MoMoException(ExceptionCode.BankException, ExceptionCode.BankException.getValue(), ExceptionCategory.METIER);
			}
			
			// MAJ du statut de la transaction
			eve.getTransaction().setStatus(TransactionStatus.SUCCESS);

			// Sauvegarde l'evenement/ecritures/transaction
			try{
				eve = mobileMoneyDAO.save(eve);
			}catch (Exception e){
				logger.info("[MoMo CONTROLE RESERVATION ERROR] : SAVE TRX EXCEPTION --> CANCEL EVE & MAJ SOLDE");
				// MAJ de l'etat des evenements dans le Core Banking (annulation)
			//	executeUpdateSystemQuery(dsCBS, "update "+ tableEvt +" set eta='IG', etap='VA' where eve = "+ eve.getEve() +" and ope= "+eve.getOpe()+" ", null);
				// Annulation de la maj du solde indicatif
				query = new ArrayList();
				query.add(new Queries("update "+ tableEvt +" set eta='IG', etap='VA' where eve = "+ eve.getEve() +" and ope= "+eve.getOpe()+" ", null));
			//	annulerMAJSoldeIndicatif(eve);
				annulerMAJSoldeIndicatif(eve,query);
				e.printStackTrace();
			}
			
		} else{
			// Transaction sans evenement (BALANCE/MINISTATEMENT)
			transaction.setStatus(TransactionStatus.SUCCESS);
		}

	}
		return eve != null ? eve.getTransaction() : transaction;

	}


	@Override
	public List<Transaction> rePosterEvenementDansCoreBanking(List<Transaction> transactions) throws Exception {
		// TODO Auto-generated method stub
		
		List<Queries> query = new ArrayList();
		
		// Initialisation de la liste des transactions a retourner
		List<Transaction> trx = new ArrayList<Transaction>();
		logger.error("[MoMo] : DEBUT RETRAITEMENT DES TRANSACTIONS INCRIMINEES");
		// Si la liste des transactions a retraiter est nulle, interrompre le traitement
		if(transactions.isEmpty()) return null;
		
		// checkGlobalConfig();
		params = findParameters();

		Map<Long,Transaction> mapTrans = new HashMap<Long,Transaction>();
		for(Transaction t : transactions) mapTrans.put(t.getId(),t);
		//logger.info("[MoMo] : Recuperation des evenements lies aux transaction");
		// s
		List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, null, RestrictionsContainer.getInstance().add(Restrictions.in("transaction",transactions)), null, LoaderModeContainer.getInstance().add("transaction", FetchMode.JOIN), 0, -1);
//		List<String> idEve = new ArrayList();
//		for(bkeve e : eves) {
//			idEve.add(""+e.getId());
//		}
		
	//	boolean b = registerIgnoreEventsToCoreBanking(idEve);
		
		for(bkeve eve : eves){
			Transaction tx = mapTrans.get(eve.getTransaction().getId());
			tx.setARetraiter(Boolean.FALSE);
	
			// Sauvegarde l'evenement
			eve = mobileMoneyDAO.save(eve);
			trx.add(tx);
		}
		
		// logger.info("[MoMo] : Parcours des evenements ");
		// 
//		for(bkeve eve : eves){
//
//			if(eve != null) {
//				//logger.info("[MoMo] : TRX = "+eve.getTransaction().toString());
//				Transaction tx = mapTrans.get(eve.getTransaction().getId());
//
//				// Table des evenements a utiliser (mode nuit)
//	//			String tableEvt = " BKEVE_EOD ";
//				
//				String tableEvt = eve.getSuspendInTFJ().booleanValue() ? " BKEVE_EOD " : " BKEVE ";
//				// Interrompre le traitement si le mode nuit est desactive
//		// commenté ce 01-09-2020
//				if(!isModeNuit()) return trx;
//
//		//		try{
//					//Mise a jour du 01-09-2020
//					
//					//Recherche de l'evenement dans core banking
//					logger.info("Recherche de l'evenement dans core banking: "+MoMoHelper.getDefaultCBSQueries().get(12).getQuery()+" EVE: "+eve.getEve()+ "OPE: "+params.getCodeOperation());
//					ResultSet rs_eve = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(12).getQuery(), new Object[]{ eve.getEve(),params.getCodeOperation() });
//					Long _numEve = null;
//					if(rs_eve != null) {
//						while(rs_eve.next()){
//							_numEve = rs_eve.getLong("eve");
//					    }
//					}
//					// Fermeture de cnx
//					rs_eve.close();rs_eve.getStatement().close();
//					logger.info("[Momo -0- EVE] = "+_numEve);
//					if(_numEve == null ){
//						logger.info("[Momo - EVE] = "+_numEve);
//						
//						// MAJ du dernier numero d'evenement utilise pour le type operation
//						query.add(new Queries("update "+ (eve.getSuspendInTFJ().booleanValue() ? "syn_" : "") +"bkope set num=(select max(eve) from "+ tableEvt +" where ope= ? ) where ope= ? ", new Object[]{ params.getCodeOperation(), params.getCodeOperation() }));
//						
//						// Recuperation du dernier numero evenement du type operation (mode nuit)
//						ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(2).getQuery().replaceAll("bkope", "syn_bkope"), new Object[]{ params.getCodeOperation() });
//	
//						// Calcul du numero d'evenement
//						Long numEve = rs != null && rs.next() ? numEve = rs.getLong("num") + 1 : 1l;
//						logger.info("[MoMo] : Prochain num eve = "+numEve);
//						rs.close(); rs.getStatement().close();
//						eve.setEve( MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0") );
//						//logger.info("[MoMo] : eve = "+eve.toString());
//						for(bkmvti m : eve.getEcritures()) m.setEve(eve.getEve());
//						
//						query.add(new Queries(eve.getSaveQuery().replaceAll(" BKEVE ", tableEvt), eve.getQueryValues()));
//						
//		/*  commenté 01-09-2020			
//					
//					//logger.info("[MoMo] : Mise a jour du dernier numero d'evenement");
//					// ???
//					executeUpdateSystemQuery(dsCBS, "update "+ (eve.getSuspendInTFJ().booleanValue() ? "syn_" : "") +"bkope set num=(select max(eve) from "+ tableEvt +" where ope='"+ params.getCodeOperation() +"') where ope='"+ params.getCodeOperation() +"' ", null);
//
//					// Recuperation du dernier numero evenement du type operation (mode nuit)
//					ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(2).getQuery().replaceAll("bkope", "syn_bkope"), new Object[]{ params.getCodeOperation() });
//
//					// Calcul du numero d'evenement
//					Long numEve = rs != null && rs.next() ? numEve = rs.getLong("num") + 1 : 1l;
//					//logger.info("[MoMo] : Prochain num eve = "+numEve);
//
//					// Fermeture de cnx
//					rs.close(); rs.getStatement().close();
//
//					eve.setEve( MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0") );
//					//logger.info("[MoMo] : eve = "+eve.toString());
//					for(bkmvti m : eve.getEcritures()) m.setEve(eve.getEve());
//
//					// Enregistrement de l'evenement dans Amplitude
//					executeUpdateSystemQuery(dsCBS, eve.getSaveQuery().replaceAll(" BKEVE ", tableEvt), eve.getQueryValues());
//					logger.error("ENREGISTREMENT EVE [num="+eve.getEve()+" - montant="+eve.getMnt1()+"]");
//					
//				
//					//logger.info("[MoMo] : Regularisation OK. On poursuit");
//					
//				}catch(java.sql.SQLException ex){
//					logger.error("[MoMo ERROR] : "+ex.getMessage());
//					logger.error("[MoMo ERROR] : Erreur lors de l'insertion dans la table "+tableEvt);
//				}
//*/	
//				// MAJ du dernier numero d'evenement utilise pour le type operation
//				//2020-09-01
//	//			executeUpdateSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(3).getQuery().replaceAll("bkope", "syn_bkope"), new Object[]{ Long.valueOf(eve.getEve()), eve.getOpe() });
//				query.add(new Queries(MoMoHelper.getDefaultCBSQueries().get(3).getQuery().replaceAll("bkope", "syn_bkope"), new Object[]{ Long.valueOf(eve.getEve()), eve.getOpe() }));
//				
//				// Si le mode Nuit est active
//				if(isModeNuit()){
//					logger.error("MAJ DU SOLDE INDICATIF ");
//					// MAJ du solde indicatif du Compte Debiteur
//				//	executeUpdateSystemQuery(dsCBS, "insert into bksin (age, dev, ncp, suf, mon, orig, flag) values (?, ?, ?, ?, ?, ?, ?) ", new Object[]{ eve.getAge1(), eve.getDev1(), eve.getNcp1(), eve.getSuf1(), -eve.getMon1(), "WEB", "O" });
//					query.add(new Queries("insert into bksin (age, dev, ncp, suf, mon, orig, flag) values (?, ?, ?, ?, ?, ?, ?) ", new Object[]{ eve.getAge1(), eve.getDev1(), eve.getNcp1(), eve.getSuf1(), -eve.getMon1(), "WEB", "O" }));
//					// MAJ du solde indicatif du compte Crediteur
//				//	executeUpdateSystemQuery(dsCBS, "insert into bksin (age, dev, ncp, suf, mon, orig, flag) values (?, ?, ?, ?, ?, ?, ?) ", new Object[]{ eve.getAge2(), eve.getDev2(), eve.getNcp2(), eve.getSuf2(), eve.getMon2(), "WEB", "O" }); //executeUpdateSystemQuery(dsCBS, "update bksin set mon = mon + ? where age= ? and ncp = ?", new Object[]{ eve.getMon2(), eve.getAge2(), eve.getNcp2() });
//					query.add(new Queries("insert into bksin (age, dev, ncp, suf, mon, orig, flag) values (?, ?, ?, ?, ?, ?, ?) ", new Object[]{ eve.getAge2(), eve.getDev2(), eve.getNcp2(), eve.getSuf2(), eve.getMon2(), "WEB", "O" }));
//				} 
//				// Si on est dans le mode Jour
//				else {
//					// Interrompre le traitement
//				//	return trx; 2020-09-01
//					
//					// MAJ du solde indicatif du Compte Debiteur
//					query.add(new Queries(MoMoHelper.getDefaultCBSQueries().get(4).getQuery(), new Object[]{ (tx.getTypeOperation().equals(TypeOperation.PUSH) ? tx.getAmount() : eve.getMnt1()) , eve.getAge1(), eve.getNcp1(), eve.getClc1() }));
//					// MAJ du solde indicatif crediteur
//					if(tx.getTypeOperation().equals(TypeOperation.PUSH) || tx.getTypeOperation().equals(TypeOperation.PULL)) 
//					    query.add(new Queries(MoMoHelper.getDefaultCBSQueries().get(5).getQuery(), new Object[]{tx.getAmount(), eve.getAge2(), eve.getNcp2(), eve.getClc2()  }));
//				}
//				
//				executeUpdateSystemQuery(dsCBS, query);
//		      }
//				// MAJ du statut de traitement de la transaction
//				//				eve.getTransaction().setARetraiter(Boolean.FALSE);
//				tx.setARetraiter(Boolean.FALSE);
//				logger.error("MAJ STATUT A RETRAITER [retraiter="+tx.getARetraiter()+"]");
//				// Sauvegarde l'evenement
//				eve = mobileMoneyDAO.save(eve);
//
//				// Ajout de la transaction dans la liste a retourner
//				//				trx.add(eve.getTransaction());
//				trx.add(tx);
//			}
//
//		}
		logger.error("[MoMo] : FIN RETRAITEMENT DES TRANSACTION INCRIMINEES");	
		return trx;
	}


	/**
	 * Envoi du PIN banque du souscripteur par SMS
	 * @param subs
	 * @throws Exception
	 */
	@Override
	@AllowedRole(name = "sendCodePINBySMS", displayName = "MoMo.Send.CodePIN.By.SMS")
	public void sendCodePINBySMS(Subscriber subs) {

		// Si le souscripteur est null on sort
		if(subs == null) return;

		// Envoi du sms
		sendSMS("Cher client Votre PIN banque pour vos opérations Pull-Push from account : " + subs.getDeCryptedBankPIN(), subs.getPhoneNumbers().get(0));

	}
	

	public void sendSMS(String message, String phoneNumber) {

		try{

			// Demarrage du service Facade du portail
			IFacadeManagerRemote portalFacadeManager = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );

			portalFacadeManager.sendSMSViaLMT( new SMSWeb("MoMo-06", "Mobile Money", "AUTO", message, phoneNumber) );

		}catch(Exception e){e.printStackTrace();}

	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#getECFromTransactions(java.util.List)
	 */
	@Override
	@SuppressWarnings("unchecked")
	@AllowedRole(name = "getECFromTransactions", displayName = "MoMo.Consulter.Ecritures.Comptables")
	public List<bkmvti> getECFromTransactions(List<Transaction> transactions, boolean poster) {

		// S'il n'ya aucune transaction on sort
		if(transactions == null || transactions.isEmpty()) return null;

		// Initialisation du filtre
		//String in = "";

		// Recuperation  de l'id de chaque transaction selectionnee
		//for(Transaction t : transactions) if(t.isSelected() && t.getStatus().equals(TransactionStatus.SUCCESS) && (poster ? !t.getPosted().booleanValue() : true) ) { in += t.getId() + ", "; }

		// Construction du filtre sur les transactions
		//if(!in.isEmpty()) in = "(" + in.substring(0, in.length()-2) + ")";

		// Si aucune transaction n'a ete selectionnee on sort
		//if(in.isEmpty()) return null;

		// Execute et retourne la liste des ecritures comptables des transactions selectionnees
		//return mobileMoneyDAO.getEntityManager().createQuery("Select e.ecritures from bkeve e where e.transaction.id in "+ in +"").getResultList();

		// Initialisation de la liste des ecritures a retourner
		List<bkmvti> mvts = new ArrayList<bkmvti>();

		// Initialisation de la restriction in
		String in = ""; int n = 0;

		for( int i=0; i<transactions.size(); i++ ){
			//logger.info("-------------------------- TEST TRX A POSTER --------------------------");
			if(transactions.get(i).isSelected() && transactions.get(i).getStatus().equals(TransactionStatus.SUCCESS) && (poster ? !transactions.get(i).getPosted().booleanValue() : true) ) { 
				Boolean trouve = true; // l'eve de la transaction existe
				// Maj des ecritures des transactions reconciliees automatiquement par le robot (message != OK)
				if(transactions.get(i).getReconcilier() && !StringUtils.isBlank(transactions.get(i).getMessage()) && (transactions.get(i).getMessage().length()>2)){
					//logger.info("-------------------------- TRX RECONCILIEE --------------------------");
					// Recuperation de l'evenement
					List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("transaction",transactions.get(i))), null, null, 0, 1);
					if(eves!=null && !eves.isEmpty() && eves.size()==1){
						//logger.info("Parcours des ecritures");
						// Parcours des ecritures comptables
						for(bkmvti mvt : eves.get(0).getEcritures()) {
							// (/RA) pour Reconciliation Automatique
							if(!mvt.getLib().contains("/RA")) {
								mvt.setLib(mvt.getLib().concat("/RA") );
								//logger.info("MAJ du libele");
								// Update de l'ecriture
								mobileMoneyDAO.update(mvt);
								//mobileMoneyDAO.getEntityManager().merge(mvt);
							}
						}
						//logger.info("------------------------ FIN TRX RECONCILIEE -------------------------");
					} else trouve = false;
					
				}
				if(trouve){
					in += transactions.get(i).getId() + ", ";
					n++;
				}
			}

			// Si on a deja atteind 1000 transactions selectionnees
			if(n>0 && n%1000==0) {

				// Construction de la liste des criteres
				in = "(".concat(in.substring(0, in.length()-2)).concat(")");

				// MAJ des transaction de
				mvts.addAll(mobileMoneyDAO.getEntityManager().createQuery("Select e.ecritures from bkeve e where e.transaction.id in "+ in +"").getResultList() ) ;

				// Reinitialisation de la variable des criteres
				in = ""; n = 0;
			}
		}

		if(!in.isEmpty() && in.length()>2) {
			in = "(".concat(in.substring(0, in.length()-2)).concat(")");
			mvts.addAll(mobileMoneyDAO.getEntityManager().createQuery("Select e.ecritures from bkeve e where e.transaction.id in "+ in +"").getResultList() ) ;
		}

		return mvts;

	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#extractECFromSelectedTransactionsIntoFile(java.util.List)
	 */
	@Override
	@AllowedRole(name = "extractECFromSelectedTransactionsIntoFile", displayName = "MoMo.Export.Ecritures.Comptable.Into.File")
	public void extractECFromSelectedTransactionsIntoFile(List<Transaction> transactions, String fileName) throws Exception {

		// Recuperation de la liste des ecritures comptables des transactions selectionnees
		List<bkmvti> mvts = getECFromTransactions(transactions, false);

		// S'il n'existe aucune ecriture on sort
		if(mvts == null || mvts.isEmpty()) return;

		// Initialisation du fichier a generer
		FileWriter fw = new FileWriter(fileName);

		// Parcours des ecritures
		for(bkmvti mvt : mvts) {

			// Ecriture de la ligne dans le fichier
			fw.write(mvt.getFileLine().concat("\n"));

		}

		// Fermeture du fichier
		fw.flush(); fw.close();
	}
	
	
	@Override
	@AllowedRole(name = "extractECFromSelectedTransactionsIntoFile", displayName = "MoMo.Export.Ecritures.Comptable.Into.File")
	public void extractECCompensationIntoFile(List<bkmvti> mvts, String fileName) throws Exception {
		
		// S'il n'existe aucune ecriture on sort
		if(mvts == null || mvts.isEmpty()) return;

		// Initialisation du fichier a generer
		FileWriter fw = new FileWriter(fileName);

		// Parcours des ecritures
		for(bkmvti mvt : mvts) {

			// Ecriture de la ligne dans le fichier
			fw.write(mvt.getFileLine().concat("\n"));

		}

		// Fermeture du fichier
		fw.flush(); fw.close();
	}
	
	
	public void extractECFacturationIntoFile(List<Object> mvts, String fileName) throws Exception {
		
		// S'il n'existe aucune ecriture on sort
		if(mvts == null || mvts.isEmpty()) return;

		// Initialisation du fichier a generer
		FileWriter fw = new FileWriter(fileName);

		// Parcours des ecritures
		for(Object obj : mvts) {
			bkmvti mvt = (bkmvti) obj;
			// Ecriture de la ligne dans le fichier
			fw.write(mvt.getFileLine().concat("\n"));

		}

		// Fermeture du fichier
		fw.flush(); fw.close();
	}
	

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public void annulerEvenements(List<Transaction> transactions, DataSystem dsCBS) throws Exception {
//
//		if(transactions == null || transactions.isEmpty()) return;
//
//		// Initialisation de la restriction in
//		String in = ""; int n = 0;
//		List eves = new ArrayList();
//
//		for( int i=0; i<transactions.size(); i++ ){
//			// ???
//			if(transactions.get(i).isSelected() ) { 
//				in += transactions.get(i).getId() + ", ";
//				n++;
//			}
//
//			// Si on a deja atteind 1000 transactions selectionnees
//			if(n>0 && n%1000==0) {
//
//				// Construction de la liste des criteres
//				in = "(".concat(in.substring(0, in.length()-2)).concat(")");
//
//				// Recuperation de la liste des evenements associes aux transactions selectionnees
//				eves.addAll(mobileMoneyDAO.getEntityManager().createQuery("Select e.eve, e.ope From bkeve e where e.transaction.id in "+ in +"").getResultList() ) ;
//
//				// MAJ des transactions
//				mobileMoneyDAO.getEntityManager().createQuery("Update Transaction t set t.posted=:posted, t.aRetraiter=:retraiter where t.id in "+ in +"").setParameter("posted", Boolean.TRUE).setParameter("retraiter", Boolean.FALSE).executeUpdate();
//
//				// Reinitialisation de la variable des criteres
//				in = ""; n = 0;
//			}
//		}
//
//		if(!in.isEmpty() && in.length()>2) {
//			in = "(".concat(in.substring(0, in.length()-2)).concat(")");
//
//			// Recuperation de la liste des evenements associes aux transactions selectionnees
//			eves.addAll(mobileMoneyDAO.getEntityManager().createQuery("Select e.eve, e.ope From bkeve e where e.transaction.id in "+ in +"").getResultList() ) ;
//
//			// MAJ des transactions
//			mobileMoneyDAO.getEntityManager().createQuery("Update Transaction t set t.posted=:posted, t.aRetraiter=:retraiter where t.id in "+ in +"").setParameter("posted", Boolean.TRUE).setParameter("retraiter", Boolean.FALSE).executeUpdate();
//		}
//
//
//
//
//
//		/*
//
//		// Initialisation du filtre des transactions
//		String in = "";
//
//		// Recuperation  de l'id de chaque transaction selectionnee dans le filtre
//		for(Transaction t : transactions) if(t.isSelected()) { in += t.getId() + ", "; }
//
//		// Construction du filtre sur les transactions
//		if(!in.isEmpty()) in = "(" + in.substring(0, in.length()-2) + ")";
//
//		// Si aucune transaction n'a ete selectionnee on sort
//		if(in.isEmpty()) return;
//
//		// Recherche des code evenements et codes operations des transactions selectionnees
//		List eves = mobileMoneyDAO.getEntityManager().createQuery("Select e.eve, e.ope From bkeve e where e.transaction.id in "+ in +"").getResultList();
//
//		 */
//
//		// Si aucun evenement trouve on sort
//		if(eves == null || eves.isEmpty()) return;
//
//		// Initialisation du filtre des evenements et du code operation
//		String inEve = ""; String ope = ""; n = 0;
//
//		for(Object o : eves) {
//			inEve += "'".concat( ((Object[])o)[0].toString()  ).concat("', ");
//			if(ope.isEmpty()) ope = ((Object[])o)[1].toString();
//			n++;
//
//			// Si on a deja atteind 1000 transactions selectionnees
//			if(n>0 && n%1000==0) {
//
//				// Construction de la liste des criteres
//				inEve = "(".concat(inEve.substring(0, inEve.length()-2)).concat(")");
//
//				// MAJ des transaction de
//				executeUpdateSystemQuery(dsCBS, "update bkeve set eta='IG', etap='VA' where eve in "+ inEve +" and ope=? ", new Object[]{ ope  } );
//
//				// Reinitialisation de la variable des criteres
//				inEve = ""; n = 0;
//			}
//		}
//
//		// Construction du filtre des evenements a mettre a jour dans Delta
//		if(!inEve.isEmpty() && inEve.length()>2) { 
//			inEve = "(".concat(inEve.substring(0, inEve.length()-2)).concat(")");
//
//			// MAJ de l'etat des evenements dans le Core Banking
//			executeUpdateSystemQuery(dsCBS, "update bkeve set eta='IG', etap='VA' where eve in "+ inEve +" and ope=? ", new Object[]{ ope  } );
//
//		}
//
//	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void annulerEvenements(List<Transaction> transactions, DataSystem dsCBS) throws Exception {

		if(transactions == null || transactions.isEmpty()) return;

		// Initialisation de la restriction in
		String in = ""; int n = 0;
		List eves = new ArrayList();
		
		for( int i=0; i<transactions.size(); i++ ){
			// Maj de la transaction
			transactions.get(i).setPosted(Boolean.TRUE);
			transactions.get(i).setARetraiter(Boolean.FALSE);
			
			in += transactions.get(i).getId() + ", ";
			n++;

			// Si on a deja atteind 1000 transactions selectionnees
			if(n>0 && n%1000==0) {

				// Construction de la liste des criteres
				in = "(".concat(in.substring(0, in.length()-2)).concat(")");

				// Recuperation de la liste des evenements associes aux transactions selectionnees
				eves.addAll(mobileMoneyDAO.getEntityManager().createQuery("Select e.eve, e.ope From bkeve e where e.transaction.id in "+ in +"").getResultList() ) ;
				
				// Reinitialisation de la variable des criteres
				in = ""; n = 0;
			}
		}

		if(!in.isEmpty() && in.length()>2) {
			in = "(".concat(in.substring(0, in.length()-2)).concat(")");

			// Recuperation de la liste des evenements associes aux transactions selectionnees
			eves.addAll(mobileMoneyDAO.getEntityManager().createQuery("Select e.eve, e.ope From bkeve e where e.transaction.id in "+ in +"").getResultList() ) ;
		}
		
		// MAJ en BD
		mobileMoneyDAO.saveList(transactions, Boolean.TRUE);
		
		// Si aucun evenement trouve on sort
		if(eves == null || eves.isEmpty()) return;

		// Initialisation du filtre des evenements et du code operation
		String inEve = ""; String ope = ""; n = 0;

		for(Object o : eves) {
			inEve += "'".concat( ((Object[])o)[0].toString()  ).concat("', ");
			if(ope.isEmpty()) ope = ((Object[])o)[1].toString();
			n++;

			// Si on a deja atteind 1000 transactions selectionnees
			if(n>0 && n%1000==0) {

				// Construction de la liste des criteres
				inEve = "(".concat(inEve.substring(0, inEve.length()-2)).concat(")");

				// MAJ des transaction de
				executeUpdateSystemQuery(dsCBS, "update bkeve set eta='IG', etap='VA' where eve in "+ inEve +" and ope=? ", new Object[]{ ope  } );

				// Reinitialisation de la variable des criteres
				inEve = ""; n = 0;
			}
		}

		// Construction du filtre des evenements a mettre a jour dans Delta
		if(!inEve.isEmpty() && inEve.length()>2) { 
			inEve = "(".concat(inEve.substring(0, inEve.length()-2)).concat(")");

			// MAJ de l'etat des evenements dans le Core Banking
			executeUpdateSystemQuery(dsCBS, "update bkeve set eta='IG', etap='VA' where eve in "+ inEve +" and ope=? ", new Object[]{ ope  } );

		}

	}
		
	
//	/*
//	 * (non-Javadoc)
//	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#posterTransactionsDansCoreBanking(java.util.List)
//	 */
//	@Override
//	@AllowedRole(name = "posterTransactionsDansCoreBanking", displayName = "MoMo.Poster.Ecritures.Comptable.Into.Delta")
//	public void posterTransactionsDansCoreBanking(List<Transaction> transactions, String user) throws Exception {
//
//		// Recuperation de la liste des ecritures comptables des transactions selectionnees
//		List<bkmvti> mvts = getECFromTransactions(transactions, true);
//
//		// Initialisation de DataStore d'Amplitude
//		if(dsCBS == null) findCBSDataSystem();
//
//		// Recuperation de la date comptable du jour
//		Date dco = getDateComptable(dsCBS);
//
//		// Ouverture d'une cnx vers la BD du Core Banking
//		if(conCBS == null || conCBS.isClosed()) conCBS = getSystemConnection(dsCBS);
//
//		// Suspension temporaire du mode blocage dans la BD du Core Banking
//		if(dsCBS.getDbConnectionString().indexOf("informix") > 0) {
//			conCBS.createStatement().executeUpdate("SET ISOLATION TO DIRTY READ");
//			//conCBS.createStatement().executeUpdate("SET LOCK MODE TO WAIT");
//		}
//
//		// Desactivation du mode AUTO COMMIT
//		conCBS.setAutoCommit(false);
//
//		// Initialisation d'un preparateur de requetes
//		PreparedStatement ps = conCBS.prepareStatement(new bkmvti().getSaveQuery());
//
//
//		// Parcours des ecritures
//		for(bkmvti mvt : mvts) {
//
//			// Ecriture pour chaque transaction de la date comptable du jour
//			mvt.setDco(dco);
//			mvt.setUti(user);
//
//			// Ecriture de la ligne dans le fichier
//			//executeUpdateSystemQuery(dsCBS, mvt.getSaveQuery(), mvt.getQueryValues());
//
//			ps = mvt.addPrepareStatement(ps);
//
//			// Ajout du Lot i
//			ps.addBatch();
//			
//		}
//
//		// Lancement de l'execution du Lot de requetes sur le serveur DELTA
//		ps.executeBatch();
//
//		// Commit
//		conCBS.setAutoCommit(true);
//		ps.close(); ps = null;
//
//		// Annulation des evenements
//		annulerEvenements(transactions, dsCBS);
//
//		// Vidage des variables
//		mvts.clear(); mvts = null; 
//
//	}
	
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#posterTransactionsDansCoreBanking(java.util.List)
	 */
	@Override
	@AllowedRole(name = "posterTransactionsDansCoreBanking", displayName = "MoMo.Poster.Ecritures.Comptable.Into.Delta")
	public void posterTransactionsDansCoreBanking(List<Transaction> transactions, String user) throws Exception {

		// Recuperation de la liste des ecritures comptables des transactions selectionnees
		List<bkmvti> mvts = getECFromTransactions(transactions, true);

		// Initialisation de DataStore d'Amplitude
		if(dsCBS == null) findCBSDataSystem();
		
		// Recuperation de la date comptable du jour
		Date dco = getDateComptable(dsCBS);

		// Ouverture d'une cnx vers la BD du Core Banking
		if(conCBS == null || conCBS.isClosed()) conCBS = getSystemConnection(dsCBS);

		// Suspension temporaire du mode blocage dans la BD du Core Banking
		if(dsCBS.getDbConnectionString().indexOf("informix") > 0) {
			conCBS.createStatement().executeUpdate("SET ISOLATION TO DIRTY READ");
			//conCBS.createStatement().executeUpdate("SET LOCK MODE TO WAIT");
		}

		// Desactivation du mode AUTO COMMIT
		conCBS.setAutoCommit(false);

		// Initialisation d'un preparateur de requetes
		PreparedStatement ps = conCBS.prepareStatement(new bkmvti().getSaveQuery());

		// Subscriber infos map from code client
		Map<String,Subscriber> mapCompte = new HashMap<String, Subscriber>();

		// Parcours des ecritures
		for(bkmvti mvt : mvts) {

			// Ecriture pour chaque transaction de la date comptable du jour
			mvt.setDco(dco);
			mvt.setUti(user);

			// Ecriture de la ligne dans le fichier
			//executeUpdateSystemQuery(dsCBS, mvt.getSaveQuery(), mvt.getQueryValues());

			ps = mvt.addPrepareStatement(ps);

			// Ajout du Lot i
			ps.addBatch();
			
			// ADD
			getRapportDetails(mvt, mapCompte, dsCBS);
			// FIN ADD
			
		}

		// Lancement de l'execution du Lot de requetes sur le serveur DELTA
		ps.executeBatch();

		// Commit
		conCBS.setAutoCommit(true);
		ps.close(); ps = null;

		// Annulation des evenements
		annulerEvenements(transactions, dsCBS);

		// Vidage des variables
		mvts.clear(); mvts = null; 
		
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
	}	


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#archiverTransactions(java.util.List)
	 */
	@Override
	@AllowedRole(name = "archiverTransactions", displayName = "MoMo.Archiver.Transactions")
	public void archiverTransactions(List<Transaction> transactions) throws Exception {
		// TODO Auto-generated method stub

	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#purgerTransactions(java.util.List)
	 */
	@Override
	@AllowedRole(name = "purgerTransactions", displayName = "MoMo.Purger.Transactions")
	public void purgerTransactions(List<Transaction> transactions) throws Exception {
		// TODO Auto-generated method stub

	}


	public Connection getSystemConnection(DataSystem system) throws Exception {
		Class.forName(system.getProviderClassName());
		return DriverManager.getConnection( system.getDbConnectionString(), system.getDbUserName(), system.getDbPassword() );
	}

	
	public ResultSet executeFilterSystemQuery(DataSystem ds, String query, Object[] parameters) throws Exception {

		ResultSet rs = null;

		if(conCBS == null || conCBS.isClosed()) conCBS = getSystemConnection(ds);

		if(conCBS != null){

			if(ds.getDbConnectionString().indexOf("informix") > 0) {
				//conCBS.createStatement().executeUpdate("SET LOCK MODE TO WAIT");
				Statement stCBS = conCBS.createStatement();
				stCBS.executeUpdate("SET ISOLATION TO DIRTY READ");
				stCBS.close();
				
			}

			PreparedStatement ps = conCBS.prepareStatement(query); //, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			if(parameters != null && parameters.length > 0){

				int i = 1;
				for(Object o : parameters){
					if(o instanceof java.util.Date) ps.setDate(i, new java.sql.Date( ((java.util.Date)o).getTime() ) );
					else if(o instanceof java.sql.Date) ps.setDate(i, (java.sql.Date)o );
					else if(o instanceof Double) ps.setDouble(i, Double.valueOf( o.toString()) );
					else if(o instanceof Long) ps.setLong(i, Long.valueOf( o.toString()) );
					else if(o instanceof Integer) ps.setInt(i, Integer.valueOf( o.toString()) );
					else ps.setString(i, o.toString());
					i++;

				}

			}

			rs = ps.executeQuery();
			// Fermeture des cnx
			// CBS_CNX_OPTI
//			if (ps != null) ps.close();
//			if(conCBS != null ) conCBS.close();
		}

		return rs;

	}
	
	public void executeUpdateSystemQuery(DataSystem ds, String query, Object[] parameters) throws Exception {

		if(conCBS == null || conCBS.isClosed()) conCBS = getSystemConnection(ds);

		if(conCBS != null){

			if(ds.getDbConnectionString().indexOf("informix") > 0) {
				Statement stCBS = conCBS.createStatement();
				stCBS.executeUpdate("SET ISOLATION TO DIRTY READ");
				stCBS.close();
			}

			PreparedStatement ps = conCBS.prepareStatement(query);

			if(parameters != null && parameters.length > 0){

				int i = 1;
				for(Object o : parameters){

					if(o == null) ps.setNull(i, java.sql.Types.NULL);
					else if(o instanceof java.util.Date) ps.setDate(i, new java.sql.Date( ((java.util.Date)o).getTime() ) );
					else if(o instanceof java.sql.Date) ps.setDate(i, (java.sql.Date)o );
					else if(o instanceof Double) ps.setDouble(i, Double.valueOf( o.toString()) );
					else if(o instanceof Long) ps.setLong(i, Long.valueOf( o.toString()) );
					else if(o instanceof Integer) ps.setInt(i, Integer.valueOf( o.toString()) );
					else ps.setString(i, o.toString());
					i++;

				}

			}
			// 580
			ps.executeUpdate();
			// Fermeture des cnx
			// CBS_CNX_OPTI
			if (ps != null) ps.close();
			if(conCBS != null ) conCBS.close();
		}

	}
	
	public void executeUpdateSystemQuery(DataSystem ds, List<Queries> queries) throws Exception {

		if(conCBS == null || conCBS.isClosed()) conCBS = getSystemConnection(ds);
		
		
		
		if(conCBS != null){
			//Set autocommit as false
			conCBS.setAutoCommit(Boolean.FALSE);
			
			if(ds.getDbConnectionString().indexOf("informix") > 0) {
				Statement stCBS = conCBS.createStatement();
				stCBS.executeUpdate("SET ISOLATION TO DIRTY READ");
				stCBS.close();
			}
			
			for(Queries q : queries) {
				String query = q.getQuery();
				Object[] parameters = q.getParams();
			
			
				PreparedStatement ps = conCBS.prepareStatement(query);
	
				if(parameters != null && parameters.length > 0){
	
					int i = 1;
					for(Object o : parameters){
	
						if(o == null) ps.setNull(i, java.sql.Types.NULL);
						else if(o instanceof java.util.Date) ps.setDate(i, new java.sql.Date( ((java.util.Date)o).getTime() ) );
						else if(o instanceof java.sql.Date) ps.setDate(i, (java.sql.Date)o );
						else if(o instanceof Double) ps.setDouble(i, Double.valueOf( o.toString()) );
						else if(o instanceof Long) ps.setLong(i, Long.valueOf( o.toString()) );
						else if(o instanceof Integer) ps.setInt(i, Integer.valueOf( o.toString()) );
						else ps.setString(i, o.toString());
						i++;
	
					}
	
				}
				
				ps.executeUpdate();
				if (ps != null) ps.close();
			}
			
			//Commit 
			conCBS.commit();
			
			// Fermeture des cnx
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
		}

	}
	

	/**
	 * Lit et retourne la date comptable dans le CBS
	 * @param dsCBS
	 * @return date comptable
	 */
	public Date getDateComptable(DataSystem dsCBS) throws Exception {

		Date dco = new Date();

		ResultSet rs = executeFilterSystemQuery(dsCBS, "select lib2, mnt1, mnt2 from bknom where ctab=? and cacc=?", new Object[]{"001", "00099"});
		if(rs != null && rs.next()){
			String date = rs.getString("lib2").trim().equals("FE") ? rs.getString("mnt1").length() == 7 ? "0".concat(rs.getString("mnt1")) : rs.getString("mnt1") : rs.getString("mnt2").length() == 7 ? "0".concat(rs.getString("mnt2")) : rs.getString("mnt2");
			dco = new SimpleDateFormat("ddMMyyyy").parse(date);
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		return dco;

	}
	

	public Date getDvaCredit() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, 1);

		while(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) cal.add(Calendar.DATE, 1);

		return cal.getTime();
	}
	

	public Date getDvaDebit() {
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, -1);

		while(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) cal.add(Calendar.DATE, -1);

		return cal.getTime();
	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#processPullTransaction(java.lang.String, java.lang.String, java.lang.Double)
	 */
	@Override
	public synchronized Transaction processPullTransaction(String phoneNumber, String bankPIN, Double amount) throws Exception, MoMoException {

		return processPullPushMessage( new RequestMessage(TypeOperation.PULL, bankPIN, phoneNumber, amount, null) );

	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#processPushTransaction(java.lang.String, java.lang.String, java.lang.Double)
	 */
	@Override
	public synchronized Transaction processPushTransaction(String phoneNumber, String bankPIN, Double amount) throws Exception, MoMoException {

		return processPullPushMessage( new RequestMessage(TypeOperation.PUSH, bankPIN, phoneNumber, amount, null) );

	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#processBalanceTransaction(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized Double processBalanceTransaction(String phoneNumber, String bankPIN) throws Exception, MoMoException {

		// Initialisation de la transaction a executer
		Transaction tx = getTransactionFromRequestMessage( new RequestMessage(TypeOperation.BALANCE, bankPIN, phoneNumber, 0d, null) );

		// En cas d'erreur on annule tout
		if(tx == null) return null;

		// Recuperation du solde du cpte
		Double solde = getSolde(tx.getAccount());

		if(solde == null) throw new MoMoException(ExceptionCode.SystemCoreBankingSystemAcces, "Erreur lors de la lecture du solde du compte", ExceptionCategory.SYSTEM);

		// MAJ du statut de la transaction
		tx.setStatus(TransactionStatus.SUCCESS);

		// Sauvegarde de la transaction
		saveTransaction(tx);

		// Retourne le solde du cpte
		return solde;
	}
	

	@AllowedRole(name = "executerSimulation", displayName = "MoMo.Executer.Simulation")
	public void executerSimulation() {

	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#processReversalTransaction(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized Map<String, String> processReversalTransaction(String remoteID) throws Exception {

		logger.info("*********************************processReversalTransaction**************************remoteID**"+remoteID);

		// Initialisation de la map a retourner
		Map<String, String> map = new HashMap<String, String>();

		// Chargement des proprietes ds la map
		map.put("remoteID", remoteID);

		Long txID = Long.valueOf(remoteID);
		List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, AliasesContainer.getInstance().add("transaction", "transaction"), RestrictionsContainer.getInstance().add(Restrictions.eq("transaction.id", txID)), null, null, 0, -1);

		if(eves == null || eves.isEmpty()) {
			map.put("statusCode", "500");
			return map;
		}

		try {

			// Recuperation de l'evenement genere par la transaction
			bkeve eve = eves.get(0);

			RestrictionsContainer rc = RestrictionsContainer.getInstance().add(Restrictions.eq("lg_Remote_ID", remoteID));
			//rc.add(Restrictions.eq("str_Phone",eve.getTransaction().getPhoneNumber()));
			for(USSDTransaction ut : filterUSSDTransactions(rc)) {

				// && StringUtils.containsIgnoreCase(ut.getStr_Status_Description(),"General failure.")
				if(ut.opeOK() == false){

					// Demarrage du service Facade du portail
					IFacadeManagerRemote portalFacadeManager = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );

					// Recuperation de la DS de cnx au CBS
					DataSystem dsCBS = (DataSystem) portalFacadeManager.findByProperty(DataSystem.class, "code", "DELTA-V10");

					// MAJ de l'evenement dans le CBS
					executeUpdateSystemQuery(dsCBS, "update bkeve set eta='IG', etap='VA' where eve='" + eve.getEve() + "' and age ='"+ eve.getAge() +"' ", null);

					// MAJ des soldes indicatifs des cptes debiteurs et crediteurs
					executeUpdateSystemQuery(dsCBS, "update bkcom set sin=sin-"+ eve.getMnt2() +" where age='"+ eve.getAge2() +"' and ncp='"+ eve.getNcp2() +"' and clc='"+ eve.getClc2() +"' ", null);
					executeUpdateSystemQuery(dsCBS, "update bkcom set sin=sin+"+ eve.getMnt1() +" where age='"+ eve.getAge1() +"' and ncp='"+ eve.getNcp1() +"' and clc='"+ eve.getClc1() +"' ", null);

					// Modification des infos de l'evenement
					eve.setEta("IG"); eve.setEtap("VA");
					eve.getTransaction().setStatus(TransactionStatus.CANCEL);
					eve.getTransaction().setReconcilier(Boolean.TRUE);

					// MAJ de l'evenement
					mobileMoneyDAO.update(eve);

					// Message d'information du client
					sendSMS("Your " + eve.getTransaction().getTypeOperation().getValue() + " transaction of XAF "+ eve.getTransaction().getAmount() +" has been cancelled!", eve.getTransaction().getPhoneNumber());

					// MAJ du statut de l'operation (executee avec succes)
					map.put("statusCode", "200");

				}else map.put("statusCode", "200");

			}

		}catch(Exception e){

			// MAJ du statut de l'operation (executee avec succes)
			map.put("statusCode", "500");
			e.printStackTrace();

		}

		// Retourne la map
		return map;

	}


	@Override
	public Map<String, String> checkSubscriber(String phoneNumber) throws Exception {

		// Verifie si l'abonne existe
		Subscriber s = findSubscriberFromPhoneNumber(phoneNumber);

		Map<String, String> map = new HashMap<String, String>();

		if(s != null){
			map.put("suscriber", "true");

			if(s.isMerchant()){
				map.put("profile", "2");
			}else{
				map.put("profile", "1"); 
			}

		}else{
			map.put("suscriber", "false");
			map.put("profile", "1"); 
		}

		return map;
	}


	@Override
	public synchronized Map<String, String> processPullTransaction(String msisdn, String bankPin, Double amount, String localID, String locale) throws Exception {

		// Initialisation de la map a retourner
		Map<String, String> map = new HashMap<String, String>();

		// Chargement des proprietes ds la map
		map.put("msisdn", msisdn);
		map.put("bankPin", bankPin);
		map.put("localID", localID);
		map.put("locale", locale);
		map.put("amount", null);
		map.put("remoteID", "0");

		if(isModeNuit() && !isModeNuitEstAuthorise()) throw new RuntimeException("Unauthorized to process the transaction at this time! Night Mode is Off!!!");

		try{

			// Execution de la transaction de Pull
			Transaction tx = processPullTransaction(msisdn, bankPin, amount);

			// Operation executee avec succes
			map.put("statusCode", "200");
			map.put("remoteID", tx.getId().toString());

			// Solde du compte après operation
			Double balance = getSolde( findSubscriberFromPhoneNumber(msisdn).getFirstAccount() ); //getBalanceTransaction(msisdn, bankPin);
			map.put("amount", String.valueOf(balance.longValue()));

		} catch(MoMoException me){

			// Erreur Momo
			if(me.getCode().equals(ExceptionCode.BankInsufficientBalance)) {
				map.put("statusCode", "504");
				map.put("error", me.getMessage());
			} else if(me.getCode().equals(ExceptionCode.SubscriberInvalidPIN)) {
				map.put("statusCode", "502");
				map.put("error", me.getMessage());
			}else if(me.getCode().equals(ExceptionCode.SubscriberSuspended)) {
				map.put("statusCode", "501");
				map.put("error", me.getMessage());
			}else {
				map.put("statusCode", "503");
				map.put("error", me.getMessage());
				logger.info(me.getCode() + " : " + me.getMessage());
				/**try{

					// Recuperation de la trace de l'erreur
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					me.printStackTrace(pw);

					// Initialisation du msg du mail a envoyer
					String traceErreur = "Pull from Account, Tel : " + msisdn + ", amount : " + amount + ". \n" + sw.toString();

					// Initialisation de la liste des adresses des destinataires
					List<String> adresses = new ArrayList<String>();
					adresses.add("aumer_soufo@afrilandfirstbank.com");
					adresses.add("francis_djiomou@afrilandfirstbank.com");
					adresses.add("boris_yetgna@afrilandfirstbank.com");

					// Demarrage du service Facade du portail
					IFacadeManagerRemote portal = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );

					// Envoi du mail contenant la trace de l'erreur generee
					portal.sendMail(new Mail("Trace Erreur Operation MAC", traceErreur, adresses), null);
					portal = null;

				}catch(Exception ex){ex.printStackTrace();}*/

			}

		} catch (Exception e){
			// Erreur Systeme
			map.put("statusCode", "500");
			map.put("error", e.getMessage());
			e.printStackTrace();

			//try{

			// Recuperation de la trace de l'erreur
			/**StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);

				// Initialisation du msg du mail a envoyer
				String traceErreur = "Pull from Account, Tel : " + msisdn + ", amount : " + amount + ". \n" + sw.toString();

				// Initialisation de la liste des adresses des destinataires
				List<String> adresses = new ArrayList<String>();
				adresses.add("aumer_soufo@afrilandfirstbank.com");
				adresses.add("francis_djiomou@afrilandfirstbank.com");
				adresses.add("boris_yetgna@afrilandfirstbank.com");

				// Demarrage du service Facade du portail
				IFacadeManagerRemote portal = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );

				// Envoi du mail contenant la trace de l'erreur generee
				//portal.sendMail(new Mail("Trace Erreur Operation MAC", traceErreur, adresses), null);
				portal = null;*/

			//}catch(Exception ex){ex.printStackTrace();}

		}

		// Log
		System.err.println("[MAC LOGGER] Voici la Map Retournee pour "+ msisdn +" : " + map.toString());

		return map;

	}
	

	@Override
	public synchronized Map<String, String> processPushTransaction(String msisdn, String bankPin, Double amount, String localID, String locale) throws Exception {

		// Initialisation de la map a retourner
		Map<String, String> map = new HashMap<String, String>();

		// Chargement des proprietes ds la map
		map.put("msisdn", msisdn);
		map.put("bankPin", bankPin);
		map.put("localID", localID);
		map.put("locale", locale);
		map.put("amount", null);
		map.put("remoteID", "0");

		if(isModeNuit() && !isModeNuitEstAuthorise()) throw new RuntimeException("Unauthorized to process the transaction at this time! Night Mode is Off!!!");

		try{

			// Execution de la transaction de Push
			Transaction tx = processPushTransaction(msisdn, bankPin, amount);

			// Operation executee avec succes
			map.put("statusCode", "200");
			map.put("remoteID", tx.getId().toString());

			// Solde du compte après operation
			Double balance = getSolde( findSubscriberFromPhoneNumber(msisdn).getFirstAccount() ); //getBalanceTransaction(msisdn, bankPin);
			map.put("amount", String.valueOf(balance.longValue()));

		} catch(MoMoException me){

			// Erreur Momo
			if(me.getCode().equals(ExceptionCode.BankInsufficientBalance)) {
				map.put("statusCode", "504");
				map.put("error", me.getMessage());
			} else if(me.getCode().equals(ExceptionCode.SubscriberInvalidPIN)) {
				map.put("statusCode", "502");
				map.put("error", me.getMessage());
			}else if(me.getCode().equals(ExceptionCode.SubscriberSuspended)) {
				map.put("statusCode", "501");
				map.put("error", me.getMessage());
			}else {
				map.put("statusCode", "503");
				map.put("error", me.getMessage());
				logger.info(me.getCode() + " : " + me.getMessage());


				/**try{

					// Recuperation de la trace de l'erreur
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					me.printStackTrace(pw);

					// Initialisation du msg du mail a envoyer
					String traceErreur = "Push to Account, Tel : " + msisdn + ", amount : " + amount + ". \n" + sw.toString();

					// Initialisation de la liste des adresses des destinataires
					List<String> adresses = new ArrayList<String>();
					adresses.add("aumer_soufo@afrilandfirstbank.com");
					adresses.add("francis_djiomou@afrilandfirstbank.com");
					adresses.add("boris_yetgna@afrilandfirstbank.com");

					// Demarrage du service Facade du portail
					IFacadeManagerRemote portal = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );

					// Envoi du mail contenant la trace de l'erreur generee
					portal.sendMail(new Mail("Trace Erreur Operation MAC", traceErreur, adresses), null);
					portal = null;

				}catch(Exception ex){}*/

			}

		} catch (Exception e){
			// Erreur Systeme
			map.put("statusCode", "500");
			map.put("error", e.getMessage());
			e.printStackTrace();


			/**try{

				// Recuperation de la trace de l'erreur
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);

				// Initialisation du msg du mail a envoyer
				String traceErreur = "Push to Account, Tel : " + msisdn + ", amount : " + amount + ". \n" + sw.toString();

				// Initialisation de la liste des adresses des destinataires
				List<String> adresses = new ArrayList<String>();
				adresses.add("aumer_soufo@afrilandfirstbank.com");
				adresses.add("francis_djiomou@afrilandfirstbank.com");
				adresses.add("boris_yetgna@afrilandfirstbank.com");

				// Demarrage du service Facade du portail
				IFacadeManagerRemote portal = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );

				// Envoi du mail contenant la trace de l'erreur generee
				portal.sendMail(new Mail("Trace Erreur Operation MAC", traceErreur, adresses), null);
				portal = null;

			}catch(Exception ex){}*/

		}

		return map;

	}
	

	@Override
	public synchronized Map<String, String> processBalanceTransaction(String msisdn, String bankPin, String localID, String locale) throws Exception {

		// Initialisation de la map a retourner
		Map<String, String> map = new HashMap<String, String>();

		// Chargement des proprietes ds la map
		map.put("msisdn", msisdn);
		map.put("bankPin", bankPin);
		map.put("localID", localID);
		map.put("locale", locale);		
		map.put("remoteID", "");

		if(isModeNuit() && !isModeNuitEstAuthorise()) throw new RuntimeException("Unauthorized to process the transaction at this time! Night Mode is Off!!!");

		try{

			// Execution de la transaction de Balance
			Double amount = processBalanceTransaction(msisdn, bankPin);
			//logger.info("SOLDE : "+amount);
			// Recuperation du solde
			map.put("amount", String.valueOf(amount.longValue()) );

			// Operation executee avec succes
			map.put("statusCode", "200");

		} catch(MoMoException me){

			// Erreur Momo
			if(me.getCode().equals(ExceptionCode.BankInsufficientBalance)) {
				map.put("statusCode", "504");
				map.put("error", me.getMessage());
			} else if(me.getCode().equals(ExceptionCode.SubscriberInvalidPIN)) {
				map.put("statusCode", "502");
				map.put("error", me.getMessage());
			}else if(me.getCode().equals(ExceptionCode.SubscriberSuspended)) {
				map.put("statusCode", "501");
				map.put("error", me.getMessage());
			}else {
				map.put("statusCode", "503");
				map.put("error", me.getMessage());
				logger.info(me.getCode() + " : " + me.getMessage());
			}

		} catch (Exception e){
			// Erreur Systeme
			map.put("statusCode", "500");
			map.put("error", e.getMessage());
			e.printStackTrace();
		}

		return map;

	}


	/**
	 * Determine si l'agence centrale est fermee ou non
	 * @return status de l'agence
	 * @throws Exception
	 */
	private String getStatutAgenceCentrale() throws Exception {

		// Lecture des Prmtrs de cnx a Amplitude
		if(dsCBS == null) findCBSDataSystem();

		// Recuperation du l'etat de l'agence centrale
		ResultSet rs = executeFilterSystemQuery(dsCBS, "select lib2 from bknom where cacc='00099'", null);
		String statutTfj = rs != null && rs.next() ? rs.getString("lib2").trim() : "OU";
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		return statutTfj;
	}
	

	/**
	 * Determine si on a lance les TFJ
	 * @return true s'il faut lancer les tfjo, false sinon
	 * @throws Exception
	 */
	private boolean isTfjEnCours() throws Exception {

		// Recuperation des parametres generaux
		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		// Recuperation du statut de l'agence centrale
		String statutAgCentral = getStatutAgenceCentrale();

		// On calcule la date 10h avec le lancement des TFJ
		Calendar cal = new GregorianCalendar(); cal.setTime( new SimpleDateFormat("dd/MM/yyyy HH':'mm").parse(params.getDateTfjo()) ); cal.add(Calendar.HOUR, 8);

		// Calcul du resultat a retourner
		boolean resultat = statutAgCentral.equalsIgnoreCase("FE") || (!params.getActive().booleanValue()  && new Date().before(cal.getTime()) );

		// Si l'agence est ouverte et on a depasse les 10h apres le lancement des TFJ et que le service n'est tjr pas actif alors on l'active
		if( statutAgCentral.equalsIgnoreCase("OU") && (!params.getActive().booleanValue()  && new Date().after(cal.getTime()) ) ){

			// Activation de la Fin du TFJ
			endTFJ();

		}

		// Retourne le resultat 
		return resultat;
	}


	/**
	 * Determine si la bd est en mode nuit
	 * @return true si la bd est en mode nuit, false sinon
	 * @throws Exception
	 */
	private boolean isModeNuit() throws Exception {

		boolean res = false;

		// Lecture des Prmtrs de cnx a Amplitude
		if(dsCBS == null) findCBSDataSystem();

		// Recuperation du l'etat de l'agence centrale
		ResultSet rs = executeFilterSystemQuery(dsCBS, "select mnt4 from bknom where ctab='098' and cacc='SITE-CENT'", null);

		// Si le champ mnt4 de l'agence centrale est = 1 alors on est en mode nuit
		res = rs != null && rs.next() ? rs.getInt("mnt4") == 1 : false;

		// Fermeture de la cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le resultat 
		return res;
	}
	

	private boolean isModeNuitEstAuthorise() throws Exception {

		return findParameters().getAllowTransDuringTFJO().booleanValue(); 

	}


	// NUIT
	/**
	 * Determine si les TFJO sont en cours dans Portal
	 * @return true les TFJO sont en cours dans Portal, false sinon
	 * @throws Exception
	 */
	public boolean isTFJOPortalEnCours() throws Exception {

		boolean res = false;

		// Recuperation des parametres generaux du module
		// checkGlobalConfig(); //Parameters p = findParameters();
		params = findParameters();

		res = params.getTfjoEnCours();

		// Log
		//logger.info("TFJP Portal En cours : "+res);

		// Retourne le resultat 
		return res;
	}


	// NUIT
	/**
	 * Mise a jour de la valeur du parametre tfjoPortalEnCours
	 * 
	 * @param tfjo valeur de l'etat du tfjo portal
	 * @throws Exception
	 */
	private void setTFJOPortalEnCours(Boolean tfjo) throws Exception {

		// Recuperation des parametres generaux du module
		// checkGlobalConfig(); 
		params = findParameters();

		// MAJ de l'etat des TFJO Portal
		if(params.getTfjoEnCours().booleanValue() != tfjo){
			//logger.info("Mise a jour de l'etat des TFJO Portal! TFJO = "+tfjo);
			params.setTfjoEnCours(tfjo); 
			params = mobileMoneyDAO.update(params);
		}

	}


	private void endTFJ() throws Exception {

		// On Reactivation du module
		Parameters p = findParameters(); 
		p.setActive(Boolean.TRUE);
		mobileMoneyDAO.update(p);

		// On Poste dans Amplitude des evts mis en attente pendant les TFJ
		posterEvenementsEnSuspendPendantTFJ();

	}
	

	/**
	 * Calcule et retourne le total des Pull qu'un client a effectue pendant les TFJ
	 * @param phoneNumber
	 * @return total des Pull qu'un client a effectue pendant les TFJ
	 * @throws Exception
	 */
	private Double getTotalAmountPullPendantTFJ(String phoneNumber) throws Exception {

		// Initialisation du mnt a retourner
		Double mnt = 0d;

		// Recherche de la liste des evenements concernes 
		List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, AliasesContainer.getInstance().add("transaction", "tx"), RestrictionsContainer.getInstance().add(Restrictions.eq("tx.typeOperation", TypeOperation.PULL)).add(Restrictions.eq("tx.phoneNumber", phoneNumber)).add(Restrictions.eq("suspendInTFJ", Boolean.TRUE)), null, null, 0, -1);

		// Calcul du mnt total des Pull
		if(eves != null && !eves.isEmpty()) for(bkeve eve : eves) mnt += eve.getTransaction().getAmount();

		// Liberation des ressources
		eves.clear();

		// Retourne le mnt total des Pull effectues
		return mnt;
	}
	

	/**
	 * Poste les evenements 
	 * @throws Exception
	 */
	public void posterEvenementsEnSuspendPendantTFJ() throws Exception {

		// Recherche des evenements non postes dans Delta pour avoir ete faits pendant les TFJ
		List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("suspendInTFJ", Boolean.TRUE)), null, null, 0, -1);

		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();

		Date dco = getDateComptable(dsCBS);

		// Parcours des Evenements trouves
		for(bkeve eve : eves){

			try {

				// Affectation de la date comptable actuelle a l'evenement
				eve.setDco(dco);

				// Enregistrement de l'evenement dans Amplitude
				executeUpdateSystemQuery(dsCBS, eve.getSaveQuery(), eve.getQueryValues());

				// MAJ du solde indicatif du compte debiteur
				executeUpdateSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(4).getQuery(), new Object[]{ (eve.getTransaction().getTypeOperation().equals(TypeOperation.PUSH) ? eve.getTransaction().getAmount() : eve.getMnt1()) , eve.getAge1(), eve.getNcp1(), eve.getClc1() } );

				// MAJ du solde indicatif crediteur
				if(eve.getTransaction().getTypeOperation().equals(TypeOperation.PUSH) || eve.getTransaction().getTypeOperation().equals(TypeOperation.PULL)) executeUpdateSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(5).getQuery(), new Object[]{eve.getTransaction().getAmount(), eve.getAge2(), eve.getNcp2(), eve.getClc2()  });

				// MAJ du dernier numero d'evenement utilise pour le type operation
				executeUpdateSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(3).getQuery(), new Object[]{ Long.valueOf(eve.getEve()), eve.getOpe() });

			} catch(Exception e){e.printStackTrace();}

			// MAJ du Statut de l'evenement
			eve.setSuspendInTFJ(Boolean.FALSE);

			// MAJ de l'evenement
			mobileMoneyDAO.update(eve);
		}

		// Liberation des ressources
		eves.clear();

	}


	public int getTotalAbonComptabilises() throws Exception {

		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);
		int nb = 0;

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, "select count(*) as nb from bkmvti where ope=? and dco=? and cha like '37%' and lib like 'FRAIS%'", new Object[]{ params.getCodeOperation(), dco });

		// Construction de la liste
		if(rs != null && rs.next()) nb = rs.getInt("nb");
		// CBS_CNX_OPTI
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		if(conCBS != null ) conCBS.close();
		
		return nb;
	}


	public int getTotalAbonComptabilises(String util, String typeOp) throws Exception {

		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);
		int nb = 0;

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, "select count(*) as nb from bkmvti where ope=? and dco=? and cha like '37%' and lib like ? and uti = ? ", new Object[]{ params.getCodeOperation(), dco, "%"+typeOp+"%", util });

		// Construction de la liste
		if(rs != null && rs.next()) nb = rs.getInt("nb");
		// CBS_CNX_OPTI
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		if(conCBS != null ) conCBS.close();
		
		return nb;
	}
	

	public Double getTotalComsAbonComptabilises() throws Exception {

		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);
		Double nb = 0d;

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, "select sum(mon) as mnt from bkmvti where ope=? and dco=? and ncp=? and lib like 'COM %' and eve='000000'", new Object[]{ params.getCodeOperation(), dco, params.getNumCompteCommissions().split("-")[1] });

		// Construction de la liste
		if(rs != null && rs.next()) nb = rs.getDouble("mnt");
		// CBS_CNX_OPTI
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		if(conCBS != null ) conCBS.close();
		
		return nb;
	}


	public Double getTotalComsAbonComptabilises(String util, String typeOp) throws Exception {

		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);
		Double nb = 0d;

		// Execution de la requete de controle de l'equilibre
		//ResultSet rs = executeFilterSystemQuery(dsCBS, "select sum(mon) as mnt from bkmvti where ope=? and dco=? and ncp=? and lib like ? and eve='000000' and uti = ? ", new Object[]{ params.getCodeOperation(), dco, params.getNumCompteCommissions().split("-")[1], typeOp+"%", util });
		ResultSet rs = executeFilterSystemQuery(dsCBS, "select sum(mon) as mnt from bkmvti where ope=? and dco=? and ncp=? and lib like ? and uti = ? ", new Object[]{ params.getCodeOperation(), dco, params.getNumCompteCommissions().split("-")[1], typeOp+"%", util });

		// Construction de la liste
		if(rs != null && rs.next()) nb = rs.getDouble("mnt");
		// CBS_CNX_OPTI
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		if(conCBS != null ) conCBS.close();
		
		return nb;
	}


	public Double getTotalTaxAbonComptabilises() throws Exception {

		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);
		Double nb = 0d;

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, "select sum(mon) as mnt from bkmvti where ope=? and dco=? and ncp=? and lib like 'TAX %' and eve='000000'", new Object[]{ params.getCodeOperation(), dco, params.getNumCompteTVA().split("-")[1] });

		// Construction de la liste
		if(rs != null && rs.next()) nb = rs.getDouble("mnt");
		// CBS_CNX_OPTI
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		if(conCBS != null ) conCBS.close();
		
		return nb;
	}


	public Double getTotalTaxAbonComptabilises(String util, String typeOp) throws Exception {

		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);
		Double nb = 0d;

		// Execution de la requete de controle de l'equilibre
		//ResultSet rs = executeFilterSystemQuery(dsCBS, "select sum(mon) as mnt from bkmvti where ope=? and dco=? and ncp=? and lib like ? and eve='000000' and uti = ? ", new Object[]{ params.getCodeOperation(), dco, params.getNumCompteTVA().split("-")[1], typeOp+"%", util });
		ResultSet rs = executeFilterSystemQuery(dsCBS, "select sum(mon) as mnt from bkmvti where ope=? and dco=? and ncp=? and lib like ? and uti = ? ", new Object[]{ params.getCodeOperation(), dco, params.getNumCompteTVA().split("-")[1], typeOp+"%", util });

		// Construction de la liste
		if(rs != null && rs.next()) nb = rs.getDouble("mnt");
		// CBS_CNX_OPTI
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		if(conCBS != null ) conCBS.close();
		
		return nb;
	}


	public List<Equilibre> getRapportEquilibre() throws Exception {

		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		List<Equilibre> data = new ArrayList<Equilibre>();

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibre(), new Object[]{ params.getCodeOperation(), dco });

		// Construction de la liste
		while(rs != null && rs.next()) data.add( new Equilibre(rs.getDate("dco"), rs.getString("uti"), rs.getString("ope"), rs.getString("sen"), rs.getInt("nbre"), rs.getDouble("total")) );

		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;

	}


	public List<Equilibre> getRapportEquilibre(String util, String typeOp) throws Exception {

		String op1;
		String op2;

		if(typeOp.equals("PULL_PUSH")){
			op1 = TypeOperation.PUSH.toString();
			op2 = TypeOperation.PULL.toString();
		} else{
			op1 = typeOp;
			op2 = typeOp;
		}

		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		List<Equilibre> data = new ArrayList<Equilibre>();

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibre(), new Object[]{ params.getCodeOperation(), dco, util, "%"+op1+"%", "%"+op2+"%" });

		// Construction de la liste
		while(rs != null && rs.next()) data.add( new Equilibre(rs.getDate("dco"), rs.getString("uti"), rs.getString("ope"), rs.getString("sen"), rs.getInt("nbre"), rs.getDouble("total")) );

		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;

	}
	
	
	public EquilibreComptes getRapportEquilibreCpteClient() throws Exception{
		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		EquilibreComptes data = new EquilibreComptes();
		data.setTypeCpte("Compte Client");

		// Execution de la requete de controle de l'equilibre PULL
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibrePULLClient(), new Object[]{ dco});

		// Construction de la liste
		while(rs != null && rs.next()){
			data.setNbrePull(rs.getInt("nbre")); 
			data.setTotalPull(rs.getDouble("total")) ;
		}
		
		// Execution de la requete de controle de l'equilibre PUSH
		rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibrePUSHClient(), new Object[]{ dco});

		// Construction de la liste
		while(rs != null && rs.next()) {
			data.setNbrePush(rs.getInt("nbre")); 
			data.setTotalPush(rs.getDouble("total")) ;
		}
		
		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;
	}
	
		
	public EquilibreComptes getRapportEquilibreCpteDAP() throws Exception{
		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		EquilibreComptes data = new EquilibreComptes();
		data.setTypeCpte("Compte DAP");

		// Execution de la requete de controle de l'equilibre PULL
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibrePULLDAP(), new Object[]{ dco});

		// Construction de la liste
		while(rs != null && rs.next()){
			data.setNbrePull(rs.getInt("nbre")); 
			data.setTotalPull(rs.getDouble("total")) ;
		}
		
		// Execution de la requete de controle de l'equilibre PUSH
		rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibrePUSHDAP(), new Object[]{ dco});

		// Construction de la liste
		while(rs != null && rs.next()) {
			data.setNbrePush(rs.getInt("nbre")); 
			data.setTotalPush(rs.getDouble("total")) ;
		}
		
		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;
	}
	
		
	public EquilibreComptes getRapportEquilibreCpteFloatMTN() throws Exception{
		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		EquilibreComptes data = new EquilibreComptes();
		data.setTypeCpte("Compte Float MTN");
		
		// Execution de la requete de controle de l'equilibre PULL
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibrePULLFloatMTN(), new Object[]{ dco});

		// Construction de la liste
		while(rs != null && rs.next()){
			data.setNbrePull(rs.getInt("nbre")); 
			data.setTotalPull(rs.getDouble("total")) ;
		}
		
		// Execution de la requete de controle de l'equilibre PUSH
		rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibrePUSHFloatMTN(), new Object[]{ dco});

		// Construction de la liste
		while(rs != null && rs.next()) {
			data.setNbrePush(rs.getInt("nbre")); 
			data.setTotalPush(rs.getDouble("total")) ;
		}
		
		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;
	}
	
	
	public Equilibre getRapportEquilibrePULLClient() throws Exception {
		
		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		Equilibre data = new Equilibre();

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibrePULLClient(), new Object[]{ dco});

		// Construction de la liste
		while(rs != null && rs.next()) data = new Equilibre(new Date(), "", "", "", rs.getInt("nbre"), rs.getDouble("total")) ;

		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;

	}
	
	
	public Equilibre getRapportEquilibrePUSHClient() throws Exception {
		
		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		Equilibre data = new Equilibre();

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibrePUSHClient(), new Object[]{ dco});

		// Construction de la liste
		while(rs != null && rs.next()) data = new Equilibre(new Date(), "", "", "", rs.getInt("nbre"), rs.getDouble("total")) ;

		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;

	}
	
	
	public Equilibre getRapportEquilibrePULLDAP() throws Exception {
		
		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		Equilibre data = new Equilibre();

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibrePULLDAP(), new Object[]{ dco});

		// Construction de la liste
		while(rs != null && rs.next()) data = new Equilibre(new Date(), "", "", "", rs.getInt("nbre"), rs.getDouble("total")) ;

		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;

	}
	
	
	public Equilibre getRapportEquilibrePUSHDAP() throws Exception {
		
		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		Equilibre data = new Equilibre();

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibrePUSHDAP(), new Object[]{ dco});

		// Construction de la liste
		while(rs != null && rs.next()) data = new Equilibre(new Date(), "", "", "", rs.getInt("nbre"), rs.getDouble("total")) ;

		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;

	}
	
	
	public Equilibre getRapportEquilibrePULLFloatMTN() throws Exception {
		
		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		Equilibre data = new Equilibre();

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibrePULLFloatMTN(), new Object[]{ dco});

		// Construction de la liste
		while(rs != null && rs.next()) data = new Equilibre(new Date(), "", "", "", rs.getInt("nbre"), rs.getDouble("total")) ;

		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;

	}
	
	
	public Equilibre getRapportEquilibrePUSHFloatMTN() throws Exception {
		
		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		Equilibre data = new Equilibre();

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlEquilibrePUSHFloatMTN(), new Object[]{ dco});

		// Construction de la liste
		while(rs != null && rs.next()) data = new Equilibre(new Date(), "", "", "", rs.getInt("nbre"), rs.getDouble("total")) ;

		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;

	}

	
	public List<Doublon> getRapportDoublon() throws Exception {

		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		List<Doublon> data = new ArrayList<Doublon>();

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlDoublon(), new Object[]{ params.getCodeOperation(), dco });

		// Construction de la liste
		while(rs != null && rs.next()) data.add( new Doublon(rs.getString("age"), rs.getString("dev"), rs.getString("ncp"), rs.getString("ope"), rs.getString("eve"), rs.getString("pie"), rs.getString("lib"), rs.getDouble("mon"), rs.getString("sen"), rs.getInt("nbre")) );

		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;

	}


	public List<Doublon> getRapportDoublon(String util, String typeOp) throws Exception {

		String op1;
		String op2;

		if(typeOp.equals("PULL_PUSH")){
			op1 = TypeOperation.PUSH.toString();
			op2 = TypeOperation.PULL.toString();
		} else{
			op1 = typeOp;
			op2 = typeOp;
		}

		// DataStore vers Amplitude
		if(dsCBS == null) findCBSDataSystem();
		// checkGlobalConfig();
		params = findParameters();

		// Recuperation de la date comptable
		Date dco = getDateComptable(dsCBS);

		// Initialisation de la liste des valeurs a retourner
		List<Doublon> data = new ArrayList<Doublon>();

		// Execution de la requete de controle de l'equilibre
		ResultSet rs = executeFilterSystemQuery(dsCBS, MoMoHelper.getQueryControlDoublon(), new Object[]{ params.getCodeOperation(), dco, util, "%"+op1+"%", "%"+op2+"%" });

		// Construction de la liste
		while(rs != null && rs.next()) data.add( new Doublon(rs.getString("age"), rs.getString("dev"), rs.getString("ncp"), rs.getString("ope"), rs.getString("eve"), rs.getString("pie"), rs.getString("lib"), rs.getDouble("mon"), rs.getString("sen"), rs.getInt("nbre")) );

		// fermeture des cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Retourne le rapport
		return data;

	}


	/*
	private boolean isModuleActive(){

		// Initialisation du resultat a retourner
		boolean result = true;

		// Recuperation des parametres Generaux
		Parameters param = findParameters();

		// Si
		if(param.getActive().booleanValue() || param.getHeureReprise() == null || param.getDateTfjo() == null) {

			return result;

		} else {

			try{

				// Initialisation du calendrier Gregorien
				GregorianCalendar cal = new GregorianCalendar();
				cal.setTime(new Date());

				// Recuperation de la date courante
				Date now = MoMoHelper.sdf.parse( MoMoHelper.sdf.format(new Date()) ) ;

				// Calcul de la Date de remise en service du module
				Date dateReprise = MoMoHelper.sdf.parse( MoMoHelper.sdf_DATE.format(cal.getTime()) + " " + param.getHeureReprise()  );

				// Recuperation de la date du Traitement (TFJO)
				Date dateTfjo = MoMoHelper.sdf.parse(param.getDateTfjo());

				// Si la Date de reprise est inferieure a la date du Traitement (alors il s'agit +to du jour suivant)
				if(dateTfjo.after(dateReprise)) {
					cal.add(Calendar.DATE, 1);
					dateReprise = MoMoHelper.sdf.parse( MoMoHelper.sdf_DATE.format(cal.getTime()) + " " + param.getHeureReprise()  );
				}

				// Si la date de remise en service du module est depassee
				if(now.after(dateReprise)) {

					// Reactivation du module
					mobileMoneyDAO.getEntityManager().createQuery("Update Parameters p set p.active = :actif").setParameter("actif", Boolean.TRUE).executeUpdate();

				} else {

					// Le module est désactive
					result = false;
				}

			}catch(Exception ex){}
		}

		return result;
	}
	 */


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#filterUSSDTransactions(com.yashiro.persistence.utils.dao.tools.RestrictionsContainer)
	 */
	@Override
	@SuppressWarnings("unchecked")
	@AllowedRole(name = "filterUSSDTransactions", displayName = "MoMo.Consulter.USSDTransactions")
	public List<USSDTransaction> filterUSSDTransactions(RestrictionsContainer rc) {

		// Recherche la liste des trx USSD ds la BD du serveur Pull/Push
		List<USSDTransaction> data = pullpushDAO.filter(USSDTransaction.class, null, rc, OrderContainer.getInstance().add(Order.desc("dt_Created")), null, 0, -1);

		if( !data.isEmpty() ) {

			// Affectation du souscripteur a chaque enregistrement trouve
			for(USSDTransaction t : data) {

				try {

					// Recherche du souscripteur possedant le numero de Tel du message 
					List<Subscriber> subs = mobileMoneyDAO.getEntityManager().createQuery("From Subscriber s left join fetch s.phoneNumbers phones where phones in ('"+ t.getStr_Phone() +"')  ").getResultList();
					t.setSubscriber( subs != null && !subs.isEmpty() ? subs.get(0) : null );

				} catch(Exception e){}
			}
		}

		return data;
	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#executerReconciliation(java.util.List)
	 */
	@Override
	@AllowedRole(name = "executerReconciliation", displayName = "MoMo.Executer.Reconciliation")
	public void executerReconciliation(List<USSDTransaction> trans, String user) throws Exception, MoMoException {

		if(trans == null || trans.isEmpty()) return;

		// Excution des transactions Pull/Push de la liste
		for(USSDTransaction t : trans) {

			// S'il s'agit d'un PUSH 
			if(t.getTypeOperation().equals(TypeOperation.PUSH))
				// Re-Execution de la transaction
				processPullPushMessage( new RequestMessage(t.getTypeOperation(), Encrypter.getInstance().decryptText( t.getSubscriber().getBankPIN() ), t.getStr_Phone(), Double.valueOf(t.getInt_Amount()), t.getSubscriber().getAccounts().get(0)) ) ;

			// S'il s'agit d'un PULL
			else if(t.getTypeOperation().equals(TypeOperation.PULL))
				// Annulation de la Transaction
				processReversalTransaction(t.getLg_Remote_ID());

			// MAJ des statuts de la transaction
			t.setStr_Status("valide"); t.setStr_Step(t.getTypeOperation().equals(TypeOperation.PUSH) ? "Reconciliee" : "Annulee"); t.setStr_Status_Description("Operation "+ (t.getTypeOperation().equals(TypeOperation.PUSH) ? "Reconciliee" : "Annulee") +" par " + user + " le " + new SimpleDateFormat("dd/MM/yyyy HH':'mm").format(new Date()));

			// MAJ des statuts de la transaction dans l'api ussd
			pullpushDAO.update(t);
		}

	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#executerTFJO()
	 */
	@Override
	@SuppressWarnings("unchecked")
	@TransactionTimeout(value = 15000)
	@AllowedRole(name = "executerTFJO", displayName = "MoMo.Executer.TFJO")
	public List<Comptabilisation> executerTFJO() throws Exception {

		// Initialisation de la liste a retourner
		List<Comptabilisation> compta = new ArrayList<Comptabilisation>();

		// Lecture des parametres generaux
		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		// Recuperation de la liste des commissions
		Map<TypeOperation, Commissions> mapComs = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation");
		Double valeurComs = 0d;
		Double tva = 0d; 
		Periodicite p = Periodicite.MOIS;

		// Construction d'une Map de periodicites
		Map<Periodicite, Integer> mapPeriods = new HashMap<Periodicite, Integer>();
		mapPeriods.put(Periodicite.MOIS, 30); mapPeriods.put(Periodicite.TRIMESTRE, 90); mapPeriods.put(Periodicite.SEMESTRE, 180); mapPeriods.put(Periodicite.ANNUEL, 365);

		// Calcul des commissions a prelever periodiquement
		valeurComs += mapComs.get(TypeOperation.PULL).getPeriodFacturation() != null ? mapComs.get(TypeOperation.PULL).getValeur() : 0d;
		valeurComs += mapComs.get(TypeOperation.PUSH).getPeriodFacturation() != null ? mapComs.get(TypeOperation.PUSH).getValeur() : 0d;

		// Recuperation de la periodicite
		p = mapComs.get(TypeOperation.PULL).getPeriodFacturation() != null ? mapComs.get(TypeOperation.PULL).getPeriodFacturation() : mapComs.get(TypeOperation.PUSH).getPeriodFacturation() ;

		// Recuperation de la tva
		tva = Math.max( mapComs.get(TypeOperation.PULL).getPeriodFacturation() != null ? mapComs.get(TypeOperation.PULL).getTauxTVA() : 0d, mapComs.get(TypeOperation.PUSH).getPeriodFacturation() != null ? mapComs.get(TypeOperation.PUSH).getTauxTVA() : 0d);

		// Si la transaction de PULL est comptabilisee periodiquement
		if( valeurComs > 0 ) { 

			// Lecture de la periodicite de facturation
			Integer nbJrs = mapPeriods.get(p); 

			// Initialisation de la requete de chargement des abonnes a comptabiliser
			String req = "Select distinct s from Subscriber s where s.status ='"+ StatutContrat.ACTIF +"' and ( s.id in (select distinct c.subscriber.id from Comptabilisation c where CURRENT_DATE - c.dateCompta >= "+ nbJrs +" and c.dateCompta = (select max(c3.dateCompta) from Comptabilisation c3 ) ) or (s.id not in (select distinct c2.subscriber.id from Comptabilisation c2) and CURRENT_DATE - s.date >= "+ nbJrs +" )  ) order by s.customerName ";

			// Chargement de la liste des abonnes a comptabiliser
			List<Subscriber> subs = new ArrayList<Subscriber>( new HashSet<Subscriber>( mobileMoneyDAO.getEntityManager().createQuery(req).getResultList() ) );

			// Parcours de la liste des abonnes trouves
//			for(Subscriber s : subs) {
//
//				// Generation des evenements a comptabiliser pour chaque abonne trouve
//				// La methode a changee
//				//compta.add( new Comptabilisation(null, new Date(), s, p, buildEvenement( new Transaction(TypeOperation.COMPTABILISATION, s, 0d, s.getAccounts().get(0), s.getPhoneNumbers().get(0) , new Date(), valeurComs, valeurComs * (1 + tva/100)  ) ) ) );
//
//			}


		}


		// Liberation des ressources
		mapComs.clear(); mapPeriods.clear(); //params = null; //com = null; com2 = null;

		if(compta != null && !compta.isEmpty()) for(int i=compta.size()-1; i>=0; i--){
			if(compta.get(i).getEve() == null) compta.remove(i);
		}

		// Retourne la liste des comptabilisations
		return compta;

	}
	

	private int getNbJrsByPeriod(Periodicite period){
		if(period.equals(Periodicite.MOIS)) return 30;
		else if(period.equals(Periodicite.TRIMESTRE)) return 90;
		else if(period.equals(Periodicite.ANNUEL)) return 365;
		else return 30;
	}
	

	@Override
	@SuppressWarnings("unchecked")
	//@TransactionTimeout(value = 15000)
	@AllowedRole(name = "executerTFJO", displayName = "MoMo.Executer.TFJO")
	public void executerTFJO2(Date date) throws Exception {

		// Initialisation de la liste des abonnes a comptabiliser
		List<Subscriber> subs = new ArrayList<Subscriber>();

		// Lecture des parametres generaux
		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		// Recuperation de la liste des commissions
		Map<TypeOperation, Commissions> mapComs = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation");

		// Initialisation de la TVA
		Double tva = 19.25d; 

		// Construction d'une Map de periodicites
		Map<Periodicite, Integer> mapPeriods = new HashMap<Periodicite, Integer>();
		mapPeriods.put(Periodicite.MOIS, 30); 
		mapPeriods.put(Periodicite.TRIMESTRE, 90); 
		mapPeriods.put(Periodicite.SEMESTRE, 180); 
		mapPeriods.put(Periodicite.ANNUEL, 365);

		// Recuperation de la tva parametree sur les commissions PULL et PUSH
		tva = Math.max( mapComs.get(TypeOperation.PULL).getPeriodFacturation() != null ? mapComs.get(TypeOperation.PULL).getTauxTVA() : 0d, mapComs.get(TypeOperation.PUSH).getPeriodFacturation() != null ? mapComs.get(TypeOperation.PUSH).getTauxTVA() : 0d);

		// Initialisation de la requete de selection des abonnements a comptabiliser (date d'anniversaire)
		//String req = "From Subscriber s where s.status = :statut and s.facturer = :facturer and s.commissions>0"; //  and (current_date - s.dateDernCompta) >= (case when s.period='MOIS' then 30 else ( case when s.period='TRIMESTRE' then 90 else ( case when s.period='SEMESTRE' then 180 else ( case when s.period='ANNUEL' then 365 else ( case when s.period='SEMAINE' then 7 else 1 end ) end ) end ) end ) end ) ";
		String req = "From Subscriber s where s.status = :statut and s.facturer = :facturer  and s.commissions > 0  and s.dateDernCompta <=  :dateDernCompta "; // and s.date < :date

		//logger.info("Recup liste des abonnes");
		// Recuperation de la liste des abonnements a comptabiliser
		//subs = mobileMoneyDAO.getEntityManager().createQuery(req).setParameter("statut", StatutContrat.ACTIF).setParameter("facturer", Boolean.TRUE).getResultList(); // .setMaxResults(1500)
		subs = mobileMoneyDAO.getEntityManager().createQuery(req).setParameter("statut", StatutContrat.ACTIF).setParameter("facturer", Boolean.TRUE).setParameter("dateDernCompta", getLastFacturationDate(date)).getResultList();
	//	subs = mobileMoneyDAO.getEntityManager().createQuery(req).setParameter("statut", StatutContrat.ACTIF).setParameter("_statut", StatutContrat.ACTIF_CBS).setParameter("facturer", Boolean.TRUE).setParameter("dateDernCompta", getLastFacturationDate(date)).getResultList();

		//logger.info("OK");
		//logger.info("Generation des eve et enregistrement");
		// Generation des evenements, transactions et ecritures a comptabiliser et enregistrement
		//mobileMoneyDAO.saveList(buildEventsForComptabilisation(subs, tva, date), true);
		logger.info("Total Subscriber: "+subs.size());
		
		List<bkeve> data = buildEventsForComptabilisation(subs, tva, date);
		mobileMoneyDAO.saveList(data, true);
		
		//logger.info("OK---"+evs.size());
		// Liberation des ressources
		mapComs.clear(); mapPeriods.clear(); subs.clear();

	}

	/*@Override
	@SuppressWarnings("unchecked")
	//@TransactionTimeout(value = 15000)
	@AllowedRole(name = "executerTFJO", displayName = "MoMo.Executer.TFJO")
	public List<Comptabilisation> executerTFJO2() throws Exception {

		// Initialisation de la liste a retourner
		List<Comptabilisation> compta = new ArrayList<Comptabilisation>();

		// Initialisation de la liste des abonnes a comptabiliser
		List<Subscriber> subs = new ArrayList<Subscriber>();

		// Lecture des parametres generaux
		// checkGlobalConfig(); //Parameters param = findParameters();

		// Recuperation de la liste des commissions
		Map<TypeOperation, Commissions> mapComs = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation");

		// Initialisation de la TVA
		Double tva = 19.25; 

		// Construction d'une Map de periodicites
		Map<Periodicite, Integer> mapPeriods = new HashMap<Periodicite, Integer>();
		mapPeriods.put(Periodicite.MOIS, 30); mapPeriods.put(Periodicite.TRIMESTRE, 90); mapPeriods.put(Periodicite.SEMESTRE, 180); mapPeriods.put(Periodicite.ANNUEL, 365);

		// Recuperation de la tva parametree sur les commissions PULL et PUSH
		tva = Math.max( mapComs.get(TypeOperation.PULL).getPeriodFacturation() != null ? mapComs.get(TypeOperation.PULL).getTauxTVA() : 0d, mapComs.get(TypeOperation.PUSH).getPeriodFacturation() != null ? mapComs.get(TypeOperation.PUSH).getTauxTVA() : 0d);

		// Initialisation de la requete de selection des abonnements a comptabiliser (date d'anniversaire)
		String req = "From Subscriber s where s.status = :statut and s.facturer = :facturer and s.commissions>0"; //  and (current_date - s.dateDernCompta) >= (case when s.period='MOIS' then 30 else ( case when s.period='TRIMESTRE' then 90 else ( case when s.period='SEMESTRE' then 180 else ( case when s.period='ANNUEL' then 365 else ( case when s.period='SEMAINE' then 7 else 1 end ) end ) end ) end ) end ) ";

		// Recuperation de la liste des abonnements a comptabiliser
		subs = mobileMoneyDAO.getEntityManager().createQuery(req).setParameter("statut", StatutContrat.ACTIF).setParameter("facturer", Boolean.TRUE).getResultList(); // .setMaxResults(1500)

		for(int i=subs.size()-1; i>=0; i--){
			if(isClientEmploye(subs.get(i).getCustomerId()) || subs.get(i).getAccounts() == null || subs.get(i).getAccounts().isEmpty() || MoMoHelper.getNbreJoursBetween(subs.get(i).getDateDernCompta(), new Date()) < getNbJrsByPeriod(subs.get(i).getPeriod()) ) subs.remove(i);
		}

		// Generation des evenements a comptabiliser
		mobileMoneyDAO.saveList(buildEventsForComptabilisation(subs, tva), true); 

		// Ajout des evenements a regulariser
		//compta.addAll( mobileMoneyDAO.filter(Comptabilisation.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("status", TransactionStatus.REGUL)), OrderContainer.getInstance().add(Order.desc("dateCompta")), null, 0, -1) );

		// Liberation des ressources
		mapComs.clear(); mapPeriods.clear(); 

		// Retourne la liste des comptabilisations
		return compta;

	}*/


	private List<bkeve> buildEventsForComptabilisation(List<Subscriber> subs, Double tva, Date dateCompta) throws Exception {
		// Initialisation de la liste a retourner
		List<bkeve> data = new ArrayList<bkeve>();

		// Recherche des parametres
		// checkGlobalConfig();
		params = findParameters();

		// Initialisation de DataStore d'Amplitude
		if(dsCBS == null) findCBSDataSystem();

		// Initialisations des ResultSets
		ResultSet rsCpteAbonne = null, rsCpteComs = null, rsCpteTVA = null, rsLiaisonAbonne = null, rsLiaisonComs = null, rsLiaisonTva = null;

		// Initialisations
		Long numEc = 1l;
		Date dco = getDateComptable(dsCBS);
		Date dvaDebit = getDvaDebit();

		//Long numEve = getLastEveNum(dsCBS);

		/**
		 * *******************************************************************
		 * RECUPERATION DES COMPTES DE COMMISSIONS ET DE TAXES DANS DELTA
		 * *******************************************************************
		 */

		// Si le compte des commissions a ete parametre
		if(params.getNumCompteCommissions() != null && !params.getNumCompteCommissions().isEmpty()) 

			// Recuperation du numero de cpte des commissions
			rsCpteComs = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(7).getQuery(), new Object[]{ params.getNumCompteCommissions().split("-")[0], params.getNumCompteCommissions().split("-")[1], params.getNumCompteCommissions().split("-")[2] });

		if(rsCpteComs != null) rsCpteComs.next();

		// Si le numero de cpte TVA a ete parametre
		if(params.getNumCompteTVA() != null && !params.getNumCompteTVA().isEmpty())

			// Recuperation du numero de compte TVA
			rsCpteTVA = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(7).getQuery(), new Object[]{ params.getNumCompteTVA().split("-")[0], params.getNumCompteTVA().split("-")[1], params.getNumCompteTVA().split("-")[2] });

		if(rsCpteTVA != null) rsCpteTVA.next();

		// Recuperation du compte de liaison l'agence des commissions
		rsLiaisonComs = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='" +params.getNumCompteCommissions().split("-")[0]+ "' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
		if(rsLiaisonComs != null) rsLiaisonComs.next();
		
		// Recuperation du compte de liaison de l'agence des taxes
		rsLiaisonTva = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='" +params.getNumCompteTVA().split("-")[0]+ "' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
		if(rsLiaisonTva != null) rsLiaisonTva.next();

		// Parcours de la liste des abonnes passee en parametre
//		logger.info("Data SUBS: "+subs.size());
//		List<Subscriber> _subs = subs.subList(0, 50);
		for(Subscriber s : subs){
			//logger.info("NCP SUBS: "+s.getFirstAccount());
			if(s.getFirstAccount()!= null && s.getFirstPhone()!= null){

				String datop = new SimpleDateFormat("ddMMyy").format(getNextFacturationDate(s.getDateDernCompta()));
				//Long numEve = getLastEveNum(dsCBS);
				Long numEve = (long) (new Random().nextInt(900000) + 100000);
				Double com = 0d;
				if(s.getProfil()!=null){
					com = s.getProfil().getCommissions();
					if(null==com) throw new Exception("Les commissions du profil "+s.getProfil().getProfilName()+" ne sont pas paramétrées. \n\nVeuillez le faire avant de continuer le traitement.");

				}else{
					com = s.getCommissions();
				}
				//logger.info("Compte : "+s.getFirstAccount()+" Phone : "+ s.getFirstPhone());
				// Initialisation de la transaction a comptabiliser
				Transaction tx = new Transaction(TypeOperation.COMPTABILISATION, s, 0d, s.getFirstAccount(), s.getFirstPhone() , new Date(), TransactionStatus.PROCESSING, com, Math.ceil( com * (1 + tva/100) ), dateCompta );

				// Initialisation de l'evenement a generer MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0")
				//bkeve eve = new bkeve(tx, params.getCodeOperation(), "000000", "001", tx.getAmount(), "VIRMAC", dco, params.getCodeUtil(), 1d, tx.getCommissions(), tx.getTaxes(), tx.getTtc());
				bkeve eve = new bkeve(tx, params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), "001", tx.getAmount(), "VIRMAC", dco, params.getCodeUtil(), 1d, tx.getCommissions(), tx.getTaxes(), tx.getTtc());
				//logger.info("Compte : "+tx.getAccount()+" Phone : "+ tx.getPhoneNumber());
				// Recherche du cpte de l'abonne
				rsCpteAbonne = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(6).getQuery(), new Object[]{ tx.getAccount().split("-")[0], tx.getAccount().split("-")[1], tx.getAccount().split("-")[2] });

				// Si le cpte de l'abonne existe dans DELTA
				if(rsCpteAbonne != null && rsCpteAbonne.next()) {

					if(rsCpteAbonne.getString("ife").equals("N") && rsCpteAbonne.getString("cfe").equals("N") && rsCpteAbonne.getString("cha").startsWith("37")){

						// Ajout du debiteur
						eve.setDebiteur(rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), rsCpteAbonne.getString("ncp"), rsCpteAbonne.getString("suf"), rsCpteAbonne.getString("clc"), rsCpteAbonne.getString("cli"), s.getCustomerName(), " ", tx.getCommissions(), tx.getCommissions(), dvaDebit, rsCpteAbonne.getDouble("sde"));

						// Ajout du debiteur
						eve.setCrediteur(rsCpteComs.getString("age"), rsCpteComs.getString("dev"), rsCpteComs.getString("ncp"), rsCpteComs.getString("suf"), rsCpteComs.getString("clc"), rsCpteComs.getString("cli"), rsCpteComs.getString("inti"), "   ", tx.getCommissions(), tx.getCommissions(), dvaDebit, rsCpteComs.getDouble("sde"));

						// Libelle de l'evenement
						eve.setLib1(datop + "/" + tx.getTypeOperation().toString().substring(0, 5) + "/MAC/" + tx.getPhoneNumber());


						/***-**************************************
						 * *** GENERATION DES ECRITURES COMPTABLES
						 * *****************************************
						 */

						// Debit du cpte client du montant valeurCom + valeurTVA
						eve.getEcritures().add( new bkmvti(rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), rsCpteAbonne.getString("cha"), rsCpteAbonne.getString("ncp"), rsCpteAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteAbonne.getString("clc"), dco, null, rsCpteAbonne.getDate("dva"), tx.getTtc(), "D", "FRAIS MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()) , "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteAbonne.getString("age"), rsCpteAbonne.getString("dev"), tx.getTtc(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

						// Recuperation du compte de liaison de l'agence du client
						rsLiaisonAbonne = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='"+ rsCpteAbonne.getString("age") +"' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);

						// Si le compte de liaison existe
						if(rsLiaisonAbonne != null) rsLiaisonAbonne.next();
		
//						COMMENTE
//						// Si l'agence du compte n'est pas Hippodrome
//						if(!rsCpteAbonne.getString("age").equals("00001")){
//
//							// Recuperation du compte de liaison de l'agence du client
//							rsLiaisonAbonne = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='"+ rsCpteAbonne.getString("age") +"' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
//
//							// Si le compte de liaison existe
//							if(rsLiaisonAbonne != null && rsLiaisonAbonne.next() ) {
//
//								// Passage de l'ecriture dans les liaisons
//								eve.getEcritures().add( new bkmvti(rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), rsLiaisonAbonne.getString("cha"), rsLiaisonAbonne.getString("ncp"), rsLiaisonAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.cpteLiaisonAbonne.getClc(), dco, null, cpteLiaisonAbonne.getDva()ate("dva"), tx.getTtc(), "C", "LS MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), tx.getTtc(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
//								eve.getEcritures().add( new bkmvti(rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), rsLiaisonComs.getString("cha"), rsLiaisonComs.getString("ncp"), rsLiaisonComs.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonComs.getString("clc"), dco, null, rsLiaisonComs.getDate("dva"), tx.getTtc(), "D", "LS MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), tx.getTtc(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
//
//							}
//
//						}
						
						
						// Comptes de debit (client) et credit (commissions) dans des agences differentes
						if( !StringUtils.equalsIgnoreCase(rsLiaisonAbonne.getString("age"), rsLiaisonComs.getString("age"))){
							
							// Credit de la liaison du client du montant des commissions
							if(rsLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), rsLiaisonAbonne.getString("cha"), rsLiaisonAbonne.getString("ncp"), rsLiaisonAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonAbonne.getString("clc"), dco, null, rsLiaisonAbonne.getDate("dva"), tx.getCommissions(), "C", "LS COM MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), tx.getCommissions(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
							// Debit de la liaison des commissions
							if(rsLiaisonComs != null) eve.getEcritures().add( new bkmvti(rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), rsLiaisonComs.getString("cha"), rsLiaisonComs.getString("ncp"), rsLiaisonComs.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonComs.getString("clc"), dco, null, rsLiaisonComs.getDate("dva"), tx.getCommissions(), "D", "LS COM MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), tx.getCommissions(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
							
						}
						// Comptes de debit (client) et credit (taxes) dans des agences differentes
						if( !StringUtils.equalsIgnoreCase(rsLiaisonAbonne.getString("age"), rsLiaisonTva.getString("age"))){
							
							// Debit de la liaison du client du montant de la taxe
							if(rsLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), rsLiaisonAbonne.getString("cha"), rsLiaisonAbonne.getString("ncp"), rsLiaisonAbonne.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonAbonne.getString("clc"), dco, null, rsLiaisonAbonne.getDate("dva"), tx.getTaxes(), "C", "LS TAX MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("dev"), tx.getTaxes(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
							// Credit de la liaison des taxes
							if(rsLiaisonTva != null) eve.getEcritures().add( new bkmvti(rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), rsLiaisonTva.getString("cha"), rsLiaisonTva.getString("ncp"), rsLiaisonTva.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonTva.getString("clc"), dco, null, rsLiaisonTva.getDate("dva"), tx.getTaxes(), "D", "LS TAX MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), tx.getTaxes(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
							
						}
						

						// Credit cpte Comissions de valeurCom
						eve.getEcritures().add( new bkmvti(rsCpteComs.getString("age"), rsCpteComs.getString("dev"), rsCpteComs.getString("cha"), rsCpteComs.getString("ncp"), rsCpteComs.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteComs.getString("clc"), dco, null, rsCpteComs.getDate("dva"), tx.getCommissions(), "C", "COM MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteComs.getString("age"), rsCpteComs.getString("dev"), tx.getCommissions(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;

						// Credit cpte TVA de valeurTVA
						eve.getEcritures().add( new bkmvti(rsCpteTVA.getString("age"), rsCpteTVA.getString("dev"), rsCpteTVA.getString("cha"), rsCpteTVA.getString("ncp"), rsCpteTVA.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteTVA.getString("clc"), dco, null, rsCpteTVA.getDate("dva"), tx.getTaxes(), "C", "TAX MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteTVA.getString("age"), rsCpteTVA.getString("dev"), tx.getTaxes(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
					}


				}

				// Ajout de l'element a comptabiliser
				eve.setId(now());
				data.add(eve);
				if(rsCpteAbonne != null) {
					rsCpteAbonne.close(); 
					if(rsCpteAbonne.getStatement() != null) {
						rsCpteAbonne.getStatement().close();
					}
				}
				if(rsLiaisonAbonne != null) {
					rsLiaisonAbonne.close(); 
					if(rsLiaisonAbonne.getStatement() != null) {
						rsLiaisonAbonne.getStatement().close();
					}
				}
				
			}
		}


		// On libere les variables
		
		if(rsCpteComs != null) {
			rsCpteComs.close(); 
			if(rsCpteComs.getStatement() != null) {
				rsCpteComs.getStatement().close();
			}
		}
		if(rsCpteTVA != null) {
			rsCpteTVA.close(); 
			if(rsCpteTVA.getStatement() != null) {
				rsCpteTVA.getStatement().close();
			}
		}
		if(rsLiaisonComs != null) {
			rsLiaisonComs.close(); 
			if(rsLiaisonComs.getStatement() != null) {
				rsLiaisonComs.getStatement().close();
			}
		}
		if(rsLiaisonTva != null) {
			rsLiaisonTva.close(); 
			if(rsLiaisonTva.getStatement() != null) {
				rsLiaisonTva.getStatement().close();
			}
		}
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// retourner le liste des evenements à comptabiliser
		//logger.info("Data EVE: "+data.size());
		
		return data;

	}
	

	/**
	 * chargerDonneesComptabiliserTFJO
	 * @return liste des transactions a comptabiliser
	 * @throws Exception
	 */
	public List<Transaction> chargerDonneesComptabiliserTFJO(Date date) throws Exception{
		return mobileMoneyDAO.filter(Transaction.class, AliasesContainer.getInstance().add("subscriber", "s"), RestrictionsContainer.getInstance().add(Restrictions.eq("typeOperation",TypeOperation.COMPTABILISATION)).add(Restrictions.eq("status",TransactionStatus.PROCESSING)), OrderContainer.getInstance().add(Order.asc("s.dateDernCompta")), null, 0, -1);
	}


	/**
	 * chargerDonneesComptabiliserRegul
	 * @return liste des transactions a regulariser
	 * @throws Exception
	 *//*
	public List<Transaction> chargerDonneesComptabiliserRegul() throws Exception{

		Map<Long,Transaction> mapTrans = new HashMap<Long,Transaction>();

		// Initialisation de DataStore d'Amplitude
		//if(dsCBS == null) findCBSDataSystem();

		// Marqueur du mode nuit
		boolean nuit = isModeNuit();
		// Log
		logger.info("Recherche des reguls à comptabiliser!");
		List<Transaction> list = new ArrayList<Transaction>(); 
		List<Transaction> values = mobileMoneyDAO.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("typeOperation",TypeOperation.COMPTABILISATION)).add(Restrictions.eq("status", TransactionStatus.REGUL)).add(Restrictions.or(Restrictions.isNull("dateControle"), Restrictions.lt("dateControle", new Date()))), OrderContainer.getInstance().add(Order.desc("dateCompta")), null, 0, 50);
		logger.info("Fin de la recherche des reguls");
		if(values.isEmpty()) return list;
		for(Transaction t : values) mapTrans.put(t.getId(),t);
		logger.info("Recuperation des evenements");
		// Parcours de la liste des abonnements a comptabiliser 
		List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, null, RestrictionsContainer.getInstance().add(Restrictions.in("transaction",values)), null, null, 0, -1);
		logger.info("Fin de la recuperation des evenements");
		//Long numEve = getLastEveNum(dsCBS);
		logger.info("Parcours des evenements");
		// Parcours de la liste des abonnements a comptabiliser
		for(bkeve eve : eves) {
			// MAJ des numeros d'evenements de numero 000000
			if(eve.getEve().equals("000000")){
				eve.setEve(MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"));
				for(bkmvti mvt : eve.getEcritures()){
					if(mvt.getLib().contains("HIP")) mvt.getLib().replaceFirst(" HIP", " LS");
					if(mvt.getEve().equals("000000")) mvt.setEve(eve.getEve());
				}
				// MAJ de l'evenement modifie et de ses ecritures
				mobileMoneyDAO.getEntityManager().merge(eve);
			}

			Transaction tx = mapTrans.get(eve.getTransaction().getId());
			tx.setDateControle(new Date());
			if(tx.getSubscriber().getFirstAccount() != null){
				logger.info("Verification du statut du compte ");
				if(!isCompteFerme(tx.getSubscriber().getFirstAccount())){
					logger.info("Compte non ferme ");
					// Si l'objet courant a ete selectionne par l'utilisateur et ses ecritures sont equilibrees
					if(eve.isEquilibre()){ //if(c.isSelected() && c.getEve().isEquilibre()){
						logger.info("Verification du solde ");
						logger.info("Solde = "+getSolde(tx.getSubscriber().getFirstAccount(), nuit));
						// Si le solde du compte est suffisant
						if(getSolde(tx.getSubscriber().getFirstAccount(), nuit) > tx.getTtc()){
							logger.info(" OK ");
							logger.info("Ajout de l'evenement "+eve.getEve());
							list.add(tx);
						}else logger.info(" NOK ");
					}

				}else{
					logger.info("Compte ferme : Annuler la transaction de facturation");
					tx.setStatus(TransactionStatus.CLOSE);
					Subscriber s = tx.getSubscriber();
					s.setStatus(StatutContrat.SUSPENDU);
					s.setUtiSuspendu("AUTO");
					s.setDateSuspendu(new Date());
					mobileMoneyDAO.getEntityManager().merge(s);
					mobileMoneyDAO.getEntityManager().merge(tx);
				}

			} //else tx.setDateControle(new Date());

		}
		logger.info("Fin Parcours des evenements");
		values.clear();
		eves.clear();
		return list;
	}*/


	/**
	 * chargerDonneesComptabiliserRegul
	 * @return liste des transactions a regulariser
	 * @throws Exception
	 */
	public List<Transaction> chargerDonneesComptabiliserRegul() throws Exception{

		Map<Long,Transaction> mapTrans = new HashMap<Long,Transaction>();
		// age-ncp-clc, obj
		Map<String,AccountUtils> mapComptes = new HashMap<String,AccountUtils>();
		// age-ncp-clc, solde (sin)
		Map<String,Double> mapSoldes = new HashMap<String,Double>();

		List<AccountUtils> lau = new ArrayList<AccountUtils>();
		
		params = findParameters();

		// Initialisation de DataStore d'Amplitude
		//if(dsCBS == null) findCBSDataSystem();

		// Marqueur du mode nuit
//		boolean nuit = isModeNuit();
		// Log
		//logger.info("Recherche des reguls a comptabiliser!");
		List<Transaction> list = new ArrayList<Transaction>(); 
		List<Transaction> toRemove = new ArrayList<Transaction>(); 
		List<String> ncps = new ArrayList<String>(); // Restrictions.eq("typeOperation",TypeOperation.COMPTABILISATION))
		List<Transaction> values = mobileMoneyDAO.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.in("typeOperation", new TypeOperation[]{TypeOperation.SUBSCRIPTION, TypeOperation.COMPTABILISATION} )).add(Restrictions.eq("status", TransactionStatus.REGUL)).add(Restrictions.or(Restrictions.isNull("dateControle"), Restrictions.lt("dateControle", new Date()))), OrderContainer.getInstance().add(Order.desc("dateCompta")), null, 0, -1);
		//logger.info("Fin de la recherche des reguls");
		if(values.isEmpty()) return list;
		//for(Transaction t : values) mapTrans.put(t.getId(),t);
		//logger.info("Traitement et recuperation de soldes");
		String sql = "";
		boolean first = false;
		int i = 0;
		// Recuperation des soldes
		for(Transaction t : values){
			// Numero de compte
			String age = t.getAccount().split("-")[0]; //t.getAccount().trim().substring(0, 5);
			String ncp = t.getAccount().split("-")[1]; //t.getAccount().trim().substring(5, 16);
			//logger.info("age : "+age+" ncp : "+ncp);
			//			mapTrans.put(t.getId(),t);
			//			if(!mapSoldes.containsKey(t.getAccount())) {
			//				mapSoldes.put(t.getAccount(), 0d);
			//				lbacc.add(new BankAccount(t.getAccount().split("-")[0] , t.getAccount().split("-")[1], t.getAccount().split("-")[2]));
			//				i++;
			//			}
			//		 	
			if(i==1000){
				//logger.info(i+" trx");
				List<AccountUtils> tmp = getMapSoldes(sql);
				lau.addAll(tmp);
				for(AccountUtils au : tmp) {
					//logger.info(au.getAge()+"-"+au.getNcp()+"-"+au.getCle()+" : "+ au.getSin());
					mapComptes.put(au.getAge()+"-"+au.getNcp()+"-"+au.getCle(), au);
				}
				sql = "";
				first = false;
				i = 0;
			}

			if(first == true ){
				sql = sql + " or ";
			}
			if(sql.trim().isEmpty()){
				sql = sql + " (age='"+age+"' and ncp='"+ncp+"') ";   
				first = true;
			}else{
				sql = sql + " (age='"+age+"' and ncp='"+ncp+"') ";
			}

			i++;
		}

		if(!sql.trim().isEmpty()){
			List<AccountUtils> tmp = getMapSoldes(sql);
			lau.addAll(tmp);
			for(AccountUtils au : tmp) {
				//logger.info(au.getAge()+"-"+au.getNcp()+"-"+au.getCle()+" : "+ au.getSin());
				mapComptes.put(au.getAge()+"-"+au.getNcp()+"-"+au.getCle(), au);
			}
		}
		////logger.info("Nbre comptes : "+ mapComptes.size());
		//logger.info("Fin Traitement et recuperation de soldes");

		//logger.info("Elimination des comptes fermes ou avec solde insuffisant et des transactions correspondantes");		
		// Elimination des comptes fermes ou avec solde insuffisant et des transactions correspondantes
		for(String ncp : mapComptes.keySet()){
			AccountUtils au = mapComptes.get(ncp);
			// Si compte non ferme et solde suffisant et pas d'opposition
			if(au.getCfe().equals("N") && au.getIfe().equals("N") && au.getSin()>=597 && !isCompteEnOpposition(ncp.split("-")[0], ncp.split("-")[1], params.getCodeOperation())){
				//logger.info("OK");
				// Ajout des transactions dans la liste
				//for(Transaction t : values){
				for(Iterator<Transaction> it = values.iterator(); it.hasNext();){
					Transaction t = it.next();
					if(t.getAccount().equals(ncp)){
						Double sin = 0d;
						sin = mapSoldes.get(ncp);
						if(sin == null){
							sin = 0d;
							sin = au.getSin();
						}
						// Calcul nouveau solde
						Double ttc = t.getTtc();
						Double fees = sin - ttc;
						if(mapSoldes.containsKey(ncp)){
							ncps.add(ncp); //mapSoldes.remove(ncp);
						}
						// Maj du solde
						mapSoldes.put(ncp, fees);
						// Si solde insuffisant
						if(sin < ttc){
							// Suppression de la liste des comptes/soldes
							ncps.add(ncp); //mapComptes.remove(ncp);
							// Suppression de la transaction de la liste
							toRemove.add(t); //values.remove(t);
						}else if(sin >= ttc){
							// Ajout de la transaction a la map
							list.add(t);
							mapTrans.put(t.getId(),t);
						}

					}

				}
			}
			// Si compte ferme ou en opposition ou solde insuffisant
			else{
				//logger.info("NOK");
				// Suppression de la liste des comptes/soldes
				ncps.add(ncp); //mapComptes.remove(ncp);
				// Suppression des transactions de la liste
				//for(Transaction t : values){
				for(Iterator<Transaction> it = values.iterator(); it.hasNext();){
					Transaction t = it.next();
					if(t.getAccount().equals(ncp)) {
						toRemove.add(t); //values.remove(t);
						if(!au.getCfe().equals("N") || !au.getIfe().equals("N")){
							//logger.info("Compte ferme : Annuler la transaction de facturation et suspendre l'utilisateur");
							t.setStatus(TransactionStatus.CLOSE);
							Subscriber s = t.getSubscriber();
							s.setStatus(StatutContrat.SUSPENDU);
							s.setUtiSuspendu("AUTO");
							s.setDateSuspendu(new Date());
							mobileMoneyDAO.update(s);
							mobileMoneyDAO.update(t);
//							mobileMoneyDAO.getEntityManager().merge(s);
//							mobileMoneyDAO.getEntityManager().merge(t);
						}
					}
				}
			}
		}
		//logger.info("Retrait des trx");
		// Retrait des transactions correspondantes aux comptes fermes ou avec solde insuffisant
		values.removeAll(toRemove);
		for(Transaction trx : toRemove){
			trx.setDateControle(new Date());
		}
		mobileMoneyDAO.saveList(toRemove, true);

		//logger.info("Retrait des comptes");
		// Retrait des comptes fermes ou avec solde insuffisant
		for(String ncp : ncps){
			if(mapSoldes.containsKey(ncp)){
				mapSoldes.remove(ncp);
			}
		}

		//		logger.info("Recuperation des evenements");
		//		// Parcours de la liste des abonnements a comptabiliser 
		//		List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, null, RestrictionsContainer.getInstance().add(Restrictions.in("transaction",values)), null, null, 0, -1);
		//		logger.info("Fin de la recuperation des evenements");
		//		//Long numEve = getLastEveNum(dsCBS);
		//		logger.info("Parcours des evenements");
		//		// Parcours de la liste des abonnements a comptabiliser
		//		for(bkeve eve : eves) {
		//			
		//			Transaction tx = mapTrans.get(eve.getTransaction().getId());
		//			tx.setDateControle(new Date());
		//			if(tx.getSubscriber().getFirstAccount() != null){
		//				// Si l'objet courant a ete selectionne par l'utilisateur et ses ecritures sont equilibrees
		//				if(eve.isEquilibre()){ //if(c.isSelected() && c.getEve().isEquilibre()){
		//					logger.info("Ajout de l'evenement "+eve.getEve());
		//					list.add(tx);
		//				}
		//			}
		//			
		//		}
		//		logger.info("Fin Parcours des evenements");
		//		eves.clear();
		mapTrans.clear();
		mapComptes.clear();
		mapSoldes.clear();
		toRemove.clear();
		ncps.clear();
		lau.clear();
		values.clear();
		//logger.info("NBRE TRX : "+list.size());
		return list;
	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#vaiderTFJO(java.util.List)
	 */
	@Override
	@AllowedRole(name = "validerTFJO", displayName = "MoMo.Valider.TFJO")
	@TransactionTimeout(value = 160000)
	public void validerTFJO(List<Comptabilisation> data, String user) throws Exception {

		// Recuperation de la DataSource du Core Banking
		if(dsCBS == null) findCBSDataSystem();
		Date dco = getDateComptable(dsCBS);

		// Marqueur du mode nuit
		boolean nuit = isModeNuit();

		// Parcours de la liste des abonnements a comptabiliser
		for(Comptabilisation c : data) {
			/*bkeve eve = mobileMoneyDAO.findByPrimaryKey(bkeve.class,c.getEve().getId(),null); 
			Subscriber sub = mobileMoneyDAO.findByPrimaryKey(Subscriber.class,c.getSubscriber().getId(),null);*/
			// Si l'objet courant a ete selectionne par l'utilisateur et ses ecritures sont equilibrees
			if(c.getEve().isEquilibre()) { //if(c.isSelected() && c.getEve().isEquilibre()) {

				//String datop = new SimpleDateFormat("ddMMyy").format(c.getDateCompta());
				// Si le solde du compte est suffisant
				if(getSolde(c.getSubscriber().getFirstAccount(), nuit) > c.getEve().getTransaction().getTtc()){

					// Parcours des ecritures comptables
					for(bkmvti mvt : c.getEve().getEcritures()) {

						// MAJ des parametres des EC generes
						mvt.setUti(user);
						mvt.setDco(dco);
						if(c.getStatus().equals(TransactionStatus.REGUL)){
							/**Subscriber s = c.getSubscriber();
							String phoneNumber = s.getFirstPhone();
							String lib = "FRAIS  RGUL/" + datop + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()) + "/" + (phoneNumber != null ? phoneNumber.replaceAll("237", "") : phoneNumber);
							 */
							mvt.setLib( mvt.getLib().replaceFirst(" MAC/", " RGUL") );
						}

						reduitSolde(c.getSubscriber().getFirstAccount(), c.getEve().getTransaction().getTtc(), nuit);

					}

					// MAJ du statut de la transaction
					c.getEve().getTransaction().setStatus(TransactionStatus.SUCCESS);
					c.getEve().getTransaction().setPosted(Boolean.TRUE);
					c.setStatus(TransactionStatus.SUCCESS);
					c.getSubscriber().setDateDernCompta(new Date());

					// Si le solde du compte est insuffisant
				}else {

					// On positionne le status de l'operation en REGUL
					c.setStatus(TransactionStatus.REGUL);
				}

				// Sauvegarde l'evenement
				//mobileMoneyDAO.getEntityManager().merge(c);
			}
		}

		// Initialisation de la liste des EC a poster
		List<bkmvti> mvts = new ArrayList<bkmvti>();

		// Parcours de la liste des abonnements a comptabiliser
		for(int i=data.size()-1; i>=0; i--){
			/*bkeve eve = mobileMoneyDAO.findByPrimaryKey(bkeve.class,data.get(i).getEve().getId(),null); 
			Subscriber sub = mobileMoneyDAO.findByPrimaryKey(Subscriber.class,data.get(i).getSubscriber().getId(),null);*/
			// Si l'abonnement courant est non selectionne ou non equilibre on l'enleve
			if(!data.get(i).getEve().isEquilibre()) data.remove(i); //if(!data.get(i).isSelected() || !data.get(i).getEve().isEquilibre()) data.remove(i);

			// Chargement de la liste des EC a poster
			else if(!data.get(i).getStatus().equals(TransactionStatus.REGUL)) mvts.addAll(data.get(i).getEve().getEcritures());
		}

		// MAJ de la liste des abonnements a comptabiliser
		//mergeCompta(data);


		// Ouverture d'une cnx vers la BD du Core Banking
		if(conCBS == null || conCBS.isClosed()) conCBS = getSystemConnection(dsCBS);

		// Suspension temporaire du mode blocage dans la BD du Core Banking
		if(dsCBS.getDbConnectionString().indexOf("informix") > 0) conCBS.createStatement().executeUpdate("SET ISOLATION TO DIRTY READ");

		// Desactivation du mode AUTO COMMIT
		conCBS.setAutoCommit(false);

		// Initialisation d'un preparateur de requetes
		PreparedStatement ps = conCBS.prepareStatement(new bkmvti().getSaveQuery());

		// Parcours de la liste des EC a poster
		for(bkmvti m : mvts) {

			// Ajout dans le lot
			ps = m.addPrepareStatement(ps);

			// Ajout du Lot i
			ps.addBatch();
		}

		// Lancement de l'execution du Lot de requetes sur le serveur DELTA
		ps.executeBatch();

		// Commit
		conCBS.setAutoCommit(true);

		// Fermeture de la cnx preparee
		ps.close(); ps = null;
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		// Liberation des ressources
		data.clear(); mvts.clear();
	}
	
	

		
//	/*
//	 * (non-Javadoc)
//	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#vaiderTFJO(java.util.List)
//	 */
//	@Override
//	@AllowedRole(name = "validerTFJO", displayName = "MoMo.Valider.TFJO")
//	//@TransactionTimeout(value = 160000)
//	public void validerTFJO(List<Transaction> data, String user, int year, int month) throws Exception {
//
//		// Recuperation de la DataSource du Core Banking
//		if(dsCBS == null) findCBSDataSystem();
//		Date dco = getDateComptable(dsCBS);
//		//Long numEve = getLastEveNum(dsCBS);
//		Date dvaDebit = getDvaDebit();
//		Date dvaCredit = getDvaCredit();
//
//		Map<Long,Transaction> mapTrans = new HashMap<Long,Transaction>();
//		for(Transaction t : data) mapTrans.put(t.getId(),t);
//
//		// Marqueur du mode nuit
//		boolean nuit = isModeNuit();
//
//		// Initialisation de la liste des EC a poster
//		//		List<bkmvti> mvts = new ArrayList<bkmvti>(); // Ajoute
//		List<Subscriber> subs = new ArrayList<Subscriber>();
//		//logger.info("Recuperation des evenements");
//		// Parcours de la liste des abonnements a comptabiliser
//		//List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, null, RestrictionsContainer.getInstance().add(Restrictions.in("transaction",data)), null, null, 0, -1);
//		List<bkeve> eves =  new ArrayList<bkeve>();
//		int max = 5000;
//		int end = (data.size()/max);
//		//logger.info("DATA SIZE : "+data.size());
//		//logger.info("END : "+end);
//		for(int i = 0; i <= end; i++){
//			// Parcours de la liste des abonnements a comptabiliser
//			if(i==end){
//				if(max*i!=data.size()){
//					eves.addAll(mobileMoneyDAO.filter(bkeve.class, null, 
//							RestrictionsContainer.getInstance().add(Restrictions.in("transaction", new ArrayList<Transaction>(data.subList(max*i, data.size())))), 
//							null, null, 0, -1));
//					//logger.info(data.size()+" EVES");
//				}
//			}
//			else {
//				eves.addAll(mobileMoneyDAO.filter(bkeve.class, null, 
//						RestrictionsContainer.getInstance().add(Restrictions.in("transaction", new ArrayList<Transaction>(data.subList(max*i, (max*(i+1)))))), 
//						null, null, 0, -1));
//				//logger.info(max*(i+1)+" EVES");
//			}
//
//		}
//		//		
//		//logger.info("Fin de la recuperation des evenements");
//
//		data.clear();
//		data = new ArrayList<Transaction>();
//		// Ouverture d'une cnx vers la BD du Core Banking
//		if(conCBS == null || conCBS.isClosed()) conCBS = getSystemConnection(dsCBS);
//
//		// Suspension temporaire du mode blocage dans la BD du Core Banking
//		if(dsCBS.getDbConnectionString().indexOf("informix") > 0) conCBS.createStatement().executeUpdate("SET ISOLATION TO DIRTY READ");
//
//		// Desactivation du mode AUTO COMMIT
//		conCBS.setAutoCommit(false);
//
//
//		// Initialisation d'un preparateur de requetes pour les solde
//		String req = "update bkcom set sin = sin - ?  where age=? and ncp=? and clc=?";
//		PreparedStatement psAmt = conCBS.prepareStatement(req);
//		// Initialisation d'un preparateur de requetes pour les ecritures
//		PreparedStatement ps = conCBS.prepareStatement(new bkmvti().getSaveQuery());
//		//logger.info("Parcours des evenements");
//
//		for(bkeve eve : eves) {
//
//			Transaction tx = mapTrans.get(eve.getTransaction().getId());
//			//Long numEve = getLastEveNum(dsCBS);
//			Long numEve = (long) (new Random().nextInt(900000) + 100000);
//
//			// Si l'objet courant a ete selectionne par l'utilisateur et ses ecritures sont equilibrees
//			if(eve.isEquilibre()) { //if(c.isSelected() && c.getEve().isEquilibre()) {
//
//				if(tx.getStatus().equals(TransactionStatus.REGUL)){
//					if(eve.getEve().equals("000000")) eve.setEve(MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"));
//
//					// Parcours des ecritures comptables
//					for(bkmvti mvt : eve.getEcritures()) {
//
//						// MAJ des parametres des EC generes
//						mvt.setUti(user);
//						mvt.setDco(dco);
//						//logger.info("MAJ des EC de regul!");
//						/**Subscriber s = c.getSubscriber();
//						String phoneNumber = s.getFirstPhone();
//						String lib = "FRAIS  RGUL/" + datop + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()) + "/" + (phoneNumber != null ? phoneNumber.replaceAll("237", "") : phoneNumber);
//						 */
//						mvt.setLib( mvt.getLib().replaceFirst(" MAC", " REGUL") );
//						if(mvt.getLib().contains("ABO")) mvt.setLib(mvt.getLib().replaceFirst("ABO", "FRAIS"));
//						if(mvt.getLib().contains("HIP")) mvt.setLib(mvt.getLib().replaceFirst("HIP", "LS"));
//						//logger.info("Ecritures : "+mvt.getLib());
//						// Mise a jour des anciens libeles
//						if(mvt.getLib().contains("/")){
//							// Mise a jour libele 2018/11/16
//							String[] lib = mvt.getLib().split("/");
//							//if(lib.length<4) mvt.setLib(lib[0]+" "+lib[3]+" "+lib[1]);
//							mvt.setLib(lib[0]+" "+lib[2]+" "+lib[1]);
//							//logger.info("NEW REGUL LIB : "+mvt.getLib());
//						}
//
//						if(mvt.getSen().equals("D")){
//							mvt.setDva(dvaDebit);
//							mvt.setDin(new Date());
//						}
//
//						if(mvt.getSen().equals("C")){
//							mvt.setDva(dvaCredit);
//							mvt.setDin(new Date());
//						}
//						if(mvt.getEve().equals("000000")) mvt.setEve(eve.getEve());				
//					}
//					//logger.info("MAJ du solde et du statut de la trx");
//					// Reduction du solde du client
//					String numCompte = tx.getSubscriber().getFirstAccount();
//					psAmt.setDouble(1, tx.getTtc());
//					psAmt.setString(2, numCompte.split("-")[0]);
//					psAmt.setString(3, numCompte.split("-")[1]);
//					psAmt.setString(4, numCompte.split("-")[2]);
//					psAmt.addBatch();
//					//reduitSolde(tx.getSubscriber().getFirstAccount(), tx.getTtc(), nuit);
//					// MAJ du statut de la transaction
//					tx.setStatus(TransactionStatus.SUCCESS);
//					tx.setPosted(Boolean.TRUE);
//					tx.setDateTraitement(new Date());
//				}
//				else{
//					// Si le solde du compte est suffisant
//					if(getSolde(tx.getSubscriber().getFirstAccount(), nuit) > tx.getTtc()){
//
//						if(eve.getEve().equals("000000")) eve.setEve(MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"));
//
//						// Parcours des ecritures comptables
//						for(bkmvti mvt : eve.getEcritures()) {
//
//							// MAJ des parametres des EC generes
//							mvt.setUti(user);
//							mvt.setDco(dco);
//							if(mvt.getEve().equals("000000")) mvt.setEve(eve.getEve());
//
//						}
//						//logger.info("MAJ du solde et du statut de la trx");
//						// Reduction du solde du client
//						String numCompte = tx.getSubscriber().getFirstAccount();
//						psAmt.setDouble(1, tx.getTtc());
//						psAmt.setString(2, numCompte.split("-")[0]);
//						psAmt.setString(3, numCompte.split("-")[1]);
//						psAmt.setString(4, numCompte.split("-")[2]);
//						psAmt.addBatch();
//						//reduitSolde(tx.getSubscriber().getFirstAccount(), tx.getTtc(), nuit);
//						// MAJ du statut de la transaction
//						tx.setStatus(TransactionStatus.SUCCESS);
//						tx.setPosted(Boolean.TRUE);
//						tx.setDateTraitement(new Date());
//
//						//mobileMoneyDAO.updateSubscriber(s);
//
//					}else {
//						//logger.info("Solde insufisant : Envoi en regul");
//						// On positionne le status de l'operation en REGUL
//						tx.setDateTraitement(new Date());
//						tx.setStatus(TransactionStatus.REGUL);
//
//					}
//				}
//
//				Subscriber s = tx.getSubscriber();
//				s.setDateSaveDernCompta(s.getDateDernCompta());
//				s.setDateDernCompta(getNextFacturationDate(s.getDateDernCompta()));
//				subs.add(s);
//				data.add(tx);
//
//				// Chargement de la liste des EC a poster
//				//				if(!tx.getStatus().equals(TransactionStatus.REGUL)) mvts.addAll(eve.getEcritures()); // Ajoute
//				if(tx.getStatus().equals(TransactionStatus.SUCCESS)) {
//					//logger.info("Ecritures comptables ajoutes\n");
//					//					mvts.addAll(eve.getEcritures()); // Ajoute
//					//logger.info("Parcours des EC de la TRX et ajout au batch");
//					// Parcours de la liste des EC de la trx
//					for(bkmvti m : eve.getEcritures()) {
//
//						// Ajout dans le lot
//						ps = m.addPrepareStatement(ps);
//
//						// Ajout du Lot des EC de la trx
//						ps.addBatch();
//
//						//						ps.execute();
//
//					}
//				}
//
//				// MAJ des evenements, transactions, abonnements a comptabiliser et ecritures par cascade
//				//mobileMoneyDAO.updateTransaction(tx);
//
//			}
//
//		}
//
//		psAmt.executeBatch();
//
//		//		// Initialisation d'un preparateur de requetes
//		//		PreparedStatement ps = conCBS.prepareStatement(new bkmvti().getSaveQuery());
//		//		logger.info("Parcours des EC et ajout au batch");
//		//		// Parcours de la liste des EC a poster
//		//		for(bkmvti m : mvts) {
//		//
//		//			// Ajout dans le lot
//		//			ps = m.addPrepareStatement(ps);
//		//
//		//			// Ajout du Lot i
//		//			ps.addBatch();
//		//
//		//		}
//		//		logger.info("FIN");
//		//logger.info("Execution du Batch");
//		// Lancement de l'execution du Lot de requetes sur le serveur DELTA
//		ps.executeBatch();
//		//logger.info("OK");
//		// Commit
//		conCBS.setAutoCommit(true);
//
//		// Fermeture de la cnx preparee
//		ps.close(); ps = null; psAmt.close(); psAmt = null;
//
//		//processClose(subs, data);
//		//logger.info("MAJ des abonnes et des trx");
//		//processupdateTFJO(subs, data);
//		mobileMoneyDAO.updateSubscriber(subs);
//		mobileMoneyDAO.updateTransaction(data);
//		//logger.info("OK");
//		// Liberation des ressources
//		//		mvts.clear();
//		mapTrans.clear();
//		data.clear();
//		subs.clear();
//		eves.clear();
//
//	}
	

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#vaiderTFJO(java.util.List)
	 */
	@Override
	@AllowedRole(name = "validerTFJO", displayName = "MoMo.Valider.TFJO")
	//@TransactionTimeout(value = 160000)
	public FactMonth validerTFJO(List<Transaction> data, String user, int year, int month) throws Exception {

		// Recuperation de la DataSource du Core Banking
		if(dsCBS == null) findCBSDataSystem();
		
		// ADD
		details = new ArrayList<FactMonthDetails>();
		mntD = 0d; mntC = 0d; nbrC = 0; nbrD = 0;
		// FIN ADD
		
		Date dco = getDateComptable(dsCBS);
		//Long numEve = getLastEveNum(dsCBS);
		Date dvaDebit = getDvaDebit();
		Date dvaCredit = getDvaCredit();

		Map<Long,Transaction> mapTrans = new HashMap<Long,Transaction>();
		for(Transaction t : data) mapTrans.put(t.getId(),t);

		// Marqueur du mode nuit
		boolean nuit = isModeNuit();

		// Initialisation de la liste des EC a poster
		//		List<bkmvti> mvts = new ArrayList<bkmvti>(); // Ajoute
		List<Subscriber> subs = new ArrayList<Subscriber>();
		//logger.info("Recuperation des evenements");
		// Parcours de la liste des abonnements a comptabiliser
		//List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, null, RestrictionsContainer.getInstance().add(Restrictions.in("transaction",data)), null, null, 0, -1);
		List<bkeve> eves =  new ArrayList<bkeve>();
		int max = 5000;
		int end = (data.size()/max);
		//logger.info("DATA SIZE : "+data.size());
		//logger.info("END : "+end);
		for(int i = 0; i <= end; i++){
			// Parcours de la liste des abonnements a comptabiliser
			if(i==end){
				if(max*i!=data.size()){
					eves.addAll(mobileMoneyDAO.filter(bkeve.class, null, 
							RestrictionsContainer.getInstance().add(Restrictions.in("transaction", new ArrayList<Transaction>(data.subList(max*i, data.size())))), 
							null, null, 0, -1));
					//logger.info(data.size()+" EVES");
				}
			}
			else {
				eves.addAll(mobileMoneyDAO.filter(bkeve.class, null, 
						RestrictionsContainer.getInstance().add(Restrictions.in("transaction", new ArrayList<Transaction>(data.subList(max*i, (max*(i+1)))))), 
						null, null, 0, -1));
				//logger.info(max*(i+1)+" EVES");
			}

		}
		//		
		//logger.info("Fin de la recuperation des evenements");

		data.clear();
		data = new ArrayList<Transaction>();
		// Ouverture d'une cnx vers la BD du Core Banking
		if(conCBS == null || conCBS.isClosed()) conCBS = getSystemConnection(dsCBS);

		// Suspension temporaire du mode blocage dans la BD du Core Banking
		if(dsCBS.getDbConnectionString().indexOf("informix") > 0) conCBS.createStatement().executeUpdate("SET ISOLATION TO DIRTY READ");

		// Desactivation du mode AUTO COMMIT
		conCBS.setAutoCommit(false);


		// Initialisation d'un preparateur de requetes pour les solde
		String req = "update bkcom set sin = sin - ?  where age=? and ncp=? and clc=?";
		PreparedStatement psAmt = conCBS.prepareStatement(req);
		// Initialisation d'un preparateur de requetes pour les ecritures
		PreparedStatement ps = conCBS.prepareStatement(new bkmvti().getSaveQuery());
		//logger.info("Parcours des evenements");

		for(bkeve eve : eves) {

			Transaction tx = mapTrans.get(eve.getTransaction().getId());
			//Long numEve = getLastEveNum(dsCBS);
			Long numEve = (long) (new Random().nextInt(900000) + 100000);

			// Si l'objet courant a ete selectionne par l'utilisateur et ses ecritures sont equilibrees
			if(eve.isEquilibre()) { //if(c.isSelected() && c.getEve().isEquilibre()) {

				if(tx.getStatus().equals(TransactionStatus.REGUL)){
					if(eve.getEve().equals("000000")) eve.setEve(MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"));

					// Parcours des ecritures comptables
					for(bkmvti mvt : eve.getEcritures()) {

						// MAJ des parametres des EC generes
						mvt.setUti(user);
						mvt.setDco(dco);
						//logger.info("MAJ des EC de regul!");
						/**Subscriber s = c.getSubscriber();
						String phoneNumber = s.getFirstPhone();
						String lib = "FRAIS  RGUL/" + datop + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()) + "/" + (phoneNumber != null ? phoneNumber.replaceAll("237", "") : phoneNumber);
						 */
						mvt.setLib( mvt.getLib().replaceFirst(" MAC", " REGUL") );
						if(mvt.getLib().contains("ABO")) mvt.setLib(mvt.getLib().replaceFirst("ABO", "FRAIS"));
						if(mvt.getLib().contains("HIP")) mvt.setLib(mvt.getLib().replaceFirst("HIP", "LS"));
						//logger.info("Ecritures : "+mvt.getLib());
						// Mise a jour des anciens libeles
						if(mvt.getLib().contains("/")){
							// Mise a jour libele 2018/11/16
							String[] lib = mvt.getLib().split("/");
							//if(lib.length<4) mvt.setLib(lib[0]+" "+lib[3]+" "+lib[1]);
							mvt.setLib(lib[0]+" "+lib[2]+" "+lib[1]);
							//logger.info("NEW REGUL LIB : "+mvt.getLib());
						}

						if(mvt.getSen().equals("D")){
							mvt.setDva(dvaDebit);
							mvt.setDin(new Date());
						}

						if(mvt.getSen().equals("C")){
							mvt.setDva(dvaCredit);
							mvt.setDin(new Date());
						}
						if(mvt.getEve().equals("000000")) mvt.setEve(eve.getEve());
						
						// ADD
						//getRapportDetails(mvt, mapCompte, dsCBS);
						FactMonthDetails det = new FactMonthDetails(Integer.valueOf(mvt.getAge()), mvt.getNcp()+"-"+mvt.getClc(),"", mvt.getLib(),null,null,null,mvt.getSen(),mvt.getMon());
						det.setTxtage(mvt.getAge());
						
						Subscriber sub = tx.getSubscriber();
						if(sub != null){
							det.setIntitule(sub.getCustomerName());
							det.setLibage(sub.getAgeName());
							det.setDateAbon(sub.getDate());
							det.setDateDernfact(sub.getDateDernCompta());
						}
						details.add(det);
						if("C".equalsIgnoreCase(mvt.getSen())){
							mntC = mntC+mvt.getMon();
							nbrC++;
						}else{
							mntD = mntD+mvt.getMon();
							nbrD++;
						}
						// FIN ADD
						
					}
					//logger.info("MAJ du solde et du statut de la trx");
					// Reduction du solde du client
					String numCompte = tx.getSubscriber().getFirstAccount();
					psAmt.setDouble(1, tx.getTtc());
					psAmt.setString(2, numCompte.split("-")[0]);
					psAmt.setString(3, numCompte.split("-")[1]);
					psAmt.setString(4, numCompte.split("-")[2]);
					psAmt.addBatch();
					//reduitSolde(tx.getSubscriber().getFirstAccount(), tx.getTtc(), nuit);
					// MAJ du statut de la transaction
					tx.setStatus(TransactionStatus.SUCCESS);
					tx.setPosted(Boolean.TRUE);
					tx.setDateTraitement(new Date());
				}
				else{
					// Si le solde du compte est suffisant
					if(getSolde(tx.getSubscriber().getFirstAccount(), nuit) > tx.getTtc()){

						if(eve.getEve().equals("000000")) eve.setEve(MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"));

						// Parcours des ecritures comptables
						for(bkmvti mvt : eve.getEcritures()) {

							// MAJ des parametres des EC generes
							mvt.setUti(user);
							mvt.setDco(dco);
							if(mvt.getEve().equals("000000")) mvt.setEve(eve.getEve());

							// ADD
							//getRapportDetails(mvt, mapCompte, dsCBS);
							FactMonthDetails det = new FactMonthDetails(Integer.valueOf(mvt.getAge()), mvt.getNcp()+"-"+mvt.getClc(),"", mvt.getLib(),null,null,null,mvt.getSen(),mvt.getMon());
							det.setTxtage(mvt.getAge());
							
							Subscriber sub = tx.getSubscriber();
							if(sub != null){
								det.setIntitule(sub.getCustomerName());
								det.setLibage(sub.getAgeName());
								det.setDateAbon(sub.getDate());
								det.setDateDernfact(sub.getDateDernCompta());
							}
							details.add(det);
							if("C".equalsIgnoreCase(mvt.getSen())){
								mntC = mntC+mvt.getMon();
								nbrC++;
							}else{
								mntD = mntD+mvt.getMon();
								nbrD++;
							}
							// FIN ADD
							
						}
						//logger.info("MAJ du solde et du statut de la trx");
						// Reduction du solde du client
						String numCompte = tx.getSubscriber().getFirstAccount();
						psAmt.setDouble(1, tx.getTtc());
						psAmt.setString(2, numCompte.split("-")[0]);
						psAmt.setString(3, numCompte.split("-")[1]);
						psAmt.setString(4, numCompte.split("-")[2]);
						psAmt.addBatch();
						//reduitSolde(tx.getSubscriber().getFirstAccount(), tx.getTtc(), nuit);
						// MAJ du statut de la transaction
						tx.setStatus(TransactionStatus.SUCCESS);
						tx.setPosted(Boolean.TRUE);
						tx.setDateTraitement(new Date());

						//mobileMoneyDAO.updateSubscriber(s);

					}else {
						//logger.info("Solde insufisant : Envoi en regul");
						// On positionne le status de l'operation en REGUL
						tx.setDateTraitement(new Date());
						tx.setStatus(TransactionStatus.REGUL);

					}
				}

				Subscriber s = tx.getSubscriber();
				s.setDateSaveDernCompta(s.getDateDernCompta());
				s.setDateDernCompta(getNextFacturationDate(s.getDateDernCompta()));
				subs.add(s);
				data.add(tx);

				// Chargement de la liste des EC a poster
				//				if(!tx.getStatus().equals(TransactionStatus.REGUL)) mvts.addAll(eve.getEcritures()); // Ajoute
				if(tx.getStatus().equals(TransactionStatus.SUCCESS)) {
					//logger.info("Ecritures comptables ajoutes\n");
					//					mvts.addAll(eve.getEcritures()); // Ajoute
					//logger.info("Parcours des EC de la TRX et ajout au batch");
					// Parcours de la liste des EC de la trx
					for(bkmvti m : eve.getEcritures()) {

						// Ajout dans le lot
						ps = m.addPrepareStatement(ps);

						// Ajout du Lot des EC de la trx
						ps.addBatch();

						//						ps.execute();

					}
				}

				// MAJ des evenements, transactions, abonnements a comptabiliser et ecritures par cascade
				//mobileMoneyDAO.updateTransaction(tx);

			}

		}

		psAmt.executeBatch();
		
		// Lancement de l'execution du Lot de requetes sur le serveur DELTA
		ps.executeBatch();
		//logger.info("OK");
		// Commit
		conCBS.setAutoCommit(true);

		// Fermeture de la cnx preparee
		ps.close(); ps = null; psAmt.close(); psAmt = null;
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		//processClose(subs, data);
		//logger.info("MAJ des abonnes et des trx");
		//processupdateTFJO(subs, data);
		mobileMoneyDAO.updateSubscriber(subs);
		mobileMoneyDAO.updateTransaction(data);
		//logger.info("OK");

		// Liberation des ressources
		//		mvts.clear();
		mapTrans.clear();
		data.clear();
		subs.clear();
		eves.clear();
		
		// ADD
		String mois = DateFormatUtils.format(new Date(),"MMMM");
		Collections.sort(details);
		FactMonth fac = new FactMonth(dco, "D", "C", mntD, mntC, nbrD, nbrC, user, new Date(), mois);
		fac = mobileMoneyDAO.save(fac);
		for(FactMonthDetails det : details) det.setParent(fac);
		mobileMoneyDAO.saveList(details,true);
		fac.setDetails(details);

		return fac;
		// FIN ADD
		
	}
	


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#vaiderTFJO(java.util.List)
	 */
	@Override
	@AllowedRole(name = "validerTFJO", displayName = "MoMo.Valider.TFJO")
	//@TransactionTimeout(value = 160000)
	public Map<String, List<?>> validerTFJO2(List<Transaction> data, String user, int year, int month) throws Exception {

		// Initialisation de la liste des EC a poster
		List<bkmvti> mvts = new ArrayList<bkmvti>();
		
		params = findParameters();
		// Recuperation de la DataSource du Core Banking
		if(dsCBS == null) findCBSDataSystem();
		
		// ADD
		details = new ArrayList<FactMonthDetails>();
		mntD = 0d; mntC = 0d; nbrC = 0; nbrD = 0;
		// FIN ADD
		
		Date dco = getDateComptable(dsCBS);
		//Long numEve = getLastEveNum(dsCBS);
		Date dvaDebit = getDvaDebit();
		Date dvaCredit = getDvaCredit();

		Map<Long,Transaction> mapTrans = new HashMap<Long,Transaction>();
		for(Transaction t : data) mapTrans.put(t.getId(),t);

		// Marqueur du mode nuit
		boolean nuit = isModeNuit();

		List<Transaction> trx = new ArrayList<Transaction>();
		List<Transaction> trxRegul = new ArrayList<Transaction>();
		
		List<Subscriber> subs = new ArrayList<Subscriber>();
		List<Subscriber> subsRegul = new ArrayList<Subscriber>();
		
		//logger.info("Recuperation des evenements");
		// Parcours de la liste des abonnements a comptabiliser
		//List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, null, RestrictionsContainer.getInstance().add(Restrictions.in("transaction",data)), null, null, 0, -1);
		List<bkeve> eves =  new ArrayList<bkeve>();
		int max = 2000;
		if(data.size() < max ) max = data.size();
		int end = (data.size()/max);
		//logger.info("DATA SIZE : "+data.size()+" ||| "+data);
		logger.info("END : "+data.size());
		
		int nonEquilibre = 0;
//		for(int i = 0; i <= end; i++){
//			// Parcours de la liste des abonnements a comptabiliser
//			logger.info("i : "+i);
//			if(i==end){
//				if(max*i!=data.size()){
//					eves.addAll(mobileMoneyDAO.filter(bkeve.class, null, 
//							RestrictionsContainer.getInstance().add(Restrictions.in("transaction", new ArrayList<Transaction>(data.subList(max*i, data.size())))), 
//							null, null, 0, -1));
//					//logger.info(data.size()+" EVES");
//				}
//			}
//			else {
//				eves.addAll(mobileMoneyDAO.filter(bkeve.class, null, 
//						RestrictionsContainer.getInstance().add(Restrictions.in("transaction", new ArrayList<Transaction>(data.subList(max*i, (max*(i+1)))))), 
//						null, null, 0, -1));
//				//logger.info(max*(i+1)+" EVES");
//			}
			
		   
			eves.addAll(mobileMoneyDAO.filter(bkeve.class, null, 
					RestrictionsContainer.getInstance().add(Restrictions.in("transaction", new ArrayList<Transaction>(data.subList(0, max)))), 
					null, null, 0, -1));
			
			if(eves==null || eves.isEmpty()) { // Aucun evenement correspondant aux transactions
				//mise à jour des transactions pour relance de la facturation
				for(Transaction transac : data) {
					transac.setStatus(TransactionStatus.FAILED);
				}
				mobileMoneyDAO.saveList(data, true);
				return null;
			}
			
			//		
			//logger.info("Fin de la recuperation des evenements");
			
			// Subscriber infos map from code client
			Map<String,Subscriber> mapCompte = new HashMap<String, Subscriber>();
			//logger.info("i = "+i+" EVES: "+eves.size());
			//logger.info("Parcours des evenements");
			
			for(bkeve eve : eves) {
			  try {
				Transaction tx = mapTrans.get(eve.getTransaction().getId());
				//Long numEve = getLastEveNum(dsCBS);
				Long numEve = (long) (new Random().nextInt(900000) + 100000);
				//logger.info(" EQUILIBRE: "+eve.isEquilibre());
				// Si l'objet courant a ete selectionne par l'utilisateur et ses ecritures sont equilibrees
				if(eve.isEquilibre()) { //if(c.isSelected() && c.getEve().isEquilibre()) {
					nonEquilibre = nonEquilibre + 1;
					if(tx.getStatus().equals(TransactionStatus.REGUL)){
						logger.info(" REGUL OK!");
						
						if(eve.getEve().equals("000000")) eve.setEve(MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"));
	
						// Parcours des ecritures comptables
						for(bkmvti mvt : eve.getEcritures()) {
	
							// MAJ des parametres des EC generes
							mvt.setUti(user);
							mvt.setDco(dco);
							//logger.info("MAJ des EC de regul!");
							/**Subscriber s = c.getSubscriber();
							String phoneNumber = s.getFirstPhone();
							String lib = "FRAIS  RGUL/" + datop + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()) + "/" + (phoneNumber != null ? phoneNumber.replaceAll("237", "") : phoneNumber);
							 */
							mvt.setLib( mvt.getLib().replaceFirst(" MAC", " REGUL") );
							if(mvt.getLib().contains("ABO")) mvt.setLib(mvt.getLib().replaceFirst("ABO", "FRAIS"));
							if(mvt.getLib().contains("HIP")) mvt.setLib(mvt.getLib().replaceFirst("HIP", "LS"));
							//logger.info("Ecritures : "+mvt.getLib());
							// Mise a jour des anciens libeles
							if(mvt.getLib().contains("/")){
								// Mise a jour libele 2018/11/16
								String[] lib = mvt.getLib().split("/");
								//if(lib.length<4) mvt.setLib(lib[0]+" "+lib[3]+" "+lib[1]);
								mvt.setLib(lib[0]+" "+lib[2]+" "+lib[1]);
								//logger.info("NEW REGUL LIB : "+mvt.getLib());
							}
	
							if(mvt.getSen().equals("D")){
								mvt.setDva(dvaDebit);
								mvt.setDin(new Date());
							}
	
							if(mvt.getSen().equals("C")){
								mvt.setDva(dvaCredit);
								mvt.setDin(new Date());
							}
							if(mvt.getEve().equals("000000")) mvt.setEve(eve.getEve());
													
						}
						// MAJ du statut de la transaction
						tx.setStatus(TransactionStatus.SUCCESS);
						tx.setPosted(Boolean.TRUE);
						tx.setDateTraitement(new Date());
						
						// Subs traite
						Subscriber s = tx.getSubscriber();
						s.setDateSaveDernCompta(s.getDateDernCompta());
						s.setDateDernCompta(getNextFacturationDate(s.getDateDernCompta()));
						subs.add(s);
	
						// Trx traitee
						trx.add(tx);
					}
					else{
						logger.info("OK! PAS D'OPPOSITION ET SOLDE SUFFISANT");
						// Si le solde du compte est suffisant et sans opposition
						logger.info("TRX: "+tx);
						if(getSolde(tx.getSubscriber().getFirstAccount(), nuit) > tx.getTtc() && 
								!isCompteEnOpposition(tx.getSubscriber().getFirstAccount().split("-")[0], tx.getSubscriber().getFirstAccount().split("-")[1], params.getCodeOperation())){
	
							if(eve.getEve().equals("000000")) eve.setEve(MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"));
	
							// Parcours des ecritures comptables
							for(bkmvti mvt : eve.getEcritures()) {
	
								// MAJ des parametres des EC generes
								mvt.setUti(user);
								mvt.setDco(dco);
								if(mvt.getEve().equals("000000")) mvt.setEve(eve.getEve());
								
							}
							// MAJ du statut de la transaction
							tx.setStatus(TransactionStatus.SUCCESS);
							tx.setPosted(Boolean.TRUE);
							tx.setDateTraitement(new Date());
							
							// Subs traite
							Subscriber s = tx.getSubscriber();
							s.setDateSaveDernCompta(s.getDateDernCompta());
							s.setDateDernCompta(getNextFacturationDate(s.getDateDernCompta()));
							subs.add(s);
	
							// Trx traitee
							trx.add(tx);
	
						}else {
							logger.info("Solde insufisant ou opposition : Envoi en regul");
							// On positionne le status de l'operation en REGUL
							tx.setDateTraitement(new Date());
							tx.setStatus(TransactionStatus.REGUL);
							
							// subs regul
							Subscriber s = tx.getSubscriber();
							s.setDateSaveDernCompta(s.getDateDernCompta());
							s.setDateDernCompta(getNextFacturationDate(s.getDateDernCompta()));
							subsRegul.add(s);
							
							// Trx regul
							logger.info("Trx envoyee en regul : "+tx.toString());
							trxRegul.add(tx);
						}
					}
					// Chargement de la liste des EC a poster
					if(tx.getStatus().equals(TransactionStatus.SUCCESS)) {
						mvts.addAll(eve.getEcritures());
					}
	
				}
				
				logger.info(" Traitee: "+nonEquilibre+", "+trx.size()+" / "+data.size());
				//if(trx.size() > 1499) break;
			  }
			  catch(Exception e) {
				  e.printStackTrace();
			  }
			}

			eves.clear();
			//if(trx.size() > 1499) break;
//		}
		data.clear();
		data = new ArrayList<Transaction>();
		
		logger.info("MAJ des abonnes et des trx envoyes en regul");
		// MAJ des abonnes et des trx envoyes en regul
		mobileMoneyDAO.updateSubscriber(subsRegul);
		mobileMoneyDAO.updateTransaction(trxRegul);
		
		logger.info("OK");
		
		Map<String, List<?>> map = new HashMap<String, List<?>>();
		map.put("EC", mvts);
		map.put("TRX", trx);
		map.put("SUBS", subs);
		map.put("REGU", trxRegul);
		
		// Liberation des ressources
//		mvts.clear();
//		list.clear();
//		trx.clear();
//		subs.clear();
		mapTrans.clear();
		subsRegul.clear();
				
		return map;
		// FIN ADD
		
	}

	
	public void majSoldeFact(List<Transaction> trx, List<Subscriber> subs) throws Exception{
		// Recuperation de la DataSource du Core Banking
		if(dsCBS == null) findCBSDataSystem();
		
		// Ouverture d'une cnx vers la BD du Core Banking
		if(conCBS == null || conCBS.isClosed()) conCBS = getSystemConnection(dsCBS);

		// Suspension temporaire du mode blocage dans la BD du Core Banking
		if(dsCBS.getDbConnectionString().indexOf("informix") > 0) conCBS.createStatement().executeUpdate("SET ISOLATION TO DIRTY READ");

		// Desactivation du mode AUTO COMMIT
		conCBS.setAutoCommit(false);
		
		// Initialisation d'un preparateur de requetes pour les solde
		String req = "update bkcom set sin = sin - ?  where age=? and ncp=? and clc=?";
		PreparedStatement psAmt = conCBS.prepareStatement(req);
		
		//Transaction tx = new Transaction();
		for(Transaction tx : trx){
			// Reduction du solde du client dans le corebanking
			String numCompte = tx.getSubscriber().getFirstAccount();
			psAmt.setDouble(1, tx.getTtc());
			psAmt.setString(2, numCompte.split("-")[0]);
			psAmt.setString(3, numCompte.split("-")[1]);
			psAmt.setString(4, numCompte.split("-")[2]);
			psAmt.addBatch();
			
		}
		
		psAmt.executeBatch();
				
		// Commit
		conCBS.setAutoCommit(true);

		// Fermeture de la cnx preparee
		psAmt.close(); psAmt = null;
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		logger.info("MAJ DANS PORTAL");
		// Maj dans Portal
		mobileMoneyDAO.updateSubscriber(subs);
		mobileMoneyDAO.updateTransaction(trx);
		logger.info("MAJ OK!");
	}
	

	@Asynchronous
	private void processClose(List<Subscriber> subs,List<Transaction> data){
		// TODO Auto-generated method stub
		mobileMoneyDAO.updateSubscriber(subs);
		mobileMoneyDAO.updateTransaction(data);    	
	}


	private void processupdateTFJO(List<Subscriber> subs,List<Transaction> data){
		// TODO Auto-generated method stub
		final List<Subscriber> sub = new ArrayList<Subscriber>(subs);
		final List<Transaction> txs = new ArrayList<Transaction>(data);
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//logger.info("RUNNING UPDATES ...");
				mobileMoneyDAO.updateSubscriber(sub);
				mobileMoneyDAO.updateTransaction(txs);
				sub.clear();
				txs.clear();
			}
		}); //.start();
		t.start();
	}


	private void mergeCompta(List<Transaction> data){
		for(Transaction tx : data)  mobileMoneyDAO.update(tx); //mobileMoneyDAO.getEntityManager().merge(tx);
	}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#exportComptabilisationIntoExcelFile(java.util.List, java.lang.String)
	 */
	@Override
	@AllowedRole(name = "exportComptabilisationIntoExcelFile", displayName = "MoMo.Export.TFJO.To.Excel")
	public void exportComptabilisationIntoExcelFile( List<Transaction> data, String fileName ) throws Exception {

		// Initialisation d'un document Excel
		SXSSFWorkbook wb = new SXSSFWorkbook();

		// Initialisation de la Feuille courante
		Sheet sheet  = wb.createSheet("Ecritures Comptables");

		// Creation d'une ligne
		Row row = sheet.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
		row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "N° Mvt" );
		row.createCell(2).setCellValue( "Agence" );
		row.createCell(3).setCellValue( "Date Comptable" );
		row.createCell(4).setCellValue( "Libellé" );
		row.createCell(5).setCellValue( "Sens" );
		row.createCell(6).setCellValue( "N° de Cpte" );
		row.createCell(7).setCellValue( "Intitulé" );
		row.createCell(8).setCellValue( "Montant" );
		row.createCell(9).setCellValue( "Opération" );
		row.createCell(10).setCellValue( "N° de Pièce" );
		row.createCell(11).setCellValue( "Réf. de Lettrage" );

		// Initialisation du compteur
		int i = 1;

		// Recuperation de la liste des evenements
		List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, null, RestrictionsContainer.getInstance().add(Restrictions.in("transaction",data)), null, null, 0, -1);

		// Parcours de la liste des evenements
		for(bkeve eve : eves) {

			for(bkmvti ec : eve.getEcritures()){

				// Initialisation d'une ligne
				row = sheet.createRow(i);

				// Affichage des colonnes dans la fichier excel
				row.createCell(0).setCellValue( i++ );
				row.createCell(1).setCellValue( ec.getMvti() );
				row.createCell(2).setCellValue( ec.getAge() );
				row.createCell(3).setCellValue( ec.getDco() );
				row.createCell(4).setCellValue( ec.getLib() );
				row.createCell(5).setCellValue( ec.getSen() );
				row.createCell(6).setCellValue( ec.getNcp() );
				row.createCell(7).setCellValue( ec.getLabel() );
				row.createCell(8).setCellValue( ec.getMon() );
				row.createCell(9).setCellValue( ec.getOpe() );
				row.createCell(10).setCellValue( ec.getPie() );
				row.createCell(11).setCellValue( ec.getRlet() );

			}

		}

		/**
		 * DEUXIEME FEUILLE
		 */

		// Initialisation de la Feuille courante
		Sheet sheet2  = wb.createSheet("Synthese");

		// Creation d'une ligne
		row = sheet2.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
		row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "N° de compte" );
		row.createCell(2).setCellValue( "Client" );
		row.createCell(3).setCellValue( "Date Abonnement" );
		row.createCell(4).setCellValue( "Date Facturation" );
		row.createCell(5).setCellValue( "Sens" );
		row.createCell(6).setCellValue( "Montant" );
		row.createCell(7).setCellValue( "Taxes" );
		row.createCell(8).setCellValue( "Agence" );

		// Initialisation du compteur
		i = 1;

		// Parcours des transactions
		for(Transaction tx : data) {

			// Initialisation d'une ligne
			row = sheet2.createRow(i);

			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(1).setCellValue( tx.getSubscriber().getFirstAccount() );
			row.createCell(2).setCellValue( tx.getSubscriber().getCustomerName() );
			row.createCell(3).setCellValue( tx.getSubscriber().getDate() );
			if(null!=tx.getDateCompta()) row.createCell(4).setCellValue( tx.getDateCompta());
			else row.createCell(4).setCellValue( "" );
			row.createCell(5).setCellValue( "D" );
			row.createCell(6).setCellValue( tx.getAmount() + tx.getCommissions() );
			row.createCell(7).setCellValue( tx.getTaxes() );
			row.createCell(8).setCellValue( tx.getSubscriber().getFirstAccount().substring(0, 5) );
		}


		// Sauvegarde du fichier
		FileOutputStream fileOut = new FileOutputStream(PortalHelper.JBOSS_DATA_DIR + File.separator + PortalHelper.PORTAL_RESOURCES_DATA_DIR + File.separator + PortalHelper.PORTAL_DOWNLOAD_DATA_DIR + File.separator + fileName);
		wb.write(fileOut);
		fileOut.close();

	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#exportComptabilisationIntoExcelFile(java.util.List, java.lang.String)
	 */
	@Override
	@AllowedRole(name = "exportComptabilisationIntoExcelFile", displayName = "MoMo.Export.TFJO.To.Excel")
	public void exportECIntoExcelFile(List<Transaction> data, List<bkmvti> ecritures, String fileName ) throws Exception {

		// Initialisation d'un document Excel
		SXSSFWorkbook wb = new SXSSFWorkbook();

		// Initialisation de la Feuille courante
		Sheet sheet  = wb.createSheet("Ecritures Comptables");

		// Creation d'une ligne
		Row row = sheet.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
		row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "N° Mvt" );
		row.createCell(2).setCellValue( "Agence" );
		row.createCell(3).setCellValue( "Date Comptable" );
		row.createCell(4).setCellValue( "Libellé" );
		row.createCell(5).setCellValue( "Sens" );
		row.createCell(6).setCellValue( "N° de Cpte" );
		row.createCell(7).setCellValue( "Intitulé" );
		row.createCell(8).setCellValue( "Montant" );
		row.createCell(9).setCellValue( "Opération" );
		row.createCell(10).setCellValue( "N° de Pièce" );
		row.createCell(11).setCellValue( "Réf. de Lettrage" );

		// Initialisation du compteur
		int i = 1;
		
		for(bkmvti ec : ecritures){

			// Initialisation d'une ligne
			row = sheet.createRow(i);

			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(1).setCellValue( ec.getMvti() );
			row.createCell(2).setCellValue( ec.getAge() );
			row.createCell(3).setCellValue( ec.getDco() );
			row.createCell(4).setCellValue( ec.getLib() );
			row.createCell(5).setCellValue( ec.getSen() );
			row.createCell(6).setCellValue( ec.getNcp() );
			row.createCell(7).setCellValue( ec.getLabel() );
			row.createCell(8).setCellValue( ec.getMon() );
			row.createCell(9).setCellValue( ec.getOpe() );
			row.createCell(10).setCellValue( ec.getPie() );
			row.createCell(11).setCellValue( ec.getRlet() );

		}

		/**
		 * DEUXIEME FEUILLE
		 */

		// Initialisation de la Feuille courante
		Sheet sheet2  = wb.createSheet("Synthese");

		// Creation d'une ligne
		row = sheet2.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
		row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "N° de compte" );
		row.createCell(2).setCellValue( "Client" );
		row.createCell(3).setCellValue( "Date Abonnement" );
		row.createCell(4).setCellValue( "Date Facturation" );
		row.createCell(5).setCellValue( "Sens" );
		row.createCell(6).setCellValue( "Montant" );
		row.createCell(7).setCellValue( "Taxes" );
		row.createCell(8).setCellValue( "Agence" );

		// Initialisation du compteur
		i = 1;

		// Parcours des transactions
		for(Transaction tx : data) {

			// Initialisation d'une ligne
			row = sheet2.createRow(i);

			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(1).setCellValue( tx.getSubscriber().getFirstAccount() );
			row.createCell(2).setCellValue( tx.getSubscriber().getCustomerName() );
			row.createCell(3).setCellValue( tx.getSubscriber().getDate() );
			if(null!=tx.getDateCompta()) row.createCell(4).setCellValue( tx.getDateCompta());
			else row.createCell(4).setCellValue( "" );
			row.createCell(5).setCellValue( "D" );
			row.createCell(6).setCellValue( tx.getAmount() + tx.getCommissions() );
			row.createCell(7).setCellValue( tx.getTaxes() );
			row.createCell(8).setCellValue( tx.getSubscriber().getFirstAccount().substring(0, 5) );
		}


		// Sauvegarde du fichier
		FileOutputStream fileOut = new FileOutputStream(PortalHelper.JBOSS_DATA_DIR + File.separator + PortalHelper.PORTAL_RESOURCES_DATA_DIR + File.separator + PortalHelper.PORTAL_DOWNLOAD_DATA_DIR + File.separator + fileName);
		wb.write(fileOut);
		fileOut.close();

	}
	
		
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#executerCompensation(java.util.Date, java.util.Date)
	 */
	@Override
	@AllowedRole(name = "executerCompensation", displayName = "MoMo.ExecuterCompensation")
	public FactMonth executerCompensation(Date deb, Date fin, String user) throws Exception {

		details = new ArrayList<FactMonthDetails>();
		mntD = 0d; mntC = 0d; nbrC = 0; nbrD = 0;
		
		// checkGlobalConfig(); //Parameters params = findParameters();
		params = findParameters();
		Double totalPull = 0d, totalPush = 0d;
		Double totalPullMarch = 0d, totalPushMarch = 0d;
		int numEc = 1; 
		String datop = new SimpleDateFormat("ddMMyyHHmm").format(new Date());
		
		ResultSet rsLiaisonMTN = null, rsLiaisonDAPPULL = null, rsLiaisonDAPPUSH = null;

		if(dsCBS == null) findCBSDataSystem();

		Date dco = getDateComptable(dsCBS);
		Date dvaDebit = getDvaDebit();
		Date dvaCredit = getDvaCredit();

		// Recuperation de la Liste des Transactions validees sur la periode   .add(Restrictions.between("date", dateDeb, dateFin))
		// Recuperation de la Liste de toutes les Transactions de souscription validees non postees
		List<Transaction> trans_sub = mobileMoneyDAO.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("status", TransactionStatus.SUCCESS)).add(Restrictions.in("typeOperation", new TypeOperation[]{TypeOperation.SUBSCRIPTION} )).add(Restrictions.eq("posted", Boolean.FALSE)), OrderContainer.getInstance().add(Order.asc("date")), null, 0, -1);

		// Recuperation de la Liste de toutes les Transactions validees non postees et reconciliees
		List<Transaction> trans = mobileMoneyDAO.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("status", TransactionStatus.SUCCESS)).add(Restrictions.eq("reconcilier", Boolean.TRUE)).add(Restrictions.in("typeOperation", new TypeOperation[]{TypeOperation.PULL, TypeOperation.PUSH, TypeOperation.SUBSCRIPTION, TypeOperation.MODIFY} )).add(Restrictions.eq("posted", Boolean.FALSE)), OrderContainer.getInstance().add(Order.asc("date")), null, 0, -1);

		if(!trans_sub.isEmpty()) trans.addAll(trans_sub);
		if(trans.isEmpty()) return new FactMonth(dco, "D", "C", mntD, mntC, nbrD, nbrC, user, new Date(), "");

		/**********************************************************************
		 ******************* Traitement des souscriptions *********************
		 **********************************************************************/
		List<Transaction> transRegul = new ArrayList<Transaction>();
		List<Transaction> transRemove = new ArrayList<Transaction>();

		// Verification des soldes des transactions de souscription
		for(Transaction trx : trans) {
			if(trx.getTypeOperation().equals(TypeOperation.SUBSCRIPTION)){
				// Verifier le solde du client
				if(getSolde(trx.getSubscriber().getFirstAccount(), isModeNuit()) < trx.getTtc()){
					//logger.info("Solde insufisant : Envoi en regul");
					transRemove.add(trx);
					// On positionne le status de l'operation en REGUL
					trx.setDateTraitement(new Date());
					trx.setStatus(TransactionStatus.REGUL);
					transRegul.add(trx);
				}
			}
		}

		// Retrait des transactions a envoyer en regul
		trans.removeAll(transRemove);

		// Maj des transaction en REGUL
		mobileMoneyDAO.saveList(transRegul, true);

		if(trans.isEmpty()) return new FactMonth(dco, "D", "C", mntD, mntC, nbrD, nbrC, user, new Date(), "");


		/**************************************************************************
		 ******************* Fin Traitement des souscriptions *********************
		 **************************************************************************/

		// NUIT
		// Activer le verrou des tfjo (tfjo lances)
//		setTFJOPortalEnCours(Boolean.TRUE);

		// Calcul des montants totaux des transactions
		for(Transaction tx : trans) {
			tx.setSelected(true);
			totalPull += !tx.getSubscriber().isMerchant() && tx.getTypeOperation().equals(TypeOperation.PULL) ? tx.getAmount() : 0d;
			totalPush += !tx.getSubscriber().isMerchant() && tx.getTypeOperation().equals(TypeOperation.PUSH) ? tx.getAmount() : 0d;
			totalPullMarch += tx.getSubscriber().isMerchant() && tx.getTypeOperation().equals(TypeOperation.PULL) ? tx.getAmount() : 0d;
			totalPushMarch += tx.getSubscriber().isMerchant() && tx.getTypeOperation().equals(TypeOperation.PUSH) ? tx.getAmount() : 0d;
		}

		// Recuperation du compte MTN
		ResultSet rsCpteMTN = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(6).getQuery(), new Object[]{ params.getNumCompteMTN().split("-")[0], params.getNumCompteMTN().split("-")[1], params.getNumCompteMTN().split("-")[2] });

		// Recuperation du compte DAP PULL
		ResultSet rsCpteDAPPull = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(8).getQuery(), new Object[]{ params.getNcpDAPPull().split("-")[0], params.getNcpDAPPull().split("-")[1], params.getNcpDAPPull().split("-")[2] });

		// Recuperation du compte DAP PUSH
		ResultSet rsCpteDAPPush = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(8).getQuery(), new Object[]{ params.getNcpDAPPush().split("-")[0], params.getNcpDAPPush().split("-")[1], params.getNcpDAPPush().split("-")[2] });

		if(!rsCpteMTN.next() || !rsCpteDAPPull.next() || !rsCpteDAPPush.next() ) throw new Exception("Comptes DAP et MTN inexistants");

		// Recuperation du compte de liaison l'agence de compte float MTN
		rsLiaisonMTN = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='" +params.getNumCompteMTN().split("-")[0]+ "' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
		
		// Recuperation du compte de liaison de l'agence du DAP PULL
		rsLiaisonDAPPULL = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='" +params.getNcpDAPPull().split("-")[0]+ "' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
		
		// Recuperation du compte de liaison de l'agence du DAP PUSH
		rsLiaisonDAPPUSH = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='" +params.getNcpDAPPush().split("-")[0]+ "' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
		
		if(!rsLiaisonMTN.next() || !rsLiaisonDAPPULL.next() || !rsLiaisonDAPPUSH.next() ) throw new Exception("Comptes de Liaisons inexistants");

		// Poste les EC des transactions dans le CoreBanking
		posterTransactionsDansCoreBanking(trans, user);


		/*************************************************************************/
		/**-------GENERATION DES EC DE COMPENSATION DANS LE COMPTE DE MTN-------**/
		/*************************************************************************/

		// Recuperation du dernier numero evenement du type operation
		ResultSet rs = executeFilterSystemQuery(dsCBS, "select max(eve) as num from bkeve where ope=?", new Object[]{ params.getCodeOperation() }); // MoMoHelper.getDefaultCBSQueries().get(2).getQuery()

		// Log
		//logger.info("Lecture du dernier numero d'evenement genere OK!");

		// Calcul du numero d'evenement
		Long numEve = rs != null && rs.next() ? numEve = Long.valueOf( rs.getString("num") != null ? rs.getString("num") : "0" )  + 1 : 1l;

		// Log
		//logger.info("Calcul du prochain numero d'evenement OK!");

		// Fermeture de cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}

		bkeve eve = new bkeve(null, params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), "001", Math.abs(totalPull + totalPullMarch - totalPush - totalPushMarch), "VIRMAC", new Date(), params.getCodeUtil(), 0d, 0d, 0d, Math.abs(totalPull + totalPullMarch - totalPush - totalPushMarch) );
		eve.setEta("IG"); //eve.setEtap("VA");

		if(totalPull - totalPush < 0) {
			eve.setDebiteur( rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), rsCpteMTN.getString("clc"), rsCpteMTN.getString("cli"), rsCpteMTN.getString("nom"), rsCpteMTN.getString("ges"),  Math.abs(totalPull - totalPush), Math.abs(totalPull - totalPush), dvaCredit, rsCpteMTN.getDouble("sde"));
			eve.setCrediteur(rsCpteDAPPull.getString("age"), rsCpteDAPPull.getString("dev"), rsCpteDAPPull.getString("ncp"), rsCpteDAPPull.getString("suf"), rsCpteDAPPull.getString("clc"), rsCpteDAPPull.getString("cli"), rsCpteDAPPull.getString("inti"), rsCpteDAPPull.getString("utic"), Math.abs(totalPull - totalPush), Math.abs(totalPull - totalPush), dvaCredit, rsCpteDAPPull.getDouble("sde"));
		} else {
			eve.setCrediteur(rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), rsCpteMTN.getString("clc"), rsCpteMTN.getString("cli"), rsCpteMTN.getString("nom"), rsCpteMTN.getString("ges"),  Math.abs(totalPull - totalPush), Math.abs(totalPull - totalPush), dvaDebit, rsCpteMTN.getDouble("sde"));
			eve.setDebiteur(rsCpteDAPPush.getString("age"), rsCpteDAPPush.getString("dev"), rsCpteDAPPush.getString("ncp"), rsCpteDAPPush.getString("suf"), rsCpteDAPPush.getString("clc"), rsCpteDAPPush.getString("cli"), rsCpteDAPPush.getString("inti"), rsCpteDAPPush.getString("utic"), Math.abs(totalPull - totalPush), Math.abs(totalPull - totalPush), dvaDebit, rsCpteDAPPush.getDouble("sde"));
		}

		if(totalPullMarch - totalPushMarch < 0) {
			eve.setDebiteur( rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), rsCpteMTN.getString("clc"), rsCpteMTN.getString("cli"), rsCpteMTN.getString("nom"), rsCpteMTN.getString("ges"),  Math.abs(totalPullMarch - totalPushMarch), Math.abs(totalPullMarch - totalPushMarch), dvaCredit, rsCpteMTN.getDouble("sde"));
			eve.setCrediteur(rsCpteDAPPull.getString("age"), rsCpteDAPPull.getString("dev"), rsCpteDAPPull.getString("ncp"), rsCpteDAPPull.getString("suf"), rsCpteDAPPull.getString("clc"), rsCpteDAPPull.getString("cli"), rsCpteDAPPull.getString("inti"), rsCpteDAPPull.getString("utic"), Math.abs(totalPullMarch - totalPushMarch), Math.abs(totalPullMarch - totalPushMarch), dvaCredit, rsCpteDAPPull.getDouble("sde"));
		} else {
			eve.setCrediteur(rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), rsCpteMTN.getString("clc"), rsCpteMTN.getString("cli"), rsCpteMTN.getString("nom"), rsCpteMTN.getString("ges"),  Math.abs(totalPullMarch - totalPushMarch), Math.abs(totalPullMarch - totalPushMarch), dvaDebit, rsCpteMTN.getDouble("sde"));
			eve.setDebiteur(rsCpteDAPPush.getString("age"), rsCpteDAPPush.getString("dev"), rsCpteDAPPush.getString("ncp"), rsCpteDAPPush.getString("suf"), rsCpteDAPPush.getString("clc"), rsCpteDAPPush.getString("cli"), rsCpteDAPPush.getString("inti"), rsCpteDAPPush.getString("utic"), Math.abs(totalPullMarch - totalPushMarch), Math.abs(totalPullMarch - totalPushMarch), dvaDebit, rsCpteDAPPush.getDouble("sde"));
		}

		// Debit du Cpte DAP des Pull
		for(Transaction tx : trans) {
			if(tx.getTypeOperation().equals(TypeOperation.PULL)){
				
//				COMMENTE
//				if(!tx.getSubscriber().isMerchant()){
//					eve.getEcritures().add( new bkmvti(rsCpteDAPPull.getString("age"), rsCpteDAPPull.getString("dev"), rsCpteDAPPull.getString("cha"), rsCpteDAPPull.getString("ncp"), rsCpteDAPPull.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteDAPPull.getString("clc"), dco, null, dvaDebit, tx.getAmount(), "D", "PULL/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteDAPPull.getString("age"), rsCpteDAPPull.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;					
//				}else{
//					// Recuperation du compte MTN Marchand
//					PlageTransactions plg = tx.getSubscriber().getProfil();
//					ResultSet rsCpteDAPPullMarch = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(8).getQuery(), new Object[]{ plg.getNcpDAPPull().split("-")[0], plg.getNcpDAPPull().split("-")[1], plg.getNcpDAPPull().split("-")[2] });
//
//					if(!rsCpteDAPPullMarch.next() ) throw new Exception("Comptes de Liaisons inexistants");
//
//					eve.getEcritures().add( new bkmvti(rsCpteDAPPullMarch.getString("age"), rsCpteDAPPullMarch.getString("dev"), rsCpteDAPPullMarch.getString("cha"), rsCpteDAPPullMarch.getString("ncp"), rsCpteDAPPullMarch.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteDAPPullMarch.getString("clc"), dco, null, dvaDebit, tx.getAmount(), "D", "PULL/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteDAPPullMarch.getString("age"), rsCpteDAPPullMarch.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;
//				}
				
				if(tx.getSubscriber().isMerchant()){
					// Recuperation du compte DAP PULL Marchand
					PlageTransactions plg = tx.getSubscriber().getProfil();
					ResultSet rsCpteDAPPullMarch = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(8).getQuery(), new Object[]{ plg.getNcpDAPPull().split("-")[0], plg.getNcpDAPPull().split("-")[1], plg.getNcpDAPPull().split("-")[2] });
					if(rsCpteDAPPullMarch!=null && !rsCpteDAPPullMarch.next() ) throw new Exception("Compte DAP PULL Marchand inexistant ");
					
					rsCpteDAPPull = rsCpteDAPPullMarch;
					
					// Recuperation du compte de liaison de l'agence du DAP PULL Marchand
					ResultSet rsLiaisonDAPPULLMarch = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='" +plg.getNcpDAPPull().split("-")[0]+ "' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
					if(rsLiaisonDAPPULLMarch!=null && !rsLiaisonDAPPULLMarch.next() ) throw new Exception("Compte de liaison du DAP PULL Marchand inexistant ");
					
					rsLiaisonDAPPULL = rsLiaisonDAPPULLMarch;
					
				}
				// Debit du DAP PULL du montant de la transaction correspondante
				eve.getEcritures().add( new bkmvti(rsCpteDAPPull.getString("age"), rsCpteDAPPull.getString("dev"), rsCpteDAPPull.getString("cha"), rsCpteDAPPull.getString("ncp"), rsCpteDAPPull.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteDAPPull.getString("clc"), dco, null, dvaDebit, tx.getAmount(), "D", "PULL/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "N", tx.getSubscriber().getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteDAPPull.getString("age"), rsCpteDAPPull.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;					
				
				// Le compte DAP PULL et le compte float MTN ne sont pas dans la meme agence
				if(!StringUtils.equalsIgnoreCase(rsCpteDAPPull.getString("age"), rsCpteMTN.getString("age"))){
					
					// Credit de la liaison du DAP PULL MTN du montant de la transaction correspondante
					if(rsLiaisonDAPPULL != null) eve.getEcritures().add( new bkmvti(rsLiaisonDAPPULL.getString("age"), rsLiaisonDAPPULL.getString("dev"), rsLiaisonDAPPULL.getString("cha"), rsLiaisonDAPPULL.getString("ncp"), rsLiaisonDAPPULL.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonDAPPULL.getString("clc"), dco, null, dvaCredit, tx.getAmount(), "C", "LS PULL/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "O", tx.getSubscriber().getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonDAPPULL.getString("age"), rsLiaisonDAPPULL.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
					// Debit de la liaison du compte float MTN du montant de la transaction correspondante
					if(rsLiaisonMTN != null) eve.getEcritures().add( new bkmvti(rsLiaisonMTN.getString("age"), rsLiaisonMTN.getString("dev"), rsLiaisonMTN.getString("cha"), rsLiaisonMTN.getString("ncp"), rsLiaisonMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonMTN.getString("clc"), dco, null, dvaDebit, tx.getAmount(), "D", "LS PULL/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "O", tx.getSubscriber().getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonMTN.getString("age"), rsLiaisonMTN.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
					
				}
				
			}
			else if(tx.getTypeOperation().equals(TypeOperation.PUSH)){
				if(tx.getSubscriber().isMerchant()){
					// Recuperation du compte DAP PUSH Marchand
					PlageTransactions plg = tx.getSubscriber().getProfil();
					ResultSet rsCpteDAPPushMarch = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(8).getQuery(), new Object[]{ plg.getNcpDAPPush().split("-")[0], plg.getNcpDAPPush().split("-")[1], plg.getNcpDAPPush().split("-")[2] });
					if(!rsCpteDAPPushMarch.next() ) throw new Exception("Comptes de Liaisons inexistants");

					rsCpteDAPPush = rsCpteDAPPushMarch;
					
					// Recuperation du compte de liaison de l'agence du DAP PUSH Marchand
					ResultSet rsLiaisonDAPPUSHMarch = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='" +plg.getNcpDAPPush().split("-")[0]+ "' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
					if(rsLiaisonDAPPUSHMarch!=null && !rsLiaisonDAPPUSHMarch.next() ) throw new Exception("Compte de liaison du DAP PUSH Marchand inexistant ");
					
					rsLiaisonDAPPUSH = rsLiaisonDAPPUSHMarch;
					
				}
				
				// Credit du DAP Push du montant de la transaction correspondante
				eve.getEcritures().add( new bkmvti(rsCpteDAPPush.getString("age"), rsCpteDAPPush.getString("dev"), rsCpteDAPPush.getString("cha"), rsCpteDAPPush.getString("ncp"), rsCpteDAPPush.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteDAPPush.getString("clc"), dco, null, dvaCredit, tx.getAmount(), "C", "PUSH/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "N", tx.getSubscriber().getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteDAPPush.getString("age"), rsCpteDAPPush.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;
				
				// Le compte DAP PUSH et le compte float MTN ne sont pas dans la meme agence
				if(!StringUtils.equalsIgnoreCase(rsCpteDAPPush.getString("age"), rsCpteMTN.getString("age"))){
					
					// Credit de la liaison du compte float MTN du montant de la transaction correspondante
					if(rsLiaisonMTN != null) eve.getEcritures().add( new bkmvti(rsLiaisonMTN.getString("age"), rsLiaisonMTN.getString("dev"), rsLiaisonMTN.getString("cha"), rsLiaisonMTN.getString("ncp"), rsLiaisonMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonMTN.getString("clc"), dco, null, dvaDebit, tx.getAmount(), "C", "LS PUSH/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "O", tx.getSubscriber().getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonMTN.getString("age"), rsLiaisonMTN.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
					// Debit de la liaison du DAP PUSH MTN du montant de la transaction correspondante
					if(rsLiaisonDAPPUSH != null) eve.getEcritures().add( new bkmvti(rsLiaisonDAPPUSH.getString("age"), rsLiaisonDAPPUSH.getString("dev"), rsLiaisonDAPPUSH.getString("cha"), rsLiaisonDAPPUSH.getString("ncp"), rsLiaisonDAPPUSH.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonDAPPUSH.getString("clc"), dco, null, dvaCredit, tx.getAmount(), "D", "LS PUSH/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "O", tx.getSubscriber().getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonDAPPUSH.getString("age"), rsLiaisonDAPPUSH.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
					
				}
				
			}
		}

		// Crédit du Cpte MTN du total des Pull
		if(totalPull > 0) eve.getEcritures().add( new bkmvti(rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("cha"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteMTN.getString("clc"), dco, null, dvaDebit, totalPull, "C", "COMPENS/" + TypeOperation.PULL.toString().toUpperCase() + "/" + datop, "N", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), totalPull, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;

		// Debit du Cpte MTN du total des Push
		if(totalPush > 0) eve.getEcritures().add( new bkmvti(rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("cha"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteMTN.getString("clc"), dco, null, dvaCredit, totalPush, "D", "COMPENS/" + TypeOperation.PUSH.toString().toUpperCase() + "/" + datop, "N", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), totalPush, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;

		// Crédit du Cpte MTN du total des Pull
		if(totalPullMarch > 0) eve.getEcritures().add( new bkmvti(rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("cha"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteMTN.getString("clc"), dco, null, dvaDebit, totalPullMarch, "C", "COMPENS MARCH/" + TypeOperation.PULL.toString().toUpperCase() + "/" + datop, "N", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), totalPullMarch, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;

		// Debit du Cpte MTN du total des Push
		if(totalPushMarch > 0) eve.getEcritures().add( new bkmvti(rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("cha"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteMTN.getString("clc"), dco, null, dvaCredit, totalPushMarch, "D", "COMPENS MARCH/" + TypeOperation.PUSH.toString().toUpperCase() + "/" + datop, "N", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), totalPushMarch, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;

//		COMMENTE
//		// Credit du DAP des Push
//		for(Transaction tx : trans) {
//			if(tx.getTypeOperation().equals(TypeOperation.PUSH)){
//				if(!tx.getSubscriber().isMerchant()){
//					eve.getEcritures().add( new bkmvti(rsCpteDAPPush.getString("age"), rsCpteDAPPush.getString("dev"), rsCpteDAPPush.getString("cha"), rsCpteDAPPush.getString("ncp"), rsCpteDAPPush.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteDAPPush.getString("clc"), dco, null, dvaCredit, tx.getAmount(), "C", "PUSH/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteDAPPush.getString("age"), rsCpteDAPPush.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;
//				}else{
//					// Recuperation du compte DAP PUSH Marchand
//					PlageTransactions plg = tx.getSubscriber().getProfil();
//					ResultSet rsCpteDAPPushMarch = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(8).getQuery(), new Object[]{ plg.getNcpDAPPush().split("-")[0], plg.getNcpDAPPush().split("-")[1], plg.getNcpDAPPush().split("-")[2] });
//
//					if(!rsCpteDAPPushMarch.next() ) throw new Exception("Comptes de Liaisons inexistants");
//
//					eve.getEcritures().add( new bkmvti(rsCpteDAPPushMarch.getString("age"), rsCpteDAPPushMarch.getString("dev"), rsCpteDAPPushMarch.getString("cha"), rsCpteDAPPushMarch.getString("ncp"), rsCpteDAPPushMarch.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteDAPPushMarch.getString("clc"), dco, null, dvaCredit, tx.getAmount(), "C", "PUSH/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "N", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteDAPPushMarch.getString("age"), rsCpteDAPPushMarch.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;					
//				}
//			}
//		}
		
		/*************************************************************************/
		/**------------MISE A JOUR DES TRANSACTIONS DANS LE SYSTEME-------------**/
		/*************************************************************************/

		// Enregistrement de l'evenement Global dans Amplitude
		executeUpdateSystemQuery(dsCBS, eve.getSaveQuery(), eve.getQueryValues());



		/*************************************************************************/
		/**---TRAITEMENT BATCH POUR POSTER LES ECRITURES DANS DELTA PAR LOTS----**/
		/*************************************************************************/

		// Ouverture d'une cnx vers la BD du Core Banking
		if(conCBS == null || conCBS.isClosed()) conCBS = getSystemConnection(dsCBS);

		// Suspension temporaire du mode blocage dans la BD du Core Banking
		if(dsCBS.getDbConnectionString().indexOf("informix") > 0) {
			conCBS.createStatement().executeUpdate("SET ISOLATION TO DIRTY READ");
			//conCBS.createStatement().executeUpdate("SET LOCK MODE TO WAIT");
		}

		// Desactivation du mode AUTO COMMIT
		conCBS.setAutoCommit(false);

		// Initialisation d'un preparateur de requetes
		PreparedStatement ps = conCBS.prepareStatement(new bkmvti().getSaveQuery());

		// Subscriber infos map from code client
		Map<String,Subscriber> mapCompte = new HashMap<String, Subscriber>();
		
		// Parcours des ecritures
		for(bkmvti mvt : eve.getEcritures()) {

			mvt.setUti(user);
			ps = mvt.addPrepareStatement(ps);

			// Ajout du Lot i
			ps.addBatch();
			
			// ADD
			getRapportDetails(mvt, mapCompte, dsCBS);
			// FIN ADD
		}
		
		// Lancement de l'execution du Lot de requetes sur le serveur DELTA
		ps.executeBatch();
		
		// Sauvegarde des ecritures comptables dans Amplitude
		//for(bkmvti mvt : eve.getEcritures()) executeUpdateSystemQuery(dsCBS, mvt.getSaveQuery(), mvt.getQueryValues());

		// MAJ du solde indicatif des comptes DAP
		//executeUpdateSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(4).getQuery(), new Object[]{ totalPush, rsCpteDAPPush.getString("age"), rsCpteDAPPush.getString("ncp"), rsCpteDAPPush.getString("clc") } );
		//executeUpdateSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(5).getQuery(), new Object[]{ totalPull, rsCpteDAPPull.getString("age"), rsCpteDAPPull.getString("ncp"), rsCpteDAPPull.getString("clc") } );
		
		// MAJ du dernier numero d'evenement utilise pour le type operation
		executeUpdateSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(3).getQuery(), new Object[]{ Long.valueOf(eve.getEve()), eve.getOpe() });
		
		// Sauvegarde l'evenement (Dans le Portal)
		mobileMoneyDAO.save(eve);
		
		// Commit
		conCBS.setAutoCommit(true);
		rs.close(); rs = null;
		ps.close(); ps = null;
		
		// On libere tous les ResultSet
		
		if(rsCpteMTN != null) {
			rsCpteMTN.close(); 
			if(rsCpteMTN.getStatement() != null) {
				rsCpteMTN.getStatement().close();
			}
		}
		if(rsCpteDAPPull != null) {
			rsCpteDAPPull.close(); 
			if(rsCpteDAPPull.getStatement() != null) {
				rsCpteDAPPull.getStatement().close();
			}
		}
		if(rsCpteDAPPush != null) {
			rsCpteDAPPush.close(); 
			if(rsCpteDAPPush.getStatement() != null) {
				rsCpteDAPPush.getStatement().close();
			}
		}
		rsCpteMTN = null; rsCpteDAPPull = null; rsCpteDAPPush = null; rs = null;
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		trans.clear();
		trans_sub.clear();
		transRegul.clear();
		transRemove.clear();
		//params = null;

		// Execution du Timer de Contrôle de la fin des TFJ
		//TFJScheduler();
		
		// ADD
		Collections.sort(details);
		FactMonth fac = new FactMonth(dco, "D", "C", mntD, mntC, nbrD, nbrC, user, new Date(), "");
		fac.setDev(dev);
		fac = mobileMoneyDAO.save(fac);
		for(FactMonthDetails det : details) det.setParent(fac);
		mobileMoneyDAO.saveList(details,true);
		fac.setDetails(details);

		return fac;
		// FIN ADD
	}
	
	

	private void getRapportDetails(bkmvti mvt, Map<String, Subscriber> mapCompte, DataSystem dsCBS) throws Exception {
		
		// Recuperation 
		FactMonthDetails det = new FactMonthDetails(Integer.valueOf(mvt.getAge()), mvt.getNcp()+"-"+mvt.getClc(),"", mvt.getLib(),null,null,null,mvt.getSen(),mvt.getMon());
		det.setTxtage(mvt.getAge());
		det.setDev(mvt.getDev());
		String cle = mvt.getNcp().trim().substring(0, 7);
		logger.info("CODE CLIENT : "+cle);
		ResultSet rst = null;
		if(mapCompte.containsKey(cle)){
			logger.info("CODE CLIENT TROUVE : "+cle);
			det.setIntitule(mapCompte.get(cle).getCustomerName());
			det.setLibage(mapCompte.get(cle).getAgeName());
			det.setDateAbon(mapCompte.get(cle).getDate());
			det.setDateDernfact(mapCompte.get(cle).getDateDernCompta());
		}else{
			Subscriber sub = findSubscriber(cle);
			if(sub != null){
				det.setIntitule(sub.getCustomerName());
				det.setLibage(sub.getAgeName());
				det.setDateAbon(sub.getDate());
				det.setDateDernfact(sub.getDateDernCompta());
				if(mapCompte.isEmpty()){
					mapCompte.put(cle, sub);
				}else if(!mapCompte.containsKey(cle)){
					mapCompte.put(cle, sub);
				}
			}
			else {
				// recherche d'un compte non client
				rst = executeFilterSystemQuery(dsCBS,MoMoHelper.getDefaultCBSQueries().get(7).getQuery(),new Object[]{mvt.getAge(),mvt.getNcp(),mvt.getClc()});
				if(rst.next()){
					det.setIntitule(rst.getString("inti"));
					if(mapCompte.isEmpty()){
						Subscriber subs = new Subscriber();
						subs.setCustomerName(rst.getString("inti"));;
						mapCompte.put(cle, subs);
					}else if(!mapCompte.containsKey(cle)){
						Subscriber subs = new Subscriber();
						subs.setCustomerName(rst.getString("inti"));;
						mapCompte.put(cle, subs);
					}
				}

				logger.info("SUBS NOT FOUND : "+cle);
			}
		}
		details.add(det);
		if("C".equalsIgnoreCase(mvt.getSen())){
			mntC = mntC+mvt.getMon();
			nbrC++;
		}else{
			mntD = mntD+mvt.getMon();
			nbrD++;
		}
		dev = mvt.getDev();
		
		// CBS_CNX_OPTI
		if(rst != null) {
			rst.close(); 
			if(rst.getStatement() != null) {
				rst.getStatement().close();
			}
		}
		if(conCBS != null ) conCBS.close();
	}


	/**
		Les tables impliquées sont :

		    - bksig_audit : pour sauvegarder les traces de consultation
			- bksigc : Pour consulter les signatures
			- table 098 avec cacc ='SIGNATURES' :  pour récupérer l'url vers le serveur des signatures
			(Tu peux consulter le dictionnaire de données pour en savoir plus sur ces tables)

		Le process est le suivant:
		   1 - Génération d'une clé unique de consultation
		   2 - Génération du délai de validité de la signature au format yyMMddHHmmss
		   3 - Insertion dans bksig_audit
		   4 - insertion dans bksigc
		   5 - fabication de l'url de consultation à partir de la clé unique générée plus haut
		   6 - Affichage de la signature à partir de l'url générée  (je le fais dans un popup du navigateur)
		   -------
		   7- Si la signature est OK (valider par l'utilisateur) le champ conf de bksig_audit est update à 'O', sinon on update à 'N'
		   la requête est : UPDATE bksig_audit SET conf =? WHERE age = ? AND ncp = ? AND dev = '001' AND suf = ? AND cli = ? AND datec = ? AND heurec = ? AND utic = ?;

	 **/

	
	/**
		1- Génération d'une clé unique de consultation
		  Génération de la clé unique
		  clé = clé max en BD + 1;
	 */
	private double genererCle() throws Exception{

		if(dsCBS == null) findCBSDataSystem();

		ResultSet rs = executeFilterSystemQuery(dsCBS, "select max (cle) as maxCle from bksigc", null) ;
		double max= rs != null && rs.next() ? rs.getDouble("maxCle") : 0;
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		//if(max == 9999999999d)            max -= 9999;
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		return  max + 1;
	}
	

	/**
		2- Génération du délai de validité de la signature au format yyMMddHHmmss
		Pour ce qui est du délai de validiter je récupère l'heure du serveur, j'ajoute une minute et je format en String au format yyMMddHHmmss
	 */

	/**
		3- Insertion dans bksig_audit
		Requête pour insertion dans bksig_audit: INSERT_INTO_BKSIG_AUDIT = "INSERT INTO bksig_audit VALUES (?,'001',?,?,?,?,?,?,'E','cbconssig')"
	 */
	public void insertIntoBksigAudi(String age, String ncp, String suf, String cli, Date datec, String heurec, String utic) throws Exception{

		if(dsCBS == null) findCBSDataSystem();

		executeUpdateSystemQuery(dsCBS, "INSERT INTO bksig_audit VALUES (?,'001',?,?,?,?,?,?,'E','cbconssig')", new Object[]{age, ncp, suf, cli, new java.sql.Date(datec.getTime()), heurec, utic}) ;

	}
	

	/**
		4- insertion dans bksigc
		Requête pour insertion dans bksigc: INSERT_INTO_BKSIGC = "INSERT INTO bksigc VALUES (?,?,?,?,?,?,?,'001','O')"
	 */

	public void insertIntoBksigC(double cle, String valid, String cuti, String cli, String ncp, String suf, String age) throws Exception{

		if(dsCBS == null) findCBSDataSystem();

		executeUpdateSystemQuery(dsCBS, "INSERT INTO bksigc VALUES (?,?,?,?,?,?,?,'001','O')", new Object[]{cle, valid, cuti, cli, ncp, suf, age}) ;
	}
	

	/**
		5 - fabication de l'url de consultation à partir de la clé unique générée plus haut
		urlfinal  = lien+cle;
		requête récupération lien vers serveur signature :  SELECT_LIEN_SIG = "SELECT lib3,lib4,lib5 FROM bknom WHERE age ='00099' AND ctab ='098' AND cacc ='SIGNATURES'";
	 */
	public String getLienSig() throws Exception{

		if(dsCBS == null) findCBSDataSystem();

		String lien = null;
		ResultSet rs = executeFilterSystemQuery(dsCBS, "SELECT lib3,lib4,lib5 FROM bknom WHERE age ='00099' AND ctab ='098' AND cacc ='SIGNATURES'", null) ;

		while (rs.next()) {
			lien = rs.getString("lib3").trim();
			if(lien.charAt(lien.length()-1) != '/')  lien+='/';
			String str = rs.getString("lib4").trim();
			if(str.charAt(0)== '/') str = str.substring(0);
			lien += str; 
			if(lien.charAt(lien.length()-1) != '/') lien+='/';
			str = rs.getString("lib5").trim();
			if(str.charAt(0)== '/') str = str.substring(0);
			lien += str;
		}
		// CBS_CNX_OPTI
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}
		if(conCBS != null ) conCBS.close();
		return lien;
	}

	
	/**
	 *	Pour avoir le lien finall la fonction finale est :
	 */
	@Override
	public String getLienSig(String age, String ncp, String suf, String cli, Date datec, String heurec, String utic) throws Exception {
		String valid;

		//gération clé
		double cle = genererCle();

		//simple test pour se rassurer que la clé n'existe pas encore voir cette méthode plus bas
		//while(!cleSigExiste(cle)) cle++;

		// validité coe je l'ai dit date du serveur + une minute formaté
		valid = new SimpleDateFormat("yyMMdd").format(datec) + heurec;

		//insertion bksig_audit
		insertIntoBksigAudi(age, ncp, suf, cli, datec, heurec, utic);

		// insertion bksigc
		insertIntoBksigC(cle, valid, utic, cli, ncp, suf, age);

		// return du lien final 
		return getLienSig() + new DecimalFormat("#").format(cle); // le type double cle est formaté sous forme de String sans partie décimal DecimalFormat df = new DecimalFormat("#");
	}

	
	/**
	 *	requête pour se rassurer qu'une clé générée n'existe pas : SELECT_INTO_BKSIGC = "SELECT * FROM bksigc WHERE cle = ?"
	 *
	private boolean cleSigExiste(double cle) throws Exception{

		if(dsCBS == null) findCBSDataSystem();

        ResultSet rs = executeFilterSystemQuery(dsCBS, "SELECT * FROM bksigc WHERE cle = " + cle, null) ;

        boolean rep = rs != null && rs.next();
        rs.close();
        return rep;
    }
	 */
	

	@SuppressWarnings("unchecked")
	public List<Subscriber> findAllSubscriberNonFactures(){

		// Initialisation de la liste a retourner
		List<Subscriber> result = new ArrayList<Subscriber>();

		// Recherche des abonnes non factures a la souscription
		result = mobileMoneyDAO.getEntityManager().createQuery("Select distinct t.subscriber from Transaction t where t.typeOperation=:typeOp and t.ttc=0 and t.subscriber.status=:actif order by t.subscriber.customerName").setParameter("typeOp", TypeOperation.SUBSCRIPTION).setParameter("actif", StatutContrat.ACTIF).getResultList(); // .filter(Subscriber.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("", 0)), null, null, 0, -1);

		// Suppression des clients Employes
		//for(int i=result.size()-1; i>=0; i--) if(isClientEmploye(result.get(i).getCustomerId()) ) result.remove(i);

		// Retourne le resultat
		return result;

	}


	public void facturerListSubscribers(List<Subscriber> list) throws Exception {

		if(list == null || list.isEmpty()) return;

		// Recuperation des parametres generaux
		// checkGlobalConfig(); //Parameters param = findParameters();
		params = findParameters();

		// Lecture de la commission du type operation
		Commissions coms = ConverterUtil.convertCollectionToMap(params.getCommissions(), "operation").get(TypeOperation.SUBSCRIPTION);

		// Si des commissions ont ete parametres sur l'operation
		if(coms != null && coms.getValeur() > 0) {

			// Parcours de la liste des abonnes
			for(Subscriber s : list){

				Transaction trx =  findTransactionByTypeAndSubscriber(TypeOperation.SUBSCRIPTION, s.getId());
				if(trx != null){
					// Annulation de la precedente transaction
					trx.setStatus(TransactionStatus.FAILED);
					mobileMoneyDAO.save(trx);
					//mobileMoneyDAO.getEntityManager().createQuery("Update Transaction t set t.status=:status where t.typeOperation=:typeOp and t.subscriber.id=:id").setParameter("status", TransactionStatus.FAILED).setParameter("typeOp", TypeOperation.SUBSCRIPTION).setParameter("id", s.getId()).executeUpdate();
					
				}
				
				// Postage de l'evenement dans le CBS
				posterEvenementDansCoreBanking( new Transaction(TypeOperation.SUBSCRIPTION, s, 0d, s.getFirstAccount(), s.getFirstPhone(), TransactionStatus.SUCCESS) );

			}

		}
	}

 
	public Date getLastFacturationDate(Date date){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		int day =  cal.get(Calendar.MONTH) == Calendar.FEBRUARY ? 28 : 30;
		return DateUtils.addDays(date, -day);
	}

	
	public Date getNextFacturationDate(Date date){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		int day =  cal.get(Calendar.MONTH) == Calendar.FEBRUARY ? 28 : 30;
		return DateUtils.addDays(date, day);
	}

	
	/**
	 * MAJ des abonnes qui sont des employes de la banque pour eviter leur facturation
	 */
	@SuppressWarnings("unchecked")
	public List<Subscriber> updateSubscriberIsEmploye() throws Exception {

		// Initialisation de la liste a retourner
		List<Subscriber> list = new ArrayList<Subscriber>();
		// Log
		//logger.info("Updating subscribers ...");
		// Initialisation de la liste des abonnes
		List<Subscriber> subs = new ArrayList<Subscriber>();

		String req = "From Subscriber"; 

		// Recuperation de la liste des abonnements
		subs = mobileMoneyDAO.getEntityManager().createQuery(req).getResultList();
		// Log
		//logger.info("Number of subscribers : "+subs.size());
		// MAJ du status des abonnes
		for(int i=subs.size()-1; i>=0; i--){
			Boolean facturer = isClientEmploye(subs.get(i).getCustomerId()) ? Boolean.FALSE : Boolean.TRUE;
			//subs.get(i).setEmploye(isClientEmploye(subs.get(i).getCustomerId()) ? Boolean.TRUE : Boolean.FALSE);
			if(facturer!=subs.get(i).getFacturer()){
				// Specifier s'il faut facturer ou pas
				subs.get(i).setFacturer(facturer);
				// Log
				//logger.info("Subscribers "+subs.get(i).getId()+" updated");
				list.add(subs.get(i));
			}
		}
		// Log
		//logger.info("Number of subscribers updated : "+list.size());
		// Log
		//logger.info("Saving updated subscribers ...");
		// Enregistrement
		mobileMoneyDAO.saveList(list, true);
		subs.clear();
		// Log
		//logger.info("End saving");
		return list;
	}
	
	/**
	 * Recherche des clients avec les date de derniere facturation incorrectes
	 */
	 @SuppressWarnings("unchecked")
	 @Override
	 public List<List<Subscriber>> listInvalidDernFact(){
		// Initialisation de la liste a retourner
			List<Subscriber> list = new ArrayList<Subscriber>();
			List<Subscriber> list1 = new ArrayList<Subscriber>();
			List<Subscriber> list2 = new ArrayList<Subscriber>();
			List<Subscriber> list3 = new ArrayList<Subscriber>();
			List<Subscriber> list4 = new ArrayList<Subscriber>();
			List<Subscriber> list5 = new ArrayList<Subscriber>();
			List<List<Subscriber>> list6 = new ArrayList<>();
			
			String req = "From Subscriber s where s.dateDernCompta > 30 + dateSaveDernCompta and s.facturer = true"; 
			list = mobileMoneyDAO.getEntityManager().createQuery(req).getResultList();
			
			// Avant le 01-01-2020 
			 //jamais facturés
			String req1 = "From Subscriber s where s.facturer = true and s.status='ACTIF' "
					+ "and s.dateSaveDernCompta is null and s.dateDernCompta < '2020-01-01'  and s.regulfacturation = false order by dateDernCompta desc "; 
			list1 = mobileMoneyDAO.getEntityManager().createQuery(req1).getResultList();
			//deja facturés
			String req2 = "From Subscriber s where s.facturer = true and s.status='ACTIF' "
					+ "and s.dateSaveDernCompta is not null and s.dateDernCompta < '2020-01-01'  and s.regulfacturation = false order by dateDernCompta desc "; 
			list2 = mobileMoneyDAO.getEntityManager().createQuery(req2).getResultList();
		    //entre 01-01-2020 et 01-04-2020 (debut de la gratuité)
			 // jamais facturés
			String req3 = "From Subscriber s where s.facturer = true and status='ACTIF'  " +
					"and s.dateSaveDernCompta is null " +
					"and s.dateDernCompta >= '2020-01-01' " + 
					"and s.dateDernCompta < '2020-04-01' " + 
					"and s.regulfacturation = false "
					+ "order by dateDernCompta desc ";
			list3 = mobileMoneyDAO.getEntityManager().createQuery(req3).getResultList();
			 // deja facturés
			String req4 = "From Subscriber s where s.facturer = true and status='ACTIF'  " +
					"and s.dateSaveDernCompta is not null " +
					"and s.dateDernCompta >= '2020-01-01' " + 
					"and s.dateDernCompta < '2020-04-01' " + 
					"and s.regulfacturation = false "
					+ "order by dateDernCompta desc ";
			list4 = mobileMoneyDAO.getEntityManager().createQuery(req4).getResultList();
			// Clients avec last_date_save > 31-08-2020 et deja facturés
			
			String req5 = "From Subscriber s where s.facturer = true and status='ACTIF'  " +
					"and s.dateSaveDernCompta is not null " +
					"and s.dateDernCompta >= '2020-06-30' " + 
					"and s.regulfacturation = false "
					+ "order by dateDernCompta desc ";
			list5 = mobileMoneyDAO.getEntityManager().createQuery(req5).getResultList();
			
			list6.add(list1);
			list6.add(list2);
			list6.add(list3);
			list6.add(list4);
			list6.add(list5);
			
			logger.info("A1: "+list1.size()+"\nA2: "+list2.size()+"\nB1: "+list3.size()+"\nB2: "+list4.size()+"\nC: "+list5.size());
			
			return list6;
	 }
	 
	 /**
	 * Recherche des employés avec les date de derniere facturation incorrectes
	 */
	 @SuppressWarnings("unchecked")
	 @Override
	 public List<Subscriber> updateDateDernFacturation(List<List<Subscriber>> subs){
		// Initialisation de la liste a retourner
			List<Subscriber> list = new ArrayList<Subscriber>();
//			List<Transaction> _list = new ArrayList<Transaction>();
//			List<Subscriber> whitelist = new ArrayList<Subscriber>();
//			List<Subscriber> list1 = new ArrayList<Subscriber>();
//			List<Subscriber> list2 = new ArrayList<Subscriber>();
//			List<Subscriber> list3 = new ArrayList<Subscriber>();
//			List<Subscriber> list4 = new ArrayList<Subscriber>();
//			List<Subscriber> list5 = new ArrayList<Subscriber>();
//			String req = "From Transaction t where t.typeOperation = :operation and t.subscriber = :subscriber and (t.status = :succes or t.status = :regul) order by date desc "; 
//			
//			//Corrections des anomalies.
//			//-- recherche min et max
//			String req_min = "select min(s.dateDernCompta) from Subscriber s "
//					+ "where s.status='ACTIF' and s.facturer = true and s.dateSaveDernCompta  is not null and (s.dateDernCompta < '2020-01-01')";
//			String req_max = "select max(s.dateDernCompta) from Subscriber s "
//					+ "where s.status='ACTIF' and s.facturer = true and s.dateSaveDernCompta  is not null and (s.dateDernCompta < '2020-01-01')";
//			
//			Date min =  (Date)mobileMoneyDAO.getEntityManager().createQuery(req_min).getSingleResult();
//			Date max =  (Date)mobileMoneyDAO.getEntityManager().createQuery(req_max).getSingleResult();
//			logger.info("Date min: "+min+" Max: "+max);
//			
//			list1 = subs.get(0);
//			list2 = subs.get(1);
//			list3 = subs.get(2);
//			list4 = subs.get(3);
//			list5 = subs.get(4);
//			
//			//-- Clients jamais facturés avant 01-01-2020
//			logger.info("Clients jamais facturés avant 01-01-2020: "+list1.size());
//			for(Subscriber s : list1) {
//				s.setDateSaveDernCompta(min);
//				s.setDateDernCompta(DateUtils.addDays(min, 90)); // 90jours de gratuité
//				s.setRegulfacturation(Boolean.TRUE);
//				
//				whitelist.add(s);
//			}
//			mobileMoneyDAO.saveList(whitelist, true);
//			list.addAll(whitelist);
//			
//			whitelist.clear();
//			//System.gc();
//		   //-- Clients deja facturés avant 01-01-2020
//			logger.info("Clients deja facturés avant 01-01-2020: "+list2.size());
//			for(Subscriber s : list2) {
//				_list = mobileMoneyDAO.getEntityManager().createQuery(req)
//						.setParameter("operation", TypeOperation.COMPTABILISATION)
//						.setParameter("subscriber", s)
//						.setParameter("succes", TransactionStatus.SUCCESS)
//						.setParameter("regul", TransactionStatus.REGUL)
//						.getResultList();
//				if(!_list.isEmpty()) {
//					logger.info("ID: "+s.getId()+" DATE DERNIERE TRX: "+_list.get(0).getDate() + " Next date: "+DateUtils.addDays(_list.get(0).getDate(), 30));
//					if(_list.get(0).getDate().after(max)) {
//						s.setDateSaveDernCompta(_list.get(0).getDate());
//						s.setDateDernCompta(DateUtils.addDays(_list.get(0).getDate(), 90));
//						s.setRegulfacturation(Boolean.TRUE);
//					}
//					else {
//						s.setDateSaveDernCompta(max);
//						s.setDateDernCompta(DateUtils.addDays(max, 90));
//						s.setRegulfacturation(Boolean.TRUE);
//					}
//				}
//				else {
//					if(s.getDateDernCompta().after(min)) {
//						s.setDateSaveDernCompta(s.getDateDernCompta());
//						s.setDateDernCompta(DateUtils.addDays(s.getDateDernCompta(), 90));
//						s.setRegulfacturation(Boolean.TRUE);
//					}
//					else {
//						s.setDateSaveDernCompta(min);
//						s.setDateDernCompta(DateUtils.addDays(min, 90));
//						s.setRegulfacturation(Boolean.TRUE);
//					}
//				}
//				whitelist.add(s);
//			}
//			
//			mobileMoneyDAO.saveList(whitelist, true);
//			list.addAll(whitelist);
//			
//			whitelist.clear();
//			
//			//-- Jamais facturés entre 01-01-2020 et 01-04-2020 (debut de la gratuité)
//			logger.info("Jamais facturés entre 01-01-2020 et 01-04-2020: "+list3.size());
//			 for(Subscriber s : list3) {
//				 try {
//					s.setDateSaveDernCompta(DateUtils.parseDate("2020-01-01", "yyyy-MM-dd"));
//					s.setDateDernCompta(DateUtils.addDays(DateUtils.parseDate("2020-01-01", "yyyy-MM-dd"), 90));
//					s.setRegulfacturation(Boolean.TRUE);
//					whitelist.add(s);
//				} catch (ParseException e) {
//					e.printStackTrace();
//				} 
//				 
//			 }
//			
//			 mobileMoneyDAO.saveList(whitelist, true);
//			 list.addAll(whitelist);
//				
//			 whitelist.clear();
//			 
//			//-- Deja facturés entre 01-01-2020 et 01-04-2020 (debut de la gratuité)
//			 logger.info("Deja facturés entre 01-01-2020 et 01-04-2020: "+list4.size());
//			 for(Subscriber s : list4) {
//				s.setDateSaveDernCompta(s.getDateDernCompta());
//				s.setDateDernCompta(DateUtils.addDays(s.getDateDernCompta(), 90));
//				s.setRegulfacturation(Boolean.TRUE);
//				whitelist.add(s); 
//			 }
//			 
//			 mobileMoneyDAO.saveList(whitelist, true);
//			 list.addAll(whitelist);
//				
//			 whitelist.clear();
//			 
//			 
//			//-- Clients avec last_date_save > 31-08-2020 et deja facturés
//			 logger.info("Clients avec last_date_save > 31-08-2020 et deja facturés: "+list5.size());
//			 try {
//				Date debut = DateUtils.parseDate("2020-01-01", "yyyy-MM-dd");
//				Date debut_gratuite = DateUtils.parseDate("2020-04-01", "yyyy-MM-dd");
//			
//				 for(Subscriber s : list5) {
//					 _list = mobileMoneyDAO.getEntityManager().createQuery(req)
//								.setParameter("operation", TypeOperation.COMPTABILISATION)
//								.setParameter("subscriber", s)
//								.setParameter("succes", TransactionStatus.SUCCESS)
//								.setParameter("regul", TransactionStatus.REGUL)
//								.getResultList();
//					 
//					 if(!_list.isEmpty()) {
//							logger.info("ID: "+s.getId()+" DATE DERNIERE TRX: "+_list.get(0).getDate() + " Next date: "+DateUtils.addDays(_list.get(0).getDate(), 30));
//							if(_list.get(0).getDate().before(debut)) {
//								s.setDateSaveDernCompta(min);
//								s.setDateDernCompta(DateUtils.addDays(min, 90)); // 90jours de gratuité
//								s.setRegulfacturation(Boolean.TRUE);
//							}
//							else if(_list.get(0).getDate().after(debut) && _list.get(0).getDate().before(debut_gratuite)) {
//								s.setDateSaveDernCompta(_list.get(0).getDate());
//								s.setDateDernCompta(DateUtils.addDays(_list.get(0).getDate(), 90));
//								s.setRegulfacturation(Boolean.TRUE);
//							}
//							else {
//								s.setDateSaveDernCompta(DateUtils.addDays(_list.get(0).getDate(), -30));
//								s.setDateDernCompta(_list.get(0).getDate());
//								s.setRegulfacturation(Boolean.TRUE);
//							}
//					 }
//					 else {
//						s.setDateSaveDernCompta(min);
//						s.setDateDernCompta(DateUtils.addDays(min, 90));
//						s.setRegulfacturation(Boolean.TRUE);
//					 }
//					 
//					 whitelist.add(s);
//				  }
//			 } catch (ParseException e) {
//					e.printStackTrace();
//			 }
//			 
//			 mobileMoneyDAO.saveList(whitelist, true);
//			 list.addAll(whitelist);
//				
//			 whitelist.clear();
//	
			return list;
			
	 }

	
	/**
	 * MAJ des donnees avec le status REGUL de la table "comptabilisation" dans la table "transaction"
	 * 
	 * return le nombre de mise a jour
	 * 
	 */
	@SuppressWarnings("unchecked")
	public int updateDataFacturation() throws Exception {

		// Initialisation de la liste des transactions maj
		List<Transaction> list = new ArrayList<Transaction>();

		// Recuperation de la liste des abonnements a comptabiliser en regul select e.trx_id from momo_bkeve e where e.id in (select eve_id from momo_tfjo where status = 'REGUL')
		String req = "select e.transaction from bkeve e where e.id in (select eve from Comptabilisation where status = :statut)";
		List<Transaction> listTrx = mobileMoneyDAO.getEntityManager().createQuery(req).setParameter("statut", TransactionStatus.REGUL).getResultList();

		// MAJ des transactions de comptabilisation
		for(Transaction t : listTrx){
			// MAJ du status de la transaction
			if(t.getStatus().equals(TransactionStatus.PROCESSING)){
				t.setStatus(TransactionStatus.REGUL);
				//t.setStatus(TransactionStatus.PROCESSING);
				list.add(t);			
			}
		}
		mobileMoneyDAO.saveList(list, true);
		return list.size();
	}
		
	
	/**
	 * MAJ des EC des facturations en REGUL en prenant en compte les comptes de liaison
	 * 
	 * return le nombre de mise a jour
	 * 
	 */
	@SuppressWarnings("unchecked")
	public int updateECRegulsFacturation() throws Exception {

		// Initialisation de la liste des transactions maj
		List<bkeve> list = new ArrayList<bkeve>();
		List<bkeve> listIdem = new ArrayList<bkeve>();
		List<bkeve> listNoAccOrPhone = new ArrayList<bkeve>();
		List<bkeve> listNoAccInCBS = new ArrayList<bkeve>();
		List<bkeve> listAccPb = new ArrayList<bkeve>();
		
		List<Transaction> trx = new ArrayList<Transaction>();
		List<Transaction> trxIdem = new ArrayList<Transaction>();
		List<Transaction> trxNonEquilibre = new ArrayList<Transaction>();
		List<Transaction> trxNoAccOrPhone = new ArrayList<Transaction>();
		List<Transaction> trxNoAccInCBS = new ArrayList<Transaction>();
		List<Transaction> trxAccPb = new ArrayList<Transaction>();

		// Recuperation de la liste des reguls
		List<Transaction> listTrx = mobileMoneyDAO.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.in("typeOperation", new TypeOperation[]{TypeOperation.SUBSCRIPTION, TypeOperation.COMPTABILISATION} )).add(Restrictions.eq("status", TransactionStatus.REGUL)).add(Restrictions.or(Restrictions.isNull("majEc"), Restrictions.eq("majEc", Boolean.FALSE))), OrderContainer.getInstance().add(Order.desc("dateCompta")), null, 0, 10000);
		
		Map<Long,Transaction> mapTrans = new HashMap<Long,Transaction>();
		for(Transaction t : listTrx) mapTrans.put(t.getId(),t);
		
		// age, obj
		Map<String,AccountInfos> mapComptesLiaison = new HashMap<String,AccountInfos>();
		
		// Recherche des parametres
		params = findParameters();

		// Initialisation de DataStore d'Amplitude
		if(dsCBS == null) findCBSDataSystem();

		// Initialisations des ResultSets
		ResultSet rsCpteAbonne = null, rsCpteComs = null, rsCpteTVA = null, rsLiaisonAbonne = null, rsLiaisonComs = null, rsLiaisonTva = null;
		AccountInfos  cpteLiaisonAbonne = null;
		// Initialisations
		Long numEc = 1l;
		Date dco = getDateComptable(dsCBS);
		Date dvaDebit = getDvaDebit();

		//Long numEve = getLastEveNum(dsCBS);

		/**
		 * *******************************************************************
		 * RECUPERATION DES COMPTES DE COMMISSIONS ET DE TAXES DANS DELTA
		 * *******************************************************************
		 */

		// Si le compte des commissions a ete parametre
		if(params.getNumCompteCommissions() != null && !params.getNumCompteCommissions().isEmpty()) 
			// Recuperation du numero de cpte des commissions
			rsCpteComs = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(7).getQuery(), new Object[]{ params.getNumCompteCommissions().split("-")[0], params.getNumCompteCommissions().split("-")[1], params.getNumCompteCommissions().split("-")[2] });

		if(rsCpteComs != null) rsCpteComs.next();

		// Si le numero de cpte TVA a ete parametre
		if(params.getNumCompteTVA() != null && !params.getNumCompteTVA().isEmpty())
			// Recuperation du numero de compte TVA
			rsCpteTVA = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(7).getQuery(), new Object[]{ params.getNumCompteTVA().split("-")[0], params.getNumCompteTVA().split("-")[1], params.getNumCompteTVA().split("-")[2] });

		if(rsCpteTVA != null) rsCpteTVA.next();

		// Recuperation du compte de liaison l'agence des commissions
		rsLiaisonComs = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='" +params.getNumCompteCommissions().split("-")[0]+ "' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
		if(rsLiaisonComs != null) rsLiaisonComs.next();
		
		// Recuperation du compte de liaison de l'agence des taxes
		rsLiaisonTva = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='" +params.getNumCompteTVA().split("-")[0]+ "' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
		if(rsLiaisonTva != null) rsLiaisonTva.next();
			
		
		//logger.info("Recuperation des evenements");
		List<bkeve> eves =  new ArrayList<bkeve>();
		int max = 2500;
		int end = (listTrx.size()/max);
		//logger.info("DATA SIZE : "+data.size());
		logger.info("END : "+end);
		for(int i = 0; i <= end; i++){
			// Parcours de la liste des reguls
			logger.info("i : "+i);
			if(i==end){
				if(max*i!=listTrx.size()){
					eves.addAll(mobileMoneyDAO.filter(bkeve.class, null, 
							RestrictionsContainer.getInstance().add(Restrictions.in("transaction", new ArrayList<Transaction>(listTrx.subList(max*i, listTrx.size())))), 
							null, null, 0, -1));
					//logger.info(data.size()+" EVES");
				}
			}
			else {
				eves.addAll(mobileMoneyDAO.filter(bkeve.class, null, 
						RestrictionsContainer.getInstance().add(Restrictions.in("transaction", new ArrayList<Transaction>(listTrx.subList(max*i, (max*(i+1)))))), 
						null, null, 0, -1));
				//logger.info(max*(i+1)+" EVES");
			}

			//logger.info("Parcours des evenements");
			for(bkeve eve : eves) {
	
				Transaction tx = mapTrans.get(eve.getTransaction().getId());
	
				// Si l'objet courant a ete selectionne par l'utilisateur et ses ecritures sont equilibrees
				if(eve.isEquilibre()) {
					
					dco = eve.getDco();
					String datop = eve.getLib1().split("/")[0];
					// ----
					Subscriber s = tx.getSubscriber();
		
					if(s.getFirstAccount()!= null && s.getFirstPhone()!= null){
						logger.info("ACCOUNT : "+s.getFirstAccount());
						// Recherche du cpte de l'abonne
						rsCpteAbonne = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(6).getQuery(), new Object[]{ tx.getAccount().split("-")[0], tx.getAccount().split("-")[1], tx.getAccount().split("-")[2] });
		
						// Si le cpte de l'abonne existe dans DELTA
						if(rsCpteAbonne != null && rsCpteAbonne.next()) {
		
//						if(rsCpteAbonne.getString("ife").equals("N") && rsCpteAbonne.getString("cfe").equals("N") && rsCpteAbonne.getString("cha").startsWith("37")){
							Boolean maj = Boolean.FALSE;
							
							if(!mapComptesLiaison.containsKey(rsCpteAbonne.getString("age"))){
								logger.info("LIAISON ABONNE PAS DANS LA MAP");
								// Recuperation du compte de liaison de l'agence du client
								rsLiaisonAbonne = executeFilterSystemQuery(dsCBS, "select age, dev, cha, ncp, suf, clc, dva, inti, sde, utic from bkcom where age='"+ rsCpteAbonne.getString("age") +"' and dev='001' and ncp='"+ params.getNumCompteLiaison() +"'", null);
								// Si le compte de liaison existe
								if(rsLiaisonAbonne != null) rsLiaisonAbonne.next();
								cpteLiaisonAbonne = new AccountInfos(rsLiaisonAbonne.getString("age"), rsLiaisonAbonne.getString("ncp"), rsLiaisonAbonne.getString("clc"), rsLiaisonAbonne.getString("dev"), rsLiaisonAbonne.getString("cha"),  rsLiaisonAbonne.getString("suf"), rsLiaisonAbonne.getDate("dva"));
								mapComptesLiaison.put(rsCpteAbonne.getString("age"), cpteLiaisonAbonne);
								logger.info("LIAISON ABONNE : "+cpteLiaisonAbonne);
							} 
							else {
								cpteLiaisonAbonne = mapComptesLiaison.get(rsCpteAbonne.getString("age"));
								logger.info("LIAISON ABONNE DANS LA MAP : "+cpteLiaisonAbonne);
							}
														
							// Si l'agence du compte de credit a change
							if(!StringUtils.equalsIgnoreCase(eve.getAge2(), rsCpteComs.getString("age"))){
								maj = Boolean.TRUE;
								logger.info("MAJ EVE (CREDITEUR)");
								// MAJ du crediteur (2)
								eve.updateCrediteur(rsCpteComs.getString("age"), rsCpteComs.getString("dev"), rsCpteComs.getString("ncp"), rsCpteComs.getString("suf"), rsCpteComs.getString("clc"), rsCpteComs.getString("cli"), rsCpteComs.getString("inti"), "   ", rsCpteComs.getDouble("sde"));
							}
							else logger.info("EVE OK");
														
							/***-**************************************
							 * *** MAJ DES ECRITURES COMPTABLES
							 * *****************************************
							 */
	
							// Si les comptes sont tous dans la meme agence
//							if( StringUtils.equalsIgnoreCase(rsCpteAbonne.getString("age"), rsCpteComs.getString("age")) 
//									&& StringUtils.equalsIgnoreCase(rsCpteAbonne.getString("age"), rsCpteTVA.getString("age"))){
							List<bkmvti> ecritures = eve.getEcritures();
							logger.info("MEME AGENCE");
							// Maj des ec
							for(bkmvti ec : ecritures){
								// S'il s'agit de l'EC des commissions et l'agence du compte a change
								if(!StringUtils.equalsIgnoreCase(ec.getNcp(), rsCpteAbonne.getString("ncp"))
										&& !StringUtils.equalsIgnoreCase(ec.getAge(), rsCpteComs.getString("age")) 
										&& ec.getLib().contains("COM")){
									logger.info("COMPTE COMMISSIONS MAJ");
									maj = Boolean.TRUE;
									// MAJ de l'EC des commissions (sauf montants)
									ec.updateBkmvti(rsCpteComs.getString("age"), rsCpteComs.getString("dev"), rsCpteComs.getString("cha"), rsCpteComs.getString("ncp"), rsCpteComs.getString("suf"), rsCpteComs.getString("clc"), 0d, rsCpteComs.getString("age"), rsCpteComs.getString("dev"), 0d);
								}
								// S'il s'agit de l'EC des taxes et l'agence du compte a change
								else if(!StringUtils.equalsIgnoreCase(ec.getNcp(), rsCpteAbonne.getString("ncp"))
										&& !StringUtils.equalsIgnoreCase(ec.getAge(), rsCpteTVA.getString("age"))
										&& ec.getLib().contains("TAX")){
									maj = Boolean.TRUE;
									logger.info("COMPTE TAXES MAJ");
									// MAJ de l'EC des taxes (sauf montants)
									ec.updateBkmvti(rsCpteTVA.getString("age"), rsCpteTVA.getString("dev"), rsCpteTVA.getString("cha"), rsCpteTVA.getString("ncp"), rsCpteTVA.getString("suf"), rsCpteTVA.getString("clc"), 0d, rsCpteTVA.getString("age"), rsCpteTVA.getString("dev"), 0d);
								}
							}
								
//							}
							// Si le compte de commissions de une agence differente de cette de l'abonne
							if( !StringUtils.equalsIgnoreCase(rsCpteAbonne.getString("age"), rsCpteComs.getString("age")) ){
//								List<bkmvti> ecritures = eve.getEcritures();
								logger.info("COMPTE COMMISSIONS DANS AGENCE DIFFERENTE : "+rsCpteComs.getString("age"));
								
								// Il n'a pas d'EC de liaison
								if(ecritures.size()==3){
									numEc = 4L;
									maj = Boolean.TRUE;
									logger.info("NBRE EC = 3 : AJOUT EC LIAISON ABONNE/COMS");
									// Ajout des EC des comptes de liaison
									// Credit de la liaison du client du montant des commissions
									if(cpteLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(cpteLiaisonAbonne.getAge(),cpteLiaisonAbonne.getDev(), cpteLiaisonAbonne.getCha(),cpteLiaisonAbonne.getNcp(),cpteLiaisonAbonne.getSuf(), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), cpteLiaisonAbonne.getClc(), dco, null, cpteLiaisonAbonne.getDva(), tx.getCommissions(), "C", "LS COM MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, cpteLiaisonAbonne.getAge(),cpteLiaisonAbonne.getDev(), tx.getCommissions(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
									// Debit de la liaison des commissions
									if(rsLiaisonComs != null) eve.getEcritures().add( new bkmvti(rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), rsLiaisonComs.getString("cha"), rsLiaisonComs.getString("ncp"), rsLiaisonComs.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonComs.getString("clc"), dco, null, rsLiaisonComs.getDate("dva"), tx.getCommissions(), "D", "LS COM MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), tx.getCommissions(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
									
								}
								// Il y'a les EC de liaison vers Hippodrome
								else if(ecritures.size()==5){
									// MAJ des EC des comptes de liaison
									for(bkmvti ec : ecritures){
										// S'il s'agit de l'EC de liaison de l'abonne
										if(StringUtils.equalsIgnoreCase(ec.getNcp(), cpteLiaisonAbonne.getNcp())
												&& StringUtils.equalsIgnoreCase(ec.getAge(), cpteLiaisonAbonne.getAge())){
											logger.info("NBRE EC = 5 : MAJ EC LIAISON ABONNE");
											maj = Boolean.TRUE;
											// MAJ du montant de l'EC de liaison du client
											ec.setMon(tx.getCommissions());
											ec.setMctv(tx.getCommissions());
										}
										// S'il s'agit de l'EC de la liaison des commissions
										else if(StringUtils.equalsIgnoreCase(ec.getNcp(), rsLiaisonComs.getString("ncp"))
												&& StringUtils.equalsIgnoreCase(ec.getAge(), rsLiaisonComs.getString("age"))){
											logger.info("NBRE EC = 5 : MAJ EC LIAISON COMS");
											maj = Boolean.TRUE;
											// MAJ du montant et du compte de l'EC de liaison des commissions
											ec.updateBkmvti(rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), rsLiaisonComs.getString("cha"), rsLiaisonComs.getString("ncp"), rsLiaisonComs.getString("suf"), rsLiaisonComs.getString("clc"), tx.getCommissions(), rsLiaisonComs.getString("age"), rsLiaisonComs.getString("dev"), tx.getCommissions());
										}
									}
								}
								
							}
							// Si le compte de taxes de une agence differente de cette de l'abonne
							if( !StringUtils.equalsIgnoreCase(rsCpteAbonne.getString("age"), rsCpteTVA.getString("age")) ){
//								List<bkmvti> ecritures = eve.getEcritures();
								logger.info("COMPTE TAXES DANS AGENCE DIFFERENTE : "+rsCpteTVA.getString("age"));
								
								// Il n'a pas d'EC de liaison
								if(ecritures.size()==3){
									numEc = 4L;
									maj = Boolean.TRUE;
									logger.info("NBRE EC = 3 : AJOUT EC LIAISON ABONNE/TAXES");
									// Ajout des EC des comptes de liaison
									// Credit de la liaison du client du montant des commissions
									if(cpteLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(cpteLiaisonAbonne.getAge(),cpteLiaisonAbonne.getDev(), cpteLiaisonAbonne.getCha(),cpteLiaisonAbonne.getNcp(),cpteLiaisonAbonne.getSuf(), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), cpteLiaisonAbonne.getClc(), dco, null, cpteLiaisonAbonne.getDva(), tx.getTaxes(), "C", "LS TAX MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, cpteLiaisonAbonne.getAge(),cpteLiaisonAbonne.getDev(), tx.getTaxes(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
									// Debit de la liaison des commissions
									if(rsLiaisonTva != null) eve.getEcritures().add( new bkmvti(rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), rsLiaisonTva.getString("cha"), rsLiaisonTva.getString("ncp"), rsLiaisonTva.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonTva.getString("clc"), dco, null, rsLiaisonTva.getDate("dva"), tx.getTaxes(), "D", "LS TAX MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), tx.getTaxes(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
									
								}
								// Il y'a les EC de liaison vers Hippodrome
								else if(ecritures.size()==5){
									// MAJ des EC des comptes de liaison
									if( !StringUtils.equalsIgnoreCase(rsCpteAbonne.getString("age"), rsCpteComs.getString("age")) ){
										numEc = 6L;
										logger.info("NBRE EC = 5 : AJOUT EC LIAISON ABONNE/TAXES");
										maj = Boolean.TRUE;
										// Ajout des EC des comptes de liaison
										// Credit de la liaison du client du montant des commissions
										if(cpteLiaisonAbonne != null) eve.getEcritures().add( new bkmvti(cpteLiaisonAbonne.getAge(),cpteLiaisonAbonne.getDev(), cpteLiaisonAbonne.getCha(),cpteLiaisonAbonne.getNcp(),cpteLiaisonAbonne.getSuf(), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), cpteLiaisonAbonne.getClc(), dco, null, cpteLiaisonAbonne.getDva(), tx.getTaxes(), "C", "LS TAX MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, cpteLiaisonAbonne.getAge(),cpteLiaisonAbonne.getDev(), tx.getTaxes(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
										// Debit de la liaison des commissions
										if(rsLiaisonTva != null) eve.getEcritures().add( new bkmvti(rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), rsLiaisonTva.getString("cha"), rsLiaisonTva.getString("ncp"), rsLiaisonTva.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsLiaisonTva.getString("clc"), dco, null, rsLiaisonTva.getDate("dva"), tx.getTaxes(), "D", "LS TAX MAC/" + datop + "/" + (tx.getPhoneNumber() != null ? tx.getPhoneNumber().replaceAll("237", "") : tx.getPhoneNumber()) + "/" + (s.getCustomerName().trim().length()>=8 ? s.getCustomerName().trim().toUpperCase().substring(0, 8) : s.getCustomerName().trim().toUpperCase()), "O", s.getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), tx.getTaxes(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) );  numEc++;
										
									}
									else{
										for(bkmvti ec : ecritures){
											// S'il s'agit de l'EC de liaison de l'abonne
											if(StringUtils.equalsIgnoreCase(ec.getNcp(), cpteLiaisonAbonne.getNcp())
													&& StringUtils.equalsIgnoreCase(ec.getAge(), cpteLiaisonAbonne.getAge())){
												logger.info("NBRE EC = 5 : MAJ EC LIAISON ABONNE");
												maj = Boolean.TRUE;
												// MAJ du montant de l'EC de liaison du client
												ec.setMon(tx.getTaxes());
												ec.setMctv(tx.getTaxes());
											}
											// S'il s'agit de l'EC de la liaison des taxes
											else if(StringUtils.equalsIgnoreCase(ec.getNcp(), rsLiaisonTva.getString("ncp"))
													&& StringUtils.equalsIgnoreCase(ec.getAge(), rsLiaisonTva.getString("age"))){
												logger.info("NBRE EC = 5 : MAJ EC LIAISON TAXES");
												maj = Boolean.TRUE;
												// MAJ du montant et du compte de l'EC de liaison des commissions
												ec.updateBkmvti(rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), rsLiaisonTva.getString("cha"), rsLiaisonTva.getString("ncp"), rsLiaisonTva.getString("suf"), rsLiaisonTva.getString("clc"), tx.getCommissions(), rsLiaisonTva.getString("age"), rsLiaisonTva.getString("dev"), tx.getCommissions());
											}
										}
									}
								}
								
							}
							if(maj) {
								logger.info("MAJ OK");
								list.add(eve);
								
								if(!rsCpteAbonne.getString("ife").equals("N")){
									tx.setMsgMajEc("COMPTE EN INSTANCE DE FERMETURE");
									tx.setStatus(TransactionStatus.CLOSE);
									s.setStatus(StatutContrat.SUSPENDU);
									s.setUtiSuspendu("AUTO");
									s.setDateSuspendu(new Date());
									mobileMoneyDAO.update(s);
								}
								else if(!rsCpteAbonne.getString("cfe").equals("N")){
									tx.setMsgMajEc("COMPTE FERME");
									tx.setStatus(TransactionStatus.CLOSE);
									s.setStatus(StatutContrat.SUSPENDU);
									s.setUtiSuspendu("AUTO");
									s.setDateSuspendu(new Date());
									mobileMoneyDAO.update(s);
								}
								else if(!rsCpteAbonne.getString("cha").startsWith("37")){
									tx.setMsgMajEc("COMPTE DANS LE CHAPITRE "+rsCpteAbonne.getString("cha"));
								}
								else tx.setMsgMajEc("OK");
								
								tx.setMajEc(Boolean.TRUE);
								tx.setDateMajEc(new Date());
								trx.add(tx);
							}
							else{
								logger.info("IDEM ");
								listIdem.add(eve);
								
								tx.setMsgMajEc("OK");
								tx.setMajEc(Boolean.TRUE);
								tx.setDateMajEc(new Date());
								trxIdem.add(tx);
							}
//						}
//						// Si le cpte de l'abonne existe dans le CBS mais a un probleme
//						else{
//							logger.info("COMPTE A PROBLEME : "+rsCpteAbonne.toString());
//							if(rsCpteAbonne.getString("ife").equals("O")){
//								tx.setMsgMajEc("IFE");
//							}
//							else if(rsCpteAbonne.getString("cfe").equals("O")){
//								tx.setMsgMajEc("CFE");
//							}
//							else if(!rsCpteAbonne.getString("cha").startsWith("37")){
//								tx.setMsgMajEc("CHA "+rsCpteAbonne.getString("cha"));
//							}
////								listIdem.add(eve);
//							tx.setStatus(TransactionStatus.CLOSE);
//							s.setStatus(StatutContrat.SUSPENDU);
//							s.setUtiSuspendu("AUTO");
//							s.setDateSuspendu(new Date());
//							mobileMoneyDAO.update(s);
//							mobileMoneyDAO.update(tx);
//							tx.setMajEc(Boolean.FALSE);
//							tx.setDateMajEc(new Date());
//							trxAccPb.add(tx);
//						}
						}
						// Si le cpte de l'abonne n'existe pas dans le CBS
						else{
							logger.info("COMPTE NON TROUVE DANS LE CBS");
							// Ajout de la transaction dans la liste des transactions a cloturer
//							tx.setStatus(TransactionStatus.CLOSE);
//							s.setStatus(StatutContrat.SUSPENDU);
//							s.setUtiSuspendu("AUTO");
//							s.setDateSuspendu(new Date());
//							mobileMoneyDAO.update(s);
//							mobileMoneyDAO.update(tx);
							
							listNoAccInCBS.add(eve);
							tx.setMsgMajEc("COMPTE INEXISTANT DANS LE CBS");
							tx.setMajEc(Boolean.FALSE);
							tx.setDateMajEc(new Date());
							trxNoAccInCBS.add(tx);
						}
					}
					// Si le cpte de l'abonne ou son numero de telephone est nul
					else{
						logger.info("COMPTE OU PHONE NON DEFINI : ");
						listNoAccOrPhone.add(eve);
						
						tx.setStatus(TransactionStatus.CLOSE);
						s.setStatus(StatutContrat.SUSPENDU);
						s.setUtiSuspendu("AUTO");
						s.setDateSuspendu(new Date());
						mobileMoneyDAO.update(s);
						
						tx.setMsgMajEc("COMPTE OU TELEPHONE NON DEFINI");
						tx.setMajEc(Boolean.TRUE);
						tx.setDateMajEc(new Date());
						trxNoAccOrPhone.add(tx);
					}
				}
				// Eve non equilibre
				else{
					logger.info("EVE NON EQUILIBRE : "+eve.toString());
//					listIdem.add(eve);
					trxNonEquilibre.add(tx);
				}
//				
				if(rsCpteAbonne != null) {
					rsCpteAbonne.close(); 
					if(rsCpteAbonne.getStatement() != null) {
						rsCpteAbonne.getStatement().close();
					}
				}
				if(rsLiaisonAbonne != null) {
					rsLiaisonAbonne.close(); 
					if(rsLiaisonAbonne.getStatement() != null) {
						rsLiaisonAbonne.getStatement().close();
					}
				}
				
			}
//			if(rsCpteAbonne != null) rsCpteAbonne.getStatement().close(); rsCpteAbonne = null;
//			if(rsLiaisonAbonne != null) rsLiaisonAbonne.getStatement().close(); rsLiaisonAbonne = null;
			logger.info("LIBERATION EVES");
			eves.clear();
			eves = new ArrayList<bkeve>();
		}		
		//logger.info("Fin de la recuperation des evenements");

		listTrx.clear();
		listTrx = new ArrayList<Transaction>();

		// On libere les variables
		if(rsCpteComs != null) {
			rsCpteComs.close(); 
			if(rsCpteComs.getStatement() != null) {
				rsCpteComs.getStatement().close();
			}
			rsCpteComs = null;
		}
		if(rsCpteTVA != null) {
			rsCpteTVA.close(); 
			if(rsCpteTVA.getStatement() != null) {
				rsCpteTVA.getStatement().close();
			}
			rsCpteTVA = null;
		}
		if(rsLiaisonComs != null) {
			rsLiaisonComs.close(); 
			if(rsLiaisonComs.getStatement() != null) {
				rsLiaisonComs.getStatement().close();
			}
			rsLiaisonComs = null;
		}
		if(rsLiaisonTva != null) {
			rsLiaisonTva.close(); 
			if(rsLiaisonTva.getStatement() != null) {
				rsLiaisonTva.getStatement().close();
			}
			rsLiaisonTva = null;
		}
		
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		logger.info("UPDATING .......................");
		mobileMoneyDAO.saveList(list, true);
		mobileMoneyDAO.saveList(trx, true);
		mobileMoneyDAO.saveList(trxIdem, true);
		mobileMoneyDAO.saveList(trxAccPb, true);
		mobileMoneyDAO.saveList(trxNoAccOrPhone, true);
		mobileMoneyDAO.saveList(trxNoAccInCBS, true);
		logger.info("UPDATE OK");
		
		exportECRegulsFactIntoExcelFile(trx, list, trxIdem, listIdem, trxNoAccInCBS, trxNoAccOrPhone, listNoAccInCBS, listNoAccOrPhone);
		
		// Liberation des variables
		trxIdem.clear();
		listIdem.clear();
		trxAccPb.clear();
		trxNoAccInCBS.clear();
		trxNoAccOrPhone.clear();
		trxNonEquilibre.clear();
		listAccPb.clear();
		listNoAccInCBS.clear();
		listNoAccOrPhone.clear();
		
		return list.size();
	}
	
	
	public void exportECRegulsFactIntoExcelFile(List<Transaction> trxMaj, List<bkeve> list, List<Transaction> trxIdem, List<bkeve> listIdem, 
			List<Transaction> trxNoAccInCBS, List<Transaction> trxNoAccOrPhone, List<bkeve> listNoAccInCBS, List<bkeve> listNoAccOrPhone) throws Exception {
		logger.info("DEBUT GENERATION FICHIER");
		// Initialisation d'un document Excel
		SXSSFWorkbook wb = new SXSSFWorkbook();

		// Initialisation de la Feuille courante
		Sheet sheet  = wb.createSheet("EC REGUL MAJ");

		// Creation d'une ligne
		Row row = sheet.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
		row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "N° Mvt" );
		row.createCell(2).setCellValue( "Agence" );
		row.createCell(3).setCellValue( "Date Comptable" );
		row.createCell(4).setCellValue( "Libellé" );
		row.createCell(5).setCellValue( "Sens" );
		row.createCell(6).setCellValue( "N° de Cpte" );
		row.createCell(7).setCellValue( "Intitulé" );
		row.createCell(8).setCellValue( "Montant" );
		row.createCell(9).setCellValue( "Opération" );
		row.createCell(10).setCellValue( "N° de Pièce" );
		row.createCell(11).setCellValue( "Réf. de Lettrage" );

		// Initialisation du compteur
		int i = 1;
		for(bkeve eve : list){
			for(bkmvti ec : eve.getEcritures()){

			// Initialisation d'une ligne
			row = sheet.createRow(i);

			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(1).setCellValue( ec.getMvti() );
			row.createCell(2).setCellValue( ec.getAge() );
			row.createCell(3).setCellValue( ec.getDco() );
			row.createCell(4).setCellValue( ec.getLib() );
			row.createCell(5).setCellValue( ec.getSen() );
			row.createCell(6).setCellValue( ec.getNcp() );
			row.createCell(7).setCellValue( ec.getLabel() );
			row.createCell(8).setCellValue( ec.getMon() );
			row.createCell(9).setCellValue( ec.getOpe() );
			row.createCell(10).setCellValue( ec.getPie() );
			row.createCell(11).setCellValue( ec.getRlet() );

		}
		}
		

		/**
		 * DEUXIEME FEUILLE
		 */

		// Initialisation de la Feuille courante
		Sheet sheet2  = wb.createSheet("TRX REGUL MAJ");

		// Creation d'une ligne
		row = sheet2.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
		row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "N° de compte" );
		row.createCell(2).setCellValue( "Client" );
		row.createCell(3).setCellValue( "Date Abonnement" );
		row.createCell(4).setCellValue( "Date Facturation" );
		row.createCell(5).setCellValue( "Sens" );
		row.createCell(6).setCellValue( "Montant" );
		row.createCell(7).setCellValue( "Taxes" );
		row.createCell(8).setCellValue( "Agence" );
		row.createCell(9).setCellValue( "Message");

		// Initialisation du compteur
		i = 1;

		// Parcours des transactions
		for(Transaction tx : trxMaj) {

			// Initialisation d'une ligne
			row = sheet2.createRow(i);

			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(1).setCellValue( tx.getSubscriber().getFirstAccount() );
			row.createCell(2).setCellValue( tx.getSubscriber().getCustomerName() );
			row.createCell(3).setCellValue( tx.getSubscriber().getDate() );
			if(null!=tx.getDateCompta()) row.createCell(4).setCellValue( tx.getDateCompta());
			else row.createCell(4).setCellValue( "" );
			row.createCell(5).setCellValue( "D" );
			row.createCell(6).setCellValue( tx.getAmount() + tx.getCommissions() );
			row.createCell(7).setCellValue( tx.getTaxes() );
			row.createCell(8).setCellValue( tx.getSubscriber().getFirstAccount().substring(0, 5) );
			row.createCell(9).setCellValue( tx.getMsgMajEc());
		}

		/**
		 * TROISIEME FEUILLE
		 */
		// Initialisation de la Feuille courante
		Sheet sheet3  = wb.createSheet("EC REGUL IDEM");

		// Creation d'une ligne
		row = sheet3.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
		row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "N° Mvt" );
		row.createCell(2).setCellValue( "Agence" );
		row.createCell(3).setCellValue( "Date Comptable" );
		row.createCell(4).setCellValue( "Libellé" );
		row.createCell(5).setCellValue( "Sens" );
		row.createCell(6).setCellValue( "N° de Cpte" );
		row.createCell(7).setCellValue( "Intitulé" );
		row.createCell(8).setCellValue( "Montant" );
		row.createCell(9).setCellValue( "Opération" );
		row.createCell(10).setCellValue( "N° de Pièce" );
		row.createCell(11).setCellValue( "Réf. de Lettrage" );

		// Initialisation du compteur
		i = 1;
		
		for(bkeve eve : listIdem){
			for(bkmvti ec : eve.getEcritures()){

				// Initialisation d'une ligne
				row = sheet3.createRow(i);
	
				// Affichage des colonnes dans la fichier excel
				row.createCell(0).setCellValue( i++ );
				row.createCell(1).setCellValue( ec.getMvti() );
				row.createCell(2).setCellValue( ec.getAge() );
				row.createCell(3).setCellValue( ec.getDco() );
				row.createCell(4).setCellValue( ec.getLib() );
				row.createCell(5).setCellValue( ec.getSen() );
				row.createCell(6).setCellValue( ec.getNcp() );
				row.createCell(7).setCellValue( ec.getLabel() );
				row.createCell(8).setCellValue( ec.getMon() );
				row.createCell(9).setCellValue( ec.getOpe() );
				row.createCell(10).setCellValue( ec.getPie() );
				row.createCell(11).setCellValue( ec.getRlet() );
	
			}
		}
		
		i++;
		
		// Creation d'une ligne
		row = sheet3.createRow(i);

		i++;
		
		// Affichage des entetes de colonnes du fichier excel trxAccPb, trxNoAccInCBS, trxNoAccOrPhone, trxNonEquilibre
		row.createCell(0).setCellValue( "EC DES TRX DONT LE NUMERO DE COMPTE N'EXISTE PAS DANS LE CBS" );

		for(bkeve eve : listNoAccInCBS){
			for(bkmvti ec : eve.getEcritures()){

				// Initialisation d'une ligne
				row = sheet3.createRow(i);
	
				// Affichage des colonnes dans la fichier excel
				row.createCell(0).setCellValue( i++ );
				row.createCell(1).setCellValue( ec.getMvti() );
				row.createCell(2).setCellValue( ec.getAge() );
				row.createCell(3).setCellValue( ec.getDco() );
				row.createCell(4).setCellValue( ec.getLib() );
				row.createCell(5).setCellValue( ec.getSen() );
				row.createCell(6).setCellValue( ec.getNcp() );
				row.createCell(7).setCellValue( ec.getLabel() );
				row.createCell(8).setCellValue( ec.getMon() );
				row.createCell(9).setCellValue( ec.getOpe() );
				row.createCell(10).setCellValue( ec.getPie() );
				row.createCell(11).setCellValue( ec.getRlet() );
	
			}
		}
		
		i++;
		
		// Creation d'une ligne
		row = sheet3.createRow(i);

		// Affichage des entetes de colonnes du fichier excel trxAccPb, trxNoAccInCBS, trxNoAccOrPhone, trxNonEquilibre
		row.createCell(0).setCellValue( "EC DES TRX DONT LE COMPTE OU LE NUMERO DE TELEPHONE N'EST PAS DEFINI" );
		
		i++;
		
		for(bkeve eve : listNoAccOrPhone){
			for(bkmvti ec : eve.getEcritures()){

				// Initialisation d'une ligne
				row = sheet3.createRow(i);
	
				// Affichage des colonnes dans la fichier excel
				row.createCell(0).setCellValue( i++ );
				row.createCell(1).setCellValue( ec.getMvti() );
				row.createCell(2).setCellValue( ec.getAge() );
				row.createCell(3).setCellValue( ec.getDco() );
				row.createCell(4).setCellValue( ec.getLib() );
				row.createCell(5).setCellValue( ec.getSen() );
				row.createCell(6).setCellValue( ec.getNcp() );
				row.createCell(7).setCellValue( ec.getLabel() );
				row.createCell(8).setCellValue( ec.getMon() );
				row.createCell(9).setCellValue( ec.getOpe() );
				row.createCell(10).setCellValue( ec.getPie() );
				row.createCell(11).setCellValue( ec.getRlet() );
	
			}
		}
		

		/**
		 * QUATRIEME FEUILLE
		 */

		// Initialisation de la Feuille courante
		Sheet sheet4  = wb.createSheet("TRX REGUL IDEM");

		// Creation d'une ligne
		row = sheet4.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
		row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "N° de compte" );
		row.createCell(2).setCellValue( "Client" );
		row.createCell(3).setCellValue( "Date Abonnement" );
		row.createCell(4).setCellValue( "Date Facturation" );
		row.createCell(5).setCellValue( "Sens" );
		row.createCell(6).setCellValue( "Montant" );
		row.createCell(7).setCellValue( "Taxes" );
		row.createCell(8).setCellValue( "Agence" );
		row.createCell(9).setCellValue( "Message");

		// Initialisation du compteur
		i = 1;

		// Parcours des transactions
		for(Transaction tx : trxIdem) {

			// Initialisation d'une ligne
			row = sheet4.createRow(i);

			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(1).setCellValue( tx.getSubscriber().getFirstAccount() );
			row.createCell(2).setCellValue( tx.getSubscriber().getCustomerName() );
			row.createCell(3).setCellValue( tx.getSubscriber().getDate() );
			if(null!=tx.getDateCompta()) row.createCell(4).setCellValue( tx.getDateCompta());
			else row.createCell(4).setCellValue( "" );
			row.createCell(5).setCellValue( "D" );
			row.createCell(6).setCellValue( tx.getAmount() + tx.getCommissions() );
			row.createCell(7).setCellValue( tx.getTaxes() );
			row.createCell(8).setCellValue( tx.getSubscriber().getFirstAccount().substring(0, 5) );
			row.createCell(9).setCellValue( tx.getMsgMajEc());
		}
		
		i++;
		
		// Creation d'une ligne
		row = sheet4.createRow(i);

		// Affichage des entetes de colonnes du fichier excel trxAccPb, trxNoAccInCBS, trxNoAccOrPhone, trxNonEquilibre
		row.createCell(0).setCellValue( "TRX DONT LE NUMERO DE COMPTE N'EXISTE PAS DANS LE CBS" );
		
		i++;
		
		// Parcours des transactions
		for(Transaction tx : trxNoAccInCBS) {

			// Initialisation d'une ligne
			row = sheet4.createRow(i);

			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(1).setCellValue( tx.getSubscriber().getFirstAccount() );
			row.createCell(2).setCellValue( tx.getSubscriber().getCustomerName() );
			row.createCell(3).setCellValue( tx.getSubscriber().getDate() );
			if(null!=tx.getDateCompta()) row.createCell(4).setCellValue( tx.getDateCompta());
			else row.createCell(4).setCellValue( "" );
			row.createCell(5).setCellValue( "D" );
			row.createCell(6).setCellValue( tx.getAmount() + tx.getCommissions() );
			row.createCell(7).setCellValue( tx.getTaxes() );
			row.createCell(8).setCellValue( tx.getSubscriber().getFirstAccount().substring(0, 5) );
			row.createCell(9).setCellValue( tx.getMsgMajEc());
		}
		
		i++;
		
		// Creation d'une ligne
		row = sheet4.createRow(i);

		// Affichage des entetes de colonnes du fichier excel trxAccPb, trxNoAccInCBS, trxNoAccOrPhone, trxNonEquilibre
		row.createCell(0).setCellValue( "TRX SANS NUMERO DE COMPTE OU TELEPHONE" );
		
		i++;
		
		// Parcours des transactions
		for(Transaction tx : trxNoAccOrPhone) {

			// Initialisation d'une ligne
			row = sheet4.createRow(i);

			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(1).setCellValue( tx.getSubscriber().getFirstAccount() );
			row.createCell(2).setCellValue( tx.getSubscriber().getCustomerName() );
			row.createCell(3).setCellValue( tx.getSubscriber().getDate() );
			if(null!=tx.getDateCompta()) row.createCell(4).setCellValue( tx.getDateCompta());
			else row.createCell(4).setCellValue( "" );
			row.createCell(5).setCellValue( "D" );
			row.createCell(6).setCellValue( tx.getAmount() + tx.getCommissions() );
			row.createCell(7).setCellValue( tx.getTaxes() );
			row.createCell(8).setCellValue( tx.getSubscriber().getFirstAccount().substring(0, 5) );
			row.createCell(9).setCellValue( tx.getMsgMajEc());
		}
				
		logger.info("FIN GENERATION FICHIER");
		logger.info("DEBUT SAUVEGARDE ");
		// Sauvegarde du fichier
		FileOutputStream fileOut = new FileOutputStream(PortalHelper.JBOSS_DATA_DIR + File.separator + PortalHelper.PORTAL_RESOURCES_DATA_DIR + File.separator + PortalHelper.PORTAL_DOWNLOAD_DATA_DIR + File.separator + "EC_MAJ_REGULS_"+new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())+".xls");
		wb.write(fileOut);
		fileOut.close();
		logger.info("FIN SAUVEGARDE ");
	}
	
	
	public static double round(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.UP); // Pour les banquiers
		return bd.doubleValue();
	}


	@Override
	public List<Transaction> filterTransactionARetraiter() {
		// TODO Auto-generated method stub
		
		RestrictionsContainer rc = RestrictionsContainer.getInstance();

		// verifier au lieu de completed ???
		rc.add(Restrictions.eq("aRetraiter",Boolean.TRUE));
		rc.add(Restrictions.eq("posted",Boolean.FALSE));
		rc.add(Restrictions.eq("status", TransactionStatus.SUCCESS ));
		rc.add(Restrictions.in("typeOperation", new Object[]{TypeOperation.PUSH, TypeOperation.PULL}));

		return mobileMoneyDAO.filter(Transaction.class, null, rc, null, null, 0, -1);

	}


	public void processRetraiterTransactions(){
		// checkGlobalConfig();
		params = findParameters();
		logger.error("DEBUT DU MAINTIEN DES RESERVATIONS");
		try{
			//logger.info("[MoMo] : IN ROBOT RETRAITEMENT");
			new Thread(new Runnable() {
				@Override
				public void run() {
					// code goes here.
					try {
						//logger.info("********************************* IN TASK RETRAITEMENT *********************************");

						if(!params.getTfjoEnCours()){
							try{
								//logger.info("***************************** RECHERCHE DES TRX A RETRAITER ******************************");
								List<Transaction> list = filterTransactionARetraiter();
								if(list.equals(null) || list.isEmpty()){
									logger.error("********************************* AUCUNE TRX *********************************");
									return;
								}
								logger.error("NBRE TRX A MAINTENIR : "+list.size());

								try{
									// Reposter les transactions dans le corebanking
									List<Transaction> trx = new ArrayList<Transaction>();
									trx = rePosterEvenementDansCoreBanking(list);

									if(!trx.isEmpty() && !trx.equals(null)){
										logger.info("***************************** Maj des transactions retraitees ******************************");
										// Maj des transactions retraitees
										mobileMoneyDAO.updateTransaction(trx);
									}

								}catch(Exception e){
									// TODO: handle exception
									logger.error("Erreur lors du retraitement des transactions");
									e.printStackTrace();
								}

							}catch(Exception e){
								e.printStackTrace();
							}
						}
						else{
							logger.error("********************************* ROBOT RETRAITEMENT OFF *********************************");
						}

					}catch(Exception e){
						//e.printStackTrace();
					}
				}
			}).start();

			//cancelChecking();

		}catch(Exception e){
			e.printStackTrace();
		}
		logger.error("FIN DU MAINTIEN DES RESERVATIONS");
	}


	/***********************************************************************************************************
	 ********************************************* DIRECT PUSH/PULL ******************************************** 
	 ***********************************************************************************************************/


	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public synchronized Map<String, String> pullTransactionECW(String trxId, String msisdn, Double amount) throws Exception {

		// Initialisation de la map a retourner
		Map<String, String> map = new HashMap<String, String>();

		// Chargement des proprietes ds la map
		map.put("msisdn", msisdn);
		map.put("amount", "0");
		map.put("remoteID", "0");

		if(isModeNuit() && !isModeNuitEstAuthorise()) throw new RuntimeException("Unauthorized to process the transaction at this time! Night Mode is Off!!!");

		Subscriber subs = findSubscriberFromPhoneNumber(msisdn);

		try{
			// Solde du compte avant operation
			Double bal = getSolde( subs.getFirstAccount() ); //processBalanceTransaction2(msisdn);//getBalanceTransaction(msisdn, bankPin);
			map.put("amount", String.valueOf(bal.longValue()));

			// Execution de la transaction de Pull
			Transaction tx = processPullTransactionECW(trxId, msisdn, amount);

			// Operation executee avec succes
			if(TransactionStatus.SUCCESS.equals(tx.getStatus())) {
				logger.info("**************************************TRX EXISTANTE AYANT REUSSIEE ");
				map.put("statusCode", "200");
			}
			// Operation existante et ayant echouee
			else{
				logger.info("*****************************************TRX EXISTANTE AYANT ECHOUEE ");
				// Erreur Momo
				if(tx.getExceptionCode()!=null){
					if(tx.getExceptionCode().equals(ExceptionCode.BankInsufficientBalance)) {
						map.put("statusCode", "504");
					} else if(tx.getExceptionCode().equals(ExceptionCode.SubscriberInvalidPIN)) {
						map.put("statusCode", "502");
					}else if(tx.getExceptionCode().equals(ExceptionCode.SubscriberSuspended)) {
						map.put("statusCode", "501");
					}else if(tx.getExceptionCode().equals(ExceptionCode.SubscriberInvalidAmount)) {
						map.put("statusCode", "503");
					}else if(tx.getExceptionCode().equals(ExceptionCode.TransactionIDAlreadyExist)) {
						map.put("statusCode", "506");
					}else {
						map.put("statusCode", "505");
					}
				}
				else {
//					logger.info("*****************************************AUCUN CODE ERREUR TROUVE ");
					map.put("statusCode", "505");
				}
				
			}

			map.put("remoteID", tx.getId().toString());

			logger.info("TRX : " + tx);

			// Solde du compte après operation
			Double balance = getSolde(subs.getFirstAccount() ); //processBalanceTransaction2(msisdn);//getBalanceTransaction(msisdn, bankPin);
			map.put("amount", String.valueOf(balance.longValue()));

		} catch(MoMoException me){

			// Erreur Momo
			if(me.getCode().equals(ExceptionCode.BankInsufficientBalance)) {
				map.put("statusCode", "504");
			} else if(me.getCode().equals(ExceptionCode.SubscriberInvalidPIN)) {
				map.put("statusCode", "502");
			}else if(me.getCode().equals(ExceptionCode.SubscriberSuspended)) {
				map.put("statusCode", "501");
			}else if(me.getCode().equals(ExceptionCode.SubscriberInvalidAmount)) {
				map.put("statusCode", "503");
			}else if(me.getCode().equals(ExceptionCode.TransactionIDAlreadyExist)) {
				map.put("statusCode", "506");
			}else {
				map.put("statusCode", "505");
				logger.info(me.getCode() + " : " + me.getMessage());
			}
			map.put("error", me.getMessage());

			// En cas d'erreur on enregistre la trace de la trx
//			if(subs != null){
//				String phoneNumber  = subs.getFirstPhone();
//				RequestMessage message = new RequestMessage(TypeOperation.PULL, phoneNumber, amount, null, trxId) ;
//				// Formation du message Transaction 
//				Transaction trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), message.getTrxId());
//				//trx.setMtnTrxId(trxId);
//				trx.setStatus(TransactionStatus.FAILED);
//				trx.setExceptionCategory(me.getCategory());
//				trx.setExceptionCode(me.getCode());
//				trx.setAccount(subs.getFirstAccount());
//				trx.setDate(new Date());
//				trx.setTtc(round(trx.getAmount(), 0));
//				trx = mobileMoneyDAO.save(trx);
//			}
			// Log de la trace
			if(subs != null) logTraceTrxECW(trxId, msisdn, amount, subs, TypeOperation.PULL, me.getCode());

		} catch (Exception e){
			logger.info("Exception lors du traitement de la transaction PULL ");
			// Erreur Systeme
			map.put("statusCode", "500");
			map.put("error", e.getMessage());

			//e.printStackTrace();
			// En cas d'erreur on enregistre la trace de la trx
//			if(subs != null){
//				String phoneNumber  = subs.getFirstPhone();
//				RequestMessage message = new RequestMessage(TypeOperation.PULL, phoneNumber, amount, null, trxId) ;
//				// Formation du message Transaction 
//				Transaction trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), message.getTrxId());
//				//trx.setMtnTrxId(trxId);
//				trx.setStatus(TransactionStatus.FAILED);
//				trx.setExceptionCategory(ExceptionCategory.SYSTEM);
//				trx.setExceptionCode(ExceptionCode.BankException);
//				trx.setAccount(subs.getFirstAccount());
//				trx.setDate(new Date());
//				trx.setTtc(round(trx.getAmount(), 0));
//				trx = mobileMoneyDAO.save(trx);
//
//				//return map;
//			}
			// Log de la trace
			if(subs != null) logTraceTrxECW(trxId, msisdn, amount, subs, TypeOperation.PULL, ExceptionCode.BankException);
		}

		// Log
//		System.err.println("[MAC LOGGER] Voici la Map Retournee pour "+ msisdn +" : " + map.toString());

		return map;

	}


	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public synchronized Map<String, String> pushTransactionECW(String trxId, String msisdn, Double amount) throws Exception {

		// Initialisation de la map a retourner
		Map<String, String> map = new HashMap<String, String>();

		// Chargement des proprietes ds la map
		map.put("msisdn", msisdn);
		map.put("amount", "0");
		map.put("remoteID", "0");

		if(isModeNuit() && !isModeNuitEstAuthorise()) throw new RuntimeException("Unauthorized to process the transaction at this time! Night Mode is Off!!!");

		Subscriber subs = findSubscriberFromPhoneNumber(msisdn);
		
		try{
			// Solde du compte avant operation
			Double bal = getSolde( subs.getFirstAccount() );// processBalanceTransaction2(msisdn);//getBalanceTransaction(msisdn, bankPin);
			map.put("amount", String.valueOf(bal.longValue()));

			// Execution de la transaction de Push
			Transaction tx = processPushTransactionECW(trxId, msisdn, amount);

			// Operation executee avec succes
			if(TransactionStatus.SUCCESS.equals(tx.getStatus())) {
				logger.info("**************************************TRX EXISTANTE AYANT REUSSIEE ");
				map.put("statusCode", "200");
			}
			
			// Operation existante et ayant echouee
			else{
				logger.info("*****************************************TRX EXISTANTE AYANT ECHOUEE ");
				// Erreur Momo
				if(tx.getExceptionCode()!=null){
					if(tx.getExceptionCode().equals(ExceptionCode.BankInsufficientBalance)) {
						map.put("statusCode", "504");
					} else if(tx.getExceptionCode().equals(ExceptionCode.SubscriberInvalidPIN)) {
						map.put("statusCode", "502");
					}else if(tx.getExceptionCode().equals(ExceptionCode.SubscriberSuspended)) {
						map.put("statusCode", "501");
					}else if(tx.getExceptionCode().equals(ExceptionCode.SubscriberInvalidAmount)) {
						map.put("statusCode", "503");
					}else if(tx.getExceptionCode().equals(ExceptionCode.TransactionIDAlreadyExist)) {
						map.put("statusCode", "506");
					}else {
						map.put("statusCode", "505");
					}
				}
				else {
//					logger.info("*****************************************AUCUN CODE ERREUR TROUVE ");
					map.put("statusCode", "505");
				}
				
			}

			map.put("remoteID", tx.getId().toString());

			logger.info("TRX : " + tx);

			// Solde du compte après operation
			Double balance = getSolde( subs.getFirstAccount() ); //getBalanceTransaction(msisdn, bankPin);
			map.put("amount", String.valueOf(balance.longValue()));

		} catch(MoMoException me){

			// Erreur Momo
			if(me.getCode().equals(ExceptionCode.BankInsufficientBalance)) {
				map.put("statusCode", "504");
				map.put("error", me.getMessage());
			} else if(me.getCode().equals(ExceptionCode.SubscriberInvalidPIN)) {
				map.put("statusCode", "502");
				map.put("error", me.getMessage());
			}else if(me.getCode().equals(ExceptionCode.SubscriberSuspended)) {
				map.put("statusCode", "501");
				map.put("error", me.getMessage());
			}else if(me.getCode().equals(ExceptionCode.SubscriberInvalidAmount)) {
				map.put("statusCode", "503");
				map.put("error", me.getMessage());
			}else if(me.getCode().equals(ExceptionCode.TransactionIDAlreadyExist)) {
				map.put("statusCode", "506");
			}else {
				map.put("statusCode", "505");
				map.put("error", me.getMessage());
				logger.info(me.getCode() + " : " + me.getMessage());
			}
			// En cas d'erreur on enregistre la trace de la trx
//			if(subs != null){
//				String phoneNumber  = subs.getFirstPhone();
//				RequestMessage message = new RequestMessage(TypeOperation.PUSH, phoneNumber, amount, null, trxId) ;
//				// Formation du message Transaction 
//				Transaction trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), message.getTrxId());
//				//trx.setMtnTrxId(trxId);
//				trx.setStatus(TransactionStatus.FAILED);
//				trx.setExceptionCategory(me.getCategory());
//				trx.setExceptionCode(me.getCode());
//				trx.setAccount(subs.getFirstAccount());
//				trx.setDate(new Date());
//				trx.setTtc(round(trx.getAmount(), 0));
//				trx = mobileMoneyDAO.save(trx);
//			}
			// Log de la trace
			if(subs != null) logTraceTrxECW(trxId, msisdn, amount, subs, TypeOperation.PUSH, me.getCode());

		} catch (Exception e){
			logger.info("Exception lors du traitement de la transaction PUSH ");
			// Erreur Systeme
			map.put("statusCode", "500");
			map.put("error", e.getMessage());

			//e.printStackTrace();
			// En cas d'erreur on enregistre la trace de la trx
//			if(subs != null){
//				String phoneNumber  = subs.getFirstPhone();
//				RequestMessage message = new RequestMessage(TypeOperation.PUSH, phoneNumber, amount, null, trxId) ;
//				// Formation du message Transaction 
//				Transaction trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), message.getTrxId());
//				//trx.setMtnTrxId(trxId);
//				trx.setStatus(TransactionStatus.FAILED);
//				trx.setExceptionCategory(ExceptionCategory.SYSTEM);
//				trx.setExceptionCode(ExceptionCode.BankException);
//				trx.setAccount(subs.getFirstAccount());
//				trx.setDate(new Date());
//				trx.setTtc(round(trx.getAmount(), 0));
//				trx = mobileMoneyDAO.save(trx);
//
//			}
			// Log de la trace
			if(subs != null) logTraceTrxECW(trxId, msisdn, amount, subs, TypeOperation.PUSH, ExceptionCode.BankException);
		}

		return map;

	}


	@Override
	public synchronized Map<String, String> getBalanceECW(String msisdn) throws Exception {

		// Initialisation de la map a retourner
		Map<String, String> map = new HashMap<String, String>();

		Subscriber subs = findSubscriberFromPhoneNumber(msisdn);

		// Chargement des proprietes ds la map
		map.put("msisdn", msisdn);		
		map.put("remoteID", "0");

		if(isModeNuit() && !isModeNuitEstAuthorise()) throw new RuntimeException("Unauthorized to process the transaction at this time! Night Mode is Off!!!");

		try{

			// Execution de la transaction de Balance
			Double amount = processBalanceTransactionECW(msisdn);
			//logger.info("SOLDE : "+amount);
			// Recuperation du solde
			map.put("amount", String.valueOf(amount.longValue()) );

			// Operation executee avec succes
			map.put("statusCode", "200");

		} catch(MoMoException me){

			// Erreur Momo
			if(me.getCode().equals(ExceptionCode.BankInsufficientBalance)) {
				map.put("statusCode", "504");
				map.put("error", me.getMessage());
			} else if(me.getCode().equals(ExceptionCode.SubscriberInvalidPIN)) {
				map.put("statusCode", "502");
				map.put("error", me.getMessage());
			}else if(me.getCode().equals(ExceptionCode.SubscriberSuspended)) {
				map.put("statusCode", "501");
				map.put("error", me.getMessage());
			}else if(me.getCode().equals(ExceptionCode.SubscriberInvalidAmount)) {
				map.put("statusCode", "503");
				map.put("error", me.getMessage());
			}else {
				map.put("statusCode", "505");
				map.put("error", me.getMessage());
				//logger.info(me.getCode() + " : " + me.getMessage());
			}

			// En cas d'erreur on enregistre la trace de la trx
//			if(subs != null){
//				String phoneNumber  = subs.getFirstPhone();
//				RequestMessage message = new RequestMessage(TypeOperation.BALANCE, phoneNumber, 0d, null,"") ;
//				// Formation du message Transaction 
//				Transaction trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), message.getTrxId());
//				//trx.setMtnTrxId(mtnTrxId);
//				trx.setStatus(TransactionStatus.FAILED);
//				trx.setExceptionCategory(ExceptionCategory.SYSTEM);
//				trx.setExceptionCode(me.getCode());
//				trx.setAccount(subs.getFirstAccount());
//				trx.setDate(new Date());
//				trx = mobileMoneyDAO.save(trx);
//			}
			// Log de la trace
			if(subs != null) logTraceTrxECW("", msisdn, 0d, subs, TypeOperation.BALANCE, me.getCode());

		} catch (Exception e){
			// Erreur Systeme
			map.put("statusCode", "500");
			map.put("error", e.getMessage());
			e.printStackTrace();

			// En cas d'erreur on enregistre la trace de la trx
//			if(subs != null){
//				String phoneNumber  = subs.getFirstPhone();
//				RequestMessage message = new RequestMessage(TypeOperation.BALANCE, phoneNumber, 0d, null,"") ;
//				// Formation du message Transaction 
//				Transaction trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), message.getTrxId());
//				//trx.setMtnTrxId(mtnTrxId);
//				trx.setStatus(TransactionStatus.FAILED);
//				trx.setExceptionCategory(ExceptionCategory.SYSTEM);
//				trx.setExceptionCode(ExceptionCode.BankException);
//				trx.setAccount(subs.getFirstAccount());
//				trx.setDate(new Date());
//				trx = mobileMoneyDAO.save(trx);
//			}
			// Log de la trace
			if(subs != null) logTraceTrxECW("", msisdn, 0d, subs, TypeOperation.BALANCE, ExceptionCode.BankException);
		}

		return map;

	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#processPullTransaction(java.lang.String, java.lang.String, java.lang.Double)
	 */
	@Override
	public List<Transaction> getMinistatementECW(String msisdn) throws Exception{

		List<Transaction> list = new ArrayList<Transaction>();
		
		Subscriber subs = findSubscriberFromPhoneNumber(msisdn);

		try{
			// Initialisation de la transaction a executer
			Transaction tx = getTransactionFromRequestMessageECW( new RequestMessage(TypeOperation.MINISTATEMENT, msisdn, 0d, null, null) );

			// Recuperation des dernieres transactions
			list = mobileMoneyDAO.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("phoneNumber", msisdn)).add(Restrictions.gt("amount", 0d)).add(Restrictions.eq("status", TransactionStatus.SUCCESS)), OrderContainer.getInstance().add(Order.desc("date")), null, 0, 5);

			// MAJ du statut de la transaction
			tx.setStatus(TransactionStatus.SUCCESS);

			// Sauvegarde de la transaction
			saveTransaction(tx);

		} catch(MoMoException me){

			// Erreur Momo
			// En cas d'erreur on enregistre la trace de la trx
//			if(subs != null){
//				String phoneNumber  = subs.getFirstPhone();
//				RequestMessage message = new RequestMessage(TypeOperation.MINISTATEMENT, phoneNumber, 0d, null,"") ;
//				// Formation du message Transaction 
//				Transaction trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), message.getTrxId());
//				//trx.setMtnTrxId(mtnTrxId);
//				trx.setStatus(TransactionStatus.FAILED);
//				trx.setExceptionCategory(ExceptionCategory.SYSTEM);
//				trx.setExceptionCode(me.getCode());
//				trx.setAccount(subs.getFirstAccount());
//				trx.setDate(new Date());
//				trx = mobileMoneyDAO.save(trx);
//			}
			// Log de la trace
			if(subs != null) logTraceTrxECW("", msisdn, 0d, subs, TypeOperation.MINISTATEMENT, me.getCode());

		} catch (Exception e){
			// Erreur Systeme
			e.printStackTrace();

			// En cas d'erreur on enregistre la trace de la trx
//			if(subs != null){
//				String phoneNumber  = subs.getFirstPhone();
//				RequestMessage message = new RequestMessage(TypeOperation.MINISTATEMENT, phoneNumber, 0d, null,"") ;
//				// Formation du message Transaction 
//				Transaction trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), message.getTrxId());
//				//trx.setMtnTrxId(mtnTrxId);
//				trx.setStatus(TransactionStatus.FAILED);
//				trx.setExceptionCategory(ExceptionCategory.SYSTEM);
//				trx.setExceptionCode(ExceptionCode.BankException);
//				trx.setAccount(subs.getFirstAccount());
//				trx.setDate(new Date());
//				trx = mobileMoneyDAO.save(trx);
//			}
			// Log de la trace
			if(subs != null) logTraceTrxECW("", msisdn, 0d, subs, TypeOperation.MINISTATEMENT, ExceptionCode.BankException);
		}

		return list;
	}
	
	
	@Override
	public synchronized void logTraceTrxECW(String trxId, String msisdn, Double amount, Subscriber subs, TypeOperation typeOp, ExceptionCode exceptionCode) {
		
		try{
			// En cas d'erreur on enregistre la trace de la trx
//			String phoneNumber  = subs.getFirstPhone();
			RequestMessage message = new RequestMessage(typeOp, msisdn, amount, null, trxId) ;
			// Formation du message Transaction 
			Transaction trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), message.getTrxId());
			//trx.setMtnTrxId(mtnTrxId);
			trx.setStatus(TransactionStatus.FAILED);
			trx.setExceptionCategory(ExceptionCategory.SYSTEM);
			trx.setExceptionCode(exceptionCode);
			trx.setAccount(subs.getFirstAccount());
			trx.setDate(new Date());
			trx.setTtc(round(trx.getAmount(), 0));
			trx = mobileMoneyDAO.save(trx);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error("ERREUR LORS DE L'ENREGISTREMENT DE LA TRACE : "+e.getMessage());
		}
	}
	


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#processPullTransaction(java.lang.String, java.lang.String, java.lang.Double)
	 */
	@Override
	public synchronized Transaction processPullTransactionECW(String trxId, String phoneNumber, Double amount) throws Exception, MoMoException {

		//return processPullPushMessage( new RequestMessage(TypeOperation.PULL, phoneNumber, amount, null) );
		// remplaced by ...
		return posterEvenementDansCoreBanking( getTransactionFromRequestMessageECW(new RequestMessage(TypeOperation.PULL, phoneNumber, amount, null, trxId)) );

	}

	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#processPushTransaction(java.lang.String, java.lang.String, java.lang.Double)
	 */
	@Override
	public synchronized Transaction processPushTransactionECW(String trxId, String phoneNumber, Double amount) throws Exception, MoMoException {

		//return processPullPushMessage( new RequestMessage(TypeOperation.PUSH, phoneNumber, amount, null) );
		// remplaced by ...
		return posterEvenementDansCoreBanking( getTransactionFromRequestMessageECW(new RequestMessage(TypeOperation.PUSH, phoneNumber, amount, null, trxId)) );

	}

	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#processBalanceTransaction(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized Double processBalanceTransactionECW(String phoneNumber) throws Exception, MoMoException {

		// Initialisation de la transaction a executer
		Transaction tx = getTransactionFromRequestMessageECW( new RequestMessage(TypeOperation.BALANCE, phoneNumber, 0d, null, null) );
        logger.info("[MANAGER SOLDE: ]"+tx.getAccount());
		// Recuperation du solde du cpteddd
		Double solde = getSolde(tx.getAccount());

		if(solde == null) throw new MoMoException(ExceptionCode.SystemCoreBankingSystemAcces, "Erreur lors de la lecture du solde du compte", ExceptionCategory.SYSTEM);

		// MAJ du statut de la transaction
		tx.setStatus(TransactionStatus.SUCCESS);

		// Sauvegarde de la transaction
		saveTransaction(tx);

		// Retourne le solde du cpte
		return solde;
	}

	
	//	/*
	//	 * (non-Javadoc)
	//	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#processReversalTransaction(java.lang.String, java.lang.String, java.lang.String)
	//	 */
	//	@Override
	//	public synchronized Map<String, String> processReversalTransaction2(String remoteID) throws Exception {
	//
	//		logger.info("*********************************processReversalTransaction**************************remoteID**"+remoteID);
	//
	//		// Initialisation de la map a retourner
	//		Map<String, String> map = new HashMap<String, String>();
	//
	//		// Chargement des proprietes ds la map
	//		map.put("remoteID", remoteID);
	//
	//		Long txID = Long.valueOf(remoteID);
	//		List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, AliasesContainer.getInstance().add("transaction", "transaction"), RestrictionsContainer.getInstance().add(Restrictions.eq("transaction.id", txID)), null, null, 0, -1);
	//
	//		if(eves == null || eves.isEmpty()) {
	//			map.put("statusCode", "500");
	//			return map;
	//		}
	//
	//		try {
	//
	//			// Recuperation de l'evenement genere par la transaction
	//			bkeve eve = eves.get(0);
	//
	//			RestrictionsContainer rc = RestrictionsContainer.getInstance().add(Restrictions.eq("lg_Remote_ID", remoteID));
	//			//rc.add(Restrictions.eq("str_Phone",eve.getTransaction().getPhoneNumber()));
	//			for(USSDTransaction ut : filterUSSDTransactions(rc)) {
	//
	//				// && StringUtils.containsIgnoreCase(ut.getStr_Status_Description(),"General failure.")
	//				if(ut.opeOK() == false){
	//
	//					// Demarrage du service Facade du portail
	//					IFacadeManagerRemote portalFacadeManager = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );
	//
	//					// Recuperation de la DS de cnx au CBS
	//					DataSystem dsCBS = (DataSystem) portalFacadeManager.findByProperty(DataSystem.class, "code", "DELTA-V10");
	//
	//					// MAJ de l'evenement dans le CBS
	//					executeUpdateSystemQuery(dsCBS, "update bkeve set eta='IG', etap='VA' where eve='" + eve.getEve() + "' and age ='"+ eve.getAge() +"' ", null);
	//
	//					// MAJ des soldes indicatifs des cptes debiteurs et crediteurs
	//					executeUpdateSystemQuery(dsCBS, "update bkcom set sin=sin-"+ eve.getMnt2() +" where age='"+ eve.getAge2() +"' and ncp='"+ eve.getNcp2() +"' and clc='"+ eve.getClc2() +"' ", null);
	//					executeUpdateSystemQuery(dsCBS, "update bkcom set sin=sin+"+ eve.getMnt1() +" where age='"+ eve.getAge1() +"' and ncp='"+ eve.getNcp1() +"' and clc='"+ eve.getClc1() +"' ", null);
	//
	//					// Modification des infos de l'evenement
	//					eve.setEta("IG"); eve.setEtap("VA");
	//					eve.getTransaction().setStatus(TransactionStatus.CANCEL);
	//
	//					// MAJ de l'evenement
	//					mobileMoneyDAO.update(eve);
	//
	//					// Message d'information du client
	//					sendSMS("Your " + eve.getTransaction().getTypeOperation().getValue() + " transaction of XAF "+ eve.getTransaction().getAmount() +" has been cancelled!", eve.getTransaction().getPhoneNumber());
	//
	//					// MAJ du statut de l'operation (executee avec succes)
	//					map.put("statusCode", "200");
	//
	//				}else map.put("statusCode", "200");
	//
	//			}
	//
	//		}catch(Exception e){
	//
	//			// MAJ du statut de l'operation (echec)
	//			map.put("statusCode", "500");
	//			e.printStackTrace();
	//
	//		}
	//
	//		// Retourne la map
	//		return map;
	//
	//	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote#processReversalTransaction(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized Map<String, String> processReversalTransactionECW(String remoteID) throws Exception {

		logger.info("*********************************processReversalTransaction**************************remoteID**"+remoteID);
		
		//Liste des requêtes
		List<Queries> query = new ArrayList();
		
		// Initialisation de la map a retourner
		Map<String, String> map = new HashMap<String, String>();

		// Chargement des proprietes ds la map
		map.put("remoteID", remoteID);

		Long txID = Long.valueOf(remoteID);
		List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, AliasesContainer.getInstance().add("transaction", "transaction"), RestrictionsContainer.getInstance().add(Restrictions.eq("transaction.id", txID)), null, null, 0, -1);

		if(eves == null || eves.isEmpty()) {
			logger.info("*********************************processReversalTransaction************************** eve not found **");
			map.put("statusCode", "500");
			map.put("error", "");
			return map;
		}

		try {

			// Recuperation de l'evenement genere par la transaction
			bkeve eve = eves.get(0);
			params = findParameters();
			if(params.getCbsServices()) {		
				//envoi de l'evenement a l'API pour annulation
				logger.info("IDEVE: "+eve.getId());
				if(reversalEventsCoreBanking(""+eve.getId()) == null) return map;
		
			}else {
				String tableEvt = eve.getSuspendInTFJ().booleanValue() ? " BKEVE_EOD " : " BKEVE ";
				
				// Demarrage du service Facade du portail
				IFacadeManagerRemote portalFacadeManager = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );
	
				// Recuperation de la DS de cnx au CBS
				DataSystem dsCBS = (DataSystem) portalFacadeManager.findByProperty(DataSystem.class, "code", "DELTA-V10");
	
				// MAJ de l'evenement dans le CBS
			//	executeUpdateSystemQuery(dsCBS, "update "+ tableEvt +" set eta='IG', etap='VA' where eve='" + eve.getEve() + "' and age ='"+ eve.getAge() +"' ", null);
				query.add(new Queries("update "+ tableEvt +" set eta='IG', etap='VA' where eve='" + eve.getEve() + "' and age ='"+ eve.getAge() +"' ", null));
				
				if(isModeNuit()){
					
					// Annulation MAJ du solde indicatif du Compte Debiteur  orig='WEB' , 'MON' ;  flag=''
				//	executeUpdateSystemQuery(dsCBS, "update bksin set mon=0 where age= ? and  dev=? and  ncp = ? and  suf=? and  mon=? and  orig=? and  flag=? ", new Object[]{ eve.getAge1(), eve.getDev1(), eve.getNcp1(), eve.getSuf1(), -eve.getMon1(), "WEB", "O" });
					query.add(new Queries("update bksin set mon=0 where age= ? and  dev=? and  ncp = ? and  suf=? and  mon=? and  orig=? and  flag=? ", new Object[]{ eve.getAge1(), eve.getDev1(), eve.getNcp1(), eve.getSuf1(), -eve.getMon1(), "WEB", "O" }));
					
					// Annulation MAJ du solde indicatif du compte Crediteur
				//	executeUpdateSystemQuery(dsCBS, "update bksin set mon=0 where age= ? and  dev=? and  ncp = ? and  suf=? and  mon=? and  orig=? and  flag=? ", new Object[]{ eve.getAge2(), eve.getDev2(), eve.getNcp2(), eve.getSuf2(), eve.getMon2(), "WEB", "O" });
					query.add(new Queries("update bksin set mon=0 where age= ? and  dev=? and  ncp = ? and  suf=? and  mon=? and  orig=? and  flag=? ", new Object[]{ eve.getAge2(), eve.getDev2(), eve.getNcp2(), eve.getSuf2(), eve.getMon2(), "WEB", "O" }));
					logger.info("*********************************processReversalTransaction************************** maj solde mode nuit OK **");
				} 
				else {
	
					// MAJ des soldes indicatifs des cptes debiteurs et crediteurs
				//	executeUpdateSystemQuery(dsCBS, "update bkcom set sin=sin-"+ eve.getMnt2() +" where age='"+ eve.getAge2() +"' and ncp='"+ eve.getNcp2() +"' and clc='"+ eve.getClc2() +"' ", null);
					query.add(new Queries("update bkcom set sin=sin-"+ eve.getMnt2() +" where age='"+ eve.getAge2() +"' and ncp='"+ eve.getNcp2() +"' and clc='"+ eve.getClc2() +"' ", null));
					
				//	executeUpdateSystemQuery(dsCBS, "update bkcom set sin=sin+"+ eve.getMnt1() +" where age='"+ eve.getAge1() +"' and ncp='"+ eve.getNcp1() +"' and clc='"+ eve.getClc1() +"' ", null);
					query.add(new Queries("update bkcom set sin=sin+"+ eve.getMnt1() +" where age='"+ eve.getAge1() +"' and ncp='"+ eve.getNcp1() +"' and clc='"+ eve.getClc1() +"' ", null));
					
					logger.info("*********************************processReversalTransaction************************** maj solde mode nuit KO **");
				}
				
				//Execution des update (modification du 2020-08-27)
				executeUpdateSystemQuery(dsCBS,query);
			 }
			
			// Modification des infos de l'evenement
	//		eve.setEta("IG"); eve.setEtap("VA");
	//		eve.getTransaction().setStatus(TransactionStatus.CANCEL);
			
			// MAJ de l'evenement
		//	mobileMoneyDAO.update(eve);

			// MAJ de l'evenement (modification du 2020-08-27)
			mobileMoneyDAO.updateBkeve("IG", "VA", eve.getId());
		    mobileMoneyDAO.updateTransactionId(TransactionStatus.CANCEL, eve.getTransaction().getMtnTrxId());
			

			
			// Message d'information du client
		    String message = "Your " + eve.getTransaction().getTypeOperation().getValue() + " transaction of "
		    		+ "XAF "+ eve.getTransaction().getAmount() +" has been cancelled!";
		    String phone = eve.getTransaction().getPhoneNumber();
		    
		    if(!params.getEtatServiceSDP().equals(StatutService.TEST)){
		    	//phone = params.getDestPhoneAlerte();
		    	sendSimpleSms(message, phone);
		    }
		//	sendSMS(message, phone);
			
			// MAJ du statut de l'operation (executee avec succes)
			map.put("statusCode", "200");

		}catch(Exception e){
			logger.error("*********************************processReversalTransaction************************** echec maj eve et/ou solde **");
			// MAJ du statut de l'operation (echec)
			map.put("statusCode", "500");
			map.put("error", e.getMessage());
			e.printStackTrace();

		}

		// Retourne la map
		return map;

	}
	
	
	/**
	 * Annulation de la maj du solde indicatif du client dans Amplitube
	 * @param eve
	 * @return true si l'annulation se passe bien et false dans le cas contraire
	 * @throws Exception
	 */
	private boolean annulerMAJSoldeIndicatif(bkeve eve) throws Exception{
		
		try {

			// Demarrage du service Facade du portail
			IFacadeManagerRemote portalFacadeManager = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );

			// Recuperation de la DS de cnx au CBS
			DataSystem dsCBS = (DataSystem) portalFacadeManager.findByProperty(DataSystem.class, "code", "DELTA-V10");
			
			if(isModeNuit()){

				// Annulation MAJ du solde indicatif du Compte Debiteur  orig='WEB' , 'MON' ;  flag=''
				executeUpdateSystemQuery(dsCBS, "update bksin set mon=0 where age= ? and  dev=? and  ncp = ? and  suf=? and  mon=? and  orig=? and  flag=? ", new Object[]{ eve.getAge1(), eve.getDev1(), eve.getNcp1(), eve.getSuf1(), -eve.getMon1(), "WEB", "O" });

				// Annulation MAJ du solde indicatif du compte Crediteur
				executeUpdateSystemQuery(dsCBS, "update bksin set mon=0 where age= ? and  dev=? and  ncp = ? and  suf=? and  mon=? and  orig=? and  flag=? ", new Object[]{ eve.getAge2(), eve.getDev2(), eve.getNcp2(), eve.getSuf2(), eve.getMon2(), "WEB", "O" });

				return Boolean.TRUE;
			} 
			else {
				// Annulation MAJ des soldes indicatifs des cptes debiteurs et crediteurs
				executeUpdateSystemQuery(dsCBS, "update bkcom set sin=sin-"+ eve.getMnt2() +" where age='"+ eve.getAge2() +"' and ncp='"+ eve.getNcp2() +"' and clc='"+ eve.getClc2() +"' ", null);
				executeUpdateSystemQuery(dsCBS, "update bkcom set sin=sin+"+ eve.getMnt1() +" where age='"+ eve.getAge1() +"' and ncp='"+ eve.getNcp1() +"' and clc='"+ eve.getClc1() +"' ", null);
			}
			
			return Boolean.TRUE;

		}catch(Exception e){

			// Exception lors de l'annulation de la maj du solde (echec)
			e.printStackTrace();
			return Boolean.FALSE;
		}
		
	}
	
	//modification 2020-08-27
    private boolean annulerMAJSoldeIndicatif(bkeve eve, List<Queries> query) throws Exception{
		
		try {

			// Demarrage du service Facade du portail
			IFacadeManagerRemote portalFacadeManager = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );

			// Recuperation de la DS de cnx au CBS
			DataSystem dsCBS = (DataSystem) portalFacadeManager.findByProperty(DataSystem.class, "code", "DELTA-V10");
			
			if(isModeNuit()){
				// Annulation MAJ du solde indicatif du Compte Debiteur  orig='WEB' , 'MON' ;  flag=''
				query.add(new Queries("update bksin set mon=0 where age= ? and  dev=? and  ncp = ? and  suf=? and  mon=? and  orig=? and  flag=? ", new Object[]{ eve.getAge1(), eve.getDev1(), eve.getNcp1(), eve.getSuf1(), -eve.getMon1(), "WEB", "O" }));
				
				// Annulation MAJ du solde indicatif du compte Crediteur
				query.add(new Queries("update bksin set mon=0 where age= ? and  dev=? and  ncp = ? and  suf=? and  mon=? and  orig=? and  flag=? ", new Object[]{ eve.getAge2(), eve.getDev2(), eve.getNcp2(), eve.getSuf2(), eve.getMon2(), "WEB", "O" }));
			
			} 
			else {
				// Annulation MAJ des soldes indicatifs des cptes debiteurs et crediteurs
				query.add(new Queries("update bkcom set sin=sin-"+ eve.getMnt2() +" where age='"+ eve.getAge2() +"' and ncp='"+ eve.getNcp2() +"' and clc='"+ eve.getClc2() +"' ", null));
				query.add(new Queries("update bkcom set sin=sin+"+ eve.getMnt1() +" where age='"+ eve.getAge1() +"' and ncp='"+ eve.getNcp1() +"' and clc='"+ eve.getClc1() +"' ", null));
			}
			
			executeUpdateSystemQuery(dsCBS, query);
			return Boolean.TRUE;

		}catch(Exception e){

			// Exception lors de l'annulation de la maj du solde (echec)
			e.printStackTrace();
			return Boolean.FALSE;
		}
		
	}


	@Override	
	public String lastExecutionRobot(){
		List<TraceRobot> tr = new ArrayList<TraceRobot>();
		tr = mobileMoneyDAO.filter(TraceRobot.class, null, null, null, null, 0, 1);
		if(tr.isEmpty()) return "";
		String req = "From TraceRobot t where t.id = (select max(t2.id) From TraceRobot t2) ";
		TraceRobot obj = (TraceRobot) mobileMoneyDAO.getEntityManager().createQuery(req).getSingleResult();
		return obj != null ?  obj.getFormattedDatetimeTrace() : "";
	}


	@Override
	public List<TraceRobot> filterTraceRobots(RestrictionsContainer rc, OrderContainer orders) {
		// TODO Auto-generated method stub
		return mobileMoneyDAO.filter(TraceRobot.class, null, rc, orders, null, 0, -1);
	}


	@Override
	public List<Transaction> filterTransactionProcessing() {
		// TODO Auto-generated method stub
		logger.info("FILTER TRANSACTIONS");
		RestrictionsContainer rc = RestrictionsContainer.getInstance();

		// verifier au lieu de completed ???
		rc.add(Restrictions.and(Restrictions.isNotNull("verifier"),Restrictions.eq("verifier",Boolean.FALSE)));
		rc.add(Restrictions.eq("posted",Boolean.FALSE));
		rc.add(Restrictions.in("typeOperation", new Object[]{TypeOperation.PUSH, TypeOperation.PULL}));
		rc.add(Restrictions.isNotNull("mtnTrxId"));

		return mobileMoneyDAO.filter(Transaction.class, null, rc, null, null, 0, -1);

	}


	@Override
	public synchronized Map<String, String> cancelDuplicateTransactionECW(String remoteID) throws Exception {

		logger.info("ANNULATION DE L'EVENEMENT");
		// Initialisation de la map a retourner
		Map<String, String> map = new HashMap<String, String>();

		Long txID = Long.valueOf(remoteID);
		List<bkeve> eves = mobileMoneyDAO.filter(bkeve.class, AliasesContainer.getInstance().add("transaction", "transaction"), RestrictionsContainer.getInstance().add(Restrictions.eq("transaction.id", txID)), null, null, 0, -1);

		if(eves == null || eves.isEmpty()) {
			logger.info("PAS D'EVENEMENT");
			map.put("statusCode", "500");
			map.put("error", "");
			return map;
		}

		try {

			// Recuperation de l'evenement genere par la transaction
			bkeve eve = eves.get(0);

			// Demarrage du service Facade du portail
			IFacadeManagerRemote portalFacadeManager = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );

			// Recuperation de la DS de cnx au CBS
			DataSystem dsCBS = (DataSystem) portalFacadeManager.findByProperty(DataSystem.class, "code", "DELTA-V10");

			// MAJ de l'evenement dans le CBS
			executeUpdateSystemQuery(dsCBS, "update bkeve set eta='IG', etap='VA' where eve='" + eve.getEve() + "' and age ='"+ eve.getAge() +"' ", null);

			// MAJ des soldes indicatifs des cptes debiteurs et crediteurs
			executeUpdateSystemQuery(dsCBS, "update bkcom set sin=sin-"+ eve.getMnt2() +" where age='"+ eve.getAge2() +"' and ncp='"+ eve.getNcp2() +"' and clc='"+ eve.getClc2() +"' ", null);
			executeUpdateSystemQuery(dsCBS, "update bkcom set sin=sin+"+ eve.getMnt1() +" where age='"+ eve.getAge1() +"' and ncp='"+ eve.getNcp1() +"' and clc='"+ eve.getClc1() +"' ", null);

			// Modification des infos de l'evenement
			eve.setEta("IG"); eve.setEtap("VA");

			// MAJ de l'evenement
			mobileMoneyDAO.update(eve);

			// MAJ du statut de l'operation (executee avec succes)
			map.put("statusCode", "200");
			logger.info("ANNULATION OK");

		}catch(Exception e){

			// MAJ du statut de l'operation (echec)
			map.put("statusCode", "500");
			map.put("error", e.getMessage());
			e.printStackTrace();

		}

		// Retourne la map
		return map;

	}	


	@Override
	public Boolean isMTNTrxIDExist(String mtnTrxID) {
		// TODO Auto-generated method stub
		// Recherche des trx 
		List<Transaction> liste = mobileMoneyDAO.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.or(Restrictions.eq("typeOperation", TypeOperation.PULL), Restrictions.eq("typeOperation", TypeOperation.PUSH))).add(Restrictions.eq("mtnTrxId", mtnTrxID)), null, null, 0, -1);

		// Calcul du resultat a retourner
		boolean resultat = liste != null && !liste.isEmpty();

		// Libere la variable
		liste.clear(); liste = null;

		// Retourne le resultat
		return resultat;
	}


	public Transaction getMTNTrxID(String mtnTrxID) {
		// TODO Auto-generated method stub .add(Restrictions.gt("date_op", "2019-04-08 00:00:00"))

		Date d = null;
		try {
//			d = new SimpleDateFormat("yyyy-MM-dd").parse("2019-04-08 00:00:00");
			//d = MoMoHelper.sdf.parse(PortalHelper.DEFAULT_DATE_FORMAT.format(new Date()).concat(" 00:01"));
			
			Calendar c = Calendar.getInstance();
			c.setTime(MoMoHelper.sdf.parse(PortalHelper.DEFAULT_DATE_FORMAT.format(new Date()).concat(" 00:01")));
			logger.info("params recherche trx id : "+params);
			c.add(Calendar.DATE, -params.getPeriodeVerifTrx());
			d = c.getTime();
			logger.info("Date recherche trx : "+MoMoHelper.sdf.format(d));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Recherche des trx 
		List<Transaction> liste = mobileMoneyDAO.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.or(Restrictions.eq("typeOperation", TypeOperation.PULL), Restrictions.eq("typeOperation", TypeOperation.PUSH))).add(Restrictions.gt("date", d)).add(Restrictions.eq("mtnTrxId", mtnTrxID)), null, null, 0, -1);

		// Calcul du resultat a retourner
		boolean resultat = liste != null && !liste.isEmpty();

		// Retourne le resultat
		return resultat ? liste.get(0) : null;

	}

	
	public Date getLastTrxDate(){
		
		String req = "select max(date_op) from Transaction";
				
		return (Date) mobileMoneyDAO.getEntityManager().createQuery(req).getSingleResult();
	}
	

	public Double getCurrentSolde(String numCompte) throws Exception{
		return getSolde(numCompte);
	}
	
	// ADD
	

	
	@Override
	public List<Transaction> getTransactionCompensation(Date deb, Date fin, String user, String mois) throws Exception {

		params = findParameters();
		
		// Recuperation de la Liste des Transactions validees sur la periode   .add(Restrictions.between("date", dateDeb, dateFin))
		// Recuperation de la Liste de toutes les Transactions de souscription validees non postees
		List<Transaction> trans_sub = mobileMoneyDAO.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("status", TransactionStatus.SUCCESS)).add(Restrictions.in("typeOperation", new TypeOperation[]{TypeOperation.SUBSCRIPTION} )).add(Restrictions.eq("posted", Boolean.FALSE)), OrderContainer.getInstance().add(Order.asc("date")), null, 0, -1);

		// Recuperation de la Liste de toutes les Transactions validees non postees et reconciliees
		List<Transaction> trans = mobileMoneyDAO.filter(Transaction.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("status", TransactionStatus.SUCCESS)).add(Restrictions.eq("reconcilier", Boolean.TRUE)).add(Restrictions.in("typeOperation", new TypeOperation[]{TypeOperation.PULL, TypeOperation.PUSH, TypeOperation.SUBSCRIPTION, TypeOperation.MODIFY} )).add(Restrictions.eq("posted", Boolean.FALSE)), OrderContainer.getInstance().add(Order.asc("date")), null, 0, -1);

		if(!trans_sub.isEmpty()) trans.addAll(trans_sub);
		if(trans.isEmpty()) return new ArrayList<Transaction>();
		
		// NUIT
		// Activer le verrou des tfjo (tfjo lances)
		setTFJOPortalEnCours(Boolean.TRUE);

		/**********************************************************************
		 ******************* Traitement des souscriptions *********************
		 **********************************************************************/
		List<Transaction> transRegul = new ArrayList<Transaction>();
		List<Transaction> transRemove = new ArrayList<Transaction>();

		// Verification des soldes des transactions de souscription
		for(Transaction trx : trans) {
			if(trx.getTypeOperation().equals(TypeOperation.SUBSCRIPTION)){
				// Verifier le solde du client et l'opposition sur le compte
				if(getSolde(trx.getSubscriber().getFirstAccount(), isModeNuit()) < trx.getTtc() || 
						isCompteEnOpposition(trx.getSubscriber().getFirstAccount().split("-")[0], trx.getSubscriber().getFirstAccount().split("-")[1], params.getCodeOperation()) ){
					//logger.info("Solde insufisant ou compte en opposition : Envoi en regul");
					transRemove.add(trx);
					// On positionne le status de l'operation en REGUL
					trx.setDateTraitement(new Date());
					trx.setStatus(TransactionStatus.REGUL);
					transRegul.add(trx);
				}
			}
		}

		// Retrait des transactions a envoyer en regul
		trans.removeAll(transRemove);

		// Maj des transaction en REGUL
		mobileMoneyDAO.saveList(transRegul, true);

		if(trans.isEmpty()) return new ArrayList<Transaction>();	
		
		return trans;
	}
	
	
	/**
	 * Generation des EC de compensation
	 * @param trans liste des transaction a compenser
	 * @return la liste des EC de compensation
	 * @throws Exception
	 */
	public List<bkmvti> genererECCompensation(List<Transaction> trans) throws Exception {

		List<bkmvti> resultats = new ArrayList<bkmvti>();
		
		details = new ArrayList<FactMonthDetails>();
		mntD = 0d; mntC = 0d; nbrC = 0; nbrD = 0;
		
		// checkGlobalConfig(); //Parameters params = findParameters();
		params = findParameters();
		Double totalPull = 0d, totalPush = 0d;
		Double totalPullMarch = 0d, totalPushMarch = 0d;
		int numEc = 1; 
		String datop = new SimpleDateFormat("ddMMyyHHmm").format(new Date());

		if(dsCBS == null) findCBSDataSystem();

		Date dco = getDateComptable(dsCBS);
		Date dvaDebit = getDvaDebit();
		Date dvaCredit = getDvaCredit();
		logger.info("DCO : "+new SimpleDateFormat("dd-MM-yy").format(dco));
		// NUIT
		// Activer le verrou des tfjo (tfjo lances)
//		setTFJOPortalEnCours(Boolean.TRUE);

		// Calcul des montants totaux des transactions
		for(Transaction tx : trans) {
			tx.setSelected(true);
			totalPull += !tx.getSubscriber().isMerchant() && tx.getTypeOperation().equals(TypeOperation.PULL) ? tx.getAmount() : 0d;
			totalPush += !tx.getSubscriber().isMerchant() && tx.getTypeOperation().equals(TypeOperation.PUSH) ? tx.getAmount() : 0d;
			totalPullMarch += tx.getSubscriber().isMerchant() && tx.getTypeOperation().equals(TypeOperation.PULL) ? tx.getAmount() : 0d;
			totalPushMarch += tx.getSubscriber().isMerchant() && tx.getTypeOperation().equals(TypeOperation.PUSH) ? tx.getAmount() : 0d;
		}

		// Recuperation du compte MTN
		ResultSet rsCpteMTN = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(6).getQuery(), new Object[]{ params.getNumCompteMTN().split("-")[0], params.getNumCompteMTN().split("-")[1], params.getNumCompteMTN().split("-")[2] });

		// Recuperation du compte MTN
		ResultSet rsCpteDAPPull = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(8).getQuery(), new Object[]{ params.getNcpDAPPull().split("-")[0], params.getNcpDAPPull().split("-")[1], params.getNcpDAPPull().split("-")[2] });

		// Recuperation du compte MTN
		ResultSet rsCpteDAPPush = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(8).getQuery(), new Object[]{ params.getNcpDAPPush().split("-")[0], params.getNcpDAPPush().split("-")[1], params.getNcpDAPPush().split("-")[2] });

		if(!rsCpteMTN.next() || !rsCpteDAPPull.next() || !rsCpteDAPPush.next() ) throw new Exception("Comptes de Liaisons inexistants");


		// Poste les EC des transactions dans le CoreBanking
		//posterTransactionsDansCoreBanking(trans, user);


		/*************************************************************************/
		/**-------GENERATION DES EC DE COMPENSATION DANS LE COMPTE DE MTN-------**/
		/*************************************************************************/

		// Recuperation du dernier numero evenement du type operation
		ResultSet rs = executeFilterSystemQuery(dsCBS, "select max(eve) as num from bkeve where ope=?", new Object[]{ params.getCodeOperation() }); // MoMoHelper.getDefaultCBSQueries().get(2).getQuery()

		// Log
		//logger.info("Lecture du dernier numero d'evenement genere OK!");

		// Calcul du numero d'evenement
		Long numEve = rs != null && rs.next() ? numEve = Long.valueOf( rs.getString("num") != null ? rs.getString("num") : "0" )  + 1 : 1l;

		// Log
		//logger.info("Calcul du prochain numero d'evenement OK!");

		// Fermeture de cnx
		if(rs != null) {
			rs.close(); 
			if(rs.getStatement() != null) {
				rs.getStatement().close();
			}
		}

		bkeve eve = new bkeve(null, params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEve), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), "001", Math.abs(totalPull + totalPullMarch - totalPush - totalPushMarch), "VIRMAC", new Date(), params.getCodeUtil(), 0d, 0d, 0d, Math.abs(totalPull + totalPullMarch - totalPush - totalPushMarch) );
		eve.setEta("IG"); //eve.setEtap("VA");
		eve.setId(now());
		
		if(totalPull - totalPush < 0) {
			eve.setDebiteur( rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), rsCpteMTN.getString("clc"), rsCpteMTN.getString("cli"), rsCpteMTN.getString("nom"), rsCpteMTN.getString("ges"),  Math.abs(totalPull - totalPush), Math.abs(totalPull - totalPush), dvaCredit, rsCpteMTN.getDouble("sde"));
			eve.setCrediteur(rsCpteDAPPull.getString("age"), rsCpteDAPPull.getString("dev"), rsCpteDAPPull.getString("ncp"), rsCpteDAPPull.getString("suf"), rsCpteDAPPull.getString("clc"), rsCpteDAPPull.getString("cli"), rsCpteDAPPull.getString("inti"), rsCpteDAPPull.getString("utic"), Math.abs(totalPull - totalPush), Math.abs(totalPull - totalPush), dvaCredit, rsCpteDAPPull.getDouble("sde"));
		} else {
			eve.setCrediteur(rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), rsCpteMTN.getString("clc"), rsCpteMTN.getString("cli"), rsCpteMTN.getString("nom"), rsCpteMTN.getString("ges"),  Math.abs(totalPull - totalPush), Math.abs(totalPull - totalPush), dvaDebit, rsCpteMTN.getDouble("sde"));
			eve.setDebiteur(rsCpteDAPPush.getString("age"), rsCpteDAPPush.getString("dev"), rsCpteDAPPush.getString("ncp"), rsCpteDAPPush.getString("suf"), rsCpteDAPPush.getString("clc"), rsCpteDAPPush.getString("cli"), rsCpteDAPPush.getString("inti"), rsCpteDAPPush.getString("utic"), Math.abs(totalPull - totalPush), Math.abs(totalPull - totalPush), dvaDebit, rsCpteDAPPush.getDouble("sde"));
		}

		if(totalPullMarch - totalPushMarch < 0) {
			eve.setDebiteur( rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), rsCpteMTN.getString("clc"), rsCpteMTN.getString("cli"), rsCpteMTN.getString("nom"), rsCpteMTN.getString("ges"),  Math.abs(totalPullMarch - totalPushMarch), Math.abs(totalPullMarch - totalPushMarch), dvaCredit, rsCpteMTN.getDouble("sde"));
			eve.setCrediteur(rsCpteDAPPull.getString("age"), rsCpteDAPPull.getString("dev"), rsCpteDAPPull.getString("ncp"), rsCpteDAPPull.getString("suf"), rsCpteDAPPull.getString("clc"), rsCpteDAPPull.getString("cli"), rsCpteDAPPull.getString("inti"), rsCpteDAPPull.getString("utic"), Math.abs(totalPullMarch - totalPushMarch), Math.abs(totalPullMarch - totalPushMarch), dvaCredit, rsCpteDAPPull.getDouble("sde"));
		} else {
			eve.setCrediteur(rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), rsCpteMTN.getString("clc"), rsCpteMTN.getString("cli"), rsCpteMTN.getString("nom"), rsCpteMTN.getString("ges"),  Math.abs(totalPullMarch - totalPushMarch), Math.abs(totalPullMarch - totalPushMarch), dvaDebit, rsCpteMTN.getDouble("sde"));
			eve.setDebiteur(rsCpteDAPPush.getString("age"), rsCpteDAPPush.getString("dev"), rsCpteDAPPush.getString("ncp"), rsCpteDAPPush.getString("suf"), rsCpteDAPPush.getString("clc"), rsCpteDAPPush.getString("cli"), rsCpteDAPPush.getString("inti"), rsCpteDAPPush.getString("utic"), Math.abs(totalPullMarch - totalPushMarch), Math.abs(totalPullMarch - totalPushMarch), dvaDebit, rsCpteDAPPush.getDouble("sde"));
		}

		// Debit du Cpte DAP des Pull
		for(Transaction tx : trans) {
			if(tx.getTypeOperation().equals(TypeOperation.PULL)){
				if(!tx.getSubscriber().isMerchant()){
					eve.getEcritures().add( new bkmvti(rsCpteDAPPull.getString("age"), rsCpteDAPPull.getString("dev"), rsCpteDAPPull.getString("cha"), rsCpteDAPPull.getString("ncp"), rsCpteDAPPull.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteDAPPull.getString("clc"), dco, null, dvaDebit, tx.getAmount(), "D", "PULL/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "N", tx.getSubscriber().getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteDAPPull.getString("age"), rsCpteDAPPull.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;					
				}else{
					// Recuperation du compte MTN Marchand
					PlageTransactions plg = tx.getSubscriber().getProfil();
					ResultSet rsCpteDAPPullMarch = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(8).getQuery(), new Object[]{ plg.getNcpDAPPull().split("-")[0], plg.getNcpDAPPull().split("-")[1], plg.getNcpDAPPull().split("-")[2] });

					if(!rsCpteDAPPullMarch.next() ) throw new Exception("Comptes de Liaisons inexistants");

					eve.getEcritures().add( new bkmvti(rsCpteDAPPullMarch.getString("age"), rsCpteDAPPullMarch.getString("dev"), rsCpteDAPPullMarch.getString("cha"), rsCpteDAPPullMarch.getString("ncp"), rsCpteDAPPullMarch.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteDAPPullMarch.getString("clc"), dco, null, dvaDebit, tx.getAmount(), "D", "PULL/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "N", tx.getSubscriber().getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteDAPPullMarch.getString("age"), rsCpteDAPPullMarch.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;
				}
			}
		}

		// Crédit du Cpte MTN du total des Pull
		if(totalPull > 0) eve.getEcritures().add( new bkmvti(rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("cha"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteMTN.getString("clc"), dco, null, dvaDebit, totalPull, "C", "COMPENS/" + TypeOperation.PULL.toString().toUpperCase() + "/" + datop, "N", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), totalPull, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;

		// Debit du Cpte MTN du total des Push
		if(totalPush > 0) eve.getEcritures().add( new bkmvti(rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("cha"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteMTN.getString("clc"), dco, null, dvaCredit, totalPush, "D", "COMPENS/" + TypeOperation.PUSH.toString().toUpperCase() + "/" + datop, "N", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), totalPush, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;

		// Crédit du Cpte MTN du total des Pull
		if(totalPullMarch > 0) eve.getEcritures().add( new bkmvti(rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("cha"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteMTN.getString("clc"), dco, null, dvaDebit, totalPullMarch, "C", "COMPENS MARCH/" + TypeOperation.PULL.toString().toUpperCase() + "/" + datop, "N", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), totalPullMarch, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;

		// Debit du Cpte MTN du total des Push
		if(totalPushMarch > 0) eve.getEcritures().add( new bkmvti(rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), rsCpteMTN.getString("cha"), rsCpteMTN.getString("ncp"), rsCpteMTN.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteMTN.getString("clc"), dco, null, dvaCredit, totalPushMarch, "D", "COMPENS MARCH/" + TypeOperation.PUSH.toString().toUpperCase() + "/" + datop, "N", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteMTN.getString("age"), rsCpteMTN.getString("dev"), totalPushMarch, null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;

		ResultSet rsCpteDAPPushMarch = null;

		// Credit du DAP des Push
		for(Transaction tx : trans) {
			if(tx.getTypeOperation().equals(TypeOperation.PUSH)){
				if(!tx.getSubscriber().isMerchant()){
					eve.getEcritures().add( new bkmvti(rsCpteDAPPush.getString("age"), rsCpteDAPPush.getString("dev"), rsCpteDAPPush.getString("cha"), rsCpteDAPPush.getString("ncp"), rsCpteDAPPush.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteDAPPush.getString("clc"), dco, null, dvaCredit, tx.getAmount(), "C", "PUSH/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "N", tx.getSubscriber().getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteDAPPush.getString("age"), rsCpteDAPPush.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;
				}else{
					// Recuperation du compte MTN
					PlageTransactions plg = tx.getSubscriber().getProfil();
					rsCpteDAPPushMarch = executeFilterSystemQuery(dsCBS, MoMoHelper.getDefaultCBSQueries().get(8).getQuery(), new Object[]{ plg.getNcpDAPPush().split("-")[0], plg.getNcpDAPPush().split("-")[1], plg.getNcpDAPPush().split("-")[2] });

					if(!rsCpteDAPPushMarch.next() ) throw new Exception("Comptes de Liaisons inexistants");

					eve.getEcritures().add( new bkmvti(rsCpteDAPPushMarch.getString("age"), rsCpteDAPPushMarch.getString("dev"), rsCpteDAPPushMarch.getString("cha"), rsCpteDAPPushMarch.getString("ncp"), rsCpteDAPPushMarch.getString("suf"), params.getCodeOperation(), MoMoHelper.padText(String.valueOf(numEc), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, params.getCodeUtil(), eve.getEve(), rsCpteDAPPushMarch.getString("clc"), dco, null, dvaCredit, tx.getAmount(), "C", "PUSH/" + new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate()) + "/" + tx.getPhoneNumber().substring(3), "N", tx.getSubscriber().getId().toString(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, rsCpteDAPPushMarch.getString("age"), rsCpteDAPPushMarch.getString("dev"), tx.getAmount(), null, null, null, null, null, null, null, null, eve.getNat(), "VA", null, null) ); numEc++;
//					rsCpteDAPPushMarch = null;
				}
			}
		}
				
		// Sauvegarde l'evenement (Dans le Portal)
		mobileMoneyDAO.save(eve);
		
		// On libere tous les ResultSet
		if(rsCpteMTN!=null) {
			rsCpteMTN.close(); 
			if(rsCpteMTN!=null) {
				rsCpteMTN.getStatement().close(); 
			}
		}
		if(rsCpteDAPPull!=null) {
			rsCpteDAPPull.close();  
			if(rsCpteDAPPull!=null) {
				rsCpteDAPPull.getStatement().close(); 
			}
		}
		if(rsCpteDAPPush!=null) {
			rsCpteDAPPush.close();  
			if(rsCpteDAPPush!=null) {
				rsCpteDAPPush.getStatement().close(); 
			}
		}
		if(rsCpteDAPPushMarch!=null) {
			rsCpteDAPPushMarch.close(); 
			if(rsCpteDAPPushMarch!=null) {
				rsCpteDAPPushMarch.getStatement().close(); 
			}
		}
		if(rs!=null) {
			rs.close(); 
			if(rs!=null) {
				rs.getStatement().close();
			}
		}
		rsCpteMTN = null; rsCpteDAPPull = null; rsCpteDAPPush = null; rsCpteDAPPushMarch = null; rs = null;
		trans.clear();
		// CBS_CNX_OPTI
		if(conCBS != null ) conCBS.close();
		
		resultats.addAll(eve.getEcritures());
		
		return resultats;

	}
	
	
	/**
	 * Generer les elements du rapport de compensation a visualiser
	 * @param mvts liste de toutes les EC des transactions et de compensation
	 * @param user
	 * @param mois
	 * @return
	 * @throws Exception
	 */
	public FactMonth visualiserRapportCompensation(List<bkmvti> mvts, String user, String mois, String filename) throws Exception {

		List<FactMonthDetails> resultats = new ArrayList<FactMonthDetails>();
		
		details = new ArrayList<FactMonthDetails>();
		mntD = 0d; mntC = 0d; nbrC = 0; nbrD = 0;
		
		// checkGlobalConfig(); //Parameters params = findParameters();
		params = findParameters();

		// Initialisation de DataStore d'Amplitude
		if(dsCBS == null) findCBSDataSystem();

		Date dco = getDateComptable(dsCBS);
				
		// Ouverture d'une cnx vers la BD du Core Banking
		if(conCBS == null || conCBS.isClosed()) conCBS = getSystemConnection(dsCBS);

		// Suspension temporaire du mode blocage dans la BD du Core Banking
		if(dsCBS.getDbConnectionString().indexOf("informix") > 0) {
			conCBS.createStatement().executeUpdate("SET ISOLATION TO DIRTY READ");
			//conCBS.createStatement().executeUpdate("SET LOCK MODE TO WAIT");
		}

		// Initialisation du fichier a generer
		FileWriter fw = new FileWriter(filename);
		
		// Desactivation du mode AUTO COMMIT
//		conCBS.setAutoCommit(false);
//
//		// Initialisation d'un preparateur de requetes
//		PreparedStatement ps = conCBS.prepareStatement(new bkmvti().getSaveQuery());

		// Subscriber infos map from code client
		Map<String,Subscriber> mapCompte = new HashMap<String, Subscriber>();
		ResultSet rst = null;
		int nb = 0;
		// Parcours des ecritures
		
		for(bkmvti mvt : mvts) {

			// Ecriture pour chaque transaction de la date comptable du jour
			mvt.setDco(dco);
			mvt.setUti(user);
			mvt.setOpe(params.getCodeIntegration());
			
			// ADD
			// Recuperation 
			FactMonthDetails det = new FactMonthDetails(Integer.valueOf(mvt.getAge()), mvt.getNcp()+"-"+mvt.getClc(),"", mvt.getLib(),null,null,null,mvt.getSen(),mvt.getMon());
			det.setTxtage(mvt.getAge());
			det.setDev(mvt.getDev());
			String cle = mvt.getNcp().trim().substring(0, 7);
//			logger.info("CODE CLIENT : "+cle);
			
			if(mapCompte.containsKey(cle)){
//				logger.info("CODE CLIENT TROUVE : "+cle);
				det.setIntitule(mapCompte.get(cle).getCustomerName());
				det.setLibage(mapCompte.get(cle).getAgeName());
				det.setDateAbon(mapCompte.get(cle).getDate());
				det.setDateDernfact(mapCompte.get(cle).getDateDernCompta());
			}else{
				Subscriber sub = findSubscriber(cle);
				if(sub != null){
					det.setIntitule(sub.getCustomerName());
					det.setLibage(sub.getAgeName());
					det.setDateAbon(sub.getDate());
					det.setDateDernfact(sub.getDateDernCompta());
					if(mapCompte.isEmpty()){
						mapCompte.put(cle, sub);
					}else if(!mapCompte.containsKey(cle)){
						mapCompte.put(cle, sub);
					}
				}
				else {
					// recherche d'un compte non client
					rst = executeFilterSystemQuery(dsCBS,MoMoHelper.getDefaultCBSQueries().get(7).getQuery(),new Object[]{mvt.getAge(),mvt.getNcp(),mvt.getClc()});
					if(rst.next()){
						det.setIntitule(rst.getString("inti"));
						if(mapCompte.isEmpty()){
							Subscriber subs = new Subscriber();
							subs.setCustomerName(rst.getString("inti"));;
							mapCompte.put(cle, subs);
						}else if(!mapCompte.containsKey(cle)){
							Subscriber subs = new Subscriber();
							subs.setCustomerName(rst.getString("inti"));;
							mapCompte.put(cle, subs);
						}
					}
//					rst = null;
//					logger.info("SUBS NOT FOUND : "+cle);
				}
			}
			if(cle.startsWith("434")) nb = nb + 1;
			resultats.add(det);
			if("C".equalsIgnoreCase(mvt.getSen())){
				mntC = mntC+mvt.getMon();
				nbrC++;
			}else{
				mntD = mntD+mvt.getMon();
				nbrD++;
			}
			dev = mvt.getDev();
			// FIN ADD
			
			// Save EC in file
			// Ecriture de la ligne dans le fichier
			fw.write(mvt.getFileLine().concat("\n"));
		}
		if(rst != null) {
			rst.close(); 
			if(rst.getStatement() != null) {
				rst.getStatement().close(); 
			}
			rst = null;
		}
		if(conCBS != null ) conCBS.close();
		
		// Fermeture du fichier
		fw.flush(); fw.close();
		
		// ADD
		Collections.sort(resultats);
		FactMonth fac = new FactMonth(dco, "D", "C", mntD, mntC, nbrD, nbrC, user, new Date(), "");
		fac.setDev(dev);
		
		fac = mobileMoneyDAO.save(fac);
		for(FactMonthDetails det : resultats) det.setParent(fac);
		mobileMoneyDAO.saveList(resultats,true);
		
		fac.setDetails(resultats);
	//	logger.info("NBRE TAX/COM : "+nb);
		
		if(params.getCbsServices()) {
			if(fac != null) {
				boolean b = Boolean.FALSE;
				b = sendAccountsEntriesToCoreBanking(mvts);
		//		logger.info("SendEntries: "+b);
				if(!b) {
					return null;
				}
			}
		}
		
		return fac;
		// FIN ADD
	}
	
	
	@Override
	public List<Transaction> annulerEves(List<Transaction> transactions) throws Exception{
		
//		// Initialisation de DataStore d'Amplitude
//		if(dsCBS == null) findCBSDataSystem();
//
//		// Ouverture d'une cnx vers la BD du Core Banking
//		if(conCBS == null || conCBS.isClosed()) conCBS = getSystemConnection(dsCBS);
//
//		// Suspension temporaire du mode blocage dans la BD du Core Banking
//		if(dsCBS.getDbConnectionString().indexOf("informix") > 0) {
//			conCBS.createStatement().executeUpdate("SET ISOLATION TO DIRTY READ");
//		}

		return cancelEvenements(transactions, dsCBS);
	}
		
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Transaction> cancelEvenements(List<Transaction> transactions, DataSystem dsCBS) throws Exception {
		params = findParameters();
		if(transactions == null || transactions.isEmpty()) return new ArrayList<Transaction>();

		// Initialisation de la restriction in
		String in = ""; int n = 0;
		List eves = new ArrayList();
		List<String> __eves = new ArrayList();
		
		for( int i=0; i<transactions.size(); i++ ){
			// Maj de la transaction
			transactions.get(i).setPosted(Boolean.TRUE);
			transactions.get(i).setDateTraitement(new Date());
			transactions.get(i).setARetraiter(Boolean.FALSE);
			
			in += transactions.get(i).getId() + ", ";
			n++;

			// Si on a deja atteind 1000 transactions selectionnees
			if(n>0 && n%1000==0) {

				// Construction de la liste des criteres
				in = "(".concat(in.substring(0, in.length()-2)).concat(")");

				// Recuperation de la liste des evenements associes aux transactions selectionnees
				eves.addAll(mobileMoneyDAO.getEntityManager().createQuery("Select e.eve, e.ope, e.id From bkeve e where e.transaction.id in "+ in +"").getResultList() ) ;
				//eves.addAll(mobileMoneyDAO.getEntityManager().createQuery("Select e.eve, e.ope From bkeve e where e.transaction.id in "+ in +"").getResultList() ) ;
				
				// Reinitialisation de la variable des criteres
				in = ""; n = 0;
			}
		}

		if(!in.isEmpty() && in.length()>2) {
			in = "(".concat(in.substring(0, in.length()-2)).concat(")");

			// Recuperation de la liste des evenements associes aux transactions selectionnees
			eves.addAll(mobileMoneyDAO.getEntityManager().createQuery("Select e.eve, e.ope, e.id From bkeve e where e.transaction.id in "+ in +"").getResultList() ) ;
			//to API 09-2020
			//eves.addAll(mobileMoneyDAO.getEntityManager().createQuery("Select e.id From bkeve e where e.transaction.id in "+ in +"").getResultList() ) ;
		}
		
		// MAJ en BD
		SaveListResult<Transaction> savedTrx = mobileMoneyDAO.saveList(transactions, Boolean.TRUE);
				
		// Si aucun evenement trouve on sort
		if(eves == null || eves.isEmpty()) return new ArrayList<Transaction>();

		// Initialisation du filtre des evenements et du code operation
		String inEve = ""; String ope = ""; n = 0;
// 09-2020 
		for(Object o : eves) {
			inEve += "'".concat( ((Object[])o)[0].toString()  ).concat("', ");
			if(ope.isEmpty()) ope = ((Object[])o)[1].toString();
			n++;

			// Si on a deja atteind 1000 transactions selectionnees
			if(n>0 && n%1000==0) {

				// Construction de la liste des criteres
				inEve = "(".concat(inEve.substring(0, inEve.length()-2)).concat(")");

				// MAJ des transaction de
				executeUpdateSystemQuery(dsCBS, "update bkeve set eta='IG', etap='VA' where eve in "+ inEve +" and ope=? ", new Object[]{ ope  } );

				// Reinitialisation de la variable des criteres
				inEve = ""; n = 0;
			}
			
			//__eves.add(((Object[])o)[2].toString());
			__eves.add(String.valueOf(((Object[])o)[2]));
		}
		
		// Construction du filtre des evenements a mettre a jour dans Delta
		if(!inEve.isEmpty() && inEve.length()>2) { 
			inEve = "(".concat(inEve.substring(0, inEve.length()-2)).concat(")");

			// MAJ de l'etat des evenements dans le Core Banking
			executeUpdateSystemQuery(dsCBS, "update bkeve set eta='IG', etap='VA' where eve in "+ inEve +" and ope=? ", new Object[]{ ope  } );

		}
		
	 if(params.getCbsServices()) {
		 boolean b = registerIgnoreEventsToCoreBanking(__eves);
		 logger.info("EOD: "+b);	
	 }
		
		return savedTrx.getRegistered();
	}
	// FIN ADD

	
	// DEBUT MAJ INTEGRATION PAR MVT EXT
	@SuppressWarnings("rawtypes")
	public void exportRapprochmentBkmvti(String fileName, List<bkmvti> ecritures, List<bkmvti> ecrituresCli, List<bkmvti> ecrituresMTN, List<bkmvti> ecrituresCliRapp, 
			List<bkmvti> ecrituresMTNRapp, List<bkmvti> ecrituresCliUnijamb, List<bkmvti> ecrituresMTNUnijamb, Map mts, List<Transaction> transUnijamb) throws Exception {
		
		// Initialisation d'un document Excel
		SXSSFWorkbook wb = new SXSSFWorkbook();
		
		logger.info("*************** ecritures.size() *************** : " + ecritures.size());
		if(ecritures.size() > 0 ) exportECBkmvti(ecritures, wb, "ALL_EC");
		logger.info("*************** ecrituresCli.size() *************** : " + ecrituresCli.size());
		if(ecrituresCli.size() > 0 ) exportECBkmvti(ecrituresCli, wb, "EC_NCP_CLIENT");
		logger.info("*************** ecrituresMTN.size() *************** : " + ecrituresMTN.size());
		if(ecrituresMTN.size() > 0 ) exportECBkmvti(ecrituresMTN, wb, "EC_NCP_DAP_COM_TAX");
		logger.info("*************** ecrituresCliRapp.size() *************** : " + ecrituresCliRapp.size());
		if(ecrituresCliRapp.size() > 0 ) exportECBkmvti(ecrituresCliRapp, wb, "EC_RAPPROCHEES_NCP_CLIENT");
		logger.info("*************** ecrituresMTNRapp.size() *************** : " + ecrituresMTNRapp.size());
		if(ecrituresMTNRapp.size() > 0 ) exportECBkmvti(ecrituresMTNRapp, wb, "EC_RAPPROCHEES_NCP_DAP_COM_TAX");
		logger.info("*************** ecrituresCliUnijamb.size() *************** : " + ecrituresCliUnijamb.size());
		if(ecrituresCliUnijamb.size() > 0 ) exportECBkmvti(ecrituresCliUnijamb, wb, "EC_UNIJAMBISTES_NCP_CLIENT");
		logger.info("*************** ecrituresMTNUnijamb.size() *************** : " + ecrituresMTNUnijamb.size());
		if(ecrituresMTNUnijamb.size() > 0 ) exportECBkmvti(ecrituresMTNUnijamb, wb, "EC_UNIJAMBISTES_NCP_DAP_COM_TAX");
		logger.info("*************** montants EC transactions *************** : ");
		exportMontantsECBkmvti(mts, wb, "EC_MONTANTS");
		logger.info("*************** transUnijamb.size() *************** : " + transUnijamb.size());
		if(transUnijamb.size() > 0 ) exportTransactionUnijambiste(transUnijamb, wb, "TRX_UNIJAMBISTES");
		
		// Sauvegarde du fichier
		FileOutputStream fileOut = new FileOutputStream(PortalHelper.JBOSS_DATA_DIR + File.separator + PortalHelper.PORTAL_RESOURCES_DATA_DIR + File.separator + PortalHelper.PORTAL_DOWNLOAD_DATA_DIR + File.separator + fileName);
		wb.write(fileOut);
		fileOut.close();
	}
	
	
	
	public void exportECBkmvti(List<bkmvti> ecritures, Object file, String sheetName) throws Exception {
        
		if (ecritures == null || ecritures.isEmpty()) {
            return;
        }

        SXSSFWorkbook wb;

        if (file instanceof SXSSFWorkbook) {
            wb = (SXSSFWorkbook) file;
        } else {
            wb = new SXSSFWorkbook();
        }

        // Initialisation de la Feuille courante
        Sheet sheet = wb.createSheet(sheetName);

        // Creation d'une ligne
        Row row = sheet.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
        row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "Utilisateur" );
		row.createCell(2).setCellValue( "Agence" );
		row.createCell(3).setCellValue( "Date Comptable" );
		row.createCell(4).setCellValue( "Libellé" );
		row.createCell(5).setCellValue( "Sens" );
		row.createCell(6).setCellValue( "N° de Cpte" );
		row.createCell(7).setCellValue( "Nature opération" );
		row.createCell(8).setCellValue( "Montant" );
		row.createCell(9).setCellValue( "Opération" );
		row.createCell(10).setCellValue( "N° de Pièce" );

        //Initialisation du compteur
		int i = 1;

        for (bkmvti ec : ecritures) {
        	// Initialisation d'une ligne
			row = sheet.createRow(i);

			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(1).setCellValue( ec.getUti() );
			row.createCell(2).setCellValue( ec.getAge() );
			row.createCell(3).setCellValue( new SimpleDateFormat("dd/MM/yyyy").format(ec.getDco()) );
			row.createCell(4).setCellValue( ec.getLib() );
			row.createCell(5).setCellValue( ec.getSen() );
			row.createCell(6).setCellValue( ec.getNcp() );
			row.createCell(7).setCellValue( ec.getNat() );
			row.createCell(8).setCellValue( ec.getMon() );
			row.createCell(9).setCellValue( ec.getOpe() );
			row.createCell(10).setCellValue( ec.getPie() );

        }
        
    }
	
	
	
	@SuppressWarnings("rawtypes")
	public void exportMontantsECBkmvti(Map mts, Object file, String sheetName) throws Exception {
        
		if (mts == null || mts.isEmpty()) {
            return;
        }

        SXSSFWorkbook wb;

        if (file instanceof SXSSFWorkbook) {
            wb = (SXSSFWorkbook) file;
        } else {
            wb = new SXSSFWorkbook();
        }

        // Initialisation de la Feuille courante
        Sheet sheet = wb.createSheet(sheetName);

        // Creation d'une ligne
        Row row = sheet.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
        row.createCell(0).setCellValue( "Intitulé" );
		row.createCell(1).setCellValue( "Montant PULL" );
		row.createCell(2).setCellValue( "Montant PUSH" );
		row.createCell(3).setCellValue( "Montant FRAIS MAC" );
		row.createCell(4).setCellValue( "Total" );
		
		// Initialisation d'une ligne
		row = sheet.createRow(1);

		// Affichage des colonnes dans la fichier excel
		row.createCell(0).setCellValue( "Compte Client" );
		row.createCell(1).setCellValue( (Double) mts.get("TOTAL_PULL") );
		row.createCell(2).setCellValue( (Double) mts.get("TOTAL_PUSH") );
		row.createCell(3).setCellValue( (Double) mts.get("TOTAL_FRAIS") );
		row.createCell(4).setCellValue( (Double) mts.get("TOTAL_CLIENT") );
		
		// Initialisation d'une ligne
		row = sheet.createRow(2);

		// Affichage des colonnes dans la fichier excel
		row.createCell(0).setCellValue( "Compte MTN/BANK (COMPENS/COM/TAX)" );
		row.createCell(1).setCellValue( (Double) mts.get("TOTAL_COMPENS_PULL") );
		row.createCell(2).setCellValue( (Double) mts.get("TOTAL_COMPENS_PUSH") );
		row.createCell(3).setCellValue( (Double) mts.get("TOTAL_COMTAX") );
		row.createCell(4).setCellValue( (Double) mts.get("TOTAL_COMPENS") );
		
    }
	
	
	
	public void exportTransactionUnijambiste(List<Transaction> trx, Object file, String sheetName) throws Exception {
        
		if (trx == null || trx.isEmpty()) {
            return;
        }

        SXSSFWorkbook wb;

        if (file instanceof SXSSFWorkbook) {
            wb = (SXSSFWorkbook) file;
        } else {
            wb = new SXSSFWorkbook();
        }

        // Initialisation de la Feuille courante
        Sheet sheet = wb.createSheet(sheetName);

        // Creation d'une ligne
        Row row = sheet.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
        row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "Opération" );
		row.createCell(2).setCellValue( "Montant" );
		row.createCell(3).setCellValue( "N° de Compte" );
		row.createCell(4).setCellValue( "Date Opération" );
		row.createCell(5).setCellValue( "Statut" );
		row.createCell(6).setCellValue( "Commission" );
		row.createCell(7).setCellValue( "TTC" );
		row.createCell(8).setCellValue( "Posté?" );
		row.createCell(9).setCellValue( "MTN Trx ID" );

        //Initialisation du compteur
		int i = 1;

        for (Transaction tx : trx) {
        	// Initialisation d'une ligne
			row = sheet.createRow(i);

			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(1).setCellValue( tx.getTypeOperation().getValue() );
			row.createCell(2).setCellValue( tx.getAmount() );
			row.createCell(3).setCellValue( tx.getAccount() );
			row.createCell(4).setCellValue( new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(tx.getDate()) );
			row.createCell(5).setCellValue( tx.getStatus().getValue() );
			row.createCell(6).setCellValue( tx.getCommissions() );
			row.createCell(7).setCellValue( tx.getTtc() );
			row.createCell(8).setCellValue( tx.getPosted() );
			row.createCell(9).setCellValue( tx.getMtnTrxId() );

        }
	} 
	


	public List<bkmvti> extractECFromTransactions(List<Transaction> transactions) throws Exception {

		// Recuperation de la liste des ecritures comptables des transactions selectionnees
		List<bkmvti> mvts = getECFromTransactions(transactions, false);
		logger.info("*************** mvts.size() *************** : " + mvts.size());
		
		List<bkmvti> ecritures = genererECCompensation(transactions);
		logger.info("*************** ecritures.size() *************** : " + ecritures.size());
		mvts.addAll(ecritures);

		// S'il n'existe aucune ecriture on sort
		if(mvts == null || mvts.isEmpty()) return null;
		
		return mvts;
	}
		
	
	public List<bkmvti> extractECIntoFile(List<bkmvti> mvts, String fileName) throws Exception {

		// S'il n'existe aucune ecriture on sort
		if(mvts == null || mvts.isEmpty()) return null;

		// Initialisation du fichier a generer
		FileWriter fw = new FileWriter(fileName);

		// Parcours des ecritures
		logger.info("*************** extractECIntoFile fileName *************** : " + fileName);
		for(bkmvti mvt : mvts) {

			// Ecriture de la ligne dans le fichier
			fw.write(mvt.getFileLine().concat("\n"));

		}

		// Fermeture du fichier
		fw.flush(); fw.close();
		
		return mvts;
	}
	// FIN MAJ INTEGRATION PAR MVT EXT

	
	@Override
	@AllowedRole(name = "ctrlReservations", displayName = "MoMo.Generer.Rapport.Controle.Reservations")
	public void controleReservations(Boolean journee, String heureDebut, String heureFin, String filename) throws Exception {
		// TODO Auto-generated method stub
		logger.info("CONTROL OF RESERVATIONS");
		Map<Long, Transaction> mapTrans = new HashMap<>();
		Map<String, Transaction> mapOK = new HashMap<>();
		Map<String, Transaction> mapNoReserv = new HashMap<>();
		Map<String, Transaction> mapReservKO = new HashMap<>();
		
		List<Transaction> trxPortal = filterTransactionPortalToControl(journee, heureDebut, heureFin);
		for(Transaction t : trxPortal) mapTrans.put(t.getId(), t);
		
		List<bkeve> evesPortal = filterEvePortalToControl(trxPortal);
		
		List<String> numEvesCBS = filterTransactionCBSToControl(journee, heureDebut, heureFin);
		
		// Parcours de la liste des eves des transactions dans Portal
		for(bkeve eve : evesPortal){
			String even = eve.getEve();
//			logger.info("EVE : "+even);
			// Si l'evenement existe dans le CBS
			if(numEvesCBS.contains(even)){
				logger.info("CONTAINS OK!");
				// Recuperation de la transaction correspondante a l'evenement
//				Transaction t = mapTrans.get(even);
				Transaction t = eve.getTransaction();
				// Si la transaction existe
				if(t!=null){
					// Ajout de la transaction dans la liste des transactions reservees
					mapOK.put(even, t);
					// Retrait de l'evenement de la liste des evenements du CBS
					numEvesCBS.remove(even);
				}
			}
			else{
				logger.info("CONTAINS KO!");
				// Recuperation de la transaction correspondante a l'evenement
//				Transaction t = mapTrans.get(even);
				Transaction t = eve.getTransaction();
				// Si la transaction existe
				if(t!=null){
					// Ajout de la transaction dans la liste des transactions non reservees
					mapNoReserv.put(even, t);
				}
			}
		}
		// S'il y'a encore des evenements du CBS
		if(!numEvesCBS.isEmpty()){
			logger.info("NUMEVESCBS NON VIDE");
			for(String e : numEvesCBS){
				// Rechercher l'evenement et la transaction correspondante dans Portal (meme si elle a echouee)
				List<bkeve> leve = mobileMoneyDAO.filter(bkeve.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("eve", e)), null, null, 0, 1);
				if(leve!=null && !leve.isEmpty()){
					Transaction t =  leve.get(0).getTransaction();
//					logger.info("TRX : "+t.toString());
					if(t!=null) mapReservKO.put(e, t);
				}
			}
			
		}
		
		// Extraire le résultat dans un fichier Excel
		exportRapportControleReservationIntoExcelFile(mapOK, mapNoReserv, mapReservKO, filename);
	}
	
	
	@Override
	public List<Transaction> filterTransactionPortalToControl(Boolean journee, String heureDebut, String heureFin) {
		// TODO Auto-generated method stub
		logger.info("FILTER TRANSACTIONS PORTAL TO CONTROL");
		String req = "From Transaction where (typeOperation='"+TypeOperation.SUBSCRIPTION+"' or typeOperation='"+TypeOperation.PULL+"' or typeOperation='"+TypeOperation.PUSH+"') "
				+ "and status=:status and reconcilier=true and posted=false ";
		
		if(!journee) req = req +"and date_part('hour',date_op)>="+Double.valueOf(heureDebut)+" and date_part('hour',date_op)<="+Double.valueOf(heureFin)+" ";
		
		@SuppressWarnings("unchecked")
		List<Transaction> trx = (List<Transaction>) mobileMoneyDAO.getEntityManager().createQuery(req).setParameter("status", TransactionStatus.SUCCESS).getResultList();
		
		return trx;
	}
	
	
	@Override
	public List<bkeve> filterEvePortalToControl(List<Transaction> data) {
		// TODO Auto-generated method stub
		logger.info("FILTER EVE PORTAL TO CONTROL");
		logger.info("TRX SIZE : "+data.size());
		List<bkeve> eves =  new ArrayList<bkeve>();
		int max = 5000;
		int end = (data.size()/max);
		for(int i = 0; i <= end; i++){
			// Parcours de la liste des abonnements a comptabiliser
			if(i==end){
				if(max*i!=data.size()){
					eves.addAll(mobileMoneyDAO.filter(bkeve.class, null, 
							RestrictionsContainer.getInstance().add(Restrictions.in("transaction", new ArrayList<Transaction>(data.subList(max*i, data.size())))), 
							null, null, 0, -1));
				}
			}
			else {
				eves.addAll(mobileMoneyDAO.filter(bkeve.class, null, 
						RestrictionsContainer.getInstance().add(Restrictions.in("transaction", new ArrayList<Transaction>(data.subList(max*i, (max*(i+1)))))), 
						null, null, 0, -1));
			}

		}
		logger.info("EVES SIZE : "+eves.size());
		return eves;
	}
	
	
	@Override
	public List<String> filterTransactionCBSToControl(Boolean journee, String heureDebut, String heureFin) {
		// TODO Auto-generated method stub
		logger.info("FILTER EVES CBS TO CONTROL");
		params = findParameters();
		List<bkeve> eves = new ArrayList<bkeve>();
		List<String> numEves = new ArrayList<String>();
		String tableEve = "bkeve";
				
		try{
			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();
			if(isModeNuit()) tableEve = "bkeve_eod";
			logger.info("TABLE EVE = "+tableEve);
			String req = "select * from "+tableEve+" where eta='VA' and ope="+params.getCodeOperation()+" ";
			
			if(!journee) {
				req = req +"and hsai>=? and hsai<=? ";
				heureDebut = heureDebut.concat(":00:00");
				heureFin = heureFin.concat(":59:59");
			}
			ResultSet rs = executeFilterSystemQuery(dsCBS, req, journee ? null : new Object[]{heureDebut, heureFin});
			
			// Parcours du resultat
			while(rs != null && rs.next()){
//				logger.info("EVE CBS : "+rs.getString("eve").trim());
				// Ajout de l'element trouve a la collection
				numEves.add(rs.getString("eve").trim());
	//			eves.add( new bkeve(null, rs.getString("ope").trim(), rs.getString("eve").trim(), mht, nat, dco, uti, tcom1, frai1, ttax1, mnt1) );
			}
			
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
		} catch(Exception e){e.printStackTrace();}
		
		logger.info("NUM EVES SIZE : "+numEves.size());
		return numEves;
	}
	
	
	public bkeve findEveFromCBS(String eve) {
		// TODO Auto-generated method stub
		logger.info("FILTER EVES CBS TO CONTROL");
		params = findParameters();
		bkeve even = null;
		String tableEve = "bkeve";
				
		try{
			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();
			if(isModeNuit()) tableEve = "bkeve_eod";
			
			String req = " * from "+tableEve+" where eta='VA' and eve='"+eve+"' and ope='"+params.getCodeOperation()+"' ";
			
			ResultSet rs = executeFilterSystemQuery(dsCBS, req, null);
			
			// Parcours du resultat
			while(rs != null && rs.next()){
				
				// Ajout de l'element trouve a la collection
				even = new bkeve(null, rs.getString("ope").trim(), rs.getString("eve").trim(), "", 0d, "", rs.getDate("dco"), "", 0d, 0d, 0d, rs.getDouble("mon1"));
				even.setLib1(rs.getString("lib1").trim());
			}
			
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
		} catch(Exception e){e.printStackTrace();}
		
		return even;
	}
	
	
	public void exportRapportControleReservationIntoExcelFile(Map<String, Transaction> mapOK , Map<String, Transaction> mapNoReserv, Map<String, Transaction> mapReservKO, String fileName ) throws Exception {

		// Initialisation d'un document Excel
		SXSSFWorkbook wb = new SXSSFWorkbook();

		// Initialisation de la Feuille courante
		Sheet sheet  = wb.createSheet("TRX RESERVEES");

		// Creation d'une ligne
		Row row = sheet.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
		row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "ID TRX BANQUE" );
		row.createCell(2).setCellValue( "ID TRX OPE" );
		row.createCell(3).setCellValue( "NUM EVE" );
		row.createCell(4).setCellValue( "TELEPHONE" );
		row.createCell(5).setCellValue( "MONTANT" );
		row.createCell(6).setCellValue( "ETAT RECONCILIE" );
		row.createCell(7).setCellValue( "STATUT" );

		// Initialisation du compteur
		int i = 1;
		
		for (Map.Entry eve : mapOK.entrySet()) {

			// Initialisation d'une ligne
			row = sheet.createRow(i);
			
			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(3).setCellValue( eve.getKey().toString() );

			row.createCell(7).setCellValue( "OK" );

			if(eve.getValue()!=null){
				Transaction t = (Transaction) eve.getValue();
				if(t.getId()!=null){
					row.createCell(1).setCellValue( t.getId() );
					row.createCell(2).setCellValue( t.getMtnTrxId() );

					row.createCell(4).setCellValue( t.getPhoneNumber() );
					row.createCell(5).setCellValue( t.getTtc() );
					row.createCell(6).setCellValue( t.getStatus().getValue() );
				}
				else{
					row.createCell(4).setCellValue( t.getPhoneNumber() );
					row.createCell(5).setCellValue( t.getTtc() );
				}
			}

		}

		/**
		 * DEUXIEME FEUILLE
		 */

		// Initialisation de la Feuille courante
		Sheet sheet2  = wb.createSheet("TRX NON RESERVEES");

		// Creation d'une ligne
		row = sheet2.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
		row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "ID TRX BANQUE" );
		row.createCell(2).setCellValue( "ID TRX OPE" );
		row.createCell(3).setCellValue( "NUM EVE" );
		row.createCell(4).setCellValue( "TELEPHONE" );
		row.createCell(5).setCellValue( "MONTANT" );
		row.createCell(6).setCellValue( "ETAT RECONCILIE" );
		row.createCell(7).setCellValue( "STATUT" );

		// Initialisation du compteur
		i = 1;
		
		for (Map.Entry eve : mapNoReserv.entrySet()) {

			// Initialisation d'une ligne
			row = sheet2.createRow(i);
			
			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(3).setCellValue( eve.getKey().toString() );

			row.createCell(7).setCellValue( "KO" );

			if(eve.getValue()!=null){
				Transaction t = (Transaction) eve.getValue();
				if(t.getId()!=null){
					row.createCell(1).setCellValue( t.getId() );
					row.createCell(2).setCellValue( t.getMtnTrxId() );

					row.createCell(4).setCellValue( t.getPhoneNumber() );
					row.createCell(5).setCellValue( t.getTtc() );
					row.createCell(6).setCellValue( t.getStatus().getValue() );
				}
				else{
					row.createCell(4).setCellValue( t.getPhoneNumber() );
					row.createCell(5).setCellValue( t.getTtc() );
				}
			}

		}

		/**
		 * TROISIEME FEUILLE
		 */

		// Initialisation de la Feuille courante
		Sheet sheet3  = wb.createSheet("RESERVATIONS FANTOMES");

		// Creation d'une ligne
		row = sheet3.createRow(0);

		// Affichage des entetes de colonnes du fichier excel
		row.createCell(0).setCellValue( "N°" );
		row.createCell(1).setCellValue( "ID TRX BANQUE" );
		row.createCell(2).setCellValue( "ID TRX OPE" );
		row.createCell(3).setCellValue( "NUM EVE" );
		row.createCell(4).setCellValue( "TELEPHONE" );
		row.createCell(5).setCellValue( "MONTANT" );
		row.createCell(6).setCellValue( "ETAT RECONCILIE" );
		row.createCell(7).setCellValue( "STATUT" );

		// Initialisation du compteur
		i = 1;
		
		for (Map.Entry eve : mapReservKO.entrySet()) {

			// Initialisation d'une ligne
			row = sheet3.createRow(i);
			
			// Affichage des colonnes dans la fichier excel
			row.createCell(0).setCellValue( i++ );
			row.createCell(3).setCellValue( eve.getKey().toString() );

			row.createCell(7).setCellValue( "KO" );

			if(eve.getValue()!=null){
				Transaction t = (Transaction) eve.getValue();
				if(t.getId()!=null){
					row.createCell(1).setCellValue( t.getId() );
					row.createCell(2).setCellValue( t.getMtnTrxId() );

					row.createCell(4).setCellValue( t.getPhoneNumber() );
					row.createCell(5).setCellValue( t.getTtc() );
					row.createCell(6).setCellValue( t.getStatus().getValue() );
				}
				else{
					row.createCell(4).setCellValue( t.getPhoneNumber() );
					row.createCell(5).setCellValue( t.getTtc() );
				}
			}

		}
		
		// Sauvegarde du fichier
		FileOutputStream fileOut = new FileOutputStream(PortalHelper.JBOSS_DATA_DIR + File.separator + PortalHelper.PORTAL_RESOURCES_DATA_DIR + File.separator + PortalHelper.PORTAL_DOWNLOAD_DATA_DIR + File.separator + fileName);
		wb.write(fileOut);
		fileOut.close();

	}

	@Override
	public Boolean isAccountActivity(String ncp) {
		// TODO Auto-generated method stub
		Boolean activity = false;
		try {

			params = findParameters();
			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();
			Date dco = getDateComptable(dsCBS);
			Date dateDebut = DateUtils.addDays(dco, -params.getPeriodeVerifTrxCBS());
			logger.info("PERIODE : "+dateDebut+" - "+dco);
			
			logger.info("CHECKING ACTIVITY !!! ");
			String sql = "select first 1 * from bkhis where age = ? and ncp = ? and ope in LIST_CODES_OPE and dco between ? and ? order by dco desc";

			String in = "";
			if(params.getCodesOpeTrx() != null && !params.getCodesOpeTrx().trim().isEmpty()) {
				for(String code : params.getCodesOpeTrx().split(",")) in += "'" + code + "'" + ", ";
			}
			if(!in.isEmpty()) in = "(".concat( in.substring(0, in.length()-2) ).concat(")");
			
			ResultSet rs = executeFilterSystemQuery(dsCBS, sql.replace("LIST_CODES_OPE", in), new Object[]{ncp.split("-")[0],ncp.split("-")[1], dateDebut, dco});
			if(rs != null && rs.next()) {
				logger.info("ACTIVITY OK!!! ");
				activity = true;
			}
			else logger.info("ACTIVITY KO!!! ");
			
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
			
		} catch(Exception e){}
		
		return activity;
	}
	
	
	@Override
	public Boolean isNewAccount(String ncp) {
		// TODO Auto-generated method stub
		Boolean activity = false;
		try {
			logger.info("CHECKING NEW ACCOUNT [NCP : "+ncp.split("-")[1]+"]");
			Date date = DateUtils.addDays(new Date(), -params.getPeriodeVerifNewNcpCBS());
			ResultSet rs = executeFilterSystemQuery(dsCBS, "select ncp, dou from bkcom where ncp = ? and dou >= ? ", new Object[]{ncp.split("-")[1], date});
			if(rs != null && rs.next()) {
				logger.info("NEW ACCOUNT OK!!! ");
				activity = true;
			}
			else  logger.info("NEW ACCOUNT KO!!! ");
			
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
			
		} catch(Exception e){}
		
		return activity;
	}
	
	
	@Override
	public Boolean isNewTrxDay(String ncp) {
		// TODO Auto-generated method stub
		Boolean activity = false;
		params = findParameters();
		try{
			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();
			String req = "select * from bkeve where eta='VA' and (ncp1 = ? or ncp2 = ?) and ope in LIST_CODES_OPE ";
			
			logger.info("CHECKING NEW TRX!!! ");
			
			String in = "";
			if(params.getCodesOpeTrx() != null && !params.getCodesOpeTrx().trim().isEmpty()) {
				for(String code : params.getCodesOpeTrx().split(",")) in += "'" + code + "'" + ", ";
			}
			if(!in.isEmpty()) in = "(".concat( in.substring(0, in.length()-2) ).concat(")");
			logger.info("LIST CODES OPE : "+in);			
			ResultSet rs = executeFilterSystemQuery(dsCBS, req.replace("LIST_CODES_OPE", in), new Object[]{ncp.split("-")[1], ncp.split("-")[1]});
			
			// Parcours du resultat
			if(rs != null && rs.next()){
				logger.info("NEW TRX OK!!! ");
				activity = true;
			}
			else logger.info("NEW TRX KO!!! ");
			
			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}
			// CBS_CNX_OPTI
			if(conCBS != null ) conCBS.close();
		} catch(Exception e){e.printStackTrace();}
		
		return activity;
	}
	

	@Override
	public List<User> getGFCDA(User gfc, String ncp) {
		// TODO Auto-generated method stub
		logger.info("NCP = "+ncp);
		List<User> users = new ArrayList<User>();
		logger.info("BRANCH = "+gfc.getBranch());
		users = getUsersDA(gfc.getBranch(), ncp.split("-")[1].substring(0, 7));
		
		users.add(gfc);
		
		return users;
	}
	
	
	public String checkTypeClient(String matricule){
		String result = null;
		try
		{			
			// Initialisation de DataStore d'Amplitude
			if(dsCBS == null) findCBSDataSystem();

			String sql ="select tcli from bkcli where cli = '"+matricule+"' ";

			ResultSet rs = executeFilterSystemQuery(dsCBS, sql, new Object[]{});
			// Parcours du resultat
			while(rs != null && rs.next()){
				result = rs.getString("tcli").trim();
			}

			if(rs != null) {
				rs.close(); 
				if(rs.getStatement() != null) {
					rs.getStatement().close();
				}
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}
	

	@SuppressWarnings("unchecked")
	public List<User> getUsersDA(Branch agence, String matricule){

		List<User> users = new ArrayList<User>();
		List<String> usersAdd = new ArrayList<String>();
		List<ProcessUser> process = new ArrayList<ProcessUser>();

		if(agence == null) return null;

		if(StringUtils.isBlank(matricule)) {
			process = (List<ProcessUser>) mobileMoneyDAO.getEntityManager().createQuery("select processUsers From Process ue where ue.workflow.typeOperation.code=:codeTypeOpe and ue.etape =:etape ").setParameter("codeTypeOpe", "OUV").setParameter("etape", Etape.CONTROLEDA).getResultList();
		}
		else {
			TypeClient typeClient = "1".equals(checkTypeClient(matricule)) ? TypeClient.PARTICULIER : TypeClient.ENTREPRISE;
			process = (List<ProcessUser>) mobileMoneyDAO.getEntityManager().createQuery("select processUsers From Process ue where ue.workflow.typeOperation.code=:codeTypeOpe and ue.workflow.typeClient=:typeClient and ue.etape =:etape ").setParameter("codeTypeOpe", "OUV").setParameter("typeClient", typeClient).setParameter("etape", Etape.CONTROLEDA).getResultList();
		}

		if("00006".equals(agence.getCode()) || "00099".equals(agence.getCode())) {
			for(ProcessUser i : process){
				if(Boolean.FALSE.equals(i.getUser().getSuspended()) && !usersAdd.contains(i.getUser().getLogin())){
					users.add(i.getUser());	
					usersAdd.add(i.getUser().getLogin());
				}
			}
		}
		else {
			for(ProcessUser i : process){
				if(Boolean.FALSE.equals(i.getUser().getSuspended()) && agence.getId().equals(i.getBranch().getId()) && !usersAdd.contains(i.getUser().getLogin())){
					users.add(i.getUser());	
					usersAdd.add(i.getUser().getLogin());
				}
			}
		}
		

		usersAdd.clear();

		if(process.isEmpty() || process.size() == 0) return null;
				
		return users;
	}
	
	
	//affiche toutes les resiliations à un  package
	@Override
//	@AllowedRole(name = "filterPackages", displayName = "MoMo.filterPackages")
	public List<ClientProduit> listAllResiliations() throws JsonGenerationException, 
		JsonMappingException, UnsupportedEncodingException, IOException, JSONException, DAOAPIException, URISyntaxException {
		    params = findParameters();
		    if(params== null) return null;
			String[] parameter = params.getUrlServicePackage().split(":");
			String[] _param = parameter[2].split("/");
			
			return this.mobileMoneyAPI.getClientProduitDAO().resiliations(parameter[1].substring(2), parameter[0], _param[0], parameter[2].substring(_param[0].length()), "MoMo-06");
	}
	
	//affiche toutes les abonnements à un  package
	@Override
//	@AllowedRole(name = "filterPackages", displayName = "MoMo.filterPackages")
	public List<ClientProduit> listAllAbonnements() throws JsonGenerationException, 
		JsonMappingException, UnsupportedEncodingException, IOException, JSONException, DAOAPIException, URISyntaxException {
		 	params = findParameters();
		 	if(params== null) return null;
			String[] parameter = params.getUrlServicePackage().split(":");
			String[] _param = parameter[2].split("/");
			return this.mobileMoneyAPI.getClientProduitDAO().abonnements(parameter[1].substring(2), parameter[0], _param[0], parameter[2].substring(_param[0].length()),"MoMo-06");
	}
	
	//affiche le statut de l'abonnement d'un client à un package
	@Override
//	@AllowedRole(name = "filterPackages", displayName = "MoMo.filterPackages")
	public String statusAbon(ClientProduit client)
			throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException,
			JSONException, DAOAPIException, URISyntaxException {
		// TODO Auto-generated method stub
		params = findParameters();
		if(params== null) return null;
		String[] parameter = params.getUrlServicePackage().split(":");
		String[] _param = parameter[2].split("/");
		
		return this.mobileMoneyAPI.getClientProduitDAO().statusAbon(parameter[1].substring(2), parameter[0], _param[0], parameter[2].substring(_param[0].length()),client);
	}
	
	@Override
	public List<Resiliation> filterResiliations(RestrictionsContainer rc, OrderContainer orders) {
		// TODO Auto-generated method stub
		return mobileMoneyDAO.filter(Resiliation.class, null, rc, orders, null, 0, -1);
	}
	
	/**
	 * Methode d'enregistrement de l'evenement
	 */
	public bkeve registerEventToCoreBanking(bkeve eve) 
			throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException,
		    JSONException, DAOAPIException, URISyntaxException{
		params = findParameters();
		if(params== null) return null;
		String url = params.getUrlCbsApi();

		return mobileMoneyAPI.getSendEventToCbsDAO().sendEventToCoreBanking(url, eve);
	}
	
	
	/**
	 * Methode de retraitement des trx marquées apres tfjo module
	 */
	public boolean registerIgnoreEventsToCoreBanking(List<String> eve) 
			throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException,
		    JSONException, DAOAPIException, URISyntaxException{
		params = findParameters();
		if(params== null) return false;
		String url = params.getUrlCbsApi();

		return mobileMoneyAPI.getSendEventToCbsDAO().sendPostEOD(url, eve);
	}
	
	
	/**
	 * Methode d'annulation des evenements
	 */
	public bkeve reversalEventsCoreBanking(String eve) 
			throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException,
		    JSONException, DAOAPIException, URISyntaxException{
		params = findParameters();
		if(params== null) return null;
		String url = params.getUrlCbsApi();

		return mobileMoneyAPI.getSendEventToCbsDAO().reverseEventFromCoreBanking(url, eve);
	}
	
	/**
	 * Methode de verification de l'evenement dans le corebanking
	 * @param eve
	 * @return
	 */
	public bkeve checkEventInCoreBanking(String eve) 
			throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException,
		    JSONException, DAOAPIException, URISyntaxException{
		params = findParameters();
		if(params == null) return null;
		String url = params.getUrlCbsApi();

		return mobileMoneyAPI.getSendEventToCbsDAO().checkEventToCoreBanking(url, eve);
	}
	
	/**
	 * Methode de verification de solde dans le corebanking
	 * @return
	 */
	public Double getSoldeInCoreBanking(String ncp) 
			throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException,
		    JSONException, DAOAPIException, URISyntaxException{
		params = findParameters();
		if(params == null) return null;
		String url = params.getUrlCbsApi();

		return mobileMoneyAPI.getSendEventToCbsDAO().getSoldeToCoreBanking(url, ncp);
	}
	
	/**
	 * Methode de ecritures apres tfjo module
	 */
	public boolean sendAccountsEntriesToCoreBanking(List<bkmvti> bkmvti) 
			throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException,
		    JSONException, DAOAPIException, URISyntaxException{
		params = findParameters();
		if(params== null) return false;
		String url = params.getUrlCbsApi();

		return mobileMoneyAPI.getSendEventToCbsDAO().sendEntriesToCoreBanking(url, bkmvti);
	}
	
	/**
	 * Envoi des sms
	 */
	public String sendSimpleSms(String sms, String phone) throws JsonGenerationException, JsonMappingException, UnsupportedEncodingException, IOException,
    JSONException, DAOAPIException, URISyntaxException{
		params = findParameters();
		if(params== null) return null;
			String url = params.getUrlServiceSms();
		
		return mobileMoneyAPI.getSendEventToCbsDAO().sendSimpleSMS(url, sms, phone);
	}
	
	@Override
	public void sendSimpleMail(String msg, String subject, String title){
		String from = "";
		List<String> filesNames = new ArrayList<String>();
		List<String> filesPath = new ArrayList<String>();
		List<String> listDest = new ArrayList<String>();
		
		params = findParameters();
		
		if(params != null) 
			
			from = params.getEmailfrom();
		    String[] mailPlafond = params.getMailPlafond().split(";");
		    for(String mail : mailPlafond) {
		    	listDest.add(mail);
		    }
		    
		    
			try {
				 
				  AfrilandSendMail.sendMail(filesNames, filesPath, listDest,  null, subject, msg, params.getMailSender(), 
				      params.getPwdSender(), from, title, params.getSmtpServerName(), params.getPortEnvoiMail());
			} catch (Exception e) {
			// TODO Auto-generated catch block
			}
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<Transaction> getTransactionControl(String sql) {

		List<Transaction> ltrx = new ArrayList<Transaction>();
		ltrx = mobileMoneyDAO.getEntityManager().createQuery(sql)
				.setFirstResult(0)
				.setMaxResults(3)
				.getResultList();
		
		return ltrx;
	}

}
