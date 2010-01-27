package de.hpi.bpmn2xpdl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;

public abstract class XMLConvertable {
	
	@SuppressWarnings("unchecked")
	@Attribute("*")
	protected HashMap unknownAttributes = new HashMap();
	
	@SuppressWarnings("unchecked")
	@Element("*")
	protected ArrayList unknownChildren;
	
	@SuppressWarnings("unchecked")
	public HashMap getUnknownAttributes() {
		return unknownAttributes;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList getUnknownChildren() {
		return unknownChildren;
	}
	
	@SuppressWarnings("unchecked")
	public void parse(JSONObject modelElement) {
		Iterator jsonKeys = modelElement.keys();
		while (jsonKeys.hasNext()) {
			String key = (String) jsonKeys.next();
			String readMethodName = "readJSON" + key;
			if (hasJSONMethod(readMethodName)) {
				try {
					if (keyNotEmpty(modelElement, key)) {
						getClass().getMethod(readMethodName, JSONObject.class).invoke(this, modelElement);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				readJSONunknown(modelElement, key);
			}
		}
	}

	public void readJSONunknown(JSONObject modelElement, String key) {
	}
	
	@SuppressWarnings("unchecked")
	public void setUnknownAttributes(HashMap unknowns) {
		unknownAttributes = unknowns;
	}
	
	@SuppressWarnings("unchecked")
	public void setUnknownChildren(ArrayList unknownElements) {
		unknownChildren = unknownElements;
	}

	protected boolean hasJSONMethod(String methodName) {
		Method[] methods = getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methods[i].getName().equals(methodName) &
				hasMethodJSONParameter(methods[i])) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	protected boolean hasMethodJSONParameter(Method method) {
		Class[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length == 1) {
			return parameterTypes[0].equals(JSONObject.class);
		}
		return false;
	}
	
	protected boolean keyNotEmpty(JSONObject modelElement, String key) {
		try {
			JSONObject objectAtKey = modelElement.getJSONObject(key);
			//Value is a valid JSONObject and has members
			return objectAtKey.length() > 0;
		} catch(JSONException objectException) {
			try {
				JSONArray arrayAtKey = modelElement.getJSONArray(key);
				//Value is a valid JSONArray and has at least one element
				return arrayAtKey.length() > 0;
			} catch(JSONException arrayException) {
				return !modelElement.optString(key).equals(""); 
			}
		}
	}
}
