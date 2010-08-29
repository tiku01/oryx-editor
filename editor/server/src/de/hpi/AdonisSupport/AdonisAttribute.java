package de.hpi.AdonisSupport;

import java.io.Serializable;

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
	protected String adonisName;
	
	private String oryxName;

	@Attribute(name="type")	
	protected String type;
	
	@Text
	protected String element;

	@SuppressWarnings("unused")
	private String language;
	
	public String getAdonisName() {
		return adonisName;
	}
	public String getType() {
		return type;
	}

	public String getElement() {
		return element;
	}

	public void setAdonisName(String value) {
		adonisName = value;
	}
	
	public void setType(String value) {
		type = value;
	}

	public void setElement(String value) {
		element = value;
	}
	
	public AdonisAttribute(){
		this.adonisName = "";
		this.type = null;
		this.element = null;
	}
	
	public AdonisAttribute(String language, String oryxName, String defaultType, String element) {
		this.adonisName = Configurator.getAdonisIdentifier(oryxName,language);
		this.type = Configurator.getStandardValue(oryxName, "type", defaultType);
		this.element = element;
	}
	
	
	public void getLanguage(String language){
		this.language = language;
	}
	
	public String getAdonisType(){
		return Configurator.getStandardValue(
				Configurator.getOryxIdentifier(getAdonisName()), 
				"type",
				"STRING");
	}
	
	public String getOryxName(){
		if (oryxName == null){
			oryxName = Configurator.getOryxIdentifier(getAdonisName());
		}
		return oryxName;
	}	
}
