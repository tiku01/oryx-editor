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
import java.io.FileWriter;
import java.util.ArrayList;


public class ViewGenerator {
	private String overviewHTMLName;
	private String toSavePath;


	public ViewGenerator(String oryxRootDirectory, String basePath) {
		this.overviewHTMLName = "Overview";
		this.toSavePath = oryxRootDirectory + basePath;
	}
	
	public String getOverviewHTMLName() {
		return overviewHTMLName + ".html";
	}
	
	
	public String getToSavePath() {
		return toSavePath;
	}
	
	
	public void generate(ArrayList<String> diagram_Ids) {
		int processes_count = diagram_Ids.size();
				
		ExtractedLanePassing lanePassings = new ExtractedLanePassing(diagram_Ids, toSavePath);
		ExtractedDataMapping dataMapping = new ExtractedDataMapping(diagram_Ids, toSavePath);
		ExtractedCommunications communications = new ExtractedCommunications(diagram_Ids, toSavePath);
		
		String communicationsSVGName = communications.getSVGName();
		communications.generateSVG();
		String lanePassingsSVGName = lanePassings.getSVGName();
		String dataMappingSVGName = dataMapping.getSVGName();
		lanePassings.generateSVG();
		dataMapping.generateSVG();
		int dataObjects_count = dataMapping.getDataObjectsCount();
		int roles_count = lanePassings.getRolesCount();
		int handovers_count = lanePassings.getHandoversCount();
		int interactions_count = communications.getInteractionsCount();
		createHTMLs(communicationsSVGName, lanePassingsSVGName, dataMappingSVGName, processes_count, dataObjects_count, roles_count, handovers_count, interactions_count);	
		}
	
	private String getHTMLNameWithSuffixForStringName(String svgName) {
		return svgName + ".html";
	}

	private void createHTMLs(String communicationsSVGName, String lanePassingsSVGName, String dataMappingSVGName, int processes_count, int dataObjects_count, int roles_count, int handovers_count, int interactions_count) {
		
		String communicationsHTMLName = getHTMLNameWithSuffixForStringName(communicationsSVGName);
		String lanePassingsHTMLName = getHTMLNameWithSuffixForStringName(lanePassingsSVGName);
		String dataMappingHTMLName = getHTMLNameWithSuffixForStringName(dataMappingSVGName);

		createHTMLFileForSVG(communicationsSVGName, communicationsHTMLName, lanePassingsHTMLName, dataMappingHTMLName);
		createHTMLFileForSVG(lanePassingsSVGName, communicationsHTMLName, lanePassingsHTMLName, dataMappingHTMLName);
		createHTMLFileForSVG(dataMappingSVGName, communicationsHTMLName, lanePassingsHTMLName, dataMappingHTMLName);
		createOverviewHTML(communicationsSVGName, communicationsHTMLName, lanePassingsSVGName, lanePassingsHTMLName, dataMappingSVGName, dataMappingHTMLName, processes_count, dataObjects_count, roles_count, handovers_count, interactions_count);
	}
	
