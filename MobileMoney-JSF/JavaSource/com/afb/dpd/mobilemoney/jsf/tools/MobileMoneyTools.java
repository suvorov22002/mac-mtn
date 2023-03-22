package com.afb.dpd.mobilemoney.jsf.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.URI;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.hibernate.criterion.Restrictions;

import com.afb.dpd.mobilemoney.jpa.entities.Parameters;
import com.afb.dpd.mobilemoney.jpa.entities.Subscriber;
import com.afb.dpd.mobilemoney.jpa.entities.TraceRobot;
import com.afb.dpd.mobilemoney.jpa.entities.Transaction;
import com.afb.dpd.mobilemoney.jpa.enums.MTNErrorCodes;
import com.afb.dpd.mobilemoney.jpa.enums.MTNTransactionStatus;
import com.afb.dpd.mobilemoney.jpa.enums.StatutContrat;
import com.afb.dpd.mobilemoney.jpa.enums.TransactionStatus;
import com.afb.dpd.mobilemoney.jsf.dto.ErrorResponse;
import com.afb.dpd.mobilemoney.jsf.dto.Identification;
import com.afb.dpd.mobilemoney.jsf.dto.Identifications;
import com.afb.dpd.mobilemoney.jsf.dto.Information;
import com.afb.dpd.mobilemoney.jsf.dto.Link;
import com.afb.dpd.mobilemoney.jsf.dto.PersonalInformations;
import com.afb.dpd.mobilemoney.jsf.dto.TransactionsStatus;
import com.afb.dpd.mobilemoney.jsf.dto.TrxStatus;
import com.afb.dpd.mobilemoney.jsf.dto.Unlink;
import com.afb.dpd.mobilemoney.jsf.forms.FrmSubscriber;
import com.afb.dpd.mobilemoney.jsf.models.InformationDialog;
import com.afb.dpd.mobilemoney.jsf.models.PortalExceptionHelper;
import com.afb.dpd.mobilemoney.jsf.models.PortalInformationHelper;
import com.afb.dpi.momo.services.MomoKYCServiceProxy;
import com.yashiro.persistence.utils.dao.tools.RestrictionsContainer;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

public class MobileMoneyTools {
	
	private static Log logger = LogFactory.getLog(MobileMoneyTools.class);
	
	public static String PULL_PUSH = "PULL_PUSH";
	
	public static String FACT_MAC = "MAC";
	public static String FACT_COM = "COM MAC";
	public static String FACT_TAX = "TAX MAC";
	
	public static String RGUL_MAC = "REGUL";
	public static String RGUL_COM = "COM REGUL";
	public static String RGUL_TAX = "TAX REGUL";
	
	/**
	 * Converti le fichier passe en parametre en tableau d'octets 
	 * @param file
	 * @return tableau d'octets
	 */
	public static byte[] getStreamDownloadResourceFile(File file) {
		
		byte[] data = null;
		
		try {
			
			// Si le fichier n'existe pas on sort
			if(!file.exists()) return data;
			
			// Obtention d'un InputStream du le fichier html genere
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			
			// Initilisation d'un flux de sortie
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			// Initialisation d'un Byte
			int b;
			
			// Parcours du fichier et ecriture dans le flux de sortie
			while((b = bis.read()) != -1) baos.write(b);
			
			// On referme
			baos.flush();
			
			// Construction du tableau de byte a partir du flux de sortie
			data = baos.toByteArray();
			
			// Fermeture des flux
			bis.close();
			
		} catch (Exception e) {
			
			// On relance
			throw new RuntimeException(e);
		}
		
		return data;
		
	}
	

