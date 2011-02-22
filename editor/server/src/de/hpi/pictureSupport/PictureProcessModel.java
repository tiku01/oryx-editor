package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("processModel")
public class PictureProcessModel {

	@Attribute
	private int id;
	
	@Element
	private String name;
	
	@Element
	private String description;
	
	@Element
	private int organisationUnitID;
	
	@Element(targetType=PictureProduct.class)
	private PictureProduct resultingProduct;
	
	@Element
	private int creatorID;
	
	@Element
	private int lastEditorID;
	
	@Element
	private int numberOfCases;
	
	@Element(targetType=PictureProcessAttributes.class)
	private PictureProcessAttributes processAttributes;
	
	@Element(targetType=PictureProcessFlow.class)
	private PictureProcessFlow processFlow;
	
	@Element(targetType=PictureBuildingBlockRepository.class)
	private PictureBuildingBlockRepository buildingBlockRepository;

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

	public int getOrganisationUnitID() {
		return organisationUnitID;
	}

	public void setOrganisationUnitID(int organisationUnitID) {
		this.organisationUnitID = organisationUnitID;
	}

	public PictureProduct getResultingProduct() {
		return resultingProduct;
	}

	public void setResultingProduct(PictureProduct resultingProduct) {
		this.resultingProduct = resultingProduct;
	}

	public int getCreatorID() {
		return creatorID;
	}

	public void setCreatorID(int creatorID) {
		this.creatorID = creatorID;
	}

	public int getLastEditorID() {
		return lastEditorID;
	}

	public void setLastEditorID(int lastEditorID) {
		this.lastEditorID = lastEditorID;
	}

	public int getNumberOfCases() {
		return numberOfCases;
	}

	public void setNumberOfCases(int numberOfCases) {
		this.numberOfCases = numberOfCases;
	}

	public PictureProcessAttributes getProcessAttributes() {
		return processAttributes;
	}

	public void setProcessAttributes(PictureProcessAttributes processAttributes) {
		this.processAttributes = processAttributes;
	}

	public PictureProcessFlow getProcessFlow() {
		return processFlow;
	}

	public void setProcessFlow(PictureProcessFlow processFlow) {
		this.processFlow = processFlow;
	}

	public PictureBuildingBlockRepository getBuildingBlockRepository() {
		return buildingBlockRepository;
	}

	public void setBuildingBlockRepository(
			PictureBuildingBlockRepository buildingBlockRepository) {
		this.buildingBlockRepository = buildingBlockRepository;
	}
}
