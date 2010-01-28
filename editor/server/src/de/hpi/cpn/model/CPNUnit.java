package de.hpi.cpn.model;

import com.thoughtworks.xstream.XStream;

public class CPNUnit 
{
	// ------------------------------------------ Mapping ------------------------------------------
	public static void registerMapping(XStream xstream)
	{
	   xstream.alias("unit", CPNUnit.class);
	}
	
}
