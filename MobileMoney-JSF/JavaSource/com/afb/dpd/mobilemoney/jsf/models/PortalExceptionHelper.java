package com.afb.dpd.mobilemoney.jsf.models;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.afb.dpd.mobilemoney.jsf.tools.MobileMoneyViewHelper;

public class PortalExceptionHelper {

	/**
	 * Default Constructor
	 */
	public PortalExceptionHelper() {}

	/**
	 * Recupere une Exception et Affiche la boite de dialogue correspondante
	 * @param ex exception
	 */
	public static void threatException(Throwable ex){
		
		// Recuperation  de la cause de l'exception
		String msg = ex.getMessage() != null && !ex.getMessage().isEmpty() ? ex.getMessage() : ex.getCause().toString();
		
		// Recuperation de la trace de l'exception
		String trace = getExceptionMessageDetails( ex );
		
		// Initialisation de la boite de dialogue de messaged
		InformationDialog infDlg = (InformationDialog)MobileMoneyViewHelper.getSessionManagedBean(InformationDialog.getName());
		
		// Si la boite de dialogue a ete correctement initialisee
		if(infDlg != null) {
			
			// Lecture du message a afficher
			infDlg.setMessage(msg);
			
			// Lecture du type de boite de dialogue
			infDlg.setTypeDialog( InformationDialog.DIALOG_ERROR );
			
			// Lecture de la trace de l'exception
			infDlg.setDetails(trace);
			
			// Ouverture de la boite de dialogue
			infDlg.setOpen(true);
		}
		
		ex.printStackTrace();
	}
	
	/**
	 * Recupere la Trace d'une exception sous forme de texte
	 * @param e exception
	 * @return Trace de exception
	 */
	public static String getExceptionAsString(Throwable e){
		String s = "";
		
		try{
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			s = sw.toString();
			sw.close(); pw.close();
		}catch(Exception ex){s = e.getMessage();}
		
		return s;
	}

	/**
	 * Methode permettant d'obtenir la pile d'erreurs survenue
	 * @param exception	Exception survenue
	 * @return	Message Pile d'erreurs
	 */
	public static String getExceptionMessageDetails(Throwable exception) {
		
		// Le Buffer
		StringBuffer buffer = new StringBuffer();
		
		// On Y met le message internationnalise
		buffer.append(exception.getMessage());

		// On Y met le message internationnalise
		buffer.append("\n");
		
		// Obtention de la pile des exception
		StackTraceElement[] elements = exception.getStackTrace();
		
		// Si la liste n'est pas vide
		if(elements != null && elements.length > 0) {
			
			// Parcours de la liste
			for(int i = 0; i < elements.length; i++) {
				
				// Un Element
				StackTraceElement element = elements[i];
				
				// Ajout du Message
				buffer.append(element.toString());
			}
		}
		
		// On retourne la chaine
		return buffer.toString();
	}
	
	
}
