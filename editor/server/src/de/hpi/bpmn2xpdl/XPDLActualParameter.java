package de.hpi.bpmn2xpdl;

import com.thoughtworks.xstream.XStream;

public class XPDLActualParameter extends XPDLScript {

	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:ActualParameter", XPDLScript.class);

		xstream.useAttributeFor(XPDLScript.class, "grammar");
		xstream.aliasField("ScriptGrammar", XPDLScript.class, "grammar");
		xstream.useAttributeFor(XPDLScript.class, "scriptType");
		xstream.aliasField("ScriptType", XPDLScript.class, "scriptType");
		xstream.useAttributeFor(XPDLScript.class, "version");
		xstream.aliasField("ScriptVersion", XPDLScript.class, "version");
	}
}
