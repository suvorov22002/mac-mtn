/**
 * 
 */
package com.afb.dpd.mobilemoney.scanner;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import afb.dsi.dpd.portal.business.facade.IFacadeManagerRemote;
import afb.dsi.dpd.portal.jpa.entities.Module;
import afb.dsi.dpd.portal.jpa.entities.Role;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

import com.afb.dpd.mobilemoney.business.MobileMoneyManager;

/**
 * Implementation du service de Scan du Module
 * @author Francis DJIOMOU
 * @version 2.0
 */
@Stateless(name = IScanner.SERVICE_NAME, mappedName = IScanner.SERVICE_NAME, description = "Implementation du service de Scanner de chaque module")
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class Scanner implements IScanner {

	/**
	 * Le Logger
	 */
	private static Log logger = LogFactory.getLog(MobileMoneyManager.class);
	
	/**
	 * Service facade du portail
	 */
	private IFacadeManagerRemote portalFacadeManager = null;
	
	/* (non-Javadoc)
	 * @see com.afb.dpd.monitoringip.scanner.IScanner#scanAndInitialiseModule()
	 */
	@SuppressWarnings({ "rawtypes" })
	@Override
	public void scanAndInitialiseModule( String fileName ) throws Exception {
		
		// Log
		logger.info("Debut du scan de l'archive : " + fileName);
		
		try{
			
			// Demarrage du service Facade du portail
			loadPortalService();
			
		} catch(Exception e) {
			
			// Log
			logger.info("impossible de demarrer lu service facade du Portail! Scan du module impossible ");
			
			// Levee de l'exception
			// throw new Exception("Echec du demarrage du service du Portail");
			
		}
		
		if(portalFacadeManager == null) return;
		
		// Initialisation
		Module module = null;
		
		try {
			
			// Scan de l'archive a la recherche du module 
			module = new EARScanner().scanFile( fileName );
			
		} catch(Exception e) {

			// Log
			logger.info("Erreur lors du scan du module");
			
			// Levee de l'exception
			throw new Exception("Echec du scan du module");
			
		}
		
		// Si aucun module n'a ete retrouve dans l'archive on sort
		if( module == null ) return;
		
		// Recherche du module en BD 
		List mdls = portalFacadeManager.filterByQuery("From Module m where m.code='"+ module.getCode() +"'", null);
		
		// Recuperation du module trouve
		Module mod = mdls != null && !mdls.isEmpty() ? (Module)mdls.get(0) : null;
		
		// Si le module n'existe pas (on le cree)
		if(mod == null) {
			
			// Recuperation des roles
			List<Role> roles = new ArrayList<Role>(module.getRoles());
			module.setRoles(null);
			
			// Creation du module
			portalFacadeManager.create(module);
			
			// Recuperation du module cree
			module = (Module) portalFacadeManager.findByProperty(Module.class, "code", module.getCode());
			
			// MAJ des roles
			for(Role r : roles) r.setModule(module);
			module.setRoles(roles);
			
			// Modification du module
			portalFacadeManager.update(module);
			
		// Le module existe deja (on ajoute les nouveaux roles s'il y'en a)
		} else{
			
			// Parcours des roles trouves dans le module scanne
			for(Role r : module.getRoles()) {
				
				// Recherche du role courant en BD
				List rls = portalFacadeManager.filterByQuery("From Role r where r.name='"+ r.getName() +"' and r.module.id="+ mod.getId() +"", null);
				
				// Si le role courant n'existe pas encore (on le cree)
				if( rls == null || rls.isEmpty() ){
					
					try{
						
						// Lecture du module dans le role
						r.setModule(mod);
						
						// Creation du nouveau role
						portalFacadeManager.create(r);
						
					}catch(Exception e){}
					
				}
			}
			
		}
		
		// Fermeture du service
		portalFacadeManager = null;
	}

	/**
	 * Demarrage du service Facade du portail
	 * @throws Exception
	 */
	private void loadPortalService() throws Exception {
		
		// Demarrage du service
		portalFacadeManager = (IFacadeManagerRemote) new InitialContext().lookup( PortalHelper.APPLICATION_EAR.concat("/").concat( IFacadeManagerRemote.SERVICE_NAME ).concat("/remote") );
		
	}
	
}
