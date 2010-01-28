package de.hpi.cpn.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oryxeditor.server.diagram.*;

public class CPNToolsTranslator 
{
	private Diagram oryxDiagram;
	private CPNWorkspaceElement cpnfile;
	private CPNArcRelations arcRelations;
	private JSONArray declarations;
	private int[] relativeBounds;
	private Hashtable<String, String> typeTable;
	
	public CPNToolsTranslator(CPNWorkspaceElement cpnfile)
	{
		setCpnfile(cpnfile);		
	}
	
	/*
	 * toDo:
	 * -> declarations
	 * -> shapes:
	 * 		-> transition
	 * 		-> places
	 * 		-> arcs
	 * 		-> token
	 * */
	
	
	public String transformCPNFile(String[] pagesToImport) throws JSONException
	{
		String resultDiagrams = "";
		
		setOryxDiagram(CPNDiagram.newColoredPetriNetDiagram());
		insertDeclarationsIntoDiagram();
		
		ArrayList<CPNPage> pages = getCpnfile().getCpnet().getPages();
		
		for (int i = 0; i < pages.size(); i++)
		{
			CPNPage page = pages.get(i);
			if (page == null)
				continue;
			
			String pageName = page.getPageattr().getName();
			if (! isPageAnImportPage(pageName, pagesToImport))
				continue;
			
			setRelativeBounds(CPNDiagram.getMaxBounds(page));
			
			CPNDiagram.setDiagramBounds(getOryxDiagram(), getRelativeBounds());
			
			prepareArcRelations(page);
			
			insertModellingElementsIntoDiagram(page);
			
			resultDiagrams += JSONBuilder.parseModeltoString(getOryxDiagram()) + ";;;";
			
			resetPageVariable();
			
		}
		
		resultDiagrams = resultDiagrams.substring(0, resultDiagrams.length() - 3);
		
		return resultDiagrams;
	}
	
	private void resetPageVariable()
	{
		setArcRelations(null);
		setTypeTable(null);
		getOryxDiagram().setBounds(null);
		getOryxDiagram().setChildShapes(new ArrayList<Shape>());
	}
	
	private boolean isPageAnImportPage(String pageToTest, String[] pagesToImport)
	{
		for (int i = 0; i < pagesToImport.length; i++)
		{
			if (pageToTest.equals(pagesToImport[i]))
				return true;
		}
		
		return false;
	}
	
	public String transfromCPNFile() throws JSONException
	{
		setOryxDiagram(CPNDiagram.newColoredPetriNetDiagram());
		
		// erstmal alle colorsets und variables auf den declarationstag mappen
		// muss vielleicht den JSONBuilder ver�ndern; in der Methode parseProperties; 
		// einfach ein paar mehr if - Abfragen rein machen
		// Alternative ist auch dass ich ein einfaches Replace mache auf den String "{ ersetzen durch {
		insertDeclarationsIntoDiagram();
		
		setRelativeBounds(CPNDiagram.getMaxBounds(getCpnfile().getCpnet().getPage(0)));
		
		CPNDiagram.setDiagramBounds(getOryxDiagram(), getRelativeBounds());
		
		prepareArcRelations(getCpnfile().getCpnet().getPage(0));
		
		insertModellingElementsIntoDiagram(getCpnfile().getCpnet().getPage(0));
		
		
		return JSONBuilder.parseModeltoString(getOryxDiagram());
	}
	
	private void prepareArcRelations(CPNPage tempPage)
	{
//		CPNPage tempPage = getCpnfile().getCpnet().getPage(0);
		getArcRelations().fillPlace(tempPage);
	}
	
	private void insertModellingElementsIntoDiagram(CPNPage page)
	{
//		CPNPage page = getCpnfile().getCpnet().getPage(0);
		
		ArrayList<CPNPlace> places = page.getPlaces();
		ArrayList<CPNTransition> transitions = page.getTransitions();
		ArrayList<CPNArc> arcs = page.getArcs();
		
		// f�r die bounds des Diagramms muss ich die gr��ten negativen positionen von Transition und Stellen rauskriegen (vieleicht auch von den Bendpoints)
//		int maxX = 0, maxY = 0;
		
		for (int i = 0; i < places.size(); i++)
		{
			CPNPlace tempPlace = places.get(i);
			
			if (tempPlace != null)
				insertNewModellingElement(tempPlace);
		}
		
		for (int i = 0; i < transitions.size(); i++)
		{
			CPNTransition tempTransitions = transitions.get(i);
			
			if (tempTransitions != null)
				insertNewModellingElement(tempTransitions);
		}
		
		for (int i = 0; i < arcs.size(); i++)
		{
			CPNArc tempArc = arcs.get(i);
			
			if (tempArc != null)
				insertNewModellingElement(tempArc);
		}
		
		
	}
	
