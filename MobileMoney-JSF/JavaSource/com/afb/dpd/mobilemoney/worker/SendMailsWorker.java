package com.afb.dpd.mobilemoney.worker;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.mail.MessagingException;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.ParametreAlertes;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.enums.StatutContrat;
import com.afb.dpd.mobilemoney.jpa.tools.MoMoHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyTools;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.afb.dpd.mobilemoney.jsf.tools.SendMail;
import com.yashiro.persistence.utils.dao.tools.AliasesContainer;
import com.yashiro.persistence.utils.dao.tools.OrderContainer;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;



public class SendMailsWorker {
	
	private static Log logger = LogFactory.getLog(SendMail.class);
	
	private static TimerTask task;

	private static java.util.Timer timer;

	/**
	 * 
	 */
	public static void initChecking(){
		try{

			if(task != null) task.cancel();
			if(timer != null) timer.cancel();
			
			task = new TimerTask(){
				@SuppressWarnings("unchecked")
				@Override
				public void run(){

					try {
						Parameters parameter = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Parameters.class, new Parameters().getCode(), null);
						
						List<ParametreAlertes> lpa = MobileMoneyViewHelper.appManager.consulterParametreAlertes();
						
						/*****************************************************************************************************************************
						 *************************************************** MAIL EN CAS D'INACTIVITE ************************************************ 
						 *****************************************************************************************************************************/
						Date date =  MobileMoneyViewHelper.appManager.getLastTrxDate();
						logger.info("-------------------- Verification de l'atteinte du delai d'inactivite -----------------");
						logger.info("-------------------- Date last trx : "+date+" -----------------");
						// Verifier si le delai d'inactivite est atteint
						if((new Date().getTime() - date.getTime()) >= parameter.getDelaiInactivite()*60*1000){
							logger.info("------------------------------------- OK --------------------------------");
							// Liste destinataires
							//List<String> listDest = SuiviCautionsTools.getListDest(seuil.getDestMail());
							List<String> listEmail = new ArrayList<String>();
							listEmail.add(parameter.getDestMailAlerte());
							
							if(!listEmail.isEmpty()){
								// Envoyer le mail
								sendMail(listEmail, "Bonjour,\n\nLe service (MAC MTN) est actuellement non fonctionnel depuis "
								+MobileMoneyViewHelper.appManager.getLastTrxDate()+" pour des causes suivantes : Aucune opération enregistrée depuis au moins "+parameter.getDelaiInactivite()
								+" minutes.\n\nCe mail est envoyé automatiquement. Bien vouloir ne pas répondre.");
								
							}
								
						}
						
						/*****************************************************************************************************************************
						 ********************************************* MAIL EN CAS D'ABONNEMENT NON VALIDE ******************************************* 
						 *****************************************************************************************************************************/

						//logger.info("DATE : "+DateUtils.addHours(new Date(), -24));
						// Rechercher les abonnements en attente de validation depuis plus de 24 heures
						// Initialisation d'un conteneur de restrictions
						RestrictionsContainer rc = RestrictionsContainer.getInstance();
						rc.add(Restrictions.eq("status", StatutContrat.WAITING ));
						rc.add(Restrictions.lt("date", DateUtils.addHours(new Date(), -24)));
						// Initialisation d'un conteneur d'ordres
						OrderContainer orders = OrderContainer.getInstance().add(Order.desc("age"));
						
						// Filtre du nombre de souscriptions en attente de validation par agence
						String req = "select s.age,s.ageName,count(s.age) as nbreAbon from Subscriber s where s.status = :status and s.date <= :date group by s.age,s.ageName order by s.age";
						List<Subscriber> souscriptions = MobileMoneyViewHelper.appDAOLocal.filter(Subscriber.class, AliasesContainer.getInstance().add("user", "user"), rc, orders, null, 0, -1);
//																		
						// Map nbre souscription en attente par agence
						Map<String,Long> mapAttente = new HashMap<String,Long>();
						//logger.info("MAP Nbre souscription par agence ");
						for(Subscriber s : souscriptions){
							if(mapAttente.containsKey(s.getAge())) mapAttente.put(s.getAge(), mapAttente.get(s.getAge())+1);
							else mapAttente.put(s.getAge(), 1l);
							//logger.info("AGENCE "+s.getAge()+" : "+mapAttente.get(s.getAge()));
						}
						//logger.info("Nbre de souscriptions en attente de validation depuis plus de 24 heures = " + mapAttente.size());
						//logger.info("Parcours MAP ");
						for (String mapKey : mapAttente.keySet()) { // utilise ici hashMap.get(mapKey) pour accéder aux valeurs }
							//logger.info("AGENCE "+mapKey);
							for(ParametreAlertes pa : lpa){
								//logger.info("DATE OK ? "+(DateUtils.addHours(new Date(), -1).getTime() <= pa.getLastSendMail().getTime()));
								if(mapKey.equals(pa.getCodeAgence()) && !pa.getEmails().isEmpty() && 
										(DateUtils.addHours(new Date(), -1).getTime() >= pa.getLastSendMail().getTime())){
									//logger.info("AGENCE PARA "+pa.getCodeAgence());
									//logger.info("OK! ENvoie du mail ");
									// Envoyer le mail
									sendMailValidation(pa.getEmails(), "Bonjour,\n\nAgence "+pa.getCodeAgence()+" ("+pa.getNomAgence()+")\nVous avez "+mapAttente.get(mapKey)+" abonnement"+(mapAttente.get(mapKey)>1 ? "s": "")
											+" en attente de validation depuis plus de 24 heures.\n\nVeuillez les valider afin d'activer le service pour les clients concernés."
											+ "\n\nCe mail est envoyé automatiquement. Bien vouloir ne pas répondre.");
									// Maj de la date du dernier mail d'alerte
									pa.setLastSendMail(new Date());
									break;
								}
							}
						}
						//logger.info("FIN Parcours MAP ");
						// Enregistrement des nouveaux parametres en BD
						MobileMoneyViewHelper.appDAOLocal.saveList(lpa, true);
					}catch(Exception e){
						//e.printStackTrace();
					}
				}	
			};

			timer = new java.util.Timer(true);
			// Repetition de la tache toutes les heures
			timer.schedule(task, DateUtils.addMinutes(new Date(), 1) , 3600*1000);	

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
	

	public static void sendMail(List<String> listDest, String msg){
		//SendMail sm = new SendMail();
		Parameters param = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Parameters.class, new Parameters().getCode(), null);
		
		try {

			String subject = "ALERTE! Interruption du Service MAC MTN";
			String to = StringUtils.join(listDest, ",");
			String format = "html";
			String from = param.getEmailfrom();
			
			MobileMoneyTools.sendHttpRequest(param.getUrlServiceMail(), msg, format, to, subject, from);
//			SendMail.sendMail(null, null, listDest, "ALERTE! Interruption du Service MAC MTN", msg, param);
			//
			String[] tels = param.getDestPhoneAlerte().trim().split(",");
			for(int i = 0; i < tels.length; i++){
				MobileMoneyViewHelper.appManager.sendSMS("Arret service (MAC MTN) depuis "+MobileMoneyViewHelper.appManager.getLastTrxDate()+". Aucune operation enregistree depuis au moins "+param.getDelaiInactivite()
				+" min", tels[i]);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	public static void sendMailValidation(List<String> listDest, String msg){
		logger.info("Sending validation mail ");
		Parameters param = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Parameters.class, new Parameters().getCode(), null);
		
		try {

			String subject = "MAC MTN : Souscriptions en attente de validation";
			String to = StringUtils.join(listDest, ",");
			String format = "html";
			String from = param.getEmailfrom();
			
			MobileMoneyTools.sendHttpRequest(param.getUrlServiceMail(), msg, format, to, subject, from);
//			SendMail.sendMail(null, null, listDest, "Souscriptions en attente de validation", msg, param);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}