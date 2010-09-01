package de.hpi.AdonisSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Helper {
	private static SimpleDateFormat adonisDateTimeFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
	private static SimpleDateFormat adonisDateFormat = new SimpleDateFormat("dd.MM.yyyy");
	private static SimpleDateFormat oryxDateFormat = new SimpleDateFormat("MM/dd/yy");
	private static Random randomGenerator = new Random();
	
	public static String generateId(String prefix){
		return prefix+randomGenerator.nextInt();
	}
	
	/**
	 * converts a adonis date time string to a oryx date string</br>
	 * adonis format: dd.MM.yyyy, HH:mm -> oryx MM/dd/yy
	 * @param adonisDate
	 * @return
	 */
	public static String dateAdonisToOryx(String adonisDate, boolean withTime){
		Date date = null;
		try {
			date = withTime ? adonisDateTimeFormat.parse(adonisDate) : adonisDateFormat.parse(adonisDate);			
		} catch (ParseException e){
			Logger.e("wrong date format of "+date+" for import from Adonis",e);
			date = new Date(System.currentTimeMillis());
		}
		return oryxDateFormat.format(date);
	}
	
	public static String dateOryxToAdonis(String oryxDate,boolean withTime){
		Date date = null;
		try {
			date = oryxDateFormat.parse(oryxDate);
		} catch (ParseException e){
			Logger.e("wrong date format of "+date+" for import from Adonis",e);
			date = new Date(System.currentTimeMillis());
		}
		return withTime ? adonisDateTimeFormat.format(date) : adonisDateFormat.format(date);
	}
	
	public static String removeExpressionTags(String element){
		return element.replaceFirst("EXPR\\p{Space}*val\\:", "").replace("\"", "");
	}
}
