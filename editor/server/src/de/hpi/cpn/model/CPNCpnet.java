package de.hpi.cpn.model;

import java.util.*;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNCpnet extends XMLConvertable 
{ 
	private CPNGlobbox globbox = new CPNGlobbox(); // Variabes Colors
	// Pages for all the nets
	private ArrayList<CPNPage> pages = new ArrayList<CPNPage>(); 
	
	
	
	// ---------------------------------- Constructor ---------------------------------------

	
	
	// ------------------------------------ Mapping ------------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
		xstream.alias("cpnet", CPNCpnet.class);
		
		xstream.addImplicitCollection(CPNCpnet.class, "pages");
		
		
		CPNGlobbox.registerMapping(xstream);
		CPNPage.registerMapping(xstream);
	}
	
   // ---------------------------------------- JSON Reader ------------------------------------
	

	 public void readJSONresourceId(JSONObject modelElement) throws JSONException
	 {
		createPage(modelElement);
		createGlobbox(modelElement);
	 }
	 
	 private void createPage(JSONObject modelElement) throws JSONException
	 {
		CPNPage page = new CPNPage();
		page.setId("ID30001"); // nur Provisorisch
			
		page.prepareResourceIDictionary(modelElement);
		
//		modelElement.put("pageId", "ID30001");
		page.parse(modelElement);
		
		getPages().add(page);
	 }
	 
	 private void createGlobbox(JSONObject modelElement)
	 {
		getGlobbox().parse(modelElement);
	 }
  

   // ------------------------------------ Accessory ----------------------------------------
   public CPNGlobbox getGlobbox()
   {
      return this.globbox;
   }
   public void setGlobbox(CPNGlobbox _globbox)
   {
      this.globbox = _globbox;
   }
   
   public ArrayList<CPNPage> getPages()
   {
      return this.pages;
   }
   public void setPages(ArrayList<CPNPage> _pages)
   {
      this.pages = _pages;
   }
   public void addPage(CPNPage _page)
   {
      this.pages.add(_page);
   }
   public void removePage(CPNPage _page)
   {
      this.pages.remove(_page);
   }
   public CPNPage getPage( int i)
   {
      return (CPNPage) this.pages.get(i);
   }
}

