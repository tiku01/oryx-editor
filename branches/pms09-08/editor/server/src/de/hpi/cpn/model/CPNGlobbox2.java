/*
 * This class is the main class for defining the declarations used in the CP Net.
 * The declarations - property in the JSON is an JSONArray, which consists of more than
 * one declarations like this ['Name', 'String', 'Colorset']. The first element is
 * the declarationId, second element is decalarationDataType. The third element 
 * expresses whether the declaration is a Colorset or a Variable, so called declarationType. 
 * 
 * This class checks the given declarations and extract only the declarations
 * which are correct and put it to the XML. Therefore you can find to block for all Colorsets
 * and all Variables.
 * 
 * Supported declarationDataTypes:
 * String,Integer or Int, Boolean or Bool, Product ()
 * 
 * Definition of a Product - declaration:
 * e.g ['NameAlter', 'Name * Alter', 'Colorset']
 * It's necessary that Name and Alter are also declarations as Colorset which are defined before. 
 * 
 *
 * */

package de.hpi.cpn.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;


public class CPNGlobbox2 extends XMLConvertable
{	
	private ArrayList<CPNColor> colors = new ArrayList<CPNColor>();
	private ArrayList<CPNVariable> vars = new ArrayList<CPNVariable>();
	private ArrayList<CPNBlock> blocks = new ArrayList<CPNBlock>();
	
	// ------------------------------------- Initialization ------------------------------
	public CPNGlobbox2()
	{
		initializeList();
	}
	
	private void initializeList()
	{		
//		setColorSetBlock(initializeColorBlock("ID1001"));
//		setVaraibleBlock(initializeVariableBlock("ID1002"));
	}
	
	private static CPNBlock initializeColorBlock( String idattr)
	{
		CPNBlock tempBlock = new CPNBlock("Colorset", idattr);
		tempBlock.setColors(new ArrayList<CPNColor>());
		
		return tempBlock;
	}
	
	private static CPNBlock initializeVariableBlock(String idattr)
	{
		CPNBlock tempBlock = new CPNBlock("Variable", idattr);
		tempBlock.setVars(new ArrayList<CPNVariable>());
		
		return tempBlock;
	}
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
	   xstream.alias("globbox", CPNGlobbox2.class);
	   
	   xstream.addImplicitCollection(CPNGlobbox2.class, "colors");
	   xstream.addImplicitCollection(CPNGlobbox2.class, "vars");
	   xstream.addImplicitCollection(CPNGlobbox2.class, "blocks");
	   
	   
	   xstream.omitField(CPNGlobbox2.class, "declarationIds");
	   
