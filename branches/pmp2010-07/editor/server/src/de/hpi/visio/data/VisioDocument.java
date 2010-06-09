package de.hpi.visio.data;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XPDLArtifacts;

@RootElement("VisioDocument")
public class VisioDocument {
	
	@Element("Pages")
	public Pages pages;
	
}