	/**
	 * Methode de generation d'un tableau d'octets de l'etat
	 * @param reportName URL du fichier jasper 
	 * @param map Parametres de l'etat
	 * @param maCollection Source de donnees de l'etat
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] getReportPDFBytes(String reportName, HashMap<Object, Object> map, Collection<?> maCollection) throws Exception {
		
		// Construction du JasperPrint
		JasperPrint jp = printReport(reportName, map, maCollection);
		
		// Construction du tableau de bytes
		return JasperExportManager.exportReportToPdf(jp);
	}
	
	
	public static void exportReportToPDFFile(String reportName, HashMap<Object, Object> map, Collection<?> maCollection, String fileName) throws Exception {
		
		// Construction du JasperPrint
		JasperPrint jp = printReport(reportName, map, maCollection);
		
		// Construction du tableau de bytes
		JasperExportManager.exportReportToPdfFile(jp, fileName);
	}
	
	public static InputStream getLogoAFB() throws Exception {
		return new BufferedInputStream( MobileMoneyTools.class.getClassLoader().getResourceAsStream("com/afb/dpd/mobilemoney/jsf/tools/logoentete.png") );
	}

	public static InputStream getLogoMoMo() throws Exception {
		return new BufferedInputStream( MobileMoneyTools.class.getClassLoader().getResourceAsStream("com/afb/dpd/mobilemoney/jsf/tools/MoMo_icon.png") );
	}

	public static InputStream getLogoEntete() throws Exception {
		return new BufferedInputStream( MobileMoneyTools.class.getClassLoader().getResourceAsStream("com/afb/dpd/mobilemoney/jsf/tools/Logo-MAC.png") );
	}

	/**
	 * Methode de construction de l'etat
	 * @param reportName
	 * @param map
	 * @param maCollection
	 * @return report
	 * @throws Exception
	 */
    private static JasperPrint printReport(String reportName, HashMap<Object, Object> map, Collection<?> maCollection) throws Exception {
		
		// Construction de la source de donnees de l'etat
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(maCollection);
		
		// Construction de l'etat
		return JasperFillManager.fillReport(reportName, map, dataSource);
	}
	
    public static String getReportsDir() {
    	return PortalHelper.JBOSS_DATA_DIR + File.separator + PortalHelper.PORTAL_RESOURCES_DATA_DIR + File.separator + PortalHelper.PORTAL_REPORTS_DIR + File.separator + MobileMoneyViewHelper.APPLICATION_EAR + File.separator;
    }

    public static String getDownloadDir() {
    	return PortalHelper.JBOSS_DATA_DIR + File.separator + PortalHelper.PORTAL_RESOURCES_DATA_DIR + File.separator + PortalHelper.PORTAL_DOWNLOAD_DATA_DIR + File.separator;
    }
    
