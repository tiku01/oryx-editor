package de.hpi.yawl;

import java.util.Locale;

public class YMultiInstanceParam {
	
	public enum CreationMode {
		DYNAMIC, STATIC
	}
	
	private int minimum = 0;
	private int maximum = 0;
	private int threshold = 0;
	private CreationMode creationMode = CreationMode.STATIC;
	private YMIDataInput miDataInput = null;
	private YMIDataOutput miDataOutput = null;
	
	public int getMinimum() {
		return minimum;
	}
	
	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}
	
	public int getMaximum() {
		return maximum;
	}
	
	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}
	
	public int getThreshold() {
		return threshold;
	}
	
	public void setThreshold(int threshold) {
		this.threshold = threshold;
	}
	
	public CreationMode getCreationMode() {
		return creationMode;
	}
	
	public void setCreationMode(CreationMode creationMode) {
		this.creationMode = creationMode;
	}
	
	public void setMiDataInput(YMIDataInput miDataInput) {
		this.miDataInput = miDataInput;
	}

	public YMIDataInput getMiDataInput() {
		return miDataInput;
	}

	public void setMiDataOutput(YMIDataOutput miDataOutput) {
		this.miDataOutput = miDataOutput;
	}

	public YMIDataOutput getMiDataOutput() {
		return miDataOutput;
	}

	public String writeToYAWL(){
		String s = "";
		
		s += "\t\t\t\t\t<minimum>" + getMinimum() + "</minimum>\n";
        s += "\t\t\t\t\t<maximum>" + getMaximum() + "</maximum>\n";
        s += "\t\t\t\t\t<threshold>" + getThreshold() + "</threshold>\n";            
        s += "\t\t\t\t\t<creationMode code=\"" + getCreationMode().toString().toLowerCase(Locale.ENGLISH) + "\" />\n";
        
        s += getMiDataInput().writeToYAWL();
        s += getMiDataOutput().writeToYAWL();
        
        return s;
	}
}
