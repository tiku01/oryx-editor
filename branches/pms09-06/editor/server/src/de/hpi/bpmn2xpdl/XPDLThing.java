package de.hpi.bpmn2xpdl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Thing")
public abstract class XPDLThing extends XMLConvertable {

	@Element("Description")
	protected String description;
	@Attribute("Id")
	protected String id;
	@Attribute("Name")
	protected String name;
	protected String resourceId;
	
	@Element("Object")
	protected XPDLObject object;
	@Element("ExtendedAttributes")
	protected XPDLExtendedAttributes extendedAttributes;
	
	public XPDLExtendedAttributes getExtendedAttributes() {
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
	
	public void readJSONdockers(JSONObject modelElement) throws JSONException {
		JSONArray dockers = modelElement.optJSONArray("dockers");
		
		if (dockers != null) {
			if (dockers.length() >= 2) {
				JSONObject firstDocker = dockers.optJSONObject(0);
				createExtendedAttribute("docker" + 0 + "X", firstDocker.optString("x"));
				
				JSONObject lastDocker = dockers.optJSONObject(dockers.length()-1);
				createExtendedAttribute("docker" + String.valueOf(dockers.length()-1) + "Y", lastDocker.optString("y"));
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
	
	public void setExtendedAttributes(XPDLExtendedAttributes attributes) {
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
			setExtendedAttributes(new XPDLExtendedAttributes());
		}
	}
	
	protected void initializeObject() {
		if (getObject() == null) {
			setObject(new XPDLObject());
		}
	}
}
