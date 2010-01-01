package de.hpi.yawl;

public class YOutputCondition extends YCondition {
	
	public YOutputCondition(String id, String name) {
		super(id, name);
	}
	
	@Override
	public String writeToYAWL()
	{
		return String.format("\t\t\t\t<outputCondition id=\"%s\"/>\n", getID());
	}
}