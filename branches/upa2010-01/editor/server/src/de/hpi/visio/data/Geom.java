package de.hpi.visio.data;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Point;
import org.xmappr.Element;
import org.xmappr.RootElement;

import java.util.Collections;

/**
 * Class for xmappr - xml to java mapping.
 * 
 * @author Thamsen
 */
@RootElement("Geom")
public class Geom {

	@Element("LineTo")
	public List<LineTo> lineTos;

	public List<LineTo> getLineTos() {
		return lineTos;
	}

	public List<Point> getDockerPointsInRightOrder() {
		if (lineTos != null) {
			List<LineTo> dockerLineTos = getLineTos().subList(0, getLineTos().size() - 1);
			// Last element is the docker to target's middle point. Not
			// necessary, because
			// that's calculated for all edges anyway.
			Collections.sort(dockerLineTos);
			List<Point> dockerPoints = new ArrayList<Point>();
			for (LineTo dockerLineTo : dockerLineTos) {
				Point dockerPoint = new Point(dockerLineTo.getX(), dockerLineTo.getY());
				dockerPoints.add(dockerPoint);
			}
			return dockerPoints;
		}
		return new ArrayList<Point>();
	}

}
