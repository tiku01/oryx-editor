package de.hpi.cpn.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNProduct
{
	
	private ArrayList<String> ids;
	
	// ------------------------------------------ Mapping ------------------------------------------
	public static void registerMapping(XStream xstream)
	{
	   xstream.alias("product", CPNProduct.class);
	   
	   xstream.alias("id", String.class);
	   
	   xstream.addImplicitCollection(CPNProduct.class, "ids");
	}
	
	// -------------------------------------------- Helper ----------------------------------------
	
	public String getLayoutText(JSONObject modelElement) throws JSONException
	{
		String layoutText = "colset ";
		layoutText = layoutText + modelElement.getString("name");
		layoutText = layoutText + " = product ";
		layoutText = layoutText + modelElement.getString("type");
		layoutText = layoutText + ";";
		
		return layoutText;		
	}

	// ------------------------------------------ Accessory --------------------------------------
	public void setIds(ArrayList<String> ids)
	{
		this.ids = ids;
	}
	public ArrayList<String> getIds()
	{
		return ids;
	}
	public void addId(String id)
	{
		getIds().add(id);
	}
}
