package de.hpi.yawl.resourcing;

public class Capability  extends ResourcingType {

	public Capability() {
		super();
		id = "CA-" + id;
	}
	
	public String writeAsMemberOfDistributionSetToYAWL(){
		String s = "";
		s += String.format("\t\t<capability>%s</capability>\n", id);
		return s;
	}
	public String writeToYAWL(){
		String s = "";
		s += String.format("\t\t<capability id=\"%s\">\n", id);
		s = writeNameToYAWL(s);
		s = writeDescriptionToYAWL(s);
		s = writeNotesToYAWL(s);
		s += "\t\t</capability>\n";
		
		return s;
	}
	
}
