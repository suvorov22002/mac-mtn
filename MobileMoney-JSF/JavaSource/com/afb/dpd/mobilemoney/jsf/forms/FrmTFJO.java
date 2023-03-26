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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.afb.dpd.mobilemoney.jpa.entities.Exercice;
import com.afb.dpd.mobilemoney.jpa.entities.FactMonth;
import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
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
import com.afb.dpd.mobilemoney.worker.AbonnementWorker;
import com.afb.dpd.mobilemoney.worker.ResiliationWorker;

import afb.dsi.dpd.portal.jpa.entities.User;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

/**
 * Formulaire de comptabilisation des Commissions sur les transactions de Pull/Push
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class FrmTFJO extends AbstractPortalForm {

	/**
	 * 
	 */
	private String monthString = "";
	
	private static Log logger = LogFactory.getLog(FrmTFJO.class);
	
	private int month;

	private int year;

	private Exercice exercice = null;
	
	private Parameters param;

	public List<Transaction> list = new ArrayList<Transaction>();
	List<bkmvti> ecritures = new ArrayList<>();
	List<Subscriber> subs = new ArrayList<>();
	public List<Transaction> fact = new ArrayList<Transaction>();
	public List<Transaction> trx = new ArrayList<Transaction>();
	public List<Transaction> trx_regul = new ArrayList<Transaction>();
	
	List<Equilibre> equilibre = new ArrayList<Equilibre>();
	List<Doublon> doublon = new ArrayList<Doublon>();
	List<EquilibreComptes> equilibreCptes = new ArrayList<EquilibreComptes>();

	private String rapportFactFileName = "Rapport_Facturation_MAC_MTN-" + new SimpleDateFormat("ddMMyy-HHmm").format(new Date()) + ".pdf";

	private String nameECFact = "EC_MoMo_Fact.unl";
	private String nameECRegul = "EC_MoMo_Regul.unl";
	
//	private String exportFileName = new SimpleDateFormat("ddMMyy-HHmm").format(new Date()) + ".unl";
	private String exportFileName = nameECFact;
	
	private String ecFileNameRapp = new SimpleDateFormat("ddMMyy-HHmm").format(new Date()) + ".xls";
	
