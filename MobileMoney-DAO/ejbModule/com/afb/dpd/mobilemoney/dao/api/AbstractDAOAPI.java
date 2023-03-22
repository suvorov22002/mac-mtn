package com.afb.dpd.mobilemoney.dao.api;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jboss.util.NotImplementedException;

import com.afb.dpd.mobilemoney.dao.api.exception.DAOAPIException;
import com.afb.dpd.mobilemoney.jpa.tools.bkeve;
import com.afb.dpd.mobilemoney.jpa.tools.bkmvti;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;


public abstract class AbstractDAOAPI<T> {
	
//	protected HttpHost target;
	final Class<T> typeParameterClass;
	ObjectMapper mapper;
	JsonStringEncoder jsonEncoder;
	public static final int TIMEOUT = 120;//seconds
	
	public AbstractDAOAPI(Class<T> type) {
		this.typeParameterClass = type;
	//	this.target = target;
		this.mapper = new ObjectMapper();
		this.mapper.setSerializationInclusion(Include.NON_NULL);
		this.jsonEncoder = JsonStringEncoder.getInstance();
	}
	
	public T get(int id) {
		throw new NotImplementedException();
	}
	
	public String get(Map<String , String> filtre,String host, String protocole, String port, String url, boolean secure) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException{
		URIBuilder builder = new URIBuilder(url);

		if(secure) {
		//	builder.setParameter("password", "scoring");
			//builder.setParameter("login", "passwordafb");
		}
		
		if(filtre != null) {
			for(String key : filtre.keySet()) {
				builder.addParameter(key, filtre.get(key));
			}
		}
		
		builder.build();
		
		//System.out.println("request url " +builder.build());

		HttpGet getRequest = new HttpGet(builder.build());
		getRequest.setHeader("Content-type", "application/json");
	//	getRequest.addHeader("Authorization", Parameters.authentication());
		
		HttpResponse httpResponse = this.getHttpClient().execute(getTarget(host, protocole, port), getRequest);
		this.checkStatusResponse(httpResponse);
		String results =null;
		
		
		HttpEntity entity = httpResponse.getEntity();
		
		if (entity != null) {
			String retSrc = EntityUtils.toString(entity);		
			
				//retSrc = new JSONArray().toString();
				JSONObject jsonObject = new JSONObject(retSrc);
				
				//System.out.println("resultString: "+retSrc);
				results = jsonObject.getString("response");
		}
		
		return results;
	}
	
	public HttpHost getTarget(String host, String protocole, String port) {
		// specify the host, protocol, and port
		if(StringUtils.isNotBlank(host) && StringUtils.isNotBlank(port) && StringUtils.isNotBlank(protocole))
			return new HttpHost(host, Integer.parseInt(port), protocole);
		else
			return null;
	}

	public List<T> filter(Map<String , String> filtre ,String host, String protocole, String port, String url, boolean secure) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		
		URIBuilder builder = new URIBuilder(url);

		if(secure) {
		//	builder.setParameter("password", "scoring");
			//builder.setParameter("login", "passwordafb");
		}
		
		if(filtre != null) {
			for(String key : filtre.keySet()) {
				builder.addParameter(key, filtre.get(key));
			}
		}
		
		builder.build();
		
		//System.out.println("request url " +builder.build());

		HttpGet getRequest = new HttpGet(builder.build());
		getRequest.setHeader("Content-type", "application/json");
	//	getRequest.addHeader("Authorization", Parameters.authentication());
		
		HttpResponse httpResponse = this.getHttpClient().execute(getTarget(host, protocole, port), getRequest);
		this.checkStatusResponse(httpResponse);
		List<T> results = new ArrayList<>();
		
		
		HttpEntity entity = httpResponse.getEntity();
		
