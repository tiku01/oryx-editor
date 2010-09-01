package de.hpi.AdonisSupport;


import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT ROW ((ATTRIBUTE | INTERREF)*)>
//<!ATTLIST ROW
//  id     ID    #IMPLIED
//  number CDATA #IMPLIED
//>


@RootElement("ROW")
public class AdonisRow extends XMLConvertible{

	private static final long serialVersionUID = 4978096240900242298L;

	@Attribute("id")
	protected String id;

	@Attribute("number")
	protected Integer number;
		
	@Element(name="ATTRIBUTE")
	protected ArrayList<AdonisAttribute> attribute;
	
	@Element(name="INTERREF")
	protected ArrayList<AdonisInterref> interref;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}
	
	public ArrayList<AdonisAttribute> getAttribute() {
		return attribute;
	}

	public void setAttribute(ArrayList<AdonisAttribute> attribute) {
		this.attribute = attribute;
	}

	public ArrayList<AdonisInterref> getInterref() {
		return interref;
	}

	public void setInterref(ArrayList<AdonisInterref> interref) {
		this.interref = interref;
	}

	public static AdonisRow create(Integer number){
		AdonisRow row = new AdonisRow();
		row.setNumber(number);
		row.setId(Helper.generateId("row."));
		row.setAttribute(new ArrayList<AdonisAttribute>());
		row.setInterref(new ArrayList<AdonisInterref>());
		return row;
	}
	
	public AdonisAttribute getAttribute(String identifier,String lang){
		for (AdonisAttribute anAttribute : getAttribute()){
			if (identifier.equals(Unifier.getOryxIdentifier(anAttribute.getAdonisName(),lang)))
				return anAttribute;
		}
		return null;
	}
	
	public AdonisInterref getInterref(String identifier,String lang){
		for (AdonisInterref anInterref : getInterref()){
			if (identifier.equals(Unifier.getOryxIdentifier(anInterref.getName(),lang)))
				return anInterref;
		}
		return null;
	}
	
	@Override
	public void writeJSON(JSONObject json) throws JSONException {
		// TODO Auto-generated method stub
		
	}
}
