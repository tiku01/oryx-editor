package de.hpi.yawl;

public class VariableMapping {

	private String query = "";
	private Variable mapsTo;
	
	public String getQuery(){
		return query;
	}
	
	public void setQuery(String newQuery){
		query = newQuery;
	}
	
	public Variable getMapsTo(){
		return mapsTo;
	}
	
	public void setMapsTo(Variable mapped){
		mapsTo = mapped; 
	}
	
	public String writeToYAWL(){
		String s = "";
		
		s += "\t\t\t\t\t<mapping>\n";
		s += "\t\t\t\t\t\t<expression query=\">" + getQuery() + "\" />\n";
		s += "\t\t\t\t\t\t<mapsTo>" + getMapsTo().getName() + "</mapsTo>\n";
		s += "\t\t\t\t\t</mapping>\n";
		
		return s;
	}
}
