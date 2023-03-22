package com.afb.dpd.mobilemoney.jpa.tools;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

import afb.dsi.dpd.portal.jpa.tools.PortalHelper;

/**
 * Table des ecritures comptables de DELTA
 * @author Francis DJIOMOU
 * @version 1.0
 */
@Entity
@Table(name = "MOMO_bkmvti")
public class bkmvti implements Serializable {

	
	
	/**
	 * Default Serial UID
	 */
	private static final long serialVersionUID = 1L;


	public bkmvti() {}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String age ="00001"; // Agence CHAR 5 1
	private String dev = "001"; // Code devise CHAR 3 3
	private String cha; // Chapitre comptable CHAR 10 4
	private String ncp; // Numéro de compte CHAR 11 6
	private String suf = " "; // Suffixe compte CHAR 2 7
	private String ope; // Code opération CHAR 3 5
	private String mvti = " "; // Numéro de mouvement CHAR 6
	private String rgp = " "; // Code regroupement pour édition CHAR 3
	private String uti = "AUTO"; // Code utilisateur CHAR 10
	private String eve; // Numéro d'évènement CHAR 6
	private String clc; // Clé de contrôle compte CHAR 2
	private Date dco = new Date(); // Date comptable DATE 2
	private String ser = " "; // Code service CHAR 4
	private Date dva = new Date(); // Date de valeur DATE
	private Double mon; // Montant DECIMAL 19 - 4
	private String sen; // Sens CHAR 1 "D" = Débit "C" = Crédit
	private String lib; // Libellé CHAR 30
	private String exo = "N"; // Exonération de commission de mouvement CHAR 1 "O" = Oui "N" = Non
	private String pie = " "; // Numéro de pièce CHAR 11
	private String rlet = " "; // Référence de lettrage CHAR 8
	private String des1 = " "; // Code désaccord 1 CHAR 4
	private String des2 = " "; // Code désaccord 2 CHAR 4
	private String des3 = " "; // Code désaccord 3 CHAR 4
	private String des4 = " "; // Code désaccord 4 CHAR 4
	private String des5 = " "; // Code désaccord 5 CHAR 4
	private String utf = " "; // Code utilisateur ayant rappelé pour forcer CHAR 10
	private String uta = " "; // Code utilisateur ayant autorisé CHAR 12
	private Double tau = 1d; // Taux de change DECIMAL 15 - 7
	private Date din = new Date(); // Date d'indisponible DATE
	private String tpr = " "; // Zone utilisée spécifiquement CHAR 12
	private Double npr = 0d; // Zone utilisée spécifiquement DECIMAL 12 - 0
	private String ncc = " "; // Numéro de compte de rattachement CHAR 11
	private String suc = " "; // Suffixe compte de rattachement CHAR 2
	private String esi = " "; // Zone utilisée spécifiquement CHAR 1
	private String imp = "O"; // Calcul date de valeur mouvement inter-agences CHAR 1 "O" = Oui "N" = Non
	private String cta = "O"; // Mouvement à envoyer en agence CHAR 1 "O" = Oui "N" = Non
	private String mar = " "; // Code de mise à jour arrêtés CHAR 1
	private Date dech = new Date(); // Date d'échéance DATE
	private String agsa = "00001"; // Code agence de saisie CHAR 5
	private String agde; // Code agence destinatrice (permet d'identifier la CHAR 5
	private String devc = "001"; // Code devise d'origine CHAR 3
	private Double mctv = 0d; // Montant d'origine DECIMAL 19 - 4
	private String pieo = " "; // Numéro de pièce comptable d'origine CHAR 11
	private String iden = " "; // Code identifiant CHAR 6
	private String lang = " "; // Code langue CHAR 3
	private String libnls = " "; // Libellé mouvement autre langue CHAR 30
	private String modu = " "; // Code module VARCHAR 0 - 3
	private String refdos = " "; // Référence du dossier VARCHAR 0 - 50
	private String refana = " "; // Référence analytique CHAR 25
	private String label = " "; // Label du mouvement VARCHAR 0 - 25
	private String nat = " "; // Nature de transaction VARCHAR 0 - 6
	private String eta = "VA"; // Code état de l'évènement VARCHAR 0 - 2
	private String destana = " "; // Destination analytique du mouvement VARCHAR 0 - 30
	private String fusion = " "; // Code mouvement fusionné CHAR 1
	
