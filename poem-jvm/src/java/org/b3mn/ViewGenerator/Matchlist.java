package org.b3mn.ViewGenerator;
import java.util.ArrayList;

class Matchlist {
		private ArrayList<String> connectionIds; 
		private ArrayList<String> connectorIds;
		
		public Matchlist() {
			connectionIds = new ArrayList<String>();
			connectorIds = new ArrayList<String>();
		}
		
		public void add(String connectionId, String connectorId) {
			connectionIds.add(connectionId);
			connectorIds.add(connectorId);
		}
		
		private ArrayList<Integer> find(String connectorId){
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
			ArrayList<Integer> indexList = find(connectorId);

			ArrayList<String> resultList = new ArrayList<String>();
					
			for(int i=0; i<(indexList.size());i++) {
				resultList.add(connectionIds.get(indexList.get(i)-i));
				connectionIds.remove(indexList.get(i)-i);
				connectorIds.remove(indexList.get(i)-i);
			}
			return resultList;
		}
		
	}

