package de.hpi.cpn.model;

import com.thoughtworks.xstream.XStream;

public class CPNVarType 
{	
	// Beispiel
	//  <type>
	//  <id>int</id>
	//</type>
	
	private String id;
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("type", CPNVarType.class);
	}

	// ------------------------------ Accessor ------------------------------------------
	
	public void setId(String _id)
	{
		this.id = _id;
	}
	
	public String getId()
	{
		return this.id;
	}
}
