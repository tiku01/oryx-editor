package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("ConnectorGraphicsInfos")
public class XPDLConnectorGraphicsInfos extends XMLConvertable {

	@Element("ConnectorGraphicsInfo")
	protected ArrayList<XPDLConnectorGraphicsInfo> connectorGraphicsInfos;

	public void add(XPDLConnectorGraphicsInfo newConnectorGraphicsInfos) {
		initializeConnectorGraphicsInfos();
		
		getConnectorGraphicsInfos().add(newConnectorGraphicsInfos);
	}
	
	public XPDLConnectorGraphicsInfo get(int index) {
		return connectorGraphicsInfos.get(index);
	}
	
	public ArrayList<XPDLConnectorGraphicsInfo> getConnectorGraphicsInfos() {
		return connectorGraphicsInfos;
	}

	public void setConnectorGraphicsInfos(ArrayList<XPDLConnectorGraphicsInfo> newConnectorGraphicsInfos) {
		this.connectorGraphicsInfos = newConnectorGraphicsInfos;
	}
	
	protected void initializeConnectorGraphicsInfos() {
		if (getConnectorGraphicsInfos() == null) {
			setConnectorGraphicsInfos(new ArrayList<XPDLConnectorGraphicsInfo>());
		}
	}
}
