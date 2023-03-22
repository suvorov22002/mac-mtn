/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.tools;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Classe representant la table des evenements de Amplitude
 * @author Francis DJIOMOU
 * @version 1.0
 */

@JsonIgnoreProperties(value={ "id" }, allowGetters= true,allowSetters= true)
@SuppressWarnings("serial")
@Entity
@NamedQueries({
    @NamedQuery(name="bkeve.findAll", query="SELECT c FROM bkeve c"),
    @NamedQuery(name=bkeve.UPDATE_EVENT,query="Update bkeve eve Set eve.eta = :eta, eve.etap = :etap Where eve.id=:id"),
}) 
@Table(name = "MOMO_bkeve")
public class bkeve implements Serializable {

	/**
	 * Default Constructor
	 */
	public bkeve() {}
	
	public static final String UPDATE_EVENT = "bkeve.updateEvent";
	
	@JsonIgnore
//	@JsonProperty("id")
	@Id
	//@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@JsonProperty("id")
	@Transient
	private String e_id;
	
	private String agsa = "00001"; 			// agence de saisie CHAR 5
	private String age = "00001"; 			// Agence CHAR 5 1 t001Agence cacc
	private String ope; 			// Code opération CHAR 3 2 bkope ope
	private String eve; 			// Numéro d'évènement CHAR 6 3
	private String typ = "100"; 			// Type rattaché CHAR 3
	private String ndos = " "; 			// Numéro de dossier rattaché CHAR 15
	private String age1; 			// Agence compte 1 CHAR 5 t001Agence cacc
	private String dev1; 			// Code devise compte 1 CHAR 3 bkcom dev
	private String ncp1; 			// Numéro de compte 1 CHAR 11 bkcom ncp
	private String suf1; 			// Suffixe compte 1 CHAR 2 bkcom suf
	private String clc1; 			// Clé de contrôle compte 1 CHAR 2
	private String cli1; 			// Code client compte 1 CHAR 15 bkcli cli
	private String nom1; 			// Nom client compte 1 CHAR 36
	private String ges1; 			// Code gestionnaire compte 1 CHAR 3 t035Gestionnaires cacc
	private String sen1 = "D"; 			// Sens opération compte 1 (D/C) CHAR 1
	private Double mht1; 			// Montant nominal en devise du compte à débiter DECIMAL 19 - 4
	private Double mon1 = 0d; 		// Montant net compte 1 DECIMAL 19 - 4
	private Date dva1; 				// Date de valeur compte 1 DATE
	private String exo1 = "N"; 			// Exonération de commission de mouvement compte 1 CHAR 1
	private Double sol1 = 0d; 		// Solde compte 1 avant opération DECIMAL 19 - 4
	private Double indh1 = 0d; 		// Indisponible hors SBF compte 1 avant opération DECIMAL 19 - 4
	private Double inds1 = 0d; 		// Indisponible SBF compte 1 avant opération DECIMAL 19 - 4
	private String desa1 = " "; 			// Code désaccord 1 à la saisie CHAR 4 t058Desaccords cacc
	private String desa2 = " "; 			// Code désaccord 2 à la saisie CHAR 4 t058Desaccords cacc
	private String desa3 = " ";			// Code désaccord 3 à la saisie CHAR 4 t058Desaccords cacc
	private String desa4 = " "; 			// Code désaccord 4 à la saisie CHAR 4 t058Desaccords cacc
	private String desa5 = " "; // Code désaccord 5 à la saisie CHAR 4 t058Desaccords cacc
	private String age2; // Agence compte 2 CHAR 5 bkcom age
	private String dev2; // Code devise compte 2 CHAR 3 bkcom dev
	private String ncp2; // Numéro de compte 2 CHAR 11 bkcom ncp
	private String suf2; // Suffixe compte 2 CHAR 2 bkcom suf
	private String clc2; // Clé de contrôle compte 2 CHAR 2
	private String cli2; // Code client compte 2 CHAR 15 bkcli cli
	private String nom2; // Nom client compte 2 CHAR 36
	private String ges2; // Code gestionnaire compte 2 CHAR 3 t035Gestionnaires cacc
	private String sen2 = "C"; // Sens opération compte 2 (D/C) CHAR 1
	private Double mht2 = 0d; // Montant nominal en devise du compte à créditer DECIMAL 19 - 4
	private Double mon2 = 0d; // Montant net compte 2 DECIMAL 19 - 4
	private Date dva2; // Date de valeur compte 2 DATE
	private Date din = new Date(); // Date d'indisponible DATE
	private String exo2 = "O"; // Exonération de commission de mouvement compte 2 CHAR 1
	private Double sol2 = 0d; // Solde compte 2 avant opération DECIMAL 19 - 4
	private Double indh2 = 0d; // Indisponible hors SBF compte 2 avant opération DECIMAL 19 - 4
	private Double inds2 = 0d; // Indisponible SBF compte 2 avant opération DECIMAL 19 - 4
	private String desc1 = " "; // Code désaccord 1 compte à créditer CHAR 4 t058Desaccords cacc
	private String desc2 = " "; // Code désaccord 2 compte à créditer CHAR 4 t058Desaccords cacc
	private String desc3 = " "; // 3 compte à créditer CHAR 4 t058Desaccords cacc
	private String desc4 = " "; // Code désaccord 4 compte à créditer CHAR 4 t058Desaccords cacc
	private String desc5 = " "; // Code désaccord 5 compte à créditer CHAR 4 t058Desaccords cacc
	private String etab = " "; // Code établissement bancaire de règlement CHAR 5 bkbqe etab
	private String guib = " "; // Code guichet CHAR 5
	private String nome = " "; // Nom de l'établissement bancaire CHAR 40
	private String domi = " "; // Domiciliation CHAR 30
	private String adb1 = " "; // Adresse banque 1 CHAR 30
	private String adb2 = " "; // Adresse banque 2 CHAR 30
	private String adb3 = " "; // Adresse banque 3 CHAR 30
	private String vilb = " "; // Ville banque CHAR 30
	private String cpob = " "; // Code postal CHAR 6
	private String cpay = " "; // Code pays banque CHAR 3
	private String etabr = " "; // Code établissement de règlement CHAR 5
	private String guibr = " "; // Code guichet de règlement CHAR 5
	private String comb = " "; // Numéro de compte CHAR 25
	private String cleb = " "; // Clé RIB CHAR 2
	private String nomb = " "; // Nom client autre banque CHAR 30
	private Double mban = 0d; // Montant net banque DECIMAL 19 - 4
	private Date dvab; // Date de valeur banque DATE
	private String cai1 = " "; // Numéro de caisse devise nationale CHAR 3 t004Caisses cacc
	private String tyc1 = " "; // Type de caisse à débiter CHAR 1
	private String dcai1 = " "; // Code devise caisse à débiter CHAR 3
	private String scai1 = " "; // Sens caisse devise nationale (D/C) CHAR 1
	private Double mcai1 = 0d; // Montant caisse devise nationale DECIMAL 19 - 4
	private Double arrc1 = 0d; // Montant arrondi caisse à débiter (retrait) DECIMAL 19 - 4
	private String cai2 = " "; // ; //Numéro de caisse autres devises CHAR 3 t004Caisses cacc
	private String tyc2 = " "; // Type de caisse à créditer CHAR 1
	private String dcai2 = " "; // Code devise caisse à créditer CHAR 3
	private String scai2 = " "; // Sens caisse autre devise (D/C) CHAR 1
	private Double mcai2 = 0d; // Montant caisse autre devise DECIMAL 19 - 4
	private Double arrc2 = 0d; // Montant arrondi caisse à créditer DECIMAL 19 - 4
	private String dev; // Code devise de la transaction CHAR 3 t005DevisesN cacc
	private Double mht = 0d; // Montant nominal opération (hors taxes) DECIMAL 19 - 4
	private Double mnat = 0d; // Contrevaleur en devise nationale DECIMAL 19 - 4
	private Double mbor = 0d; // Montant du bordereau DECIMAL 19 - 4
	private String nbor = " "; // Numéro de bordereau CHAR 6
	private Integer nblig = 0; // Nombre de lignes constituant un lot saisi SMALLINT
	private Double tcai2 = 1d; // Taux de change devise transaction/devise compte DECIMAL 15 - 7
	private Double tcai3 = 1d; // Taux de change devise transaction/devise nationale DECIMAL 15 - 7
	private String nat = "VIRMAC"; // Nature de la transaction CHAR 6 t052NaturOper cacc
	private String nato = " "; // Nature d'origine CHAR 6
	private String opeo = " "; // Code opération d'origine CHAR 3
	private String eveo = " "; // Numéro d'évènement d'origine CHAR 6
	private String pieo = " "; // Numéro de pièce comptable d'origine CHAR 11
	private Date dou = new Date(); // Date de création DATE
	private Date dco; // Date comptable DATE
	private String eta = "VA"; // Etat de l'évènement (VA/AT/FO/VF/IG/IF/AB) CHAR 2
	private String etap = "  "; // Etat précédent de l'évènement CHAR 2
	private Integer nbv = 0; // Nombre de validations requises DECIMAL 1 - 0
	private Integer nval = 0; // validation effectué DECIMAL 1 - 0
	private String uti; // Code utilisateur ayant initié l'évènement CHAR 10 evuti cuti
	private String utf = " "; // Code utilisateur ayant rappelé pour forcer CHAR 10 evuti cuti
	private String uta = " "; // Code utilisateur ayant autorisé CHAR 10 evuti cuti
	private Double moa = 0d; // Clé de forçage DECIMAL 6 - 0
	private Double mof = 0d; // Clé de forçage retrait déplacé DECIMAL 6 - 0
	private String lib1 = "MOMO PULL/PUSH FROM ACCOUNT"; // Libellé libre CHAR 40
	private String lib2 = " "; // Idem CHAR 40
	private String lib3 = " "; // Idem CHAR 40
	private String lib4 = " "; // Idem CHAR 40
	private String lib5 = " "; // Idem CHAR 40
	private String lib6 = " "; // Libellé libre CHAR 50
	private String lib7 = " "; // Idem CHAR 50
	private String lib8 = " "; // Idem CHAR 50
	private String lib9 = " "; // Libellé réservé pour mémorisation du code lettrage CHAR 50
	private String lib10 = " "; // Libellé libre CHAR 50
	private String agec = " "; // Agence de centralisation pour service étranger CHAR 5 t001Agence cacc
	private String agep = " "; // Agence de provenance CHAR 5
	private String intr = "C"; // Code édition livret CHAR 1
	private String orig = "S"; // = "I";
	private String rlet = " "; // Référence de lettrage CHAR 8
	private String catr = " "; // Code évènement à transférer CHAR 1
	private String ceb = "N"; // Opération compensable (O/N) CHAR 1
	private String plb; // Place bancable (O/N) CHAR 1
	private String cco = "0"; // Code compensation (0/1/2/3/4/5) CHAR 1
	private Date dret; // Date de retour compensation DATE
	private String natp = " "; // Nature pièce d'identité CHAR 10
	private String nump = " "; // Numéro de pièce d'identité CHAR 20
	private String datp; // Date de délivrance de la pièce d'identité DATE
	private String nomp = " "; // Nom du porteur CHAR 36
	private String ad1p = " "; // Adresse 1 du porteur CHAR 30
	private String ad2p = " "; // Adresse 2 du porteur CHAR 30
	private String delp = " "; // Lieu de délivrance de la pièce d'identité porteur CHAR 30
	private String serie = " "; // Série de numéro de chèque CHAR 2
	private String nche = " "; // Numéro de chèque CHAR 14 bkchq nche
	private String chql = "N"; // Chèque client (O/N) CHAR 1
	private String chqc = "N"; // Code chèque certifié (O/N) CHAR 1
	private String cab = "N"; // Chèque autre banque (O/N) CHAR 1
	private String ncff = " "; // émis en devise CHAR 8
	private String csa = " "; // Type de chèque (S/A) CHAR 1
	private Date dech; // Date d'échéance DATE
	private String tire = " "; // Code tiré autre banque CHAR 6 bktir cod
	private String agti = " "; // Agios à la charge du tiré (O/N) CHAR 1
	private String agre = " "; // Agios à la charge du tiré à rétrocéder (O/N) CHAR 1
	private Integer nbji = 0; // Nombre de jours d'intérêts DECIMAL 3 - 0
	private String ptfc = " "; // Effet généré en portefeuille centralisé (O/N) CHAR 1
	private String efav = " "; // Effet avalisé (O/N) CHAR 1
	private String navl = " "; // Numéro d'aval (caution) CHAR 6
	private String edom = " "; // Effet domicilié (O/N) CHAR 1
	private String eopp = " "; // Effet en opposition (O/N) CHAR 1
	private String efac = " "; // Effet accepté (O/N) CHAR 1
	private String moti = " "; // Motif de non acceptation CHAR 4
	private String envacc = " "; // Effet à envoyer à l'acceptation (O/n) CHAR 1
	private String enom = "N"; // Edition du nom CHAR 1
	private String vicl = "N"; // Virement même client CHAR 1
	private String teco = " "; // Télex ou courrier CHAR 1
	private String tenv = " "; // Télex envoyé (O/N) CHAR 1
	private String exjo = "N"; // Virement à exécution jour (O/N) CHAR 1
	private String org = " "; // Code organisme domiciliateur de prélèvements CHAR 5
	private String cai3 = " "; // Numéro de caisse T.C. CHAR 3 t004Caisses cacc
	private Double mcai3 = 0d; // Montant caisse T.C. DECIMAL 19 - 4
	private String forc = " "; // Opération pouvant être forcée CHAR 1
	private String ocai3 = "  "; // Organisme T.C. CHAR 2 t053OrganismeTC cacc
	private Integer ncai3 = 0; // Nombre de T.C. DECIMAL 3 - 0
	private String csp1 = "VMoMo"; // Code spécifique CHAR 10
	private String csp2 = " "; // Idem CHAR 10
	private String csp3 = " "; // Idem CHAR 10
	private String csp4 = " "; // Idem CHAR 10
	private String csp5 = " "; // Idem CHAR 10
	private String ndom = " "; // Numéro de domiciliation (étranger) CHAR 25
	private String cmod = " "; // Code motif déclaré CHAR 10
	private String devf = "001"; // Devise compte de frais CHAR 3 bkcom dev
	private String ncpf; // Numéro de compte de frais CHAR 11 bkcom ncp
	private String suff = "  "; // Suffixe du compte de frais CHAR 2 bkcom suf
	private Double monf = 0d; // Montant du compte de frais DECIMAL 19 - 4
	private Date dvaf; // Date de valeur du compte de frais DATE
	private String exof = " "; // Exonération de commission compte de frais CHAR 1
	private String agee = " "; // Compte d'encaissement/engagement : agence CHAR 5 bkcom ncp
	private String deve = " "; // Compte d'encaissement/engagement : code devise CHAR 3 bkcom dev
	private String ncpe = " "; // Compte d'encaissement/engagement : numéro CHAR 11 bkcom ncp
	private String sufe = " "; // Compte d'encaissement/engagement : suffixe CHAR 2 bkcom suf
	private String clce = "  "; // : clé de contrôl CHAR 2
	private String ncpi = " "; // Numéro de compte d'impayé CHAR 11
	private String sufi = "  "; // Suffixe compte d'impayé CHAR 2
	private Double mimp = 0d; // Montant frais d'impayés DECIMAL 19 - 4
	private Date dvai; // Date de valeur impayé DATE
	private String ncpp = " "; // Numéro de compte de provision CHAR 11
	private String sufp = "  "; // Suffixe compte de provision CHAR 2
	private Double prga = 0d; // Pourcentage retenue de garantie DECIMAL 10 - 7
	private Double mrga = 0d; // Montant provision en devise du compte DECIMAL 19 - 4
	private String term = " "; // Terme du dossier CHAR 1
	private String tvar = "O"; // TVA récupérée (O/N) CHAR 1
	private String intp = " "; // Intérêts précomptés (O/N) CHAR 1
	private String cap = " "; // Capitalisation des intérêts (O/N) CHAR 1
	private String prll = " "; // Prélèvements libératoires CHAR 1
	private String ano = " "; // BDC anonyme (O/N) CHAR 1
	private String etab1 = " "; // Code établissement bancaire 1 CHAR 5 bkbqe etab
	private String guib1 = " "; // Code guichet bancaire 1 CHAR 5 bkbqe guib
	private String com1b = " "; // Numéro de compte 1 client autre banque CHAR 15
	private String etab2 = " "; // Code établissement bancaire 2 CHAR 5 bkbqe etab
	private String guib2 = " "; // Code guichet bancaire 2 CHAR 5 bkbqe guib
	private String com2b = " "; // Numéro de compte client 2 autre banque CHAR 15
	private Double tcom1 = 0d; // Taux de commission 1 DECIMAL 15 - 7
	private Double mcom1 = 0d; // Montant commission 1 en devise nationale DECIMAL 19 - 4
	private Double tcom2 = 0d; // Taux de commission 2 DECIMAL 15 - 7
	private Double mcom2 = 0d; // Montant commission 2 en devise nationale DECIMAL 19 - 4
	private Double tcom3 = 0d; // Taux de commission 3 DECIMAL 15 - 7
	private Double mcom3 = 0d; // Montant commission 3 en devise nationale DECIMAL 19 - 4
	private Double frai1 = 0d; // Montant frais 1 en devise nationale DECIMAL 19 - 4
	private Double frai2 = 0d; // Montant frais 2 en devise nationale DECIMAL 19 - 4
	private Double frai3 = 0d; // Montant frais 3 en devise nationale DECIMAL 19 - 4
	private Double ttax1 = 0d; // Taux de taxe 1 DECIMAL 15 - 7
	private Double mtax1 = 0d; // Montant taxe 1 en devise nationale DECIMAL 19 - 4
	private Double ttax2 = 0d; // Taux de taxe 2 DECIMAL 15 - 7
	private Double mtax2 = 0d; // Montant taxe 2 en devise nationale DECIMAL 19 - 4
	private Double ttax3 = 0d; // Taux de taxe 3 DECIMAL 15 - 7
	private Double mtax3 = 0d; // Montant taxe 3 en devise nationale DECIMAL 19 - 4
	private Double mnt1 = 0d; // Montant libre DECIMAL 19 - 4
	private Double mnt2 = 0d; // Idem DECIMAL 19 - 4
	private Double mnt3 = 0d; // Idem DECIMAL 19 - 4
	private Double mnt4 = 0d; // Idem DECIMAL 19 - 4
	private Double mnt5 = 0d; // Idem DECIMAL 19 - 4
	private String tyc3 = "N";
	private String dcai3 = " "; // Devise numérique de la caisse CHAR 3 t002DevisesA dev
	private String scai3 = " "; // Sens de la caisse CHAR 1
	private Double arrc3 = 0d; // Arrondi caisse DECIMAL 19 - 4
	private Double mhtd = 0d; // Différence entre le net à créditer et le montant DECIMAL 19 - 4
	private Double tcai4 = 1d; // Taux de change entre la devise des billets DECIMAL 15 - 7
	private String tope = " "; // la télécompensation CHAR 3
	private String img = "N";
	private Date dsai = new Date(); // Date de saisie DATE
	private String hsai = new SimpleDateFormat("HH':'mm':'ss").format(new Date()); // Heure de saisie CHAR 12
	private String paysp = " "; // Code pays du remettant et du porteur CHAR 3
	private String pdelp = " "; // Pièce délivrée par CHAR 30
	private String manda = "N"; // Mandataire (O/N) CHAR 1
	private String refdos = " "; // Référence de la procuration VARCHAR 0 - 20
	private Double tchfr = 0d; // Taux de change francophone DECIMAL 15 - 7
	
