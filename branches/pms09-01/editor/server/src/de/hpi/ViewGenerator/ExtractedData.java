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
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

class ExtractedData {
//	super class which holds the generic algorithms to extract data from a oryx model in json-format
//	and the call to merge extracted data from different models
//	needs a ReadWriteAdapter as parameter from which it can request the list with diagramPaths and the json belonging to one model
	protected ExtractedConnectionList extractedConnectionList;
	protected ReadWriteAdapter rwa;

	public ExtractedData(ReadWriteAdapter rwa) {
		this.rwa = rwa;
		this.extractedConnectionList = new ExtractedConnectionList();
	}	
	
	private ConnectionList extractFromChildShapes(JSONObject jsonObject, ConnectionList connectionList, ConnectorList connectorList,  HashMap<String, JSONObject> stencilLevels) {
		ConnectionList connectionList_new = connectionList;
		try {
//			remember jsonObject belonging to stencilId (for getting attributes belonging to parentStencils when already at childrenStencils level)
			String stencilId = jsonObject.getJSONObject("stencil").get("id").toString();
			stencilLevels.put(stencilId, jsonObject);
			
//			extract target attribute
			extractTargetAttribute(connectorList, connectionList_new, stencilLevels, stencilId);
					
//			extract source attribute by looking at outgoingArrays
			JSONArray outgoingArray = jsonObject.getJSONArray("outgoing");
			extractSourceAttribute(outgoingArray, connectorList, connectionList_new, stencilLevels, stencilId);
			
			JSONArray childShapesArray = jsonObject.getJSONArray("childShapes");
			if (!(childShapesArray.length() != 0) && connectorList.isPossibleParentStencil(stencilId)) {
//				no childShapes or stencilId is no possible parentStencil of any searched Connector
//				abort of recursion
			}
			else {
				for (int index = 0; index < childShapesArray.length(); index++) {
//					recursive extraction for all children
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
	
	private void extractTargetAttribute(ConnectorList connectorList, ConnectionList connectionList_new, HashMap<String, JSONObject> stencilLevels, String stencilId) {
		JSONObject jsonObject = stencilLevels.get(stencilId);
		try {						
			if (connectorList.containsConnectorWithStencil(stencilId)) {
//				stencil is possible Connector, get the connections for which it was the target
				 ArrayList<String> targetResultList = connectionList_new.matchInTargetMatchlist(jsonObject.getString("resourceId"));

//				for all connections for which it has been target, set the targetAttribute
				for(int i=0; i<targetResultList.size(); i++) {
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
//			iterate over outgoing shapes
			for(int i=0; i<outgoingArray.length(); i++){
				JSONObject outgoingObject = new JSONObject(outgoingArray.get(i).toString());
				String connectionId = outgoingObject.getString("resourceId");
//				look if outgoing shape is a searched connection and if it is, set the sourceAttribute for this connection 
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
//			at least one attribute has to be saved for stencilId
			DataToSave dataToSave = connectorList.dataToSaveForConnectorWithStencil(stencilId);
			String attributeToSave = dataToSave.getAttributeToSave(0);
			String stencilLevelToSave = dataToSave.getStencilLevelToSave(0);
			String jsonId = dataToSave.getJSONIdWithAttributeToSave(0);
			JSONObject saveObject = stencilLevels.get(stencilLevelToSave);
			String attributeToSaveFromSaveObject = extractAttribute(saveObject, jsonId, attributeToSave);

//			for each further attribute that has to be saved according to its DataToSave Object
//			create a string where all attributes are separated by a escaped newline character
			for(int a=1; a<dataToSave.size(); a++) {
				attributeToSave = dataToSave.getAttributeToSave(a);
				stencilLevelToSave = dataToSave.getStencilLevelToSave(a);
				saveObject = stencilLevels.get(stencilLevelToSave);
				jsonId = dataToSave.getJSONIdWithAttributeToSave(a);
				String attributeToSaveFromObject_tmp = extractAttribute(saveObject, jsonId, attributeToSave);
				attributeToSaveFromSaveObject = attributeToSaveFromObject_tmp + "\\n" + attributeToSaveFromSaveObject;
			}
			return attributeToSaveFromSaveObject;
		}
		catch (NullPointerException e) {
//			Connector with no DataToSave, will result in an empty line
			return null;
		}
	}
	
	private String replaceEscChars(String fileName) {
//		replacing escaped chars, e.g. for graphVizLabels
		String fileName_new = fileName.replace("\n", "").replace("\t","").replace("\r","");
		return fileName_new;
	}
	
	private String extractAttribute(JSONObject saveObject, String jsonId, String attributeToSave) {
//		trying to extract 'attributeToSave' from the JSONObject with 'jsonId' in 'saveObject'
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
			return replaceEscChars(attributeToSaveFromSaveObject);
		}
		catch (JSONException e) {
//			ConnectorElement where the saveLevel has no jsonId, possible bad Connectordefinition or old editor version
			  e.printStackTrace(); 
		}
		return "";
	}
	
	public ConnectionList extractConnection(JSONArray jsonArray, ConnectionList connectionList, String connection, ConnectorList connectorList) {
//		extract information from connection with name 'connection', add extracted information to 'connectionList'
//		extract from 'jsonArray', searching for connected Connectors form 'connectorList'
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
//		call to generic merge method on ExtractedConnectionList Class which can be used from subclasses
//		with right values for symmetric (if true: treat A:B and B:A as same key) and storeRecursive (if true: store A:A)
		extractedConnectionList.merge(connectionList, symmetric, storeRecursive);
	}
	
	private ConnectionList initializeTargetMatchList(JSONArray jsonArray, ConnectionList connectionList, String connection) {
//		memorize all target resourceIds of connections, which have to be matched
//		initialize ConnectionList with all resourceIds of searched connections
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
//		remove entries from connectionList where either target or source could not be found
//		e.g. if Connectors where not connected properly or not all possible Connectors were in used ConnectorList
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
		return rwa.getJSON(diagramPath);
	}
		
	public ExtractedConnectionList getExtractedConnectionList() {
		return extractedConnectionList;
	}
}
	

