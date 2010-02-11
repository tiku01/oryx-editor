package de.hpi.ViewGenerator;
import java.util.ArrayList;

class Connector {
	private String stencil;
	private DataToSave datToSave;
	private ArrayList<String> parentStencils;
	
	public Connector(String stencilId, DataToSave dataToSave, ArrayList<String> possibleParentStencils) {
		stencil = stencilId;
		datToSave = dataToSave;
		parentStencils = possibleParentStencils;
	}
	
	public DataToSave getDataToSave() {
		return datToSave;
	}
	
	public String getStencil() {
		return stencil;
	}
			
	public ArrayList<String> getParentStencils() {
		return parentStencils;
	}

}