	private void insertDeclarationsIntoDiagram() throws JSONException
	{
		CPNGlobbox globbox = getCpnfile().getCpnet().getGlobbox();
		ArrayList<CPNColor> colors = globbox.getColors();
		ArrayList<CPNVariable> variables = globbox.getVars();
		ArrayList<CPNBlock> blocks = globbox.getBlocks();
		
		if (colors != null)
		{
			// for all colors in globbox level
			for (int i = 0; i < colors.size(); i++)
			{
				CPNColor tempColor = colors.get(i);
				if (tempColor != null)
					insertNewDeclaration(tempColor);			
			}
		}
		
		if (variables != null)
		{
			// for all varaibles in globbox level
			for (int i = 0; i < variables.size(); i++)
			{
				CPNVariable tempVariable = variables.get(i);
				if (tempVariable != null)
					insertNewDeclaration(tempVariable);			
			}
		}
		
		if (blocks == null)
			return;
		
		for (int i = 0; i < blocks.size(); i++)
		{
			CPNBlock tempBlock = blocks.get(i);
			if (tempBlock != null)
			{
				colors = tempBlock.getColors();
				variables = tempBlock.getVars();
				
				if (colors != null)
				{
					// for all colors in block level
					for (int j = 0; j < colors.size(); j++)
					{
						CPNColor tempColor = colors.get(j);
						if (tempColor != null)
							insertNewDeclaration(tempColor);			
					}
				}
				
				if (variables != null)
				{
					// for all varaibles in block level
					for (int j = 0; j < variables.size(); j++)
					{
						CPNVariable tempVariable = variables.get(j);
						if (tempVariable != null)
							insertNewDeclaration(tempVariable);			
					}
				}				
			}			
		}
		
		// adding declarations to the diagram property
		JSONObject declarationJSON = CPNDiagram.getDeclarationJSONObject(getDeclarations());
		getOryxDiagram().putProperty("declarations", declarationJSON.toString());
	}
	
	private void insertNewModellingElement(CPNArc tempArc)
	{
		String arcId = tempArc.getId();
		
		Shape arc = CPNDiagram.getanArc(arcId);
		
		// properties
		arc.putProperty("id", arcId);
									// sorry for the long way
		arc.putProperty("label", tempArc.getAnnot().getText().getText());
		
		// bounds
		// I don't have to take care about bounds; they can be 0
		CPNDiagram.setArcBounds(arc); 
				
		// outgoing
		String targetId = getArcRelations().getTargetValue(arcId);
		Shape targetShape = new Shape(targetId);
		arc.addOutgoing(targetShape);
		arc.setTarget(targetShape);
		
		// dockers
		setDockers(arc, arcId);
		
		getOryxDiagram().getChildShapes().add(arc);
		
	}
	private void setDockers(Shape arc, String arcId)
	{
		String sourceId = getArcRelations().getSourceValue(arcId);
		String sourceType = getTypeTable().get(sourceId);
		
		if (sourceType.equals("Transition"))
		{
			// der Mittelpunkt einer TransitionShape
			// also H�lfte der definierten Breite und H�he der Shape, die in CPNDiagram definiert ist
			arc.getDockers().add(new Point(20.0, 20.0));			
			arc.getDockers().add(new Point(32.0, 32.0));
		}
		else // otherwise it must be a Place
		{
			arc.getDockers().add(new Point(32.0, 32.0));
			arc.getDockers().add(new Point(20.0, 20.0));
		}		
	}
	
	private void insertNewModellingElement(CPNTransition tempTransition)
	{
		String transitionId = tempTransition.getId();
		
		Shape transition = CPNDiagram.getaTransition(transitionId);
		
		// properties
		transition.putProperty("id", transitionId);
		transition.putProperty("title", tempTransition.getText());
													// sorry for the long way
		transition.putProperty("guard", tempTransition.getCond().getText().getText());
		
		// bounds
		CPNDiagram.setTransitionBounds(transition, getRelativeBounds(), tempTransition); 
				
		// outgoing
		Iterator<String> outgoingIter = getArcRelations().getSourcesFor(transitionId).iterator();
		
		while (outgoingIter.hasNext())
		{
			String outgoingId = outgoingIter.next();
			transition.addOutgoing(new Shape(outgoingId));
		}		
		
		// telling which type it is
		getTypeTable().put(transitionId, "Transition");
		
		getOryxDiagram().getChildShapes().add(transition);		
	}
	
	private void insertNewModellingElement(CPNPlace tempPlace)
	{
		String placeId = tempPlace.getId();
		
		Shape place = CPNDiagram.getaPlace(placeId);
		
		// properties
		place.putProperty("id", placeId);
		place.putProperty("title", tempPlace.getText());
													// sorry for the long way
		place.putProperty("colordefinition", tempPlace.getType().getText().getText());
		
		// bounds
		CPNDiagram.setPlaceBounds(place, getRelativeBounds(), tempPlace);
		
		// tokens
		insertNewTokens(place, tempPlace.getInitmark()); 
				
		// outgoing
		Iterator<String> outgoingIter = getArcRelations().getSourcesFor(placeId).iterator();
		
		while (outgoingIter.hasNext())
		{
			String outgoingId = outgoingIter.next();
			place.addOutgoing(new Shape(outgoingId));
		}		
		
		// telling which type it is
		getTypeTable().put(placeId, "Place");
		
		getOryxDiagram().getChildShapes().add(place);		
	}
	