	@Transient
	@JsonIgnore
	@JsonProperty("modenuit")
	private boolean modenuit;
	
	@Transient
	@JsonIgnore
	private Date dateTraitementsuspendInTFJ;
	/**
	 * Transaction ayant genere l'evenement
	 */
	// Ajoute fetch = FetchType.LAZY
	@JsonIgnore
	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
	@JoinColumn(name = "TRX_ID")
	private Transaction transaction;
	
	/**
	 * Liste des ecritures de la transaction
	 */
	// Change fetch = FetchType.EAGER
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
    @JoinTable(
			name = "MoMo_eve_bkmvti",
			joinColumns = {@JoinColumn(name = "EVE_ID")}
	)
	private List<bkmvti> ecritures = new ArrayList<bkmvti>();
	
	/**
	 * Marqueur determinant si l'evenement n'a pas ete poste dans Delta pour cause de TFJ
	 */
	@Column(name = "SUSPEND_IN_TFJ")
	private Boolean suspendInTFJ = Boolean.FALSE;
	
	//private List<bkevec> commissions = new ArrayList<bkevec>();
	
	@Version
	@Column(columnDefinition = "integer DEFAULT 0", nullable = false)  
	private Long version;
	
	
	/**
	 * 
	 * @param transaction
	 * @param ope
	 * @param eve
	 * @param dev
	 * @param mht
	 * @param nat
	 * @param dco
	 * @param uti
	 * @param tcom1
	 * @param frai1
	 * @param ttax1
	 * @param mnt1
	 */
	public bkeve(Transaction transaction, String ope, String eve, String dev, Double mht, String nat, Date dco, String uti, Double tcom1, Double frai1, Double ttax1, Double mnt1) {
		super();
		this.transaction = transaction;
		this.ope = ope;
		this.eve = eve;
		this.dev = dev;
		this.mht = mht;
		this.mnat = mht;
		this.mbor = mht;
		this.nat = nat;
		this.dco = dco;
		this.din = dco;
		this.dou = dco;
		//this.dsai = dco;
		this.uti = uti;
		this.tcom1 = tcom1;
		this.frai1 = frai1;
		this.ttax1 = ttax1;
		this.mnt1 = mnt1;
	}

	
	
	/**
	 * 
	 * @param transaction
	 * @param ope
	 * @param eve
	 * @param dev
	 * @param mht
	 * @param nat
	 * @param dco
	 * @param uti
	 * @param tcom1
	 * @param frai1
	 * @param ttax1
	 * @param mnt1
	 * @param mnt2
	 */
	public bkeve(Transaction transaction, String ope, String eve, String dev, Double mht, String nat, Date dco, String uti, Double tcom1, Double frai1, Double ttax1, Double mnt1, Double mnt2) {
		super();
		this.transaction = transaction;
		this.ope = ope;
		this.eve = eve;
		this.dev = dev;
		this.mht = mht;
		this.mnat = mht;
		this.mbor = mht;
		this.nat = nat;
		this.dco = dco;
		this.din = dco;
		this.dou = dco;
		//this.dsai = dco;
		this.uti = uti;
		this.tcom1 = tcom1;
		this.frai1 = frai1;
		this.ttax1 = ttax1;
		this.mnt1 = mnt1;
		this.mnt2 = mnt2;
	}


