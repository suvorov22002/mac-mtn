package com.afb.dpd.mobilemoney.jpa.dto;

import java.io.IOException;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.afb.dpd.mobilemoney.jpa.dto.Account;
import com.afb.dpd.mobilemoney.jpa.dto.Client;
import com.afb.dpd.mobilemoney.jpa.dto.ResponseData;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Shared {
	
	private static final int TIMEOUT = 100;
	private static final ObjectMapper mapper = new ObjectMapper();
	private static JsonStringEncoder jsonEncoder;
	
	
	public static CloseableHttpClient getClosableHttpClient() {
		
		RequestConfig config = RequestConfig.custom()
		  .setConnectTimeout(TIMEOUT * 20000)
		  .setConnectionRequestTimeout(TIMEOUT * 20000)
		  .setSocketTimeout(TIMEOUT * 20000).build();
		CloseableHttpClient client = 
		  HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		
		return client;
	}
	
	public static boolean isJSONValid(String jsonInString ) {
	    try {
	       mapper.readTree(jsonInString);
	       return true;
	    } catch (IOException e) {
	       return false;
	    }
	}
	
	private static String encodeJson(String jsonString) {
		return new String(jsonEncoder.quoteAsString(jsonString));
	}
	
	public static String mapToJsonString(Object obj) throws JsonGenerationException, JsonMappingException, IOException, JSONException{
		     ObjectMapper mapper = new ObjectMapper();
			String map = null;
			try {
			     map = mapper.writeValueAsString(obj);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
			return map;
	}
	
	public static String removeWhitespaces(String json) {
	    boolean quoted = false;

	    StringBuilder builder = new StringBuilder();

	    int len = json.length();
	    for (int i = 0; i < len; i++) {
	        char c = json.charAt(i);
	        if (c == '\"')
	            quoted = !quoted;

	        if (quoted || !Character.isWhitespace(c))
	            builder.append(c);
	    }

	    return builder.toString();
	}
	
	public static ResponseData mapToObject(JSONObject obj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		ResponseData o = mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(obj.toString(), ResponseData.class);
		return o;
	}
	
	public static String mapToString(JSONObject obj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		String o = mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(obj.toString(), String.class);
		return o;
	}
	
	public static Client mapToClient(JSONObject obj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		Client o = mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(obj.toString(), Client.class);
		return o;
	}
	
	public static Account mapToAccount(JSONObject obj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		Account o = mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(obj.toString(), Account.class);
		return o;
	}
	
	public static ResponseDataClient mapToResponseDataClient(JSONObject obj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		ResponseDataClient o = mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(obj.toString(), ResponseDataClient.class);
		return o;
	}
	
	public static ResponseDataAccount mapToResponseDataAccount(JSONObject obj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		ResponseDataAccount o = mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(obj.toString(), ResponseDataAccount.class);
		return o;
	}
	
	public static ResponseDataHis mapToResponseDataHis(JSONObject obj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		ResponseDataHis o = mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(obj.toString(), ResponseDataHis.class);
		return o;
	}
	
	public static ResponseDataBkeve mapToResponseDataBkeve(JSONObject obj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		ResponseDataBkeve o = mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(obj.toString(), ResponseDataBkeve.class);
		return o;
	}
	
	public static ResponseDataBkcom mapToResponseDataBkcom(JSONObject obj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		ResponseDataBkcom o = mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(obj.toString(), ResponseDataBkcom.class);
		return o;
	}
	
	public static ResponseBkcom mapToResponseBkcom(JSONObject obj) throws JsonParseException, JsonMappingException, IOException, JSONException {
		ResponseBkcom o = mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY).readValue(obj.toString(), ResponseBkcom.class);
		return o;
	}
}
