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
 * TODO translation and lookup process needs to be reworked
 *
 */
@SuppressWarnings("unused")
public class Configurator {
	
	private static String configurationPath = "D:\\Development\\Eclipse Oryx\\Oryx\\editor\\server\\src\\de\\hpi\\AdonisSupport\\CompatibilityMappings.json";
	
	private static String attributeTypePath = "D:\\Development\\Eclipse Oryx\\Oryx\\editor\\server\\src\\de\\hpi\\AdonisSupport\\AttributeTypes.json";
	
	private static JSONObject configuration = null;
	private static JSONObject attributeTypes = null;
	
	private static void readInCompatibilityFile(){
//		FileInputStream fileStream = null;
//		InputStreamReader fileStreamReader  = null;
//		BufferedReader bufferedReader  = null;
//		try {
//			fileStream = new FileInputStream(configurationPath);
//			fileStreamReader = new InputStreamReader(fileStream,"UTF-8");
//			bufferedReader = new BufferedReader(fileStreamReader);
//			
//			String line = null;
//			StringBuilder content = new StringBuilder();
//
//			while ((line = bufferedReader.readLine()) != null){
//				content.append(line);
//			}
//			//System.out.println(content.toString());
//			configuration = new JSONObject(content.toString());
//			
//			bufferedReader.close();
//			fileStreamReader.close();
//			fileStream.close();
		
		try {
			//TODO needs a good place on server file system
			configuration = new JSONObject("{ 	\"translation\": {	\"world area\":{	\"World area\": [\"en\"],	\"Weltkoordinaten\": [\"de\"]	},	\"process\":{	\"Prozeß\": [\"de\"],	\"Process\": [\"en\",\"us\"]	},	\"note\":{	\"Note\": [\"en\"]	},	\"performance indicator overview\": {	\"Performance indicator overview\": [\"en\"]	},			\"performance\": {	\"Leistung\": [\"de\"],	\"Performance\": [\"en\"]	},	\"has process\": {	\"Has process\": [\"en\"],	\"Hat Prozeß\": [\"de\"]	},	\"has note\": {	\"has Note\": [\"en\"]	},	\"value flow\": {	\"Value flow\": [\"en\"]	},	\"has cross-reference\": {	\"Has cross-reference\": [\"en\"],	\"Hat Querverweis\":[\"de\"]	},	\"is inside\": {	\"is inside\":[\"en\"],	\"ist innerhalb\": [\"de\"]	},	\"swimlane (vertical)\": {	\"Schwimmbahn (senkrecht)\":[\"de\"],	\"Swimlane (vertical)\":[\"en\"]	},	\"swimlane (horizontal)\": {	\"Schwimmbahn (waagerecht)\": [\"de\"],	\"Swimlane (horizontal)\":[\"en\"]	},	\"aggregation\": {	\"Aggregation\": [\"de\",\"en\"]	},	\"external partner\": {	\"External partner\": [\"en\"]	},			\"company map\": {	\"Company map\": [\"en\"],	\"Prozeßlandkarte\": [\"de\"]	} 	}, 	\"configuration\": {	\"model\": {	\"language\":\"en\"	},	\"performance\": {	\"type\":\"node\",	\"offsetPercentageX\":\"50\",	\"offsetPercentageY\":\"50\",	\"w\":3.26,	\"h\":1.40	},	\"process\": {	\"type\":\"node\",	\"offsetPercentageX\":\"50\",	\"offsetPercentageY\":\"50\",	\"w\":3.25,	\"h\":1.40	},	\"aggregation\": {	\"type\":\"node\",	\"offsetPercentageX\":\"0\",	\"offsetPercentageY\":\"0\",	\"w\":6.00,	\"h\":9.00	},	\"actor\": {	\"type\":\"node\",	\"offsetPercentageX\":\"50\",	\"offsetPercentageY\":\"50\",	\"w\":1.20,	\"h\":1.75	},	\"external partner\": {	\"type\":\"node\",	\"offsetPercentageX\":\"50\",	\"offsetPercentageY\":\"50\",	\"w\":1.20,	\"h\":1.75	},	\"external partner\": {	\"type\":\"node\",	\"offsetPercentageX\":\"50\",	\"offsetPercentageY\":\"50\",	\"w\":1.00,	\"h\":1.39	},	\"performance indicator\": {	\"type\":\"node\",	\"offsetPercentageX\":\"50\",	\"offsetPercentageY\":\"50\",	\"w\":1.40,	\"h\":1.40	},	\"performance indicator overview\": {	\"type\":\"node\",	\"offsetPercentageX\":\"66\",	\"offsetPercentageY\":\"50\",	\"w\":0.50,	\"h\":0.60	},	\"cross reference\": {	\"type\":\"node\",	\"offsetPercentageX\":\"50\",	\"offsetPercentageY\":\"50\",	\"w\":3.30,	\"h\":1.00	},	\"note\": {	\"type\":\"node\",	\"offsetPercentageX\":\"0\",	\"offsetPercentageY\":\"0\",	\"w\":4.50,	\"h\":4.50	} 	} }");
		} catch (JSONException e) {
			configuration = new JSONObject();
			e.printStackTrace();
		}
//		} catch (Exception e){
//			Log.e("Could not initialize config file for Adonis compatibility");
//			e.printStackTrace();
//			if (bufferedReader != null)
//				try {
//					bufferedReader.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				} 
//			if (fileStreamReader != null )
//				try {
//					fileStreamReader.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//			if (fileStream != null )
//				try {
//					fileStream.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//		}
		
	}
	
