package com.afb.dpd.mobilemoney.dao.api;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jettison.json.JSONException;

import com.afb.dpd.mobilemoney.dao.api.exception.DAOAPIException;
import com.afb.dpd.mobilemoney.jpa.tools.ClientProduit;

public class ClientProduitDAO extends AbstractDAOAPI<ClientProduit> {
	
//	private final static String URL_Res = "/packageproduct/rest/package/resiliations";
	//private final static String URL_Sta = "/packageproduct/rest/package/statusabon";
	//private final static String URL_Abon = "/packageproduct/rest/package/abonnements";

	public ClientProduitDAO() {
		super(ClientProduit.class);
	}

	@Override
	public String getUrl() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<ClientProduit> resiliations(String host, String protocole, String port, String url, String module) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException{
		Map<String  , String> filter = new HashMap<>();
		filter.put("module" , module);
		url = url + "/resiliations";
		return this.filter(filter, host, protocole, port, url, false);
		
	}
	
	public List<ClientProduit> abonnements(String host, String protocole, String port, String url, String module) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException{
		Map<String  , String> filter = new HashMap<>();
		filter.put("module" , module);
		url = url + "/abonnements";
		return this.filter(filter , host, protocole, port, url, false);
		
	}
	
	public String statusAbon(String host, String protocole, String port, String url, ClientProduit client) throws ClientProtocolException, IOException, JSONException, URISyntaxException, DAOAPIException{
		Map<String  , String> filter = new HashMap<>();
		filter.put("module" , client.getProduit());
		filter.put("matricule", client.getMatricule());
		
		url = url + "/statusabon";
		return this.get(filter, host, protocole, port, url, false);
		
	}
}
