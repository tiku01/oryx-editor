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

class ExtractedConnectionList {
//	an ExtractedConnectionList can hold the extracted data of multiple models
//	for this it can merge a ConnectionList to its hold data
	private HashMap<ArrayList<String>, ArrayList<String>> extractedConnectionList; 
	private HashMap<String,String> origins; 
	
	public ExtractedConnectionList() {
		this.extractedConnectionList = new HashMap<ArrayList<String>, ArrayList<String>>();
		this.origins = new HashMap<String,String>();
	}

	private void mergeConnectionWithId(String connectionId, ConnectionList connectionList, boolean symmetric, boolean storeRecursive){
		ArrayList<String> extractedConnectionKey1 = new ArrayList<String>();
		ArrayList<String> extractedConnectionKey2 = new ArrayList<String>();
		ConnectionAttributes connectionAttributes = connectionList.getConnectionAttributesFor(connectionId);
		String source = connectionAttributes.getSourceAttribute();
		String target = connectionAttributes.getTargetAttribute();		
		
//		create both possible source/target combinations, because if treated as symmetric they are treated equivalently
		extractedConnectionKey1.add(source);
		extractedConnectionKey2.add(target);
		extractedConnectionKey1.add(target);
		extractedConnectionKey2.add(source);
		
		if (storeRecursive || (!source.equals(target))) {
//			source and target are different or we want to save recursive values because storeRecursive is true
			
			if ((extractedConnectionList.containsKey(extractedConnectionKey1))) {
//				key is present, add values to existing values
				ArrayList<String> extractedConnectionValue = extractedConnectionList.get(extractedConnectionKey1);
				if (!extractedConnectionValue.contains(connectionId)) {
					extractedConnectionValue.add(connectionId);
				}
				extractedConnectionList.put(extractedConnectionKey1, extractedConnectionValue);
			}
			
			else if (!extractedConnectionList.containsKey(extractedConnectionKey1) && !symmetric){
//				key is not present and we do not have to look for extractedConnectionKey2 because symmetric is false
//				therefore create new key/value pair
				ArrayList<String> extractedConnectionValue = new ArrayList<String>();
				extractedConnectionValue.add(connectionId);				
				extractedConnectionList.put(extractedConnectionKey1, extractedConnectionValue);
			}
			
			else if (!extractedConnectionList.containsKey(extractedConnectionKey1) && symmetric){
//				still have to look for extractedConnectionKey2 because symmetric is true
				if (extractedConnectionList.containsKey(extractedConnectionKey2)) {
//					key is present, add values to existing values
					ArrayList<String> extractedConnectionValue = extractedConnectionList.get(extractedConnectionKey2);
					if (!extractedConnectionValue.contains(connectionId)) {
						extractedConnectionValue.add(connectionId);
					}
					extractedConnectionList.put(extractedConnectionKey2, extractedConnectionValue);				
				}
				else {
//					key is not present, create new key/value pair
					ArrayList<String> extractedConnectionValue = new ArrayList<String>();
					extractedConnectionValue.add(connectionId);					
					extractedConnectionList.put(extractedConnectionKey1, extractedConnectionValue);
				}			
			}
		}
	}
	
	
	public void merge(ConnectionList connectionList, boolean symmetric, boolean storeRecursive){
//		will merge the data hold by connectionList to extractedConnectionList according to the values of symmetric and storeRecursive
//		symmetric (if true: treat A:B and B:A as same key) and storeRecursive (if true: store A:A)
		for (String connectionId: connectionList.connectionIds()) {
//			add new origins and merge every connection
			origins.put(connectionId, connectionList.getOrigin());
			mergeConnectionWithId(connectionId, connectionList, symmetric, storeRecursive);
		}	
	}
	
	public ArrayList<String> getOriginsForConnectionAttributePair(ArrayList<String> connectionAttributes) {
//		return all diagramPaths where this attributePair could be extracted
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
	
	private Set<ArrayList<String>> removeRedundantEdges(Set<ArrayList<String>> redundant) {
//		method removes double entries (edge because one attributePair represents/will be interpreted as an edge)
		Set<ArrayList<String>> no_redundant = redundant;
		ArrayList<ArrayList<String>> redundant_tmp = new ArrayList<ArrayList<String>>();
		
		for (ArrayList<String> attributePair: redundant) {
			redundant_tmp.add(attributePair);
		}
		
		for (int i=0; i<redundant_tmp.size(); i++) {
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
	
	public Set<ArrayList<String>> connectionAttributePairs() {
		return removeRedundantEdges(extractedConnectionList.keySet());
	}
	
	public boolean containsConnectionAttributePair(ArrayList<String> attributePair) {
		return extractedConnectionList.containsKey(attributePair);
	}	
}
