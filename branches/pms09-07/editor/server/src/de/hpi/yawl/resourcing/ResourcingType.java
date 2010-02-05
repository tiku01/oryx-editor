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
	
	public String writeAsMemberOfDistributionSetToYAWL(){
		return "";
	}
	
	public String writeToYAWL(){		
		return "";
	}

	/**
	 * @param s
	 * @return
	 */
	protected String writeNotesToYAWL(String s) {
		if (notes.isEmpty())
			s+= "\t<notes />\n";
		else
			s += String.format("\t\t\t<notes>%s</notes>\n", notes);

		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	protected String writeDescriptionToYAWL(String s) {
		if (description.isEmpty())
			s+= "\t<description />\n";
		else
			s += String.format("\t\t\t<description>%s</description>\n", description);

		return s;
	}

	/**
	 * @param s
	 * @return
	 */
	protected String writeNameToYAWL(String s) {
		s += String.format("\t\t\t<name>%s</name>\n", name);
		return s;
	}

}