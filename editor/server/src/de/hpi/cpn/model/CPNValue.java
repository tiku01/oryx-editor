package de.hpi.cpn.model;

public class CPNValue
{
	private String bool;
	private String text;

	public void setBool(String _bool) {
		this.bool = _bool;
	}

	public String getBool() {
		return this.bool;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	public CPNValue(String _value, boolean boolortext) // if true then initialize bool otherwise "text"
	{
		if (boolortext)
			setBool(_value);
		else
			setText(_value);
	}
}
