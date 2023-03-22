package com.afb.dpd.mobilemoney.jpa.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResponseDataAccount extends ResponseBase{
	
	@JsonIgnore
	private static final long serialVersionUID = 1L;
	
	private List<Account> datas = new ArrayList<Account>();
	
	public ResponseDataAccount() {
		super();
	}

	/**
	 * @return the datas
	 */
	public List<Account> getDatas() {
		return datas;
	}

	/**
	 * @param datas the datas to set
	 */
	public void setDatas(List<Account> datas) {
		this.datas = datas;
	}
	
}
