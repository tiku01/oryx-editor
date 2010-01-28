package de.hpi.cpn.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.oryxeditor.server.diagram.*;

public class CPNDiagram 
{
	public static int[] getMaxBounds(CPNPage tempPage)
	{
					// x y
		int[] array = {0,0};
		
		ArrayList<CPNPlace> places = tempPage.getPlaces();
		ArrayList<CPNTransition> transitions = tempPage.getTransitions();
		
		for (int i = 0; i < places.size(); i++)
		{
			CPNPlace tempPlace = places.get(i);
			
			if (tempPlace != null)
			{
				int X = (int) Double.parseDouble(tempPlace.getPosattr().getX());
				int Y = (int) Double.parseDouble(tempPlace.getPosattr().getY());
				
				if (X < array[0])
					array[0] = X;
				if (Y > array[1])
					array[1] = Y;
			}
		}
		
		for (int i = 0; i < transitions.size(); i++)
		{
			CPNTransition tempTransition = transitions.get(i);
			
			if (tempTransition != null)
			{
				int X = (int) Double.parseDouble(tempTransition.getPosattr().getX());
				int Y = (int) Double.parseDouble(tempTransition.getPosattr().getY());
				
				if (X < array[0])
					array[0] = X;
				if (Y > array[1])
					array[1] = Y;
			}
		}
		
		return array;
	}
	
	public static void setDiagramBounds(Diagram diagram, int[] boundsArray)
	{
		Point UpperLeft = new Point(0.0, 0.0);
		Point LowerRight = new Point(1485.0 + boundsArray[0], 1050.0 + boundsArray[1]); 
		
		diagram.setBounds(new Bounds(LowerRight, UpperLeft));				
	}
	
	public static Diagram newColoredPetriNetDiagram()
	{
		String resourceId = "oryx-canvas123";
		
		StencilType type = new StencilType("Diagram");
		
		String stencilSetNs = "http://b3mn.org/stencilset/coloredpetrinet#";
		// vielleicht mit der root aufpassen die root ist hier "/oryx/"
		String url ="/oryx/stencilsets/coloredpetrinets/coloredpetrinet.json";
		StencilSet stencilSet = new StencilSet(url, stencilSetNs);
		
		Diagram diagram = new Diagram(resourceId, type, stencilSet);
		
		diagram.setProperties(getDiagramProperties());
		
		return diagram;
	}
	
	private static HashMap<String, String> getDiagramProperties()
	{
		HashMap<String, String> propertyMap = new HashMap<String, String>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date today = new Date();
		
		propertyMap.put("title","");
		propertyMap.put("engine","false");
		propertyMap.put("version","");
		propertyMap.put("author","");
		propertyMap.put("language","English");
		propertyMap.put("creationdate",sdf.format(today).toString());
		propertyMap.put("modificationdate",sdf.format(today).toString());
		propertyMap.put("documentation","");
		propertyMap.put("declarations","");
		
		return propertyMap;
	}
	
	public static JSONObject getDeclarationJSONObject(JSONArray declarations) throws JSONException
	{
		JSONObject declarationJSONObject = new JSONObject();
//		{"totalCount":4,"items":[{"name":"Name","type":"String","declarationtype":"Colorset"},{"name":"Alter","type":"Integer","declarationtype":"Colorset"},{"name":"n","type":"","declarationtype":"Variable"},{"name":"a","type":"","declarationtype":"Variable"}
		declarationJSONObject.put("totalCount", declarations.length());
		declarationJSONObject.put("items", declarations);
		
		return declarationJSONObject;
	}
		
	public static JSONObject getOneDeclaration(String name, String type, String declarationtype) throws JSONException
	{
		JSONObject declaration = new JSONObject();
		
		declaration.put("name", name);
		declaration.put("type", type);
		declaration.put("declarationtype", declarationtype);
		
		return declaration;
	}
	
	public static Shape getanArc(String resourceId)
	{		
		StencilType stencil = new StencilType("Arc");
		
		Shape arc = new Shape(resourceId, stencil);
		
//		"properties":{"id":"","label":"","transformation":""}
		arc.getProperties().put("id", "");
		arc.getProperties().put("label", "");
		arc.getProperties().put("transformation", "");		
		
		return arc;
	}
	
