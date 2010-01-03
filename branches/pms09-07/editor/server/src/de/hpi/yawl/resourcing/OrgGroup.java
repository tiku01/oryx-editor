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

	/**
	 * @param s
	 * @return
	 */
	private String writeBelongsToIdToYAWL(String s) {
		if(belongsToID != null)
			s += String.format("\t\t\t<belongsToID>%s</belongsToID>\n", belongsToID.getId());
		return s;
	}
	
	public String writeToYAWL(){
		String s = "";
		s += "\t\t<orgGroup id=\"" + id + "\">\n";
		s += "\t\t\t<groupName>" + name + "</groupName>\n";
		s += "\t\t\t<groupType>" + groupType + "</groupType>\n";
		s = writeDescriptionToYAWL(s);
		s = writeNotesToYAWL(s);
		s = writeBelongsToIdToYAWL(s);
		s += "\t\t</orgGroup>\n";
		
		return s;
	}


	
	
}
