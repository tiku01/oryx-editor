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
import java.util.Set;

class ConnectionList {
//	a ConnectionList holds the extracted data from one model

	private HashMap<String,ConnectionAttributes> connectionList;
	private Matchlist targetMatchlist;
	private String origin;
		
	public ConnectionList(String diagramPath) {
		this.origin = diagramPath;
		this.connectionList = new HashMap<String,ConnectionAttributes>();
		this.targetMatchlist = new Matchlist();
	}
		
	public String getOrigin() {
//		return the diagramPath from which this ConnectionList was extracted
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
