/**
 * 
 */
package com.afb.dpd.mobilemoney.scanner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import afb.dsi.dpd.portal.jpa.entities.Module;
import afb.dsi.dpd.portal.jpa.entities.Role;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;


/**
 * Classe permettant le scan d'une EAR
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class EARScanner {
	
	/**
     * Logger
     */
	private static Log logger = LogFactory.getLog(EARScanner.class.getName());
    
	/**
	 *  Le Scanner de Fichier
	 */
	private FileScanner fileScanner = null;
	
	/**
	 *  ClassLoader à utiliser pour charger la classe
	 */
	private ClassLoader loader = null;
	
	
	/**
	 * Constructeur par defaut
	 */
	public EARScanner() {
		
		fileScanner = new FileScanner(this.getClass().getClassLoader());
	}
		
	/**
	 * Constructeur avec ClassLoader
	 * @param classLoader	ClassLoader a utiliser
	 */
	public EARScanner(ClassLoader classLoader) {
		
		fileScanner = new FileScanner(classLoader);
	}
	
	/* (non-Javadoc)
	 * @see com.yashiro.jfreesx.scanners.FileScanner#scanFile(java.lang.String)
	 */
	@SuppressWarnings("resource")
	public Module scanFile(String path) {

		// Fichier Jar
		JarFile jarFile = null;
		
		// Un log
		logger.trace("EarScanner#scanFile");
		
		// Si le chemin est vide
		if(path == null || path.trim().length() == 0) {
			
			// On retourne une liste vide
			return null;
		}
		
		// Un log
		logger.trace("#### Chemin à scanner: " + path);
		
		// Recherche du fichier
		File file = new File(path);
		
		// Si le fichier n'existe pas
		if(!file.exists()) {
			
			// Un log
			logger.trace("EarScanner#scanFiles - Le fichier n'existe pas");
			
			// On retourne la liste vide
			return null;
		}
		
		try {
			
			// Le JarFile
			jarFile = new JarFile(new File(path), true);
			
		} catch (Exception e) {
			e.printStackTrace();
			// On retourne la liste vide
			return null;
		}
		
		
		// Enumeration des Entrée de Jars
		Enumeration<JarEntry> jarEntries = jarFile.entries();
		
		// Si le JAR est vide
		if(jarEntries == null || !jarEntries.hasMoreElements()) {
			
			// On retourne la liste vide
			return null;
		}

		// Initialisation du module a retourner
		Module module = null;
		List<Role> roles = new ArrayList<Role>();
		
		// Parcours
		LBLJARENTRIES: while (jarEntries.hasMoreElements()) {

			// Obtention d'une entrée
			JarEntry jarEntry = (JarEntry) jarEntries.nextElement();
			
			// Si c'est un repertoire : On passe
			if(jarEntry.isDirectory()){
				
				// On passe
				continue LBLJARENTRIES;
			}
						
			// Entree en cours
			String entryName = jarEntry.getName();
			
			// Si le Nom de l'entree est nul : on passe
			if(entryName == null || entryName.trim().length() == 0) {
				
				// On passe
				continue LBLJARENTRIES;
			}
			
			
			if(entryName.toUpperCase().endsWith( PortalHelper.PORTAL_CONFIG_FILENAME.toUpperCase() ) ) {
				
				try{
					
					module = new Module();
					
					SAXBuilder sxb = new SAXBuilder();
					
					//InputStreamReader isr = new InputStreamReader( jarFile.getInputStream(jarEntry) );
			        //BufferedReader reader = new BufferedReader(isr);
			        
					Document document = sxb.build( jarFile.getInputStream(jarEntry)  );
					
					Element racine = document.getRootElement();
					
					List<Element> listEtudiants = racine.getChildren("module");
					Iterator<Element> i = listEtudiants.iterator();
					if(i.hasNext())	{
						Element courant = (Element)i.next();
						module.setTitle(courant.getChild("title").getText());
						module.setDescription(courant.getChild("description").getText());
						module.setEarName( path.substring(path.lastIndexOf(File.separator) + 1, path.length()) ); // courant.getChild("earName").getText());
						module.setVersion(courant.getChild("version").getText());
						module.setCode(courant.getChild("code").getText());
						module.setDeveloper(courant.getChild("developer").getText());
						module.setDevAdr(courant.getChild("address").getText());
						module.setLink(courant.getChild("context-root").getText());
						module.setDate( new SimpleDateFormat("dd/MM/yyyy").parse( courant.getChild("date").getText()) );
						module.setPicture( courant.getChild("image").getText() );
						
						System.out.println("Module recupere dans la config : Code = " + module.getCode() + ", Title = " + module.getTitle() + ", Version = " + module.getVersion() + ", Dev = " + module.getDeveloper() );
						
						//reader.close();
					}
				}catch(Exception e1){e1.printStackTrace();}
				
			}
			
				
			// Si C'est une EAR
			if(entryName.toUpperCase().endsWith(".EAR") || entryName.toUpperCase().endsWith(".JAR") ) {
				
				// JarInputStream sur le JarEntry
				JarInputStream entryJarInputStream = null;
				
				try {
					
					// Obtention de l'InputStream sur Cette entree
					entryJarInputStream = new JarInputStream(jarFile.getInputStream(jarEntry));
					
					// Prochaine entrée
					JarEntry entry = null;
					
					// Parcours de cette entree
					LBLENTRYJARENTRY: while((entry = entryJarInputStream.getNextJarEntry()) != null) {
						
						// Si l'entree est un répertoire
						if(entry.isDirectory()) {
							
							// On passe
							continue LBLENTRYJARENTRY;
						}
						
						// Nom de l'entrée
						String name = entry.getName();
						
						// Liste des Objets Proteges temporaire
						roles.addAll( fileScanner.scanFile(name) );
						
					}
				} catch (Exception e) {
					
					// On affiche l'erreur
					e.printStackTrace();
					
					// On passe
					continue LBLJARENTRIES;
				}	
			}
		}
		
		if(module != null) module.setRoles(roles);
		
		// On retourne la liste d'objets protégés
		return module;
	}
	
	/**
	 * @return the fileScanner
	 */
	public synchronized FileScanner getFileScanner() {
		return fileScanner;
	}

	/**
	 * @param fileScanner the fileScanner to set
	 */
	public synchronized void setFileScanner(FileScanner fileScanner) {
		this.fileScanner = fileScanner;
	}

	/**
	 * @return the loader
	 */
	public ClassLoader getLoader() {
		return loader;
	}

	/**
	 * @param loader the loader to set
	 */
	public void setLoader(ClassLoader loader) {
		this.loader = loader;
	}


	public static void main(String[] args) {
		
		
	}

}
