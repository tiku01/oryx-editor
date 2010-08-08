package de.hpi.pattern;

import java.io.Serializable;

import org.json.JSONString;

public class Pattern implements Serializable, JSONString {
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -476899378745085991L;
	
	private final String serPattern;
	private final int id;
	private final String imageUrl;
	private String description;
	
	public Pattern(int id, String serPattern, String imageUrl, String description) {
		this.id = id;
		this.serPattern = serPattern;
		this.imageUrl = imageUrl;
		this.description = description;
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

	@Override
	public String toJSONString() {
		return 	"{\"id\": " + this.getId() + ", " +
				"\"serPattern\": \"" + this.getSerPattern() + "\", " +
				"\"imageUrl\": \"" + this.getImageUrl() + "\", " +
				"\"description\": \"" + this.getDescription() + "\"}";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
