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


public class ExtractedCommunications extends ExtractedData {
	private String connection;
	private ConnectorList connectorList;
	private String layoutAlgorithm;
//	id-attribute is used temporarily, a separate attribute should be created in bpmn2.0 stencil-definition for messageflows 
	private String correlationKeyAttribute;
	private String graphLabel;
	private String svgName;
	private HashMap<String,ArrayList<String>> correlationKeyDictionary;
	private int interactionsCount;
	
	public ExtractedCommunications(ArrayList<String> diagramPaths, String toSavePath) {
		super(toSavePath);
		this.interactionsCount = 0;
		this.connection = "MessageFlow";
		this.graphLabel = "Conversation_View";
		this.svgName = "Conversation View";
		this.layoutAlgorithm = "neato";
		this.correlationKeyAttribute = "correlation_key";
		initializeConnectorList();
		this.correlationKeyDictionary = new HashMap<String,ArrayList<String>>();
		extractCommunications(diagramPaths);
		fusionCommunicationsOnCorrelationKey();
	}
	
	public String getSVGName() {
		return svgName;
	}
	
	public int getInteractionsCount() {
		return interactionsCount;
	}
	
	private void initializeConnectorList() {
		connectorList = new ConnectorList();
		ArrayList<String> parentListNoParents = new ArrayList<String>();
		
		ArrayList<String> parentListOnlyPool = new ArrayList<String>();
		parentListOnlyPool.add("Pool");
		
		ArrayList<String> parentListTaskEventGateway = new ArrayList<String>();
		parentListTaskEventGateway.add("Pool");
		parentListTaskEventGateway.add("Lane");
		parentListTaskEventGateway.add("Subprocess");
		parentListTaskEventGateway.add("EventSubprocess");
		
		DataToSave NamePool = new DataToSave("name", "properties", "Pool");
		DataToSave NameCollapsedPool = new DataToSave("name", "properties", "CollapsedPool");
		
		connectorList.addConnector(new Connector("CollapsedPool", NameCollapsedPool, parentListNoParents));
		connectorList.addConnector(new Connector("Pool", NamePool, parentListNoParents));
		connectorList.addConnector(new Connector("CollapsedSubprocess", NamePool, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("Subprocess", NamePool, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("Task", NamePool, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("Lane", NamePool, parentListOnlyPool));
		connectorList.addConnector(new Connector("IntermediateMessageEventCatching", NamePool, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("EndMessageEvent", NamePool, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("StartMessageEvent", NamePool, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateMessageEventThrowing", NamePool, parentListTaskEventGateway));
	}

	
	private void extractCommunications(ArrayList<String> diagramPaths) {
		
		for (String diagramPath: diagramPaths) {
			
			ConnectionList connectionList = new ConnectionList(diagramPath);	
			String json = getJSON(diagramPath);
			JSONObject jsonObject = null;
			
			try {
				jsonObject = new JSONObject(json);
				JSONArray jsonArray = jsonObject.getJSONArray("childShapes");
				HashMap<String,ArrayList<String>> correlationKeyDic_part = createCorrelationKeyDictionary(jsonArray);
				mergeCorrelationKeyDictionary(correlationKeyDic_part);
				connectionList = extractConnection(jsonArray, connectionList, connection, connectorList);
			}
			catch (JSONException e) {
				e.printStackTrace(); 
			}			  
			merge(connectionList, true, false);
		}
	}
	
	private HashMap<String,ArrayList<String>> createCorrelationKeyDictionary(JSONArray jsonArray) {
		HashMap<String,ArrayList<String>> correlationKeyDic_part = new HashMap<String,ArrayList<String>>();
		try {
			for(int index = 0; index < jsonArray.length(); index++) {
				JSONObject jsonObject = new JSONObject(jsonArray.get(index).toString());
				
				if (jsonObject.getJSONObject("stencil").get("id").equals(connection)) {
					
					String resourceId = jsonObject.getString("resourceId");		
					String correlationKey = jsonObject.getJSONObject("properties").getString(correlationKeyAttribute);
//					System.out.println(correlationKey);
					
					if (!correlationKey.equals("")) {
						if (!correlationKeyDic_part.containsKey(correlationKey)) {
							ArrayList<String> value = new ArrayList<String>();
							value.add(resourceId);
							correlationKeyDic_part.put(correlationKey, value);
						}
						else {
							ArrayList<String> value = correlationKeyDic_part.get(correlationKey);
							value.add(resourceId);
							correlationKeyDic_part.put(correlationKey, value);
						}
					}					
				}
			}
		}
		catch (JSONException e) {
//			correlationKeyAttribute no attribute of MessageFlow in the stencilset
//			treat as if correlation_key was empty string - no entry in the correlationKeyDictionary
		}
		return correlationKeyDic_part;		
	}
	
	private void fusionCommunicationsOnCorrelationKey() {
		HashMap<String,ArrayList<String>> communicationDictionary = new HashMap<String,ArrayList<String>>();
		
		for (ArrayList<String> attributePair: extractedConnectionList.connectionAttributePairs()) {
			ArrayList<String> resourceIds = extractedConnectionList.getResourceIdsFor(attributePair);
			
			for (String resourceId: resourceIds) {		
				communicationDictionary.put(resourceId, attributePair);
			}
		}
		
		for (String correlationKey: correlationKeyDictionary.keySet()) {
			
			ArrayList<String> resourceIds = correlationKeyDictionary.get(correlationKey);
			ArrayList<String> participants_new = calculateNewParticipants(resourceIds, communicationDictionary);
			applyNewParticipants(participants_new, resourceIds, communicationDictionary);		
		}
	}
	
	
	private void applyNewParticipants(ArrayList<String> participants_new, ArrayList<String> resourceIds, HashMap<String,ArrayList<String>> communicationDictionary) {
		for (String resourceId: resourceIds){
			if (communicationDictionary.containsKey(resourceId)) {
				ArrayList<String> participants = communicationDictionary.get(resourceId);
				if (!participants.equals(participants_new)) {
					
					ArrayList<String> ids_tmp = extractedConnectionList.getResourceIdsFor(participants);
					if (ids_tmp.size() == 1) {
						extractedConnectionList.removeConnectionAttributePair(participants);
					}
					else {
						ids_tmp.remove(resourceId);
						extractedConnectionList.putResourceIdsFor(ids_tmp, participants);
					}
					
					if (!extractedConnectionList.containsConnectionAttributePair(participants_new)) {
						ArrayList<String> ids = new ArrayList<String>();
						ids.add(resourceId);
						extractedConnectionList.putResourceIdsFor(ids, participants_new);
					}
					else {
						ArrayList<String> ids = extractedConnectionList.getResourceIdsFor(participants_new);
						ids.add(resourceId);
						extractedConnectionList.putResourceIdsFor(ids, participants_new);
					}
				}
			}
		}
	}
	
	private ArrayList<String> calculateNewParticipants(ArrayList<String> resourceIds, HashMap<String,ArrayList<String>> communicationDictionary) {
		String resourceId = resourceIds.get(0);
		ArrayList<String> participants_new = new ArrayList<String>();
		int i = 0;
		
		while (i<resourceIds.size()) {
			resourceId = resourceIds.get(i);
			if (communicationDictionary.containsKey(resourceId)) {
				ArrayList<String> participants = communicationDictionary.get(resourceId);
				for (String participant: participants) {
					if (!participants_new.contains(participant)) {
						participants_new.add(participant);
					}
				}
			}
			i+=1;
		}
		return participants_new;
	}
	
	
	private void mergeCorrelationKeyDictionary(HashMap<String, ArrayList<String>> correlationKeyDic_part) {
		for (String correlationKey: correlationKeyDic_part.keySet()) {
			if (!correlationKeyDictionary.containsKey(correlationKey)) {
				ArrayList<String> value = correlationKeyDic_part.get(correlationKey);
				correlationKeyDictionary.put(correlationKey, value);
			}
			else {
				ArrayList<String> value = correlationKeyDictionary.get(correlationKey);
				ArrayList<String> value_add = correlationKeyDic_part.get(correlationKey);

				for (String element: value_add) {
					if (!value.contains(element)) {
						value.add(element);
					}
				}
				correlationKeyDictionary.put(correlationKey, value);
				
//				remove empty key-entries, because an empty string means the attribute is not set
				
				correlationKeyDictionary.remove("");
				correlationKeyDictionary.remove(null);
				
			}
		}
//		System.out.println(correlationKeyDictionary);
	}
	
	public void generateSVG() {
		TranslatorInput translatorInput = createTranslatorInput(extractedConnectionList);
		generateFiles(graphLabel, translatorInput, layoutAlgorithm, svgName);
	}
	
	private TranslatorInput createTranslatorInput(ExtractedConnectionList extractedConnectionList) {
		TranslatorInput input = new TranslatorInput(layoutAlgorithm);
		ArrayList<String> done_participantIds = new ArrayList<String>();
		ArrayList<String> done_communicationIds = new ArrayList<String>();
		ArrayList<TranslatorInputNode> communicationNodes = new ArrayList<TranslatorInputNode>();
				
		for (ArrayList<String> attributePair: extractedConnectionList.connectionAttributePairs()) {
//			count one communication as one interaction
			interactionsCount += 1;
			
//			Node for communication
			String communicationNodeId = "\"" + attributePair.toString() + "\"";
			if (!done_communicationIds.contains(communicationNodeId)) {
				
				TranslatorInputNode communicationNode = new TranslatorInputNode(communicationNodeId);
				communicationNode.setAttribute("shape", "hexagon");
				communicationNode.setAttribute("label", "\"" + "   " + "\"");
				communicationNode.setAttribute("width", ".3");
				communicationNode.setAttribute("height", ".3");
				communicationNode.setAttribute("fixedsize", "true");
				communicationNode.setAttribute("URL", "\"" + replaceBadChars(attributePair.toString()) + ".html" + "\"");
				communicationNode.setAttribute("target", "_blank");

//				Store communicationNode in communicationNodes - communicationNodes have to be added after participantNodes,
//				because otherwise the URL attribute will not be set properly by GraphViz
				
				communicationNodes.add(communicationNode);
				done_communicationIds.add(communicationNodeId);
			}
	
			
			for (String participant: attributePair) {
				
//				Node for participant
				String participantNodeId = "\"" + participant + "\"";
				if (!done_participantIds.contains(participantNodeId)) {
					TranslatorInputNode participantNode = new TranslatorInputNode(participantNodeId);
					participantNode.setAttribute("shape", "box");
					participantNode.setAttribute("label", "\"" + participant + "\"");

					input.addNode(participantNode);
					done_participantIds.add(participantNodeId);
				}
				
//				Edge between participant and communication
				input.addEdge(new TranslatorInputEdge(participantNodeId,communicationNodeId));					
			}
		}	
		
//		add previously stored communicationNodes to the TranslatorInput
		for (TranslatorInputNode comNode: communicationNodes) {
			input.addNode(comNode);
		}
		return input;
	}
}