//	private String nameECFact = "EC_Facturation_MAC_MTN-";
//	private String nameECRegul = "EC_Regul_Facturation_MAC_MTN-";
	
	
	private String nameRapportFact = "Rapport_Facturation_MAC_MTN-";
	private String nameRapportRegul = "Rapport_Regul_Facturation_MAC_MTN-";

	int num = 1, nbParPage = 2000, nbPages = 0, numPage = 0, nbAbo = 0; boolean fileIsGenerated = false;
	Double mntComs = 0d, mntTax = 0d;


	private Double montant = 0d;
	private Integer nombre = 0;
	private TransactionStatus status = null;
	
	private Date dateRapport = new Date();

	boolean precedent = false;
	boolean suivant = true;
	private Boolean postFile;

	/**
	 * Default Constructor
	 */
	public FrmTFJO() {}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		// TODO Auto-generated method stub
		super.initForm(); numPage = 0;
		list = new ArrayList<Transaction>();
		fact = new ArrayList<Transaction>();
		equilibre = new ArrayList<Equilibre>();
		doublon = new ArrayList<Doublon>();
		dateRapport = new Date();
		postFile = Boolean.FALSE;
	}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Comptabilisation p√©riodique des abonnements Pull/Push";
	}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose(); list.clear(); fact.clear(); num = 1; fileIsGenerated = false;
		equilibre.clear(); doublon.clear(); nbAbo= 0; mntComs = 0d; mntTax = 0d; numPage = 0;
		monthString = ""; month = 0; montant = 0d; year = 0; exercice = null; nombre = 0;
		equilibreCptes = new ArrayList<EquilibreComptes>();
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
	 * Execution de la Comptabilisation
	 */
	@SuppressWarnings("unchecked")
	public void executerTFJO(){
		
		// Recuperation des parametres de l'utilisateur connecte
		User user = MobileMoneyViewHelper.getSessionUser();
		String login = "";
		if (user != null ) login = user.getLogin();
		
		//controle des abonnements de la veille
		AbonnementWorker.processResiliation();
		
		//controle des resiliations de la veille
		ResiliationWorker.processResiliation();
		
		List<Transaction> compta = new ArrayList<Transaction>();
		// Recuperer la date de traitement
		Date dateCompt =  new Date(); 
		try {
			logger.info("Chargement des comptabilisations");
			// Verifier si les TFJO on deja ete effectues (Transaction avec le status PROCESSING)
			fact = MobileMoneyViewHelper.appManager.chargerDonneesComptabiliserTFJO(dateCompt);

			if(!fact.isEmpty()){
				logger.info("OK : "+fact.size()+" TRX");
				postFile = Boolean.TRUE;
				// Comptabilisation de operations
				somme(fact);
				numPage = 1;
				num = 1;
				nbPages = (fact.size()/nbParPage) + 1;
				chargerDonnees();
				compta.clear();
				PortalInformationHelper.showInformationDialog("Traitement de la facturation en cours.\n\n Veuillez exÈcuter la comptabilisation.", InformationDialog.DIALOG_INFORMATION);
				status = TransactionStatus.PROCESSING;
				
				// Extraction des EC
				//extraireECRappochement();
				return;
			}
			compta.clear();
			 
			logger.info("NOK");
			logger.info("Facturation des abonnes");
			// Facturation des abonnes
			MobileMoneyViewHelper.appManager.executerTFJO2(dateCompt); //list = MobileMoneyViewHelper.appManager.executerTFJO2();
			
			logger.info("Chargement des facturations");
			// Chargement des operations de Comptabilisation des facturations
			fact = MobileMoneyViewHelper.appManager.chargerDonneesComptabiliserTFJO(dateCompt);
			somme(fact);
			
			// Msg d'information
			if(fact.isEmpty()) PortalInformationHelper.showInformationDialog("Aucune comptabilisation a effectuer cette p√©riode!", InformationDialog.DIALOG_INFORMATION);
			else postFile = Boolean.TRUE;
			numPage = 1;
			num = 1;
			nbPages = (fact.size()/nbParPage) + 1;
			chargerDonnees();
			logger.info("OK : "+fact.size()+" TRX");
			status = TransactionStatus.PROCESSING;
			// Extraction des EC
			//extraireECRappochement();
		} catch(Exception ex) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(ex);
		}
	}


	/**
	 * Execution de la Comptabilisation
	 */
	public void executerRegulTFJO(){

		try {
			
			// Chargement des operations de Comptabilisation des REGUL
			fact = MobileMoneyViewHelper.appManager.chargerDonneesComptabiliserRegul();
			logger.info("OK");
			// Msg d'information
			if(fact.isEmpty()) PortalInformationHelper.showInformationDialog("Aucune comptabilisation en REGUL √† traiter, veuillez rÈessayer!", InformationDialog.DIALOG_INFORMATION);
			else postFile = Boolean.TRUE;
			numPage = 1;
			num = 1;
			nbPages = (fact.size()/nbParPage) + 1;
			chargerDonnees();
			somme(fact);
	
		} catch(Exception ex) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(ex);
		}
		status = TransactionStatus.REGUL;
	}
	
	
	/**
	 * Extrait les ecritures comptables des transactions et de rapprochement sous excel
	 */
	public void extraireECRappochement(){
		
		try {
			// Renommer le fichier
			if(status != null && status.equals(TransactionStatus.PROCESSING)) ecFileNameRapp = nameECFact.substring(0, nameECFact.length() - 4) + ecFileNameRapp.substring(ecFileNameRapp.length() - 15, ecFileNameRapp.length());
			else if(status != null && status.equals(TransactionStatus.REGUL)) ecFileNameRapp = nameECRegul.substring(0, nameECRegul.length() - 4) + ecFileNameRapp.substring(ecFileNameRapp.length() - 15, ecFileNameRapp.length());
			logger.info("EXTRACTION EC");
			// Export des donnees de comptabilisation
//			MobileMoneyViewHelper.appManager.exportComptabilisationIntoExcelFile(trx, ecFileNameRapp);
			MobileMoneyViewHelper.appManager.exportECIntoExcelFile(trx, ecritures, ecFileNameRapp);
			logger.info("OK");
			num = 1; fileIsGenerated = true;

		} catch(Exception ex) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(ex);
		}
	}

	
	/**
	 * Chargement de la liste des abonnement a comptabiliser
	 */
	public void chargerDonnees(){
		// Chargement de la liste des abonnements a afficher
		try {
			//logger.info("NUMPAGE :"+numPage);
			list = (numPage==nbPages) ? fact.subList((numPage-1)*nbParPage, fact.size()) : fact.subList((numPage-1)*nbParPage, numPage*nbParPage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}

	public void pageSuivante(){

		try {
			if(numPage > 1 && fact.isEmpty()) return;
			numPage++;
			chargerDonnees();
//			num = 1;

		} catch(Exception ex) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(ex);
		}
	}

	public void pagePrecedente(){

		try {
			num = num - ((numPage==nbPages) ? ((fact.size() - (numPage-1)*nbParPage) + nbParPage) : nbParPage*2);
			if(numPage == 1) return;
			numPage--;
			chargerDonnees();
		} catch(Exception ex) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(ex);
		}
	}

	
	private void somme(List<Transaction> list){
		montant = 0d;
		for(Transaction tx : list) {
			montant += tx.getTtc();
		}
		nombre = list.size();
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
			
			// Construction des MAPs d'ecritures
			HashMap<Object, Object> mapCOM = new HashMap<Object, Object>();
			HashMap<Object, Object> mapTAX = new HashMap<Object, Object>();
			HashMap<Object, Object> mapCli = new HashMap<Object, Object>();
			List<bkmvti> ecrituresCOM = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresTAX = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresCli = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresCOMRapp = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresTAXRapp = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresCliRapp = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresCOMUnijamb = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresTAXUnijamb = new ArrayList<bkmvti>();
			List<bkmvti> ecrituresCliUnijamb = new ArrayList<bkmvti>();
			Double totalFrais = 0d;
			Double totalCom = 0d;
			Double totalTax = 0d;
			HashMap<String, Double> montants = new HashMap<String, Double>();
			
			// Similuation de l'ajour des ecritures unijambistes
			// A enlever lors du passage en production
			bkmvti e1 = (bkmvti) ecritures.get(1);
			bkmvti e2 = (bkmvti) ecritures.get(2);
			
			logger.info("*************** ecritures.size() sans ajout *************** : " + ecritures.size());
			if(param.getEtatServiceSDP().equals(StatutService.TEST)){
				ecritures.add( new bkmvti("00001", "001", e1.getCha(), "04942231051", e1.getSuf(), "", MoMoHelper.padText(String.valueOf(0001), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, "AUTO", "", e1.getClc(), new Date(), null, e1.getDva(), 18500d, "C", "PUSH/2703191600/695000124", "N", "XXXXXX", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, e1.getAge(), e1.getDev(), 18500d, null, null, null, null, null, null, null, null, e1.getNat(), "VA", null, null) ); 
				ecritures.add( new bkmvti("00001", "001", e2.getCha(), "04996661003", e2.getSuf(), "", MoMoHelper.padText(String.valueOf(0002), MoMoHelper.LEFT, MoMoHelper.TAILLE_CODE_EVE, "0"), null, "AUTO", "", e2.getClc(), new Date(), null, e2.getDva(), 19500d, "D", "PULL/2703191600/699020154", "N", "XXXXXX", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, e2.getAge(), e2.getDev(), 19500d, null, null, null, null, null, null, null, null, e2.getNat(), "VA", null, null) ); 
			}
			
			logger.info("*************** ecritures.size() avec ajout *************** : " + ecritures.size());
			
			for(Object eve : ecritures) {
//				logger.info("ECRITURE :  "+e.getNcp()+" | "+e.getEve()+" | "+e.getSen()+" | "+e.getLib()+" | "+e.getMon());
				bkmvti e = (bkmvti) eve;
				String tel = e.getLib().trim().split("/")[2];
				String date = e.getLib().trim().split("/")[1];
				String typeOpe = e.getLib().trim().split("/")[0];
				
				String cle  = tel + date.trim() + Integer.toString(e.getMon().intValue()) + typeOpe;
				//logger.info("CLE / VALEUR : "+cle+" / "+typeOpe);
				e.setCleLettrage(cle);
				e.setTypeOperation(typeOpe);

				// EC impactant le compte de commission
				if(e.getNcp().equals(param.getNumCompteCommissions().trim().split("-")[1]) && typeOpe.equals("COMMAC")) {
					if(!mapCOM.containsKey(cle+e.getSen())) {
						mapCOM.put(cle+e.getSen(), e);	
//						logger.info("MAP MTN KEY :  "+cle+e.getSen());
						totalCom = totalCom + e.getMon();
					}
//					logger.info("ECRITURE MTN :  "+e.getNcp()+" | "+e.getEve()+" | "+e.getSen()+" | "+e.getLib()+" | "+e.getMon());
					ecrituresCOM.add(e);
					
				}
				// EC impactant le compte de tax
				else if(e.getNcp().equals(param.getNumCompteTVA().trim().split("-")[1]) && typeOpe.equals("TAXMAC")) {
					if(!mapTAX.containsKey(cle+e.getSen())) {
						mapTAX.put(cle+e.getSen(), e);	
//						logger.info("MAP MTN KEY :  "+cle+e.getSen());
						totalTax = totalTax + e.getMon();
					}
//					logger.info("ECRITURE MTN :  "+e.getNcp()+" | "+e.getEve()+" | "+e.getSen()+" | "+e.getLib()+" | "+e.getMon());
					ecrituresTAX.add(e);
					
				}
				// EC impactant le compte du client
				else if(e.getNcp().trim().substring(0, 1).equals("0") && !e.getNcp().equals(param.getNumCompteMTN().trim().split("-")[1]) && typeOpe.equals("FRAISMAC") ) {
					//logger.info(" CLIENT ");
					if(!mapCli.containsKey(cle+e.getSen())) {
						mapCli.put(cle+e.getSen(), e);	
//						logger.info("MAP CLT KEY :  "+cle+e.getSen());
					}
//					logger.info("ECRITURE CLT :  "+e.getNcp()+" | "+e.getEve()+" | "+e.getSen()+" | "+e.getLib()+" | "+e.getMon());
					ecrituresCli.add(e);
				}

			}
			
			montants.put("TOTAL_COM", totalCom);
			montants.put("TOTAL_TAX", totalTax);
			montants.put("TOTAL_COMTAX", totalCom + totalTax);
			
			logger.info("*************** ecrituresCli.size() avant *************** : " + ecrituresCli.size());
			logger.info("*************** ecrituresCOM.size() avant *************** : " + ecrituresCOM.size());
			logger.info("*************** ecrituresTAX.size() avant *************** : " + ecrituresTAX.size());
			logger.info("*************** mapCli.size() avant *************** : " + mapCli.size());
			logger.info("*************** mapCOM.size() avant *************** : " + mapCOM.size());
			logger.info("*************** mapTAX.size() avant *************** : " + mapTAX.size());
			
//			logger.info("MAP MTN : "+mapMTN.toString());
			//Rapprochement des ecritures
			logger.info("\n*************** Rapprochement des ecritures ***************\n");
			for(bkmvti e : ecrituresCli) {
//				logger.info("ECRITURE CLI :  "+e.getNcp()+" | "+e.getEve()+" | "+e.getSen()+" | "+e.getLib()+" | "+e.getMon());
				// 
				if(e.getTypeOperation().equals(TypeOperation.PULL.toString()) || e.getTypeOperation().equals(TypeOperation.PUSH.toString())){
//					logger.info("OK!"); 
					if(mapCOM.containsKey(e.getCleLettrage() + "C" ) && mapTAX.containsKey(e.getCleLettrage() + "C" )) {
						//logger.info("OK!OK!"); 
						// Total PULL/PUSH client
						if(e.getLib().contains("FRAISMAC")) totalFrais = totalFrais + e.getMon();
//						if(e.getLib().contains("COMMAC")) totalCom = totalCom + e.getMon();
//						if(e.getLib().contains("TAXMAC")) totalTax = totalTax + e.getMon();
						
						ecrituresCliRapp.add(e);
//						logger.info("RAPP CLI :  "+e.getNcp()+" | "+e.getEve()+" | "+e.getSen()+" | "+e.getLib()+" | "+e.getMon());
						ecrituresCOMRapp.add((bkmvti) mapCOM.get(e.getCleLettrage() + "C" ));
						ecrituresTAXRapp.add((bkmvti) mapTAX.get(e.getCleLettrage() + "C" ));	
//						logger.info("RAPP MTN :  "+e.getCleLettrage() + "C" );
//						logger.info("RAPP MTN :  "+e.getCleLettrage() + "D" );
						mapCOM.remove(e.getCleLettrage() + "C" );
						mapTAX.remove(e.getCleLettrage() + "C" );

						mapCli.remove(e.getCleLettrage()+e.getSen());
					}
										
				}

			}
			
			montants.put("TOTAL_FRAIS", totalFrais);
			
			logger.info("*************** ecrituresCli.size() after *************** : " + ecrituresCli.size());
			logger.info("*************** ecrituresCOM.size() after *************** : " + ecrituresCOM.size());
			logger.info("*************** ecrituresTAX.size() after *************** : " + ecrituresTAX.size());
			logger.info("*************** mapCli.size() after *************** : " + mapCli.size());
			logger.info("*************** mapCOM.size() after *************** : " + mapCOM.size());
			logger.info("*************** mapTAX.size() after *************** : " + mapTAX.size());
			
			// vidage des ecritures unijambistes
			Iterator itr = mapCli.entrySet().iterator();
			while(itr.hasNext()) {
				Map.Entry mapentry = (Map.Entry) itr.next();
				ecrituresCliUnijamb.add((bkmvti) mapentry.getValue());
			}

			itr = mapCOM.entrySet().iterator();
			while(itr.hasNext()) {
				Map.Entry mapentry = (Map.Entry) itr.next();
				ecrituresCOMUnijamb.add((bkmvti) mapentry.getValue());
			}
			
			itr = mapTAX.entrySet().iterator();
			while(itr.hasNext()) {
				Map.Entry mapentry = (Map.Entry) itr.next();
				ecrituresTAXUnijamb.add((bkmvti) mapentry.getValue());
			}

			// Export du rapprochement au format Excel
//			MobileMoneyViewHelper.appManager.exportRapprochmentBkmvti(ecFileNameRapp, ecritures, ecrituresCli, ecrituresCOM, ecrituresCliRapp, ecrituresCOMRapp, ecrituresCliUnijamb, ecrituresCOMUnijamb, montants);
//			MobileMoneyViewHelper.appManager.exportRapprochmentBkmvti(ecFileNameRapp, ecritures, ecrituresCli, ecrituresCOM, ecrituresTAX, ecrituresCliRapp, ecrituresCOMRapp, ecrituresTAXRapp, ecrituresCliUnijamb, ecrituresCOMUnijamb, ecrituresTAXUnijamb, montants);
			
			if(ecrituresCliUnijamb.size() > 0 ) ecritures.removeAll(ecrituresCliUnijamb);
			if(ecrituresCOMUnijamb.size() > 0 ) ecritures.removeAll(ecrituresCOMUnijamb);
			if(ecrituresTAXUnijamb.size() > 0 ) ecritures.removeAll(ecrituresTAXUnijamb);
			
			logger.info("*************** ecritures.size() apres *************** : " + ecritures.size());
			
			
			String result = "";
			result = result + ">>> √âcritures transactions unijambitses Client : " + "\n";
			for (bkmvti e : ecrituresCliUnijamb) {
				result = result + "   *** " + e.getLib() + "__" + e.getMon() + "\n";
			}
			result = result + "------------------------------------------------ \n";

			result = result + ">>> √âcritures compensation unijambitses COM : " + "\n";
			for (bkmvti e : ecrituresCOMUnijamb) {
				result = result + "   *** " + e.getLib() + "__" + e.getMon() + "\n";
			}
			
			result = result + ">>> √âcritures compensation unijambitses TAX : " + "\n";
			for (bkmvti e : ecrituresTAXUnijamb) {
				result = result + "   *** " + e.getLib() + "__" + e.getMon() + "\n";
			}
			
			//logger.info("*************** result *************** : " + result);
			if(ecrituresCliUnijamb.size() > 0 || ecrituresCOMUnijamb.size() > 0 || ecrituresTAXUnijamb.size() > 0){
				// Message d'information
				PortalInformationHelper.showInformationDialog("Ecritures comptables unijambitses recens√©e(s)! \n" + result, InformationDialog.DIALOG_SUCCESS);
			}
			else {
				// Message d'information
				PortalInformationHelper.showInformationDialog("Verification des ecritures comptables effectuee avec succes !", InformationDialog.DIALOG_SUCCESS);
			}
			
			fileIsGenerated = true;
			
		}catch(Exception ex) {

			// Traitement de l'exception
			//PortalExceptionHelper.threatException(ex);
			ex.printStackTrace();

		}

	}
	
	
	/**
	 * Transfert du fichier des EC de Comptabilisation
	 */
	public void validerTFJO(){
		
		// Renommer le fichier EC .unl
		if(status != null && status.equals(TransactionStatus.PROCESSING)) exportFileName = nameECFact; //exportFileName = nameECFact + exportFileName.substring(exportFileName.length() - 15, exportFileName.length());
		else if(status != null && status.equals(TransactionStatus.REGUL)) exportFileName = nameECRegul; //exportFileName = nameECRegul + exportFileName.substring(exportFileName.length() - 15, exportFileName.length());
					
		if(status==null) {
			PortalInformationHelper.showInformationDialog("Veuillez d'abord charger les operations ‡† comptabiliser", InformationDialog.DIALOG_WARNING);
			status = null;
			return;
		}

		if(fact.isEmpty()) {
			PortalInformationHelper.showInformationDialog("Aucune opÈration ‡† comptabiliser", InformationDialog.DIALOG_WARNING);
			status = null;
			return;
		}
		
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
		if(!StringUtils.isNotBlank(param.getUrlCbsApi())){
			// Affichage de la Boite de dialogue d'information
			PortalInformationHelper.showInformationDialog("Veuillez saisir l'url de l'API CBS !",InformationDialog.DIALOG_INFORMATION);
			// On retourne false
			return ;
		}

		try {
			Map<String, List<?>> map = new HashMap<String, List<?>>();
			FactMonth fac = new FactMonth();
			// Validation de la comptabilisation
//			if(status.equals(TransactionStatus.REGUL)){
			int max = 5000;
			logger.info("TRAITEMENT");
//			if(fact.size()>=max){
//				// Validation de la comptabilisation
//				map = MobileMoneyViewHelper.appManager.validerTFJO2(new ArrayList<Transaction>(fact.subList(0, max)), MobileMoneyViewHelper.getSessionUser().getLogin(), year, month);
//			}
//			else{
				// Validation de la comptabilisation
				map = MobileMoneyViewHelper.appManager.validerTFJO2(fact, MobileMoneyViewHelper.getSessionUser().getLogin(), year, month);
//			}
			
			if(map!=null && map.size()!=0){
				logger.info("OK");
				///logger.info("MAP : "+map);
				ecritures = (List<bkmvti>) map.get("EC");
//				fac = (FactMonth) (map.containsKey("FAC") ? map.get("FAC").get(0) : new FactMonth());
				subs = (List<Subscriber>) map.get("SUBS");
				trx = (List<Transaction>) map.get("TRX");
				trx_regul = (List<Transaction>) map.get("REGU");
			}
			else {
				PortalInformationHelper.showInformationDialog("Veuillez d'abord charger les operations ‡† comptabiliser !!!", InformationDialog.DIALOG_INFORMATION);
				postFile = Boolean.FALSE;
				return;
			}
			logger.info("NBRE TRX IN: "+fact.size());
			logger.info("NBRE TRX / EC : "+trx.size()+" / "+ecritures.size());
			logger.info("EXTRACTION EC EXCEL");
			// Extraction des EC
			extraireECRappochement();
			logger.info("EXTRACTION EC EXCEL OK!");
			
//			}
//			else MobileMoneyViewHelper.appManager.validerTFJO(fact, MobileMoneyViewHelper.getSessionUser().getLogin(), year, month);
			
//			MobileMoneyViewHelper.appManager.validerTFJO(fact, MobileMoneyViewHelper.getSessionUser().getLogin(), year, month);
				
		//	fact.clear();
			list = new ArrayList<Transaction>();
			montant = 0d;
			nombre = 0;
			numPage = 0;
			nbPages = 0;
			precedent = false;
			suivant = false;
			
			fac = MobileMoneyViewHelper.appManager.visualiserRapportCompensation(ecritures, MobileMoneyViewHelper.getSessionUser().getLogin(), 
							null,MobileMoneyTools.getDownloadDir() + File.separator + exportFileName);
			
			if(fac == null ){

				PortalInformationHelper.showInformationDialog("Aucune ecriture de facturation postÈe dans le core banking !!!", InformationDialog.DIALOG_ERROR);
				fact.clear();
				return;

			}else{
				
				 if(!param.getCbsServices()) {
					//Transmission du fichier vers Amplitude	
					String user = param.getUserLoginServerAmpli().trim();
					String pwd = param.getUserPasswordServerAmpli();
		
					boolean transfert = CSftp.send(MobileMoneyTools.getDownloadDir() + File.separator + exportFileName, user, param.getIpAdressAmpli().trim(), param.getFilePathAmpli().trim(), "", pwd);
					if(transfert == false){
						PortalInformationHelper.showInformationDialog("Echec du transfert du fichier vers le core banking!", InformationDialog.DIALOG_INFORMATION);
						fact.clear();
						return;
					}		
				 }
				
				
					//transactions = MobileMoneyViewHelper.appManager.annulerEves(transactions);
					logger.info("TRANSFERT OK!");
					MobileMoneyViewHelper.appManager.majSoldeFact(trx, subs);
					logger.info("MAJ OK!");
				
					logger.info("IMPRESSION ");
					//logger.info("Fin Generation des Rapports de Controle");
					imprimerRapportECFacturation(fac);
					logger.info("IMPRESSION OK!");
	
					ecritures = new ArrayList<>();
					
					// Msg d'information
					String msg = status.equals(TransactionStatus.REGUL) ? "de rÈgularisation" : "de facturation  "; // du mois de +monthString;
					PortalInformationHelper.showInformationDialog(trx.size()+" abonnements facturÈs, "+trx_regul.size()+" en regul sur "+fact.size()+" transactions\n "+msg+" ont ÈtÈ postÈes avec succËs dans le Core Banking!", InformationDialog.DIALOG_INFORMATION);
					
					num = 1;
					postFile = Boolean.FALSE;
					fact.clear();
			}
			
		} catch(Exception ex) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(ex);
		}
		status = null;
	}
	

