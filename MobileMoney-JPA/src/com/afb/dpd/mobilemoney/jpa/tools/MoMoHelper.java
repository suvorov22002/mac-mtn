/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.afb.dpd.mobilemoney.jpa.tools;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.afb.dpd.mobilemoney.jpa.entities.Commissions;
import com.afb.dpd.mobilemoney.jpa.enums.ModeFacturation;
import com.afb.dpd.mobilemoney.jpa.enums.TypeOperation;
import com.afb.dpd.mobilemoney.jpa.enums.TypeValeurFrais;

import afb.dsi.dpd.portal.jpa.entities.DataQuery;
import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

/**
 * Helper
 * @author Francis DJIOMOU
 * @version 1.0
 */
public class MoMoHelper {
    
    public static int LEFT = 1;
    public static int RIGHT = 2;
    
    //public static String CODE_OPE_VIREMENT_SIMPLE = "131";
    //public static String CODE_OPE_VIREMENT_INTERAG = "135";
    public static int TAILLE_CODE_EVE = 6;
    public static int PHONES_LENGTH = 12;
    public static String PHONES_MASK = "2376";
    
    public static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH':'mm");
    public static SimpleDateFormat sdf_DATE = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat sdf_HOUR = new SimpleDateFormat("HH':'mm");
    
    public static String PERIODE_DAY = "JOUR";
    public static String PERIODE_HOUR = "HEURES";
    
    /**
     * Default Constructor
     */
    public MoMoHelper(){}
    
    
    /**
     * Retourne la liste des requetes necessaires pour la lecture des donnees dans Amplitude
     * @return liste des requetes necessaires pour la lecture des donnees dans Amplitude
     */
    public static List<DataQuery> getDefaultCBSQueries() {
    	
    	// Initialisation
    	List<DataQuery> dqs = new ArrayList<DataQuery>();
    	
    	// Ajout des requetes a la liste dqs.add( new DataQuery("01", "Types de comptes", "select distinct cpro as code, lib as nom from bkprod where cpro like '%' ", null) );
    	dqs.add( new DataQuery("01", "Types de comptes", "select distinct cpro as code, lib as nom from bkprod ", null) );
    	dqs.add( new DataQuery("02", "Recherche comptes client", "select bkcom.cpro, bkcom.age, bkcom.ncp, bkcom.clc, bkcli.nom, nvl(bkcli.pre, ' ') as pre, bkadcli.adr1, bkadcli.ville from bkcom, bkcli, bkadcli where bkcom.cli = bkcli.cli and bkadcli.cli = bkcli.cli and bkcom.cfe='N' and bkcom.ife='N' and bkcom.cli = ? ", null) );
    	
    	// Requete de Lecture du dernier numero d'evenement genere par type operation 
    	dqs.add( new DataQuery("03", "Dernier numero d'evenement genere", "select num from bkope where ope = ? and age='00099'", null) );
    	
    	// Requete de MAJ du dernier numero d'evenement genere par type operation
    	dqs.add( new DataQuery("04", "MAJ du dernier numero d'evenement utilise", "update bkope set num = ? where ope = ? and age='00099' ", null) );
    	
    	// MAJ du solde indicatif du compte debiteur
    	dqs.add( new DataQuery("05", "MAJ du solde indicatif du compte", "update bkcom set sin = sin - ? where age = ? and ncp = ? and clc = ? ", null) );
    	
    	// MAJ du solde indicatif du compte crediteur
    	dqs.add( new DataQuery("06", "MAJ du solde indicatif du compte", "update bkcom set sin = sin + ? where age = ? and ncp = ? and clc = ? ", null) );

    	// Recherche d'un compte dans le CBS
    	dqs.add( new DataQuery("07", "Recherche Compte Client", "select bkcom.*, bkcli.nom, bkcli.ges from bkcom, bkcli where bkcli.cli = bkcom.cli and bkcom.age = ? and bkcom.ncp = ? and bkcom.clc = ?", null) );

    	// Recherche d'un compte dans le CBS
    	dqs.add( new DataQuery("08", "Recherche Compte Non Client", "select * from bkcom where age = ? and ncp = ? and clc = ? and cfe='N' and ife='N'", null) );
    	
    	dqs.add( new DataQuery("09", "Recherche Compte", "select * from bkcom where bkcom.age = ? and bkcom.ncp = ? and bkcom.clc = ? and cfe='N' and ife='N'", null) );
    	
    	dqs.add( new DataQuery("10", "MAJ Solde Indicatif TFJ", "update bksin set mon = mon - ? where age = ? and ncp = ? ", null) );
    	dqs.add( new DataQuery("11", "MAJ Solde Indicatif TFJ2", "update bksin set mon = mon + ? where age = ? and ncp = ? ", null) );
    	dqs.add( new DataQuery("12", "Recherche Autorisation Decouvert", "select sum(maut) as maut from bkautc where ncp = ? and fin > ? and ETA in ('VA', 'VF', 'FO') ", null) );
    	dqs.add( new DataQuery("13", "Recherche de l'evenement dans core banking", "select eve,ope from bkeve where eve = ? and ope = ? ", null) );
    	
    	// Retourne la liste
    	return dqs;
    	
    }
    
