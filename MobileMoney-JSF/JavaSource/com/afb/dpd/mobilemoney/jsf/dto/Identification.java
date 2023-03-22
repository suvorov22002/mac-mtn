/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.dto;


/**
 * @author AFB
 *
 */

public class Identification {
	
	/**
	 * Mandatory
	 * The type of identification
	 */
	private String idtype;
		
	/**
	 * Mandatory
	 * The id
	 */
	private String id;
	
		
	/**
	 * 
	 */
	public Identification() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param idtype
	 * @param id
	 */
	public Identification(String idtype, String id) {
		super();
		this.idtype = idtype;
		this.id = id;
	}


	/**
	 * @return the idtype
	 */
	public String getIdtype() {
		return idtype;
	}


	/**
	 * @param idtype the idtype to set
	 */
	public void setIdtype(String idtype) {
		this.idtype = idtype;
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Identification [idtype=" + idtype + ", id=" + id + "]";
	}
	

}