    public static String getSaveDir() {
    	return PortalHelper.JBOSS_DATA_DIR + File.separator + PortalHelper.PORTAL_RESOURCES_DATA_DIR + File.separator + PortalHelper.PORTAL_DOWNLOAD_DATA_DIR + File.separator + MobileMoneyViewHelper.APPLICATION_EAR + File.separator;
    }
    
    
    public static String getSubscriberFRI(Subscriber sub){
    	return sub.getFirstAccount().substring(6,13) + sub.getFirstAccount().substring(18);
    }
    
    
    public static void verifyAndExecuteLinkage(String phoneNumber){
    	
		//logger.info("Execution du bulk linkage");
		// Recherche des utilisateurs non lies a ECW
		Subscriber sub = new Subscriber();
		//logger.info("Recherche des spouscriptions non liees cote ECW");
		sub = MobileMoneyViewHelper.appManager.findSubscriberFromPhoneNumber(phoneNumber);
		//logger.info("OK! "+listeSubs.size()+" Abonnes");
		if(sub != null) {
//			logger.error("USER FOUND OK!");
			// Non lie sur ECW
			if(!(sub.getLinkageECW()!=null && sub.getLinkageECW())){
				Boolean valid = false;
//				logger.error("USER NOT LINK OK!");
				if(sub.getFirstAccount()!=null && sub.getFirstPhone()!=null){
					// Initier le linkage cote MTN
					try {
						// Recuperation des parametres
						Parameters param = MobileMoneyViewHelper.appManager.findParameters();
						// Liaison de l'abonne chez MTN
						MomoKYCServiceProxy proxy = new MomoKYCServiceProxy();
						//logger.info("URL : "+param.getUrlKYCApi());
				        proxy.setEndpoint(param.getUrlKYCApi());
				        String linkage = "";
//				        logger.error("Bank Account : "+sub.getFirstAccount().substring(6,13) + sub.getFirstAccount().substring(18));
				        //logger.info("Phone Number : "+sub.getFirstPhone());
				       	// Recuperation du resultat du linkage depuis la plateforme de MTN
				        linkage = proxy.linkFinancialResourceInformation(sub.getFirstAccount().substring(6,13) + sub.getFirstAccount().substring(18), phoneNumber, null);
				        //linkage = proxy.linkFinancialResourceInformation(sub.getFirstAccount().substring(13).replace("-", ""), phoneNumber, null);
//				        logger.error("RESPONSE LINK = "+linkage);
						// Si on obtient une erreur
						if(linkage.contains("errorResponse") || linkage.contains("errorcode")){
							// Recuperer le message d'erreur
							String error = StringUtils.substringBetween(linkage, "errorcode=\"", "\"");
//				        	logger.info("Erreur : "+error);
				        	if(linkage.contains("<arguments") && linkage.contains("name=")){
								// Recuperer le message d'erreur
								String name = StringUtils.substringBetween(linkage, "name=\"", "\"");
					        	error = error +" ("+name+" : ";
								name = null;
					        }
				        	if(linkage.contains("<arguments") || linkage.contains("value=")){
								// Recuperer le message d'erreur
								String value = StringUtils.substringBetween(linkage, "value=\"", "\"");
								error = error +value+")";
								value = null;
					        }
				        	if(linkage.contains("ACCOUNTHOLDER_NOT_ACTIVE")){
				        		logger.info("NOT ACTIVE PHONE NUMBER : "+sub.getFirstPhone());
					        }
							error = null;
				        }
						
						// Si on obtient la reponse attendue
						else if(linkage.contains("linkfinancialresourceinformationresponse")){
							// Recuperer les parametres de la reponse
							if(linkage.contains("valid")){
					        	 valid = Boolean.valueOf(StringUtils.substringBetween(linkage, "<valid>", "</valid>"));
					        }
						}
						else{
							String error;
							// Recuperer le message d'erreur
							if(linkage.contains("faultstring")){
								error = StringUtils.substringBetween(linkage, "<faultstring>", "</faultstring>");
					        }else{
					        	error = StringUtils.substringBetween(linkage, "(", ")"); // "\"/>"
					        }
							
				        	//logger.info("Erreur : "+error);
							error = null;
						}
						
						if(valid){
							//logger.info("LINKAGE OK!");
							// Maj de l'etat du linkage de l'abonne
				        	sub.setLinkageECW(Boolean.TRUE);
							// Enregistrement de l'abonne maj
							MobileMoneyViewHelper.appDAOLocal.save(sub);
							//logger.info("SUBSCRIBER UPDATED!");
						}
						
						sub = null;
						param = null;
						proxy = null;
						linkage = null;
					} catch(Exception e){
						
						// Affichage de l'exception
						PortalExceptionHelper.threatException(e);
						
					}
				}
			}
		}
		
	}
    
