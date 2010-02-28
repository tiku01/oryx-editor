package de.hpi.ViewGenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ReadWriteAdapter {
//	responsible for requesting diagrams and returning file handles for newly created files
	private String toSavePath;
	private HashMap<String,Document> modelDictionary;
	private String cookie;
	
	public ReadWriteAdapter(ArrayList<String> diagramPaths, String toSavePath, String cookie) {
		this.toSavePath = toSavePath;
		this.cookie = cookie;
		this.initializeModelDictionary(diagramPaths);
	}
	
	private void initializeModelDictionary(ArrayList<String> diagramPaths) {
//		making one request for each diagram and storing them in dictionary
//		instead of making one request everytime when requesting json/svg/description/...
		modelDictionary = new HashMap<String,Document>();
		for (String diagramPath: diagramPaths) {
			try {
				URI uri = new URI(diagramPath);
				URLConnection con = uri.toURL().openConnection();
				con.setRequestProperty("Cookie", cookie);
			
//				seems like used cookie has wrong sessionid
//				File doc = createFile("log");
//				FileWriter fw = new FileWriter(doc);
//				fw.write(cookie);
//				fw.close();
				
				con.connect();
				InputStream st = con.getInputStream();
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
	
	public String getOriginName(String origin) {
		try {
	  		URLConnection originCon = new URL(origin).openConnection();
	  		String originName = originCon.getHeaderField("Content-Disposition");
	  		if (originName == null) {
//	  			intended Header Field not set or local file - treating as local file
	  			originName = origin.substring(origin.lastIndexOf("/")+1,origin.lastIndexOf("."));
	  		}
	  		else {
		  		originName = originName.substring(originName.indexOf("=")+1,originName.lastIndexOf(".") );
	  		}
	  		return originName;
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		}
		catch (IOException e) {
			e.printStackTrace();
			return "";
		}

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
	
	public String getOriginIndexHTMLName(ArrayList<String> attributePair) {
		return getFileName(attributePair.toString())+"_index.html";
	}
	
	public String getOriginSVGName(ArrayList<String> attributePair, int originNumber) {
		return getFileName(attributePair.toString()) + originNumber + ".svg";
	}
	
	public String getOriginHTMLName(ArrayList<String> attributePair) {
		return getFileName(attributePair.toString()) + ".html";
	}
}
