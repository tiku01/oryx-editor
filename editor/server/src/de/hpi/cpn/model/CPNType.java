package de.hpi.cpn.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNType extends CPNModellingThing
{
	private CPNText text = new CPNText();
	
	
	public CPNType()
	{
		super();
		
		getFillattr().setPattern("Solid");
		getLineattr().setThick("0");		
	}
	
	
	// ---------------------------------------- Mapping -----------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("type", CPNType.class);
	}

	// -------------------------------------- JSON Reader ------------------------------------
	
	public void readJSONcolordefinition(JSONObject modelElement) throws JSONException
	{
		String colorsettype = modelElement.getString("colordefinition");
		
		getText().setText(colorsettype);
	}
	
	public void readJSONlowerRight(JSONObject modelElement) throws JSONException
	{
		int defaultShiftX = 43;
		int defaultShiftY = -23;
		
		String lowerRight = modelElement.getString("lowerRight");		
		
		JSONObject lowerRightJSON = new JSONObject(lowerRight);
		
		int x = Integer.parseInt(lowerRightJSON.getString("x")) + defaultShiftX;
		int y = Integer.parseInt(lowerRightJSON.getString("y")) + defaultShiftY;
		
		getPosattr().setX("" + x + ".000000");
		getPosattr().setY("" + y + ".000000");		
	}
	
	// ---------------------------------------- Accessory -----------------------------------
	
	public void setText(CPNText text) 
	{
		this.text = text;
	}

	public CPNText getText() 
	{
		return text;
	}
	
}
