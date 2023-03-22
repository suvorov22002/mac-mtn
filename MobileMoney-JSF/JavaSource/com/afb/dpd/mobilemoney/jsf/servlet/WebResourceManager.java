package com.afb.dpd.mobilemoney.jsf.servlet;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

/**
 * Service web permettant de gerer les resources
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class WebResourceManager extends HttpServlet {
	
	/**
	 * ID Genere par Eclipse
	 */
	private static final long serialVersionUID = 1L;
    
	/**
	 * MAP des Types MIMES ranges par Extension
	 */
	public static Map<String, String> mimes = new HashMap<String, String>();
	
	/**
	 * Bloc d'initialisation statique
	 */
	static {
		
		// Ajout des Types MIMES
		mimes.put("dwg", "application/acad");
		mimes.put("ccad", "application/clariscad");
		mimes.put("drw", "application/drafting");
		mimes.put("dxf", "application/dxf");
		mimes.put("unv", "application/i-deas");
		mimes.put("igs", "application/iges");
		mimes.put("iges", "application/iges");
		mimes.put("bin", "application/octet-stream");
		mimes.put("oda", "application/oda");
		mimes.put("pdf", "application/pdf");
		mimes.put("ai", "application/postscript");
		mimes.put("eps", "application/postscript");
		mimes.put("ps", "application/postscript");
		mimes.put("prt", "application/pro_eng");
		mimes.put("rtf", "application/rtf");
		mimes.put("set", "application/set");
		mimes.put("stl", "application/sla");
		mimes.put("dwg", "application/solids");
		mimes.put("step", "application/step");
		mimes.put("vda", "application/vda");
		mimes.put("mif", "application/x-mif");
		mimes.put("dwg", "application/x-csh");
		mimes.put("dvi", "application/x-dvi");
		mimes.put("hdf", "application/hdf");
		mimes.put("latex", "application/x-latex");
		mimes.put("nc", "application/x-netcdf");
		mimes.put("cdf", "application/x-netcdf");
		mimes.put("dwg", "application/x-sh");
		mimes.put("tcl", "application/x-tcl");
		mimes.put("tex", "application/x-tex");
		mimes.put("texinfo", "application/set");
		mimes.put("set", "application/x-texinfo");
		mimes.put("texi", "application/x-texinfo");
		mimes.put("bcpio", "application/x-bcpio");
		mimes.put("cpio", "application/x-cpio");
		mimes.put("gtar", "application/x-gtar");
		mimes.put("shar", "application/x-shar");
		mimes.put("sv4cpio", "application/x-sv4cpio");
		mimes.put("sc4crc", "application/x-sc4crc");
		mimes.put("tar", "application/x-tar");
		mimes.put("man", "application/x-ustar");
		mimes.put("zip", "application/x-zip");
		mimes.put("gz", "application/x-gzip");
		mimes.put("gzip", "application/x-gzip");
		mimes.put("wav", "audio/x-wav");
		mimes.put("gif", "image/gif");
		mimes.put("png", "image/png");
		mimes.put("ief", "image/ief");
		mimes.put("jpg", "image/jpg");
		mimes.put("jpeg", "image/jpeg");
		mimes.put("jpe", "image/jpe");
		mimes.put("tiff", "image/tiff");
		mimes.put("tif", "image/tiff");
		mimes.put("cmu", "image/x-cmu-raster");
		mimes.put("pnm", "image/x-portable-anymap");
		mimes.put("pbm", "image/x-portable-bitmap");
		mimes.put("pgm", "image/x-portable-graymap");
		mimes.put("ppm", "image/x-portable-pixmap");
		mimes.put("rgb", "image/x-rgb");
		mimes.put("xbm", "image/x-xbitmap");
		mimes.put("xpm", "image/x-xpixmap");
		mimes.put("htm", "text/html");
		mimes.put("html", "text/html");
		mimes.put("xhtml", "text/html");
		mimes.put("mpeg", "video/mpeg");
		mimes.put("mpg", "video/mpeg");
		mimes.put("mpe", "video/mpeg");
		mimes.put("qt", "video/quicktime");
		mimes.put("mov", "video/quicktime");
		mimes.put("avi", "video/msvideo");
		mimes.put("movie", "video/x-sgi-movie");
		mimes.put("tar", "application/x-tar");
		mimes.put("tar", "application/x-tar");
		mimes.put("tar", "application/x-tar");
		mimes.put("tar", "application/x-tar");
		mimes.put("tar", "application/x-tar");
		mimes.put("tar", "application/x-tar");
		mimes.put("tar", "application/x-tar");
		mimes.put("css", "text/css");
		mimes.put("xls", "application/vnd.ms-excel");
		mimes.put("txt", "text/plain");
		
	}
	

    /**
     * @see HttpServlet#HttpServlet()
     */
    public WebResourceManager() {
        
    	// Appel Parent
    	super();
    	 
    }

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
        // Obtention de l'URI de la requete
    	String requestURI = request.getRequestURI();
    	
    	// Obtention du chemin complet vers la ressource demandée
    	String resourcePath = "file:////" + PortalHelper.JBOSS_DATA_DIR.replaceAll("\\\\", "/") + requestURI.replaceAll(request.getContextPath(), "/");
    	
    	// L'URL
    	URL url = new URL(resourcePath);
    	
    	// Construction d'un File sur le chemin de la ressource
    	File resourceFile = new File(url.getPath());
    	
    	// Si la ressource n'existe pas
    	if(!resourceFile.exists()) {
        	
    		// On retourne une erreur de Type NotFound
    		response.sendError(HttpServletResponse.SC_NOT_FOUND);
    		
    		// On sort
    		return;
    	}
    	
    	// Si la resource n'est pas un fichier
    	if(!resourceFile.isFile()) {
    		    		
    		// On retourne une erreur de Type NotFound
    		response.sendError(HttpServletResponse.SC_NOT_FOUND);
    		
    		// On sort
    		return;
    	}
    	
    	// Si la ressource n'est pas accessible en lecture
    	if(!resourceFile.canRead()) {
    		    		
    		// On retourne une erreur de Type Non Authorisé
    		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    		
    		// On sort
    		return;
    	}
    	
    	try {
    		
    		// Envoi du fichier
    		sendFile(resourceFile, request, response);
    		
		} catch (Exception e) {
			
    		// On retourne une erreur de Type Non Authorisé
    		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    		
    		// On sort
    		return;
    	}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Appel de doGet
		doGet(request, response);
	}
	
	/**
	 * Methode d'envoie du fichier
	 * @param request	Requete Http
	 * @param response	Reponse Http
	 */
	private void sendFile(File file, HttpServletRequest request, HttpServletResponse response) {
		
		try {

			// Le Type MIME
			String mimeType = mimes.get("bin");
			
			// Index du dernier "."
			int lastCommaIndex = file.getAbsolutePath().lastIndexOf(".");
			
			// Si l'index est < 0
			if(lastCommaIndex > 0) {

				// Extension du fichier
				String ext = file.getAbsolutePath().substring(lastCommaIndex + 1);
				
				// Si l'extension n'est pas vide
				if(ext != null && ext.trim().length() > 0) {
					
					// Recherche du Type MIME
					mimeType = mimes.get(ext.toLowerCase());
					
					// Si le Type est null
					if(mimeType == null || mimeType.trim().length() == 0) mimeType = mimes.get("bin");
				}
			}
			
			// On positionne le Type MIME
			response.setContentType(mimeType);
			
			// Ouverture du Fichier
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
			
			// Le Stream de sortie
			OutputStream outputStream = response.getOutputStream();
			
			// Donnee lu
			int data;
			
			// Lecture
			while((data = bufferedInputStream.read()) != -1) {
				
				// On ecrit
				outputStream.write(data);
			}
			
			// On flushe
			outputStream.close();
			
			// On referme le flux de lecture
			bufferedInputStream.close();
			
		} catch (Exception e) {
			
			// On relance
			throw new RuntimeException(e);
		}
	}
}