	@JsonIgnore
	@Version
	@Column(columnDefinition = "integer DEFAULT 0", nullable = false)  
	private Long version;
	
	@JsonIgnore
	@Transient
	private String cleLettrage;
	
	@JsonIgnore
	@Transient
	private String typeOperation;
	
	
	/**
	 * @param age
	 * @param dev
	 * @param cha
	 * @param ncp
	 * @param suf
	 * @param ope
	 * @param mvti
	 * @param rgp
	 * @param uti
	 * @param eve
	 * @param clc
	 * @param dco
	 * @param ser
	 * @param dva
	 * @param mon
	 * @param sen
	 * @param lib
	 * @param exo
	 * @param pie
	 * @param rlet
	 * @param des1
	 * @param des2
	 * @param des3
	 * @param des4
	 * @param des5
	 * @param utf
	 * @param uta
	 * @param tau
	 * @param din
	 * @param tpr
	 * @param npr
	 * @param ncc
	 * @param suc
	 * @param esi
	 * @param imp
	 * @param cta
	 * @param mar
	 * @param dech
	 * @param agsa
	 * @param agde
	 * @param devc
	 * @param mctv
	 * @param pieo
	 * @param iden
	 * @param lang
	 * @param libnls
	 * @param modu
	 * @param refdos
	 * @param refana
	 * @param label
	 * @param nat
	 * @param eta
	 * @param destana
	 * @param fusion
	 */
	public bkmvti(String age, String dev, String cha, String ncp, String suf,
			String ope, String mvti, String rgp, String uti, String eve,
			String clc, Date dco, String ser, Date dva, Double mon, String sen,
			String lib, String exo, String pie, String rlet, String des1,
			String des2, String des3, String des4, String des5, String utf,
			String uta, Double tau, Date din, String tpr, Double npr,
			String ncc, String suc, String esi, String imp, String cta,
			String mar, Date dech, String agsa, String agde, String devc,
			Double mctv, String pieo, String iden, String lang, String libnls,
			String modu, String refdos, String refana, String label,
			String nat, String eta, String destana, String fusion) {
		super();
		this.age = age;
		this.dev = dev;
		this.cha = cha;
		this.ncp = ncp;
		if(suf != null) this.suf = suf;
		this.ope = ope;
		if(mvti != null) this.mvti = mvti;
		if(rgp != null) this.rgp = rgp;
		this.uti = uti;
		this.eve = eve;
		this.clc = clc;
		this.dco = dco;
		this.din = dco;
		this.dva = dco;
		this.dech = dco;
		//this.dco = dco;
		if(ser != null) this.ser = ser;
		if(dva != null) this.dva = dva;
		this.mon = mon;
		this.sen = sen;
		this.lib = lib;
		if(exo != null) this.exo = exo;
		if(pie != null) this.pie = pie;
		if(rlet != null) this.rlet = rlet;
		if(des1 != null) this.des1 = des1;
		if(des2 != null) this.des2 = des2;
		if(des3 != null) this.des3 = des3;
		if(des4 != null) this.des4 = des4;
		if(des5 != null) this.des5 = des5;
		if(utf != null) this.utf = utf;
		if(uta != null) this.uta = uta;
		if(tau != null) this.tau = tau;
		if(din != null) this.din = din;
		if(tpr != null) this.tpr = tpr;
		if(npr != null) this.npr = npr;
		if(ncc != null) this.ncc = ncc;
		if(suc != null) this.suc = suc;
		if(esi != null) this.esi = esi;
		if(imp != null) this.imp = imp;
		if(cta != null) this.cta = cta;
		if(mar != null) this.mar = mar;
		if(dech != null) this.dech = dech;
		if(agsa != null) this.agsa = agsa;
		if(agde != null) this.agde = agde;
		if(devc != null) this.devc = devc;
		if(mctv != null) this.mctv = mctv;
		if(pieo != null) this.pieo = pieo;
		if(iden != null) this.iden = iden;
		if(lang != null) this.lang = lang;
		if(libnls != null) this.libnls = libnls;
		if(modu != null) this.modu = modu;
		if(refdos != null) this.refdos = refdos;
		if(refana != null) this.refana = refana;
		if(label != null) this.label = label;
		this.nat = nat;
		this.eta = eta;
		if(destana != null) this.destana = destana;
		if(fusion != null) this.fusion = fusion;
	}
	
	
	/**
	 * @param age
	 * @param dev
	 * @param cha
	 * @param ncp
	 * @param suf
	 * @param ope
	 * @param mvti
	 * @param rgp
	 * @param uti
	 * @param eve
	 * @param clc
	 * @param dco
	 * @param ser
	 * @param dva
	 * @param mon
	 * @param sen
	 * @param lib
	 * @param exo
	 * @param pie
	 * @param rlet
	 * @param des1
	 * @param des2
	 * @param des3
	 * @param des4
	 * @param des5
	 * @param utf
	 * @param uta
	 * @param tau
	 * @param din
	 * @param tpr
	 * @param npr
	 * @param ncc
	 * @param suc
	 * @param esi
	 * @param imp
	 * @param cta
	 * @param mar
	 * @param dech
	 * @param agsa
	 * @param agde
	 * @param devc
	 * @param mctv
	 * @param pieo
	 * @param iden
	 * @param lang
	 * @param libnls
	 * @param modu
	 * @param refdos
	 * @param refana
	 * @param label
	 * @param nat
	 * @param eta
	 * @param destana
	 * @param fusion
	 */
	@JsonIgnore
	public void updateBkmvti(String age, String dev, String cha, String ncp, String suf,
			String clc, Double mon, String agde, String devc,
			Double mctv) {
		this.age = age;
		this.dev = dev;
		this.cha = cha;
		this.ncp = ncp;
		if(suf != null) this.suf = suf;
		this.clc = clc;
		if(mon != 0d) this.mon = mon;
		if(agde != null) this.agde = agde;
		if(devc != null) this.devc = devc;
		if(mctv != 0d) this.mctv = mctv;
	}
	
