package com.afb.dpi.momo.services;

public class MomoKYCServiceProxy implements com.afb.dpi.momo.services.MomoKYCService {
  private String _endpoint = null;
  private com.afb.dpi.momo.services.MomoKYCService momoKYCService = null;
  
  public MomoKYCServiceProxy() {
    _initMomoKYCServiceProxy();
  }
  
  public MomoKYCServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initMomoKYCServiceProxy();
  }
  
  private void _initMomoKYCServiceProxy() {
    try {
      momoKYCService = (new com.afb.dpi.momo.services.MomoKYCServiceServiceLocator()).getMomoKYCServicePort();
      if (momoKYCService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)momoKYCService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)momoKYCService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (momoKYCService != null)
      ((javax.xml.rpc.Stub)momoKYCService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.afb.dpi.momo.services.MomoKYCService getMomoKYCService() {
    if (momoKYCService == null)
      _initMomoKYCServiceProxy();
    return momoKYCService;
  }
  
  public java.lang.String getTransactionStatus(java.lang.String transactionid, java.lang.String identity, java.lang.String version) throws java.rmi.RemoteException{
    if (momoKYCService == null)
      _initMomoKYCServiceProxy();
    return momoKYCService.getTransactionStatus(transactionid, identity, version);
  }
  
  public java.lang.String getAccountHolderPersonalInformation(java.lang.String identity, java.lang.String version) throws java.rmi.RemoteException{
    if (momoKYCService == null)
      _initMomoKYCServiceProxy();
    return momoKYCService.getAccountHolderPersonalInformation(identity, version);
  }
  
  public java.lang.String linkFinancialResourceInformation(java.lang.String fri, java.lang.String accountholder, java.lang.String version) throws java.rmi.RemoteException{
    if (momoKYCService == null)
      _initMomoKYCServiceProxy();
    return momoKYCService.linkFinancialResourceInformation(fri, accountholder, version);
  }
  
  public java.lang.String unlinkFinancialResourceInformation(java.lang.String fri, java.lang.String accountholder, java.lang.String version) throws java.rmi.RemoteException{
    if (momoKYCService == null)
      _initMomoKYCServiceProxy();
    return momoKYCService.unlinkFinancialResourceInformation(fri, accountholder, version);
  }
  
  public java.lang.String getAccountHolderIdentification(java.lang.String identity, java.lang.String version) throws java.rmi.RemoteException{
    if (momoKYCService == null)
      _initMomoKYCServiceProxy();
    return momoKYCService.getAccountHolderIdentification(identity, version);
  }
  
  
}