//	/**
//	 * Validation de la Comptabilisation
//	 */
//	public void validerTFJO(){
//
//		equilibreCptes = new ArrayList<EquilibreComptes>();
//		
//		if(status==null) {
//			PortalInformationHelper.showInformationDialog("Veuillez d'abord charger les op√©rations √† comptabiliser", InformationDialog.DIALOG_WARNING);
//			status = null;
//			return;
//		}
//
//		if(fact.isEmpty()) {
//			PortalInformationHelper.showInformationDialog("Aucune op√©ration √† comptabiliser", InformationDialog.DIALOG_WARNING);
//			status = null;
//			return;
//		}
//
//		try {
//			
//			// Validation de la comptabilisation
////			if(status.equals(TransactionStatus.REGUL)){
//				int max = 5000;
//				if(fact.size()>=max){
//					// Validation de la comptabilisation
//					MobileMoneyViewHelper.appManager.validerTFJO(new ArrayList<Transaction>(fact.subList(0, max)), MobileMoneyViewHelper.getSessionUser().getLogin(), year, month);
//				}
//				else{
//					// Validation de la comptabilisation
//					MobileMoneyViewHelper.appManager.validerTFJO(fact, MobileMoneyViewHelper.getSessionUser().getLogin(), year, month);
//				}
////			}
////			else MobileMoneyViewHelper.appManager.validerTFJO(fact, MobileMoneyViewHelper.getSessionUser().getLogin(), year, month);
//			
////			MobileMoneyViewHelper.appManager.validerTFJO(fact, MobileMoneyViewHelper.getSessionUser().getLogin(), year, month);
//				
//			fact.clear();
//			list = new ArrayList<Transaction>();
//			montant = 0d;
//			nombre = 0;
//			numPage = 0;
//			nbPages = 0;
//			precedent = false;
//			suivant = false;
//
//			//logger.info("Generation des Rapports de Controle");
//			// Recuperation des Rapports de Controle
//			if(status.equals(TransactionStatus.REGUL)){
//				equilibre = MobileMoneyViewHelper.appManager.getRapportEquilibre(MobileMoneyViewHelper.getSessionUser().getLogin(), MobileMoneyTools.RGUL_MAC);
//				doublon = MobileMoneyViewHelper.appManager.getRapportDoublon(MobileMoneyViewHelper.getSessionUser().getLogin(), MobileMoneyTools.RGUL_MAC);
//				nbAbo = MobileMoneyViewHelper.appManager.getTotalAbonComptabilises(MobileMoneyViewHelper.getSessionUser().getLogin(), MobileMoneyTools.RGUL_MAC);
//				mntComs = MobileMoneyViewHelper.appManager.getTotalComsAbonComptabilises(MobileMoneyViewHelper.getSessionUser().getLogin(), MobileMoneyTools.RGUL_COM);
//				mntTax = MobileMoneyViewHelper.appManager.getTotalTaxAbonComptabilises(MobileMoneyViewHelper.getSessionUser().getLogin(), MobileMoneyTools.RGUL_TAX);
//			}else {
//				equilibre = MobileMoneyViewHelper.appManager.getRapportEquilibre(MobileMoneyViewHelper.getSessionUser().getLogin(), MobileMoneyTools.FACT_MAC);
//				doublon = MobileMoneyViewHelper.appManager.getRapportDoublon(MobileMoneyViewHelper.getSessionUser().getLogin(), MobileMoneyTools.FACT_MAC);
//				nbAbo = MobileMoneyViewHelper.appManager.getTotalAbonComptabilises(MobileMoneyViewHelper.getSessionUser().getLogin(), MobileMoneyTools.FACT_MAC);
//				mntComs = MobileMoneyViewHelper.appManager.getTotalComsAbonComptabilises(MobileMoneyViewHelper.getSessionUser().getLogin(), MobileMoneyTools.FACT_COM);
//				mntTax = MobileMoneyViewHelper.appManager.getTotalTaxAbonComptabilises(MobileMoneyViewHelper.getSessionUser().getLogin(), MobileMoneyTools.FACT_TAX);
//			}
//			equilibreCptes.add(MobileMoneyViewHelper.appManager.getRapportEquilibreCpteClient());
//			equilibreCptes.add(MobileMoneyViewHelper.appManager.getRapportEquilibreCpteDAP());
//			equilibreCptes.add(MobileMoneyViewHelper.appManager.getRapportEquilibreCpteFloatMTN());
//			
//			/*equilibre = MobileMoneyViewHelper.appManager.getRapportEquilibre();
//			doublon = MobileMoneyViewHelper.appManager.getRapportDoublon();
//			nbAbo = MobileMoneyViewHelper.appManager.getTotalAbonComptabilises();
//			mntComs = MobileMoneyViewHelper.appManager.getTotalComsAbonComptabilises();
//			mntTax = MobileMoneyViewHelper.appManager.getTotalTaxAbonComptabilises();*/
//			
//			//logger.info("Fin Generation des Rapports de Controle");
//			imprimerRapportFacturation();
//			
//			// Msg d'information
//			String msg = status.equals(TransactionStatus.REGUL) ? "de r√©gularisation" : "de facturation  "; // du mois de +monthString;
//			PortalInformationHelper.showInformationDialog("Les √©critures comptables "+msg+" ont √©t√© post√©es avec succ√®s dans le Core Banking!", InformationDialog.DIALOG_INFORMATION);
//			
//			num = 1;
//
//		} catch(Exception ex) {
//
//			// Traitement de l'exception
//			PortalExceptionHelper.threatException(ex);
//		}
//		status = null;
//	}
		
	
	
	/**
	 * Imprime le rapport de la facturation
	 */
	public void imprimerRapportECFacturation(FactMonth fac){
		// Renommer le fichier
		if(status != null && status.equals(TransactionStatus.PROCESSING)) rapportFactFileName = nameRapportFact + rapportFactFileName.substring(rapportFactFileName.length() - 15, rapportFactFileName.length());
		else if(status != null && status.equals(TransactionStatus.REGUL)) rapportFactFileName = nameRapportRegul + rapportFactFileName.substring(rapportFactFileName.length() - 15, rapportFactFileName.length());
		
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
				map.put("titre", (status.equals(TransactionStatus.PROCESSING) ? "ABONNEMENTS" : "REGUL ABONNEMENTS"));

				// Affichage du rapport de traitement 
				List<FactMonth> data = new ArrayList<FactMonth>();
				data.add(fac);
				 
				MobileMoneyTools.exportReportToPDFFile( MobileMoneyTools.getReportsDir().concat("RapportCompense.jasper"), map, data, MobileMoneyTools.getDownloadDir()+rapportFactFileName);
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
			
			File f = new File(MobileMoneyTools.getDownloadDir()+rapportFactFileName);
			logger.info("RAPPORT FACT/REGULS FILE PATH : "+f.getAbsolutePath()+" SIZE : "+f.length());
			DeplacerFichier(f);

			//Suppression Rapport TFJO
			f.delete();


			//Deplacer File EC
			File fec = new File(MobileMoneyTools.getDownloadDir() + File.separator + exportFileName);
			logger.info("FICHIER INTEGRATIO FACT/REGULS FILE PATH : "+fec.getAbsolutePath()+" SIZE : "+fec.length());
			DeplacerFichier(fec);
			
			//Deplacer File RAPP
			File frapp = new File(MobileMoneyTools.getDownloadDir() + File.separator + ecFileNameRapp);
			logger.info("RAPPORT EC RAPPROCHEMENT FILE PATH : "+frapp.getAbsolutePath()+" SIZE : "+frapp.length());
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
	
	
	
	/**
	 * Imprime le rapport de la facturation
	 */
	public void imprimerRapportFacturation(){
		//logger.info("Impression des Rapports de Controle");
		try{
			//logger.info("AVANT");
			if(equilibre == null || equilibre.isEmpty()) return;
			//logger.info("APRES");
			// Recuperation du visualisateur d'etats dans le FacesContext
			ReportViewerDialog viewer = (ReportViewerDialog) MobileMoneyViewHelper.getSessionManagedBean("reportViewerDialog");

			// initialisation du visualisateur
			if(viewer != null) {
				//logger.info("Impression des Rapports de Controle");
				// Lecture du Type mime du fichier a afficher
				viewer.setMimeType(WebResourceManager.mimes.get("pdf"));

				// Initialisation de la map des parametres de l'etat
				HashMap<Object, Object> map = new HashMap<Object, Object>();

				//map.put("logoAFB", MobileMoneyTools.getLogoAFB());
				//map.put("logo", MobileMoneyTools.getLogoEntete());
				//map.put("logoMoMo", MobileMoneyTools.getLogoMoMo());
				map.put("SUBREPORT_DIR", MobileMoneyTools.getReportsDir());
                map.put("codeUser", MobileMoneyViewHelper.getSessionUser().getLogin());
                map.put("equilibre", equilibre);
				map.put("doublon", doublon);
				map.put("equilibreCptes", equilibreCptes);
				
				map.put("nbAbo", nbAbo);
				map.put("mntComs", mntComs);
				map.put("mntTax", mntTax);
				
				MobileMoneyTools.exportReportToPDFFile( MobileMoneyTools.getReportsDir().concat("RapportFacturation.jasper"), map, new ArrayList<>(), MobileMoneyTools.getDownloadDir()+rapportFactFileName);
				//logger.info("Impression OK");
				//logger.info("Affichage des Rapports de Controle");
				// Lecture du flux de donnees
				viewer.setStreamData( MobileMoneyTools.getReportPDFBytes( MobileMoneyTools.getReportsDir().concat("RapportFacturation.jasper"), map, new ArrayList<>())  );
				
				// Ouverture du Visualisateur
				viewer.open();
				//logger.info("Affichage OK");
			}

		}catch(Exception ex) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(ex);
			ex.printStackTrace();

		}

	}
	

	/**
	 * Imprime le rapport de la simulation
	 */
	public void imprimerRapportSimulation(){

		try{

			if(fact == null || fact.isEmpty()) return;

			// Recuperation du visualisateur d'etats dans le FacesContext
			ReportViewerDialog viewer = (ReportViewerDialog) MobileMoneyViewHelper.getSessionManagedBean("reportViewerDialog");

			// initialisation du visualisateur
			if(viewer != null) {

				// Lecture du Type mime du fichier a afficher
				viewer.setMimeType(WebResourceManager.mimes.get("pdf"));

				// Initialisation de la map des parametres de l'etat
				HashMap<Object, Object> map = new HashMap<Object, Object>();

				map.put("logo", MobileMoneyTools.getLogoEntete());
				map.put("SUBREPORT_DIR", MobileMoneyTools.getReportsDir());
				map.put("codeUser", MobileMoneyViewHelper.getSessionUser().getLogin());

				// Lecture du flux de donnees
				viewer.setStreamData( MobileMoneyTools.getReportPDFBytes( MobileMoneyTools.getReportsDir().concat("SimulationFacturation.jasper"), map, fact)  );

				// Ouverture du Visualisateur
				viewer.open();
			}

		}catch(Exception ex) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(ex);
			ex.printStackTrace();

		}

	}

	/**
	 * Extrait les ecritures comptables des transactions selectionnees sous excel
	 */
	public void extraireECSousExcel(){

		if(fact.isEmpty()) {
			PortalInformationHelper.showInformationDialog("La liste est vide! Impossible d'effectuer cette action.", InformationDialog.DIALOG_WARNING);
			return;
		}

		try {

			// Export des donnees de comptabilisation
			MobileMoneyViewHelper.appManager.exportComptabilisationIntoExcelFile(fact, exportFileName);

			// Msg d'information
			PortalInformationHelper.showInformationDialog("Les √©critures comptables ont √©t√© extraites! Veuillez cliquer sur le lien pour t√©l√©charger le fichier.", InformationDialog.DIALOG_INFORMATION);
			num = 1; fileIsGenerated = true;

		} catch(Exception ex) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(ex);
		}
	}
	
	
	/**
	 * @return the list
	 */
	public List<Transaction> getList() {
		return list;
	}
	

	public Double getMontant() {
		return montant;
	}

	public void setMontant(Double montant) {
		this.montant = montant;
	}

	public Integer getNombre() {
		return nombre;
	}

	public void setNombre(Integer nombre) {
		this.nombre = nombre;
	}
	
	
	public boolean isFormRapportOpen(){
		return !equilibre.isEmpty();
	}

	/**
	 * @return the num
	 */
	public int getNum() {
		return num++;
	}

	public String getExportFileName() {
		return exportFileName;
	}
	
	public String getecFileNameRapp() {
		return ecFileNameRapp;
	}

	/**
	 * Determine si le fichier des ecritures cptables a ete genere
	 * @return true si le fichier des ecritures comptables a tet genere, false sinon
	 */
	public boolean isExportedFileExist() {
		return new File( MobileMoneyTools.getDownloadDir() + File.separator + ecFileNameRapp).exists() && fileIsGenerated;
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

	
	/**
	 * @return the nbAbo
	 */
	public int getNbAbo() {
		return nbAbo;
	}

	public String getFormattedMntComs() {
		return MoMoHelper.espacement(mntComs);
	}

	public String getFormattedMntTaxes() {
		return MoMoHelper.espacement(mntTax);
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
	 * @return the nbParPage
	 */
	public int getNbParPage() {
		return nbParPage;
	}

	/**
	 * @param nbParPage the nbParPage to set
	 */
	public void setNbParPage(int nbParPage) {
		this.nbParPage = nbParPage;
	}

	/**
	 * @return the nbPages
	 */
	public int getNbPages() {
		return nbPages;
	}

	/**
	 * @param nbPages the nbPages to set
	 */
	public void setNbPages(int nbPages) {
		this.nbPages = nbPages;
	}

	/**
	 * @return the numPage
	 */
	public int getNumPage() {
		return numPage;
	}

	/**
	 * @return the monthString
	 */
	public String getMonthString() {
		return monthString;
	}

	/**
	 * @param monthString the monthString to set
	 */
	public void setMonthString(String monthString) {
		this.monthString = monthString;
	}

	/**
	 * @return the month
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * @param month the month to set
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}

	/**
	 * @return the precedent
	 */
	public boolean isPrecedent() {
		if(numPage>1){
			precedent = true;
		}else precedent = false;
		return precedent;
	}

	/**
	 * @param precedent the precedent to set
	 */
	public void setPrecedent(boolean precedent) {
		this.precedent = precedent;
	}

	/**
	 * @return the suivant
	 */
	public boolean isSuivant() {
		if((nbPages)>numPage){
			suivant = true;
		}else suivant = false;
		return suivant;
	}

	/**
	 * @param suivant the suivant to set
	 */
	public void setSuivant(boolean suivant) {
		this.suivant = suivant;
	}

}
