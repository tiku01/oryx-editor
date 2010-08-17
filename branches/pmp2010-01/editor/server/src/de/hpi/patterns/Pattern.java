/**
 * Copyright (c) 2010
 * 
 * Kai Höwelmeyer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 **/
package de.hpi.patterns;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * This class represents a pattern of the pattern repository on the server side.
 * @author Kai Höwelmeyer
 *
 */
public class Pattern implements Serializable, JSONString {
	/**
	 * Generated serialVersionUID
	 */
	private static final long serialVersionUID = -476899378745085991L;
	
	/** 
	 * Serialized array of oryx shapes 
	 */
	private final String serPattern; //the serialized oryx shapes of the pattern
	
	/**
	 * Unique id for each pattern of the stencilset
	 */
	private int id;
	
	/**
	 * Url of the thumbnail image of the pattern (not yet implemented)
	 */
	private final String imageUrl;
	
	/**
	 * Name of the pattern as displayed in pattern repository
	 */
	private String name;
	
	/**
	 * Repository that saves this pattern. This field is not serialized as the reference changes.
	 * 
	 */
	private transient PatternPersistanceProvider repos = null; //is not serialized
	
	/**
	 * Constructor for pattern. Usually instances are created by means of @see fromJSON(String).
	 * @param id unique id for each pattern in each stencilset
	 * @param serPattern serialized array of oryx shapes
	 * @param imageUrl url of the thumbnail image of the pattern
	 * @param name as shown in pattern repository
	 */
	public Pattern(int id, String serPattern, String imageUrl, String name) {
		
		this.id = id;		
		this.serPattern = serPattern;
		this.imageUrl = imageUrl;
		this.name = name;
	}
	
	/**
	 * Gets the serialized array of oryx shapes that represent the pattern.
	 * @return String of JSON serialization of pattern shapes
	 */
	public String getSerPattern() {
		return serPattern;
	}
	
	/**
	 * Returns the id of the pattern.
	 * @return int id of the pattern
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * simply sets the id of the pattern. Warning: Uniqueness of id is not checked!
	 * This method is intended for PatternPersistanceProviders.
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * gets the url of the thumbnail image of the pattern (not yet implemented)
	 * @return url of thumbnail
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * Returns a JSON representation of this pattern containing id, serPattern, imageUrl, and name.
	 * The representation includes the serialized pattern as well. One might consider to
	 * exclude the serPattern from the representation but this yields greater architectural
	 * ramifications. Hence serPattern is included.
	 * @returns String JSON representation as string
	 */
	@Override
	public String toJSONString() {
		return this.toJSONObject().toString();
	}

	/**
	 * Gets the name of the pattern as displayed in pattern repository.
	 * @return name of the pattern
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of the pattern.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Factory Method: Constructs new pattern instance from supplied JSON string.
	 * @param jsonString the JSON representation of the pattern
	 * @return new pattern instance with values from the JSON representations
	 * @throws JSONException if JSON is malformed or serPattern was not present
	 */
	public static Pattern fromJSON(String jsonString) throws JSONException {		
		JSONObject jsonObject = new JSONObject(jsonString);
		int id = jsonObject.optInt("id");
		String serPattern = jsonObject.getString("serPattern");
		String imageUrl = jsonObject.optString("imageUrl");
		String name = jsonObject.optString("name");
			
		return new Pattern(id, serPattern, imageUrl, name);
	}

	/**
	 * Returns the repository that saves this pattern.
	 * @return repository of this pattern
	 */
	public PatternPersistanceProvider getRepos() {
		return repos;
	}

	/**
	 * Sets the the repository of this pattern.
	 * Only to be used by @see PatternPersistanceProvider implementations
	 * 
	 * @param repos
	 */
	public void setRepos(PatternPersistanceProvider repos) {
		this.repos = repos;
	}
	
	/**
	 * Constructs a JSONObject containing id, serPattern, imageUrl, and name of the current pattern.
	 * @return JSONObject of current pattern
	 */
	public JSONObject toJSONObject() {
		JSONObject jo = new JSONObject();

		try {
			jo.put("id", this.getId());
			jo.put("serPattern", this.getSerPattern());
			jo.put("imageUrl", this.getImageUrl());
			jo.put("name", this.getName());
		} catch (JSONException e) {
			e.printStackTrace(); //can be ignored safely:
								 //JSONException only thrown if key of put is null.
		}	
		
		return jo;
	}
	
	/**
	 * Two instances of Pattern are considered equal if they feature the same id.
	 * @return boolean true if obj is a Pattern and has the same id
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) 
			return true;
		if (!(obj instanceof Pattern))
			return false;
		Pattern p = (Pattern) obj;
		return p.id == this.id;		
	}
	
	/**
	 * Simple hashCode method as proposed by Joshua Bloch in Effective Java.
	 * @return int the hashcode of this pattern.
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = result*37 + this.id;
		return result;
	}
	
	/**
	 * Returns a textual representation of the pattern in terms of id and name.
	 * @return String
	 */
	@Override
	public String toString() {
		return "Pattern id: " + this.id + " name: " + this.name;
	}
	
}
