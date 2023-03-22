package com.afb.dpd.mobilemoney.jsf.forms;

import java.util.ArrayList;
import java.util.List;

import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;

public class FrmMAJDateFacturation extends AbstractPortalForm{
	
	private List<Subscriber> abonnements  = new ArrayList<Subscriber>();
	private List<List<Subscriber>> abons = new ArrayList<>();
	private int num = 1;
	
	/**
	 * Default Constructor
	 */
	public FrmMAJDateFacturation() {
		
	}
	
	@Override
	public void initForm() {
		
		// Appel Parent
		super.initForm();
		
		dateFacturationAregulariser();
	}
	
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Mise à jour des dates de facturation";
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
	
	public void dateFacturationAregulariser() {
		abons = new ArrayList<>();
		try {
			// MAJ des dates facturation
			abons = MobileMoneyViewHelper.appManager.listInvalidDernFact();
			
			abonnements.clear();
			for(List<Subscriber> a : abons) {
				abonnements.addAll(a);
			}
			
			num = 1;
			// Message d'information
			if(!abonnements.isEmpty())
				PortalInformationHelper.showInformationDialog(abonnements.size()+" Abonnements à date de facturation en anomalies. Veuillez mettre à jour.", InformationDialog.DIALOG_SUCCESS);
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
	}
	
	public void mAJLastDateFact() {
		if(abonnements.isEmpty()) {
			PortalInformationHelper.showInformationDialog(" Aucune anomalie detecté.", InformationDialog.DIALOG_INFORMATION);
			return;
		}
		num = 1;
		abonnements = MobileMoneyViewHelper.appManager.updateDateDernFacturation(abons);
		PortalInformationHelper.showInformationDialog(abonnements.size()+" Abonnements mis à jour.", InformationDialog.DIALOG_SUCCESS);
		
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
