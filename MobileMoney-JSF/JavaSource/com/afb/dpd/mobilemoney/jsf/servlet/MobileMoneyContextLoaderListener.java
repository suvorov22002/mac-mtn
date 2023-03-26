/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.servlet;

import java.io.File;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import afb.dsi.dpd.portal.business.facade.IFacadeManagerRemote;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

import com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote;
import com.afb.dpd.mobilemoney.dao.IMobileMoneyDAOLocal;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.afb.dpd.mobilemoney.scanner.IScanner;
import com.afb.dpd.mobilemoney.worker.AbonnementWorker;
import com.afb.dpd.mobilemoney.worker.ResiliationWorker;
import com.afb.dpd.mobilemoney.worker.SendMailsWorker;
import com.afb.dpd.mobilemoney.worker.SimulationWorker;
import com.afb.dpd.mobilemoney.worker.TransactionWorker;

/**
 * Listener du chargement du Contexte de l'application
 * @author Francis DJIOMOU
 * @version 2.0
 */
public class MobileMoneyContextLoaderListener implements ServletContextListener {

	/**
	 *  Contexte JNDI
	 */
	private Context ctx = null;

	/**
	 * Logger
	 */
	protected Log logger = LogFactory.getLog(MobileMoneyContextLoaderListener.class);

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {

		try {

			// Si le contexte n'est pas null
			if(ctx != null) ctx.close();

		} catch (Exception e) {

			// On relance l'exception
			throw new RuntimeException("Erreur lors de la fermeture du Contexte JNDI", e);

		}
	}



	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent event) {

		try {

			// Initialisation du contexte JNDI
			ctx = new InitialContext();

			// Log
			logger.info("Initialisation du Contexte OK !!!");

		} catch (Exception e) {

			// On relance l'exception
			throw new RuntimeException("Erreur lors de l'initialisation du Contexte JNDI", e);

		}

		try {

			/*** DEMARRAGE DES SERVICES METIERS ***/

			MobileMoneyViewHelper.appDAOLocal = (IMobileMoneyDAOLocal)ctx.lookup(MobileMoneyViewHelper.APPLICATION_EAR + "/" + IMobileMoneyDAOLocal.SERVICE_NAME + "/local" );
			MobileMoneyViewHelper.appManager = (IMobileMoneyManagerRemote)ctx.lookup(MobileMoneyViewHelper.APPLICATION_EAR + "/" + IMobileMoneyManagerRemote.SERVICE_NAME + "/remote" );
			
			// Log
			logger.info("Demarrage des services metiers OK !!!");

		} catch (Exception e) {

			// On relance l'exception
			throw new RuntimeException("Erreur lors du chargement des Services Métiers", e);

		}

		try {

			/*** INTIALIZATIONS ***/
//			MobileMoneyViewHelper.appManager.processReconciliationAuto();
//
//			// Log
//			logger.info("Initialisations de la reconciliation automatique terminees avec succes!!!");

		} catch (Exception e) {

			// Log
			logger.error("Une erreur s'est produite lors des initialisations de la reconciliation automatique !!!");
			e.printStackTrace();

		}
		
		try {

			/*** INTIALIZATIONS ***/
			// Initialisation du service d'envoi des mailks et SMS
			SendMailsWorker.initChecking();

			// Log
			logger.info("Initialisations de l'envoi des mails terminees avec succes!!!");

		} catch (Exception e) {

			// Log
			logger.error("Une erreur s'est produite lors des initialisations de l'envoi des mails !!!");
			e.printStackTrace();

		}


		try {

			/*** INTIALIZATIONS ***/
			MobileMoneyViewHelper.appManager.initialisations();

			// Log
			logger.info("Initialisations terminees avec succes!!!");

		} catch (Exception e) {

			// Log
			logger.error("Une erreur s'est produite lors des initialisations !!!");
			e.printStackTrace();

		}

		
		try{

			/*** DEMARRAGE DU SERVICE FACADE DU PORTAIL ***/
			MobileMoneyViewHelper.portalFacadeManager = (IFacadeManagerRemote) ctx.lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );
			logger.info("PortalManager : "+MobileMoneyViewHelper.portalFacadeManager);
		} catch ( Exception e ) {
			logger.info("Portail Inaccessible!!!");
		}
		
		try {

			/*** AUTOSCAN DU MODULE ***/

			// Log
			logger.info("Auto-Scan du Module MobileMoney");

			// Demarrage du service
			IScanner scanner = (IScanner)ctx.lookup(MobileMoneyViewHelper.APPLICATION_EAR + "/" + IScanner.SERVICE_NAME + "/remote" );

			// Scan du module
			scanner.scanAndInitialiseModule(PortalHelper.JBOSS_DEPLOY_DIR + File.separator + MobileMoneyViewHelper.APPLICATION_EAR + ".ear");

			// Arret du service
			scanner = null;

			// Log
			logger.info("Fin de l'auto-scan avec succes!!!");
			
			TransactionWorker.initChecking();
//			ResiliationWorker.initChecking();
//			AbonnementWorker.initChecking();
//			SimulationWorker.initChecking();
//			SimulationWorker.initChecking2();

		} catch (Exception e) {

			// Log
			logger.error("Erreur lors de l'auto-scan du module !!!");

			e.printStackTrace();

		}


	}
}
