package com.afb.dpd.mobilemoney.jpa.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ResponseData<T> extends ResponseBase {
	
	@JsonIgnore
	private static final long serialVersionUID = 1L;

	
	private List<T> datas = new ArrayList<T>();
	
	
	/**
	 * 
	 */
	public ResponseData() {
		super();
	}


	/**
	 * @return the datas
	 */
	public List<T> getDatas() {
		return datas;
	}


	/**
	 * @param datas the datas to set
	 */
	public void setDatas(List<T> datas) {
		this.datas = datas;
	}
}
