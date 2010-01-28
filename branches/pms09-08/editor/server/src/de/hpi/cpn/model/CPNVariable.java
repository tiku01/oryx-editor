package de.hpi.cpn.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNVariable extends XMLConvertable
{
	
	//	Beispiel
	//	<var id="ID88708">
	//    <type>
	//      <id>int</id>
	//    </type>
	//    <id>y</id>
	//    <layout>var y: int;</layout>
	//  </var>
    
    
	private String idattri;
	private CPNVarType type;
	private String idtag;
	
	private String layout;
	
// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("var", CPNVariable.class);
		
		xstream.aliasField("id", CPNVariable.class, "idattri");
		xstream.aliasField("id", CPNVariable.class, "idtag");
		
		xstream.useAttributeFor(CPNVariable.class, "idattri");
	}

	// ----------------------------------- JSON Reader -----------------------------------
	
	public void readJSONname(JSONObject modelElement) throws JSONException
	{
		setIdtag(modelElement.getString("name"));
		setIdattri(modelElement.getString("id"));
		
		setType(new CPNVarType());
		getType().setId(modelElement.getString("type"));
		
		setLayout(getLayoutText(modelElement));
	}
	
	// ---------------------------------------- Helper -----------------------------------
	
	public String getLayoutText(JSONObject modelElement) throws JSONException
	{
		String layoutText = "var ";
		layoutText = layoutText + modelElement.getString("name");
		layoutText = layoutText + ": ";
		layoutText = layoutText + modelElement.getString("type");
		layoutText = layoutText + ";";
		
		return layoutText;		
	}
	
	// ------------------------------ Accessor ------------------------------------------
	
	
	public void setIdtag(String idtag) {
		this.idtag = idtag;
	}
	public String getIdtag() {		
		return idtag;
	}

	public void setType(CPNVarType type) {
		this.type = type;
	}
	public CPNVarType getType() {
		return type;
	}

	public void setIdattri(String idattri) {
		this.idattri = idattri;
	}
	public String getIdattri() {
		return idattri;
	}
	
	public void setLayout(String layout) {
		this.layout = layout;
	}
	public String getLayout() {
		return layout;
	}
	

}
