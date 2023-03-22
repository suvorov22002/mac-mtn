/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.forms;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.afb.dpd.mobilemoney.jsf.tools.SelectData;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

/**
 * Formulaire de gestion des Parametres generaux
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class FrmModifSouscription extends AbstractPortalForm {

	private Subscriber subscriber;
	
	private Parameters param;
	
	private String txtCustomerId;
	
	private boolean toModify = false;
	private boolean toAdd = false;
	
	private List<SelectData> phones = new ArrayList<SelectData>();
	private List<SelectData> accounts = new ArrayList<SelectData>();
	
	/**
	 * Default Constructor
	 */
	public FrmModifSouscription() {}

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
		
		//clearForm();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Modification d'un contrat de Souscription Pull/Push";
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose();
		toAdd = false; toModify = false;
		param = null; accounts.clear(); phones.clear(); subscriber = null; txtCustomerId = null;
	}
	
	/**
	 * Recherche la souscription sur la base du code client fournit
	 */
	public void findSubscriber() {
		
		try {
			
			if(txtCustomerId == null || txtCustomerId.isEmpty()) return;
			
			accounts = new ArrayList<SelectData>(); phones = new ArrayList<SelectData>();
			
			// Recherche de la souscription du client fourni
			List<Subscriber> list = MobileMoneyViewHelper.appManager.filterSubscriptions(RestrictionsContainer.getInstance().add(Restrictions.eq("customerId", txtCustomerId)), null);
			
			// Recuperation du souscripteur 
			subscriber = list != null && !list.isEmpty() ? list.get(0) : null;
			
			// Si on n'a pas trouve la souscription
			if( subscriber == null ) {
	
				// Message d'information
				PortalInformationHelper.showInformationDialog("Souscription inexistante.", InformationDialog.DIALOG_WARNING);
				
				return;
			}
			
			// Recherche de tous les cptes du client dans le CBS
			List<String> accs = MobileMoneyViewHelper.appManager.filterCustomerAccountsFromCBS(txtCustomerId);
			
			// Chargement des cptes a selectionner sur la vue
			for(String s : accs) accounts.add( new SelectData( subscriber.getAccounts().contains(s) ? Boolean.TRUE : Boolean.FALSE, s) );
			
			// Chargement des num de tel a saisir sur la vue
			for(String s : subscriber.getPhoneNumbers()) phones.add( new SelectData(s) );
			
		}catch(Exception e){PortalExceptionHelper.threatException(e);}
	}
	
	/**
	 * Methode de modification du contrat
	 */
	public void save() {
		
		// Execution de la methode d'ajout d'un nouveau Numero ou Cpte
		if(toAdd) addPhoneOrAccount();
		
		// Execution de la methode de modification de telephone ou de cpte
		else if(toModify) modifyPhoneOrAccount();
	}
	
	/**
	 * Methode de modification du PIN Banque
	 */
	public void modifyBankPIN() {
		
		try {
			
			if(subscriber == null) {

				// Message d'information
				PortalInformationHelper.showInformationDialog("Vous devez rechercher une souscription avant d'effectuer cette opération.", InformationDialog.DIALOG_WARNING);
				
				return;
			}
			
			// Mise a jour
			MobileMoneyViewHelper.appManager.updateBankPIN(subscriber);
			
			// Message d'information
			PortalInformationHelper.showInformationDialog("Le nouveau PIN a été généré avec succès et sera tranféré au client par SMS.", InformationDialog.DIALOG_SUCCESS);
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	/**
	 * Methode de modification du numero de Tel ou du numero de cpte
	 */
	public void modifyPhoneOrAccount() {
		
		try {
			
			if(subscriber == null) throw new Exception("Souscripteur inexistant! Impossible d'effectuer l'opération");
			
			if( !preconditionsOK() ) return;
			
			readSubscriverInfos();
			
			// Enregistrement des parametres
			MobileMoneyViewHelper.appManager.updateSubscriber(subscriber);
			
			// Message d'information
			PortalInformationHelper.showInformationDialog("Le contrat de souscription a été mis à jour!.", InformationDialog.DIALOG_SUCCESS);
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	/**
	 * Methode d'ajout d'un numero de telephone ou d'un numero de cpte au contrat
	 */
	public void addPhoneOrAccount() {
		
		try {
			
			if( !preconditionsOK() ) return;
			
			//readSubscriverInfos();
			
			// Enregistrement des parametres
			MobileMoneyViewHelper.appManager.updateSubscriber(subscriber);
			
			// Message d'information
			PortalInformationHelper.showInformationDialog("Le contrat de souscription a été mit à jour!.", InformationDialog.DIALOG_SUCCESS);
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}

	private boolean preconditionsOK() {

		readSubscriverInfos();
		
		// Si aucun compte n'a ete selectionne
		if( subscriber.getCustomerId() == null)  {

			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Veuillez recherche un client par son code SVP!", InformationDialog.DIALOG_WARNING);
			
			// Annulation
			return false;
		}

		// Si aucun compte n'a ete selectionne
		if( subscriber == null)  {

			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Aucun client sélectionné. Impossible de valider la souscription", InformationDialog.DIALOG_WARNING);
			
			// Annulation
			return false;
		}
		
		// Si aucun compte n'a ete selectionne
		if( subscriber.getAccounts().isEmpty() )  {

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
			PortalInformationHelper.showInformationDialog("Aucun numero de téléphone fournit. Impossible de valider la souscription.", InformationDialog.DIALOG_WARNING);
			
			// Annulation
			return false;
		}
		
		// Si le nbre de numeros de telephones fourni depasse le maximum parametre
		if( subscriber.getPhoneNumbers().size() > param.getMaxPhoneNumbers() )  {

			// Message d'avertissement
			PortalInformationHelper.showInformationDialog("Le nombre maximum de numéros de téléphones autorisé est de "+ param.getMaxPhoneNumbers() +". Impossible de poursuivre l'opération", InformationDialog.DIALOG_WARNING);
			
			// Annulation
			return false;
		}
		
		// Si l'un des numeros de telephone fournit appartient deja a un client
		for(String s : subscriber.getPhoneNumbers()) {
			
			if(MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(s, subscriber.getId()) != null) {

				// Message d'avertissement
				PortalInformationHelper.showInformationDialog("le numéro de téléphone "+ s +" appartient déjà à un autre souscripteur.", InformationDialog.DIALOG_WARNING);
				
				// Annulation
				return false;
				
			}
			
		}
		
		return true;
	}

	/**
	 * Recuperation des num de tel et des comptes saisis sur le form de souscription
	 */
	private void readSubscriverInfos() {
		
		// Recuperation des n° de tel saisis
		//for(int i=subscriber.getPhoneNumbers().size()-1; i>=0; i--) if(subscriber.getPhoneNumbers().get(i).isEmpty()) subscriber.getPhoneNumbers().remove(i);
	
		subscriber.getAccounts().clear();
		subscriber.getPhoneNumbers().clear();

		// S'il existe au moins un num tel
		if(phones != null && !phones.isEmpty()) {
			
			// Recuperation des n° de cpte selectionnes
			for(SelectData sd : phones) if(sd.getValue() != null && !sd.getValue().isEmpty() ) subscriber.getPhoneNumbers().add( sd.getValue());
		}
		
		// S'il existe au moins un compte
		if(accounts != null && !accounts.isEmpty()) {
			
			// Recuperation des n° de cpte selectionnes
			for(SelectData sd : accounts) if(sd.getChecked().booleanValue() ) subscriber.getAccounts().add( sd.getValue());
		}
		
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
	 * @return the toModify
	 */
	public boolean isToModify() {
		return toModify;
	}

	/**
	 * @param toModify the toModify to set
	 */
	public void setToModify(boolean toModify) {
		this.toModify = toModify;
	}

	/**
	 * @return the toAdd
	 */
	public boolean isToAdd() {
		return toAdd;
	}

	/**
	 * @param toAdd the toAdd to set
	 */
	public void setToAdd(boolean toAdd) {
		this.toAdd = toAdd;
	}
	
	public boolean isBtnAddEnabled(){
		if(subscriber == null || param == null) return false;
		return param.getMaxAccounts() > subscriber.getAccounts().size() || param.getMaxPhoneNumbers() > subscriber.getPhoneNumbers().size();
	}

	public boolean isBtnModifyEnabled() {
		return subscriber != null;
	}
	
	public void clickBtnAdd(){
		toAdd = true; toModify = false;
		if(phones.size() < param.getMaxPhoneNumbers() ) phones.add(new SelectData(""));
	}
	
	public void clickBtnModifiy(){
		toAdd = false; toModify = true;
	}

	/**
	 * @return the param
	 */
	public Parameters getParam() {
		return param;
	}
	
}

