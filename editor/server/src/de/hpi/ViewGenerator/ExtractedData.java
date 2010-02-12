package de.hpi.ViewGenerator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.hpi.ViewGenerator.ConnectionList;
import de.hpi.ViewGenerator.ConnectorList;
import de.hpi.ViewGenerator.ExtractedConnectionList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


class ExtractedData {
	
	protected ExtractedConnectionList extractedConnectionList;
	private int jsonChildNr;
	private int svgChildNr;
	private int descriptionChildNr;
	protected String serverBaseUrl;
	protected String toSavePath;

	public ExtractedData(String toSaveDirectory) {
		extractedConnectionList = new ExtractedConnectionList();
		jsonChildNr = 5;
		svgChildNr = 7;
		descriptionChildNr = 1;
		toSavePath = toSaveDirectory;
	}
	
	
	private ConnectionList extractFromChildShapes(JSONObject jsonObject, ConnectionList connectionList, ConnectorList connectorList,  HashMap<String, JSONObject> stencilLevels) {
		ConnectionList connectionList_new = connectionList;
		try {
			String stencilId = jsonObject.getJSONObject("stencil").get("id").toString();			
			stencilLevels.put(stencilId, jsonObject);
			extractTargetAttribute(jsonObject, connectorList, connectionList_new, stencilLevels, stencilId);
					
			JSONArray outgoingArray = jsonObject.getJSONArray("outgoing");
			extractSourceAttribute(outgoingArray, connectorList, connectionList_new, stencilLevels, stencilId);
			
			JSONArray childShapesArray = jsonObject.getJSONArray("childShapes");
			
			if (!(childShapesArray.length() != 0) && connectorList.isPossibleParentStencil(stencilId)) {}
			else {
				for (int index = 0; index < childShapesArray.length(); index++) {
					JSONObject childShapeObject = new JSONObject(childShapesArray.get(index).toString());
					connectionList_new = extractFromChildShapes(childShapeObject,connectionList_new, connectorList, stencilLevels);	
				}
			}
		}
		catch (JSONException e) {
			  e.printStackTrace(); 
		}			
		return connectionList_new;
	}

	
	private void extractTargetAttribute(JSONObject jsonObject, ConnectorList connectorList, ConnectionList connectionList_new, HashMap<String, JSONObject> stencilLevels, String stencilId) {
		try {						
			if (connectorList.containsConnectorWithStencil(stencilId)) {
				 ArrayList<String> targetResultList = connectionList_new.matchInTargetMatchlist(jsonObject.getString("resourceId"));

				for(int i=0; i<targetResultList.size();i++) {
					String attributeToSaveFromSaveObject = extractAttributeToSaveFromSaveObject(connectorList, stencilLevels, stencilId);
					connectionList_new.addTargetAttributeForConnection(attributeToSaveFromSaveObject, targetResultList.get(i));
				}
			}
		}
		catch (JSONException e) {
			  e.printStackTrace(); 
		}	
	}
	
	
	private void extractSourceAttribute(JSONArray outgoingArray, ConnectorList connectorList, ConnectionList connectionList_new, HashMap<String, JSONObject> stencilLevels, String stencilId) {
		try {
			for(int i=0; i<outgoingArray.length();i++){
				JSONObject outgoingObject = new JSONObject(outgoingArray.get(i).toString());
				String connectionId = outgoingObject.getString("resourceId");
				if (connectionList_new.containsConnectionId(connectionId)){
					String attributeToSaveFromSaveObject = extractAttributeToSaveFromSaveObject(connectorList, stencilLevels, stencilId);
					connectionList_new.addSourceAttributeForConnection(attributeToSaveFromSaveObject, connectionId);	
				}
			}		
		}
		catch (JSONException e) {
			  e.printStackTrace(); 
		}	
	}
	
	
	private String extractAttributeToSaveFromSaveObject(ConnectorList connectorList, HashMap<String, JSONObject> stencilLevels, String stencilId) {	
		try {
			DataToSave dataToSave = connectorList.dataToSaveForConnectorWithStencil(stencilId);
			String attributeToSave = dataToSave.getAttributeToSave(0);
			String stencilLevelToSave = dataToSave.getStencilLevelToSave(0);
			String jsonId = dataToSave.getJSONIdWithAttributeToSave(0);
			JSONObject saveObject = stencilLevels.get(stencilLevelToSave);
			String attributeToSaveFromSaveObject = extractAttribute(saveObject, jsonId, attributeToSave);

			for(int a=1; a<dataToSave.size(); a++) {
				attributeToSave = dataToSave.getAttributeToSave(a);
				stencilLevelToSave = dataToSave.getStencilLevelToSave(a);
				saveObject = stencilLevels.get(stencilLevelToSave);
				jsonId = dataToSave.getJSONIdWithAttributeToSave(a);
				String attributeToSaveFromObject_tmp = extractAttribute(saveObject, jsonId, attributeToSave);
//				escaping bad chars
				attributeToSaveFromSaveObject = attributeToSaveFromObject_tmp + "\\n" + attributeToSaveFromSaveObject;
			}
			return attributeToSaveFromSaveObject;
		}
		catch (NullPointerException e) {
//			Connector with no DataToSave
			return null;
		}
	}
	
