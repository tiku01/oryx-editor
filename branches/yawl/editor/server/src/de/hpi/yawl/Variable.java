package de.hpi.yawl;

public class Variable {

	private String name = "";
	private String type = "";
	private String namespace = "";
	private String initialValue = "";
	
	public Variable(String name, String type, String namespace, String initialValue){
		setName(name);
		setType(type);
		setNamespace(namespace);
		setInitialValue(initialValue);
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String variableName){
		name = variableName;
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String variableType){
		type = variableType;
	}
	
	public String getNamespace(){
		return namespace;
	}
	
	public void setNamespace(String variableNamespace){
		namespace = variableNamespace;
	}
	
	public String getInitialValue(){
		return initialValue;
	}
	
	public void setInitialValue(String variableValue){
		initialValue = variableValue;
	}
	
	public String writeToYAWL(){
		String s = "";
		
		s += "\t\t\t\t\t\t<name>" + getName() + "</name>\n";
		s += "\t\t\t\t\t\t<type>" + getType() + "</type>\n";
		s += "\t\t\t\t\t\t<namespace>" + getNamespace() + "</namespace>\n";
		
		if(getInitialValue().length() > 0){
			s += "\t\t\t\t\t\t<initialValue>" + getInitialValue() + "</initialValue>\n";
		}
		
		return s;
	}
}
