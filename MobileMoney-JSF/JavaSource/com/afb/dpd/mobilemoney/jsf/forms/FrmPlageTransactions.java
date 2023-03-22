package com.afb.dpd.mobilemoney.jsf.forms;

import java.util.ArrayList;
import java.util.List;

import com.afb.dpd.mobilemoney.jpa.entities.PlageTransactions;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
 
/**
 * Formulaire de gestion des Parametres generaux
 * @author Francis KONCHOU
 * @version 1.0
 */
public class FrmPlageTransactions extends AbstractPortalForm {

	/**
	 * Plage
	 */
	private PlageTransactions plage;

	/**
	 * Plages
	 */
	List<PlageTransactions> plages = new ArrayList<PlageTransactions>();

	/**
	 * Default Constructor
	 */
	public FrmPlageTransactions() {}

	
	
	/**
	 * @return the plage
	 */
	public PlageTransactions getPlage() {
		return plage;
	}



	/**
	 * @param plage the plage to set
	 */
	public void setPlage(PlageTransactions plage) {
		this.plage = plage;
	}



	/**
	 * @return the plages
	 */
	public List<PlageTransactions> getPlages() {
		return plages;
	}



	/**
	 * @param plages the plages to set
	 */
	public void setPlages(List<PlageTransactions> plages) {
		this.plages = plages;
	}



	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {

		// Appel Parent
		super.initForm();

		// Lecture des parametres
		plages = MobileMoneyViewHelper.appManager.filterPlageTransactions();

		if(plages.isEmpty()){
			plages = new ArrayList<PlageTransactions>();
			plages.add(new PlageTransactions(PlageTransactions.Default));
		}
		

	}

	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose();

		// Annulation des valeurs
		plages = null; 
	}

	
	public void processAdd() {
		
		if(plages == null ) plages = new ArrayList<PlageTransactions>();
		plages.add(new PlageTransactions());
	}

	public void savePamareters() {

		try {

			// Enregistrement des parametres
			MobileMoneyViewHelper.appManager.savePlageTransactions(plages);

			// Message d'information
			PortalInformationHelper.showInformationDialog("Les parametres ont ete sauvegardés avec succes.", InformationDialog.DIALOG_SUCCESS);

		} catch(Exception e){

			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);

		}

	}


	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Paramètres Plage";
	}

}
