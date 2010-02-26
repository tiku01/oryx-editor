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

public class ExtractedLanePassing extends ExtractedData {
	private String connection;
	private ConnectorList connectorList;
	private String layoutAlgorithm;
	private String graphLabel;
	private String svgName;
	private int rolesCount;
	private int handoversCount;


	public ExtractedLanePassing(ArrayList<String> diagramPaths, String toSavePath) {
		super(diagramPaths,toSavePath);
		this.handoversCount = 0;
		this.rolesCount = 0;
		this.connection = "SequenceFlow";
		this.graphLabel = "Handovers";
		this.svgName = "Handovers";
		this.layoutAlgorithm = "dot";
		initializeConnectorList();
		extractLanePassings(diagramPaths);
	}
	
	public String getSVGName() {
		return svgName;
	}
	
	public int getRolesCount() {
		return rolesCount;
	}
	
	public int getHandoversCount() {
		return handoversCount;
	}
	
	private void initializeConnectorList() {
		connectorList = new ConnectorList();
		ArrayList<String> parentListTaskEventGateway = new ArrayList<String>();
		parentListTaskEventGateway.add("Pool");
		parentListTaskEventGateway.add("Lane");
		parentListTaskEventGateway.add("Subprocess");
		parentListTaskEventGateway.add("EventSubprocess");
	
		
		DataToSave NamePoolNameLane = new DataToSave("name", "properties", "Lane");
		NamePoolNameLane.addDataToSave("name","properties", "Pool");
		
		connectorList.addConnector(new Connector("Task", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("Subprocess", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("CollapsedSubprocess", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("StartEscalationEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateEscalationEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateEscalationEventThrowing", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("EndEscalationEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("Exclusive_Databased_Gateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("EventbasedGateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("ParallelGateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("InclusiveGateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("ComplexGateway", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("StartNoneEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("StartMessageEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("StartTimerEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("StartConditionalEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("StartErrorEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("StartCompensationEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("StartSignalEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("StartMultipleEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("StartParallelMultipleEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateTimerEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateConditionalEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateMessageEventCatching", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateLinkEventCatching", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateErrorEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateCancelEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateCompensationEventCatching", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateSignalEventCatching", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateMultipleEventCatching", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateParallelMultipleEventCatching", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateMessageEventThrowing", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateLinkEventThrowing", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateCompensationEventThrowing", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateSignalEventThrowing", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("IntermediateMultipleEventThrowing", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("EndNoneEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("EndMessageEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("EndErrorEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("EndCancelEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("EndCompensationEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("EndSignalEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("EndMultipleEvent", NamePoolNameLane, parentListTaskEventGateway));
		connectorList.addConnector(new Connector("EndTerminateEvent", NamePoolNameLane, parentListTaskEventGateway));		
	}
	
	private void extractLanePassings(ArrayList<String> diagramPaths) {
		
		for (String diagramPath: diagramPaths) {
			
			ConnectionList connectionList = new ConnectionList(diagramPath);				
			String json = getJSON(diagramPath);
			JSONObject jsonObject = null;
			
			try {
				jsonObject = new JSONObject(json);
				JSONArray jsonArray = jsonObject.getJSONArray("childShapes");

				connectionList = extractConnection(jsonArray, connectionList, connection, connectorList);
			}
			catch (JSONException e) {
				e.printStackTrace(); 
			}			  
			
			merge(connectionList, false, false);	
		}
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
	
	private TranslatorInputNode createHumanAgentNode(String nodeId) {
		TranslatorInputNode humanAgentNode = new TranslatorInputNode(nodeId);
		humanAgentNode.setAttribute("shape", "box");
		humanAgentNode.setAttribute("imagescale", "true");
		humanAgentNode.setAttribute("labelloc", "b");
		humanAgentNode.setAttribute("margin", "1.11,0.01");
		humanAgentNode.setAttribute("image", "\"../static/human_agent.png\"");
		humanAgentNode.setAttribute("label", nodeId);
		rolesCount +=1;
		
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
		
//			treat any handover with same direction (same source and target) as the same handover			
			handoversCount +=1;
			
//			attributePairs should have a length of 2, a target and a source
			String sourceId = attributePair.get(0);
			String targetId = attributePair.get(1);
			
//			Node for source
			String sourceNodeId = "\"" + removeEscChars(sourceId) + "\"";
			TranslatorInputNode sourceNode;
			
			if (!done_Ids.contains(sourceId)) {
				sourceNode = createHumanAgentNode(sourceNodeId);
				input.addNode(sourceNode);
				done_Ids.add(sourceId);
			}
				
//			Node for target
			String targetNodeId = "\"" + removeEscChars(targetId) + "\"";
			TranslatorInputNode targetNode;

			if (!done_Ids.contains(targetId)) {
				targetNode = createHumanAgentNode(targetNodeId);
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
