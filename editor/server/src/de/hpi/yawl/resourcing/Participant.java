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
	
	public String writeToYAWL(){
		String s = "";
		String isAdministratorValue = "";
		if (isAdministrator)
			isAdministratorValue = "true";
		else
			isAdministratorValue = "false";
				
		String isAvailableValue = "";
		if (isAvailable)
			isAvailableValue = "true";
		else
			isAvailableValue = "false";
		
		s += "\t\t<participant id=\"" + id + "\">\n";
		s += "\t\t\t<userid>" + userid + "</userid>\n";
	    s += "\t\t\t<password>" + password + "</password>\n";
	    s += "\t\t\t<firstname>" + firstname + "</firstname>\n";
	    s += "\t\t\t<lastname>" + lastname + "</lastname>\n";
	    s += description.isEmpty() ? "\t\t\t<description />\n" : 
			"\t\t\t<description>" + description + "</description>\n";
	    s += notes.isEmpty() ? "\t\t\t<notes />\n" : 
			"\t\t\t<notes>" + notes + "</notes>\n";
	    
	    s += "\t\t\t<isAdministrator>" + isAdministratorValue + "</isAdministrator>\n";
	    s += "\t\t\t<isAvailable>" + isAvailableValue + "</isAvailable>\n";
	    
	    if(roles.isEmpty())
	    	s += "\t\t\t<roles />\n";
	    else{
	    	s += "\t\t\t<roles>\n";
	    	for (Role role : roles){
	    		s += "\t\t\t\t<role>" + role.getId() + "</role>\n";
	    	}
	    	s += "\t\t\t</roles>\n";
	    }
	    
	    if(positions.isEmpty())
	    	s += "\t\t\t<positions />\n";
	    else{
	    	s += "\t\t\t<positions>\n";
	    	for (Position position : positions){
	    		s += "\t\t\t\t<position>" + position.getId() + "</position>\n";
	    	}
	    	s += "\t\t\t</positions>\n";
	    }
	    
	    if(capabilities.isEmpty())
	    	s += "\t\t\t<capabilities />\n";
	    else{
	    	s += "\t\t\t<capabilities>\n";
	    	for (Capability capability : capabilities){
	    		s += "\t\t\t\t<capability>" + capability.getId() + "</capability>\n";
	    	}
	    	s += "\t\t\t</capabilities>\n";
	    }
	    
	    s += "\t\t\t<privileges>" + privileges + "</privileges>\n";
	    s += "\t\t</participant>\n";
		
		return s;
	}
	
}
