package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("MessageFlows")
public class XPDLMessageFlows extends XMLConvertable {

	@Element("MessageFlow")
	protected ArrayList<XPDLMessageFlow> messageFlows;

	public void add(XPDLMessageFlow newFlow) {
		initializeMessageFlows();
		
		getMessageFlows().add(newFlow);
	}
	
	public ArrayList<XPDLMessageFlow> getMessageFlows() {
		return messageFlows;
	}

	public void setMessageFlows(ArrayList<XPDLMessageFlow> flow) {
		this.messageFlows = flow;
	}
	
	protected void initializeMessageFlows() {
		if (getMessageFlows() == null) {
			setMessageFlows(new ArrayList<XPDLMessageFlow>());
		}
	}
}
