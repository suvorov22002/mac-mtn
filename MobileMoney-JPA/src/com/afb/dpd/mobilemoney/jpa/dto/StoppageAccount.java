package com.afb.dpd.mobilemoney.jpa.dto;

import java.util.Date;

public class StoppageAccount {
	
	private String stoppageType;
    private String code;
    private String designation;
    private String stoppageReason;
    private String stoppageEndReason;
    private Date startDate;
    private Date endDate;
    private Date stoppageEndDate;
    private String status;
    private String stoppageEndUser;
    private String creationUser;
	/**
	 * 
	 */
	public StoppageAccount() {
		super();
	}
	/**
	 * @return the stoppageType
	 */
	public String getStoppageType() {
		return stoppageType;
	}
	/**
	 * @param stoppageType the stoppageType to set
	 */
	public void setStoppageType(String stoppageType) {
		this.stoppageType = stoppageType;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}
	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}
	/**
	 * @return the stoppageReason
	 */
	public String getStoppageReason() {
		return stoppageReason;
	}
	/**
	 * @param stoppageReason the stoppageReason to set
	 */
	public void setStoppageReason(String stoppageReason) {
		this.stoppageReason = stoppageReason;
	}
	/**
	 * @return the stoppageEndReason
	 */
	public String getStoppageEndReason() {
		return stoppageEndReason;
	}
	/**
	 * @param stoppageEndReason the stoppageEndReason to set
	 */
	public void setStoppageEndReason(String stoppageEndReason) {
		this.stoppageEndReason = stoppageEndReason;
	}
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the stoppageEndDate
	 */
	public Date getStoppageEndDate() {
		return stoppageEndDate;
	}
	/**
	 * @param stoppageEndDate the stoppageEndDate to set
	 */
	public void setStoppageEndDate(Date stoppageEndDate) {
		this.stoppageEndDate = stoppageEndDate;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the stoppageEndUser
	 */
	public String getStoppageEndUser() {
		return stoppageEndUser;
	}
	/**
	 * @param stoppageEndUser the stoppageEndUser to set
	 */
	public void setStoppageEndUser(String stoppageEndUser) {
		this.stoppageEndUser = stoppageEndUser;
	}
	/**
	 * @return the creationUser
	 */
	public String getCreationUser() {
		return creationUser;
	}
	/**
	 * @param creationUser the creationUser to set
	 */
	public void setCreationUser(String creationUser) {
		this.creationUser = creationUser;
	}

}
