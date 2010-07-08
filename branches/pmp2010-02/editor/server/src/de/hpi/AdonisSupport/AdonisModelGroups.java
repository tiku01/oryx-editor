package de.hpi.AdonisSupport;
import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.Elements;
import org.xmappr.RootElement;

//<!ELEMENT MODELGROUPS (MODELGROUP+)>

@RootElement("MODELGROUPS")
public class AdonisModelGroups {

	@Elements({
		@Element(name="MODELGROUP", targetType=AdonisModelGroup.class)
	})
	protected ArrayList<AdonisModelGroup> children;
	
	public ArrayList<AdonisModelGroup> getChildren(){
		return children;
	}
	public void setChildren(ArrayList<AdonisModelGroup> list){
		children = list;
	}
	
}
