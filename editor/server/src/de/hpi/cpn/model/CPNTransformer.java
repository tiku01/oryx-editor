package de.hpi.cpn.model;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import org.json.JSONException;
import org.json.JSONObject;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class CPNTransformer
{
	private CPNGlobbox globbox = new CPNGlobbox();
	private ArrayList<CPNPage> pages = new ArrayList<CPNPage>();
	
	
	public String transformtoCPN(String json) throws JSONException 
	{
		// TODO Auto-generated method stub
		/*
		 * do it for globbox and for pages
		 * */
		
		
		XStream xstream = new XStream(new DomDriver());
		
		CPNWorkspaceElement workElement = new CPNWorkspaceElement();
		
		workElement.parse(new JSONObject(json));
		
		CPNWorkspaceElement.registerMapping(xstream);
		
		String Result = xstream.toXML(workElement);
		
		
//		String globboxXMl = transformDeclaration(json);
//		String pageXML = transformPage(json);
//		String Result = "<workspaceElements><generator tool=\"CPN Tools\" version=\"2.2.0\" format=\"6\"/><cpnet>" + globboxXMl + pageXML + "</cpnet></workspaceElements>";
		
		return Result;
	}
	

	private String transformDeclaration(String json) throws JSONException 
	{
		
		getGlobbox().parse(new JSONObject(json));
		
		XStream xstream = new XStream(new DomDriver());
		CPNGlobbox.registerMapping(xstream);
		
		return xstream.toXML(getGlobbox());
	}
	
	private String transformPage(String json) throws JSONException
	{
		CPNPage page = new CPNPage();
			
		page.setId("ID30001"); // nur Provisorisch
		
		
		JSONObject jsonOb = new JSONObject(json);
		
		page.prepareResourceIDictionary(jsonOb);
		
		page.parse(jsonOb);
		
		XStream xstream = new XStream(new DomDriver());
		CPNPage.registerMapping(xstream);
		
		getPages().add(page);
		
		return xstream.toXML(getPages());
	}
	
//	public String[] getAllPages(String cpnXML)
//	{
//		ArrayList<String> allPages = new ArrayList<String>();
//		XStream xstream = new XStream(new DomDriver());
//		
//		CPNWorkspaceElement.registerMapping(xstream);
//		
//		CPNWorkspaceElement workElement = (CPNWorkspaceElement) xstream.fromXML(cpnXML);
//		
//		ArrayList<CPNPage> pages = workElement.getCpnet().getPages();
//		
//		for (int i = 0; i < pages.size(); i++)
//		{	
//			CPNPage tempPage = pages.get(i);
//			
//			allPages.add(tempPage.getPageattr().getName());
//		}
//		
//		
//		
//		return null;
//	}
	
	public void fromXML(String xml)
	{
		try
		{

			XStream xstream = new XStream(new DomDriver());
			
			CPNWorkspaceElement.registerMapping(xstream);
			
			CPNWorkspaceElement workElement = (CPNWorkspaceElement) xstream.fromXML(xml);
			
			CPNToolsTranslator translator = new CPNToolsTranslator(workElement);
			
			String oryxJson = translator.transfromCPNFile();
			
			translator = new CPNToolsTranslator(workElement);
			String[] pagesToImport = { workElement.getCpnet().getPage(0).getPageattr().getName(), workElement.getCpnet().getPage(1).getPageattr().getName() };
			String oryxJson2 = translator.transformCPNFile(pagesToImport);
			
			
//			System.out.println(oryxJson.equals(oryxJson2));
			
			
			int i = 0;
			i = 2;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void fromXML(String xml, String[] definedPages)
	{
		try
		{

			XStream xstream = new XStream(new DomDriver());
			
			CPNWorkspaceElement.registerMapping(xstream);
			
			CPNWorkspaceElement workElement = (CPNWorkspaceElement) xstream.fromXML(xml);
			
			CPNToolsTranslator translator = new CPNToolsTranslator(workElement);
			
			String oryxJson = translator.transfromCPNFile();
			
//			oryxJson = oryxJson.replaceAll("\"", "'");
//			oryxJson = oryxJson.replaceAll("",	"");
			
			System.out.println(oryxJson);
			
			
			int i = 0;
			i = 2;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void setGlobbox(CPNGlobbox globbox) {
		this.globbox = globbox;
	}

	public CPNGlobbox getGlobbox() {
		return globbox;
	}

	public void setPages(ArrayList<CPNPage> pages) {
		this.pages = pages;
	}
	public ArrayList<CPNPage> getPages() {
		return pages;
	}
	public void addPage(CPNPage page)
	{
		this.pages.add(page);
	}

	

}
