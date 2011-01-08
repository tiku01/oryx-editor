package de.hpi.olc;

public class SyncSet {
	private String first;
	private String second;
	private String succeeder;
	
	public SyncSet(String first, String second, String succeeder) {
		this.first = first;
		this.second = second;
		this.succeeder = succeeder;
	}
	
	@Override
	public String toString() {
		return "("+first+","+second+","+succeeder+")";
	}
}
