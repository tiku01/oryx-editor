package de.hpi.AdonisSupport;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * this class provides access to the configuration file
 * to provide support for different adonis versions (i.e. german or english)
 * @author logge002
 *
 */
public class Configurator {
	
	private static String filePath = "D:\\Development\\Eclipse Oryx\\Oryx\\editor\\server\\src\\de\\hpi\\AdonisSupport\\CompatibilityMappings.json";
	
	private static JSONObject configurationTree = null;
	
	private static void readInCompatibilityFile(){
		try {
			FileInputStream fileStream = new FileInputStream(filePath);
			InputStreamReader fileStreamReader = new InputStreamReader(fileStream,"UTF-8");
			BufferedReader bufferedReader = new BufferedReader(fileStreamReader);
			
			String line = null;
			StringBuilder content = new StringBuilder();

			while ((line = bufferedReader.readLine()) != null){
				content.append(line);
			}
			//System.out.println(content.toString());
			configurationTree = new JSONObject(content.toString());
			
		} catch (Exception e){
			Log.e("Could not initialize config file for Adonis compatibility");
			e.printStackTrace();
		}
	}
	
	
	
	public static String getLanguage(String adonisName){
		if (configurationTree == null){
			readInCompatibilityFile();
		}
		try {
			JSONObject translation = configurationTree.getJSONObject("translation");
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
		if (configurationTree == null){
			readInCompatibilityFile();
		}
		try {
			JSONObject translation = configurationTree.getJSONObject("translation");
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
	
	public static JSONObject getStencilConfiguration(String oryxName) throws JSONException{
		if (configurationTree == null){
			readInCompatibilityFile();
		}
		JSONObject standard = configurationTree.getJSONObject("configuration");
		return standard.getJSONObject(oryxName);
	}


}
