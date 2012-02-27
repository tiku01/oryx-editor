package de.hpi.netgraph2xml;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.shared.uuid.UUID;

import de.hpi.bpmn2xpdl.XMLConvertible;
import de.hpi.netgraph2xml.netgraph.Scenario;

public class XMLConvertibleUtils {
    public static void initializeChildShapes(JSONObject modelElement) throws JSONException {
	if (modelElement.optJSONArray("childShapes") == null) {
	    modelElement.put("childShapes", new JSONArray());
	}
    }
    public static void writeChildren(JSONObject modelElement,
	    Collection<? extends XMLConvertible> programs) throws JSONException {
	if (programs != null) {
	    initializeChildShapes(modelElement);

	    JSONArray childShapes = modelElement.getJSONArray("childShapes");
	    for (XMLConvertible c:programs) {
		JSONObject newActivity = new JSONObject();
		c.write(newActivity);
		childShapes.put(newActivity);
	    }
	}
    }
    public static String generateResourceId(){
	return UUID.create().toString();
    }
    public static JSONObject switchToProperties(JSONObject modelElement)
            throws JSONException {
        JSONObject props = modelElement.optJSONObject("properties");
        if(props == null){
            props = new JSONObject();
            modelElement.put("properties", props);
        }
        modelElement = props;
        return modelElement;
    }
}
