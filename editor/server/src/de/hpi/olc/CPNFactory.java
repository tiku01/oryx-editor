package de.hpi.olc;

import org.oryxeditor.server.diagram.Shape;
import org.oryxeditor.server.diagram.Diagram;

import de.hpi.cpn.converter.CPNDiagram;
import de.hpi.olc.CPNGenerator.ColorSet;

public class CPNFactory {
	private Diagram cpn;
	private RessourceIDGenerator ressourceIdGenerator = null;
	
	public CPNFactory() {
		this.ressourceIdGenerator = new RessourceIDGenerator();
		this.cpn = CPNDiagram.newColoredPetriNetDiagram();
		cpn.getProperties().put("declarations", CPNDeclarations.getDeclarations());
	}
	
	public Diagram getCpn() {
		// funny, isn't it ...
		cpn.setChildShapes(cpn.getShapes());
		return this.cpn;
	}
	
	/**
	 * Adds a place to the diagram
	 * 
	 * @param title
	 *            : Name of the place
	 * @param colorSet
	 *            : ColorSet of the place
	 * @return the new place
	 */
	public Shape getAPlace(String title, ColorSet colorSet) {
		Shape place = CPNDiagram.getaPlace(ressourceIdGenerator.getNewId());
		cpn.addShapes(place);
		place.getProperties().put("title", title);
		place.getProperties().put("colorsettype", colorSet.toString());
		return place;
	}

	/**
	 * Adds a new transition with guard condition to the diagram
	 * 
	 * @param title
	 *            : Name of the transition
	 * @param guard
	 *            : GuardCondition of the transition
	 * @return the new transition
	 */
	public Shape getATransition(String title, String guard) {
		Shape transition = getATransition(title);
		transition.getProperties().put("guard", guard);
		return transition;
	}

	/**
	 * Adds a new transition to the diagram
	 * 
	 * @param title
	 *            : Name of the transition
	 * @return the new transition
	 */
	public Shape getATransition(String title) {
		Shape transition = CPNDiagram.getaTransition(ressourceIdGenerator.getNewId());
		transition.getProperties().put("title", title);
		cpn.addShapes(transition);
		return transition;
	}

	/**
	 * Adds a new arc to the diagram
	 * 
	 * @param condition
	 *            : label of the arc
	 * @return the new arc
	 */
	public Shape getAnArc(String condition) {
		Shape arc = CPNDiagram.getanArc(ressourceIdGenerator.getNewId());
		arc.getProperties().put("label", condition);
		CPNDiagram.setArcBounds(arc);
		cpn.addShapes(arc);
		return arc;
	}

	/**
	 * Adds a new token to the diagram
	 * 
	 * @param state
	 *            : initial marking of the token
	 * @param i
	 *            : A number to determine the bounds of the token in its place
	 * @return the new token
	 */
	public Shape getAToken(String state) {
		Shape token = CPNDiagram.getaToken(ressourceIdGenerator.getNewId());
		token.setBounds(Layout.getBoundsForToken());
		token.getProperties().put("initialmarking", state);
		return token;
	}

	/**
	 * Connects two shapes with an arc
	 * 
	 * @param source
	 *            : start node (either a place or a transition)
	 * @param arc
	 *            : the arc that should connect both nodes
	 * @param target
	 *            : target node (either a transition or a place)
	 */
	public void connect(Shape source, Shape arc, Shape target) {
		connect(source, arc, target, true);
	}

	public void connect(Shape source, Shape arc, Shape target, boolean mode) {
		source.addOutgoing(arc);
		arc.setTarget(target);
		arc.addOutgoing(target);
		target.addIncoming(arc);
		arc.setDockers(Layout.getDockersForArc(source, target, arc, mode));
	}
	
	private class RessourceIDGenerator {
		private int id = 0;

		public String getNewId() {
			id++;
			return "oryx_" + id;
		}
	}
}
