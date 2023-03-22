/**
 * 
 */
package com.afb.dpd.mobilemoney.scanner;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import afb.dsi.dpd.portal.jpa.entities.Role;

import com.yashiro.persistence.utils.annotations.AllowedRole;
import com.yashiro.persistence.utils.annotations.ProtectedClass;

/**
 * <p>
 * 	<b>Classe de scan d'un Fichier à la recherche d'objets Protégé</b>
 * 	@author Francis DJIOMOU
 * 	@version 1.0
 * </p>
 */
@SuppressWarnings("unchecked")
public class FileScanner {
	
	/**
     * Logger
     */
    private static Log logger = LogFactory.getLog(FileScanner.class.getName());
    
	
	/**
	 *  ClassLoader à utiliser pour charger la classe
	 */
	private ClassLoader loader = null;
	
	/**
	 *  Constante slash
	 */
	public static final char SLASH = '/';

	/**
	 *  Constante anti-slash
	 */
	public static final char ANTISLASH = '\\';
	
	/**
	 *  Constante point
	 */
	public static final char POINT = '.';
	
	/**
	 *  Constante extension de fichiers class
	 */
	public static final String CLASSEXT = ".class";
	
	/**
	 *  Constantes contenue dans les classes internes
	 */
	public static final String INTERNALCLASS = "$";
		
	/**
	 * Constructeur par defaut
	 */
	public FileScanner() {}
			
	/**
	 * Constructeur prenant un Logger
	 * @param loader Loader a utiliser
	 */
	public FileScanner(ClassLoader loader) {
		
		// On positionne le loader
		this.loader = loader;
	}
		
	//@Override
	@SuppressWarnings("rawtypes")
	public List<Role> scanFile(String path) {
		
		// Un log
		logger.trace("FileScanner#scanFile");
		
		// Liste des Objets proteges
		List<Role> roles = new ArrayList<Role>();
		
		// Si le chemin est vide
		if(path == null || path.trim().length() == 0) {
			
			// Un log
			logger.trace("Chemin vide");
			
			// On retourne une liste vide
			return roles;
		}

		// Un log
		logger.trace("Chemin à scanner: " + path);
		
		// Si le fichier n'a pas l'extension d'un fichier de classe
		if(!hasClassFileExtension(path)) {
			
			// Un log
			logger.trace("Ce fichier n'a pas l'extension d'un fichier de classe: " + path);
			
			// On retourne une liste vide
			return roles;
		}
		
		// Obtention du Nom de la classe
		String className = buildClassName(path);
		
		// Si le nom de classe est vide : on retourne la liste vide
		if(className == null || className.trim().length() == 0) {
			
			// Un log
			logger.trace("Le Nom de classe est vide");
			
			// On retourne une liste vide
			return roles;
		}
		
		// Un log
		logger.trace("Nom de la classe: " + className);
		
		// Classe chargée
		Class loadedClass = null;
		
		try {
			
			// Tentative de chargement de la classe sans classloader
			if(loader != null) loadedClass = Class.forName(className, false, loader);
			
			// Tentative de chargement de la classe avec classloader
			else loadedClass = Class.forName(className);
		
		} catch (NoClassDefFoundError e) {
			
			// On retourne une liste vide
			return roles;
		
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return roles;
		} 

		// Si la classe n'a pas  été chargée
		if(loadedClass == null) {

			// On retourne une liste vide
			return roles;
		}
		
		// Obtention de la liste des objets proteges de cette classe
		roles.addAll( scanClass(loadedClass) );
				
		// On retourne la liste des 
		return roles;
	}

