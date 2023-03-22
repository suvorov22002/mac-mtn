package com.afb.dpd.mobilemoney.jpa.tools;

public class Queries {
	
	private String query;
	private Object[] params;
	/**
	 * @param query
	 * @param params
	 */
	public Queries(String query, Object[] params) {
		super();
		this.query = query;
		this.params = params;
	}
	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(String query) {
		this.query = query;
	}
	/**
	 * @return the params
	 */
	public Object[] getParams() {
		return params;
	}
	/**
	 * @param params the params to set
	 */
	public void setParams(Object[] params) {
		this.params = params;
	}
	
}
