package de.hpi.AdonisSupport;

import org.xmappr.Attribute;

//<!ELEMENT FROM EMPTY>
//<!ATTLIST FROM
//  class    CDATA #REQUIRED
//  instance CDATA #REQUIRED
//>

//<!ELEMENT TO EMPTY>
//<!ATTLIST TO
//class    CDATA #REQUIRED
//instance CDATA #REQUIRED
//>

public class AdonisConnectionPoint {

	@Attribute(name="instance")
	protected String instance;
	
	@Attribute(name="class")
	protected String stencilClass;

	
	public String getInstance(){
		return instance;
	}
	
	public void setInstance(String value){
		instance = value;
	}
	
	public String getStencilClass(){
		return stencilClass;
	}
	
	public void setStencilClass(String value){
		stencilClass = value;
	}

}