		if (entity != null) {
			String retSrc = EntityUtils.toString(entity);				
			if(retSrc.length() == 0) {
				retSrc = new JSONArray().toString();
			}
			//System.out.println("resultString: "+retSrc);
			if(retSrc.substring(0, 1).equalsIgnoreCase("{")){
				retSrc = "["+retSrc+"]";
			}
			
			// CONVERT RESPONSE STRING TO JSON ARRAY
			JSONArray ja = new JSONArray(retSrc);
			int n = ja.length();
			for(int i=0 ; i< n ; i++) {
				JSONObject jo = ja.getJSONObject(i);
				results.add(this.mapToObject(jo));
			}
		}
		
		return results;
	}

	public HttpClient getHttpClient() {
		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectTimeout(TIMEOUT*1000)
				.setConnectionRequestTimeout(TIMEOUT*1000)
				.setSocketTimeout(TIMEOUT*1000)
				.build();
		//System.out.println("Connection Timeout , " + requestConfig.getConnectionRequestTimeout());
		HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
		return client;
	}
	
	public CloseableHttpClient getClosableHttpClient() {
		
		RequestConfig config = RequestConfig.custom()
		  .setConnectTimeout(TIMEOUT * 20000)
		  .setConnectionRequestTimeout(TIMEOUT * 20000)
		  .setSocketTimeout(TIMEOUT * 20000).build();
		CloseableHttpClient client = 
		  HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		
		return client;
	}
	
//	public HttpHost getTarget() {
//		return this.target;
//	}
	
	public T mapToObject(JSONObject obj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		ObjectMapper map = new ObjectMapper();
		StringReader reader = new StringReader(obj.toString());
		T o = this.mapper.readValue(reader, this.typeParameterClass);
		//System.out.println("returned object " +  o);
		return o;
	}
	
	public String mapToJsonStrings(T obj) throws JsonGenerationException, JsonMappingException, IOException, JSONException {
		String map = this.mapper.writeValueAsString(obj);
		String encoded = this.encodeJson(map);
		//System.out.println("encoded "+ encoded);
		return map;
	}
	
	private String encodeJson(String jsonString) {
		return new String(jsonEncoder.quoteAsString(jsonString));
	}
	
	protected void checkStatusResponse(HttpResponse response) throws DAOAPIException {
		int status = response.getStatusLine().getStatusCode();
		
		if(status != 200 && status != 201 && status != 202) {
			throw new DAOAPIException(" HTTP : "+status + " result "+response.getStatusLine().getReasonPhrase());
		}
	}
	
	public String getListAllUrl() {
		return this.getUrl()+"/list-all";
	}
	
	public abstract String getUrl();

	public bkeve sendPostEvent(String url, bkeve bkeve) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		
		String playload = null;
		String resp_code;
		
		
		playload = mapToJsonString(bkeve);
		if(!isJSONValid(playload)) {
			return null;
		}
		
        HttpPost post = new HttpPost(url+"/event");

        // add request parameter, form parameters
        post.setHeader("content-type", "application/json");
		post.setEntity(new StringEntity(playload , Consts.UTF_8));

        try (CloseableHttpResponse response = this.getClosableHttpClient().execute(post)) {
        	bkeve eve = new bkeve();
        	try{
        		HttpEntity entity = response.getEntity();
        		
        		if (entity != null) {
        			
	        		String content = EntityUtils.toString(entity);
	        	//	System.out.println(content);
	                JSONObject json = new JSONObject(content);
	                
	                //Verification du code reponse
	                resp_code = json.getString("code");
	                
	                if(!resp_code.equalsIgnoreCase("200")) {
	                	return null;
	                }
	                
	                //Recuperation de l'objet renvoyé
	                JSONObject eveObject = json.getJSONObject("eve");
	                
	                //Conversion de l'objet en JsonArray
	                JSONArray eveArray = new JSONArray();
	                eveArray.put(eveObject);
	      
	    	    	eve = new bkeve();
    				// CONVERT JSON ARRAY to bkeve
    			
    				int n = eveArray.length();
    				for(int i=0 ; i< n ; i++) {
    					JSONObject jo = eveArray.getJSONObject(i);
    					eve = this.mapToBkeve(jo);
    				}
    			}
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        		return null;
        	}
            
            return eve;
        }

    }
	
	public boolean sendPostEOD(String url, List<String> bkeve) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		
		String _json = new Gson().toJson(bkeve);
		
		String playload = mapToJsonString(bkeve);
	//	System.out.println("\n"+_json);
		if(!isValidJson(playload)) {
			return false;
		}
        HttpPost post = new HttpPost(url+"/EODevents");

        // add request parameter, form parameters
        post.setHeader("content-type", "application/json");
		post.setEntity(new StringEntity(_json , Consts.UTF_8));
	
		try (CloseableHttpResponse response = this.getClosableHttpClient().execute(post)) {
        	
        	try{
        		HttpEntity entity = response.getEntity();
        		
        		if (entity != null) {
        			
	        		String content = EntityUtils.toString(entity);
	        		System.out.println(content); 
	        			
	                JSONObject json = new JSONObject(content);
	                playload = json.getString("code");
	    	    	//System.out.println("CODE REPONSE: "+playload);
    			}
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        	}
        	
        	return playload.equalsIgnoreCase("200");
      }
    }
	
	public bkeve _sendPostReverseEvent(String url, String bkeve) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		
		String resp_code;
		//System.out.println("EXECUTE REVERSE: "+url+"/reverseevent/"+bkeve);
        HttpPost post = new HttpPost(url+"/reverseevent/"+bkeve);
      
        post.setHeader("content-type", "application/json");

