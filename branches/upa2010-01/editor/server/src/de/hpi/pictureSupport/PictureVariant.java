package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureVariants.
 */
@RootElement("variant")
public class PictureVariant extends PictureStencil {

	/** The probability. */
	@Attribute
	private int probability;
	
	/** The id. */
	@Attribute
	private String id;
	
	/** The name. */
	@Element
	private String name;
	
	/** The variant attributes. */
	@Element(targetType=PictureVariantAttributes.class)
	private PictureVariantAttributes variantAttributes;
	
	/** The building block sequence. */
	@Element(targetType=PictureBuildingBlockSequence.class)
	private PictureBuildingBlockSequence buildingBlockSequence;
	
	/** The predecessor. */
	@Element
	private String predecessor;
	
	/** The successor. */
	@Element
	private String successor;

	/**
	 * Gets the probability.
	 *
	 * @return the probability
	 */
	public int getProbability() {
		return probability;
	}

	/**
	 * Sets the probability.
	 *
	 * @param probability the new probability
	 */
	public void setProbability(int probability) {
		this.probability = probability;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
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
	 * Gets the variant attributes.
	 *
	 * @return the variant attributes
	 */
	public PictureVariantAttributes getVariantAttributes() {
		return variantAttributes;
	}

	/**
	 * Sets the variant attributes.
	 *
	 * @param variantAttributes the new variant attributes
	 */
	public void setVariantAttributes(PictureVariantAttributes variantAttributes) {
		this.variantAttributes = variantAttributes;
	}

	/**
	 * Gets the building block sequence.
	 *
	 * @return the building block sequence
	 */
	public PictureBuildingBlockSequence getBuildingBlockSequence() {
		return buildingBlockSequence;
	}

	/**
	 * Sets the building block sequence.
	 *
	 * @param buildingBlockSequence the new building block sequence
	 */
	public void setBuildingBlockSequence(
			PictureBuildingBlockSequence buildingBlockSequence) {
		this.buildingBlockSequence = buildingBlockSequence;
	}

	/**
	 * Gets the predecessor.
	 *
	 * @return the predecessor
	 */
	public String getPredecessor() {
		return predecessor;
	}

	/**
	 * Sets the predecessor.
	 *
	 * @param predecessor the new predecessor
	 */
	public void setPredecessor(String predecessor) {
		this.predecessor = predecessor;
	}

	/**
	 * Gets the successor.
	 *
	 * @return the successor
	 */
	public String getSuccessor() {
		return successor;
	}

	/**
	 * Sets the successor.
	 *
	 * @param successor the new successor
	 */
	public void setSuccessor(String successor) {
		this.successor = successor;
	}
}
