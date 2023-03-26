package com.afb.dpd.mobilemoney.jpa.tools;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Bkhis implements Serializable, Comparable<Bkhis>{
	
	private static final long serialVersionUID = 1L;

	private String dev;
	private String ncp;
	private String suf;
	private Date dco;
	private String ope;
	private String mvt;
	private String ser;
	private Date dva;
	private Date din;
	private double mon;
	private String sen;
	private String lib;
	private String exo;
	private String pie;
	private String des1;
	private String des2;
	private String des3;
	private String des4;
	private String des5;
	private String uti;
	private String utf;
	private String uta;
	private String eve;
	private String agem;
	private Date dag;
	private String ncc;
	private String suc;
	private String cpl;
	private Date ddl;
	private String rlet;
	private String utl;
	private String mar;
	private Date dech;
	private String agsa;
	private String agde;
	private String devc;
	private double mctv;
	private String pieo;
	private String iden;
	private Date dexa;
	private String modu;
	private String refdos;
	private String label;
	private String nat;
	private String eta;
	private String schema;
	private String ceticpt;
	private String fusion;

	public String getDev(){
		return dev;
	}



	/**
	 * 
	 */
	public Bkhis() {
		super();
	}



	/**
	 * 
	 */
//	public Bkhis addEve1(com.afriland.cbsapi.amplitude.restservices.Bkheve e) {
//		this.setDev(e.getDev());
//		this.setNcp(e.getNcp1());
//		this.setDco(e.getDco());
//		this.setUti(e.getUti());
//		this.setEta(e.getEta());
//		this.setMon(e.getMon1());
//		this.setEve(e.getEve());
//		this.setOpe(e.getOpe());
//		this.setDva(e.getDva1());
//		this.setNat(e.getNat());
//		this.setSuf(e.getSuf1());
//		this.setPie(e.getPieo());
//		this.setSen("D");
//		return this;
//	}

	/**
	 * 
	 */
//	public Bkhis addEve2(com.afriland.cbsapi.amplitude.restservices.Bkheve e) {
//		this.setDev(e.getDev());
//		this.setNcp(e.getNcp2());
//		this.setDco(e.getDco());
//		this.setUti(e.getUti());
//		this.setEta(e.getEta());
//		this.setMon(e.getMon2());
//		this.setEve(e.getEve());
//		this.setOpe(e.getOpe());
//		this.setDva(e.getDva2());
//		this.setNat(e.getNat());
//		this.setSuf(e.getSuf2());
//		this.setPie(e.getPieo());
//		this.setSen("C");
//		return this;
//	}


	public Bkhis(ResultSet rs) throws SQLException {
		super();
		this.agde = rs.getString("age");
		this.dev = rs.getString("dev");
		this.ncp = rs.getString("ncp");
		this.ope = rs.getString("ope");
		this.mvt = rs.getString("mvt");
		this.dva = rs.getDate("dva");
		this.mon = rs.getDouble("mon");
		this.sen = rs.getString("sen");
		this.lib = rs.getString("lib");
		this.pie = rs.getString("pie");
		this.eve = rs.getString("eve");
		this.dag = rs.getDate("dag");
		this.agsa = rs.getString("agsa");
		this.eta = rs.getString("eta");
		this.dco = rs.getDate("dco");
		this.cpl = rs.getString("clc");
		/*this.suf = rs.getString("suf");
		this.ser = rs.getString("ser");
		this.agem = rs.getString("agem");
		this.din = rs.getDate("din");
		this.ncc = rs.getString("ncc");
		this.exo = rs.getString("exo");
		this.des1 = rs.getString("des1");
		this.des2 = rs.getString("des2");
		this.des3 = rs.getString("des3");
		this.des4 = rs.getString("des4");
		this.des5 = rs.getString("des5");
		this.uti = rs.getString("uti");
		this.utf = rs.getString("utf");
		this.uta = rs.getString("uta");
		this.suc = rs.getString("suc");
		this.cpl = rs.getString("cpl");
		this.ddl = rs.getDate("ddl");
		this.rlet = rs.getString("rlet");
		this.utl = rs.getString("utl");
		this.mar = rs.getString("mar");
		this.dech = rs.getDate("dech");
		this.agde = rs.getString("agde");
		this.devc = rs.getString("devc");
		this.mctv = rs.getDouble("mctv");
		this.pieo = rs.getString("pieo");
		this.iden = rs.getString("iden");
		this.dexa = rs.getDate("dexa");
		this.modu = rs.getString("modu");
		this.refdos = rs.getString("refdos");
		this.label = rs.getString("label");
		this.nat = rs.getString("nat");
		this.schema = rs.getString("schema");
		this.ceticpt = rs.getString("ceticpt");
		this.fusion = rs.getString("fusion");*/
	}


	public void addEve1(ResultSet rs) throws SQLException {
		this.agde = rs.getString("age1");
		this.dev = rs.getString("dev");
		this.ncp = rs.getString("ncp1");
		this.dco = rs.getDate("dco");
		this.ope = rs.getString("ope");
		this.dva = rs.getDate("dva1");
		this.mon = rs.getDouble("mon1");
		this.sen = rs.getString("sen1");
		this.lib = rs.getString("lib1") != null && !rs.getString("lib1").trim().isEmpty() ? rs.getString("lib1") : rs.getString("lib2");
		//this.exo = rs.getString("exo");
		this.pie = rs.getString("pieo");
		this.des1 = rs.getString("lib1");
		this.des2 = rs.getString("lib2");
		this.des3 = rs.getString("lib3");
		this.des4 = rs.getString("lib4");
		this.des5 = rs.getString("lib5");
		this.uti = rs.getString("uti");
		this.eve = rs.getString("eve");
		this.agsa = rs.getString("agsa");
		this.pieo = rs.getString("pieo");
		this.eta = rs.getString("eta");
		this.cpl = rs.getString("clc1");
	}



	public void addEve2(ResultSet rs) throws SQLException {
		this.agde = rs.getString("age2");
		this.dev = rs.getString("dev");
		this.ncp = rs.getString("ncp2");
		this.dco = rs.getDate("dco");
		this.ope = rs.getString("ope");
		this.dva = rs.getDate("dva2");
		this.mon = rs.getDouble("mon2");
		this.sen = rs.getString("sen2");
		this.lib = rs.getString("lib1") != null && !rs.getString("lib1").trim().isEmpty() ? rs.getString("lib1") : rs.getString("lib2");
		//this.exo = rs.getString("exo");
		this.pie = rs.getString("pieo");
		this.des1 = rs.getString("lib1");
		this.des2 = rs.getString("lib2");
		this.des3 = rs.getString("lib3");
		this.des4 = rs.getString("lib4");
		this.des5 = rs.getString("lib5");
		this.uti = rs.getString("uti");
		this.eve = rs.getString("eve");
		this.agsa = rs.getString("agsa");
		this.pieo = rs.getString("pieo");
		this.eta = rs.getString("eta");
		this.cpl = rs.getString("clc2");
	}


	public void setDev(String dev){
		this.dev=dev;
	}

	public String getNcp(){
		return ncp;
	}

	public void setNcp(String ncp){
		this.ncp=ncp;
	}

	public String getSuf(){
		return suf;
	}

	public void setSuf(String suf){
		this.suf=suf;
	}

	public Date getDco(){
		return dco;
	}

	public void setDco(Date dco){
		this.dco=dco;
	}

	public String getOpe(){
		return ope;
	}

	public void setOpe(String ope){
		this.ope=ope;
	}

	public String getMvt(){
		return mvt;
	}

	public void setMvt(String mvt){
		this.mvt=mvt;
	}

	public String getSer(){
		return ser;
	}

	public void setSer(String ser){
		this.ser=ser;
	}

	public Date getDva(){
		return dva;
	}

	public void setDva(Date dva){
		this.dva=dva;
	}

	public Date getDin(){
		return din;
	}

	public void setDin(Date din){
		this.din=din;
	}

	public double getMon(){
		return mon;
	}

	public void setMon(double mon){
		this.mon=mon;
	}

	public String getSen(){
		return sen;
	}

	public void setSen(String sen){
		this.sen=sen;
	}

	public String getLib(){
		return lib;
	}

	public void setLib(String lib){
		this.lib=lib;
	}

	public String getExo(){
		return exo;
	}

	public void setExo(String exo){
		this.exo=exo;
	}

	public String getPie(){
		return pie;
	}

	public void setPie(String pie){
		this.pie=pie;
	}

	public String getDes1(){
		return des1;
	}

	public void setDes1(String des1){
		this.des1=des1;
	}

	public String getDes2(){
		return des2;
	}

	public void setDes2(String des2){
		this.des2=des2;
	}

	public String getDes3(){
		return des3;
	}

	public void setDes3(String des3){
		this.des3=des3;
	}

	public String getDes4(){
		return des4;
	}

	public void setDes4(String des4){
		this.des4=des4;
	}

	public String getDes5(){
		return des5;
	}

	public void setDes5(String des5){
		this.des5=des5;
	}

	public String getUti(){
		return uti;
	}

	public void setUti(String uti){
		this.uti=uti;
	}

	public String getUtf(){
		return utf;
	}

	public void setUtf(String utf){
		this.utf=utf;
	}

	public String getUta(){
		return uta;
	}

	public void setUta(String uta){
		this.uta=uta;
	}

	public String getEve(){
		return eve;
	}

	public void setEve(String eve){
		this.eve=eve;
	}

	public String getAgem(){
		return agem;
	}

	public void setAgem(String agem){
		this.agem=agem;
	}

	public Date getDag(){
		return dag;
	}

	public void setDag(Date dag){
		this.dag=dag;
	}

	public String getNcc(){
		return ncc;
	}

	public void setNcc(String ncc){
		this.ncc=ncc;
	}

	public String getSuc(){
		return suc;
	}

	public void setSuc(String suc){
		this.suc=suc;
	}

	public String getCpl(){
		return cpl;
	}

	public void setCpl(String cpl){
		this.cpl=cpl;
	}

	public Date getDdl(){
		return ddl;
	}

	public void setDdl(Date ddl){
		this.ddl=ddl;
	}

	public String getRlet(){
		return rlet;
	}

	public void setRlet(String rlet){
		this.rlet=rlet;
	}

	public String getUtl(){
		return utl;
	}

	public void setUtl(String utl){
		this.utl=utl;
	}

	public String getMar(){
		return mar;
	}

	public void setMar(String mar){
		this.mar=mar;
	}

	public Date getDech(){
		return dech;
	}

	public void setDech(Date dech){
		this.dech=dech;
	}

	public String getAgsa(){
		return agsa;
	}

	public void setAgsa(String agsa){
		this.agsa=agsa;
	}

	public String getAgde(){
		return agde;
	}

	public void setAgde(String agde){
		this.agde=agde;
	}

	public String getDevc(){
		return devc;
	}

	public void setDevc(String devc){
		this.devc=devc;
	}

	public double getMctv(){
		return mctv;
	}

	public void setMctv(double mctv){
		this.mctv=mctv;
	}

	public String getPieo(){
		return pieo;
	}

	public void setPieo(String pieo){
		this.pieo=pieo;
	}

	public String getIden(){
		return iden;
	}

	public void setIden(String iden){
		this.iden=iden;
	}

	public Date getDexa(){
		return dexa;
	}

	public void setDexa(Date dexa){
		this.dexa=dexa;
	}

	public String getModu(){
		return modu;
	}

	public void setModu(String modu){
		this.modu=modu;
	}

	public String getRefdos(){
		return refdos;
	}

	public void setRefdos(String refdos){
		this.refdos=refdos;
	}

	public String getLabel(){
		return label;
	}

	public void setLabel(String label){
		this.label=label;
	}

	public String getNat(){
		return nat;
	}

	public void setNat(String nat){
		this.nat=nat;
	}

	public String getEta(){
		return eta;
	}

	public void setEta(String eta){
		this.eta=eta;
	}

	public String getSchema(){
		return schema;
	}

	public void setSchema(String schema){
		this.schema=schema;
	}

	public String getCeticpt(){
		return ceticpt;
	}

	public void setCeticpt(String ceticpt){
		this.ceticpt=ceticpt;
	}

	public String getFusion(){
		return fusion;
	}

	public void setFusion(String fusion){
		this.fusion=fusion;
	}



	@Override
	public int compareTo(Bkhis o) {
		if(o.dco == null)
			return -1;
		return o.dco.compareTo(this.dco);
	}
}
