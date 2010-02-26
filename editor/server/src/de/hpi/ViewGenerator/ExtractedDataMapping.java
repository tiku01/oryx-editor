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
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExtractedDataMapping extends ExtractedData {
	private String connection_uni;
	private String connection_bi;
	private String connection_un;
	private String connection_help;
	private String layoutAlgorithm;
	private String graphLabel;
	private ConnectorList connectorList_dir;
	private ConnectorList connectorList_undir;
	private ConnectorList connectorList_help;
	private String svgName;
	private int dataObjectsCount;

	
	public ExtractedDataMapping(ArrayList<String> diagramPaths, String toSavePath) {
		super(toSavePath);
		this.dataObjectsCount = 0;
		this.connection_uni = "Association_Unidirectional";
		this.connection_bi = "Association_Bidirectional";
		this.connection_un = "Association_Undirected";
		this.connection_help = "SequenceFlow";
		this.layoutAlgorithm = "dot";
		this.graphLabel = "Information_Access";
		this.svgName = "Information Access";
		initializeConnectorLists();
		extractDataMappings(diagramPaths);
	}
	
	public String getSVGName() {
		return svgName;
	}
	
	public int getDataObjectsCount() {
		return dataObjectsCount;
	}
	
	private void initializeConnectorLists() {
		
		ArrayList<String> parentListNoParents = new ArrayList<String>();
		ArrayList<String> parentListTaskEventGateway = new ArrayList<String>();
		parentListTaskEventGateway.add("Pool");
		parentListTaskEventGateway.add("Lane");
		parentListTaskEventGateway.add("Subprocess");
		parentListTaskEventGateway.add("EventSubprocess");
		
		ArrayList<String> parentListData = new ArrayList<String>();
		parentListData.add("Pool");
		parentListData.add("Lane");
		parentListData.add("Subprocess");
		
//		possible in oryx, but not intended from bpmn
//		DataToSave NamePool = new DataToSave("name", "properties", "Pool");
//		DataToSave NameCollapsedPool = new DataToSave("name", "properties", "CollapsedPool");
//		
		DataToSave NamePoolNameLane = new DataToSave("name", "properties", "Lane");
		NamePoolNameLane.addDataToSave("name","properties", "Pool");
		
		DataToSave StencilIdDataObjectNamePoolNameLaneNameDataObject = new DataToSave ("name", "properties", "DataObject");
		StencilIdDataObjectNamePoolNameLaneNameDataObject.addDataToSave("name","properties", "Lane");
		StencilIdDataObjectNamePoolNameLaneNameDataObject.addDataToSave("name","properties", "Pool");		
		StencilIdDataObjectNamePoolNameLaneNameDataObject.addDataToSave("id","stencil", "DataObject");
		
		DataToSave StencilIdDataStoreNamePoolNameLaneNameDataStore = new DataToSave ("name", "properties", "DataStore");
		StencilIdDataStoreNamePoolNameLaneNameDataStore.addDataToSave("name","properties", "Lane");
		StencilIdDataStoreNamePoolNameLaneNameDataStore.addDataToSave("name","properties", "Pool");	
		StencilIdDataStoreNamePoolNameLaneNameDataStore.addDataToSave("id","stencil", "DataStore");
		
//		initialize connectorList for directed associations
		connectorList_dir = new ConnectorList();
		connectorList_dir.addConnector(new Connector("DataObject", StencilIdDataObjectNamePoolNameLaneNameDataObject,parentListData));
		connectorList_dir.addConnector(new Connector("DataStore", StencilIdDataStoreNamePoolNameLaneNameDataStore,parentListData));
		connectorList_dir.addConnector(new Connector("Task", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_dir.addConnector(new Connector("Subprocess", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_dir.addConnector(new Connector("CollapsedSubprocess", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_dir.addConnector(new Connector("Exclusive_Databased_Gateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_dir.addConnector(new Connector("EventbasedGateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_dir.addConnector(new Connector("ParallelGateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_dir.addConnector(new Connector("InclusiveGateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_dir.addConnector(new Connector("ComplexGateway", NamePoolNameLane, parentListTaskEventGateway));
//		connectorList_dir.addConnector(new Connector("CollapsedPool", NameCollapsedPool, parentListNoParents));
//		connectorList_dir.addConnector(new Connector("Pool", NamePool, parentListNoParents));
		
//		initialize connectorList for undirected associations		
		DataToSave SequenceFlowId = new DataToSave("resourceId", "SequenceFlow");
		
		connectorList_undir = new ConnectorList();
		connectorList_undir.addConnector(new Connector("DataObject", StencilIdDataObjectNamePoolNameLaneNameDataObject,parentListData));
		connectorList_undir.addConnector(new Connector("SequenceFlow", SequenceFlowId,parentListNoParents));
		
		
//		initialize connectorList for helperconnection (=SequenceFlow)		
		
		connectorList_help = new ConnectorList();	
//		only possible in old versions because of a bug, not intended from bpmn
//		connectorList_help.addConnector(new Connector("Association_Undirected", AssociationUndirectedId,parentListNoParents));
		connectorList_help.addConnector(new Connector("Task", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("Subprocess", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("CollapsedSubprocess", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("StartEscalationEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateEscalationEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateEscalationEventThrowing", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("EndEscalationEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("Exclusive_Databased_Gateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("EventbasedGateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("ParallelGateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("InclusiveGateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("ComplexGateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("StartNoneEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("StartMessageEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("StartTimerEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("StartConditionalEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("StartErrorEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("StartCompensationEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("StartSignalEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("StartMultipleEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("StartParallelMultipleEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateTimerEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateConditionalEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateMessageEventCatching", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateLinkEventCatching", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateErrorEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateCancelEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateCompensationEventCatching", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateSignalEventCatching", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateMultipleEventCatching", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateParallelMultipleEventCatching", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateMessageEventThrowing", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateLinkEventThrowing", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateCompensationEventThrowing", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateSignalEventThrowing", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("IntermediateMultipleEventThrowing", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("EndNoneEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("EndMessageEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("EndErrorEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("EndCancelEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("EndCompensationEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("EndSignalEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("EndMultipleEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList_help.addConnector(new Connector("EndTerminateEvent", NamePoolNameLane, parentListTaskEventGateway));
	}
	
	private void extractDataMappings(ArrayList<String> diagramPaths) {
				
		for (String diagramPath: diagramPaths) {
			
			ConnectionList connectionList_uni = new ConnectionList(diagramPath);
			ConnectionList connectionList_bi = new ConnectionList(diagramPath);
			ConnectionList connectionList_un = new ConnectionList(diagramPath);
			ConnectionList connectionList_help = new ConnectionList(diagramPath);
				  		
			String json = getJSON(diagramPath);
	
			JSONObject jsonObject = null;
			try {
				jsonObject = new JSONObject(json.toString());	
				JSONArray jsonArray = jsonObject.getJSONArray("childShapes");

				connectionList_uni = extractConnection(jsonArray, connectionList_uni, connection_uni, connectorList_dir);
				connectionList_bi = extractConnection(jsonArray, connectionList_bi, connection_bi, connectorList_dir);
				connectionList_un = extractConnection(jsonArray, connectionList_un, connection_un, connectorList_undir);
				connectionList_help = extractConnection(jsonArray, connectionList_help, connection_help, connectorList_help);
					  			  
			}
			catch (JSONException e) {
				e.printStackTrace(); 
			}
				
			merge(connectionList_uni, false, true);
			merge(splitToUnidirected(connectionList_bi), false, true);
			merge(joinSplitToUnidirected(connectionList_un, connectionList_help),false, true);
		}
	}
	
	private ConnectionList splitToUnidirected(ConnectionList connectionList_bidirected) {
		String prefixForDir1 = "1_";
		String prefixForDir2 = "2_";
		String origin = connectionList_bidirected.getOrigin();
		
		ConnectionList connectionList_uni = new ConnectionList(origin);
		for (String connectionId: connectionList_bidirected.connectionIds()) {

			String connectionIdForDir1 = prefixForDir1 + connectionId;
			String connectionIdForDir2 = prefixForDir2 + connectionId;
			ConnectionAttributes connectionAttributes = connectionList_bidirected.getConnectionAttributesFor(connectionId);
			connectionList_uni.addConnection(connectionIdForDir1);
			connectionList_uni.addTargetAttributeForConnection(connectionAttributes.getTargetAttribute(), connectionIdForDir1);
			connectionList_uni.addSourceAttributeForConnection(connectionAttributes.getSourceAttribute(), connectionIdForDir1);
			
			connectionList_uni.addConnection(connectionIdForDir2);
			connectionList_uni.addTargetAttributeForConnection(connectionAttributes.getSourceAttribute(), connectionIdForDir2);
			connectionList_uni.addSourceAttributeForConnection(connectionAttributes.getTargetAttribute(), connectionIdForDir2);			
		}
		return connectionList_uni;
	}
	

	private ConnectionList joinSplitToUnidirected(ConnectionList connectionList_undirected, ConnectionList directedJoinList) {
		String prefixForDir1 = "1_";
		String prefixForDir2 = "2_";
		String origin = connectionList_undirected.getOrigin();
		
		ConnectionList connectionList_uni = new ConnectionList(origin);
		
		for (String connectionId: connectionList_undirected.connectionIds()) {
			ConnectionAttributes undirected_attributes = connectionList_undirected.getConnectionAttributesFor(connectionId);
			String undirected_target = undirected_attributes.getTargetAttribute();
			String undirected_source = undirected_attributes.getSourceAttribute();
			
			if (directedJoinList.containsConnectionId(undirected_target)) {
//				target of undirected connection was sequence flow
				String connectionIdForDir1 = prefixForDir1 + connectionId;
				String connectionIdForDir2 = prefixForDir2 + connectionId;
				
				ConnectionAttributes directed_attributes = directedJoinList.getConnectionAttributesFor(undirected_target);
				String directed_target = directed_attributes.getTargetAttribute();
				String directed_source = directed_attributes.getSourceAttribute();
				
//				directed association from source of undirected connection to target of directed connection 
				connectionList_uni.addConnection(connectionIdForDir1);
				connectionList_uni.addTargetAttributeForConnection(directed_target, connectionIdForDir1);
				connectionList_uni.addSourceAttributeForConnection(undirected_source, connectionIdForDir1);
				
//				directed association from source of directed connection to source of undirected association
				connectionList_uni.addConnection(connectionIdForDir2);
				connectionList_uni.addTargetAttributeForConnection(undirected_source, connectionIdForDir2);
				connectionList_uni.addSourceAttributeForConnection(directed_source, connectionIdForDir2);	
			}
			
			if (directedJoinList.containsConnectionId(undirected_source)) {
//				source of undirected connection was sequence flow
				String connectionIdForDir1 = prefixForDir1 + connectionId;
				String connectionIdForDir2 = prefixForDir2 + connectionId;
				
				ConnectionAttributes directed_attributes = directedJoinList.getConnectionAttributesFor(undirected_source);
				String directed_target = directed_attributes.getTargetAttribute();
				String directed_source = directed_attributes.getSourceAttribute();
				
//				directed association from target of undirected connection to target of directed connection 
				connectionList_uni.addConnection(connectionIdForDir1);
				connectionList_uni.addTargetAttributeForConnection(directed_target, connectionIdForDir1);
				connectionList_uni.addSourceAttributeForConnection(undirected_target, connectionIdForDir1);
				
//				directed association from source of directed connection to target of undirected association
				connectionList_uni.addConnection(connectionIdForDir2);
				connectionList_uni.addTargetAttributeForConnection(undirected_target, connectionIdForDir2);
				connectionList_uni.addSourceAttributeForConnection(directed_source, connectionIdForDir2);	
			}			
		}
		return connectionList_uni;
	}
	
	
	public void generateSVG() {
		TranslatorInput translatorInput = createTranslatorInput(extractedConnectionList);
		generateFiles(graphLabel, translatorInput, layoutAlgorithm, svgName);
	}
	
	private Set<ArrayList<String>> removeRedundantEdges(Set<ArrayList<String>> redundant) {
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
	
	private TranslatorInputNode createDataObjectNode(String nodeId) {
		TranslatorInputNode dataObjectNode = new TranslatorInputNode(nodeId);
		dataObjectNode.setAttribute("shape", "box");
		dataObjectNode.setAttribute("imagescale", "true");
		dataObjectNode.setAttribute("labelloc", "c");
		dataObjectNode.setAttribute("margin", "0.81,0.155");
		dataObjectNode.setAttribute("image", "\"../static/data_object.png\"");
		dataObjectNode.setAttribute("label", nodeId);
		dataObjectsCount +=1;
		
		return dataObjectNode;
	}
	
	private TranslatorInputNode createDataStoreNode(String nodeId) {
		TranslatorInputNode dataStoreNode = new TranslatorInputNode(nodeId);
		dataStoreNode.setAttribute("shape", "box");
		dataStoreNode.setAttribute("imagescale", "true");
		dataStoreNode.setAttribute("labelloc", "b");
		dataStoreNode.setAttribute("margin", "0.81,0.155");
		dataStoreNode.setAttribute("image", "\"../static/data_store.png\"");
		dataStoreNode.setAttribute("label", nodeId);
		dataObjectsCount +=1;
		
		return dataStoreNode;
	}
	
	private TranslatorInputNode createHumanAgentNode(String nodeId) {
		TranslatorInputNode humanAgentNode = new TranslatorInputNode(nodeId);
		humanAgentNode.setAttribute("shape", "box");
		humanAgentNode.setAttribute("imagescale", "true");
		humanAgentNode.setAttribute("labelloc", "b");
		humanAgentNode.setAttribute("margin", "1.11,0.01");
		humanAgentNode.setAttribute("image", "\"../static/human_agent.png\"");
		humanAgentNode.setAttribute("label", nodeId);
		
		return humanAgentNode;
	}
	
	private TranslatorInputEdge createEdge(String sourceNodeId, String targetNodeId, String urlAttribute) {
		TranslatorInputEdge edge = new TranslatorInputEdge(sourceNodeId, targetNodeId);
		edge.setAttribute("URL", urlAttribute);
		edge.setAttribute("target", "_blank");
		return edge;
	}
	
	private TranslatorInput createTranslatorInput(ExtractedConnectionList extractedConnectionList) {
		TranslatorInput input = new TranslatorInput(layoutAlgorithm);
		ArrayList<String> done_Ids = new ArrayList<String>();

		for (ArrayList<String> attributePair: (removeRedundantEdges(extractedConnectionList.connectionAttributePairs()))) {
//			attributePairs should have a length of 2, a target and a source
			String sourceId = attributePair.get(0);
			String targetId = attributePair.get(1);
				
//			Node for source
			String sourceNodeId = "\"" + sourceId + "\"";
			
			if (!done_Ids.contains(sourceId)) {
				TranslatorInputNode sourceNode;
				
				if (sourceId.contains("DataObject\\n")) {
					sourceNode = createDataObjectNode(sourceNodeId);
				}
				else if (sourceId.contains("DataStore\\n")) {
					sourceNode = createDataStoreNode(sourceNodeId);
				}
				else {
					sourceNode = createHumanAgentNode(sourceNodeId);
				}
				input.addNode(sourceNode);
				done_Ids.add(sourceId);
			}
				
//			Node for target
			String targetNodeId = "\"" + targetId + "\"";
			
			if (!done_Ids.contains(targetId)) {
				TranslatorInputNode targetNode;
				
				if (targetId.contains("DataObject\\n")) {
					targetNode = createDataObjectNode(targetNodeId);
				}
				else if (targetId.contains("DataStore\\n")) {
					targetNode = createDataStoreNode(targetNodeId);
				}
				else {
					targetNode = createHumanAgentNode(targetNodeId);
				}
				input.addNode(targetNode);
				done_Ids.add(targetId);
			}
				
//			edge between source and target 
			String urlAttribute = "\"" + getOriginHTMLName(attributePair) + "\"";
			TranslatorInputEdge edge = createEdge(sourceNodeId, targetNodeId, urlAttribute);
			input.addEdge(edge);											
		}
		return input;
	}
}
