package de.hpi.AdonisSupport;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT RECORD (ROW*)>
//<!ATTLIST RECORD
//name CDATA #REQUIRED
//>

@RootElement("RECORD")
public class AdonisRecord extends XMLConvertible {
	@Attribute("name")
	protected String name;
	
	@Element(name="ROW", targetType=AdonisRow.class)
	protected ArrayList<AdonisRow> children;
	
	public String getName(){
		return name;
	}
	
	public void setName(String value){
		name = value;
	}
	
	public void setChildren(ArrayList<AdonisRow> list){
		children = list;
	}
	
	public ArrayList<AdonisRow> getChildren(){
		return children;
	}

	@Override
	public void write(JSONObject json) throws JSONException {
//		json.object();
//			json.key("name").value(getName());
//			json.key("record_children").array();
//				for (AdonisRow aRow : getChildren()){
//					aRow.write(json);
//				}
//			json.endArray();
//		json.endObject();
	}
}
