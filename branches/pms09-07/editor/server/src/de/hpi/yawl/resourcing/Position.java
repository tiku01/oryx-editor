package de.hpi.yawl.resourcing;

public class Position extends ResourcingType {

	String positionId = "";
	OrgGroup orgGroupBelongingTo;
	Position reportsTo;
	
	public Position() {
		super();
		id = "PO-" + id;
	}

	public String getPositionId() {
		return positionId;
	}

	public void setPositionId(String positionId) {
		this.positionId = positionId;
	}

	public OrgGroup getOrgGroupBelongingTo() {
		return orgGroupBelongingTo;
	}

	public void setOrgGroupBelongingTo(OrgGroup orgGroupBelongingTo) {
		this.orgGroupBelongingTo = orgGroupBelongingTo;
	}

	public Position getReportsTo() {
		return reportsTo;
	}

	public void setReportsTo(Position reportsTo) {
		this.reportsTo = reportsTo;
	}
	
	public String writeToYAWL(){
		String s = "";
		s += "\t\t<position id=\"" + id + "\">\n";
		s += "\t\t\t<title>" + name + "</title>\n";
		s = writePositionIdToYAWL(s);
		s = writeDescriptionToYAWL(s);
		s = writeNotesToYAWL(s);
		s = writeOrgGroupBelongingToToYAWL(s);
		s = writeReportsToToYAWL(s);
		s += "\t\t</position>\n";
		
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writePositionIdToYAWL(String s) {
		if (positionId.isEmpty())
			s += "\t\t\t<positionid />\n";
		else
			s += String.format("\t\t\t<positionid>%s</positionid>\n", positionId);

		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeReportsToToYAWL(String s) {
		if(reportsTo != null)
			s += "\t\t\t<reportstoid>" + reportsTo.getId() + "</reportstoid>\n";
		
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeOrgGroupBelongingToToYAWL(String s) {
		if(orgGroupBelongingTo != null)
			s += "\t\t\t<orggroupid>" + orgGroupBelongingTo.getId() + "</orggroupid>\n";
		
		return s;
	}
	
}
