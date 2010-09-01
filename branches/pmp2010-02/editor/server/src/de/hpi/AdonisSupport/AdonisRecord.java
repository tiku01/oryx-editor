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
	/**
	 * 
	 */
	private static final long serialVersionUID = 9006289053085606498L;

	@Attribute("name")
	protected String name;
	
	@Element(name="ROW", targetType=AdonisRow.class)
	protected ArrayList<AdonisRow> row;
	
	public String getName(){
		return name;
	}
	
	public void setName(String value){
		name = value;
	}
	
	public void setRow(ArrayList<AdonisRow> list){
		row = list;
	}
	
	public ArrayList<AdonisRow> getRow(){
		return row;
	}
	
	public static AdonisRecord create(String name,String language){
		AdonisRecord record = new AdonisRecord();
		record.setName(Configurator.getAdonisIdentifier(name,language));
		record.setRow(new ArrayList<AdonisRow>());
		return record;
	}

	@Override
	public void writeJSON(JSONObject json) throws JSONException {
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
