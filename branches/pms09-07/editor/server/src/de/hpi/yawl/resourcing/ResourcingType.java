package de.hpi.yawl.resourcing;

import java.util.UUID;

import de.hpi.yawl.FileWritingForYAWL;

public abstract class ResourcingType implements FileWritingForYAWL {

	String id = "";
	String name = "";
	String description = "";
	String notes = "";

	public ResourcingType() {
		super();
		UUID generatedUuid = UUID.randomUUID();
		this.id = generatedUuid.toString();
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String writeToYAWL(){		
		return "";
	}

}