package de.hpi.bpmn2xpdl;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Object")
public class XPDLObject extends XMLConvertable {
	
	@Element("Categories")
	protected XPDLCategories categories;
	@Element("Documentation")
	protected XPDLDocumentation documentation;
	@Attribute("Id")
	protected String id;
	
	public XPDLCategories getCategories() {
		return categories;
	}
	
	public XPDLDocumentation getDocumentation() {
		return documentation;
	}
	
	public String getId() {
		return id;
	}

	public void readJSONcategories(JSONObject modelElement) throws JSONException {
		initializeCategories();
		
		JSONObject passObject = new JSONObject();
		passObject.put("id", modelElement.optString("id"));
		passObject.put("content", modelElement.optString("categories"));
		
		XPDLCategory nextCategory = new XPDLCategory();
		nextCategory.parse(passObject);
		getCategories().add(nextCategory);		
	}
	
	public void readJSONdocumentation(JSONObject modelElement) {
		XPDLDocumentation newDocumentation = new XPDLDocumentation();
		newDocumentation.setContent(modelElement.optString("documentation"));
		
		setDocumentation(newDocumentation);
	}
	
	public void readJSONid(JSONObject modelElement) {
		setId(modelElement.optString("id")+"-object");
	}
	
	public void setCategories(XPDLCategories categoriesList) {
		categories = categoriesList;
	}
	
	public void setDocumentation(XPDLDocumentation documentationValue) {
		documentation = documentationValue;
	}
	
	public void setId(String idValue) {
		id = idValue;
	}
	
	protected void initializeCategories() {
		if (getCategories() == null) {
			setCategories(new XPDLCategories());
		}
	}
}
