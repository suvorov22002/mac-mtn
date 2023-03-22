/**
 * 
 */
package com.afb.dpd.mobilemoney.jsf.dto;


/**
 * @author AFB
 *
 */

public class Information {
	
	/**
	 * Mandatory
	 * The firstname
	 */
	private String firstname;
		
	/**
	 * Optional
	 * The surname
	 */
	private String surname;
	
	/**
	 * Mandatory
	 * The gender
	 */
	private String gender;
	
	/**
	 * Mandatory
	 * The first language
	 */
	private String language;
		
	/**
	 * Mandatory
	 * The date of birth
	 */
	private String date;
	
	/**
	 * Mandatory
	 * The city of birth
	 */
	private String city;
	
	/**
	 * Optional
	 * The region of birth
	 */
	private String region;
	
	/**
	 * Mandatory
	 * The country of birth
	 */
	private String country;
	
	/**
	 * Optional
	 * The profession
	 */
	private String profession;
	
	/**
	 * Optional
	 * The residencial status
	 */
	private String residencialstatus;
	
	
	/**
	 * 
	 */
	public Information() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param firstname
	 * @param surname
	 * @param gender
	 * @param language
	 * @param date
	 * @param city
	 * @param region
	 * @param country
	 * @param profession
	 * @param residencialstatus
	 */
	public Information(String firstname, String surname, String gender, String language, String date, String city,
			String region, String country, String profession, String residencialstatus) {
		super();
		this.firstname = firstname;
		this.surname = surname;
		this.gender = gender;
		this.language = language;
		this.date = date;
		this.city = city;
		this.region = region;
		this.country = country;
		this.profession = profession;
		this.residencialstatus = residencialstatus;
	}


	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}


	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}


	/**
	 * @return the surname
	 */
	public String getSurname() {
		return surname;
	}


	/**
	 * @param surname the surname to set
	 */
	public void setSurname(String surname) {
		this.surname = surname;
	}


	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}


	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}


	/**
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}


	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}


	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}


	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}


	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}


	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}


	/**
	 * @return the region
	 */
	public String getRegion() {
		return region;
	}


	/**
	 * @param region the region to set
	 */
	public void setRegion(String region) {
		this.region = region;
	}


	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}


	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}


	/**
	 * @return the profession
	 */
	public String getProfession() {
		return profession;
	}


	/**
	 * @param profession the profession to set
	 */
	public void setProfession(String profession) {
		this.profession = profession;
	}


	/**
	 * @return the residencialstatus
	 */
	public String getResidencialstatus() {
		return residencialstatus;
	}


	/**
	 * @param residencialstatus the residencialstatus to set
	 */
	public void setResidencialstatus(String residencialstatus) {
		this.residencialstatus = residencialstatus;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Information [firstname=" + firstname + ", surname=" + surname + ", gender=" + gender + ", language="
				+ language + ", date=" + date + ", city=" + city + ", region=" + region + ", country=" + country
				+ ", profession=" + profession + ", residencialstatus=" + residencialstatus + "]";
	}
	

}
