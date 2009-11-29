package de.hpi.yawl.resourcing;

public class Capability  extends ResourcingType {

	public Capability() {
		super();
		id = "CA-" + id;
	}
	
	public String writeToYAWL(){
		String s = "";
		s += "\t\t<capability id=\"" + id + "\">\n";
		s += "\t\t\t<name>" + name + "</name>\n";
		s += description.isEmpty() ? "\t<description />\n" : 
			"\t\t\t<description>" + description + "</description>\n";
		s += notes.isEmpty() ? "\t<notes />\n" : 
			"\t\t\t<notes>" + notes + "</notes>\n";
		s += "\t\t</capability>\n";
		
		return s;
	}
	
}