	private void insertNewTokens(Shape place, CPNProperty tempInitMark)
	{
		String initialDefinition = tempInitMark.getText().getText();
		
		if ( initialDefinition.equals(""))
			return;
		
		if (initialDefinition.indexOf("++") != -1)
		{	// a regex which means ++; so the string is splited when a "++" occurs
			String[] initialDefinitionParts = initialDefinition.split("\\+\\+");
			
			for (int i = 0; i < initialDefinitionParts.length; i++)
			{
				Shape token = CPNDiagram.getaToken(tempInitMark.getId() + i);
				if (initialDefinitionParts[i].indexOf("`") != -1)
				{
					String[] initialDefinitionParts2 = initialDefinitionParts[i].split("`");
					// properties
					token.putProperty("initialmarking", initialDefinitionParts2[1]);
					token.putProperty("quantity", initialDefinitionParts2[0]);
					
					// bounds
					CPNDiagram.setTokenBounds(token, i);
					
					place.getChildShapes().add(token);
				}
				else
				{					
					// properties
					token.putProperty("initialmarking", initialDefinition);
					token.putProperty("quantity", "1");
					
					// bounds
					CPNDiagram.setTokenBounds(token, 0);
					
					place.getChildShapes().add(token);
				}
			}
		}
		else // schreibe ich einfach hin was es gibt
		{
			// das gleiche wie oben
			Shape token = CPNDiagram.getaToken(tempInitMark.getId());
			
			// properties
			token.putProperty("initialmarking", initialDefinition);
			token.getProperties().put("quantity", "1");
			
			// bounds
			CPNDiagram.setTokenBounds(token, 0);
			
			place.getChildShapes().add(token);
		}
		
	}
	
	private void insertNewDeclaration(CPNVariable tempVariable) throws JSONException
	{
		String name, type, declarationtype;
		name = tempVariable.getIdtag();
		declarationtype = "Variable";
		type = tempVariable.getType().getId();		
		
		getDeclarations().put(CPNDiagram.getOneDeclaration(name, type, declarationtype));
	}
	
	private void insertNewDeclaration(CPNColor tempColor) throws JSONException
	{
		// besser nicht mit layout umgehen; zu riskant
		
		String name, type, declarationtype;
		name = tempColor.getIdtag();
		declarationtype = "Colorset";
		type = "";
		
		if (tempColor.getStringtag() != null)
			type = "String";
		
		else if (tempColor.getBooleantag() != null)
			type = "Boolean";
		
		else if (tempColor.getIntegertag() != null)
			type = "Integer";
				
		else if (tempColor.getProducttag() != null)
		{
			CPNProduct tempProduct = tempColor.getProducttag();
			int i;
			for (i = 0; i < tempProduct.getIds().size() - 1; i++)
				type += tempProduct.getId(i) + " * ";			
			
			type += tempProduct.getId(i);
		}
		else if (tempColor.getListtag() != null)
		{
			// muss implementiert werden.
			type  = "not yet implemeted";
		}
		else if (tempColor.getUnittag() != null)
		{
			// muss noch implementiert werden
			type = "not yet implemented";
		}
		else
			type = "no support for: " + tempColor.getLayout();
		
		
		getDeclarations().put(CPNDiagram.getOneDeclaration(name, type, declarationtype));		
	}
	
	
	public Diagram getOryxDiagram()
	{
		return oryxDiagram;
	}
	private void setOryxDiagram(Diagram diagram)
	{
		oryxDiagram = diagram;
	}

	private void setCpnfile(CPNWorkspaceElement cpnfile) 
	{
		this.cpnfile = cpnfile;
	}
	public CPNWorkspaceElement getCpnfile()
	{
		return cpnfile;
	}

	private JSONArray getDeclarations()
	{
		if (declarations == null)
			declarations = new JSONArray();
		return declarations;
	}
	
	private CPNArcRelations getArcRelations()
	{
		if (arcRelations == null)
			arcRelations = new CPNArcRelations();
		return arcRelations;
	}
	
	private void setArcRelations(CPNArcRelations _ArcRelations)
	{
		this.arcRelations = _ArcRelations;
	}

	private void setRelativeBounds(int[] relativeBounds)
	{
		this.relativeBounds = new int[2];
		// 50 ist nur ein Puffer damit es ein bisschen Abstand von der Wand gibt
		this.relativeBounds[0] = relativeBounds[0] * -1 + 50;
		this.relativeBounds[1] = relativeBounds[1] * -1 - 50;
	}

	private int[] getRelativeBounds() {
		return relativeBounds;
	}

	private Hashtable<String, String> getTypeTable() {
		if (typeTable == null)
			typeTable = new Hashtable<String, String>();
		return typeTable;
	}
	
	private void setTypeTable(Hashtable<String, String> _typeTable)
	{
		this.typeTable = _typeTable;
	}
	
	
}