//		System.out.println("URI: "+post.getURI());
			 try (CloseableHttpResponse response = this.getClosableHttpClient().execute(post)) {
		        	bkeve eve = new bkeve();
		        	try{
		        		HttpEntity entity = response.getEntity();
		        		
		        		if (entity != null) {
		        			
			        		String content = EntityUtils.toString(entity);
			        		System.out.println(content); 
			                JSONObject json = new JSONObject(content);
			                
			                //Verification du code reponse
			                resp_code = json.getString("code");
			         //       System.out.println(resp_code); 
			                if(!resp_code.equalsIgnoreCase("200")) {
			                	return null;
			                }
			        		
			                JSONObject eveObject = json.getJSONObject("eve");
			                JSONArray eveArray = new JSONArray();
			                eveArray.put(eveObject);
			  
			    	    	eve = new bkeve();
		    				// CONVERT  JSON ARRAY to object bkeve
		    			
		    				int n = eveArray.length();
		    				for(int i=0 ; i< n ; i++) {
		    					JSONObject jo = eveArray.getJSONObject(i);
		    					eve = this.mapToBkeve(jo);
		    				}
		    				
		    		//		System.out.println("EVE LIST: "+eve);
		    			}
		        	}
		        	catch(Exception e) {
		        		e.printStackTrace();
		        		return null;
		        	}
		        	
		        	return eve;
        }
    }
	
   public bkeve sendPostReverseEvent(String baseUrl, String bkeve) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		
	   bkeve eve = null;
	   String resp_code;
		try (CloseableHttpClient client = HttpClientBuilder.create().useSystemProperties().build()) {
			java.net.URI uri = null;
			try {
				uri = new URIBuilder(baseUrl+"/reverseevent/"+bkeve)
						.build();
				
				HttpPost getRequest = new HttpPost(uri);
				
				CloseableHttpResponse resp = client.execute(getRequest);
				HttpEntity entity = resp.getEntity();
				if (entity != null) {
					String content = EntityUtils.toString(entity);
		    		//System.out.println(content); 
		    		JSONObject json = new JSONObject(content);
	                
	                //Verification du code reponse
	                resp_code = json.getString("code");
	              //  System.out.println(resp_code); 
	                if(!resp_code.equalsIgnoreCase("200")) {
	                	return null;
	                }
	        		
	                JSONObject eveObject = json.getJSONObject("eve");
	                JSONArray eveArray = new JSONArray();
	                eveArray.put(eveObject);
	  
	    	    	eve = new bkeve();
					// CONVERT  JSON ARRAY to object bkeve
				
					int n = eveArray.length();
					for(int i=0 ; i< n ; i++) {
						JSONObject jo = eveArray.getJSONObject(i);
						eve = this.mapToBkeve(jo);
					}
				}
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
			
			return eve;
		}
	}
	
	public bkeve getEvent(String url, String bkeve) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		
		String resp_code;
		HttpGet getRequest = new HttpGet(url+"/checkevent/"+bkeve);
		// add request parameter, form parameters
		getRequest.setHeader("content-type", "application/json");
		
		try (CloseableHttpResponse response = this.getClosableHttpClient().execute(getRequest)) {
        	bkeve eve = new bkeve();
        	HttpEntity entity = null;
        	
        	try{
        		entity = response.getEntity();
        		
        		if (entity != null) {
        			
	        		String content = EntityUtils.toString(entity);
	        	
	                JSONObject json = new JSONObject(content);
	                
	                //Verification du code reponse
	                resp_code = json.getString("code");
	                
	                if(!resp_code.equalsIgnoreCase("200")) {
	                	return null;
	                }
	                
	                JSONObject eveObject = json.getJSONObject("eve");
	                JSONArray eveArray = new JSONArray();
	                eveArray.put(eveObject);
	    	    	eve = new bkeve();
    				// CONVERT RESPONSE  JSON ARRAY
    			
    				int n = eveArray.length();
    				for(int i=0 ; i< n ; i++) {
    					JSONObject jo = eveArray.getJSONObject(i);
    					eve = this.mapToBkeve(jo);
    				}
    				
    				
    			}
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        		return null;
        	}
        	

			return eve;
        	//return eve;
		}
    }
	
	public Double getBalance(String url, String ncp) throws ClientProtocolException, IOException, 
		JSONException, URISyntaxException, DAOAPIException {
		
		String age = ncp.split("-")[0];
		String cli = ncp.split("-")[1];
		String resp_code;
		System.out.println("URL: "+url+"/findbalance/"+age+"/"+cli);
		HttpGet getRequest = new HttpGet(url+"/findbalance/"+age+"/"+cli);
		// add request parameter, form parameters
		getRequest.setHeader("content-type", "application/json");
		
		try (CloseableHttpResponse response = this.getClosableHttpClient().execute(getRequest)) {
        	Double solde = 0d;
        	HttpEntity entity = null;
        	
        	try{
        		entity = response.getEntity();
        		
        		if (entity != null) {
        			
	        		String content = EntityUtils.toString(entity);
	        	//	System.out.println(content);
	        		if(!isValidJson(content)) {
	        			return null;
	        		}
	                JSONObject json = new JSONObject(content);
	                
	                //Verification du code reponse
	                resp_code = json.getString("code");
	                
	                if(!resp_code.equalsIgnoreCase("200")) {
	                	return null;
	                }
	                
	                String retSrc = json.getString("data");
	            //    System.out.println(retSrc);
	                JSONObject jsonObj = new JSONObject(retSrc.toString());
	                
	                if(jsonObj.has("sin")) {
	                	solde = jsonObj.getDouble("sin");
	                }
	                else {
	                	solde = 0d;
	                }
	               
	               // System.out.println("Solde: "+solde);
	                
	                
//	                retSrc = retSrc.replaceAll("\\s", "");
//	                
//	                String bkcom[] = StringUtils.split(retSrc, ",");
//	                for(String s : bkcom) {
//	                	if(StringUtils.contains(s, "sin:")) {
//	                		//System.out.println("STR: "+s);
//	                		solde = Double.valueOf(StringUtils.substringAfterLast(s.trim(), ":"));
//	                		System.out.println("Solde: "+solde);
//	                		break;
//	                	}
//	                }
	    
	              //retSrc = retSrc.replaceAll(":,", ":\"\",");
	          //    retSrc = retSrc.replaceAll(":-", "&");
//	                retSrc = retSrc.replaceAll("-", "");
	     
	                
	              //NumberUtils.createDouble(StringUtils.substringAfterLast(bkcom[49].trim(), ":"));
	              //  solde = Double.valueOf(StringUtils.substringAfterLast(bkcom[49].trim(), ":"));
	      
	                
//	                if(retSrc.startsWith("&")) retSrc.replace("&", "-");
////	            JSONObject jsonObj = new JSONObject(retSrc.toString());
//	                ObjectMapper mapper = new ObjectMapper();
//	                mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//	                String str = mapper.readValue(retSrc, String.class);
//	                System.out.println("STR: "+str);

////	            solde = jsonObj.getDouble("sin");
	           
    			}
        	}
        	catch(Exception e) {
       // 	catch(NumberFormatException | NullPointerException | JSONException e) {
        		//System.err.println(e.getMessage());
        		e.printStackTrace();
        		solde = 0d;
        		return solde;
        	}
        	
			return solde;
		}
	}
	
	public boolean sendAccountings(String url, List<bkmvti> bkmvti) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		
		//String _json = new Gson().toJson(bkmvti);
		String playload = mapToJsonString(bkmvti);
		String resp_code = "";
		if(!isValidJson(playload)) {
			return false;
		}
        HttpPost post = new HttpPost(url+"/accountingentries");

        // add request parameter, form parameters
        post.setHeader("content-type", "application/json");
     //   System.out.println("URI: "+post.getURI()+"\n"+_json);
	//	post.setEntity(new StringEntity(_json));
		
		post.setEntity(new StringEntity(playload , Consts.UTF_8));

		try (CloseableHttpResponse response = this.getClosableHttpClient().execute(post)) {
        	
        	try{
        		HttpEntity entity = response.getEntity();
        		
        		if (entity != null) {
        			
	        		String content = EntityUtils.toString(entity);
	        		System.out.println(content); 
	        			
	                JSONObject json = new JSONObject(content);
	                resp_code = json.getString("code");
    			}
        	}
        	catch(Exception e) {
        		e.printStackTrace();
        	}
        	
        	return resp_code.equalsIgnoreCase("200");
      }

    }
	
	public String mapToJsonString(Object obj) throws JsonGenerationException, JsonMappingException, IOException, JSONException{
	 //	ObjectMapper mapper = new ObjectMapper();
	 	//mapper.setSerializationInclusion(Include.NON_NULL);
		String map = null;
		try {
		     map = this.mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		String encoded = this.encodeJson(map);
		return map;
	}
	
	public bkeve mapToBkeve(JSONObject obj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		bkeve o = this.mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(obj.toString(), bkeve.class);
		return o;
	}
	
	private boolean isJSONValid(String jsonInString ) {
	    try {
	      // final ObjectMapper mapper = new ObjectMapper();
	       this.mapper.readTree(jsonInString);
	       return true;
	    } catch (IOException e) {
	       return false;
	    }
	}
	
	private Boolean isValidJson(String maybeJson){
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(maybeJson);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
	
	public String sendSimpleSMS(String baseUrl, String message, String phone) throws ClientProtocolException, IOException, URISyntaxException{
		
		try (CloseableHttpClient client = HttpClientBuilder.create().useSystemProperties().build()) {
			java.net.URI uri = null;
			try {
				uri = new URIBuilder(baseUrl)
						.addParameter("message", message)
						.addParameter("phone", phone)
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
	
	public String sendSimpleMAIL(String baseUrl, String message, String phone) throws ClientProtocolException, IOException, URISyntaxException{
		return null;
	}
}
