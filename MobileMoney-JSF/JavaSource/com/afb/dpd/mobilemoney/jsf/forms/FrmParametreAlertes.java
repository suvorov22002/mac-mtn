/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.ParametreAlertes;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.yashiro.persistence.utils.dao.tools.AliasesContainer;
import com.yashiro.persistence.utils.dao.tools.OrderContainer;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

import afb.dsi.dpd.portal.jpa.entities.Branch;

/**
 * Formulaire de gestion des Parametres des alertes de non validation des souscriptions
 * @author Alex JAZA
 * @version 1.0
 */
public class FrmParametreAlertes extends AbstractPortalForm {
	
	private static Log logger = LogFactory.getLog(FrmParametreAlertes.class);
	
	//************************** Parametres alertes mails ***************************//
	private List<ParametreAlertes> listParamAlertes = new ArrayList<ParametreAlertes>();
	
	private ParametreAlertes selectedParamAlerte = new ParametreAlertes();
	
	private List<Branch> listAgences = new ArrayList<Branch>();
	
	private String email;
	
	private String selectedEmail;

	private int num;
	
	/**
	 * Default Constructor
	 */
	public FrmParametreAlertes() {}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		
		// Appel Parent
		super.initForm();
			
		//****************************************************************************************************************************//
		//listParamAlertes = new ArrayList<ParametreAlertes>();
		//logger.info("------------------------------------- Getting liste des agences --------------------------------");
		// Lecture des agences
		listAgences = MobileMoneyViewHelper.appDAOLocal.filter(Branch.class, AliasesContainer.getInstance().add("type", "t"), RestrictionsContainer.getInstance().add(Restrictions.eq("t.code", "AG")), OrderContainer.getInstance().add(Order.asc("code")), null, 0, -1);
		//SuiviCautionsViewHelper.appDAOLocal.filter(Branch.class, AliasesContainer.getInstance().add("type", "t"), RestrictionsContainer.getInstance().add(Restrictions.eq("t.code", "AG")), OrderContainer.getInstance().add(Order.asc("name")), null, 0, -1);
		// Lecture des parametres
		List<ParametreAlertes> alertes = MobileMoneyViewHelper.appManager.consulterParametreAlertes();
		//logger.info("------------------------------------- Parcours branch --------------------------------");
		for(Branch agence : listAgences){
			Boolean trouve = false;
			for(int i=0; i<alertes.size(); i++){
				if(alertes.get(i).getCodeAgence().equals(agence.getCode())){
					//logger.info("------------------------------------- OK --------------------------------");
					listParamAlertes.add(alertes.get(i));
					trouve = true;
					i = alertes.size();
					break;
				}
				//else i++;
			}
			// Parametre de l'agence non trouve : Ajout du Parametre
			if(!trouve) {
				//logger.info("------------------------------------- NOK --------------------------------");
				List<ParametreAlertes> paramAlerte = MobileMoneyViewHelper.appDAOLocal.filter(ParametreAlertes.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("codeAgence", agence.getCode())), null, null, 0, 1);
				if(paramAlerte.isEmpty()) {
					//logger.info("AGENCE ["+agence.getCode()+", "+agence.getName()+"]");
					listParamAlertes.add(new ParametreAlertes(agence.getCode(), agence.getName(), new Date()));
				}
				else listParamAlertes.add(paramAlerte.get(0));
			}
		}
		
		num = 1;
		selectedParamAlerte = new ParametreAlertes();
		email = "";
		//selectedParamAlerte.setSeuilImprimes(0);
		
		alertes.clear();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Paramètres des Alertes pour Validation des Souscriptions en Attente";
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose();
		
		// Annulation des valeurs
		listParamAlertes = new ArrayList<ParametreAlertes>(); 
		listAgences = new ArrayList<Branch>();
	}
	
	
	public void addEmailAlertes() {
		
		try {
			//logger.info("SELECTED PARAM : "+selectedParamAlerte);
			// Verification de la bonne saisie de la valeur du seuil  || selectedSeuil.toString().isEmpty()
			if(selectedParamAlerte.getCodeAgence() == null) {
				// Message d'information
				PortalInformationHelper.showInformationDialog("Veuillez sélectionner le paramètre à modifier.", InformationDialog.DIALOG_INFORMATION);
				return;
			}
			//logger.info("EMAIL AVANT : "+email);
			// Verification de la bonne saisie de la valeur du seuil
			if(!checkBonneSaisieMail(email)) {
				PortalInformationHelper.showInformationDialog("Veuillez saisir une adresse mail valide.", InformationDialog.DIALOG_INFORMATION);
				return;
			}
			//logger.info("EMAIL : "+email);
			List<String> emails = new ArrayList<String>();
			emails = selectedParamAlerte.getEmails();
			emails.add(email);
			selectedParamAlerte.setEmails(emails);
			
			email = "";
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	
	public void removeEmailAlertes() {
		
		try {
			
			// Verification de la bonne saisie de la valeur du seuil  || selectedSeuil.toString().isEmpty()
			if(selectedParamAlerte.getCodeAgence() == null) {
				// Message d'information
				PortalInformationHelper.showInformationDialog("Veuillez sélectionner le paramètre à modifier.", InformationDialog.DIALOG_INFORMATION);
				return;
			}
			
			List<String> emails = selectedParamAlerte.getEmails();
			emails.remove(selectedEmail);
			selectedParamAlerte.setEmails(emails);
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	

	public void updateParametreAlertes() {
		
		try {
			
			// Verification de la bonne saisie de la valeur du seuil  || selectedSeuil.toString().isEmpty()
			if(selectedParamAlerte.getCodeAgence() == null) {
				// Message d'information
				PortalInformationHelper.showInformationDialog("Veuillez sélectionner le paramètre à modifier.", InformationDialog.DIALOG_INFORMATION);
				return;
			}
			
//			for(ParametreAlertes ps : listSeuils){
//				if(ps.getLastSendMail()==null) ps.setLastSendMail(new Date());
//				if(ps.getCodeAgence().equals(selectedParamAlerte.getCodeAgence())){
//					ps.setSeuilImprimes(selectedParamAlerte.getSeuilImprimes());
//					ps.setDestMail(selectedParamAlerte.getDestMail().trim());
//					num = 1;
//					selectedParamAlerte = new ParametreAlertes();
//					selectedParamAlerte.setSeuilImprimes(0);
//					return;
//				}
//			}
			closeEditEmailAlertes();
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	
	public void saveParametreAlertes() {
		
		try {
						
			// Enregistrement des parametres
			listParamAlertes = MobileMoneyViewHelper.appManager.saveListParametreAlertes(listParamAlertes);
			
			// Message d'information
			PortalInformationHelper.showInformationDialog("Les parametres d'alerte ont ete sauvegardés avec succes.", InformationDialog.DIALOG_SUCCESS);
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	
	private boolean checkBonneSaisieMail(String mail) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(mail);
			emailAddr.validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
		
	}
	
	
	/**
	 * Numeroteur de lignes dans la grille
	 * @return
	 */
	public int getNum() {
		return num++;
	}

	/**
	 * @return the listParamAlertes
	 */
	public List<ParametreAlertes> getListParamAlertes() {
		for(ParametreAlertes p : listParamAlertes){
			//logger.info("PARAM : "+p);
		}
		return listParamAlertes;
	}

	/**
	 * @param listParamAlertes the listParamAlertes to set
	 */
	public void setListParamAlertes(List<ParametreAlertes> listParamAlertes) {
		this.listParamAlertes = listParamAlertes;
	}

	/**
	 * @return the selectedParamAlerte
	 */
	public ParametreAlertes getSelectedParamAlerte() {
		return selectedParamAlerte;
	}

	/**
	 * @param selectedParamAlerte the selectedParamAlerte to set
	 */
	public void setSelectedParamAlerte(ParametreAlertes selectedParamAlerte) {
		this.selectedParamAlerte = selectedParamAlerte;
	}

	/**
	 * @return the listAgences
	 */
	public List<Branch> getListAgences() {
		return listAgences;
	}

	/**
	 * @param listAgences the listAgences to set
	 */
	public void setListAgences(List<Branch> listAgences) {
		this.listAgences = listAgences;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
		
	/**
	 * @return the selectedEmail
	 */
	public String getSelectedEmail() {
		return selectedEmail;
	}

	/**
	 * @param selectedEmail the selectedEmail to set
	 */
	public void setSelectedEmail(String selectedEmail) {
		this.selectedEmail = selectedEmail;
		if(this.selectedEmail != null) removeEmailAlertes();
	}

	public void closeEditEmailAlertes() {
		selectedParamAlerte = new ParametreAlertes();
	}
	
//	public void EditEMailAlertes() {
//		selectedParamAlerte = new ParametreAlertes();
//	}
	
	public boolean isFormEditEmailAlertesOpen(){
		return !(selectedParamAlerte.getId() == null);
	}
	
			
	public String getFrmEditEmailAlertesName(){
		return "frmEditEmailAlertes";
	}
	
	
	public String getFrmEditEmailAlertesFileDefinition(){
		return isFormEditEmailAlertesOpen() ? "FrmEditEmailAlertes.xhtml" : "emptyPage.xhtml";
	}
	
	
}
