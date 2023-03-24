package com.afb.dpd.mobilemoney.business;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import org.codehaus.jettison.json.JSONException;

import com.afb.dpd.mobilemoney.dao.api.exception.DAOAPIException;
import com.afb.dpd.mobilemoney.jpa.entities.Comptabilisation;
import com.afb.dpd.mobilemoney.jpa.entities.FactMonth;
import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.ParametreAlertes;
import com.afb.dpd.mobilemoney.jpa.entities.PlageTransactions;
import com.afb.dpd.mobilemoney.jpa.entities.RequestMessage;
import com.afb.dpd.mobilemoney.jpa.entities.Resiliation;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.TraceRobot;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.entities.USSDTransaction;
import com.afb.dpd.mobilemoney.jpa.enums.ExceptionCode;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jpa.exception.MoMoException;
import com.afb.dpd.mobilemoney.jpa.tools.ClientProduit;
import com.afb.dpd.mobilemoney.jpa.tools.Doublon;
import com.afb.dpd.mobilemoney.jpa.tools.Equilibre;
import com.afb.dpd.mobilemoney.jpa.tools.EquilibreComptes;
import com.afb.dpd.mobilemoney.jpa.tools.TypeCompte;
import com.afb.dpd.mobilemoney.jpa.tools.bkeve;
import com.afb.dpd.mobilemoney.jpa.tools.bkmvti;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.yashiro.persistence.utils.dao.tools.OrderContainer;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

import afb.dsi.dpd.portal.jpa.entities.User;

/**
 * Service Principal de Gestion du Mobile Money
 * @author Francis DJIOMOU
 * @version 1.0
 */
@Remote
public interface IMobileMoneyManagerRemote {
	
	/**
	 * Nom du service
	 */
	public static final String SERVICE_NAME = "MobileMoneyManager";
	
	
	public void activerSubscriptions();
		
	public void profilSubscriptions();
	
	/**
	 * Enregistre les parametres generaux
	 * @param param
	 */
	public Parameters saveParameters(Parameters param);
	
	
	/**
	 * Enregistrer les parametres d'alerte d'une agence
	 * @param pa parametres
	 */
	public ParametreAlertes saveParametreAlertes(ParametreAlertes pa);
	
	
	/**
	 * Enregistrer la liste des parametres d'alerte  des agences
	 * @param list parametres
	 */
	public List<ParametreAlertes> saveListParametreAlertes(List<ParametreAlertes> list);
	

	/**
	 * Editer les parametres d'alerte d'une agence
	 * @param parametres parametres
	 * @return parametres mise a jour
	 */
	public ParametreAlertes updateParametreAlertes(ParametreAlertes ps);
	
	
	/**
	 * Consulter la liste des parametres d'alerte des differentes agences
	 * @return list des parametres
	 */
	public List<ParametreAlertes> consulterParametreAlertes();
	
	
	/**
	 * Recuperer la liste des emails a alerter d'une agance
	 * @param code code de l'agence
	 * @return liste des emails a alerter dans l'agence
	 */
	public List<String> getEmailsAlerteAgence(String code);
	
	
	public void savePlageTransactions(List<PlageTransactions> plages);
	
	/**
	 * Enregistre une souscription
	 * @param subscriber
	 * @return subscriber saved
	 */
	public Subscriber saveSubscriber(Subscriber subscriber) throws Exception;
	
	
	/**
	 * Enregistre une souscription
	 * @param subscriber
	 * @return subscriber saved
	 */
	public Subscriber saveSubscriberECW(Subscriber subscriber) throws Exception;
	
	
	/**
	 * Modifie une souscription
	 * @param subscriber
	 * @return subscriber updated
	 */
	public Subscriber updateSubscriber(Subscriber subscriber) throws Exception;
	
	/**
	 * Modifie le bank PIN d'un souscripteur
	 * @param subscriber
	 * @return subscriber updated
	 */
	public Subscriber updateBankPIN(Subscriber subscriber) throws Exception;
	
	/**
	 * Supprime une souscription
	 * @param subscriberId
	 */
	public void deleteSubscriber(Long subscriberId);
	
	/**
	 * Determine si le client possede deja une souscription
	 * @param customerId
	 * @return true if subscription already exist, false else
	 */
	public boolean subscriptionAlreadyExist(String customerId);
	
	/**
	 * Determine si le client possede deja une souscription
	 * @param customerId : code client
	 * @param phoneNumber : N° de telephone
	 * @return true if subscription already exist, false else
	 */
	public boolean subscriptionAlreadyExistECW(String customerId, String phoneNumber);
	
