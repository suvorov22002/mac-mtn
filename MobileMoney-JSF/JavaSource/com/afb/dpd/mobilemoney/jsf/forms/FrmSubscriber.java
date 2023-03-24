/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.forms;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.enums.StatutContrat;
import com.afb.dpd.mobilemoney.jpa.enums.StatutService;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jsf.dto.Identifications;
import com.afb.dpd.mobilemoney.jsf.dto.Link;
import com.afb.dpd.mobilemoney.jsf.dto.PersonalInformations;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.models.ReportViewerDialog;
import com.afb.dpd.mobilemoney.jsf.servlet.WebResourceManager;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyTools;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.afb.dpd.mobilemoney.jsf.tools.SelectData;
import com.afb.dpi.momo.services.MomoKYCServiceProxy;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

import afb.dsi.dpd.portal.jpa.entities.User;

/**
 * Formulaire de gestion des Parametres generaux
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class FrmSubscriber extends AbstractPortalForm {
	
	private static Log logger = LogFactory.getLog(FrmSubscriber.class);

	private Subscriber subscriber;
	
	private Parameters param;
	
	private String txtCustomerId;
	
	private String msisdn = "";

	private String key = "";
		
	private String firstname = "";
	
	private String surname = "";
	
	private String dob = "";
	
	private String cin = "";
	
	private String gender = "";

	private String language = "";
		
	private String country = "";
	
	private String region = "";
	
	private String city = "";
	
	private String profession = "";
	
	String urlSignature = null;
	
	private List<SelectData> phones = new ArrayList<SelectData>();
	private List<SelectData> accounts = new ArrayList<SelectData>();
	
	private List<SelectItem> languageItems = new ArrayList<SelectItem>();
	
	private String choixLang;
	
	private Boolean valide = Boolean.FALSE;
	
	private Boolean verifyOtp = Boolean.FALSE;
	
	private int maxOtp = 0;
	
	private String otp;
	
	/**
	 * Default Constructor
	 */
	public FrmSubscriber() {}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		
		// Appel Parent
		super.initForm();
		
		// Recuperation des parametres
		param = MobileMoneyViewHelper.appManager.findParameters();
		
		// Initialisation visuelle du formulaire
		clearForm();

		languageItems = new ArrayList<SelectItem>();
		languageItems.add( new SelectItem("FR", "FRANCAIS") );
		languageItems.add( new SelectItem("EN", "ENGLISH") ); 
		choixLang = "FR";
		valide = Boolean.FALSE;
		verifyOtp = Boolean.FALSE;
		otp = "";
		maxOtp = 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Souscription Pull/Push from Account";
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose();
		
		disposeResources();
	}
	
	public void disposeResources() {
		
		param = null; accounts.clear(); phones.clear(); subscriber = null; txtCustomerId = null; urlSignature = null;
		surname = ""; firstname = ""; dob = ""; cin = ""; gender = ""; language = ""; country = ""; region = ""; city = ""; profession = "";
	}
	
	
	/**
	 * Verifie si les pre-conditions sont respectees avant la validation de la souscription
	 * @return true si pre-conditions OK, false sinon
	 */
	private boolean preconditionsOK() {
		param = MobileMoneyViewHelper.appManager.findParameters();
		// Si aucun compte n'a ete selectionne
		if( subscriber.getCustomerId() == null)  {

			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Veuillez recherche un client par son code SVP!", InformationDialog.DIALOG_WARNING);
			
			// Annulation
			return false;
		}

		// Si le client a deja souscrit
//		if( MobileMoneyViewHelper.appManager.subscriptionAlreadyExist( subscriber.getCustomerId() ) )  {
//
//			// Message d'avertissement
//			PortalInformationHelper.showInformationDialog("Ce client a deja souscrit au service Pull/Push From Account!", InformationDialog.DIALOG_WARNING);
//			
//			// Annulation
//			return false;
//		}
		
		// Si aucun compte n'a ete selectionne
		if( subscriber == null)  {

			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Aucun client sélectionné. Impossible de valider la souscription", InformationDialog.DIALOG_WARNING);
			
			// Annulation
			return false;
		}
		
//		if(!validatePhoneInCBS()){
//			// Message d'information
//			PortalInformationHelper.showInformationDialog("Le numéro de téléphone "+ msisdn +" n'existe pas parmi les informations du client. Impossible de valider la souscription", InformationDialog.DIALOG_WARNING);
//			// Annulation
//			return false;
//		}
		
		// Mode test
//		if(!param.getEtatServiceSDP().equals(StatutService.TEST)){
//			logger.error("OPERATION EXECUTEE EN MODE MAINTENANCE PAR "+MobileMoneyViewHelper.getSessionUser());
//			// Si les informations cote MTN ne sont pas disponibles
//			if(StringUtils.isBlank(firstname) && StringUtils.isBlank(surname) 
//					&& StringUtils.isBlank(cin) && StringUtils.isBlank(dob)){
//				
//				// Message d'avertissement
//				PortalInformationHelper.showInformationDialog("Aucune information client cote MTN. Impossible de valider la souscription", InformationDialog.DIALOG_WARNING);
//				
//				// Annulation
//				return false;
//			}
//		}
		
		// Si aucun compte n'a ete selectionne
		if( subscriber.getAccounts().isEmpty())  {

			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Aucun compte n'a été sélectionné. Impossible de valider la souscription", InformationDialog.DIALOG_WARNING);
			
			// Annulation
			return false;
		}
		
		// Si le nbre de cpte selectionnes depasse le maximum parametre
		if( subscriber.getAccounts().size() > param.getMaxAccounts() )  {

			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Le nombre maximum de comptes à fournir est de "+ param.getMaxAccounts() +". Impossible de poursuivre l'opération.", InformationDialog.DIALOG_WARNING);
			
			// Annulation
			return false;
		}
		
		// Si aucun numero de telephone n'a ete fourni
		if( subscriber.getPhoneNumbers().isEmpty() )  {

			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Aucun numero de téléphone sélectionné. Veuillez sélectionné le numéro de téléphone du client pour poursuivre la souscription.", InformationDialog.DIALOG_WARNING);
			
			// Annulation
			return false;
		}

		// Si aucun numero de telephone n'a ete fourni
//		for(String s : subscriber.getPhoneNumbers()) if( !s.startsWith(MoMoHelper.PHONES_MASK) || s.length() != MoMoHelper.PHONES_LENGTH )  {
//
//			// Message d'avertissement
//			PortalInformationHelper.showInformationDialog("Numéro de Téléphone Incorrect!.", InformationDialog.DIALOG_WARNING);
//			
//			// Annulation
//			return false;
//		}
		
		// Si le nbre de numeros de telephones fourni depasse le maximum parametre
		if( subscriber.getPhoneNumbers().size() > param.getMaxPhoneNumbers() )  {

			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Le nombre maximum de numéros de téléphones autorisé est de "+ param.getMaxPhoneNumbers() +". Impossible de poursuivre l'opération", InformationDialog.DIALOG_WARNING);
			
			// Annulation
			return false;
		}
		
		// Si l'un des numeros de telephone fournit appatient deja a un client
		for(String s : subscriber.getPhoneNumbers()) {
			
			if(MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(s) != null) {

				// Message d'avertissement
				PortalInformationHelper.showInformationDialog("le numéro de téléphone "+ s +" appartient déjà à un autre client ou à une souscription en attente de validation.", InformationDialog.DIALOG_WARNING);
				
				// Annulation
				return false;
				
			}
			
		}
		
		// Si l'un des numeros de telephone fournit appartient deja a un client dont la souscription est en attente de validation
//		for(String s : subscriber.getPhoneNumbers()) {
//			
//			if(MobileMoneyViewHelper.appManager.verifySubscriberFromPhoneNumber(s) != null) {
//
//				// Message d'avertissement
//				PortalInformationHelper.showInformationDialog("le numéro de téléphone "+ s +" appartient déjà à une souscription en attente de validation.", InformationDialog.DIALOG_WARNING);
//				
//				// Annulation
//				return false;
//					
//			}
//			
//		}
				
		return true;
	}
	
	 
	/**
	 * Recuperation des num de tel et des comptes saisis sur le form de souscription
	 */
	private void readSubscriverCBSInfos() {
		//System.out.println("Reading subscriber infos");
		// Lecture du code client
		subscriber.setCustomerId( txtCustomerId );
		
		// added
		subscriber.getPhoneNumbers().clear(); subscriber.getAccounts().clear();
		
		// Si la liste des phones n'est pas vide
		if(phones != null && !phones.isEmpty()) {
			
			// Recuperation des n° de tel saisis
			for(SelectData sd : phones) {
				if(sd.getChecked().booleanValue() && sd.getValue() != null && !sd.getValue().trim().isEmpty() && sd.getValue().trim().length()==12) 
					subscriber.getPhoneNumbers().add( sd.getValue().replaceAll(" ", "") );
			}
			// added if only one account
			if(subscriber.getPhoneNumbers().isEmpty() && phones.size() == 1){
				for(SelectData sd : phones){
		 			sd.setChecked(Boolean.TRUE);
					subscriber.getPhoneNumbers().add(sd.getValue());
				}
			}
		}
		
		// S'il existe au moins un compte
		if(accounts != null && !accounts.isEmpty()) {
			
			// Recuperation des n° de cpte selectionnes
			for(SelectData sd : accounts) if(sd.getChecked().booleanValue() ) subscriber.getAccounts().add( sd.getValue());
			// added if only one account
			if(subscriber.getAccounts().isEmpty() && accounts.size() == 1){
				for(SelectData sd : accounts){
					sd.setChecked(Boolean.TRUE);
					subscriber.getAccounts().add(sd.getValue());
				}
			}
			
		}
	}
	
	
	/**
	 * Recuperation des num de tel et des comptes saisis sur le form de souscription
	 */
	private void readSubscriverInfos() {
		//System.out.println("Reading subscriber infos");
		// Lecture du code client
//		subscriber.setCustomerId( txtCustomerId );
//		
//		// added
//		subscriber.getPhoneNumbers().clear(); subscriber.getAccounts().clear();
//		
//		// Si la liste des phones n'est pas vide
//		if(phones != null && !phones.isEmpty()) {
//			
//			// Recuperation des n° de tel saisis
//			for(SelectData sd : phones) {
//				if(sd.getChecked().booleanValue() && sd.getValue() != null && !sd.getValue().trim().isEmpty() && sd.getValue().trim().length()==12) 
//					subscriber.getPhoneNumbers().add( sd.getValue().replaceAll(" ", "") );
//			}
//			// added if only one account
//			if(subscriber.getPhoneNumbers().isEmpty() && phones.size() == 1){
//				for(SelectData sd : phones){
//					sd.setChecked(Boolean.TRUE);
//					subscriber.getPhoneNumbers().add(sd.getValue());
//				}
//			}
//		}
//		
//		// S'il existe au moins un compte
//		if(accounts != null && !accounts.isEmpty()) {
//			
//			// Recuperation des n° de cpte selectionnes
//			for(SelectData sd : accounts) if(sd.getChecked().booleanValue() ) subscriber.getAccounts().add( sd.getValue());
//			// added if only one account
//			if(subscriber.getAccounts().isEmpty() && accounts.size() == 1){
//				for(SelectData sd : accounts){
//					sd.setChecked(Boolean.TRUE);
//					subscriber.getAccounts().add(sd.getValue());
//				}
//			}
//			
//		}
		
		if(choixLang != null) subscriber.setChoixLangue(choixLang);
		
		subscriber.setUser( MobileMoneyViewHelper.getSessionUser() );// ???
		
		//System.out.println("Update subscriber infos");
		// MAJ des parametres de KYC recuperes depuis la plateforme de MTN
		//if(subscriber.getPhoneNumbers().isEmpty()) subscriber.getPhoneNumbers().add("237"+msisdn);
		subscriber.setMtnSubFirstname(firstname);
		subscriber.setMtnSubSurname(surname);
		subscriber.setMtnSubGender(gender);
		subscriber.setMtnSubLanguage(language);
		//System.out.println("BEFORE");
		try {
			subscriber.setMtnSubDob(new SimpleDateFormat("yyyy-MM-dd").parse(dob));
		} catch(Exception e){
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
		}
		//System.out.println("AFTER");
		subscriber.setMtnSubCni(StringUtils.isBlank(cin) ? null : cin);
		subscriber.setMtnSubCountry(country);
		subscriber.setMtnSubRegion(region);
		subscriber.setMtnSubCity(city);
		subscriber.setMtnSubProfession(profession);
		//System.out.println("LANGUE BEFORE SAVE : "+choixLang);
		subscriber.setChoixLangue(choixLang);
		//if(subscriber.getUser().equals(null)) subscriber.setUser(MobileMoneyViewHelper.getSessionUser());
		//subscriber.setStatus(StatutContrat.ACTIF);
		
		//System.out.println("End of reading subscriber infos");
	}
	
	
	public String linkageECW (Subscriber subscriber, String msisdn){
        String linkage = "";
		try {
			// Liaison de l'abonne chez MTN
			MomoKYCServiceProxy proxy = new MomoKYCServiceProxy();
	        proxy.setEndpoint(param.getUrlKYCApi());
	       	// Recuperation du resultat du linkage depuis la plateforme de MTN
	        //linkage = proxy.linkFinancialResourceInformation(subscriber.getFirstAccount().substring(13).replace("-", ""), "237"+msisdn, null);
	        linkage = proxy.linkFinancialResourceInformation(MobileMoneyTools.getFRI(subscriber), msisdn, null);
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		return linkage;
	}
	
	 
	/**
	 * Enregistre la souscription du client au service Pull/Push from account
	 */
	public void saveSouscription() {
		Boolean valid = false;
		try {
			
			if(subscriber == null) throw new Exception("Client inexistant! Impossible d'effectuer l'opération");
			
			// Lit les infos de souscription saisies sur le formulaire
			readSubscriverInfos();
			
			// Si les preconditions ne sont pas respectees on annule l'operation
			if( !preconditionsOK() ) {
				// Commenter
				subscriber.getPhoneNumbers().clear(); subscriber.getAccounts().clear();
				return;
			}
			 
			//System.out.println("Saving subscription");
			// Enregistrement des parametres de souscription
			subscriber = MobileMoneyViewHelper.appManager.saveSubscriberECW(subscriber);
			
			// Mode test
			if(param.getEtatServiceSDP().equals(StatutService.TEST)) {
				logger.error("OPERATION EXECUTEE EN MODE MAINTENANCE PAR "+MobileMoneyViewHelper.getSessionUser());
				valid = Boolean.TRUE;
			}
			else{
				// Liaison de l'abonne chez MTN
				MomoKYCServiceProxy proxy = new MomoKYCServiceProxy();
		        proxy.setEndpoint(param.getUrlKYCApi());
		        String linkage = "";
		       	// Recuperation du resultat du linkage depuis la plateforme de MTN
		        linkage = proxy.linkFinancialResourceInformation(MobileMoneyTools.getFRI(subscriber), msisdn, null);
		        //linkage = proxy.linkFinancialResourceInformation(subscriber.getFirstAccount().substring(13).replace("-", ""), "237"+msisdn, null);
				//System.out.println("RESPONSE = "+linkage);
		        Link link = new Link();
		        link = MobileMoneyTools.getLinkage(linkage);
		        
		        // Si on obtient une erreur
				if(link.getError()!=null){
					cancelSouscriptionProcess();
					// Message d'information
		        	PortalInformationHelper.showInformationDialog("Erreur : "+link.getError().getErrorcode()+" ("+link.getError().getErrormessage()+")", InformationDialog.DIALOG_ERROR);
		        	return;
				}
				else if(link.getValid()!=null){
					valid = link.getValid();
				}
		        
				// Si on obtient une erreur
//				if(linkage.contains("errorResponse") || linkage.contains("errorcode")){
//					// Recuperer le message d'erreur
//					String error = StringUtils.substringBetween(linkage, "errorcode=\"", "\"");
//		        	logger.info("Erreur : "+error);
//		        	if(linkage.contains("<arguments") && linkage.contains("name=")){
//						// Recuperer le message d'erreur
//						String name = StringUtils.substringBetween(linkage, "name=\"", "\"");
//			        	error = error +" ("+name+" : ";
//			        }
//		        	if(linkage.contains("<arguments") || linkage.contains("value=")){
//						// Recuperer le message d'erreur
//						String value = StringUtils.substringBetween(linkage, "value=\"", "\"");
//						error = error +value+")";
//			        }
//		        	cancelSouscriptionProcess();
//		        	// Message d'information
//		        	PortalInformationHelper.showInformationDialog("Erreur : "+error, InformationDialog.DIALOG_ERROR);
//		        	return;
//		        }
//				
//				// Si on obtient la reponse attendue
//				if(linkage.contains("linkfinancialresourceinformationresponse")){
//					// Recuperer les parametres de la reponse
//					if(linkage.contains("valid")){
//			        	 valid = Boolean.valueOf(StringUtils.substringBetween(linkage, "<valid>", "</valid>"));
//			        	 //System.out.println("VALID : "+valid);
//			        }//else return;
//				}
//				else{
//					String error;
//					// Recuperer le message d'erreur
//					if(linkage.contains("faultstring")){
//						error = StringUtils.substringBetween(linkage, "<faultstring>", "</faultstring>");
//			        }else{
//			        	error = StringUtils.substringBetween(linkage, "(", ")"); // "\"/>"
//			        }
//					
//		        	logger.info("Erreur : "+error);
//		        	
//		        	cancelSouscriptionProcess();
//		        	// Message d'information
//		        	PortalInformationHelper.showInformationDialog("Erreur : "+error, InformationDialog.DIALOG_ERROR);
//		        	return;
//				}
			}
			
			if(valid){
				
				// Facturer la souscription
//				subscriber = MobileMoneyViewHelper.appManager.facturerSouscription(subscriber);
				//System.out.println("Subscription OK : En attente de validation");				
				// Message d'information
				//PortalInformationHelper.showInformationDialog("Souscription effectuée avec succes. Le code PIN sera transféré au client pas SMS.", InformationDialog.DIALOG_SUCCESS);
				PortalInformationHelper.showInformationDialog("Souscription effectuée avec succès.", InformationDialog.DIALOG_SUCCESS);
				
				// Impression du recu
				printRecu();
				
				// Reinitialisation du formulaire
				clearForm(); 
				msisdn = "";
				txtCustomerId = "";
				subscriber = null;
				surname = ""; 
				firstname = ""; 
				dob = ""; 
				cin = ""; 
				gender = ""; 
				language = ""; 
				country = ""; 
				region = ""; 
				city = ""; 
				profession = "";
				otp = "";
				valide = Boolean.FALSE;
			}
			else{
				cancelSouscriptionProcess();
				// Message d'information
				PortalInformationHelper.showInformationDialog("Echec de la souscription.\n\nUne erreur est survenue côté MTN ou l'utilisateur y est déjà abonné.", InformationDialog.DIALOG_INFORMATION);
				
			}
			
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	
	private void cancelSouscriptionProcess(){
		// Annulation cote bank
//		try {
//			MobileMoneyViewHelper.appManager.annulerSouscription(subscriber.getId(), "AUTO");
//		} catch (Exception e) {
//			// Affichage de l'exception
//			PortalExceptionHelper.threatException(e);
//		}
		// Suspendre la souscription
		subscriber.setFacturer(false);
		subscriber.setStatus(StatutContrat.SUSPENDU);
		subscriber.setDateSuspendu(new Date());
		subscriber.setUtiSuspendu("AUTO");
		subscriber.setActive(false);
		MobileMoneyViewHelper.appDAOLocal.save(subscriber);
	}
	
	/**
	 * Initialise les champs du formulaire
	 */
	private void clearForm() {

		subscriber = null;
		
		// Initialisation des listes
		accounts = new ArrayList<SelectData>();
		phones = new ArrayList<SelectData>();
		urlSignature = null;
		
		// Initialisation de la liste des numeros de telephones a fournir
		//for(int i=1; i<=param.getMaxPhoneNumbers(); i++) phones.add( new SelectData("237") ); // subscriber.getPhoneNumbers().add("");
		
	}
	
	
	public void findSubscriberFromECW(){
		surname = ""; 
		firstname = ""; 
		dob = ""; 
		cin = ""; 
		gender = ""; 
		language = ""; 
		country = ""; 
		region = ""; 
		city = ""; 
		profession = "";
		try{
			
			if(msisdn == null || msisdn.trim().isEmpty()){
				// Message d'information
				PortalInformationHelper.showInformationDialog("Veuillez saisir le numéro de téléphone !!", InformationDialog.DIALOG_WARNING);
				return;
			}
			
			if(msisdn.trim().length()!=12){
				// Message d'information
				PortalInformationHelper.showInformationDialog("Numéro de téléphone incorrect !!", InformationDialog.DIALOG_WARNING);
				return;
			}
			
			if(null == param.getUrlKYCApi()){
				// Message d'information
				PortalInformationHelper.showInformationDialog("Veuillez renseigner l'url de l'API du KYC", InformationDialog.DIALOG_WARNING);
				return;
			}

			if(subscriber == null) subscriber = new Subscriber();
			
			// Mode test
			if(param.getEtatServiceSDP().equals(StatutService.TEST)){
				firstname = "TEST";
				surname = "Test";
				gender = "Male";
				language = "en";
				dob = "2000-01-17";
				country = "CM";
				region = "CENTRE";
				city = "YAOUNDE";
				profession = "STUDENT";
				
				// MAJ de la langue d'impression
				if(language.equals("fr")) {
					choixLang = "FR";
					subscriber.setChoixLangue("FR");
				}
				else {
					choixLang = "EN";
					subscriber.setChoixLangue("EN");
				}
				
				cin = "1230456789";
			}
			else{
				// Get account holder personal information (KYC)
				MomoKYCServiceProxy proxy = new MomoKYCServiceProxy();
		        proxy.setEndpoint(param.getUrlKYCApi());
		        String kyc = "";
		       	// Recuperation des infos de l'abonne (KYC) depuis la plateforme de MTN
				kyc = proxy.getAccountHolderPersonalInformation(msisdn.trim(), null);
				//System.out.println("RESPONSE = "+kyc);
				
				PersonalInformations personalInfos = new PersonalInformations();
				personalInfos = MobileMoneyTools.getPersonalInformations(kyc);
				
				// Si on obtient une erreur
				if(personalInfos.getError()!=null || personalInfos == null){
					// Message d'information
		        	PortalInformationHelper.showInformationDialog("Erreur : "+personalInfos.getError().getErrorcode()+" ("+personalInfos.getError().getErrormessage()+")", InformationDialog.DIALOG_ERROR);
		        	return;
				}
				else if(personalInfos.getInformation()!=null){
					firstname = personalInfos.getInformation().getFirstname();
					surname = personalInfos.getInformation().getSurname();
					gender = personalInfos.getInformation().getGender();
					language = personalInfos.getInformation().getLanguage();
					dob = personalInfos.getInformation().getDate();
					country = personalInfos.getInformation().getCountry();
					region = personalInfos.getInformation().getRegion();
					city = personalInfos.getInformation().getCity();
					profession = personalInfos.getInformation().getProfession();
					
					// MAJ de la langue d'impression
					if(language.equals("fr")) {
						choixLang = "FR";
						subscriber.setChoixLangue("FR");
					}
					else {
						choixLang = "EN";
						subscriber.setChoixLangue("EN");
					}
									
					// Get account holder identification (ID Card Number)
					MomoKYCServiceProxy pidProxy = new MomoKYCServiceProxy();
					pidProxy.setEndpoint(param.getUrlKYCApi());
			        String identification = "";
			       	// Recuperation du numero de cni de l'abonne (ID Card Number) depuis la plateforme de MTN
					identification = pidProxy.getAccountHolderIdentification(msisdn.trim(), null);
					
					Identifications id = new Identifications();
					id = MobileMoneyTools.getIdentifications(identification);
					
					// Si on obtient une erreur
					if(id.getError()!=null || id == null){
						// Message d'information
			        	PortalInformationHelper.showInformationDialog("Impossible de récupérer le numéro de CNI. \n\nErreur : "+id.getError().getErrorcode()+" ("+id.getError().getErrormessage()+")", InformationDialog.DIALOG_ERROR);
			        	return;
					}
					else if(id.getIdentification()!=null){
						cin = id.getIdentification().getId();
					}
					
				}
			}
			
			// Si on obtient une erreur
//			if(kyc.contains("errorResponse") || kyc.contains("errorcode")){
//				// Recuperer le message d'erreur
//				String error = StringUtils.substringBetween(kyc, "errorcode=\"", "\""); // "errorcode=\"", "\"/>"
//				logger.info("Erreur : "+error);
//	        	if(kyc.contains("<arguments") && kyc.contains("name=")){
//					// Recuperer le message d'erreur
//					String name = StringUtils.substringBetween(kyc, "name=\"", "\"");
//		        	error = error +" ("+name+" : ";
//		        }
//	        	if(kyc.contains("<arguments") || kyc.contains("value=")){
//					// Recuperer le message d'erreur
//					String value = StringUtils.substringBetween(kyc, "value=\"", "\"");
//					error = error +value+")";
//		        }
//	        	// Message d'information
//	        	PortalInformationHelper.showInformationDialog("Erreur : "+error, InformationDialog.DIALOG_ERROR);
//	        	return;
//	        }
//			
//			// Si on obtient la reponse attendue
//			if(kyc.contains("getaccountholderpersonalinformationresponse")){
//				// Recuperer les parametres de la reponse
//				if(kyc.contains("firstname")){
//		        	 firstname = StringUtils.substringBetween(kyc, "<firstname>", "</firstname>");
//		        	 //System.out.println(" : "+firstname);
//		        }
//				if(kyc.contains("surname")){
//		        	 surname = StringUtils.substringBetween(kyc, "<surname>", "</surname>");
//		        	 //System.out.println("surname : "+surname);
//		        }
//				if(kyc.contains("gender")){
//					gender = StringUtils.substringBetween(kyc, "<gender>", "</gender>");
//		        	//System.out.println("gender : "+gender);
//		        }
//				if(kyc.contains("language")){
//					language = StringUtils.substringBetween(kyc, "<language>", "</language>");
//		        	//System.out.println("language : "+language);
//		        }
//				if(kyc.contains("date")){
//					dob = StringUtils.substringBetween(kyc, "<date>", "</date>").substring(0, 10);
//					//System.out.println("date : "+dob);
//		        }
//				if(kyc.contains("country")){
//					country = StringUtils.substringBetween(kyc, "<country>", "</country>");
//		        	 //System.out.println("country : "+country);
//		        }
//				if(kyc.contains("province")){
//		        	 region = StringUtils.substringBetween(kyc, "<province>", "</province>");
//		        	 //System.out.println("province : "+region);
//		        }
//				if(kyc.contains("city")){
//					city = StringUtils.substringBetween(kyc, "<city>", "</city>");
//		        	 //System.out.println("city : "+city);
//		        }
//				if(kyc.contains("profession")){
//					profession = StringUtils.substringBetween(kyc, "<profession>", "</profession>");
//		        	 //System.out.println("profession : "+profession);
//		        }
//				if(kyc.contains("residentialstatus")){
//		        	 String error = StringUtils.substringBetween(kyc, "<residentialstatus>", "</residentialstatus>");
//		        	 //System.out.println("residentialstatus : "+error);
//		        }
//				
//				// MAJ de la langue d'impression
//				if(language.equals("fr")) {
//					choixLang = "FR";
//					subscriber.setChoixLangue("FR");
//				}
//				else {
//					choixLang = "EN";
//					subscriber.setChoixLangue("EN");
//				}
//				
//				//System.out.println("CHOIX LANGUE = "+choixLang);
//				
//				// Get account holder identification (ID Card Number)
//				MomoKYCServiceProxy pidProxy = new MomoKYCServiceProxy();
//				pidProxy.setEndpoint(param.getUrlKYCApi());
//		        String identification = "";
//		       	// Recuperation du numero de cni de l'abonne (ID Card Number) depuis la plateforme de MTN
//				identification = pidProxy.getAccountHolderIdentification(msisdn.trim(), null);
//				//System.out.println("RESPONSE = "+identification);
//				// Si on obtient une erreur
//				if(identification.contains("errorResponse") || identification.contains("errorcode")){
//					// Recuperer le message d'erreur
//					String error = StringUtils.substringBetween(identification, "errorcode=\"", "\""); // "errorcode=\"", "\"/>"
//					logger.info("Erreur : "+error);
//		        	if(identification.contains("<arguments") && identification.contains("name=")){
//						// Recuperer le message d'erreur
//						String name = StringUtils.substringBetween(identification, "name=\"", "\"");
//			        	error = error +" ("+name+" : ";
//			        }
//		        	if(identification.contains("<arguments") || identification.contains("value=")){
//						// Recuperer le message d'erreur
//						String value = StringUtils.substringBetween(identification, "value=\"", "\"");
//						error = error +value+")";
//			        }
//		        	// Message d'information
//		        	PortalInformationHelper.showInformationDialog("Impossible de récupérer le numéro de CNI. \n\nErreur : "+error, InformationDialog.DIALOG_WARNING);
//		        	return;
//		        }
//				
//				// Si on obtient la reponse attendue
//				if(identification.contains("getaccountholderidentificationresponse")){
//					// Recuperer les parametres de la reponse
//					if(identification.contains("<Id>")){
//						cin = StringUtils.substringBetween(identification, "<Id>", "</Id>");
//			        	 //System.out.println("cin : "+cin);
//			        }
//					
//		        }
//				else{
//					// Recuperer le message d'erreur
//					String error = StringUtils.substringBetween(identification, "(", ")"); // "\"/>"
//					logger.info("Erreur CIN : "+error);
//		        	// Message d'information
//		        	PortalInformationHelper.showInformationDialog("Impossible de récupérer le numéro de CNI. \n\nErreur : "+error, InformationDialog.DIALOG_WARNING);
//		        	return;
//				}
//				
//	        }
//			else{
//				// Recuperer le message d'erreur
//				String error = StringUtils.substringBetween(kyc, "(", ")"); // "\"/>"
//				logger.info("Erreur KYC : "+error);
//	        	// Message d'information
//	        	PortalInformationHelper.showInformationDialog("Erreur : "+error, InformationDialog.DIALOG_ERROR);
//	        	return;
//			}
				
		} catch(RemoteException e){
			e.printStackTrace();
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
		}catch(Exception e){
			e.printStackTrace();
			PortalExceptionHelper.threatException(e);
						
		}
		
	}
	
	
	/**
	 * Recherche un client dans le core banking a partir de son code
	 */
	public void findSubscriberFromCBS() {
		
		try {
			
			// Reinitialisation du formulaire
			clearForm();
			
			if(!StringUtils.isNotBlank(txtCustomerId)){
				// Message d'information
				PortalInformationHelper.showInformationDialog("Veuillez saisir le code client!", InformationDialog.DIALOG_WARNING);
				return;
			}
			
			// Control s'il sagit d'une souscription existante
			if(MobileMoneyViewHelper.appManager.subscriptionAlreadyExistECW(txtCustomerId,msisdn.trim())){
				// Message d'information
				PortalInformationHelper.showInformationDialog("Ce Client a déjà souscrit au produit !", InformationDialog.DIALOG_WARNING);
				return;
			}
			
			// Recherche du client correspondant dans Amplitude
			subscriber = MobileMoneyViewHelper.appManager.findCustomerFromAmplitude(txtCustomerId);
			
			if(subscriber != null) {
				
				// Si le client possede au moins 1 compte
				if(subscriber.getAccounts() != null && !subscriber.getAccounts().isEmpty()){
					
					// Si le client possede un seul compte et le N° tel non specifie
					if(subscriber.getAccounts().size()==1 && (msisdn==null || msisdn.trim()=="")){
						// s'il sagit d'une souscription existante
						if(MobileMoneyViewHelper.appManager.subscriptionAlreadyExist(txtCustomerId)){
							// Message d'information
							PortalInformationHelper.showInformationDialog("Ce Client a déjà souscrit au produit ou son abonnement est en attente de validation !", InformationDialog.DIALOG_WARNING);
							return;
						}
					}
					
					// Chargement des items de comptes
					for(String s : subscriber.getAccounts()) accounts.add( new SelectData( s) );
					// On affiche tous les numéros de l'abonne pour le choix du bon numéro
					for(String s : subscriber.getPhoneNumbers()) {
						s = s.trim().replace(" ", "");
						s = getFormattedPhonenumber(s);
						phones.add( new SelectData( s) );
					}
					
					// Validation du numero de telephone du client dans le CBS
//					if(validatePhoneInCBS()) phones.add( new SelectData("237"+((null==msisdn || msisdn=="") ? "" : msisdn.trim())));
//					else{
//						// Message d'information
//						PortalInformationHelper.showInformationDialog("Le numéro de téléphone "+ msisdn +" n'existe pas parmi les informations du client. Veuillez mettre à jour les informations du client!", InformationDialog.DIALOG_WARNING);
//						return;
//					}
					
					Calendar cal = new GregorianCalendar();
					cal.setTime(new Date());
					cal.add(Calendar.MINUTE, 5);
					
					// Recherche de la signature du client
					urlSignature = MobileMoneyViewHelper.appManager.getLienSig(subscriber.getFirstAccount().split("-")[0], subscriber.getFirstAccount().split("-")[1], "  ", subscriber.getCustomerId(), new Date(), new SimpleDateFormat("HHmmss").format(cal.getTime()), MobileMoneyViewHelper.getSessionUser().getLogin());
					
					subscriber.getAccounts().clear();
					subscriber.getPhoneNumbers().clear();
					//System.out.println("NBRE CPTE : "+subscriber.getAccounts().size());
					
				}
				
			} else{

				// Message d'information
				PortalInformationHelper.showInformationDialog("Compte inexistant ou Fermé!", InformationDialog.DIALOG_WARNING);
				
			}
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	
	/**
	 * Validation des informamions du client recuperes du CBS selectionnees par l'utilisateur
	 * Selection d'un numero de telephone valide et d'un seul numéro de compte
	 * @return
	 */
	public void validateCBSInfos(){
		
		readSubscriverCBSInfos();
		
		// Si les preconditions ne sont pas respectees on annule l'operation
		if( !preconditionsOK() ) {
			// Commenter
//			subscriber.getPhoneNumbers().clear(); subscriber.getAccounts().clear();
			return;
		}
		
		// Verifier si le compte selectionne n'est pas dormant
		if( param.getPeriodeVerifTrxCBS() < 1 ) {
			// Message d'information
			PortalInformationHelper.showInformationDialog("Veuillez configurer un nombre de jours > 0 pour la vérification des opérations sur le compte du client! Impossible d'effectuer la souscription", InformationDialog.DIALOG_WARNING);
			
			disposeResources();
			
			return;
		}
		
		// Verifier si le compte selectionne n'est pas dormant
		if( StringUtils.isBlank(param.getCodesOpeTrx()) ) {
			// Message d'information
			PortalInformationHelper.showInformationDialog("Veuillez configurer les codes des opérations à contrôler avant de continuer! Impossible d'effectuer la souscription", InformationDialog.DIALOG_WARNING);
			
			disposeResources();
			
			return;
		}
		
		// Verifier si le compte est nouveau
		if(!MobileMoneyViewHelper.appManager.isNewAccount(subscriber.getFirstAccount())){
			// Verifier si le compte selectionne est dormant
			if( !MobileMoneyViewHelper.appManager.isAccountActivity(subscriber.getFirstAccount())){
				List<User> users = new ArrayList<User>();
				
				// Envoyer le mail au GFC et DA
				users = MobileMoneyViewHelper.appManager.getGFCDA(MobileMoneyViewHelper.getSessionUser(), subscriber.getFirstAccount());
				logger.info("NB USERS : "+users.size());
				List<String> listDest = new ArrayList<>();
				for(User user : users){
					logger.info("USERS : "+user.getEmail());
					listDest.add(user.getEmail());
				}
				
				if(param.getEtatServiceSDP().equals(StatutService.TEST)){
					listDest = new ArrayList<>();
					listDest.add(param.getDestMailAlerte());
				}
				
				if( !MobileMoneyViewHelper.appManager.isNewTrxDay(subscriber.getFirstAccount())){
					// Annuler l'abonnement et envoyer le mail au GFC et DA
					String msg = "Bonjour,<br><br>Le client titulaire du compte numero "+subscriber.getFirstAccount()+", n'ayant effectue aucune operation lors des "
							+param.getPeriodeVerifTrxCBS()+" derniers jours, a essaye de souscrire au produit MAC MTN.";

					try {
						
						String subject = "MAC MTN : ALERTE COMPTE DORMANT";
						String to = StringUtils.join(listDest, ",");
						String format = "html";
						String from = param.getEmailfrom();
						String title = "MAC MTN";
						
						//String resp = MobileMoneyTools.sendHttpRequest(param.getUrlServiceMail(), msg, format, to, subject, from);
						MobileMoneyViewHelper.appManager.sendSimpleMail(msg, subject, title);
						//logger.info("RESULT : "+resp);
//					} catch (UnsupportedEncodingException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// Message d'information
					PortalInformationHelper.showInformationDialog("Aucune activite detectee sur le compte selectionne lors des "+param.getPeriodeVerifTrxCBS()+" derniers jours! Impossible de valider la souscription", InformationDialog.DIALOG_WARNING);

					disposeResources();
					
					return;
				}
				else{
					// Continuer l'abonnement mais envoyer le mail au GFC et DA
					String msg = "Bonjour,<br><br>Le client titulaire du compte numero "+subscriber.getFirstAccount()+", n'ayant effectue aucune operation lors des "
							+param.getPeriodeVerifTrxCBS()+" derniers jours, a essaye de souscrire au produit MAC MTN.<br><br>"
									+ "La souscription a ete autorisee suite � la detection d'une nouvelle operation ce jour sur ce compte.";

					try {

						String subject = "MAC MTN : ALERTE COMPTE DORMANT";
						String to = StringUtils.join(listDest, ",");
						String format = "html";
						String from = param.getEmailfrom();
						String title = "MAC MTN";
						
						//String resp = MobileMoneyTools.sendHttpRequest(param.getUrlServiceMail(), msg, format, to, subject, from);
						MobileMoneyViewHelper.appManager.sendSimpleMail(msg, subject, title);
						//logger.info("RESULT : "+resp);
//					} catch (UnsupportedEncodingException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// Message d'information
					PortalInformationHelper.showInformationDialog("Une nouvelle op�ration d�tect�e ce jour sur le compte s�lectionn� apr�s plus de "+param.getPeriodeVerifTrxCBS()+" jours d'inactivit�! Continuez la souscription", InformationDialog.DIALOG_INFORMATION);
					
				}
				
			}
		}
			
		// Verifier si le numero de telephone selectionne est valide (conforme au mobile money)
		if( !isValidFormattedPhoneNumber(subscriber.getFirstPhone())){
			
			// Message d'information
			PortalInformationHelper.showInformationDialog("Le num�ro de t�l�phone s�lectionn� est incorrect! Impossible de valider la souscription", InformationDialog.DIALOG_WARNING);
			return;
		}

		msisdn = subscriber.getFirstPhone();
		
	}
			
	
	/**
	 * Validation du numero de telephone du client dans le CBS
	 * @return
	 */
	private Boolean isValidFormattedPhoneNumber(String s){
		
		List<String> mtn = Arrays.asList("67", "68", "650", "651", "652", "653", "654");
		
		Boolean trouve = false;
		logger.info("tel : "+s);
		if(s.length()==12 && s.startsWith("237")){
//			logger.info(s.substring(2, 5)+" - "+s.substring(2, 6));
			if(mtn.contains(s.substring(3, 5)) || mtn.contains(s.substring(3, 6)))
				trouve = true;
		}
		
		return trouve;
	}
	
	
	/**
	 * Formattage du numero de telephone du client recupere du CBS
	 * @return
	 */
	private String getFormattedPhonenumber(String s){
		
		logger.info("tel : "+s);
		// Elimination des espaces
		s = s.trim().replaceAll(" ", "");
		if(!s.startsWith("237")){
			if(s.startsWith("+237")){
				s = s.substring(1, s.length());
			}
			else{
				s = "237"+s;
			}
		}
		logger.info("Formatted phone number : "+s);
		return s;
	}
	
	
	/**
	 * Confirmer la validation des informations, generer l'OTP et enregistrer dans l'abonne actuel
	 * @return
	 */
	public void generateAndSendOTP(){
		
		logger.error("OPERATION EXECUTEE PAR "+MobileMoneyViewHelper.getSessionUser());
		// Si les informations cote MTN ne sont pas disponibles
		if(StringUtils.isBlank(firstname) && StringUtils.isBlank(surname) 
				&& StringUtils.isBlank(cin) && StringUtils.isBlank(dob)){
			
			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Aucune information client cote MTN. Impossible de valider la souscription", InformationDialog.DIALOG_WARNING);
			
			// Annulation
			return;
		}
		
		param = MobileMoneyViewHelper.appManager.findParameters();
		String genOTP = RandomStringUtils.randomAlphabetic(6).toUpperCase();
		subscriber.setSubsOTP(genOTP);
		logger.info("OTP : "+genOTP);
		
		verifyOtp = Boolean.TRUE;
		
		// Mode test
		if(param.getEtatServiceSDP().equals(StatutService.TEST)){
			logger.error("OPERTION EXECUTEE EN MODE MAINTENANCE PAR "+MobileMoneyViewHelper.getSessionUser());
			if(param.getNumerosTest()!=null && !param.getNumerosTest().isEmpty()){
				for(String st : param.getNumerosTest()){
					// Envoi de l'OTP par SMS au numero de test
					MobileMoneyViewHelper.appManager.sendSMS("Votre OTP pour la validation de votre souscription au service MAC MTN pour le numéro "+msisdn.trim()+" est "+genOTP, st);
				}
			}
			else{
				PortalInformationHelper.showInformationDialog("Veuillez configurer les numéros de test pour l'envoi de l'OTP", InformationDialog.DIALOG_INFORMATION);
				return;
			}
		}
		else{
			// Envoi de l'OTP par SMS au client
			MobileMoneyViewHelper.appManager.sendSMS("Votre OTP pour la validation de votre souscription au service MAC MTN est "+genOTP, msisdn.trim());
		}
		
		PortalInformationHelper.showInformationDialog("L'OTP a été généré et envoyé au client. Veuillez le renseigner pour finaliser la souscription", InformationDialog.DIALOG_INFORMATION);
	}

	
	/**
	 * Validation du numero de telephone du client dans le CBS
	 * @return
	 */
	public void validerOTP(){
		
		if(!StringUtils.isNotBlank(otp)){
			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Veuillez saisir l'OTP reçu par le client", InformationDialog.DIALOG_INFORMATION);
			
			// Annulation
			return;
		}
		
		if(StringUtils.equalsIgnoreCase(otp, subscriber.getSubsOTP())) {
			valide = Boolean.TRUE;
			verifyOtp = Boolean.FALSE;
			maxOtp = 0;
		}
		else{
			maxOtp++;
			if(maxOtp==3){
				verifyOtp = Boolean.FALSE;
				// Message d'avertissement
				PortalInformationHelper.showInformationDialog("L'OTP saisi n'est pas valide. Vous avez atteint le nombre maximum d'essai.", InformationDialog.DIALOG_WARNING);
			}
			else{
				// Message d'avertissement
				PortalInformationHelper.showInformationDialog("L'OTP saisi n'est pas valide", InformationDialog.DIALOG_WARNING);
			}
		}
		
	}
	


	/**
	 * Imprime la balance agee
	 */
	public void printRecu() {

		try{
			
			Transaction transaction = MobileMoneyViewHelper.appManager.findTransactionBySubscriber(subscriber.getId()) ;
			
			if(transaction == null) transaction = new Transaction(TypeOperation.SUBSCRIPTION, subscriber, 0d, subscriber.getAccounts().get(0), subscriber.getPhoneNumbers().get(0), "");
			
			List<Transaction> data = new ArrayList<Transaction>(); data.add(transaction);
			
			// Recuperation du visualisateur d'etats dans le FacesContext
			ReportViewerDialog viewer = (ReportViewerDialog) MobileMoneyViewHelper.getSessionManagedBean("reportViewerDialog");
			
			// initialisation du visualisateur
			if(viewer != null) {
				
				// Lecture du Type mime du fichier a afficher
				viewer.setMimeType(WebResourceManager.mimes.get("pdf"));
				
				// Initialisation de la map des parametres de l'etat
				HashMap<Object, Object> map = new HashMap<Object, Object>();
				
				map.put("logoAFB", MobileMoneyTools.getLogoAFB());
				map.put("logo", MobileMoneyTools.getLogoEntete());
				map.put("logoMoMo", MobileMoneyTools.getLogoMoMo());
				map.put("SUBREPORT_DIR", MobileMoneyTools.getReportsDir());
                map.put("codeUser", MobileMoneyViewHelper.getSessionUser().getLogin());
                 
                if(subscriber.getChoixLangue().equalsIgnoreCase("FR")){
                	// Lecture du flux de donnees
    				viewer.setStreamData( MobileMoneyTools.getReportPDFBytes( MobileMoneyTools.getReportsDir().concat("recuSouscriptionFR.jasper"), map, data) );
                }else{
                	// Lecture du flux de donnees
    				viewer.setStreamData( MobileMoneyTools.getReportPDFBytes( MobileMoneyTools.getReportsDir().concat("recuSouscriptionEN.jasper"), map, data) );
                }
								
				// Ouverture du Visualisateur
				viewer.open();
			}
			
		}catch(Exception ex) {
			
			// Traitement de l'exception
			//PortalExceptionHelper.threatException(ex);
			ex.printStackTrace();
			
		}
		
	}
	
		
	public void executerBulkLinkage(){
		int succes = 0;
		int echec = 0;
		//logger.info("Execution du bulk linkage");
		// Recherche des utilisateurs non lies a ECW
		List<Subscriber> listeSubs = new ArrayList<Subscriber>();
		RestrictionsContainer rc = RestrictionsContainer.getInstance();
		rc.add(Restrictions.eq("status", StatutContrat.ACTIF));
		rc.add(Restrictions.or(Restrictions.ne("linkageECW", Boolean.TRUE),Restrictions.isNull("linkageECW")));
		//logger.info("Recherche des spouscriptions non liees cote ECW");
		listeSubs = MobileMoneyViewHelper.appManager.filterSubscriptions(rc, null);
		//logger.info("OK! "+listeSubs.size()+" Abonnes");
		if(listeSubs.isEmpty()) {
			PortalInformationHelper.showInformationDialog("Aucun client non lié à la plateforme ECW de MTN trouvé", InformationDialog.DIALOG_INFORMATION);
			return;
		}
		
//		PortalInformationHelper.showInformationDialog(listeSubs.size()+" clients non liés à la plateforme ECW de MTN trouvés", InformationDialog.DIALOG_INFORMATION);
		
//		if(listeSubs.size()>10) listeSubs = listeSubs.subList(0, 10);
		int nb = 1;
		// Parcour des abonnes trouves pour linkage
		for(Subscriber sub : listeSubs){
			Boolean valid = false;
			
			if(sub.getFirstAccount()!=null && sub.getFirstPhone()!=null){
				// Initier le linkage cote MTN
				try {
					
					// Liaison de l'abonne chez MTN
					MomoKYCServiceProxy proxy = new MomoKYCServiceProxy();
					//logger.info("URL : "+param.getUrlKYCApi());
			        proxy.setEndpoint(param.getUrlKYCApi());
			        String linkage = "";
			        //logger.info("Bank Account : "+sub.getFirstAccount().substring(13).replace("-", ""));
			        //logger.info("Phone Number : "+sub.getFirstPhone());
			       	// Recuperation du resultat du linkage depuis la plateforme de MTN
			        linkage = proxy.linkFinancialResourceInformation(sub.getFirstAccount().substring(13).replace("-", ""), sub.getFirstPhone(), null);
			        logger.info("RESPONSE LINK = "+linkage);
					// Si on obtient une erreur
					if(linkage.contains("errorResponse") || linkage.contains("errorcode")){
						// Recuperer le message d'erreur
						String error = StringUtils.substringBetween(linkage, "errorcode=\"", "\"");
//			        	logger.info("Erreur : "+error);
			        	if(linkage.contains("<arguments") && linkage.contains("name=")){
							// Recuperer le message d'erreur
							String name = StringUtils.substringBetween(linkage, "name=\"", "\"");
				        	error = error +" ("+name+" : ";
				        }
			        	if(linkage.contains("<arguments") || linkage.contains("value=")){
							// Recuperer le message d'erreur
							String value = StringUtils.substringBetween(linkage, "value=\"", "\"");
							error = error +value+")";
				        }
			        	if(linkage.contains("ACCOUNTHOLDER_NOT_ACTIVE")){
			        		logger.info("NOT ACTIVE PHONE NUMBER : "+sub.getFirstPhone());
				        }
			        	// Maj de l'etat du linkage de l'abonne
			        	sub.setLinkageECW(Boolean.FALSE);
			        	
//			        	return;
			        }
					
					// Si on obtient la reponse attendue
					else if(linkage.contains("linkfinancialresourceinformationresponse")){
						// Recuperer les parametres de la reponse
						if(linkage.contains("valid")){
				        	 valid = Boolean.valueOf(StringUtils.substringBetween(linkage, "<valid>", "</valid>"));
				        }
					}
					else{
						String error;
						// Recuperer le message d'erreur
						if(linkage.contains("faultstring")){
							error = StringUtils.substringBetween(linkage, "<faultstring>", "</faultstring>");
				        }else{
				        	error = StringUtils.substringBetween(linkage, "(", ")"); // "\"/>"
				        }
						
			        	logger.info("Erreur : "+error);
			        	
			        	// Maj de l'etat du linkage de l'abonne
			        	sub.setLinkageECW(Boolean.FALSE);
			        	
//			        	return;
					}
					
					if(valid){
						// Maj de l'etat du linkage de l'abonne
			        	sub.setLinkageECW(Boolean.TRUE);
						
					}
					else{
						// Maj de l'etat du linkage de l'abonne
			        	sub.setLinkageECW(Boolean.FALSE);
						
					}
					
					
				} catch(Exception e){
					
					// Affichage de l'exception
					PortalExceptionHelper.threatException(e);
					
				}
			}
			else{
				if(sub.getFirstAccount()!=null){
					logger.info("Numero de compte inexistant");
				}
				else if(sub.getFirstPhone()!=null){
					logger.info("Numero de telephone inexistant");
				}
				else{
					logger.info("Numero de compte et numero de telephone inexistant");
				}
				
			}
			
			if(sub.getLinkageECW()!=null && sub.getLinkageECW().equals(Boolean.TRUE)) succes++;
			else echec++;
			// Enregistrement de l'abonne maj
			MobileMoneyViewHelper.appDAOLocal.save(sub);
		}
		//logger.info("Enregistrement de la liste des abonnes maj");
		// Enregistrement de la liste des abonnes maj
//		MobileMoneyViewHelper.appDAOLocal.saveList(listeSubs, true);
		// Message d'information sur l'etat des traitement effectues
		PortalInformationHelper.showInformationDialog(listeSubs.size()+" souscriptions traités : \n"+succes+" succès et \n"+echec+" échec", InformationDialog.DIALOG_INFORMATION);
	}
	
	
	/**
	 * @return the msisdn
	 */
	public String getMsisdn() {
		return msisdn;
	}

	/**
	 * @param msisdn the msisdn to set
	 */
	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key){
		this.key = key;
	}
	
	
	/**
	 * @return the subscriber
	 */
	public Subscriber getSubscriber() {
		return subscriber;
	}

	/**
	 * @param subscriber the subscriber to set
	 */
	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}

	/**
	 * @return the txtCustomerId
	 */
	public String getTxtCustomerId() {
		return txtCustomerId;
	}

	/**
	 * @param txtCustomerId the txtCustomerId to set
	 */
	public void setTxtCustomerId(String txtCustomerId) {
		this.txtCustomerId = txtCustomerId;
	}

	/**
	 * @return the accountItems
	 *
	public List<SelectItem> getAccountItems() {
		return accountItems;
	}*/

	/**
	 * @return the accounts
	 */
	public List<SelectData> getAccounts() {
		return accounts;
	}

	/**
	 * @param accounts the accounts to set
	 */
	public void setAccounts(List<SelectData> accounts) {
		this.accounts = accounts;
	}

	/**
	 * @return the phones
	 */
	public List<SelectData> getPhones() {
		return phones;
	}

	/**
	 * @param phones the phones to set
	 */
	public void setPhones(List<SelectData> phones) {
		this.phones = phones;
	}
	
	/**
	 * 
	 * @return URL de la signature
	 */
	public String getUrlSignature() {
		return urlSignature;
	}

	/**
	 * @return the param
	 */
	public Parameters getParam() {
		return param;
	}

	/**
	 * @param param the param to set
	 */
	public void setParam(Parameters param) {
		this.param = param;
	}

	/**
	 * @return the surname
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * @param lastname the surname to set
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the dob
	 */
	public String getDob() {
		return dob;
	}

	/**
	 * @param dob the dob to set
	 */
	public void setDob(String dob) {
		this.dob = dob;
	}

	/**
	 * @return the cin
	 */
	public String getCin() {
		return cin;
	}

	/**
	 * @param cin the cin to set
	 */
	public void setCin(String cin) {
		this.cin = cin;
	}
	
	
	/**
	 * @return the languageItems
	 */
	public List<SelectItem> getLanguageItems() {
		return languageItems;
	}

	/**
	 * @return the choixLang
	 */
	public String getChoixLang() {
		//System.out.println("GET LANGUE = "+choixLang);
		//System.out.println("GET LANGUE FR = "+(choixLang.equalsIgnoreCase("FR")?true:false));
		//System.out.println("GET LANGUE EN = "+(choixLang.equalsIgnoreCase("EN")?true:false));
		return choixLang;
	}

	/**
	 * @param choixLang the choixLang to set
	 */
	public void setChoixLang(String choixLang) {
		//System.out.println("SET LANGUE = "+choixLang);
		//System.out.println("SET LANGUE FR = "+(choixLang.equalsIgnoreCase("FR") ? true : false));
		//System.out.println("SET LANGUE EN = "+(choixLang.equalsIgnoreCase("EN") ? true:false));
		this.choixLang = choixLang;
	}

	
	/**
	 * @return the valide
	 */
	public Boolean getValide() {
		return valide;
	}

	/**
	 * @param valid the valide to set
	 */
	public void setValide(Boolean valide) {
		this.valide = valide;
	}

	/**
	 * @return the otp
	 */
	public String getOtp() {
		return otp;
	}

	/**
	 * @param otp the otp to set
	 */
	public void setOtp(String otp) {
		this.otp = otp;
	}

	/**
	 * @return the verifyOtp
	 */
	public Boolean getVerifyOtp() {
		return verifyOtp;
	}

	/**
	 * @param verifyOtp the verifyOtp to set
	 */
	public void setVerifyOtp(Boolean verifyOtp) {
		this.verifyOtp = verifyOtp;
	}
	
		
}

