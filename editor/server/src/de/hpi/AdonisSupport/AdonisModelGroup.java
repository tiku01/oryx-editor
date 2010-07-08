package de.hpi.AdonisSupport;
import java.util.ArrayList;
import java.util.Map;

import org.xmappr.Attribute;
import org.xmappr.Attributes;
import org.xmappr.Element;
import org.xmappr.Elements;
import org.xmappr.RootElement;

//<!ELEMENT MODELGROUP (MODELREFERENCE*, MODELGROUP*)>
//<!ATTLIST MODELGROUP
//  name CDATA #REQUIRED
//>


@RootElement("MODELGROUP")
public class AdonisModelGroup {

	@Attributes({
		@Attribute("name")
	})
	protected Map<String,String> attributes;
	
	
	
	@Elements({
		@Element(name="MODELGROUP", targetType=AdonisModelGroup.class),
		@Element(name="MODELREFERENCE", targetType=AdonisModelReference.class)
	})
	protected ArrayList<?> children;
	
	public Map<String,String> getAttributes(){
		return attributes;
	}
	public void setAttributes(Map<String,String> map){
		attributes = map;
	}
	
	public ArrayList<?> getChildren(){
		return children;
	}
	public void setChildren(ArrayList<?> list){
		children = list;
	}
	

}
