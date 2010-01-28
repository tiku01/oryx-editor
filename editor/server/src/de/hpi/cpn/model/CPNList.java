package de.hpi.cpn.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNList
{
	private String id;
	
	// ------------------------------------------ Mapping ------------------------------------------
	public static void registerMapping(XStream xstream)
	{
	   xstream.alias("list", CPNList.class);
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
	public void setIds(String id)
	{
		this.id = id;
	}
	public String getIds()
	{
		return this.id;
	}

}
