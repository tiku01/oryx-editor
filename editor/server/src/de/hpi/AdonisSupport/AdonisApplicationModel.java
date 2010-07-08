package de.hpi.AdonisSupport;
import java.util.ArrayList;
import java.util.Map;

import org.xmappr.Attribute;
import org.xmappr.Attributes;
import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT APPLICATIONMODEL (MODELREFERENCE+)>
//<!ATTLIST APPLICATIONMODEL
//  name   CDATA #REQUIRED
//  applib CDATA #REQUIRED
//>

@RootElement("APPLICATIONMODEL")
public class AdonisApplicationModel {

	@Attributes({
		@Attribute("name"),
		@Attribute("applib")
	})
	protected Map<String,String> attributes;
	
	@Element(name="MODELREFERENCE", targetType=AdonisModelReference.class)
	protected ArrayList<AdonisModelReference> children;
	
	public Map<String,String> getAttributes(){
		return attributes;
	}
	
	public void setAttributes(Map<String,String> map){
		attributes = map;
	}
	
	public ArrayList<AdonisModelReference> getChildren(){
		return children;
	}
	
	public void setChildren(ArrayList<AdonisModelReference> list){
		children = list;
	}
	


}
