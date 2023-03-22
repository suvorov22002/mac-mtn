/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import afb.dsi.dpd.portal.jpa.entities.User;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

import com.afb.dpd.mobilemoney.jpa.enums.Periodicite;
import com.afb.dpd.mobilemoney.jpa.enums.StatutContrat;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jpa.tools.MoMoHelper;
import com.yashiro.persistence.utils.dao.tools.encrypter.Encrypter;

/**
 * Classe representant un abonne MAC
 * @author Francis DJIOMOU
 * @version 1.0
 */
@Entity
@NamedQueries({
    @NamedQuery(name="Subscriber.findAll", query="SELECT c FROM Subscriber c"),
    @NamedQuery(name=Subscriber.UPDATE_SUBSCRIBER,query="update Subscriber s set s.dateSaveDernCompta=:dateSaveDernCompta, s.dateDernCompta=:dateDernCompta  where s.id=:id"),
}) 
@Table(name = "MoMo_SUBSCRIBER")
public class Subscriber implements Serializable {//, Comparable<Subscriber> {

	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static final String UPDATE_SUBSCRIBER = "Subscriber.mergepostingTransaction";

	/**
	 * Default Constructor
	 */
	public Subscriber() {}

	/**
	 * Id auto genere
	 */
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Code du client
	 */
	@Column(name = "CUST_ID", nullable = false)
	private String customerId;

	/**
	 * Code PIN du client
	 */
	@Column(name = "PIN", nullable = false)
	private String bankPIN;

	/**
	 * numeros de telephones du client
	 */
	// Change fetch = FetchType.EAGER
	@CollectionOfElements(fetch = FetchType.EAGER)
	@JoinTable(
			name = "MoMo_SUBS_PHONES",
			joinColumns = {@JoinColumn(name = "SUBS_ID")}
	)
	@Column(name = "PHONES", nullable = false)
	@Fetch(FetchMode.SUBSELECT)
	//@LazyCollection(LazyCollectionOption.FALSE)
	private List<String> phoneNumbers = new ArrayList<String>();

	/**
	 * Numeros de comptes
	 */
	// Change fetch = FetchType.EAGER
	@CollectionOfElements(fetch = FetchType.EAGER)
	@JoinTable(
			name = "MoMo_SUBS_ACCOUNTS",
			joinColumns = {@JoinColumn(name = "SUBS_ID")}
	)
	@Column(name = "ACCOUNTS")
	@Fetch(FetchMode.SUBSELECT)
	//@LazyCollection(LazyCollectionOption.FALSE)
	private List<String> accounts = new ArrayList<String>();
  
	/**
	 * Nom du client
	 */
	@Column(name = "NAME")
	private String customerName;

	/**
	 * Adresse
	 */
	@Column(name = "ADDRESS")
	private String customerAddress;
	
	/**
	 * Nom du client
	 */
	@Column(name = "MTN_SUB_FIRSTNAME")
	private String mtnSubFirstname;

	/**
	 * Prenon du client
	 */
	@Column(name = "MTN_SUBS_SURNAME")
	private String mtnSubSurname;
	
	/**
	 * Genre du client
	 */
	@Column(name = "MTN_SUB_GENDER")
	private String mtnSubGender;

	/**
	 * Langue du client
	 */
	@Column(name = "MTN_SUB_LANGUAGE")
	private String mtnSubLanguage;
	
	/**
	 * Date de naissance du client
	 */
	@Column(name = "MTN_SUB_DOB")
	@Temporal(TemporalType.DATE)
	private Date mtnSubDob;
	
	/**
	 * N° CNI du client
	 */
	@Column(name = "MTN_SUB_CNI")
	private String mtnSubCni;

	/**
	 * Pays du client
	 */
	@Column(name = "MTN_SUB_COUNTRY")
	private String mtnSubCountry;
	
	/**
	 * Region du client
	 */
	@Column(name = "MTN_SUB_REGION")
	private String mtnSubRegion;

	/**
	 * Ville du client
	 */
	@Column(name = "MTN_SUB_CITY")
	private String mtnSubCity;
	
	/**
	 * Profession du client
	 */
	@Column(name = "MTN_SUB_PROFESSION")
	private String mtnSubProfession;
	
	/**
	 * Determine si le client un employe ou non
	 *//*
	@Column(name = "EMPLOYE")
	private Boolean employe = Boolean.FALSE;
	*/
	/**
	 * Date de Souscription
	 */
	@Column(name = "SUBS_DATE")
	@Temporal(TemporalType.DATE)
	private Date date;

	/**
	 * Etat du contrat
	 */
	@Column(name = "STATUS")
	@Enumerated(EnumType.STRING)
	private StatutContrat status = StatutContrat.WAITING;

	/**
	 * N° Piece d'identite du client
	 */
	@Column(name = "PID")
	private String pid;

	/**
	 * Periodicite de comptabilisation des commissions
	 */
	@Column(name = "PERIOD_COMPTA")
	@Enumerated(EnumType.STRING)
	private Periodicite period = Periodicite.MOIS;

	/**
	 * Valeur des commissions prelevees
	 */
	@Column(name = "COMMISSIONS")
	private Double commissions = 0d;

	/**
	 * Determine si le client doit etre facture ou non
	 */
	@Column(name = "FACTURER")
	private Boolean facturer = Boolean.TRUE;

	/**
	 * Date de la derniere comptabilisation/facturation
	 */
	@Column(name = "LAST_DATE_COMPTA")
	@Temporal(TemporalType.DATE)
	private Date dateDernCompta;
	
