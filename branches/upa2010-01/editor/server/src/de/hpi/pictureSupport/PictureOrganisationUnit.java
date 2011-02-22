package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * The Class PictureOrganisationUnit.
 */
@RootElement("organisationUnit")
public class PictureOrganisationUnit {

	/** The id. */
	@Attribute
	private int id;
	
	/** The name. */
	@Element
	private String name;
	
	/** The positions. */
	@Element(targetType=PicturePositions.class)
	private PicturePositions positions;

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
	 * Gets the positions.
	 *
	 * @return the positions
	 */
	public PicturePositions getPositions() {
		return positions;
	}

	/**
	 * Sets the positions.
	 *
	 * @param positions the new positions
	 */
	public void setPositions(PicturePositions positions) {
		this.positions = positions;
	}
}
