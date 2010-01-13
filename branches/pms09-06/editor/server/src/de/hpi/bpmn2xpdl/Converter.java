package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class Converter {
	protected XMLConvertable convertObject;

	public XMLConvertable getConvertObject() {
		return convertObject;
	}

	public String exportXPDL(String json) throws JSONException {
		setConvertObject(new XPDLPackage());
		convertObject.parse(new JSONObject(json));

		XStream xstream = new XStream();
		registerXPDLMapping(xstream);
		return xstream.toXML(getConvertObject());
	}

	public void setConvertObject(XMLConvertable toConvert) {
		convertObject = toConvert;
	}

	protected void registerXPDLMapping(XStream xstream) {
		XPDLThing.registerMapping(xstream);
		XPDLThingConnectorGraphics.registerMapping(xstream);
		XPDLThingNodeGraphics.registerMapping(xstream);
		
		XPDLArtifact.registerMapping(xstream);
		XPDLAssociation.registerMapping(xstream);
		XPDLConformanceClass.registerMapping(xstream);
		XPDLConnectorGraphicsInfo.registerMapping(xstream);
		XPDLCoordinates.registerMapping(xstream);
		XPDLDataObject.registerMapping(xstream);
		XPDLExtendedAttribute.registerMapping(xstream);
		XPDLGraphicsInfo.registerMapping(xstream);
		XPDLLane.registerMapping(xstream);
		XPDLMessageFlow.registerMapping(xstream);
		XPDLMessageType.registerMapping(xstream);
		XPDLNodeGraphicsInfo.registerMapping(xstream);
		XPDLPackage.registerMapping(xstream);
		XPDLPackageHeader.registerMapping(xstream);
		XPDLPool.registerMapping(xstream);
		XPDLRedefinableHeader.registerMapping(xstream);
		XPDLScriptType.registerMapping(xstream);
		XPDLWorkflowProcess.registerMapping(xstream);
	}
}
