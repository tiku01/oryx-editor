package de.hpi.cpn.model;

import java.util.ArrayList;
import java.util.Dictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;


public class CPNPage extends XMLConvertable
{
	
//	e.g.
//	<page id="ID6">
//    <pageattr name="Tutorial"/>
//	  <place id="ID23568">
//			...
//	  </place>
//			...
//	</page>
		
	private transient String auxtag, grouptag;
	
	private CPNArcRelations arcRelation = new CPNArcRelations();
	// not needed for mapping; only helper Variable
	
	private String idattri;
	private CPNPageattr pageattr = new CPNPageattr();
	private ArrayList<CPNPlace> places = new ArrayList<CPNPlace>();
	private ArrayList<CPNTransition> transitions = new ArrayList<CPNTransition>();
	private ArrayList<CPNArc> arcs = new ArrayList<CPNArc>();

	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("page", CPNPage.class);
				
		xstream.useAttributeFor(CPNPage.class, "idattri");
		xstream.aliasAttribute(CPNPage.class, "idattri", "id");
		
		xstream.addImplicitCollection(CPNPage.class, "places", CPNPlace.class);
		xstream.addImplicitCollection(CPNPage.class, "transitions", CPNTransition.class);
		xstream.addImplicitCollection(CPNPage.class, "arcs", CPNArc.class);
		
		xstream.registerConverter(new CPNTextConverter());
		
		xstream.omitField(CPNPage.class, "arcRelation");
		
		xstream.aliasField("Aux", CPNPage.class, "auxtag");
		xstream.aliasField("group", CPNPage.class, "grouptag");
		
