package de.hpi.pattern;

import java.io.IOException;
import java.io.ObjectStreamException;
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
	private static int runningId = NEWID;
	
	
	private final String serPattern;
	private final int id;
	private final String imageUrl;
	private String description;
	private transient PatternPersistanceProvider repos = null; //is not serialized
	private transient boolean isNew;
	
	
	private static int generateId() {
		return ++Pattern.runningId == Pattern.NEWID ? ++Pattern.runningId : Pattern.runningId;
	}
	
	public Pattern(int id, String serPattern, String imageUrl, String description) {
		
		if (id == Pattern.NEWID) {
			this.id = Pattern.generateId();
			this.isNew = true;
		} else {
			this.id = id;
			this.isNew = false;
		}
		
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
		return this.toJSONObject().toString();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public static Pattern fromJSON(String jsonString) {
		Pattern p = null;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			int id = jsonObject.optInt("id", Pattern.NEWID);
			String serPattern = jsonObject.getString("serPattern");
			String imageUrl = jsonObject.optString("imageUrl");
			String description = jsonObject.optString("description");
			
			p = new Pattern(id, serPattern, imageUrl, description);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return p;
	}

	public boolean isNew() {
		return isNew;  //TODO do i have to change the id to Integer? What happens with json undefined??
	}

	public PatternPersistanceProvider getRepos() {
		return repos;
	}

	public void setRepos(PatternPersistanceProvider repos) {
		this.repos = repos;
		this.isNew = false;
	}
	
	public JSONObject toJSONObject() {
		JSONObject jo = new JSONObject();
		
		try {
			jo.put("id", this.getId());
			jo.put("serPattern", this.getSerPattern());
			jo.put("imageUrl", this.getImageUrl());
			jo.put("description", this.getDescription());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jo;
	}
	
}
