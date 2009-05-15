/**
 * Copyright (c) 2009
 * Ingo Kitzmann, Christoph Koenig
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
package de.unihannover.se.infocup2008.bpmn.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;

/**
 * Implements the <code>BPMNElement</code> Interface.
 * 
 * @author Team Royal Fawn
 * 
 */
public class BPMNElementImpl implements BPMNElement {
	private String type = "";
	private String id = "";
	private List<BPMNElement> outgoingLinks = new LinkedList<BPMNElement>();
	private List<BPMNElement> incomingLinks = new LinkedList<BPMNElement>();
	private Node boundsNode = null;
	private Node dockersNode = null;
	private BPMNGeometry geometry = new BPMNGeometryImpl();
	private BPMNElement parent = null;

	/**
	 * @return the geometry
	 */
	public BPMNGeometry getGeometry() {
		return geometry;
	}

	/**
	 * @param geometry
	 *            the geometry to set
	 */
	public void setGeometry(BPMNGeometry geometry) {
		this.geometry = geometry;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Node getBoundsNode() {
		return boundsNode;
	}

	public void setBoundsNode(Node node) {
		this.boundsNode = node;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<BPMNElement> getOutgoingLinks() {
		return outgoingLinks;
	}

	public void setOutgoingLinks(List<BPMNElement> outgoingLinks) {
		this.outgoingLinks = outgoingLinks;
	}

	public void addOutgoingLink(BPMNElement element) {
		this.outgoingLinks.add(element);
	}

	public void updateNodes() {
		this.boundsNode.setNodeValue(geometry.getX() + "," + geometry.getY()
				+ "," + geometry.getX2() + "," + geometry.getY2());
	}

	public String toString() {
		String out = "BPMNElement: ";
		out += " ID=" + getId();
		out += " Type=" + getType();
		out += geometry.toString();
		out += " links=" + getOutgoingLinks().size();
		return out;
	}

	public List<BPMNElement> getFollowingElements() {
		List<BPMNElement> followingElements = new LinkedList<BPMNElement>();

		for (BPMNElement element : getOutgoingLinks()) {
			if (BPMNType.isAConnectingElement(element.getType())) {
				followingElements.addAll(element.getFollowingElements());
			} else if (BPMNType.isAActivity(this.type)
					&& BPMNType.isACatchingIntermediateEvent(element.getType())) {
				followingElements.addAll(element.getFollowingElements());
			} else if (!BPMNType.isASwimlane(element.getType())) {
				followingElements.add(element);
			}
		}

		return followingElements;
	}

	public Node getDockersNode() {
		return this.dockersNode;
	}

	public void setDockersNode(Node node) {
		this.dockersNode = node;
	}

	public List<BPMNElement> getIncomingLinks() {
		return this.incomingLinks;
	}

	public void setIncomingLinks(List<BPMNElement> incomingLinks) {
		this.incomingLinks = incomingLinks;
	}

	public void addIncomingLink(BPMNElement element) {
		this.incomingLinks.add(element);
	}

	public List<BPMNElement> getPrecedingElements() {
		List<BPMNElement> precedingElements = new LinkedList<BPMNElement>();

		for (BPMNElement element : getIncomingLinks()) {
			if (BPMNType.isAConnectingElement(element.getType())) {
				precedingElements.addAll(element.getPrecedingElements());
			} else if (BPMNType.isACatchingIntermediateEvent(this.type)
					&& BPMNType.isAActivity(element.getType())) {
				precedingElements.addAll(element.getPrecedingElements());
			} else if (element.isADockedIntermediateEvent()) {
				precedingElements.addAll(element.getIncomingLinks());
			} else if (!BPMNType.isASwimlane(element.getType())) {
				precedingElements.add(element);
			}
		}

		return precedingElements;
	}

	public void removeIncomingLink(BPMNElement element) {
		this.incomingLinks.remove(element);
	}

	public void removeOutgoingLink(BPMNElement element) {
		this.outgoingLinks.remove(element);
	}

	public boolean isJoin() {
		return this.getPrecedingElements().size() > 1;
	}

	public boolean isSplit() {
		return this.getFollowingElements().size() > 1;
	}

	public int backwardDistanceTo(BPMNElement other) {
		return _backwardDistanceTo(other, Collections.EMPTY_SET);
	}

	/**
	 * @param other
	 * @return
	 */
	private int _backwardDistanceTo(BPMNElement other, Set<BPMNElement> history) {
		
		if (other == this) {
			return 0;
		}
		if (history.contains(this)){
			//Workaround to backwardsSeek Bug
			return Integer.MAX_VALUE;
		}
		int d = Integer.MAX_VALUE;
		Set<BPMNElement> newHistory = new HashSet<BPMNElement>(history);
		newHistory.add(this);
		for (BPMNElement el : this.getPrecedingElements()) {
			d = Math.min(d, ((BPMNElementImpl) el)._backwardDistanceTo(other, newHistory));
		}
		return d == Integer.MAX_VALUE ? d : d + 1;
	}

	public int forwardDistanceTo(BPMNElement other) {
		if (other == this) {
			return 0;
		}
		int d = Integer.MAX_VALUE;
		for (BPMNElement el : this.getFollowingElements()) {
			d = Math.min(d, el.forwardDistanceTo(other));
		}
		return d == Integer.MAX_VALUE ? d : d + 1;
	}

	public BPMNElement prevSplit() {
		int distance = Integer.MAX_VALUE;
		int candidateDistance = 0;
		BPMNElement split = null;
		BPMNElement candidate;
		for (BPMNElement elem : this.getPrecedingElements()) {
			if (elem.isSplit() && elem.getParent() == this.getParent()) {
				return elem;
			}
			candidate = elem.prevSplit();
			if (this.isJoin()) {
				// Performance Twaek. If this is not a join, we have only one
				// precedessor and do not need to determine the closest one
				candidateDistance = elem.backwardDistanceTo(candidate);
			}
			if (candidateDistance < distance) {
				split = candidate;
				distance = candidateDistance;
			}
		}
		return split;
	}

	public boolean isADockedIntermediateEvent() {
		if (!BPMNType.isACatchingIntermediateEvent(this.type)) {
			return false;
		}

		for (BPMNElement element : this.incomingLinks) {
			if (BPMNType.isAActivity(element.getType())) {
				return true;
			}
		}

		return false;
	}

	public BPMNElement getParent() {
		return this.parent;
	}

	public boolean hasParent() {
		return this.parent != null;
	}

	public void setParent(BPMNElement element) {
		this.parent = element;
	}

}
