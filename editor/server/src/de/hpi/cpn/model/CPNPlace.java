package de.hpi.cpn.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;


public class CPNPlace extends CPNModellingThing
{
	
	

	private String text;
	private CPNLittleProperty ellipse = CPNLittleProperty.ellipse();
	private CPNLittleProperty token = CPNLittleProperty.token();	
	private CPNLittleProperty marking = CPNLittleProperty.marking();
	private CPNProperty type = new CPNProperty();
	private CPNProperty initmark = new CPNProperty();	// aufpassen beim Export k�nnen es auch merh sein
	
	
	
	public CPNPlace()
	{
		super();		
	}
	
// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("place", CPNPlace.class);
	
//		xstream.addImplicitCollection(CPNPlace.class, "markings");
//		xstream.addImplicitCollection(CPNPlace.class, "tokens");
//		xstream.addImplicitCollection(CPNPlace.class, "initmarks");
		
		CPNToken.registerMapping(xstream);
		CPNMarking.registerMapping(xstream);
		CPNLittelForm.registerMapping(xstream);
	}
	
	// ----------------------------------------- JSON Reader ------------------------------------
	
	public void readJSONproperties(JSONObject modelElement) throws JSONException
	{
		JSONObject properties = new JSONObject(modelElement.getString("properties"));
		this.parse(properties);
	}
	
	public void readJSONtitle(JSONObject modelElement) throws JSONException
	{
		String text = modelElement.getString("title");
		
		setText(text);
	}
	
	public void readJSONcolorsettype(JSONObject modelElement) throws JSONException
	{
		String colorsettype = modelElement.getString("colorsettype");
		
		JSONObject tempJSON = new JSONObject();
		tempJSON.put("colordefinition", colorsettype);
		tempJSON.put("id", getId() + "1");
		
		getType().parse(tempJSON);		
	}	
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException
	{
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		JSONObject tokenProperties = new JSONObject();
		
		if (childShapes != null)
		{
			for (int i = 0; i < childShapes.length(); i++) 
			{
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				if (stencil.equals("Token"))
				{
					tokenProperties = new JSONObject(childShape.getString("properties"));					
				}
			}
		}
		
		tokenProperties.put("id", getId() + 2);
		
		getInitmark().parse(tokenProperties);
		
		
	}
	
	public void readJSONbounds(JSONObject modelElement) throws JSONException
	{
		JSONObject boundsJSON = modelElement.getJSONObject("bounds").getJSONObject("upperLeft");
		
		setPositionAttributes(boundsJSON);
		
		settypePositionAttributes(boundsJSON);
		setinitmarkPositionAttributes(boundsJSON);
	}
	
	// ------------------------------------------ Helper ----------------------------------------
	
	public static boolean handlesStencil(String stencil)
	{		
		return stencil.equals("Place");
	}
	
	public void setPositionAttributes(JSONObject modelElement) throws JSONException
	{		
		getPosattr().setX(modelElement.getString("x") + ".000000");
		getPosattr().setY(modelElement.getString("y") + ".000000");
	}
	
	public void setinitmarkPositionAttributes(JSONObject modelElement) throws JSONException
	{
		int defaultShiftX = 57;
		int defaultShiftY = 23;
		
		JSONObject initmarkingPositionJSON = new JSONObject();
		
		int x = (int) Double.parseDouble(modelElement.getString("x")) + defaultShiftX;
		int y = (int) Double.parseDouble(modelElement.getString("y")) + defaultShiftY;
		
		initmarkingPositionJSON.put("initpostattrX", "" + x + ".000000");
		initmarkingPositionJSON.put("initpostattrY", "" + y + ".000000");
		
		getInitmark().parse(initmarkingPositionJSON);
	}
	
	public void settypePositionAttributes(JSONObject modelElement) throws JSONException
	{
		int defaultShiftX = 43;
		int defaultShiftY = -23;
		
		JSONObject typePositionJSON = new JSONObject();
		
		int x = (int) Double.parseDouble(modelElement.getString("x")) + defaultShiftX;
		int y = (int) Double.parseDouble(modelElement.getString("y")) + defaultShiftY;
		
		typePositionJSON.put("typepostattrX", "" + x + ".000000");
		typePositionJSON.put("typepostattrY", "" + y + ".000000");
		
		getType().parse(typePositionJSON);
	}
	
	// ---------------------------------------- Accessory ----------------------------------------

  
   
   public CPNLittleProperty getEllipse()
   {
      return this.ellipse;
   }
   public void setEllipse(CPNLittleProperty _ellipse)
   {
      this.ellipse = _ellipse;
   }

   
   
   public String getText()
   {
	  return text;
   }
   public void setText(String _text)
   {
	   this.text = _text;
   }

   public void setToken(CPNLittleProperty token) 
   {
	   this.token = token;
   }   
   public CPNLittleProperty getToken() 
   {
	   return token;
   }
   
   public void setMarking(CPNLittleProperty marking) 
   {
	   this.marking = marking;
   }
   public CPNLittleProperty getMarking()
   {
	return marking;
   }

   public void setInitmark(CPNProperty initmarking)
   {
	   this.initmark = initmarking;
   }   
   public CPNProperty getInitmark()
   {
	   return initmark;
   }
	
   public void setType(CPNProperty type) 
   {
	   this.type = type;
   }	
   public CPNProperty getType() 
   {
	   return type;
   }
   
