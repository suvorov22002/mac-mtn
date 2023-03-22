package com.afb.dpd.mobilemoney.jsf.forms;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jettison.json.JSONException;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.afb.dpd.mobilemoney.dao.api.exception.DAOAPIException;
import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.Resiliation;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.enums.StatusAbon;
import com.afb.dpd.mobilemoney.jpa.enums.StatutContrat;
import com.afb.dpd.mobilemoney.jpa.enums.StatutService;
import com.afb.dpd.mobilemoney.jpa.tools.ClientProduit;
import com.afb.dpd.mobilemoney.jpa.tools.MoMoHelper;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.afb.dpi.momo.services.MomoKYCServiceProxy;
import com.yashiro.persistence.utils.dao.tools.OrderContainer;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

import afb.dsi.dpd.portal.jpa.entities.User;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

public class FrmPackages extends AbstractPortalForm{
	
	private static Log logger = LogFactory.getLog(FrmPackages.class);

	private List<ClientProduit> cltproduit = new ArrayList<ClientProduit>();
	private List<SelectItem> statutItems = new ArrayList<SelectItem>();
	
	private List<Resiliation> listTraces = new ArrayList<Resiliation>();
	private static Parameters param;
	private int num = 0;
	private String operation;
	
	private String txtDateDeb = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
	private String txtDateFin = PortalHelper.DEFAULT_DATE_FORMAT.format(new Date());
	/**
	 * Default Constructor
	 */
	public FrmPackages() {}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		// TODO Auto-generated method stub
		super.initForm();
		num = 1;
		param = MobileMoneyViewHelper.appManager.findParameters();
		