	/**
	 * Liste les souscriptions
	 * @param rc
	 * @param orders
	 * @return list of subscribers
	 */
	public List<Subscriber> filterSubscriptions( RestrictionsContainer rc, OrderContainer orders );
	
	
	/**
	 * Liste les souscriptions sans aliase sur l'utilisateur
	 * @param rc
	 * @param orders
	 * @return list of subscribers
	 */
	public List<Subscriber> filterSubscriptionsWithoutAlias( RestrictionsContainer rc, OrderContainer orders );
	
	
	/**
	 * Executer le linkage de masse des clients existant lors de la migration vers la plateforme ECW
	 */
	public void executerBulkLinkage();
	
	
	/**
	 * Facturer une souscription
	 * @param subscriber
	 * @return subscriber factured
	 */
	public Subscriber facturerSouscription(Subscriber subscriber) throws Exception;
	
	/**
	 * Annule une souscription
	 * @param sousId
	 * @param login
	 */
	public void annulerSouscription(Long sousId, String login) throws Exception;
	/**
	 * Annule une souscription
	 * @param sousId
	 * @param login
	 */
	public void annulerSouscriptionTemp(Long sousId, String login) throws Exception;
	
	/**
	 * Supprime une souscription
	 * @param sousId
	 */
	public void deleteSouscription(Long sousId);
	
	/**
	 * Genere une transaction sur la base d'un message recu du SDP
	 * @param message
	 * @return transaction generated
	 */
	public Transaction generateTransactionFromMessage( RequestMessage message );
	
	/**
	 * Sauvegarde une transaction
	 * @param transaction
	 * @return transaction saved
	 */
	public Transaction saveTransaction( Transaction transaction );
	
	/**
	 * Met a jour la transaction passee en parametre
	 * @param transaction
	 * @return transaction updated
	 */
	public Transaction updateTransaction( Transaction transaction );
	
	/**
	 * Filtre les transactions
	 * @param rc
	 * @param orders
	 * @return list of transactions
	 */
	public List<Transaction> filterTransactions( RestrictionsContainer rc, OrderContainer orders ); 
	
	/**
	 * Supprime la liste de transactions passees en parametre
	 * @param transactions
	 *
	public void purgeTransactions( List<Transaction> transactions );*/
	
	/**
	 * Consulte la configuration du module
	 * @return parmètres de configuration du module
	 */
	public Parameters consulterConfiguration();
	
	/**
	 * Filtrer les plages de transactions
	 * @return liste des plages de transactions
	 */
	public List<PlageTransactions> filterPlageTransactions(); 
	
	/**
	 * Genere la liste des ecritures comptables pour une liste de transactions definies
	 * @param transactions
	 */
	public void generateAccountingEntries( List<Transaction> transactions );
	
	/**
	 * Initialisation des parametres par defaut du module
	 */
	public void initialisations();
	
	public void processReconciliationAuto();
	
	public void StopReconciliationAuto();
	
	/**
	 * Retourne les parametres generaux du module
	 * @return parametres generaux du module
	 */
	public Parameters findParameters();
	
	/**
	 * Recupere la liste des types de comptes dans Amplitude
	 * @return liste des types de comptes dans Amplitude
	 */
	public List<TypeCompte> filterTypeCompteFromAmplitude();
	
	/**
	 * Recherche un client et ses comptes dans Amplitude
	 * @param customerId
	 * @return subscriber
	 */
	public Subscriber findCustomerFromAmplitude(String customerId);
	
	/**
	 * Recherche une transaction a partir de l'id du souscripteur
	 * @param subsId
	 * @return transaction
	 */
	public Transaction findTransactionBySubscriber(Long subsId);
	
	
	/**
	 * Verifier l'existance d'un abonne en cours d'abonnement a partir du numero de telephone
	 * @param phoneNumber
	 * @return subscriber
	 */
	public Subscriber verifySubscriberFromPhoneNumber(String phoneNumber);
	
	/**
	 * Recherche un souscripteur a partir du numero de telephone
	 * @param phoneNumber
	 * @return subscriber
	 */
	public Subscriber findSubscriberFromPhoneNumber(String phoneNumber);
	
	/**
	 * Recherche un souscripteur autre que celui passe en parametre a partir du numero de telephone
	 * @param phoneNumber
	 * @param subsID
	 * @return subscriber
	 */
	public Subscriber findSubscriberFromPhoneNumber(String phoneNumber, Long subsID);
	
	/**
	 * Poste l'evenement de la transaction dans Amplitude et met a jour les soldes des comptes
	 * @param transaction
	 * @throws Exception
	 *
	public void posterEvenementDansAmplitude(Transaction transaction) throws Exception;
	*/
	
