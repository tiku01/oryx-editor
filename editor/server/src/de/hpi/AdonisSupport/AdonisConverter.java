package de.hpi.AdonisSupport;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;



public class AdonisConverter {
	public static boolean export = true;

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
				content.append(line+"\r\n");
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
		if (xml.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>") < 0
				|| xml.indexOf("<!DOCTYPE ADOXML SYSTEM \"adoxml31.dtd\">") < 0){
			Logger.w("could not detect correct version - import could possibly fail");
		}
		String xmlWithoutXMLTag = xml.replaceFirst("<\\?xml[^\\?]*\\?>", "");
		String xmlWithoutDoctype = xmlWithoutXMLTag.replaceFirst("<\\![^\\>]*>","");
		return xmlWithoutDoctype;
	}
	
	public String addXMLHeader(String body){
		return	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n"
			+ "<!DOCTYPE ADOXML SYSTEM \"adoxml31.dtd\">"
			+ body;
	}
	
	/**
	 * import a xml file and convert it into a json 
	 * @param xml
	 * @return
	 */
	public String importXML(String xml){
		export = false;
		Logger.i("importXML");

		String filteredXML = filterXML(xml);
		StringReader stringReader = new StringReader(filteredXML);
		
		//Log.v("ImportXML: "+filteredXML);
		
		Xmappr xmappr = new Xmappr(AdonisXML.class);
//		xmappr.addConverter(new StringConverter());
		AdonisXML modelCollection = (AdonisXML) xmappr.fromXML(stringReader);
		Logger.i("mapping xml to java done");
				
		Vector<JSONObject> models = null;
		try {
			models = modelCollection.writeDiagrams();
		} catch (JSONException e) {
			Logger.e("E importXML",e);
			e.printStackTrace();
		}
		Logger.i("mapping java to json done");
		Logger.i("result: "+models.elementAt(0).toString());
		return models.elementAt(0).toString();
	}
	
	public String exportXML(String json) throws JSONException {
		Logger.i("EXPORT: "+json);
		export = true;
		AdonisXML model = new AdonisXML();
		JSONObject jsonModel = new JSONObject(json);
		model.readJSON(jsonModel);
		
		StringWriter writer = new StringWriter();
		Xmappr xmappr = new Xmappr(AdonisXML.class);
//		xmappr.addConverter(new StringConverter());
		//xmappr.setPrettyPrint(true); //- can not be enabled due Adonis doesn't ignores newlines
		xmappr.toXML(model,writer);
		
		return addXMLHeader(writer.toString().replaceAll(">=", "&gt;="));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AdonisConverter ac = new AdonisConverter();
		String json = ac.importXML(
				//importFromFile("D:\\Desktop\\Adonis\\Demo\\demo.xml"));
				//importFromFile("D:\\Desktop\\Adonis\\Example exports\\Is inside.xml"));
				//importFromFile("D:\\Desktop\\Adonis\\Example Exports\\CompanyMap.xml"));
				//importFromFile("D:\\Desktop\\Adonis\\Example Exports\\einzelhaendler.xml"));
				//importFromFile("D:\\Desktop\\Adonis\\Example Exports\\small.xml"));
				importFromFile("D:\\Desktop\\Adonis\\Example Exports\\architekt.xml"));
		
		try {
			File file = new File("D:\\Desktop\\Eclipse Export.json");
			FileWriter fileWriter = new FileWriter(file,false);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(json);
			bufferedWriter.close();
			fileWriter.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			File file = new File("D:\\Desktop\\Eclipse Export.xml");
			FileWriter fileWriter = new FileWriter(file,false);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			String out = ac.exportXML(json);
			bufferedWriter.write(out);
			bufferedWriter.close();
			fileWriter.close();
			System.err.println(out);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.printSummary();
		Logger.reset();
		

	}
	

}
