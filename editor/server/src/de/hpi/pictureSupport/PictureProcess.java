package de.hpi.pictureSupport;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("process")
public class PictureProcess {

	@Attribute
	private int id;
	
	@Element
	private String name;
	
	@Element(targetType=PictureProcessModels.class)
	private PictureProcessModels processModels;

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

	public PictureProcessModels getProcessModels() {
		return processModels;
	}

	public void setProcessModels(PictureProcessModels processModels) {
		this.processModels = processModels;
	}
}
