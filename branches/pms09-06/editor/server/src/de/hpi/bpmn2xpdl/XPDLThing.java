package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public abstract class XPDLThing extends XMLConvertable {

	protected String description;
	protected String id;
	protected String name;
	protected String resourceId;
	
	protected XPDLObject object;
	protected ArrayList<XPDLExtendedAttribute> extendedAttributes;

	public static void registerMapping(XStream xstream) {
		xstream.alias("Thing", XPDLThing.class);

		xstream.useAttributeFor(XPDLThing.class, "id");
		xstream.aliasField("Id", XPDLThing.class, "id");
		xstream.useAttributeFor(XPDLThing.class, "name");
		xstream.aliasField("Name", XPDLThing.class, "name");
		
		xstream.aliasField("xpdl2:Object", XPDLThing.class, "object");
		xstream.aliasField("xpdl2:ExtendedAttributes", XPDLThing.class, "extendedAttributes");
		xstream.aliasField("xpdl2:Description", XPDLThing.class, "description");
		
		xstream.omitField(XPDLThing.class, "resourceId");
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
	
	public String getResourceId() {
		return resourceId;
	}
	
	public void readJSONcategories(JSONObject modelElement) throws JSONException {
		initializeObject();
		
		JSONObject passObject = new JSONObject();
		passObject.put("id", getProperId(modelElement));
		passObject.put("categories", modelElement.optString("categories"));
		
		getObject().parse(passObject);
	}
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
	}
	
	public void readJSONdocumentation(JSONObject modelElement) throws JSONException {
		initializeObject();
		
		JSONObject passObject = new JSONObject();
		passObject.put("id", getProperId(modelElement));
		passObject.put("documentation", modelElement.optString("documentation"));
		
		getObject().parse(passObject);
	}
	
	public void readJSONlanes(JSONObject modelElement) {
	}
	
	public void readJSONbgcolor(JSONObject modelElement) throws JSONException {
	}
	
	public void readJSONdockers(JSONObject modelElement) {
		JSONArray dockers = modelElement.optJSONArray("dockers");
		
		if (dockers != null) {
			for (int i = 0; i < dockers.length(); i++) {
				JSONObject docker = dockers.optJSONObject(i);
				
				createExtendedAttribute("docker" + i + "X", docker.optString("x"));
				createExtendedAttribute("docker" + i + "Y", docker.optString("y"));
			}
		}
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(getProperId(modelElement));
	}

	public void readJSONname(JSONObject modelElement) {
		setName(modelElement.optString("name"));
	}
	
	public void readJSONoutgoing(JSONObject modelElement) {
	}
	
	public void readJSONpool(JSONObject modelElement) {
	}

	public void readJSONproperties(JSONObject modelElement) throws JSONException {
		JSONObject properties = modelElement.optJSONObject("properties");
		properties.put("resourceId", getProperId(modelElement));
		parse(properties);
	}

	public void readJSONresourceId(JSONObject modelElement) throws JSONException {
		setResourceId(modelElement.optString("resourceId"));
		
		setId(getProperId(modelElement));
	}
	
	public void readJSONstencil(JSONObject modelElement) {
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
	
	public void setResourceId(String idValue) {
		resourceId = idValue;
	}
	
	protected void createExtendedAttribute(String key, String value) {
		initializeExtendedAttributes();
		
		XPDLExtendedAttribute attribute = new XPDLExtendedAttribute();
		attribute.setName(key);
		attribute.setValue(value);
		
		getExtendedAttributes().add(attribute);
	}
	
	protected String getProperId(JSONObject modelElement) {
		String idValue = modelElement.optString("id");;
		
		if (!idValue.equals("")) {
			return modelElement.optString("id");
		}
		return modelElement.optString("resourceId");
	}
	
	protected void initializeExtendedAttributes() {
		if (getExtendedAttributes() == null) {
			setExtendedAttributes(new ArrayList<XPDLExtendedAttribute>());
		}
	}
	
	protected void initializeObject() {
		if (getObject() == null) {
			setObject(new XPDLObject());
		}
	}
}
