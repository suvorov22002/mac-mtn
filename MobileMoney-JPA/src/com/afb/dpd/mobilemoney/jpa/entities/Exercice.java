package com.afb.dpd.mobilemoney.jpa.entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.NotNull;

import com.afb.dpd.mobilemoney.jpa.enums.ExerciceStatus;
import com.yashiro.persistence.utils.annotations.validator.SizeDAOValidator;
import com.yashiro.persistence.utils.annotations.validator.SizeDAOValidators;
import com.yashiro.persistence.utils.dao.constant.DAOMode;


/**
 * Classe representant l' exercice
 * @author Francis Konchou
 * @version 1.0
 */
@SizeDAOValidators({
	@SizeDAOValidator(mode = DAOMode.SAVE,   expr = "from Exercice m where (m.code = ${code})", max = 0, message = "Exercice.save.code.exist"),
	@SizeDAOValidator(mode = DAOMode.UPDATE,   expr = "from Exercice m where (m.id != ${id}) and (m.code = ${code})", max = 0, message = "Exercice.save.code.exist")
})
@Entity(name = "Exercice")
@Table(name = "MoMo_EXERCICE")
public class Exercice implements Serializable{
	
	/**
	 * ID Generer par eclise
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * ID de l'enregistrement
	 */
	@Id
	@Column(name = "ID")
	@SequenceGenerator(name="Seq_ExerciceEHR", sequenceName="SEQ_EXERCICEEHR", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Seq_ExerciceEHR")
	private Long id;
	
	/**
	 * Code de l'exercice
	 */
	@Column(name = "EXO_CODE", unique = true)
	@NotNull(message = "Exercice.code.notNull")
	private Integer code = 0000;
	
	/**
	 * Date debut
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "E_STARTDATE")
	@NotNull(message = "Exercice.startDate.notNull")
	private Date startDate;
	
	/**
	 * Date fin
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "E_ENDDATE")
	@NotNull(message = "Exercice.endDate.notNull")
	private Date endDate;
	
	/**
	 * Statut de l'exercice
	 */
	@Enumerated(EnumType.STRING)
	@Column(name = "E_STATUS")
	@NotNull(message = "Exercice.status.notNull")
	private ExerciceStatus status = ExerciceStatus.WAITING;
		
	/**
	 * Etat Mois de l'exercice
	 */
	@Column(name = "E_JAN" )
	private Boolean january = Boolean.FALSE;
	
	/**
	 * Etat Mois de l'exercice
	 */
	@Column(name = "E_FEB" )
	private Boolean february = Boolean.FALSE;
	
	/**
	 * Etat Mois de l'exercice
	 */
	@Column(name = "E_MARCH" )
	private Boolean march = Boolean.FALSE;
	
	/**
	 * Etat Mois de l'exercice
	 */
	@Column(name = "E_APRIL" )
	private Boolean april = Boolean.FALSE;
	
	/**
	 * Etat Mois de l'exercice
	 */
	@Column(name = "E_MAY" )
	private Boolean may = Boolean.FALSE;
	
	/**
	 * Etat Mois de l'exercice
	 */
	@Column(name = "E_JUNE" )
	private Boolean june = Boolean.FALSE;
	
	/**
	 * Etat Mois de l'exercice
	 */
	@Column(name = "E_JULY" )
	private Boolean july = Boolean.FALSE;
	
	/**
	 * Etat Mois de l'exercice
	 */
	@Column(name = "E_AUG" )
	private Boolean august = Boolean.FALSE;
	
	/**
	 * Etat Mois de l'exercice
	 */
	@Column(name = "E_SEP" )
	private Boolean september = Boolean.FALSE;
	
	
	/**
	 * Etat Mois de l'exercice
	 */
	@Column(name = "E_OCT" )
	private Boolean october = Boolean.FALSE;
	
	/**
	 * Etat Mois de l'exercice
	 */
	@Column(name = "E_NOV" )
	private Boolean november = Boolean.FALSE;
	
	/**
	 * Etat Mois de l'exercice
	 */
	@Column(name = "E_DEC" )
	private Boolean december = Boolean.FALSE;

	
	/**
	 * constructeur par default
	 */
	public Exercice(){
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.MONTH, Calendar.JANUARY);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		this.startDate =  cal.getTime();
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DAY_OF_MONTH, 31);
		this.endDate =  cal.getTime();
		
	}
	
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the status
	 */
	public ExerciceStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ExerciceStatus status) {
		this.status = status;
	}

	/**
	 * @return the january
	 */
	public Boolean getJanuary() {
		return january;
	}

	/**
	 * @param january the january to set
	 */
	public void setJanuary(Boolean january) {
		this.january = january;
	}

	/**
	 * @return the february
	 */
	public Boolean getFebruary() {
		return february;
	}

	/**
	 * @param february the february to set
	 */
	public void setFebruary(Boolean february) {
		this.february = february;
	}

	/**
	 * @return the march
	 */
	public Boolean getMarch() {
		return march;
	}

	/**
	 * @param march the march to set
	 */
	public void setMarch(Boolean march) {
		this.march = march;
	}

	/**
	 * @return the april
	 */
	public Boolean getApril() {
		return april;
	}

	/**
	 * @param april the april to set
	 */
	public void setApril(Boolean april) {
		this.april = april;
	}

	/**
	 * @return the may
	 */
	public Boolean getMay() {
		return may;
	}

	/**
	 * @param may the may to set
	 */
	public void setMay(Boolean may) {
		this.may = may;
	}

	/**
	 * @return the june
	 */
	public Boolean getJune() {
		return june;
	}

	/**
	 * @param june the june to set
	 */
	public void setJune(Boolean june) {
		this.june = june;
	}

	/**
	 * @return the july
	 */
	public Boolean getJuly() {
		return july;
	}

	/**
	 * @param july the july to set
	 */
	public void setJuly(Boolean july) {
		this.july = july;
	}

	/**
	 * @return the august
	 */
	public Boolean getAugust() {
		return august;
	}

	/**
	 * @param august the august to set
	 */
	public void setAugust(Boolean august) {
		this.august = august;
	}

	/**
	 * @return the september
	 */
	public Boolean getSeptember() {
		return september;
	}

	/**
	 * @param september the september to set
	 */
	public void setSeptember(Boolean september) {
		this.september = september;
	}

	/**
	 * @return the october
	 */
	public Boolean getOctober() {
		return october;
	}

	/**
	 * @param october the october to set
	 */
	public void setOctober(Boolean october) {
		this.october = october;
	}

	/**
	 * @return the november
	 */
	public Boolean getNovember() {
		return november;
	}

	/**
	 * @param november the november to set
	 */
	public void setNovember(Boolean november) {
		this.november = november;
	}

	/**
	 * @return the december
	 */
	public Boolean getDecember() {
		return december;
	}

	/**
	 * @param december the december to set
	 */
	public void setDecember(Boolean december) {
		this.december = december;
	}

	

}
