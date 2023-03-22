/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.forms;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

import com.afb.dpd.mobilemoney.jpa.entities.FactMonth;
import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.enums.StatutService;
import com.afb.dpd.mobilemoney.jpa.enums.TransactionStatus;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jpa.tools.CSftp;
import com.afb.dpd.mobilemoney.jpa.tools.Doublon;
import com.afb.dpd.mobilemoney.jpa.tools.Equilibre;
import com.afb.dpd.mobilemoney.jpa.tools.EquilibreComptes;
import com.afb.dpd.mobilemoney.jpa.tools.MoMoHelper;
import com.afb.dpd.mobilemoney.jpa.tools.bkmvti;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.models.ReportViewerDialog;
import com.afb.dpd.mobilemoney.jsf.servlet.WebResourceManager;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyTools;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.afb.dpd.mobilemoney.worker.TransactionWorker;
import com.yashiro.persistence.utils.dao.tools.OrderContainer;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

/**
 * Formulaire de Consultation des transactions
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class FrmTransactions extends AbstractPortalForm {
	
	private static Log logger = LogFactory.getLog(FrmTransactions.class);
	
	/**
	 * Liste des transactions
	 */
	List<Transaction> transactions = new ArrayList<Transaction>();
	
	/**
	 * Numeroteur de lignes
	 */
	private int num = 0, numEC = 0;

	/**
	 * Periode de dates saisie
	 */
	private String txtDateDeb = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
	private String txtDateFin = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
	
	private String rapportCompenseFileName = "Rapport_Compensation_MAC_MTN-" + new SimpleDateFormat("ddMMyy-HHmm").format(new Date()) + ".pdf";
		
	/**
	 * Operation selectionnee
	 */
	private TypeOperation txtSearchOp = null;
	
	private Boolean txtSearchPosted;
	
	/**
	 * Periode d'heures saisie
	 */
	private String txtHeureDeb = "00";
	private String txtHeureFin = "01";
	
	/**
	 * Statut recherche
	 */
	private TransactionStatus txtSearchStatus = null;
	
	/**
	 * periode de generation de rapport de controle des reservations
	 */
	private String periode = MoMoHelper.PERIODE_DAY;
	private Boolean heures = Boolean.FALSE;

	/**
	 * Items de types d'operations
	 */
	private List<SelectItem> opItems = new ArrayList<SelectItem>();
	
	private List<SelectItem> postedItems = new ArrayList<SelectItem>();
	
	private List<SelectItem> periodeItems = new ArrayList<SelectItem>();
	
	/**
	 * Items des statuts de transactions
	 */
	private List<SelectItem> statutItems = new ArrayList<SelectItem>();
	
	/**
	 * Transaction selectionnee dans la liste
	 */
	private Transaction selectedTransaction;
	
	List<bkmvti> ecritures = new ArrayList<bkmvti>();
	List<Equilibre> equilibre = new ArrayList<Equilibre>();
	List<Doublon> doublon = new ArrayList<Doublon>();
	List<EquilibreComptes> equilibreCptes = new ArrayList<EquilibreComptes>();
	
	/**
	 * Fichier d'export des EC
	 */
