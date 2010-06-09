package de.hpi.visio.data;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

import de.hpi.bpmn2xpdl.XPDLArtifact;

@RootElement("Pages")
public class Pages {
	
	@Element("Page")
	public ArrayList<Page> pages;

}
