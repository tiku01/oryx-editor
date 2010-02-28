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
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExtractedLanePassing extends ExtractedData {
	private String connection;
	private ConnectorList connectorList;
	private int rolesCount;
	private int handoversCount;


	public ExtractedLanePassing(ReadWriteAdapter rwa) {
		super(rwa);
		this.connection = "SequenceFlow";
		this.initializeConnectorList();
		this.extractLanePassings(rwa.getDiagramPaths());
	}
	
	private void initializeCounts(Set<String> diagramPaths) {
		ArrayList<String> done_Ids = new ArrayList<String>();
		handoversCount = 0;
		rolesCount = 0;	
		for (ArrayList<String> attributePair: (removeRedundantEdges(extractedConnectionList.connectionAttributePairs()))) {
		
//			treat any handover with same direction (same source and target) as the same handover			
			handoversCount +=1;
			
//			attributePairs should have a length of 2, a target and a source
			String sourceId = attributePair.get(0);
			String targetId = attributePair.get(1);
			
			if (!done_Ids.contains(sourceId)) {
				rolesCount +=1;	
				done_Ids.add(sourceId);
			}
			if (!done_Ids.contains(targetId)) {
				rolesCount +=1;
				done_Ids.add(targetId);
			}										
		}
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
	
	private void extractLanePassings(Set<String> diagramPaths) {
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
		initializeCounts(diagramPaths);
	}
}
