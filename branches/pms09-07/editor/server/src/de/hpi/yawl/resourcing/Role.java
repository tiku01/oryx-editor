package de.hpi.yawl.resourcing;

public class Role extends ResourcingType {

	public Role() {
		super();
		id = "RO-" + id;
	}
	
	public String writeAsMemberOfDistributionSetToYAWL(){
		String s = "";
		s += String.format("\t\t<role>%s</role>\n", id);
		return s;
	}
	
	public String writeToYAWL(){
		String s = "";
		s += String.format("\t\t<role id=\"%s\">\n", id);
		s = writeNameToYAWL(s);
		s = writeDescriptionToYAWL(s);
		s = writeNotesToYAWL(s);
		s += "\t\t</role>\n";
		
		return s;
	}
	
}