	private static void readInAttributeTypesFile(){
		FileInputStream fileStream = null;
//		InputStreamReader fileStreamReader  = null;
//		BufferedReader bufferedReader  = null;
//		try {
//			fileStream = new FileInputStream(attributeTypePath);
//			fileStreamReader = new InputStreamReader(fileStream,"UTF-8");
//			bufferedReader = new BufferedReader(fileStreamReader);
//			
//			String line = null;
//			StringBuilder content = new StringBuilder();
//
//			while ((line = bufferedReader.readLine()) != null){
//				content.append(line);
//			}
//			attributeTypes = new JSONObject(content.toString());
			try {
				//TODO needs a good place in the server file system
				attributeTypes = new JSONObject("{\"Reviewed by\":\"STRING\",\"Kommentar\":\"STRING\",\"Font size\":\"INTEGER\",\"Number+of+objects+and+relations\":\"INTEGER\",\"Ermittelte Bearbeitungszeit\":\"EXPRESSION\",\"Model type\":\"ENUMERATION\",\"Ermittelte Wartezeit\":\"EXPRESSION\",\"External+tool+coupling\":\"STRING\",\"World+area\":\"STRING\",\"Namen anzeigen\":\"ENUMERATION\",\"Sprache\":\"EXPRESSION\",\"Aggregated+costs\":\"EXPRESSION\",\"Reihenfolge\":\"INTEGER\",\"Display name\":\"ENUMERATION\",\"Creation+date\":\"STRING\",\"Schriftstil\":\"ENUMERATION\",\"Display name and reference\":\"ENUMERATION\",\"Representation\":\"ENUMERATION\",\"Score\":\"EXPRESSION\",\"Keywords\":\"STRING\",\"Open questions\":\"STRING\",\"Open+questions\":\"STRING\",\"Lines\":\"ENUMERATION\",\"Visualise+referenced+IT+system+elements\":\"INTEGER\",\"Aggregated+resting+time\":\"EXPRESSION\",\"Update table\":\"PROGRAMCALL\",\"Zoom\":\"INTEGER\",\"Connector+marks\":\"STRING\",\"Comment\":\"STRING\",\"Current+page+layout\":\"STRING\",\"Visualization\":\"ENUMERATION\",\"probe_overview_ref\":\"EXPRESSION\",\"Display+name+and+reference\":\"ENUMERATION\",\"Color\":\"STRING\",\"Anzahl der Objekte und Beziehungen\":\"INTEGER\",\"Change counter\":\"INTEGER\",\"Context+of+version\":\"STRING\",\"VisibleAttrs\":\"STRING\",\"Zugriffsstatus\":\"ENUMERATION\",\"Update+table\":\"PROGRAMCALL\",\"Display status\":\"INTEGER\",\"refObjectNameEN_2\":\"EXPRESSION\",\"Current value\":\"EXPRESSION\",\"Raster\":\"STRING\",\"Aggregated+transport+time\":\"EXPRESSION\",\"Aggregated resting time\":\"EXPRESSION\",\"Current page layout\":\"STRING\",\"Aggregated transport time\":\"EXPRESSION\",\"Referenzierte IT-Systemelemente visualisieren\":\"INTEGER\",\"Aggregated+personnel+costs\":\"EXPRESSION\",\"Creation date\":\"STRING\",\"Author\":\"STRING\",\"Version+number\":\"STRING\",\"Typ\":\"STRING\",\"Abgenommen am\":\"STRING\",\"refObjectNameEN\":\"EXPRESSION\",\"Fontcolor\":\"EXPRESSION\",\"Reviewed+by\":\"STRING\",\"Aggregated waiting time\":\"EXPRESSION\",\"Aggregated+waiting+time\":\"EXPRESSION\",\"Grid\":\"STRING\",\"Number of objects and relations\":\"INTEGER\",\"probe_overview_status\":\"EXPRESSION\",\"Connector number\":\"INTEGER\",\"Linienart\":\"ENUMERATION\",\"Change+counter\":\"INTEGER\",\"Planning period\":\"ATTRPROFREF\",\"_exprMonochrome_\":\"EXPRESSION\",\"Graphical representation\":\"ENUMERATION\",\"External+process\":\"ENUMERATION\",\"Externe Toolanbindung\":\"STRING\",\"Aggregated personnel costs\":\"EXPRESSION\",\"Viewable area\":\"STRING\",\"Reviewed on\":\"STRING\",\"Insertion\":\"ENUMERATION\",\"calc_probe_overview_visualization\":\"EXPRESSION\",\"Display current value\":\"INTEGER\",\"Date last changed\":\"STRING\",\"Language\":\"EXPRESSION\",\"Schriftfarbe\":\"EXPRESSION\",\"Schriftgröße\":\"INTEGER\",\"Positions\":\"STRING\",\"Last+user\":\"STRING\",\"Wasserzeichen anzeigen\":\"ENUMERATION\",\"Updated\":\"EXPRESSION\",\"Display score\":\"INTEGER\",\"Base+name\":\"STRING\",\"Database access\":\"ATTRPROFREF\",\"Aggregated cycle time\":\"EXPRESSION\",\"Ermittelte Kosten\":\"EXPRESSION\",\"Contact person\":\"STRING\",\"fontcolor\":\"EXPRESSION\",\"Base name\":\"STRING\",\"Abgenommen von\":\"STRING\",\"Sichtbarer Bereich\":\"STRING\",\"Display weighting\":\"INTEGER\",\"Beschreibung\":\"STRING\",\"Access+state\":\"ENUMERATION\",\"__NameGeneration__\":\"STRING\",\"Konnektormarken\":\"STRING\",\"Farbe\":\"STRING\",\"Font+size\":\"INTEGER\",\"POSITION\":\"STRING\",\"Externe Dokumentation\":\"PROGRAMCALL\",\"Aktuelles Seitenlayout\":\"STRING\",\"Versionskontext\":\"STRING\",\"Ermittelte Transportzeit\":\"EXPRESSION\",\"Aggregated execution time\":\"EXPRESSION\",\"Aktueller Modus\":\"STRING\",\"probe_overview_ref_deu\":\"EXPRESSION\",\"Angelegt am\":\"STRING\",\"Viewable+area\":\"STRING\",\"Access state\":\"ENUMERATION\",\"Ermittelte Durchlaufzeit\":\"EXPRESSION\",\"AutoConnect\":\"STRING\",\"Visualise referenced IT system elements\":\"INTEGER\",\"External process\":\"ENUMERATION\",\"Model+type\":\"ENUMERATION\",\"External documentation\":\"PROGRAMCALL\",\"Letzte Änderung am\":\"STRING\",\"Basisname\":\"STRING\",\"Name und Referenz anzeigen\":\"ENUMERATION\",\"Date+last+changed\":\"STRING\",\"Version number\":\"STRING\",\"Darstellungsform\":\"ENUMERATION\",\"Name (english)\":\"STRING\",\"Ausrichtung\":\"ENUMERATION\",\"Letzter Bearbeiter\":\"STRING\",\"Contact+person\":\"STRING\",\"Subprocessname\":\"EXPRESSION\",\"Documentation\":\"STRING\",\"Context of version\":\"STRING\",\"Last user\":\"STRING\",\"Type\":\"STRING\",\"Order\":\"INTEGER\",\"Current mode\":\"STRING\",\"Ermittelte Personalkosten\":\"EXPRESSION\",\"Categories\":\"STRING\",\"Target value\":\"EXPRESSION\",\"Reviewed+on\":\"STRING\",\"Modelltyp\":\"ENUMERATION\",\"Aggregated costs\":\"EXPRESSION\",\"Ansprechpartner\":\"STRING\",\"Visualisierung\":\"ENUMERATION\",\"Aggregated+cycle+time\":\"EXPRESSION\",\"Offene Fragen\":\"STRING\",\"Schriftgrad\":\"ENUMERATION\",\"Current+mode\":\"STRING\",\"Description\":\"STRING\",\"world area\":\"STRING\",\"External tool coupling\":\"STRING\",\"probe_overview_score\":\"EXPRESSION\",\"Konnektornummer\":\"INTEGER\",\"Referenz\":\"EXPRESSION\",\"Display periodicity\":\"INTEGER\",\"Ermittelte Liegezeit\":\"EXPRESSION\",\"Name\":\"STRING\",\"Status\":\"ENUMERATION\",\"Darstellung\":\"ENUMERATION\",\"Externer Prozeß\":\"ENUMERATION\",\"Font style\":\"ENUMERATION\",\"State\":\"ENUMERATION\",\"Schlagworte\":\"STRING\",\"Aggregated+execution+time\":\"EXPRESSION\",\"Autor\":\"STRING\",\"Position\":\"STRING\",\"Connector marks\":\"STRING\",\"Bezeichnung\":\"STRING\",\"Versionsnummer\":\"STRING\",\"Änderungszähler\":\"INTEGER\"}");
			} catch (JSONException e) {
				attributeTypes = new JSONObject();
				e.printStackTrace();
			}
//		} catch (Exception e){
//			Log.e("Could not initialize attributeTypes file for Adonis compatibility");
//			e.printStackTrace();
//			if (bufferedReader != null)
//				try {
//					bufferedReader.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				} 
//			if (fileStreamReader != null )
//				try {
//					fileStreamReader.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//			if (fileStream != null )
//				try {
//					fileStream.close();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//		}
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
				//TODO currently the mapping is hard coded - needs to be reworked 
				//writeOutAttributeTypesFile();
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
