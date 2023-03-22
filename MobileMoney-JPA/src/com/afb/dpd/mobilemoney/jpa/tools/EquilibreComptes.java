/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.tools;

import java.io.Serializable;
import java.util.Date;

import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

/**
 * @author Alex JAZA
 * @version 1.0
 */
@SuppressWarnings("serial")
public class EquilibreComptes implements Serializable {
	
	private String typeCpte;
	private Integer nbrePull;
	private Double totalPull;
	private Integer nbrePush;
	private Double totalPush;
	
	
	/**
	 * 
	 */
	public EquilibreComptes() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param typeCpte
	 * @param nbrePull
	 * @param totalPull
	 * @param nbrePush
	 * @param totalPush
	 */
	public EquilibreComptes(String typeCpte,Integer nbrePull, Double totalPull, Integer nbrePush, Double totalPush) {
		super();
		this.typeCpte = typeCpte;
		this.nbrePull = nbrePull;
		this.totalPull = totalPull;
		this.nbrePush = nbrePush;
		this.totalPush = totalPush;
	}

	
	/**
	 * @return the typeCpte
	 */
	public String getTypeCpte() {
		return typeCpte;
	}


	/**
	 * @param typeCpte the typeCpte to set
	 */
	public void setTypeCpte(String typeCpte) {
		this.typeCpte = typeCpte;
	}


	/**
	 * @return the nbrePull
	 */
	public Integer getNbrePull() {
		return nbrePull;
	}


	/**
	 * @param nbrePull the nbrePull to set
	 */
	public void setNbrePull(Integer nbrePull) {
		this.nbrePull = nbrePull;
	}


	/**
	 * @return the totalPull
	 */
	public Double getTotalPull() {
		return totalPull;
	}


	/**
	 * @param totalPull the totalPull to set
	 */
	public void setTotalPull(Double totalPull) {
		this.totalPull = totalPull;
	}


	/**
	 * @return the nbrePush
	 */
	public Integer getNbrePush() {
		return nbrePush;
	}


	/**
	 * @param nbrePush the nbrePush to set
	 */
	public void setNbrePush(Integer nbrePush) {
		this.nbrePush = nbrePush;
	}


	/**
	 * @return the totalPush
	 */
	public Double getTotalPush() {
		return totalPush;
	}


	/**
	 * @param totalPush the totalPush to set
	 */
	public void setTotalPush(Double totalPush) {
		this.totalPush = totalPush;
	}
	
	
	public String getFormattedMonPull() {
		return MoMoHelper.espacement(totalPull);
	}

	
	public String getFormattedMonPush() {
		return MoMoHelper.espacement(totalPush);
	}
	
}
