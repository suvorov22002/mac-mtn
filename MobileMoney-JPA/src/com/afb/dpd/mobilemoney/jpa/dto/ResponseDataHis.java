package com.afb.dpd.mobilemoney.jpa.dto;

import java.util.List;

import com.afb.dpd.mobilemoney.jpa.tools.Bkhis;

public class ResponseDataHis extends ResponseBase {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;	
	private List<Bkhis> data;

	/**
	 * 
	 */
	public ResponseDataHis() {
		super();
	}


	public static ResponseDataHis getIntance(){
		return new ResponseDataHis();
	}
	
	

	public ResponseDataHis event(List<Bkhis> data) {
		this.setData(data);
		return this;
	}
	
	public ResponseDataHis error(String msg) {
		this.setCode("200");
		this.setMessage("FAIL");
		this.setError(msg);
		return this;
	}
	
	public ResponseDataHis sucess(String msg) {
		this.setCode("000");
		this.setMessage("SUCCESS");
		this.setError(msg);
		return this;
	}
	
	

	/**
	 * @return the data
	 */
	public List<Bkhis> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<Bkhis> data) {
		this.data = data;
	}

	
	


}