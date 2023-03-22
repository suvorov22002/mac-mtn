package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Map;

import org.json.JSONTokener;

public class JSONObject extends org.json.JSONObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JSONObject() {
		super();
		// TODO Auto-generated constructor stub
	}

	public JSONObject(JSONTokener x) throws ParseException {
		super(x);
		// TODO Auto-generated constructor stub
	}

	public JSONObject(Map map) {
		super(map);
		// TODO Auto-generated constructor stub
	}

	public JSONObject(String string) throws ParseException {
		super(string);
		// TODO Auto-generated constructor stub
	}
	

}
