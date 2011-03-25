package de.hpi.pictureSupport.helper;

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

		// some xmappr basic steps, to be found on prject's site
		StringReader stringReader = new StringReader(xml);
		Xmappr xmappr = new Xmappr(PictureXML.class);
		PictureXML newPicture = (PictureXML) xmappr.fromXML(stringReader);
		Logger.i("mapping xml to java done");
		
		// prepare the repository and the mapping of PICTURE to Oryx-PICTURE names
		BlockTypeMapping.prepareMapping();
		BlockRepository.initializeRepository(newPicture);
		
		// build the JSON
		Vector<JSONObject> importObject = newPicture.writeJSON();
		
		Logger.i("mapping java to json done");
		Logger.i("result: "+importObject.toString());
		
		// FIXME ATM only the first JSON in the list is returned
		// picturesupport.js needs to be adjusted to handle more than one returning String
		return importObject.get(0).toString();
	}
}