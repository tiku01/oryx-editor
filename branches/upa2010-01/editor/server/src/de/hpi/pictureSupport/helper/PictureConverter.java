package de.hpi.pictureSupport.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Xmappr;


/**
 * The Class PictureConverter.
 */
public class PictureConverter {
	
	/**
	 * import a XML file and convert it into a JSON.
	 *
	 * @param xml the XML to import
	 * @return the string that represents the JSON for Oryx
	 * @throws IOException 
	 */
	public static String importXML(String xml) throws JSONException, IOException{
		Logger.i("importXML");

		StringReader stringReader = new StringReader(xml);
		
		Xmappr xmappr = new Xmappr(PictureXML.class);
		PictureXML newPicture = (PictureXML) xmappr.fromXML(stringReader);
		Logger.i("mapping xml to java done");
		BlockTypeMapping.prepareMapping();
		BlockRepository.initializeRepository(newPicture);
		Vector<JSONObject> importObject = newPicture.writeJSON();
		
		Logger.i("mapping java to json done");
		Logger.i("result: "+importObject.toString());
		return importObject.get(0).toString();
	}
	
	public static String getXMLNamed(String filename) throws IOException
	{
		try
		{
			File f = new File(filename);

			FileReader fReader = new FileReader(f);
			BufferedReader bReader = new BufferedReader(fReader);
			String xml = "";
			while (bReader.ready())
			{
				xml += bReader.readLine();
			}
			return xml;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new IOException();
		}
	}
	
	public static void main(String[] args) throws IOException, JSONException{
		String s = File.separator;
		String pref = "T:" + s + "Dokumente" + s + "Eclipse" + s + "picture entwicklung" + s;
		//String pref = "C:" + s + "Users" + s + "Tobi BP" + s + "Documents" + s + "EclipseWorkspace" + s;
		String pictureXML = getXMLNamed(pref + "oryx" + s + "editor" + s + "server" + s + "src" + s + "de" + s + "hpi" + s + "pictureSupport" + s + "process1.xml");
		String importString = importXML(pictureXML);
		System.out.println(importString);
	}
}