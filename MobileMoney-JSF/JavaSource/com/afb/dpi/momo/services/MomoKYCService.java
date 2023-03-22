/**
 * MomoKYCService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.afb.dpi.momo.services;

public interface MomoKYCService extends java.rmi.Remote {
    public java.lang.String getAccountHolderIdentification(java.lang.String identity, java.lang.String version) throws java.rmi.RemoteException;
    public java.lang.String getTransactionStatus(java.lang.String transactionid, java.lang.String identity, java.lang.String version) throws java.rmi.RemoteException;
    public java.lang.String getAccountHolderPersonalInformation(java.lang.String identity, java.lang.String version) throws java.rmi.RemoteException;
    public java.lang.String linkFinancialResourceInformation(java.lang.String fri, java.lang.String accountholder, java.lang.String version) throws java.rmi.RemoteException;
    public java.lang.String unlinkFinancialResourceInformation(java.lang.String fri, java.lang.String accountholder, java.lang.String version) throws java.rmi.RemoteException;
}
