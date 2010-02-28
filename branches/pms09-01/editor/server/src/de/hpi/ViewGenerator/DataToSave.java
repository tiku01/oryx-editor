/***************************************
 * Copyright (c) 2010 
 * Martin Krüger
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
****************************************/

package de.hpi.ViewGenerator;
import java.util.ArrayList;

public class DataToSave {
//	a DataToSave instance holds for one stencilId (of an Connector) all the data that has to be saved
	private ArrayList<String> attributesToSave;
	private ArrayList<String> stencilLevelsToSave;
	private ArrayList<String> jsonIdsToSave;
	
//	constructor for DataToSave objects where the initial attributes saveOrigin is an JSONObject
	public DataToSave(String attributeToSave, String jsonIdWithAttributeToSave, String stencilLevelToSave) {
		this.attributesToSave = new ArrayList<String>();
		this.attributesToSave.add(attributeToSave);
		this.stencilLevelsToSave = new ArrayList<String>();
		this.stencilLevelsToSave.add(stencilLevelToSave);
		this.jsonIdsToSave = new ArrayList<String>();
		this.jsonIdsToSave.add(jsonIdWithAttributeToSave);
	}
	
//	constructor for DataToSave objects where the initial attributes saveOrigin is a String
	public DataToSave(String attributeToSave, String stencilLevelToSave) {
		this.attributesToSave = new ArrayList<String>();
		this.attributesToSave.add(attributeToSave);
		this.stencilLevelsToSave = new ArrayList<String>();
		this.stencilLevelsToSave.add(stencilLevelToSave);
		this.jsonIdsToSave = new ArrayList<String>();
		this.jsonIdsToSave.add(null);
	}
	
//	add method for attributes objects where the saveOrigin is an JSONObject
	public void addDataToSave(String attributeToSave, String jsonIdWithAttributeToSave, String stencilLevelToSave) {
		attributesToSave.add(attributeToSave);
		stencilLevelsToSave.add(stencilLevelToSave);
		jsonIdsToSave.add(jsonIdWithAttributeToSave);
	}
	
//	add Method for attributes objects where the saveOrigin is a String
	public void addDataToSave(String attributeToSave, String stencilLevelToSave) {
		attributesToSave.add(attributeToSave);
		stencilLevelsToSave.add(stencilLevelToSave);
		jsonIdsToSave.add(null);
	}
	
	public ArrayList<String> attributesToSave() {
		return attributesToSave;
	}
	
	public ArrayList<String> stencilLevelsToSave() {
		return stencilLevelsToSave;
	}
	
	public ArrayList<String> jsonIdsToSave() {
		return jsonIdsToSave;
	}
	
	public String getAttributeToSave(int index) {
		return attributesToSave.get(index);
	}
	
	public String getStencilLevelToSave(int index) {
		return stencilLevelsToSave.get(index);
	}
	
	public String getJSONIdWithAttributeToSave(int index) {
		return jsonIdsToSave.get(index);
	}
		
	public int size() {
		return attributesToSave.size();
	}
}
