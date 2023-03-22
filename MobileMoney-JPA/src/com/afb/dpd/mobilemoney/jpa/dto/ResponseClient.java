package com.afb.dpd.mobilemoney.jpa.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResponseClient extends ResponseBase{
	
	/**
	 * 
	 */
	@JsonIgnore
	private static final long serialVersionUID = 1L;
	private List<Client> datas = new ArrayList<Client>();
	
	
	/**
	 * 
	 */
	public ResponseClient() {
		super();
	}


	/**
	 * @return the datas
	 */
	public List<Client> getDatas() {
		return datas;
	}


	/**
	 * @param datas the datas to set
	 */
	public void setDatas(List<Client> datas) {
		this.datas = datas;
	}
	
}