	/**
	 * Utilisateur ayant effectue l'abonnement
	 */
	@NotFound(
	        action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "USER_ID")
	private User user;
	
	/**
	 * Activation/Desactivation du Service
	 */
	@Column(name = "ACTIF")
	private Boolean active = Boolean.TRUE;


	/**
	 * PlageTransactions
	 */
	@NotFound(
	        action = NotFoundAction.IGNORE)
	@ManyToOne
	@JoinColumn(name = "PROFIl_ID")
	private PlageTransactions profil;
	

	/**
	 * Code agence de l'utilisateur
	 */
	@Column(name = "age")
	private String age;
	
	/**
	 * Nom agence de l'utilisateur
	 */
	@Column(name = "ageName")
	private String ageName;

	/**
	 * 
	 */
	@Column(name = "utiValid")
	private String utiValid;
	 
	/**
	 * 
	 */
	@Column(name = "SUBS_DATEVALID")
	@Temporal(TemporalType.DATE)
	private Date dateValid;
	
	@Column(name = "LASTSAVE_DATE_COMPTA")
	@Temporal(TemporalType.DATE)
	private Date dateSaveDernCompta;
	
	@Column(name = "utiSuspendu")
	private String utiSuspendu;
	
	@Column(name = "dateSuspendu")
	@Temporal(TemporalType.DATE)
	private Date dateSuspendu;
		
	@Transient
	private String profilId;
	
	/**
	 * Linkage cote MTN (ECW)
	 */
	@Column(name = "LINKAGE_ECW")
	private Boolean linkageECW = Boolean.TRUE;
	
	@Column(name = "langueImpression")
	private String choixLangue = "EN";
	


	@Temporal(TemporalType.DATE)
	@Column
	private Date dateCrtlLimitPushDay;
	
	@Column
	private Double amountLimitPushDay;
	
	
	@Temporal(TemporalType.DATE)
	@Column
	private Date dateCrtlLimitPullDay;
	
	@Column
	private Double amountLimitPullDay;
	
	
	
	@Temporal(TemporalType.DATE)
	@Column
	private Date dateCrtlLimitPushWeek;
	
	@Column
	private Double amountLimitPushWeek;
	
	
	@Temporal(TemporalType.DATE)
	@Column
	private Date dateCrtlLimitPullWeek;
	
	@Column
	private Double amountLimitPullWeek;
	
	
	
	@Temporal(TemporalType.DATE)
	@Column
	private Date dateCrtlLimitPushMonth;
	
	@Column
	private Double amountLimitPushMonth;
	
	
	@Temporal(TemporalType.DATE)
	@Column
	private Date dateCrtlLimitPullMonth;
	
	@Column
	private Double amountLimitPullMonth;
	
//	@Column(name = "SUBS_OTP")
//	private String subsOTP;
	
	@Transient
	private Boolean signatureVerifie = Boolean.FALSE;
	
	@Transient
	private String subsOTP;
	
	@Column(name="audit_susp_temp", length = 1024)
	private String suspensTemp;
	
	
	/**
	 * @return the signatureVerifie
	 */
	public Boolean getSignatureVerifie() {
		return signatureVerifie;
	}
	
	/**
	 * @param signatureVerifie the signatureVerifie to set
	 */
	public void setSignatureVerifie(Boolean signatureVerifie) {
		this.signatureVerifie = signatureVerifie;
	}

	
	//************************** CONTROLE PLAFOND JOURNALIER ************************

