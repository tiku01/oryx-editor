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
	
	/**
	 * @param s
	 * @return
	 */
	private String writeVariableSettingsToYAWL(String s) {
		s += String.format("\t\t\t\t\t\t<name>%s</name>\n", getName());
		s += String.format("\t\t\t\t\t\t<type>%s</type>\n", getType());
		s += String.format("\t\t\t\t\t\t<namespace>%s</namespace>\n", getNamespace());
		return s;
	}
	
	public String writeAsParameterToYAWL(){
		String s = "";
		s = writeVariableSettingsToYAWL(s);
		return s;
	}
	
	//TODO: may be removable
	public String writeToYAWL(){
		String s = "";
		s = writeVariableSettingsToYAWL(s);
		
		if((getInitialValue() != null) && (getInitialValue().length() > 0)){
			s += String.format("\t\t\t\t\t\t<initialValue>%s</initialValue>\n", getInitialValue());
		}
		return s;
	}
}
