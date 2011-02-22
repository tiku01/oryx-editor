package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("subprocess")
public class PictureSubprocess {

	@Attribute
	private int id;
	
	@Element
	private String name;
	
	@Element
	private String description;
	
	@Element
	private int executingOrganisationUnit;
	
	@Element
	private int numberOfCases;
	
	@Element(targetType=PictureSubprocessAttributes.class)
	private PictureSubprocessAttributes subProcessAttributes;
	
	@Element(targetType=PictureVariants.class)
	private PictureVariants variants;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public int getExecutingOrganisationUnit() {
		return executingOrganisationUnit;
	}

	public void setExecutingOrganisationUnit(int executingOrganisationUnit) {
		this.executingOrganisationUnit = executingOrganisationUnit;
	}

	public int getNumberOfCases() {
		return numberOfCases;
	}

	public void setNumberOfCases(int numberOfCases) {
		this.numberOfCases = numberOfCases;
	}

	public PictureSubprocessAttributes getSubProcessAttributes() {
		return subProcessAttributes;
	}

	public void setSubProcessAttributes(
			PictureSubprocessAttributes subProcessAttributes) {
		this.subProcessAttributes = subProcessAttributes;
	}

	public PictureVariants getVariants() {
		return variants;
	}

	public void setVariants(PictureVariants variants) {
		this.variants = variants;
	}
}
