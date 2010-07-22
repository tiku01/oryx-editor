package de.hpi.AdonisSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
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
		if (xml.indexOf("<?xml version=\"1.0\" encoding=\"UTF-8\"?>") < 0
				|| xml.indexOf("<!DOCTYPE ADOXML SYSTEM \"adoxml31.dtd\">") < 0){
			Log.w("could not detect correct version - import could possibly fail");
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
		Log.v("importXML");

		String filteredXML = filterXML(xml);
		StringReader stringReader = new StringReader(filteredXML);
		
		Log.v("ImportXML: "+filteredXML);
		
		Xmappr xmappr = new Xmappr(AdonisXML.class);
		AdonisXML modelCollection = (AdonisXML) xmappr.fromXML(stringReader);
		Log.v("mapping xml to java done");
				
		Vector<JSONObject> models = null;
		try {
			models = modelCollection.writeDiagrams();
		} catch (JSONException e) {
			Log.e("E importXML",e);
			e.printStackTrace();
		}
		Log.v("mapping java to json done");
		Log.v("result: "+models.elementAt(0).toString());
		return models.elementAt(0).toString();
	}
	
	public String exportXML(String json) throws JSONException {
		AdonisXML model = new AdonisXML();
		JSONObject jsonModel = new JSONObject(json);
		model.parse(jsonModel);
		
		StringWriter writer = new StringWriter();
		Xmappr xmappr = new Xmappr(AdonisXML.class);
		xmappr.setPrettyPrint(true);
		xmappr.toXML(model,writer);
		
		return addXMLHeader(writer.toString());
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AdonisConverter ac = new AdonisConverter();
		String json = ac.importXML(
				importFromFile(
//						"D:\\Desktop\\Adonis\\Example Exports\\nestedContainer.xml"
//						"D:\\Desktop\\Adonis\\Example Exports\\CompanyMap.xml"
//						"D:\\Desktop\\Adonis\\Example Exports\\architekt.xml"
//						"D:\\Desktop\\Adonis\\Example Exports\\einzelhaendler.xml"
						"D:\\Desktop\\Adonis\\Example Exports\\minmalNestedContainer.xml"
				)); 
		System.out.println(json);
		try {
			System.out.println(ac.exportXML(json));
		} catch (JSONException e){
			System.err.println(e.getMessage());
			e.printStackTrace();
		}

	}
	

}
