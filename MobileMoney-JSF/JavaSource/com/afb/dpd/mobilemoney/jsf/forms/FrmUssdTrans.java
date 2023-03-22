/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.forms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.criterion.Restrictions;

import afb.dsi.dpd.portal.jpa.entities.User;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

import com.afb.dpd.mobilemoney.jpa.entities.USSDTransaction;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jpa.exception.MoMoException;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

/**
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class FrmUssdTrans extends AbstractPortalForm {

	/**
	 * Liste des transactions USSD
	 */
	private List<USSDTransaction> list = new ArrayList<USSDTransaction>();
	
	/**
	 * Transaction en cours
	 */
	private USSDTransaction trans;
	
	/**
	 * Transaction selectionnee
	 */
	private String selectedTransId; 
	
	private String txtDateDeb = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date()), txtDateFin = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date()), searchPhone;
	private String selectedTypOp, selectedTypStatus, selectedTypProcess;
	
	private List<SelectItem> typOpItem = new ArrayList<SelectItem>();
	private List<SelectItem> typStatusItem = new ArrayList<SelectItem>();
	private List<SelectItem> typProcessItem = new ArrayList<SelectItem>();
	
	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH':'mm':'ss");
	
	private int num = 1;
	
	/**
	 * Default Constructor
	 */
	public FrmUssdTrans(){}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		// TODO Auto-generated method stub
		super.initForm();  num = 1;
		
		// Chargement des items de types d'operations
		typOpItem.add( new SelectItem(null, " ") );
		typOpItem.add( new SelectItem("CREDIT", TypeOperation.PULL.getValue()) );
		typOpItem.add( new SelectItem("DEBIT", TypeOperation.PUSH.getValue()) );
		
		// Chargement des etats des transactions
		typStatusItem.add( new SelectItem(null, " ") );
		typStatusItem.add( new SelectItem("valide", "Validé") );
		typStatusItem.add( new SelectItem("erreur", "Echec") );
		typStatusItem.add( new SelectItem("en cours", "En Cours") );
		typStatusItem.add( new SelectItem("confirme", "Confirmé") );

		// Chargement des niveaux de traitement
		typProcessItem.add( new SelectItem(null, " ") );
		typProcessItem.add( new SelectItem("valide", "Validé") );
		typProcessItem.add( new SelectItem("Reconciliee", "Réconcilié") );
		typProcessItem.add( new SelectItem("REVERSE", "Annulé") );
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Consultation des Transactions USSD";
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose();
		searchPhone = null; typProcessItem.clear(); selectedTypProcess = null; selectedTypStatus = null;
		selectedTypOp = null; typOpItem.clear(); typStatusItem.clear(); num = 1; list.clear();
	}

	/**
	 * Recherche la liste des transactions ussd
	 */
	public void filterTransactions(){
		
		try {
			
			// Initialisation d'un conteneur de restrictions
			RestrictionsContainer rc = RestrictionsContainer.getInstance().add(Restrictions.between("dt_Created", sdf.parseObject(txtDateDeb + " 00:00:01"), sdf.parseObject(txtDateFin + " 23:59:00")));
			
			// Ajout des restrictions au conteneur
			if(selectedTypOp != null && !selectedTypOp.isEmpty()) rc.add(Restrictions.eq("str_Type", selectedTypOp));
			if(selectedTypProcess != null && !selectedTypProcess.isEmpty()) rc.add(Restrictions.eq("str_Step", selectedTypProcess));
			if(selectedTypStatus != null && !selectedTypStatus.isEmpty()) rc.add(Restrictions.eq("str_Status", selectedTypStatus));
			if(searchPhone != null && !searchPhone.isEmpty()) rc.add(Restrictions.like("str_Phone", "%" + searchPhone + "%"));
			
			// Filtre de la liste des transactions
			list = MobileMoneyViewHelper.appManager.filterUSSDTransactions(rc); 
			
			for(USSDTransaction ut : list) {
				ut.setSelected(ut.isAreconcilier());
				//ut.setSelected(ut.isProcess() == false && ut.isOk() == false);
				//ut.setSelected(ut.opeOK());
				if(ut.getSubscriber() == null) ut.setSubscriber( MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(ut.getStr_Phone()) );
			}
			
			num = 1;
			
		} catch(Exception ex) {
			
			// Traitement de m'exception
			PortalExceptionHelper.threatException(ex);
		}
		
	}
	
	public void processReconciliationAuto() {
		MobileMoneyViewHelper.appManager.processReconciliationAuto();
	}
	
	public void StopReconciliationAuto(){
		MobileMoneyViewHelper.appManager.StopReconciliationAuto();
	}
	
	
	

	/**
	 * Recherche la liste des transactions ussd
	 */
	public void executerReconciliation() {
		
		try {
			
			if(list == null || list.isEmpty()) return;
			
			// Initialisation de la liste des trx a reconcilier
			List<USSDTransaction> tmp = new ArrayList<USSDTransaction>();
			
			// Recuperation des transactions selectionnees
			for(USSDTransaction t : list) if(t.isSelected()) tmp.add(t);
			
			if(tmp.isEmpty()) {
				
				// Msg d'information
				PortalInformationHelper.showInformationDialog("Aucune Transaction sélectionnée.", InformationDialog.DIALOG_WARNING);
				
			} else {
				
				User u = MobileMoneyViewHelper.getSessionUser();
				
				// Execution de la reconciliation
				MobileMoneyViewHelper.appManager.executerReconciliation(tmp, u.getLogin() + " : " + u.getName());
				
				// Suppression des transactions reconciliees de la liste
				for(int i=list.size()-1; i>=0; i--) if(list.get(i).isSelected()) list.remove(i);
				
				// Msg de confirmation
				PortalInformationHelper.showInformationDialog("Les Transactions sélectionnées ont été ré-exécutées avec succès!", InformationDialog.DIALOG_INFORMATION);
			}
			
		} catch(MoMoException ex) {
			
			// Traitement de m'exception
			PortalExceptionHelper.threatException(ex);
			
		} catch(Exception ex) {
			
			// Traitement de m'exception
			PortalExceptionHelper.threatException(ex);
		}
		
		num = 1;
	}
	
	
	/**
	 * @return the trans
	 */
	public USSDTransaction getTrans() {
		return trans;
	}

	/**
	 * @param trans the trans to set
	 */
	public void setTrans(USSDTransaction trans) {
		this.trans = trans;
	}

	/**
	 * @return the selectedTransId
	 */
	public String getSelectedTransId() {
		return selectedTransId;
	}

	/**
	 * @param selectedTransId the selectedTransId to set
	 */
	public void setSelectedTransId(String selectedTransId) {
		this.selectedTransId = selectedTransId;
	}

	/**
	 * @return the list
	 */
	public List<USSDTransaction> getList() {
		return list;
	}

	/**
	 * @return the txtDateDeb
	 */
	public String getTxtDateDeb() {
		return txtDateDeb;
	}

	/**
	 * @param txtDateDeb the txtDateDeb to set
	 */
	public void setTxtDateDeb(String txtDateDeb) {
		this.txtDateDeb = txtDateDeb;
	}

	/**
	 * @return the txtDateFin
	 */
	public String getTxtDateFin() {
		return txtDateFin;
	}

	/**
	 * @param txtDateFin the txtDateFin to set
	 */
	public void setTxtDateFin(String txtDateFin) {
		this.txtDateFin = txtDateFin;
	}

	/**
	 * @return the searchPhone
	 */
	public String getSearchPhone() {
		return searchPhone;
	}

	/**
	 * @param searchPhone the searchPhone to set
	 */
	public void setSearchPhone(String searchPhone) {
		this.searchPhone = searchPhone;
	}


	/**
	 * @return the typOpItem
	 */
	public List<SelectItem> getTypOpItem() {
		return typOpItem;
	}

	/**
	 * @return the typStatusItem
	 */
	public List<SelectItem> getTypStatusItem() {
		return typStatusItem;
	}

	/**
	 * @return the typProcessItem
	 */
	public List<SelectItem> getTypProcessItem() {
		return typProcessItem;
	}

	/**
	 * @return the selectedTypOp
	 */
	public String getSelectedTypOp() {
		return selectedTypOp;
	}

	/**
	 * @param selectedTypOp the selectedTypOp to set
	 */
	public void setSelectedTypOp(String selectedTypOp) {
		this.selectedTypOp = selectedTypOp;
	}

	/**
	 * @return the selectedTypStatus
	 */
	public String getSelectedTypStatus() {
		return selectedTypStatus;
	}

	/**
	 * @param selectedTypStatus the selectedTypStatus to set
	 */
	public void setSelectedTypStatus(String selectedTypStatus) {
		this.selectedTypStatus = selectedTypStatus;
	}

	/**
	 * @return the selectedTypProcess
	 */
	public String getSelectedTypProcess() {
		return selectedTypProcess;
	}

	/**
	 * @param selectedTypProcess the selectedTypProcess to set
	 */
	public void setSelectedTypProcess(String selectedTypProcess) {
		this.selectedTypProcess = selectedTypProcess;
	}
	
	public int getNum() {
		return num++;
	}
}
