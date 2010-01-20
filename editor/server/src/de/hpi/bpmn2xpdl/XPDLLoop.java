package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLLoop extends XMLConvertable {
	
	protected String loopType;
	
	protected XPDLLoopStandard loopStandard;
	protected XPDLMultiInstance multiInstance;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Loop", XPDLLoop.class);
		
		xstream.useAttributeFor(XPDLLoop.class, "loopType");
		xstream.aliasField("LoopType", XPDLLoop.class, "loopType");
		
		xstream.aliasField("xpdl2:LoopStandard", XPDLLoop.class, "loopStandard");
		xstream.aliasField("xpdl2:MultiInstance", XPDLLoop.class, "multiInstance");
	}
	
	public XPDLLoopStandard getLoopStandard() {
		return loopStandard;
	}
	
	public String getLoopType() {
		return loopType;
	}
	
	public XPDLMultiInstance getMultiInstance() {
		return multiInstance;
	}
	
	public void readJSONloopcondition(JSONObject modelElement) throws JSONException {
		passInformationToStandard(modelElement, "loopcondition");
	}
	
	public void readJSONloopcounter(JSONObject modelElement) throws JSONException {
		if (modelElement.optString("looptype").equals("MultiInstance")) {
			passInformationToMI(modelElement, "loopcounter");
		} else {
			passInformationToStandard(modelElement, "loopcounter");
		}
	}
	
	public void readJSONloopmaximum(JSONObject modelElement) throws JSONException {
		passInformationToStandard(modelElement, "loopmaximum");
	}
	
	public void readJSONlooptype(JSONObject modelElement) {
		setLoopType(modelElement.optString("looptype"));
	}
	
	public void readJSONmi_condition(JSONObject modelElement) throws JSONException {
		passInformationToMI(modelElement, "mi_condition");
	}
	
	public void readJSONmi_flowcondition(JSONObject modelElement) throws JSONException {
		passInformationToMI(modelElement, "mi_flowcondition");
	}
	
	public void readJSONmi_ordering(JSONObject modelElement) throws JSONException {
		passInformationToMI(modelElement, "mi_ordering");
	}
	
	public void readJSONtesttime(JSONObject modelElement) throws JSONException {
		passInformationToStandard(modelElement, "testtime");
	}
	
	public void setLoopStandard(XPDLLoopStandard loop) {
		loopStandard = loop;
	}
	
	public void setLoopType(String typeValue) {
		loopType = typeValue;
	}
	
	public void setMultiInstance(XPDLMultiInstance loop) {
		multiInstance = loop;
	}
	
	protected void initializeLoopStandard() {
		if (getLoopStandard() == null) {
			setLoopStandard(new XPDLLoopStandard());
		}
	}
	
	protected void initializeMultiInstance() {
		if (getMultiInstance() == null) {
			setMultiInstance(new XPDLMultiInstance());
		}
	}
	
	protected void passInformationToMI(JSONObject modelElement, String key) throws JSONException {
		initializeMultiInstance();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		
		getMultiInstance().parse(passObject);
	}
	
	protected void passInformationToStandard(JSONObject modelElement, String key) throws JSONException {
		initializeLoopStandard();
		
		JSONObject passObject = new JSONObject();
		passObject.put(key, modelElement.optString(key));
		
		getLoopStandard().parse(passObject);
	}	
}
