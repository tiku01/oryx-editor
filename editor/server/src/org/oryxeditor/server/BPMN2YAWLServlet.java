package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.rdf.BPMN11RDFImporter;
import de.hpi.bpmn.rdf.BPMNRDFImporter;

import de.hpi.bpmn2yawl.BPMN2YAWLConverter;
import de.hpi.bpmn2yawl.BPMN2YAWLSyntaxChecker;
import de.hpi.bpmn2yawl.BPMN2YAWLNormalizer;

/**
 * Copyright (c) 2009 Armin Zamani
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
public class BPMN2YAWLServlet extends HttpServlet {
	private static final long serialVersionUID = -4589304713944851993L;

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		try {
			System.out.println("Hello, I am the Servlet.");
			//TODO correct mime type??
			res.setContentType("application/xhtml");

			String rdf = req.getParameter("data");
			//String rdf="<rdf:RDF><rdf:Description rdf:about=\"\"><admin:generatorAgent rdf:resource=\"http://purl.org/NET/erdf/extract\"/></rdf:Description><rdf:Description rdf:about=\"\"/><rdf:Description rdf:about=\"#oryx-canvas123\"><rdf:type rdf:resource=\"http://oryx-editor.org/canvas\"/><type>http://b3mn.org/stencilset/bpmn1.1#BPMNDiagram</type><id/><name/><version/><author/><language>English</language><expressionlanguage/><querylanguage/><creationdate/><modificationdate/><pools/><documentation/><mode>writable</mode><mode>fullscreen</mode><stencilset rdf:resource=\"/oryx/stencilsets/bpmn1.1/bpmn1.1.json\"/><render rdf:resource=\"#oryx_D5380587-FE13-4212-953C-5DCA6C04FC8F\"/><render rdf:resource=\"#oryx_D83E984D-C164-44D7-8C08-23FBC17343CB\"/><render rdf:resource=\"#oryx_7DDEFA34-AD47-4449-AD51-FD2E4F2051C3\"/><render rdf:resource=\"#oryx_718EE88F-DFA0-44B9-B503-4438FA6A1AA6\"/><render rdf:resource=\"#oryx_3FA71C47-F560-456C-AA87-75BC775D4A03\"/></rdf:Description><rdf:Description rdf:about=\"#oryx_D5380587-FE13-4212-953C-5DCA6C04FC8F\"><type>http://b3mn.org/stencilset/bpmn1.1#StartEvent</type><id/><categories/><documentation/><name/><assignments>{'totalCount':2, 'items':[{to:\"first\", from:\"second\", assigntime:\"Start\"}, {to:\"second\", from:\"Helo\", assigntime:\"End\"}]}</assignments><pool/><lanes/><eventtype>Start</eventtype><trigger>None</trigger><bgcolor>#ffffff</bgcolor><bounds>80.99998474121094,186.99999618530273,110.99998474121094,216.99999618530273</bounds><outgoing rdf:resource=\"#oryx_7DDEFA34-AD47-4449-AD51-FD2E4F2051C3\"/><parent rdf:resource=\"#oryx-canvas123\"/></rdf:Description><rdf:Description rdf:about=\"#oryx_D83E984D-C164-44D7-8C08-23FBC17343CB\"><type>http://b3mn.org/stencilset/bpmn1.1#Task</type><id>a</id><categories/><documentation/><name>a</name><assignments>{'totalCount':2, 'items':[{to:\"first\", from:\"second\", assigntime:\"Start\"}, {to:\"second\", from:\"Helo\", assigntime:\"End\"}]}</assignments><pool/><lanes/><activitytype>Task</activitytype><status>None</status><performers/><properties>{'totalCount':1, 'items':[{name:\"first\", type:\"String\", value:\"Hallo\", correlation:\"false\"}]}</properties><inputsets/><inputs/><outputsets/><outputs/><iorules/><startquantity>1</startquantity><completionquantity>1</completionquantity><looptype>None</looptype><loopcondition/><loopcounter>1</loopcounter><loopmaximum>1</loopmaximum><testtime>After</testtime><mi_condition/><mi_ordering>Sequential</mi_ordering><mi_flowcondition>All</mi_flowcondition><complexmi_condition/><iscompensation/><tasktype>None</tasktype><inmessage/><outmessage/><implementation>Webservice</implementation><messageref/><instantiate/><script/><taskref/><bgcolor>#ffffcc</bgcolor><bounds>185.99998474121094,161.99999618530273,285.99998474121094,241.99999618530273</bounds><outgoing rdf:resource=\"#oryx_3FA71C47-F560-456C-AA87-75BC775D4A03\"/><parent rdf:resource=\"#oryx-canvas123\"/></rdf:Description><rdf:Description rdf:about=\"#oryx_7DDEFA34-AD47-4449-AD51-FD2E4F2051C3\"><type>http://b3mn.org/stencilset/bpmn1.1#SequenceFlow</type><id/><categories/><documentation/><name/><sourceref/><targetref/><conditiontype>None</conditiontype><conditionexpression/><quantity>1</quantity><showdiamondmarker>false</showdiamondmarker><bounds>111.21873474121094,200.99999618530273,185.78123474121094,202.99999618530273</bounds><dockers>15 15 50 40  # </dockers><outgoing rdf:resource=\"#oryx_D83E984D-C164-44D7-8C08-23FBC17343CB\"/><parent rdf:resource=\"#oryx-canvas123\"/><target rdf:resource=\"#oryx_D83E984D-C164-44D7-8C08-23FBC17343CB\"/></rdf:Description><rdf:Description rdf:about=\"#oryx_718EE88F-DFA0-44B9-B503-4438FA6A1AA6\"><type>http://b3mn.org/stencilset/bpmn1.1#EndEvent</type><id/><categories/><documentation/><name/><assignments/><pool/><lanes/><eventtype>End</eventtype><result>None</result><bgcolor>#ffffff</bgcolor><bounds>360.99998474121094,187.99999618530273,388.99998474121094,215.99999618530273</bounds><parent rdf:resource=\"#oryx-canvas123\"/></rdf:Description><rdf:Description rdf:about=\"#oryx_3FA71C47-F560-456C-AA87-75BC775D4A03\"><type>http://b3mn.org/stencilset/bpmn1.1#SequenceFlow</type><id/><categories/><documentation/><name/><sourceref/><targetref/><conditiontype>None</conditiontype><conditionexpression/><quantity>1</quantity><showdiamondmarker>false</showdiamondmarker><bounds>286.95310974121094,200.99999618530273,360.96873474121094,202.99999618530273</bounds><dockers>50 40 14 14  # </dockers><outgoing rdf:resource=\"#oryx_718EE88F-DFA0-44B9-B503-4438FA6A1AA6\"/><parent rdf:resource=\"#oryx-canvas123\"/><target rdf:resource=\"#oryx_718EE88F-DFA0-44B9-B503-4438FA6A1AA6\"/></rdf:Description></rdf:RDF>";
					
			DocumentBuilder builder;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes()));
			
			processDocument(document, res.getWriter());
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	protected void processDocument(Document document, PrintWriter writer) {
		FileWriter fileWriter;
		String yawlXML;
		String type = new StencilSetUtil().getStencilSet(document);
		BPMNDiagram diagram = null;
		
		System.out.println("BPMN2YAWLServlet: Importer will be called now");
		if (type.equals("bpmn.json"))
			diagram = new BPMNRDFImporter(document).loadBPMN();
		else if (type.equals("bpmn1.1.json"))
			diagram = new BPMN11RDFImporter(document).loadBPMN();
		System.out.println("BPMN2YAWLServlet: Importer has done its work");

		//Syntax-Checking
		BPMN2YAWLSyntaxChecker checker = new BPMN2YAWLSyntaxChecker(diagram);
		checker.checkSyntax();
		
		//normalize the given diagram for easier mapping
		BPMN2YAWLNormalizer normalizerForBPMN = new BPMN2YAWLNormalizer(diagram);
		normalizerForBPMN.normalizeForYAWL();
		System.out.println("BPMN2YAWLServlet: The normalizer has normalized the diagram.");
		
		BPMN2YAWLConverter converter = new BPMN2YAWLConverter();
		yawlXML = converter.translate(diagram);
		
		try{
			fileWriter = new FileWriter("OryxBPMNToYAWL.yawl");
			fileWriter.write(yawlXML);
			fileWriter.close();
		} catch (IOException e){
			System.out.println("Fehler beim Erstellen des YAWL-Files");
		}
		
		System.out.println("Hello world, I am the BPMN2YAWLservlet.");
	}
}