	private void initLimit(TypeOperation type){
		if(TypeOperation.PULL.equals(type)){
			this.dateCrtlLimitPullDay = new Date();
			this.amountLimitPullDay = 0d; 	
		}else if(TypeOperation.PUSH.equals(type)){
			this.dateCrtlLimitPushDay = new Date();
			this.amountLimitPushDay = 0d; 	
		}
	}
	
		
	public Boolean islimit(TypeOperation type,Double amtTrans,Double amtLimit){
		if(amtLimit == null) return Boolean.TRUE;
		if(Boolean.TRUE.equals(crtlLimit(type,amtTrans,amtLimit))){
			if(TypeOperation.PULL.equals(type)) this.amountLimitPullDay = this.amountLimitPullDay + amtTrans ;
			else if(TypeOperation.PUSH.equals(type)) this.amountLimitPushDay = this.amountLimitPushDay + amtTrans;
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
		
	private Boolean crtlLimit(TypeOperation type,Double amtTrans , Double amtLimit){
		if(Boolean.FALSE.equals(sameDays(TypeOperation.PULL.equals(type) ? this.dateCrtlLimitPullDay : this.dateCrtlLimitPushDay))){
			initLimit(type);
		}
		return TypeOperation.PULL.equals(type) ? amtLimit.compareTo(this.amountLimitPullDay+amtTrans) >= 0 : amtLimit.compareTo(this.amountLimitPushDay+amtTrans) >= 0;
	}
	
	
	private Boolean sameDays(Date dateCrtlLimit){
		if(dateCrtlLimit == null) return Boolean.FALSE;
		String pattern = "dd/MM/yyyy";
		String dlimit = DateFormatUtils.format(dateCrtlLimit, pattern);
		String dtoday = DateFormatUtils.format(new Date(), pattern);
		return StringUtils.equalsIgnoreCase(dlimit, dtoday);
	}
	
	
	public Date nextDate(Date date, int nbreJour){
		return DateUtils.addDays(date, nbreJour);
	}
		
	
	//************************** CONTROLE PLAFOND HEBDOMADAIRE - MENSUEL ************************
		
	private void initLimitWeekHebd(TypeOperation type, String periode){
		if("W".equals(periode)){
			if(TypeOperation.PULL.equals(type)){
				this.dateCrtlLimitPullWeek = nextDate(new Date(), 7);
				this.amountLimitPullWeek = 0d; 	
			}else if(TypeOperation.PUSH.equals(type)){
				this.dateCrtlLimitPushWeek = nextDate(new Date(), 7);
				this.amountLimitPushWeek = 0d; 	
			}
		}
		else if("M".equals(periode)){
			if(TypeOperation.PULL.equals(type)){
				this.dateCrtlLimitPullMonth = nextDate(new Date(), 30);
				this.amountLimitPullMonth = 0d; 	
			}else if(TypeOperation.PUSH.equals(type)){
				this.dateCrtlLimitPushMonth = nextDate(new Date(), 30);
				this.amountLimitPushMonth = 0d; 	
			}
		}		
	}
	
	
	public Boolean islimitWeekHebd(TypeOperation type,Double amtTrans,Double amtLimit, String periode){
		if(amtLimit == null) return Boolean.TRUE;
		if(Boolean.TRUE.equals(crtlLimitWeekHebd(type,amtTrans,amtLimit,periode))){
			
			if("W".equals(periode)){
				if(TypeOperation.PULL.equals(type)) this.amountLimitPullWeek = this.amountLimitPullWeek + amtTrans ;
				else if(TypeOperation.PUSH.equals(type)) this.amountLimitPushWeek = this.amountLimitPushWeek + amtTrans;
				return Boolean.TRUE;
			}
			else if("M".equals(periode)){
				if(TypeOperation.PULL.equals(type)) this.amountLimitPullMonth = this.amountLimitPullMonth + amtTrans ;
				else if(TypeOperation.PUSH.equals(type)) this.amountLimitPushMonth = this.amountLimitPushMonth + amtTrans;
				return Boolean.TRUE;
			}
			
		}
		else{
			if("W".equals(periode)){
				if(TypeOperation.PULL.equals(type)) this.amountLimitPullDay = this.amountLimitPullDay - amtTrans ;
				else if(TypeOperation.PUSH.equals(type)) this.amountLimitPushDay = this.amountLimitPushDay - amtTrans;
				return Boolean.FALSE;
			}
			else if("M".equals(periode)){
				if(TypeOperation.PULL.equals(type)) {
					this.amountLimitPullDay = this.amountLimitPullDay - amtTrans ;
					this.amountLimitPullWeek = this.amountLimitPullWeek - amtTrans ;
				}
				else if(TypeOperation.PUSH.equals(type)){
					this.amountLimitPushDay = this.amountLimitPushDay - amtTrans;
					this.amountLimitPushWeek = this.amountLimitPushWeek - amtTrans;
				}
				return Boolean.FALSE;
			}
		}
		return Boolean.FALSE;
	}
	
	
	private Boolean crtlLimitWeekHebd(TypeOperation type,Double amtTrans , Double amtLimit, String periode){
		
		boolean result = Boolean.FALSE;
		
		if("W".equals(periode)){
			// Jour de controle de la limite hebdo atteint ou depassee : on reinitialise
			if(Boolean.TRUE.equals(sameDaysWeekHebd(TypeOperation.PULL.equals(type) ? this.dateCrtlLimitPullWeek : this.dateCrtlLimitPushWeek))){
				initLimitWeekHebd(type,periode);
			}
			return TypeOperation.PULL.equals(type) ? amtLimit.compareTo(this.amountLimitPullWeek+amtTrans) >= 0 : amtLimit.compareTo(this.amountLimitPushWeek+amtTrans) >= 0;
		}
		else if("M".equals(periode)){
			// Jour de controle de la limite mensuelle atteint ou depassee : on reinitialise
			if(Boolean.TRUE.equals(sameDaysWeekHebd(TypeOperation.PULL.equals(type) ? this.dateCrtlLimitPullMonth : this.dateCrtlLimitPushMonth))){
				initLimitWeekHebd(type,periode);
			}
			return TypeOperation.PULL.equals(type) ? amtLimit.compareTo(this.amountLimitPullMonth+amtTrans) >= 0 : amtLimit.compareTo(this.amountLimitPushMonth+amtTrans) >= 0;
		}
		
		return result;
	}
	
	
	private Boolean sameDaysWeekHebd(Date dateCrtlLimit){
		if(dateCrtlLimit == null) return Boolean.TRUE;
		String pattern = "dd/MM/yyyy";
		String dlimit = DateFormatUtils.format(dateCrtlLimit, pattern);
		String dtoday = DateFormatUtils.format(new Date(), pattern);
		return StringUtils.equalsIgnoreCase(dlimit, dtoday) || MoMoHelper.getNbreJoursBetween(dateCrtlLimit, new Date()) >= 0;
	}
	
	//************************** CONTROLE PLAFOND MENSUEL ************************
		
	
	/**
	 * 
	 */
	@PrePersist
	public void PrePersist(){
		if(user != null){
			age = user.getBranch().getCode();
			ageName = user.getBranch().getName();
		}
	}

	/**
	 * @param customerId
	 * @param customerName
	 * @param customerAddress
	 */
	public Subscriber(String customerId, String customerName, String customerAddress) {
		super();
		this.customerId = customerId;
		this.customerName = customerName;
		this.customerAddress = customerAddress;
	}

	/**
	 * Determine s'il s'agit d'un marchand
	 * @return true si c'est un marchand, false sinon
	 */
	public Boolean isMerchant(){
		if(getProfil() == null ) return Boolean.FALSE;
		else{
			if(Boolean.FALSE.equals(getProfil().getActive())) return Boolean.FALSE;
			else{
				if(!getProfil().getId().equals(PlageTransactions.Default)) return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	public Date getDateSaveDernCompta() {
		return dateSaveDernCompta;
	}

	public void setDateSaveDernCompta(Date dateSaveDernCompta) {
		//if(this.dateSaveDernCompta == null ) this.dateSaveDernCompta = dateSaveDernCompta; // commenté lors de la correction de l'anomalie sur la facturation
		this.dateSaveDernCompta = dateSaveDernCompta;
	}

	/**
	 * @return the utiValid
	 */
	public String getUtiValid() {
		return utiValid;
	}

	/**
	 * @param utiValid the utiValid to set
	 */
	public void setUtiValid(String utiValid) {
		this.utiValid = utiValid;
	}

	public String getUtiSuspendu() {
		return utiSuspendu;
	}

	public void setUtiSuspendu(String utiSuspendu) {
		this.utiSuspendu = utiSuspendu;
	}

	public Date getDateSuspendu() {
		return dateSuspendu;
	}

	public void setDateSuspendu(Date dateSuspendu) {
		this.dateSuspendu = dateSuspendu;
	}

	/**
	 * @return the dateValid
	 */
	public Date getDateValid() {
		return dateValid;
	}

	/**
	 * @param dateValid the dateValid to set
	 */
	public void setDateValid(Date dateValid) {
		this.dateValid = dateValid;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
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
	 * @return the ageName
	 */
	public String getAgeName() {
		return ageName;
	}

	/**
	 * @param ageName the ageName to set
	 */
	public void setAgeName(String ageName) {
		this.ageName = ageName;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @return the profilId
	 */
	public String getProfilId() {
		/**if(getProfil() != null){
			this.profilId = getProfil().getId().toString();
		}*/
		return this.profilId;
	}
	
	public void initProfilId() {
		if(getProfil() != null){
			this.profilId = getProfil().getId().toString();
		}
	}

	/**
	 * @param profilId the profilId to set
	 */
	public void setProfilId(String profilId) {
		this.profilId = profilId;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}
 
	/**
	 * @return the profil
	 */
	public PlageTransactions getProfil() {
		return profil;
	}

	/**
	 * @param profil the profil to set
	 */
	public void setProfil(PlageTransactions profil) {
		this.profil = profil;
	}

		
	/**
	 * @return the linkageECW
	 */
	public Boolean getLinkageECW() {
		return linkageECW;
	}


	/**
	 * @param linkageECW the linkageECW to set
	 */
	public void setLinkageECW(Boolean linkageECW) {
		this.linkageECW = linkageECW;
	}


	/**
	 * @return the customerId
	 */
	public String getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the bankPIN
	 */
	public String getBankPIN() {
		return bankPIN;
	}

	/**
	 * @param bankPIN the bankPIN to set
	 */
	public void setBankPIN(String bankPIN) {
		this.bankPIN = Encrypter.getInstance().encryptText( bankPIN );
	}

	/**
	 * @return the phoneNumbers
	 */
	public List<String> getPhoneNumbers() {
		return phoneNumbers;
	}

	/**
	 * @param phoneNumbers the phoneNumbers to set
	 */
	public void setPhoneNumbers(List<String> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}

	/**
	 * @return the accounts
	 */
	public List<String> getAccounts() {
		return accounts;
	}

	/**
	 * @param accounts the accounts to set
	 */
	public void setAccounts(List<String> accounts) {
		this.accounts = accounts;
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
	 * @return the customerAddress
	 */
	public String getCustomerAddress() {
		return customerAddress;
	}

	/**
	 * @param customerAddress the customerAddress to set
	 */
	public void setCustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}
	
	
	/*public Boolean getEmploye() {
		return employe;
	}

	
	public void setEmploye(Boolean employe) {
		this.employe = employe;
	}*/

	/**
	 * @return the mtnSubFirstname
	 */
	public String getMtnSubFirstname() {
		return mtnSubFirstname;
	}

	/**
	 * @param mtnSubFirstname the mtnSubFirstname to set
	 */
	public void setMtnSubFirstname(String mtnSubFirstname) {
		this.mtnSubFirstname = mtnSubFirstname;
	}

	/**
	 * @return the mtnSubSurname
	 */
	public String getMtnSubSurname() {
		return mtnSubSurname;
	}

	/**
	 * @param mtnSubSurname the mtnSubSurname to set
	 */
	public void setMtnSubSurname(String mtnSubSurname) {
		this.mtnSubSurname = mtnSubSurname;
	}

	/**
	 * @return the mtnSubGender
	 */
	public String getMtnSubGender() {
		return mtnSubGender;
	}

	/**
	 * @param mtnSubGender the mtnSubGender to set
	 */
	public void setMtnSubGender(String mtnSubGender) {
		this.mtnSubGender = mtnSubGender;
	}

	/**
	 * @return the mtnSubLanguage
	 */
	public String getMtnSubLanguage() {
		return mtnSubLanguage;
	}

	/**
	 * @param mtnSubLanguage the mtnSubLanguage to set
	 */
	public void setMtnSubLanguage(String mtnSubLanguage) {
		this.mtnSubLanguage = mtnSubLanguage;
	}

	/**
	 * @return the mtnSubDob
	 */
	public Date getMtnSubDob() {
		return mtnSubDob;
	}

	/**
	 * @param mtnSubDob the mtnSubDob to set
	 */
	public void setMtnSubDob(Date mtnSubDob) {
		this.mtnSubDob = mtnSubDob;
	}

	
	/**
	 * @return the mtnCni
	 */
	public String getMtnSubCni() {
		return mtnSubCni;
	}

	/**
	 * @param mtnCni the mtnCni to set
	 */
	public void setMtnSubCni(String mtnCni) {
		this.mtnSubCni = mtnCni;
	}

	/**
	 * @return the mtnSubCountry
	 */
	public String getMtnSubCountry() {
		return mtnSubCountry;
	}

	/**
	 * @param mtnSubCountry the mtnSubCountry to set
	 */
	public void setMtnSubCountry(String mtnSubCountry) {
		this.mtnSubCountry = mtnSubCountry;
	}

	/**
	 * @return the mtnSubRegion
	 */
	public String getMtnSubRegion() {
		return mtnSubRegion;
	}

	/**
	 * @param mtnSubRegion the mtnSubRegion to set
	 */
	public void setMtnSubRegion(String mtnSubRegion) {
		this.mtnSubRegion = mtnSubRegion;
	}

	/**
	 * @return the mtnSubCity
	 */
	public String getMtnSubCity() {
		return mtnSubCity;
	}

	/**
	 * @param mtnSubCity the mtnSubCity to set
	 */
	public void setMtnSubCity(String mtnSubCity) {
		this.mtnSubCity = mtnSubCity;
	}

	/**
	 * @return the mtnSubProfession
	 */
	public String getMtnSubProfession() {
		return mtnSubProfession;
	}

	/**
	 * @param mtnSubProfession the mtnSubProfession to set
	 */
	public void setMtnSubProfession(String mtnSubProfession) {
		this.mtnSubProfession = mtnSubProfession;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the status
	 */
	public StatutContrat getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(StatutContrat status) {
		this.status = status;
	}

	public String getDeCryptedBankPIN() {
		return Encrypter.getInstance().decryptText(this.bankPIN);
	}

	public String getFormattedDate(){
		return PortalHelper.DEFAULT_DATE_FORMAT.format(date);
	}
	
	public String getFormattedLastFactDate(){
		return PortalHelper.DEFAULT_DATE_FORMAT.format(dateDernCompta);
	}

	public String getFirstPhone(){
		return this.phoneNumbers != null && !this.phoneNumbers.isEmpty() ? this.phoneNumbers.get(0) : null;
	}
	public String getFirstAccount(){
		return this.accounts!= null && !this.accounts.isEmpty() ? this.accounts.get(0) : null;
	}

	/**
	 * @return the period
	 */
	public Periodicite getPeriod() {
		return period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(Periodicite period) {
		this.period = period;
	}

	/**
	 * @return the commissions
	 */
	public Double getCommissions() {
		return commissions;
	}

	/**
	 * @param commissions the commissions to set
	 */
	public void setCommissions(Double commissions) {
		this.commissions = commissions;
	}

	/**
	 * @return the facturer
	 */
	public Boolean getFacturer() {
		return facturer;
	}

	/**
	 * @param facturer the facturer to set
	 */
	public void setFacturer(Boolean facturer) {
		this.facturer = facturer;
	}

	/*
	public Boolean getFirstCompta() {
		return firstCompta;
	}

	public void setFirstCompta(Boolean firstCompta) {
		this.firstCompta = firstCompta;
	}
*/

	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * @return the dateDernCompta
	 */
	public Date getDateDernCompta() {
		return dateDernCompta;
	}
 
	/**
	 * @param dateDernCompta the dateDernCompta to set
	 */
	public void setDateDernCompta(Date dateDernCompta) {
		this.dateDernCompta = dateDernCompta;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
		
	
	/**
	 * @return the choixLangue
	 */
	public String getChoixLangue() {
		return null==choixLangue ? "EN" : choixLangue;
	}
	

	/**
	 * @param choixLangue the choixLangue to set
	 */
	public void setChoixLangue(String choixLangue) {
		this.choixLangue = choixLangue;
	}
	
	
	//******************************************************************************//

	/**
	* @return the dateCrtlLimitPushDay
	*/
	public Date getDateCrtlLimitPushDay() {
		return dateCrtlLimitPushDay;
	}


	/**
	* @param dateCrtlLimitPushDay the dateCrtlLimitPushDay to set
	*/
	public void setDateCrtlLimitPushDay(Date dateCrtlLimitPushDay) {
		this.dateCrtlLimitPushDay = dateCrtlLimitPushDay;
	}


	/**
	* @return the amountLimitPushDay
	*/
	public Double getAmountLimitPushDay() {
		return null==amountLimitPushDay ? 0d : amountLimitPushDay;
	}


	/**
	* @param amountLimitPushDay the amountLimitPushDay to set
	*/
	public void setAmountLimitPushDay(Double amountLimitPushDay) {
		this.amountLimitPushDay = amountLimitPushDay;
	}


	/**
	* @return the dateCrtlLimitPullDay
	*/
	public Date getDateCrtlLimitPullDay() {
		return dateCrtlLimitPullDay;
	}


	/**
	* @param dateCrtlLimitPullDay the dateCrtlLimitPullDay to set
	*/
	public void setDateCrtlLimitPullDay(Date dateCrtlLimitPullDay) {
		this.dateCrtlLimitPullDay = dateCrtlLimitPullDay;
	}


	/**
	* @return the amountLimitPullDay
	*/
	public Double getAmountLimitPullDay() {
		return null==amountLimitPullDay ? 0d : amountLimitPullDay;
	}


	/**
	* @param amountLimitPullDay the amountLimitPullDay to set
	*/
	public void setAmountLimitPullDay(Double amountLimitPullDay) {
		this.amountLimitPullDay = amountLimitPullDay;
	}
	

	/**
	* @return the dateCrtlLimitPushWeek
	*/
	public Date getamountLimitPushDayWeek() {
		return dateCrtlLimitPushWeek;
	}


	/**
	* @param dateCrtlLimitPushWeek the dateCrtlLimitPushWeek to set
	*/
	public void setDateCrtlLimitPushWeek(Date dateCrtlLimitPushWeek) {
		this.dateCrtlLimitPushWeek = dateCrtlLimitPushWeek;
	}


	/**
	* @return the amountLimitPushWeek
	*/
	public Double getAmountLimitPushWeek() {
		return null==amountLimitPushWeek ? 0d : amountLimitPushWeek;
	}


	/**
	* @param amountLimitPushWeek the amountLimitPushWeek to set
	*/
	public void setAmountLimitPushWeek(Double amountLimitPushWeek) {
		this.amountLimitPushWeek = amountLimitPushWeek;
	}


	/**
	* @return the dateCrtlLimitPullWeek
	*/
	public Date getDateCrtlLimitPullWeek() {
		return dateCrtlLimitPullWeek;
	}


	/**
	* @param dateCrtlLimitPullWeek the dateCrtlLimitPullWeek to set
	*/
	public void setDateCrtlLimitPullWeek(Date dateCrtlLimitPullWeek) {
		this.dateCrtlLimitPullWeek = dateCrtlLimitPullWeek;
	}


	/**
	* @return the amountLimitPullWeek
	*/
	public Double getAmountLimitPullWeek() {
		return null==amountLimitPullWeek ? 0d : amountLimitPullWeek;
	}


	/**
	* @param amountLimitPullWeek the amountLimitPullWeek to set
	*/
	public void setAmountLimitPullWeek(Double amountLimitPullWeek) {
		this.amountLimitPullWeek = amountLimitPullWeek;
	}


	/**
	* @return the dateCrtlLimitPushMonth
	*/
	public Date getDateCrtlLimitPushMonth() {
		return dateCrtlLimitPushMonth;
	}


	/**
	* @param dateCrtlLimitPushMonth the dateCrtlLimitPushMonth to set
	*/
	public void setDateCrtlLimitPushMonth(Date dateCrtlLimitPushMonth) {
		this.dateCrtlLimitPushMonth = dateCrtlLimitPushMonth;
	}


	/**
	* @return the amountLimitPushMonth
	*/
	public Double getAmountLimitPushMonth() {
		return null==amountLimitPushMonth ? 0d : amountLimitPushMonth;
	}


	/**
	* @param amountLimitPushMonth the amountLimitPushMonth to set
	*/
	public void setAmountLimitPushMonth(Double amountLimitPushMonth) {
		this.amountLimitPushMonth = amountLimitPushMonth;
	}


	/**
	* @return the dateCrtlLimitPullMonth
	*/
	public Date getDateCrtlLimitPullMonth() {
		return dateCrtlLimitPullMonth;
	}


	/**
	* @param dateCrtlLimitPullMonth the dateCrtlLimitPullMonth to set
	*/
	public void setDateCrtlLimitPullMonth(Date dateCrtlLimitPullMonth) {
		this.dateCrtlLimitPullMonth = dateCrtlLimitPullMonth;
	}


	/**
	* @return the amountLimitPullMonth
	*/
	public Double getAmountLimitPullMonth() {
		return null==amountLimitPullMonth ? 0d : amountLimitPullMonth;
	}


	/**
	* @param amountLimitPullMonth the amountLimitPullMonth to set
	*/
	public void setAmountLimitPullMonth(Double amountLimitPullMonth) {
		this.amountLimitPullMonth = amountLimitPullMonth;
	}
	

	/**
	 * @return the subsOTP
	 */
	public String getSubsOTP() {
		return subsOTP;
	}

	/**
	 * @param subsOTP the subsOTP to set
	 */
	public void setSubsOTP(String subsOTP) {
		this.subsOTP = subsOTP;
	}
	
	/**
	 * @return the suspensTemp
	 */
	public String getSuspensTemp() {
		return suspensTemp;
	}

	/**
	 * @param suspensTemp the suspensTemp to set
	 */
	public void setSuspensTemp(String suspensTemp) {
		this.suspensTemp = suspensTemp;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result
//		+ ((customerId == null) ? 0 : customerId.hashCode());
//		return result;
//	}
//		

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Subscriber other = (Subscriber) obj;
//		if (customerId == null) {
//			if (other.customerId != null)
//				return false;
//		} else if (!customerId.equals(other.customerId))
//			return false;
//		return true;
//	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accounts == null) ? 0 : accounts.hashCode());
		result = prime * result + ((active == null) ? 0 : active.hashCode());
		result = prime * result + ((age == null) ? 0 : age.hashCode());
		result = prime * result + ((ageName == null) ? 0 : ageName.hashCode());
		result = prime * result + ((amountLimitPullDay == null) ? 0 : amountLimitPullDay.hashCode());
		result = prime * result + ((amountLimitPullMonth == null) ? 0 : amountLimitPullMonth.hashCode());
		result = prime * result + ((amountLimitPullWeek == null) ? 0 : amountLimitPullWeek.hashCode());
		result = prime * result + ((amountLimitPushDay == null) ? 0 : amountLimitPushDay.hashCode());
		result = prime * result + ((amountLimitPushMonth == null) ? 0 : amountLimitPushMonth.hashCode());
		result = prime * result + ((amountLimitPushWeek == null) ? 0 : amountLimitPushWeek.hashCode());
		result = prime * result + ((bankPIN == null) ? 0 : bankPIN.hashCode());
		result = prime * result + ((choixLangue == null) ? 0 : choixLangue.hashCode());
		result = prime * result + ((commissions == null) ? 0 : commissions.hashCode());
		result = prime * result + ((customerAddress == null) ? 0 : customerAddress.hashCode());
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((customerName == null) ? 0 : customerName.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((dateCrtlLimitPullDay == null) ? 0 : dateCrtlLimitPullDay.hashCode());
		result = prime * result + ((dateCrtlLimitPullMonth == null) ? 0 : dateCrtlLimitPullMonth.hashCode());
		result = prime * result + ((dateCrtlLimitPullWeek == null) ? 0 : dateCrtlLimitPullWeek.hashCode());
		result = prime * result + ((dateCrtlLimitPushDay == null) ? 0 : dateCrtlLimitPushDay.hashCode());
		result = prime * result + ((dateCrtlLimitPushMonth == null) ? 0 : dateCrtlLimitPushMonth.hashCode());
		result = prime * result + ((dateCrtlLimitPushWeek == null) ? 0 : dateCrtlLimitPushWeek.hashCode());
		result = prime * result + ((dateDernCompta == null) ? 0 : dateDernCompta.hashCode());
		result = prime * result + ((dateSaveDernCompta == null) ? 0 : dateSaveDernCompta.hashCode());
		result = prime * result + ((dateSuspendu == null) ? 0 : dateSuspendu.hashCode());
		result = prime * result + ((dateValid == null) ? 0 : dateValid.hashCode());
		result = prime * result + ((facturer == null) ? 0 : facturer.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((linkageECW == null) ? 0 : linkageECW.hashCode());
		result = prime * result + ((mtnSubCity == null) ? 0 : mtnSubCity.hashCode());
		result = prime * result + ((mtnSubCni == null) ? 0 : mtnSubCni.hashCode());
		result = prime * result + ((mtnSubCountry == null) ? 0 : mtnSubCountry.hashCode());
		result = prime * result + ((mtnSubDob == null) ? 0 : mtnSubDob.hashCode());
		result = prime * result + ((mtnSubFirstname == null) ? 0 : mtnSubFirstname.hashCode());
		result = prime * result + ((mtnSubGender == null) ? 0 : mtnSubGender.hashCode());
		result = prime * result + ((mtnSubLanguage == null) ? 0 : mtnSubLanguage.hashCode());
		result = prime * result + ((mtnSubProfession == null) ? 0 : mtnSubProfession.hashCode());
		result = prime * result + ((mtnSubRegion == null) ? 0 : mtnSubRegion.hashCode());
		result = prime * result + ((mtnSubSurname == null) ? 0 : mtnSubSurname.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((phoneNumbers == null) ? 0 : phoneNumbers.hashCode());
		result = prime * result + ((pid == null) ? 0 : pid.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((utiSuspendu == null) ? 0 : utiSuspendu.hashCode());
		result = prime * result + ((utiValid == null) ? 0 : utiValid.hashCode());
		return result;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subscriber other = (Subscriber) obj;
		if (accounts == null) {
			if (other.accounts != null)
				return false;
		} else if (!accounts.equals(other.accounts))
			return false;
		if (active == null) {
			if (other.active != null)
				return false;
		} else if (!active.equals(other.active))
			return false;
		if (age == null) {
			if (other.age != null)
				return false;
		} else if (!age.equals(other.age))
			return false;
		if (ageName == null) {
			if (other.ageName != null)
				return false;
		} else if (!ageName.equals(other.ageName))
			return false;
		if (amountLimitPullDay == null) {
			if (other.amountLimitPullDay != null)
				return false;
		} else if (!amountLimitPullDay.equals(other.amountLimitPullDay))
			return false;
		if (amountLimitPullMonth == null) {
			if (other.amountLimitPullMonth != null)
				return false;
		} else if (!amountLimitPullMonth.equals(other.amountLimitPullMonth))
			return false;
		if (amountLimitPullWeek == null) {
			if (other.amountLimitPullWeek != null)
				return false;
		} else if (!amountLimitPullWeek.equals(other.amountLimitPullWeek))
			return false;
		if (amountLimitPushDay == null) {
			if (other.amountLimitPushDay != null)
				return false;
		} else if (!amountLimitPushDay.equals(other.amountLimitPushDay))
			return false;
		if (amountLimitPushMonth == null) {
			if (other.amountLimitPushMonth != null)
				return false;
		} else if (!amountLimitPushMonth.equals(other.amountLimitPushMonth))
			return false;
		if (amountLimitPushWeek == null) {
			if (other.amountLimitPushWeek != null)
				return false;
		} else if (!amountLimitPushWeek.equals(other.amountLimitPushWeek))
			return false;
		if (bankPIN == null) {
			if (other.bankPIN != null)
				return false;
		} else if (!bankPIN.equals(other.bankPIN))
			return false;
		if (choixLangue == null) {
			if (other.choixLangue != null)
				return false;
		} else if (!choixLangue.equals(other.choixLangue))
			return false;
		if (commissions == null) {
			if (other.commissions != null)
				return false;
		} else if (!commissions.equals(other.commissions))
			return false;
		if (customerAddress == null) {
			if (other.customerAddress != null)
				return false;
		} else if (!customerAddress.equals(other.customerAddress))
			return false;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		if (customerName == null) {
			if (other.customerName != null)
				return false;
		} else if (!customerName.equals(other.customerName))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (dateCrtlLimitPullDay == null) {
			if (other.dateCrtlLimitPullDay != null)
				return false;
		} else if (!dateCrtlLimitPullDay.equals(other.dateCrtlLimitPullDay))
			return false;
		if (dateCrtlLimitPullMonth == null) {
			if (other.dateCrtlLimitPullMonth != null)
				return false;
		} else if (!dateCrtlLimitPullMonth.equals(other.dateCrtlLimitPullMonth))
			return false;
		if (dateCrtlLimitPullWeek == null) {
			if (other.dateCrtlLimitPullWeek != null)
				return false;
		} else if (!dateCrtlLimitPullWeek.equals(other.dateCrtlLimitPullWeek))
			return false;
		if (dateCrtlLimitPushDay == null) {
			if (other.dateCrtlLimitPushDay != null)
				return false;
		} else if (!dateCrtlLimitPushDay.equals(other.dateCrtlLimitPushDay))
			return false;
		if (dateCrtlLimitPushMonth == null) {
			if (other.dateCrtlLimitPushMonth != null)
				return false;
		} else if (!dateCrtlLimitPushMonth.equals(other.dateCrtlLimitPushMonth))
			return false;
		if (dateCrtlLimitPushWeek == null) {
			if (other.dateCrtlLimitPushWeek != null)
				return false;
		} else if (!dateCrtlLimitPushWeek.equals(other.dateCrtlLimitPushWeek))
			return false;
		if (dateDernCompta == null) {
			if (other.dateDernCompta != null)
				return false;
		} else if (!dateDernCompta.equals(other.dateDernCompta))
			return false;
		if (dateSaveDernCompta == null) {
			if (other.dateSaveDernCompta != null)
				return false;
		} else if (!dateSaveDernCompta.equals(other.dateSaveDernCompta))
			return false;
		if (dateSuspendu == null) {
			if (other.dateSuspendu != null)
				return false;
		} else if (!dateSuspendu.equals(other.dateSuspendu))
			return false;
		if (dateValid == null) {
			if (other.dateValid != null)
				return false;
		} else if (!dateValid.equals(other.dateValid))
			return false;
		if (facturer == null) {
			if (other.facturer != null)
				return false;
		} else if (!facturer.equals(other.facturer))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (linkageECW == null) {
			if (other.linkageECW != null)
				return false;
		} else if (!linkageECW.equals(other.linkageECW))
			return false;
		if (mtnSubCity == null) {
			if (other.mtnSubCity != null)
				return false;
		} else if (!mtnSubCity.equals(other.mtnSubCity))
			return false;
		if (mtnSubCni == null) {
			if (other.mtnSubCni != null)
				return false;
		} else if (!mtnSubCni.equals(other.mtnSubCni))
			return false;
		if (mtnSubCountry == null) {
			if (other.mtnSubCountry != null)
				return false;
		} else if (!mtnSubCountry.equals(other.mtnSubCountry))
			return false;
		if (mtnSubDob == null) {
			if (other.mtnSubDob != null)
				return false;
		} else if (!mtnSubDob.equals(other.mtnSubDob))
			return false;
		if (mtnSubFirstname == null) {
			if (other.mtnSubFirstname != null)
				return false;
		} else if (!mtnSubFirstname.equals(other.mtnSubFirstname))
			return false;
		if (mtnSubGender == null) {
			if (other.mtnSubGender != null)
				return false;
		} else if (!mtnSubGender.equals(other.mtnSubGender))
			return false;
		if (mtnSubLanguage == null) {
			if (other.mtnSubLanguage != null)
				return false;
		} else if (!mtnSubLanguage.equals(other.mtnSubLanguage))
			return false;
		if (mtnSubProfession == null) {
			if (other.mtnSubProfession != null)
				return false;
		} else if (!mtnSubProfession.equals(other.mtnSubProfession))
			return false;
		if (mtnSubRegion == null) {
			if (other.mtnSubRegion != null)
				return false;
		} else if (!mtnSubRegion.equals(other.mtnSubRegion))
			return false;
		if (mtnSubSurname == null) {
			if (other.mtnSubSurname != null)
				return false;
		} else if (!mtnSubSurname.equals(other.mtnSubSurname))
			return false;
		if (period != other.period)
			return false;
		if (phoneNumbers == null) {
			if (other.phoneNumbers != null)
				return false;
		} else if (!phoneNumbers.equals(other.phoneNumbers))
			return false;
		if (pid == null) {
			if (other.pid != null)
				return false;
		} else if (!pid.equals(other.pid))
			return false;
		if (status != other.status)
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (utiSuspendu == null) {
			if (other.utiSuspendu != null)
				return false;
		} else if (!utiSuspendu.equals(other.utiSuspendu))
			return false;
		if (utiValid == null) {
			if (other.utiValid != null)
				return false;
		} else if (!utiValid.equals(other.utiValid))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "customerName ";
	}

	/*@Override
	public int compareTo(Subscriber arg0) {
		// TODO Auto-generated method stub
		return 0;
	}*/

}
