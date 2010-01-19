package de.hpi.bpmn2xpdl;

import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLScript extends XMLConvertable {

	protected String grammar;
	protected String scriptType;
	protected String version;

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Script", XPDLScript.class);

		xstream.useAttributeFor(XPDLScript.class, "grammar");
		xstream.aliasField("Grammar", XPDLScript.class, "grammar");
		xstream.useAttributeFor(XPDLScript.class, "scriptType");
		xstream.aliasField("Type", XPDLScript.class, "scriptType");
		xstream.useAttributeFor(XPDLScript.class, "version");
		xstream.aliasField("Version", XPDLScript.class, "version");
	}

	public String getGrammar() {
		return grammar;
	}
	
	public String getType() {
		return scriptType;
	}
	
	public String getVersion() {
		return version;
	}

	public void readJSONexpressionlanguage(JSONObject modelElement) {
		setType(modelElement.optString("expressionlanguage"));
	}
	
	public void setGrammar(String grammarUrl) {
		grammar = grammarUrl;
	}

	public void setType(String typeValue) {
		scriptType = typeValue;
	}
	
	public void setVersion(String versionValue) {
		version = versionValue;
	}
}
