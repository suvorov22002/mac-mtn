package com.afb.dpd.mobilemoney.worker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.Resiliation;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.TraceRobot;
import com.afb.dpd.mobilemoney.jpa.enums.StatusAbon;
import com.afb.dpd.mobilemoney.jpa.enums.StatutContrat;
import com.afb.dpd.mobilemoney.jpa.enums.StatutService;
import com.afb.dpd.mobilemoney.jpa.tools.ClientProduit;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.afb.dpi.momo.services.MomoKYCServiceProxy;
import com.yashiro.persistence.utils.dao.tools.OrderContainer;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

public class ResiliationWorker {
	
	private static Log logger = LogFactory.getLog(ResiliationWorker.class);
	
	private static TimerTask task;
	
	private static java.util.Timer timer;
	
	public static void initChecking(){
		try{
			
			if(task != null) task.cancel();
			if(timer != null) timer.cancel();
				

			task = new TimerTask(){
				@Override
				public void run(){
					
					try {
												
						Parameters params = MobileMoneyViewHelper.appManager.findParameters();
						if("ON".equals(params.getExecutionRobot())){
							logger.info("********************************* RESILIATION ROBOT ON *********************************");
							if("ON".equals(params.getLancementRobot())){
								logger.info("********************************* RESILIATION ROBOT ON AND TASK STARTED *********************************");
								process();
							}
							else{
								logger.info("********************************* RESILIATION ROBOT ON AND TASK STOPPED *********************************");
							}
						}
						else{
							logger.info("********************************* RESILIATION ROBOT OFF *********************************");
						}

						params = null;
					}catch(Exception e){
						//e.printStackTrace();
					}
				}	
			};

			timer = new java.util.Timer(true);
			int sec = 60;
			int min = 15;
			timer.schedule(task, DateUtils.addMinutes(new Date(), 5) , min*sec*1000);	
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public static void runChecking(){
		try{

			if(task != null) task.cancel();
			if(timer != null) timer.cancel();


			task = new TimerTask(){
				@Override
				public void run(){

					process();

				}	
			};

			timer = new java.util.Timer(true);
			int sec = 60;
			int min = 15;
			timer.schedule(task, DateUtils.addMinutes(new Date(), 5) , min*sec*1000);	

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 */
	public static void cancelChecking(){
		try{

			if(task != null) task.cancel();
			if(timer != null) timer.cancel();

			task = null;
			timer = null;

		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void process(){
		//Verification des resiliations dans le CBS
				logger.info("************* VERIFICATION-RESILIATION ************");
				
				Parameters params = MobileMoneyViewHelper.appManager.findParameters();
				
				TraceRobot traceResil = new TraceRobot();
				traceResil.setDatetimeTrace(new Date());
			//	traceResil.setOperation("VERIFICATION-RESILIATION");
				HashMap<String, String> mapHeure = new HashMap<String, String>();
				if("ON".equals(params.getExecutionRobot()) && StringUtils.isNotBlank(params.getHeureVerifResiliation()) && StringUtils.isNotBlank(params.getUrlServicePackage())) {
					logger.info("************* params.getHeureVerifResiliation() ************ : " + params.getHeureVerifResiliation());
					for(String s : params.getHeureVerifResiliation().split("-")) {
						if(!s.equals("")) mapHeure.put(s, s);
					}

					try {
						if(mapHeure.containsKey(new SimpleDateFormat("HH").format(new Date()))) {
							logger.info("************* params.getExecutionRobot() ************ : " + params.getExecutionRobot());
							if("ON".equals(params.getExecutionRobot())){
								logger.info("************* processResiliation ************");
								processResiliation();
							}
							traceResil.setCommentaire("PROCESS RESILIATION");
							MobileMoneyViewHelper.appDAOLocal.save(traceResil);
						}
					} catch (Exception e) {
						traceResil.setCommentaire("ERREUR VERIFICATION RESILIATION");
						MobileMoneyViewHelper.appDAOLocal.save(traceResil);
					}
				}
				else {
					traceResil.setCommentaire("AUCUN PARAMETRE DEFINI : HORAIRE - URL");
					MobileMoneyViewHelper.appDAOLocal.save(traceResil);
				}
				
	}
	
	public static void processResiliation(){
		
		Parameters params = MobileMoneyViewHelper.appManager.findParameters();
		try {
			
			List<Resiliation> resiliations = MobileMoneyViewHelper.appDAOLocal.filter(Resiliation.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("dateResiliation", new Date())), null, null, 0, -1);
			HashMap<Object, Object> mapResi = new HashMap<Object, Object>();
			for(Resiliation r : resiliations) {
				mapResi.put(r.getCustomerId(), r);
			}
			
			List<ClientProduit> results = MobileMoneyViewHelper.appManager.listAllResiliations();
			logger.info("************* results.size() ************ : " + results.size());
			for(ClientProduit c : results){
				logger.info("************* c.getStatut() - c.getMatricule() ************ : " + c.getStatut() + " - " + c.getMatricule());
				if(StatusAbon.RESILIE.equals(c.getStatut()) && !mapResi.containsKey(c.getMatricule())) {
					// Initialisation d'un conteneur de restrictions
					RestrictionsContainer rc = RestrictionsContainer.getInstance();

					// Ajout de la restriction sur le code du client
					rc.add(Restrictions.eq("customerId", c.getMatricule()));
					//*** rc.add(Restrictions.ne("dateResiliationCBS", new SimpleDateFormat("ddMMyyyy").format(new Date())));

					// Initialisation d'un conteneur d'ordres
					OrderContainer orders = OrderContainer.getInstance().add(Order.desc("date"));

					// Filtre des souscriptions
					List<Subscriber> souscriptions = MobileMoneyViewHelper.appManager.filterSubscription(rc, orders);
					logger.info("************* souscriptions.size() ************ : " + souscriptions.size());
					
					//annulation du contrat de souscription
					for(Subscriber  s : souscriptions) {
						if(params.getEtatServiceSDP().equals(StatutService.TEST)){
							annulerSouscriptionTest(s.getId());
						}
						else {
							annulerSouscription(s.getId());
						}
							
					}
					
				}
			}
		} catch (Exception e) {

		}
	}
	
	/**
	 * Methode d'annulation du contrat de souscription
	 */
	public static void annulerSouscriptionTest(Long idAnnulation) {

		try {

			logger.info("************* annulerSouscription - idAnnulation ************ : " + idAnnulation);
			Subscriber subscriber = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Subscriber.class,idAnnulation,null);

			if(subscriber.getStatus().equals(StatutContrat.SUSPENDU)){
				return;
			}else{
				
				//TEST
				// Suspendre la souscription
				subscriber.setFacturer(false);
				subscriber.setStatus(StatutContrat.SUSPENDU);
				subscriber.setDateSuspendu(new Date());
				subscriber.setUtiSuspendu("AUTO");
				subscriber.setActive(false);
				MobileMoneyViewHelper.appDAOLocal.update(subscriber);
				
				//Trace de la Resiliation
				Resiliation resi = new Resiliation(null, subscriber.getCustomerId(), new Date(), "RESILIATION", "AUTO");
				MobileMoneyViewHelper.appDAOLocal.save(resi);
								
			}

		} catch(Exception e){
			e.printStackTrace();
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
		}

	}
	
	public static void annulerSouscription(Long idAnnulation) {
		
		Parameters param = MobileMoneyViewHelper.appManager.findParameters();
		Subscriber subscriber = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Subscriber.class,idAnnulation,null);
		
		try {
			
			// Annulation cote MTN
			MomoKYCServiceProxy proxy = new MomoKYCServiceProxy();
	        proxy.setEndpoint(param.getUrlKYCApi());
	        String unlinkage = "";
	       	// Recuperation du resultat du linkage depuis la plateforme de MTN
	        unlinkage = proxy.unlinkFinancialResourceInformation(subscriber.getFirstAccount().substring(6,13) + subscriber.getFirstAccount().substring(18), subscriber.getFirstPhone(), null);
	    
			logger.info("RESPONSE UNLINK = "+unlinkage);
			// Si on obtient une erreur
			if(unlinkage.contains("errorResponse") || unlinkage.contains("errorcode")){
				// Recuperer le message d'erreur
				String error = StringUtils.substringBetween(unlinkage, "errorcode=\"", "\"");
	        	logger.info("Erreur : "+error);
	        	if(unlinkage.contains("<arguments") && unlinkage.contains("name=")){
					// Recuperer le message d'erreur
					String name = StringUtils.substringBetween(unlinkage, "name=\"", "\"");
		        	error = error +" ("+name+" : ";
		        }
	        	if(unlinkage.contains("<arguments") || unlinkage.contains("value=")){
					// Recuperer le message d'erreur
					String value = StringUtils.substringBetween(unlinkage, "value=\"", "\"");
					error = error +value+")";
		        }
	        	// Annulation deja effectue cote MTN 
	        	if(error.contains("ACCOUNTHOLDER_NOT_FOUND") || error.contains("ACCOUNTHOLDER_NOT_ACTIVE") || (error.contains("COULD_NOT_PERFORM_OPERATION") && error.contains("FRI not found or it has been unlinked already"))){
	        		// Annulation cote bank
					MobileMoneyViewHelper.appManager.annulerSouscription(subscriber.getId(), "AUTO");
					
					//Trace de la Resiliation
					Resiliation resi = new Resiliation(null, subscriber.getCustomerId(), new Date(), "RESILIATION", "AUTO");
					MobileMoneyViewHelper.appDAOLocal.save(resi);
					// Message d'information
			//		PortalInformationHelper.showInformationDialog("Le contrat de souscription a été annulée avec succès!", InformationDialog.DIALOG_SUCCESS);
					
	        	}
	        	else{
		        	// Message d'information
		        //	PortalInformationHelper.showInformationDialog("Erreur : "+error, InformationDialog.DIALOG_ERROR);
		        	return;
	        	}
	        }
			
			// Si on obtient la reponse attendue
			if(unlinkage.contains("unlinkfinancialresourceinformationresponse")){
				// l'annulation de l'abonnement s'est bien deroulee
							
				// Annulation cote bank
				MobileMoneyViewHelper.appManager.annulerSouscription(subscriber.getId(), "AUTO");
				
				//Trace de la Resiliation
				Resiliation resi = new Resiliation(null, subscriber.getCustomerId(), new Date(), "RESILIATION", "AUTO");
				MobileMoneyViewHelper.appDAOLocal.save(resi);
				
				// Message d'information
	//			PortalInformationHelper.showInformationDialog("Le contrat de souscription a été annulée avec succès!", InformationDialog.DIALOG_SUCCESS);

			}
			
		} catch(Exception e) {
			
			// Traitement de l'exception
		//	PortalExceptionHelper.threatException(e);
		}
		
	}
}
