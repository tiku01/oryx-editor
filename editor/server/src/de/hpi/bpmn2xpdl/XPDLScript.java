package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLScript extends XMLConvertable {

	protected String scriptType;

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Script", XPDLScript.class);

		xstream.useAttributeFor(XPDLScript.class, "scriptType");
		xstream.aliasField("Type", XPDLScript.class, "scriptType");
	}
	
	public String getType() {
		return scriptType;
	}

	public void readJSONexpressionlanguage(JSONObject modelElement) {
		setType(modelElement.optString("expressionlanguage"));
	}

	public void setType(String typeValue) {
		scriptType = typeValue;
	}
}
