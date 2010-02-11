package org.b3mn.ViewGenerator;

class ConnectionAttributes {
	private String targetAttr;
	private String sourceAttr;
	
	public ConnectionAttributes() {

	}

	public void setTargetAttribute(String targetAttribute) {
		targetAttr = targetAttribute;
	}
	
	public void setSourceAttribute(String sourceAttribute) {
		sourceAttr = sourceAttribute;
	}
	
	public String getTargetAttribute() {
		return targetAttr;
	}
	
	public String getSourceAttribute() {
		return sourceAttr;
	}
	
	public boolean hasTargetAttribute() {
		return (targetAttr != null);
	}
	
	public boolean hasSourceAttribute() {
		return (sourceAttr != null);
	}
	
}