    public static String getQueryControlEquilibre(){
    	return "select dco,uti,ope,sen,count(*) as nbre, sum(mon) as total from bkmvti " +
    			"where ope = ? and dco = ? and uti = ? and (lib like ? or lib like ?) " +
    			"group by dco,uti,ope,sen " +
    			"order by dco,uti,ope,sen ";
    }
    
    public static String getQueryControlEquilibrePULLClient(){
    	return "select count(*) as nbre, sum(mon) as total from bkmvti "
    			+ "where ope = '580' and dco = ? and (ncp like '0%' and ncp != '02523481001') and sen = 'D' and lib like 'PULL%'";
    }
    
    public static String getQueryControlEquilibrePUSHClient(){
    	return "select count(*) as nbre, sum(mon) as total  from bkmvti "
    			+ "where ope = '580' and dco = ? and (ncp like '0%' and ncp != '02523481001') and sen = 'C' and lib like 'PUSH%' ";
    }
    
    public static String getQueryControlEquilibrePULLDAP(){
    	return "select count(*) as nbre, sum(mon) as total from bkmvti "
    			+ "where ope = '580' and dco = ? and ncp in('38140090106', '38140090103') and sen = 'C' and lib like 'PULL%' ";
    }
    
    public static String getQueryControlEquilibrePUSHDAP(){
    	return "select count(*) as nbre, sum(mon) as total from bkmvti "
    			+ "where ope = '580' and dco = ? and ncp in ('38640090005', '38640090002') and sen = 'D' and lib like 'PUSH%' ";
    }
    
    public static String getQueryControlEquilibrePULLFloatMTN(){
    	return "select count(*) as nbre, sum(mon) as total from bkmvti "
    			+ "where ope = '580' and dco = ? and ncp like '02523481001' and sen = 'C' ";
    }
    
    public static String getQueryControlEquilibrePUSHFloatMTN(){
    	return "select count(*) as nbre, sum(mon) as total from bkmvti "
    			+ "where ope = '580' and dco = ? and ncp like '02523481001' and sen = 'D' ";
    }

    public static String getQueryControlDoublon(){
    	return "select distinct h2.age,h2.dev,h2.ncp,h2.ope,h2.eve,h2.pie,h2.lib,h2.mon,h2.sen,count(*) as nbre " +
    			"from bkmvti h2 where h2.ope = ? and h2.dco = ? and h2.uti = ? and (h2.lib like ? or h2.lib like ?) " +
    			"group by h2.age,h2.dev,h2.ncp,h2.ope,h2.eve,h2.pie,h2.lib,h2.mon,h2.sen " +
    			"having count(h2.pie)>=2";
    }
    
