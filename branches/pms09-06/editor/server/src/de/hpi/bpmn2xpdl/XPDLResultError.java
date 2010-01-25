package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;

public class XPDLResultError extends XMLConvertable {
	
	@Attribute("ErrorCode")
	protected String errorCode;

	public String getErrorCode() {
		return errorCode;
	}

	public void readJSONerrorcode(JSONObject modelElement) {
		setErrorCode(modelElement.optString("errorcode"));
	}
	
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