	/**
	 * 
	 * @param age1
	 * @param dev1
	 * @param ncp1
	 * @param suf1
	 * @param clc1
	 * @param cli1
	 * @param nom1
	 * @param ges1
	 * @param mht1
	 * @param mon1
	 * @param dva1
	 * @param sol1
	 */
	@JsonIgnore
	public void setDebiteur(String age1, String dev1, String ncp1,
			String suf1, String clc1, String cli1, String nom1, String ges1,
			Double mht1, Double mon1, Date dva1, 
			Double sol1) {
		this.age1 = age1;
		this.dev1 = dev1;
		this.ncp1 = ncp1;
		this.suf1 = suf1;
		this.clc1 = clc1;
		this.cli1 = cli1;
		this.nom1 = nom1;
		this.ges1 = ges1;
		this.mht1 = mht1;
		this.mon1 = mon1;
		this.dva1 = dva1;
		this.sol1 = sol1;
	}
	
	
	/**
	 * 
	 * @param age1
	 * @param dev1
	 * @param ncp1
	 * @param suf1
	 * @param clc1
	 * @param cli1
	 * @param nom1
	 * @param ges1
	 * @param sol1
	 */
	@JsonIgnore
	public void setDebiteur(String age1, String dev1, String ncp1,
			String suf1, String clc1, String cli1, String nom1, String ges1, Double sol1) {
		this.age1 = age1;
		this.dev1 = dev1;
		this.ncp1 = ncp1;
		this.suf1 = suf1;
		this.clc1 = clc1;
		this.cli1 = cli1;
		this.nom1 = nom1;
		this.ges1 = ges1;
		this.sol1 = sol1;
	}
	

	/**
	 * 
	 * @param age2
	 * @param dev2
	 * @param ncp2
	 * @param suf2
	 * @param clc2
	 * @param cli2
	 * @param nom2
	 * @param ges2
	 * @param mht2
	 * @param mon2
	 * @param dva2
	 * @param sol2
	 */
	@JsonIgnore
	public void setCrediteur(String age2, String dev2, String ncp2, String suf2,
			String clc2, String cli2, String nom2, String ges2, 
			Double mht2, Double mon2, Date dva2, Double sol2) {
		this.age2 = age2;
		this.dev2 = dev2;
		this.ncp2 = ncp2;
		this.suf2 = suf2;
		this.clc2 = clc2;
		this.cli2 = cli2;
		this.nom2 = nom2;
		this.ges2 = ges2;
		this.mht2 = mht2;
		this.mon2 = mon2;
		this.dva2 = dva2;
		this.sol2 = sol2;
	}

	
	/**
	 * 
	 * @param age2
	 * @param dev2
	 * @param ncp2
	 * @param suf2
	 * @param clc2
	 * @param cli2
	 * @param nom2
	 * @param ges2
	 * @param sol2
	 */
	@JsonIgnore
	public void updateCrediteur(String age2, String dev2, String ncp2, String suf2,
			String clc2, String cli2, String nom2, String ges2, Double sol2) {
		this.age2 = age2;
		this.dev2 = dev2;
		this.ncp2 = ncp2;
		this.suf2 = suf2;
		this.clc2 = clc2;
		this.cli2 = cli2;
		this.nom2 = nom2;
		this.ges2 = ges2;
		this.sol2 = sol2;
	}


	/**
	 * Retourne la requete de creation de l'evenement
	 * @return requete de creation de l'evenement
	 */
	@JsonIgnore
	public String getSaveQuery() {
		return "INSERT INTO BKEVE (AGSA, AGE,   OPE, EVE, TYP,   NDOS, AGE1, DEV1,   NCP1, SUF1, CLC1, CLI1, NOM1, GES1, SEN1, MHT1, MON1, " + 
				"DVA1, EXO1, SOL1, INDH1, INDS1, DESA1, DESA2, DESA3, DESA4, DESA5, AGE2, DEV2, NCP2, SUF2, CLC2, CLI2, NOM2, GES2,SEN2, MHT2, MON2, " +
				"DVA2, DIN, EXO2, SOL2, INDH2, INDS2, DESC1, DESC2, DESC3, DESC4, DESC5, ETAB, GUIB, NOME, DOMI, ADB1, ADB2, ADB3, VILB, CPOB, CPAY, " +
				"ETABR, GUIBR, COMB, CLEB, NOMB, MBAN, DVAB, CAI1, TYC1, DCAI1, SCAI1, MCAI1, ARRC1, CAI2, TYC2, DCAI2, SCAI2, MCAI2, ARRC2, DEV, MHT, " +
				"MNAT, MBOR, NBOR, NBLIG, TCAI2, TCAI3, NAT, NATO, OPEO, EVEO, PIEO, DOU, DCO, ETA, ETAP, NBV, NVAL, UTI, UTF, UTA, MOA, MOF, LIB1, LIB2, " +
				"LIB3, LIB4, LIB5, LIB6, LIB7, LIB8, LIB9, LIB10, AGEC, AGEP, INTR, ORIG, RLET, CATR, CEB, PLB, CCO, DRET, NATP, NUMP, DATP, NOMP, AD1P, AD2P, " +
				"DELP, SERIE, NCHE, CHQL, CHQC, CAB, NCFF, CSA, DECH, TIRE, AGTI, AGRE, NBJI, PTFC, EFAV, NAVL, EDOM, EOPP, EFAC, MOTI, ENVACC, ENOM, VICL, TECO, " +
				"TENV, EXJO, ORG, CAI3, MCAI3, FORC, OCAI3, NCAI3, CSP1, CSP2, CSP3, CSP4, CSP5, NDOM, CMOD, DEVF, NCPF, SUFF, MONF, DVAF, EXOF, AGEE, DEVE, NCPE, " +
				"SUFE, CLCE, NCPI, SUFI, MIMP, DVAI, NCPP, SUFP, PRGA, MRGA, TERM, TVAR, INTP, CAP, PRLL, ANO, ETAB1, GUIB1, COM1B, ETAB2, GUIB2, COM2B, TCOM1, MCOM1," + 
				"TCOM2, MCOM2, TCOM3, MCOM3, FRAI1, FRAI2, FRAI3, TTAX1, MTAX1, TTAX2, MTAX2, TTAX3, MTAX3, MNT1, MNT2, MNT3, MNT4, MNT5, TYC3, DCAI3, SCAI3," + 
				"ARRC3, MHTD, TCAI4, TOPE, IMG, DSAI, HSAI, PAYSP, PDELP, MANDA, REFDOS, TCHFR)" +
				"VALUES (?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?," + 
				"?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, " +
				"?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, " +
				"?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?," + 
				"?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?," + 
				"?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?,  ?, ?,  ?, ? )";
	}
	
	
	@JsonIgnore
	public Object[] getQueryValues() {
		
		Object[] values = new Object[233];
		
		values[0] = agsa;
		values[1] = age; 
		values[2] = ope; 
		values[3] = eve; 
		values[4] = typ; 
		values[5] = ndos;
		values[6] = age1;
		values[7] = dev1;
		values[8] = ncp1;
		values[9] = suf1;
		values[10] = clc1;
		values[11] = cli1;
		values[12] = nom1;
		values[13] = ges1;
		values[14] = sen1;
		values[15] = mht1;
		values[16] = mon1;
		values[17] = dva1;
		values[18] = exo1;
		values[19] = sol1;
		values[20] = indh1;
		values[21] = inds1;
		values[22] = desa1;
		values[23] = desa2;
		values[24] = desa3;
		values[25] = desa4;
		values[26] = desa5;
		values[27] = age2; 
		values[28] = dev2; 
		values[29] = ncp2; 
		values[30] = suf2;
		values[31] = clc2; 
		values[32] = cli2; 
		values[33] = nom2; 
		values[34] = ges2;
		values[35] = sen2;
		values[36] = mht2;
		values[37] = mon2 ;
		values[38] = dva2;
		values[39] = din;
		values[40] = exo2;
		values[41] = sol2;
		values[42] = indh2;
		values[43] = inds2;
		values[44] = desc1;
		values[45] = desc2;
		values[46] = desc3;
		values[47] = desc4;
		values[48] = desc5;
		values[49] = etab;
		values[50] = guib;
		values[51] = nome;
		values[52] = domi;
		values[53] = adb1;
		values[54] = adb2;
		values[55] = adb3;
		values[56] = vilb;
		values[57] = cpob;
		values[58] = cpay;
		values[59] = etabr;
		values[60] = guibr;
		values[61] = comb; 
		values[62] = cleb;
		values[63] = nomb;
		values[64] = mban;
		values[65] = dvab;
		values[66] = cai1;
		values[67] = tyc1;
		values[68] = dcai1;
		values[69] = scai1;
		values[70] = mcai1;
		values[71] = arrc1;
		values[72] = cai2;
		values[73] = tyc2;
		values[74] = dcai2;
		values[75] = scai2;
		values[76] = mcai2;
		values[77] = arrc2;
		values[78] = dev;
		values[79] = mht;
		values[80] = mnat;
		values[81] = mbor;
		values[82] = nbor;
		values[83] = nblig;
		values[84] = tcai2;
		values[85] = tcai3;
		values[86] = nat;
		values[87] = nato;
		values[88] = opeo;
		values[89] = eveo;
		values[90] = pieo;
		values[91] = dou;
		values[92] = dco;
		values[93] = eta;
		values[94] = etap;
		values[95] = nbv;
		values[96] = nval;
		values[97] = uti;
		values[98] = utf;
		values[99] = uta;
		values[100] = moa;
		values[101] = mof;
		values[102] = lib1;
		values[103] = lib2;
		values[104] = lib3;
		values[105] = lib4;
		values[106] = lib5;
		values[107] = lib6;
		values[108] = lib7;
		values[109] = lib8;
		values[110] = lib9; 
		values[111] = lib10;
		values[112] = agec;
		values[113] = agep;
		values[114] = intr;
		values[115] = orig;
		values[116] = rlet;
		values[117] = catr;
		values[118] = ceb; 
		values[119] = plb; 
		values[120] = cco; 
		values[121] = dret;
		values[122] = natp;
		values[123] = nump;
		values[124] = datp;
		values[125] = nomp;
		values[126] = ad1p;
		values[127] = ad2p;
		values[128] = delp;
		values[129] = serie;
		values[130] = nche;
		values[131] = chql;
		values[132] = chqc;
		values[133] = cab; 
		values[134] = ncff;
		values[135] = csa; 
		values[136] = dech;
		values[137] = tire;
		values[138] = agti;
		values[139] = agre;
		values[140] = nbji ;
		values[141] = ptfc;
		values[142] = efav;
		values[143] = navl;
		values[144] = edom;
		values[145] = eopp;
		values[146] = efac;
		values[147] = moti;
		values[148] = envacc;
		values[149] = enom;
		values[150] = vicl;
		values[151] = teco;
		values[152] = tenv;
		values[153] = exjo;
		values[154] = org;
		values[155] = cai3;
		values[156] = mcai3;
		values[157] = forc;
		values[158] = ocai3;
		values[159] = ncai3;
		values[160] = csp1;
		values[161] = csp2;
		values[162] = csp3;
		values[163] = csp4;
		values[164] = csp5;
		values[165] = ndom;
		values[166] = cmod;
		values[167] = devf;
		values[168] = ncpf;
		values[169] = suff;
		values[170] = monf;
		values[171] = dvaf;
		values[172] = exof;
		values[173] = agee;
		values[174] = deve;
		values[175] = ncpe;
		values[176] = sufe;
		values[177] = clce;
		values[178] = ncpi;
		values[179] = sufi;
		values[180] = mimp;
		values[181] = dvai;
		values[182] = ncpp;
		values[183] = sufp;
		values[184] = prga;
		values[185] = mrga;
		values[186] = term;
		values[187] = tvar;
		values[188] = intp;
		values[189] = cap;
		values[190] = prll;
		values[191] = ano;
		values[192] = etab1;
		values[193] = guib1;
		values[194] = com1b;
		values[195] = etab2;
		values[196] = guib2;
		values[197] = com2b;
		values[198] = tcom1;
		values[199] = mcom1;
		values[200] = tcom2;
		values[201] = mcom2;
		values[202] = tcom3;
		values[203] = mcom3;
		values[204] = frai1;
		values[205] = frai2;
		values[206] = frai3;
		values[207] = ttax1;
		values[208] = mtax1;
		values[209] = ttax2;
		values[210] = mtax2;
		values[211] = ttax3;
		values[212] = mtax3;
		values[213] = mnt1;
		values[214] = mnt2;
		values[215] = mnt3;
		values[216] = mnt4;
		values[217] = mnt5;
		values[218] = tyc3;
		values[219] = dcai3;
		values[220] = scai3;
		values[221] = arrc3;
		values[222] = mhtd;
		values[223] = tcai4;
		values[224] = tope;
		values[225] = img;
		values[226] = dsai;
		values[227] = hsai;
		values[228] = paysp;
		values[229] = pdelp;
		values[230] = manda;
		values[231] = refdos;
		values[232] = tchfr;
		
		return values;
		
	}
	
