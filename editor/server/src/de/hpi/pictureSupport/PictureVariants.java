package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("variants")
public class PictureVariants {

	@Attribute
	private int probability;
	
	@Attribute
	private int id;
	
	@Element
	private String name;
	
	@Element(targetType=PictureVariantAttributes.class)
	private PictureVariantAttributes variantAttributes;
	
	@Element(targetType=PictureBuildingBlockSequence.class)
	private PictureBuildingBlockSequence buildingBlockSequence;
	
	@Element
	private String predecessor;
	
	@Element
	private String successor;

	public int getProbability() {
		return probability;
	}

	public void setProbability(int probability) {
		this.probability = probability;
	}

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

	public PictureVariantAttributes getVariantAttributes() {
		return variantAttributes;
	}

	public void setVariantAttributes(PictureVariantAttributes variantAttributes) {
		this.variantAttributes = variantAttributes;
	}

	public PictureBuildingBlockSequence getBuildingBlockSequence() {
		return buildingBlockSequence;
	}

	public void setBuildingBlockSequence(
			PictureBuildingBlockSequence buildingBlockSequence) {
		this.buildingBlockSequence = buildingBlockSequence;
	}

	public String getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(String predecessor) {
		this.predecessor = predecessor;
	}

	public String getSuccessor() {
		return successor;
	}

	public void setSuccessor(String successor) {
		this.successor = successor;
	}
}
