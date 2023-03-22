package com.afb.dpd.mobilemoney.jsf.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

import com.afb.dpd.mobilemoney.jpa.entities.PlageTransactions;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.enums.StatutContrat;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jpa.tools.MoMoHelper;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.models.ReportViewerDialog;
import com.afb.dpd.mobilemoney.jsf.servlet.WebResourceManager;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyTools;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.yashiro.persistence.utils.dao.tools.OrderContainer;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

public class FrmProfilSubscriber extends AbstractPortalForm{


	private List<Subscriber> souscriptions = new ArrayList<Subscriber>();

	private List<SelectItem> itemsStatuts = new ArrayList<SelectItem>();

	private String txtDateDeb = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());

	private String txtDateFin = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());

	private String txtSearchClient;

	private String txtSearchCustId;

	private Subscriber selectedSubscriber = null;

	private Subscriber selectedRecu = null;

	private int num = 0;

	private Long idAnnulation;

	/**
	 * Default Constructor
	 */
	public FrmProfilSubscriber() {}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		// TODO Auto-generated method stub
		super.initForm();
		List<PlageTransactions> plgs = MobileMoneyViewHelper.appManager.filterPlageTransactions();
		itemsStatuts = new ArrayList<SelectItem>();
		itemsStatuts.add(new SelectItem(null,"Default"));
		for(PlageTransactions s : plgs){
			itemsStatuts.add(new SelectItem(s.getId(),s.getProfilName()));
		}

	}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	public String getTitle() {
		return "Profils des souscriptions";
	};

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose();
		selectedRecu = null; selectedSubscriber = null;
		if(souscriptions != null) souscriptions.clear(); txtSearchClient = null; txtSearchCustId = null;
	}

	public void filterSouscriptions() {

		try {

			// Initialisation d'un conteneur de restrictions
			RestrictionsContainer rc = RestrictionsContainer.getInstance();
			
			rc.add(Restrictions.eq("status", StatutContrat.ACTIF ));
			
			// Ajout de la restriction sur la periode de date
			if(txtDateDeb != null && txtDateFin != null && !txtDateDeb.isEmpty() && !txtDateFin.isEmpty()) rc.add(Restrictions.between("date", MoMoHelper.sdf.parse(txtDateDeb.concat(" 00:01") ), MoMoHelper.sdf.parse(txtDateFin.concat(" 23:58") ) ));

			// Ajout de la restriction sur le nom du client
			if(txtSearchClient != null && !txtSearchClient.isEmpty()) rc.add(Restrictions.like("customerName", "%".concat(txtSearchClient).concat("%") ));

			// Ajout de la restriction sur le code du client
			if(txtSearchCustId != null && !txtSearchCustId.isEmpty()) rc.add(Restrictions.eq("customerId", txtSearchCustId ));

			// Initialisation d'un conteneur d'ordres
			OrderContainer orders = OrderContainer.getInstance().add(Order.desc("date")).add(Order.asc("customerName"));

			// Filtre des souscriptions
			souscriptions = MobileMoneyViewHelper.appManager.filterSubscriptions(rc, orders); 

			for(Subscriber s : souscriptions){
				s.initProfilId();
			}
			
			//System.out.println("Nbre de souscriptions trouvees = " + souscriptions.size());

			// Initialisation du compteur
			num = 1;

		} catch(Exception e) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
		}

	}



	public void filterAbonnesNonFactures() {

		try {

			// Filtre des souscriptions
			souscriptions = MobileMoneyViewHelper.appManager.findAllSubscriberNonFactures(); 

			// Initialisation du compteur
			num = 1;

		} catch(Exception e) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
		}

	}


	public void processSave() {
		String msg = "";
		String subject = "";
		String title = "";
		
		try {

			// Filtre des souscriptions
			for(Subscriber s : souscriptions){
				PlageTransactions pl = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(PlageTransactions.class,Long.valueOf(s.getProfilId()),null);
				if(s.getProfilId() != null){
					//PlageTransactions pl = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(PlageTransactions.class,Long.valueOf(s.getProfilId()),null);
					if(pl != null){
						s.setProfil(pl);
						s.setProfilId(pl.getId().toString());
						MobileMoneyViewHelper.appDAOLocal.update(s);
					}
				}else{
					s.setProfil(null);
					s.setProfilId(null);
					MobileMoneyViewHelper.appDAOLocal.update(s);
				}
				s.initProfilId();
				
				msg = "Le profil du client abonné avec le n° "+s.getFirstPhone()+" a été modifié vers: "+pl.getProfilName()+".";
				subject = "Alerte modification profil";
				title = "PROFIL";
				MobileMoneyViewHelper.appManager.sendSimpleMail(msg, subject, title);
			}
			

			// Message d'information
			PortalInformationHelper.showInformationDialog("Enregistré avec succès!", InformationDialog.DIALOG_SUCCESS);

		} catch(Exception e) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
		}

	}

	public void facturerAbonnesNonFactures() {

		try {

			// Filtre des souscriptions
			MobileMoneyViewHelper.appManager.facturerListSubscribers(souscriptions); 

			// Initialisation du compteur
			num = 1;

			// Message d'information
			PortalInformationHelper.showInformationDialog("Facturation avec succès!", InformationDialog.DIALOG_SUCCESS);

		} catch(Exception e) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
		}

	}


	/**
	 * Methode d'annulation du contrat de souscription
	 */
	public void annulerSouscription() {

		try {

			// Annulation
			MobileMoneyViewHelper.appManager.annulerSouscription(idAnnulation, MobileMoneyViewHelper.getSessionUser().getLogin());

			// MAJ de l'objet correspondant dans la liste
			for(Subscriber s : souscriptions) {
				if(s.getId().equals(idAnnulation)) {
					s.setStatus(StatutContrat.SUSPENDU);
					break;
				}
			}

			// Message d'information
			PortalInformationHelper.showInformationDialog("Le contrat de souscription a été annulée avec succès!", InformationDialog.DIALOG_SUCCESS);

		} catch(Exception e) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
		}

	}


	/**
	 * Imprime 
	 */
	public void printRecu() {

		try{

			Transaction transaction = MobileMoneyViewHelper.appManager.findTransactionBySubscriber(selectedRecu.getId()) ;

			if(transaction == null) transaction = new Transaction(TypeOperation.SUBSCRIPTION, selectedRecu, 0d, selectedRecu.getAccounts().get(0), selectedRecu.getPhoneNumbers().get(0), "");

			List<Transaction> data = new ArrayList<Transaction>(); data.add(transaction);

			// Recuperation du visualisateur d'etats dans le FacesContext
			ReportViewerDialog viewer = (ReportViewerDialog) MobileMoneyViewHelper.getSessionManagedBean("reportViewerDialog");

			// initialisation du visualisateur
			if(viewer != null) {

				// Lecture du Type mime du fichier a afficher
				viewer.setMimeType(WebResourceManager.mimes.get("pdf"));

				// Initialisation de la map des parametres de l'etat
				HashMap<Object, Object> map = new HashMap<Object, Object>();

				map.put("logoAFB", MobileMoneyTools.getLogoAFB());
				map.put("logoMoMo", MobileMoneyTools.getLogoMoMo());
				map.put("logo", MobileMoneyTools.getLogoEntete());
				map.put("SUBREPORT_DIR", MobileMoneyTools.getReportsDir());
				map.put("codeUser", MobileMoneyViewHelper.getSessionUser().getLogin());

				// Lecture du flux de donnees
				viewer.setStreamData( MobileMoneyTools.getReportPDFBytes( MobileMoneyTools.getReportsDir().concat("recuSouscription.jasper"), map, data)  );

				// Ouverture du Visualisateur
				viewer.open();
			}

		}catch(Exception ex) {

			// Traitement de l'exception
			//PortalExceptionHelper.threatException(ex);
			ex.printStackTrace();

		}

	}


	/**
	 * Numeroteur de lignes dans la grille
	 * @return numero de la ligne
	 */
	public int getNum() {
		return num++;
	}

	/**
	 * @return liste des souscriptions
	 */
	public List<Subscriber> getSouscriptions() {
		return souscriptions;
	}

	/**
	 * @return liste des itemsStatuts
	 */
	public List<SelectItem> getItemsStatuts() {
		return itemsStatuts;
	}

	/**
	 * @param itemsStatuts the itemsStatuts to set
	 */
	public void setItemsStatuts(List<SelectItem> itemsStatuts) {
		this.itemsStatuts = itemsStatuts;
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
	 * @return the txtSearchClient
	 */
	public String getTxtSearchClient() {
		return txtSearchClient;
	}

	/**
	 * @param txtSearchClient the txtSearchClient to set
	 */
	public void setTxtSearchClient(String txtSearchClient) {
		this.txtSearchClient = txtSearchClient;
	}

	/**
	 * @return the txtSearchCustId
	 */
	public String getTxtSearchCustId() {
		return txtSearchCustId;
	}

	/**
	 * @param txtSearchCustId the txtSearchCustId to set
	 */
	public void setTxtSearchCustId(String txtSearchCustId) {
		this.txtSearchCustId = txtSearchCustId;
	}

	/**
	 * @return the selectedSubscriber
	 */
	public Subscriber getSelectedSubscriber() {
		return selectedSubscriber;
	}

	/**
	 * @return the idAnnulation
	 */
	public Long getIdAnnulation() {
		return idAnnulation;
	}

	/**
	 * @param idAnnulation the idAnnulation to set
	 */
	public void setIdAnnulation(Long idAnnulation) {
		this.idAnnulation = idAnnulation;

		if(this.idAnnulation != null) annulerSouscription();

	}

	/**
	 * @param selectedSubscriber the selectedSubscriber to set
	 */
	public void setSelectedSubscriber(Subscriber selectedSubscriber) {
		this.selectedSubscriber = selectedSubscriber;

		if(this.selectedSubscriber != null){

			try {

				// Envoi du PIN par SMS
				MobileMoneyViewHelper.appManager.sendCodePINBySMS(this.selectedSubscriber);

				// Message d'information
				PortalInformationHelper.showInformationDialog("Le PIN Banque du client lui a été transféré par SMS!", InformationDialog.DIALOG_SUCCESS);

			} catch(Exception ex) {

				// Traitement de l'exception
				PortalExceptionHelper.threatException(ex);
			}

		}
	}

	/**
	 * @return the selectedRecu
	 */
	public Subscriber getSelectedRecu() {
		return selectedRecu;
	}

	/**
	 * @param selectedRecu the selectedRecu to set
	 */
	public void setSelectedRecu(Subscriber selectedRecu) {
		this.selectedRecu = selectedRecu;

		if(this.selectedRecu != null) printRecu();

	}


}
