package de.hpi.cpn.model;

import com.thoughtworks.xstream.XStream;

public class CPNLittelForm 
{
	
	private String w;	
	private String h;

	
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream) {
		xstream.alias("ellipse", CPNLittelForm.class);
		
		xstream.useAttributeFor(CPNLittelForm.class, "w");
		xstream.useAttributeFor(CPNLittelForm.class, "h");
	}

	
	public void setEllipseTag()
	{
		
		String defaultW = "60.000000";
        String defaultH = "40.000000";
		
        setW(defaultW);
		setH(defaultH);
	}
	
	public void setBoxTag()
	{
		
		String defaultW = "60.000000";
        String defaultH = "40.000000";
		
        setW(defaultW);
		setH(defaultH);
	}
	

	// ------------------------------ Accessory ------------------------------
	
	
	public void setW(String x) {
		this.w = x;
	}
	public String getW() {
		return w;
	}

	public void setH(String _h) {
		this.h = _h;
	}
	public String getH() {
		return this.h;
	}

}
