package com.afb.dpd.mobilemoney.jpa.dto;

import java.util.List;

import com.afb.dpd.mobilemoney.jpa.tools.Bkcom;
import com.afb.dpd.mobilemoney.jpa.tools.Bkhis;

public class ResponseDataBkcom extends ResponseBase{
	
	private static final long serialVersionUID = 1L;	
	private List<Bkcom> data;

	/**
	 * 
	 */
	public ResponseDataBkcom() {
		super();
	}

	/**
	 * @return the data
	 */
	public List<Bkcom> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<Bkcom> data) {
		this.data = data;
	}
	
	

}
