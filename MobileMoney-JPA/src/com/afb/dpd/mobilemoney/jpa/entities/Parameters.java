/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.afb.dpd.mobilemoney.jpa.enums.StatutService;
import com.afb.dpd.mobilemoney.jpa.tools.MoMoHelper;
import com.yashiro.persistence.utils.dao.tools.encrypter.Encrypter;

import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

/**
 * Parametres Generaux du Module
 * @author Francis DJIOMOU
 * @version 1.0
 */
@Entity
@Table(name = "MoMo_PRMTRS")
public class Parameters implements Serializable {

	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default Constructor
	 */
	public Parameters() {initCommissions();}
	
	/**
	 * Code par defaut
	 */
	public static final String CODE_PARAM = "MoMo_PARAMS_PULL/PUSH";
	
	/**
	 * Code du parametre
	 */
	@Id
	@Column(name = "CODE")
	private String code = Encrypter.getInstance().hashText(CODE_PARAM);
	
	/**
	 * Longueur du PIN banque
	 */
	@Column(name = "bankPINLength")
	private Integer bankPINLength = 5;
	
	/**
	 * PIN minimal
	 */
	@Column(name = "minPIN")
	private Integer minPIN = 1240;
	
	/**
	 * PIN maximal
	 */
	@Column(name = "maxPIN")
	private Integer maxPIN = 99999;
	
	/**
	 * Nbre de comptes autorises
	 */
	@Column(name = "maxAccounts")
	private Integer maxAccounts = 1;
	
	/**
	 * Nbre de numeros de telephones autorises
	 */
	@Column(name = "maxPhoneNumbers")
	private Integer maxPhoneNumbers = 1;
	
	/**
	 * Duree de traitement d'une transaction
	 */
	@Column(name = "trnsactionTimeOut")
	private Integer trnsactionTimeOut = 60;
	
	/**
	 * Montant max du Pull
	 */
	@Column(name = "maxPullAmount")
	private Double maxPullAmount = 0d;
	
	/**
	 * Montant max du Push
	 */
	@Column(name = "maxPushAmount")
	private Double maxPushAmount = 0d;


	/**
	 * Montant max du Pull journalier
	 */
	@Column(name = "maxPullAmountDay")
	private Double maxPullAmountDay = 0d;
	
	/**
	 * Montant max du Push journalier
	 */
	@Column(name = "maxPushAmountDay")
	private Double maxPushAmountDay = 0d;
	
	/**
	 * Montant max du Pull hebdo
	 */
	@Column(name = "maxPullAmountWeek")
	private Double maxPullAmountWeek = 0d;
	
	/**
	 * Montant max du Push hebdo
	 */
	@Column(name = "maxPushAmountWeek")
	private Double maxPushAmountWeek = 0d;
	
	/**
	 * Montant max du Pull mensuel
	 */
	@Column(name = "maxPullAmountMonth")
	private Double maxPullAmountMonth = 0d;
	
	/**
	 * Montant max du Push mensuel
	 */
	@Column(name = "maxPushAmountMonth")
	private Double maxPushAmountMonth = 0d;
	
	
	/**
	 * Types de comptes autorises
	 */
    @CollectionOfElements(fetch = FetchType.EAGER)
	@JoinTable(
			name = "MoMo_PRM_ACCTYPS",
			joinColumns = {@JoinColumn(name = "PRM_CODE")}
	)
	@Column(name = "ACC_TYPS")
    @Fetch(FetchMode.SUBSELECT)
	private List<String> accountTypes = new ArrayList<String>();
    
    /**
     * Adresse de Test de la plateforme SDP
     */
    @Column(name = "addressSDPTest")
    private String addressSDPTest = "41.206.4.219";
    
    /**
     * Adresse de production de la plateforme SDP
     */
    @Column(name = "addressSDPProd")
    private String addressSDPProd = "41.206.4.162";
    
    /**
     * Code operation a utiliser dans le core banking
     */
    @Column(name = "codeOperation")
    private String codeOperation = "000";

    /**
     * Code de l'utilisateur dans Delta
     */
    @Column(name = "codeUtil")
    private String codeUtil = "MOMO";

    /**
     * DAP des Operations de Push
     */
    @Column(name = "NCPDAPPUSH")
    private String ncpDAPPush = "00001-02523481001-82";

    /**
     * DAP des operations de Pull
     */
    @Column(name = "NCPDAPPULL")
    private String ncpDAPPull = "00001-02523481001-82";

    /**
     * Numero de compte de MTN
     */
    @Column(name = "ncpMTN")
    private String numCompteMTN = "00001-02523481001-82";

    /**
     * Numero de compte des Commissions
     */
    @Column(name = "numCompteCommissions")
    private String numCompteCommissions = "00001-72900090301-90";

    /**
     * Numero de compte TVA
     */
    @Column(name = "numCompteTVA")
    private String numCompteTVA = "00001-43400090035-17";

