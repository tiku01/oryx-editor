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
		s += positionId.isEmpty() ? "\t\t\t<positionid />\n" : 
			"\t\t\t<positionid>" + positionId + "</positionid>\n";
		s += description.isEmpty() ? "\t\t\t<description />\n" : 
			"\t\t\t<description>" + description + "</description>\n";
		s += notes.isEmpty() ? "\t\t\t<notes />\n" : 
			"\t\t\t<notes>" + notes + "</notes>\n";
		if(orgGroupBelongingTo != null){
			s += "\t\t\t<orggroupid>" + orgGroupBelongingTo.getId() + "</orggroupid>\n";
		}
		if(reportsTo != null){
			s += "\t\t\t<reportstoid>" + reportsTo.getId() + "</reportstoid>\n";
		}
		s += "\t\t</position>\n";
		
		return s;
	}
	
}