	@JsonIgnore
	public String getCheckQuery(){
		return "select * from BKEVE where OPE=? and EVE=? and AGE1=? and NCP1=? and CLC1=? and MON1=? and AGE2=? and NCP2=? and CLC2=? and MON2=? and DCO=? and ETA=?";		
	}
	
	@JsonIgnore
	public Object[] getQueryCheckValues() {

		Object[] values = new Object[12];
		values[0] = ope; 
		values[1] = eve; 
		values[2] = age1;
		values[3] = ncp1;
		values[4] = clc1;
		values[5] = mon1;
		values[6] = age2; 
		values[7] = ncp2; 
		values[8] = clc2; 
		values[9] = mon2 ;
		values[10] = dco;
		values[11] = eta;
		return values;

	}
	
	
	/**
	 * @return the agsa
	 */
	public String getAgsa() {
		return agsa;
	}
	/**
	 * @param agsa the agsa to set
	 */
	public void setAgsa(String agsa) {
		this.agsa = agsa;
	}
	/**
	 * @return the age
	 */
	public String getAge() {
		return age;
	}
	/**
	 * @param age the age to set
	 */
	public void setAge(String age) {
		this.age = age;
	}
	/**
	 * @return the ope
	 */
	public String getOpe() {
		return ope;
	}
	/**
	 * @param ope the ope to set
	 */
	public void setOpe(String ope) {
		this.ope = ope;
	}
	/**
	 * @return the eve
	 */
	public String getEve() {
		return eve;
	}
	/**
	 * @param eve the eve to set
	 */
	public void setEve(String eve) {
		this.eve = eve;
	}
	/**
	 * @return the typ
	 */
	public String getTyp() {
		return typ;
	}
	/**
	 * @param typ the typ to set
	 */
	public void setTyp(String typ) {
		this.typ = typ;
	}
	/**
	 * @return the ndos
	 */
	public String getNdos() {
		return ndos;
	}
	/**
	 * @param ndos the ndos to set
	 */
	public void setNdos(String ndos) {
		this.ndos = ndos;
	}
	/**
	 * @return the age1
	 */
	public String getAge1() {
		return age1;
	}
	/**
	 * @param age1 the age1 to set
	 */
	public void setAge1(String age1) {
		this.age1 = age1;
	}
	/**
	 * @return the dev1
	 */
	public String getDev1() {
		return dev1;
	}
	/**
	 * @param dev1 the dev1 to set
	 */
	public void setDev1(String dev1) {
		this.dev1 = dev1;
	}
	/**
	 * @return the ncp1
	 */
	public String getNcp1() {
		return ncp1;
	}
	/**
	 * @param ncp1 the ncp1 to set
	 */
	public void setNcp1(String ncp1) {
		this.ncp1 = ncp1;
	}
	/**
	 * @return the suf1
	 */
	public String getSuf1() {
		return suf1;
	}
	/**
	 * @param suf1 the suf1 to set
	 */
	public void setSuf1(String suf1) {
		this.suf1 = suf1;
	}
	/**
	 * @return the clc1
	 */
	public String getClc1() {
		return clc1;
	}
	/**
	 * @param clc1 the clc1 to set
	 */
	public void setClc1(String clc1) {
		this.clc1 = clc1;
	}
	/**
	 * @return the cli1
	 */
	public String getCli1() {
		return cli1;
	}
	/**
	 * @param cli1 the cli1 to set
	 */
	public void setCli1(String cli1) {
		this.cli1 = cli1;
	}
	/**
	 * @return the nom1
	 */
	public String getNom1() {
		return nom1;
	}
	/**
	 * @param nom1 the nom1 to set
	 */
	public void setNom1(String nom1) {
		this.nom1 = nom1;
	}
	/**
	 * @return the ges1
	 */
	public String getGes1() {
		return ges1;
	}
	/**
	 * @param ges1 the ges1 to set
	 */
	public void setGes1(String ges1) {
		this.ges1 = ges1;
	}
	/**
	 * @return the sen1
	 */
	public String getSen1() {
		return sen1;
	}
	/**
	 * @param sen1 the sen1 to set
	 */
	public void setSen1(String sen1) {
		this.sen1 = sen1;
	}
	/**
	 * @return the mht1
	 */
	public Double getMht1() {
		return mht1;
	}
	/**
	 * @param mht1 the mht1 to set
	 */
	public void setMht1(Double mht1) {
		this.mht1 = mht1;
	}
	/**
	 * @return the mon1
	 */
	public Double getMon1() {
		return mon1;
	}
	/**
	 * @param mon1 the mon1 to set
	 */
	public void setMon1(Double mon1) {
		this.mon1 = mon1;
	}
	/**
	 * @return the dva1
	 */
	public Date getDva1() {
		return dva1;
	}
	/**
	 * @param dva1 the dva1 to set
	 */
	public void setDva1(Date dva1) {
		this.dva1 = dva1;
	}
	/**
	 * @return the exo1
	 */
	public String getExo1() {
		return exo1;
	}
	/**
	 * @param exo1 the exo1 to set
	 */
	public void setExo1(String exo1) {
		this.exo1 = exo1;
	}
	/**
	 * @return the sol1
	 */
	public Double getSol1() {
		return sol1;
	}
	/**
	 * @param sol1 the sol1 to set
	 */
	public void setSol1(Double sol1) {
		this.sol1 = sol1;
	}
	/**
	 * @return the indh1
	 */
	public Double getIndh1() {
		return indh1;
	}
	/**
	 * @param indh1 the indh1 to set
	 */
	public void setIndh1(Double indh1) {
		this.indh1 = indh1;
	}
	/**
	 * @return the inds1
	 */
	public Double getInds1() {
		return inds1;
	}
	/**
	 * @param inds1 the inds1 to set
	 */
	public void setInds1(Double inds1) {
		this.inds1 = inds1;
	}
	/**
	 * @return the desa1
	 */
	public String getDesa1() {
		return desa1;
	}
	/**
	 * @param desa1 the desa1 to set
	 */
	public void setDesa1(String desa1) {
		this.desa1 = desa1;
	}
	/**
	 * @return the desa2
	 */
	public String getDesa2() {
		return desa2;
	}
	/**
	 * @param desa2 the desa2 to set
	 */
	public void setDesa2(String desa2) {
		this.desa2 = desa2;
	}
	/**
	 * @return the desa3
	 */
	public String getDesa3() {
		return desa3;
	}
	/**
	 * @param desa3 the desa3 to set
	 */
	public void setDesa3(String desa3) {
		this.desa3 = desa3;
	}
	/**
	 * @return the desa4
	 */
	public String getDesa4() {
		return desa4;
	}
	/**
	 * @param desa4 the desa4 to set
	 */
	public void setDesa4(String desa4) {
		this.desa4 = desa4;
	}
	/**
	 * @return the desa5
	 */
	public String getDesa5() {
		return desa5;
	}
	/**
	 * @param desa5 the desa5 to set
	 */
	public void setDesa5(String desa5) {
		this.desa5 = desa5;
	}
	/**
	 * @return the age2
	 */
	public String getAge2() {
		return age2;
	}
	/**
	 * @param age2 the age2 to set
	 */
	public void setAge2(String age2) {
		this.age2 = age2;
	}
	/**
	 * @return the dev2
	 */
	public String getDev2() {
		return dev2;
	}
	/**
	 * @param dev2 the dev2 to set
	 */
	public void setDev2(String dev2) {
		this.dev2 = dev2;
	}
	/**
	 * @return the ncp2
	 */
	public String getNcp2() {
		return ncp2;
	}
	/**
	 * @param ncp2 the ncp2 to set
	 */
	public void setNcp2(String ncp2) {
		this.ncp2 = ncp2;
	}
	/**
	 * @return the suf2
	 */
	public String getSuf2() {
		return suf2;
	}
	/**
	 * @param suf2 the suf2 to set
	 */
	public void setSuf2(String suf2) {
		this.suf2 = suf2;
	}
	/**
	 * @return the clc2
	 */
	public String getClc2() {
		return clc2;
	}
	/**
	 * @param clc2 the clc2 to set
	 */
	public void setClc2(String clc2) {
		this.clc2 = clc2;
	}
	/**
	 * @return the cli2
	 */
	public String getCli2() {
		return cli2;
	}
	/**
	 * @param cli2 the cli2 to set
	 */
	public void setCli2(String cli2) {
		this.cli2 = cli2;
	}
	/**
	 * @return the nom2
	 */
	public String getNom2() {
		return nom2;
	}
	/**
	 * @param nom2 the nom2 to set
	 */
	public void setNom2(String nom2) {
		this.nom2 = nom2;
	}
	/**
	 * @return the ges2
	 */
	public String getGes2() {
		return ges2;
	}
	/**
	 * @param ges2 the ges2 to set
	 */
	public void setGes2(String ges2) {
		this.ges2 = ges2;
	}
	/**
	 * @return the sen2
	 */
	public String getSen2() {
		return sen2;
	}
	/**
	 * @param sen2 the sen2 to set
	 */
	public void setSen2(String sen2) {
		this.sen2 = sen2;
	}
	/**
	 * @return the mht2
	 */
	public Double getMht2() {
		return mht2;
	}
	/**
	 * @param mht2 the mht2 to set
	 */
	public void setMht2(Double mht2) {
		this.mht2 = mht2;
	}
	/**
	 * @return the mon2
	 */
	public Double getMon2() {
		return mon2;
	}
	/**
	 * @param mon2 the mon2 to set
	 */
	public void setMon2(Double mon2) {
		this.mon2 = mon2;
	}
	/**
	 * @return the dva2
	 */
	public Date getDva2() {
		return dva2;
	}
	/**
	 * @param dva2 the dva2 to set
	 */
	public void setDva2(Date dva2) {
		this.dva2 = dva2;
	}
	/**
	 * @return the din
	 */
	public Date getDin() {
		return din;
	}
	/**
	 * @param din the din to set
	 */
	public void setDin(Date din) {
		this.din = din;
	}
	/**
	 * @return the exo2
	 */
	public String getExo2() {
		return exo2;
	}
	/**
	 * @param exo2 the exo2 to set
	 */
	public void setExo2(String exo2) {
		this.exo2 = exo2;
	}
	/**
	 * @return the sol2
	 */
	public Double getSol2() {
		return sol2;
	}
	/**
	 * @param sol2 the sol2 to set
	 */
	public void setSol2(Double sol2) {
		this.sol2 = sol2;
	}
	/**
	 * @return the indh2
	 */
	public Double getIndh2() {
		return indh2;
	}
	/**
	 * @param indh2 the indh2 to set
	 */
	public void setIndh2(Double indh2) {
		this.indh2 = indh2;
	}
	/**
	 * @return the inds2
	 */
	public Double getInds2() {
		return inds2;
	}
	/**
	 * @param inds2 the inds2 to set
	 */
	public void setInds2(Double inds2) {
		this.inds2 = inds2;
	}
	/**
	 * @return the desc1
	 */
	public String getDesc1() {
		return desc1;
	}
	/**
	 * @param desc1 the desc1 to set
	 */
	public void setDesc1(String desc1) {
		this.desc1 = desc1;
	}
	/**
	 * @return the desc2
	 */
	public String getDesc2() {
		return desc2;
	}
	/**
	 * @param desc2 the desc2 to set
	 */
	public void setDesc2(String desc2) {
		this.desc2 = desc2;
	}
	/**
	 * @return the desc3
	 */
	public String getDesc3() {
		return desc3;
	}
	/**
	 * @param desc3 the desc3 to set
	 */
	public void setDesc3(String desc3) {
		this.desc3 = desc3;
	}
	/**
	 * @return the desc4
	 */
	public String getDesc4() {
		return desc4;
	}
	/**
	 * @param desc4 the desc4 to set
	 */
	public void setDesc4(String desc4) {
		this.desc4 = desc4;
	}
	/**
	 * @return the desc5
	 */
	public String getDesc5() {
		return desc5;
	}
	/**
	 * @param desc5 the desc5 to set
	 */
	public void setDesc5(String desc5) {
		this.desc5 = desc5;
	}
	/**
	 * @return the etab
	 */
	public String getEtab() {
		return etab;
	}
	/**
	 * @param etab the etab to set
	 */
	public void setEtab(String etab) {
		this.etab = etab;
	}
	/**
	 * @return the guib
	 */
	public String getGuib() {
		return guib;
	}
	/**
	 * @param guib the guib to set
	 */
	public void setGuib(String guib) {
		this.guib = guib;
	}
	/**
	 * @return the nome
	 */
	public String getNome() {
		return nome;
	}
	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}
	/**
	 * @return the domi
	 */
	public String getDomi() {
		return domi;
	}
	/**
	 * @param domi the domi to set
	 */
	public void setDomi(String domi) {
		this.domi = domi;
	}
	/**
	 * @return the adb1
	 */
	public String getAdb1() {
		return adb1;
	}
	/**
	 * @param adb1 the adb1 to set
	 */
	public void setAdb1(String adb1) {
		this.adb1 = adb1;
	}
	/**
	 * @return the adb2
	 */
	public String getAdb2() {
		return adb2;
	}
	/**
	 * @param adb2 the adb2 to set
	 */
	public void setAdb2(String adb2) {
		this.adb2 = adb2;
	}
	/**
	 * @return the adb3
	 */
	public String getAdb3() {
		return adb3;
	}
	/**
	 * @param adb3 the adb3 to set
	 */
	public void setAdb3(String adb3) {
		this.adb3 = adb3;
	}
	/**
	 * @return the vilb
	 */
	public String getVilb() {
		return vilb;
	}
	/**
	 * @param vilb the vilb to set
	 */
	public void setVilb(String vilb) {
		this.vilb = vilb;
	}
	/**
	 * @return the cpob
	 */
	public String getCpob() {
		return cpob;
	}
	/**
	 * @param cpob the cpob to set
	 */
	public void setCpob(String cpob) {
		this.cpob = cpob;
	}
	/**
	 * @return the cpay
	 */
	public String getCpay() {
		return cpay;
	}
	/**
	 * @param cpay the cpay to set
	 */
	public void setCpay(String cpay) {
		this.cpay = cpay;
	}
	/**
	 * @return the etabr
	 */
	public String getEtabr() {
		return etabr;
	}
	/**
	 * @param etabr the etabr to set
	 */
	public void setEtabr(String etabr) {
		this.etabr = etabr;
	}
	/**
	 * @return the guibr
	 */
	public String getGuibr() {
		return guibr;
	}
	/**
	 * @param guibr the guibr to set
	 */
	public void setGuibr(String guibr) {
		this.guibr = guibr;
	}
	/**
	 * @return the comb
	 */
	public String getComb() {
		return comb;
	}
	/**
	 * @param comb the comb to set
	 */
	public void setComb(String comb) {
		this.comb = comb;
	}
	/**
	 * @return the cleb
	 */
	public String getCleb() {
		return cleb;
	}
	/**
	 * @param cleb the cleb to set
	 */
	public void setCleb(String cleb) {
		this.cleb = cleb;
	}
	/**
	 * @return the nomb
	 */
	public String getNomb() {
		return nomb;
	}
	/**
	 * @param nomb the nomb to set
	 */
	public void setNomb(String nomb) {
		this.nomb = nomb;
	}
	/**
	 * @return the mban
	 */
	public Double getMban() {
		return mban;
	}
	/**
	 * @param mban the mban to set
	 */
	public void setMban(Double mban) {
		this.mban = mban;
	}
	/**
	 * @return the dvab
	 */
	public Date getDvab() {
		return dvab;
	}
	/**
	 * @param dvab the dvab to set
	 */
	public void setDvab(Date dvab) {
		this.dvab = dvab;
	}
	/**
	 * @return the cai1
	 */
	public String getCai1() {
		return cai1;
	}
	/**
	 * @param cai1 the cai1 to set
	 */
	public void setCai1(String cai1) {
		this.cai1 = cai1;
	}
	/**
	 * @return the tyc1
	 */
	public String getTyc1() {
		return tyc1;
	}
	/**
	 * @param tyc1 the tyc1 to set
	 */
	public void setTyc1(String tyc1) {
		this.tyc1 = tyc1;
	}
	/**
	 * @return the dcai1
	 */
	public String getDcai1() {
		return dcai1;
	}
	/**
	 * @param dcai1 the dcai1 to set
	 */
	public void setDcai1(String dcai1) {
		this.dcai1 = dcai1;
	}
	/**
	 * @return the scai1
	 */
	public String getScai1() {
		return scai1;
	}
	/**
	 * @param scai1 the scai1 to set
	 */
	public void setScai1(String scai1) {
		this.scai1 = scai1;
	}
	/**
	 * @return the mcai1
	 */
	public Double getMcai1() {
		return mcai1;
	}
	/**
	 * @param mcai1 the mcai1 to set
	 */
	public void setMcai1(Double mcai1) {
		this.mcai1 = mcai1;
	}
	/**
	 * @return the arrc1
	 */
	public Double getArrc1() {
		return arrc1;
	}
	/**
	 * @param arrc1 the arrc1 to set
	 */
	public void setArrc1(Double arrc1) {
		this.arrc1 = arrc1;
	}
	/**
	 * @return the cai2
	 */
	public String getCai2() {
		return cai2;
	}
	/**
	 * @param cai2 the cai2 to set
	 */
	public void setCai2(String cai2) {
		this.cai2 = cai2;
	}
	/**
	 * @return the tyc2
	 */
	public String getTyc2() {
		return tyc2;
	}
	/**
	 * @param tyc2 the tyc2 to set
	 */
	public void setTyc2(String tyc2) {
		this.tyc2 = tyc2;
	}
	/**
	 * @return the dcai2
	 */
	public String getDcai2() {
		return dcai2;
	}
	/**
	 * @param dcai2 the dcai2 to set
	 */
	public void setDcai2(String dcai2) {
		this.dcai2 = dcai2;
	}
	/**
	 * @return the scai2
	 */
	public String getScai2() {
		return scai2;
	}
	/**
	 * @param scai2 the scai2 to set
	 */
	public void setScai2(String scai2) {
		this.scai2 = scai2;
	}
	/**
	 * @return the mcai2
	 */
	public Double getMcai2() {
		return mcai2;
	}
	/**
	 * @param mcai2 the mcai2 to set
	 */
	public void setMcai2(Double mcai2) {
		this.mcai2 = mcai2;
	}
	/**
	 * @return the arrc2
	 */
	public Double getArrc2() {
		return arrc2;
	}
	/**
	 * @param arrc2 the arrc2 to set
	 */
	public void setArrc2(Double arrc2) {
		this.arrc2 = arrc2;
	}
	/**
	 * @return the dev
	 */
	public String getDev() {
		return dev;
	}
	/**
	 * @param dev the dev to set
	 */
	public void setDev(String dev) {
		this.dev = dev;
	}
	/**
	 * @return the mht
	 */
	public Double getMht() {
		return mht;
	}
	/**
	 * @param mht the mht to set
	 */
	public void setMht(Double mht) {
		this.mht = mht;
	}
	/**
	 * @return the mnat
	 */
	public Double getMnat() {
		return mnat;
	}
	/**
	 * @param mnat the mnat to set
	 */
	public void setMnat(Double mnat) {
		this.mnat = mnat;
	}
	/**
	 * @return the mbor
	 */
	public Double getMbor() {
		return mbor;
	}
	/**
	 * @param mbor the mbor to set
	 */
	public void setMbor(Double mbor) {
		this.mbor = mbor;
	}
	/**
	 * @return the nbor
	 */
	public String getNbor() {
		return nbor;
	}
	/**
	 * @param nbor the nbor to set
	 */
	public void setNbor(String nbor) {
		this.nbor = nbor;
	}
	/**
	 * @return the nblig
	 */
	public Integer getNblig() {
		return nblig;
	}
	/**
	 * @param nblig the nblig to set
	 */
	public void setNblig(Integer nblig) {
		this.nblig = nblig;
	}
	/**
	 * @return the tcai2
	 */
	public Double getTcai2() {
		return tcai2;
	}
	/**
	 * @param tcai2 the tcai2 to set
	 */
	public void setTcai2(Double tcai2) {
		this.tcai2 = tcai2;
	}
	/**
	 * @return the tcai3
	 */
	public Double getTcai3() {
		return tcai3;
	}
	/**
	 * @param tcai3 the tcai3 to set
	 */
	public void setTcai3(Double tcai3) {
		this.tcai3 = tcai3;
	}
	/**
	 * @return the nat
	 */
	public String getNat() {
		return nat;
	}
	/**
	 * @param nat the nat to set
	 */
	public void setNat(String nat) {
		this.nat = nat;
	}
	/**
	 * @return the nato
	 */
	public String getNato() {
		return nato;
	}
	/**
	 * @param nato the nato to set
	 */
	public void setNato(String nato) {
		this.nato = nato;
	}
	/**
	 * @return the opeo
	 */
	public String getOpeo() {
		return opeo;
	}
	/**
	 * @param opeo the opeo to set
	 */
	public void setOpeo(String opeo) {
		this.opeo = opeo;
	}
	/**
	 * @return the eveo
	 */
	public String getEveo() {
		return eveo;
	}
	/**
	 * @param eveo the eveo to set
	 */
	public void setEveo(String eveo) {
		this.eveo = eveo;
	}
	/**
	 * @return the pieo
	 */
	public String getPieo() {
		return pieo;
	}
	/**
	 * @param pieo the pieo to set
	 */
	public void setPieo(String pieo) {
		this.pieo = pieo;
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
	 * @return the dco
	 */
	public Date getDco() {
		return dco;
	}
	/**
	 * @param dco the dco to set
	 */
	public void setDco(Date dco) {
		this.dco = dco;
	}
	/**
	 * @return the eta
	 */
	public String getEta() {
		return eta;
	}
	/**
	 * @param eta the eta to set
	 */
	public void setEta(String eta) {
		this.eta = eta;
	}
	/**
	 * @return the etap
	 */
	public String getEtap() {
		return etap;
	}
	/**
	 * @param etap the etap to set
	 */
	public void setEtap(String etap) {
		this.etap = etap;
	}
	/**
	 * @return the nbv
	 */
	public Integer getNbv() {
		return nbv;
	}
	/**
	 * @param nbv the nbv to set
	 */
	public void setNbv(Integer nbv) {
		this.nbv = nbv;
	}
	/**
	 * @return the nval
	 */
	public Integer getNval() {
		return nval;
	}
	/**
	 * @param nval the nval to set
	 */
	public void setNval(Integer nval) {
		this.nval = nval;
	}
	/**
	 * @return the uti
	 */
	public String getUti() {
		return uti;
	}
	/**
	 * @param uti the uti to set
	 */
	public void setUti(String uti) {
		this.uti = uti;
	}
	/**
	 * @return the utf
	 */
	public String getUtf() {
		return utf;
	}
	/**
	 * @param utf the utf to set
	 */
	public void setUtf(String utf) {
		this.utf = utf;
	}
	/**
	 * @return the uta
	 */
	public String getUta() {
		return uta;
	}
	/**
	 * @param uta the uta to set
	 */
	public void setUta(String uta) {
		this.uta = uta;
	}
	/**
	 * @return the moa
	 */
	public Double getMoa() {
		return moa;
	}
	/**
	 * @param moa the moa to set
	 */
	public void setMoa(Double moa) {
		this.moa = moa;
	}
	/**
	 * @return the mof
	 */
	public Double getMof() {
		return mof;
	}
	/**
	 * @param mof the mof to set
	 */
	public void setMof(Double mof) {
		this.mof = mof;
	}
	/**
	 * @return the lib1
	 */
	public String getLib1() {
		return lib1;
	}
	/**
	 * @param lib1 the lib1 to set
	 */
	public void setLib1(String lib1) {
		this.lib1 = lib1;
	}
	/**
	 * @return the lib2
	 */
	public String getLib2() {
		return lib2;
	}
	/**
	 * @param lib2 the lib2 to set
	 */
	public void setLib2(String lib2) {
		this.lib2 = lib2;
	}
	/**
	 * @return the lib3
	 */
	public String getLib3() {
		return lib3;
	}
	/**
	 * @param lib3 the lib3 to set
	 */
	public void setLib3(String lib3) {
		this.lib3 = lib3;
	}
	/**
	 * @return the lib4
	 */
	public String getLib4() {
		return lib4;
	}
	/**
	 * @param lib4 the lib4 to set
	 */
	public void setLib4(String lib4) {
		this.lib4 = lib4;
	}
	/**
	 * @return the lib5
	 */
	public String getLib5() {
		return lib5;
	}
	/**
	 * @param lib5 the lib5 to set
	 */
	public void setLib5(String lib5) {
		this.lib5 = lib5;
	}
	/**
	 * @return the lib6
	 */
	public String getLib6() {
		return lib6;
	}
	/**
	 * @param lib6 the lib6 to set
	 */
	public void setLib6(String lib6) {
		this.lib6 = lib6;
	}
	/**
	 * @return the lib7
	 */
	public String getLib7() {
		return lib7;
	}
	/**
	 * @param lib7 the lib7 to set
	 */
	public void setLib7(String lib7) {
		this.lib7 = lib7;
	}
	/**
	 * @return the lib8
	 */
	public String getLib8() {
		return lib8;
	}
	/**
	 * @param lib8 the lib8 to set
	 */
	public void setLib8(String lib8) {
		this.lib8 = lib8;
	}
	/**
	 * @return the lib9
	 */
	public String getLib9() {
		return lib9;
	}
	/**
	 * @param lib9 the lib9 to set
	 */
	public void setLib9(String lib9) {
		this.lib9 = lib9;
	}
	/**
	 * @return the lib10
	 */
	public String getLib10() {
		return lib10;
	}
	/**
	 * @param lib10 the lib10 to set
	 */
	public void setLib10(String lib10) {
		this.lib10 = lib10;
	}
	/**
	 * @return the agec
	 */
	public String getAgec() {
		return agec;
	}
	/**
	 * @param agec the agec to set
	 */
	public void setAgec(String agec) {
		this.agec = agec;
	}
	/**
	 * @return the agep
	 */
	public String getAgep() {
		return agep;
	}
	/**
	 * @param agep the agep to set
	 */
	public void setAgep(String agep) {
		this.agep = agep;
	}
	/**
	 * @return the intr
	 */
	public String getIntr() {
		return intr;
	}
	/**
	 * @param intr the intr to set
	 */
	public void setIntr(String intr) {
		this.intr = intr;
	}
	/**
	 * @return the orig
	 */
	public String getOrig() {
		return orig;
	}
	/**
	 * @param orig the orig to set
	 */
	public void setOrig(String orig) {
		this.orig = orig;
	}
	/**
	 * @return the rlet
	 */
	public String getRlet() {
		return rlet;
	}
	/**
	 * @param rlet the rlet to set
	 */
	public void setRlet(String rlet) {
		this.rlet = rlet;
	}
	/**
	 * @return the catr
	 */
	public String getCatr() {
		return catr;
	}
	/**
	 * @param catr the catr to set
	 */
	public void setCatr(String catr) {
		this.catr = catr;
	}
	/**
	 * @return the ceb
	 */
	public String getCeb() {
		return ceb;
	}
	/**
	 * @param ceb the ceb to set
	 */
	public void setCeb(String ceb) {
		this.ceb = ceb;
	}
	/**
	 * @return the plb
	 */
	public String getPlb() {
		return plb;
	}
	/**
	 * @param plb the plb to set
	 */
	public void setPlb(String plb) {
		this.plb = plb;
	}
	/**
	 * @return the cco
	 */
	public String getCco() {
		return cco;
	}
	/**
	 * @param cco the cco to set
	 */
	public void setCco(String cco) {
		this.cco = cco;
	}
	/**
	 * @return the dret
	 */
	public Date getDret() {
		return dret;
	}
	/**
	 * @param dret the dret to set
	 */
	public void setDret(Date dret) {
		this.dret = dret;
	}
	/**
	 * @return the natp
	 */
	public String getNatp() {
		return natp;
	}
	/**
	 * @param natp the natp to set
	 */
	public void setNatp(String natp) {
		this.natp = natp;
	}
	/**
	 * @return the nump
	 */
	public String getNump() {
		return nump;
	}
	/**
	 * @param nump the nump to set
	 */
	public void setNump(String nump) {
		this.nump = nump;
	}
	/**
	 * @return the datp
	 */
	public String getDatp() {
		return datp;
	}
	/**
	 * @param datp the datp to set
	 */
	public void setDatp(String datp) {
		this.datp = datp;
	}
	/**
	 * @return the nomp
	 */
	public String getNomp() {
		return nomp;
	}
	/**
	 * @param nomp the nomp to set
	 */
	public void setNomp(String nomp) {
		this.nomp = nomp;
	}
	/**
	 * @return the ad1p
	 */
	public String getAd1p() {
		return ad1p;
	}
	/**
	 * @param ad1p the ad1p to set
	 */
	public void setAd1p(String ad1p) {
		this.ad1p = ad1p;
	}
	/**
	 * @return the ad2p
	 */
	public String getAd2p() {
		return ad2p;
	}
	/**
	 * @param ad2p the ad2p to set
	 */
	public void setAd2p(String ad2p) {
		this.ad2p = ad2p;
	}
	/**
	 * @return the delp
	 */
	public String getDelp() {
		return delp;
	}
	/**
	 * @param delp the delp to set
	 */
	public void setDelp(String delp) {
		this.delp = delp;
	}
	/**
	 * @return the serie
	 */
	public String getSerie() {
		return serie;
	}
	/**
	 * @param serie the serie to set
	 */
	public void setSerie(String serie) {
		this.serie = serie;
	}
	/**
	 * @return the nche
	 */
	public String getNche() {
		return nche;
	}
	/**
	 * @param nche the nche to set
	 */
	public void setNche(String nche) {
		this.nche = nche;
	}
	/**
	 * @return the chql
	 */
	public String getChql() {
		return chql;
	}
	/**
	 * @param chql the chql to set
	 */
	public void setChql(String chql) {
		this.chql = chql;
	}
	/**
	 * @return the chqc
	 */
	public String getChqc() {
		return chqc;
	}
	/**
	 * @param chqc the chqc to set
	 */
	public void setChqc(String chqc) {
		this.chqc = chqc;
	}
	/**
	 * @return the cab
	 */
	public String getCab() {
		return cab;
	}
	/**
	 * @param cab the cab to set
	 */
	public void setCab(String cab) {
		this.cab = cab;
	}
	/**
	 * @return the ncff
	 */
	public String getNcff() {
		return ncff;
	}
	/**
	 * @param ncff the ncff to set
	 */
	public void setNcff(String ncff) {
		this.ncff = ncff;
	}
	/**
	 * @return the csa
	 */
	public String getCsa() {
		return csa;
	}
	/**
	 * @param csa the csa to set
	 */
	public void setCsa(String csa) {
		this.csa = csa;
	}
	/**
	 * @return the dech
	 */
	public Date getDech() {
		return dech;
	}
	/**
	 * @param dech the dech to set
	 */
	public void setDech(Date dech) {
		this.dech = dech;
	}
	/**
	 * @return the tire
	 */
	public String getTire() {
		return tire;
	}
	/**
	 * @param tire the tire to set
	 */
	public void setTire(String tire) {
		this.tire = tire;
	}
	/**
	 * @return the agti
	 */
	public String getAgti() {
		return agti;
	}
	/**
	 * @param agti the agti to set
	 */
	public void setAgti(String agti) {
		this.agti = agti;
	}
	/**
	 * @return the agre
	 */
	public String getAgre() {
		return agre;
	}
	/**
	 * @param agre the agre to set
	 */
	public void setAgre(String agre) {
		this.agre = agre;
	}
	/**
	 * @return the nbji
	 */
	public Integer getNbji() {
		return nbji;
	}
	/**
	 * @param nbji the nbji to set
	 */
	public void setNbji(Integer nbji) {
		this.nbji = nbji;
	}
	/**
	 * @return the ptfc
	 */
	public String getPtfc() {
		return ptfc;
	}
	/**
	 * @param ptfc the ptfc to set
	 */
	public void setPtfc(String ptfc) {
		this.ptfc = ptfc;
	}
	/**
	 * @return the efav
	 */
	public String getEfav() {
		return efav;
	}
	/**
	 * @param efav the efav to set
	 */
	public void setEfav(String efav) {
		this.efav = efav;
	}
	/**
	 * @return the navl
	 */
	public String getNavl() {
		return navl;
	}
	/**
	 * @param navl the navl to set
	 */
	public void setNavl(String navl) {
		this.navl = navl;
	}
	/**
	 * @return the edom
	 */
	public String getEdom() {
		return edom;
	}
	/**
	 * @param edom the edom to set
	 */
	public void setEdom(String edom) {
		this.edom = edom;
	}
	/**
	 * @return the eopp
	 */
	public String getEopp() {
		return eopp;
	}
	/**
	 * @param eopp the eopp to set
	 */
	public void setEopp(String eopp) {
		this.eopp = eopp;
	}
	/**
	 * @return the efac
	 */
	public String getEfac() {
		return efac;
	}
	/**
	 * @param efac the efac to set
	 */
	public void setEfac(String efac) {
		this.efac = efac;
	}
	/**
	 * @return the moti
	 */
	public String getMoti() {
		return moti;
	}
	/**
	 * @param moti the moti to set
	 */
	public void setMoti(String moti) {
		this.moti = moti;
	}
	/**
	 * @return the envacc
	 */
	public String getEnvacc() {
		return envacc;
	}
	/**
	 * @param envacc the envacc to set
	 */
	public void setEnvacc(String envacc) {
		this.envacc = envacc;
	}
	/**
	 * @return the enom
	 */
	public String getEnom() {
		return enom;
	}
	/**
	 * @param enom the enom to set
	 */
	public void setEnom(String enom) {
		this.enom = enom;
	}
	/**
	 * @return the vicl
	 */
	public String getVicl() {
		return vicl;
	}
	/**
	 * @param vicl the vicl to set
	 */
	public void setVicl(String vicl) {
		this.vicl = vicl;
	}
	/**
	 * @return the teco
	 */
	public String getTeco() {
		return teco;
	}
	/**
	 * @param teco the teco to set
	 */
	public void setTeco(String teco) {
		this.teco = teco;
	}
	/**
	 * @return the tenv
	 */
	public String getTenv() {
		return tenv;
	}
	/**
	 * @param tenv the tenv to set
	 */
	public void setTenv(String tenv) {
		this.tenv = tenv;
	}
	/**
	 * @return the exjo
	 */
	public String getExjo() {
		return exjo;
	}
	/**
	 * @param exjo the exjo to set
	 */
	public void setExjo(String exjo) {
		this.exjo = exjo;
	}
	/**
	 * @return the org
	 */
	public String getOrg() {
		return org;
	}
	/**
	 * @param org the org to set
	 */
	public void setOrg(String org) {
		this.org = org;
	}
	/**
	 * @return the cai3
	 */
	public String getCai3() {
		return cai3;
	}
	/**
	 * @param cai3 the cai3 to set
	 */
	public void setCai3(String cai3) {
		this.cai3 = cai3;
	}
	/**
	 * @return the mcai3
	 */
	public Double getMcai3() {
		return mcai3;
	}
	/**
	 * @param mcai3 the mcai3 to set
	 */
	public void setMcai3(Double mcai3) {
		this.mcai3 = mcai3;
	}
	/**
	 * @return the forc
	 */
	public String getForc() {
		return forc;
	}
	/**
	 * @param forc the forc to set
	 */
	public void setForc(String forc) {
		this.forc = forc;
	}
	/**
	 * @return the ocai3
	 */
	public String getOcai3() {
		return ocai3;
	}
	/**
	 * @param ocai3 the ocai3 to set
	 */
	public void setOcai3(String ocai3) {
		this.ocai3 = ocai3;
	}
	/**
	 * @return the ncai3
	 */
	public Integer getNcai3() {
		return ncai3;
	}
	/**
	 * @param ncai3 the ncai3 to set
	 */
	public void setNcai3(Integer ncai3) {
		this.ncai3 = ncai3;
	}
	/**
	 * @return the csp1
	 */
	public String getCsp1() {
		return csp1;
	}
	/**
	 * @param csp1 the csp1 to set
	 */
	public void setCsp1(String csp1) {
		this.csp1 = csp1;
	}
	/**
	 * @return the csp2
	 */
	public String getCsp2() {
		return csp2;
	}
	/**
	 * @param csp2 the csp2 to set
	 */
	public void setCsp2(String csp2) {
		this.csp2 = csp2;
	}
	/**
	 * @return the csp3
	 */
	public String getCsp3() {
		return csp3;
	}
	/**
	 * @param csp3 the csp3 to set
	 */
	public void setCsp3(String csp3) {
		this.csp3 = csp3;
	}
	/**
	 * @return the csp4
	 */
	public String getCsp4() {
		return csp4;
	}
	/**
	 * @param csp4 the csp4 to set
	 */
	public void setCsp4(String csp4) {
		this.csp4 = csp4;
	}
	/**
	 * @return the csp5
	 */
	public String getCsp5() {
		return csp5;
	}
	/**
	 * @param csp5 the csp5 to set
	 */
	public void setCsp5(String csp5) {
		this.csp5 = csp5;
	}
	/**
	 * @return the ndom
	 */
	public String getNdom() {
		return ndom;
	}
	/**
	 * @param ndom the ndom to set
	 */
	public void setNdom(String ndom) {
		this.ndom = ndom;
	}
	/**
	 * @return the cmod
	 */
	public String getCmod() {
		return cmod;
	}
	/**
	 * @param cmod the cmod to set
	 */
	public void setCmod(String cmod) {
		this.cmod = cmod;
	}
	/**
	 * @return the devf
	 */
	public String getDevf() {
		return devf;
	}
	/**
	 * @param devf the devf to set
	 */
	public void setDevf(String devf) {
		this.devf = devf;
	}
	/**
	 * @return the ncpf
	 */
	public String getNcpf() {
		return ncpf;
	}
	/**
	 * @param ncpf the ncpf to set
	 */
	public void setNcpf(String ncpf) {
		this.ncpf = ncpf;
	}
	/**
	 * @return the suff
	 */
	public String getSuff() {
		return suff;
	}
	/**
	 * @param suff the suff to set
	 */
	public void setSuff(String suff) {
		this.suff = suff;
	}
	/**
	 * @return the monf
	 */
	public Double getMonf() {
		return monf;
	}
	/**
	 * @param monf the monf to set
	 */
	public void setMonf(Double monf) {
		this.monf = monf;
	}
	/**
	 * @return the dvaf
	 */
	public Date getDvaf() {
		return dvaf;
	}
	/**
	 * @param dvaf the dvaf to set
	 */
	public void setDvaf(Date dvaf) {
		this.dvaf = dvaf;
	}
	/**
	 * @return the exof
	 */
	public String getExof() {
		return exof;
	}
	/**
	 * @param exof the exof to set
	 */
	public void setExof(String exof) {
		this.exof = exof;
	}
	/**
	 * @return the agee
	 */
	public String getAgee() {
		return agee;
	}
	/**
	 * @param agee the agee to set
	 */
	public void setAgee(String agee) {
		this.agee = agee;
	}
	/**
	 * @return the deve
	 */
	public String getDeve() {
		return deve;
	}
	/**
	 * @param deve the deve to set
	 */
	public void setDeve(String deve) {
		this.deve = deve;
	}
	/**
	 * @return the ncpe
	 */
	public String getNcpe() {
		return ncpe;
	}
	/**
	 * @param ncpe the ncpe to set
	 */
	public void setNcpe(String ncpe) {
		this.ncpe = ncpe;
	}
	/**
	 * @return the sufe
	 */
	public String getSufe() {
		return sufe;
	}
	/**
	 * @param sufe the sufe to set
	 */
	public void setSufe(String sufe) {
		this.sufe = sufe;
	}
	/**
	 * @return the clce
	 */
	public String getClce() {
		return clce;
	}
	/**
	 * @param clce the clce to set
	 */
	public void setClce(String clce) {
		this.clce = clce;
	}
	/**
	 * @return the ncpi
	 */
	public String getNcpi() {
		return ncpi;
	}
	/**
	 * @param ncpi the ncpi to set
	 */
	public void setNcpi(String ncpi) {
		this.ncpi = ncpi;
	}
	/**
	 * @return the sufi
	 */
	public String getSufi() {
		return sufi;
	}
	/**
	 * @param sufi the sufi to set
	 */
	public void setSufi(String sufi) {
		this.sufi = sufi;
	}
	/**
	 * @return the mimp
	 */
	public Double getMimp() {
		return mimp;
	}
	/**
	 * @param mimp the mimp to set
	 */
	public void setMimp(Double mimp) {
		this.mimp = mimp;
	}
	/**
	 * @return the dvai
	 */
	public Date getDvai() {
		return dvai;
	}
	/**
	 * @param dvai the dvai to set
	 */
	public void setDvai(Date dvai) {
		this.dvai = dvai;
	}
	/**
	 * @return the ncpp
	 */
	public String getNcpp() {
		return ncpp;
	}
	/**
	 * @param ncpp the ncpp to set
	 */
	public void setNcpp(String ncpp) {
		this.ncpp = ncpp;
	}
	/**
	 * @return the sufp
	 */
	public String getSufp() {
		return sufp;
	}
	/**
	 * @param sufp the sufp to set
	 */
	public void setSufp(String sufp) {
		this.sufp = sufp;
	}
	/**
	 * @return the prga
	 */
	public Double getPrga() {
		return prga;
	}
	/**
	 * @param prga the prga to set
	 */
	public void setPrga(Double prga) {
		this.prga = prga;
	}
	/**
	 * @return the mrga
	 */
	public Double getMrga() {
		return mrga;
	}
	/**
	 * @param mrga the mrga to set
	 */
	public void setMrga(Double mrga) {
		this.mrga = mrga;
	}
	/**
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	/**
	 * @param term the term to set
	 */
	public void setTerm(String term) {
		this.term = term;
	}
	/**
	 * @return the tvar
	 */
	public String getTvar() {
		return tvar;
	}
	/**
	 * @param tvar the tvar to set
	 */
	public void setTvar(String tvar) {
		this.tvar = tvar;
	}
	/**
	 * @return the intp
	 */
	public String getIntp() {
		return intp;
	}
	/**
	 * @param intp the intp to set
	 */
	public void setIntp(String intp) {
		this.intp = intp;
	}
	/**
	 * @return the cap
	 */
	public String getCap() {
		return cap;
	}
	/**
	 * @param cap the cap to set
	 */
	public void setCap(String cap) {
		this.cap = cap;
	}
	/**
	 * @return the prll
	 */
	public String getPrll() {
		return prll;
	}
	/**
	 * @param prll the prll to set
	 */
	public void setPrll(String prll) {
		this.prll = prll;
	}
	/**
	 * @return the ano
	 */
	public String getAno() {
		return ano;
	}
	/**
	 * @param ano the ano to set
	 */
	public void setAno(String ano) {
		this.ano = ano;
	}
	/**
	 * @return the etab1
	 */
	public String getEtab1() {
		return etab1;
	}
	/**
	 * @param etab1 the etab1 to set
	 */
	public void setEtab1(String etab1) {
		this.etab1 = etab1;
	}
	/**
	 * @return the guib1
	 */
	public String getGuib1() {
		return guib1;
	}
	/**
	 * @param guib1 the guib1 to set
	 */
	public void setGuib1(String guib1) {
		this.guib1 = guib1;
	}
	/**
	 * @return the com1b
	 */
	public String getCom1b() {
		return com1b;
	}
	/**
	 * @param com1b the com1b to set
	 */
	public void setCom1b(String com1b) {
		this.com1b = com1b;
	}
	/**
	 * @return the etab2
	 */
	public String getEtab2() {
		return etab2;
	}
	/**
	 * @param etab2 the etab2 to set
	 */
	public void setEtab2(String etab2) {
		this.etab2 = etab2;
	}
	/**
	 * @return the guib2
	 */
	public String getGuib2() {
		return guib2;
	}
	/**
	 * @param guib2 the guib2 to set
	 */
	public void setGuib2(String guib2) {
		this.guib2 = guib2;
	}
	/**
	 * @return the com2b
	 */
	public String getCom2b() {
		return com2b;
	}
	/**
	 * @param com2b the com2b to set
	 */
	public void setCom2b(String com2b) {
		this.com2b = com2b;
	}
	/**
	 * @return the tcom1
	 */
	public Double getTcom1() {
		return tcom1;
	}
	/**
	 * @param tcom1 the tcom1 to set
	 */
	public void setTcom1(Double tcom1) {
		this.tcom1 = tcom1;
	}
	/**
	 * @return the mcom1
	 */
	public Double getMcom1() {
		return mcom1;
	}
	/**
	 * @param mcom1 the mcom1 to set
	 */
	public void setMcom1(Double mcom1) {
		this.mcom1 = mcom1;
	}
	/**
	 * @return the tcom2
	 */
	public Double getTcom2() {
		return tcom2;
	}
	/**
	 * @param tcom2 the tcom2 to set
	 */
	public void setTcom2(Double tcom2) {
		this.tcom2 = tcom2;
	}
	/**
	 * @return the mcom2
	 */
	public Double getMcom2() {
		return mcom2;
	}
	/**
	 * @param mcom2 the mcom2 to set
	 */
	public void setMcom2(Double mcom2) {
		this.mcom2 = mcom2;
	}
	/**
	 * @return the tcom3
	 */
	public Double getTcom3() {
		return tcom3;
	}
	/**
	 * @param tcom3 the tcom3 to set
	 */
	public void setTcom3(Double tcom3) {
		this.tcom3 = tcom3;
	}
	/**
	 * @return the mcom3
	 */
	public Double getMcom3() {
		return mcom3;
	}
	/**
	 * @param mcom3 the mcom3 to set
	 */
	public void setMcom3(Double mcom3) {
		this.mcom3 = mcom3;
	}
	/**
	 * @return the frai1
	 */
	public Double getFrai1() {
		return frai1;
	}
	/**
	 * @param frai1 the frai1 to set
	 */
	public void setFrai1(Double frai1) {
		this.frai1 = frai1;
	}
	/**
	 * @return the frai2
	 */
	public Double getFrai2() {
		return frai2;
	}
	/**
	 * @param frai2 the frai2 to set
	 */
	public void setFrai2(Double frai2) {
		this.frai2 = frai2;
	}
	/**
	 * @return the frai3
	 */
	public Double getFrai3() {
		return frai3;
	}
	/**
	 * @param frai3 the frai3 to set
	 */
	public void setFrai3(Double frai3) {
		this.frai3 = frai3;
	}
	/**
	 * @return the ttax1
	 */
	public Double getTtax1() {
		return ttax1;
	}
	/**
	 * @param ttax1 the ttax1 to set
	 */
	public void setTtax1(Double ttax1) {
		this.ttax1 = ttax1;
	}
	/**
	 * @return the mtax1
	 */
	public Double getMtax1() {
		return mtax1;
	}
	/**
	 * @param mtax1 the mtax1 to set
	 */
	public void setMtax1(Double mtax1) {
		this.mtax1 = mtax1;
	}
	/**
	 * @return the ttax2
	 */
	public Double getTtax2() {
		return ttax2;
	}
	/**
	 * @param ttax2 the ttax2 to set
	 */
	public void setTtax2(Double ttax2) {
		this.ttax2 = ttax2;
	}
	/**
	 * @return the mtax2
	 */
	public Double getMtax2() {
		return mtax2;
	}
	/**
	 * @param mtax2 the mtax2 to set
	 */
	public void setMtax2(Double mtax2) {
		this.mtax2 = mtax2;
	}
	/**
	 * @return the ttax3
	 */
	public Double getTtax3() {
		return ttax3;
	}
	/**
	 * @param ttax3 the ttax3 to set
	 */
	public void setTtax3(Double ttax3) {
		this.ttax3 = ttax3;
	}
	/**
	 * @return the mtax3
	 */
	public Double getMtax3() {
		return mtax3;
	}
	/**
	 * @param mtax3 the mtax3 to set
	 */
	public void setMtax3(Double mtax3) {
		this.mtax3 = mtax3;
	}
	/**
	 * @return the mnt1
	 */
	public Double getMnt1() {
		return mnt1;
	}
	/**
	 * @param mnt1 the mnt1 to set
	 */
	public void setMnt1(Double mnt1) {
		this.mnt1 = mnt1;
	}
	/**
	 * @return the mnt2
	 */
	public Double getMnt2() {
		return mnt2;
	}
	/**
	 * @param mnt2 the mnt2 to set
	 */
	public void setMnt2(Double mnt2) {
		this.mnt2 = mnt2;
	}
	/**
	 * @return the mnt3
	 */
	public Double getMnt3() {
		return mnt3;
	}
	/**
	 * @param mnt3 the mnt3 to set
	 */
	public void setMnt3(Double mnt3) {
		this.mnt3 = mnt3;
	}
	/**
	 * @return the mnt4
	 */
	public Double getMnt4() {
		return mnt4;
	}
	/**
	 * @param mnt4 the mnt4 to set
	 */
	public void setMnt4(Double mnt4) {
		this.mnt4 = mnt4;
	}
	/**
	 * @return the mnt5
	 */
	public Double getMnt5() {
		return mnt5;
	}
	/**
	 * @param mnt5 the mnt5 to set
	 */
	public void setMnt5(Double mnt5) {
		this.mnt5 = mnt5;
	}
	/**
	 * @return the tyc3
	 */
	public String getTyc3() {
		return tyc3;
	}
	/**
	 * @param tyc3 the tyc3 to set
	 */
	public void setTyc3(String tyc3) {
		this.tyc3 = tyc3;
	}
	/**
	 * @return the dcai3
	 */
	public String getDcai3() {
		return dcai3;
	}
	/**
	 * @param dcai3 the dcai3 to set
	 */
	public void setDcai3(String dcai3) {
		this.dcai3 = dcai3;
	}
	/**
	 * @return the scai3
	 */
	public String getScai3() {
		return scai3;
	}
	/**
	 * @param scai3 the scai3 to set
	 */
	public void setScai3(String scai3) {
		this.scai3 = scai3;
	}
	/**
	 * @return the arrc3
	 */
	public Double getArrc3() {
		return arrc3;
	}
	/**
	 * @param arrc3 the arrc3 to set
	 */
	public void setArrc3(Double arrc3) {
		this.arrc3 = arrc3;
	}
	/**
	 * @return the mhtd
	 */
	public Double getMhtd() {
		return mhtd;
	}
	/**
	 * @param mhtd the mhtd to set
	 */
	public void setMhtd(Double mhtd) {
		this.mhtd = mhtd;
	}
	/**
	 * @return the tcai4
	 */
	public Double getTcai4() {
		return tcai4;
	}
	/**
	 * @param tcai4 the tcai4 to set
	 */
	public void setTcai4(Double tcai4) {
		this.tcai4 = tcai4;
	}
	/**
	 * @return the tope
	 */
	public String getTope() {
		return tope;
	}
	/**
	 * @param tope the tope to set
	 */
	public void setTope(String tope) {
		this.tope = tope;
	}
	/**
	 * @return the img
	 */
	public String getImg() {
		return img;
	}
	/**
	 * @param img the img to set
	 */
	public void setImg(String img) {
		this.img = img;
	}
	/**
	 * @return the dsai
	 */
	public Date getDsai() {
		return dsai;
	}
	/**
	 * @param dsai the dsai to set
	 */
	public void setDsai(Date dsai) {
		this.dsai = dsai;
	}
	/**
	 * @return the hsai
	 */
	public String getHsai() {
		return hsai;
	}
	/**
	 * @param hsai the hsai to set
	 */
	public void setHsai(String hsai) {
		this.hsai = hsai;
	}
	/**
	 * @return the paysp
	 */
	public String getPaysp() {
		return paysp;
	}
	/**
	 * @param paysp the paysp to set
	 */
	public void setPaysp(String paysp) {
		this.paysp = paysp;
	}
	/**
	 * @return the pdelp
	 */
	public String getPdelp() {
		return pdelp;
	}
	/**
	 * @param pdelp the pdelp to set
	 */
	public void setPdelp(String pdelp) {
		this.pdelp = pdelp;
	}
	/**
	 * @return the manda
	 */
	public String getManda() {
		return manda;
	}
	/**
	 * @param manda the manda to set
	 */
	public void setManda(String manda) {
		this.manda = manda;
	}
	/**
	 * @return the refdos
	 */
	public String getRefdos() {
		return refdos;
	}
	/**
	 * @param refdos the refdos to set
	 */
	public void setRefdos(String refdos) {
		this.refdos = refdos;
	}
	/**
	 * @return the tchfr
	 */
	public Double getTchfr() {
		return tchfr;
	}
	/**
	 * @param tchfr the tchfr to set
	 */
	public void setTchfr(Double tchfr) {
		this.tchfr = tchfr;
	}



	/**
	 * @return the ecritures
	 */
	public List<bkmvti> getEcritures() {
		return ecritures;
	}



	/**
	 * @param ecritures the ecritures to set
	 */
	public void setEcritures(List<bkmvti> ecritures) {
		this.ecritures = ecritures;
	}



	/**
	 * @return the transaction
	 */
	public Transaction getTransaction() {
		return transaction;
	}



	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}



	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}



	/**
	 * @return the suspendInTFJ
	 */
	public Boolean getSuspendInTFJ() {
		return suspendInTFJ;
	}



	/**
	 * @param suspendInTFJ the suspendInTFJ to set
	 */
	public void setSuspendInTFJ(Boolean suspendInTFJ) {
		this.suspendInTFJ = suspendInTFJ;
	}



	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * @return the e_id
	 */
	public String getE_id() {
		return e_id;
	}



	/**
	 * @param e_id the e_id to set
	 */
	public void setE_id(String e_id) {
		this.e_id = e_id;
	}


	@JsonIgnore
	public boolean isEquilibre(){
		boolean result = true;
	
		if(this.ecritures != null && !this.ecritures.isEmpty()){
			Double deb = 0d, cred = 0d;
			for(bkmvti ec : this.ecritures){
				deb += ec.getSen().equalsIgnoreCase("D") ? ec.getMon() : 0d;
				cred += ec.getSen().equalsIgnoreCase("C") ? ec.getMon() : 0d;
			}
			result = Math.ceil(deb) == Math.ceil(cred);
		}
		
		return result;
	}
	
	
	/**
	 * @return the modenuit
	 */
	public boolean isModenuit() {
		return modenuit;
	}



	/**
	 * @param modenuit the modenuit to set
	 */
	public void setModenuit(boolean modenuit) {
		this.modenuit = modenuit;
	}



	/**
	 * @return the dateTraitementsuspendInTFJ
	 */
	public Date getDateTraitementsuspendInTFJ() {
		return dateTraitementsuspendInTFJ;
	}



	/**
	 * @param dateTraitementsuspendInTFJ the dateTraitementsuspendInTFJ to set
	 */
	public void setDateTraitementsuspendInTFJ(Date dateTraitementsuspendInTFJ) {
		this.dateTraitementsuspendInTFJ = dateTraitementsuspendInTFJ;
	}



	/**
	 * @return the version
	 */
	public Long getVersion() {
		return version;
	}
	
	@JsonIgnore
	public Date getDateFutur(int nbJrs){
		Calendar cal = new GregorianCalendar();
		cal.setTime(new Date());
		cal.add(Calendar.DATE, nbJrs);
		return cal.getTime();
	}
}
