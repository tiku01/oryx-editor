package de.hpi.visio.data;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Point;
import org.xmappr.Element;
import org.xmappr.RootElement;

import java.util.Collections;

/**
 * Additional information for edges: lineTos are used to create dockers
 * @author Thamsen
 *
 */
@RootElement("Geom")
public class Geom {
	
	/**
	 * Information abount edge's shaping: Used to create dockers
	 */
	@Element("LineTo") 
	public List<LineTo> lineTos;

	public List<LineTo> getLineTos() {
		return lineTos;
	}

	public List<Point> getDockerPointsInRightOrder() {
		if (lineTos != null) {
			List<LineTo> dockerLineTos = getLineTos().subList(0, getLineTos().size() - 1); 
			// last is from last docker to target shape's middle point, not necessary
			Collections.sort(dockerLineTos);
			List<Point> dockerPoints = new ArrayList<Point>();
			for(LineTo dockerLineTo : dockerLineTos) {
				Point dockerPoint = new Point(dockerLineTo.getX(), dockerLineTo.getY());
				dockerPoints.add(dockerPoint);
			}
			return dockerPoints;
		}
		return new ArrayList<Point>();
	}
	
}
