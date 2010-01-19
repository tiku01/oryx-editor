package de.hpi.bpmn2xpdl;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class XPDLObject extends XMLConvertable {
	
	protected ArrayList<XPDLCategory> categories;
	protected String documentation;
	protected String id;
	protected String name;
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("xpdl2:Object", XPDLObject.class);
		
		xstream.useAttributeFor(XPDLObject.class, "id");
		xstream.aliasField("Id", XPDLObject.class, "id");
		xstream.useAttributeFor(XPDLObject.class, "name");
		xstream.aliasField("Name", XPDLObject.class, "name");
		
		xstream.aliasField("xpdl2:Categories", XPDLObject.class, "categories");
		xstream.aliasField("xpdl2:Documentation", XPDLObject.class, "documentation");
	}
	
	public ArrayList<XPDLCategory> getCategories() {
		return categories;
	}
	
	public String getDocumentation() {
		return documentation;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void readJSONcategories(JSONObject modelElement) throws JSONException {
		initializeCategories();
		
		JSONObject passObject = new JSONObject();
		passObject.put("id", modelElement.optString("id"));
		passObject.put("categories", modelElement.optString("categories"));
		
		XPDLCategory nextCategory = new XPDLCategory();
		nextCategory.parse(passObject);
		getCategories().add(nextCategory);		
	}
	
	public void readJSONdocumentation(JSONObject modelElement) {
		setDocumentation(modelElement.optString("documentation"));
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(modelElement.optString("id")+"-object");
	}
	
	public void setCategories(ArrayList<XPDLCategory> categoriesList) {
		categories = categoriesList;
	}
	
	public void setDocumentation(String documentationValue) {
		documentation = documentationValue;
	}
	
	public void setId(String idValue) {
		id = idValue;
	}
	
	public void setName(String nameValue) {
		name = nameValue;
	}
	
	protected void initializeCategories() {
		if (getCategories() == null) {
			setCategories(new ArrayList<XPDLCategory>());
		}
	}
}
