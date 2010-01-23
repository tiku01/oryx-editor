package de.hpi.cpn.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class Converter {
	protected XMLConvertable convertObject;

	public XMLConvertable getConvertObject() {
		return convertObject;
	}

	public String exportCPN(String json) throws JSONException
	{
		CPNTransformer transformer = new CPNTransformer();
		return transformer.transformtoCPN(json);
	}
	

	public void setConvertObject(XMLConvertable toConvert) {
		convertObject = toConvert;
	}

	protected void registerXPDLMapping(XStream xstream) {
//		e.g.
//		XPDLGraphicsInfo.registerMapping(xstream);
//		XPDLThing.registerMapping(xstream);
//		XPDLThingConnectorGraphics.registerMapping(xstream);
//		XPDLThingNodeGraphics.registerMapping(xstream);
		
	}
}

