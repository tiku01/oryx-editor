package de.hpi.cpn.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;

public class CPNColor extends XMLConvertable
{
//	e.g.
//	<color id="ID161365">
//    <id>NameAlter</id>
//    <product>
//      <id>Name</id>
//      <id>Alter</id>
//    </product>
//    <layout>colset NameAlter = product Name * Alter;</layout>
//  </color>
	
	// things I cannot support
	private transient String mltag, alias;
	
	private String idattri;
	private String idtag;

	private CPNString stringtag;
	private CPNProduct producttag;
	private CPNInteger integertag;
	private CPNBoolean booleantag;
	private CPNList listtag;
	private CPNUnit unittag;
	
	private String layout;
	
	// ---------------------------------------- Mapping ----------------------------------------
	
	public static void registerMapping(XStream xstream)
	{
	   xstream.alias("color", CPNColor.class);
	   
	   xstream.aliasField("id", CPNColor.class, "idattri");
	   xstream.aliasField("id", CPNColor.class, "idtag");
	   xstream.aliasField("bool", CPNColor.class, "booleantag");
	   xstream.aliasField("string", CPNColor.class, "stringtag");
	   xstream.aliasField("int", CPNColor.class, "integertag");
	   xstream.aliasField("product", CPNColor.class, "producttag");
	   xstream.aliasField("list", CPNColor.class, "listtag");
	   xstream.aliasField("unit", CPNColor.class, "unittag");
	   
	   xstream.useAttributeFor(CPNColor.class, "idattri");
	   
//	   xstream.addImplicitCollection(CPNGlobbox.class, "colors");
//	   xstream.addImplicitCollection(CPNGlobbox.class, "vars");
//	   xstream.addImplicitCollection(CPNGlobbox.class, "blocks");
	   
	   
	   CPNString.registerMapping(xstream);
	   CPNProduct.registerMapping(xstream);
	   CPNBoolean.registerMapping(xstream);
	   CPNInteger.registerMapping(xstream);
	}
	
	// ------------------------------------------ JSON Reader ------------------------------------------
	
	public void readJSONname(JSONObject modelElement) throws JSONException 
	{
		setIdtag(modelElement.getString("name"));
		setIdattri(modelElement.getString("id"));
		
		String declarationDataType = modelElement.getString("type");
		
		CharSequence productChar = " * "; // for product definition
		
		if (declarationDataType.contains(productChar))
			addProduct(modelElement);
		
		if (declarationDataType.equalsIgnoreCase("string"))
			addString(modelElement);
			
		if (declarationDataType.equalsIgnoreCase("integer") || declarationDataType.equalsIgnoreCase("int"))
			addInteger(modelElement);
		
		if (declarationDataType.equalsIgnoreCase("boolean") || declarationDataType.equalsIgnoreCase("bool"))
			addBoolean(modelElement);
			
//		dataTypes.add("int");
//		dataTypes.add("integer");
//		dataTypes.add("bool");
//		dataTypes.add("boolean");
//		dataTypes.add("string");
		
		
	}
	
	
	private void addProduct(JSONObject modelElement) throws JSONException
	{
		CPNProduct tempProduct = new CPNProduct();
		String[] declarationDataTypeSegments = modelElement.getString("type").split(" ");
		tempProduct.setIds(new ArrayList<String>());
		
		for (int i = 0; i < declarationDataTypeSegments.length; i = i + 2)
			tempProduct.addId(declarationDataTypeSegments[i]);
		
		setProducttag(tempProduct);
		
		String layoutText = tempProduct.getLayoutText(modelElement);
		setLayout(layoutText);
	}
	
	private void addString(JSONObject modelElement) throws JSONException
	{
		CPNString tempString = new CPNString();
		
		setStringtag(tempString);
		
		String layoutText = tempString.getLayoutText(modelElement);
		setLayout(layoutText);
	}
	
	private void addInteger(JSONObject modelElement) throws JSONException
	{
		CPNInteger tempInteger = new CPNInteger();
		
		setIntegertag(tempInteger);
		
		String layoutText = tempInteger.getLayoutText(modelElement);
		setLayout(layoutText);
	}
	
	private void addBoolean(JSONObject modelElement) throws JSONException
	{
		CPNBoolean tempBoolean = new CPNBoolean();
		
		setBooleantag(tempBoolean);
		
		String layoutText = tempBoolean.getLayoutText(modelElement);
		setLayout(layoutText);
	}
	
	// must be implmented
	private void addList(JSONObject modelElement) throws JSONException
	{
		CPNList tempList = new CPNList();
	}
	
	// ---------------------------------------- Accessory ----------------------------------------

	public void setIdattri(String idattri) {
		this.idattri = idattri;
	}
	public String getIdattri() {
		return idattri;
	}

	public void setIdtag(String idtag) {
		this.idtag = idtag;
	}
	public String getIdtag() {
		return idtag;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}
	public String getLayout() {
		return layout;
	}

	public void setStringtag(CPNString stringtag) {
		this.stringtag = stringtag;
	}

	public CPNString getStringtag() {
		return stringtag;
	}

	public void setProducttag(CPNProduct producttag) {
		this.producttag = producttag;
	}

	public CPNProduct getProducttag() {
		return producttag;
	}

	public void setIntegertag(CPNInteger integertag) {
		this.integertag = integertag;
	}

	public CPNInteger getIntegertag() {
		return integertag;
	}

	public void setBooleantag(CPNBoolean booleantag) {
		this.booleantag = booleantag;
	}

	public CPNBoolean getBooleantag() {
		return booleantag;
	}

	public void setListtag(CPNList listtag) {
		this.listtag = listtag;
	}

	public CPNList getListtag() {
		return listtag;
	}

	public void setUnittag(CPNUnit unittag) {
		this.unittag = unittag;
	}

	public CPNUnit getUnittag() {
		return unittag;
	}

	
	

}