	/**
	 * Recherche les numeros de comptes du client fourni 
	 * @param customerId
	 * @return liste des numeros de comptes
	 */
	public List<String> filterCustomerAccountsFromCBS(String customerId);
	
	/**
	 * Poste l'evenement d'une transaction dans le Core Banking
	 * @param transaction
	 * @throws Exception
	 * @return transaction postee
	 */
	public Transaction posterEvenementDansCoreBanking(Transaction transaction) throws Exception;
	

	/**
	 * Reposte les evenements des transactions dans le Core Banking lorsque le mode nuit Amplitude est active.
	 * Ce traitement est effectue dans le cadre de l'amélioration du mode nuit
	 * @param transaction
	 * @throws Exception
	 * @return transaction repostee
	 */
	public List<Transaction> rePosterEvenementDansCoreBanking(List<Transaction> transaction) throws Exception;
	
	
	/**
	 * Genere l'evenement Delta de la transaction 
	 * @param transaction
	 * @return evenement generee
	 * @throws Exception
	 */
	public bkeve buildEvenement(Transaction transaction) throws Exception;
	
	/**
	 * Poste les ecritures comptables dans bkmvti
	 * @param mvts
	 * @throws Exception
	 *
	public void posterEcrituresDansCoreBanking(List<bkmvti> mvts) throws Exception;*/
	
	/**
	 * Envoi du PIN banque par SMS au client
	 * @param subs
	 */
	public void sendCodePINBySMS(Subscriber subs);
	
	
	/**
	 * 
	 * @param deb
	 * @param fin
	 * @param user
	 * @param mois
	 * @return
	 * @throws Exception
	 */
	public List<Transaction> getTransactionCompensation(Date deb, Date fin, String user, String mois) throws Exception;
	
	
	/**
	 * Extrait la liste des ecritures comptables d'une liste de transactions selectionnees
	 * @param transactions
	 * @param poster Determine si on veut poster les ecritures dans le Core Banking
	 * @return liste des ecritures comptables
	 */
	public List<bkmvti> getECFromTransactions(List<Transaction> transactions, boolean poster);
	
	
	/**
	 * Generation des EC de compensation
	 * @param trans liste des transaction a compenser
	 * @return la liste des EC de compensation
	 * @throws Exception
	 */
	public List<bkmvti> genererECCompensation(List<Transaction> trans) throws Exception;
	
	
	/**
	 * Exporte la liste des ecritures comptables des transactions passees en parametre dans un fichier
	 * @param transactions
	 * @param fileName
	 * @throws Exception
	 */
	public void extractECFromSelectedTransactionsIntoFile(List<Transaction> transactions, String fileName) throws Exception;
	
	
	/**
	 * Exporte la liste des ecritures comptables des transactions passees en parametre dans un fichier
	 * @param Ecritures comptables a extraire
	 * @param fileName
	 * @throws Exception
	 */
	public void extractECCompensationIntoFile(List<bkmvti> bkmvti, String fileName) throws Exception;
	
	
	/**
	 * Exporte la liste des ecritures comptables des transactions passees en parametre dans un fichier
	 * @param Ecritures comptables a extraire
	 * @param fileName
	 * @throws Exception
	 */
	public void extractECFacturationIntoFile(List<Object> mvts, String fileName) throws Exception;
	
	
	/**
	 * Generer les elements du rapport de compensation a visualiser
	 * @param mvts liste des toutes les EC des transactions et de compensation
	 * @param user
	 * @param mois
	 * @param filename 
	 * @return
	 * @throws Exception
	 */
	public FactMonth visualiserRapportCompensation(List<bkmvti> mvts, String user, String mois, String filename) throws Exception;
	
	
	/**
	 * Poste les ecritures comptables issues des transactions passees en parametre dans Delta
	 * @param transactions
	 */
	public void posterTransactionsDansCoreBanking(List<Transaction> transactions, String user) throws Exception;
	
	/**
	 * Archive la liste des transactions selectionnees
	 * @param transactions
	 * @throws Exception
	 */
	public void archiverTransactions(List<Transaction> transactions) throws Exception;
	
	/**
	 * Supprime la liste des transactions selectionnees
	 * @param transactions
	 * @throws Exception
	 */
	public void purgerTransactions(List<Transaction> transactions) throws Exception;
	
	/**
	 * Envoi un sms au numero de telephone fournit
	 * @param message
	 * @param phoneNumber
	 */
	public void sendSMS(String message, String phoneNumber);
	
