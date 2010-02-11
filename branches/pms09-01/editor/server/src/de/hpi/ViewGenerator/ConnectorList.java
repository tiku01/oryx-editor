package de.hpi.ViewGenerator;
import java.util.ArrayList;
import java.util.HashSet;

import de.hpi.ViewGenerator.Connector;

class ConnectorList {
	private ArrayList<String> stencilList;
	private ArrayList<DataToSave> dataToSaveList;
	private HashSet<String> parentStencils;

	public ConnectorList() {
		stencilList = new ArrayList<String>();
		dataToSaveList = new ArrayList<DataToSave>();
		parentStencils = new HashSet<String>();
	}
	
	public void addConnector(Connector connector) {
		stencilList.add(connector.getStencil());
		dataToSaveList.add(connector.getDataToSave());
		parentStencils.addAll(connector.getParentStencils());
	}

	private int findIndexOfConnectorWithStencil(String stencil) {
		return stencilList.indexOf(stencil);
	}
	
	public int size() {
		return stencilList.size();
	}
	
	public boolean containsConnectorWithStencil(String stencil) {
		return stencilList.contains(stencil);
	}
	
	public DataToSave dataToSaveForConnectorWithStencil(String stencil) {
		int index = findIndexOfConnectorWithStencil(stencil);
		try {
			return dataToSaveList.get(index);
		}
		catch (ArrayIndexOutOfBoundsException e){
			return null;
		}
	}
	
	public ArrayList<String> stencilLevelsToSaveForConnectorWithStencil(String stencil) {
		int index = findIndexOfConnectorWithStencil(stencil);
		return dataToSaveList.get(index).stencilLevelsToSave();
	}
	
	public ArrayList<String> attributesToSaveForConnectorWithStencil(String stencil) {
		int index = findIndexOfConnectorWithStencil(stencil);
		return dataToSaveList.get(index).attributesToSave();
	}
		
	public boolean isPossibleParentStencil(String stencilId) {
		return parentStencils.contains(stencilId);
	}
	
	
	public boolean shouldBeSaved(String stencil, String stencilToSave) {
		return stencilLevelsToSaveForConnectorWithStencil(stencil).contains(stencilToSave);
	}

}
