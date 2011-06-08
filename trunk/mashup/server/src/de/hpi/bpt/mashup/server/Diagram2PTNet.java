package de.hpi.bpt.mashup.server;

import java.util.HashMap;

import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.petrinet.PetriNetUtils;
import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.PTNetFactory;
import de.hpi.petrinet.FlowRelationship;
import de.hpi.petrinet.LabeledTransition;
import de.hpi.petrinet.Place;
import de.hpi.petrinet.Node;
import de.hpi.petrinet.Transition;
import de.hpi.util.Bounds;

public class Diagram2PTNet {

	private static PTNetFactory factory = PTNetFactory.eINSTANCE;
	private static final String NAMESPACE = "http://b3mn.org/stencilset/petrinet#";
	/**
	 * Transforms a given generic Diagram into a PTNet.
	 * @param the Diagram to transform
	 * @return a PTNet instance
	 * @throws TransformationException 
	 */
	public static PTNet convert(Diagram diagram) throws TransformationException {
		if (diagram == null || !diagram.getStencilset().getNamespace().equals(NAMESPACE))
			throw new IllegalArgumentException("The diagram is no petrinet!");
		PTNet net = factory.createPetriNet();
		net.setId(diagram.getResourceId());
		HashMap<Shape, Node> shapeMap = new HashMap<Shape, Node>();
		for (Shape child:diagram.getChildShapes()) {
			// first run: initialize all shapes
			// we don't check the childShapes of a shape, because there
			// shouldn't be any nested shapes in a petrinet
			if (child.getStencilId().equals("Place")) {
				Place p = PetriNetUtils.addPlace(net);
				p.setResourceId(child.getResourceId());
				p.setLabel(child.getProperty("title") != null ? child.getProperty("title") : "");
				p.setBounds(new Bounds(new String[]{
						child.getBounds().getUpperLeft().getX().toString(),
						child.getBounds().getUpperLeft().getY().toString(),
						child.getBounds().getLowerRight().getX().toString(),
						child.getBounds().getLowerRight().getY().toString()
						}));
				shapeMap.put(child, p);
			} else if (child.getStencilId().equals("Transition")) {
				Transition t;
				if (child.getProperty("title") != null) {
					t = factory.createLabeledTransition();
					((LabeledTransition) t).setLabel(child.getProperty("title")); 
					net.getTransitions().add(t);
				} else 
					t = PetriNetUtils.addSilentTransition(net);
				t.setResourceId(child.getResourceId());
				t.setBounds(new Bounds(new String[]{
						child.getBounds().getUpperLeft().getX().toString(),
						child.getBounds().getUpperLeft().getY().toString(),
						child.getBounds().getLowerRight().getX().toString(),
						child.getBounds().getLowerRight().getY().toString()
						}));
				shapeMap.put(child, t);
			} else if (child.getStencilId().equals("VerticalEmptyTransition")) {
				Transition t = PetriNetUtils.addSilentTransition(net);
				t.setResourceId(child.getResourceId());
				t.setBounds(new Bounds(new String[]{
						child.getBounds().getUpperLeft().getX().toString(),
						child.getBounds().getUpperLeft().getY().toString(),
						child.getBounds().getLowerRight().getX().toString(),
						child.getBounds().getLowerRight().getY().toString()
						}));
				shapeMap.put(child, t);
			} else if (child.getStencilId().equals("Arc")) {
				// do nothing, we will handle them later
			} else {
				throw new TransformationException("Found unexpected stencil type: " + child.getStencilId());
			}
			
		}
		for (Shape child:diagram.getChildShapes()) {
			// second run: link the shapes (create the flows)
			if (child.getStencilId().equals("Arc")) {
				if (child.getIncomings().size() == 1 && child.getOutgoings().size() == 1) {	
					FlowRelationship flow = PetriNetUtils.addFlowRelationship(net, 
							shapeMap.get(child.getIncomings().get(0)), 
							shapeMap.get(child.getOutgoings().get(0)));
					flow.setResourceId(child.getResourceId());
					flow.setLabel(child.getProperty("title") != null ? child.getProperty("title") : "");
				} else {
					throw new TransformationException("Found an Arc with multiple incoming/outgoing connections.");
				}
			}
		}
		return net;
	}
}