	/**
	 * Determine si le numero de cpte passe en parametre est en regle ou non pour des operations
	 * @param numCompte
	 * @return true si le compte est ferme, false sinon
	 */
	public boolean isCompteFerme(String numCompte);
	
	/**
	 * Verifi si le solde du cpte est suffisant pour le retrait
	 * @param numCompte
	 * @param montant
	 * @return true si le solde du cpte est suffisant, false sinon
	 */
	public boolean isSoldeSuffisant(String numCompte, Double montant) throws Exception;
	
	
	/**
	 * Traite le message passe en parametre
	 * @param message
	 * @throws Exception
	 * @return transaction
	 */
	public Transaction processPullPushMessage(RequestMessage message) throws Exception, MoMoException;
	
	
	/**
	 * Service de Traitement d'une transaction de type PULL
	 * @param phoneNumber
	 * @param bankPIN
	 * @param amount
	 * @throws Exception
	 * @return transaction
	 */
	public Transaction processPullTransaction(String phoneNumber, String bankPIN, Double amount) throws Exception, MoMoException;
	
	
	/**
	 * Service de traitement d'une transaction de type PUSH
	 * @param phoneNumber
	 * @param bankPIN
	 * @param amount
	 * @throws Exception
	 * @return transaction
	 */
	public Transaction processPushTransaction(String phoneNumber, String bankPIN, Double amount) throws Exception, MoMoException;
	
	
	/**
	 * Service de traitement d'une transaction de type BALANCE
	 * @param phoneNumber
	 * @param bankPIN
	 * @throws Exception
	 * @return balance : solde du compte
	 */
	public Double processBalanceTransaction(String phoneNumber, String bankPIN) throws Exception, MoMoException;
	
	
	/**
	 * Service de traitement d'une transaction de type REVERSAL
	 * @param remoteID
	 * @throws Exception
	 * @return liste des status et leur code
	 */
	public Map<String, String> processReversalTransaction(String remoteID) throws Exception;
	
	
	/**
	 * Determine si le numero de telephone passe en parametre est celui d'un client ayant souscrit au Pusll/Push
	 * @param phoneNumber
	 * @return informations sur l'abonne : status et profile
	 * @throws Exception
	 */
	//public boolean checkSubscriber(String phoneNumber) throws Exception;
	//public JSONObject checkSubscriber(String phoneNumber) throws Exception;
	public Map<String, String> checkSubscriber(String phoneNumber) throws Exception;
	
	
	/**
	 * Procede a l'execution d'une transaction PULL
	 * @param msisdn
	 * @param bankPin
	 * @param amount
	 * @param localID
	 * @param locale
	 * @return liste des parametres de la transaction
	 * @throws Exception
	 */
	public Map<String, String> processPullTransaction(String msisdn, String bankPin, Double amount, String localID, String locale) throws Exception;
	
	
	/**
	 * Procede a l'execution d'une transaction PUSH
	 * @param msisdn
	 * @param bankPin
	 * @param amount
	 * @param localID
	 * @param locale
	 * @return liste des parametres de la transaction
	 * @throws Exception
	 */
	public Map<String, String> processPushTransaction(String msisdn, String bankPin, Double amount, String localID, String locale) throws Exception;
	
	
	/**
	 * Procede a l'execution d'une transaction BALANCE
	 * @param msisdn
	 * @param bankPin
	 * @param localID
	 * @param locale
	 * @return liste des parametres de la transaction
	 * @throws Exception
	 */
	public Map<String, String> processBalanceTransaction(String msisdn, String bankPin, String localID, String locale) throws Exception;
	
	
	/**
	 * Retourne la liste des transactions USSD
	 * @param rc
	 * @return liste des transactions USSD
	 */
	public List<USSDTransaction> filterUSSDTransactions(RestrictionsContainer rc);
	
	/**
	 * Execute la reconciliation en cas d'ecehec des transactions MoMo
	 * @param trans
	 */
	public void executerReconciliation(List<USSDTransaction> trans, String user) throws Exception, MoMoException;
	
	/**
	 * Comptabilisation periodique des transactions Pull et Push
	 * @return liste des comptabilisations
	 * @throws Exception
	 */
	public List<Comptabilisation> executerTFJO() throws Exception;
	
	/*public List<Comptabilisation> executerTFJO2(int year, int month) throws Exception;*/
	
	/**
	 * Executer les TFJO
	 * @param dateFacturation
	 * @throws Exception
	 */
	public void executerTFJO2(Date dateFacturation) throws Exception;
	
	/*public List<Comptabilisation>  chargerDonneesComptabiliserTFJO()  throws Exception;
	
	public List<Comptabilisation>  chargerDonneesComptabiliserRegul()  throws Exception;*/
	
