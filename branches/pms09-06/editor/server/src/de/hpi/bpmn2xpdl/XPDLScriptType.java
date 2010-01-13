package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLScriptType extends XMLConvertable {

	/*
	 * TODO: version, grammar
	 */

	protected String scriptType;

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:ScriptType", XPDLScriptType.class);

		xstream.useAttributeFor(XPDLScriptType.class, "scriptType");
		xstream.aliasField("Type", XPDLScriptType.class, "scriptType");
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
