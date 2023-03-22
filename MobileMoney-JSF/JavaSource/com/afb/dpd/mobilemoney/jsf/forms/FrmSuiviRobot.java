/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.forms;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyTools;
import com.afb.dpd.mobilemoney.worker.AbonnementWorker;
import com.afb.dpd.mobilemoney.worker.ResiliationWorker;
import com.afb.dpd.mobilemoney.worker.TransactionWorker;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.Resiliation;
import com.afb.dpd.mobilemoney.jpa.entities.TraceRobot;
import com.afb.dpd.mobilemoney.jpa.tools.MoMoHelper;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.yashiro.persistence.utils.dao.tools.OrderContainer;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

/**
 * FrmSuiviRobot
 * 
 * @author AFB
 * @version 1.0
 */
public class FrmSuiviRobot extends AbstractPortalForm {

	private List<TraceRobot> listTraces = new ArrayList<TraceRobot>();

	private Parameters params;

	private String txtDateDeb = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date()), txtDateFin = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
	private String executionRobot;


	private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH':'mm':'ss");

	private boolean fileIsGenerated = false, robotLancer = false;

	private String exportFileName = "ListeTraceRobots.xls", lastExecution;

	private int num = 1;

	/**
	 * Default Constructor
	 */
	public FrmSuiviRobot(){}

	
	/*
	 * (non-Javadoc)
	 * @see cMobileMoney.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		// TODO Auto-generated method stub
		super.initForm();  
		
		num = 1;
		
		// Lecture des parametres
		params = MobileMoneyViewHelper.appManager.findParameters();
		
		txtDateDeb = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
		txtDateFin = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
		
		executionRobot = params.getExecutionRobot();
		
		lastExecution = "Date dernière exécution du robot : " + MobileMoneyViewHelper.appManager.lastExecutionRobot();

		if("ON".equals(params.getLancementRobot())) robotLancer = true; else robotLancer = false;

	}
	

	/*
	 * (non-Javadoc)
	 * @see cMobileMoney.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub Consulter les transactions Orange Mobile Money
		return "Suivi des traces du robot de réconciliation";
	}



	/*
	 * (non-Javadoc)
	 * @see cMobileMoney.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose(); executionRobot = null;  num = 1; listTraces.clear(); fileIsGenerated = false;
		exportFileName = null;  txtDateDeb = null;  txtDateFin = null; params = null; lastExecution = null;
	}



	/**
	 * Recherche la liste des transactions ussd
	 */
	public void filterTraceRobots(){

		try {
			num = 1;

			if(txtDateDeb == null || txtDateFin == null){
				// Msg Info
				PortalInformationHelper.showInformationDialog("Veuillez saisir toutes les dates de recherche SVP", InformationDialog.DIALOG_INFORMATION);
				// Annulation
				return;
			}
			
			if(MoMoHelper.sdf_DATE.parse(txtDateDeb).after(MoMoHelper.sdf_DATE.parse(txtDateFin))){
				// Msg Info
				PortalInformationHelper.showInformationDialog("La date de début ne saurait être supérieure à la date de fin", InformationDialog.DIALOG_INFORMATION);
				// Annulation
				return;
			}
			
			//System.out.println("DEBUT : "+txtDateDeb);
			//System.out.println("FIN : "+txtDateFin);
			// Initialisation d'un conteneur de restrictions
			RestrictionsContainer rc = RestrictionsContainer.getInstance().add(Restrictions.between("datetimeTrace", sdf.parseObject(txtDateDeb + " 00:00:00"), sdf.parseObject(txtDateFin + " 23:59:00")));
			//System.out.println("DEBUT : "+sdf.parseObject(txtDateDeb + " 00:00:00"));
			//System.out.println("FIN : "+sdf.parseObject(txtDateFin + " 23:59:00"));
			OrderContainer orders = OrderContainer.getInstance().add(Order.desc("datetimeTrace"));
			listTraces = MobileMoneyViewHelper.appManager.filterTraceRobots(rc, orders);
			
		}catch(Exception ex){
			// Traitement de m'exception
			PortalExceptionHelper.threatException(ex);
		}

	}
	
	
	public void actualiserPage() {

		try {

			lastExecution = "Date dernière exécution du robot : " + MobileMoneyViewHelper.appManager.lastExecutionRobot();

		} catch(Exception e){

			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);

		}

	}
		
	
	public void saveExecution() {

		try {

			if(StringUtils.isBlank(executionRobot)){
				PortalInformationHelper.showInformationDialog("Veuillez choisir un élément !", InformationDialog.DIALOG_WARNING);
				return;
			}

			params.setExecutionRobot(executionRobot);

			// Enregistrement des parametres
			MobileMoneyViewHelper.appManager.saveParameters(params);
			
			params = MobileMoneyViewHelper.appManager.findParameters();
			
			if("ON".equals(executionRobot) && "ON".equals(params.getExecutionRobot())){
				if("ON".equals(params.getLancementRobot())){
					TransactionWorker.runChecking();
					ResiliationWorker.runChecking();
					robotLancer = true; 
					// Message d'information
					PortalInformationHelper.showInformationDialog("Exécution du Robot activée avec succes.", InformationDialog.DIALOG_SUCCESS);
				}
				else{
					robotLancer = false;
					// Message d'information
					PortalInformationHelper.showInformationDialog("Exécution du Robot activée avec succes. Veuillez démarrer le Robot pour réconcilier les transactions.", InformationDialog.DIALOG_SUCCESS);
				}
				
			}
			else if("OFF".equals(executionRobot) && "OFF".equals(params.getExecutionRobot())){ 
				if("ON".equals(params.getLancementRobot())){
					robotLancer = true; 
					// Message d'information
					PortalInformationHelper.showInformationDialog("Exécution du Robot activée avec succes.", InformationDialog.DIALOG_SUCCESS);
				}
				else{
					TransactionWorker.cancelChecking();
					ResiliationWorker.cancelChecking();
					robotLancer = false;
					// Message d'information
					PortalInformationHelper.showInformationDialog("Exécution du Robot désactivée avec succes.", InformationDialog.DIALOG_SUCCESS);
				}
				
			}
			else {
				// Message d'information
				PortalInformationHelper.showInformationDialog("Problème rencotré lors de l'activation/désactivation du Robot.", InformationDialog.DIALOG_SUCCESS);
			}

		} catch(Exception e){

			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);

		}

	}
	

	public void demarrerRobot() {

		try {

			TransactionWorker.runChecking();
			ResiliationWorker.runChecking();
			AbonnementWorker.runChecking();
			params.setLancementRobot("ON");

			// Enregistrement des parametres
			MobileMoneyViewHelper.appManager.saveParameters(params);
			params = MobileMoneyViewHelper.appManager.findParameters();
			
			if("ON".equals(params.getLancementRobot())){
				robotLancer = true;
				if("ON".equals(params.getExecutionRobot())){
					// Message d'information
					PortalInformationHelper.showInformationDialog("Robot démarré avec succes.", InformationDialog.DIALOG_SUCCESS);
				}
				else{
					// Message d'information
					PortalInformationHelper.showInformationDialog("Robot démarré avec succes. Veuiller activer l'exécution du suivi du Robot de réconciliation", InformationDialog.DIALOG_SUCCESS);
				}
			}
			else {
				robotLancer = false;
				// Message d'information
				PortalInformationHelper.showInformationDialog("Problème rencotré lors du démarrage du Robot.", InformationDialog.DIALOG_INFORMATION);
			}


		} catch(Exception e){

			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);

		}

	}
	

	public void arreterRobot() {

		try {

			TransactionWorker.cancelChecking();
			ResiliationWorker.cancelChecking();
			AbonnementWorker.cancelChecking();
			params.setLancementRobot("OFF");

			// Enregistrement des parametres
			MobileMoneyViewHelper.appManager.saveParameters(params);
			params = MobileMoneyViewHelper.appManager.findParameters();
			if("ON".equals(params.getLancementRobot())){
				robotLancer = true; 
				// Message d'information
				PortalInformationHelper.showInformationDialog("Problème rencotré lors de l'arrêt du Robot.", InformationDialog.DIALOG_INFORMATION);
			}
			else {
				robotLancer = false;
				// Message d'information
				PortalInformationHelper.showInformationDialog("Robot arreté avec succes.", InformationDialog.DIALOG_SUCCESS);
			}
			
		} catch(Exception e){

			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);

		}

	}
	

	public void exportListeExcell(){
		try {

			if(listTraces.isEmpty()){
				PortalInformationHelper.showInformationDialog("Aucune transaction dans la liste !",InformationDialog.DIALOG_INFORMATION);
				return;
			}

			exportFileName = "LISTE_TRANSACTIONS_"+new SimpleDateFormat("ddMMyyyyhhmmss").format(new Date())+".xlsx";
			//**** MobileMoneyViewHelper.appManager.exportTraceRobotIntoExcelFile(list,exportFileName);

			fileIsGenerated = true;

			// Message d'information
			PortalInformationHelper.showInformationDialog("Export effectue avec succes! Vous pouvez telecharger le fichier généré.", InformationDialog.DIALOG_SUCCESS);

		} catch (Exception ex) {
			PortalExceptionHelper.threatException(ex);
			fileIsGenerated = false;
		}
	}


	public boolean isExportedFileExist() {
		return new File( MobileMoneyTools.getDownloadDir() + exportFileName).exists() && fileIsGenerated;
	}
	

	public SimpleDateFormat getSdf() {
		return sdf;
	}

	public void setSdf(SimpleDateFormat sdf) {
		this.sdf = sdf;
	}

	public void setNum(int num) {
		this.num = num;
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


	public int getNum() {
		return num++;
	}


	public String getExportFileName() {
		return exportFileName;
	}

	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}

	public boolean isFileIsGenerated() {
		return fileIsGenerated;
	}

	public void setFileIsGenerated(boolean fileIsGenerated) {
		this.fileIsGenerated = fileIsGenerated;
	}

	public String getExecutionRobot() {
		return executionRobot;
	}

	public void setExecutionRobot(String executionRobot) {
		this.executionRobot = executionRobot;
	}

	public List<TraceRobot> getListTraces() {
		return listTraces;
	}

	public void setListTraces(List<TraceRobot> listTraces) {
		this.listTraces = listTraces;
	}

	/**
	 * @return the params
	 */
	public Parameters getParams() {
		return params;
	}

	public boolean isRobotLancer() {
		return robotLancer;
	}

	public void setRobotLancer(boolean robotLancer) {
		this.robotLancer = robotLancer;
	}

	public String getLastExecution() {
		return lastExecution;
	}

	public void setLastExecution(String lastExecution) {
		this.lastExecution = lastExecution;
	}



}
