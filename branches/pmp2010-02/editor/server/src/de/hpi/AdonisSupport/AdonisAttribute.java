package de.hpi.AdonisSupport;

import java.io.Serializable;

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
public class AdonisAttribute extends XMLConvertible implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Attribute(name="name")
	protected String name;

	@Attribute(name="type")	
	protected String type;
	
	@Text
	protected String element;

	private String oryxName;
	
	private String language;
	
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
		if (type != null)
			Configurator.setAttributeType(name, type);
	}
	
	public void setType(String value) {
		type = value;
		if (name != null)
			Configurator.setAttributeType(name, type);
	}

	public void setElement(String value) {
		element = value;
	}
	
	public AdonisAttribute(){
		this.name = "";
		this.type = null;
		this.element = null;
	}
	
	public AdonisAttribute(String name, String type, String element) {
		this.name = name;
		this.type = type;
		this.element = element;
	}
	
	
	private void setLanguage(String lang){
		language = lang;
	}
	
	private void setAdonisType(String type){
		Configurator.setAttributeType(name, type);
	}
	
	public String getAdonisType(){
		return Configurator.getAttibuteType(name);
	}
	
	public String getLanguage(){
		if (language == null){
			language = Configurator.getLanguage("model");
		}
		return language;
	}
	
	
	
	public String getOryxName(){
		if (oryxName == null){
			setLanguage(Configurator.getLanguage(getName()));
			oryxName = Configurator.getTranslationToOryx(getName());
		}
		return oryxName;
	}
	
	@Override
	public void write(JSONObject json) throws JSONException {
		JSONObject attribute = getJSONObject(json,getName());
		attribute.putOpt("type", getAdonisType());
		attribute.putOpt("element", getElement());
		
	}
	
}
