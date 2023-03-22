package com.afb.dpd.mobilemoney.jpa.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Client implements Serializable{
		
	private static final long serialVersionUID = 1L;
	private String matricule;
	private String customerName;
	private String departementNaissance;
	private String adresse1;
	private String adresse2;
	private String adresse3;
	private String profil;
	private String codeProfil;
	private String agenceClient;
    private String codeAgenceClient;
	private String sexe;
	private String codeGes;
	private String nomGes;
	private String typeClient;
	private String typePieceIdentite;
	private String formeJuridique;
	private String categorieInterne;
    private String codeCategorieInterne;
	private String titre;
	private String categorieBanqueCentrale;
	private String paysResidence;
	private String prenomMere;
	private String prenomPere;
	private String raisonSociale;
	private String raisonSociale2;
	private String registreCommerce;
	private String validiteRegistreCommerce;
	private String numeroPatente;
	private Date validitePatente;
	private String groupe;
	private String numeroIdentiteSociale;
	private String numeroIdentiteFiscale;
	private String numeroCasier;
	private String emailClient;
	private String residence;
	private String langue;
	private String intitule;
    private String nom;
    private String nomSimple;
    private String prenom;
    private String nomJeuneFille;
    private String nationalite;
    private String typePiece;
    private String numeroPiece;
    private Date dateDelivPiece;
    private String LieudelivPiece;
    private Date dateValiditePiece;
    private Date dateNaissance;
    private String villeNaissance;
    private String paysNaiss;
    private String telDir;
    private String emailDir;
    private String profession;
    private String numTelephone1;
    private String numTelephone2;
    private String revenu;
    private String regimeMat;
    private String employeur;
    private String sitmat;
    private String nomConjoint;
    private Date dateNaissanceConj;
    private String villeNaissanceConj;
    private String typeLienCotitu;
    private String nomMere;
    private String nomPere;
    private String numCompte;
    private String raisonSocial;
    private String siegeSocial;
    private String sigle;
    private String secteurActivite;
    private String numImmatri;
    private Date dateImmatri;
    private String lieuImmatri;
    private String autoriteImm;
    private String numIdentSocial;
    private String numContribuable;
    private Date dateNumContri;
    private String lieuNumContri;
    private String numPatente;
    private Date dateValPatente;
    private String lieuNumPatente;
    private Double chiffreAffaire;
    private Integer effectif;
    private String origineFonds;
    private String tel;
    private String adresse;
    private String email;
    private String siteWeb;
    private String fax;
    private String localisation1;
    private String localisation2;
    private String ville;
    private String nomContact;
    private String prenomContact;
    private String telContact;
    private String emailContact;
    private String nomDir1;
    private String nationaliteDir1;
    private String typePieceDir1;
    private String numeroPieceDir1;
    private Date dateDelivPieceDir1;
    private String LieudelivPieceDir1;
    private Date dateValiditePieceDir1;
    private Date dateNaissanceDir1;
    private String villeNaissanceDir1;
    private String paysNaissDir1;
    private String numTelephoneDir1;
    private String numTelephoneDir2;
    private String emailDir1;
    private String intituleDir1;
    private String fonctionDir1;
    private String nomDir2;
    private String nationaliteDir2;
    private String typePieceDir2;
    private String numeroPieceDir2;
    private Date dateDelivPieceDir2;
    private String LieudelivPieceDir2;
    private Date dateValiditePieceDir2;
    private Date dateNaissanceDir2;
    private String villeNaissanceDir2;
    private String paysNaissDir2;
    private String num1Dir2;
    private String num2Dir2;
    private String emailDir2;
    private String intituleDir2;
    private String fonctionDir2;
    private String dateDelivPieceDir1Modif;
    private String dateValiditePieceDir1Modif;
    private String dateNaissanceDir1Modif;
    private String dateDelivPieceDir2Modif;
    private String dateValiditePieceDir2Modif;
    private String dateNaissanceDir2Modif;
    @JsonIgnore
    private List<Object> autresContact;
    @JsonIgnore
    private List<Object> autresDirigeants;
    private String utiCreation;
    private String utiModification;
    private Date dateCreation;
    private Date dateModification;
    private String segment;
    private String numeroStatistique;
    private Boolean taxable;
    
    
    
    
    
	/**
	 * 
	 */
	public Client() {
		super();
	}
	/**
	 * @return the matricule
	 */
	public String getMatricule() {
		return matricule;
	}
	/**
	 * @param matricule the matricule to set
	 */
	public void setMatricule(String matricule) {
		this.matricule = matricule;
	}
	/**
	 * @return the customerName
	 */
	public String getCustomerName() {
		return customerName;
	}
	/**
	 * @param customerName the customerName to set
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	/**
	 * @return the departementNaissance
	 */
	public String getDepartementNaissance() {
		return departementNaissance;
	}
	/**
	 * @param departementNaissance the departementNaissance to set
	 */
	public void setDepartementNaissance(String departementNaissance) {
		this.departementNaissance = departementNaissance;
	}
	/**
	 * @return the adresse1
	 */
	public String getAdresse1() {
		return adresse1;
	}
	/**
	 * @param adresse1 the adresse1 to set
	 */
	public void setAdresse1(String adresse1) {
		this.adresse1 = adresse1;
	}
	/**
	 * @return the adresse2
	 */
	public String getAdresse2() {
		return adresse2;
	}
	/**
	 * @param adresse2 the adresse2 to set
	 */
	public void setAdresse2(String adresse2) {
		this.adresse2 = adresse2;
	}
	/**
	 * @return the adresse3
	 */
	public String getAdresse3() {
		return adresse3;
	}
	/**
	 * @param adresse3 the adresse3 to set
	 */
	public void setAdresse3(String adresse3) {
		this.adresse3 = adresse3;
	}
	/**
	 * @return the profil
	 */
	public String getProfil() {
		return profil;
	}
	/**
	 * @param profil the profil to set
	 */
	public void setProfil(String profil) {
		this.profil = profil;
	}
	/**
	 * @return the codeProfil
	 */
	public String getCodeProfil() {
		return codeProfil;
	}
	/**
	 * @param codeProfil the codeProfil to set
	 */
	public void setCodeProfil(String codeProfil) {
		this.codeProfil = codeProfil;
	}
	/**
	 * @return the agenceClient
	 */
	public String getAgenceClient() {
		return agenceClient;
	}
	/**
	 * @param agenceClient the agenceClient to set
	 */
	public void setAgenceClient(String agenceClient) {
		this.agenceClient = agenceClient;
	}
	/**
	 * @return the codeAgenceClient
	 */
	public String getCodeAgenceClient() {
		return codeAgenceClient;
	}
	/**
	 * @param codeAgenceClient the codeAgenceClient to set
	 */
	public void setCodeAgenceClient(String codeAgenceClient) {
		this.codeAgenceClient = codeAgenceClient;
	}
	/**
	 * @return the sexe
	 */
	public String getSexe() {
		return sexe;
	}
	/**
	 * @param sexe the sexe to set
	 */
	public void setSexe(String sexe) {
		this.sexe = sexe;
	}
	/**
	 * @return the codeGes
	 */
	public String getCodeGes() {
		return codeGes;
	}
	/**
	 * @param codeGes the codeGes to set
	 */
	public void setCodeGes(String codeGes) {
		this.codeGes = codeGes;
	}
	/**
	 * @return the nomGes
	 */
	public String getNomGes() {
		return nomGes;
	}
	/**
	 * @param nomGes the nomGes to set
	 */
	public void setNomGes(String nomGes) {
		this.nomGes = nomGes;
	}
	/**
	 * @return the typeClient
	 */
	public String getTypeClient() {
		return typeClient;
	}
	/**
	 * @param typeClient the typeClient to set
	 */
	public void setTypeClient(String typeClient) {
		this.typeClient = typeClient;
	}
	/**
	 * @return the typePieceIdentite
	 */
	public String getTypePieceIdentite() {
		return typePieceIdentite;
	}
	/**
	 * @param typePieceIdentite the typePieceIdentite to set
	 */
	public void setTypePieceIdentite(String typePieceIdentite) {
		this.typePieceIdentite = typePieceIdentite;
	}
	/**
	 * @return the formeJuridique
	 */
	public String getFormeJuridique() {
		return formeJuridique;
	}
	/**
	 * @param formeJuridique the formeJuridique to set
	 */
	public void setFormeJuridique(String formeJuridique) {
		this.formeJuridique = formeJuridique;
	}
	/**
	 * @return the categorieInterne
	 */
	public String getCategorieInterne() {
		return categorieInterne;
	}
	/**
	 * @param categorieInterne the categorieInterne to set
	 */
	public void setCategorieInterne(String categorieInterne) {
		this.categorieInterne = categorieInterne;
	}
	/**
	 * @return the codeCategorieInterne
	 */
	public String getCodeCategorieInterne() {
		return codeCategorieInterne;
	}
	/**
	 * @param codeCategorieInterne the codeCategorieInterne to set
	 */
	public void setCodeCategorieInterne(String codeCategorieInterne) {
		this.codeCategorieInterne = codeCategorieInterne;
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
	 * @return the categorieBanqueCentrale
	 */
	public String getCategorieBanqueCentrale() {
		return categorieBanqueCentrale;
	}
	/**
	 * @param categorieBanqueCentrale the categorieBanqueCentrale to set
	 */
	public void setCategorieBanqueCentrale(String categorieBanqueCentrale) {
		this.categorieBanqueCentrale = categorieBanqueCentrale;
	}
	/**
	 * @return the paysResidence
	 */
	public String getPaysResidence() {
		return paysResidence;
	}
	/**
	 * @param paysResidence the paysResidence to set
	 */
	public void setPaysResidence(String paysResidence) {
		this.paysResidence = paysResidence;
	}
	/**
	 * @return the prenomMere
	 */
	public String getPrenomMere() {
		return prenomMere;
	}
	/**
	 * @param prenomMere the prenomMere to set
	 */
	public void setPrenomMere(String prenomMere) {
		this.prenomMere = prenomMere;
	}
	/**
	 * @return the prenomPere
	 */
	public String getPrenomPere() {
		return prenomPere;
	}
	/**
	 * @param prenomPere the prenomPere to set
	 */
	public void setPrenomPere(String prenomPere) {
		this.prenomPere = prenomPere;
	}
	/**
	 * @return the raisonSociale
	 */
	public String getRaisonSociale() {
		return raisonSociale;
	}
	/**
	 * @param raisonSociale the raisonSociale to set
	 */
	public void setRaisonSociale(String raisonSociale) {
		this.raisonSociale = raisonSociale;
	}
	/**
	 * @return the raisonSociale2
	 */
	public String getRaisonSociale2() {
		return raisonSociale2;
	}
	/**
	 * @param raisonSociale2 the raisonSociale2 to set
	 */
	public void setRaisonSociale2(String raisonSociale2) {
		this.raisonSociale2 = raisonSociale2;
	}
	/**
	 * @return the registreCommerce
	 */
	public String getRegistreCommerce() {
		return registreCommerce;
	}
	/**
	 * @param registreCommerce the registreCommerce to set
	 */
	public void setRegistreCommerce(String registreCommerce) {
		this.registreCommerce = registreCommerce;
	}
	/**
	 * @return the validiteRegistreCommerce
	 */
	public String getValiditeRegistreCommerce() {
		return validiteRegistreCommerce;
	}
	/**
	 * @param validiteRegistreCommerce the validiteRegistreCommerce to set
	 */
	public void setValiditeRegistreCommerce(String validiteRegistreCommerce) {
		this.validiteRegistreCommerce = validiteRegistreCommerce;
	}
	/**
	 * @return the numeroPatente
	 */
	public String getNumeroPatente() {
		return numeroPatente;
	}
	/**
	 * @param numeroPatente the numeroPatente to set
	 */
	public void setNumeroPatente(String numeroPatente) {
		this.numeroPatente = numeroPatente;
	}
	/**
	 * @return the validitePatente
	 */
	public Date getValiditePatente() {
		return validitePatente;
	}
	/**
	 * @param validitePatente the validitePatente to set
	 */
	public void setValiditePatente(Date validitePatente) {
		this.validitePatente = validitePatente;
	}
	/**
	 * @return the groupe
	 */
	public String getGroupe() {
		return groupe;
	}
	/**
	 * @param groupe the groupe to set
	 */
	public void setGroupe(String groupe) {
		this.groupe = groupe;
	}
	/**
	 * @return the numeroIdentiteSociale
	 */
	public String getNumeroIdentiteSociale() {
		return numeroIdentiteSociale;
	}
	/**
	 * @param numeroIdentiteSociale the numeroIdentiteSociale to set
	 */
	public void setNumeroIdentiteSociale(String numeroIdentiteSociale) {
		this.numeroIdentiteSociale = numeroIdentiteSociale;
	}
	/**
	 * @return the numeroIdentiteFiscale
	 */
	public String getNumeroIdentiteFiscale() {
		return numeroIdentiteFiscale;
	}
	/**
	 * @param numeroIdentiteFiscale the numeroIdentiteFiscale to set
	 */
	public void setNumeroIdentiteFiscale(String numeroIdentiteFiscale) {
		this.numeroIdentiteFiscale = numeroIdentiteFiscale;
	}
	/**
	 * @return the numeroCasier
	 */
	public String getNumeroCasier() {
		return numeroCasier;
	}
	/**
	 * @param numeroCasier the numeroCasier to set
	 */
	public void setNumeroCasier(String numeroCasier) {
		this.numeroCasier = numeroCasier;
	}
	/**
	 * @return the emailClient
	 */
	public String getEmailClient() {
		return emailClient;
	}
	/**
	 * @param emailClient the emailClient to set
	 */
	public void setEmailClient(String emailClient) {
		this.emailClient = emailClient;
	}
	/**
	 * @return the residence
	 */
	public String getResidence() {
		return residence;
	}
	/**
	 * @param residence the residence to set
	 */
	public void setResidence(String residence) {
		this.residence = residence;
	}
	/**
	 * @return the langue
	 */
	public String getLangue() {
		return langue;
	}
	/**
	 * @param langue the langue to set
	 */
	public void setLangue(String langue) {
		this.langue = langue;
	}
	/**
	 * @return the intitule
	 */
	public String getIntitule() {
		return intitule;
	}
	/**
	 * @param intitule the intitule to set
	 */
	public void setIntitule(String intitule) {
		this.intitule = intitule;
	}
	/**
	 * @return the nom
	 */
	public String getNom() {
		return nom;
	}
	/**
	 * @param nom the nom to set
	 */
	public void setNom(String nom) {
		this.nom = nom;
	}
	/**
	 * @return the nomSimple
	 */
	public String getNomSimple() {
		return nomSimple;
	}
	/**
	 * @param nomSimple the nomSimple to set
	 */
	public void setNomSimple(String nomSimple) {
		this.nomSimple = nomSimple;
	}
	/**
	 * @return the prenom
	 */
	public String getPrenom() {
		return prenom;
	}
	/**
	 * @param prenom the prenom to set
	 */
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	/**
	 * @return the nomJeuneFille
	 */
	public String getNomJeuneFille() {
		return nomJeuneFille;
	}
	/**
	 * @param nomJeuneFille the nomJeuneFille to set
	 */
	public void setNomJeuneFille(String nomJeuneFille) {
		this.nomJeuneFille = nomJeuneFille;
	}
	/**
	 * @return the nationalite
	 */
	public String getNationalite() {
		return nationalite;
	}
	/**
	 * @param nationalite the nationalite to set
	 */
	public void setNationalite(String nationalite) {
		this.nationalite = nationalite;
	}
	/**
	 * @return the typePiece
	 */
	public String getTypePiece() {
		return typePiece;
	}
	/**
	 * @param typePiece the typePiece to set
	 */
	public void setTypePiece(String typePiece) {
		this.typePiece = typePiece;
	}
	/**
	 * @return the numeroPiece
	 */
	public String getNumeroPiece() {
		return numeroPiece;
	}
	/**
	 * @param numeroPiece the numeroPiece to set
	 */
	public void setNumeroPiece(String numeroPiece) {
		this.numeroPiece = numeroPiece;
	}
	/**
	 * @return the dateDelivPiece
	 */
	public Date getDateDelivPiece() {
		return dateDelivPiece;
	}
	/**
	 * @param dateDelivPiece the dateDelivPiece to set
	 */
	public void setDateDelivPiece(Date dateDelivPiece) {
		this.dateDelivPiece = dateDelivPiece;
	}
	/**
	 * @return the lieudelivPiece
	 */
	public String getLieudelivPiece() {
		return LieudelivPiece;
	}
	/**
	 * @param lieudelivPiece the lieudelivPiece to set
	 */
	public void setLieudelivPiece(String lieudelivPiece) {
		LieudelivPiece = lieudelivPiece;
	}
	/**
	 * @return the dateValiditePiece
	 */
	public Date getDateValiditePiece() {
		return dateValiditePiece;
	}
	/**
	 * @param dateValiditePiece the dateValiditePiece to set
	 */
	public void setDateValiditePiece(Date dateValiditePiece) {
		this.dateValiditePiece = dateValiditePiece;
	}
	/**
	 * @return the dateNaissance
	 */
	public Date getDateNaissance() {
		return dateNaissance;
	}
	/**
	 * @param dateNaissance the dateNaissance to set
	 */
	public void setDateNaissance(Date dateNaissance) {
		this.dateNaissance = dateNaissance;
	}
	/**
	 * @return the villeNaissance
	 */
	public String getVilleNaissance() {
		return villeNaissance;
	}
	/**
	 * @param villeNaissance the villeNaissance to set
	 */
	public void setVilleNaissance(String villeNaissance) {
		this.villeNaissance = villeNaissance;
	}
	/**
	 * @return the paysNaiss
	 */
	public String getPaysNaiss() {
		return paysNaiss;
	}
	/**
	 * @param paysNaiss the paysNaiss to set
	 */
	public void setPaysNaiss(String paysNaiss) {
		this.paysNaiss = paysNaiss;
	}
	/**
	 * @return the telDir
	 */
	public String getTelDir() {
		return telDir;
	}
	/**
	 * @param telDir the telDir to set
	 */
	public void setTelDir(String telDir) {
		this.telDir = telDir;
	}
	/**
	 * @return the emailDir
	 */
	public String getEmailDir() {
		return emailDir;
	}
	/**
	 * @param emailDir the emailDir to set
	 */
	public void setEmailDir(String emailDir) {
		this.emailDir = emailDir;
	}
	/**
	 * @return the profession
	 */
	public String getProfession() {
		return profession;
	}
	/**
	 * @param profession the profession to set
	 */
	public void setProfession(String profession) {
		this.profession = profession;
	}
	/**
	 * @return the numTelephone1
	 */
	public String getNumTelephone1() {
		return numTelephone1;
	}
	/**
	 * @param numTelephone1 the numTelephone1 to set
	 */
	public void setNumTelephone1(String numTelephone1) {
		this.numTelephone1 = numTelephone1;
	}
	/**
	 * @return the numTelephone2
	 */
	public String getNumTelephone2() {
		return numTelephone2;
	}
	/**
	 * @param numTelephone2 the numTelephone2 to set
	 */
	public void setNumTelephone2(String numTelephone2) {
		this.numTelephone2 = numTelephone2;
	}
	/**
	 * @return the revenu
	 */
	public String getRevenu() {
		return revenu;
	}
	/**
	 * @param revenu the revenu to set
	 */
	public void setRevenu(String revenu) {
		this.revenu = revenu;
	}
	/**
	 * @return the regimeMat
	 */
	public String getRegimeMat() {
		return regimeMat;
	}
	/**
	 * @param regimeMat the regimeMat to set
	 */
	public void setRegimeMat(String regimeMat) {
		this.regimeMat = regimeMat;
	}
	/**
	 * @return the employeur
	 */
	public String getEmployeur() {
		return employeur;
	}
	/**
	 * @param employeur the employeur to set
	 */
	public void setEmployeur(String employeur) {
		this.employeur = employeur;
	}
	/**
	 * @return the sitmat
	 */
	public String getSitmat() {
		return sitmat;
	}
	/**
	 * @param sitmat the sitmat to set
	 */
	public void setSitmat(String sitmat) {
		this.sitmat = sitmat;
	}
	/**
	 * @return the nomConjoint
	 */
	public String getNomConjoint() {
		return nomConjoint;
	}
	/**
	 * @param nomConjoint the nomConjoint to set
	 */
	public void setNomConjoint(String nomConjoint) {
		this.nomConjoint = nomConjoint;
	}
	/**
	 * @return the dateNaissanceConj
	 */
	public Date getDateNaissanceConj() {
		return dateNaissanceConj;
	}
	/**
	 * @param dateNaissanceConj the dateNaissanceConj to set
	 */
	public void setDateNaissanceConj(Date dateNaissanceConj) {
		this.dateNaissanceConj = dateNaissanceConj;
	}
	/**
	 * @return the villeNaissanceConj
	 */
	public String getVilleNaissanceConj() {
		return villeNaissanceConj;
	}
	/**
	 * @param villeNaissanceConj the villeNaissanceConj to set
	 */
	public void setVilleNaissanceConj(String villeNaissanceConj) {
		this.villeNaissanceConj = villeNaissanceConj;
	}
	/**
	 * @return the typeLienCotitu
	 */
	public String getTypeLienCotitu() {
		return typeLienCotitu;
	}
	/**
	 * @param typeLienCotitu the typeLienCotitu to set
	 */
	public void setTypeLienCotitu(String typeLienCotitu) {
		this.typeLienCotitu = typeLienCotitu;
	}
	/**
	 * @return the nomMere
	 */
	public String getNomMere() {
		return nomMere;
	}
	/**
	 * @param nomMere the nomMere to set
	 */
	public void setNomMere(String nomMere) {
		this.nomMere = nomMere;
	}
	/**
	 * @return the nomPere
	 */
	public String getNomPere() {
		return nomPere;
	}
	/**
	 * @param nomPere the nomPere to set
	 */
	public void setNomPere(String nomPere) {
		this.nomPere = nomPere;
	}
	/**
	 * @return the numCompte
	 */
	public String getNumCompte() {
		return numCompte;
	}
	/**
	 * @param numCompte the numCompte to set
	 */
	public void setNumCompte(String numCompte) {
		this.numCompte = numCompte;
	}
	/**
	 * @return the raisonSocial
	 */
	public String getRaisonSocial() {
		return raisonSocial;
	}
	/**
	 * @param raisonSocial the raisonSocial to set
	 */
	public void setRaisonSocial(String raisonSocial) {
		this.raisonSocial = raisonSocial;
	}
	/**
	 * @return the siegeSocial
	 */
	public String getSiegeSocial() {
		return siegeSocial;
	}
	/**
	 * @param siegeSocial the siegeSocial to set
	 */
	public void setSiegeSocial(String siegeSocial) {
		this.siegeSocial = siegeSocial;
	}
	/**
	 * @return the sigle
	 */
	public String getSigle() {
		return sigle;
	}
	/**
	 * @param sigle the sigle to set
	 */
	public void setSigle(String sigle) {
		this.sigle = sigle;
	}
	/**
	 * @return the secteurActivite
	 */
	public String getSecteurActivite() {
		return secteurActivite;
	}
	/**
	 * @param secteurActivite the secteurActivite to set
	 */
	public void setSecteurActivite(String secteurActivite) {
		this.secteurActivite = secteurActivite;
	}
	/**
	 * @return the numImmatri
	 */
	public String getNumImmatri() {
		return numImmatri;
	}
	/**
	 * @param numImmatri the numImmatri to set
	 */
	public void setNumImmatri(String numImmatri) {
		this.numImmatri = numImmatri;
	}
	/**
	 * @return the dateImmatri
	 */
	public Date getDateImmatri() {
		return dateImmatri;
	}
	/**
	 * @param dateImmatri the dateImmatri to set
	 */
	public void setDateImmatri(Date dateImmatri) {
		this.dateImmatri = dateImmatri;
	}
	/**
	 * @return the lieuImmatri
	 */
	public String getLieuImmatri() {
		return lieuImmatri;
	}
	/**
	 * @param lieuImmatri the lieuImmatri to set
	 */
	public void setLieuImmatri(String lieuImmatri) {
		this.lieuImmatri = lieuImmatri;
	}
	/**
	 * @return the autoriteImm
	 */
	public String getAutoriteImm() {
		return autoriteImm;
	}
	/**
	 * @param autoriteImm the autoriteImm to set
	 */
	public void setAutoriteImm(String autoriteImm) {
		this.autoriteImm = autoriteImm;
	}
	/**
	 * @return the numIdentSocial
	 */
	public String getNumIdentSocial() {
		return numIdentSocial;
	}
	/**
	 * @param numIdentSocial the numIdentSocial to set
	 */
	public void setNumIdentSocial(String numIdentSocial) {
		this.numIdentSocial = numIdentSocial;
	}
	/**
	 * @return the numContribuable
	 */
	public String getNumContribuable() {
		return numContribuable;
	}
	/**
	 * @param numContribuable the numContribuable to set
	 */
	public void setNumContribuable(String numContribuable) {
		this.numContribuable = numContribuable;
	}
	/**
	 * @return the dateNumContri
	 */
	public Date getDateNumContri() {
		return dateNumContri;
	}
	/**
	 * @param dateNumContri the dateNumContri to set
	 */
	public void setDateNumContri(Date dateNumContri) {
		this.dateNumContri = dateNumContri;
	}
	/**
	 * @return the lieuNumContri
	 */
	public String getLieuNumContri() {
		return lieuNumContri;
	}
	/**
	 * @param lieuNumContri the lieuNumContri to set
	 */
	public void setLieuNumContri(String lieuNumContri) {
		this.lieuNumContri = lieuNumContri;
	}
	/**
	 * @return the numPatente
	 */
	public String getNumPatente() {
		return numPatente;
	}
	/**
	 * @param numPatente the numPatente to set
	 */
	public void setNumPatente(String numPatente) {
		this.numPatente = numPatente;
	}
	/**
	 * @return the dateValPatente
	 */
	public Date getDateValPatente() {
		return dateValPatente;
	}
	/**
	 * @param dateValPatente the dateValPatente to set
	 */
	public void setDateValPatente(Date dateValPatente) {
		this.dateValPatente = dateValPatente;
	}
	/**
	 * @return the lieuNumPatente
	 */
	public String getLieuNumPatente() {
		return lieuNumPatente;
	}
	/**
	 * @param lieuNumPatente the lieuNumPatente to set
	 */
	public void setLieuNumPatente(String lieuNumPatente) {
		this.lieuNumPatente = lieuNumPatente;
	}
	/**
	 * @return the chiffreAffaire
	 */
	public Double getChiffreAffaire() {
		return chiffreAffaire;
	}
	/**
	 * @param chiffreAffaire the chiffreAffaire to set
	 */
	public void setChiffreAffaire(Double chiffreAffaire) {
		this.chiffreAffaire = chiffreAffaire;
	}
	/**
	 * @return the effectif
	 */
	public Integer getEffectif() {
		return effectif;
	}
	/**
	 * @param effectif the effectif to set
	 */
	public void setEffectif(Integer effectif) {
		this.effectif = effectif;
	}
	/**
	 * @return the origineFonds
	 */
	public String getOrigineFonds() {
		return origineFonds;
	}
	/**
	 * @param origineFonds the origineFonds to set
	 */
	public void setOrigineFonds(String origineFonds) {
		this.origineFonds = origineFonds;
	}
	/**
	 * @return the tel
	 */
	public String getTel() {
		return tel;
	}
	/**
	 * @param tel the tel to set
	 */
	public void setTel(String tel) {
		this.tel = tel;
	}
	/**
	 * @return the adresse
	 */
	public String getAdresse() {
		return adresse;
	}
	/**
	 * @param adresse the adresse to set
	 */
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the siteWeb
	 */
	public String getSiteWeb() {
		return siteWeb;
	}
	/**
	 * @param siteWeb the siteWeb to set
	 */
	public void setSiteWeb(String siteWeb) {
		this.siteWeb = siteWeb;
	}
	/**
	 * @return the fax
	 */
	public String getFax() {
		return fax;
	}
	/**
	 * @param fax the fax to set
	 */
	public void setFax(String fax) {
		this.fax = fax;
	}
	/**
	 * @return the localisation1
	 */
	public String getLocalisation1() {
		return localisation1;
	}
	/**
	 * @param localisation1 the localisation1 to set
	 */
	public void setLocalisation1(String localisation1) {
		this.localisation1 = localisation1;
	}
	/**
	 * @return the localisation2
	 */
	public String getLocalisation2() {
		return localisation2;
	}
	/**
	 * @param localisation2 the localisation2 to set
	 */
	public void setLocalisation2(String localisation2) {
		this.localisation2 = localisation2;
	}
	/**
	 * @return the ville
	 */
	public String getVille() {
		return ville;
	}
	/**
	 * @param ville the ville to set
	 */
	public void setVille(String ville) {
		this.ville = ville;
	}
	/**
	 * @return the nomContact
	 */
	public String getNomContact() {
		return nomContact;
	}
	/**
	 * @param nomContact the nomContact to set
	 */
	public void setNomContact(String nomContact) {
		this.nomContact = nomContact;
	}
	/**
	 * @return the prenomContact
	 */
	public String getPrenomContact() {
		return prenomContact;
	}
	/**
	 * @param prenomContact the prenomContact to set
	 */
	public void setPrenomContact(String prenomContact) {
		this.prenomContact = prenomContact;
	}
	/**
	 * @return the telContact
	 */
	public String getTelContact() {
		return telContact;
	}
	/**
	 * @param telContact the telContact to set
	 */
	public void setTelContact(String telContact) {
		this.telContact = telContact;
	}
	/**
	 * @return the emailContact
	 */
	public String getEmailContact() {
		return emailContact;
	}
	/**
	 * @param emailContact the emailContact to set
	 */
	public void setEmailContact(String emailContact) {
		this.emailContact = emailContact;
	}
	/**
	 * @return the nomDir1
	 */
	public String getNomDir1() {
		return nomDir1;
	}
	/**
	 * @param nomDir1 the nomDir1 to set
	 */
	public void setNomDir1(String nomDir1) {
		this.nomDir1 = nomDir1;
	}
	/**
	 * @return the nationaliteDir1
	 */
	public String getNationaliteDir1() {
		return nationaliteDir1;
	}
	/**
	 * @param nationaliteDir1 the nationaliteDir1 to set
	 */
	public void setNationaliteDir1(String nationaliteDir1) {
		this.nationaliteDir1 = nationaliteDir1;
	}
	/**
	 * @return the typePieceDir1
	 */
	public String getTypePieceDir1() {
		return typePieceDir1;
	}
	/**
	 * @param typePieceDir1 the typePieceDir1 to set
	 */
	public void setTypePieceDir1(String typePieceDir1) {
		this.typePieceDir1 = typePieceDir1;
	}
	/**
	 * @return the numeroPieceDir1
	 */
	public String getNumeroPieceDir1() {
		return numeroPieceDir1;
	}
	/**
	 * @param numeroPieceDir1 the numeroPieceDir1 to set
	 */
	public void setNumeroPieceDir1(String numeroPieceDir1) {
		this.numeroPieceDir1 = numeroPieceDir1;
	}
	/**
	 * @return the dateDelivPieceDir1
	 */
	public Date getDateDelivPieceDir1() {
		return dateDelivPieceDir1;
	}
	/**
	 * @param dateDelivPieceDir1 the dateDelivPieceDir1 to set
	 */
	public void setDateDelivPieceDir1(Date dateDelivPieceDir1) {
		this.dateDelivPieceDir1 = dateDelivPieceDir1;
	}
	/**
	 * @return the lieudelivPieceDir1
	 */
	public String getLieudelivPieceDir1() {
		return LieudelivPieceDir1;
	}
	/**
	 * @param lieudelivPieceDir1 the lieudelivPieceDir1 to set
	 */
	public void setLieudelivPieceDir1(String lieudelivPieceDir1) {
		LieudelivPieceDir1 = lieudelivPieceDir1;
	}
	/**
	 * @return the dateValiditePieceDir1
	 */
	public Date getDateValiditePieceDir1() {
		return dateValiditePieceDir1;
	}
	/**
	 * @param dateValiditePieceDir1 the dateValiditePieceDir1 to set
	 */
	public void setDateValiditePieceDir1(Date dateValiditePieceDir1) {
		this.dateValiditePieceDir1 = dateValiditePieceDir1;
	}
	/**
	 * @return the dateNaissanceDir1
	 */
	public Date getDateNaissanceDir1() {
		return dateNaissanceDir1;
	}
	/**
	 * @param dateNaissanceDir1 the dateNaissanceDir1 to set
	 */
	public void setDateNaissanceDir1(Date dateNaissanceDir1) {
		this.dateNaissanceDir1 = dateNaissanceDir1;
	}
	/**
	 * @return the villeNaissanceDir1
	 */
	public String getVilleNaissanceDir1() {
		return villeNaissanceDir1;
	}
	/**
	 * @param villeNaissanceDir1 the villeNaissanceDir1 to set
	 */
	public void setVilleNaissanceDir1(String villeNaissanceDir1) {
		this.villeNaissanceDir1 = villeNaissanceDir1;
	}
	/**
	 * @return the paysNaissDir1
	 */
	public String getPaysNaissDir1() {
		return paysNaissDir1;
	}
	/**
	 * @param paysNaissDir1 the paysNaissDir1 to set
	 */
	public void setPaysNaissDir1(String paysNaissDir1) {
		this.paysNaissDir1 = paysNaissDir1;
	}
	/**
	 * @return the numTelephoneDir1
	 */
	public String getNumTelephoneDir1() {
		return numTelephoneDir1;
	}
	/**
	 * @param numTelephoneDir1 the numTelephoneDir1 to set
	 */
	public void setNumTelephoneDir1(String numTelephoneDir1) {
		this.numTelephoneDir1 = numTelephoneDir1;
	}
	/**
	 * @return the numTelephoneDir2
	 */
	public String getNumTelephoneDir2() {
		return numTelephoneDir2;
	}
	/**
	 * @param numTelephoneDir2 the numTelephoneDir2 to set
	 */
	public void setNumTelephoneDir2(String numTelephoneDir2) {
		this.numTelephoneDir2 = numTelephoneDir2;
	}
	/**
	 * @return the emailDir1
	 */
	public String getEmailDir1() {
		return emailDir1;
	}
	/**
	 * @param emailDir1 the emailDir1 to set
	 */
	public void setEmailDir1(String emailDir1) {
		this.emailDir1 = emailDir1;
	}
	/**
	 * @return the intituleDir1
	 */
	public String getIntituleDir1() {
		return intituleDir1;
	}
	/**
	 * @param intituleDir1 the intituleDir1 to set
	 */
	public void setIntituleDir1(String intituleDir1) {
		this.intituleDir1 = intituleDir1;
	}
	/**
	 * @return the fonctionDir1
	 */
	public String getFonctionDir1() {
		return fonctionDir1;
	}
	/**
	 * @param fonctionDir1 the fonctionDir1 to set
	 */
	public void setFonctionDir1(String fonctionDir1) {
		this.fonctionDir1 = fonctionDir1;
	}
	/**
	 * @return the nomDir2
	 */
	public String getNomDir2() {
		return nomDir2;
	}
	/**
	 * @param nomDir2 the nomDir2 to set
	 */
	public void setNomDir2(String nomDir2) {
		this.nomDir2 = nomDir2;
	}
	/**
	 * @return the nationaliteDir2
	 */
	public String getNationaliteDir2() {
		return nationaliteDir2;
	}
	/**
	 * @param nationaliteDir2 the nationaliteDir2 to set
	 */
	public void setNationaliteDir2(String nationaliteDir2) {
		this.nationaliteDir2 = nationaliteDir2;
	}
	/**
	 * @return the typePieceDir2
	 */
	public String getTypePieceDir2() {
		return typePieceDir2;
	}
	/**
	 * @param typePieceDir2 the typePieceDir2 to set
	 */
	public void setTypePieceDir2(String typePieceDir2) {
		this.typePieceDir2 = typePieceDir2;
	}
	/**
	 * @return the numeroPieceDir2
	 */
	public String getNumeroPieceDir2() {
		return numeroPieceDir2;
	}
	/**
	 * @param numeroPieceDir2 the numeroPieceDir2 to set
	 */
	public void setNumeroPieceDir2(String numeroPieceDir2) {
		this.numeroPieceDir2 = numeroPieceDir2;
	}
	/**
	 * @return the dateDelivPieceDir2
	 */
	public Date getDateDelivPieceDir2() {
		return dateDelivPieceDir2;
	}
	/**
	 * @param dateDelivPieceDir2 the dateDelivPieceDir2 to set
	 */
	public void setDateDelivPieceDir2(Date dateDelivPieceDir2) {
		this.dateDelivPieceDir2 = dateDelivPieceDir2;
	}
	/**
	 * @return the lieudelivPieceDir2
	 */
	public String getLieudelivPieceDir2() {
		return LieudelivPieceDir2;
	}
	/**
	 * @param lieudelivPieceDir2 the lieudelivPieceDir2 to set
	 */
	public void setLieudelivPieceDir2(String lieudelivPieceDir2) {
		LieudelivPieceDir2 = lieudelivPieceDir2;
	}
	/**
	 * @return the dateValiditePieceDir2
	 */
	public Date getDateValiditePieceDir2() {
		return dateValiditePieceDir2;
	}
	/**
	 * @param dateValiditePieceDir2 the dateValiditePieceDir2 to set
	 */
	public void setDateValiditePieceDir2(Date dateValiditePieceDir2) {
		this.dateValiditePieceDir2 = dateValiditePieceDir2;
	}
	/**
	 * @return the dateNaissanceDir2
	 */
	public Date getDateNaissanceDir2() {
		return dateNaissanceDir2;
	}
	/**
	 * @param dateNaissanceDir2 the dateNaissanceDir2 to set
	 */
	public void setDateNaissanceDir2(Date dateNaissanceDir2) {
		this.dateNaissanceDir2 = dateNaissanceDir2;
	}
	/**
	 * @return the villeNaissanceDir2
	 */
	public String getVilleNaissanceDir2() {
		return villeNaissanceDir2;
	}
	/**
	 * @param villeNaissanceDir2 the villeNaissanceDir2 to set
	 */
	public void setVilleNaissanceDir2(String villeNaissanceDir2) {
		this.villeNaissanceDir2 = villeNaissanceDir2;
	}
	/**
	 * @return the paysNaissDir2
	 */
	public String getPaysNaissDir2() {
		return paysNaissDir2;
	}
	/**
	 * @param paysNaissDir2 the paysNaissDir2 to set
	 */
	public void setPaysNaissDir2(String paysNaissDir2) {
		this.paysNaissDir2 = paysNaissDir2;
	}
	/**
	 * @return the num1Dir2
	 */
	public String getNum1Dir2() {
		return num1Dir2;
	}
	/**
	 * @param num1Dir2 the num1Dir2 to set
	 */
	public void setNum1Dir2(String num1Dir2) {
		this.num1Dir2 = num1Dir2;
	}
	/**
	 * @return the num2Dir2
	 */
	public String getNum2Dir2() {
		return num2Dir2;
	}
	/**
	 * @param num2Dir2 the num2Dir2 to set
	 */
	public void setNum2Dir2(String num2Dir2) {
		this.num2Dir2 = num2Dir2;
	}
	/**
	 * @return the emailDir2
	 */
	public String getEmailDir2() {
		return emailDir2;
	}
	/**
	 * @param emailDir2 the emailDir2 to set
	 */
	public void setEmailDir2(String emailDir2) {
		this.emailDir2 = emailDir2;
	}
	/**
	 * @return the intituleDir2
	 */
	public String getIntituleDir2() {
		return intituleDir2;
	}
	/**
	 * @param intituleDir2 the intituleDir2 to set
	 */
	public void setIntituleDir2(String intituleDir2) {
		this.intituleDir2 = intituleDir2;
	}
	/**
	 * @return the fonctionDir2
	 */
	public String getFonctionDir2() {
		return fonctionDir2;
	}
	/**
	 * @param fonctionDir2 the fonctionDir2 to set
	 */
	public void setFonctionDir2(String fonctionDir2) {
		this.fonctionDir2 = fonctionDir2;
	}
	/**
	 * @return the dateDelivPieceDir1Modif
	 */
	public String getDateDelivPieceDir1Modif() {
		return dateDelivPieceDir1Modif;
	}
	/**
	 * @param dateDelivPieceDir1Modif the dateDelivPieceDir1Modif to set
	 */
	public void setDateDelivPieceDir1Modif(String dateDelivPieceDir1Modif) {
		this.dateDelivPieceDir1Modif = dateDelivPieceDir1Modif;
	}
	/**
	 * @return the dateValiditePieceDir1Modif
	 */
	public String getDateValiditePieceDir1Modif() {
		return dateValiditePieceDir1Modif;
	}
	/**
	 * @param dateValiditePieceDir1Modif the dateValiditePieceDir1Modif to set
	 */
	public void setDateValiditePieceDir1Modif(String dateValiditePieceDir1Modif) {
		this.dateValiditePieceDir1Modif = dateValiditePieceDir1Modif;
	}
	/**
	 * @return the dateNaissanceDir1Modif
	 */
	public String getDateNaissanceDir1Modif() {
		return dateNaissanceDir1Modif;
	}
	/**
	 * @param dateNaissanceDir1Modif the dateNaissanceDir1Modif to set
	 */
	public void setDateNaissanceDir1Modif(String dateNaissanceDir1Modif) {
		this.dateNaissanceDir1Modif = dateNaissanceDir1Modif;
	}
	/**
	 * @return the dateDelivPieceDir2Modif
	 */
	public String getDateDelivPieceDir2Modif() {
		return dateDelivPieceDir2Modif;
	}
	/**
	 * @param dateDelivPieceDir2Modif the dateDelivPieceDir2Modif to set
	 */
	public void setDateDelivPieceDir2Modif(String dateDelivPieceDir2Modif) {
		this.dateDelivPieceDir2Modif = dateDelivPieceDir2Modif;
	}
	/**
	 * @return the dateValiditePieceDir2Modif
	 */
	public String getDateValiditePieceDir2Modif() {
		return dateValiditePieceDir2Modif;
	}
	/**
	 * @param dateValiditePieceDir2Modif the dateValiditePieceDir2Modif to set
	 */
	public void setDateValiditePieceDir2Modif(String dateValiditePieceDir2Modif) {
		this.dateValiditePieceDir2Modif = dateValiditePieceDir2Modif;
	}
	/**
	 * @return the dateNaissanceDir2Modif
	 */
	public String getDateNaissanceDir2Modif() {
		return dateNaissanceDir2Modif;
	}
	/**
	 * @param dateNaissanceDir2Modif the dateNaissanceDir2Modif to set
	 */
	public void setDateNaissanceDir2Modif(String dateNaissanceDir2Modif) {
		this.dateNaissanceDir2Modif = dateNaissanceDir2Modif;
	}
	/**
	 * @return the utiCreation
	 */
	public String getUtiCreation() {
		return utiCreation;
	}
	/**
	 * @param utiCreation the utiCreation to set
	 */
	public void setUtiCreation(String utiCreation) {
		this.utiCreation = utiCreation;
	}
	/**
	 * @return the utiModification
	 */
	public String getUtiModification() {
		return utiModification;
	}
	/**
	 * @param utiModification the utiModification to set
	 */
	public void setUtiModification(String utiModification) {
		this.utiModification = utiModification;
	}
	/**
	 * @return the dateCreation
	 */
	public Date getDateCreation() {
		return dateCreation;
	}
	/**
	 * @param dateCreation the dateCreation to set
	 */
	public void setDateCreation(Date dateCreation) {
		this.dateCreation = dateCreation;
	}
	/**
	 * @return the dateModification
	 */
	public Date getDateModification() {
		return dateModification;
	}
	/**
	 * @param dateModification the dateModification to set
	 */
	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
	}
	/**
	 * @return the segment
	 */
	public String getSegment() {
		return segment;
	}
	/**
	 * @param segment the segment to set
	 */
	public void setSegment(String segment) {
		this.segment = segment;
	}
	/**
	 * @return the numeroStatistique
	 */
	public String getNumeroStatistique() {
		return numeroStatistique;
	}
	/**
	 * @param numeroStatistique the numeroStatistique to set
	 */
	public void setNumeroStatistique(String numeroStatistique) {
		this.numeroStatistique = numeroStatistique;
	}
	/**
	 * @return the taxable
	 */
	public Boolean getTaxable() {
		return taxable;
	}
	/**
	 * @param taxable the taxable to set
	 */
	public void setTaxable(Boolean taxable) {
		this.taxable = taxable;
	}
	/**
	 * @return the autresContact
	 */
	public List<Object> getAutresContact() {
		return autresContact;
	}
	/**
	 * @param autresContact the autresContact to set
	 */
	public void setAutresContact(List<Object> autresContact) {
		this.autresContact = autresContact;
	}
	/**
	 * @return the autresDirigeants
	 */
	public List<Object> getAutresDirigeants() {
		return autresDirigeants;
	}
	/**
	 * @param autresDirigeants the autresDirigeants to set
	 */
	public void setAutresDirigeants(List<Object> autresDirigeants) {
		this.autresDirigeants = autresDirigeants;
	}
	
	
    
}
