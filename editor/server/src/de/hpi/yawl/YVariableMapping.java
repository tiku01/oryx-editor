package de.hpi.yawl;

public class YVariableMapping implements FileWritingForYAWL {

	private String query = "";
	private YVariable mapsTo = null;
	
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
		s += String.format("\t\t\t\t\t\t<expression query=\"%s\" />\n", getQuery());
		s += String.format("\t\t\t\t\t\t<mapsTo>%s</mapsTo>\n", getMapsTo().getName());
		s += "\t\t\t\t\t</mapping>\n";
		
		return s;
	}
}
