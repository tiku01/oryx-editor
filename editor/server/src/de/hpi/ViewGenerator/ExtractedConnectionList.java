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


class ExtractedConnectionList {

	private HashMap<ArrayList<String>, ArrayList<String>> extractedConnectionList; 
	private HashMap<String,String> origins; 
	
	public ExtractedConnectionList() {
		extractedConnectionList = new HashMap<ArrayList<String>, ArrayList<String>>();
		origins = new HashMap<String,String>();
	}

	private void mergeConnectionWithId(String connectionId, ConnectionList connectionList, boolean symmetric, boolean storeRecursive){
		ArrayList<String> extractedConnectionKey1 = new ArrayList<String>();
		ArrayList<String> extractedConnectionKey2 = new ArrayList<String>();
		ConnectionAttributes connectionAttributes = connectionList.getConnectionAttributesFor(connectionId);
		String source = connectionAttributes.getSourceAttribute();
		String target = connectionAttributes.getTargetAttribute();		
		
		extractedConnectionKey1.add(source);
		extractedConnectionKey2.add(target);
		extractedConnectionKey1.add(target);
		extractedConnectionKey2.add(source);
		
		
		if (storeRecursive || (!source.equals(target))) {
			if ((extractedConnectionList.containsKey(extractedConnectionKey1))) {
				ArrayList<String> extractedConnectionValue = extractedConnectionList.get(extractedConnectionKey1);
				if (!extractedConnectionValue.contains(connectionId)) {
					extractedConnectionValue.add(connectionId);
				}
				extractedConnectionList.put(extractedConnectionKey1, extractedConnectionValue);
			}
			
			else if (!extractedConnectionList.containsKey(extractedConnectionKey1) && !symmetric){
				
				ArrayList<String> extractedConnectionValue = new ArrayList<String>();
				extractedConnectionValue.add(connectionId);				
				extractedConnectionList.put(extractedConnectionKey1, extractedConnectionValue);
			}
			
			else if (!extractedConnectionList.containsKey(extractedConnectionKey1) && symmetric){
				
				if (extractedConnectionList.containsKey(extractedConnectionKey2)) {
					
					ArrayList<String> extractedConnectionValue = extractedConnectionList.get(extractedConnectionKey2);
					if (!extractedConnectionValue.contains(connectionId)) {
						extractedConnectionValue.add(connectionId);
					}
					extractedConnectionList.put(extractedConnectionKey2, extractedConnectionValue);
					
				}
				else {
					ArrayList<String> extractedConnectionValue = new ArrayList<String>();
					extractedConnectionValue.add(connectionId);					
					extractedConnectionList.put(extractedConnectionKey1, extractedConnectionValue);
				}			
			}
		}
	}
	
	
	public void merge(ConnectionList connectionList, boolean symmetric, boolean storeRecursive){
		for (String connectionId: connectionList.connectionIds()) {
			origins.put(connectionId, connectionList.getOrigin());
			mergeConnectionWithId(connectionId, connectionList, symmetric, storeRecursive);
		}	
	}
	
	public ArrayList<String> getOriginsForConnectionAttributePair(ArrayList<String> connectionAttributes) {
		ArrayList<String> originsForCon = new ArrayList<String>();
		for (String connectionId: getResourceIdsFor(connectionAttributes)) {
			originsForCon.add(origins.get(connectionId));
		}
		return originsForCon;
	}
	
	public int size(){
		return extractedConnectionList.size();
	}
	
	public void removeConnectionAttributePair(ArrayList<String> connectionAttributes) {
		extractedConnectionList.remove(connectionAttributes);
	}
	
	public ArrayList<String> getResourceIdsFor(ArrayList<String> attributePair) {
		return extractedConnectionList.get(attributePair);
	}
	
	public void putResourceIdsFor(ArrayList<String> resourceIds, ArrayList<String> attributePair) {
		extractedConnectionList.put(attributePair, resourceIds);
	}
	
	public Set<ArrayList<String>> connectionAttributePairs() {
		return extractedConnectionList.keySet();
	}
	
	public boolean containsConnectionAttributePair(ArrayList<String> attributePair) {
		return extractedConnectionList.containsKey(attributePair);
	}	
}
