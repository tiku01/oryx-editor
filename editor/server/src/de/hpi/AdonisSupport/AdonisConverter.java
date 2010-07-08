package de.hpi.AdonisSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;


public class AdonisConverter {
	
	/**
	 * helper to read a file
	 * @param filePath
	 * @return
	 */
	public static String importFromFile(String filePath){
		File file = new File(filePath);
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line = null;
			StringBuilder content = new StringBuilder();
			while ((line = bufferedReader.readLine()) != null){
				content.append(line);
			}
			return content.toString();
		} catch (IOException e){
			System.err.println(e.getMessage());
			return e.getMessage();
		}
	}
	/**
	 * remove starting tags of adonis xml-document
	 * <?xml version="1.0" encoding="UTF-8"?>
	 * <!DOCTYPE ADOXML SYSTEM "adoxml31.dtd">
	 * @param xmls
	 * @return
	 */
	public String filterXML(String xml){
//		<?xml version="1.0" encoding="UTF-8"?>
//		<!DOCTYPE ADOXML SYSTEM "adoxml31.dtd">
		String xmlWithoutXMLTag = xml.replaceFirst("<\\?xml[^\\?]*\\?>", "");
		String xmlWithoutDoctype = xmlWithoutXMLTag.replaceFirst("<\\![^\\>]*>","");
		return xmlWithoutDoctype;
	}
	
	/**
	 * import a xml file and convert it into a json 
	 * @param xml
	 * @return
	 */
	public String importXML(String xml){
//		System.err.println("importXML\n"+xml);
		String filteredXML = filterXML(xml);
		StringReader stringReader = new StringReader(filteredXML);
		
		Xmappr xmappr = new Xmappr(AdonisXML.class);
		AdonisXML xmlModelCollection = (AdonisXML) xmappr.fromXML(stringReader);
		xmlModelCollection.collectAttributes();
		
		Vector<JSONObject> models = null;
		try {
//			xmlModelCollection.write(model);
			models = xmlModelCollection.writeDiagrams();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return models.elementAt(0).toString();
	}
	
	public char[] exportXML(String json) {
//		System.err.println("exportXML\n"+json);
		return new char[]{'a','b'};
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AdonisConverter ac = new AdonisConverter();
		System.out.println(
				ac.importXML(importFromFile("D:\\Desktop\\Adonis\\Bank CompanyMap"+/* Referenced*/".xml")));
		
		
		
	}
	

}
