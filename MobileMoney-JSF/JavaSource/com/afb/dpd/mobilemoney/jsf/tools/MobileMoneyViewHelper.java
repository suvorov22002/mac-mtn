package com.afb.dpd.mobilemoney.jsf.tools;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import afb.dsi.dpd.portal.business.facade.IFacadeManagerRemote;
import afb.dsi.dpd.portal.jpa.entities.User;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

import com.afb.dpd.mobilemoney.business.IMobileMoneyManagerRemote;
import com.afb.dpd.mobilemoney.dao.IMobileMoneyDAOLocal;

/**
 * Utilitaire de presentation
 * @author Francis DJIOMOU
 * @version 2.0
 */
public class MobileMoneyViewHelper {

	/**
	 * Service principal de gestion des 
	 */
	public static IMobileMoneyManagerRemote appManager;
	
	/**
	 * Nom de l'application
	 */
	public final static String APPLICATION_EAR = "MobileMoney";
	
	/**
	 * Service de gestion des utilisateurs du portail
	 */
	public static IMobileMoneyDAOLocal appDAOLocal;
	
	/**
	 * Service Facade du portail
	 */
	public static IFacadeManagerRemote portalFacadeManager;
	
	/**
	 * Methode permettant d'obtenir le Bean Manage ClientArea
	 * @return	Bean Manage ClientArea
	 */
	public static Object getSessionManagedBean( String managedBeanName ) {
		
		// On retourne le Bean
		return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(managedBeanName);
	}
	

	/**
	 * Methode permettant d'obtenir le Bean Manage ClientArea
	 * @param managedBeanName
	 * @param bean
	 */
	public static void setSessionManagedBean( String managedBeanName, Object bean ) {
		
		// On retourne le Bean
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(managedBeanName, bean);
	}
	
	public static User getSessionUser() {
		return (User) ((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true)).getAttribute(PortalHelper.CONNECTED_USER_SESSION_NAME);
	}
	

	/**
	 * Methode permettant d'obtenir le contexte de l'application
	 * @return	Contexte de l'application
	 */
	public static String getApplicationContext() {

		// Contexte de la servlet
		String servletContextPath = FacesContext.getCurrentInstance().getExternalContext().getRequestServletPath();
		
		// Requete entiere
		String requestURL = ((HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURL().toString().replace(servletContextPath, "");
		
		// On retourne le contest
		return requestURL;
	}
	
		
}
