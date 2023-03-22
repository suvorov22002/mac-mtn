package com.afb.dpd.mobilemoney.jpa.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	
public 	final static String monthpatern =  "MMMM";
	
	/** The Constant DATE_SLASH_FORMAT_SHOET. */
	public 	final static String  DATE_SLASH_FORMAT_SHOET = "dd/MM/yyyy";
	
	/** The Constant DATE_MINUS_FORMAT_SHORT. */
	public 	final static String  DATE_MINUS_FORMAT_SHORT = "dd-MM-yyyy";
	
	/** The Constant DATE_MINUS_FORMAT_SHORT. */
	public 	final static String  DATE_MINUS_FORMAT_SINGLE = "ddMMyyyy";
	
	/** The Constant DATE_TIME_MINUS_FORMAT_LONG. */
	public final static String DATE_TIME_MINUS_FORMAT_LONG="dd-MM-yyyy HH:mm";
	
	/** The Constant DATE_TIME_MINUS_FORMAT_LONG. */
	public final static String DATE_TIME_MINUS_FORMAT_="dd-MM-yyyy";
	
	/** The Constant DATE_TIME_MINUS_FORMAT_LONG. */
	public final static String DATE_HOUR_FORMAT_SINGLE="HH:mm:ss";
	
	/** The Constant DATE_TIME_MINUS_FORMAT_LONG. */
	public final static String DATE_HOUR_FORMAT_MOMO="yyyy-MM-dd HH:mm:ss";

	public final static String DATE_HOUR_FORMAT_UP="yyyy-MM-dd-HH:mm";
	
	/** The Constant DATE_TIME_MINUS_FORMAT_LONG. */
	public final static String DATE_HOUR_FORMAT="dd/MM/yyyy HH':'mm':'ss";
	
	/** The Constant DATE_TIME_MINUS_FORMAT_LONG. */
	public final static String DATE_HOUR_FORMAT_TT="yyMMddHHmmss";
	
	/** The Constant DATE_TIME_MINUS_FORMAT_LONG. */
	public final static String DATE_HOUR_FORMAT_TT_SH ="yyMMdd";
	
	/** The date. */
	private Date date;
	
	/**
	 * Instantiates a new date util.
	 * 
	 * @param date
	 *            the date
	 */
	public DateUtil(Date date){
		if(date == null){
			date = new Date();
		}
		this.date = date;
	}
	
		
	public static Long now(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_HOUR_FORMAT_TT);
		String format = simpleDateFormat.format(new Date());
		return Long.valueOf(format) ;
	}
	
	public static String nowshort(){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_HOUR_FORMAT_TT_SH);
		String format = simpleDateFormat.format(new Date());
		return format ;
	}
	
	/**
	 * Begin of day.
	 * 
	 * @return the date
	 */
	public Date beginOfDay(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.date);
		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);	
		Date startDate = calendar.getTime();
		return startDate;
	}
	
	/**
	 * End of day.
	 * 
	 * @return the date
	 */
	public Date endOfDay(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.date);
		
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Date endDate = calendar.getTime();
		return endDate;
	}
	
	/**
	 * Format.
	 * 
	 * @param date
	 *            the date
	 * @param patern
	 *            the patern
	 * @return the string
	 */
	public static String  format(Date date , String patern){
		if(date ==null) return null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patern);
		String format = simpleDateFormat.format(date);
		return format ;
	}
	
	
	/**
	 * Parses the.
	 * 
	 * @param stringFormat
	 *            the string format
	 * @param patern
	 *            the patern
	 * @return the date
	 */
	public static Date parse(String stringFormat,String patern){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patern);
		Date date =  null ;
		try {
			date  = simpleDateFormat.parse(stringFormat);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
		return date;
	}
}
