package de.hpi.olc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.hpi.olc.CPNGenerator.ColorSet;


public class CPNDeclarations {
	private static String CS = "Colorset";
	private static String VAR = "Variable";
	private static String state = ColorSet.State.toString();
	private static String stateList = ColorSet.StateList.toString();
	private static String syncState = ColorSet.SyncState.toString();
	private static JSONArray items;
	
	public static String getDeclarations() {
		JSONObject declarations = new JSONObject();
		items = new JSONArray();
		try {
			declarations.put("totalCount", 8);
			declarations.put("items", items);

			addColorSet(state, "string");
			addColorSet(stateList, "list " + state);
			addColorSet(syncState, state + " * " + state + " * " + state);

			addVariable("i", state);
			addVariable("j", state);
			addVariable("k", state);
			addVariable("l", state);
			addVariable("a", stateList);
			
		} catch (JSONException e) {
		}
		return declarations.toString();
	}
	
	private static void addVariable(String name, String type) throws JSONException {
		JSONObject var = new JSONObject();
		var.put("name", name);
		var.put("type", type);
		var.put("declarationtype", VAR);
		items.put(var);
	}
	
	private static void addColorSet(String name, String type) throws JSONException {
		JSONObject colorSet = new JSONObject();
		colorSet.put("name", name);
		colorSet.put("type", type);
		colorSet.put("declarationtype", CS);
		items.put(colorSet);
	}
}
