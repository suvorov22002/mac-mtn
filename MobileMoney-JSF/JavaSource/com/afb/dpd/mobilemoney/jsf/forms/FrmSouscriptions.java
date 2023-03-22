/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
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
import com.afb.dpi.momo.services.MomoKYCServiceProxy;
import com.yashiro.persistence.utils.dao.tools.OrderContainer;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

/**
 * Formulaire de filtre des souscriptions
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class FrmSouscriptions extends AbstractPortalForm {
	
	private static Log logger = LogFactory.getLog(FrmSouscriptions.class);

	private Parameters param;

	private List<Subscriber> souscriptions = new ArrayList<Subscriber>();

	private String txtDateDeb = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
	
	private String txtDateFin = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
	
	private String txtSearchClient;
	
	private String txtSearchCustId;
	
	private String txtSearchCodeUti;
	
	private Subscriber selectedSubscriber = null;
	
	private Subscriber cancelSubscriber = null;
	
	private Subscriber selectedRecu = null;
	
	private int num = 0;
	
	private Long idAnnulation;
	
	/**
	 * Default Constructor
	 */
	public FrmSouscriptions() {}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		// TODO Auto-generated method stub
		super.initForm();
		
		// Recuperation des parametres
		param = MobileMoneyViewHelper.appManager.findParameters();
				
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	public String getTitle() {
		return "Liste des souscriptions";
	};
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose();
		selectedRecu = null; selectedSubscriber = null; cancelSubscriber = null;
		if(souscriptions != null) souscriptions.clear(); txtSearchClient = null; txtSearchCustId = null; txtSearchCodeUti=null;
	}

	public void filterSouscriptions() {
		
		try {
			
			// Initialisation d'un conteneur de restrictions
			RestrictionsContainer rc = RestrictionsContainer.getInstance();
			
			// Ajout de la restriction sur la periode de date
			if(txtDateDeb != null && txtDateFin != null && !txtDateDeb.isEmpty() && !txtDateFin.isEmpty()) rc.add(Restrictions.between("date", MoMoHelper.sdf.parse(txtDateDeb.concat(" 00:01") ), MoMoHelper.sdf.parse(txtDateFin.concat(" 23:58") ) ));
			
			// Ajout de la restriction sur le nom du client
			if(txtSearchClient != null && !txtSearchClient.isEmpty()) rc.add(Restrictions.like("customerName", "%".concat(txtSearchClient).concat("%") ));
			
			// Ajout de la restriction sur le code du client
			if(txtSearchCodeUti != null && !txtSearchCodeUti.isEmpty()) rc.add(Restrictions.or(Restrictions.like("user.login", "%".concat(txtSearchCodeUti.toUpperCase()).concat("%")), Restrictions.like("utiValid", "%".concat(txtSearchCodeUti.toUpperCase()).concat("%") ) ));
			
			// Ajout de la restriction sur le code du client
			if(txtSearchCustId != null && !txtSearchCustId.isEmpty()) rc.add(Restrictions.eq("customerId", txtSearchCustId ));
			System.out.println("Code client = " + txtSearchCustId);
			// Initialisation d'un conteneur d'ordres
			OrderContainer orders = OrderContainer.getInstance().add(Order.desc("date")).add(Order.asc("customerName"));
			
			// Filtre des souscriptions
			souscriptions = MobileMoneyViewHelper.appManager.filterSubscriptions(rc, orders);
//						
//			// Filtre des souscriptions
//			List<Subscriber> list = MobileMoneyViewHelper.appManager.filterSubscriptionsWithoutAlias(rc, orders);
//			System.out.println("Nbre de souscriptions = " + list.size());
//			for(Subscriber s : list){
//				if(!souscriptions.contains(s)) souscriptions.add(s);
//			}
			
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
			
			// Annulation cote MTN
			MomoKYCServiceProxy proxy = new MomoKYCServiceProxy();
	        proxy.setEndpoint(param.getUrlKYCApi());
	        String unlinkage = "";
	       	// Recuperation du resultat du linkage depuis la plateforme de MTN
	        unlinkage = proxy.unlinkFinancialResourceInformation(cancelSubscriber.getFirstAccount().substring(6,13) + cancelSubscriber.getFirstAccount().substring(18), cancelSubscriber.getFirstPhone(), null);
	        //unlinkage = proxy.unlinkFinancialResourceInformation(cancelSubscriber.getFirstAccount().substring(13).replace("-", ""), cancelSubscriber.getFirstPhone(), null);
			logger.info("RESPONSE UNLINK = "+unlinkage);
			// Si on obtient une erreur
			if(unlinkage.contains("errorResponse") || unlinkage.contains("errorcode")){
				// Recuperer le message d'erreur
				String error = StringUtils.substringBetween(unlinkage, "errorcode=\"", "\"");
	        	logger.info("Erreur : "+error);
	        	if(unlinkage.contains("<arguments") && unlinkage.contains("name=")){
					// Recuperer le message d'erreur
					String name = StringUtils.substringBetween(unlinkage, "name=\"", "\"");
		        	error = error +" ("+name+" : ";
		        }
	        	if(unlinkage.contains("<arguments") || unlinkage.contains("value=")){
					// Recuperer le message d'erreur
					String value = StringUtils.substringBetween(unlinkage, "value=\"", "\"");
					error = error +value+")";
		        }
	        	// Annulation deja effectue cote MTN 
	        	if(error.contains("ACCOUNTHOLDER_NOT_FOUND") || error.contains("ACCOUNTHOLDER_NOT_ACTIVE") || (error.contains("COULD_NOT_PERFORM_OPERATION") && error.contains("FRI not found or it has been unlinked already"))){
	        		// Annulation cote bank
					MobileMoneyViewHelper.appManager.annulerSouscription(cancelSubscriber.getId(), MobileMoneyViewHelper.getSessionUser().getLogin());
					
					// MAJ de l'objet correspondant dans la liste
					for(Subscriber s : souscriptions) {
						if(s.getId().equals(cancelSubscriber.getId())) {
							s.setStatus(StatutContrat.SUSPENDU);
							break;
						}
					}

					// Message d'information
					PortalInformationHelper.showInformationDialog("Le contrat de souscription a été annulée avec succès!", InformationDialog.DIALOG_SUCCESS);
					
	        	}
	        	else{
		        	// Message d'information
		        	PortalInformationHelper.showInformationDialog("Erreur : "+error, InformationDialog.DIALOG_ERROR);
		        	return;
	        	}
	        }
			
			// Si on obtient la reponse attendue
			if(unlinkage.contains("unlinkfinancialresourceinformationresponse")){
				// l'annulation de l'abonnement s'est bien deroulee
							
				// Annulation cote bank
				MobileMoneyViewHelper.appManager.annulerSouscription(cancelSubscriber.getId(), MobileMoneyViewHelper.getSessionUser().getLogin());
				
				// MAJ de l'objet correspondant dans la liste
				for(Subscriber s : souscriptions) {
					if(s.getId().equals(cancelSubscriber.getId())) {
						s.setStatus(StatutContrat.SUSPENDU);
						break;
					}
				}
				
				// Message d'information
				PortalInformationHelper.showInformationDialog("Le contrat de souscription a été annulée avec succès!", InformationDialog.DIALOG_SUCCESS);

			}
			
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
                
                if(selectedRecu.getChoixLangue().equalsIgnoreCase("FR")){
                	// Lecture du flux de donnees
    				viewer.setStreamData( MobileMoneyTools.getReportPDFBytes( MobileMoneyTools.getReportsDir().concat("recuSouscriptionFR.jasper"), map, data) );
                }else{
                	// Lecture du flux de donnees
    				viewer.setStreamData( MobileMoneyTools.getReportPDFBytes( MobileMoneyTools.getReportsDir().concat("recuSouscriptionEN.jasper"), map, data) );
                }
                
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
	 * @return the souscriptions
	 */
	public List<Subscriber> getSouscriptions() {
		return souscriptions;
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
	 * @return the txtSearchCodeUti
	 */
	public String getTxtSearchCodeUti() {
		return txtSearchCodeUti;
	}

	/**
	 * @param txtSearchCodeUti the txtSearchCodeUti to set
	 */
	public void setTxtSearchCodeUti(String txtSearchCodeUti) {
		this.txtSearchCodeUti = txtSearchCodeUti;
	}

	/**
	 * @return the selectedSubscriber
	 */
	public Subscriber getSelectedSubscriber() {
		return selectedSubscriber;
	}

	
	/**
	 * @return the cancelSubscriber
	 */
	public Subscriber getcancelSubscriber() {
		return cancelSubscriber;
	}
	
	
	/**
	 * @param cancelSubscriber the cancelSubscriber to set
	 */
	public void setcancelSubscriber(Subscriber cancelSubscriber) {
		this.cancelSubscriber = cancelSubscriber;
		
		if(this.cancelSubscriber != null) annulerSouscription();
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
