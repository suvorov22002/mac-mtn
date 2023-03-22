/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.forms;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.UrlValidator;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.enums.ModeFacturation;
import com.afb.dpd.mobilemoney.jpa.enums.Periodicite;
import com.afb.dpd.mobilemoney.jpa.enums.StatutService;
import com.afb.dpd.mobilemoney.jpa.enums.TypeValeurFrais;
import com.afb.dpd.mobilemoney.jpa.tools.TypeCompte;
import com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;
import com.afb.dpd.mobilemoney.jsf.tools.SelectData;

/**
 * Formulaire de gestion des Parametres generaux
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class FrmParameters extends AbstractPortalForm {
	
	private Parameters params;
	
	private List<SelectData> types = new ArrayList<SelectData>();
	
	private List<SelectItem> typeValeurItems = new ArrayList<SelectItem>();
	private List<SelectItem> modeItems = new ArrayList<SelectItem>();
	private List<SelectItem> periodItems = new ArrayList<SelectItem>();
	private List<SelectItem> booleanItems = new ArrayList<SelectItem>();
	private List<SelectItem> _booleanItems = new ArrayList<SelectItem>();
	private List<SelectItem> etatServiceSDPItems = new ArrayList<SelectItem>();
	
	private String phoneNumber = "237";
	private String selectedPhoneNumber = "";

	private int num;
	
	/**
	 * Default Constructor
	 */
	public FrmParameters() {}

	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#initForm()
	 */
	@Override
	public void initForm() {
		
		// Appel Parent
		super.initForm();
		
		// Lecture des parametres
		params = MobileMoneyViewHelper.appManager.consulterConfiguration();
		
		// Initialisation des types de comptes
		if(params.getAccountTypes() == null) params.setAccountTypes( new ArrayList<String>() );
		
		// Initialisation des commissions si elles n'esistent pas
		if(params.getCommissions() == null || params.getCommissions().isEmpty()) params.initCommissions();
		
		// Chargement de la liste des Types de comptes
		List<TypeCompte> accountTypes = MobileMoneyViewHelper.appManager.filterTypeCompteFromAmplitude();
		
		if(accountTypes != null) {
			
			// Chargement des items de Types de comptes disponibles
			for(TypeCompte tc : accountTypes) types.add( new SelectData( (params.getAccountTypes().contains(tc.getCode()) ? Boolean.TRUE : Boolean.FALSE ), tc.getCode() + " : " + tc.getNom()) );
			
		}
		
		// Chargement des items de types de valeurs
		for(TypeValeurFrais to : TypeValeurFrais.getValues()) typeValeurItems.add( new SelectItem(to, to.getValue()) );
		
		// Items de modes de facturation
		typeValeurItems = new ArrayList<SelectItem>();
		modeItems = new ArrayList<SelectItem>();
		modeItems.add( new SelectItem(null, " ") );
		for(ModeFacturation m : ModeFacturation.getValues()) modeItems.add( new SelectItem(m, m.getValue()) );
		
		// Items de Periodictes de facturation
		periodItems = new ArrayList<SelectItem>();
		periodItems.add( new SelectItem(null, " ") );
		for(Periodicite p : Periodicite.getValues()) periodItems.add( new SelectItem(p, p.getValue()) );
		
		booleanItems = new ArrayList<SelectItem>();
		booleanItems.add( new SelectItem(null, " ") );
		booleanItems.add( new SelectItem(Boolean.TRUE, "Oui") ); booleanItems.add( new SelectItem(Boolean.FALSE, "Non") );
		
		_booleanItems = new ArrayList<SelectItem>();
		_booleanItems.add( new SelectItem(Boolean.FALSE, "Non") );
		_booleanItems.add( new SelectItem(Boolean.TRUE, "Oui") ); 
		
		etatServiceSDPItems = new ArrayList<SelectItem>();
		etatServiceSDPItems.add( new SelectItem(StatutService.ON, StatutService.ON.getValue()) );
		etatServiceSDPItems.add( new SelectItem(StatutService.OFF, StatutService.OFF.getValue()) );
		etatServiceSDPItems.add( new SelectItem(StatutService.TEST, StatutService.TEST.getValue()) );
		
		phoneNumber = "237";
		selectedPhoneNumber = "";
		
		accountTypes.clear();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#getTitle()
	 */
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Paramètres Généraux";
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.afb.dpd.mobilemoney.jsf.models.AbstractPortalForm#disposeResourcesOnClose()
	 */
	@Override
	public void disposeResourcesOnClose() {
		// TODO Auto-generated method stub
		super.disposeResourcesOnClose();
		
		// Annulation des valeurs
		params = null; types.clear(); periodItems.clear(); modeItems.clear(); booleanItems.clear(); _booleanItems.clear();etatServiceSDPItems.clear();
	}
	
	private void readSelectedTypes() {
		
		params.setAccountTypes( new ArrayList<String>() );
		
		for(SelectData sd : types) {
			if(sd.getChecked().booleanValue()) params.getAccountTypes().add( sd.getValue().split(" : ")[0].trim() );
		}
		
	}
	
	
	public void savePamareters() {
		
		try {
			
			// Check de la bonne saisie des numeros de compte
			if(!checkBonneSaisieCompte(params.getNumCompteCommissions())) return;
			//if(!checkBonneSaisieCompte(params.getNumCompteLiaison())) return;
			if(!checkBonneSaisieCompte(params.getNumCompteTVA())) return;
			if(!checkBonneSaisieCompte(params.getNumCompteMTN())) return;
			if(!checkBonneSaisieCompte(params.getNcpDAPPull())) return;
			if(!checkBonneSaisieCompte(params.getNcpDAPPush())) return;
			
			if(!checkBonneSaisieUrl(params.getUrlKYCApi())) return;
			if(!checkBonneSaisieUrl(params.getUrlCbsApi())) return;
			
			if(!checkBonneSaisieMail(params.getDestMailAlerte())) return;
			
			if(!checkBonneSaisieTokenCbs(params.getUrlServiceSms())) return;
			
			if(!checkBonneSaisiePhones(params.getDestPhoneAlerte())) return;
			
			if(!checkBonneSaisieTokenCbs(params.getTokenCbsApi())) return;
			
			for(String m : params.getMailPlafond().split(";")) {
				if(!checkBonneSaisieMail(m)) return;
			}
			
			
//			if(!checkBonneSaisieIP(params.getIpAdressAmpli()) && !checkBonneSaisieUrl(params.getIpAdressAmpli())) return;
			
			// Recupere les types de compte selectionnes
			readSelectedTypes();
			
			// Enregistrement des parametres
			params = MobileMoneyViewHelper.appManager.saveParameters(params);
			
			// Message d'information
			PortalInformationHelper.showInformationDialog("Les parametres ont ete sauvegardés avec succes.", InformationDialog.DIALOG_SUCCESS);
			
		} catch(Exception e){
			// if(e.getCause().getClass().isInstance("OptimisticLockException"))
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	
	public void ajouterNumeroTest() {
				
		try {
			
			// Check de la bonne saisie des numeros de compte			
			if(!checkBonneSaisiePhone(phoneNumber)) return;
			
			List<String> phones = new ArrayList<String>();
			phones = params.getNumerosTest();
			for(String p : phones){
				if(p.equals(phoneNumber)) PortalInformationHelper.showInformationDialog("Le numéro de téléphone saisi exite déjà dans la liste des numéros de test", InformationDialog.DIALOG_INFORMATION);
			}
			phones.add(phoneNumber);
			params.setNumerosTest(phones);
			
			// initialisation de la valeur du numero de telephone
			phoneNumber = "237";
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}

	
	public void removePhoneNumber() {
		
		try {
			List<String> phones = new ArrayList<String>();
			phones = params.getNumerosTest();
			phones.remove(selectedPhoneNumber);
			params.setNumerosTest(phones);
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	
	public void majDataFacturation() {
		
		try {
			int maj = 0;
			// MAJ des donnees de facturation
			maj = MobileMoneyViewHelper.appManager.updateDataFacturation();
			
			// Message d'information
			PortalInformationHelper.showInformationDialog(maj+" données de facturation ont ete mis à jour avec succes.", InformationDialog.DIALOG_SUCCESS);
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	
	public void majECReguls() {
		
		try {
			int maj = 0;
			// MAJ des EC de reguls
			maj = MobileMoneyViewHelper.appManager.updateECRegulsFacturation();
			
			// Message d'information
			PortalInformationHelper.showInformationDialog("Les EC de "+maj+" reguls de facturation ont ete mis à jour avec succes.", InformationDialog.DIALOG_SUCCESS);
			
		} catch(Exception e){
			
			// Affichage de l'exception
			PortalExceptionHelper.threatException(e);
			
		}
		
	}
	
	
	private boolean checkBonneSaisieCompte(String numCompte) {
		
		try {
			
			if(numCompte.split("-").length != 3) {
				PortalInformationHelper.showInformationDialog("Format de numero de compte incorrect (Format = xxxxx-xxxxxxxxxxx-xx)", InformationDialog.DIALOG_WARNING);
				return false;
			}
			
			if(MobileMoneyViewHelper.appManager.isCompteFerme(numCompte)) {
				PortalInformationHelper.showInformationDialog("Compte " + numCompte + " inexistant ou en instance de fermeture!", InformationDialog.DIALOG_WARNING);
				return false;
			}
			
		} catch(Exception e) {
			PortalInformationHelper.showInformationDialog("Format de numero de compte incorrect (Format = xxxxx-xxxxxxxxxxx-xx)", InformationDialog.DIALOG_WARNING);
			return false;
		}
		
		return true;
		
	}
	
	private boolean checkBonneSaisieTokenCbs(String token) {
		if(!StringUtils.isNotBlank(token)) {
			PortalInformationHelper.showInformationDialog("Token Cbs absent.", InformationDialog.DIALOG_WARNING);
			return false;
		}
		return true;
	}
	
	private boolean checkBonneSaisieIP(String ip) {
		
		try {
			String[] tab = ip.split("\\.");
			if(tab.length != 4) {
				PortalInformationHelper.showInformationDialog("Format de l'adresse IP incorrect (Format = xxx.xxx.xxx.xxx)", InformationDialog.DIALOG_WARNING);
				return false;
			}
			
			for(int i=0; i<tab.length; i++){
				if(Integer.valueOf(tab[i]) > 255){
					PortalInformationHelper.showInformationDialog("Adresse IP incorrect", InformationDialog.DIALOG_WARNING);
					return false;
				}
			}
			
		} catch(Exception e) {
			PortalInformationHelper.showInformationDialog("Format de l'adresse IP incorrect (Format = xxx.xxx.xxx.xxx)", InformationDialog.DIALOG_WARNING);
			return false;
		}
		
		return true;
		
	}
	
	
	private boolean checkBonneSaisieUrl(String url) {
		String[] schemes = {"http","https"}; // DEFAULT schemes = "http", "https", "ftp"
		UrlValidator urlValidator = new UrlValidator(schemes);
		if(StringUtils.isBlank(url)) {
			PortalInformationHelper.showInformationDialog("Veuillez indiquer l'URL des services CBS.", InformationDialog.DIALOG_WARNING);
			return false;
		}
		if (urlValidator.isValid(url)) {
		   //System.out.println("URL is valid");
			return true;
		} else {
		   //System.out.println("URL is invalid");
		   PortalInformationHelper.showInformationDialog("URL incorrect (Format = protocol://ip:port/path)", InformationDialog.DIALOG_WARNING);
		   return false;
		}
		
	}
	
	
	private boolean checkBonneSaisieMail(String mail) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(mail);
			emailAddr.validate();
		} catch (AddressException ex) {
			PortalInformationHelper.showInformationDialog("Adresse mail incorrecte (Format = example@afrilandfirstbank.com)", InformationDialog.DIALOG_WARNING);
			result = false;
		}
		return result;
		
	}
	
	private boolean checkBonneSaisiePackage(String mail) {
		boolean result = true;
		try {
			InternetAddress emailAddr = new InternetAddress(mail);
			emailAddr.validate();
		} catch (AddressException ex) {
			PortalInformationHelper.showInformationDialog("Adresse mail incorrecte (Format = example@afrilandfirstbank.com)", InformationDialog.DIALOG_WARNING);
			result = false;
		}
		return result;
		
	}
	
	
	private boolean checkBonneSaisiePhones(String phones) {
		boolean result = true;
		String[] tels = phones.trim().split(",");
		for(int i = 0; i < tels.length; i++){
			if(tels[i].length()!=12 || !StringUtils.isNumeric(tels[i])) {
				PortalInformationHelper.showInformationDialog("Format des numéros de téléphone incorrect (Format = 237xxxxxxxxxx,237xxxxxxxxxx,237xxxxxxxxxx)", InformationDialog.DIALOG_WARNING);
				return false;
			}
		}
		return result;
		
	}
	
	
	private boolean checkBonneSaisiePhone(String phone) {
		boolean result = true;
		if(phone.length()!=12 || !StringUtils.isNumeric(phone) || !phone.substring(0, 3).equals("237")) {
			PortalInformationHelper.showInformationDialog("Format du numéro de téléphone incorrect (Format = 237xxxxxxxxxx)", InformationDialog.DIALOG_WARNING);
			return false;
		}
		return result;
		
	}
	
	
	/**
	 * @return the params
	 */
	public Parameters getParams() {
		return params;
	}

	/**
	 * 
	 * @return
	 */
	public List<SelectData> getTypes() {
		return types;
	}

	/**
	 * @param types the types to set
	 */
	public void setTypes(List<SelectData> types) {
		this.types = types;
	}

	/**
	 * @return the typeValeurItems
	 */
	public List<SelectItem> getTypeValeurItems() {
		return typeValeurItems;
	}

	/**
	 * @return the modeItems
	 */
	public List<SelectItem> getModeItems() {
		return modeItems;
	}

	/**
	 * @return the periodItems
	 */
	public List<SelectItem> getPeriodItems() {
		return periodItems;
	}

	/**
	 * @return the booleanItems
	 */
	public List<SelectItem> getBooleanItems() {
		return booleanItems;
	}

	
	/**
	 * @return the _booleanItems
	 */
	public List<SelectItem> get_booleanItems() {
		return _booleanItems;
	}

	/**
	 * @return the etatServiceSDPItems
	 */
	public List<SelectItem> getEtatServiceSDPItems() {
		return etatServiceSDPItems;
	}
	

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}
	

	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	
	/**
	 * @return the selectedPhoneNumber
	 */
	public String getSelectedPhoneNumber() {
		return selectedPhoneNumber;
	}

	
	/**
	 * @param selectedPhoneNumber the selectedPhoneNumber to set
	 */
	public void setSelectedPhoneNumber(String selectedPhoneNumber) {
		this.selectedPhoneNumber = selectedPhoneNumber;
		if(this.selectedPhoneNumber != null) removePhoneNumber();
	}
	
	
	/**
	 * Numeroteur de lignes dans la grille
	 * @return
	 */
	public int getNum() {
		return num++;
	}

}
