package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureSubprocess.
 */
@RootElement("subprocess")
public class PictureSubprocess {

	/** The id. */
	@Attribute
	private int id;
	
	/** The name. */
	@Element
	private String name;
	
	/** The description. */
	@Element
	private String description;
	
	/** The executing organisation unit. */
	@Element
	private int executingOrganisationUnit;
	
	/** The number of cases. */
	@Element
	private int numberOfCases;
	
	/** The sub process attributes. */
	@Element(targetType=PictureSubprocessAttributes.class)
	private PictureSubprocessAttributes subProcessAttributes;
	
	/** The variants. */
	@Element(targetType=PictureVariants.class)
	private PictureVariants variants;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the executing organisation unit.
	 *
	 * @return the executing organisation unit
	 */
	public int getExecutingOrganisationUnit() {
		return executingOrganisationUnit;
	}

	/**
	 * Sets the executing organisation unit.
	 *
	 * @param executingOrganisationUnit the new executing organisation unit
	 */
	public void setExecutingOrganisationUnit(int executingOrganisationUnit) {
		this.executingOrganisationUnit = executingOrganisationUnit;
	}

	/**
	 * Gets the number of cases.
	 *
	 * @return the number of cases
	 */
	public int getNumberOfCases() {
		return numberOfCases;
	}

	/**
	 * Sets the number of cases.
	 *
	 * @param numberOfCases the new number of cases
	 */
	public void setNumberOfCases(int numberOfCases) {
		this.numberOfCases = numberOfCases;
	}

	/**
	 * Gets the sub process attributes.
	 *
	 * @return the sub process attributes
	 */
	public PictureSubprocessAttributes getSubProcessAttributes() {
		return subProcessAttributes;
	}

	/**
	 * Sets the sub process attributes.
	 *
	 * @param subProcessAttributes the new sub process attributes
	 */
	public void setSubProcessAttributes(
			PictureSubprocessAttributes subProcessAttributes) {
		this.subProcessAttributes = subProcessAttributes;
	}

	/**
	 * Gets the variants.
	 *
	 * @return the variants
	 */
	public PictureVariants getVariants() {
		return variants;
	}

	/**
	 * Sets the variants.
	 *
	 * @param variants the new variants
	 */
	public void setVariants(PictureVariants variants) {
		this.variants = variants;
	}
}
