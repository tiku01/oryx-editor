package org.oryxeditor.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import de.hpi.PTnet.PTNet;
import de.hpi.PTnet.serialization.PTNetRDFImporter;
import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.BPMNFactory;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn.rdf.BPMNRDFImporter;
import de.hpi.bpmn2pn.converter.Preprocessor;
import de.hpi.bpmn2pn.converter.StandardConverter;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.converter.IBPMNConverter;
import de.hpi.ibpmn.rdf.IBPMNRDFImporter;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.serialization.InteractionNetPNMLExporter;
import de.hpi.interactionnet.serialization.InteractionNetRDFImporter;
import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.serialization.PetriNetPNMLExporter;
import de.hpi.petrinet.serialization.PetriNetPNMLExporter.Tool;
import de.hpi.util.LibConfigToJsonConvert;

/**
 * Copyright (c) 2010 Philipp Berger
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
public class LolaPetrinetSoundnessChecker extends HttpServlet {

	private static final String INPUT_TEXT 		= "input";
//	private static final String TOOL 			= "tool";
//	private static final String INPUT_FORMAT 	= "input_format";
//	private static final String PNML 			= "pnml";
//	private static final String INPUT			= "input";
//	private static final String TEXT			= "text";
	private static final String LOLA_URI 		= "http://esla.informatik.uni-rostock.de/service-tech/.lola/lola.php";
	private static final long serialVersionUID = 6150856095430348410L;
	private Tool tool;
	public Tool getTool() {
		return this.tool;
	}
	public void setTool(Tool tool) {
		this.tool = tool;
	}
	/**
	 * @param pnmlDoc
	 * @param net
	 * @param exp
	 */
	private void convertNetToDocumentUsing(Document pnmlDoc, PetriNet net,
			PetriNetPNMLExporter exp) {
		exp.setTargetTool(getTool());
		/*
		 * if no initial Marking given set one token to the inital place
		 */
		if(net.getInitialMarking().getNumTokens()==0){
			net.getInitialMarking().setNumTokens(net.getInitialPlace(), 1);
		}
		exp.savePetriNet(pnmlDoc, net);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			res.setContentType("application/xhtml");
			/*
			 * get the rdf
			 */
			String rdf = req.getParameter("data");
			this.setTool( PetriNetPNMLExporter.Tool.LOLA);
			
			/*
			 * transform to xml document
			 */
			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes()));
			Document pnmlDoc = builder.newDocument();

			processDocument(document, pnmlDoc);

			OutputFormat format = new OutputFormat(pnmlDoc);
			/*
			 * write pnml to String
			 */
			StringWriter stringOut = new StringWriter();
			XMLSerializer serial2 = new XMLSerializer(stringOut, format);
			serial2.asDOMSerializer();
			serial2.serialize(pnmlDoc.getDocumentElement());
			
			
			sendPostToLola(res, stringOut.toString(), "lola-deadlock");

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	protected void processBPMN(Document document, Document pnmlDoc) {
		BPMNRDFImporter importer = new BPMNRDFImporter(document);
		BPMNDiagram diagram = (BPMNDiagram) importer.loadBPMN();
		new Preprocessor(diagram, new BPMNFactory()).process();

		PetriNet net = new StandardConverter(diagram).convert();

		PetriNetPNMLExporter exp = new PetriNetPNMLExporter();
		convertNetToDocumentUsing(pnmlDoc, net, exp);
	}

	protected void processBPMN11(Document document, Document pnmlDoc) {
		BPMN11RDFImporter importer = new BPMN11RDFImporter(document);
		BPMNDiagram diagram = (BPMNDiagram) importer.loadBPMN();
		new Preprocessor(diagram, new BPMNFactory()).process();

		PetriNet net = new StandardConverter(diagram).convert();

		PetriNetPNMLExporter exp = new PetriNetPNMLExporter();
		convertNetToDocumentUsing(pnmlDoc, net, exp);
	}

	protected void processDocument(Document document, Document pnmlDoc) {
		String type = new StencilSetUtil().getStencilSet(document);
		if (type.equals("ibpmn.json"))
			processIBPMN(document, pnmlDoc);
		else if (type.equals("bpmn.json") || type.equals("bpmnexec.json") || type.equals("bpmnexecutable.json"))
			processBPMN(document, pnmlDoc);
		else if (type.equals("bpmn1.1.json"))
			processBPMN11(document, pnmlDoc);
		else if (type.equals("interactionpetrinets.json"))
			processIPN(document, pnmlDoc);
		else if (type.equals("petrinet.json"))
			processPN(document, pnmlDoc);
	}


	protected void processIBPMN(Document document, Document pnmlDoc) {
		IBPMNRDFImporter importer = new IBPMNRDFImporter(document);
		BPMNDiagram diagram = (IBPMNDiagram) importer.loadIBPMN();

		PetriNet net = new IBPMNConverter(diagram).convert();

		InteractionNetPNMLExporter exp = new InteractionNetPNMLExporter();
		convertNetToDocumentUsing(pnmlDoc, net, exp);

	}

	protected void processIPN(Document document, Document pnmlDoc) {
		InteractionNetRDFImporter importer = new InteractionNetRDFImporter(document);
		InteractionNet net = (InteractionNet) importer.loadInteractionNet();

		InteractionNetPNMLExporter exp = new InteractionNetPNMLExporter();
		convertNetToDocumentUsing(pnmlDoc, net, exp);

	}

	protected void processPN(Document document, Document pnmlDoc) {
		PTNetRDFImporter importer = new PTNetRDFImporter(document);
		PTNet net = (PTNet) importer.loadPTNet();

		PetriNetPNMLExporter exp = new PetriNetPNMLExporter();
		convertNetToDocumentUsing(pnmlDoc, net, exp);

	}

	/**
	 * Sends a request to the Lola Webservice and write the response to the {@link HttpServletResponse}
	 * @param res the response to write on
	 * @param tool the tool which should be called
	 * @param pnmlAsString 
	 * @throws IOException 
	 */
	private void sendPostToLola(HttpServletResponse res, String pnmlAsString, String tool)
	throws 
	IOException {
		URLConnection con;
		try{
		URL lola = new URL(LOLA_URI);
		con = lola.openConnection();
		con.setDoOutput(true);
		con.setUseCaches(false);
		}catch (MalformedURLException e) {
			writeException(res, e);
			return;
		}catch (IOException e) {
			writeException(res, e);
			return;
		}
		StringBuilder databld = new StringBuilder();
		databld.append(		   INPUT_TEXT	+	"=" + URLEncoder.encode(pnmlAsString, "UTF-8"));
//		databld.append(	"&"	+ 	INPUT		+	"=" + TEXT);
//		databld.append(	"&"	+ INPUT_FORMAT	+	"=" + PNML);
//		databld.append(	"&"	+ 	TOOL		+	"=" + tool);
		OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

		//write parameters
		writer.write(databld.toString());
		writer.flush();
		StringBuffer answer = new StringBuffer();
		BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			answer.append(line);
		}

		try {
			String a =answer.toString();
			/*
			 * remove added pre container if there
			 */
			if(a.startsWith("<pre>")){
				a = a.replace("<pre>","");
				a = a.replace("</pre>", "");
			}
			JSONObject rs = LibConfigToJsonConvert.parseString(a);
			rs.put("errors", new JSONArray());
			res.getWriter().print(rs);
		} catch (JSONException e) {
			writeException(res, e);
		}
	}
	/**
	 * @param res
	 * @param e
	 * @throws IOException
	 */
	private void writeException(HttpServletResponse res, Exception e)
			throws IOException {
		JSONObject o = new JSONObject();
		JSONArray a = new JSONArray();
		a.put(e.getLocalizedMessage());
		try {
			o.put("errors", a);
		} catch (JSONException e1) {
			// nothing to do
		}
		res.getWriter().print(o);
	}


}
