package com.afb.dpd.mobilemoney.jpa.tools;

public class bkevec {

	public bkevec() {}

	
	private String age = "00001"; // Code de l'agence CHAR 5 1 bkage age
	private String ope; // Code de l'opération CHAR 3 2 bkeve ope
	private String eve; // Numéro d'évènement CHAR 6 3 bkeve eve
	private String nat = "COMVRT"; // Nature de frais/commission/taxe CHAR 6 4 t051NaturCondition cacc
	private String iden = "COM01"; // Identification CHAR 5
	private String typc = "D"; // Type de commission CHAR 1
	private String devr = "001"; // Code de la devise de référence condition CHAR 3 t005DevisesN cacc
	private Double mcomr = 0d; // Montant en devise de référence DECIMAL 19 - 4
	private Double txref = 1d; // Taux de change devise de référence/devise opé. DECIMAL 18 - 10
	private Double mcomc = 0d; // Montant en devise compte DECIMAL 19 - 4
	private Double mcomn = 0d; // Montant en devise nationale DECIMAL 19 - 4
	private Double mcomt = 0d; // Montant en devise de transaction DECIMAL 19 - 4
	private String tax = "O"; // Taxable (O/N) CHAR 1
	private Double tcom = 0d; // Taux de commission/taxe appliqué DECIMAL 15 - 7
	
	/**
	 * @param age
	 * @param ope
	 * @param eve
	 * @param nat
	 * @param iden
	 * @param typc
	 * @param devr
	 * @param mcomr
	 * @param txref
	 * @param mcomc
	 * @param mcomn
	 * @param mcomt
	 * @param tax
	 * @param tcom
	 */
	public bkevec(String age, String ope, String eve, String nat, String iden,
			String typc, String devr, Double mcomr, Double txref, Double mcomc,
			Double mcomn, Double mcomt, String tax, Double tcom) {
		super();
		this.age = age;
		this.ope = ope;
		this.eve = eve;
		this.nat = nat;
		this.iden = iden;
		this.typc = typc;
		this.devr = devr;
		this.mcomr = mcomr;
		this.txref = txref;
		this.mcomc = mcomc;
		this.mcomn = mcomn;
		this.mcomt = mcomt;
		this.tax = tax;
		this.tcom = tcom;
	}

	/**
	 * @param ope
	 * @param eve
	 * @param mcomr
	 * @param tax
	 */
	public bkevec(String ope, String eve, Double mcomr, String tax) {
		super();
		this.ope = ope;
		this.eve = eve;
		this.mcomr = mcomr;
		this.mcomc = mcomr;
		this.mcomn = mcomr;
		this.mcomt = mcomr;
		this.tax = tax;
	}
	
	public String getSaveQuery() {
		return "insert into bkevec (age, ope, eve, nat, iden, typc, devr, mcomr, txref, mcomc, mcomn, mcomt, tax, tcom) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	}
	
	public Object[] getQueryValues(){
		Object[] values = new Object[14];
		
		values[0] = age;
		values[1] = ope;
		values[2] = eve;
		values[3] = nat;
		values[4] = iden;
		values[5] = typc;
		values[6] = devr;
		values[7] = mcomr;
		values[8] = txref;
		values[9] = mcomc;
		values[10] = mcomn;
		values[11] = mcomt;
		values[12] = tax;
		values[13] = tcom;
		
		return values;
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
	 * @return the typc
	 */
	public String getTypc() {
		return typc;
	}
	/**
	 * @param typc the typc to set
	 */
	public void setTypc(String typc) {
		this.typc = typc;
	}
	/**
	 * @return the devr
	 */
	public String getDevr() {
		return devr;
	}
	/**
	 * @param devr the devr to set
	 */
	public void setDevr(String devr) {
		this.devr = devr;
	}
	/**
	 * @return the mcomr
	 */
	public Double getMcomr() {
		return mcomr;
	}
	/**
	 * @param mcomr the mcomr to set
	 */
	public void setMcomr(Double mcomr) {
		this.mcomr = mcomr;
	}
	/**
	 * @return the txref
	 */
	public Double getTxref() {
		return txref;
	}
	/**
	 * @param txref the txref to set
	 */
	public void setTxref(Double txref) {
		this.txref = txref;
	}
	/**
	 * @return the mcomc
	 */
	public Double getMcomc() {
		return mcomc;
	}
	/**
	 * @param mcomc the mcomc to set
	 */
	public void setMcomc(Double mcomc) {
		this.mcomc = mcomc;
	}
	/**
	 * @return the mcomn
	 */
	public Double getMcomn() {
		return mcomn;
	}
	/**
	 * @param mcomn the mcomn to set
	 */
	public void setMcomn(Double mcomn) {
		this.mcomn = mcomn;
	}
	/**
	 * @return the mcomt
	 */
	public Double getMcomt() {
		return mcomt;
	}
	/**
	 * @param mcomt the mcomt to set
	 */
	public void setMcomt(Double mcomt) {
		this.mcomt = mcomt;
	}
	/**
	 * @return the tax
	 */
	public String getTax() {
		return tax;
	}
	/**
	 * @param tax the tax to set
	 */
	public void setTax(String tax) {
		this.tax = tax;
	}
	/**
	 * @return the tcom
	 */
	public Double getTcom() {
		return tcom;
	}
	/**
	 * @param tcom the tcom to set
	 */
	public void setTcom(Double tcom) {
		this.tcom = tcom;
	}
	
}
