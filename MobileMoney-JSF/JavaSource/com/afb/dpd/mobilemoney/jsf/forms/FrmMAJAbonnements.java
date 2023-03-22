/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.forms;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.enums.ModeFacturation;
import com.afb.dpd.mobilemoney.jpa.enums.Periodicite;
import com.afb.dpd.mobilemoney.jpa.enums.TypeValeurFrais;
import com.afb.dpd.mobilemoney.jpa.tools.TypeCompte;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.afb.dpd.mobilemoney.jsf.tools.SelectData;

/**
 * Formulaire de mise a jour des abonnements
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class FrmMAJAbonnements extends AbstractPortalForm {
	
	private List<Subscriber> abonnements  = new ArrayList<Subscriber>();
	
	private int num = 1;
	
	/**
	 * Default Constructor
	 */
	public FrmMAJAbonnements() {}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		
		// Appel Parent
		super.initForm();
		
		majAbonnements();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Mise à jour des abonnements";
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
		abonnements.clear();
	}	
	
	
	public void majAbonnements() {
		
		try {
			
			// MAJ des abonnements
			abonnements = MobileMoneyViewHelper.appManager.updateSubscriberIsEmploye();
			
			num = 1;
			// Message d'information
			PortalInformationHelper.showInformationDialog(abonnements.size()+" abonnements ont ete mis à jour avec succes.", InformationDialog.DIALOG_SUCCESS);
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	

	/**
	 * @return the abonnements
	 */
	public List<Subscriber> getAbonnements() {
		return abonnements;
	}

	/**
	 * @param abonnements the abonnements to set
	 */
	public void setAbonnements(List<Subscriber> abonnements) {
		this.abonnements = abonnements;
	}

	/**
	 * @return the num
	 */
	public int getNum() {
		return num++;
	}

	/**
	 * @param num the num to set
	 */
	public void setNum(int num) {
		this.num = num;
	}
	
	
}
