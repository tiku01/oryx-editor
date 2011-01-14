package de.hpi.bpt.mashup.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.StencilSet;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.PTnet.PTNet;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Transition;

public class PTNet2Diagram {

	private static final double DEFAULT_HEIGHT = 1200.0;
	private static final double DEFAULT_WIDTH = 1600.0;
	
	public static Diagram convert(PTNet net, StencilSet stencilSet, StencilType stencilType) {
		Diagram diagram = new Diagram("oryx-canvas123", stencilType, stencilSet);
		initializeDiagram(diagram);
		
		HashMap<Node, Shape> nodeMap = new HashMap<Node, Shape>();
		
		for (Node node:net.getNodes()) {
			Shape shape = null;
			if (node instanceof Place) {
				shape = createPlace((Place) node);
			} else if (node instanceof LabeledTransition) {
				shape = createTransition((LabeledTransition) node);
			} else if (node instanceof Transition) {
				shape = createEmptyTransition((Transition) node);
			}
			if (shape != null) {
				diagram.getChildShapes().add(shape);
				nodeMap.put(node, shape);
			}
		}
		for (FlowRelationship flow:net.getFlowRelationships()) {
			Shape shape = createArc(flow);
			if (flow.getSource() != null) {
				Shape source = nodeMap.get(flow.getSource());
				source.addOutgoing(shape);
				shape.addIncoming(source);
			}
			if (flow.getTarget() != null) {
				Shape target = nodeMap.get(flow.getTarget());
				target.addIncoming(shape);
				shape.addOutgoing(target);
			}
			diagram.getChildShapes().add(shape);
		}
		
		return diagram;
	}
	
	private static void initializeDiagram(Diagram diagram) {
		Calendar cal = Calendar.getInstance();
		final String date = new SimpleDateFormat("dd/MM/yy").format(cal.getTime());
		HashMap<String, String> diagramProps = new HashMap<String, String>() {{
			put("title", "");
			put("engine", "false");
			put("version", "");
			put("author", "");
			put("language", "English");
			put("creationdate", date);
			put("modificationdate", date);
			put("documentation", "This petrinet was generated automatically.");
		}};
		diagram.setProperties(diagramProps);
		diagram.setBounds(new Bounds(new Point(DEFAULT_WIDTH, DEFAULT_HEIGHT), new Point(0.0, 0.0)));
		System.out.println("Init shapes in diagram");
		//diagram.setShapes(new ArrayList<Shape>());
	}
	
	private static Bounds convertBounds(de.hpi.util.Bounds old) {
		return new Bounds(
				new Point((double)old.getX2(), (double)old.getY2()), 
				new Point((double) old.getX1(), (double) old.getY1())
				);
	}
	
	private static Shape createPlace(Place place) {
		Shape shape = new Shape(place.getResourceId(), new StencilType("Place"));
		final String label = place.getLabel() != null ? place.getLabel() : "";
		HashMap<String, String> properties = new HashMap<String, String>() {{
			put("title", label);
			put("id", "");
			put("numberoftokens", "");
			put("numberoftokens_text", "");
			put("numberoftokens_drawing", "0");
			put("external", "false");
			put("exttype", "Push");
			put("href", "");
			put("locatornames", "");
			put("locatortypes", "");
			put("locatorexpr", "");
		}};
		shape.setProperties(properties);
		shape.setBounds(convertBounds(place.getBounds()));
		return shape;
	}
	
	private static Shape createTransition(LabeledTransition trans) {
		Shape shape = new Shape(trans.getResourceId(), new StencilType("Transition"));
		final String label = trans.getLabel() != null ? trans.getLabel() : "";
		HashMap<String, String> properties = new HashMap<String, String>() {{
			put("title", label);
			put("id", "");
			put("firetype", "Automatic");
			put("href", "");
			put("omodel", "");
			put("oform", "");
			put("guard", "");
			put("communicationchannel", "");
			put("communicationtype", "Default");
		}};
		shape.setProperties(properties);
		shape.setBounds(convertBounds(trans.getBounds()));
		return shape;
	}
	
	private static Shape createEmptyTransition(Transition trans) {
		Shape shape = new Shape(trans.getResourceId(), new StencilType("VerticalEmptyTransition"));
		HashMap<String, String> properties = new HashMap<String, String>() {{
			put("title", "");
			put("id", "");
			put("firetype", "Automatic");
			put("href", "");
			put("omodel", "");
			put("oform", "");
			put("guard", "");
		}};
		shape.setProperties(properties);
		shape.setBounds(convertBounds(trans.getBounds()));
		return shape;
	}
	
	private static Shape createArc(FlowRelationship flow) {
		// TODO: what about the bounds? 
		Shape shape = new Shape(flow.getResourceId(), new StencilType("Arc"));
		final String label = flow.getLabel() != null ? flow.getLabel() : "";
		HashMap<String, String> properties = new HashMap<String, String>() {{
			put("id", label);
			put("label", "");
			put("transformation", "");
		}};
		shape.setProperties(properties);
		// calculate boundaries based on the boundaries of source and target
		if (flow.getSource() != null && flow.getTarget() != null) {
			Bounds source = convertBounds(flow.getSource().getBounds());
			Bounds target = convertBounds(flow.getTarget().getBounds());
			double x1 = (source.getLowerRight().getX() + source.getUpperLeft().getX()) / 2; 
			double y1 = (source.getLowerRight().getY() + source.getUpperLeft().getY()) / 2;
			double x2 = (target.getLowerRight().getX() + target.getUpperLeft().getX()) / 2; 
			double y2 = (target.getLowerRight().getY() + target.getUpperLeft().getY()) / 2;
			shape.setBounds(new Bounds(
					new Point(x1 < x2 ? x2 : x1, y1 < y2 ? y2 : y1), 
					new Point(x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2)
					));
		}
		// set default dockers
		ArrayList<Point> dockers = new ArrayList<Point>();
		dockers.add(new Point(20.0, 20.0));
		dockers.add(new Point(15.0, 15.0));
		shape.setDockers(dockers);
		return shape;
	}
}
