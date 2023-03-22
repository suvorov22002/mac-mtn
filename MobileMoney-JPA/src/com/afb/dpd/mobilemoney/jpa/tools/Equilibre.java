/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.tools;

import java.io.Serializable;
import java.util.Date;

import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

/**
 * @author Francis DJIOMOU
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Equilibre implements Serializable {

	private Date dco;
	private String uti;
	private String ope;
	private String sen;
	private Integer nbre;
	private Double total;
	/**
	 * 
	 */
	public Equilibre() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param dco
	 * @param uti
	 * @param ope
	 * @param sen
	 * @param nbre
	 * @param total
	 */
	public Equilibre(Date dco, String uti, String ope, String sen, Integer nbre, Double total) {
		super();
		this.dco = dco;
		this.uti = uti;
		this.ope = ope;
		this.sen = sen;
		this.nbre = nbre;
		this.total = total;
	}
	/**
	 * @return the dco
	 */
	public Date getDco() {
		return dco;
	}
	public String getFormattedDco(){
		return PortalHelper.DEFAULT_DATE_FORMAT.format(dco);
	}

	public String getFormattedMon() {
		return MoMoHelper.espacement(total);
	}

	/**
	 * @param dco the dco to set
	 */
	public void setDco(Date dco) {
		this.dco = dco;
	}
	/**
	 * @return the uti
	 */
	public String getUti() {
		return uti;
	}
	/**
	 * @param uti the uti to set
	 */
	public void setUti(String uti) {
		this.uti = uti;
	}
	/**
	 * @return the ope
	 */
	public String getOpe() {
		return ope;
	}
	/**
	 * @param ope the ope to set
	 */
	public void setOpe(String ope) {
		this.ope = ope;
	}
	/**
	 * @return the sen
	 */
	public String getSen() {
		return sen;
	}
	/**
	 * @param sen the sen to set
	 */
	public void setSen(String sen) {
		this.sen = sen;
	}
	/**
	 * @return the nbre
	 */
	public Integer getNbre() {
		return nbre;
	}
	/**
	 * @param nbre the nbre to set
	 */
	public void setNbre(Integer nbre) {
		this.nbre = nbre;
	}
	/**
	 * @return the total
	 */
	public Double getTotal() {
		return total;
	}
	/**
	 * @param total the total to set
	 */
	public void setTotal(Double total) {
		this.total = total;
	}
	
}
