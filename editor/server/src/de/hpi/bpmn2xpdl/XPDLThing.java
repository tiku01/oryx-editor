package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public abstract class XPDLThing extends XMLConvertable {

	protected String description;
	protected String id;
	protected String name;
	protected XPDLObject object;
	protected ArrayList<XPDLExtendedAttribute> extendedAttributes;

	public static void registerMapping(XStream xstream) {
		xstream.alias("Thing", XPDLThing.class);

		xstream.useAttributeFor(XPDLThing.class, "id");
		xstream.aliasField("Id", XPDLThing.class, "id");
		xstream.useAttributeFor(XPDLThing.class, "name");
		xstream.aliasField("Name", XPDLThing.class, "name");
		
		xstream.aliasField("xpdl2:ExtendedAttributes", XPDLThing.class, "extendedAttributes");
		xstream.aliasField("xpdl2:Description", XPDLThing.class, "description");
	}

	public ArrayList<XPDLExtendedAttribute> getExtendedAttributes() {
		return extendedAttributes;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public XPDLObject getObject() {
		return object;
	}
	
	public void readJSONcategories(JSONObject modelElement) throws JSONException {
		initializeObject();
		
		JSONObject passObject = new JSONObject();
		passObject.put("id", getProperId(modelElement));
		passObject.put("categories", modelElement.optString("categories"));
		
		getObject().parse(passObject);
	}
	
	public void readJSONdocumentation(JSONObject modelElement) throws JSONException {
		initializeObject();
		
		JSONObject passObject = new JSONObject();
		passObject.put("id", getProperId(modelElement));
		passObject.put("documentation", modelElement.optString("documentation"));
		
		getObject().parse(passObject);
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(getProperId(modelElement));
	}

	public void readJSONname(JSONObject modelElement) {
		setName(modelElement.optString("name"));
	}

	public void readJSONproperties(JSONObject modelElement) throws JSONException {
		parse(modelElement.getJSONObject("properties"));
	}

	public void readJSONresourceId(JSONObject modelElement) throws JSONException {
		/* TODO: Create Extended Attribute for resource ID */
		
		setId(getProperId(modelElement));
	}

	public void setDescription(String descriptionValue) {
		description = descriptionValue;
	}
	
	public void setExtendedAttributes(ArrayList<XPDLExtendedAttribute> attributes) {
		extendedAttributes = attributes;
	}
	
	public void setId(String idValue) {
		id = idValue;
	}

	public void setName(String nameValue) {
		name = nameValue;
	}
	
	public void setObject(XPDLObject objectValue) {
		object = objectValue;
	}
	
	protected String getProperId(JSONObject modelElement) {
		String idValue = modelElement.optString("id");;
		
		if (!idValue.equals("")) {
			return modelElement.optString("id");
		}
		return modelElement.optString("resourceId");
	}
	
	protected void initializeObject() {
		if (getObject() == null) {
			setObject(new XPDLObject());
		}
	}
}
