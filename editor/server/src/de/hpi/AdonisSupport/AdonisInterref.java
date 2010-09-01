package de.hpi.AdonisSupport;

import java.util.ArrayList;

import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

//<!ELEMENT INTERREF (IREF*)>
//<!ATTLIST INTERREF
//  name CDATA #REQUIRED
//>


@RootElement("INTERREF")
public class AdonisInterref extends XMLConvertible{

	private static final long serialVersionUID = 1673749394819513296L;

	@Attribute("name")
	protected String name;
	
	@Element(name="IREF", targetType=AdonisIref.class)
	protected ArrayList<AdonisIref> iref;

	
	public String getName(){
		return name;
	}
	
	public void setName(String value){
		name = value;
	}
	
	public ArrayList<AdonisIref> getIref(){
		return iref;
	}
	
	public void setIref(ArrayList<AdonisIref> list){
		iref = list;
	}
	
	public static AdonisInterref createInterref(String name,String language){
		AdonisInterref interref = new AdonisInterref();
		interref.setName(Configurator.getAdonisIdentifier(name,language));
		interref.setIref(new ArrayList<AdonisIref>());
		return interref;
	}	
}
