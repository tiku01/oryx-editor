package de.hpi.ViewGenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadWriteAdapter {
	private String toSavePath;
	private HashMap<String,Document> modelDictionary;
	private Cookie[] cookies;
	
	public ReadWriteAdapter(ArrayList<String> diagramPaths, String toSavePath, Cookie[] cookies) {
		this.toSavePath = toSavePath;
		this.initializeModelDictionary(diagramPaths);
		this.cookies = cookies;
	}

	private void initializeModelDictionary(ArrayList<String> diagramPaths) {
		modelDictionary = new HashMap<String,Document>();
		for (String diagramPath: diagramPaths) {
			try {
				URI uri = new URI(diagramPath);
				InputStream st = uri.toURL().openStream();
				Document xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(st);
				
			  	modelDictionary.put(diagramPath,xml);
			}
			catch (URISyntaxException ue) {
//				invalid parameter
				ue.printStackTrace();
			}
			catch (ParserConfigurationException pe) {
				pe.printStackTrace();
			}
			catch (SAXException se) {	
				se.printStackTrace();
			}
			catch (IOException ioe) { 
				ioe.printStackTrace(); 
			}
		}
	}
		
	public String getJSON(String diagramPath) {
		Document xml = modelDictionary.get(diagramPath);			
		NodeList nodeList = xml.getElementsByTagName("json-representation");
		Node jsonNode = nodeList.item(0);	
		String json = jsonNode.getTextContent();
				
		return json;
	}
		
		
	public String getSVG(String diagramPath) {
		Document xml = modelDictionary.get(diagramPath);
	  	NodeList nodeList = xml.getElementsByTagName("svg-representation");
	  	Node svgNode = nodeList.item(0);	
		String svg = svgNode.getTextContent();
			
		return svg;
	}
		
		
	public String getDescription(String diagramPath) {
		Document xml = modelDictionary.get(diagramPath);	
		NodeList nodeList = xml.getElementsByTagName("description");
		Node descriptionNode = nodeList.item(0);	
		String description = descriptionNode.getTextContent();
				
		return description;
	}
	
	public File createFile(String fileName) {
		return new File(toSavePath + fileName);
	}
	
	public String getToSavePath() {
		return toSavePath;
	}
	
	public Set<String> getDiagramPaths() {
		return modelDictionary.keySet();
	}
	
	public String getFileName(String fileName) {
//		using hashCode so caseSensitive fileNames will not result in same file on windows systems
		return fileName.hashCode()+"";
	}
}