	@JsonIgnore
	public String getSaveQuery() {
		return "INSERT INTO BKMVTI (AGE, DEV, CHA, NCP, SUF, OPE,MVTI, RGP, UTI,EVE, CLC, DCO, SER, DVA, MON,SEN, LIB, EXO,PIE, RLET, DES1,DES2, DES3, DES4, " + 
				"DES5, UTF, UTA, TAU, DIN, TPR, NPR, NCC, SUC, ESI, IMP, CTA,MAR, DECH, AGSA,AGDE, DEVC, MCTV, PIEO, IDEN, LANG, LIBNLS, MODU, REFDOS, " +
				"REFANA, LABEL, NAT, ETA, DESTANA, FUSION, noseq) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
	}
	@JsonIgnore
	public String getFileLine(){
		String line = "";
		String noseq = "";
		String agem = "";
		String schema = "";
		String ceticpt = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		line = age + "|" + dev + "|" + cha + "|" + ncp + "|" + suf + "|" + ope + "|" + mvti + "|" + rgp + "|" + uti + "|" + eve + "|" + clc + "|" + sdf.format(dco) + "|" + ser + "|" + (dva != null ? sdf.format(dva) : "(null)") + "|" + String.valueOf(mon) + "|" + sen + "|" + lib + "|" + exo + "|" + pie + "|" + rlet + "|" + des1 + "|" + des2 + "|" + des3 + "|" + des4 + "|" + des5 + "|" + utf + "|" + uta + "|" + String.valueOf(tau) + "|" + (din == null ? "(null)" : sdf.format(din)) + "|" + tpr + "|" + String.valueOf(npr) + "|" + ncc + "|" + suc + "|" + esi + "|" + imp + "|" + cta + "|" + mar + "|" + (dech == null ? "(null)" : sdf.format(dech)) + "|" + agsa  + "|" + agem + "|" + agde + "|" + devc + "|" + String.valueOf(mctv) + "|" + pieo + "|" + iden + "|" + noseq + "|" + lang + "|" + libnls + "|" + modu + "|" + refdos + "|" + refana + "|" + label + "|" + nat + "|" + eta + "|" + schema + "|" + ceticpt + "|" + destana + "|" + fusion + "|";
		line = age + "|" + dev + "|" + cha + "|" + ncp + "|" + suf + "|" + ope + "|" + mvti + "|" + rgp + "|" + uti + "|" + eve + "|" + clc + "|" + sdf.format(dco) + "|" + ser + "|" + (dva != null ? sdf.format(dva) : "(null)") + "|" + String.valueOf(mon).replace('.', ',') + "|" + sen + "|" + lib + "|" + exo + "|" + pie + "|" + rlet + "|" + des1 + "|" + des2 + "|" + des3 + "|" + des4 + "|" + des5 + "|" + utf + "|" + uta + "|" + String.valueOf(tau).replace('.', ',') + "|" + (din == null ? "(null)" : sdf.format(din)) + "|" + tpr + "|" + String.valueOf(npr).replace('.', ',') + "|" + ncc + "|" + suc + "|" + esi + "|" + imp + "|" + cta + "|" + mar + "|" + (dech == null ? "(null)" : sdf.format(dech)) + "|" + agsa  + "|" + agem + "|" + agde + "|" + devc + "|" + String.valueOf(mctv).replace('.', ',') + "|" + pieo + "|" + iden + "|" + noseq + "|" + lang + "|" + libnls + "|" + modu + "|" + refdos + "|" + refana + "|" + label + "|" + nat + "|" + eta + "|" + schema + "|" + ceticpt + "|" + destana + "|" + fusion + "|";
		return line;
	}
	@JsonIgnore
	public Object[] getQueryValues() {
			
		Object[] values = new Object[54];
		
		values[0] = age;
		values[1] = dev;
		values[2] = cha;
		values[3] = ncp;
		values[4] = suf;
		values[5] = ope;
		values[6] = mvti;
		values[7] = rgp;
		values[8] = uti;
		values[9] = eve;
		values[10] = clc;
		values[11] = dco;
		values[12] = ser;
		values[13] = dva;
		values[14] = mon;
		values[15] = sen;
		values[16] = lib;
		values[17] = exo;
		values[18] = pie;
		values[19] = rlet;
		values[20] = des1;
		values[21] = des2;
		values[22] = des3;
		values[23] = des4;
		values[24] = des5;
		values[25] = utf;
		values[26] = uta;
		values[27] = tau;
		values[28] = din;
		values[29] = tpr;
		values[30] = npr;
		values[31] = ncc;
		values[32] = suc;
		values[33] = esi;
		values[34] = imp;
		values[35] = cta;
		values[36] = mar;
		values[37] = dech;
		values[38] = agsa;
		values[39] = agde;
		values[40] = devc;
		values[41] = mctv;
		values[42] = pieo;
		values[43] = iden;
		values[44] = lang;
		values[45] = libnls;
		values[46] = modu;
		values[47] = refdos;
		values[48] = refana;
		values[49] = label;
		values[50] = nat;
		values[51] = eta;
		values[52] = destana;
		values[53] = fusion;
		
		return values;
	
	}
	
