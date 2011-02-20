package de.hpi.pictureSupport;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;



public class PictureConverter {

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
	 * import a xml file and convert it into a json 
	 * @param xml
	 * @return
	 */
	public String importXML(String xml){
		Logger.i("importXML");

		StringReader stringReader = new StringReader(xml);
		
		Xmappr xmappr = new Xmappr(PictureXML.class);
		PictureXML modelCollection = (PictureXML) xmappr.fromXML(stringReader);
		Logger.i("mapping xml to java done");
				
		Vector<JSONObject> models = null;
		try {
			models = modelCollection.writeJSON();
		} catch (JSONException e) {
			Logger.e("E importXML",e);
			e.printStackTrace();
		}
		Logger.i("mapping java to json done");
		Logger.i("result: "+models.elementAt(0).toString());
		return models.toString();
	}
}