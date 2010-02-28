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
import java.util.List;
import java.util.Set;

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
	
	protected Set<ArrayList<String>> removeRedundantEdges(Set<ArrayList<String>> redundant) {
		Set<ArrayList<String>> no_redundant = redundant;
		ArrayList<ArrayList<String>> redundant_tmp = new ArrayList<ArrayList<String>>();
		
		for (ArrayList<String> attributePair: redundant) {
			redundant_tmp.add(attributePair);
		}
		
		for (int i=0; i<redundant_tmp.size();i++) {
			ArrayList<String> attributePair = redundant_tmp.get(i);

			List<ArrayList<String>> redundant_subcol = new ArrayList<ArrayList<String>>();
			redundant_subcol = redundant_tmp.subList(i, redundant_tmp.size()-1);
			
			if (redundant_subcol.contains(attributePair)) {
				int index = redundant_subcol.indexOf(attributePair) + i;
				redundant_tmp.remove(index);
				no_redundant.remove(index);
			}
		}
		return no_redundant;
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
		return rwa.getJSON(diagramPath);
	}
		
	public ExtractedConnectionList getExtractedConnectionList() {
		return extractedConnectionList;
	}
}
	

