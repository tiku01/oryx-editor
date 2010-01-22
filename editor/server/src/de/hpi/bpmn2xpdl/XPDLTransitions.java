package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Transitions")
public class XPDLTransitions extends XMLConvertable {

	@Element("Transition")
	protected ArrayList<XPDLTransition> transitions;

	public void add(XPDLTransition newTransition) {
		initializeTransitions();
		
		getTransitions().add(newTransition);
	}
	
	public ArrayList<XPDLTransition> getTransitions() {
		return transitions;
	}

	public void setTransitions(ArrayList<XPDLTransition> transitions) {
		this.transitions = transitions;
	}
	
	protected void initializeTransitions() {
		if (getTransitions() == null) {
			setTransitions(new ArrayList<XPDLTransition>());
		}
	}
}
