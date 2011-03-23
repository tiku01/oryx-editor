package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureProcessModel.
 */
@RootElement("processModel")
public class PictureProcessModel {

	/** The id. */
	@Attribute
	private int id;
	
	/** The name. */
	@Element
	private String name;
	
	/** The description. */
	@Element
	private String description;
	
	/** The organization unit id. */
	@Element
	private int organisationUnitID;
	
	/** The resulting product. */
	@Element(targetType=PictureResultingProduct.class)
	private PictureResultingProduct resultingProduct;
	
	/** The creator id. */
	@Element
	private int creatorID;
	
	/** The last editor id. */
	@Element
	private int lastEditorID;
	
	/** The number of cases. */
	@Element
	private int numberOfCases;
	
	/** The process attributes. */
	@Element(targetType=PictureProcessAttributes.class)
	private PictureProcessAttributes processAttributes;
	
	/** The process flow. */
	@Element(targetType=PictureProcessFlow.class)
	private PictureProcessFlow processFlow;
	
	/** The building block repository. */
	@Element(targetType=PictureBuildingBlockRepository.class)
	private PictureBuildingBlockRepository buildingBlockRepository;

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
	 * Gets the organisation unit id.
	 *
	 * @return the organisation unit id
	 */
	public int getOrganisationUnitID() {
		return organisationUnitID;
	}

	/**
	 * Sets the organisation unit id.
	 *
	 * @param organisationUnitID the new organisation unit id
	 */
	public void setOrganisationUnitID(int organisationUnitID) {
		this.organisationUnitID = organisationUnitID;
	}

	/**
	 * Gets the resulting product.
	 *
	 * @return the resulting product
	 */
	public PictureResultingProduct getResultingProduct() {
		return resultingProduct;
	}

	/**
	 * Sets the resulting product.
	 *
	 * @param resultingProduct the new resulting product
	 */
	public void setResultingProduct(PictureResultingProduct resultingProduct) {
		this.resultingProduct = resultingProduct;
	}

	/**
	 * Gets the creator id.
	 *
	 * @return the creator id
	 */
	public int getCreatorID() {
		return creatorID;
	}

	/**
	 * Sets the creator id.
	 *
	 * @param creatorID the new creator id
	 */
	public void setCreatorID(int creatorID) {
		this.creatorID = creatorID;
	}

	/**
	 * Gets the last editor id.
	 *
	 * @return the last editor id
	 */
	public int getLastEditorID() {
		return lastEditorID;
	}

	/**
	 * Sets the last editor id.
	 *
	 * @param lastEditorID the new last editor id
	 */
	public void setLastEditorID(int lastEditorID) {
		this.lastEditorID = lastEditorID;
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
	 * Gets the process attributes.
	 *
	 * @return the process attributes
	 */
	public PictureProcessAttributes getProcessAttributes() {
		return processAttributes;
	}

	/**
	 * Sets the process attributes.
	 *
	 * @param processAttributes the new process attributes
	 */
	public void setProcessAttributes(PictureProcessAttributes processAttributes) {
		this.processAttributes = processAttributes;
	}

	/**
	 * Gets the process flow.
	 *
	 * @return the process flow
	 */
	public PictureProcessFlow getProcessFlow() {
		return processFlow;
	}

	/**
	 * Sets the process flow.
	 *
	 * @param processFlow the new process flow
	 */
	public void setProcessFlow(PictureProcessFlow processFlow) {
		this.processFlow = processFlow;
	}

	/**
	 * Gets the building block repository.
	 *
	 * @return the building block repository
	 */
	public PictureBuildingBlockRepository getBuildingBlockRepository() {
		return buildingBlockRepository;
	}

	/**
	 * Sets the building block repository.
	 *
	 * @param buildingBlockRepository the new building block repository
	 */
	public void setBuildingBlockRepository(
			PictureBuildingBlockRepository buildingBlockRepository) {
		this.buildingBlockRepository = buildingBlockRepository;
	}
}
