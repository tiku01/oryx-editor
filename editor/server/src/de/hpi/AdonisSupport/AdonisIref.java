package de.hpi.AdonisSupport;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

//<!ELEMENT IREF EMPTY>
//<!ATTLIST IREF
//  type       CDATA #REQUIRED
//  tmodeltype CDATA #REQUIRED
//  tmodelname CDATA #REQUIRED
//  tmodelver  CDATA #REQUIRED
//  tclassname CDATA #IMPLIED
//  tobjname   CDATA #IMPLIED
//>




@RootElement("IREF")
public class AdonisIref extends XMLConvertible{

	private static final long serialVersionUID = 4777979439718824214L;

	@Attribute("type")
	protected String type;
	public void setType(String value){type = value;}
	public String getType(){return type;}
	@Attribute("tmodeltype")
	protected String tmodeltype;
	public void setTmodeltype(String value){tmodeltype = value;}
	public String getTmodeltype(){return tmodeltype;}
	@Attribute("tmodelname")
	protected String tmodelname;
	public void setTmodelname(String value){tmodelname = value;}
	public String getTmodelname(){return tmodelname;}
	
	@Attribute(name="tmodelver")
	protected String tmodelver = "fucking bullshit";
	public void setTmodelver(String value){tmodelver = value;}
	public String getTmodelver(){if (tmodelver == null || tmodelver == "") return "fucking bullshit"; else return tmodelver;}
	
	@Attribute("tclassname")
	protected String tclassname;
	public void setTclassname(String value){tclassname = value;}
	public String getTclassname(){return tclassname;}
	
	@Attribute("tobjname")
	protected String tobjname;
	public void setTobjname(String value){tobjname = value;}
	public String getTobjname(){return tobjname;}
	
	@Override
	public void write(JSONObject json) throws JSONException {
//		json.object();
//		json.key("type").value(getType());
//		json.key("tmodeltype").value(getTmodeltype());
//		json.key("tmodelname").value(getTmodelname());
//		json.key("tmodelver").value(getTmodelver());
//		if (getTclassname() != null) json.key("tclassname").value(getTclassname());
//		if (getTobjname() != null) json.key("tobjname").value(getTobjname());
//		json.endObject();
	}
	
}
