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

class Matchlist {
//	class which holds a number of connectionIds and its belonging targetIds
	
	private ArrayList<String> connectionIds; 
	private ArrayList<String> connectorIds;
		
	public Matchlist() {
		this.connectionIds = new ArrayList<String>();
		this.connectorIds = new ArrayList<String>();
	}
		
	public void add(String connectionId, String connectorId) {
		connectionIds.add(connectionId);
		connectorIds.add(connectorId);
	}
		
	private ArrayList<Integer> find(String connectorId){
//		find all indexes of connectionId where the target equals the connectorId
		int index = 0;
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		
		while(index < (connectorIds.size())){
			if (connectorIds.get(index).equals(connectorId)) {
				indexList.add(index);								
			}
			index += 1;
		}
		return indexList;
	}
		
	public ArrayList<String> match(String connectorId) {
//		find all connectionIds where the target equals the connectorId and remove the connectionIds 
//		and connectorIds from the matchlist
		ArrayList<Integer> indexList = find(connectorId);
		ArrayList<String> resultList = new ArrayList<String>();
					
		for(int i=0; i<(indexList.size()); i++) {
			resultList.add(connectionIds.get(indexList.get(i)-i));
			connectionIds.remove(indexList.get(i)-i);
			connectorIds.remove(indexList.get(i)-i);
		}
		return resultList;
	}		
}