package de.hpi.yawl;

public class YVariableMapping {

	private String query = "";
	private YVariable mapsTo;
	
	public YVariableMapping(String newQuery, YVariable newMapsTo){
		setQuery(newQuery);
		setMapsTo(newMapsTo);
	}
	
	public String getQuery(){
		return query;
	}
	
	public void setQuery(String newQuery){
		query = newQuery;
	}
	
	public YVariable getMapsTo(){
		return mapsTo;
	}
	
	public void setMapsTo(YVariable mapped){
		mapsTo = mapped; 
	}
	
	public String writeToYAWL(){
		String s = "";
		
		s += "\t\t\t\t\t<mapping>\n";
		s += "\t\t\t\t\t\t<expression query=\"" + getQuery() + "\" />\n";
		s += "\t\t\t\t\t\t<mapsTo>" + getMapsTo().getName() + "</mapsTo>\n";
		s += "\t\t\t\t\t</mapping>\n";
		
		return s;
	}
}
