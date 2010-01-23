package de.hpi.cpn.model;


import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;



public class CPNWorkspaceElement extends XMLConvertable
{
   private CPNGenerator generator = new CPNGenerator();
   private CPNCpnet cpnet = new CPNCpnet();
   
   

   // ---------------------------------------- Mapping ----------------------------------------
	
   public static void registerMapping(XStream xstream)
   {
	   xstream.alias("workspaceElements", CPNWorkspaceElement.class);
	   
	   CPNCpnet.registerMapping(xstream);
	   CPNGenerator.registerMapping(xstream);
   }	
   
   // ---------------------------------------- JSON Reader ----------------------------------------
   
   public void readJSONresourceId(JSONObject modelElement) throws JSONException
   {
	   getCpnet().parse(modelElement);
   }
   
   
   
   // ---------------------------------------- Accessory -----------------------------------------
   
   public CPNCpnet getCpnet()
   {
      return this.cpnet;
   }
   public void setCpnet(CPNCpnet _cpnet)
   {
      this.cpnet = _cpnet;
   }
   
   public CPNGenerator getGenerator()
   {
      return this.generator;
   }
   public void setGenerator(CPNGenerator _generator)
   {
      this.generator = _generator;
   }   
}
