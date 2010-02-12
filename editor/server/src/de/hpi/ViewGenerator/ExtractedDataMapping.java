package de.hpi.ViewGenerator;

import java.util.ArrayList;
import java.util.Set;
import java.util.List;


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
	private TranslatorInput translatorInput;
	private ConnectorList connectorList_dir;
	private ConnectorList connectorList_undir;
	private ConnectorList connectorList_help;
	private SVGGenerator generator;
	private String svgName;
	private int dataObjectsCount;

	
	public ExtractedDataMapping(ArrayList<String> diagramPaths, String toSavePath) {
		super(toSavePath);
		dataObjectsCount = 0;
		connection_uni = "Association_Unidirectional";
		connection_bi = "Association_Bidirectional";
		connection_un = "Association_Undirected";
		connection_help = "SequenceFlow";
		layoutAlgorithm = "dot";
		graphLabel = "Information_Access";
		svgName = "Information Access";
		initializeConnectorLists();
		extractDataMappings(diagramPaths);
		translatorInput = createTranslatorInput(extractedConnectionList);
		generator = new SVGGenerator(toSavePath, graphLabel, translatorInput, layoutAlgorithm, svgName);
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
		createOriginSVGs(extractedConnectionList);
		createOriginsHTMLs(extractedConnectionList);
		generator.generateSVG();
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
				TranslatorInputNode sourceNode = new TranslatorInputNode(sourceNodeId);
				sourceNode.setAttribute("shape", "box");
				sourceNode.setAttribute("imagescale", "true");

				if (sourceId.contains("DataObject\\n")) {
					sourceNode.setAttribute("labelloc", "c");
					sourceNode.setAttribute("margin", "0.81,0.155");
					sourceNode.setAttribute("image", "\"static/data_object.png\"");
					dataObjectsCount +=1;

				}
				else if (sourceId.contains("DataStore\\n")) {
					sourceNode.setAttribute("labelloc", "b");
					sourceNode.setAttribute("margin", "0.81,0.155");
					sourceNode.setAttribute("image", "\"static/data_store.png\"");
					dataObjectsCount +=1;
				}
				else {
					sourceNode.setAttribute("labelloc", "b");
					sourceNode.setAttribute("margin", "1.11,0.01");
					sourceNode.setAttribute("image", "\"static/human_agent.png\"");
				}
				
				sourceNode.setAttribute("label", sourceNodeId);
				input.addNode(sourceNode);
				done_Ids.add(sourceId);
			}
				
//			Node for target
			String targetNodeId = "\"" + targetId + "\"";
			
			if (!done_Ids.contains(targetId)) {
				TranslatorInputNode targetNode = new TranslatorInputNode(targetNodeId);
				targetNode.setAttribute("shape", "box");
				targetNode.setAttribute("imagescale", "true");
				
				if (targetId.contains("DataObject\\n")) {
					targetNode.setAttribute("labelloc", "c");
					targetNode.setAttribute("margin", "0.81,0.155");
					targetNode.setAttribute("image", "\"static/data_object.png\"");
					dataObjectsCount +=1;

				}
				else if (targetId.contains("DataStore\\n")) {
					targetNode.setAttribute("labelloc", "b");
					targetNode.setAttribute("margin", "0.81,0.155");
					targetNode.setAttribute("image", "\"static/data_store.png\"");
					dataObjectsCount +=1;
				}
				else {
					targetNode.setAttribute("labelloc", "b");
					targetNode.setAttribute("image", "\"static/human_agent.png\"");
					targetNode.setAttribute("margin", "1.11,0.01");
				}
				
				targetNode.setAttribute("label", targetNodeId);
				input.addNode(targetNode);
				done_Ids.add(targetId);
			}
				
//			edge between source and target 
			TranslatorInputEdge edge = new TranslatorInputEdge(sourceNodeId, targetNodeId);
			edge.setAttribute("URL", "\"" + replaceBadChars(attributePair.toString()) + ".html" + "\"");
			edge.setAttribute("target", "_blank");

			input.addEdge(edge);											
		}
		return input;
	}
}
