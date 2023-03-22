package com.afb.dpd.mobilemoney.jsf.models;

public interface IPortalForm {

	

	/**
	 * Methode d'ouverture du Composant
	 */
	public void open();

	/**
	 * Methode d'ouverture du Composant
	 */
	//public void open(DialogFormMode mode);
	
	/**
	 * Methode de fermeture du Composant
	 */
	public void close();
	

	/**
	 * Methode permettant d'obtenir le Titre de la boite
	 * @return	Titre de la Boite
	 */
	public String getTitle();
	
	/**
	 * Methode permettant d'obtenir l'icone de la boite
	 * @return	Icone de la boite
	 */
	public String getIcon();
	
	/**
	 * Chemins vers le fichier contenant la definition de la Boite
	 */
	public String getFileDefinition();
		
	/**
	 * Methode permettant de verifier si la boite est ouverte
	 * @return	Etat d'ouverture de la boite
	 */
	public boolean isOpen();

	/**
	 * Methode permettant de fermer la boite anormalement
	 */
	public void cancel();
	
	/**
	 * Retourne le nom du formulaire
	 * @return nom du formulaire
	 */
	public String getName();

	
	/**
	 * Libere les ressources a la fermeture du formulaire
	 */
	public void disposeResourcesOnClose();
	
	/**
	 * Methode d'initialisation d'un formulaire a l'ouverture
	 */
	public void initForm();
}
