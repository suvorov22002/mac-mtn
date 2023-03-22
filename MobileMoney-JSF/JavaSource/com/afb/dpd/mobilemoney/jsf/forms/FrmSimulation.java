/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.forms;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.axis.utils.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.RequestMessage;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.enums.ExceptionCode;
import com.afb.dpd.mobilemoney.jpa.enums.StatutContrat;
import com.afb.dpd.mobilemoney.jpa.enums.TransactionStatus;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.jcraft.jsch.Logger;
import com.yashiro.persistence.utils.dao.tools.OrderContainer;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;
import com.yashiro.persistence.utils.dao.tools.encrypter.Encrypter;

/**
 * Modele du formulaire de simulation d'envoi de messages de transaction PULL/PUSH
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class FrmSimulation extends AbstractPortalForm {

	/**
	 * Message recu du client
	 */
	private RequestMessage message = new RequestMessage(TypeOperation.PULL, "", "237", 0d, "");
	
	private Map<String, String> map = new HashMap<String, String>();
	
	private TypeOperation operationBalance = TypeOperation.BALANCE ;
	private TypeOperation operationMinistatement = TypeOperation.MINISTATEMENT ;
	
	private Parameters params;
	/**
	 * Items de types d'operations
	 */
	private List<SelectItem> opItems = new ArrayList<SelectItem>();
	private List<SelectItem> opItemsECW = new ArrayList<SelectItem>();
	private Boolean digitalisation = Boolean.TRUE;
		
	Transaction trx = null;
	
	private String nbreAuto;
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
	
	/**
	 * Default Constructor
	 */
	public FrmSimulation() {}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		// TODO Auto-generated method stub
		super.initForm();
		opItemsECW = new ArrayList<SelectItem>();
		// Chargement des items de types d'operations
		opItemsECW.add( new SelectItem(TypeOperation.PULL, TypeOperation.PULL.getValue()) );
		opItemsECW.add( new SelectItem(TypeOperation.PUSH, TypeOperation.PUSH.getValue()) );
		opItemsECW.add( new SelectItem(TypeOperation.BALANCE, TypeOperation.BALANCE.getValue()) );
		opItemsECW.add( new SelectItem(TypeOperation.MINISTATEMENT, TypeOperation.MINISTATEMENT.getValue()) );
		//message = new RequestMessage(TypeOperation.PULL, "", "237", 0d, "");
	
		opItems = new ArrayList<SelectItem>();
		opItems.add( new SelectItem(TypeOperation.PULL, TypeOperation.PULL.getValue()) );
		opItems.add( new SelectItem(TypeOperation.PUSH, TypeOperation.PUSH.getValue()) );
		opItems.add( new SelectItem(TypeOperation.BALANCE, TypeOperation.BALANCE.getValue()) );
		
		message = new RequestMessage(TypeOperation.PULL, "", "237", 0d, "");
		params = MobileMoneyViewHelper.appManager.findParameters();
		
	}
	
	/**
	 * @return the params
	 */
	public Parameters getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Parameters params) {
		this.params = params;
	}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Simulateur de transactions PULL/PUSH";
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose();
		digitalisation = Boolean.TRUE;
		message = null;
	}
	
	/**
	 * Methode de test des preconditions d'envoi d'un message
	 * @return true si test OK, false sinon
	 */
	private boolean preConditionOK() throws Exception {
		
		trx = null;
		
		// Test du montant
		if((!message.getOperation().equals(operationBalance) && !message.getOperation().equals(operationMinistatement)) && (message.getAmount() <= 0 || ((message.getAmount() - message.getAmount().intValue())!=0))) {
			
			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Montant incorrect", InformationDialog.DIALOG_WARNING);
			
			return false;
		}
		
		Subscriber subs = MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(message.getPhoneNumber());
		
		// Test du numero de telephone
		if( subs == null ) {
			String msg = "Le propriétaire du n° "+message.getPhoneNumber()+" essaie de faire une opération. Cet abonnenement est inexistant ou client suspendu!. ";
			String subject = "Alerte abonnement suspendu";
			String title = "SUSPENS";
			 MobileMoneyViewHelper.appManager.sendSimpleMail(msg, subject, title);
			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Numero de telephone inexistant ou client suspendu!", InformationDialog.DIALOG_WARNING);
			
			return false;
		}

		// Test etat souscription
		if( subs.getStatus().equals(StatutContrat.SUSPENDU) ) {
			String msg = "Le propriétaire du n° "+message.getPhoneNumber()+" essaie de faire une opération. Cet abonnenement est suspendu. ";
			String subject = "Alerte abonnement suspendu";
			String title = "SUSPENS";
			 MobileMoneyViewHelper.appManager.sendSimpleMail(msg, subject, title);
			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("La souscription de ce client a été suspendue. Impossible d'effectuer des transactions Pull/Push from account", InformationDialog.DIALOG_WARNING);
			
			return false;
		}

		// Test etat souscription
		if( subs.getStatus().equals(StatutContrat.WAITING) ) {
			
			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("La souscription de ce client est en attente de validation. Impossible d'effectuer des transactions Pull/Push from account", InformationDialog.DIALOG_WARNING);
			
			return false;
		}
		
		// Lecture du compte
		message.setAccount( subs.getAccounts().get(0) );

		// Test de l'existence du compte 
		if( MobileMoneyViewHelper.appManager.isCompteFerme(message.getAccount()) ) {
			
			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Numéro de Compte inexistant ou en instance de fermeture. Impossible d'effectuer des transactions Pull/Push from account", InformationDialog.DIALOG_WARNING);
			
			return false;
		}
		
		/*
		// Si c'est un retrait (verification du solde)	
		if(message.getOperation().equals(TypeOperation.PULL)) {
			
			// Test du solde du compte 
			if( MobileMoneyViewHelper.appManager.isSoldeSuffisant(message.getAccount(), message.getAmount()) ) {
				
				// Message d'avertissement
				PortalInformationHelper.showInformationDialog("Solde du compte insuffisant. Impossible de faire le PULL", InformationDialog.DIALOG_WARNING);
				
				return false;
			}
		}
		*/
		
		// test du PIN
//		if( !subs.getBankPIN().equals( Encrypter.getInstance().encryptText( message.getBankPIN() ) ) ) {
//			
//			// Message d'avertissement
//			PortalInformationHelper.showInformationDialog("Code PIN incorrect", InformationDialog.DIALOG_WARNING);
//			
//			return false;
//		}
//		
		// Initialisation de la transaction
		trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), "");
		
		// Tout est OK
		return true;
	}
	
	private boolean preConditionsOK() throws Exception {
		
		trx = null;
		
		Subscriber subs = MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(message.getPhoneNumber());
		
		// Test du numero de telephone
		if( subs == null ) {
			return false;
		}

		// Test etat souscription
		if( subs.getStatus().equals(StatutContrat.SUSPENDU) ) {
			return false;
		}

		// Test etat souscription
		if( subs.getStatus().equals(StatutContrat.WAITING) ) {
			return false;
		}
		
		// Lecture du compte
		message.setAccount( subs.getAccounts().get(0) );

		// Test de l'existence du compte 
		if( MobileMoneyViewHelper.appManager.isCompteFerme(message.getAccount()) ) {
			return false;
		}
		// Initialisation de la transaction
		trx = new Transaction(message.getOperation(), subs, message.getAmount(), message.getAccount(), message.getPhoneNumber(), "");
		return true;
	}
	
	
	/**
	 * Methode d'envoi du message au serveur pour traitement
	 */
	public void sendMessage() {
		
		try {
			
			// Test des preconditions
			if(!preConditionOK()) return;
			
			// Postage de l'evenement dans Delta
			//MobileMoneyViewHelper.appManager.posterEvenementDansCoreBanking(trx);
			
			if(message.getOperation().equals(TypeOperation.PULL)){
				map = MobileMoneyViewHelper.appManager.processPullTransaction(message.getPhoneNumber(), message.getBankPIN(), message.getAmount(), ""	, "");
				if(map.get("statusCode").equals("200")) 
					PortalInformationHelper.showInformationDialog("Transaction effectuee avec succes!\nID transaction : "+map.get("remoteID")+", \n Nouveau solde : "+map.get("amount"), InformationDialog.DIALOG_SUCCESS);
				else
					PortalInformationHelper.showInformationDialog("Error: " + map.get("statusCode") + " "+map.get("error"), InformationDialog.DIALOG_ERROR);
			}
			else if(message.getOperation().equals(TypeOperation.PUSH)){
				map = MobileMoneyViewHelper.appManager.processPushTransaction(message.getPhoneNumber(), message.getBankPIN(), message.getAmount(), ""	, "");
				if(map.get("statusCode").equals("200")) 
					PortalInformationHelper.showInformationDialog("Transaction effectuee avec succes!\nID transaction : "+map.get("remoteID")+", \n Nouveau solde : "+map.get("amount"), InformationDialog.DIALOG_SUCCESS);
				else
					PortalInformationHelper.showInformationDialog("Error: " + map.get("statusCode") + " "+map.get("error"), InformationDialog.DIALOG_ERROR);
			}
			else if(message.getOperation().equals(TypeOperation.BALANCE)){
				map = MobileMoneyViewHelper.appManager.processBalanceTransaction(message.getPhoneNumber(), message.getBankPIN(), "", "");
				// Message de succes
				if(map.get("statusCode").equals("200")) 
					PortalInformationHelper.showInformationDialog("Votre solde est de "+map.get("amount")+" XAF", InformationDialog.DIALOG_SUCCESS);
				else
					PortalInformationHelper.showInformationDialog("Error: " + map.get("statusCode") + " "+map.get("error"), InformationDialog.DIALOG_ERROR);
			}
			map.clear();
			//sendSMSConfirmation();
			
		} catch(Exception e) {
			
			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
		}
		
	}
	
	
	/**
	 * Methode d'envoi du message au serveur pour traitement
	 */
	public void sendMessageECW() {
		List<Transaction> listTrx = new ArrayList<Transaction>();
		try {
			
			 if (StringUtils.isEmpty(message.getPhoneNumber())) {

				 String[] aleaNbre = {"237677835592","237650683514","237671812711","237681578943","237679455423","237677579900","237673658994","237670851508","237676063737","237674947190","23768237114","237677766785","237670317273"
						 ,"237678273172","237650209558" ,"237674200763","237676002632","237675289301","237650515730","237676017949","237651494407","237670838919","237674019194","237677613322","237677343501"
						 ,"237652126119","237677428733","237652497784","237674909566","237675257406","237671342727","237670608811","237675723371","237677543349","237677743476","237675637990","237677232564"
						 ,"237679673234","237654334835","237654183320","237675742397","237653095012","237677946887","237672749445" ,"237677733826" ,"237672548493","237672796831","237674806249","237654408506"
						 ,"237678702230","237673760314","237675275670","237677018149","237670241714","237650188376","237677673775","237677534078","237679913640","237674156273","237677484235","237679155906",
						 "237675224764","237677229539","237677831527","237672530790","237670720327","237676448181","237678344209","237674773089","237675851322","237653110481","237674962650",
						 "237677931002","237678759950","237674317044","237677986810","237678048930","237674136192","237676400017","237651370283","237672212530","237673515287","237676058243","237670274733"
						 ,"237653722120","237674430769","237683781042","237675871058","237671887806","237680846304","237676761831","237670253813","237675952780","237673356854","237650518578","237675914689","237676309572"
						 ,"237675965345","237678744143"};
				 int compteur = 0;
				 for (String str : aleaNbre) {
					 //compteur++;
					 int max = 1000;
					 int min = 200;
					 int b = (int)(Math.random()*(max-min+1)+min);
					 message.setAmount((double) b);
					 message.setPhoneNumber(str);
					 //System.out.println("[FRMSIMULATION] "+message.getPhoneNumber());
					 if(!preConditionsOK()) continue;
					 
					 if(message.getOperation().equals(TypeOperation.PULL)){
						 map = MobileMoneyViewHelper.appManager.pullTransactionECW(null, message.getPhoneNumber(), message.getAmount());
						 // Message de succes
						 if(!map.get("statusCode").equals("200")) 
							 continue;
					 }
					 else if(message.getOperation().equals(TypeOperation.PUSH)){
						 map = MobileMoneyViewHelper.appManager.pushTransactionECW(null, message.getPhoneNumber(), message.getAmount());
						 if(!map.get("statusCode").equals("200")) 
							 continue;
					 }
					 
					 Thread.sleep(1000);
					// if (compteur == nbre) break;
					 compteur++;
				 }
				 PortalInformationHelper.showInformationDialog(compteur+" opérations effectuées. ", InformationDialog.DIALOG_SUCCESS);
			 }
			 else {
				// Test des preconditions
					if(!preConditionOK()) return;
					
					// Postage de l'evenement dans Delta
					//MobileMoneyViewHelper.appManager.posterEvenementDansCoreBanking(trx);
					
//					if(message.getOperation().equals(TypeOperation.PULL)) 
//						map = MobileMoneyViewHelper.appManager.processPullTransaction(message.getPhoneNumber(), message.getBankPIN(), message.getAmount(), ""	, "");
//					else
//						map = MobileMoneyViewHelper.appManager.processPushTransaction(message.getPhoneNumber(), message.getBankPIN(), message.getAmount(), ""	, "");
//					RestrictionsContainer rc = RestrictionsContainer.getInstance();
//					OrderContainer orders = OrderContainer.getInstance();
					
					if(message.getOperation().equals(TypeOperation.PULL)){
						map = MobileMoneyViewHelper.appManager.pullTransactionECW(null, message.getPhoneNumber(), message.getAmount());
						// Message de succes
						if(map.get("statusCode").equals("200")) 
							PortalInformationHelper.showInformationDialog("Transaction effectuee avec succes!\nID transaction : "+map.get("remoteID")+", \n Nouveau solde : "+map.get("amount"), InformationDialog.DIALOG_SUCCESS);
//						else if(map.get("statusCode").equals("501")) 
//							PortalInformationHelper.showInformationDialog(ExceptionCode.SubscriberSuspended.getValue(), InformationDialog.DIALOG_ERROR);
//						else if(map.get("statusCode").equals("503")) 
//							PortalInformationHelper.showInformationDialog(ExceptionCode.SubscriberInvalidAmount.getValue(), InformationDialog.DIALOG_ERROR);
//						else if(map.get("statusCode").equals("504")) 
//							PortalInformationHelper.showInformationDialog(ExceptionCode.BankInsufficientBalance.getValue(), InformationDialog.DIALOG_ERROR);
						else
							PortalInformationHelper.showInformationDialog("Error: " + map.get("statusCode") + " "+map.get("error"), InformationDialog.DIALOG_ERROR);
					}
					else if(message.getOperation().equals(TypeOperation.PUSH)){
						map = MobileMoneyViewHelper.appManager.pushTransactionECW(null, message.getPhoneNumber(), message.getAmount());
						// Message de succes
						if(map.get("statusCode").equals("200")) 
							PortalInformationHelper.showInformationDialog("Transaction effectuee avec succes!\nID transaction : "+map.get("remoteID")+", \n Nouveau solde : "+map.get("amount"), InformationDialog.DIALOG_SUCCESS);
//						else if(map.get("statusCode").equals("501")) 
//							PortalInformationHelper.showInformationDialog(ExceptionCode.SubscriberSuspended.getValue(), InformationDialog.DIALOG_ERROR);
//						else if(map.get("statusCode").equals("503")) 
//							PortalInformationHelper.showInformationDialog(ExceptionCode.SubscriberInvalidAmount.getValue(), InformationDialog.DIALOG_ERROR);
//						else if(map.get("statusCode").equals("504")) 
//							PortalInformationHelper.showInformationDialog(ExceptionCode.BankInsufficientBalance.getValue(), InformationDialog.DIALOG_ERROR);
						else
							PortalInformationHelper.showInformationDialog("Error: " + map.get("statusCode") + " "+map.get("error"), InformationDialog.DIALOG_ERROR);
					}
					else if(message.getOperation().equals(TypeOperation.BALANCE)){
						map = MobileMoneyViewHelper.appManager.getBalanceECW(message.getPhoneNumber());
						// Message de succes
						if(map.get("statusCode").equals("200")) 
							PortalInformationHelper.showInformationDialog("Votre solde est de "+map.get("amount")+" XAF", InformationDialog.DIALOG_SUCCESS);
						else
							PortalInformationHelper.showInformationDialog("Error: " + map.get("statusCode") + " "+map.get("error"), InformationDialog.DIALOG_ERROR);
					}
					else if(message.getOperation().equals(TypeOperation.MINISTATEMENT)){
						listTrx = MobileMoneyViewHelper.appManager.getMinistatementECW(message.getPhoneNumber());
						
						if(listTrx.isEmpty()){
							PortalInformationHelper.showInformationDialog("Aucune transaction trouvée!", InformationDialog.DIALOG_INFORMATION);
						}
						else{
							String last = "";
							for(Transaction tx : listTrx){
					        	last = last +"\n"+new SimpleDateFormat("dd-MM-yy").format(tx.getDate().getTime())+" "+tx.getTypeOperation()+" "+((tx.getTypeOperation().equals(TypeOperation.PULL)) ? "-" : "")+ tx.getAmount().longValue()+"XAF";
					        }
							PortalInformationHelper.showInformationDialog("Dernières opérations "+last, InformationDialog.DIALOG_SUCCESS);
						}
					}
									
					//sendSMSConfirmation();
					if(message.getOperation().equals(TypeOperation.PULL) || message.getOperation().equals(TypeOperation.PUSH)){
						String sql = "From Transaction t where (t.typeOperation = '"+TypeOperation.PUSH+"' or t.typeOperation = '"+TypeOperation.PULL+"') and "
								+ "t.status = '"+TransactionStatus.SUCCESS+"' and t.phoneNumber = '"+message.getPhoneNumber()+"' order by t.date desc ";
						List<Transaction> ltrx = MobileMoneyViewHelper.appManager.getTransactionControl(sql);
						
						String h1,h2,d1,d2;
						Date parsedDate,_parsedDate;
						Timestamp timestamp,_timestamp;
						String msg = "";
						String subject = "";
						String title = "";
						
						if(ltrx.size() > 2) {
							h1 = ltrx.get(0).getHour();
							h2 = ltrx.get(2).getHour();
							d1 = ltrx.get(0).getFormattedDate();
							d2 = ltrx.get(2).getFormattedDate();
							
							parsedDate = dateFormat.parse(d1+" "+h1);
							_parsedDate = dateFormat.parse(d2+" "+h2);
							timestamp = new java.sql.Timestamp(parsedDate.getTime());
							_timestamp = new java.sql.Timestamp(_parsedDate.getTime());
							
						//	System.out.println("Timestamp "+ (timestamp.getTime()-_timestamp.getTime()));
							if((timestamp.getTime()-_timestamp.getTime()) < 120000) { //superieur à 3 minutes
								msg = "L'abonné, propriétaire du n° "+message.getPhoneNumber()+" a effectué trois opérations en moins de deux minutes.";
								subject = "Alerte opérations suspectes";
								title = "OPERATIONS";
								MobileMoneyViewHelper.appManager.sendSimpleMail(msg, subject, title);
							}
						}
						
					}
			 }
			
	
		} catch(Exception e) {
			
			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
		}
		
	}
	
	/*
	private void sendSMSConfirmation() {
		
		MobileMoneyViewHelper.appManager.sendSMS("Cher Abonne. Votre cpte N° "+ message.getAccount().split("-")[1] +" a ete "+ (message.getOperation().equals(TypeOperation.PULL) ? "Debite" : "Credite") +" du montant "+ message.getAmount().toString() +" lors d'une transaction Pull/Push from account", message.getPhoneNumber());
		
	}
	*/
	
	/**
	 * @return the message
	 */
	public RequestMessage getMessage() {
		return message;
	}

	/**
	 * @return the opItems
	 */
	public List<SelectItem> getOpItems() {
		return opItems;
	}
	
	/**
	 * @return the opItemsECW
	 */
	public List<SelectItem> getOpItemsECW() {
		return opItemsECW;
	}

	/**
	 * @return the operationBalance
	 */
	public TypeOperation getOperationBalance() {
		return operationBalance;
	}

	/**
	 * @param operationBalance the operationBalance to set
	 */
	public void setOperationBalance(TypeOperation operationBalance) {
		this.operationBalance = operationBalance;
	}

	/**
	 * @return the operationMinistatement
	 */
	public TypeOperation getOperationMinistatement() {
		return operationMinistatement;
	}

	/**
	 * @param operationMinistatement the operationMinistatement to set
	 */
	public void setOperationMinistatement(TypeOperation operationMinistatement) {
		this.operationMinistatement = operationMinistatement;
	}
 
	/**
	 * @return the digitalisation
	 */
	public Boolean getDigitalisation() {
		return digitalisation;
	}

	/**
	 * @param digitalisation the digitalisation to set
	 */
	public void setDigitalisation(Boolean digitalisation) {
		this.digitalisation = digitalisation;
	}

	/**
	 * @return the nbreAuto
	 */
	public String getNbreAuto() {
		return nbreAuto;
	}

	/**
	 * @param nbreAuto the nbreAuto to set
	 */
	public void setNbreAuto(String nbreAuto) {
		this.nbreAuto = nbreAuto;
	}
	
	
			
}
