/***************************************
 * Copyright (c) 2010 
 * Martin Krüger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/

package de.hpi.ViewGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import org.apache.commons.lang.StringEscapeUtils;


public class ViewGenerator {
//	responsible for extracting the necessary data and creating/generating all necessary files for the wanted views
	private String overviewHTMLName;
	private ReadWriteAdapter rwa;

	public ViewGenerator(ReadWriteAdapter rwa) {
		this.overviewHTMLName = "Overview";
		this.rwa = rwa;
	}
	
	public String getOverviewHTMLName() {
//		name of the generated file for overview/project navigator file
		return overviewHTMLName + ".html";
	}
	
	
	public String getToSavePath() {
		return rwa.getToSavePath();
	}
	
	
	public void generate(ArrayList<String> diagram_Ids) {
//		processes_count represents the number of input diagrams
//		and will be presented on the project navigator page
		int processes_count = diagram_Ids.size();
		
//		three views to generate:
//		lanePassings/Handovers, dataMapping/Information Access, communications/Interactions
//		
//		for each view instantiate corresponding ExtractedData-SubClass
		ExtractedLanePassing lanePassings = new ExtractedLanePassing(rwa);
		ExtractedDataMapping dataMapping = new ExtractedDataMapping(rwa);
		ExtractedCommunications communications = new ExtractedCommunications(rwa);
		
////		tell the SubClasses to generate a SVG-representation of their hold data
//		communications.generateSVG();
//		lanePassings.generateSVG();
//		dataMapping.generateSVG();
		
//		ask for the names of the generated files for embedding it in html
		String communicationsSVGName = "Conversation View";
		String lanePassingsSVGName = "Handovers";
		String dataMappingSVGName = "Information Access";

//		ask for specific values which the SubClasses have counted
//		these will be listed in overview/project navigator html
		
		int dataObjects_count = dataMapping.getDataObjectsCount();
		int roles_count = lanePassings.getRolesCount();
		int handovers_count = lanePassings.getHandoversCount();
		int interactions_count = communications.getInteractionsCount();
		
//		get the ExtractedConnectionLists
		ExtractedConnectionList extractedCommunications = communications.getExtractedConnectionList();
		ExtractedConnectionList extractedLanePassings = lanePassings.getExtractedConnectionList();
		ExtractedConnectionList extractedDataMappings = dataMapping.getExtractedConnectionList();
		
//		create OriginSVGs and OriginHTMLs
		createOriginFiles(extractedCommunications);
		createOriginFiles(extractedLanePassings);
		createOriginFiles(extractedDataMappings);
		
//		generate Views
		SVGGenerator generator = new SVGGenerator(rwa);
		generator.generateConversationView(extractedCommunications, communicationsSVGName);
		generator.generateHandoversView(extractedLanePassings, lanePassingsSVGName);
		generator.generateInformationAccessView(extractedDataMappings, dataMappingSVGName);
		
//		create all other HTMLs
		createHTMLs(communicationsSVGName, lanePassingsSVGName, dataMappingSVGName, processes_count, dataObjects_count, roles_count, handovers_count, interactions_count);	
		}
	
	private String getHTMLNameWithSuffixForStringName(String name) {
		return name + ".html";
	}

	private void createHTMLs(String communicationsSVGName, String lanePassingsSVGName, String dataMappingSVGName, int processes_count, int dataObjects_count, int roles_count, int handovers_count, int interactions_count) {
		
//		getting corresponding HTMLNamesWithSuffix for svgNames without ".svg"
		String communicationsHTMLName = getHTMLNameWithSuffixForStringName(communicationsSVGName);
		String lanePassingsHTMLName = getHTMLNameWithSuffixForStringName(lanePassingsSVGName);
		String dataMappingHTMLName = getHTMLNameWithSuffixForStringName(dataMappingSVGName);

		createHTMLFileForSVG(communicationsSVGName, communicationsHTMLName, lanePassingsHTMLName, dataMappingHTMLName);
		createHTMLFileForSVG(lanePassingsSVGName, communicationsHTMLName, lanePassingsHTMLName, dataMappingHTMLName);
		createHTMLFileForSVG(dataMappingSVGName, communicationsHTMLName, lanePassingsHTMLName, dataMappingHTMLName);
		createOverviewHTML(communicationsSVGName, communicationsHTMLName, lanePassingsSVGName, lanePassingsHTMLName, dataMappingSVGName, dataMappingHTMLName, processes_count, dataObjects_count, roles_count, handovers_count, interactions_count);
	}
	
	private String getHeaderDiv(int headFontSize, String name, int fontSize, String communicationsHTMLName, String lanePassingsHTMLName, String dataMappingHTMLName) {
//		the html-div for the header with linkline and headline
        String headerDiv = "<!doctype html>\n" + 
        "<html>\n<head>\n<title>"+name+"</title>\n</head>" +    
        "<link href=\"../static/style.css\" rel=\"stylesheet\" type=\"text/css\">" + 
        "</head>" +
        "<body>" + 
        "<div style=\"left: 0px; top: 0px; width: 1152px;\">" + 
        "<div class=\"upper\" style=\"width: 1152px; height: 35px;\">" +
        "<div id=\"viewgenerator_header\">" +
        "<img title=\"Oryx\" alt=\"\" id=\"Oryx_logo\" src=\"\"></a>" +
        "<font size = "+headFontSize+" color=\"#0D0D0D \"><b>"+name+"</b></font>" +
        "</div></div></div>" +
        
        "<div class=\"lower\" style=\"height: 35px; left: 0px; top: 35px; width: 1152px;\">" +
        "<table cellspacing=\"0\"><tbody><tr>" +
        
//        linkLine	          
        "<td>" +
        "<div class=\"linkLineElement\">" +
        "<A HREF=\""+getOverviewHTMLName()+"\"><font size = " +fontSize+" color=\"#0D0D0D \">Project Navigator</font></A></td><td>" +
        "</div>" +
        "<div class=\"linkLineElement\">" +
        "<A HREF=\""+communicationsHTMLName+"\"><font size = " +fontSize+" color=\"#0D0D0D \">Conversation View</font></A></td><td>" +
        "</div>" +
        "<div class=\"linkLineElement\">" +
        "<A HREF=\""+lanePassingsHTMLName+"\"><font size = " +fontSize+" color=\"#0D0D0D \">Handovers</font></A></td><td>" +
        "</div>" +
        "<div class=\"linkLineElement\">" +
        "<A HREF=\""+dataMappingHTMLName+"\"><font size = " +fontSize+" color=\"#0D0D0D \">Information Access</font></A></td><td>" +
        "</div>" +
        "</tr></tbody></table></div></div></div></td>" +	 
        
        "<td>" +
        "<br><br><table width=\"100%\">" +	          
        "<tr>";
        return headerDiv;
	}
	
	private String getEmbeddedSVGDiv(String sourceNameWithSuffix, String svgHeight, String svgWidth) {
//		html-div for embedded svgs
		String embeddedSVGDiv = "<object data=\""+sourceNameWithSuffix+"\" width=\""+svgWidth+"\" height=\""+svgHeight+"\" type=\"image/svg+xml\">"+
        	"<embed src=\""+sourceNameWithSuffix+"\" width=\""+svgWidth+"\" height=\""+svgHeight+"\" type=\"image/svg+xml\" />"+
        	"</object>";
		return embeddedSVGDiv;
	}
	
	private void createHTMLFileForSVG(String svgName, String communicationsHTMLName, String lanePassingsHTMLName, String dataMappingHTMLName) {
		File htmlFile;
		String svgWidth = "1200";
		String svgHeight = "600";
		String svgNameWithSuffix = svgName + ".svg";
		int fontSize = 4;
		int headFontSize = 6;
		
		String htmlNameWithSuffix = svgName + ".html";
	     try {
	    	  htmlFile = new File(getToSavePath() + htmlNameWithSuffix);
	    	  
	    	  OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(htmlFile),"UTF-8");
	          String headerDiv = getHeaderDiv(headFontSize, svgName, fontSize, communicationsHTMLName, lanePassingsHTMLName, dataMappingHTMLName);
	          out.write(headerDiv);      
	          
//	          embedded svg 
	          String embeddedSVGDiv = getEmbeddedSVGDiv(svgNameWithSuffix, svgHeight, svgWidth);
	          out.write("<br>");
	          out.write(embeddedSVGDiv);
	          out.write("</body></html>");
	          out.close();
	     }
	     catch (java.io.IOException e) { 
	    	 e.printStackTrace();
	     }
	}
	
	
	private void createOverviewHTML(String communicationsSVGName, String communicationsHTMLName, String lanePassingsSVGName, String lanePassingsHTMLName, String dataMappingSVGName, String dataMappingHTMLName, int processes_count, int dataObjects_count, int roles_count, int handovers_count, int interactions_count) {
		File htmlFile;
		int headFontSize = 6;
		int fontSize = 4;
		String svgHeight = "200";
		String svgWidth = "200";
		String communicationsSVGNameWithSuffix = communicationsSVGName + ".svg";
		String lanePassingsSVGNameWithSuffix = lanePassingsSVGName + ".svg";
		String dataMappingSVGNameWithSuffix = dataMappingSVGName + ".svg";
		
	      try {  
	    	  htmlFile = new File(getToSavePath() + getOverviewHTMLName());
	    	  OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(htmlFile),"UTF-8");
	       
	          String headerDiv = getHeaderDiv(headFontSize, "Project Navigator", fontSize, communicationsHTMLName, lanePassingsHTMLName, dataMappingHTMLName);
	          out.write(headerDiv);  
	          
//	          Navigator content
	          out.write("<td><table width=\"100%\">");
	          out.write("<tr><td><font size = " +fontSize+">This project contains:</font></tr>");
	          out.write("<tr><td><font size = " +fontSize+">Processes: </font></td>");
	          out.write("<td><font size = " +fontSize+">"+processes_count+"</font></td></tr>");
	          out.write("<tr><td><font size = " +fontSize+">Data Objects: </font></td>");
	          out.write("<td><font size = " +fontSize+">"+dataObjects_count+"</font></td></tr>");
	          out.write("<tr><td><font size = " +fontSize+">Roles: </font></td>");
	          out.write("<td><font size = " +fontSize+">"+roles_count+"</font></td></tr>");
	          out.write("<tr><td><font size = " +fontSize+">Handovers: </font></td>");
	          out.write("<td><font size = " +fontSize+">"+handovers_count+"</font></td></tr>");
	          out.write("<tr><td><font size = " +fontSize+">Interactions between partners: </font></td>");
	          out.write("<td><font size = " +fontSize+">"+interactions_count+"</font></td></tr>");
	          out.write("</table></td></tr>");
	          
	          out.write("<td><br></td>");
	          out.write("<td><br></td>");
	          
//	          Preview Conversation View
	          out.write("<tr><td>");
	          String previewConversationView = getEmbeddedSVGDiv(communicationsSVGNameWithSuffix, svgHeight, svgWidth);
	          out.write(previewConversationView);
	          out.write("<A HREF=\""+communicationsHTMLName+"\"><font size = " +fontSize+">Conversation View</font></A></td>");

//	          Preview Handovers
	          out.write("<td>");
	          String previewHandovers = getEmbeddedSVGDiv(lanePassingsSVGNameWithSuffix, svgHeight, svgWidth);
	          out.write(previewHandovers);
	          out.write("<A HREF=\""+lanePassingsHTMLName+"\"><font size = " +fontSize+">Handovers</font></A></td>");
	          	          
//	          Preview Information Access
	          out.write("<td>");
	          String previewInformationAccess = getEmbeddedSVGDiv(dataMappingSVGNameWithSuffix, svgHeight, svgWidth);
	          out.write(previewInformationAccess);
	          out.write("<A HREF=\""+dataMappingHTMLName+"\"><font size = " +fontSize+">Information Access</font></A></td>");
	          out.write("</tr>");
	          
	          out.write("</table></body></html>");     
	          out.close();
	      } 
	      catch (java.io.IOException e) { 
			  e.printStackTrace();
	      }
	}
	
	private String getSVG(String diagramPath) {
		return rwa.getSVG(diagramPath);
	}
		
	private String getDescription(String diagramPath) {
		return rwa.getDescription(diagramPath);
	}
		
	private String getOriginIndexHTMLName(ArrayList<String> attributePair) {
		return rwa.getOriginIndexHTMLName(attributePair);
	}
	
	private String getOriginSVGName(ArrayList<String> attributePair, int originNumber) {
		return rwa.getOriginSVGName(attributePair, originNumber);
	}
	
	private String getOriginHTMLName(ArrayList<String> attributePair) {
		return rwa.getOriginHTMLName(attributePair);
	}
				
	private String replaceSpecialCharsForHTML(String s) {
//		replacing special chars related to displaying in html
		String fileName_new = StringEscapeUtils.escapeHtml(s);
		return fileName_new;
	}
		
	private void createOriginFiles(ExtractedConnectionList extractedConnectionList) {
		createOriginSVGs(extractedConnectionList);
		createOriginsHTMLs(extractedConnectionList);
	}
		
	private File createOriginSVG(ArrayList<String> attributePair, String diagramPath, int originNumber) {
		File svgFile;
		String svg = getSVG(diagramPath);
	      try {
	    	  String fileName = getOriginSVGName(attributePair, originNumber);
	    	  svgFile = rwa.createFile(fileName);
	    	  OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(svgFile),"UTF-8");
	          out.write(svg);
	          out.close();
	          return svgFile;
	      } 	      
	      catch (java.io.IOException e) { 
			  e.printStackTrace();
			  return null;
	      }
	}
	
	private void createOriginSVGs(ExtractedConnectionList extractedConnectionList) {
//		the variable right frame of an originsHTML
		for (ArrayList<String> attributePair: extractedConnectionList.connectionAttributePairs()) {	
			ArrayList<String> origins = extractedConnectionList.getOriginsForConnectionAttributePair(attributePair);

			for (int i=0; i<origins.size(); i++) {
				String diagramPath = origins.get(i);
				createOriginSVG(attributePair, diagramPath, i);
			}
		}
	}
	
	private File createOriginsIndexHTML(ArrayList<String> attributePair, ArrayList<String> origins) {
//		the fixed left frame of an originsHTML
		int fontSize = 4;
		File htmlFile;
		
		try {
			String fileName = getOriginIndexHTMLName(attributePair);
		    htmlFile = rwa.createFile(fileName);	    
	    	OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(htmlFile),"UTF-8");
	
		    out.write("<!doctype html>\n");
		    out.write("<html><head>");
		    out.write("<script type=\"text/javascript\" src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js\"></script>");
		    out.write("<script type=\"text/javascript\" src=\"../static/infoBox.js\"></script>");
		    out.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"../static/infoBox.css\" />");
		    out.write("</head>");
		    out.write("<body><h3><center>Origins for "+replaceSpecialCharsForHTML(attributePair.toString())+"</center></h3><hr>");

		  	for (int i=0; i<origins.size(); i++) {
		  		String originSVG = getOriginSVGName(attributePair, i);
		  		String origin = origins.get(i);
		  		String originName = rwa.getOriginName(origin);		  		
		  		out.write("<div class=\"origin\">");
			    out.write("<div class=\"originName\">");
		        out.write("<td><A HREF=\""+originSVG+"\" target=\"content\"><font size = " +fontSize+">"+replaceSpecialCharsForHTML(originName)+"</font></A></td>");
			    out.write("</div>");
			    out.write("<div class=\"infoBox\">");
			    String description = replaceSpecialCharsForHTML(getDescription(origins.get(i)));
			    if (description.equals("")) {
				    out.write("<font>No Description given.</font>");
			    }
			    else {
				    out.write("<font>"+description+"</font>");
			    }
			    out.write("</div>");
			    out.write("</div>");
		  	}
	        out.write("</body></html>");
	        out.close();
	        return htmlFile;	
		} 	      
		catch (java.io.IOException e) { 
			e.printStackTrace();
			return null;
		}
	}
	
	private void createOriginsHTMLs(ExtractedConnectionList extractedConnectionList) {
		for (ArrayList<String> attributePair: extractedConnectionList.connectionAttributePairs()) {
			ArrayList<String> origins = extractedConnectionList.getOriginsForConnectionAttributePair(attributePair);
			createOriginsHTML(attributePair, origins);	
		}
	}
	
	private File createOriginsHTML(ArrayList<String> attributePair, ArrayList<String> origins) {
		createOriginsIndexHTML(attributePair, origins);
		File htmlFile;
		try {
			String fileName = getOriginHTMLName(attributePair);
		    htmlFile = rwa.createFile(fileName);
		    
	    	OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(htmlFile),"UTF-8");
		    out.write("<!doctype html>\n");
		    out.write("<html>\n<head>\n<title>Origins for " +replaceSpecialCharsForHTML(attributePair.toString())+ "</title>\n</head>");
		    out.write("<frameset cols=\"15%,85%\">");
		    out.write("<body>");
		    out.write("<frame name=index src=\""+ getOriginIndexHTMLName(attributePair)+"\">");
		    out.write("<frame name=content src=\""+ getOriginSVGName(attributePair, 0) + "\">");
		    out.write("</frameset>");
	        out.write("</body></html>");
	        out.close();
	        return htmlFile;	
		} 	    	
		catch (java.io.IOException e) { 
			e.printStackTrace();
			return null;
		}
	}
}