    /**
     * Numero de compte de liaison
     */
    @Column(name = "numCompteLiaison")
    private String numCompteLiaison = "45920090100";
    
    /**
     * Activation/Desactivation du Service
     */
    @Column(name = "ACTIF")
    private Boolean active = Boolean.TRUE;

    /**
     * Authorise les transactions pendant les TFJ
     */
    @Column(name = "ALLOW_TRANS_IN_TFJ")
    private Boolean allowTransDuringTFJO = Boolean.TRUE;
    
    /**
     * TFJ lances?
     */
    @Column(name = "TFJ_EN_COURS")
    private Boolean tfjoEnCours = Boolean.FALSE;
    
    /**
     * Date d'execution de la compense
     */
    @Column(name = "DATE_TFJO")
    private String dateTfjo;
    
    /**
     * Heure de Remise en service du module (apres TFJO)
     */
    @Column(name = "HEURE_REPRISE")
    private String heureReprise = "00:00";
    
    
    @Column(name = "emailAlerte")
    private String emailAlerte;
    
    @Column(name = "emailfrom")
    private String emailfrom = "alerte_firstbankportal@afrilandfirstbank.com";
    
    @Column(name = "emailsubject")
    private String emailsubject;
    
    @Column(name = "emailmessageCorps")
    private String emailmessageCorps;
    
    @Column(name = "activeAlerte")
    private Boolean activeAlerte = Boolean.FALSE;
    
    @Column(name = "cbsServices")
    private Boolean cbsServices = Boolean.FALSE;

    // URL de l'API de KYC au format protocol://ip:port/path
    @Column(name = "URL_KYC_API")
    private String urlKYCApi ="http://192.168.11.65:80/MomoWEB/MomoKYCService";
            
    @Column(name = "EXECUTION_ROBOT")
	private String executionRobot = " ";
    
    @Column(name = "LANCEMENT_ROBOT")
	private String lancementRobot = " ";
    
    /* Parametres d'envoi de mails */
    /**
	 * URL du service d'envoi de mails
	 */
	@Column(name = "URL_SERVICE_MAIL")
	private String urlServiceMail = "http://192.168.11.58/mailGatwear/rest/mail/sendSimpleMail";
	
	/**
	 * URL du service de package
	 */
	@Column(name = "URL_SERVICE_PACKAGE")
	private String urlServicePackage = "http://devdigitalfirst.afrilandfirstbank.cm:8000/packageproduct/rest/package";
    
	/**
	 * Url du service du core banking
	 */
	@Column(name = "URL_API_CBS")
	private String urlCbsApi;
	/**
	 * Nom ou adresse IP du serveur de mail
	 */
	@Column(name = "SMTP_SERVER_NAME")
	private String smtpServerName = "mail.afrilandfirstbank.cm"; // "172.21.10.91"
	
	/**
	 * Adresse mail source
	 */
	@Column(name = "MAIL_SENDER")
	private String mailSender = "serviceapresvente@afrilandfirstbank.com";
	
	/**
	 * Mot de passe adresse mail source
	 */
	@Column(name = "PWD_SENDER")
	private String pwdSender = "sav00001";
	
	/**
	 * Nom ou adresse IP du serveur de mail
	 */
	@Column(name = "PORT_ENVOI_MAIL")
	private String portEnvoiMail = "25";
    
    /**
	 * Delai inactivite en minutes
	 */
	@Column(name = "DELAI_INACTIVITE")
	private Long delaiInactivite = 10l;
	
	/**
	 * Destinataires des alertes SMS
	 */
	@Column(name = "DEST_PHONE_ALERTE")
	private String destPhoneAlerte = "237673498292";
    
    /**
	 * Destinataires des alertes mails
	 */
	@Column(name = "DEST_MAIL_ALERTE")
	private String destMailAlerte = "personnel_permanence@afrilandfirstbank.com";
	
	/**
	 * Date de derniere transaction reussite
	 */
	@Column(name = "LAST_SUCCESS_TRX")
	private Date lastSuccessTrx = new Date();
	
	/**
     * Etat du service SDP
     */
    @Column(name = "ETAT_SERVICE_SDP")
    private StatutService etatServiceSDP = StatutService.ON;
    
    /**
     * Authorise les transactions end-user provenant de la SDP
     */
    @Column(name = "ALLOW_ENDUSER_TRX_SDP")
    private Boolean allowEndUserTrxSDP = Boolean.TRUE;
    
    /**
     * Authorise les transactions marchand provenant de la SDP
     */
    @Column(name = "ALLOW_MERCHAND_TRX_SDP")
    private Boolean allowMerchandTrxSDP = Boolean.TRUE;
    
    /**
     * Periode de verification des transactions (en jour)
     */
    @Column(name = "PERIODE_VERIF_TRX")
    private Integer periodeVerifTrx = 3; 
    
    /**
	* ip adress du server Amplitude
	*/
	@Column(name = "IP_ADRESS_AMPLI")
	private String ipAdressAmpli;
	
