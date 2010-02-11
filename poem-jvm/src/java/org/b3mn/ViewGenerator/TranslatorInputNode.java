package org.b3mn.ViewGenerator;
import java.util.HashMap;
import java.util.Set;



public class TranslatorInputNode {
	
	private String id;
	private HashMap<String,String> attributes;

	
	public TranslatorInputNode(String NodeId) {
		id = NodeId;
		attributes = new HashMap<String,String>();
	}

	public String getAttribute(String attribute) {
		return attributes.get(attribute);
	}
	
	public void setAttribute(String attribute, String value) {
		attributes.put(attribute, value);
	}
	
	public boolean hasAttribute(String attribute) {
		return attributes.containsKey(attribute);
	}
	
	public Set<String> attributes() {
		return attributes.keySet();
	}
	
	public String getNodeId() {
		return id;
	}
}
