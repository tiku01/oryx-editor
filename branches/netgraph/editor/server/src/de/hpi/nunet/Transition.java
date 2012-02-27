/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package de.hpi.nunet;

import java.util.Iterator;


public class Transition extends Node {

	/**
	 * 
	 * @return true if outgoing flow connection has a "new" assigned, else false
	 */
	public boolean createsName() {
		for (Iterator<de.hpi.petrinet.FlowRelationship> it = getOutgoingFlowRelationships().iterator(); it.hasNext(); ) {
			if (((FlowRelationship)it.next()).getVariables().contains(NuNet.NEW))
				return true;
		}
		return false;
	}

	public boolean isCommunicationTransition() {
		for (Iterator<de.hpi.petrinet.FlowRelationship> iter=getIncomingFlowRelationships().iterator(); iter.hasNext(); ) {
			Place p = (Place)iter.next().getSource();
			if (p.isCommunicationPlace())
				return true;
		}
		for (Iterator<de.hpi.petrinet.FlowRelationship> iter=getOutgoingFlowRelationships().iterator(); iter.hasNext(); ) {
			Place p = (Place)iter.next().getTarget();
			if (p.isCommunicationPlace())
				return true;
		}
		return false;
	}

} // Transition