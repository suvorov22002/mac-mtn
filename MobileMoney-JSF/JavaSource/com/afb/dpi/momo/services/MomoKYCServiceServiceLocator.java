/**
 * MomoKYCServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.afb.dpi.momo.services;

public class MomoKYCServiceServiceLocator extends org.apache.axis.client.Service implements com.afb.dpi.momo.services.MomoKYCServiceService {

    public MomoKYCServiceServiceLocator() {
    }


    public MomoKYCServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MomoKYCServiceServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MomoKYCServicePort
    private java.lang.String MomoKYCServicePort_address = "http://momotest:80/MomoWEB/MomoKYCService";

    public java.lang.String getMomoKYCServicePortAddress() {
        return MomoKYCServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MomoKYCServicePortWSDDServiceName = "MomoKYCServicePort";

    public java.lang.String getMomoKYCServicePortWSDDServiceName() {
        return MomoKYCServicePortWSDDServiceName;
    }

    public void setMomoKYCServicePortWSDDServiceName(java.lang.String name) {
        MomoKYCServicePortWSDDServiceName = name;
    }

    public com.afb.dpi.momo.services.MomoKYCService getMomoKYCServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MomoKYCServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMomoKYCServicePort(endpoint);
    }

    public com.afb.dpi.momo.services.MomoKYCService getMomoKYCServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.afb.dpi.momo.services.MomoKYCServiceServiceSoapBindingStub _stub = new com.afb.dpi.momo.services.MomoKYCServiceServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getMomoKYCServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMomoKYCServicePortEndpointAddress(java.lang.String address) {
        MomoKYCServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.afb.dpi.momo.services.MomoKYCService.class.isAssignableFrom(serviceEndpointInterface)) {
                com.afb.dpi.momo.services.MomoKYCServiceServiceSoapBindingStub _stub = new com.afb.dpi.momo.services.MomoKYCServiceServiceSoapBindingStub(new java.net.URL(MomoKYCServicePort_address), this);
                _stub.setPortName(getMomoKYCServicePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("MomoKYCServicePort".equals(inputPortName)) {
            return getMomoKYCServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://services.momo.dpi.afb.com/", "MomoKYCServiceService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://services.momo.dpi.afb.com/", "MomoKYCServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MomoKYCServicePort".equals(portName)) {
            setMomoKYCServicePortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
