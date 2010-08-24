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
	
	


//	public class StringConverter extends ValueConverter {
//
//
//	    public boolean canConvert(Class type) {
//	        return String.class.isAssignableFrom(type);
//	    }
//
//	    /**
//	     * called in conversion from xml to java
//	     */
//	    public Object fromValue(String value, String format, Class targetType, Object targetObject) {
//	    	Log.d("XMAPPRLOG from xml to java "+value+" - "+format);
//	    	if (value != null && value.contains(">=")){
//	    		value = value.replaceAll(">=", ">=");
//	    		Log.d("XMAPPRLOG from xml to java ## "+value+" - "+format);
//	    	}
//	        return value.intern();
//	    }
//
//	    /**
//	     * called in conversion from java to xml
//	     */
//	    public String toValue(Object object, String format) {
//	    	Log.d("XMAPPRLOG from java to xml "+(String) object+" - "+format);
//	    	String value = (String)object;
////	    	if (value != null && value.contains(">")){
////	    		return value.replace(">", "&gt;").intern();
////	    	}
//	    	return value;
//			
//	    }
//
//
//	    @Override
//	    public boolean convertsEmpty() {
//	        return true;
//	    }
//	}
//	
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
		export = false;
		Log.v("importXML");

		String filteredXML = filterXML(xml);
		StringReader stringReader = new StringReader(filteredXML);
		
		Log.v("ImportXML: "+filteredXML);
		
		Xmappr xmappr = new Xmappr(AdonisXML.class);
//		xmappr.addConverter(new StringConverter());
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
		export = true;
		AdonisXML model = new AdonisXML();
		JSONObject jsonModel = new JSONObject(json);
		model.parse(jsonModel);
		
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
//		String json = ac.importXML(
//				importFromFile("D:\\Desktop\\Adonis\\Demo\\demo.xml"));
//		
//		try {
//			File file = new File("D:\\Desktop\\Eclipse Export.json");
//			FileWriter fileWriter = new FileWriter(file,false);
//			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
//			bufferedWriter.write(json);
//			bufferedWriter.close();
//			fileWriter.close();
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		try {
			File file = new File("D:\\Desktop\\Eclipse Export.xml");
			FileWriter fileWriter = new FileWriter(file,false);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			String out = ac.exportXML("{\"resourceId\":\"oryx-canvas123\",\"properties\":{\"name\":\"Model\",\"author\":\"Admin\",\"keywords\":\"\",\"comment\":\"\",\"description\":\"\",\"state\":\"In process\",\"reviewed on\":\"20.04.2007\",\"reviewed by\":\"\"},\"stencil\":{\"id\":\"diagram\"},\"childShapes\":[{\"resourceId\":\"oryx_B17C0CF1-5C0F-4F6B-8149-33741E3ABAF4\",\"properties\":{\"name\":\"Note\",\"text\":\"\"},\"stencil\":{\"id\":\"note\"},\"childShapes\":[],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":186,\"y\":187},\"upperLeft\":{\"x\":146,\"y\":147}},\"dockers\":[]},{\"resourceId\":\"oryx_12BCC8DE-55ED-4617-BC45-8C185B4E5BD4\",\"properties\":{\"name\":\"Note (1)\",\"text\":\"\"},\"stencil\":{\"id\":\"note\"},\"childShapes\":[],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":472,\"y\":191},\"upperLeft\":{\"x\":432,\"y\":151}},\"dockers\":[]},{\"resourceId\":\"oryx_DFC355EF-54DD-4C75-BED4-9C0A9EEA2949\",\"properties\":{\"name\":\"Process\",\"categories\":\"\",\"documentation\":\"\",\"order\":\"0\",\"description\":\"\",\"comment\":\"\",\"open questions\":\"\",\"external process\":\"no\",\"subprocessname\":\"\"},\"stencil\":{\"id\":\"process\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_CF02FE22-B149-4954-A02F-7CD45C8B174C\"}],\"bounds\":{\"lowerRight\":{\"x\":342,\"y\":292},\"upperLeft\":{\"x\":262,\"y\":232}},\"dockers\":[]},{\"resourceId\":\"oryx_CF02FE22-B149-4954-A02F-7CD45C8B174C\",\"properties\":{},\"stencil\":{\"id\":\"has note\"},\"childShapes\":[],\"outgoing\":[{\"resourceId\":\"oryx_B17C0CF1-5C0F-4F6B-8149-33741E3ABAF4\"}],\"bounds\":{\"lowerRight\":{\"x\":262.930202096759,\"y\":234.7085970528831},\"upperLeft\":{\"x\":186.476047903241,\"y\":181.3031216971169}},\"dockers\":[{\"x\":40,\"y\":30},{\"x\":20,\"y\":20}],\"target\":{\"resourceId\":\"oryx_B17C0CF1-5C0F-4F6B-8149-33741E3ABAF4\"}}],\"bounds\":{\"lowerRight\":{\"x\":1485,\"y\":1050},\"upperLeft\":{\"x\":0,\"y\":0}},\"stencilset\":{\"url\":\"/oryx//stencilsets/adonis/adonis.json\",\"namespace\":\"http://b3mn.org/stencilset/adonis#\"},\"ssextensions\":[]}");
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
		
		

	}
	

}
