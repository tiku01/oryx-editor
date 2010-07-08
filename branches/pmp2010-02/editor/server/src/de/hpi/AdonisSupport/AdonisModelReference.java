package de.hpi.AdonisSupport;
import java.util.Map;

import org.xmappr.Attribute;
import org.xmappr.Attributes;
import org.xmappr.RootElement;

//<!ELEMENT MODELREFERENCE EMPTY>
//<!ATTLIST MODELREFERENCE
//  name      CDATA #REQUIRED
//  version   CDATA #REQUIRED
//  modeltype CDATA #REQUIRED
//  libtype   CDATA #REQUIRED
//>

@RootElement("MODELREFERENCE")
public class AdonisModelReference {

	@Attributes({
		@Attribute("name"),
		@Attribute("version"),
		@Attribute("modeltype"),
		@Attribute("libtype")
	})
	protected Map<String,String> attributes;
	
	public Map<String,String> getAttributes(){
		return attributes;
	}
	public void setAttributes(Map<String,String> map){
		attributes = map;
	}
	

}
