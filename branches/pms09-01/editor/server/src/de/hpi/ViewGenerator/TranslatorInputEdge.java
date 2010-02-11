package de.hpi.ViewGenerator;
import java.util.HashMap;
import java.util.Set;

public class TranslatorInputEdge {

	private String sourceId;
	private String targetId;
	private HashMap<String,String> attributes;

	public TranslatorInputEdge(String sourceNodeId, String targetNodeId) {
		sourceId = sourceNodeId;
		targetId = targetNodeId;
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
	
	public String getSourceNodeId() {
		return sourceId;
	}
	
	public String getTargetNodeId() {
		return targetId;
	}
}
