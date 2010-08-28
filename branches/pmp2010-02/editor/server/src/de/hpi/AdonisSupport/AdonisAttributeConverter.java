package de.hpi.AdonisSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AdonisAttributeConverter {

//TODO	Position (Type node, type connector)
	public static int CentimeterToPixel = 75;
	
	/**
	 * convert a area tag in Adonis to a useable format in Oryx 
	 * sets x, y, width and height if needed 
	 * @return a String[] with {x,y,w,h}
	 */
	public static String[] getArea(AdonisAttribute input){
		String[] rawBounds = null;
		String[] result = new String[4];
		if (input.getAdonisName().equalsIgnoreCase("world area")){
			//w:12.01cm h:14.5cm
			rawBounds = input.getElement().replaceAll("[a-zA-Z:]", "").split(" ");
			result[0] = "0";
			result[1] = "0";
			result[2] = "" + (int)(Float.parseFloat(rawBounds[0]) * CentimeterToPixel);
			result[3] = "" + (int)(Float.parseFloat(rawBounds[1]) * CentimeterToPixel);
		}
		if (input.getAdonisName().equalsIgnoreCase("viewable area")){
			//VIEW representation:graphic 
			//GRAPHIC x:-16 y:-16 w:904 h:891
			//TABLE "Process"
			String[] unfilterdRawBounds = input.getElement().replace("[a-zA-Z:]", "").split(" ");
			int counter = 0;
			for (String temp : unfilterdRawBounds){
				if (temp != null && temp != ""){
					rawBounds[counter] = temp;
					counter++;
				}
			}
			int xOffset = Integer.parseInt(rawBounds[0]);
			int yOffset = Integer.parseInt(rawBounds[1]);
			result[0] = "0";
			result[1] = "0";
			result[2] = "" + (int)((Float.parseFloat(rawBounds[2]) * CentimeterToPixel) - xOffset);
			result[3] = "" + (int)((Float.parseFloat(rawBounds[3]) * CentimeterToPixel) - yOffset);
 		}
		return result;
	}
	
	/**
	 * convert a date of Adonis to a useable date for Oryx
	 * @return 
	 */
	public static String getDateTime(AdonisAttribute input){
		String rawDate = input.getElement().replace(",", "");
		SimpleDateFormat converter = new SimpleDateFormat();
		String formatedDate = null;
		try {
			formatedDate = converter.format(converter.parse(rawDate));
		} catch (ParseException e) {
			System.err.println("AdonisAttributeConverter: could not convert "+input.getElement()+" to a valid date - use default");
			formatedDate = converter.format(new Date(System.currentTimeMillis()));
		}
		return formatedDate;
	}
}
