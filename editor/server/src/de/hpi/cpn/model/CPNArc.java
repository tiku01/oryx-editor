package de.hpi.cpn.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;


public class CPNArc extends CPNModellingThing
{
	private transient String bendpointtag;
	
	private String orientation;
	private String order = "1";
	private CPNLittleProperty arrowattr = CPNLittleProperty.arrowattr();
	private CPNLittleProperty transend;
    private CPNLittleProperty placeend;
	private CPNProperty annot = new CPNProperty();
	
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("arc", CPNArc.class);
		
		xstream.aliasField("bendpoint", CPNArc.class, "bendpointtag");
	
		xstream.useAttributeFor(CPNArc.class, "orientation");
		xstream.useAttributeFor(CPNArc.class, "order");
	}
	
	
	// ------------------------------------------- JSON Reader --------------------------------
	
	public void readJSONproperties(JSONObject modelElement) throws JSONException
	{
		JSONObject properties = new JSONObject(modelElement.getString("properties"));
		this.parse(properties);
	}
	
	public void readJSONlabel(JSONObject modelElement) throws JSONException
	{
		String annot = modelElement.getString("label");
		
		JSONObject tempJSON = new JSONObject();
		tempJSON.put("label", annot);
		tempJSON.put("id", getId() + "1");
		
		getAnnot().parse(tempJSON);
	}
	
	public void readJSONtransend(JSONObject modelElement) throws JSONException
	{
		String transendIdref = modelElement.getString("transend");
		
		setTransend(CPNLittleProperty.transend(transendIdref));
	}
	
	public void readJSONplaceend(JSONObject modelElement) throws JSONException
	{
		String placeendIdref = modelElement.getString("placeend");
		
		setPlaceend(CPNLittleProperty.placeend(placeendIdref));
	}
	
	public void readJSONorientation(JSONObject modelElement) throws JSONException
	{
		String orientation = modelElement.getString("orientation");
		
		setOrientation(orientation);
	}
	
	
	
	public static boolean handlesStencil(String stencil)
	{		
		return stencil.equals("Arc");
	}
	
	public static CPNArc newCPNArc(CPNArc arc)
	{
		CPNArc tempArc = new CPNArc();
		
		tempArc.setAnnot(arc.getAnnot());
		tempArc.setArrowattr(arc.getArrowattr());
		tempArc.setFillattr(arc.getFillattr());
		tempArc.setId(arc.getId());
		tempArc.setLineattr(arc.getLineattr());
		tempArc.setOrder(arc.getOrder());
		tempArc.setOrientation(arc.getOrientation());
		tempArc.setPlaceend(arc.getPlaceend());
		tempArc.setPosattr(arc.getPosattr());
		tempArc.setTextattr(arc.getTextattr());
		tempArc.setTransend(arc.getTransend());
		
		return tempArc;
	}
	
	// ---------------------------------------- Accessory ------------------------------
	
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
	public String getOrientation() {
		return orientation;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	public String getOrder() {
		return order;
	}

	public void setArrowattr(CPNLittleProperty arrowattr) {
		this.arrowattr = arrowattr;
	}
	public CPNLittleProperty getArrowattr() {
		return arrowattr;
	}

	public void setTransend(CPNLittleProperty transend) {
		this.transend = transend;
	}
	public CPNLittleProperty getTransend() {
		return transend;
	}

	public void setPlaceend(CPNLittleProperty placeend) {
		this.placeend = placeend;
	}
	public CPNLittleProperty getPlaceend() {
		return placeend;
	}

	public void setAnnot(CPNProperty annot) {
		this.annot = annot;
	}
	public CPNProperty getAnnot() {
		return annot;
	}
}
