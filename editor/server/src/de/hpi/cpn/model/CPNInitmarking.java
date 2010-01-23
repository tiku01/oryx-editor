package de.hpi.cpn.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNInitmarking extends CPNModellingThing
{
	private CPNText text = new CPNText();
	
	
	public CPNInitmarking()
	{
		super();
		
		getFillattr().setPattern("Solid");
		getLineattr().setThick("0");		
	}
	
	
	// ---------------------------------------- Mapping -----------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("initmark", CPNInitmarking.class);
	}

	// -------------------------------------- JSON Reader ------------------------------------
	
	public void readJSONinitialmarking(JSONObject modelElement) throws JSONException
	{
		String initialmarking = modelElement.getString("initialmarking");
		String quantity = modelElement.getString("quantity");
		
		if (quantity.isEmpty())
			quantity = "1";
		
		getText().insertTextforToken(initialmarking, quantity);
	}
	
	public void readJSONlowerRight(JSONObject modelElement) throws JSONException
	{
		int defaultShiftX = 57;
		int defaultShiftY = 23;
		
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
