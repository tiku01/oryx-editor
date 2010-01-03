package de.hpi.yawl.resourcing;

import java.util.ArrayList;

import de.hpi.yawl.FileWritingForYAWL;

public class OrgData implements FileWritingForYAWL {

	ArrayList<Participant> participants = new ArrayList<Participant>();
	ArrayList<Role> roles = new ArrayList<Role>();
	ArrayList<Position> positions = new ArrayList<Position>();
	ArrayList<Capability> capabilities = new ArrayList<Capability>();
	ArrayList<OrgGroup> orgGroups = new ArrayList<OrgGroup>();
	
	public OrgData() {
		super();
	}

	public ArrayList<Participant> getParticipants() {
		return participants;
	}

	public ArrayList<Role> getRoles() {
		return roles;
	}

	public ArrayList<Position> getPositions() {
		return positions;
	}

	public ArrayList<Capability> getCapabilities() {
		return capabilities;
	}

	public ArrayList<OrgGroup> getOrgGroups() {
		return orgGroups;
	}

	public String writeToYAWL(){
		String s = "";
		s += "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
		s += "<orgdata>\n";
		
		s = writeParticipantsToYAWL(s);
		
		s = writeRolesToYAWL(s);
		
		s = writePositionsToYAWL(s);
		
		s = writeOrgGroupsToYAWL(s);
		
		s += "</orgdata>";
		
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeOrgGroupsToYAWL(String s) {
		if(orgGroups.isEmpty())
	    	s += "\t<orggroups />\n";
	    else{
	    	s += "\t<orggroups>\n";
	    	for (OrgGroup orgGroup : orgGroups)
	    		s += orgGroup.writeToYAWL();
	    	
	    	s += "\t</orggroups>\n";
	    }
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writePositionsToYAWL(String s) {
		if(positions.isEmpty())
	    	s += "\t<positions />\n";
	    else{
	    	s += "\t<positions>\n";
	    	for (Position position : positions)
	    		s += position.writeToYAWL();
	    	
	    	s += "\t</positions>\n";
	    }
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeRolesToYAWL(String s) {
		if(roles.isEmpty())
	    	s += "\t<roles />\n";
	    else{
	    	s += "\t<roles>\n";
	    	for (Role role : roles)
	    		s += role.writeToYAWL();
	    	
	    	s += "\t</roles>\n";
	    }
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeParticipantsToYAWL(String s) {
		if(participants.isEmpty())
	    	s += "\t<participants />\n";
	    else{
	    	s += "\t<participants>\n";
	    	for (Participant participant : participants)
	    		s += participant.writeToYAWL();
	    	
	    	s += "\t</participants>\n";
	    }
		return s;
	}
}