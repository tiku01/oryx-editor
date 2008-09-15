/***************************************
 * Copyright (c) 2008
 * Bjoern Wagner
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

package org.b3mn.poem.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.b3mn.poem.Identity;
import org.b3mn.poem.Persistance;
import org.b3mn.poem.TagRelation;
import org.b3mn.poem.business.Model;
import org.b3mn.poem.business.User;
import org.b3mn.poem.util.FilterMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class FilterHandler extends HandlerBase {
	
	private class InstanceMethodRecord {
		public Object instance;
		public Method method;
	}
	
	private Map<String, InstanceMethodRecord> mapping = new HashMap<String, InstanceMethodRecord>();
	
	@Override
	public void init() {
		registerFilters(this);
	}
	
	public void registerFilters(Object o) {
		for (Method method : o.getClass().getMethods()) {
			// Check if the method is annotated with the FilterMethod annotation,
			// returns an integer array and has 3 arguments
			if ((method.getAnnotation(FilterMethod.class) != null) && 
					(method.getReturnType().equals(Collection.class)) && 
					(method.getParameterTypes().length == 3)) {
				// Check: 1st parameter: int, 2nd Collection<Integer> and 3rd String
				if ((method.getParameterTypes()[0].equals(int.class)) && 
						(method.getParameterTypes()[1].equals(Collection.class)) &&
						(method.getGenericParameterTypes()[2].equals(String.class))) {
					// Add record to the filter mapping
					InstanceMethodRecord record = new InstanceMethodRecord();
					record.instance = o;
					record.method = method;
					this.mapping.put(method.getName().toLowerCase(), record);
				}
			}
		}
	}
	
	
	/* This handler returns a json encoded array of model ids that 
	 * 
	 * 
	 */
	@Override
    public void doPost(HttpServletRequest request, HttpServletResponse response, Identity subject, Identity object) throws Exception {
		
		String filterName = request.getParameter("filterName");
		String modelIdsStr = request.getParameter("modelIds"); // Array of model ids as JSON array
		String params = request.getParameter("params");
		
		if ((filterName != null) && (modelIdsStr != null)) {
			filterName.toLowerCase(); // filter names are case insensitive
			// Check if the mapping exists
			if (this.mapping.get(filterName) != null) {
				JSONArray inputIds = new JSONArray(modelIdsStr); // Decode string to JSON
				// Add all model ids that was sent by the client to a collection
				Collection<Integer> modelIdsCollection = new ArrayList<Integer>();				
				for (int i = 0; i < inputIds.length(); i++) {
					modelIdsCollection.add(inputIds.getInt(i));
				}
				// Get instance and method from mapping
				InstanceMethodRecord record = this.mapping.get(filterName); 
				// Create array with model id and params parameter
				Object[] paramArray = { subject.getId(), modelIdsCollection, params }; 
				// Invoke the mapped method	and encode the collection to an JSONArray
				JSONArray outputIds = new JSONArray(
						(Collection<Integer>)record.method.invoke(record.instance, paramArray));
				outputIds.write(response.getWriter()); // Write JSON to http response
				response.setStatus(200);
				
			}
			// TODO: add some error handling. may return error message
		}
	}
	
	
	/* Returns all modelIds of the input which are tagged with the tags passed in the params 
	 * parameter. params must be an JSON array of tags
	 */
	
	@FilterMethod
	public Collection<Integer> tagFilter(int subjectId, Collection<Integer> modelIds, String params) throws Exception {
		
		JSONArray jsonArray = new JSONArray(params);
		String sqlTagQuery = "";
		for (int i = 0; i < jsonArray.length() - 1; i++) {
			try {
				sqlTagQuery += " tag_definition.name='" + jsonArray.getString(i) + "' OR ";
			} catch (JSONException e) {}
		}
		try {
			sqlTagQuery += " tag_definition.name='" + jsonArray.getString(jsonArray.length() - 1) + "' ";
		} catch (JSONException e) {}
			
		// Access database directly to minimize performance impact
		List<?> databaseIds = Persistance.getSession()
				.createSQLQuery("SELECT tag_relation.object_id "
				+ "FROM tag_relation, tag_definition, access "
				+ "WHERE tag_relation.tag_id=tag_definition.id "
				+ "AND access.subject_id=:subject_id "
				+ "AND tag_relation.object_id=access.object_id " 
				+ "AND(" + sqlTagQuery + ")")
				//.addEntity("tag_relation", TagRelation.class)
				.setInteger("subject_id", subjectId) 
				.list();
		
		Persistance.commit();
		ArrayList<Integer> outputIds = new ArrayList<Integer>();
		for (Integer modelId : modelIds) {
			if (databaseIds.contains(modelId)) {
				outputIds.add(modelId);
			}
		}
		
		return outputIds;
	}
}