	/**
	* port du serveur Amplitude
	*/
	@Column(name = "PORT_SERV_AMPLI")
	private String portServerAmpli;
	
	/**
	* login serveur Amplitude
	*/
	@Column(name = "USER_LOGIN_SERV_AMPLI")
	private String userLoginServerAmpli;

	/**
	* mdp serveur Amplitude
	*/
	@Column(name = "USER_PASSWORD_AMPLI")
	private String userPasswordServerAmpli;
	
	/**
	* repertoire fichier Amplitude
	*/
	@Column(name = "FILE_PATH_AMPLI")
	private String filePathAmpli;
	
	/**
	* nom fichier Amplitude
	*/
	@Column(name = "FILE_NAME_AMPLI")
	private String fileNameAmpli;
	
	/**
	* extension fichier Amplitude
	*/
	@Column(name = "FILE_EXT_AMPLI")
	private String fileExtensionAmpli;    
    
    /**
     * Liste des commissions
     */
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(
			name = "MoMo_PRM_COMS",
			joinColumns = {@JoinColumn(name = "PRM_CODE")}
	)
    private List<Commissions> commissions = new ArrayList<Commissions>();
    
    /**
     * Liste des numeros de test
     */
    @CollectionOfElements(fetch = FetchType.EAGER)
	@JoinTable(
			name = "MoMo_PRM_PHONE_TEST",
			joinColumns = {@JoinColumn(name = "PRM_CODE")}
	)
	@Column(name = "PHONE_NUMBER")
	@Fetch(FetchMode.SUBSELECT)
	private List<String> numerosTest = new ArrayList<String>();
        
    /**
     * Code integration a utiliser dans le core banking pour l'integration par mouvement externe
     */
    @Column(name = "codeIntegration")
    private String codeIntegration = "588";
    
    /**
     * Liste des codes d'opposition bloquant
     */
    @Column(name = "codesOpp")
    private String codesOpp = "20";
    
    /**
     * Periode de verification des transactions pour les comptes dormant (en jours)
     */
    @Column(name = "PERIODE_VERIF_TRX_CBS")
    private Integer periodeVerifTrxCBS = 30;
    
    /**
     * Periode de verification des transactions pour les comptes dormant (en jours)
     */
    @Column(name = "PERIODE_VERIF_NEW_NCP_CBS")
    private Integer periodeVerifNewNcpCBS = 10;
    
    /**
     * Liste des codes operation des transactions a verifier
     */
    @Column(name = "codesOpeTrx")
    private String codesOpeTrx = "580";

	@Version
	@Column(columnDefinition = "integer DEFAULT 0", nullable = false)
	private Long version;
	
	@Column(name = "heure_resil")
	private String heureVerifResiliation = "8-13";
	
	@Column(name = "tokenCbsApi")
	private String tokenCbsApi;
    
	/**
	 * URL du service d'envoi des sms
	 */
	@Column(name = "URL_SERVICE_SMS")
	private String urlServiceSms = "http://192.168.11.58/smsGatwear/rest/sms/sendSimpleSMS";
	
	@Column(name = "ALERTE_PLAFOND")
	private String mailPlafond = "rodrigue_toukam@afrilandfirstbank.com";
	
    /**
	 * @return the emailAlerte
	 */
	public String getEmailAlerte() {
		return emailAlerte;
	}

	/**
	 * @param emailAlerte the emailAlerte to set
	 */
	public void setEmailAlerte(String emailAlerte) {
		this.emailAlerte = emailAlerte;
	}

	/**
	 * @return the activeAlerte
	 */
	public Boolean getActiveAlerte() {
		return activeAlerte;
	}

	/**
	 * @param activeAlerte the activeAlerte to set
	 */
	public void setActiveAlerte(Boolean activeAlerte) {
		this.activeAlerte = activeAlerte;
	}

	/**
	 * @return the cbsServices
	 */
	public Boolean getCbsServices() {
		return cbsServices == null ? Boolean.FALSE : cbsServices;
	}

