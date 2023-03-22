/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.bean;

import java.util.ArrayList;
import java.util.List;

import afb.dsi.dpd.portal.jpa.entities.Role;
import afb.dsi.dpd.portal.jpa.entities.User;

import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;

/**
 * Classe de gestion des habilitations visuelles de l'utilisateur connecte
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class UserHabilitation {
	
	/**
	 * Liste des roles de l'utilisateur sur le module
	 */
	private List<Role> roles = new ArrayList<Role>();
	
	private boolean btnValiderSouscriptionEnabled = false;
	private boolean btnConfimerSouscriptionEnabled = false;
	private boolean btnProfilSouscriptionEnabled = false;
	private boolean btnModifierSouscriptionEnabled = false;
	private boolean btnModifierPINEnabled = false;
	private boolean btnConsulterSouscriptionsEnabled = false;
	private boolean btnConsulterPackageEnabled = false;
	private boolean btnAnnulerSouscriptionEnabled = false;
	private boolean btnTransferPINToCustomerEnabled = false;
	private boolean btnExecuterBulkLinkageEnabled = false;
	
	private boolean btnConsulterTransactionsEnabled = false;
	private boolean btnExportECIntoFileEnabled = false;
	private boolean btnConsulterECEnabled = false;
	private boolean btnPosterECIntoDeltaEnabled = false;
	private boolean btnArchiverTransacEnabled = false;
	private boolean btnPurgerTransacEnabled = false;
	private boolean btnEffectuerSimulationEnabled = false;
	
	private boolean btnConsulterConfigurationEnabled = false;
	private boolean btnModifierConfigurationEnabled = false;
	
	private boolean btnFilterUSSDTransactionEnabled = false;
	private boolean btnExecuteReconciliationEnabled = false;
	
	private boolean btnExecuterTFJOEnabled = false;
	private boolean btnValiderTFJOEnabled = false;
	private boolean btnExportTFJOExcelEnabled = false;
	
	private boolean btnConfigPlageTransactionEnabled = false;
	
	private boolean btnConfigAlerteValidationEnabled = false;
	
	private boolean btnCtrlReservationsEnabled = false;
	private boolean btnConsulterSouscriptionsTempEnabled = false;
	private boolean btnAnnulerSouscriptionTempEnabled =false;
	private boolean btnSaveSouscriptionTempEnabled = false;
	
	/**
	 * Default Constructor
	 */
	public UserHabilitation() {
		
		try {
			
			// Recuperation de l'utilisateur connecte dans la session
			User connected = MobileMoneyViewHelper.getSessionUser();
			
			// Recuperation des roles de l'utilisateur pour le module
			roles = MobileMoneyViewHelper.portalFacadeManager.filterUserRoleFromModule(connected.getId(), "MoMo-06");
			
			// Si l'utilisateur possede des roles sur le module
			if(roles != null && !roles.isEmpty()) {
			
				// Parcours des roles de l'utilisateur
				for(Role r : roles) {
										
					if(r.getName().equals("saveSubscriber")) btnValiderSouscriptionEnabled = true;
					else if(r.getName().equals("activerSubscriptions")) btnConfimerSouscriptionEnabled = true;
					else if(r.getName().equals("profilSubscriptions")) btnProfilSouscriptionEnabled = true;
					else if(r.getName().equals("updateBankPIN")) btnModifierPINEnabled = true;
					else if(r.getName().equals("updateSubscriber")) btnModifierSouscriptionEnabled = true;
					else if(r.getName().equals("filterSubscriptions")) btnConsulterSouscriptionsEnabled = true;
					else if(r.getName().equals("filterPackages")) btnConsulterPackageEnabled = true;
					else if(r.getName().equals("annulerSouscription")) btnAnnulerSouscriptionEnabled = true;
					else if(r.getName().equals("sendCodePINBySMS")) btnTransferPINToCustomerEnabled = true;
					else if(r.getName().equals("executerBulkLinkage")) btnExecuterBulkLinkageEnabled = true;
					
					else if(r.getName().equals("filterTransactions")) btnConsulterTransactionsEnabled = true;
					else if(r.getName().equals("extractECFromSelectedTransactionsIntoFile")) btnExportECIntoFileEnabled = true;
					else if(r.getName().equals("getECFromTransactions")) btnConsulterECEnabled = true;
					else if(r.getName().equals("posterTransactionsDansCoreBanking")) btnPosterECIntoDeltaEnabled = true;
					else if(r.getName().equals("archiverTransactions")) btnArchiverTransacEnabled = true;
					else if(r.getName().equals("purgerTransactions")) btnPurgerTransacEnabled = true;
					
					else if(r.getName().equals("consulterConfiguration")) btnConsulterConfigurationEnabled = true;
					else if(r.getName().equals("saveParameters")) btnModifierConfigurationEnabled = true;
					else if(r.getName().equals("filterUSSDTransactions")) btnFilterUSSDTransactionEnabled = true;
					else if(r.getName().equals("executerReconciliation")) btnExecuteReconciliationEnabled = true;
					
					else if(r.getName().equals("executerTFJO")) btnExecuterTFJOEnabled = true;
					else if(r.getName().equals("validerTFJO")) btnValiderTFJOEnabled = true;
					else if(r.getName().equals("exportComptabilisationIntoExcelFile")) btnExportTFJOExcelEnabled = true;
					else if(r.getName().equals("executerSimulation")) btnEffectuerSimulationEnabled = true;
					
					else if(r.getName().equals("filterPlageTransactions")) btnConfigPlageTransactionEnabled = true;
					
					else if(r.getName().equals("saveParametreAlertes")) btnConfigAlerteValidationEnabled = true;
					
					else if(r.getName().equals("ctrlReservations")) btnCtrlReservationsEnabled = true;
					else if(r.getName().equals("consulterSusTemporaire")) btnConsulterSouscriptionsTempEnabled = true;
					else if(r.getName().equals("voidSusTemporaire")) btnAnnulerSouscriptionTempEnabled = true;
					else if(r.getName().equals("saveSusTemporaire")) btnSaveSouscriptionTempEnabled = true;

				}
				
			}
			
		} catch(Exception e){ e.printStackTrace(); }
		
	}

	public boolean isMenuPullPushEnabled() {
		return btnValiderSouscriptionEnabled || btnModifierSouscriptionEnabled || btnModifierPINEnabled || btnConsulterSouscriptionsEnabled || btnAnnulerSouscriptionEnabled || btnTransferPINToCustomerEnabled;
	}

	public boolean isMenuTraitementEnabled(){
		return btnConsulterTransactionsEnabled || btnExportECIntoFileEnabled || btnConsulterECEnabled || btnPosterECIntoDeltaEnabled || btnArchiverTransacEnabled || btnPurgerTransacEnabled || btnEffectuerSimulationEnabled;
	}
	
	public boolean isMenuConfigsEnabled(){
		return btnConsulterConfigurationEnabled || btnModifierConfigurationEnabled || btnConfigPlageTransactionEnabled || btnConfigAlerteValidationEnabled;
	}

	public boolean isMenuSouscriptionEnabled() {
		return btnValiderSouscriptionEnabled || btnExecuterBulkLinkageEnabled;
	}

	public boolean isMenuModifierContratEnabled() {
		return btnModifierSouscriptionEnabled || btnModifierPINEnabled;
	}

	public boolean isMenuStatsSouscriptionsEnabled() {
		return btnConsulterSouscriptionsEnabled || btnAnnulerSouscriptionEnabled || btnTransferPINToCustomerEnabled;
	}

	public boolean isMenuSouscriptionsTempEnabled() {
		return btnAnnulerSouscriptionTempEnabled;
	}
	
	public boolean isMenuPackagesEnabled() {
		return btnConsulterPackageEnabled;
	}
	
	public boolean isMenuSimulationEnabled(){
		return btnEffectuerSimulationEnabled;
	}

	public boolean isMenuStatsTransactionsEnabled(){
		return btnConsulterTransactionsEnabled || btnExportECIntoFileEnabled || btnConsulterECEnabled || btnPosterECIntoDeltaEnabled || btnArchiverTransacEnabled || btnPurgerTransacEnabled || btnCtrlReservationsEnabled;
	}

	/**
	 * @return the btnProfilSouscriptionEnabled
	 */
	public boolean isBtnProfilSouscriptionEnabled() {
		return btnProfilSouscriptionEnabled;
	}

	/**
	 * @return the btnValiderSouscriptionEnabled
	 */
	public boolean isBtnValiderSouscriptionEnabled() {
		return btnValiderSouscriptionEnabled;
	}

	/**
	 * @return the btnConfimerSouscriptionEnabled
	 */
	public boolean isBtnConfimerSouscriptionEnabled() {
		return btnConfimerSouscriptionEnabled;
	}

	/**
	 * @return the btnModifierSouscriptionEnabled
	 */
	public boolean isBtnModifierSouscriptionEnabled() {
		return btnModifierSouscriptionEnabled;
	}

	/**
	 * @return the btnModifierPINEnabled
	 */
	public boolean isBtnModifierPINEnabled() {
		return btnModifierPINEnabled;
	}

	/**
	 * @return the btnConsulterSouscriptionsEnabled
	 */
	public boolean isBtnConsulterSouscriptionsEnabled() {
		return btnConsulterSouscriptionsEnabled;
	}
	
	/**
	 * @return the btnConsulterPackagesEnabled
	 */
	public boolean isBtnConsulterPackagesEnabled() {
		return btnConsulterPackageEnabled;
	}

	/**
	 * @return the btnAnnulerSouscriptionEnabled
	 */
	public boolean isBtnAnnulerSouscriptionEnabled() {
		return btnAnnulerSouscriptionEnabled;
	}

	/**
	 * @return the btnTransferPINToCustomerEnabled
	 */
	public boolean isBtnTransferPINToCustomerEnabled() {
		return btnTransferPINToCustomerEnabled;
	}

	/**
	 * @return the btnConsulterTransactionsEnabled
	 */
	public boolean isBtnConsulterTransactionsEnabled() {
		return btnConsulterTransactionsEnabled;
	}

	/**
	 * @return the btnExportECIntoFileEnabled
	 */
	public boolean isBtnExportECIntoFileEnabled() {
		return btnExportECIntoFileEnabled;
	}

	/**
	 * @return the btnConsulterECEnabled
	 */
	public boolean isBtnConsulterECEnabled() {
		return btnConsulterECEnabled;
	}

	/**
	 * @return the btnPosterECIntoDeltaEnabled
	 */
	public boolean isBtnPosterECIntoDeltaEnabled() {
		return btnPosterECIntoDeltaEnabled;
	}

	/**
	 * @return the btnArchiverTransacEnabled
	 */
	public boolean isBtnArchiverTransacEnabled() {
		return btnArchiverTransacEnabled;
	}

	/**
	 * @return the btnPurgerTransacEnabled
	 */
	public boolean isBtnPurgerTransacEnabled() {
		return btnPurgerTransacEnabled;
	}

	/**
	 * @return the btnEffectuerSimulationEnabled
	 */
	public boolean isBtnEffectuerSimulationEnabled() {
		return btnEffectuerSimulationEnabled;
	}

	/**
	 * @return the btnConsulterConfigurationEnabled
	 */
	public boolean isBtnConsulterConfigurationEnabled() {
		return btnConsulterConfigurationEnabled;
	}

	/**
	 * @return the btnModifierConfigurationEnabled
	 */
	public boolean isBtnModifierConfigurationEnabled() {
		return btnModifierConfigurationEnabled;
	}

	
	/**
	 * @return the btnCtrlReservationsEnabled
	 */
	public boolean isBtnCtrlReservationsEnabled() {
		return btnCtrlReservationsEnabled;
	}

	/**
	 * @return the roles
	 */
	public List<Role> getRoles() {
		return roles;
	}

	/**
	 * @return the btnFilterUSSDTransactionEnabled
	 */
	public boolean isBtnFilterUSSDTransactionEnabled() {
		return btnFilterUSSDTransactionEnabled;
	}

	/**
	 * @return the btnExecuteReconciliationEnabled
	 */
	public boolean isBtnExecuteReconciliationEnabled() {
		return btnExecuteReconciliationEnabled;
	}
	
	public boolean isMenuUSSDTransEnabled(){
		return btnFilterUSSDTransactionEnabled || btnExecuteReconciliationEnabled;
	}

	/**
	 * @return the btnExecuterTFJOEnabled
	 */
	public boolean isBtnExecuterTFJOEnabled() {
		return btnExecuterTFJOEnabled;
	}

	/**
	 * @return the btnValiderTFJOEnabled
	 */
	public boolean isBtnValiderTFJOEnabled() {
		return btnValiderTFJOEnabled;
	}

	/**
	 * @return the btnExportTFJOExcelEnabled
	 */
	public boolean isBtnExportTFJOExcelEnabled() {
		return btnExportTFJOExcelEnabled;
	}
	
	/**
	 * @return the btnConfigPlageTransactionEnabled
	 */
	public boolean isBtnConfigPlageTransactionEnabled() {
		return btnConfigPlageTransactionEnabled;
	}
	
	public boolean isBtnConsulterSouscriptionsTempEnabled() {
		return btnConsulterSouscriptionsTempEnabled;
	}
	
	public boolean isBtnAnnulerSouscriptionTempEnabled() {
		return btnAnnulerSouscriptionTempEnabled;
	}
	
	public boolean isBtnSaveSouscriptionTempEnabled() {
		return btnSaveSouscriptionTempEnabled;
	}
	
		
	/**
	 * @return the btnConfigAlerteValidationEnabled
	 */
	public boolean isBtnConfigAlerteValidationEnabled() {
		return btnConfigAlerteValidationEnabled;
	}

	/**
	 * @param btnConfigAlerteValidationEnabled the btnConfigAlerteValidationEnabled to set
	 */
	public void setBtnConfigAlerteValidationEnabled(boolean btnConfigAlerteValidationEnabled) {
		this.btnConfigAlerteValidationEnabled = btnConfigAlerteValidationEnabled;
	}

	public boolean isMenuTFJOEnabled(){
		return btnExportTFJOExcelEnabled || btnValiderTFJOEnabled || btnExecuterTFJOEnabled;
	}

	/**
	 * @return the btnExecuterBulkLinkageEnabled
	 */
	public boolean isBtnExecuterBulkLinkageEnabled() {
		return btnExecuterBulkLinkageEnabled;
	}
			
}
