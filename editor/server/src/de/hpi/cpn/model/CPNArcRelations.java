package de.hpi.cpn.model;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CPNArcRelations
{
	private Hashtable<String, String> sourceTable = new Hashtable<String, String>();
	private Hashtable<String, String> targetTable = new Hashtable<String, String>();
	
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException {
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		if (childShapes != null)
		{
			for (int i = 0; i<childShapes.length(); i++) 
			{
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				if (CPNTransition.handlesStencil(stencil))
					newSourceEntry(childShape);
				
				else if (CPNPlace.handlesStencil(stencil))
					newSourceEntry(childShape);
				
				else if (CPNArc.handlesStencil(stencil))
					newTargetEntry(childShape);
			}
		}
	}


	
	private void newSourceEntry(JSONObject childShape) throws JSONException
	{
		String childShapeResourceId = childShape.getString("resourceId");
		
		JSONArray outgoing = childShape.optJSONArray("outgoing");
		
		if (outgoing != null)
		{
			for (int i = 0; i < outgoing.length(); i++) 
			{
				JSONObject outgoingNode = outgoing.getJSONObject(i);
				String outgoingNodeResourceId = outgoingNode.getString("resourceId");
				
				getSourceTable().put(outgoingNodeResourceId, childShapeResourceId);
			}
		}		
	}
	
	private void newTargetEntry(JSONObject childShape) throws JSONException
	{
		String arcResourceId = childShape.getString("resourceId");		
		String targetResourceId = childShape.getJSONObject("target").getString("resourceId");
		
		getTargetTable().put(arcResourceId, targetResourceId);
	}
	
	public void changePlaceId(String oldId, String newId)
	{
		changeIdvalue(getSourceTable(), oldId, newId);
		changeIdvalue(getTargetTable(), oldId, newId);
	}
	
	public void changeTransitionId(String oldId, String newId)
	{		
		changeIdvalue(getSourceTable(), oldId, newId);
		changeIdvalue(getTargetTable(), oldId, newId);
	}
	
	private void changeIdvalue(Hashtable<String, String> hashtable, String oldId, String newId)
	{
		Enumeration<String> tempEnumeration = hashtable.keys();
		
		while (tempEnumeration.hasMoreElements())
		{
			String key = tempEnumeration.nextElement();
			String value = (String) hashtable.get(key);
			
			// if value and oldId are the same then the new should be put into the dictionary
			if (value.equals(oldId))
				hashtable.put(key, newId);
			
		}
	}
	
	public String getTargetValue(String resourceId)
	{
		return (String) getTargetTable().get(resourceId);
	}
	
	public String getSourceValue(String resourceId)
	{
		return (String) getSourceTable().get(resourceId);
	}

	
	// ---------------------------------------- Accessory -----------------------------------

	public void setSourceTable(Hashtable<String, String> sourceTable)
	{
		this.sourceTable = sourceTable;
	}
	public Hashtable<String, String> getSourceTable() 
	{
		return sourceTable;
	}

	public void setTargetTable(Hashtable<String, String> targetTable)
	{
		this.targetTable = targetTable;
	}
	public Hashtable<String, String> getTargetTable()
	{
		return targetTable;
	}
}
