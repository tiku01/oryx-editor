package de.hpi.yawl;

public class YInputCondition extends YCondition {
	
	public YInputCondition(String id, String name) {
		super(id, name);
	}
	
	@Override
	public String writeToYAWL()
	{
		String s = "";
		s += String.format("\t\t\t\t<inputCondition id=\"%s\">\n", getID());
		s = writeOutgoingEdgesToYAWL(s);
		s += "\t\t\t\t</inputCondition>\n";
		
		return s;
	}
}
