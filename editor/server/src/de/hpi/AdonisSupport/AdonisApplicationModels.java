package de.hpi.AdonisSupport;
import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT APPLICATIONMODELS (APPLICATIONMODEL+)>

@RootElement("APPLICATIONMODELS")
public class AdonisApplicationModels {

	@Element(name="APPLICATIONMODEL", targetType=AdonisApplicationModel.class)
	protected ArrayList<AdonisApplicationModel> children;
	
	public ArrayList<AdonisApplicationModel> getChildren(){
		return children;
	}
	
	public void setChildren(ArrayList<AdonisApplicationModel> list){
		children = list;
	}
	

}
