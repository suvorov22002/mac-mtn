package com.afb.dpd.mobilemoney.jsf.models;

/**
 * Bean manage permettant l'affichage de boites de dialogues a l'utilisateur
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class InformationDialog {

	public static final int DIALOG_INFORMATION = 0;
	public static final int DIALOG_SUCCESS = 1;
	public static final int DIALOG_WARNING = 2;
	public static final int DIALOG_ERROR = 3;
	
	/**
	 * Type de boite de dialogue
	 */
	private int typeDialog = DIALOG_INFORMATION;
	
	/**
	 * Message de la boite de dialogue
	 */
	private String message = "";
	
	private String details = "";
	
	/**
	 * Titre de la boite de dialogue
	 */
	private String title = "Information Dialog";
	
	/**
	 * Determine si la boite de dialogue est ouverte ou non
	 */
	private boolean open = false;
	
	/**
	 * Default Constructor
	 */
	public InformationDialog() {}

	/**
	 * Retourne l'icone appropriee au type de message
	 * @return icone appropriee au type de message
	 */
	public String getIcon(){
		
		String icon = "Info_32.png";
		
		switch (this.typeDialog) {
		case DIALOG_INFORMATION:
			icon = "Info_32.png";
			title = "Information";
			break;

		case DIALOG_SUCCESS:
			icon = "success_32.png";
			title = "Succes";
			break;

		case DIALOG_WARNING:
			icon = "Alert-Icon_32.png";
			title = "Avertissement";
			break;

		case DIALOG_ERROR:
			icon = "error_32.png";
			title = "Erreur";
			break;
		}
		
		return icon;
		
	}
	
	/**
	 * @return the typeDialog
	 */
	public int getTypeDialog() {
		return typeDialog;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param typeDialog the typeDialog to set
	 */
	public void setTypeDialog(int typeDialog) {
		this.typeDialog = typeDialog;
		getIcon();
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	public static String getName(){
		return "informationDialog"; 
	}

	/**
	 * @return the open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * @param open the open to set
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}

	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(String details) {
		this.details = details;
	}

	public String getFileDefinition(){
		return open ? "informationDialog.xhtml" : "emptyPage.xhtml";
	}
	

	public String getEmptyPage(){
		return "emptyPage.xhtml";
	}
		
	
	public void close() {
		this.open = false;
	}
}