    public static String generateTransactionStatusResponse(String trxID, String phoneNumber, TransactionStatus status){
    	StringBuffer xmlString = new StringBuffer();
		String errorCode = null;
    	
    	// TRX REPRISE
    	if(trxID.length()>33){
    		// INTERNAL ERROR
    		errorCode = "INTERNAL_ERROR";
			xmlString.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); //  standalone=\"yes\"
	        xmlString.append("<ns0:errorResponse xmlns:ns0=\"http://www.ericsson.com/lwac\" errorcode=\""+errorCode+"\">\n");
	        xmlString.append("</ns0:errorResponse>");
    		
    	}
    	// TRX NORMALE
    	else if(trxID.length()>=7 && trxID.length()<=33){
    		xmlString = new StringBuffer();
    		String state = null;
    		if(status.equals(TransactionStatus.SUCCESS)){
    			// create a list of states
    	        List<String> list = new ArrayList<>(); 
    	        // add elements in ArrayList 
    	        list.add(MTNTransactionStatus.PENDING.toString()); 
    	        list.add(MTNTransactionStatus.SUCCESSFUL.toString());
    	        list.add(MTNTransactionStatus.FAILED.toString()); 
    			state = getRandomElement(list);
        		xmlString.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); //  standalone=\"yes\"
                xmlString.append("<ns0:gettransactionstatusresponse  xmlns:ns2=\"http://www.ericsson.com/em/emm/financial/v1_2\">\n");
                xmlString.append("<status>"+state+"</status>\n");
                xmlString.append("<providertransactionid>5a87ef89-7217-4233-85ba-f7d8217b7a8b</providertransactionid>\n");
                xmlString.append("</ns2:gettransactionstatusresponse>");
    		}
    		else{
    			state = "FAILED";
        		xmlString.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); //  standalone=\"yes\"
                xmlString.append("<ns0:gettransactionstatusresponse  xmlns:ns2=\"http://www.ericsson.com/em/emm/financial/v1_2\">\n");
                xmlString.append("<status>"+state+"</status>\n");
                xmlString.append("</ns2:gettransactionstatusresponse>");
    		}
    	}
    	// 
    	else{
    		Boolean choix = new Random().nextBoolean();
    		xmlString = new StringBuffer();
    		if(choix){
    			// *************** WRONG TRX ID ***************
    			errorCode = "TRANSACTION_NOT_FOUND";
    			xmlString.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); //  standalone=\"yes\"
    	        xmlString.append("<ns0:errorResponse xmlns:ns0=\"http://www.ericsson.com/lwac\" errorcode=\""+errorCode+"\">\n");
    	        xmlString.append("</ns0:errorResponse>");
    		}
    		else{
    			// *************** WRONG PHONE NUMBER OR NON EXIST ***************
    			errorCode = "ACCOUNTHOLDER_NOT_FOUND";
    			xmlString.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); //  standalone=\"yes\"
    	        xmlString.append("<ns0:errorResponse xmlns:ns0=\"http://www.ericsson.com/lwac\" errorcode=\""+errorCode+"\">\n");
    	        xmlString.append("<arguments name=\"id\" value=\""+phoneNumber+"\"/>\n");
    	        xmlString.append("</ns0:errorResponse>");
    		}
    		
    	}
		    	
    	return xmlString.toString();
    }
    
    public static boolean stringContainsItemFromList(String inputStr, List<MTNErrorCodes> list){
        for(int i =0; i < list.size(); i++)
        {
            if(inputStr.contains(list.get(i).toString()))
            {
                return true;
            }
        }
        return false;
    }
    
    
    public static String getRandomElement(List<String> list) 
    { 
        Random rand = new Random(); 
        return list.get(rand.nextInt(list.size())); 
    } 
    
    
    public static void saveTraceRobot(Transaction t, TransactionStatus txStatus, String commentaire){
    	TraceRobot trace = new TraceRobot();
		trace.setDatetimeTrace(new Date());
		
		trace.setStatus(txStatus);
    	trace.setCommentaire(commentaire);
		
		trace.setOperation(t.getTypeOperation());
    	trace.setAccount(t.getAccount());
    	trace.setAmount(t.getAmount());
    	trace.setPhone(t.getPhoneNumber());
    	trace.setTrxId(t.getId());
    	
    	// Save trace
    	MobileMoneyViewHelper.appDAOLocal.save(trace);
    }
    
    
    public static String getFRI(Subscriber subscriber){
		
		return subscriber.getFirstAccount().substring(6,13) + subscriber.getFirstAccount().substring(18);
    }
    
    
    public static ErrorResponse getErrorResponse(String kyc){
		
		ErrorResponse error = null;
		
		// Si on obtient une erreur
		if(kyc.contains("errorResponse") || kyc.contains("errorcode")){
			error = new ErrorResponse();
			// Recuperer le message d'erreur
			error.setErrorcode(StringUtils.substringBetween(kyc, "errorcode=\"", "\""));
			
        	if(kyc.contains("<arguments") && kyc.contains("name=")){
				// Recuperer le message d'erreur
        		error.setErrorname(StringUtils.substringBetween(kyc, "name=\"", "\""));
	        }
        	if(kyc.contains("<arguments") || kyc.contains("value=")){
				// Recuperer le message d'erreur
        		error.setErrormessage(StringUtils.substringBetween(kyc, "value=\"", "\""));
	        }
        }
		
		return error;
	}
	
	
	public static PersonalInformations getPersonalInformations(String kyc){
		
		Information info = null;
		ErrorResponse error = getErrorResponse(kyc);
		
		// Si on obtient la reponse attendue
		if(kyc.contains("getaccountholderpersonalinformationresponse")){
			info = new Information();
			// Recuperer les parametres de la reponse
			if(kyc.contains("firstname")){
				info.setFirstname(StringUtils.substringBetween(kyc, "<firstname>", "</firstname>"));
	        }
			if(kyc.contains("surname")){
				info.setSurname(StringUtils.substringBetween(kyc, "<surname>", "</surname>"));
	        }
			if(kyc.contains("gender")){
				info.setGender(StringUtils.substringBetween(kyc, "<gender>", "</gender>"));
	        }
			if(kyc.contains("language")){
				info.setLanguage(StringUtils.substringBetween(kyc, "<language>", "</language>"));
	        }
			else {
				info.setLanguage("fr");
			}
			if(kyc.contains("date")){
				String date = StringUtils.substringBetween(kyc, "<date>", "</date>").substring(0, 10);
				info.setDate(date);
//				DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//				try {
//					Date d = format.parse(date);
//					info.setDate(new SimpleDateFormat("dd MMM yyyy").format(d));
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					info.setDate(null);
//					e.printStackTrace();
//				}
	        }
			if(kyc.contains("country")){
				info.setCountry(StringUtils.substringBetween(kyc, "<country>", "</country>"));
	        }
			if(kyc.contains("province")){
				info.setRegion(StringUtils.substringBetween(kyc, "<province>", "</province>"));
	        }
			if(kyc.contains("city")){
				info.setCity(StringUtils.substringBetween(kyc, "<city>", "</city>"));
	        }
			if(kyc.contains("profession")){
				info.setProfession(StringUtils.substringBetween(kyc, "<profession>", "</profession>"));
	        }
			if(kyc.contains("residentialstatus")){
				info.setResidencialstatus(StringUtils.substringBetween(kyc, "<residentialstatus>", "</residentialstatus>"));
	        }
		}
		else if(error == null){
			error = new ErrorResponse();
			// Recuperer le message d'erreur
			error.setErrormessage(StringUtils.substringBetween(kyc, "(", ")"));
		}
		
		return new PersonalInformations(info, error);
	}
	
	
	public static Identifications getIdentifications(String id){
		
		Identification identity = null;
		ErrorResponse error = getErrorResponse(id);
		
		// Si on obtient la reponse attendue
		if(id.contains("getaccountholderidentificationresponse")){
			identity = new Identification();
			// Recuperer les parametres de la reponse
			if(id.contains("<Id>")){
				identity.setId(StringUtils.substringBetween(id, "<Id>", "</Id>"));
	        }
			if(id.contains("<IdTp>")){
				identity.setIdtype(StringUtils.substringBetween(id, "<IdTp>", "</IdTp>"));
	        }
			
        }
		else if(error == null){
			error = new ErrorResponse();
			// Recuperer le message d'erreur
			error.setErrormessage(StringUtils.substringBetween(id, "(", ")"));
		}
				
		return new Identifications(identity, error);
	}
	
	
	public static Link getLinkage(String link){
		
		ErrorResponse error = getErrorResponse(link);
		Boolean valid = null;
		
		// Si on obtient la reponse attendue
		if(link.contains("linkfinancialresourceinformationresponse")){
			// Recuperer les parametres de la reponse
			if(link.contains("valid")){
				valid = Boolean.valueOf(StringUtils.substringBetween(link, "<valid>", "</valid>"));
	        }
        }
		else if(error == null){
			error = new ErrorResponse();
			// Recuperer le message d'erreur
			if(link.contains("faultstring")){
				error.setErrormessage(StringUtils.substringBetween(link, "<faultstring>", "</faultstring>"));
	        }else{
	        	error.setErrormessage(StringUtils.substringBetween(link, "(", ")"));
	        }
		}
				
		return new Link(valid, error);
	}


	public static Unlink getUnlinkage(String unlink){
	
		Boolean valid = null;
		ErrorResponse error = getErrorResponse(unlink);
		
		// Si on obtient la reponse attendue
		if(unlink.contains("unlinkfinancialresourceinformationresponse")){
			// Recuperer les parametres de la reponse
			valid = Boolean.TRUE;			
	    }
				
		return new Unlink(valid, error);
	}
	
	
	public static TrxStatus getTransactionStatus(String trxStatus){
		
		TransactionsStatus status = null;
		ErrorResponse error = getErrorResponse(trxStatus);
		
		// Si on obtient la reponse attendue
		if(trxStatus.contains("gettransactionstatusresponse")){
			status = new TransactionsStatus();
			// Recuperer les parametres de la reponse
			if(trxStatus.contains("financialtransactionid")){
				status.setFinancialtransactionid(StringUtils.substringBetween(trxStatus, "<financialtransactionid>", "</financialtransactionid>"));
	        }
			if(trxStatus.contains("status")){
				status.setStatus(StringUtils.substringBetween(trxStatus, "<status>", "</status>"));
	        }
			
        }
		else if(error == null){
			error = new ErrorResponse();
			// Recuperer le message d'erreur
			error.setErrormessage(StringUtils.substringBetween(trxStatus, "(", ")"));
		}
				
		return new TrxStatus(status, error);
	}
	
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
//	public HttpEntity sendRequestHttpEntity(String url) throws ClientProtocolException, IOException{
//		// specify the get request
//		HttpClient httpclient = null;
//		HttpGet request = new HttpGet(url);
//		HttpResponse httpResponse = httpclient.execute(target, request);
//		return entity = httpResponse.getEntity();	
//	}
	
	
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String sendHttpRequest(String url) throws ClientProtocolException, IOException{
		
		logger.info("Encoded url : "+url);
		CloseableHttpClient client = HttpClients.createDefault();
	    HttpGet httpGet = new HttpGet(url);
	    
	    httpGet.setHeader("Accept", "application/json");
	    httpGet.setHeader("Content-type", "application/json");
	    // Integrer le certificat si necessaire
	    CloseableHttpResponse response = client.execute(httpGet);
	    logger.info("After execute");
	    
	    String content = EntityUtils.toString(response.getEntity());
	    logger.info("Response : "+content);
	    
	    client.close();
    
	    return content;
	}
	
	
	/**
	 * 
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String sendHttpRequest(String baseUrl, String message, String format, String to, String subject, String from) throws ClientProtocolException, IOException{
		
		logger.info("BASE URL : "+baseUrl);
		logger.info("MESSAGE : "+message);
		logger.info("TO : "+to);
		logger.info("FROM : "+from);
		
		try (CloseableHttpClient client = HttpClientBuilder.create().useSystemProperties().build()) {
			java.net.URI uri = null;
			try {
				uri = new URIBuilder(baseUrl)
						.addParameter("message", message)
						.addParameter("format", format)
						.addParameter("to", to)
						.addParameter("subject", subject)
						.addParameter("from", from)
						.build();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpGet getRequest = new HttpGet(uri);
			
			CloseableHttpResponse resp = client.execute(getRequest);
			
			return resp.getEntity().toString();
		}
	}
	
}
