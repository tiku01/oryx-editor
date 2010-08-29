package de.hpi.AdonisSupport;


import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Attributes;
import org.xmappr.Element;
import org.xmappr.Elements;
import org.xmappr.RootElement;

//<!ELEMENT ROW ((ATTRIBUTE | INTERREF)*)>
//<!ATTLIST ROW
//  id     ID    #IMPLIED
//  number CDATA #IMPLIED
//>


@RootElement("ROW")
public class AdonisRow extends XMLConvertible{

	@Attributes({
		@Attribute("id"),
		@Attribute("number")
	})
	protected Map<String,String> attributes;
	
	@Elements({
		@Element(name="ATTRIBUTE", targetType=AdonisAttribute.class),
		@Element(name="INTERREF", targetType=AdonisInterref.class)
	})
	protected ArrayList<?> children;

	public Map<String,String> getAttributes(){
		return attributes;
	}
	
	public void setAttributes(Map<String,String> map){
		attributes = map;
	}
	
	public ArrayList<?> getChildren(){
		return children;
	}
	
	public void setChildren(ArrayList<?> list){
		children = list;
	}

	@Override
	public void writeJSON(JSONObject json) throws JSONException {
		// TODO Auto-generated method stub
		
	}
}