	/**
	 * Charger les donnees de comptabiliser des TFJO
	 * @param dateFacturation
	 * @return liste des transactions
	 * @throws Exception
	 */
	public List<Transaction> chargerDonneesComptabiliserTFJO(Date dateFacturation)  throws Exception;
	
	/**
	 * Charger les donnees de comptabiliser des REGUL
	 * @return liste des transactions
	 * @throws Exception
	 */
	public List<Transaction> chargerDonneesComptabiliserRegul()  throws Exception;
	
	/**
	 * Validation de la comptabilisation des operations Pull/Push
	 * @param data
	 * @param user
	 * @throws Exception
	 */
	public void validerTFJO(List<Comptabilisation> data, String user) throws Exception;
	
//	/**
//	 * Validation de la comptabilisation des operations Pull/Push
//	 * @param data
//	 * @param user
//	 * @param year
//	 * @param month
//	 * @throws Exception
//	 */
//	public void validerTFJO(List<Transaction> data,String user, int year, int month) throws Exception;
	
	
	/**
	 * Validation de la comptabilisation des operations Pull/Push
	 * @param data
	 * @param user
	 * @param year
	 * @param month
	 * @throws Exception
	 */
	public FactMonth validerTFJO(List<Transaction> data,String user, int year, int month) throws Exception;
	
	
	/**
	 * Validation de la comptabilisation des operations Pull/Push
	 * @param data
	 * @param user
	 * @param year
	 * @param month
	 * @throws Exception
	 */
	public Map<String, List<?>> validerTFJO2(List<Transaction> data,String user, int year, int month) throws Exception;
	
	
	/**
	 * Maj du solde des abonnes factures
	 * @param trx liste des transactions de facturation
	 * @param subs liste des abonnes factures
	 * @throws Exception
	 */
	public void majSoldeFact(List<Transaction> trx, List<Subscriber> subs) throws Exception;
	
	
	/**
	 * Exporte les ecritures generees lors de la comptabilisation ds un fichier excel
	 * @param data
	 * @param fileName
	 * @throws Exception
	 */
	public void exportComptabilisationIntoExcelFile( List<Transaction> data, String fileName ) throws Exception;
	
	
	/**
	 * Exporte les ecritures generees lors de la comptabilisation ds un fichier excel
	 * @param data
	 * @param ec
	 * @param fileName
	 * @throws Exception
	 */
	public void exportECIntoExcelFile(List<Transaction> data, List<bkmvti> ec, String fileName ) throws Exception;
	
	
//	/**
//	 * Execute la compensation des operations Pull/Push
//	 * @param dateDeb
//	 * @param dateFin
//	 * @param user
//	 * @throws Exception
//	 */
//	public void executerCompensation(Date dateDeb, Date dateFin, String user) throws Exception;
	
	
	/**
	 * Execute la compensation des operations Pull/Push
	 * @param dateDeb
	 * @param dateFin
	 * @param user
	 * @throws Exception
	 */
	public FactMonth executerCompensation(Date dateDeb, Date dateFin, String user) throws Exception;
	
	
	/**
	 * Annulation des evenements des transactions compensees dans le corebanking et maj des transactions
	 * @param transactions liste des transactions compensees
	 * @return liste des transactions mises a jour
	 * @throws Exception
	 */
	public List<Transaction> annulerEves(List<Transaction> transactions) throws Exception;
	
	
	/**
	 * Recupere l'url de la signature
	 * @param age
	 * @param ncp
	 * @param suf
	 * @param cli
	 * @param datec
	 * @param heurec
	 * @param utic
	 * @return URL de la signature
	 * @throws Exception
	 */
	public String getLienSig(String age, String ncp, String suf, String cli, Date datec, String heurec, String utic) throws Exception;
	
	/**
	 * Recuperer tous les abonnes non factures
	 * @return liste de abonnes
	 */
	public List<Subscriber> findAllSubscriberNonFactures();
	
	/**
	 * Facturer les abonnes
	 * @param list
	 * @throws Exception
	 */
	public void facturerListSubscribers(List<Subscriber> list) throws Exception;
	
	/*public List<Comptabilisation> executerTFJO2() throws Exception;*/
	
	/**
	 * Recuperer le rapport d'equilibre
	 * @return Liste des equilibres
	 * @throws Exception
	 */
	public List<Equilibre> getRapportEquilibre() throws Exception;
	
	/**
	 * Recuperer le rapport des doublons
	 * @return liste des doublons
	 * @throws Exception
	 */
	public List<Doublon> getRapportDoublon() throws Exception;
	
