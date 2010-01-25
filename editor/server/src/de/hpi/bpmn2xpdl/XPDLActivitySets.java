package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("ActivitySets")
public class XPDLActivitySets extends XMLConvertable {

	@Element("ActivitySet")
	protected ArrayList<XPDLActivitySet> actvitySets;

	public void add(XPDLActivitySet set) {
		initializeActivitySets();
		
		actvitySets.add(set);
	}
	
	public ArrayList<XPDLActivitySet> getActvitySets() {
		return actvitySets;
	}
	
	public void setActvitySets(ArrayList<XPDLActivitySet> actvitySets) {
		this.actvitySets = actvitySets;
	}
	
	protected void initializeActivitySets() {
		if (getActvitySets() == null) {
			setActvitySets(new ArrayList<XPDLActivitySet>());
		}
	}
}
