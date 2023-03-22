package com.afb.dpd.mobilemoney.jsf.models;

import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;


/**
 * Helper de Gestion de la boite de dialogue d'information
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class PortalInformationHelper {
	
	/**
	 * Default Constructor
	 */
	public PortalInformationHelper() {}
	
	/**
	 * Affiche la boite de dialogue d'information
	 * @param message
	 * @param typeDialog
	 */
	public static void showInformationDialog(String message, int typeDialog){
		
		// Recuperation de la boite de dialogue dans la session
		InformationDialog infDlg = (InformationDialog)MobileMoneyViewHelper.getSessionManagedBean(InformationDialog.getName());
		
		// Lecture du message 
		infDlg.setMessage(message);
		
		// Lecture du type de message
		infDlg.setTypeDialog( typeDialog  );
		
		// Aucun details
		infDlg.setDetails("");
		
		// Ouverture de la boite de dialogue
		infDlg.setOpen(true);
	}
	
}
