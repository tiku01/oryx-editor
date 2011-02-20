package de.hpi.olc;

public class Constants {
	public static final String arcState = "i";
	public static final String arcNextState = "l";
	public static final String arcStateList = "a";
	public static final String arcSync = "(j,k,l)";
	public static final String guardEnter = "[contains(i,a)]";
	public static final String guardContinue = "[not(contains(i,a))]";
	public static final String guardFirst = "[i=j]";
	public static final String guardSecond = "[i=k]";
}
