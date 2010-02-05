package de.hpi.yawl.resourcing;

import java.util.ArrayList;

public class Participant extends ResourcingType {

	String userid;
	String password;
	String firstname;
	String lastname;
	Boolean isAdministrator = false;
	Boolean isAvailable = true;
	ArrayList<Role> roles = new ArrayList<Role>();
	ArrayList<Position> positions = new ArrayList<Position>();
	ArrayList<Capability> capabilities = new ArrayList<Capability>();
	String privileges = "11100000"; //has to be changed (perhaps)
	
	public Participant() {
		super();
		id = "PA-" + id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Boolean getIsAdministrator() {
		return isAdministrator;
	}

	public void setIsAdministrator(Boolean isAdministrator) {
		this.isAdministrator = isAdministrator;
	}

	public Boolean getIsAvailable() {
		return isAvailable;
	}

	public void setIsAvailable(Boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public ArrayList<Role> getRoles() {
		return roles;
	}

	public void setRoles(ArrayList<Role> roles) {
		this.roles = roles;
	}

	public ArrayList<Position> getPositions() {
		return positions;
	}

	public void setPositions(ArrayList<Position> positions) {
		this.positions = positions;
	}

	public ArrayList<Capability> getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(ArrayList<Capability> capabilities) {
		this.capabilities = capabilities;
	}

	public String getPrivileges() {
		return privileges;
	}

	public void setPrivileges(String privileges) {
		this.privileges = privileges;
	}
	
	public String writeAsMemberOfDistributionSetToYAWL(){
		String s = "";
		s += String.format("\t\t<participant>%s</participant>\n", id);
		return s;
	}
	
	public String writeToYAWL(){
		String s = "";
		
		s += String.format("\t\t<participant id=\"%s\">\n", id);
		s += String.format("\t\t\t<userid>%s</userid>\n", userid);
	    s += String.format("\t\t\t<password>%s</password>\n", password);
	    s += String.format("\t\t\t<firstname>%s</firstname>\n", firstname);
	    s += String.format("\t\t\t<lastname>%s</lastname>\n", lastname);
	    s = writeDescriptionToYAWL(s);
	    s = writeNotesToYAWL(s);
	    
	    s += String.format("\t\t\t<isAdministrator>%s</isAdministrator>\n", writeIsAdministratorToYAWL());
	    s += String.format("\t\t\t<isAvailable>%s</isAvailable>\n", writeIsAvailableToYAWL());
	    
	    s = writeRolesToYAWL(s);
	    s = writePositionsToYAWL(s);
	    s = writeCapabilitiesToYAWL(s);
	    
	    s += String.format("\t\t\t<privileges>%s</privileges>\n", privileges);
	    s += "\t\t</participant>\n";
		
		return s;
	}

	/**
	 * @return
	 */
	private String writeIsAvailableToYAWL() {
		if (isAvailable)
			return "true";
		
		return "false";
	}

	/**
	 * @return
	 */
	private String writeIsAdministratorToYAWL() {
		if (isAdministrator)
			return "true";
		
		return "false";
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeRolesToYAWL(String s) {
		if(roles.isEmpty())
	    	s += "\t\t\t<roles />\n";
	    else{
	    	s += "\t\t\t<roles>\n";
	    	for (Role role : roles){
	    		s += "\t\t\t\t<role>" + role.getId() + "</role>\n";
	    	}
	    	s += "\t\t\t</roles>\n";
	    }
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writePositionsToYAWL(String s) {
		if(positions.isEmpty())
	    	s += "\t\t\t<positions />\n";
	    else{
	    	s += "\t\t\t<positions>\n";
	    	for (Position position : positions){
	    		s += "\t\t\t\t<position>" + position.getId() + "</position>\n";
	    	}
	    	s += "\t\t\t</positions>\n";
	    }
		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	private String writeCapabilitiesToYAWL(String s) {
		if(capabilities.isEmpty())
	    	s += "\t\t\t<capabilities />\n";
	    else{
	    	s += "\t\t\t<capabilities>\n";
	    	for (ResourcingType capability : capabilities){
	    		s += "\t\t\t\t<capability>" + capability.getId() + "</capability>\n";
	    	}
	    	s += "\t\t\t</capabilities>\n";
	    }
		return s;
	}
	
}