	   CPNBlock.registerMapping(xstream);	   
	}
	
	
	// ----------------------------------- JSON Reader -----------------------------------
	
	
	public void readJSONproperties(JSONObject modelElement) throws JSONException 
	{
		JSONObject properties = new JSONObject(modelElement.getString("properties"));
		this.parse(properties);
	}
	
	// Achtung!!! Aufpassen bei �nderungen im StencilSet JSON
	public void readJSONdeclarations(JSONObject modelElement) throws JSONException
	{
		JSONObject declarations = modelElement.optJSONObject("declarations");
		
		if (declarations != null)
		{
			// Set DeclarationIds in order to use it later for checking declaration
			JSONArray jsonDeclarations = declarations.optJSONArray("items");
			
			
			setDeclarationIds(allIds(jsonDeclarations));
			
			// Iterating over all declarations
			
			for (int i = 0; i < jsonDeclarations.length(); i++)
			{			
				JSONObject declaration = jsonDeclarations.getJSONObject(i);
				
				// Check if the declaration is correct 
				if (correctDeclaration(declaration))
					addDeclaration(declaration, i);
			}
		}
	}
	
	
	
	// ------------------------------ Helper ---------------------------------------------
	
	private ArrayList<String> declarationIds;	
	
	private ArrayList<String> allIds(JSONArray jsonDeclarations) throws JSONException
	{
		int jsonlen = jsonDeclarations.length();
		ArrayList<String> tempDeclarationIds = new ArrayList<String>(jsonlen);
		
		for (int i = 0; i < jsonlen; i++)
		{
			String tempDeclaration = jsonDeclarations.getJSONObject(i).getString("name");
			tempDeclarationIds.add(tempDeclaration);
		}
		
		System.out.println(tempDeclarationIds);
		
		return tempDeclarationIds;
	}
	
	private void addDeclaration(JSONObject declarationJSON, int id) throws JSONException
	{
		// id is important because each colorset must have an unique Id
		int IdStart = 20000;
		String declarationType = declarationJSON.getString("declarationtype");
		
//		 
//		declarationJSON.put("id", "ID" + (IdStart + id));
//				
//		// Choose correct block depending on the declarationType
//		if	(declarationType.equalsIgnoreCase("Colorset"))
////			getColorSetBlock().parse(declarationJSON);
//		
//		else
////			getVaraibleBlock().parse(declarationJSON);
			
	}	
	 	
	private boolean correctDeclaration(JSONObject declaration) throws JSONException
	{
		String declarationType = declaration.getString("declarationtype");
		String declarationDataType = declaration.getString("type");
		
		if (declarationType.equals("Colorset"))
			return correctColorSet(declarationDataType);
		 // Otherwise it must be "Variable"
		else
			return getDeclarationIds().contains(declarationDataType);
	
	}
	
	private boolean correctColorSet(String declarationDataType)
	{
		CharSequence productChar = " * "; // for product definition
		
		// Checking of the declaration is a product (a Complex Declaration)
		if (declarationDataType.contains(productChar)) // as long it's not a product or List
			return testProductDataType(declarationDataType.split(" "));
		
		// if it's not a complex declarationDataType, you only have to look if it is a normal datatype 
		// like string, int, ... by looking into the ArrayList 
		else 
			return getnormalDataTypes().contains(declarationDataType.toLowerCase());
	}
	
	private boolean testProductDataType(String[] decalarationColorElements)
	{
		// all odd array elements must be "*" for product definition
		// all even array elements must be a declarationId which is contained in this.declarationIds 
		for (int i = 0; i < decalarationColorElements.length; i++)
		{
			if (i % 2 == 0 && !getDeclarationIds().contains(decalarationColorElements[i]))
				return false;
		 	if (i % 2 == 1 && !decalarationColorElements[i].equals("*"))		 		
		 		return false;		 	
		}
		
		return true;
	}
	
	private ArrayList<String> getnormalDataTypes()
	{
		ArrayList<String> dataTypes = new ArrayList<String>();
		
		dataTypes.add("int");
		dataTypes.add("integer");
		dataTypes.add("bool");
		dataTypes.add("boolean");
		dataTypes.add("string");
		
		return dataTypes;
	}
	
	// ------------------------------ Accessor ------------------------------------------
	public ArrayList<CPNColor> getColors()
	   {
	      return this.colors;
	   }
	   public void setColors(ArrayList _colors)
	   {
	      this.colors = _colors;
	   }
	   public void addColor(CPNColor _color)
	   {
	      this.colors.add(_color);
	   }
	   public void removeColor(CPNColor _color)
	   {
	      this.colors.remove(_color);
	   }
	   public CPNColor getColor( int i)
	   {
	      return (CPNColor) this.colors.get(i);
	   }
	
	   public ArrayList<CPNVariable> getVars()
	   {
	      return this.vars;
	   }
	   public void setVars(ArrayList<CPNVariable> _vars)
	   {
	      this.vars = _vars;
	   }
	   public void addVar(CPNVariable _var)
	   {
	      this.vars.add(_var);
	   }
	   public void removeVar(CPNVariable _var)
	   {
	      this.vars.remove(_var);
	   }
	   public CPNVariable getVar( int i)
	   {
	      return (CPNVariable) this.vars.get(i);
	   }	   

	   public ArrayList<CPNBlock> getBlocks()
	   {
	      return this.blocks;
	   }
	   public void setBlocks(ArrayList _blocks)
	   {
	      this.blocks = _blocks;
	   }
	   public void addBlock(CPNBlock _block)
	   {
	      this.blocks.add(_block);
	   }
	   public void removeBlock(CPNBlock _block)
	   {
	      this.blocks.remove(_block);
	   }
	   public CPNBlock getBlock( int i)
	   {
	      return (CPNBlock) this.blocks.get(i);
	   }
	
	   private void setDeclarationIds(ArrayList<String> _declarations) 
	   {
		   this.declarationIds = _declarations;
	   }
	   private ArrayList<String> getDeclarationIds() 
	   {
		   return declarationIds;
	   }
	   
}