	@JsonIgnore
	public PreparedStatement addPrepareStatement( PreparedStatement ps ) throws Exception {
			
		ps.setString(1, age);
		ps.setString(2, dev);
		ps.setString(3, cha);
		ps.setString(4, ncp);
		ps.setString(5, suf);
		ps.setString(6, ope);
		ps.setString(7, mvti);
		ps.setString(8, rgp);
		ps.setString(9, uti);
		ps.setString(10, eve);
		ps.setString(11, clc);
		ps.setDate(12, dco != null ? new java.sql.Date(dco.getTime()) : null );
		ps.setString(13, ser);
		ps.setDate(14, dva != null ? new java.sql.Date(dva.getTime()) : null);
		ps.setDouble(15, mon);
		ps.setString(16, sen);
		ps.setString(17, lib);
		ps.setString(18, exo);
		ps.setString(19, pie);
		ps.setString(20, rlet);
		ps.setString(21, des1);
		ps.setString(22, des2);
		ps.setString(23, des3);
		ps.setString(24, des4);
		ps.setString(25, des5);
		ps.setString(26, utf);
		ps.setString(27, uta);
		ps.setDouble(28, tau);
		ps.setDate(29, din != null ? new java.sql.Date(din.getTime()) : null);
		ps.setString(30, tpr);
		ps.setDouble(31, npr);
		ps.setString(32, ncc);
		ps.setString(33, suc);
		ps.setString(34, esi);
		ps.setString(35, imp);
		ps.setString(36, cta);
		ps.setString(37, mar);
		ps.setDate(38, dech != null ? new java.sql.Date(dech.getTime()) : null);
		ps.setString(39, agsa);
		ps.setString(40, agde);
		ps.setString(41, devc);
		ps.setDouble(42, mctv);
		ps.setString(43, pieo);
		ps.setString(44, iden);
		ps.setString(45, lang);
		ps.setString(46, libnls);
		ps.setString(47, modu);
		ps.setString(48, refdos);
		ps.setString(49, refana);
		ps.setString(50, label);
		ps.setString(51, nat);
		ps.setString(52, eta);
		ps.setString(53, destana);
		ps.setString(54, fusion);
		
		return ps;
	
	}
	
	
	/**
	 * @return the age
	 */
	public String getAge() {
		return age;
	}
	/**
	 * @param age the age to set
	 */
	public void setAge(String age) {
		this.age = age;
	}
	/**
	 * @return the dev
	 */
	public String getDev() {
		return dev;
	}
	/**
	 * @param dev the dev to set
	 */
	public void setDev(String dev) {
		this.dev = dev;
	}
	/**
	 * @return the cha
	 */
	public String getCha() {
		return cha;
	}
	/**
	 * @param cha the cha to set
	 */
	public void setCha(String cha) {
		this.cha = cha;
	}
	/**
	 * @return the ncp
	 */
	public String getNcp() {
		return ncp;
	}
	/**
	 * @param ncp the ncp to set
	 */
	public void setNcp(String ncp) {
		this.ncp = ncp;
	}
	/**
	 * @return the suf
	 */
	public String getSuf() {
		return suf;
	}
	/**
	 * @param suf the suf to set
	 */
	public void setSuf(String suf) {
		this.suf = suf;
	}
	/**
	 * @return the ope
	 */
	public String getOpe() {
		return ope;
	}
	/**
	 * @param ope the ope to set
	 */
	public void setOpe(String ope) {
		this.ope = ope;
	}
	/**
	 * @return the mvti
	 */
	public String getMvti() {
		return mvti;
	}
	/**
	 * @param mvti the mvti to set
	 */
	public void setMvti(String mvti) {
		this.mvti = mvti;
	}
	/**
	 * @return the rgp
	 */
	public String getRgp() {
		return rgp;
	}
	/**
	 * @param rgp the rgp to set
	 */
	public void setRgp(String rgp) {
		this.rgp = rgp;
	}
	/**
	 * @return the uti
	 */
	public String getUti() {
		return uti;
	}
	/**
	 * @param uti the uti to set
	 */
	public void setUti(String uti) {
		this.uti = uti;
	}
	/**
	 * @return the eve
	 */
	public String getEve() {
		return eve;
	}
	/**
	 * @param eve the eve to set
	 */
	public void setEve(String eve) {
		this.eve = eve;
	}
	/**
	 * @return the clc
	 */
	public String getClc() {
		return clc;
	}
	/**
	 * @param clc the clc to set
	 */
	public void setClc(String clc) {
		this.clc = clc;
	}
	/**
	 * @return the dco
	 */
	public Date getDco() {
		return dco;
	}
	/**
	 * @param dco the dco to set
	 */
	public void setDco(Date dco) {
		this.dco = dco;
	}
	/**
	 * @return the ser
	 */
	public String getSer() {
		return ser;
	}
	/**
	 * @param ser the ser to set
	 */
	public void setSer(String ser) {
		this.ser = ser;
	}
	/**
	 * @return the dva
	 */
	public Date getDva() {
		return dva;
	}
	/**
	 * @param dva the dva to set
	 */
	public void setDva(Date dva) {
		this.dva = dva;
	}
	/**
	 * @return the mon
	 */
	public Double getMon() {
		return mon;
	}
	/**
	 * @param mon the mon to set
	 */
	public void setMon(Double mon) {
		this.mon = mon;
	}
	/**
	 * @return the sen
	 */
	public String getSen() {
		return sen;
	}
	/**
	 * @param sen the sen to set
	 */
	public void setSen(String sen) {
		this.sen = sen;
	}
	/**
	 * @return the lib
	 */
	public String getLib() {
		return lib;
	}
	/**
	 * @param lib the lib to set
	 */
	public void setLib(String lib) {
		this.lib = lib;
	}
	/**
	 * @return the exo
	 */
	public String getExo() {
		return exo;
	}
	/**
	 * @param exo the exo to set
	 */
	public void setExo(String exo) {
		this.exo = exo;
	}
	/**
	 * @return the pie
	 */
	public String getPie() {
		return pie;
	}
	/**
	 * @param pie the pie to set
	 */
	public void setPie(String pie) {
		this.pie = pie;
	}
	/**
	 * @return the rlet
	 */
	public String getRlet() {
		return rlet;
	}
	/**
	 * @param rlet the rlet to set
	 */
	public void setRlet(String rlet) {
		this.rlet = rlet;
	}
	/**
	 * @return the des1
	 */
	public String getDes1() {
		return des1;
	}
	/**
	 * @param des1 the des1 to set
	 */
	public void setDes1(String des1) {
		this.des1 = des1;
	}
	/**
	 * @return the des2
	 */
	public String getDes2() {
		return des2;
	}
	/**
	 * @param des2 the des2 to set
	 */
	public void setDes2(String des2) {
		this.des2 = des2;
	}
	/**
	 * @return the des3
	 */
	public String getDes3() {
		return des3;
	}
	/**
	 * @param des3 the des3 to set
	 */
	public void setDes3(String des3) {
		this.des3 = des3;
	}
	/**
	 * @return the des4
	 */
	public String getDes4() {
		return des4;
	}
	/**
	 * @param des4 the des4 to set
	 */
	public void setDes4(String des4) {
		this.des4 = des4;
	}
	/**
	 * @return the des5
	 */
	public String getDes5() {
		return des5;
	}
	/**
	 * @param des5 the des5 to set
	 */
	public void setDes5(String des5) {
		this.des5 = des5;
	}
	/**
	 * @return the utf
	 */
	public String getUtf() {
		return utf;
	}
	/**
	 * @param utf the utf to set
	 */
	public void setUtf(String utf) {
		this.utf = utf;
	}
	/**
	 * @return the uta
	 */
	public String getUta() {
		return uta;
	}
	/**
	 * @param uta the uta to set
	 */
	public void setUta(String uta) {
		this.uta = uta;
	}
	/**
	 * @return the tau
	 */
	public Double getTau() {
		return tau;
	}
	/**
	 * @param tau the tau to set
	 */
	public void setTau(Double tau) {
		this.tau = tau;
	}
	/**
	 * @return the din
	 */
	public Date getDin() {
		return din;
	}
	/**
	 * @param din the din to set
	 */
	public void setDin(Date din) {
		this.din = din;
	}
	/**
	 * @return the tpr
	 */
	public String getTpr() {
		return tpr;
	}
	/**
	 * @param tpr the tpr to set
	 */
	public void setTpr(String tpr) {
		this.tpr = tpr;
	}
	/**
	 * @return the npr
	 */
	public Double getNpr() {
		return npr;
	}
	/**
	 * @param npr the npr to set
	 */
	public void setNpr(Double npr) {
		this.npr = npr;
	}
	/**
	 * @return the ncc
	 */
	public String getNcc() {
		return ncc;
	}
	/**
	 * @param ncc the ncc to set
	 */
	public void setNcc(String ncc) {
		this.ncc = ncc;
	}
	/**
	 * @return the suc
	 */
	public String getSuc() {
		return suc;
	}
	/**
	 * @param suc the suc to set
	 */
	public void setSuc(String suc) {
		this.suc = suc;
	}
	/**
	 * @return the esi
	 */
	public String getEsi() {
		return esi;
	}
	/**
	 * @param esi the esi to set
	 */
	public void setEsi(String esi) {
		this.esi = esi;
	}
	/**
	 * @return the imp
	 */
	public String getImp() {
		return imp;
	}
	/**
	 * @param imp the imp to set
	 */
	public void setImp(String imp) {
		this.imp = imp;
	}
	/**
	 * @return the cta
	 */
	public String getCta() {
		return cta;
	}
	/**
	 * @param cta the cta to set
	 */
	public void setCta(String cta) {
		this.cta = cta;
	}
	/**
	 * @return the mar
	 */
	public String getMar() {
		return mar;
	}
	/**
	 * @param mar the mar to set
	 */
	public void setMar(String mar) {
		this.mar = mar;
	}
	/**
	 * @return the dech
	 */
	public Date getDech() {
		return dech;
	}
	/**
	 * @param dech the dech to set
	 */
	public void setDech(Date dech) {
		this.dech = dech;
	}
	/**
	 * @return the agsa
	 */
	public String getAgsa() {
		return agsa;
	}
	/**
	 * @param agsa the agsa to set
	 */
	public void setAgsa(String agsa) {
		this.agsa = agsa;
	}
	/**
	 * @return the agde
	 */
	public String getAgde() {
		return agde;
	}
	/**
	 * @param agde the agde to set
	 */
	public void setAgde(String agde) {
		this.agde = agde;
	}
	/**
	 * @return the devc
	 */
	public String getDevc() {
		return devc;
	}
	/**
	 * @param devc the devc to set
	 */
	public void setDevc(String devc) {
		this.devc = devc;
	}
	/**
	 * @return the mctv
	 */
	public Double getMctv() {
		return mctv;
	}
	/**
	 * @param mctv the mctv to set
	 */
	public void setMctv(Double mctv) {
		this.mctv = mctv;
	}
	/**
	 * @return the pieo
	 */
	public String getPieo() {
		return pieo;
	}
	/**
	 * @param pieo the pieo to set
	 */
	public void setPieo(String pieo) {
		this.pieo = pieo;
	}
	/**
	 * @return the iden
	 */
	public String getIden() {
		return iden;
	}
	/**
	 * @param iden the iden to set
	 */
	public void setIden(String iden) {
		this.iden = iden;
	}
	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}
	/**
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}
	/**
	 * @return the libnls
	 */
	public String getLibnls() {
		return libnls;
	}
	/**
	 * @param libnls the libnls to set
	 */
	public void setLibnls(String libnls) {
		this.libnls = libnls;
	}
	/**
	 * @return the modu
	 */
	public String getModu() {
		return modu;
	}
	/**
	 * @param modu the modu to set
	 */
	public void setModu(String modu) {
		this.modu = modu;
	}
	/**
	 * @return the refdos
	 */
	public String getRefdos() {
		return refdos;
	}
	/**
	 * @param refdos the refdos to set
	 */
	public void setRefdos(String refdos) {
		this.refdos = refdos;
	}
	/**
	 * @return the refana
	 */
	public String getRefana() {
		return refana;
	}
	/**
	 * @param refana the refana to set
	 */
	public void setRefana(String refana) {
		this.refana = refana;
	}
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
	/**
	 * @return the nat
	 */
	public String getNat() {
		return nat;
	}
	/**
	 * @param nat the nat to set
	 */
	public void setNat(String nat) {
		this.nat = nat;
	}
	/**
	 * @return the eta
	 */
	public String getEta() {
		return eta;
	}
	/**
	 * @param eta the eta to set
	 */
	public void setEta(String eta) {
		this.eta = eta;
	}
	/**
	 * @return the destana
	 */
	public String getDestana() {
		return destana;
	}
	/**
	 * @param destana the destana to set
	 */
	public void setDestana(String destana) {
		this.destana = destana;
	}
	/**
	 * @return the fusion
	 */
	public String getFusion() {
		return fusion;
	}
	/**
	 * @param fusion the fusion to set
	 */
	public void setFusion(String fusion) {
		this.fusion = fusion;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	
		
	/**
	 * @return the version
	 */
	public Long getVersion() {
		return version;
	}
	

	/**
	 * @return the cleLettrage
	 */
	public String getCleLettrage() {
		return cleLettrage;
	}

	/**
	 * @param cleLettrage the cleLettrage to set
	 */
	public void setCleLettrage(String cleLettrage) {
		this.cleLettrage = cleLettrage;
	}

	/**
	 * @return the typeOperation
	 */
	public String getTypeOperation() {
		return typeOperation;
	}

	/**
	 * @param typeOperation the typeOperation to set
	 */
	public void setTypeOperation(String typeOperation) {
		this.typeOperation = typeOperation;
	}
	
	@JsonIgnore
	public String getFormattedDCO() {
		return PortalHelper.DEFAULT_DATE_FORMAT.format(dco);
	}
	
	@JsonIgnore
	public String getFormattedMon() {
		return MoMoHelper.espacement(mon);
	}

}
