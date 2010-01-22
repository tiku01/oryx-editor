package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Associations")
public class XPDLAssociations extends XMLConvertable {

	@Element("Association")
	protected ArrayList<XPDLAssociation> associations;

	public void add(XPDLAssociation newAssociation) {
		initializeAssociations();
		
		getAssociations().add(newAssociation);
	}
	
	public ArrayList<XPDLAssociation> getAssociations() {
		return associations;
	}

	public void setAssociations(ArrayList<XPDLAssociation> association) {
		this.associations = association;
	}
	
	protected void initializeAssociations() {
		if (getAssociations() == null) {
			setAssociations(new ArrayList<XPDLAssociation>());
		}
	}
}
