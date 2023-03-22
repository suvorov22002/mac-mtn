package com.afb.dpd.mobilemoney.jpa.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Account implements Serializable{
	
	private String agence;

	private String libelleAgence;

	private String codeDevise;

	private String codeISODevise;

	private String devise;

	private String ncp;

	private String cle;

	private String suffixe;

	private String categorie;

	private String iban;

	private String chapitre;

	private String codeTypeCompte;

	private String typeCompte;

	private String codeProduit;

	private String libelleProduit;

	private String titre;

	private String codeClient;

	private String nomClient;

	private String codeGestionnaire;

	private String nomGestionnaire;

	private String statutCompte;

	private Date dou;

	private Double sde;

	private String deviseSde;

	private Double sin;

	private Double sdis;

	private String sens;

	private String codeEmargement;

	private String soumisAuxArretes;

	private boolean taxable;

	private boolean ife;

	private boolean cfe;

	private Date dmo;

	private Date dife;

	private Date dclo;

	private Date dva;

	private Double sdeHistorique;

	private Double sdeArrete;

	private Date darr;

	private Double sdeMvtCredit;

	private Double sdeMvtDebit;

	private Date dateLastMvt;

	private Date dateLastMvtCredit;

	private Date dateLastMvtDebit;

	private Double autorisation;

	private Date dateLastAutorisation;

	private String raisonCloture;

	private String userCreation;

	private String nomUserCreation;

	private String userModification;

	private String nomUserModification;

	private String userCloture;

	private String nomUserCloture;

	private String ouvertTemporaire;

	private boolean jointAccount;

	private String jointAccountTitle;

	private String responsibleCustomer;

	private List<StoppageAccount> stoppages;

	/**
	 * @return the agence
	 */
	public String getAgence() {
		return agence;
	}

	/**
	 * @param agence the agence to set
	 */
	public void setAgence(String agence) {
		this.agence = agence;
	}

	/**
	 * @return the libelleAgence
	 */
	public String getLibelleAgence() {
		return libelleAgence;
	}

	/**
	 * @param libelleAgence the libelleAgence to set
	 */
	public void setLibelleAgence(String libelleAgence) {
		this.libelleAgence = libelleAgence;
	}

	/**
	 * @return the codeDevise
	 */
	public String getCodeDevise() {
		return codeDevise;
	}

	/**
	 * @param codeDevise the codeDevise to set
	 */
	public void setCodeDevise(String codeDevise) {
		this.codeDevise = codeDevise;
	}

	/**
	 * @return the codeISODevise
	 */
	public String getCodeISODevise() {
		return codeISODevise;
	}

	/**
	 * @param codeISODevise the codeISODevise to set
	 */
	public void setCodeISODevise(String codeISODevise) {
		this.codeISODevise = codeISODevise;
	}

	/**
	 * @return the devise
	 */
	public String getDevise() {
		return devise;
	}

	/**
	 * @param devise the devise to set
	 */
	public void setDevise(String devise) {
		this.devise = devise;
	}

	/**
	 * @return the ncp
	 */
	public String getNcp() {
		return ncp;
	}

	/**
	 * @param ncp the ncp to set
	 */
	public void setNcp(String ncp) {
		this.ncp = ncp;
	}

	/**
	 * @return the cle
	 */
	public String getCle() {
		return cle;
	}

	/**
	 * @param cle the cle to set
	 */
	public void setCle(String cle) {
		this.cle = cle;
	}

	/**
	 * @return the suffixe
	 */
	public String getSuffixe() {
		return suffixe;
	}

	/**
	 * @param suffixe the suffixe to set
	 */
	public void setSuffixe(String suffixe) {
		this.suffixe = suffixe;
	}

	/**
	 * @return the categorie
	 */
	public String getCategorie() {
		return categorie;
	}

	/**
	 * @param categorie the categorie to set
	 */
	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	/**
	 * @return the iban
	 */
	public String getIban() {
		return iban;
	}

	/**
	 * @param iban the iban to set
	 */
	public void setIban(String iban) {
		this.iban = iban;
	}

	/**
	 * @return the chapitre
	 */
	public String getChapitre() {
		return chapitre;
	}

	/**
	 * @param chapitre the chapitre to set
	 */
	public void setChapitre(String chapitre) {
		this.chapitre = chapitre;
	}

	/**
	 * @return the codeTypeCompte
	 */
	public String getCodeTypeCompte() {
		return codeTypeCompte;
	}

	/**
	 * @param codeTypeCompte the codeTypeCompte to set
	 */
	public void setCodeTypeCompte(String codeTypeCompte) {
		this.codeTypeCompte = codeTypeCompte;
	}

	/**
	 * @return the typeCompte
	 */
	public String getTypeCompte() {
		return typeCompte;
	}

	/**
	 * @param typeCompte the typeCompte to set
	 */
	public void setTypeCompte(String typeCompte) {
		this.typeCompte = typeCompte;
	}

	/**
	 * @return the codeProduit
	 */
	public String getCodeProduit() {
		return codeProduit;
	}

	/**
	 * @param codeProduit the codeProduit to set
	 */
	public void setCodeProduit(String codeProduit) {
		this.codeProduit = codeProduit;
	}

	/**
	 * @return the libelleProduit
	 */
	public String getLibelleProduit() {
		return libelleProduit;
	}

	/**
	 * @param libelleProduit the libelleProduit to set
	 */
	public void setLibelleProduit(String libelleProduit) {
		this.libelleProduit = libelleProduit;
	}

	/**
	 * @return the titre
	 */
	public String getTitre() {
		return titre;
	}

	/**
	 * @param titre the titre to set
	 */
	public void setTitre(String titre) {
		this.titre = titre;
	}

	/**
	 * @return the codeClient
	 */
	public String getCodeClient() {
		return codeClient;
	}

	/**
	 * @param codeClient the codeClient to set
	 */
	public void setCodeClient(String codeClient) {
		this.codeClient = codeClient;
	}

	/**
	 * @return the nomClient
	 */
	public String getNomClient() {
		return nomClient;
	}

	/**
	 * @param nomClient the nomClient to set
	 */
	public void setNomClient(String nomClient) {
		this.nomClient = nomClient;
	}

	/**
	 * @return the codeGestionnaire
	 */
	public String getCodeGestionnaire() {
		return codeGestionnaire;
	}

	/**
	 * @param codeGestionnaire the codeGestionnaire to set
	 */
	public void setCodeGestionnaire(String codeGestionnaire) {
		this.codeGestionnaire = codeGestionnaire;
	}

	/**
	 * @return the nomGestionnaire
	 */
	public String getNomGestionnaire() {
		return nomGestionnaire;
	}

	/**
	 * @param nomGestionnaire the nomGestionnaire to set
	 */
	public void setNomGestionnaire(String nomGestionnaire) {
		this.nomGestionnaire = nomGestionnaire;
	}

	/**
	 * @return the statutCompte
	 */
	public String getStatutCompte() {
		return statutCompte;
	}

	/**
	 * @param statutCompte the statutCompte to set
	 */
	public void setStatutCompte(String statutCompte) {
		this.statutCompte = statutCompte;
	}

	/**
	 * @return the dou
	 */
	public Date getDou() {
		return dou;
	}

	/**
	 * @param dou the dou to set
	 */
	public void setDou(Date dou) {
		this.dou = dou;
	}

	/**
	 * @return the sde
	 */
	public Double getSde() {
		return sde;
	}

	/**
	 * @param sde the sde to set
	 */
	public void setSde(Double sde) {
		this.sde = sde;
	}

	/**
	 * @return the deviseSde
	 */
	public String getDeviseSde() {
		return deviseSde;
	}

	/**
	 * @param deviseSde the deviseSde to set
	 */
	public void setDeviseSde(String deviseSde) {
		this.deviseSde = deviseSde;
	}

	/**
	 * @return the sin
	 */
	public Double getSin() {
		return sin;
	}

	/**
	 * @param sin the sin to set
	 */
	public void setSin(Double sin) {
		this.sin = sin;
	}

	/**
	 * @return the sdis
	 */
	public Double getSdis() {
		return sdis;
	}

	/**
	 * @param sdis the sdis to set
	 */
	public void setSdis(Double sdis) {
		this.sdis = sdis;
	}

	/**
	 * @return the sens
	 */
	public String getSens() {
		return sens;
	}

	/**
	 * @param sens the sens to set
	 */
	public void setSens(String sens) {
		this.sens = sens;
	}

	/**
	 * @return the codeEmargement
	 */
	public String getCodeEmargement() {
		return codeEmargement;
	}

	/**
	 * @param codeEmargement the codeEmargement to set
	 */
	public void setCodeEmargement(String codeEmargement) {
		this.codeEmargement = codeEmargement;
	}

	/**
	 * @return the soumisAuxArretes
	 */
	public String getSoumisAuxArretes() {
		return soumisAuxArretes;
	}

	/**
	 * @param soumisAuxArretes the soumisAuxArretes to set
	 */
	public void setSoumisAuxArretes(String soumisAuxArretes) {
		this.soumisAuxArretes = soumisAuxArretes;
	}

	/**
	 * @return the taxable
	 */
	public boolean isTaxable() {
		return taxable;
	}

	/**
	 * @param taxable the taxable to set
	 */
	public void setTaxable(boolean taxable) {
		this.taxable = taxable;
	}

	/**
	 * @return the ife
	 */
	public boolean isIfe() {
		return ife;
	}

	/**
	 * @param ife the ife to set
	 */
	public void setIfe(boolean ife) {
		this.ife = ife;
	}

	/**
	 * @return the cfe
	 */
	public boolean isCfe() {
		return cfe;
	}

	/**
	 * @param cfe the cfe to set
	 */
	public void setCfe(boolean cfe) {
		this.cfe = cfe;
	}

	/**
	 * @return the dmo
	 */
	public Date getDmo() {
		return dmo;
	}

	/**
	 * @param dmo the dmo to set
	 */
	public void setDmo(Date dmo) {
		this.dmo = dmo;
	}

	/**
	 * @return the dife
	 */
	public Date getDife() {
		return dife;
	}

	/**
	 * @param dife the dife to set
	 */
	public void setDife(Date dife) {
		this.dife = dife;
	}

	/**
	 * @return the dclo
	 */
	public Date getDclo() {
		return dclo;
	}

	/**
	 * @param dclo the dclo to set
	 */
	public void setDclo(Date dclo) {
		this.dclo = dclo;
	}

	/**
	 * @return the dva
	 */
	public Date getDva() {
		return dva;
	}

	/**
	 * @param dva the dva to set
	 */
	public void setDva(Date dva) {
		this.dva = dva;
	}

	/**
	 * @return the sdeHistorique
	 */
	public Double getSdeHistorique() {
		return sdeHistorique;
	}

	/**
	 * @param sdeHistorique the sdeHistorique to set
	 */
	public void setSdeHistorique(Double sdeHistorique) {
		this.sdeHistorique = sdeHistorique;
	}

	/**
	 * @return the sdeArrete
	 */
	public Double getSdeArrete() {
		return sdeArrete;
	}

	/**
	 * @param sdeArrete the sdeArrete to set
	 */
	public void setSdeArrete(Double sdeArrete) {
		this.sdeArrete = sdeArrete;
	}

	/**
	 * @return the darr
	 */
	public Date getDarr() {
		return darr;
	}

	/**
	 * @param darr the darr to set
	 */
	public void setDarr(Date darr) {
		this.darr = darr;
	}

	/**
	 * @return the sdeMvtCredit
	 */
	public Double getSdeMvtCredit() {
		return sdeMvtCredit;
	}

	/**
	 * @param sdeMvtCredit the sdeMvtCredit to set
	 */
	public void setSdeMvtCredit(Double sdeMvtCredit) {
		this.sdeMvtCredit = sdeMvtCredit;
	}

	/**
	 * @return the sdeMvtDebit
	 */
	public Double getSdeMvtDebit() {
		return sdeMvtDebit;
	}

	/**
	 * @param sdeMvtDebit the sdeMvtDebit to set
	 */
	public void setSdeMvtDebit(Double sdeMvtDebit) {
		this.sdeMvtDebit = sdeMvtDebit;
	}

	/**
	 * @return the dateLastMvt
	 */
	public Date getDateLastMvt() {
		return dateLastMvt;
	}

	/**
	 * @param dateLastMvt the dateLastMvt to set
	 */
	public void setDateLastMvt(Date dateLastMvt) {
		this.dateLastMvt = dateLastMvt;
	}

	/**
	 * @return the dateLastMvtCredit
	 */
	public Date getDateLastMvtCredit() {
		return dateLastMvtCredit;
	}

	/**
	 * @param dateLastMvtCredit the dateLastMvtCredit to set
	 */
	public void setDateLastMvtCredit(Date dateLastMvtCredit) {
		this.dateLastMvtCredit = dateLastMvtCredit;
	}

	/**
	 * @return the dateLastMvtDebit
	 */
	public Date getDateLastMvtDebit() {
		return dateLastMvtDebit;
	}

	/**
	 * @param dateLastMvtDebit the dateLastMvtDebit to set
	 */
	public void setDateLastMvtDebit(Date dateLastMvtDebit) {
		this.dateLastMvtDebit = dateLastMvtDebit;
	}

	/**
	 * @return the autorisation
	 */
	public Double getAutorisation() {
		return autorisation;
	}

	/**
	 * @param autorisation the autorisation to set
	 */
	public void setAutorisation(Double autorisation) {
		this.autorisation = autorisation;
	}

	/**
	 * @return the dateLastAutorisation
	 */
	public Date getDateLastAutorisation() {
		return dateLastAutorisation;
	}

	/**
	 * @param dateLastAutorisation the dateLastAutorisation to set
	 */
	public void setDateLastAutorisation(Date dateLastAutorisation) {
		this.dateLastAutorisation = dateLastAutorisation;
	}

	/**
	 * @return the raisonCloture
	 */
	public String getRaisonCloture() {
		return raisonCloture;
	}

	/**
	 * @param raisonCloture the raisonCloture to set
	 */
	public void setRaisonCloture(String raisonCloture) {
		this.raisonCloture = raisonCloture;
	}

	/**
	 * @return the userCreation
	 */
	public String getUserCreation() {
		return userCreation;
	}

	/**
	 * @param userCreation the userCreation to set
	 */
	public void setUserCreation(String userCreation) {
		this.userCreation = userCreation;
	}

	/**
	 * @return the nomUserCreation
	 */
	public String getNomUserCreation() {
		return nomUserCreation;
	}

	/**
	 * @param nomUserCreation the nomUserCreation to set
	 */
	public void setNomUserCreation(String nomUserCreation) {
		this.nomUserCreation = nomUserCreation;
	}

	/**
	 * @return the userModification
	 */
	public String getUserModification() {
		return userModification;
	}

	/**
	 * @param userModification the userModification to set
	 */
	public void setUserModification(String userModification) {
		this.userModification = userModification;
	}

	/**
	 * @return the nomUserModification
	 */
	public String getNomUserModification() {
		return nomUserModification;
	}

	/**
	 * @param nomUserModification the nomUserModification to set
	 */
	public void setNomUserModification(String nomUserModification) {
		this.nomUserModification = nomUserModification;
	}

	/**
	 * @return the userCloture
	 */
	public String getUserCloture() {
		return userCloture;
	}

	/**
	 * @param userCloture the userCloture to set
	 */
	public void setUserCloture(String userCloture) {
		this.userCloture = userCloture;
	}

	/**
	 * @return the nomUserCloture
	 */
	public String getNomUserCloture() {
		return nomUserCloture;
	}

	/**
	 * @param nomUserCloture the nomUserCloture to set
	 */
	public void setNomUserCloture(String nomUserCloture) {
		this.nomUserCloture = nomUserCloture;
	}

	/**
	 * @return the ouvertTemporaire
	 */
	public String getOuvertTemporaire() {
		return ouvertTemporaire;
	}

	/**
	 * @param ouvertTemporaire the ouvertTemporaire to set
	 */
	public void setOuvertTemporaire(String ouvertTemporaire) {
		this.ouvertTemporaire = ouvertTemporaire;
	}

	/**
	 * @return the jointAccount
	 */
	public boolean isJointAccount() {
		return jointAccount;
	}

	/**
	 * @param jointAccount the jointAccount to set
	 */
	public void setJointAccount(boolean jointAccount) {
		this.jointAccount = jointAccount;
	}

	/**
	 * @return the jointAccountTitle
	 */
	public String getJointAccountTitle() {
		return jointAccountTitle;
	}

	/**
	 * @param jointAccountTitle the jointAccountTitle to set
	 */
	public void setJointAccountTitle(String jointAccountTitle) {
		this.jointAccountTitle = jointAccountTitle;
	}

	/**
	 * @return the responsibleCustomer
	 */
	public String getResponsibleCustomer() {
		return responsibleCustomer;
	}

	/**
	 * @param responsibleCustomer the responsibleCustomer to set
	 */
	public void setResponsibleCustomer(String responsibleCustomer) {
		this.responsibleCustomer = responsibleCustomer;
	}

	/**
	 * @return the stoppages
	 */
	public List<StoppageAccount> getStoppages() {
		return stoppages;
	}

	/**
	 * @param stoppages the stoppages to set
	 */
	public void setStoppages(List<StoppageAccount> stoppages) {
		this.stoppages = stoppages;
	}
	
}
