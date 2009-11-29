package de.hpi.bpmn;

public class Property {

	private String name;
	private String type;
	private String value = "";
	private Boolean correlation = false;
	
	public Property(String newName, String newType, String newValue, Boolean hasCorrelation){
		setName(newName);
		setType(newType);
		setValue(newValue);
		setCorrelation(hasCorrelation);
	}
	
	public void setName(String newName){
		name = newName;
	}
	
	public String getName(){
		return name;
	}
	
	public void setType(String newType){
		type = newType;
	}
	
	public String getType(){
		return type;
	}
	
	public void setValue(String newValue){
		value = newValue;
	}
	
	public String getValue(){
		return value;
	}
	
	public void setCorrelation(Boolean isCorrelation){
		correlation = isCorrelation;
	}
	
	public Boolean isCorrelation(){
		return correlation;
	}
	
	
}