//	private String exportFileName = "EC-MoMo-" + new SimpleDateFormat("ddMMyy-HHmm").format(new Date()) + ".unl";
	private String exportFileName = "EC_MoMo_Comp.unl";
	private String ecFileNameRapp = "EC-Rapprochement-MoMo-" + new SimpleDateFormat("ddMMyy-HHmm").format(new Date()) + ".xls";
	private String controleReservationsFileName = "";
	
	/**
	 * Marqueur determinant si on a lance la generation du fichier des ecritures comptables
	 */
	private boolean fileIsGenerated = false;

	private Date dateRapport = new Date();
	
	private String searchName, searchPhone;
	
	private Parameters param;
	
	private Boolean doSearch;
	private Boolean visualiser;
	private Boolean postFile;
	
	/**
	 * Default Constructor
	 */
	public FrmTransactions() {}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		// TODO Auto-generated method stub
		super.initForm();

		dateRapport = new Date();
		// Chargement des items de types d'operations
		opItems.add( new SelectItem(null, "(Toutes)") );
		for(TypeOperation to : TypeOperation.getValues()) opItems.add( new SelectItem(to, to.getValue()) );

		// Chargement des items de statut
		statutItems.add( new SelectItem(null, " ") );
		for(TransactionStatus to : TransactionStatus.getValues()) statutItems.add( new SelectItem(to, to.getValue()) );
		
		// Chargement des items de Postage
		postedItems.add( new SelectItem(null, " ") );
		postedItems.add( new SelectItem(Boolean.TRUE, "Postées") );
		postedItems.add( new SelectItem(Boolean.FALSE, "Non postées") );
		
		// Chargement des items de periodes
		periodeItems.add( new SelectItem(MoMoHelper.PERIODE_DAY, "Journée") );
		periodeItems.add( new SelectItem(MoMoHelper.PERIODE_HOUR, "Heures") );
		
		doSearch = Boolean.TRUE;
		visualiser = Boolean.FALSE;
		postFile = Boolean.FALSE;
		MobileMoneyViewHelper.appManager.initialisations();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Consultation des transactions de Pull/Push from account";
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose();
		transactions = new ArrayList<Transaction>(); 
		opItems.clear(); statutItems.clear(); txtSearchOp = null; txtSearchStatus = null; periodeItems.clear();
		selectedTransaction = null; fileIsGenerated = false; closeEC(); postedItems.clear(); equilibre.clear(); doublon.clear();
		equilibreCptes = new ArrayList<EquilibreComptes>();
		if(isExportedFileExist()) new File( MobileMoneyTools.getDownloadDir() + File.separator + exportFileName).delete();
		
	}
	
	/**
	 * Methode de recherche des transactions sur les criteres saisis
	 */
	public void filterTransactions(){

		try {
			
			// Initialisation d'un conteneur de restrictions
			RestrictionsContainer rc = RestrictionsContainer.getInstance();
			
			// Ajout de la restriction sur la periode de date
			if(txtDateDeb != null && txtDateFin != null && !txtDateDeb.isEmpty() && !txtDateFin.isEmpty()) rc.add(Restrictions.between("date", MoMoHelper.sdf.parse(txtDateDeb.concat(" 00:01") ), MoMoHelper.sdf.parse(txtDateFin.concat(" 23:58") ) ));
			
			// Ajout de la restriction sur l'operation
			if(txtSearchOp != null) rc.add(Restrictions.eq("typeOperation", txtSearchOp )); else rc.add(Restrictions.not(Restrictions.in("typeOperation", new Object[]{TypeOperation.COMPTABILISATION})));// new Object[]{TypeOperation.BALANCE, TypeOperation.COMPTABILISATION})

			// Ajout de la restriction sur l'etat de la transaction
			if(txtSearchStatus != null) rc.add(Restrictions.eq("status", txtSearchStatus ));

			// Ajout de la restriction sur l'etat de postage dans Delta
			if(txtSearchPosted != null) rc.add(Restrictions.eq("posted", txtSearchPosted ));

			// Ajout de la restriction sur le n° de telephone
			if(searchPhone != null && !searchPhone.isEmpty()) rc.add(Restrictions.like("phoneNumber", "%" + searchPhone + "%" ));

			// Ajout de la restriction sur le nom du client
			if(searchName != null && !searchName.isEmpty()) rc.add(Restrictions.like("subscriber.customerName", "%" + searchName.toUpperCase() + "%" ));
			
			// Initialisation d'un conteneur d'ordres
			OrderContainer orders = OrderContainer.getInstance().add(Order.desc("date")) ;
			
			// Filtre des transactions
			transactions = MobileMoneyViewHelper.appManager.filterTransactions(rc, orders); 
			
			// Initialisation du compteur
			num = 1;
			
		} catch(Exception e) {
			
			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
		}
		
	}
	
	
	/**
	 * Generer le rapport de controle des reservations
	 */
	public void genererRapportControleReservations(){
		try {
			logger.info("PERIODE : "+periode);
			// 
			if(periode != null && periode.equals(MoMoHelper.PERIODE_DAY)){
				logger.info("CONTROLE JOURNALIER");
				controleReservationsFileName = "Rapport-Controle-Reservations-MoMo-" + new SimpleDateFormat("ddMMyy-HHmm").format(new Date()) + ".xls";
				MobileMoneyViewHelper.appManager.controleReservations(Boolean.TRUE, null, null, controleReservationsFileName);
			}
			else if(txtHeureDeb != null && txtHeureFin != null && !txtHeureDeb.isEmpty() && !txtHeureFin.isEmpty() && periode != null && periode.equals(MoMoHelper.PERIODE_HOUR)){
				logger.info("CONTROLE HORAIRE");
				if(Double.valueOf(txtHeureDeb)>23 || Double.valueOf(txtHeureFin)>23 || Double.valueOf(txtHeureDeb)>Double.valueOf(txtHeureFin)) {
					PortalInformationHelper.showInformationDialog("Periode horaire incorrecte !",InformationDialog.DIALOG_INFORMATION);
					return;
				}
				controleReservationsFileName = "Rapport-Controle-Reservations-MoMo-" + new SimpleDateFormat("ddMMyy-HHmm").format(new Date()) +"-"+txtHeureDeb+"h-"+txtHeureFin+"h.xls";
				MobileMoneyViewHelper.appManager.controleReservations(Boolean.FALSE, txtHeureDeb, txtHeureFin, controleReservationsFileName);
			}
			
			// Message d'information
			PortalInformationHelper.showInformationDialog("Rapport de contrôle des reservations généré avec succès", InformationDialog.DIALOG_SUCCESS);
			fileIsGenerated = true;
		} catch(Exception e) {
			
			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
			fileIsGenerated = false;
		}
	}
	
	
	
	/**
	 * Charger les transactions de compensation
	 */
	public void chargerTrxCompensation(){
		
		try {
			logger.info("Chargement des transactions de compensation");
			transactions = new ArrayList<Transaction>();
			
			Date d1 = new Date();
			transactions = MobileMoneyViewHelper.appManager.getTransactionCompensation(null, null, MobileMoneyViewHelper.getSessionUser().getLogin(), null);
			Date d2 = new Date();
			logger.info("Facturation en."+((d2.getTime()-d1.getTime())/1000)+" sec");
			
			
			if(transactions.isEmpty()){
				PortalInformationHelper.showInformationDialog("Aucune transaction PULL/PUSH à compenser! Veuillez vérifier que le robot fonctionne correctement.", InformationDialog.DIALOG_INFORMATION);
				return;
			}
			logger.info("TRX : "+transactions.size());
			
			// Verifier la reconciliation des transactions a compenser ???
//			verifierStatusTrxCompensation();
			
			// Initialisation du compteur
			num = 1;
			logger.info("Extraction des EC de compensation");
			// Extraction des EC de compensation
			extraireECCompensation();
			logger.info("Rapprochement auto des EC");
			// Rapprochement auto des EC
			checkEC();
								
			// Message d'information
			//PortalInformationHelper.showInformationDialog("Chargement  des transactions PULL/PUSH à compenser effectué avec succès!", InformationDialog.DIALOG_SUCCESS);
			
		} catch(Exception e) {
			
			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	
	/**
	 * verifier la reconciliation des transactions (status cote MTN) avant compensation
	 */
	public void verifierStatusTrxCompensation(){
		
		TransactionWorker.reProcess(transactions);		
	}
	
	
	/**
	 * Consulter les ecritures comptables des transactions a compenser
	 */
	public void consulterECTransactions() {
		
		try {
			
			// Extraction des ecritures comptables des transactions
			ecritures = MobileMoneyViewHelper.appManager.getECFromTransactions(transactions, false);
			
			numEC = 1;
			
		} catch(Exception e) {
			
			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
		}
		
	}
	
	
	/**
	 * Etrait les ecritures comptables des transactions a compenser dans un fichier
	 */
	public void extraireECCompensation() {
		
		try {
			logger.info("Extraction des EC trx ");
			// Chargement des ecritures comptables des transactions
			ecritures = MobileMoneyViewHelper.appManager.getECFromTransactions(transactions, false);
			
			// Ajout des ecritures comptables de compensation
			ecritures.addAll(MobileMoneyViewHelper.appManager.genererECCompensation(transactions));
			logger.info("Extraction des EC de compensation "+ecritures.size());
			
			if(ecritures.isEmpty() || ecritures.size() == 0){
				PortalInformationHelper.showInformationDialog("Aucune transaction à comptabiliser !", InformationDialog.DIALOG_INFORMATION);
				return;
			}
						
		} catch(Exception e) {
			
			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
			fileIsGenerated = false;
		}
		
	}
	
	
	//
	/**
	* Methode de rapprochement des EC
	*/
	@SuppressWarnings("rawtypes")
	public void checkEC(){

		// Initialisation du compteur
		num = 1;

		param = MobileMoneyViewHelper.appManager.findParameters();
		
		// Recherche des transactions a compenser
		try{
			
			HashMap<Object, Object> mapTrx = new HashMap<Object, Object>();
			List<Transaction> transactionSimu = new ArrayList<Transaction>();
			List<Transaction> transUnijamb = new ArrayList<Transaction>();
			
			// Construction des MAPs d'ecritures
			HashMap<Object, Object> mapMTN = new HashMap<Object, Object>(); // Map DAP/COM/TAX
			HashMap<Object, Object> mapCli = new HashMap<Object, Object>();
			HashMap<Object, Object> mapLiaison = new HashMap<Object, Object>();
			List<bkmvti> ecrituresMTN = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresCli = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresLiaison = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresMTNRapp = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresCliRapp = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresLiaisonRapp = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresMTNUnijamb = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresCliUnijamb = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresLiaisonUnijamb = new ArrayList<bkmvti>();
			
			// Pour les EC similaires (2 transactions a la meme minute)
			HashMap<Object, Object> mapMTN2 = new HashMap<Object, Object>(); // Map DAP/COM/TAX
			HashMap<Object, Object> mapCli2 = new HashMap<Object, Object>();
			HashMap<Object, Object> mapLiaison2 = new HashMap<Object, Object>();
			
			List<bkmvti> ecrituresCompens = new ArrayList<bkmvti>();
			
			Double totalPull = 0d;
			Double totalPush = 0d;
			Double totalCompensPull = 0d;
			Double totalCompensPush = 0d;
			Double totalFrais = 0d;
			Double totalCom = 0d;
			Double totalTax = 0d;
			
			Double mtUnijambPull = 0d;
			Double mtUnijambPush = 0d;
			Double mtUnijambPullMarch = 0d;
			Double mtUnijambPushMarch = 0d;
			
			String tel;
			String date;
			String typeOpe;
			String cle;
			
			HashMap<String, Double> montants = new HashMap<String, Double>();
			
			for(Transaction tx : transactions) {
				tel = tx.getPhoneNumber().substring(3);
			    date = new SimpleDateFormat("ddMMyyHHmmss").format(tx.getDate());
				typeOpe = tx.getTypeOperation().toString().trim();
				
				cle  = typeOpe.contains(TypeOperation.SUBSCRIPTION.toString()) ? tel + date.trim() + typeOpe.trim() : tel + date.trim() + Integer.toString(tx.getAmount().intValue()) + typeOpe.trim();
//				logger.info("CLE / VALEUR : "+cle+" / "+typeOpe);
				if(Integer.toString(tx.getAmount().intValue()).equalsIgnoreCase("2000")) logger.info("CLE TRX: "+cle);
				transactionSimu.add(tx);		
				mapTrx.put(cle, tx);
			}
			
			// Similuation de l'ajout des ecritures unijambistes
			// A enlever lors du passage en production
		//	bkmvti e1 = ecritures.get(1);
		//	bkmvti e2 = ecritures.get(2);
			logger.info("****************************** : " + ecritures.size());
			
			
			for(bkmvti e : ecritures) {
				// MAJ du code de l'utilisateur qui traite
				
				e.setUti(MobileMoneyViewHelper.getSessionUser().getLogin());
				
				try {
					tel = e.getLib().trim().split("/")[2];
					date = e.getLib().trim().split("/")[1];
//					if(date.length()>10) date = date.substring(0, 10);
				    typeOpe = e.getLib().trim().split("/")[0];
				}
				catch(Exception ex) {
					logger.info("[FrmTransactions] ecritures Libelle: " + e.getLib());
					continue;
				}
				
				
				cle  = typeOpe.contains("MAC") ? tel + date.trim() + typeOpe.trim() : tel + date.trim() + Integer.toString(e.getMon().intValue()) + typeOpe.trim();
				e.setCleLettrage(cle);
				e.setTypeOperation(typeOpe);

				// EC impactant le compte DAP PULL/PUSH
//				if((e.getNcp().equals(param.getNcpDAPPull().trim().split("-")[1]) || e.getNcp().equals(param.getNcpDAPPush().trim().split("-")[1])) && (typeOpe.equals(TypeOperation.PULL.toString()) || (typeOpe.equals(TypeOperation.PUSH.toString())))) {
				if(e.getNcp().startsWith("38") && (typeOpe.equals(TypeOperation.PULL.toString()) || (typeOpe.equals(TypeOperation.PUSH.toString())))) {
					if(!mapMTN.containsKey(cle+e.getSen())) {
						mapMTN.put(cle+e.getSen(), e);	
					}
					else if(!mapMTN2.containsKey(cle+e.getSen())) {
						mapMTN2.put(cle+e.getSen(), e);	
					}
					ecrituresMTN.add(e);
					
				}
				// EC PULL/PUSH impactant le compte du client
				else if(e.getNcp().trim().substring(0, 1).equals("0") && !e.getNcp().equals(param.getNumCompteMTN().trim().split("-")[1]) && (typeOpe.equals(TypeOperation.PULL.toString()) || (typeOpe.equals(TypeOperation.PUSH.toString())))) {
					if(!mapCli.containsKey(cle+e.getSen())) {
						mapCli.put(cle+e.getSen(), e);	
					}
					else if(!mapCli2.containsKey(cle+e.getSen())) {
						mapCli2.put(cle+e.getSen(), e);	
					}
					ecrituresCli.add(e);
				}
				// EC PULL/PUSH impactant le compte MTN (COMPENS)
				else if(e.getNcp().equals(param.getNumCompteMTN().trim().split("-")[1])) {
					// Total PULL/PUSH COMPENS
					if(e.getLib().contains("COMPENS/PULL")) {
						logger.info("MONTANT COMPENS/PULL :  "+e.getMon());
						totalCompensPull = totalCompensPull + e.getMon();
						ecrituresCompens.add(e);
					}
					if(e.getLib().contains("COMPENS/PUSH")) {
						logger.info("MONTANT COMPENS/PUSH :  "+e.getMon());
						totalCompensPush = totalCompensPush + e.getMon();
						ecrituresCompens.add(e);
					}
					if(e.getLib().contains("COMPENS MARCH/PULL")) {
						logger.info("MONTANT COMPENS MARCH/PULL :  "+e.getMon());
						totalCompensPull = totalCompensPull + e.getMon();
						ecrituresCompens.add(e);
					}
					if(e.getLib().contains("COMPENS MARCH/PUSH")) {
						logger.info("MONTANT COMPENS MARCH/PUSH :  "+e.getMon());
						totalCompensPush = totalCompensPush + e.getMon();
						ecrituresCompens.add(e);
					}
				}
				
				// EC SOUSCRIPTION impactant le compte de commission
				else if(e.getNcp().equals(param.getNumCompteCommissions().trim().split("-")[1]) && (typeOpe.contains("FRAIS") || typeOpe.contains("COM"))) {
					logger.info(" COM ");
					if(!mapMTN.containsKey(cle.replace("FRAIS", "COM")+e.getSen())) {
						mapMTN.put(cle.replace("FRAIS", "COM")+e.getSen(), e);	
						logger.info("MAP MTN COM KEY :  "+cle+e.getSen());
						totalCom = totalCom + e.getMon();
					}
					ecrituresMTN.add(e);
					
				}
				// EC SOUSCRIPTION impactant le compte de tax
				else if(e.getNcp().equals(param.getNumCompteTVA().trim().split("-")[1]) && typeOpe.contains("TAX")) {
					logger.info(" TAX ");
					if(!mapMTN.containsKey(cle+e.getSen())) {
						mapMTN.put(cle+e.getSen(), e);	
						logger.info("MAP MTN TAX KEY :  "+cle+e.getSen());
						totalTax = totalTax + e.getMon();
					};
					ecrituresMTN.add(e);
					
				}
				// EC SOUSCRIPTION impactant le compte du client
				else if(e.getNcp().trim().substring(0, 1).equals("0") && !e.getNcp().equals(param.getNumCompteMTN().trim().split("-")[1]) && typeOpe.contains("FRAIS") ) {
					logger.info(" FRAIS ");
					if(!mapCli.containsKey(cle+e.getSen())) {
						mapCli.put(cle+e.getSen(), e);	
						logger.info("MAP CLT FRAIS KEY :  "+cle+e.getSen());
					}
					ecrituresCli.add(e);
				}
				// EC impactant les comptes de liaison
				else if(e.getNcp().equals(param.getNumCompteLiaison().trim())) {
					//logger.info("COMPTE DE LIAISON: "+e.getNcp());
					if(!mapLiaison.containsKey(cle+e.getSen())) {
						mapLiaison.put(cle+e.getSen(), e);	
					}
					else if(!mapLiaison2.containsKey(cle+e.getSen())) {
						mapLiaison2.put(cle+e.getSen(), e);	
					}
					ecrituresLiaison.add(e);
				}

			}
			
			logger.info("*************** ecritures.size() sans ajout *************** : " + ecritures.size());
			if(param.getEtatServiceSDP().equals(StatutService.TEST)){
				bkmvti e3 = ecrituresCli.get(1);
				logger.info("EC CLIENT : "+e3.getLib());
				logger.info("ecrituresMTN : "+ecrituresMTN.size());
				bkmvti e4 = ecrituresMTN.get(4);
				
				logger.info("EC DAP : "+e4.getLib());
				mapCli.put(e3.getCleLettrage()+e3.getSen(), e3);
				mapMTN.put(e4.getCleLettrage()+e4.getSen(), e4);
//				ecritures.add( new bkmvti("00001", "001", e1.getCha(), "04942231051", e1.getSuf(), "", MoMoHelper.padText(String.valueOf(0001), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, "AUTO", "", e1.getClc(), new Date(), null, e1.getDva(), 18500d, "C", "PUSH/2703191600/695000124", "N", "XXXXXX", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, e1.getAge(), e1.getDev(), 18500d, null, null, null, null, null, null, null, null, e1.getNat(), "VA", null, null) ); 
//				ecritures.add( new bkmvti("00001", "001", e2.getCha(), "04996661003", e2.getSuf(), "", MoMoHelper.padText(String.valueOf(0002), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, "AUTO", "", e2.getClc(), new Date(), null, e2.getDva(), 19500d, "D", "PULL/2703191600/699020154", "N", "XXXXXX", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, e2.getAge(), e2.getDev(), 19500d, null, null, null, null, null, null, null, null, e2.getNat(), "VA", null, null) ); 
			}
			
			logger.info("*************** ecritures.size() avec ajout *************** : " + ecritures.size());
						
			montants.put("TOTAL_COM", totalCom);
			montants.put("TOTAL_TAX", totalTax);
			montants.put("TOTAL_COMTAX", totalCom + totalTax);
			
			logger.info("*************** ecrituresCli.size() avant *************** : " + ecrituresCli.size());
			logger.info("*************** ecrituresMTN.size() avant *************** : " + ecrituresMTN.size());
			logger.info("*************** ecrituresLiaison.size() avant *************** : " + ecrituresLiaison.size());
			logger.info("*************** mapCli.size() avant *************** : " + mapCli.size());
			logger.info("*************** mapMTN.size() avant *************** : " + mapMTN.size());
			logger.info("*************** mapLiaison.size() avant *************** : " + mapLiaison.size());
			
			//Rapprochement des ecritures
			logger.info("\n*************** Rapprochement des ecritures ***************\n");
			for(bkmvti e : ecrituresCli) {
				// Operations PULL/PUSH
				if(e.getTypeOperation().equals(TypeOperation.PULL.toString()) || e.getTypeOperation().equals(TypeOperation.PUSH.toString())){
					logger.info("SEARCH MTN KEY :  "+e.getCleLettrage() );
					if(mapMTN.containsKey(e.getCleLettrage() + "C" ) && mapMTN.containsKey(e.getCleLettrage() + "D" )) {
						// Total PULL/PUSH client
						if(e.getLib().contains("PULL")) totalPull = totalPull + e.getMon();
						if(e.getLib().contains("PUSH")) totalPush = totalPush + e.getMon();
						// Marquer l'EC compte Client comme rapprochee
						ecrituresCliRapp.add(e);
						// Marquer les EC comptes DAP comme rapprochee
						ecrituresMTNRapp.add((bkmvti) mapMTN.get(e.getCleLettrage() + "C" ));
						ecrituresMTNRapp.add((bkmvti) mapMTN.get(e.getCleLettrage() + "D" ));	
						// Retirer les EC de la map DAP
						mapMTN.remove(e.getCleLettrage() + "C" );
						mapMTN.remove(e.getCleLettrage() + "D" );
						// Retirer l'EC de la map Client
						mapCli.remove(e.getCleLettrage()+e.getSen());
						// Retirer les EC de la map Liaison
						mapLiaison.remove(e.getCleLettrage() + "C" );
						mapLiaison.remove(e.getCleLettrage() + "D" );
					}
					// Operations PULL/PUSH unijambistes
					else{
						
						logger.info("ELSE UNI : "+e.getTypeOperation());
						
						// Retrait de la transaction sur les compenses a traiter
						if(mapCli.containsKey(e.getCleLettrage() + "C") || mapCli.containsKey(e.getCleLettrage() + "D")){
							logger.info("TRX PULL/PUSH UNIJAMBISTE : "+e.getCleLettrage()); //e.getCleLettrage());
							Transaction trx = (Transaction) mapTrx.get(e.getCleLettrage());
							if(trx!=null){
								transUnijamb.add(trx);
								transactions.remove(trx);
								// Retrait du montant de la transaction sur le montant des compenses
								if(e.getLib().contains("PULL")) {
									totalCompensPull = totalCompensPull - e.getMon();
									// verifier s'il s'agit d'un marchand
									mtUnijambPull += !trx.getSubscriber().isMerchant() && trx.getTypeOperation().equals(TypeOperation.PULL) ? trx.getAmount() : 0d;
									mtUnijambPullMarch += trx.getSubscriber().isMerchant() && trx.getTypeOperation().equals(TypeOperation.PULL) ? trx.getAmount() : 0d;
								}
								if(e.getLib().contains("PUSH")) {
									totalCompensPush = totalCompensPush - e.getMon();
									// verifier s'il s'agit d'un marchand
									mtUnijambPush += !trx.getSubscriber().isMerchant() && trx.getTypeOperation().equals(TypeOperation.PUSH) ? trx.getAmount() : 0d;
									mtUnijambPushMarch += trx.getSubscriber().isMerchant() && trx.getTypeOperation().equals(TypeOperation.PUSH) ? trx.getAmount() : 0d;
								}
							}
							else{
								logger.info("TRX NULL : ");
							}
							
						}
						else{
							// Trx executee la meme minute qu'une autre
							logger.info("REGULARISATION : "+e.getTypeOperation());
							// Total PULL/PUSH client
							if(e.getLib().contains("PULL")) totalPull = totalPull + e.getMon();
							if(e.getLib().contains("PUSH")) totalPush = totalPush + e.getMon();
							// Marquer l'EC compte Client comme rapprochee
							ecrituresCliRapp.add(e);
							// Marquer les EC comptes DAP comme rapprochee
							ecrituresMTNRapp.add((bkmvti) mapMTN2.get(e.getCleLettrage() + "C" ));
							ecrituresMTNRapp.add((bkmvti) mapMTN2.get(e.getCleLettrage() + "D" ));	
							// Retirer les EC de la map DAP
							mapMTN2.remove(e.getCleLettrage() + "C" );
							mapMTN2.remove(e.getCleLettrage() + "D" );
							// Retirer l'EC de la map Client
							mapCli2.remove(e.getCleLettrage()+e.getSen());
							// Retirer les EC de la map Liaison
							mapLiaison2.remove(e.getCleLettrage() + "C" );
							mapLiaison2.remove(e.getCleLettrage() + "D" );
						}
					}
					
				}
				// Operations de souscription
				else if(e.getTypeOperation().contains("FRAIS")){
					
					logger.info("ELSE FRAIS : "+e.getTypeOperation());
					
					if((mapMTN.containsKey(e.getCleLettrage().replace("FRAIS", "COM") + "C" ) && mapMTN.containsKey(e.getCleLettrage().replace("FRAIS", "TAX") + "C" ))) {
						
						// Total PULL/PUSH client
						if(e.getLib().contains("FRAIS")) totalFrais = totalFrais + e.getMon();
						// Marquer l'EC compte Client comme rapprochee
						ecrituresCliRapp.add(e);
						// Marquer les EC comptes COM et TAX comme rapprochee
						ecrituresMTNRapp.add((bkmvti) mapMTN.get(e.getCleLettrage().replace("FRAIS", "COM") + "C" ));
						ecrituresMTNRapp.add((bkmvti) mapMTN.get(e.getCleLettrage().replace("FRAIS", "TAX") + "C" ));	
						// Retirer les EC de la map COM/TAX
						mapMTN.remove(e.getCleLettrage().replace("FRAIS", "COM") + "C" );
						mapMTN.remove(e.getCleLettrage().replace("FRAIS", "TAX") + "C" );
						// Retirer l'EC de la map Client
						mapCli.remove(e.getCleLettrage()+e.getSen());
						// Retirer les EC de la map Liaison
					//	mapLiaison.remove(e.getCleLettrage() + "C" );
					//	mapLiaison.remove(e.getCleLettrage() + "D" );

						mapLiaison.remove(e.getCleLettrage().replace("FRAIS", "COM") + "C" );
						mapLiaison.remove(e.getCleLettrage().replace("FRAIS", "COM") + "D" );
						mapLiaison.remove(e.getCleLettrage().replace("FRAIS", "TAX") + "D" );
						mapLiaison.remove(e.getCleLettrage().replace("FRAIS", "TAX") + "C" );
					}
					// Operations unijambistes
					else{
						// Retrait de la transaction sur les compenses a traiter
						if(mapCli.containsKey(e.getCleLettrage() + "D" )){

							logger.info("TRX SOUSCRIPTION UNIJAMBISTE : "+e.getCleLettrage());
							Transaction trx = (Transaction) mapTrx.get(e.getCleLettrage().replaceAll("FRAIS MAC", TypeOperation.SUBSCRIPTION.toString()) );
							if(trx!=null){
								transUnijamb.add(trx);
								transactions.remove(trx);
							}
							
						}
					}
					
				}
				// Operations unijambistes
				else{
					logger.info("ELSE FINAL : "+e.getTypeOperation());
				}

			}
			
			montants.put("TOTAL_FRAIS", totalFrais);
			
			montants.put("TOTAL_PULL", totalPull);
			montants.put("TOTAL_PUSH", totalPush);
			montants.put("TOTAL_CLIENT", totalPull + totalPush + totalFrais);

			montants.put("TOTAL_COMPENS_PULL", totalCompensPull);
			montants.put("TOTAL_COMPENS_PUSH", totalCompensPush);
			montants.put("TOTAL_COMPENS", totalCompensPull + totalCompensPush + totalCom + totalTax);
			
			logger.info("*************** ecrituresCli.size() after *************** : " + ecrituresCli.size());
			logger.info("*************** ecrituresMTN.size() after *************** : " + ecrituresMTN.size());
			logger.info("*************** mapCli.size() after *************** : " + mapCli.size());
			logger.info("*************** mapMTN.size() after *************** : " + mapMTN.size());
			logger.info("*************** mapLiaison.size() after *************** : " + mapLiaison.size());
			
			// vidage des ecritures unijambistes
			Iterator itr = mapCli.entrySet().iterator();
			while(itr.hasNext()) {
				Map.Entry mapentry = (Map.Entry) itr.next();
				ecrituresCliUnijamb.add((bkmvti) mapentry.getValue());
			}

			itr = mapMTN.entrySet().iterator();
			while(itr.hasNext()) {
				Map.Entry mapentry = (Map.Entry) itr.next();
				bkmvti ecriture = (bkmvti) mapentry.getValue();
				ecrituresMTNUnijamb.add(ecriture);
//				}
			}
			
			itr = mapLiaison.entrySet().iterator();
			while(itr.hasNext()) {
				Map.Entry mapentry = (Map.Entry) itr.next();
				ecrituresLiaisonUnijamb.add((bkmvti) mapentry.getValue());
			}
			
			// Mise a jour des EC de compensation avec les montants rapproches
			if(mtUnijambPull>0d || mtUnijambPush>0d || mtUnijambPullMarch>0d || mtUnijambPushMarch>0d){
				for(bkmvti ec : ecrituresCompens){
					if(ec.getLib().contains("COMPENS/PULL")) {
						if(mtUnijambPull>0d){
							logger.info("BEFORE REMOVE PULL : " + ec);
							ecritures.remove(ec);
							if(ec.getMon() - mtUnijambPull>0d){
								logger.info("MONTANT PULL OK ");
								ec.setMon(ec.getMon() - mtUnijambPull);
								logger.info("AFTER REMOVE PULL : " + ec);
								ecritures.add(ec);
								// Mise a jour de l'EC en BD
								MobileMoneyViewHelper.appDAOLocal.save(ec);
							}
						}
					}
					if(ec.getLib().contains("COMPENS/PUSH")){
						if(mtUnijambPush>0d){
							logger.info("BEFORE REMOVE PUSH : " + ec);
							ecritures.remove(ec);
							if(ec.getMon() - mtUnijambPush>0d){
								logger.info("MONTANT PUSH OK ");
								ec.setMon(ec.getMon() - mtUnijambPush);
								logger.info("AFTER REMOVE PUSH : " + ec);
								ecritures.add(ec);
								// Mise a jour de l'EC en BD
								MobileMoneyViewHelper.appDAOLocal.save(ec);
							}
						}
					}
					if(ec.getLib().contains("COMPENS MARCH/PULL")) {
						if(mtUnijambPullMarch>0d){
							logger.info("BEFORE REMOVE PULL MARCH : " + ec);
							ecritures.remove(ec);
							if(ec.getMon() - mtUnijambPullMarch>0d){
								logger.info("MONTANT PULL MARCH OK ");
								ec.setMon(ec.getMon() - mtUnijambPullMarch);
								logger.info("AFTER REMOVE PULL MARCH : " + ec);
								ecritures.add(ec);
								// Mise a jour de l'EC en BD
								MobileMoneyViewHelper.appDAOLocal.save(ec);
							}
						}
					}
					if(ec.getLib().contains("COMPENS MARCH/PUSH")) {
						if(mtUnijambPushMarch>0d){
							logger.info("BEFORE REMOVE PUSH MARCH : " + ec);
							ecritures.remove(ec);
							if(ec.getMon() - mtUnijambPushMarch>0d){
								logger.info("MONTANT PUSH MARCH OK ");
								ec.setMon(ec.getMon() - mtUnijambPushMarch);
								logger.info("AFTER REMOVE PUSH MARCH : " + ec);
								ecritures.add(ec);
								// Mise a jour de l'EC en BD
								MobileMoneyViewHelper.appDAOLocal.save(ec);
							}
						}
					}
				}
				
			}

			logger.info("NBRE TRX FINAL : " + transactions.size());
			logger.info("NBRE TRX UNIJAMBISTES : " + transUnijamb.size());
			
			// Export du rapprochement au format Excel
			MobileMoneyViewHelper.appManager.exportRapprochmentBkmvti(ecFileNameRapp, ecritures, ecrituresCli, ecrituresMTN, ecrituresCliRapp, ecrituresMTNRapp, ecrituresCliUnijamb, ecrituresMTNUnijamb, montants, transUnijamb);

			if(ecrituresCliUnijamb.size() > 0 ) ecritures.removeAll(ecrituresCliUnijamb);
			if(ecrituresMTNUnijamb.size() > 0 ) ecritures.removeAll(ecrituresMTNUnijamb);
			if(ecrituresLiaisonUnijamb.size() > 0 ) ecritures.removeAll(ecrituresLiaisonUnijamb);
			
			logger.info("*************** ecritures.size() apres *************** : " + ecritures.size());
			
			
			String result = "";
			result = result + ">>> Écritures transactions unijambitses Client : " + "\n";
			for (bkmvti e : ecrituresCliUnijamb) {
				result = result + "   *** " + e.getLib() + "__" + e.getMon() + "\n";
			}
			result = result + "------------------------------------------------ \n";

			result = result + ">>> Écritures compensation unijambitses MTN : " + "\n";
			for (bkmvti e : ecrituresMTNUnijamb) {
				if(mapCli.containsKey(e.getCleLettrage() + "C") || mapCli.containsKey(e.getCleLettrage() + "D")){
					logger.info("TRX PULL/PUSH UNIJAMBISTE : "+e.getCleLettrage());
					Transaction trx = (Transaction) mapTrx.get(e.getCleLettrage());
					if(trx!=null){
						transUnijamb.add(trx);
						transactions.remove(trx);
					}
				}
				else if(mapCli.containsKey(e.getCleLettrage() + "D" )){

					logger.info("TRX SOUSCRIPTION UNIJAMBISTE : "+e.getCleLettrage());
					Transaction tx = (Transaction) mapTrx.get(e.getCleLettrage().replaceAll("FRAIS MAC", TypeOperation.SUBSCRIPTION.toString()) );
					if(tx!=null){
						transUnijamb.add(tx);
						transactions.remove(tx);
					}
					
				}
				result = result + "   *** " + e.getLib() + "__" + e.getMon() + "\n";
			}
			result = result + "------------------------------------------------ \n";
			
			result = result + ">>> Transactions unijambitses : " + "\n";
			for (Transaction t : transUnijamb) {
				result = result + "   *** " + t.getTypeOperation()  + "__" + new SimpleDateFormat("dd-MM-yyyy HH-mm").format(t.getDate()) + "__" + t.getPhoneNumber() + "__" + t.getAmount() + "\n";
			}
			
			//logger.info("*************** result *************** : " + result);
			if(ecrituresCliUnijamb.size() > 0 || ecrituresMTNUnijamb.size() > 0){
				// Message d'information
				PortalInformationHelper.showInformationDialog("Ecritures comptables unijambitses recensée(s)! \n" + result, InformationDialog.DIALOG_SUCCESS);
			}
			else {
				// Message d'information
				PortalInformationHelper.showInformationDialog("Verification des ecritures comptables effectuee avec succes !", InformationDialog.DIALOG_SUCCESS);
			}
			
			 
			doSearch = Boolean.TRUE;
			postFile = Boolean.TRUE;
			fileIsGenerated = true;
			
		}catch(Exception ex) {

			// Traitement de l'exception
			//PortalExceptionHelper.threatException(ex);
			ex.printStackTrace();

		}

	}
	
	
	public void transfertFichierECCompensation(){
		
		param = MobileMoneyViewHelper.appManager.findParameters();

		if(!StringUtils.isNotBlank(param.getIpAdressAmpli())){
			// Affichage de la Boite de dialogue d'information
			PortalInformationHelper.showInformationDialog("Veuillez saisir l'adresse IP du serveur Amplitude !",InformationDialog.DIALOG_INFORMATION);
			// On retourne false
			return ;
		}

		if(!StringUtils.isNotBlank(param.getPortServerAmpli())){
			// Affichage de la Boite de dialogue d'information
			PortalInformationHelper.showInformationDialog("Veuillez saisir le port du serveur Amplitude !",InformationDialog.DIALOG_INFORMATION);
			// On retourne false
			return ;
		}

		if(!StringUtils.isNotBlank(param.getUserLoginServerAmpli())){
			// Affichage de la Boite de dialogue d'information
			PortalInformationHelper.showInformationDialog("Veuillez saisir le login de l'utilisateur du serveur Amplitude !",InformationDialog.DIALOG_INFORMATION);
			// On retourne false
			return ;
		}

		if(!StringUtils.isNotBlank(param.getUserPasswordServerAmpli())){
			// Affichage de la Boite de dialogue d'information
			PortalInformationHelper.showInformationDialog("Veuillez saisir le mot de passe de l'utilisateur du serveur Amplitude !",InformationDialog.DIALOG_INFORMATION);
			// On retourne false
			return ;
		}
		
		if(!StringUtils.isNotBlank(param.getFilePathAmpli())){
			// Affichage de la Boite de dialogue d'information
			PortalInformationHelper.showInformationDialog("Veuillez saisir le repertoire de fichier Amplitude !",InformationDialog.DIALOG_INFORMATION);
			// On retourne false
			return ;
		}

		if(!StringUtils.isNotBlank(param.getFileNameAmpli())){
			// Affichage de la Boite de dialogue d'information
			PortalInformationHelper.showInformationDialog("Veuillez saisir le nom de fichier Amplitude !",InformationDialog.DIALOG_INFORMATION);
			// On retourne false
			return ;
		}

		if(!StringUtils.isNotBlank(param.getFileExtensionAmpli())){
			// Affichage de la Boite de dialogue d'information
			PortalInformationHelper.showInformationDialog("Veuillez saisir l'extension de fichier Amplitude !",InformationDialog.DIALOG_INFORMATION);
			// On retourne false
			return ;
		}

		try {

			if(transactions.size() == 0 || transactions.isEmpty()){
				// Affichage de la Boite de dialogue d'information
				PortalInformationHelper.showInformationDialog("Aucune transaction à comptabiliser !",InformationDialog.DIALOG_INFORMATION);
				// On retourne false
				return ;
			}

			if(ecritures.size() == 0 || ecritures.isEmpty()){
				// Affichage de la Boite de dialogue d'information
				PortalInformationHelper.showInformationDialog("Aucune ecriture à poster !",InformationDialog.DIALOG_INFORMATION);
				// On retourne false
				return ;
			}
			
			try{	
				// Recuperation du rapport de facturation
				// Chargement des elements du rapport
				FactMonth fac = MobileMoneyViewHelper.appManager.visualiserRapportCompensation(ecritures, MobileMoneyViewHelper.getSessionUser().getLogin(), null, MobileMoneyTools.getDownloadDir() + File.separator + exportFileName);
				
				if(fac == null ){

					PortalInformationHelper.showInformationDialog("Le système n'a détecté aucune transaction à comptabiliser. Rassurez-vous d'avoir effectuer la reconciliation globale ! !", InformationDialog.DIALOG_INFORMATION);

					return;

				}else{
					
					
					
					// Extraction de toutes les ecritures comptables des transactions
//					MobileMoneyViewHelper.appManager.extractECCompensationIntoFile(ecritures, MobileMoneyTools.getDownloadDir() + File.separator + exportFileName);
					// Generation du fichier
					//fileIsGenerated = true;
					
				 if(!param.getCbsServices()) {
					//Transmission du fichier vers Amplitude	
					String user = param.getUserLoginServerAmpli().trim();
					String pwd = param.getUserPasswordServerAmpli();

					boolean transfert = CSftp.send(MobileMoneyTools.getDownloadDir() + File.separator + exportFileName, user, param.getIpAdressAmpli().trim(), param.getFilePathAmpli().trim(), "", pwd);
					if(transfert == false){
						// Annulation
						PortalInformationHelper.showInformationDialog("Echec du transfert du fichier vers le core banking!", InformationDialog.DIALOG_INFORMATION);
						return;
					}	
				 }
					
					num = 1;
					
					transactions = MobileMoneyViewHelper.appManager.annulerEves(transactions);

					ecritures = new ArrayList<bkmvti>();
					
					doSearch = Boolean.TRUE;
					postFile = Boolean.FALSE;
					
					fileIsGenerated = false;
					
					// Afficher le rapport compensation
					imprimerRapportECCompensation(fac);
					
					//Suppression File EC
					File fec = new File(MobileMoneyTools.getDownloadDir() + File.separator + exportFileName);
					fec.delete();
					
					//Suppression File RAPP
					File frapp = new File(MobileMoneyTools.getDownloadDir() + File.separator + ecFileNameRapp);
					frapp.delete();
					
					
					// Message d'information
					if(param.getCbsServices()) 
						PortalInformationHelper.showInformationDialog("EC intégrées vers le Core Banking avec succes!", InformationDialog.DIALOG_SUCCESS);
				    else
						PortalInformationHelper.showInformationDialog("Transmission du fichier EC vers le Core Banking effectuee avec succes!", InformationDialog.DIALOG_SUCCESS);
			 }

			}catch(Exception ex){
				// Traitement de l'exception
				PortalExceptionHelper.threatException(ex);
			}
		} catch(Exception e) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
		}		
		
	}
	

	/**
	 * Imprime le rapport de la compensation
	 */
	public void imprimerRapportECCompensation(FactMonth fac){
		logger.info("Impression des Rapports de compensation");
		rapportCompenseFileName = "Rapport_Compensation_MAC_MTN-" + new SimpleDateFormat("ddMMyy-HHmm").format(new Date()) + ".pdf";
		try{			
			// Recuperation du visualisateur d'etats dans le FacesContext
			ReportViewerDialog viewer = (ReportViewerDialog) MobileMoneyViewHelper.getSessionManagedBean("reportViewerDialog");

			// initialisation du visualisateur
			if(viewer != null) {
				
				// Lecture du Type mime du fichier a afficher
				viewer.setMimeType(WebResourceManager.mimes.get("pdf"));

				// Initialisation de la map des parametres de l'etat
				HashMap<Object, Object> map = new HashMap<Object, Object>();
				map.put("SUBREPORT_DIR", MobileMoneyTools.getReportsDir());
				map.put("mois", "");
				map.put("titre", "TRANSACTIONS");

				// Affichage du rapport de traitement 
				List<FactMonth> data = new ArrayList<FactMonth>();
				data.add(fac);
				
				MobileMoneyTools.exportReportToPDFFile( MobileMoneyTools.getReportsDir().concat("RapportCompense.jasper"), map, data, MobileMoneyTools.getDownloadDir()+rapportCompenseFileName);
				logger.info("Impression OK");
				logger.info("Affichage des Rapports de Controle");
				List<FactMonth> datas = new ArrayList<FactMonth>();
				datas.add(fac);
				// Lecture du flux de donnees
				byte[] flux = MobileMoneyTools.getReportPDFBytes( MobileMoneyTools.getReportsDir().concat("RapportCompense.jasper"), map, datas);
				viewer.setStreamData( flux );
				
				//Deplacement du fichier
				archiverDocs(flux);			
				
				// Ouverture du Visualisateur
				viewer.open();
				logger.info("Affichage OK");
				
			}

		}catch(Exception ex) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(ex);
			ex.printStackTrace();

		}

	}
	
		
	public void archiverDocs(byte[] flux) {

		try {

			//Deplacer Rapport TFJO
//			FileOutputStream fileOuputStream = new FileOutputStream(MobileMoneyTools.getReportsDir() + "MOMO-" + new SimpleDateFormat("ddMMyyyy").format(new Date())+".pdf");
//			fileOuputStream.write(flux);
//			fileOuputStream.close();
//			File f = new File(MobileMoneyTools.getReportsDir() + "MOMO-" + new SimpleDateFormat("ddMMyyyy").format(new Date())+".pdf");
			
			File f = new File(MobileMoneyTools.getDownloadDir()+rapportCompenseFileName);
			logger.info("RAPPORT COMPENS FILE PATH : "+f.getAbsolutePath());
			DeplacerFichier(f);

			//Suppression Rapport TFJO
			f.delete();


			//Deplacer File EC
			File fec = new File(MobileMoneyTools.getDownloadDir() + File.separator + exportFileName);
			logger.info("RAPPORT EC FILE PATH : "+fec.getAbsolutePath());
			DeplacerFichier(fec);
			
			//Deplacer File RAPP
			File frapp = new File(MobileMoneyTools.getDownloadDir() + File.separator + ecFileNameRapp);
			logger.info("RAPPORT RAPPROCHEMENT FILE PATH : "+frapp.getAbsolutePath());
			DeplacerFichier(frapp);

		} catch(Exception e) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);

		}

	}



	/**
	* 
	* @param file
	* @param agence
	*/
	public void DeplacerFichier(File file){

		try{

			String name = file.getName();
			logger.info("*************** name *************** : " + name);
			byte[] fileByte = loadTemporairyUploadedFile(file);

			copyFile(fileByte, name);

		}catch (Exception e){
			e.printStackTrace();
		}

	}



	/**
	* 
	* @param file
	* @return
	*/
	private static byte[] loadTemporairyUploadedFile(File file)
	{
		if (file == null) {
			return null;
		}
		if (!file.exists()) {
			return null;
		}
		if (!file.isFile()) {
			return null;
		}
		if (!file.canRead()) {
			return null;
		}
		BufferedInputStream bis = null;
		try
		{
			bis = new BufferedInputStream(new FileInputStream(file));
		}
		catch (Exception e)
		{
			throw new RuntimeException("enterprisepanel.uploadlistener.errorontmpfileloading");
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			int data = 0;
			while ((data = bis.read()) != -1) {
				baos.write(data);
			}
		}
		catch (Exception e)
		{
			try
			{
				bis.close();
			}
			catch (Exception localException1) {}
			throw new RuntimeException("enterprisepanel.uploadlistener.errorontmpfileloading");
		}
		try
		{
			bis.close();
		}
		catch (Exception localException2) {}
		return baos.toByteArray();
	}



	/**
	* 
	* @param donnee
	* @param chemin
	* @param name
	*/
	public void copyFile(byte[] donnee, String name){

		try{

			param = MobileMoneyViewHelper.appManager.findParameters();
			String chemin = MobileMoneyTools.getSaveDir();
			logger.info("*************** chemin *************** : " + chemin);
			
			//String chemin = "E:\\ACQUISITION\\ARCHIVES";
			SimpleDateFormat formaterYear = new SimpleDateFormat("yyyy");
			SimpleDateFormat formaterDate = new SimpleDateFormat("dd-MM-yyyy");
			SimpleDateFormat formaterMonth = new SimpleDateFormat("MMMMM");
			//SimpleDateFormat formaterHeur = new SimpleDateFormat("HH:mm:ss");
			Date aujourdhui = new Date();

			String annee = formaterYear.format(aujourdhui);
			String mois = formaterMonth.format(aujourdhui);
			String jour = formaterDate.format(aujourdhui);
			//String heure = formaterHeur.format(aujourdhui).replaceAll(":","");

			chemin = chemin + File.separator + annee + File.separator + mois + File.separator + jour;
			//name = heure+"_"+name;

			// Chemin
			File rep = new File(chemin);
			if (!rep.isDirectory()) rep.mkdirs();

			FileOutputStream fileOuputStream = new FileOutputStream(chemin + File.separator + name);
			fileOuputStream.write(donnee);
			fileOuputStream.close();

		}catch(Exception e){

			e.printStackTrace();

		}

	}
	
	