		CPNPageattr.registerMapping(xstream);
		CPNModellingThing.registerMapping(xstream);
		CPNPlace.registerMapping(xstream);
		CPNTransition.registerMapping(xstream);
		CPNArc.registerMapping(xstream);
		CPNLittleProperty.registerMapping(xstream);
	}
	
	// ----------------------------------- JSON Reader  ----------------------------------------
	
	public void readJSONpageId(JSONObject modelElement) throws JSONException
	{
		String Id = modelElement.getString("pageId");
		
		setId(Id);
	}
	
	public void readJSONproperties(JSONObject modelElement) throws JSONException
	{
		JSONObject properties = new JSONObject(modelElement.getString("properties"));
		this.readJSONtitle(properties);
	}
	
	public void readJSONtitle(JSONObject modelElement) throws JSONException
	{
		String title = modelElement.getString("title");
		
		if (title.isEmpty())
			title = "Exported CPN";
		
		getPageattr().setName(title);
	}
	
	public void readJSONchildShapes(JSONObject modelElement) throws JSONException 
	{
		JSONArray arcs = new JSONArray();
		
		JSONArray childShapes = modelElement.optJSONArray("childShapes");
		
		
		if (childShapes != null)
		{	
			int i;
			for ( i = 0; i<childShapes.length(); i++) 
			{
				JSONObject childShape = childShapes.getJSONObject(i);
				String stencil = childShape.getJSONObject("stencil").getString("id");
				
				if (CPNTransition.handlesStencil(stencil))
					createTransition(childShape,i);
				
				else if (CPNPlace.handlesStencil(stencil))
					createPlace(childShape, i);
				
				else if (CPNArc.handlesStencil(stencil))
					arcs.put(childShape);
			}
			
			for (i = 0; i < arcs.length(); i++)
				createArc(arcs.getJSONObject(i), i);
				
		}		
	}
	
	// -------------------------------- Helper --------------------------------------------
	
	private void createPlace(JSONObject modelElement, int index) throws JSONException
	{
		// um mehr Platz zu haben multipliziere ich den Index mit 10.
		// So sind die einzlnen IDs immer die Differenz 10.
		// Und in der Stelle hab ich mehr Platz f�r anderen Ids die die Schei** braucht!!!
		String resourceId = modelElement.getString("resourceId");
		String placeId = "ID" + (3000 + index * 10);
		
		// correct the Hashtable - Entry 
		getArcRelation().changePlaceId(resourceId, placeId);
		
		CPNPlace place = new CPNPlace();
			
		place.setId(placeId);
		
		place.parse(modelElement);
		
		getPlaces().add(place);		
	}

	private void createTransition(JSONObject modelElement, int index) throws JSONException
	{
		String resourceId = modelElement.getString("resourceId");
		String transId = "ID" + (4000 + index * 10);
		
		getArcRelation().changeTransitionId(resourceId, transId);
		
		CPNTransition transition = new CPNTransition();
		
		transition.setId(transId);
		
		transition.parse(modelElement);
		
		getTransitions().add(transition);		
	}
	
	private void createArc(JSONObject modelElement, int index) throws JSONException
	{
		String resourceId = modelElement.getString("resourceId");
		String target = getArcRelation().getTargetValue(resourceId);
		String source = getArcRelation().getSourceValue(resourceId);
		String arcId = "ID" + (5000 + index * 10);
		
		String transend, placeend, orientation;
		
		if (isTransition(target))
		{
			transend = target;
			placeend = source;
			orientation = "PtoT";
		}
		else
		{
			transend = source;
			placeend = target;
			orientation = "TtoP";
		}
		
		modelElement.put("transend", transend);
		modelElement.put("placeend", placeend);
		modelElement.put("orientation", orientation);
		
		
		CPNArc arc = new CPNArc();
		
		arc.setId(arcId);
		
		arc.parse(modelElement);
		
		getArcs().add(arc);		
	}
	
	public void prepareResourceIDictionary(JSONObject modelElement) throws JSONException
	{
		getArcRelation().readJSONchildShapes(modelElement);		
	}
	
	private boolean isTransition(String Id)
	{
		char letterafterId = Id.charAt(2);
		
		return letterafterId == '4';
	}
	
	// ------------------------------ Accessor ------------------------------------------
	   public String getId()
	   {
	      return this.idattri;
	   }

	   public void setId(String _id)
	   {
	      this.idattri = _id;
	   }
	   
	   public CPNPageattr getPageattr()
	   {
	      return this.pageattr;
	   }
	   public void setPageattr(CPNPageattr _pageattr)
	   {
	      this.pageattr = _pageattr;
	   }
	   
	   
	   public ArrayList<CPNPlace> getPlaces()
	   {
	      return this.places;
	   }
	   public void setPlaces(ArrayList<CPNPlace> _places)
	   {
	      this.places = _places;
	   }
	   public void addPlace(CPNPlace _place)
	   {
	      this.places.add(_place);
	   }
	   public void removePlace(CPNPlace _place)
	   {
	      this.places.remove(_place);
	   }
	   public CPNPlace getPlace( int i)
	   {
	      return (CPNPlace) this.places.get(i);
	   }
	   

	   public ArrayList<CPNArc> getArcs()
	   {
	      return this.arcs;
	   }
	   public void setArcs(ArrayList<CPNArc> _arcs)
	   {
	      this.arcs = _arcs;
	   }
	   public void addArc(CPNArc _arc)
	   {
	      this.arcs.add(_arc);
	   }
	   public void removeArc(CPNArc _arc)
	   {
	      this.arcs.remove(_arc);
	   }
	   public CPNArc getArc( int i)
	   {
	      return (CPNArc)this.arcs.get(i);
	   }
	   
	   public ArrayList<CPNTransition> getTransitions()
	   {
	      return this.transitions;
	   }
	   public void setTransitions(ArrayList<CPNTransition> _transs)
	   {
	      this.transitions = _transs;
	   }
	   public void addTransition(CPNTransition _trans)
	   {
	      this.transitions.add(_trans);
	   }
	   public void removeTransition(CPNTransition _trans)
	   {
	      this.transitions.remove(_trans);
	   }
	   public CPNTransition getTrans( int i)
	   {
	      return (CPNTransition) this.transitions.get(i);
	   }

	public void setArcRelation(CPNArcRelations arcRelation) {
		this.arcRelation = arcRelation;
	}

	public CPNArcRelations getArcRelation() {
		return arcRelation;
	}
	   
	   
	   
	   
	   
	   /*
	   private ArrayList hguidelines = new ArrayList();
	   private ArrayList vguidelines = new ArrayList();
	   private ArrayList auxs = new ArrayList();
	   private ArrayList groups = new ArrayList();
	   
	   
	   public ArrayList getHguidelines()
	   {
	      return this.hguidelines;
	   }
	   public void setHguidelines(ArrayList _hguidelines)
	   {
	      this.hguidelines = _hguidelines;
	   }
	   public void addHguideline(Hguideline _hguideline)
	   {
	      this.hguidelines.add(_hguideline);
	   }
	   public void removeHguideline(Hguideline _hguideline)
	   {
	      this.hguidelines.remove(_hguideline);
	   }
	   public Hguideline getHguideline( int i)
	   {
	      return (Hguideline)this.hguidelines.get(i);
	   }
	   

	   public ArrayList getVguidelines()
	   {
	      return this.vguidelines;
	   }
	   public void setVguidelines(ArrayList _vguidelines)
	   {
	      this.vguidelines = _vguidelines;
	   }
	   public void addVguideline(Vguideline _vguideline)
	   {
	      this.vguidelines.add(_vguideline);
	   }
	   public void removeVguideline(Vguideline _vguideline)
	   {
	      this.vguidelines.remove(_vguideline);
	   }
	   public Vguideline getVguideline( int i)
	   {
	      return (Vguideline)this.vguidelines.get(i);
	   }
	   

	   public ArrayList getAuxs()
	   {
	      return this.auxs;
	   }
	   public void setAuxs(ArrayList _auxs)
	   {
	      this.auxs = _auxs;
	   }
	//   public void addAux(Aux _aux)
	//   {
//	      this.auxs.add(_aux);
	//   }
	//   public void removeAux(Aux _aux)
	//   {
//	      this.auxs.remove(_aux);
	//   }
	//   public Aux getAux( int i)
	//   {
//	      return (Aux)this.auxs.get(i);
	//   }
	   

	   public ArrayList getGroups()
	   {
	      return this.groups;
	   }
	   public void setGroups(ArrayList _groups)
	   {
	      this.groups = _groups;
	   }
	   public void addGroup(Group _group)
	   {
	      this.groups.add(_group);
	   }
	   public void removeGroup(Group _group)
	   {
	      this.groups.remove(_group);
	   }
	   public Group getGroup( int i)
	   {
	      return (Group)this.groups.get(i);
	   }
	   */
}