    /**
     * Retourne les commissions par defaut
     * @return Liste des commissions par defaut
     */
    public static List<Commissions> getDefaultCommissions() {
    	
    	List<Commissions> coms = new ArrayList<Commissions>();
    	
    	coms.add( new Commissions(TypeOperation.PULL, TypeValeurFrais.FIXE, 0d, 19.25d, ModeFacturation.TRANSACTION, null) );
    	coms.add( new Commissions(TypeOperation.PUSH, TypeValeurFrais.FIXE, 0d, 19.25d, ModeFacturation.TRANSACTION, null) );
    	coms.add( new Commissions(TypeOperation.SUBSCRIPTION, TypeValeurFrais.FIXE, 0d, 19.25d, ModeFacturation.TRANSACTION, null) );
    	coms.add( new Commissions(TypeOperation.MODIFY, TypeValeurFrais.FIXE, 0d, 19.25d, ModeFacturation.TRANSACTION, null) );
    	
    	return coms;
    }
    
    public static String padText(String text, int direction, int lenght, String addString ){
        
        String s = text;
        
        if(s == null){
            
            for(int i=1; i<=lenght; i++) s += addString;
            
        }else{
            
            if(s.length() < lenght){
                
                while(s.length() < lenght){
                    s = direction == LEFT ? addString + s : s + addString;
                }
                
            }
            
        }
        
        s = s.substring(0, lenght);
        
        return s;
    }
    
    
        
    public static String getCode(){
        return new SimpleDateFormat("ddMMyyhhmmss").format(new java.util.Date());
    }
         
    public static String getCode2(){
        return new SimpleDateFormat("ddMMyy").format(new java.util.Date());
    }
       
    
    
    public static String espacement(double montant){
        try{
            return espacement(new DecimalFormat("#0.###").format(new BigDecimal(montant)).replaceAll(",", "."));
        }catch(Exception e){
            return "0";
        }
    }


    public static String espacement(String mnt){
        try{
        if(mnt.isEmpty()) return "0";
        String partieDeci = ((mnt.indexOf(".")>=0)?(mnt.substring(mnt.indexOf("."), mnt.length()) ):(""));
        if(mnt.indexOf(".")>=0) mnt = mnt.substring(0, mnt.indexOf("."));
        if(mnt.indexOf(" ")>=0) mnt = mnt.replaceAll(" ", "");
        
        if(mnt.length()<=3){
            return mnt + partieDeci;
        }else{
            
            int nbreParties = ((mnt.length()%3)==0)?( (int)Math.ceil( (mnt.length()/3)  ) ):(  (int)Math.ceil(Double.parseDouble( String.valueOf( (mnt.length()/3) ).split(",")[0] + ".8" ))    );
            //System.out.println("Montant : " + mnt + ", nbre de parties = " + nbreParties);
            int i=0; String s = "";
            while(i<nbreParties && mnt.length()>0){
                s = " " + mnt.substring( ((mnt.length()>=3)?(mnt.length()-3):(0)) , mnt.length()) + s;
                mnt = mnt.substring(0, ((mnt.length()>=3)?(mnt.length()-3):(mnt.length())) );
                i++;
            }
            if(s.startsWith(" ")) s = s.substring(1, s.length());
            return s + partieDeci;
        }
        }catch(Exception e){
            e.printStackTrace();
            return mnt;
        }
    }

    
    public static String getPiecesJointesDir() {
		return PortalHelper.JBOSS_DATA_DIR + File.separator + PortalHelper.PORTAL_RESOURCES_DATA_DIR + File.separator + PortalHelper.PORTAL_REPORTS_DIR + File.separator + "MobileMoney" + File.separator + "PJs" + File.separator;
	}
    
    
    public static long getNbreJoursBetween(Date date1, Date date2){
        if(date1 == null || date2 == null) return 0;
        return (date2.getTime() - date1.getTime()) / (long)(1000 * 60 * 60 * 24);
    }
    
    
    
}
