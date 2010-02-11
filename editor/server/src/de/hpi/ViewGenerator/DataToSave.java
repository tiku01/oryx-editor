package de.hpi.ViewGenerator;
import java.util.ArrayList;

public class DataToSave {

	private ArrayList<String> attrToSave;
	private ArrayList<String> lvlsToSave;
	private ArrayList<String> jsonToSave;
	
//	constructor for DataToSave objects where the initial attributes saveOrigin is an JSONObject
	public DataToSave(String attributeToSave, String jsonIdWithAttributeToSave, String stencilLevelToSave) {
		attrToSave = new ArrayList<String>();
		attrToSave.add(attributeToSave);
		lvlsToSave = new ArrayList<String>();
		lvlsToSave.add(stencilLevelToSave);
		jsonToSave = new ArrayList<String>();
		jsonToSave.add(jsonIdWithAttributeToSave);
	}
	
//	constructor for DataToSave objects where the initial attributes saveOrigin is a String
	public DataToSave(String attributeToSave, String stencilLevelToSave) {
		attrToSave = new ArrayList<String>();
		attrToSave.add(attributeToSave);
		lvlsToSave = new ArrayList<String>();
		lvlsToSave.add(stencilLevelToSave);
		jsonToSave = new ArrayList<String>();
		jsonToSave.add(null);
	}
	
//	add method for attributes objects where the saveOrigin is an JSONObject
	public void addDataToSave(String attributeToSave, String jsonIdWithAttributeToSave, String stencilLevelToSave) {
		attrToSave.add(attributeToSave);
		lvlsToSave.add(stencilLevelToSave);
		jsonToSave.add(jsonIdWithAttributeToSave);
	}
	
//	add Method for attributes objects where the saveOrigin is a String
	public void addDataToSave(String attributeToSave, String stencilLevelToSave) {
		attrToSave.add(attributeToSave);
		lvlsToSave.add(stencilLevelToSave);
		jsonToSave.add(null);
	}
	
	public ArrayList<String> attributesToSave() {
		return attrToSave;
	}
	
	public ArrayList<String> stencilLevelsToSave() {
		return lvlsToSave;
	}
	
	public ArrayList<String> jsonIdsToSave() {
		return jsonToSave;
	}
	
	public String getAttributeToSave(int index) {
		return attrToSave.get(index);
	}
	
	public String getStencilLevelToSave(int index) {
		return lvlsToSave.get(index);
	}
	
	public String getJSONIdWithAttributeToSave(int index) {
		return jsonToSave.get(index);
	}
		
	public int size() {
		return attrToSave.size();
	}
}
