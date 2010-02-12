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

	private ArrayList<String> attrToSave;
	private ArrayList<String> lvlsToSave;
	private ArrayList<String> jsonToSave;
	
//	constructor for DataToSave objects where the initial attributes saveOrigin is an JSONObject
	public DataToSave(String attributeToSave, String jsonIdWithAttributeToSave, String stencilLevelToSave) {
		attrToSave = new ArrayList<String>();
		attrToSave.add(attributeToSave);
		lvlsToSave = new ArrayList<String>();
		lvlsToSave.add(stencilLevelToSave);
		jsonToSave = new ArrayList<String>();
		jsonToSave.add(jsonIdWithAttributeToSave);
	}
	
//	constructor for DataToSave objects where the initial attributes saveOrigin is a String
	public DataToSave(String attributeToSave, String stencilLevelToSave) {
		attrToSave = new ArrayList<String>();
		attrToSave.add(attributeToSave);
		lvlsToSave = new ArrayList<String>();
		lvlsToSave.add(stencilLevelToSave);
		jsonToSave = new ArrayList<String>();
		jsonToSave.add(null);
	}
	
//	add method for attributes objects where the saveOrigin is an JSONObject
	public void addDataToSave(String attributeToSave, String jsonIdWithAttributeToSave, String stencilLevelToSave) {
		attrToSave.add(attributeToSave);
		lvlsToSave.add(stencilLevelToSave);
		jsonToSave.add(jsonIdWithAttributeToSave);
	}
	
//	add Method for attributes objects where the saveOrigin is a String
	public void addDataToSave(String attributeToSave, String stencilLevelToSave) {
		attrToSave.add(attributeToSave);
		lvlsToSave.add(stencilLevelToSave);
		jsonToSave.add(null);
	}
	
	public ArrayList<String> attributesToSave() {
		return attrToSave;
	}
	
	public ArrayList<String> stencilLevelsToSave() {
		return lvlsToSave;
	}
	
	public ArrayList<String> jsonIdsToSave() {
		return jsonToSave;
	}
	
	public String getAttributeToSave(int index) {
		return attrToSave.get(index);
	}
	
	public String getStencilLevelToSave(int index) {
		return lvlsToSave.get(index);
	}
	
	public String getJSONIdWithAttributeToSave(int index) {
		return jsonToSave.get(index);
	}
		
	public int size() {
		return attrToSave.size();
	}
}
