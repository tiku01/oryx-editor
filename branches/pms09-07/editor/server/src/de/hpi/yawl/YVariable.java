package de.hpi.yawl;

public class YVariable {

	private String name = "";
	private String type = "";
	private String namespace = "";
	private String initialValue = "";
	private Boolean readOnly = false;
	
	public YVariable(String name, String type, String namespace, String initialValue, Boolean readOnly){
		setName(name);
		setType(type);
		setNamespace(namespace);
		setInitialValue(initialValue);
		setReadOnly(readOnly);
	}

	public YVariable() {
		
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
	
	public Boolean getReadOnly(){
		return readOnly;
	}
	
	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}
	
	public String writeToYAWL(Boolean param){
		String s = "";
		
		s += "\t\t\t\t\t\t<name>" + getName() + "</name>\n";
		s += "\t\t\t\t\t\t<type>" + getType() + "</type>\n";
		s += "\t\t\t\t\t\t<namespace>" + getNamespace() + "</namespace>\n";
		
		if(!param){
			if(getInitialValue().length() > 0){
				s += "\t\t\t\t\t\t<initialValue>" + getInitialValue() + "</initialValue>\n";
			}
		}
		
		return s;
	}
}
