package com.afb.dpd.mobilemoney.scanner;

import javax.ejb.Remote;

/**
 * Service de Scan du module
 * @author Francis DJIOMOU
 * @version 2.0
 */
@Remote
public interface IScanner {

	/**
	 * Nom du service
	 */
	public static String SERVICE_NAME = "Scanner";
	
	/**
	 * Methode de scan du module
	 */
	public void scanAndInitialiseModule( String fileName ) throws Exception;
	
}
