package de.hpi.AdonisSupport;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public abstract class AdonisBaseObject extends XMLConvertible{
	
	public static final int CMTOPX = 100; 
	

	
	

	static public void writeError(Exception e){
		System.err.println(e.getMessage()); 
		e.printStackTrace();
	}
	
}