		// Chargement des items de Postage
		statutItems.add( new SelectItem(null, "-- Choisir --") );
		statutItems.add( new SelectItem("RESILIATION", "Résiliées") );
		statutItems.add( new SelectItem("ABONNEMENT", "Abonnées") );
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	public String getTitle() {
		return "Gestion des packages";
	};
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose();
		num = 1; listTraces.clear();
		txtDateDeb = null;  txtDateFin = null;
		statutItems.clear();
	}
	
	/**
	 * @return the statutItems
	 */
	public List<SelectItem> getStatutItems() {
		return statutItems;
	}

	public List<Resiliation> getListTraces() {
		return listTraces;
	}

	public void setListTraces(List<Resiliation> listTraces) {
		this.listTraces = listTraces;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	public int getNum() {
		return num++;
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
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}

	public void miseAJResiliations() {
		int count_abon = 0;
		List<Resiliation> resiliations = MobileMoneyViewHelper.appDAOLocal.filter(Resiliation.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("dateResiliation", new Date())), null, null, 0, -1);
		HashMap<Object, Object> mapResi = new HashMap<Object, Object>();
		for(Resiliation r : resiliations) {
			mapResi.put(r.getCustomerId(), r);
		}
		
		try {
			// recherche de toutes les resiliations de la veille
			this.cltproduit = MobileMoneyViewHelper.appManager.listAllResiliations();
			logger.info("Total resil: "+cltproduit.size());
			for(ClientProduit c : cltproduit){
				if(StatusAbon.RESILIE.equals(c.getStatut()) && !mapResi.containsKey(c.getMatricule())) {
					// Initialisation d'un conteneur de restrictions
					RestrictionsContainer rc = RestrictionsContainer.getInstance();

					// Ajout de la restriction sur le code du client
					rc.add(Restrictions.eq("customerId", c.getMatricule()));
					
					// Initialisation d'un conteneur d'ordres
					OrderContainer orders = OrderContainer.getInstance().add(Order.desc("date"));

					// Filtre des souscriptions
					List<Subscriber> souscriptions = MobileMoneyViewHelper.appManager.filterSubscriptions(rc, orders);
					//logger.info("************* souscriptions.size() ************ : " + souscriptions.size());
					
					//annulation du contrat de souscription
					for(Subscriber  s : souscriptions) {
						if(param.getEtatServiceSDP().equals(StatutService.TEST)){
							count_abon += annulerSouscriptionTest(s.getId());
						}
						else {
							count_abon += annulerSouscription(s.getId());
						}
							
					}
						
				}
			}
			
			// Message d'information
			PortalInformationHelper.showInformationDialog(count_abon+" souscriptions résiliées", InformationDialog.DIALOG_SUCCESS);
		} catch (IOException | JSONException | DAOAPIException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void miseAJAbonnements() {
		int count_resi = 0;
		List<Resiliation> resiliations = MobileMoneyViewHelper.appDAOLocal.filter(Resiliation.class, null, RestrictionsContainer.getInstance().add(Restrictions.eq("dateResiliation", new Date())), null, null, 0, -1);
		HashMap<Object, Object> mapAbon = new HashMap<Object, Object>();
		for(Resiliation r : resiliations) {
			mapAbon.put(r.getCustomerId(), r);
		}
		
		try {
			// recherche de toutes les resiliations de la veille
			this.cltproduit = MobileMoneyViewHelper.appManager.listAllAbonnements();
			logger.info("Total abon: "+cltproduit.size());
			for(ClientProduit c : cltproduit){
				if(StatusAbon.FACTURE.equals(c.getStatut()) && !mapAbon.containsKey(c.getMatricule())) {
					// Initialisation d'un conteneur de restrictions
					RestrictionsContainer rc = RestrictionsContainer.getInstance();

					// Ajout de la restriction sur le code du client
					rc.add(Restrictions.eq("customerId", c.getMatricule()));
					
					// Initialisation d'un conteneur d'ordres
					OrderContainer orders = OrderContainer.getInstance().add(Order.desc("date"));

					// Filtre des souscriptions
					List<Subscriber> souscriptions = MobileMoneyViewHelper.appManager.filterSubscriptions(rc, orders);
					logger.info("************* souscriptions.size() ************ : " + souscriptions.size());
					
					//annulation du contrat de souscription
					for(Subscriber  s : souscriptions) count_resi += annulerFacturation(s.getId());
				}
			}
			
			// Message d'information
			PortalInformationHelper.showInformationDialog(count_resi+" Abonnés", InformationDialog.DIALOG_SUCCESS);
		} catch (IOException | JSONException | DAOAPIException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void filterResiliations() {
		num = 1;
		try {
			//this.cltproduit = MobileMoneyViewHelper.appManager.listAllResiliations();
			//logger.info(cltproduit);
			num = 1;

			if(txtDateDeb == null || txtDateDeb.isEmpty() || txtDateFin == null || txtDateFin.isEmpty()){
				// Msg Info
				PortalInformationHelper.showInformationDialog("Veuillez saisir toutes les dates de recherche SVP", InformationDialog.DIALOG_INFORMATION);
				// Annulation
				return;
			}

			// Initialisation d'un conteneur de restrictions
			RestrictionsContainer rc = RestrictionsContainer.getInstance().add(Restrictions.between("dateResiliation", MoMoHelper.sdf.parse(txtDateDeb.concat(" 00:01") ), MoMoHelper.sdf.parse(txtDateFin.concat(" 23:58") ) ));
			
			// Ajout de la restriction sur l'etat de postage dans Delta
			if(operation != null) rc.add(Restrictions.eq("typeResiliation", operation ));
			
			OrderContainer orders = OrderContainer.getInstance().add(Order.desc("dateResiliation"));
			
			listTraces = MobileMoneyViewHelper.appManager.filterResiliations(rc, orders);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void filterAbonnements() {
		num = 1;
		try {
			this.cltproduit = MobileMoneyViewHelper.appManager.listAllAbonnements();
		} catch (IOException | JSONException | DAOAPIException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
   
   private int annulerFacturation(Long idAnnulation) {
	   int count = 0;
	   User user = MobileMoneyViewHelper.getSessionUser();
	   Subscriber subscriber = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Subscriber.class,idAnnulation,null);
	   
	   try {
		   if(subscriber.getStatus().equals(StatutContrat.ACTIF_CBS)){
				return 0;
			}else{
				MobileMoneyViewHelper.appManager.annulerFacturation(subscriber.getId(), user.getLogin());
				count = 1;
			}
		   
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 //Trace de la Resiliation
	   Resiliation resi = new Resiliation(null, subscriber.getCustomerId(), new Date(), "ABONNEMENT", user.getLogin());
	   MobileMoneyViewHelper.appDAOLocal.save(resi);
	   
	   return count;
   }
   
   private int annulerSouscription(Long idAnnulation) {
		int count = 0;
	    User user = MobileMoneyViewHelper.getSessionUser();
		Subscriber subscriber = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Subscriber.class,idAnnulation,null);
		
		if(subscriber.getStatus().equals(StatutContrat.SUSPENDU)){
			return 0;
		}else{
			subscriber.setStatus(StatutContrat.ACTIF_CBS);
		}
		
		try {
			
			// Annulation cote MTN
			MomoKYCServiceProxy proxy = new MomoKYCServiceProxy();
	        proxy.setEndpoint(param.getUrlKYCApi());
	        String unlinkage = "";
	       	// Recuperation du resultat du linkage depuis la plateforme de MTN
	        unlinkage = proxy.unlinkFinancialResourceInformation(subscriber.getFirstAccount().substring(6,13) + subscriber.getFirstAccount().substring(18), subscriber.getFirstPhone(), null);
	    
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
					MobileMoneyViewHelper.appManager.annulerSouscription(subscriber.getId(), user.getLogin());
					
					//Trace de la Resiliation
					Resiliation resi = new Resiliation(null, subscriber.getCustomerId(), new Date(), "RESILIATION", user.getLogin());
					MobileMoneyViewHelper.appDAOLocal.save(resi);
					// Message d'information
					PortalInformationHelper.showInformationDialog("Le contrat de souscription a été annulée avec succès!", InformationDialog.DIALOG_SUCCESS);
					
	        	}
	        	else{
		        	// Message d'information
		        	PortalInformationHelper.showInformationDialog("Erreur : "+error, InformationDialog.DIALOG_ERROR);
		        	return 0;
	        	}
	        }
			
			// Si on obtient la reponse attendue
			if(unlinkage.contains("unlinkfinancialresourceinformationresponse")){
				// l'annulation de l'abonnement s'est bien deroulee
							
				// Annulation cote bank
				MobileMoneyViewHelper.appManager.annulerSouscription(subscriber.getId(), user.getLogin());
				
				//Trace de la Resiliation
				Resiliation resi =  new Resiliation(null, subscriber.getCustomerId(), new Date(), "RESILIATION", user.getLogin());
				MobileMoneyViewHelper.appDAOLocal.save(resi);
				
				// Message d'information
				PortalInformationHelper.showInformationDialog("Le contrat de souscription a été annulée avec succès!", InformationDialog.DIALOG_SUCCESS);
				count = 1;
			}
			
		} catch(Exception e) {
			
			// Traitement de l'exception
			PortalExceptionHelper.threatException(e);
		}
		return count;
	}
   
   private int annulerSouscriptionTest(Long idAnnulation) {
	   int count = 0;
	   User user = MobileMoneyViewHelper.getSessionUser();
		try {

			logger.info("************* annulerSouscription - idAnnulation ************ : " + idAnnulation);
			Subscriber subscriber = MobileMoneyViewHelper.appDAOLocal.findByPrimaryKey(Subscriber.class,idAnnulation,null);

			if(subscriber.getStatus().equals(StatutContrat.SUSPENDU)){
				return 0;
			}else{
				
				//TEST
				// Suspendre la souscription
				subscriber.setFacturer(false);
				subscriber.setStatus(StatutContrat.SUSPENDU);
				subscriber.setDateSuspendu(new Date());
				subscriber.setUtiSuspendu("AUTO");
				subscriber.setActive(false);
				MobileMoneyViewHelper.appDAOLocal.update(subscriber);
				
				//Trace de la Resiliation
				Resiliation resi = new Resiliation(null, subscriber.getCustomerId(), new Date(), "RESILIATION", user.getLogin());
				MobileMoneyViewHelper.appDAOLocal.save(resi);
				count = 1;				
			}

		} catch(Exception e){
			e.printStackTrace();
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
		}
		return count;
	}
}
