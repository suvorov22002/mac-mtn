package com.afb.dpd.mobilemoney.jpa.dto;

import com.afb.dpd.mobilemoney.jpa.tools.Bkcom;

public class ResponseBkcom extends ResponseBase{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Bkcom data;
	
	public ResponseBkcom() {
		super();
	}

	/**
	 * @return the data
	 */
	public Bkcom getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Bkcom data) {
		this.data = data;
	}
	
}
