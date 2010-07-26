package de.hpi.AdonisSupport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import javax.swing.text.html.HTMLDocument.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * this class provides access to the configuration file
 * to provide support for different adonis versions (i.e. german or english)
 * @author logge002
 *
 */
public class Configurator {
	
	private static String configurationPath = "D:\\Development\\Eclipse Oryx\\Oryx\\editor\\server\\src\\de\\hpi\\AdonisSupport\\CompatibilityMappings.json";
	
	private static String attributeTypePath = "D:\\Development\\Eclipse Oryx\\Oryx\\editor\\server\\src\\de\\hpi\\AdonisSupport\\AttributeTypes.json";
	
	private static JSONObject configuration = null;
	private static JSONObject attributeTypes = null;
	
	private static void readInCompatibilityFile(){
		FileInputStream fileStream = null;
		InputStreamReader fileStreamReader  = null;
		BufferedReader bufferedReader  = null;
		try {
			fileStream = new FileInputStream(configurationPath);
			fileStreamReader = new InputStreamReader(fileStream,"UTF-8");
			bufferedReader = new BufferedReader(fileStreamReader);
			
			String line = null;
			StringBuilder content = new StringBuilder();

			while ((line = bufferedReader.readLine()) != null){
				content.append(line);
			}
			//System.out.println(content.toString());
			configuration = new JSONObject(content.toString());
			
			bufferedReader.close();
			fileStreamReader.close();
			fileStream.close();
			
		} catch (Exception e){
			Log.e("Could not initialize config file for Adonis compatibility");
			e.printStackTrace();
			if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
			if (fileStreamReader != null )
				try {
					fileStreamReader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			if (fileStream != null )
				try {
					fileStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
		
	}
	
	private static void readInAttributeTypesFile(){
		FileInputStream fileStream = null;
		InputStreamReader fileStreamReader  = null;
		BufferedReader bufferedReader  = null;
		try {
			fileStream = new FileInputStream(attributeTypePath);
			fileStreamReader = new InputStreamReader(fileStream,"UTF-8");
			bufferedReader = new BufferedReader(fileStreamReader);
			
			String line = null;
			StringBuilder content = new StringBuilder();

			while ((line = bufferedReader.readLine()) != null){
				content.append(line);
			}
			//System.out.println(content.toString());
			attributeTypes = new JSONObject(content.toString());
			
		} catch (Exception e){
			Log.e("Could not initialize attributeTypes file for Adonis compatibility");
			e.printStackTrace();
			if (bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				} 
			if (fileStreamReader != null )
				try {
					fileStreamReader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			if (fileStream != null )
				try {
					fileStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		}
	}

	private static void writeOutAttributeTypesFile(){
		File file = null; 
		FileOutputStream fileOutputStream = null;
		PrintStream printStream = null;
		try {
			file = new File(attributeTypePath);
			fileOutputStream = new FileOutputStream(file);
			printStream = new PrintStream(fileOutputStream);
			printStream.print(attributeTypes.toString());
			Log.v("aktualized attribute Types file");
		} catch (IOException e){
			Log.e("could not write to attributeTypes file\n"+e.getMessage());
			e.printStackTrace();
		}
		if (fileOutputStream != null)
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		if (printStream != null) printStream.close();
	}
	
	
	
	
	public static void setAttributeType(String adonisName,String type){
		if  (attributeTypes == null){
			readInAttributeTypesFile();
		}
		String oryxName = getTranslationToOryx(adonisName);
		if (!attributeTypes.has(oryxName)){
			try {
				attributeTypes.put(oryxName,type);
				writeOutAttributeTypesFile();
			} catch (Exception e){
				Log.e("could not add "+oryxName+" to AttributeTypes File\n"+e.getMessage());
			}
		} else 
			try {
				if (!attributeTypes.getString(oryxName).equals(type)) {
					Log.w("types for "+oryxName+" are not equal - " +
							"written "+attributeTypes.getString(oryxName)+" - " +
							"new "+type);
				}
			} catch (JSONException e){
				Log.e("unexpected exception in setAttributeType\n"+e.getMessage());
				e.printStackTrace();
			}
	}
	
	public static String getAttibuteType(String adonisName){
		if  (attributeTypes == null){
			readInAttributeTypesFile();
		}
		// fallback
		if (attributeTypes == null){
			return "STRING";
		}
		String oryxName = getTranslationToOryx(adonisName);
		return attributeTypes.optString(oryxName,"STRING");
			
	}
	
	public static String getLanguage(String adonisName){
		if (configuration == null){
			readInCompatibilityFile();
		}
		try {
			JSONObject translation = configuration.getJSONObject("translation");
			JSONObject stencil = null;
			String[] oryxNames = JSONObject.getNames(translation);
			//	get all translated identifiers - look up where the adonisName belongs to
			for (String oryxName : oryxNames){
				stencil = translation.getJSONObject(oryxName);
				for (String key : JSONObject.getNames(stencil)){
					if (key.equalsIgnoreCase(adonisName)){
						return stencil.getString(key);
					}
				}
			}
		} catch (JSONException e ){
			Log.e("Could not detect language for "+adonisName+"\n"+e.getMessage());
		}
		return "en";
	}
	
	public static String getTranslationToOryx(String adonisName){
		if (configuration == null){
			readInCompatibilityFile();
		}
		try {
			JSONObject translation = configuration.getJSONObject("translation");
			JSONObject stencil = null;
			String[] oryxNames = JSONObject.getNames(translation);
			//get all translated identifiers - look up where the adonisName belongs to
			for (String oryxName : oryxNames){
				stencil = translation.getJSONObject(oryxName);
				for (String key : JSONObject.getNames(stencil)){
					if (key.equalsIgnoreCase(adonisName)){
						return oryxName;
					}
				}
			}
		} catch (JSONException e){
			Log.e("Could not detect translation of "+adonisName+"\n"+e.getMessage());
		}
		return adonisName;	
	}
	public static String getTranslationToAdonis(String oryxName,String language) {
		if (configuration == null){
			readInCompatibilityFile();
		}
		try {
			JSONObject translation = configuration.getJSONObject("translation");
			JSONObject stencils = translation.getJSONObject(oryxName);
			JSONArray translations = stencils.names();
			String fallBackTranslation = null;
			for (int i = 0; i < translations.length(); i++){
				String adonisName =  translations.getString(i);
				JSONArray languages = stencils.getJSONArray(adonisName);
				for (int j = 0; j < languages.length(); j++){
					if ("en".equalsIgnoreCase(languages.getString(j))){
						fallBackTranslation = adonisName;
					}
					if (language.equalsIgnoreCase(languages.getString(j))){
						return adonisName;
					}
				}
			}
			if (fallBackTranslation != null){
				return fallBackTranslation;
			}
		} catch (JSONException e){
			Log.e("Could not detect translation of "+oryxName+"\n"+e.getMessage());
		}
		return oryxName;
		
	}
	public static JSONObject getStencilConfiguration(String oryxName) throws JSONException{
		if (configuration == null){
			readInCompatibilityFile();
		}
		JSONObject standard = configuration.getJSONObject("configuration");
		return standard.getJSONObject(oryxName);
	}

	
	


}
