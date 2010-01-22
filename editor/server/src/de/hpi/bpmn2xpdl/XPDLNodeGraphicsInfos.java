package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("NodeGraphicsInfos")
public class XPDLNodeGraphicsInfos extends XMLConvertable {

	@Element("NodeGraphicsInfo")
	protected ArrayList<XPDLNodeGraphicsInfo> nodeGraphicsInfos;

	public void add(XPDLNodeGraphicsInfo newNodeGraphicsInfos) {
		initializeNodeGraphicsInfos();
		
		getNodeGraphicsInfos().add(newNodeGraphicsInfos);
	}
	
	public XPDLNodeGraphicsInfo get(int index) {
		return nodeGraphicsInfos.get(index);
	}
	
	public ArrayList<XPDLNodeGraphicsInfo> getNodeGraphicsInfos() {
		return nodeGraphicsInfos;
	}

	public void setNodeGraphicsInfos(ArrayList<XPDLNodeGraphicsInfo> newNodeGraphicsInfos) {
		this.nodeGraphicsInfos = newNodeGraphicsInfos;
	}
	
	protected void initializeNodeGraphicsInfos() {
		if (getNodeGraphicsInfos() == null) {
			setNodeGraphicsInfos(new ArrayList<XPDLNodeGraphicsInfo>());
		}
	}
}
