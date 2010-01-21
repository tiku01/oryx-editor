package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class BPMN2XPDLConverter {
	protected XMLConvertable convertObject;

	public XMLConvertable getConvertObject() {
		return convertObject;
	}

	public String exportXPDL(String json) throws JSONException {
		setConvertObject(new XPDLPackage());
		convertObject.parse(new JSONObject(json));

		XStream xstream = new XStream(new DomDriver());
		registerXPDLMapping(xstream);
		return xstream.toXML(getConvertObject());
	}
	
	public String importXPDL(String xml) {
		XStream xstream = new XStream(new DomDriver());
		registerXPDLMapping(xstream);
		
		XPDLPackage model = (XPDLPackage)xstream.fromXML(xml);
		return "";
	}

	public void setConvertObject(XMLConvertable toConvert) {
		convertObject = toConvert;
	}

	protected void registerXPDLMapping(XStream xstream) {
		XPDLThing.registerMapping(xstream);
		XPDLThingConnectorGraphics.registerMapping(xstream);
		XPDLThingNodeGraphics.registerMapping(xstream);
		
		XPDLActivity.registerMapping(xstream);
		XPDLArtifact.registerMapping(xstream);;
		XPDLAssignment.registerMapping(xstream);
		XPDLAssociation.registerMapping(xstream);
		XPDLCategory.registerMapping(xstream);
		XPDLConformanceClass.registerMapping(xstream);
		XPDLConnectorGraphicsInfo.registerMapping(xstream);
		XPDLCoordinates.registerMapping(xstream);
		XPDLDataObject.registerMapping(xstream);
		XPDLEndEvent.registerMapping(xstream);
		XPDLEvent.registerMapping(xstream);
		XPDLExtendedAttribute.registerMapping(xstream);
		XPDLGraphicsInfo.registerMapping(xstream);
		XPDLIntermediateEvent.registerMapping(xstream);
		XPDLLane.registerMapping(xstream);
		XPDLLoop.registerMapping(xstream);
		XPDLLoopStandard.registerMapping(xstream);
		XPDLMessageFlow.registerMapping(xstream);
		XPDLMessage.registerMapping(xstream);
		XPDLMultiInstance.registerMapping(xstream);
		XPDLNodeGraphicsInfo.registerMapping(xstream);
		XPDLObject.registerMapping(xstream);
		XPDLPackage.registerMapping(xstream);
		XPDLPackageHeader.registerMapping(xstream);
		XPDLPool.registerMapping(xstream);
		XPDLRedefinableHeader.registerMapping(xstream);
		XPDLRoute.registerMapping(xstream);
		XPDLStartEvent.registerMapping(xstream);
		XPDLScript.registerMapping(xstream);
		XPDLTransition.registerMapping(xstream);
		XPDLWorkflowProcess.registerMapping(xstream);
	}
}