	public static void setArcBounds(Shape arc)
	{
		// oryx doesn't need in order to position the arc correctly
		// translate the position in the cpnFile to a bounds position in oryx
		Point UpperLeft = new Point(0.0, 0.0);
		Point LowerRight = new Point(0.0, 0.0); 
		
		arc.setBounds(new Bounds(LowerRight, UpperLeft));	
	}
	
	public static Shape getaTransition(String resourceId)
	{		
		StencilType stencil = new StencilType("Transition");
		
		Shape transition = new Shape(resourceId, stencil);
		
		
		transition.getProperties().put("id", "");
		transition.getProperties().put("title", "");
		transition.getProperties().put("firetype", "Automatic");
		transition.getProperties().put("href", "");
		transition.getProperties().put("omodel", "");
		transition.getProperties().put("oform", "");
		transition.getProperties().put("guard", "");
		
		
		return transition;
	}
	
	public static void setTransitionBounds(Shape transition, int[] boundsArray, CPNTransition tempTransition)
	{
		int w = 40, h = 40;
		
		int xPos = (int) Double.parseDouble(tempTransition.getPosattr().getX()); 
		int yPos = (int) Double.parseDouble(tempTransition.getPosattr().getY()); 
		// mit einem kleinen Faktor w�rde sich das alles in die L�nge ziehen  
		
		// translate the position in the cpnFile to a bounds position in oryx
		Point UpperLeft = new Point(0.0 + xPos + boundsArray[0], (0.0 + yPos + boundsArray[1]) * -1);
		Point LowerRight = new Point(0.0 + xPos + boundsArray[0] + w, (0.0 + yPos + boundsArray[1]) * -1 + h); 
		
		transition.setBounds(new Bounds(LowerRight, UpperLeft));	
	}
	
	public static Shape getaPlace(String resourceId)
	{
		StencilType stencil = new StencilType("Place");
		
		Shape place = new Shape(resourceId, stencil);
		
		
		place.getProperties().put("id", "");
		place.getProperties().put("title", "");
		place.getProperties().put("external", "false");
		place.getProperties().put("exttype", "Push");
		place.getProperties().put("href", "");
		place.getProperties().put("locatornames", "");
		place.getProperties().put("locatortypes", "");
		place.getProperties().put("locatorexpr", "");
		place.getProperties().put("colordefinition", "");
		
		
		return place;
	}
	
	public static void setPlaceBounds(Shape place, int[] boundsArray, CPNPlace tempPlace)
	{
		int w = 64, h = 64;
		// these are the center positions of the Place
		// but oryx only knows UpperLeft and lowerRight
		// so I have to translate it into oryx format; and that's why boundsArray are needed
		// it is a relative position which I have to add to 
		// every Place, Trans .. in order to set a correct Bound
		int xPos = (int) Double.parseDouble(tempPlace.getPosattr().getX()); 
		int yPos = (int) Double.parseDouble(tempPlace.getPosattr().getY()); 
		
		// translate the position in the cpnFile to a bounds position in oryx
		Point UpperLeft = new Point(0.0 + xPos + boundsArray[0], (0.0 + yPos + boundsArray[1]) * -1);
		Point LowerRight = new Point(0.0 + xPos + boundsArray[0] + w, (0.0 + yPos + boundsArray[1]) * -1 + h); 
		
		place.setBounds(new Bounds(LowerRight, UpperLeft));		
	}
	
	public static Shape getaToken(String resourceId)
	{
		StencilType stencil = new StencilType("Token");
		
		Shape token = new Shape(resourceId, stencil);
		
//		"properties":{"initialmarking":"\"Gerardo\"","quantity":"1","color":""}
		token.getProperties().put("initialmarking", "");
		token.getProperties().put("quantity", "1");
		token.getProperties().put("color", "#ffffff");
		token.getProperties().put("exttype", "Push");		
		
		return token;
	}
	
	public static void setTokenBounds(Shape token, int i)
	{
		int w = 12, h = 12;
		// because Token bounds are relative to the bounds of the place shape
		int xMid = 26, yMid = 26; // 26 = 32 - 12 / 2 
		
		// in order to position the token in a circle in the place
		double c = 10.0;
		double yRel = Math.sin((Math.PI / 6) * i) * c;
		double xRel = Math.cos((Math.PI / 6) * i) * c;
		
		Point UpperLeft = new Point(xMid + xRel, yMid + yRel);
		Point LowerRight = new Point(xMid + xRel + w, yMid + yRel + h); 
		
		token.setBounds(new Bounds(LowerRight, UpperLeft));		
	}

}