	/**
	 * Recuperer le nombre total des abonnes comptabilises
	 * @return nombre total des abonnes comptabilises
	 * @throws Exception
	 */
	public int getTotalAbonComptabilises() throws Exception;
	
	/**
	 * Recuperer le montant total des commisions des abonnements comptabilises
	 * @return montant total des commisions des abonnements comptabilises
	 * @throws Exception
	 */
	public Double getTotalComsAbonComptabilises() throws Exception;
	
	/**
	 * Recuperer le montant total des taxes des abonnements comptabilises
	 * @return montant total des taxes des abonnements comptabilises
	 * @throws Exception
	 */
	public Double getTotalTaxAbonComptabilises() throws Exception;
	
	
	/**
	 * Recuperer le rapport d'equilibre
	 * @return Liste des equilibres
	 * @throws Exception
	 */
	public List<Equilibre> getRapportEquilibre(String util, String typeOp) throws Exception;
	
	
	/**
	 * Recuperer le rapport d'equilibre PULL/PUSL du compte client
	 * @return Le rapport d'equilibre
	 * @throws Exception
	 */
	public EquilibreComptes getRapportEquilibreCpteClient() throws Exception;
	
	
	/**
	 * Recuperer le rapport d'equilibre PULL/PUSL du compte DAP
	 * @return Le rapport d'equilibre
	 * @throws Exception
	 */
	public EquilibreComptes getRapportEquilibreCpteDAP() throws Exception;
	
	
	/**
	 * Recuperer le rapport d'equilibre PULL/PUSL du compte float MTN
	 * @return Le rapport d'equilibre
	 * @throws Exception
	 */
	public EquilibreComptes getRapportEquilibreCpteFloatMTN() throws Exception;
	
	
	/**
	 * Recuperer le rapport d'equilibre PULL client
	 * @return Le rapport d'equilibre
	 * @throws Exception
	 */
	public Equilibre getRapportEquilibrePULLClient() throws Exception;
	
	
	/**
	 * Recuperer le rapport d'equilibre PUSH client
	 * @return Le rapport d'equilibre
	 * @throws Exception
	 */
	public Equilibre getRapportEquilibrePUSHClient() throws Exception;
	
	
	/**
	 * Recuperer le rapport d'equilibre PULL DAP
	 * @return Le rapport d'equilibre
	 * @throws Exception
	 */
	public Equilibre getRapportEquilibrePULLDAP() throws Exception;
	
	
	/**
	 * Recuperer le rapport d'equilibre PUSH DAP
	 * @return Le rapport d'equilibre
	 * @throws Exception
	 */
	public Equilibre getRapportEquilibrePUSHDAP() throws Exception;
	
	
	/**
	 * Recuperer le rapport d'equilibre PULL float MTN
	 * @return Le rapport d'equilibre
	 * @throws Exception
	 */
	public Equilibre getRapportEquilibrePULLFloatMTN() throws Exception;
	
	
	/**
	 * Recuperer le rapport d'equilibre PUSH float MTN
	 * @return Le rapport d'equilibre
	 * @throws Exception
	 */
	public Equilibre getRapportEquilibrePUSHFloatMTN() throws Exception;
	
	
	/**
	 * Recuperer le rapport des doublons
	 * @return liste des doublons
	 * @throws Exception
	 */
	public List<Doublon> getRapportDoublon(String util, String typeOp) throws Exception;
	
	/**
	 * Recuperer le nombre total des abonnes comptabilises
	 * @return nombre total des abonnes comptabilises
	 * @throws Exception
	 */
	public int getTotalAbonComptabilises(String util, String typeOp) throws Exception;
	
	/**
	 * Recuperer le montant total des commisions des abonnements comptabilises
	 * @return montant total des commisions des abonnements comptabilises
	 * @throws Exception
	 */
	public Double getTotalComsAbonComptabilises(String util, String typeOp) throws Exception;
	
	/**
	 * Recuperer le montant total des taxes des abonnements comptabilises
	 * @return montant total des taxes des abonnements comptabilises
	 * @throws Exception
	 */
	public Double getTotalTaxAbonComptabilises(String util, String typeOp) throws Exception;
	
	
	/**
	 * MAJ du status des abonnes
	 * @return liste des abonnés mis à jour
	 * @throws Exception
	 */
	public List<Subscriber> updateSubscriberIsEmploye() throws Exception;
	
	/**
	 * MAJ des donnees de facturation apres l'implementation du nouveau modele de facturation
	 * @return nombre de donnees de facturation maj
	 * @throws Exception
	 */
	public int updateDataFacturation() throws Exception;
	
