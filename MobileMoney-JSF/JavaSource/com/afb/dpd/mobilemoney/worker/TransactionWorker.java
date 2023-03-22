package com.afb.dpd.mobilemoney.worker;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import javax.persistence.OptimisticLockException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.TraceRobot;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.enums.MTNTransactionStatus;
import com.afb.dpd.mobilemoney.jpa.enums.StatutService;
import com.afb.dpd.mobilemoney.jpa.enums.TransactionStatus;
import com.afb.dpd.mobilemoney.jsf.dto.TrxStatus;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyTools;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.afb.dpi.momo.services.MomoKYCServiceProxy;


public class TransactionWorker {

	private static Log logger = LogFactory.getLog(TransactionWorker.class);
	
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
				@Override
				public void run(){
					
					try {
												
						Parameters params = MobileMoneyViewHelper.appManager.findParameters();
						if("ON".equals(params.getExecutionRobot())){
							logger.info("********************************* ROBOT ON *********************************");
							if("ON".equals(params.getLancementRobot())){
								logger.info("********************************* ROBOT ON AND TASK STARTED *********************************");
								process();
							}
							else{
								logger.info("********************************* ROBOT ON AND TASK STOPPED *********************************");
							}
						}
						else{
							logger.info("********************************* ROBOT OFF *********************************");
						}

						params = null;
					}catch(Exception e){
						//e.printStackTrace();
					}
				}	
			};

			timer = new java.util.Timer(true);
			int sec = 60;
			int min = 1;
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
					
					try {
						
						Parameters params = MobileMoneyViewHelper.appManager.findParameters();
						if("ON".equals(params.getExecutionRobot())){
							logger.info("********************************* ROBOT ON *********************************");
							if("ON".equals(params.getLancementRobot())){
								logger.info("********************************* ROBOT ON AND TASK STARTED *********************************");
								process();
							}
							else{
								logger.info("********************************* ROBOT ON AND TASK STOPPED *********************************");
							}
						}
						else{
							logger.info("********************************* ROBOT OFF *********************************");
						}

						params = null;
					}catch(Exception e){
						//e.printStackTrace();
					}
				}	
			};

			timer = new java.util.Timer(true);
			int sec = 60;
			int min = 1;
			timer.schedule(task, DateUtils.addSeconds(new Date(), 5) , min*sec*1000);	

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
	
	
	/**
	 * 
	 */
	public static void process(){
		String status = "";
//		TraceRobot trace;
		
		try{
			// MAJ de ka derniere execution du robot
			
			
			Parameters params = MobileMoneyViewHelper.appManager.findParameters();
			
			List<Transaction> list = MobileMoneyViewHelper.appManager.filterTransactionProcessing();
			if(list.equals(null) || list.isEmpty() || list==null){
				logger.info("********************************* AUCUNE TRX *********************************");
				return;
			}
			logger.info("NBRE TRX : "+list.size());
			for(Transaction t :list){
				//logger.info("********************************* "+t.getMtnTrxId()+" TRX *********************************");
				try{
//					trace = new TraceRobot();
//					trace.setDatetimeTrace(new Date());

			        String trxStatus = "";
					
			        // Mode test
					if(params.getEtatServiceSDP().equals(StatutService.TEST)){
						// Generer des reponses aleatoires
						trxStatus = MobileMoneyTools.generateTransactionStatusResponse(t.getMtnTrxId(), t.getPhoneNumber(), t.getStatus());
					}
					else{
						// Recuperation du status de la transaction
						MomoKYCServiceProxy proxy = new MomoKYCServiceProxy();
				        proxy.setEndpoint(params.getUrlKYCApi());

						// gettransactionstatus
				        trxStatus = proxy.getTransactionStatus(t.getMtnTrxId(), t.getPhoneNumber(), null);
					}
					
					//logger.info("RESPONSE = "+trxStatus);
					
					TrxStatus statutResponse = new TrxStatus();
			        statutResponse = MobileMoneyTools.getTransactionStatus(trxStatus);
			        
			        // Si on obtient une erreur
					if(statutResponse.getError()!=null){
						String error = "Erreur : "+statutResponse.getError().getErrorcode()+" ("+statutResponse.getError().getErrormessage()+")";
						if(error.contains("TRANSACTION_NOT_FOUND") || error.contains("ACCOUNTHOLDER_NOT_FOUND")){
			        		logger.info("Erreur : "+error);
			        		logger.info("**************************** MTN ---> TRANSACTION_NOT_FOUND / ACCOUNTHOLDER_NOT_FOUND ****************************");
			        		// MAJ de transaction avec le message obtenu
			        		// Annulation de la Transaction
							Map<String, String> map = MobileMoneyViewHelper.appManager.processReversalTransactionECW(t.getId().toString());
							if(map.containsKey("statusCode")){
								// L'annulation s'est bien passee
								if(map.get("statusCode") == "200"){
									// Recuperation de la derniere version de la transaction annulee
									t = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Transaction.class, t.getId(), null);
									t.setStatus(TransactionStatus.CANCEL);
									t.setReconcilier(Boolean.TRUE);
									t.setDatereconcilier(new Date());
									t.setVerifier(Boolean.TRUE);
									t.setMessage(error);

									try{
										MobileMoneyViewHelper.appDAOLocal.update(t);
									}catch (OptimisticLockException e){
										logger.info("1- Tentative de modification d'une version récente par une version plus ancienne!!!");
										logger.error(e.getMessage());
										continue; // Passage a l'iteration suivante
									}
									// Save trace
						        	MobileMoneyTools.saveTraceRobot(t, TransactionStatus.SUCCESS, "TRANSACTION CANCELLED because "+error);
								}
								// Transaction echouee (sans evenements)
								else if(map.get("statusCode") == "500"){
									logger.info("Transaction echouee sans evenements");
									t.setVerifier(Boolean.TRUE);
									t.setReconcilier(Boolean.TRUE);
									t.setDatereconcilier(new Date());
									t.setMessage(error);
									
									try{
										MobileMoneyViewHelper.appDAOLocal.update(t);
									}catch (OptimisticLockException e){
										logger.info("2- Tentative de modification d'une version récente par une version plus ancienne!!!");
										logger.error(e.getMessage());
										continue; // Passage a l'iteration suivante
									}
									// Save trace
						        	MobileMoneyTools.saveTraceRobot(t, TransactionStatus.SUCCESS, error);
								}
								// L'annulation ne s'est pas bien passee
								else {
									logger.info("TRX NON RECONCILIEE **************************** Erreur : "+error);
									// Save trace
									MobileMoneyTools.saveTraceRobot(t, TransactionStatus.FAILED, "ECHEC REVERSAL : ERROR CODE "+map.get("statusCode"));
								}
							}
							map = null;
			        	}
			        	else{
			        		logger.info("TRX NON RECONCILIEE **************************** Erreur : "+error);
			        		// Save trace
				        	MobileMoneyTools.saveTraceRobot(t, TransactionStatus.FAILED, error);
			        	}
					}
					else if(statutResponse.getTransactionstatus()!=null){
						// Recuperer les parametres de la reponse
						status = statutResponse.getTransactionstatus().getStatus();
						//logger.info("MTN STATUT : "+status+" / BANK STATUT : "+t.getStatus());
						
						// Trx echouee cote MTN (FAILED) et reussite cote BANK (SUCCESS)
						if(status.equals(MTNTransactionStatus.FAILED.toString()) && TransactionStatus.SUCCESS.equals(t.getStatus())){
//							logger.info("**************************** STATUS FAILED (MTN) / SUCCESS (BANK) ****************************");
//							logger.info("TRX VERSION BEFORE : "+t.getVersion());
							// Annulation de la Transaction
							Map<String, String> map = MobileMoneyViewHelper.appManager.processReversalTransactionECW(t.getId().toString());
							if(map.containsKey("statusCode")){
								// L'annulation s'est bien passee
								if(map.get("statusCode") == "200"){
									logger.info("Annulation OK!!! ");
									// Recuperation de la derniere version de la transaction annulee
									t = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Transaction.class, t.getId(), null);
									logger.info("TRX VERSION AFTER : "+t.getVersion());
									t.setStatus(TransactionStatus.CANCEL);
									t.setReconcilier(Boolean.TRUE);
									t.setDatereconcilier(new Date());
									t.setVerifier(Boolean.TRUE);
									t.setMessage("CANCELED TRANSACTION");
									
									try{
									//	logger.info("UPDATING TRX ");
										MobileMoneyViewHelper.appDAOLocal.update(t);
									//	logger.info("UPDATE TRX OK!");
									}catch (OptimisticLockException e){
										logger.info("3- Tentative de modification d'une version récente par une version plus ancienne!!!");
										logger.error(e.getMessage());
										continue; // Passage a l'iteration suivante
									}
						        	// Save trace
						        	MobileMoneyTools.saveTraceRobot(t, TransactionStatus.SUCCESS, "CANCELED TRANSACTION");
								}
								// L'annulation ne s'est pas bien passee
								else {
									// Save trace
									MobileMoneyTools.saveTraceRobot(t, TransactionStatus.FAILED, "ECHEC REVERSAL : ERROR CODE "+map.get("statusCode"));
								}
							}
						}
						// Trx reussi cote MTN et echouee cote BANK (commente plus haut) -- MTN se chargera de regulariser de son cote
						// Trx en attente cote MTN (PENDING)
						else if(status.equals(MTNTransactionStatus.PENDING.toString()) && (TransactionStatus.SUCCESS.equals(t.getStatus()) || TransactionStatus.FAILED.equals(t.getStatus()))){
							// On ne fait rien
//							logger.info("TRX NON RECONCILIEE **************************** ");
//							logger.info("MTN = PENDING (On ne fait rien - On attend que la transaction soit complete cote MTN)");
							
				        	// Save trace
				        	MobileMoneyTools.saveTraceRobot(t, TransactionStatus.FAILED, "PENDING TRANSACTION (MTN)");
						}
						// Meme status cote MTN et cote BANK (SUCCESSFUL/SUCCESS) ou (FAILED/FAILED) 
						else if((status.equals(MTNTransactionStatus.SUCCESSFUL.toString()) && TransactionStatus.SUCCESS.equals(t.getStatus())) || 
								status.equals(MTNTransactionStatus.FAILED.toString()) && TransactionStatus.FAILED.equals(t.getStatus())){
							logger.info("**************************** STATUS IDENTIQUES ****************************");
							// Marque la transaction verifiee et la sauvegarder
							t.setReconcilier(Boolean.TRUE);
							t.setDatereconcilier(new Date());
							t.setVerifier(Boolean.TRUE);
							t.setMessage("OK");
							
							// Verifier si les tfj portal sont en cours et la trx executee avant le debut des tfj mais reconciliee apres
							if(!t.getTfjoLance() && MobileMoneyViewHelper.appManager.isTFJOPortalEnCours()){
								// Marquer la trx a retraiter
								t.setARetraiter(Boolean.TRUE);
							//	logger.info("TRX MARQUEE A RETRAITER : "+t.toString());
							}

							try{
								MobileMoneyViewHelper.appDAOLocal.update(t);
							}catch (OptimisticLockException e){
								logger.info("4- Tentative de modification d'une version récente par une version plus ancienne!!!");
								logger.error(e.getMessage());
								continue; // Passage a l'iteration suivante
							}
				        	// Save trace
				        	MobileMoneyTools.saveTraceRobot(t, TransactionStatus.SUCCESS, "OK");
						}
					}
					else {
						// Echec verification
						// Recuperer le message d'erreur
						String error = StringUtils.substringBetween(trxStatus, "(", ")"); // "\"/>"
						logger.info("TRX NON RECONCILIEE **************************** Erreur : "+error);
				        
			        	// Save trace
						MobileMoneyTools.saveTraceRobot(t, TransactionStatus.FAILED, error);
			        	
			        	error = null;
					}
					
					
/* DEBUT ANCIENNE IMPLEMENTATION VERIF STATUT */
					// Si on obtient une erreur
//					if(trxStatus.contains("errorResponse") || trxStatus.contains("errorcode")){
//						// Recuperer le message d'erreur
//						String error = StringUtils.substringBetween(trxStatus, "errorcode=\"", "\"");
//						if(trxStatus.contains("<arguments") && trxStatus.contains("name=")){
//							// Recuperer le message d'erreur
//							String name = StringUtils.substringBetween(trxStatus, "name=\"", "\"");
//				        	error = error +" ("+name+" : ";
//				        }
//			        	if(trxStatus.contains("<arguments") || trxStatus.contains("value=")){
//							// Recuperer le message d'erreur
//							String value = StringUtils.substringBetween(trxStatus, "value=\"", "\"");
//							error = error +value+")";
//				        }
//			        	
//			        	//
//			        	if(MobileMoneyTools.stringContainsItemFromList(error, MTNErrorCodes.getValues())){
////			        		logger.info("Contains : TRUE");
//			        	}
//			        	//
//			        	
//			        	if(error.contains("TRANSACTION_NOT_FOUND") || error.contains("ACCOUNTHOLDER_NOT_FOUND")){
//			        		logger.info("Erreur : "+error);
//			        		logger.info("**************************** MTN ---> TRANSACTION_NOT_FOUND / ACCOUNTHOLDER_NOT_FOUND ****************************");
//			        		// MAJ de transaction avec le message obtenu
//			        		// Annulation de la Transaction
//							Map<String, String> map = MobileMoneyViewHelper.appManager.processReversalTransactionECW(t.getId().toString());
//							if(map.containsKey("statusCode")){
//								// L'annulation s'est bien passee
//								if(map.get("statusCode") == "200"){
//									// Recuperation de la derniere version de la transaction annulee
//									t = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Transaction.class, t.getId(), null);
//									t.setStatus(TransactionStatus.CANCEL);
//									t.setReconcilier(Boolean.TRUE);
//									t.setDatereconcilier(new Date());
//									t.setVerifier(Boolean.TRUE);
//									t.setMessage(error);
//
//									try{
//										MobileMoneyViewHelper.appDAOLocal.update(t);
//									}catch (OptimisticLockException e){
//										logger.info("1- Tentative de modification d'une version récente par une version plus ancienne!!!");
//										logger.error(e.getMessage());
//										continue; // Passage a l'iteration suivante
//									}
//									
////									trace.setStatus(TransactionStatus.SUCCESS);
////						        	trace.setCommentaire("TRANSACTION CANCELLED because "+error);
//									// Save trace
//						        	MobileMoneyTools.saveTraceRobot(t, TransactionStatus.SUCCESS, "TRANSACTION CANCELLED because "+error);
//								}
//								// Transaction echouee (sans evenements)
//								else if(map.get("statusCode") == "500"){
//									logger.info("Transaction echouee sans evenements");
//									t.setVerifier(Boolean.TRUE);
//									t.setReconcilier(Boolean.TRUE);
//									t.setDatereconcilier(new Date());
//									t.setMessage(error);
//									
//									try{
//										MobileMoneyViewHelper.appDAOLocal.update(t);
//									}catch (OptimisticLockException e){
//										logger.info("2- Tentative de modification d'une version récente par une version plus ancienne!!!");
//										logger.error(e.getMessage());
//										continue; // Passage a l'iteration suivante
//									}
//									
////									trace.setStatus(TransactionStatus.SUCCESS);
////						        	trace.setCommentaire(error);
//									// Save trace
//						        	MobileMoneyTools.saveTraceRobot(t, TransactionStatus.SUCCESS, error);
//								}
//								// L'annulation ne s'est pas bien passee
//								else {
//									logger.info("TRX NON RECONCILIEE **************************** Erreur : "+error);
////									trace.setStatus(TransactionStatus.FAILED);
////									trace.setCommentaire("ECHEC REVERSAL : ERROR CODE "+map.get("statusCode"));
//									// Save trace
//									MobileMoneyTools.saveTraceRobot(t, TransactionStatus.FAILED, "ECHEC REVERSAL : ERROR CODE "+map.get("statusCode"));
//								}
//							}
////			        		t.setReconcilier(Boolean.TRUE);
////							t.setDatereconcilier(new Date());
////							t.setVerifier(Boolean.TRUE);
////							t.setMessage(error);
////							MobileMoneyViewHelper.appDAOLocal.update(t);
////			        		trace.setStatus(TransactionStatus.SUCCESS);
//							map = null;
//			        	}
//			        	else{
//			        		logger.info("TRX NON RECONCILIEE **************************** Erreur : "+error);
////				        	trace.setStatus(TransactionStatus.FAILED);
////				        	trace.setCommentaire(error);
//			        		// Save trace
//				        	MobileMoneyTools.saveTraceRobot(t, TransactionStatus.FAILED, error);
//			        	}
//			        	// Traitement a effectuer dans ce cas
////			        	trace.setOperation(t.getTypeOperation());
////			        	trace.setAccount(t.getAccount());
////			        	trace.setAmount(t.getAmount());
////			        	trace.setPhone(t.getPhoneNumber());
////			        	trace.setTrxId(t.getId());
////			        	
////			        	// Save trace
////			        	MobileMoneyViewHelper.appDAOLocal.save(trace);
//			        	//break;
//			        }
//					
//					// Si on obtient la reponse attendue
//					else if(trxStatus.contains("gettransactionstatusresponse")){
//						// Recuperer les parametres de la reponse
//						if(trxStatus.contains("status")){
//							status = StringUtils.substringBetween(trxStatus, "<status>", "</status>");
//							logger.info("MTN STATUT : "+status+" / BANK STATUT : "+t.getStatus());
//							
//							// Trx echouee cote MTN (FAILED) et reussite cote BANK (SUCCESS)
//							if(status.equals(MTNTransactionStatus.FAILED.toString()) && TransactionStatus.SUCCESS.equals(t.getStatus())){
//								logger.info("**************************** STATUS FAILED (MTN) / SUCCESS (BANK) ****************************");
//								logger.info("TRX VERSION BEFORE : "+t.getVersion());
//								// Annulation de la Transaction
//								Map<String, String> map = MobileMoneyViewHelper.appManager.processReversalTransactionECW(t.getId().toString());
//								if(map.containsKey("statusCode")){
//									// L'annulation s'est bien passee
//									if(map.get("statusCode") == "200"){
//										logger.info("Annulation OK!!! ");
//										// Recuperation de la derniere version de la transaction annulee
//										t = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Transaction.class, t.getId(), null);
//										logger.info("TRX VERSION AFTER : "+t.getVersion());
//										t.setStatus(TransactionStatus.CANCEL);
//										t.setReconcilier(Boolean.TRUE);
//										t.setDatereconcilier(new Date());
//										t.setVerifier(Boolean.TRUE);
//										t.setMessage("CANCELED TRANSACTION");
//										
//										try{
//											logger.info("UPDATING TRX ");
//											MobileMoneyViewHelper.appDAOLocal.update(t);
//											logger.info("UPDATE TRX OK!");
//										}catch (OptimisticLockException e){
//											logger.info("3- Tentative de modification d'une version récente par une version plus ancienne!!!");
//											logger.error(e.getMessage());
//											continue; // Passage a l'iteration suivante
//										}
//										
////										trace.setStatus(TransactionStatus.SUCCESS);
////							        	trace.setCommentaire("CANCELED TRANSACTION");
//							        	// Save trace
//							        	MobileMoneyTools.saveTraceRobot(t, TransactionStatus.SUCCESS, "CANCELED TRANSACTION");
//									}
//									// L'annulation ne s'est pas bien passee
//									else {
////										trace.setStatus(TransactionStatus.FAILED);
////										trace.setCommentaire("ECHEC REVERSAL : ERROR CODE "+map.get("statusCode"));
//										// Save trace
//										MobileMoneyTools.saveTraceRobot(t, TransactionStatus.FAILED, "ECHEC REVERSAL : ERROR CODE "+map.get("statusCode"));
//									}
//								}
//								
////								trace.setOperation(t.getTypeOperation());
////					        	trace.setAccount(t.getAccount());
////					        	trace.setAmount(t.getAmount());
////					        	trace.setPhone(t.getPhoneNumber());
////					        	trace.setTrxId(t.getId());
////					        	
////					        	// Save trace
////					        	MobileMoneyViewHelper.appDAOLocal.save(trace);
//							}
//							// Trx reussi cote MTN et echouee cote BANK
////							if(status.equals(MTNTransactionStatus.SUCCESSFUL) && TransactionStatus.FAILED.equals(t.getStatus())){
//								//logger.info("MTN = SUCCESS AND BANK = FAILED");
////								Map<String, String> map = null;
////								//logger.info("EXECUTE AGAIN");
////								// Rexecution de la Transaction
////								if(t.getTypeOperation().equals(TypeOperation.PULL)){
////									map = MobileMoneyViewHelper.appManager.pullTransactionECW(t.getMtnTrxId(), t.getPhoneNumber(), t.getAmount());
////								}else if(t.getTypeOperation().equals(TypeOperation.PUSH)){
////									map = MobileMoneyViewHelper.appManager.pushTransactionECW(t.getMtnTrxId(), t.getPhoneNumber(), t.getAmount());
////								}
////								//logger.info("END : STATUS = "+map.get("statusCode"));
////								if(map.containsKey("statusCode")){
////									// Trx reexecutee avec succes
////									if(map.get("statusCode") == "200"){
////										t.setReconcilier(Boolean.TRUE);
////										t.setDatereconcilier(new Date());
////										t.setVerifier(Boolean.TRUE);
////										t.setMessage("RE-EXECUTED TRANSACTION");
////										MobileMoneyViewHelper.appDAOLocal.update(t);
////										
////										trace.setStatus(TransactionStatus.SUCCESS);
////							        	trace.setCommentaire("RE-EXECUTED TRANSACTION");
////									}
////									// La reexecutee ne s'est pas bien passee
////									else {
////										//logger.info("UNABLE TO EXECUTE AGAIN");
////										logger.info("ERROR : "+map.get("error"));
////										trace.setStatus(TransactionStatus.FAILED);
////										trace.setCommentaire("REPEAT TRANSACTION FAILED : ERROR CODE "+map.get("statusCode"));
////									}
////								}
////								
////								trace.setOperation(t.getTypeOperation());
////					        	trace.setAccount(t.getAccount());
////					        	trace.setAmount(t.getAmount());
////					        	trace.setPhone(t.getPhoneNumber());
////					        	trace.setTrxId(t.getId());
////					        	//logger.info("SAVING TRACE");
////					        	// Save trace
////					        	MobileMoneyViewHelper.appDAOLocal.save(trace);
////					        	
////					        	map = null;
////							}
//							// Trx reussi cote MTN et echouee cote BANK (commente plus haut) -- MTN se chargera de regulariser de son cote
//							// Trx en attente cote MTN (PENDING)
//							else if(status.equals(MTNTransactionStatus.PENDING.toString()) && (TransactionStatus.SUCCESS.equals(t.getStatus()) || TransactionStatus.FAILED.equals(t.getStatus()))){
//								// On ne fait rien
//								logger.info("TRX NON RECONCILIEE **************************** ");
//								logger.info("MTN = PENDING (On ne fait rien - On attend que la transaction soit complete cote MTN)");
//								
////								trace.setStatus(TransactionStatus.FAILED);
////					        	trace.setCommentaire("PENDING TRANSACTION (MTN)");
////									
////								trace.setOperation(t.getTypeOperation());
////					        	trace.setAccount(t.getAccount());
////					        	trace.setAmount(t.getAmount());
////					        	trace.setPhone(t.getPhoneNumber());
////					        	trace.setTrxId(t.getId());
////					        	//logger.info("SAVING TRACE");
////					        	// Save trace
////					        	MobileMoneyViewHelper.appDAOLocal.save(trace);
//					        	// Save trace
//					        	MobileMoneyTools.saveTraceRobot(t, TransactionStatus.FAILED, "PENDING TRANSACTION (MTN)");
//							}
//							// Meme status cote MTN et cote BANK (SUCCESSFUL/SUCCESS) ou (FAILED/FAILED) 
//							else if((status.equals(MTNTransactionStatus.SUCCESSFUL.toString()) && TransactionStatus.SUCCESS.equals(t.getStatus())) || 
//									status.equals(MTNTransactionStatus.FAILED.toString()) && TransactionStatus.FAILED.equals(t.getStatus())){
//								logger.info("**************************** STATUS IDENTIQUES ****************************");
//								// Marque la transaction verifiee et la sauvegarder
//								t.setReconcilier(Boolean.TRUE);
//								t.setDatereconcilier(new Date());
//								t.setVerifier(Boolean.TRUE);
//								t.setMessage("OK");
//								
//								// Verifier si les tfj portal sont en cours et la trx executee avant le debut des tfj mais reconciliee apres
//								if(!t.getTfjoLance() && MobileMoneyViewHelper.appManager.isTFJOPortalEnCours()){
//									// Marquer la trx a retraiter
//									t.setARetraiter(Boolean.TRUE);
//									logger.info("TRX MARQUEE A RETRAITER : "+t.toString());
//								}
//
//								try{
//									MobileMoneyViewHelper.appDAOLocal.update(t);
//								}catch (OptimisticLockException e){
//									logger.info("4- Tentative de modification d'une version récente par une version plus ancienne!!!");
//									logger.error(e.getMessage());
//									continue; // Passage a l'iteration suivante
//								}
//								
//								//
////								trace.setOperation(t.getTypeOperation());
////					        	trace.setAccount(t.getAccount());
////					        	trace.setAmount(t.getAmount());
////					        	trace.setPhone(t.getPhoneNumber());
////					        	trace.setTrxId(t.getId());
////					        	trace.setStatus(TransactionStatus.SUCCESS);
////					        	trace.setCommentaire("OK");
////					        	// Save trace
////					        	MobileMoneyViewHelper.appDAOLocal.save(trace);
//					        	// Save trace
//					        	MobileMoneyTools.saveTraceRobot(t, TransactionStatus.SUCCESS, "OK");
//							}
//				        }
//						
//			        }
//					else {
//						// Echec verification
//						// Recuperer le message d'erreur
//						String error = StringUtils.substringBetween(trxStatus, "(", ")"); // "\"/>"
//						logger.info("TRX NON RECONCILIEE **************************** Erreur : "+error);
//				        	
////						trace.setOperation(t.getTypeOperation());
////			        	trace.setAccount(t.getAccount());
////			        	trace.setAmount(t.getAmount());
////			        	trace.setPhone(t.getPhoneNumber());
////			        	trace.setTrxId(t.getId());
////			        	trace.setStatus(TransactionStatus.FAILED);
////			        	//trace.setCommentaire("UNABLE TO VERIFY THE TRANSACTION STATUS");
////			        	trace.setCommentaire(error);
////			        	
////			        	MobileMoneyViewHelper.appDAOLocal.save(trace);
//			        	// Save trace
//						MobileMoneyTools.saveTraceRobot(t, TransactionStatus.FAILED, error);
//			        	
//			        	error = null;
//					}
					
/* FIN ANCIENNE IMPLEMENTATION VERIF STATUT */					
					
				}catch(Exception e){
					// TODO: handle exception
					e.printStackTrace();
				}

			}

			list = null;
			params = null;
