package de.hpi.pictureSupport;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.pictureSupport.container.PictureProcessModels;

/**
 * The Class PictureProcess.
 */
@RootElement("process")
public class PictureProcess {
	
	/** The name. */
	@Element
	private String name;
	
	/** The process models. */
	@Element(targetType=PictureProcessModels.class)
	private PictureProcessModels processModels;

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
	 * Gets the process models.
	 *
	 * @return the process models
	 */
	public PictureProcessModels getProcessModels() {
		return processModels;
	}

	/**
	 * Sets the process models.
	 *
	 * @param processModels the new process models
	 */
	public void setProcessModels(PictureProcessModels processModels) {
		this.processModels = processModels;
	}
}
