package com.afb.dpd.mobilemoney.jsf.models;


public class AbstractPortalForm implements IPortalForm {

	/**
	 * Mode d'ouverture du formulaire
	 */
	protected DialogFormMode mode = DialogFormMode.CREATE;
	
	/**
	 * Zone d'affichage des formulaires
	 */
	public static String CLIENT_AREA = "ContainArea";
	
	/**
	 * Zone vide
	 */
	public static String EMPTY_AREA = "EmptyClientArea";
	
	/**
	 * Designe si un formulaire est ouvert ou non
	 */
	protected boolean open = false;
	
	protected String areaToRender = CLIENT_AREA;
	
	@Override
	public void open() {
		
		// Si le formulaire est deja affiche on annule l'ouverture du formulaire
		if( isOpen() ) {
			
			// Annulation de la zone a rafraichir tant que le formulaire est ouvert
			setAreaToRender(EMPTY_AREA);
			
			return;
		}
		
		try{
			
			// Initialisation du formulaire
			initForm();
			
			// Annulation de la zone a rafraichir tant que le formulaire est ouvert
			setAreaToRender(getName());
			
			// Ouverture du formulaire
			this.open = true;
			
		}catch(Exception ex) {
			
			// Traitement de l'exception liee a l'initialisation du formulaire
			PortalExceptionHelper.threatException(ex);
		}
	}

	@Override
	public void close() {
		
		// Vidage des ressources
		disposeResourcesOnClose();
		
		// On repositionne le panneau des formulaire comme zone a rafraichir
		setAreaToRender(getName());
		
		// Fermeture
		this.open = false;
	}
	
	@Override
	public String getTitle() {
		return "Default Title";
	}

	@Override
	public String getIcon() {
		return "defaulticon.png";
	}

	/**
	 * @return the areaToRender
	 */
	public String getAreaToRender() {
		return areaToRender;
	}

	/**
	 * @param areaToRender the areaToRender to set
	 */
	public void setAreaToRender(String areaToRender) {
		this.areaToRender = areaToRender;
	}

	@Override
	public String getFileDefinition() {
		return this.isOpen() ? getClass().getSimpleName() + ".xhtml" : "emptyPage.xhtml";
	}

	@Override
	public boolean isOpen() {
		return this.open;
	}

	@Override
	public void cancel() {
		this.close();
	}

	@Override
	public String getName() {
		return getClass().getSimpleName().substring(0, 1).toLowerCase() + getClass().getSimpleName().substring(1, getClass().getSimpleName().length() );
	}

	//@Override
	public void open(DialogFormMode mode) {
		this.open = true;
		this.mode = mode;
	}

	@Override
	public void disposeResourcesOnClose() {
		
	}

	@Override
	public void initForm() {
		
	}
	
}
