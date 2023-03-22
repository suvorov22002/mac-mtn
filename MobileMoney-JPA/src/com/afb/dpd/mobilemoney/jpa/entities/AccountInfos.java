/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.entities;

import java.util.Date;

import com.afb.dpd.mobilemoney.jpa.tools.MoMoHelper;
import com.afb.dpd.mobilemoney.jpa.tools.bkmvti;

/**
 * @author AFB
 *
 */
public class AccountInfos {

	private String age;
	
	private String ncp;
	
	private String clc;
	
	private String dev;
	
	private String cha;
	
	private String suf;
	
	private Date dva;

		
	
	/**
	 * @param age
	 * @param ncp
	 * @param clc
	 * @param dev
	 * @param cha
	 * @param suf
	 * @param dva
	 */
	public AccountInfos(String age, String ncp, String clc, String dev, String cha, String suf, Date dva) {
		super();
		this.age = age;
		this.ncp = ncp;
		this.clc = clc;
		this.dev = dev;
		this.cha = cha;
		this.suf = suf;
		this.dva = dva;
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
	 * @return the clc
	 */
	public String getClc() {
		return clc;
	}



	/**
	 * @param clc the clc to set
	 */
	public void setClc(String clc) {
		this.clc = clc;
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
	 * @return the cha
	 */
	public String getCha() {
		return cha;
	}



	/**
	 * @param cha the cha to set
	 */
	public void setCha(String cha) {
		this.cha = cha;
	}



	/**
	 * @return the suf
	 */
	public String getSuf() {
		return suf;
	}



	/**
	 * @param suf the suf to set
	 */
	public void setSuf(String suf) {
		this.suf = suf;
	}



	/**
	 * @return the dva
	 */
	public Date getDva() {
		return dva;
	}



	/**
	 * @param dva the dva to set
	 */
	public void setDva(Date dva) {
		this.dva = dva;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AccountInfos [age=" + age + ", ncp=" + ncp + ", clc=" + clc + ", dev=" + dev + ", cha=" + cha + ", suf="
				+ suf + ", dva=" + dva + "]";
	}	
	
}
