package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Lanes")
public class XPDLLanes extends XMLConvertable {

	@Element("Lane")
	protected ArrayList<XPDLLane> lanes;

	public void add(XPDLLane newLane) {
		initializeLanes();
		
		getLanes().add(newLane);
	}
	
	public ArrayList<XPDLLane> getLanes() {
		return lanes;
	}

	public void setLanes(ArrayList<XPDLLane> lanes) {
		this.lanes = lanes;
	}
	
	protected void initializeLanes() {
		if (getLanes() == null) {
			setLanes(new ArrayList<XPDLLane>());
		}
	}
}
