package com.afb.dpd.mobilemoney.jsf.tools;

import java.io.Serializable;

public class SelectData implements Serializable {

	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;
	
	private Boolean checked = Boolean.FALSE;
	
	private String value = "";
	
	public SelectData(){}

	/**
	 * @param selected
	 * @param value
	 */
	public SelectData(Boolean selected, String value) {
		super();
		this.checked = selected;
		this.value = value;
	}

	/**
	 * @param value
	 */
	public SelectData(String value) {
		super();
		this.value = value;
	}

	/**
	 * @return the checked
	 */
	public Boolean getChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return value;
	}
	
}
