package de.hpi.AdonisSupport;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT INTERREF (IREF*)>
//<!ATTLIST INTERREF
//  name CDATA #REQUIRED
//>


@RootElement("INTERREF")
public class AdonisInterref extends XMLConvertible{
	
	@Attribute("name")
	protected String name;
	
	@Element("iref")
	protected ArrayList<AdonisIref> children;

	
	public String getName(){
		return name;
	}
	
	public void setName(String value){
		name = value;
	}
	
	public ArrayList<AdonisIref> getChildren(){
		return children;
	}
	
	public void setChildren(ArrayList<AdonisIref> list){
		children = list;
	}
}