//			trace = null;
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	
	/**
	 * 
	 */
	public static void reProcess(List<Transaction> list){
		String status = "";
		TraceRobot trace;
		
		try{
			Parameters params = MobileMoneyViewHelper.appManager.findParameters();
			
			if(list.equals(null) || list.isEmpty()){
				logger.info("********************************* AUCUNE TRX *********************************");
				return;
			}
			logger.info("NBRE TRX : "+list.size());
			for(Transaction t :list){
				//logger.info("********************************* "+t.getMtnTrxId()+" TRX *********************************");
				try{
					trace = new TraceRobot();
					trace.setDatetimeTrace(new Date());

			        String trxStatus = "";
					
			        // Mode test
					if(params.getEtatServiceSDP().equals(StatutService.TEST)){
						// Generer des reponses aleatoires
						trxStatus = MobileMoneyTools.generateTransactionStatusResponse(t.getMtnTrxId(), t.getPhoneNumber(), t.getStatus());
					}
					else{
						// Recuperation du status de la transaction
						MomoKYCServiceProxy proxy = new MomoKYCServiceProxy();
				        proxy.setEndpoint(params.getUrlKYCApi());

						// gettransactionstatus
				        trxStatus = proxy.getTransactionStatus(t.getMtnTrxId(), t.getPhoneNumber(), null);
					}
					
					//logger.info("RESPONSE = "+trxStatus);
					// Si on obtient une erreur
					if(trxStatus.contains("errorResponse") || trxStatus.contains("errorcode")){
						// Recuperer le message d'erreur
						String error = StringUtils.substringBetween(trxStatus, "errorcode=\"", "\"");
						if(trxStatus.contains("<arguments") && trxStatus.contains("name=")){
							// Recuperer le message d'erreur
							String name = StringUtils.substringBetween(trxStatus, "name=\"", "\"");
				        	error = error +" ("+name+" : ";
				        }
			        	if(trxStatus.contains("<arguments") || trxStatus.contains("value=")){
							// Recuperer le message d'erreur
							String value = StringUtils.substringBetween(trxStatus, "value=\"", "\"");
							error = error +value+")";
				        }
			        	
			        	if(error.contains("TRANSACTION_NOT_FOUND") || error.contains("ACCOUNTHOLDER_NOT_FOUND")){
			        		logger.info("Erreur : "+error);
			        		logger.info("**************************** MTN ---> TRANSACTION_NOT_FOUND / ACCOUNTHOLDER_NOT_FOUND ****************************");
			        		// MAJ de transaction avec le message obtenu
			        		// Annulation de la Transaction
							Map<String, String> map = MobileMoneyViewHelper.appManager.processReversalTransactionECW(t.getId().toString());
							if(map.containsKey("statusCode")){
								// L'annulation s'est bien passee
								if(map.get("statusCode") == "200"){
									// Recuperation de la derniere version de la transaction annulee
									t = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Transaction.class, t.getId(), null);
									t.setStatus(TransactionStatus.CANCEL);
									t.setReconcilier(Boolean.TRUE);
									t.setDatereconcilier(new Date());
									t.setVerifier(Boolean.TRUE);
									t.setMessage(error);

									try{
										MobileMoneyViewHelper.appDAOLocal.update(t);
									}catch (OptimisticLockException e){
										logger.info("1- Tentative de modification d'une version récente par une version plus ancienne!!!");
										logger.error(e.getMessage());
										continue; // Passage a l'iteration suivante
									}
									
									trace.setStatus(TransactionStatus.SUCCESS);
						        	trace.setCommentaire("TRANSACTION CANCELLED because "+error);
								}
								// Transaction echouee (sans evenements)
								else if(map.get("statusCode") == "500"){
									t.setVerifier(Boolean.TRUE);
									t.setReconcilier(Boolean.TRUE);
									t.setDatereconcilier(new Date());
									t.setMessage(error);
									
									try{
										MobileMoneyViewHelper.appDAOLocal.update(t);
									}catch (OptimisticLockException e){
										logger.info("2- Tentative de modification d'une version récente par une version plus ancienne!!!");
										logger.error(e.getMessage());
										continue; // Passage a l'iteration suivante
									}
									
									trace.setStatus(TransactionStatus.SUCCESS);
						        	trace.setCommentaire(error);
								}
								// L'annulation ne s'est pas bien passee
								else {
									logger.info("TRX NON RECONCILIEE **************************** Erreur : "+error);
									trace.setStatus(TransactionStatus.FAILED);
									trace.setCommentaire("ECHEC REVERSAL : ERROR CODE "+map.get("statusCode"));
								}
							}
							map = null;
			        	}
			        	else{
			        		logger.info("TRX NON RECONCILIEE **************************** Erreur : "+error);
				        	trace.setStatus(TransactionStatus.FAILED);
				        	trace.setCommentaire(error);
			        	}
			        	// Traitement a effectuer dans ce cas
			        	trace.setOperation(t.getTypeOperation());
			        	trace.setAccount(t.getAccount());
			        	trace.setAmount(t.getAmount());
			        	trace.setPhone(t.getPhoneNumber());
			        	trace.setTrxId(t.getId());
			        	
			        	// Save trace
			        	MobileMoneyViewHelper.appDAOLocal.save(trace);
			        	//break;
			        }
					
					// Si on obtient la reponse attendue
					else if(trxStatus.contains("gettransactionstatusresponse")){
						// Recuperer les parametres de la reponse
						if(trxStatus.contains("status")){
							status = StringUtils.substringBetween(trxStatus, "<status>", "</status>");
//							logger.info(" : "+status);
							
							// Trx echouee cote MTN (FAILED) et reussite cote BANK (SUCCESS)
							if(status.equals(MTNTransactionStatus.FAILED.toString()) && TransactionStatus.SUCCESS.equals(t.getStatus())){
								logger.info("**************************** STATUS FAILED (MTN) / SUCCESS (BANK) ****************************");
								// Annulation de la Transaction
								Map<String, String> map = MobileMoneyViewHelper.appManager.processReversalTransactionECW(t.getId().toString());
								if(map.containsKey("statusCode")){
									// L'annulation s'est bien passee
									if(map.get("statusCode") == "200"){
										// Recuperation de la derniere version de la transaction annulee
										t = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Transaction.class, t.getId(), null);
										t.setStatus(TransactionStatus.CANCEL);
										t.setReconcilier(Boolean.TRUE);
										t.setDatereconcilier(new Date());
										t.setVerifier(Boolean.TRUE);
										t.setMessage("CANCELED TRANSACTION");
										
										try{
											MobileMoneyViewHelper.appDAOLocal.update(t);
										}catch (OptimisticLockException e){
											logger.info("3- Tentative de modification d'une version récente par une version plus ancienne!!!");
											logger.error(e.getMessage());
											continue; // Passage a l'iteration suivante
										}
										
										trace.setStatus(TransactionStatus.SUCCESS);
							        	trace.setCommentaire("CANCELED TRANSACTION");
									}
									// L'annulation ne s'est pas bien passee
									else {
										trace.setStatus(TransactionStatus.FAILED);
										trace.setCommentaire("ECHEC REVERSAL : ERROR CODE "+map.get("statusCode"));
									}
								}
								
								trace.setOperation(t.getTypeOperation());
					        	trace.setAccount(t.getAccount());
					        	trace.setAmount(t.getAmount());
					        	trace.setPhone(t.getPhoneNumber());
					        	trace.setTrxId(t.getId());
					        	
					        	// Save trace
					        	MobileMoneyViewHelper.appDAOLocal.save(trace);
							}
							// Trx en attente cote MTN (PENDING)
							if(status.equals(MTNTransactionStatus.PENDING.toString()) && (TransactionStatus.SUCCESS.equals(t.getStatus()) || TransactionStatus.FAILED.equals(t.getStatus()))){
								// On ne fait rien
								logger.info("TRX NON RECONCILIEE **************************** ");
								logger.info("MTN = PENDING (On ne fait rien - On attend que la transaction soit complete cote MTN)");
								
								//t.setStatus(TransactionStatus.CANCEL);
								t.setReconcilier(Boolean.FALSE);
								t.setDatereconcilier(null);
								t.setVerifier(Boolean.TRUE);
								t.setMessage("PENDING TRANSACTION");
								
								trace.setStatus(TransactionStatus.FAILED);
					        	trace.setCommentaire("PENDING TRANSACTION (MTN)");
									
								trace.setOperation(t.getTypeOperation());
					        	trace.setAccount(t.getAccount());
					        	trace.setAmount(t.getAmount());
					        	trace.setPhone(t.getPhoneNumber());
					        	trace.setTrxId(t.getId());
					        	//logger.info("SAVING TRACE");
					        	// Save trace
					        	MobileMoneyViewHelper.appDAOLocal.save(trace);
							}
							// Meme status cote MTN et cote BANK (SUCCESSFUL/SUCCESS) ou (FAILED/FAILED) 
						}
			        }
					else{
						// Echec verification
						// Recuperer le message d'erreur
						String error = StringUtils.substringBetween(trxStatus, "(", ")"); // "\"/>"
						logger.info("TRX NON RECONCILIEE **************************** Erreur : "+error);
				        	
						trace.setOperation(t.getTypeOperation());
			        	trace.setAccount(t.getAccount());
			        	trace.setAmount(t.getAmount());
			        	trace.setPhone(t.getPhoneNumber());
			        	trace.setTrxId(t.getId());
			        	trace.setStatus(TransactionStatus.FAILED);
			        	//trace.setCommentaire("UNABLE TO VERIFY THE TRANSACTION STATUS");
			        	trace.setCommentaire(error);
			        	// Save trace
			        	MobileMoneyViewHelper.appDAOLocal.save(trace);
			        	error = null;
					}
					
				}catch(Exception e){
					// TODO: handle exception
					e.printStackTrace();
				}

			}

			list = null;
			params = null;
			trace = null;
		}catch(Exception e){
			e.printStackTrace();
		}

	}


}
