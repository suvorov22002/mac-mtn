/**
 * 
 */
package com.afb.dpd.mobilemoney.jpa.entities;

/**
 * @author AFB
 *
 */
public class AccountUtils {

	private String age;
	
	private String ncp;
	
	private String cle;
	
	private String ife;
	
	private String cfe;
	
	private Double sin;
	
		
	/**
	 * @param age
	 * @param ncp
	 * @param cle
	 * @param ife
	 * @param cfe
	 * @param sin
	 */
	public AccountUtils(String age, String ncp, String cle, String ife, String cfe, Double sin) {
		super();
		this.age = age;
		this.ncp = ncp;
		this.cle = cle;
		this.ife = ife;
		this.cfe = cfe;
		this.sin = sin;
	}


	public String getAge() {
		return age;
	}
	
	
	public void setAge(String age) {
		this.age = age;
	}
	
	
	public String getNcp() {
		return ncp;
	}
	
	
	public void setNcp(String ncp) {
		this.ncp = ncp;
	}
	
	
	public String getCle() {
		return cle;
	}
	
	
	public void setCle(String cle) {
		this.cle = cle;
	}


	public String getIfe() {
		return ife;
	}


	public void setIfe(String ife) {
		this.ife = ife;
	}


	public String getCfe() {
		return cfe;
	}


	public void setCfe(String cfe) {
		this.cfe = cfe;
	}


	public Double getSin() {
		return sin;
	}


	public void setSin(Double sin) {
		this.sin = sin;
	}


	@Override
	public String toString() {
		return "AccountUtils [age=" + age + ", ncp=" + ncp + ", cle=" + cle + ", ife=" + ife + ", cfe=" + cfe + ", sin="
				+ sin + "]";
	}
	
	
}