	/**
	 * @param cbsServices the cbsServices to set
	 */
	public void setCbsServices(Boolean cbsServices) {
		this.cbsServices = cbsServices;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
     * Initialisation des commissions par defaut
     */
    public void initCommissions() {
    	commissions = MoMoHelper.getDefaultCommissions();
    }
    
	/**
	 * @return the bankPINLength
	 */
	public Integer getBankPINLength() {
		return bankPINLength;
	}

	/**
	 * @param bankPINLength the bankPINLength to set
	 */
	public void setBankPINLength(Integer bankPINLength) {
		this.bankPINLength = bankPINLength;
	}

	/**
	 * @return the minPIN
	 */
	public Integer getMinPIN() {
		return minPIN;
	}

	/**
	 * @param minPIN the minPIN to set
	 */
	public void setMinPIN(Integer minPIN) {
		this.minPIN = minPIN;
	}

	/**
	 * @return the maxPIN
	 */
	public Integer getMaxPIN() {
		return maxPIN;
	}

	/**
	 * @param maxPIN the maxPIN to set
	 */
	public void setMaxPIN(Integer maxPIN) {
		this.maxPIN = maxPIN;
	}

	/**
	 * @return the maxAccounts
	 */
	public Integer getMaxAccounts() {
		return maxAccounts;
	}

	/**
	 * @param maxAccounts the maxAccounts to set
	 */
	public void setMaxAccounts(Integer maxAccounts) {
		this.maxAccounts = maxAccounts;
	}

	/**
	 * @return the maxPhoneNumbers
	 */
	public Integer getMaxPhoneNumbers() {
		return maxPhoneNumbers;
	}

	/**
	 * @param maxPhoneNumbers the maxPhoneNumbers to set
	 */
	public void setMaxPhoneNumbers(Integer maxPhoneNumbers) {
		this.maxPhoneNumbers = maxPhoneNumbers;
	}

	/**
	 * @return the trnsactionTimeOut
	 */
	public Integer getTrnsactionTimeOut() {
		return trnsactionTimeOut;
	}

	/**
	 * @param trnsactionTimeOut the trnsactionTimeOut to set
	 */
	public void setTrnsactionTimeOut(Integer trnsactionTimeOut) {
		this.trnsactionTimeOut = trnsactionTimeOut;
	}

	/**
	 * @return the maxPullAmount
	 */
	public Double getMaxPullAmount() {
		return maxPullAmount;
	}

	/**
	 * @param maxPullAmount the maxPullAmount to set
	 */
	public void setMaxPullAmount(Double maxPullAmount) {
		this.maxPullAmount = maxPullAmount;
	}

	/**
	 * @return the maxPushAmount
	 */
	public Double getMaxPushAmount() {
		return maxPushAmount;
	}

	/**
	 * @param maxPushAmount the maxPushAmount to set
	 */
	public void setMaxPushAmount(Double maxPushAmount) {
		this.maxPushAmount = maxPushAmount;
	}
	

	/**
	 * @return the maxPullAmountDay
	 */
	public Double getMaxPullAmountDay() {
		return maxPullAmountDay = (null==maxPullAmountDay ? 0d : maxPullAmountDay);
	}

	/**
	 * @param maxPullAmountDay the maxPullAmountDay to set
	 */
	public void setMaxPullAmountDay(Double maxPullAmountDay) {
		this.maxPullAmountDay = maxPullAmountDay;
	}

	/**
	 * @return the maxPushAmountDay
	 */
	public Double getMaxPushAmountDay() {
		return maxPushAmountDay = (null==maxPushAmountDay ? 0d : maxPushAmountDay);
	}

	/**
	 * @param maxPushAmountDay the maxPushAmountDay to set
	 */
	public void setMaxPushAmountDay(Double maxPushAmountDay) {
		this.maxPushAmountDay = maxPushAmountDay;
	}
		

	/**
	 * @return the maxPullAmountWeek
	 */
	public Double getMaxPullAmountWeek() {
		return maxPullAmountWeek = (null==maxPullAmountWeek ? 0d : maxPullAmountWeek);
	}


	/**
	 * @param maxPullAmountWeek the maxPullAmountWeek to set
	 */
	public void setMaxPullAmountWeek(Double maxPullAmountWeek) {
		this.maxPullAmountWeek = maxPullAmountWeek;
	}


	/**
	 * @return the maxPushAmountWeek
	 */
	public Double getMaxPushAmountWeek() {
		return maxPushAmountWeek = (null==maxPushAmountWeek ? 0d : maxPushAmountWeek);
	}


	/**
	 * @param maxPushAmountWeek the maxPushAmountWeek to set
	 */
	public void setMaxPushAmountWeek(Double maxPushAmountWeek) {
		this.maxPushAmountWeek = maxPushAmountWeek;
	}


	/**
	 * @return the maxPullAmountMonth
	 */
	public Double getMaxPullAmountMonth() {
		return maxPullAmountMonth = (null==maxPullAmountMonth ? 0d : maxPullAmountMonth);
	}


	/**
	 * @param maxPullAmountMonth the maxPullAmountMonth to set
	 */
	public void setMaxPullAmountMonth(Double maxPullAmountMonth) {
		this.maxPullAmountMonth = maxPullAmountMonth;
	}


	/**
	 * @return the maxPushAmountMonth
	 */
	public Double getMaxPushAmountMonth() {
		return maxPushAmountMonth = (null==maxPushAmountMonth ? 0d : maxPushAmountMonth);
	}


	/**
	 * @param maxPushAmountMonth the maxPushAmountMonth to set
	 */
	public void setMaxPushAmountMonth(Double maxPushAmountMonth) {
		this.maxPushAmountMonth = maxPushAmountMonth;
	}
	

	/**
	 * @return the accountTypes
	 */
	public List<String> getAccountTypes() {
		return accountTypes;
	}

	/**
	 * @param accountTypes the accountTypes to set
	 */
	public void setAccountTypes(List<String> accountTypes) {
		this.accountTypes = accountTypes;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @return the addressSDPTest
	 */
	public String getAddressSDPTest() {
		return addressSDPTest;
	}

	/**
	 * @param addressSDPTest the addressSDPTest to set
	 */
	public void setAddressSDPTest(String addressSDPTest) {
		this.addressSDPTest = addressSDPTest;
	}

	/**
	 * @return the addressSDPProd
	 */
	public String getAddressSDPProd() {
		return addressSDPProd;
	}

	/**
	 * @param addressSDPProd the addressSDPProd to set
	 */
	public void setAddressSDPProd(String addressSDPProd) {
		this.addressSDPProd = addressSDPProd;
	}

	/**
	 * @return the ncpDAPPush
	 */
	public String getNcpDAPPush() {
		return ncpDAPPush;
	}

	/**
	 * @param ncpDAPPush the ncpDAPPush to set
	 */
	public void setNcpDAPPush(String ncpDAPPush) {
		this.ncpDAPPush = ncpDAPPush;
	}

	/**
	 * @return the ncpDAPPull
	 */
	public String getNcpDAPPull() {
		return ncpDAPPull;
	}

	/**
	 * @param ncpDAPPull the ncpDAPPull to set
	 */
	public void setNcpDAPPull(String ncpDAPPull) {
		this.ncpDAPPull = ncpDAPPull;
	}

	/**
	 * @return the numCompteMTN
	 */
	public String getNumCompteMTN() {
		return numCompteMTN;
	}

	/**
	 * @param numCompteMTN the numCompteMTN to set
	 */
	public void setNumCompteMTN(String numCompteMTN) {
		this.numCompteMTN = numCompteMTN;
	}

	/**
	 * @return the numCompteCommissions
	 */
	public String getNumCompteCommissions() {
		return numCompteCommissions;
	}

	/**
	 * @param numCompteCommissions the numCompteCommissions to set
	 */
	public void setNumCompteCommissions(String numCompteCommissions) {
		this.numCompteCommissions = numCompteCommissions;
	}

	/**
	 * @return the numCompteTVA
	 */
	public String getNumCompteTVA() {
		return numCompteTVA;
	}

	/**
	 * @param numCompteTVA the numCompteTVA to set
	 */
	public void setNumCompteTVA(String numCompteTVA) {
		this.numCompteTVA = numCompteTVA;
	}

	/**
	 * @return the codeOperation
	 */
	public String getCodeOperation() {
		return codeOperation;
	}

	/**
	 * @param codeOperation the codeOperation to set
	 */
	public void setCodeOperation(String codeOperation) {
		this.codeOperation = codeOperation;
	}

	/**
	 * @return the commissions
	 */
	public List<Commissions> getCommissions() {
		return commissions;
	}

	/**
	 * @param commissions the commissions to set
	 */
	public void setCommissions(List<Commissions> commissions) {
		this.commissions = commissions;
	}
	
	public String getUrlServiceSms() {
		return urlServiceSms = (null==urlServiceSms ? "http://192.168.11.58/smsGatwear/rest/sms/sendSimpleSMS" : urlServiceSms);
	}

	public void setUrlServiceSms(String urlServiceSms) {
		this.urlServiceSms = urlServiceSms;
	}
	
	/**
	 * @return the codeUtil
	 */
	public String getCodeUtil() {
		return codeUtil;
	}

	/**
	 * @param codeUtil the codeUtil to set
	 */
	public void setCodeUtil(String codeUtil) {
		this.codeUtil = codeUtil;
	}

	/**
	 * @return the numCompteLiaison
	 */
	public String getNumCompteLiaison() {
		return numCompteLiaison;
	}

	/**
	 * @param numCompteLiaison the numCompteLiaison to set
	 */
	public void setNumCompteLiaison(String numCompteLiaison) {
		this.numCompteLiaison = numCompteLiaison;
	}

	/**
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * @param active the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * @return the heureReprise
	 */
	public String getHeureReprise() {
		return heureReprise;
	}

	/**
	 * @param heureReprise the heureReprise to set
	 */
	public void setHeureReprise(String heureReprise) {
		this.heureReprise = heureReprise;
	}

	/**
	 * @return the dateTfjo
	 */
	public String getDateTfjo() {
		return dateTfjo;
	}

	/**
	 * @param dateTfjo the dateTfjo to set
	 */
	public void setDateTfjo(String dateTfjo) {
		this.dateTfjo = dateTfjo;
	}

	/**
	 * @return the allowTransDuringTFJO
	 */
	public Boolean getAllowTransDuringTFJO() {
		return allowTransDuringTFJO;
	}

	/**
	 * @param allowTransDuringTFJO the allowTransDuringTFJO to set
	 */
	public void setAllowTransDuringTFJO(Boolean allowTransDuringTFJO) {
		this.allowTransDuringTFJO = allowTransDuringTFJO;
	}

	
	/**
	 * @return the tfjoEnCours
	 */
	public Boolean getTfjoEnCours() {
		return tfjoEnCours = (null==tfjoEnCours ? false : tfjoEnCours);
	}

	/**
	 * @param tfjoEnCours the tfjoEnCours to set
	 */
	public void setTfjoEnCours(Boolean tfjoEnCours) {
		this.tfjoEnCours = tfjoEnCours;
	}

	
	/**
	 * @return the emailfrom
	 */
	public String getEmailfrom() {
		return emailfrom = (null==emailfrom ? "alerte_firstbankportal@afrilandfirstbank.com" : emailfrom);
	}

	/**
	 * @param emailfrom the emailfrom to set
	 */
	public void setEmailfrom(String emailfrom) {
		this.emailfrom = emailfrom;
	}
	
	/**
	 * @return the mailPlafond
	 */
	public String getMailPlafond() {
		return mailPlafond = (null == mailPlafond ? "rodrigue_toukam@afrilandfirstbank.com" : mailPlafond);
	}

	/**
	 * @param mailPlafond the mailPlafond to set
	 */
	public void setMailPlafond(String mailPlafond) {
		this.mailPlafond = mailPlafond;
	}

	/**
	 * @return the emailsubject
	 */
	public String getEmailsubject() {
		return emailsubject;
	}

	/**
	 * @param emailsubject the emailsubject to set
	 */
	public void setEmailsubject(String emailsubject) {
		this.emailsubject = emailsubject;
	}

	/**
	 * @return the emailmessageCorps
	 */
	public String getEmailmessageCorps() {
		return emailmessageCorps;
	}

	/**
	 * @param emailmessageCorps the emailmessageCorps to set
	 */
	public void setEmailmessageCorps(String emailmessageCorps) {
		this.emailmessageCorps = emailmessageCorps;
	}

	/**
	 * @return the urlKYCApi
	 */
	public String getUrlKYCApi() {
		return urlKYCApi = (null==urlKYCApi ? "http://192.168.11.65:80/MomoWEB/MomoKYCService" : urlKYCApi);
	}

	/**
	 * @param urlKYCApi the urlKYCApi to set
	 */
	public void setUrlKYCApi(String urlKYCApi) {
		this.urlKYCApi = urlKYCApi;
	}

	/**
	 * @return the executionRobot
	 */
	public String getExecutionRobot() {
		return executionRobot;
	}

	/**
	 * @param executionRobot the executionRobot to set
	 */
	public void setExecutionRobot(String executionRobot) {
		this.executionRobot = executionRobot;
	}

	/**
	 * @return the lancementRobot
	 */
	public String getLancementRobot() {
		return lancementRobot;
	}

	/**
	 * @param lancementRobot the lancementRobot to set
	 */
	public void setLancementRobot(String lancementRobot) {
		this.lancementRobot = lancementRobot;
	}
	
	
	/**
	 * @return the urlServiceMail
	 */
	public String getUrlServiceMail() {
		return urlServiceMail = (null==urlServiceMail ? "http://192.168.11.58/mailGatwear/rest/mail/sendSimpleMail" : urlServiceMail);
	}

	/**
	 * @param urlServiceMail the urlServiceMail to set
	 */
	public void setUrlServiceMail(String urlServiceMail) {
		this.urlServiceMail = urlServiceMail;
	}
	
	/**
	 * @return the urlServicePackage
	 */
	public String getUrlServicePackage() {
		return urlServicePackage = (null==urlServicePackage ? "http://devdigitalfirst.afrilandfirstbank.com:8000/packageproduct/rest/package" : urlServicePackage);
	}

	/**
	 * @param urlServicePackage the urlServicePackage to set
	 */
	public void setUrlServicePackage(String urlServicePackage) {
		this.urlServicePackage = urlServicePackage;
	}

	/**
	 * @return the urlCbsApi
	 */
	public String getUrlCbsApi() {
		return urlCbsApi;
	}

	/**
	 * @param urlCbsApi the urlCbsApi to set
	 */
	public void setUrlCbsApi(String urlCbsApi) {
		this.urlCbsApi = urlCbsApi;
	}
			
	/**
	 * @return the smtpServerName
	 */
	public String getSmtpServerName() {
		return smtpServerName = (null==smtpServerName ? "mail.afrilandfirstbank.cm" : smtpServerName);
	}

	/**
	 * @param smtpServerName the smtpServerName to set
	 */
	public void setSmtpServerName(String smtpServerName) {
		this.smtpServerName = smtpServerName;
	}

	/**
	 * @return the mailSender
	 */
	public String getMailSender() {
		return mailSender = (null==mailSender ? "serviceapresvente@afrilandfirstbank.com" : mailSender);
	}

	/**
	 * @param mailSender the mailSender to set
	 */
	public void setMailSender(String mailSender) {
		this.mailSender = mailSender;
	}

	/**
	 * @return the pwdSender
	 */
	public String getPwdSender() {
		return pwdSender = (null==pwdSender ? "sav00001" : pwdSender);
	}

	/**
	 * @param pwdSender the pwdSender to set
	 */
	public void setPwdSender(String pwdSender) {
		this.pwdSender = pwdSender;
	}

	/**
	 * @return the portEnvoiMail
	 */
	public String getPortEnvoiMail() {
		return portEnvoiMail = (null==portEnvoiMail ? "25" : portEnvoiMail);
	}

	/**
	 * @param portEnvoiMail the portEnvoiMail to set
	 */
	public void setPortEnvoiMail(String portEnvoiMail) {
		this.portEnvoiMail = portEnvoiMail;
	}

	/**
	 * @return the destMailAlerte
	 */
	public String getDestMailAlerte() {
		return destMailAlerte = (null==destMailAlerte ? "personnel_permanence@afrilandfirstbank.com" : destMailAlerte);
	}

	/**
	 * @param destMailAlerte the destMailAlerte to set
	 */
	public void setDestMailAlerte(String destMailAlerte) {
		this.destMailAlerte = destMailAlerte;
	}

	/**
	 * @return the lastSuccessTrx
	 */
	public Date getLastSuccessTrx() {
		return lastSuccessTrx = (null==lastSuccessTrx ? new Date() : lastSuccessTrx);
	}

	/**
	 * @param lastSuccessTrx the lastSuccessTrx to set
	 */
	public void setLastSuccessTrx(Date lastSuccessTrx) {
		this.lastSuccessTrx = lastSuccessTrx;
	}

	
	/**
	 * @return the delaiInactivite
	 */
	public Long getDelaiInactivite() {
		return delaiInactivite = (null==delaiInactivite ? 10 : delaiInactivite);
	}

	/**
	 * @param delaiInactivite the delaiInactivite to set
	 */
	public void setDelaiInactivite(Long delaiInactivite) {
		this.delaiInactivite = delaiInactivite;
	}

	/**
	 * @return the destPhoneAlerte
	 */
	public String getDestPhoneAlerte() {
		return destPhoneAlerte = (null==destPhoneAlerte ? "237679529970" : destPhoneAlerte);
	}

	/**
	 * @param destPhoneAlerte the destPhoneAlerte to set
	 */
	public void setDestPhoneAlerte(String destPhoneAlerte) {
		this.destPhoneAlerte = destPhoneAlerte;
	}
	

	/**
	 * @return the etatServiceSDP
	 */
	public StatutService getEtatServiceSDP() {
		return etatServiceSDP = (null==etatServiceSDP ? StatutService.ON : etatServiceSDP);
	}

	/**
	 * @param etatServiceSDP the etatServiceSDP to set
	 */
	public void setEtatServiceSDP(StatutService etatServiceSDP) {
		this.etatServiceSDP = etatServiceSDP;
	}

	/**
	 * @return the allowEndUserTrxSDP
	 */
	public Boolean getAllowEndUserTrxSDP() {
		return allowEndUserTrxSDP = (null==allowEndUserTrxSDP ? Boolean.TRUE : allowEndUserTrxSDP);
	}

	/**
	 * @param allowEndUserTrxSDP the allowEndUserTrxSDP to set
	 */
	public void setAllowEndUserTrxSDP(Boolean allowEndUserTrxSDP) {
		this.allowEndUserTrxSDP = allowEndUserTrxSDP;
	}

	/**
	 * @return the allowMerchandTrxSDP
	 */
	public Boolean getAllowMerchandTrxSDP() {
		return allowMerchandTrxSDP = (null==allowMerchandTrxSDP ? Boolean.TRUE : allowMerchandTrxSDP);
	}

	/**
	 * @param allowMerchandTrxSDP the allowMerchandTrxSDP to set
	 */
	public void setAllowMerchandTrxSDP(Boolean allowMerchandTrxSDP) {
		this.allowMerchandTrxSDP = allowMerchandTrxSDP;
	}
	
	
	/**
	 * @return the periodeVerifTrx
	 */
	public Integer getPeriodeVerifTrx() {
		return (null==periodeVerifTrx ? 3 : periodeVerifTrx);
	}

	/**
	 * @param periodeVerifTrx the periodeVerifTrx to set
	 */
	public void setPeriodeVerifTrx(Integer periodeVerifTrx) {
		this.periodeVerifTrx = periodeVerifTrx;
	}
	
	
	public String getIpAdressAmpli() {
		return ipAdressAmpli == null ? "" : ipAdressAmpli;
	}

	public void setIpAdressAmpli(String ipAdressAmpli) {
		this.ipAdressAmpli = ipAdressAmpli;
	}

	public String getPortServerAmpli() {
		return portServerAmpli == null ? "" : portServerAmpli;
	}

	public void setPortServerAmpli(String portServerAmpli) {
		this.portServerAmpli = portServerAmpli;
	}

	public String getUserLoginServerAmpli() {
		return Encrypter.getInstance().decryptText(userLoginServerAmpli);
	}

	public void setUserLoginServerAmpli(String userLoginServerAmpli) {
		this.userLoginServerAmpli = Encrypter.getInstance().encryptText(userLoginServerAmpli);
	}

	public String getUserPasswordServerAmpli() {
		return Encrypter.getInstance().decryptText(userPasswordServerAmpli);
	}

	public void setUserPasswordServerAmpli(String userPasswordServerAmpli) {
		this.userPasswordServerAmpli = Encrypter.getInstance().encryptText(userPasswordServerAmpli);
	}

	public String getFilePathAmpli() {
		return filePathAmpli = (null==filePathAmpli ? "" : filePathAmpli);
	}

	public void setFilePathAmpli(String filePathAmpli) {
		this.filePathAmpli = filePathAmpli;
	}

	public String getFileNameAmpli() {
		return fileNameAmpli = (null==fileNameAmpli ? "MoMo" : fileNameAmpli);
	}

	public void setFileNameAmpli(String fileNameAmpli) {
		this.fileNameAmpli = fileNameAmpli;
	}

	public String getFileExtensionAmpli() {
		return fileExtensionAmpli = (null==fileExtensionAmpli ? "unl" : fileExtensionAmpli);
	}

	public void setFileExtensionAmpli(String fileExtensionAmpli) {
		this.fileExtensionAmpli = fileExtensionAmpli;
	}
	

	/**
	 * @return the numerosTest
	 */
	public List<String> getNumerosTest() {
		return numerosTest;
	}

	/**
	 * @param numerosTest the numerosTest to set
	 */
	public void setNumerosTest(List<String> numerosTest) {
		this.numerosTest = numerosTest;
	}
	
		
	/**
	 * @return the codeIntegration
	 */
	public String getCodeIntegration() {
		return codeIntegration = (null==codeIntegration ? "588" : codeIntegration);
	}

	/**
	 * @param codeIntegration the codeIntegration to set
	 */
	public void setCodeIntegration(String codeIntegration) {
		this.codeIntegration = codeIntegration;
	}
	
	
	/**
	 * @return the codesOpp
	 */
	public String getCodesOpp() {
		return codesOpp = (null==codesOpp ? "20" : codesOpp);
	}

	/**
	 * @param codesOpp the codesOpp to set
	 */
	public void setCodesOpp(String codesOpp) {
		this.codesOpp = codesOpp;
	}

	
	/**
	 * @return the periodeVerifTrxCBS
	 */
	public Integer getPeriodeVerifTrxCBS() {
		return periodeVerifTrxCBS = (null==periodeVerifTrxCBS ? 30 : periodeVerifTrxCBS);
	}

	/**
	 * @param periodeVerifTrxCBS the periodeVerifTrxCBS to set
	 */
	public void setPeriodeVerifTrxCBS(Integer periodeVerifTrxCBS) {
		this.periodeVerifTrxCBS = periodeVerifTrxCBS;
	}

	
	/**
	 * @return the periodeVerifNewNcpCBS
	 */
	public Integer getPeriodeVerifNewNcpCBS() {
		return periodeVerifNewNcpCBS = (null==periodeVerifNewNcpCBS ? 10 : periodeVerifNewNcpCBS);
	}

	/**
	 * @param periodeVerifNewNcpCBS the periodeVerifNewNcpCBS to set
	 */
	public void setPeriodeVerifNewNcpCBS(Integer periodeVerifNewNcpCBS) {
		this.periodeVerifNewNcpCBS = periodeVerifNewNcpCBS;
	}

	/**
	 * @return the codesOpeTrx
	 */
	public String getCodesOpeTrx() {
		return codesOpeTrx = (null==codesOpeTrx ? "580" : codesOpeTrx);
	}

	/**
	 * @param codesOpeTrx the codesOpeTrx to set
	 */
	public void setCodesOpeTrx(String codesOpeTrx) {
		this.codesOpeTrx = codesOpeTrx;
	}

	/**
	 * @return the tokenCbsApi
	 */
	public String getTokenCbsApi() {
		return tokenCbsApi;
	}

	/**
	 * @param tokenCbsApi the tokenCbsApi to set
	 */
	public void setTokenCbsApi(String tokenCbsApi) {
		this.tokenCbsApi = tokenCbsApi;
	}

	/**
	 * @return the version
	 */
	public Long getVersion() {
		return version;
	}
	
	/**
	 * @return the heureVerifResiliation
	 */
	public String getHeureVerifResiliation() {
		return heureVerifResiliation;
	}

	/**
	 * @param heureVerifResiliation the heureVerifResiliation to set
	 */
	public void setHeureVerifResiliation(String heureVerifResiliation) {
		this.heureVerifResiliation = heureVerifResiliation;
	}
	

	public String getFormattedDate() {
		return PortalHelper.DEFAULT_DATE_FORMAT.format(lastSuccessTrx);
	}
	
}
