package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("Script")
public class XPDLScript extends XMLConvertable {

	@Attribute("Type")
	protected String scriptType;
	
	public String getScriptType() {
		return scriptType;
	}

	public void readJSONexpressionlanguage(JSONObject modelElement) {
		setScriptType(modelElement.optString("expressionlanguage"));
	}

	public void setScriptType(String typeValue) {
		scriptType = typeValue;
	}
}