	private String extractAttribute(JSONObject saveObject, String jsonId, String attributeToSave) {
		String attributeToSaveFromSaveObject;
		
		try {
			if (jsonId != null) {
				try {
					attributeToSaveFromSaveObject = saveObject.getJSONObject(jsonId).getString(attributeToSave);
				}
				catch (NullPointerException e) {
//					ConnectorElement where attribute that was intended to be saved is missing, e.g. EndEvent with no ParentPool
//					possible Modelling error, feedback could be useful, but hard to distinguish from error resulting from bad ConnectorDefinition
					return "";
				}
			}
			else {
				attributeToSaveFromSaveObject = saveObject.getString(attributeToSave);
			}
//			escaping bad chars
			attributeToSaveFromSaveObject = replaceSpecialChars(attributeToSaveFromSaveObject);
			
			return attributeToSaveFromSaveObject;
		}
		catch (JSONException e) {
//			ConnectorElement where the saveLevel has no jsonId, possible bad Connectordefinition or old editor version
			  e.printStackTrace(); 
		}
		return "";
	}
	
	protected ConnectionList extractConnection(JSONArray jsonArray, ConnectionList connectionList, String connection, ConnectorList connectorList) {
		ConnectionList connectionList_new = connectionList;
		
		try {
			connectionList_new = initializeTargetMatchList(jsonArray, connectionList_new, connection);
			
			for(int index = 0; index < jsonArray.length(); index++) {
				JSONObject jsonObject = new JSONObject(jsonArray.get(index).toString());
				
				connectionList_new = extractFromChildShapes(jsonObject, connectionList_new, connectorList, new HashMap<String, JSONObject>());
			}
		}
		catch (JSONException e) {
			  e.printStackTrace(); 
		}
//		remove Entries where either target or source could not be found - possible modelling error, feedback could be added
		return removeUncompleteEntries(connectionList_new);
	}
	
	
	protected void merge(ConnectionList connectionList, boolean symmetric, boolean storeRecursive) {
		extractedConnectionList.merge(connectionList, symmetric, storeRecursive);
	}
	