	private String getHeaderDiv(int headFontSize, String name, int fontSize, String communicationsHTMLName, String lanePassingsHTMLName, String dataMappingHTMLName) {
		
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
	
	private void createHTMLFileForSVG(String svgName, String communicationsHTMLName, String lanePassingsHTMLName, String dataMappingHTMLName) {
		File htmlFile;
		String svgWidth = "1200";
		String svgHeight = "600";
		String svgNameWithSuffix = svgName + ".svg";
		int fontSize = 4;
		int headFontSize = 6;
		
		String htmlNameWithSuffix = svgName + ".html";
	     try {
	    	  htmlFile = new File(toSavePath + htmlNameWithSuffix);
	          FileWriter fout = new FileWriter(htmlFile);
	          String headerDiv = getHeaderDiv(headFontSize, svgName, fontSize, communicationsHTMLName, lanePassingsHTMLName, dataMappingHTMLName);
	          fout.write(headerDiv);      
	          
//	          embedded svg  
	          fout.write("<br><object data=\""+svgNameWithSuffix+"\" width=\""+svgWidth+"\" height=\""+svgHeight+"\" type=\"image/svg+xml\">");
	          fout.write("<embed src=\""+svgNameWithSuffix+"\" width=\""+svgWidth+"\" height=\""+svgHeight+"\" type=\"image/svg+xml\" />");
	          fout.write("</object>");
	          fout.write("</body></html>");
	          fout.close();
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
	    	  htmlFile = new File(toSavePath + getOverviewHTMLName());
	          FileWriter fout = new FileWriter(htmlFile);
	          
	          String headerDiv = getHeaderDiv(headFontSize, "Project Navigator", fontSize, communicationsHTMLName, lanePassingsHTMLName, dataMappingHTMLName);
	          fout.write(headerDiv);  
	          
//	          Navigator content
	          fout.write("<td><table width=\"100%\">");
	          fout.write("<tr><td><font size = " +fontSize+">This project contains:</font></tr>");
	          fout.write("<tr><td><font size = " +fontSize+">Processes: </font></td>");
	          fout.write("<td><font size = " +fontSize+">"+processes_count+"</font></td></tr>");
	          fout.write("<tr><td><font size = " +fontSize+">Data Objects: </font></td>");
	          fout.write("<td><font size = " +fontSize+">"+dataObjects_count+"</font></td></tr>");
	          fout.write("<tr><td><font size = " +fontSize+">Roles: </font></td>");
	          fout.write("<td><font size = " +fontSize+">"+roles_count+"</font></td></tr>");
	          fout.write("<tr><td><font size = " +fontSize+">Handovers: </font></td>");
	          fout.write("<td><font size = " +fontSize+">"+handovers_count+"</font></td></tr>");
	          fout.write("<tr><td><font size = " +fontSize+">Interactions between partners: </font></td>");
	          fout.write("<td><font size = " +fontSize+">"+interactions_count+"</font></td></tr>");
	          fout.write("</table></td></tr>");
	          
	          fout.write("<td><br></td>");
	          fout.write("<td><br></td>");
	          
//	          Preview Conversation View
	          fout.write("<tr><td>");
	          fout.write("<object data=\""+communicationsSVGNameWithSuffix+"\" width=\""+svgWidth+"\" height=\""+svgHeight+"\" type=\"image/svg+xml\">");
	          fout.write("<embed src=\""+communicationsSVGNameWithSuffix+"\" width=\""+svgWidth+"\" height=\""+svgHeight+"\" type=\"image/svg+xml\" />");
	          fout.write("</object>");
	          fout.write("<A HREF=\""+communicationsHTMLName+"\"><font size = " +fontSize+">Conversation View</font></A></td>");

//	          Preview Handovers
	          fout.write("<td>");
	          fout.write("<object data=\""+lanePassingsSVGNameWithSuffix+"\" width=\""+svgWidth+"\" height=\""+svgHeight+"\" type=\"image/svg+xml\">");
	          fout.write("<embed src=\""+lanePassingsSVGNameWithSuffix+"\" width=\""+svgWidth+"\" height=\""+svgHeight+"\" type=\"image/svg+xml\" />");
	          fout.write("</object>");
	          fout.write("<A HREF=\""+lanePassingsHTMLName+"\"><font size = " +fontSize+">Handovers</font></A></td>");
	          	          
//	          Preview Information Access
	          fout.write("<td>");
	          fout.write("<object data=\""+dataMappingSVGNameWithSuffix+"\" width=\""+svgWidth+"\" height=\""+svgHeight+"\" type=\"image/svg+xml\">");
	          fout.write("<embed src=\""+dataMappingSVGNameWithSuffix+"\" width=\""+svgWidth+"\" height=\""+svgHeight+"\" type=\"image/svg+xml\" />");
	          fout.write("</object>");
	          fout.write("<A HREF=\""+dataMappingHTMLName+"\"><font size = " +fontSize+">Information Access</font></A></td>");
	          fout.write("</tr>");
	          
	          fout.write("</table></body></html>");     
	          fout.close();
	      } 
	      catch (java.io.IOException e) { 
			  e.printStackTrace();
	      }
	}
}