	/**
	 * MAJ des EC des facturations en REGUL en prenant en compte les comptes de liaison
	 * @return nombre de donnees de facturation en regul maj
	 * @throws Exception
	 */
	public int updateECRegulsFacturation() throws Exception;
	
	
	/**
	 * Recuperer la liste des transaction a retraiter (transactions intervenues entre le debut des tfjo portal et l'activation du mode nuit Amplitude)
	 * @return liste des transaction a retraiter
	 */
	public List<Transaction> filterTransactionARetraiter();  
	
	
	
	/***********************************************************************************************************
	 ********************************************* DIRECT PUSH/PULL ******************************************** 
	 ***********************************************************************************************************/
	
	
	/**
	 * Service de Traitement d'une transaction de type PULL
	 * @param phoneNumber
	 * @param amount
	 * @throws Exception
	 * @return transaction
	 */
	public Transaction processPullTransactionECW(String trxId, String phoneNumber, Double amount) throws Exception, MoMoException;
	
	
	/**
	 * Service de traitement d'une transaction de type PUSH
	 * @param phoneNumber
	 * @param amount
	 * @throws Exception
	 * @return transaction
	 */
	public Transaction processPushTransactionECW(String trxId, String phoneNumber, Double amount) throws Exception, MoMoException;
	
	
	/**
	 * Service de traitement d'une transaction de type BALANCE
	 * @param phoneNumber
	 * @throws Exception
	 * @return balance : solde du compte
	 */
	public Double processBalanceTransactionECW(String phoneNumber) throws Exception, MoMoException;
	
	
	/**
	 * Procede a l'execution d'une transaction PULL en Direct Push/Pull
	 * @param trxId
	 * @param msisdn
	 * @param amount
	 * @return liste des parametres de la transaction
	 * @throws Exception
	 */
	public Map<String, String> pullTransactionECW(String trxId, String msisdn, Double amount) throws Exception;
	
	
	/**
	 * Procede a l'execution d'une transaction PUSH en Direct Push/Pull
	 * @param trxId
	 * @param msisdn
	 * @param amount
	 * @return liste des parametres de la transaction
	 * @throws Exception
	 */
	public Map<String, String> pushTransactionECW(String trxId, String msisdn, Double amount) throws Exception;
	
	
	/**
	 * Procede a l'execution d'une transaction BALANCE en Direct Push/Pull
	 * @param msisdn
	 * @return liste des parametres de la transaction
	 * @throws Exception
	 */
	public Map<String, String> getBalanceECW(String msisdn) throws Exception;
	
	
	/**
	 * Procede a l'enregistrement de la trace d'une transaction non enregistree (En dernier recourt)
	 * @param trxId
	 * @param msisdn
	 * @param amount
	 * @param typeOp
	 * @param exceptionCode
	 * @return 
	 * @throws Exception
	 */
	public void logTraceTrxECW(String trxId, String msisdn, Double amount, Subscriber subs, TypeOperation typeOp, ExceptionCode exceptionCode);
	
	
	/**
	 * Annule une souscription
	 * @param sousId
	 * @param login
	 */
	public void annulerSouscriptionECW(Long sousId, String login) throws Exception;
	
	
	/**
	 * Service de traitement d'une transaction de type REVERSAL (ECW)
	 * @param trxID
	 * @throws Exception
	 * @return liste des status et leur code
	 */
	public Map<String, String> processReversalTransactionECW(String trxID) throws Exception;
	
	
	
	/**
	 * Procede a l'execution d'une transaction MINI STATEMENT en Direct Push/Pull
	 * @param msisdn
	 * @return liste des transactions
	 * @throws Exception
	 */
	public List<Transaction> getMinistatementECW(String msisdn) throws Exception;

	/**
	 * 
	 * @param rc
	 * @param orders
	 * @return
	 */
	public List<TraceRobot> filterTraceRobots(RestrictionsContainer rc, OrderContainer orders);

	/**
	 * Recuperation de la date de derniere execution du robot
	 * @return
	 */
	public String lastExecutionRobot();
	
	
	/**
	 * Verification du lancement des tfj portal
	 * @return
	 * @throws Exception
	 */
	public boolean isTFJOPortalEnCours() throws Exception;

	/**
	 * Recherche des transactions a reconcilier
	 * @return
	 */
	public List<Transaction> filterTransactionProcessing();
	
	
	/**
	 * Annulation d'une transaction dupliquee (ancien)
	 * @param remoteID
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> cancelDuplicateTransactionECW(String remoteID) throws Exception;
	
	
	/**
	 * Verifier l'existance d'une transaction avec l'id
	 * @param mtnTrxID
	 * @return true or false
	 */
	public Boolean isMTNTrxIDExist(String mtnTrxID);
	
	
//	public Transaction getMTNTrxID(String mtnTrxID);
	
