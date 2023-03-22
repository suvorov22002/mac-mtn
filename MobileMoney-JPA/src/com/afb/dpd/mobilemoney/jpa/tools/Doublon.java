/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.tools;

import java.io.Serializable;

/**
 * Classe de controle de doublons des ecritures comptables inseres dans le CoreBanking
 * @author Francis DJIOMOU
 * @version 1.0
 */
@SuppressWarnings("serial")
public class Doublon implements Serializable {

	private String age;
	private String dev; 
	private String ncp; 
	private String ope;
	private String eve; 
	private String pie;
	private String lib;
	private Double mon;
	private String sen;
	private Integer nbre;
	
	/**
	 * 
	 */
	public Doublon() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param age
	 * @param dev
	 * @param ncp
	 * @param ope
	 * @param eve
	 * @param pie
	 * @param lib
	 * @param mon
	 * @param sen
	 * @param nbre
	 */
	public Doublon(String age, String dev, String ncp, String ope, String eve, String pie, String lib, Double mon,
			String sen, Integer nbre) {
		super();
		this.age = age;
		this.dev = dev;
		this.ncp = ncp;
		this.ope = ope;
		this.eve = eve;
		this.pie = pie;
		this.lib = lib;
		this.mon = mon;
		this.sen = sen;
		this.nbre = nbre;
	}
	/**
	 * @return the age
	 */
	public String getAge() {
		return age;
	}
	/**
	 * @param age the age to set
	 */
	public void setAge(String age) {
		this.age = age;
	}
	/**
	 * @return the dev
	 */
	public String getDev() {
		return dev;
	}
	/**
	 * @param dev the dev to set
	 */
	public void setDev(String dev) {
		this.dev = dev;
	}
	/**
	 * @return the ncp
	 */
	public String getNcp() {
		return ncp;
	}
	/**
	 * @param ncp the ncp to set
	 */
	public void setNcp(String ncp) {
		this.ncp = ncp;
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
	 * @return the eve
	 */
	public String getEve() {
		return eve;
	}
	/**
	 * @param eve the eve to set
	 */
	public void setEve(String eve) {
		this.eve = eve;
	}
	/**
	 * @return the pie
	 */
	public String getPie() {
		return pie;
	}
	/**
	 * @param pie the pie to set
	 */
	public void setPie(String pie) {
		this.pie = pie;
	}
	/**
	 * @return the lib
	 */
	public String getLib() {
		return lib;
	}
	/**
	 * @param lib the lib to set
	 */
	public void setLib(String lib) {
		this.lib = lib;
	}
	/**
	 * @return the mon
	 */
	public Double getMon() {
		return mon;
	}
	/**
	 * @param mon the mon to set
	 */
	public void setMon(Double mon) {
		this.mon = mon;
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
	
}
