package de.hpi.AdonisSupport;

import java.util.ArrayList;

import org.xmappr.Element;
import org.xmappr.RootElement;


//<!ELEMENT MODELATTRIBUTES ((ATTRIBUTE | RECORD)*)>


@RootElement("MODELATTRIBUTES")
public class AdonisModelAttributes extends XMLConvertible{

	
	
	@Element(name="ATTRIBUTE")//, targetType=AdonisAttribute.class)
	protected ArrayList<AdonisAttribute> attribute;
	
	@Element(name="RECORD")//, targetType=AdonisRecord.class)
	protected ArrayList<AdonisRecord> record;
	
	public ArrayList<AdonisAttribute> getAttribute(){
		if (attribute == null) {attribute = new ArrayList<AdonisAttribute>();}
		return attribute;
	}
	
	public void setAttribute(ArrayList<AdonisAttribute> list){
		attribute = list;
	}
	
	
	public ArrayList<AdonisRecord> getRecord(){
		if (record == null) {record = new ArrayList<AdonisRecord>();}
		return record;
	}
	
	public void setRecord(ArrayList<AdonisRecord> list){
		record = list;
	}
}
