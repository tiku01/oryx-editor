package de.hpi.pictureSupport;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import org.json.JSONObject;
import org.xmappr.Xmappr;

import org.oryxeditor.server.PictureImporter;

/**
 * The Class PictureConverter.
 */
public class PictureConverter {

	/* unused ATM, useful for testing and loading own file from HDD
	 /**
	 \*\/
	 * helper to read a file
	 * @param filePath
	 * @return
	 \*\/
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
	*/
	
	/**
	 * import a xml file and convert it into a json.
	 *
	 * @param xml the xml
	 * @return the string
	 */
	public static String importXML(String xml){
		Logger.i("importXML");

		StringReader stringReader = new StringReader(xml);
		
		Xmappr xmappr = new Xmappr(PictureXML.class);
		PictureXML newPicture = (PictureXML) xmappr.fromXML(stringReader);
		Logger.i("mapping xml to java done");
				
		JSONObject importObject = new JSONObject();
		newPicture.write(importObject);
		
		Logger.i("mapping java to json done");
		Logger.i("result: "+importObject.toString());
		return importObject.toString();
	}
	
	public static void main(String[] args) throws IOException{
		String s = File.separator;
		String pictureXML = PictureImporter.getXMlNamed("C:" + s + "Users" + s + "Tobi BP" + s + "Documents" + s + "EclipseWorkspace" + s + "oryx" + s + "editor" + s + "server" + s + "src" + s + "de" + s + "hpi" + s + "pictureSupport" + s + "process1.xml");
		//String pictureXML = PictureImporter.getXMlNamed("C:" + s + "Users" + s + "Tobi BP" + s + "Documents" + s + "EclipseWorkspace" + s + "oryx" + s + "editor" + s + "server" + s + "src" + s + "de" + s + "hpi" + s + "pictureSupport" + s + "process1.xml");
		String importString = PictureConverter.importXML(pictureXML);
		System.out.println(importString);
	}
}