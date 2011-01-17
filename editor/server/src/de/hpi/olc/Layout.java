package de.hpi.olc;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;

public class Layout {
	public static Bounds getBoundsForTransition(int x, int y) {
		int width = 80;
		int height = 40;
		Bounds bounds = new Bounds(new Point(x+width/2.0, y+height/2.0),new Point(x-width/2.0, y-height/2.0));
		return bounds;
	}
	
	public static Bounds getBoundsForPlace(int x, int y) {
		int width = 40;
		int height = 40;
		Bounds bounds = new Bounds(new Point(x+width/2.0, y+height/2.0),new Point(x-width/2.0, y-height/2.0));
		return bounds;
	}
	
	public static Bounds getBoundsForToken() {
		Bounds bounds = new Bounds(new Point(26.0,26.0), new Point(14.0,14.0));
		return bounds;
	}
	
	public static List<Point> getDockersForArc(Shape source, Shape target, Shape arc, boolean mode) {
		double x1 = (source.getBounds().getLowerRight().getX() - source.getBounds().getUpperLeft().getX())/2.0;
		double y1 = (source.getBounds().getLowerRight().getY() - source.getBounds().getUpperLeft().getY())/2.0;
		double middleX1 = source.getBounds().getLowerRight().getX() - x1;
		double middleY1 = source.getBounds().getLowerRight().getY() - y1;
		
		double x2 = (target.getBounds().getLowerRight().getX() - target.getBounds().getUpperLeft().getX())/2.0;
		double y2 = (target.getBounds().getLowerRight().getY() - target.getBounds().getUpperLeft().getY())/2.0;
		double middleX2 = target.getBounds().getLowerRight().getX() - x2;
		double middleY2 = target.getBounds().getLowerRight().getY() - y2;
		
		List<Point> dockers = new ArrayList<Point>();
		dockers.add(new Point(x1,y1));
		if(middleX1 != middleX2 && middleY1 != middleY2) {
			if(mode) dockers.add(new Point(middleX1,middleY2));
			else dockers.add(new Point(middleX2,middleY1));
		}
		dockers.add(new Point(x2,y2));
		return dockers;
	}
	
}
