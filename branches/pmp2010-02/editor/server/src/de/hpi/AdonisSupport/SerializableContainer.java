package de.hpi.AdonisSupport;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class  SerializableContainer<Container> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected HashMap<String,String> attributes;
	protected ArrayList<Container> elements;

	
	public HashMap<String,String> getAttributes() {
		if (attributes == null){
			attributes = new HashMap<String,String>();
		}
		return attributes;
	}
	
	public ArrayList<Container> getElements() {
		if (elements == null){
			elements = new ArrayList<Container>();
		}
		return elements;
	}
	
	public void setAttributes(HashMap<String,String> unknowns) {
		attributes = unknowns;
	}
	
	public void setElements(ArrayList<Container> unknowns) {
		elements = unknowns;
	}
	
	
	
}
