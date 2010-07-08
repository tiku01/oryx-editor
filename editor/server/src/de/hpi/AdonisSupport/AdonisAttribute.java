package de.hpi.AdonisSupport;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;
import org.xmappr.Text;

//<!ELEMENT ATTRIBUTE (#PCDATA)>
//<!ATTLIST ATTRIBUTE
//  name CDATA #REQUIRED
//  type CDATA #REQUIRED
//>


@RootElement("ATTRIBUTE")
public class AdonisAttribute extends AdonisBaseObject{
	
	@Attribute(name="name")
	protected String name;

	@Attribute(name="type")	
	protected String type;
	
	@Text
	protected String element;

	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}

	public String getElement() {
		return element;
	}

	public void setName(String value) {
		name = value;
	}
	
	public void setType(String value) {
		type = value;
	}

	public void setElement(String value) {
		element = value;
	}
	
	@Override
	public void write(JSONObject json) throws JSONException {
		JSONObject attribute = getJSONObject(json,getName());
		attribute.putOpt("type", getType());
		attribute.putOpt("element", getElement());
		
	}
	
}
