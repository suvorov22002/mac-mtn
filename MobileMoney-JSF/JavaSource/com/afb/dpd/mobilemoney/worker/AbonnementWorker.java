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
import com.afb.dpd.mobilemoney.jpa.enums.TransactionStatus;
import com.afb.dpd.mobilemoney.jpa.tools.ClientProduit;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.yashiro.persistence.utils.dao.tools.OrderContainer;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

import afb.dsi.dpd.portal.jpa.entities.User;

public class AbonnementWorker {
	
	private static Log logger = LogFactory.getLog(AbonnementWorker.class);
	private static TimerTask task;
	private static java.util.Timer timer;
	private static TraceRobot traceResil = new TraceRobot();
	
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
							if("ON".equals(params.getLancementRobot())){
								logger.info("********************************* RESILIATION ROBOT ON AND ABONNEMENT TASK STARTED *********************************");
								process();
							}
							else{
								logger.info("********************************* RESILIATION ROBOT ON AND ABONNEMENT TASK STOPPED *********************************");
							}
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
				System.out.println("************* VERIFICATION-ABONNEMENT ************");
				
				Parameters params = MobileMoneyViewHelper.appManager.findParameters();
				
				traceResil = new TraceRobot();
				traceResil.setDatetimeTrace(new Date());
				HashMap<String, String> mapHeure = new HashMap<String, String>();
				if("ON".equals(params.getExecutionRobot()) && StringUtils.isNotBlank(params.getHeureVerifResiliation()) && StringUtils.isNotBlank(params.getUrlServicePackage())) {
					for(String s : params.getHeureVerifResiliation().split("-")) {
						if(!s.equals("")) mapHeure.put(s, s);
					}
					try {
						if(mapHeure.containsKey(new SimpleDateFormat("HH").format(new Date()))) {
							if("ON".equals(params.getExecutionRobot())){
								System.out.println("************* processAbonnement ************");
								processResiliation();
							}
						}
					} catch (Exception e) {
						traceResil.setCommentaire("ERREUR VERIFICATION ABONNEMENT");
						traceResil.setStatus(TransactionStatus.FAILED);
						MobileMoneyViewHelper.appDAOLocal.save(traceResil);
					}
				}
	}
	
	public static void processResiliation(){
		
		Parameters params = MobileMoneyViewHelper.appManager.findParameters();
		try {
			
			List<Resiliation> resiliations = MobileMoneyViewHelper.appDAOLocal.filter(Resiliation.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("dateResiliation", new Date())), null, null, 0, -1);
			HashMap<Object, Object> mapAbon = new HashMap<Object, Object>();
			for(Resiliation r : resiliations) {
				mapAbon.put(r.getCustomerId(), r);
			}
			// recherche de toutes les resiliations de la veille
			List<ClientProduit> results = MobileMoneyViewHelper.appManager.listAllAbonnements();
			
			for(ClientProduit c : results){
				
					if(StatusAbon.FACTURE.equals(c.getStatut()) && !mapAbon.containsKey(c.getMatricule())) {
						// Initialisation d'un conteneur de restrictions
						RestrictionsContainer rc = RestrictionsContainer.getInstance();

						// Ajout de la restriction sur le code du client
						rc.add(Restrictions.eq("customerId", c.getMatricule()));
						
						// Initialisation d'un conteneur d'ordres
						OrderContainer orders = OrderContainer.getInstance().add(Order.desc("date"));

						// Filtre des souscriptions
						List<Subscriber> souscriptions = MobileMoneyViewHelper.appManager.filterSubscription(rc, orders);
					
						//annulation du contrat de souscription
						for(Subscriber  s : souscriptions) {
							if(!StatutContrat.WAITING.equals(s.getStatus())) {
								annulerFacturation(s.getId());
								traceResil.setCommentaire("PROCESS ABONNEMENT");
								traceResil.setStatus(TransactionStatus.SUCCESS);
								MobileMoneyViewHelper.appDAOLocal.save(traceResil);
							}
						}
						
						
					}
			}
		} catch (Exception e) {

		}
	}
	
	 private static void annulerFacturation(Long idAnnulation) {
			
		   User user = MobileMoneyViewHelper.getSessionUser();
		   Subscriber subscriber = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Subscriber.class,idAnnulation,null);
		   
		   try {
			   MobileMoneyViewHelper.appManager.annulerFacturation(subscriber.getId(), user.getLogin());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 //Trace de la Resiliation
		   Resiliation resi = new Resiliation(null, subscriber.getCustomerId(), new Date(), "ABONNEMENT", "AUTO");
		   MobileMoneyViewHelper.appDAOLocal.save(resi);
	  }
}
