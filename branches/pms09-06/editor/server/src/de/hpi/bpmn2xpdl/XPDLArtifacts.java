package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Artifacts")
public class XPDLArtifacts extends XPDLArtifact {

	@Element("Artifact")
	protected ArrayList<XPDLArtifact> artifacts;

	public void add(XPDLArtifact newArtifact) {
		initializeArtifacts();
		
		getArtifacts().add(newArtifact);
	}
	
	public ArrayList<XPDLArtifact> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(ArrayList<XPDLArtifact> artifacts) {
		this.artifacts = artifacts;
	}
	
	protected void initializeArtifacts() {
		if (getArtifacts() == null) {
			setArtifacts(new ArrayList<XPDLArtifact>());
		}
	}
}