//   private ArrayList<CPNMarking> markings = new ArrayList<CPNMarking>();
//   private ArrayList<CPNToken> tokens = new ArrayList<CPNToken>();	
//	private ArrayList<CPNInitmarking> initmarks = new ArrayList<CPNInitmarking>();	
//	private ArrayList<CPNType> types = new ArrayList<CPNType>();
//	
//   public ArrayList<CPNMarking> getMarkings()
//   {
//      return this.markings;
//   }
//   public void setMarkings(ArrayList<CPNMarking> _markings)
//   {
//      this.markings = _markings;
//   }
//   public void addMarking(CPNMarking _marking)
//   {
//      this.markings.add(_marking);
//   }
//   public void removeMarking(CPNMarking _marking)
//   {
//      this.markings.remove(_marking);
//   }
//   public CPNMarking getMarking( int i)
//   {
//      return (CPNMarking)this.markings.get(i);
//   }
//	public ArrayList<CPNToken> getTokens()
//	   {
//	      return this.tokens;
//	   }
//	   public void setTokens(ArrayList<CPNToken> _tokens)
//	   {
//	      this.tokens = _tokens;
//	   }
//	   public void addToken(CPNToken _token)
//	   {
//	      this.tokens.add(_token);
//	   }
//	   public void removeToken(CPNToken _token)
//	   {
//	      this.tokens.remove(_token);
//	   }
//	   public CPNToken getToken( int i)
//	   {
//	      return (CPNToken)this.tokens.get(i);
//	   }
//
//	   public ArrayList<CPNInitmarking> getInitmarks()
//	   {
//	      return this.initmarks;
//	   }
//	   public void setInitmarks(ArrayList<CPNInitmarking> _initmarks)
//	   {
//	      this.initmarks = _initmarks;
//	   }
//	   public void addInitmark(CPNInitmarking _initmark)
//	   {
//	      this.initmarks.add(_initmark);
//	   }
//	   public void removeInitmark(CPNInitmarking _initmark)
//	   {
//	      this.initmarks.remove(_initmark);
//	   }
//	   public CPNInitmarking getInitmark( int i)
//	   {
//	      return (CPNInitmarking)this.initmarks.get(i);
//	   }	   
//
//	   public ArrayList<CPNType> getTypes()
//	   {
//	      return this.types;
//	   }
//	   public void setTypes(ArrayList<CPNType> _types)
//	   {
//	      this.types = _types;
//	   }
//	   public void addType(CPNType _type)
//	   {
//	      this.types.add(_type);
//	   }
//	   public void removeType(CPNType _type)
//	   {
//	      this.types.remove(_type);
//	   }
//	   public CPNType getType( int i)
//	   {
//	      return (CPNType)this.types.get(i);
//	   }
}
