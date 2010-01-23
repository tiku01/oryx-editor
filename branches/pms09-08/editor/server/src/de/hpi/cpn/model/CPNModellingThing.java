package de.hpi.cpn.model;

import com.thoughtworks.xstream.XStream;


public class CPNModellingThing extends XMLConvertable
{
	private String id;
	private CPNPosattr posattr = new CPNPosattr();
	private CPNFillattr fillattr = new CPNFillattr();
	private CPNLineattr lineattr = new CPNLineattr();
	private CPNTextattr textattr = new CPNTextattr();
	
	
	public CPNModellingThing()
	{
		getFillattr().setdefaultCPNFillattr();
		getLineattr().setdefaultCPNLineattr();
		getTextattr().setdefaultCPNTextattr();
	}
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("ModellingThing", CPNModellingThing.class);
		
		xstream.useAttributeFor(CPNModellingThing.class, "id");
		
		CPNPosattr.registerMapping(xstream);
		CPNFillattr.registerMapping(xstream);
		CPNTextattr.registerMapping(xstream);
		CPNLineattr.registerMapping(xstream);	
	}
	
	// ---------------------------------------- Accessor ----------------------------------------
	
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	
	public void setPosattr(CPNPosattr _posattr)
	{
		this.posattr = _posattr;
	}
	public CPNPosattr getPosattr() {
		return posattr;
	}
	
	public void setFillattr(CPNFillattr _fillattr) {
		this.fillattr = _fillattr;
	}
	public CPNFillattr getFillattr() {
		return fillattr;
	}
	
	public void setLineattr(CPNLineattr _lineattr) {
		this.lineattr = _lineattr;
	}
	public CPNLineattr getLineattr() {
		return lineattr;
	}
	
	public void setTextattr(CPNTextattr _textattr) {
		this.textattr = _textattr;
	}
	public CPNTextattr getTextattr() {
		return textattr;
	}		
}