	/**
	 * Recherche de la date de la dernière transaction
	 * @return
	 */
	public Date getLastTrxDate();
	
	
	/**
	 * Recuperation du solde courant a partir du numero de compte
	 * @param numCompte
	 * @return solde courant
	 * @throws Exception
	 */
	public Double getCurrentSolde(String numCompte) throws Exception;
	
	
	/**
	 * Export des ecritures comptables de rapprochement dans un fichier excel
	 * @param fileName
	 * @param ecritures
	 * @param ecrituresCli
	 * @param ecrituresMTN
	 * @param ecrituresCliRapp
	 * @param ecrituresMTNRapp
	 * @param ecrituresCliUnijamb
	 * @param ecrituresMTNUnijamb
	 * @param mts
	 * @param transUnijamb
	 * @throws Exception
	 */
	public void exportRapprochmentBkmvti(String fileName, List<bkmvti> ecritures, List<bkmvti> ecrituresCli, List<bkmvti> ecrituresMTN, List<bkmvti> ecrituresCliRapp, 
			List<bkmvti> ecrituresMTNRapp, List<bkmvti> ecrituresCliUnijamb, List<bkmvti> ecrituresMTNUnijamb, Map mts, List<Transaction> transUnijamb) throws Exception;

	
	/**
	 * Recherche des transactions pour le controle des reservations
	 * @param journee
	 * @param heureDebut
	 * @param heureFin
	 * @return
	 */
	List<Transaction> filterTransactionPortalToControl(Boolean journee, String heureDebut, String heureFin);

	
	/**
	 * 
	 * @param data
	 * @return
	 */
	List<bkeve> filterEvePortalToControl(List<Transaction> data);

	
	/**
	 * 
	 * @param journee
	 * @param heureDebut
	 * @param heureFin
	 * @return
	 */
	List<String> filterTransactionCBSToControl(Boolean journee, String heureDebut, String heureFin);

	
	/**
	 * 
	 * @param journee
	 * @param heureDebut
	 * @param heureFin
	 * @param filename
	 * @throws Exception 
	 */
	void controleReservations(Boolean journee, String heureDebut, String heureFin, String filename) throws Exception;
	
	
	/**
	 * Verification de l'activite sur le compte
	 * @param ncp
	 * @return
	 */
	Boolean isAccountActivity(String ncp);
	
	
	/**
	 * Verification du compte nouvellement ouvert
	 * @param ncp
	 * @return
	 */
	Boolean isNewAccount(String ncp);
	
	
	/**
	 * Verification de transaction ce jour sur le compte
	 * @param ncp
	 * @return
	 */
	Boolean isNewTrxDay(String ncp);
	
	
	/**
	 * Recuperation de la liste des GFC et DA
	 * @param ncp
	 * @return
	 */
	List<User> getGFCDA(User gfc, String ncp);
	
	/**
	 * Recuperation de la liste des resiliations à un package
	 * @param module
	 * @return Liste de ClientProduit
	 */
	List<ClientProduit> listAllResiliations() throws JsonGenerationException, 
	JsonMappingException, UnsupportedEncodingException, IOException, JSONException, DAOAPIException, URISyntaxException;
	
	/**
	 * Recuperation de la liste des abonnements de la vaille à un package
	 * @param module
	 * @return Liste de ClientProduit
	 */
	List<ClientProduit> listAllAbonnements() throws JsonGenerationException, 
	JsonMappingException, UnsupportedEncodingException, IOException, JSONException, DAOAPIException, URISyntaxException;
	
	/**
	 * Recuperation du statut d'un client à un package
	 * @param module
	 * @return Liste de ClientProduit
	 */
	String statusAbon(ClientProduit client) throws JsonGenerationException, 
	JsonMappingException, UnsupportedEncodingException, IOException, JSONException, DAOAPIException, URISyntaxException;

	List<Resiliation> filterResiliations(RestrictionsContainer rc, OrderContainer orders);

	void annulerFacturation(Long sousId, String login) throws Exception;

	List<Subscriber> filterSubscription(RestrictionsContainer rc, OrderContainer orders);

	public List<List<Subscriber>> listInvalidDernFact();
	
	public List<Subscriber> updateDateDernFacturation(List<List<Subscriber>> subs);

	public Long now();

	public void sendSimpleMail(String msg, String subject, String title);

	public List<Transaction> getTransactionControl(String sql);

}