	private ConnectionList initializeTargetMatchList(JSONArray jsonArray, ConnectionList connectionList, String connection) {
		ConnectionList connectionList_new = connectionList;
		try {
			for(int index = 0; index < jsonArray.length(); index++) {
				JSONObject jsonObject = new JSONObject(jsonArray.get(index).toString());
				
				if (jsonObject.getJSONObject("stencil").get("id").equals(connection)) {
					String resourceId = jsonObject.getString("resourceId");
					try {
						String targetId = jsonObject.getJSONObject("target").getString("resourceId");
						connectionList_new.addToTargetMatchlist(resourceId,targetId);
						connectionList_new.addConnection(resourceId);	
					}
					catch (JSONException e) {
//						target is not set, connection not connected properly
						continue;
					}
				}
			}
		}
		catch (JSONException e) {
			  e.printStackTrace(); 
		}
		return connectionList_new;

	}
	
	
	private ConnectionList removeUncompleteEntries(ConnectionList connectionList) {
		String origin = connectionList.getOrigin();
		ConnectionList connectionList_new = new ConnectionList(origin);
		
		for (String connectionId: connectionList.connectionIds()) {
			ConnectionAttributes connectionAttributes = connectionList.getConnectionAttributesFor(connectionId);
			
			if (connectionAttributes.hasSourceAttribute() && connectionAttributes.hasTargetAttribute()) {
				connectionList_new.addConnection(connectionId);
				connectionList_new.addSourceAttributeForConnection(connectionAttributes.getSourceAttribute(), connectionId);
				connectionList_new.addTargetAttributeForConnection(connectionAttributes.getTargetAttribute(), connectionId);
			}
		}
		return connectionList_new;
	}
	
	
	protected String getJSON(String diagramPath) {
		try {
			System.out.println(diagramPath);
			URI uri = new URI(diagramPath);
			System.out.println("URI " + uri);
			InputStream st = uri.toURL().openStream();
			Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(st);
			
			Element xmlDoc = xml.getDocumentElement();
		  	NodeList nodeList = xmlDoc.getChildNodes();
		  	Node jsonNode = nodeList.item(jsonChildNr);	
			String json = jsonNode.getTextContent();
				
			return json;
		}
		catch (URISyntaxException e) {
//			invalid parameter
			e.printStackTrace();
		}
		catch (ParserConfigurationException e) {
		}
		catch (SAXException e) {		
		}
		catch (IOException e) { 
			e.printStackTrace(); 
		}
		return "";
	}
	
	
	protected String getSVG(String diagramPath) {
		try {
			URI uri = new URI(diagramPath);
			InputStream st = uri.toURL().openStream();
			Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(st);
			
			Element xmlDoc = xml.getDocumentElement();
		  	NodeList nodeList = xmlDoc.getChildNodes();
		  	Node svgNode = nodeList.item(svgChildNr);	
			String svg = svgNode.getTextContent();
			
			return svg;
		}
		catch (URISyntaxException e) {
//			invalid parameter
			e.printStackTrace();

		}
		catch (ParserConfigurationException e) {
		}
		catch (SAXException e) {		
		}
		catch (IOException e) { 
			e.printStackTrace(); 
		}
		return "";
	}
	
	
	protected String getDescription(String diagramPath) {
		try {
			URI uri = new URI(diagramPath);
			InputStream st = uri.toURL().openStream();
			Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(st);
			
			
			Element xmlDoc = xml.getDocumentElement();
		  	NodeList nodeList = xmlDoc.getChildNodes();
		  	Node descriptionNode = nodeList.item(descriptionChildNr);	
			String description = descriptionNode.getTextContent();
			
			return description;
		}
		catch (URISyntaxException e) {
//			invalid parameter
			e.printStackTrace();

		}
		catch (ParserConfigurationException e) {
		}
		catch (SAXException e) {		
		}
		catch (IOException e) { 
			e.printStackTrace(); 
		}
		return "";
	}
	
	protected String replaceBadChars(String fileName) {
//		replacing badChars from fileNames
		String fileName_new = fileName.replace("?","que").replace("\"", "quo").replace("/", "sl").replace("\\", "bs");
		fileName_new = fileName_new.replace("<","lt").replace(">","gt").replace(":","dp").replace("*", "st").replace("|","li");
		fileName_new = fileName_new.replace("ä","ae").replace("ö", "oe").replace("ü", "ue").replace("ß","ss");
		fileName_new = fileName_new.replace("Ä","Ae").replace("Ö", "Oe").replace("Ü", "Ue");
		fileName_new = fileName_new.replace("\n", "").replace("\t","").replace("\r","");
		return fileName_new;
	}
	
	protected String replaceEscChars(String fileName) {
//		replacing escaped chars
		String fileName_new = fileName.replace("\\", "\\\\");
		fileName_new = fileName_new.replace("\n", "").replace("\t","").replace("\r","");
		return fileName_new;
	}
	
	protected String replaceSpecialChars(String fileName) {
//		replacing special chars for linkdisplay
		String fileName_new = fileName.replace("\\", "\\\\");
		fileName_new = fileName_new.replace("ä","ae").replace("ö", "oe").replace("ü", "ue").replace("ß","ss");
		fileName_new = fileName_new.replace("Ä","Ae").replace("Ö", "Oe").replace("Ü", "Ue");
		fileName_new = fileName_new.replace("\n", "").replace("\t","").replace("\r","");
		return fileName_new;
	}

	
	protected File createOriginSVG(ArrayList<String> attributePair, String diagramPath, int originNumber) {
		File svgFile;
		String svg = getSVG(diagramPath);
	      try {
//	          svgFile = File.createTempFile(attributePair.toString(), ".svg", new File(path));
	    	  String fileName = replaceBadChars(attributePair.toString());
	    	  svgFile = new File(toSavePath + fileName + originNumber + ".svg");
	          FileWriter fout = new FileWriter(svgFile);
	          fout.write(svg);
	          fout.close();
	          return svgFile;
	      } 	      
	      catch (java.io.IOException e) { 
			  e.printStackTrace();
			  return null;
	      }
	}
	
