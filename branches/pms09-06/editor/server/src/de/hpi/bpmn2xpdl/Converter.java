package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

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
		XPDLActualParameter.registerMapping(xstream);
		XPDLArtifact.registerMapping(xstream);
		XPDLApplication.registerMapping(xstream);
		XPDLAssociation.registerMapping(xstream);
		XPDLCategory.registerMapping(xstream);
		XPDLConformanceClass.registerMapping(xstream);
		XPDLConnectorGraphicsInfo.registerMapping(xstream);
		XPDLCoordinates.registerMapping(xstream);
		XPDLDataField.registerMapping(xstream);
		XPDLDataMapping.registerMapping(xstream);
		XPDLDataObject.registerMapping(xstream);
		XPDLExpression.registerMapping(xstream);
		XPDLExtendedAttribute.registerMapping(xstream);
		XPDLExternalPackage.registerMapping(xstream);
		XPDLExternalReference.registerMapping(xstream);
		XPDLGraphicsInfo.registerMapping(xstream);
		XPDLLane.registerMapping(xstream);
		XPDLLayoutInfo.registerMapping(xstream);
		XPDLMessageFlow.registerMapping(xstream);
		XPDLMessage.registerMapping(xstream);
		XPDLNodeGraphicsInfo.registerMapping(xstream);
		XPDLObject.registerMapping(xstream);
		XPDLPackage.registerMapping(xstream);
		XPDLPackageHeader.registerMapping(xstream);
		XPDLPage.registerMapping(xstream);
		XPDLParticipant.registerMapping(xstream);
		XPDLParticipantType.registerMapping(xstream);
		XPDLPool.registerMapping(xstream);
		XPDLRedefinableHeader.registerMapping(xstream);
		XPDLRoute.registerMapping(xstream);
		XPDLScript.registerMapping(xstream);
		XPDLTransition.registerMapping(xstream);
		XPDLTypeDeclaration.registerMapping(xstream);
		XPDLWorkflowProcess.registerMapping(xstream);
	}
}
