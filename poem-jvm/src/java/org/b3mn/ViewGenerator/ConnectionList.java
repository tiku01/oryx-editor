package org.b3mn.ViewGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

class ConnectionList {
//		a ConnectionList holds the extracted Data from one diagram
	
		private HashMap<String,ConnectionAttributes> connectionList;
		private Matchlist targetMatchlist;
		private String origin;

		
		public ConnectionList(String diagramPath) {
			origin = diagramPath;
			connectionList = new HashMap<String,ConnectionAttributes>();
			targetMatchlist = new Matchlist();
		}
		
		public String getOrigin() {
			return origin;
		}
		
		public void addConnection(String connectorId) {
			ConnectionAttributes connectionAttributes = new ConnectionAttributes();
			connectionList.put(connectorId, connectionAttributes);
		}
			
		public void addTargetAttributeForConnection(String targetAttribute, String connectorId) {
			ConnectionAttributes connectionAttributes = connectionList.get(connectorId);
			if (!connectionAttributes.hasTargetAttribute()) {
				connectionAttributes.setTargetAttribute(targetAttribute);
				connectionList.put(connectorId, connectionAttributes);
			}
		}
		
		public void addSourceAttributeForConnection(String sourceAttribute, String connectorId) {
			ConnectionAttributes connectionAttributes = connectionList.get(connectorId);
			if (!connectionAttributes.hasSourceAttribute()) {
				connectionAttributes.setSourceAttribute(sourceAttribute);
				connectionList.put(connectorId, connectionAttributes);
			}
		}
		
		public int size(){
			return connectionList.size();
		}
		
		public ConnectionAttributes getConnectionAttributesFor(String connectionId) {
			return connectionList.get(connectionId);
		}
		
		public Set<String> connectionIds() {
			return connectionList.keySet();
		}
		
		public boolean containsConnectionId(String connectionId) {
			return connectionList.containsKey(connectionId);
		}
		
		public void addToTargetMatchlist(String connectionId, String connectorId) {
			targetMatchlist.add(connectionId, connectorId);
		}
		
		public ArrayList<String> matchInTargetMatchlist(String connectorId) {
			ArrayList<String> resultList = targetMatchlist.match(connectorId);
			return resultList;
		}
}