	protected void createOriginSVGs(ExtractedConnectionList extractedConnectionList) {
		for (ArrayList<String> attributePair: extractedConnectionList.connectionAttributePairs()) {	
			ArrayList<String> origins = extractedConnectionList.getOriginsForConnectionAttributePair(attributePair);

			for (int i=0; i<origins.size(); i++) {
				String diagramPath = origins.get(i);
				createOriginSVG(attributePair, diagramPath, i);
			}
		}
	}
	
	private File createOriginsIndexHTML(ArrayList<String> attributePair, ArrayList<String> origins) {
		int fontSize = 4;
		File htmlFile;
		
		try {
			String fileName = replaceBadChars(attributePair.toString()) + "_index";
		    htmlFile = new File(toSavePath + fileName + ".html");
		    FileWriter fout = new FileWriter(htmlFile);
		    fout.write("<!doctype html>\n");
		    
		    fout.write("<html><head>");
		    fout.write("<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js\"></script>");
		    fout.write("<script type=\"text/javascript\" src=\"static/popup.js\"></script>");
		    fout.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"static/popup.css\" />");
		    fout.write("</head>");
		    fout.write("<body><h3><center>Origins for "+attributePair+"</center></h3><hr>");

		  	for (int i=0; i<origins.size(); i++) {
		  		String originSVG = replaceBadChars(attributePair.toString()) + i + ".svg";
		  		String origin = origins.get(i);
		  		String suffix = ".oryx.xml";
		  		String savePath = origin.substring(0,origin.lastIndexOf("/")+1);
		  		origin = origin.substring(savePath.length(), origin.length() - suffix.length());
		  		fout.write("<div class=\"bubbleInfo\">");
			    fout.write("<div class=\"trigger\">");
		        fout.write("<td><A HREF=\""+originSVG+"\" target=\"content\"><font size = " +fontSize+">"+replaceSpecialChars(origin)+"</font></A></td>");
			    fout.write("</div>");
			    fout.write("<div class=\"popup\">");
			    String description = replaceSpecialChars(getDescription(origins.get(i)));
			    if (description.equals("")) {
				    fout.write("<font>No Description given.</font>");
			    }
			    else {
				    fout.write("<font>"+description+"</font>");
			    }
			    fout.write("</div>");
			    fout.write("</div>");
		  	}
	        fout.write("</body></html>");
	        fout.close();
	        return htmlFile;	
		} 	      
		catch (java.io.IOException e) { 
			e.printStackTrace();
			return null;
		}
	}
	
	
	private File createOriginsHTML(ArrayList<String> attributePair, ArrayList<String> origins) {
		
		createOriginsIndexHTML(attributePair, origins);

		File htmlFile;
		try {
			String fileName = replaceBadChars(attributePair.toString());
		    htmlFile = new File(toSavePath + fileName + ".html");
		    FileWriter fout = new FileWriter(htmlFile);
		    fout.write("<!doctype html>\n");
		    fout.write("<html>\n<head>\n<title>Origins for " +attributePair+ "</title>\n</head>");
		    fout.write("<frameset cols=\"15%,85%\">");
		    fout.write("<body>");
		    fout.write("<frame name=index src=\""+ fileName+"_index.html"+"\">");
		    fout.write("<frame name=content src=\""+ replaceBadChars(attributePair.toString())+0+".svg" + "\">");
		    fout.write("</frameset>");
	        fout.write("</body></html>");
	        fout.close();
	        return htmlFile;	
		} 	    	
		catch (java.io.IOException e) { 
			e.printStackTrace();
			return null;
		}
	}
	
	
	protected void createOriginsHTMLs(ExtractedConnectionList extractedConnectionList) {
		for (ArrayList<String> attributePair: extractedConnectionList.connectionAttributePairs()) {
			ArrayList<String> origins = extractedConnectionList.getOriginsForConnectionAttributePair(attributePair);
			createOriginsHTML(attributePair, origins);	
		}
	}
	
}
	