//	public void visualiserECCompensation(){
//		
//		//extraireECCompensation();
//		
//		try {
//						
//			// Chargement des elements du rapport
//			FactMonth fac = MobileMoneyViewHelper.appManager.visualiserRapportCompensation(ecritures, MobileMoneyViewHelper.getSessionUser().getLogin(), null);
//			
//			imprimerRapportECCompensation(fac);
//			
//		} catch(Exception e) {
//			
//			// Traitement de l'exception
//			PortalExceptionHelper.threatException(e);
//			fileIsGenerated = false;
//		}
//	}
//	
//		
//	/**
//	 * Etrait les ecritures comptables des transactions selectionnees dans un fichier
//	 */
//	public void extraireEcritures() {
//		
//		try {
//			
//			// Extraction des ecritures comptables des transactions selectionnees
//			MobileMoneyViewHelper.appManager.extractECFromSelectedTransactionsIntoFile(transactions, MobileMoneyTools.getDownloadDir() + File.separator + exportFileName);
//			
//			fileIsGenerated = true;
//
//			// Message d'information
//			PortalInformationHelper.showInformationDialog("Export effectue avec succes! Vous pouvez telecharger le fichier genere.", InformationDialog.DIALOG_SUCCESS);
//			
//		} catch(Exception e) {
//			
//			// Traitement de l'exception
//			PortalExceptionHelper.threatException(e);
//			fileIsGenerated = false;
//		}
//		
//	}
//
//	/**
//	 * Etrait les ecritures comptables des transactions selectionnees dans un fichier
//	 */
//	public void consulterEcritures() {
//		
//		try {
//			
//			// Extraction des ecritures comptables des transactions selectionnees
//			ecritures = MobileMoneyViewHelper.appManager.getECFromTransactions(transactions, false);
//			
//			numEC = 1;
//			
//		} catch(Exception e) {
//			
//			// Traitement de l'exception
//			PortalExceptionHelper.threatException(e);
//		}
//		
//	}
//	
//	
//	
//	/**
//	 * Poste les ecritures comptables des transactions selectionnees dans Delta
//	 */
//	public void posterEcritures() {
//		
//		try {
//			
//			// Postage des ecritures comptables dans Delta
//			MobileMoneyViewHelper.appManager.posterTransactionsDansCoreBanking(transactions,  MobileMoneyViewHelper.getSessionUser().getLogin());
//			
//			// Message d'information
//			PortalInformationHelper.showInformationDialog("Les écritures comptables des transactions selectionnées ont été postées avec succès dans le Core Banking!", InformationDialog.DIALOG_SUCCESS);
//			
//		} catch(Exception e) {
//			
//			// Traitement de l'exception
//			PortalExceptionHelper.threatException(e);
//			
//		}
//		
//	}
//
//
//	
//	/**
//	 * Poste les ecritures comptables des transactions selectionnees dans Delta
//	 */
//	public void executerCompensation() {
//		
//		try {
//			equilibreCptes = new ArrayList<EquilibreComptes>();
//			
//			// Recherche des parametres generaux
//			Parameters p = MobileMoneyViewHelper.appManager.findParameters();
//			
//			// Si le module est desactive
//			/*if(!p.getActive().booleanValue()){
//
//				// Message d'information
//				PortalInformationHelper.showInformationDialog("Un TFJ est en cours d'exécution, il n'est pas possible d'exécuter la Compensation.", InformationDialog.DIALOG_WARNING);
//				
//				return;
//			}*/
//			
//			// Postage des ecritures comptables dans Delta
//			FactMonth fac = MobileMoneyViewHelper.appManager.executerCompensation( PortalHelper.DEFAULT_DATE_FORMAT.parse(txtDateDeb) , PortalHelper.DEFAULT_DATE_FORMAT.parse(txtDateFin),  MobileMoneyViewHelper.getSessionUser().getLogin());
//
//			// Recuperation des Rapports de Controle
//			equilibre = MobileMoneyViewHelper.appManager.getRapportEquilibre(MobileMoneyViewHelper.getSessionUser().getLogin(), MobileMoneyTools.PULL_PUSH);
//			doublon = MobileMoneyViewHelper.appManager.getRapportDoublon(MobileMoneyViewHelper.getSessionUser().getLogin(), MobileMoneyTools.PULL_PUSH);
//			
//			equilibreCptes.add(MobileMoneyViewHelper.appManager.getRapportEquilibreCpteClient());
//			equilibreCptes.add(MobileMoneyViewHelper.appManager.getRapportEquilibreCpteDAP());
//			equilibreCptes.add(MobileMoneyViewHelper.appManager.getRapportEquilibreCpteFloatMTN());
//			
//			/*equilibre = MobileMoneyViewHelper.appManager.getRapportEquilibre();
//			doublon = MobileMoneyViewHelper.appManager.getRapportDoublon();*/
//			
//			imprimerRapportCompensation(fac);
//			
//			// Message d'information
//			//PortalInformationHelper.showInformationDialog("Compensation des Opérations Pull et Push from account de la période ["+txtDateDeb + ", " + txtDateFin +" ] effectuée avec succès!", InformationDialog.DIALOG_SUCCESS);
//			PortalInformationHelper.showInformationDialog("Compensation des Opérations Pull et Push from account effectuée avec succès!", InformationDialog.DIALOG_SUCCESS);
//			
//		} catch(Exception e) {
//			
//			// Traitement de l'exception
//			PortalExceptionHelper.threatException(e);
//			
//		}
//		
//	}
//	
//	
//	/**
//	 * Imprime le rapport de la compensation
//	 */
//	public void imprimerRapportCompensation(FactMonth fac){
//		logger.info("Impression des Rapports de compensation");
//		rapportCompenseFileName = "Rapport_Compensation_MAC_MTN-" + new SimpleDateFormat("ddMMyy-HHmm").format(new Date()) + ".pdf";
//		try{
//			
//			if(equilibre == null || equilibre.isEmpty()) return;
//			
//			// Recuperation du visualisateur d'etats dans le FacesContext
//			ReportViewerDialog viewer = (ReportViewerDialog) MobileMoneyViewHelper.getSessionManagedBean("reportViewerDialog");
//
//			// initialisation du visualisateur
//			if(viewer != null) {
//				
//				// Lecture du Type mime du fichier a afficher
//				viewer.setMimeType(WebResourceManager.mimes.get("pdf"));
//
//				// Initialisation de la map des parametres de l'etat
//				HashMap<Object, Object> map = new HashMap<Object, Object>();
//				
//				map.put("SUBREPORT_DIR", MobileMoneyTools.getReportsDir());
//				map.put("mois", "");
//				map.put("titre", "Traitement des Transactions");
//
//				// Affichage du rapport de traitement 
//				List<FactMonth> data = new ArrayList<FactMonth>();
//				data.add(fac);
//				
//				MobileMoneyTools.exportReportToPDFFile( MobileMoneyTools.getReportsDir().concat("RapportCompense.jasper"), map, data, MobileMoneyTools.getDownloadDir()+rapportCompenseFileName);
//				logger.info("Impression OK");
//				logger.info("Affichage des Rapports de Controle");
//				List<FactMonth> datas = new ArrayList<FactMonth>();
//				datas.add(fac);
//				// Lecture du flux de donnees
//				viewer.setStreamData( MobileMoneyTools.getReportPDFBytes( MobileMoneyTools.getReportsDir().concat("RapportCompense.jasper"), map, datas));
//								
//				// Ouverture du Visualisateur
//				viewer.open();
//				logger.info("Affichage OK");
//			}
//
//		}catch(Exception ex) {
//
//			// Traitement de l'exception
//			PortalExceptionHelper.threatException(ex);
//			ex.printStackTrace();
//
//		}
//
//	}
	

	public boolean isFormRapportOpen(){
		return !equilibre.isEmpty();
	}
	
	public void archiverEcritures() {
		
		try {
			
		} catch(Exception e) {
			
			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}

	public void purgerEcritures() {
		
		try {
			
		} catch(Exception e) {
			
			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	/**
	 * @return the transactions
	 */
	public List<Transaction> getTransactions() {
		return transactions;
	}

	/**
	 * @return the num
	 */
	public int getNum() {
		return num++;
	}

	/**
	 * @return the numEC
	 */
	public int getNumEC() {
		return numEC++;
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
	 * @return the opItems
	 */
	public List<SelectItem> getOpItems() {
		return opItems;
	}

	/**
	 * @return the txtSearchOp
	 */
	public TypeOperation getTxtSearchOp() {
		return txtSearchOp;
	}

	/**
	 * @param txtSearchOp the txtSearchOp to set
	 */
	public void setTxtSearchOp(TypeOperation txtSearchOp) {
		this.txtSearchOp = txtSearchOp;
	}

	/**
	 * @return the selectedTransaction
	 */
	public Transaction getSelectedTransaction() {
		return selectedTransaction;
	}

	/**
	 * @param selectedTransaction the selectedTransaction to set
	 */
	public void setSelectedTransaction(Transaction selectedTransaction) {
		this.selectedTransaction = selectedTransaction;
	}

	/**
	 * @return the statutItems
	 */
	public List<SelectItem> getStatutItems() {
		return statutItems;
	}

	/**
	 * @return the txtSearchStatus
	 */
	public TransactionStatus getTxtSearchStatus() {
		return txtSearchStatus;
	}

	/**
	 * @param txtSearchStatus the txtSearchStatus to set
	 */
	public void setTxtSearchStatus(TransactionStatus txtSearchStatus) {
		this.txtSearchStatus = txtSearchStatus;
	}

	/**
	 * @return the exportFileName
	 */
	public String getExportFileName() {
		return exportFileName;
	}
	
	/**
	 * @return the txtSearchPosted
	 */
	public Boolean getTxtSearchPosted() {
		return txtSearchPosted;
	}

	/**
	 * @param txtSearchPosted the txtSearchPosted to set
	 */
	public void setTxtSearchPosted(Boolean txtSearchPosted) {
		this.txtSearchPosted = txtSearchPosted;
	}

	/**
	 * @return the postedItems
	 */
	public List<SelectItem> getPostedItems() {
		return postedItems;
	}

	public boolean isExportedFileExist() {
		return (new File( MobileMoneyTools.getDownloadDir() + File.separator + ecFileNameRapp).exists() && fileIsGenerated) 
				|| (new File( MobileMoneyTools.getDownloadDir() + File.separator + controleReservationsFileName).exists() && fileIsGenerated);
	}
	
//	public boolean isExportedFileExist() {
//		return new File( MobileMoneyTools.getDownloadDir() + File.separator + ecFileNameRapp).exists() && fileIsGenerated;
//	}
//	
//	public boolean isControlFileExist() {
//		return new File( MobileMoneyTools.getDownloadDir() + File.separator + controleReservationsFileName).exists() && fileIsGenerated;
//	}

	/**
	 * @return the searchName
	 */
	public String getSearchName() {
		return searchName;
	}

	/**
	 * @param searchName the searchName to set
	 */
	public void setSearchName(String searchName) {
		this.searchName = searchName;
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
	 * @return the ecritures
	 */
	public List<bkmvti> getEcritures() {
		return ecritures;
	}
	
		
	/**
	 * @return the doSearch
	 */
	public Boolean getDoSearch() {
		return doSearch;
	}

	/**
	 * @param doSearch the doSearch to set
	 */
	public void setDoSearch(Boolean doSearch) {
		this.doSearch = doSearch;
	}

	/**
	 * @return the visualiser
	 */
	public Boolean getVisualiser() {
		return visualiser;
	}

	/**
	 * @param visualiser the visualiser to set
	 */
	public void setVisualiser(Boolean visualiser) {
		this.visualiser = visualiser;
	}
	
	
	/**
	 * @return the postFile
	 */
	public Boolean getPostFile() {
		return postFile;
	}

	/**
	 * @param postFile the postFile to set
	 */
	public void setPostFile(Boolean postFile) {
		this.postFile = postFile;
	}

	/**
	 * @param doublon the doublon to set
	 */
	public void setDoublon(List<Doublon> doublon) {
		this.doublon = doublon;
	}

	
	public String getECFileDefinition() {
		return ecritures != null && !ecritures.isEmpty() ? "FrmEC.xhtml" : "";
	}
	
	public void closeEC() {
		ecritures.clear();
	}
	
		
	/**
	 * @return the txtHeureDeb
	 */
	public String getTxtHeureDeb() {
		return txtHeureDeb;
	}

	/**
	 * @param txtHeureDeb the txtHeureDeb to set
	 */
	public void setTxtHeureDeb(String txtHeureDeb) {
		this.txtHeureDeb = txtHeureDeb;
	}

	/**
	 * @return the txtHeureFin
	 */
	public String getTxtHeureFin() {
		return txtHeureFin;
	}

	/**
	 * @param txtHeureFin the txtHeureFin to set
	 */
	public void setTxtHeureFin(String txtHeureFin) {
		this.txtHeureFin = txtHeureFin;
	}

	/**
	 * @return the periode
	 */
	public String getPeriode() {
		return periode;
	}

	/**
	 * @param periode the periode to set
	 */
	public void setPeriode(String periode) {
		this.periode = periode;
		logger.info("SET PERIODE : "+periode);
		if(periode.equals(MoMoHelper.PERIODE_DAY)) setHeures(Boolean.FALSE);
		else setHeures(Boolean.TRUE);
	}

	/**
	 * @return the periodeItems
	 */
	public List<SelectItem> getPeriodeItems() {
		return periodeItems;
	}

	/**
	 * @param periodeItems the periodeItems to set
	 */
	public void setPeriodeItems(List<SelectItem> periodeItems) {
		this.periodeItems = periodeItems;
	}

	
	/**
	 * @return the heures
	 */
	public Boolean getHeures() {
		return heures;
	}

	/**
	 * @param heures the heures to set
	 */
	public void setHeures(Boolean heures) {
		this.heures = heures;
		logger.info("SET HEURES : "+heures);
	}

	
	/**
	 * @return the controleReservationsFileName
	 */
	public String getControleReservationsFileName() {
		return controleReservationsFileName;
	}

	/**
	 * @param controleReservationsFileName the controleReservationsFileName to set
	 */
	public void setControleReservationsFileName(String controleReservationsFileName) {
		this.controleReservationsFileName = controleReservationsFileName;
	}

	public String getTotalDebit() {
		double mnt = 0;
		
		for(bkmvti mvt : ecritures){
			mnt += mvt.getSen().equals("D") ? mvt.getMon() : 0;
		}
		
		return MoMoHelper.espacement(mnt);
	}

	public String getTotalCredit() {
		double mnt = 0;

		for(bkmvti mvt : ecritures){
			mnt += mvt.getSen().equals("C") ? mvt.getMon() : 0;
		}
		
		return MoMoHelper.espacement(mnt);
	}

	public String getTotalCommissions() {
		double mnt = 0;

		/*for(bkmvti mvt : ecritures){
			mnt += mvt.getSen().equals("C") ? mvt.getMon() : 0;
		}*/
		
		return MoMoHelper.espacement(mnt);
	}

	public String getTotalTaxes() {
		double mnt = 0;

		/*for(bkmvti mvt : ecritures){
			mnt += mvt.getSen().equals("C") ? mvt.getMon() : 0;
		}*/
		
		return MoMoHelper.espacement(mnt);
	}

	/**
	 * @return the equilibre
	 */
	public List<Equilibre> getEquilibre() {
		return equilibre;
	}

	/**
	 * @return the doublon
	 */
	public List<Doublon> getDoublon() {
		return doublon;
	}
	
	
	/**
	 * @return the equilibreCptes
	 */
	public List<EquilibreComptes> getEquilibreCptes() {
		return equilibreCptes;
	}
	
	
	public String getFrmRapportName(){
		return "frmRapportControl";
	}
	
	public String getFrmRapportFileDefinition(){
		return isFormRapportOpen() ? "FrmRapportControl.xhtml" : ( ((FrmTFJO) MobileMoneyViewHelper.getSessionManagedBean("frmTFJO")).isFormRapportOpen() ? "FrmRapportControl.xhtml" : "emptyPage.xhtml");
	}
	
	
	/**
	 * @return the dateRapport
	 */
	public Date getDateRapport() {
		return dateRapport;
	}

	
	/**
	 * @param dateRapport the dateRapport to set
	 */
	public void setDateRapport(Date dateRapport) {
		this.dateRapport = dateRapport;
	}
	
	
	/**
	 * @return the dateRapport
	 */
	public String getFormattedDateRapport() {
		return PortalHelper.DEFAULT_DATE_FORMAT.format(dateRapport);
	}

	/**
	 * @return the ecFileNameRapp
	 */
	public String getEcFileNameRapp() {
		return ecFileNameRapp;
	}

	/**
	 * @param ecFileNameRapp the ecFileNameRapp to set
	 */
	public void setEcFileNameRapp(String ecFileNameRapp) {
		this.ecFileNameRapp = ecFileNameRapp;
	}
	
	
	
}
