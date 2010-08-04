package de.hpi.pattern;

import java.io.Serializable;

public class Pattern implements Serializable{
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -476899378745085991L;
	
	private final String serPattern;
	private final int id;
	private final String imageUrl;
	
	public Pattern(int id, String serPattern, String imageUrl) {
		this.id = id;
		this.serPattern = serPattern;
		this.imageUrl = imageUrl;
	}
	
	public String getSerPattern() {
		return serPattern;
	}
	public int getId() {
		return id;
	}
	public String getImageUrl() {
		return imageUrl;
	}
}
