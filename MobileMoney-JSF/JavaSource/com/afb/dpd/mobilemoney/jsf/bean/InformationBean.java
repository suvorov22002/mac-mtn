package com.afb.dpd.mobilemoney.jsf.bean;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import afb.dsi.dpd.portal.jpa.entities.User;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;

/**
 * Bean Manage d'informations
 * @author Francis DJIOMOU
 * @version 2.0
 */
public class InformationBean {
	
	/**
	 * Chemin du contexte
	 */
	private String contextPath = "";
	
	private User connectedUser;
	
	/**
	 * Default Constructor
	 */
	public InformationBean() {
		
		// Calcul du chemin du contexte
		contextPath = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getContextPath();
		
		loadConnectedUser();
		
	}
	
	/**
	 * Retourne le Chemin du Contexte
	 * @return Chemin du Contexte
	 */
	public String getContextPath() {
		return contextPath;
	}
	
	/**
	 * Retourne l'utilisateur connecte
	 * @return utilisateur connecte
	 */
	public User getConnectedUser() {
		return connectedUser;
	}
	
	public void loadConnectedUser() {
		
		connectedUser = MobileMoneyViewHelper.getSessionUser();
		
	}
	
	public String getClientAreaID(){
		return AbstractPortalForm.CLIENT_AREA;
	}

	public String getEmptyClientAreaID(){
		return AbstractPortalForm.EMPTY_AREA;
	}

	public String getDownloadDataURL() {
		
		// Contexte de la servlet
		String servletContextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath();
		
		// Requete entiere
		String requestURL = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURL().toString().replace(servletContextPath, "");
		
		String skinURI = PortalHelper.PORTAL_RESOURCES_DATA_DIR.concat("/").concat(PortalHelper.PORTAL_DOWNLOAD_DATA_DIR).concat("/");
		
		// On retourne l'URL
		return requestURL.concat("/").concat(skinURI);
	}

	public String getGraphicUIURL() {
		
		// Contexte de la servlet
		String servletContextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath();
		
		// Requete entiere
		String requestURL = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURL().toString().replace(servletContextPath, "");
		
		String skinURI = PortalHelper.PORTAL_RESOURCES_DATA_DIR.concat("/").concat(PortalHelper.PORTAL_GRAPHIC_UI_DIR);
		
		// On retourne l'URL
		return requestURL.concat("/").concat(skinURI);
	}
	
	public void deconnexion() throws Throwable {
		
		// On invalide la session
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		
		// Si la session n'est pas nulle
		session.invalidate();
		
		// Reponse Http
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		// On Vide le Buffer
		response.flushBuffer();
		
		// On redirect
		response.sendRedirect(MobileMoneyViewHelper.getApplicationContext().concat("/views/jsp/blank.jsp"));
		
		// On rend une fois la vue
		FacesContext.getCurrentInstance().responseComplete();
	}
	
}
