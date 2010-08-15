package de.hpi.pattern;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

public class Pattern implements Serializable, JSONString {
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -476899378745085991L;
	public final static int NEWID = 0; //the id used for new patterns
	private final String serPattern;
	private int id;
	private final String imageUrl;
	private String description;
	private transient PatternPersistanceProvider repos = null; //is not serialized
	
	
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
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}

	@Override
	public String toJSONString() {
		return this.toJSONObject().toString();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public static Pattern fromJSON(String jsonString) throws JSONException {		
		JSONObject jsonObject = new JSONObject(jsonString);
		int id = jsonObject.optInt("id", Pattern.NEWID);
		String serPattern = jsonObject.getString("serPattern");
		String imageUrl = jsonObject.optString("imageUrl");
		String description = jsonObject.optString("description");
			
		return new Pattern(id, serPattern, imageUrl, description);
	}

	public PatternPersistanceProvider getRepos() {
		return repos;
	}

	public void setRepos(PatternPersistanceProvider repos) {
		this.repos = repos;
	}
	
	public JSONObject toJSONObject() {
		JSONObject jo = new JSONObject();

		try {
			jo.put("id", this.getId());
			jo.put("serPattern", this.getSerPattern());
			jo.put("imageUrl", this.getImageUrl());
			jo.put("description", this.getDescription());
		} catch (JSONException e) {
			e.printStackTrace(); //TODO can be ignored safely:
								// JSONException only thrown if key is null.
		}	
		
		return jo;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) 
			return true;
		if (!(obj instanceof Pattern))
			return false;
		Pattern p = (Pattern) obj;
		return p.id == this.id;		
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = result*37 + this.id;
		return result;
	}
	
	@Override
	public String toString() {
		return "Pattern id: " + this.id + " description: " + this.description;
	}
	
}
