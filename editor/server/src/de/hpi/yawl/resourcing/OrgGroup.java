package de.hpi.yawl.resourcing;

public class OrgGroup extends ResourcingType {

	String groupType;
	OrgGroup belongsToID;
	
	public OrgGroup() {
		super();
		id = "OG-" + id;
	}

	public String getGroupType() {
		return groupType;
	}

	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}

	public OrgGroup getBelongsToID() {
		return belongsToID;
	}

	public void setBelongsToID(OrgGroup belongsToID) {
		this.belongsToID = belongsToID;
	}

	public String writeToYAWL(){
		String s = "";
		s += "\t\t<orgGroup id=\"" + id + "\">\n";
		s += "\t\t\t<groupName>" + name + "</groupName>\n";
		s += "\t\t\t<groupType>" + groupType + "</groupType>\n";
		s += description.isEmpty() ? "\t\t\t<description />\n" : 
			"\t\t\t<description>" + description + "</description>\n";
		s += notes.isEmpty() ? "\t\t\t<notes />\n" : 
			"\t\t\t<notes>" + notes + "</notes>\n";
		
		if(belongsToID != null)
			s += "\t\t\t<belongsToID>" + belongsToID.getId() + "</belongsToID>\n";
		
		s += "\t\t</orgGroup>\n";
		
		return s;
	}
	
	
}
