/***************************************
 * Copyright (c) 2010 
 * Martin Krüger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/

package de.hpi.ViewGenerator;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


class ExtractedData {
	
	protected ExtractedConnectionList extractedConnectionList;
	protected ReadWriteAdapter rwa;

	public ExtractedData(ReadWriteAdapter rwa) {
		this.rwa = rwa;
		this.extractedConnectionList = new ExtractedConnectionList();
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
//			attributeToSaveFromSaveObject = replaceSpecialCharsForHTML(attributeToSaveFromSaveObject);
			
			return attributeToSaveFromSaveObject;
		}
		catch (JSONException e) {
//			ConnectorElement where the saveLevel has no jsonId, possible bad Connectordefinition or old editor version
			  e.printStackTrace(); 
		}
		return "";
	}
	
	public ConnectionList extractConnection(JSONArray jsonArray, ConnectionList connectionList, String connection, ConnectorList connectorList) {
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
		
	public void merge(ConnectionList connectionList, boolean symmetric, boolean storeRecursive) {
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
	
	protected void generateFiles(String graphLabel, TranslatorInput translatorInput, String layoutAlgorithm, String svgName) {
		SVGGenerator generator = new SVGGenerator(rwa, graphLabel, translatorInput, layoutAlgorithm, svgName);
		createOriginSVGs(extractedConnectionList);
		createOriginsHTMLs(extractedConnectionList);
		generator.generateSVG();
	}
	
	
	protected String getJSON(String diagramPath) {		
		return rwa.getJSON(diagramPath);
	}
	
	
	protected String getSVG(String diagramPath) {
		return rwa.getSVG(diagramPath);
	}
	
	
	protected String getDescription(String diagramPath) {
		return rwa.getDescription(diagramPath);
	}
	
	private String getFileName(String attributePair) {
		return rwa.getFileName(attributePair);
	}
	
	protected String getOriginIndexHTMLName(ArrayList<String> attributePair) {
		return getFileName(attributePair.toString())+"_index.html";
	}
	
	protected String getOriginSVGName(ArrayList<String> attributePair, int originNumber) {
		return getFileName(attributePair.toString()) + originNumber + ".svg";
	}
	
	protected String getOriginHTMLName(ArrayList<String> attributePair) {
		return getFileName(attributePair.toString()) + ".html";
	}
		
	protected String removeEscChars(String fileName) {
//		replacing escaped chars, e.g. for graphVizLabels
		String fileName_new = fileName.replace("\n", "").replace("\t","").replace("\r","");
		return fileName_new;
	}
	
	protected String replaceSpecialCharsForHTML(String s) {
//		replacing special chars related to displaying in html
		String fileName_new = StringEscapeUtils.escapeHtml(s);
		return fileName_new;
	}
		
	protected File createOriginSVG(ArrayList<String> attributePair, String diagramPath, int originNumber) {
		File svgFile;
		String svg = getSVG(diagramPath);
	      try {
	    	  String fileName = getOriginSVGName(attributePair, originNumber);
	    	  svgFile = rwa.createFile(fileName);
	    	  OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(svgFile),"UTF-8");
	          out.write(svg);
	          out.close();
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
			String fileName = getOriginIndexHTMLName(attributePair);
		    htmlFile = rwa.createFile(fileName);	    
	    	OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(htmlFile),"UTF-8");
	
		    out.write("<!doctype html>\n");
		    out.write("<html><head>");
		    out.write("<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js\"></script>");
		    out.write("<script type=\"text/javascript\" src=\"../static/infoBox.js\"></script>");
		    out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"../static/infoBox.css\" />");
		    out.write("</head>");
		    out.write("<body><h3><center>Origins for "+replaceSpecialCharsForHTML(attributePair.toString())+"</center></h3><hr>");

		  	for (int i=0; i<origins.size(); i++) {
		  		String originSVG = getOriginSVGName(attributePair, i);
		  		String origin = origins.get(i);
		  		URLConnection originCon = new URL(origin).openConnection();
		  		String originName = originCon.getHeaderField("Content-Disposition");
		  		if (originName == null) {
//		  			Header Field not set or local file
		  			originName = origin.substring(origin.lastIndexOf("/")+1,origin.lastIndexOf("."));
		  		}
		  		else {
			  		originName = originName.substring(originName.indexOf("=")+1,originName.lastIndexOf(".") );
		  		}
		  		out.write("<div class=\"origin\">");
			    out.write("<div class=\"originName\">");
		        out.write("<td><A HREF=\""+originSVG+"\" target=\"content\"><font size = " +fontSize+">"+replaceSpecialCharsForHTML(originName)+"</font></A></td>");
			    out.write("</div>");
			    out.write("<div class=\"infoBox\">");
			    String description = replaceSpecialCharsForHTML(getDescription(origins.get(i)));
			    if (description.equals("")) {
				    out.write("<font>No Description given.</font>");
			    }
			    else {
				    out.write("<font>"+description+"</font>");
			    }
			    out.write("</div>");
			    out.write("</div>");
		  	}
	        out.write("</body></html>");
	        out.close();
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
			String fileName = getOriginHTMLName(attributePair);
		    htmlFile = rwa.createFile(fileName);
		    
	    	OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(htmlFile),"UTF-8");
		    out.write("<!doctype html>\n");
		    out.write("<html>\n<head>\n<title>Origins for " +replaceSpecialCharsForHTML(attributePair.toString())+ "</title>\n</head>");
		    out.write("<frameset cols=\"15%,85%\">");
		    out.write("<body>");
		    out.write("<frame name=index src=\""+ getOriginIndexHTMLName(attributePair)+"\">");
		    out.write("<frame name=content src=\""+ getOriginSVGName(attributePair, 0) + "\">");
		    out.write("</frameset>");
	        out.write("</body></html>");
	        out.close();
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
	
	public ExtractedConnectionList getExtractedConnections() {
		return extractedConnectionList;
	}
}
	

