package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.b3mn.poem.util.JsonErdfTransformation;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.DiagramObject;
import de.hpi.bpmn.extract.ExtractProcessConfiguration;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn.serialization.erdf.BPMNeRDFSerializer;
import de.hpi.bpmn.validation.BPMNValidator;
import de.hpi.bpt.process.epc.EPCFactory;
import de.hpi.bpt.process.epc.IControlFlow;
import de.hpi.bpt.process.epc.IEPC;
import de.hpi.bpt.process.epc.util.OryxParser;
import de.hpi.epc.Marking;
import de.hpi.epc.validation.EPCSoundnessChecker;

/**
 * Copyright (c) 2009 Willi Tscheschner
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


public class ExtractCoreProcessServlet extends HttpServlet {
	
	private static final long serialVersionUID = -1;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			res.setContentType("text/json");

			String modelA = req.getParameter("modelA");
			String modelB = req.getParameter("modelB");

			
			BPMNDiagram  extractModel = new ExtractProcessConfiguration(getDiagram(modelA), getDiagram(modelB)).extract();
			
			BPMNeRDFSerializer serializer = new BPMNeRDFSerializer();
	    	String eRDF = serializer.serializeBPMNDiagram(extractModel);
	    	
			res.getWriter().print(eRDF);
			
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	private BPMNDiagram getDiagram(String rdf) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException{
		
		//JsonErdfTransformation jsonTransform = new JsonErdfTransformation(json)
		//String erdf = jsonTransform.toString();
	 
		DocumentBuilder builder;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(rdf.getBytes("UTF-8")));
		
		
		return new BPMNRDFImporter(doc).loadBPMN();
		
	}
}
