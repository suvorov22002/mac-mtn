package com.afb.dpd.mobilemoney.jsf.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.enums.StatusAbon;
import com.afb.dpd.mobilemoney.jpa.enums.StatutContrat;
import com.afb.dpd.mobilemoney.jpa.tools.ClientProduit;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

import afb.dsi.dpd.portal.jpa.entities.User;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

/**
 * Formulaire de souscriptions
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class FrmSouscriptionsTemp extends AbstractPortalForm{
	
	private static Log logger = LogFactory.getLog(FrmSouscriptionsTemp.class);
	private Parameters param;

	private List<Subscriber> souscriptions = new ArrayList<Subscriber>();

	private String txtDateDeb = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
	
	private String txtDateFin = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
	
	private String txtStatut;
	
	private String txtSearchCustId;
	
	private String txtSearchTel;
	
	private Subscriber selectedSubscriber = null;
	
	private Subscriber cancelSubscriber = null;
	
	private List<SelectItem> statutItems = new ArrayList<SelectItem>();
	
	private int num = 0;
	
	
	/**
	 * Default Constructor
	 */
	public FrmSouscriptionsTemp() {}

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
		
		// Chargement des items de statut
		statutItems = new ArrayList<SelectItem>();
		///statutItems.add( new SelectItem(null, " ") );
		for(StatutContrat s : StatutContrat.getValues()){
			if(s.equals(StatutContrat.ACTIF_CBS) || s.equals(StatutContrat.WAITING)) continue;
			statutItems.add(new SelectItem(s,s.getValue()));
		}
				
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	public String getTitle() {
		return "Souscriptions Temporaires";
	};
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose();
		selectedSubscriber = null; cancelSubscriber = null;
		if(souscriptions != null) souscriptions.clear(); txtStatut = null; txtSearchCustId = null;
		statutItems = new ArrayList<SelectItem>();
	}

	public void filterSouscriptions() {
		souscriptions.clear();
		try {
			
			// Initialisation d'un conteneur de restrictions
			RestrictionsContainer rc = RestrictionsContainer.getInstance();
			if(txtSearchTel != null && !txtSearchTel.isEmpty()) {
				// Filtre des souscriptions
				Subscriber sub = MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber("237"+txtSearchTel);
				if(sub != null) {
					souscriptions.add(sub);
					num = 1;
					return;
				}
				else {
					PortalInformationHelper.showInformationDialog("Numéro de telephone incorrect ou absent!", InformationDialog.DIALOG_WARNING);
					return;
				}
					
			}
			
			// Ajout de la restriction sur le code du client
			if(txtSearchCustId != null && !txtSearchCustId.isEmpty()) rc.add(Restrictions.eq("customerId", txtSearchCustId ));
			else {
				PortalInformationHelper.showInformationDialog("Veuillez indiquer le matricule client ou un numéro de telephone.", InformationDialog.DIALOG_WARNING);
				return;
			}
			System.out.println("Code client = " + txtSearchCustId);
			
			// Filtre des souscriptions
			souscriptions = MobileMoneyViewHelper.appManager.filterSubscriptions(rc, null);
			num = 1;
			
		} catch(Exception e) {
			
			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
		}
		
	}
	
	public void processSave() {
		// Enregistrement des modifications effectuees
		try {
			
			// Filtre des souscriptions
			User user = MobileMoneyViewHelper.getSessionUser();
			for(Subscriber s : souscriptions){
				                                   
				if(s.getStatus().equals(StatutContrat.SUSPENDU)){
			        MobileMoneyViewHelper.appManager.annulerSouscriptionTemp(s.getId(), MobileMoneyViewHelper.getSessionUser().getLogin());
				}
				else if(s.getStatus().equals(StatutContrat.ACTIF)){
					
					ClientProduit client = new ClientProduit();
					client.setMatricule(s.getFirstAccount().split("-")[1].substring(0,7));
					client.setProduit("MoMo-06");
					try {
						String statut = MobileMoneyViewHelper.appManager.statusAbon(client);
						statut = statut.replace('"', ' ').trim();
						client.setStatut(StatusAbon.valueOf(StatusAbon.class, String.valueOf(statut)));
						if(client.getStatut().equals(StatusAbon.FACTURE)) {
							s.setStatus(StatutContrat.ACTIF_CBS);
						}
					}
					catch(Exception e) {
						s.setStatus(StatutContrat.ACTIF);
						e.printStackTrace();
					}
					
					
					
//					s.setUtiValid(user.getLogin());
//					s.setDateValid(new Date());
					s.setSuspensTemp((s.getSuspensTemp()!=null ? s.getSuspensTemp()+"_":"")+MobileMoneyViewHelper.getSessionUser().getLogin()+"|"+MobileMoneyViewHelper.appManager.now()+"|"+"A" );
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
	 * @return the txtStatut
	 */
	public String getTxtStatut() {
		return txtStatut;
	}

	/**
	 * @param txtStatut the txtStatut to set
	 */
	public void setTxtStatut(String txtStatut) {
		this.txtStatut = txtStatut;
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
	public String getTxtSearchTel() {
		return txtSearchTel;
	}

	/**
	 * @param txtSearchCodeUti the txtSearchCodeUti to set
	 */
	public void setTxtSearchTel(String txtSearchTel) {
		this.txtSearchTel = txtSearchTel;
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
	 * @return the itemsStatuts
	 */
	public List<SelectItem> getStatutItems() {
		return statutItems;
	}

	/**
	 * @param itemsStatuts the itemsStatuts to set
	 */
	public void setStatutItems(List<SelectItem> statutItems) {
		this.statutItems = statutItems;
	}
}
