package com.afb.dpd.mobilemoney.jpa.dto;

import java.util.List;

import com.afb.dpd.mobilemoney.jpa.tools.bkeve;

public class ResponseDataBkeve extends ResponseBase{
	
	private static final long serialVersionUID = 1L;	
	private List<bkeve> data;
	
	public ResponseDataBkeve() {
		super();
	}
	
	

	


	/**
	 * @return the data
	 */
	public List<bkeve> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<bkeve> data) {
		this.data = data;
	}
	
	
	
}
