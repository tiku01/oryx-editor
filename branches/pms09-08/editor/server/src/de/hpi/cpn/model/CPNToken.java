package de.hpi.cpn.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNToken extends XMLConvertable
{
	private String x;	
	private String y;

	public CPNToken()
	{
		String defaultX = "-10.000000";
		String defaultY = "0.000000";
		
		setX(defaultX);
		setY(defaultY);
	}
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("token", CPNToken.class);
		
		xstream.useAttributeFor(CPNToken.class, "x");
		xstream.useAttributeFor(CPNToken.class, "y");
	}

	
	// ------------------------------ Accessory ------------------------------
	
	
	public void setX(String x) {
		this.x = x;
	}
	public String getX() {
		return x;
	}

	public void setY(String _y) {
		this.y = _y;
	}
	public String getY() {
		return this.y;
	}

}
