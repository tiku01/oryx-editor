package de.hpi.visio.data;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

/**
 * Class for xmappr - xml to java mapping. LineTo actually isn't a line, but a
 * point to draw lines to next dockers to get similar edges as in Visio.
 * 
 * @author Thamsen
 */
@RootElement("LineTo")
public class LineTo implements Comparable<LineTo> {

	@Attribute("IX")
	public String id;

	@Element("X")
	public PinX pinX;

	@Element("Y")
	public PinY pinY;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getX() {
		return pinX.getX();
	}

	public Double getY() {
		return pinY.getY();
	}

	/**
	 * Compares two lineTos (dockers): Sorting of lineTos is done by their ids,
	 * so that the order is as in Visio.
	 */
	@Override
	public int compareTo(LineTo other) {
		if (other == null) {
			return -1;
		}
		if (other.getId() == null) {
			if (this.getId() == null) {
				return 0;
			} else {
				return -1;
			}
		}
		if (this.getId() == null) {
			return 1;
		}
		Integer thisId = Integer.valueOf(this.getId());
		Integer otherId = Integer.valueOf(other.getId());
		return thisId.compareTo(otherId);
	}

}
