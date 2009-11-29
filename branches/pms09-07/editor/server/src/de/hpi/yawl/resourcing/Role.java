package de.hpi.yawl.resourcing;

public class Role extends ResourcingType {

	public Role() {
		super();
		id = "RO-" + id;
	}
	
	public String writeToYAWL(){
		String s = "";
		s += "\t\t<role id=\"" + id + "\">\n";
		s += "\t\t\t<name>" + name + "</name>\n";
		s += description.isEmpty() ? "\t\t\t<description />\n" : 
			"\t\t\t<description>" + description + "</description>\n";
		s += notes.isEmpty() ? "\t\t\t<notes />\n" : 
			"\t\t\t<notes>" + notes + "</notes>\n";
		s += "\t\t</role>\n";
		
		return s;
	}
	
}
