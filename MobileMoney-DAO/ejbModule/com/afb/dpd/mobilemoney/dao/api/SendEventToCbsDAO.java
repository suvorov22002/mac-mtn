package com.afb.dpd.mobilemoney.dao.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jettison.json.JSONException;

import com.afb.dpd.mobilemoney.dao.api.exception.DAOAPIException;
import com.afb.dpd.mobilemoney.jpa.tools.bkeve;
import com.afb.dpd.mobilemoney.jpa.tools.bkmvti;

public class SendEventToCbsDAO extends AbstractDAOAPI<SendEventToCbsDAO>{
	
	public SendEventToCbsDAO() {
		super(SendEventToCbsDAO.class);
	}

	@Override
	public String getUrl() {
		return null;
	}
	
	public bkeve sendEventToCoreBanking(String url, bkeve eve) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		return this.sendPostEvent(url, eve);
	}
	
	public boolean sendEventToCoreBankingEOD(String url, List<String> eve) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		return this.sendPostEOD(url, eve);
	}
	
	public boolean sendEntriesToCoreBanking(String url, List<bkmvti> bkmvti) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		return this.sendAccountings(url, bkmvti);
	}
	
	public bkeve reverseEventFromCoreBanking(String url, String bkeve) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		return this.sendPostReverseEvent(url, bkeve);
	}
	
	public bkeve checkEventToCoreBanking(String url, String bkeve) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		return this.getEvent(url, bkeve);
	}
	
	public Double getSoldeToCoreBanking(String url, String ncp) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException {
		return this.getBalance(url, ncp);
	}
	
	public String sendSms(String url, String sms, String phone) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException{
		return this.sendSimpleSMS(url, sms, phone);
	}
}
