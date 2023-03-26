package com.afb.dpd.mobilemoney.jsf.forms;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.enums.StatusAbon;
import com.afb.dpd.mobilemoney.jpa.enums.StatutContrat;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jpa.tools.ClientProduit;
import com.afb.dpd.mobilemoney.jpa.tools.MoMoHelper;
import com.afb.dpd.mobilemoney.jsf.dto.Unlink;
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

import afb.dsi.dpd.portal.jpa.entities.User;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

/**
 * FrmValidSubscriber
 * @author Owner
 *
 */
public class FrmValidSubscriber extends AbstractPortalForm{

	private Parameters param;
	
	private static Log logger = LogFactory.getLog(FrmValidSubscriber.class);

	private List<Subscriber> souscriptions = new ArrayList<Subscriber>();
	
	private List<SelectItem> itemsStatuts = new ArrayList<SelectItem>();

	private String txtDateDeb = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
	
	private String txtDateFin = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
	
	private String txtSearchClient;
	
	private String txtSearchCustId;
	
	private Subscriber selectedSubscriber = null;
	
	private Subscriber selectedRecu = null;
	
	private Subscriber selectedSub = null;
	
	String urlSignature = null;
	
	private int num = 0;
	
	private Long idAnnulation;
	
	/**
	 * Default Constructor
	 */
	public FrmValidSubscriber() {}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		// TODO Auto-generated method stub
		super.initForm();
		itemsStatuts = new ArrayList<SelectItem>();
		for(StatutContrat s : StatutContrat.getValues()){
			if(s.equals(StatutContrat.ACTIF_CBS)) continue;
			itemsStatuts.add(new SelectItem(s,s.getValue()));
		}
		// Recuperation des parametres
		param = MobileMoneyViewHelper.appManager.findParameters();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	public String getTitle() {
		return "Validation des souscriptions";
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
			
			User user = MobileMoneyViewHelper.getSessionUser();
			if(!StringUtils.equalsIgnoreCase("00006",user.getBranch().getCode())){
				rc.add(Restrictions.eq("age",user.getBranch().getCode()));
			}
			
			rc.add(Restrictions.eq("status", StatutContrat.WAITING ));
			
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
	
	
	public String getSignature(Subscriber subscriber) {
		
		try {
			Calendar cal = new GregorianCalendar();
			cal.setTime(new Date());
			cal.add(Calendar.MINUTE, 5);
			// Recherche de la signature du client
			return MobileMoneyViewHelper.appManager.getLienSig(subscriber.getFirstAccount(), MobileMoneyViewHelper.getSessionUser().getLogin());
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		return null;
	}
	
	
	public void processSave() {
		// Enregistrement des modifications effectuees
		try {

			// Filtre des souscriptions
			User user = MobileMoneyViewHelper.getSessionUser();
			for(Subscriber s : souscriptions){
				                               
				if(s.getStatus().equals(StatutContrat.SUSPENDU)){
					//System.out.println("Annulation de la souscription "+s.getId());
					//Annulation cote MTN
					MomoKYCServiceProxy proxy = new MomoKYCServiceProxy();
			        proxy.setEndpoint(param.getUrlKYCApi());
			        String unlinkage = "";
			       	// Recuperation du resultat du unlinkage depuis la plateforme de MTN
			        unlinkage = proxy.unlinkFinancialResourceInformation(s.getFirstAccount().substring(6,13) + s.getFirstAccount().substring(18), s.getFirstPhone(), null);
			        //unlinkage = proxy.unlinkFinancialResourceInformation(s.getFirstAccount().substring(13).replace("-", ""), s.getFirstPhone(), null);
					System.out.println("RESPONSE UNLINK = "+unlinkage);
					
					Unlink unlink = new Unlink();
			        unlink = MobileMoneyTools.getUnlinkage(unlinkage);
			        
			        // Si on obtient une erreur
					if(unlink.getError()!=null){
						// Annulation deja effectue cote MTN
			        	if(unlink.getError().getErrorcode().contains("COULD_NOT_PERFORM_OPERATION") && unlink.getError().getErrormessage().contains("FRI not found or it has been unlinked already")){
			        		// Annulation cote bank
			        		MobileMoneyViewHelper.appManager.annulerSouscription(s.getId(), MobileMoneyViewHelper.getSessionUser().getLogin());
			        	}
			        	else{
			        		// Initialisation du compteur
			    			num = 1;
			    			
			        		// Message d'information
			    			PortalInformationHelper.showInformationDialog("Erreur : "+unlink.getError().getErrorcode()+" ("+unlink.getError().getErrormessage()+")", InformationDialog.DIALOG_ERROR);
				        	return;
			        	}
					}
					else if(unlink.getValid()!=null){
						MobileMoneyViewHelper.appManager.annulerSouscription(s.getId(), MobileMoneyViewHelper.getSessionUser().getLogin());
					}
				}
				// Activer l'abonnement
				else if(s.getStatus().equals(StatutContrat.ACTIF)){
					//System.out.println("Facturation de la souscription "+s.getId());

					//on vérifie si le client a deja souscrit à un package
				
					ClientProduit client = new ClientProduit();
					client.setMatricule(s.getFirstAccount().split("-")[1].substring(0,7));
					client.setProduit("MoMo-06");
					String statut = MobileMoneyViewHelper.appManager.statusAbon(client);
					
					client.setStatut(StatusAbon.valueOf(StatusAbon.class, String.valueOf(statut)));
					
					if(client.getStatut().equals(StatusAbon.FACTURE)) {
						s.setStatus(StatutContrat.ACTIF_CBS);
					}
				     
					// Facturer la souscription
					MobileMoneyViewHelper.appManager.facturerSouscription(s);
					s.setUtiValid(user.getLogin());
					s.setDateValid(new Date());
					MobileMoneyViewHelper.appDAOLocal.update(s);
				}
								
			}
			// Actualiser la liste des souscriptions
			filterSouscriptions();
			
			// Initialisation du compteur
			num = 1;
			
			// Message d'information
			PortalInformationHelper.showInformationDialog("Enregistré avec succès!", InformationDialog.DIALOG_SUCCESS);

		} catch(Exception e) {

			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
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
	 * @return the itemsStatuts
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

	/**
	 * @return the selectedSub
	 */
	public Subscriber getSelectedSub() {
		return selectedSub;
	}

	/**
	 * @param selectedSub the selectedSub to set
	 */
	public void setSelectedSub(Subscriber selectedSub) {
		this.selectedSub = selectedSub;
		
		User user = MobileMoneyViewHelper.getSessionUser();
		logger.info("user--selected: "+user.getLogin()+" -- "+selectedSub.getUser().getLogin());
		
		if (user.getLogin().equals(selectedSub.getUser().getLogin())) {
			PortalInformationHelper.showInformationDialog("Utilisateur de validation doit être different.", InformationDialog.DIALOG_WARNING);
			return;
		}
		
		if(this.selectedSub != null) {
			urlSignature = getSignature(selectedSub);
			logger.info("Url Signature : "+urlSignature);
			for(Subscriber s : souscriptions){
				logger.info("sub id : "+s.getId()+" / Selected sub id : "+selectedSub.getId());
				if(s.getId()==selectedSub.getId()) {
					logger.info("OK!!!");
					s.setSignatureVerifie(Boolean.TRUE);
					break;
				}
			}
		}
	}

	/**
	 * @return the urlSignature
	 */
	public String getUrlSignature() {
		return urlSignature;
	}

	/**
	 * @param urlSignature the urlSignature to set
	 */
	public void setUrlSignature(String urlSignature) {
		this.urlSignature = urlSignature;
	}
			
}