	/*
	@Override
	public Map<SXProtectedSystem, List<Role>> scanFiles(List<String> paths) {
		
		// Un log
		logger.trace("FileScanner#scanFiles");
		
		// Liste des Objets proteges
		Map<SXProtectedSystem, List<Role>> protectedObjects = new HashMap<SXProtectedSystem, List<Role>>();
				
		// Si la liste des chemins est vide : on retourne une liste vide
		if(paths == null || paths.size() == 0) {
			
			// On retourne une liste vide
			return protectedObjects;
		}
		
		// Parcours de la liste
		LBLPATH: for (String path : paths) {
			
			// Si le chemin est vide : on passe
			if(path == null || path.trim().length() == 0) {
				
				// On passe
				continue LBLPATH;
			}
			
			// On charge la liste des Objets proteges retouves dans ce fichier
			Map<SXProtectedSystem, List<Role>> tmpList = scanFile(path);
			
			// Si la liste est vide : on passe
			if(tmpList == null || tmpList.size() == 0) {
				
				// On passe
				continue LBLPATH;
			}
			
			// Parcours de cette liste
			LBLTMPMAP: for (SXProtectedSystem system : tmpList.keySet()) {
				
				// Si cette liste est vide
				if(protectedObjects.get(system) == null) protectedObjects.put(system, new ArrayList<Role>());

				// Liste des Roles present de ce systeme
				List<Role> alreadyRoles = protectedObjects.get(system);
				
				// Obtention de la liste des roles de ce systeme
				List<Role> roles = tmpList.get(system);
				
				// Si la liste est vide
				if(roles == null || roles.size() == 0) continue LBLTMPMAP;
				
				// Parcours de la liste de roles
				LBLTMPLIST : for (Role protectedObject : roles) {

					// Si l'objet protege est null
					if(protectedObject == null) {
						
						// On passe
						continue LBLTMPLIST;
					}
					
					// Si l'objet n'est pas contenu dans la liste
					if(alreadyRoles.contains(protectedObject)) {
						
						// on passe
						continue LBLTMPLIST;
					}
					
					// On ajoute
					alreadyRoles.add(protectedObject);
				}
				
			}			
		}
		
		// Un log
		logger.trace("Taille de la liste d'objets Proteges : " + protectedObjects.size());
		
		// On retourne la liste d'objets proteges
		return protectedObjects;
	}
	*/
	
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
	
	/**
	 * Methode permettant de tester si un fichier a l'extension d'un fichier de classe
	 */
	public static boolean hasClassFileExtension(String path) {
		
		// Un log
		logger.trace("FileScanner#hasClassFileExtension");
		
		// Si le chemin est vide : false
		if(path == null || path.trim().length() == 0) return false;
		
		// On retourne le resultat
		return path.endsWith(CLASSEXT);
	}
	
	/**
	 * Methode permettant de construire le nom de la classe encapsulee dans un fichier de classe
	 */
	public static String buildClassName(String path) {
		
		// Un log
		logger.trace("FileScanner#buildClassName");
		
		// Nom de la classe à retourner
		String className = path;
		
		// Si le nom de fichier est vide : null
		if(path == null || path.trim().length() == 0){
			
			// On retourne un nom vide
			return "";
		}
		
		// Si le chemin contient le symbole des classes internes
		if(path.contains(INTERNALCLASS)) {
			
			// On retourne un nom vide
			return "";
		}

		try {

			// Remplacement des slash (/) par des .
			className = path.replace(SLASH, POINT);
			
			// Remplacement des AntiSlash (\) par des .
			className = className.replace(ANTISLASH, POINT);
			
			// Suppression de l'extension .class
			className = className.substring(0, className.indexOf(CLASSEXT));
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			// On retourne un nom vide
			return "";
			
		}

		// Un log
		logger.trace("Nom de la classe: " + className);
		
		// On retourne le nom
		return className;
	}
	
	/**
	 * Introspection d'une classe à la recherche d'objets protégés
	 */
	@SuppressWarnings("rawtypes")
	public List<Role> scanClass(Class classToScan) {
		
		// Un log
		logger.trace("FileScanner#scanClass");
		
		// Liste des Objets proteges
		List<Role> roles = new ArrayList<Role>();
		
		try {
			
			// Si la classe est nulle
			if(classToScan == null) {
				
				// On retourne la liste d'objets vide
				return roles;
			}
			
			// Recherche de l'annotation de sécurité
			ProtectedClass protectedClassMarker = (ProtectedClass) classToScan.getAnnotation(ProtectedClass.class);
			
			// Si la classe ne possède pas cette annotation : On retourne une liste vide
			if(protectedClassMarker == null) return roles;
			
			// Recuperation de toutes les methodes de la classe
			Method[] methods = classToScan.getMethods();
			
			// Si la classe ne possede aucune methode on sort
			if(methods == null || methods.length == 0) return roles;
			
			// Parcours des methodes de la classe
			for(Method m : methods){
				
				// Recherche de l'annotation de securite
				AllowedRole role = m.getAnnotation(AllowedRole.class);
				
				// Si l'annotation existe
				if(role != null){
					
					// Ajout du nouveau role a la collection
					roles.add( new Role(role.name(), role.displayName()) );
					
				}
			
			}
			
			// On retourne la liste des objets protégés
			return roles;
		
		} catch (Exception e) {
			
			e.printStackTrace();
			
			// On retourne la liste vide
			return roles;
		}
	}
	
	
}
