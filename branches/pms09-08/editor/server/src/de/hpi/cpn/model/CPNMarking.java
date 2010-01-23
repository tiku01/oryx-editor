package de.hpi.cpn.model;

import com.thoughtworks.xstream.XStream;

public class CPNMarking
{	
	private String x;	
	private String y;
	private String hidden;
	
	
	public CPNMarking()
	{
		String defaultHidden = "false";
		String defaultX = "0.000000";
		String defaultY = "0.000000";
		
		setX(defaultX);
		setY(defaultY);
		setHidden(defaultHidden);
	}
	
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("token", CPNMarking.class);
		
		xstream.useAttributeFor(CPNMarking.class, "x");
		xstream.useAttributeFor(CPNMarking.class, "y");
		xstream.useAttributeFor(CPNMarking.class, "hidden");
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

	private void setHidden(String hidden) {
		this.hidden = hidden;
	}
	private String getHidden() {
		return hidden;
	}
}
