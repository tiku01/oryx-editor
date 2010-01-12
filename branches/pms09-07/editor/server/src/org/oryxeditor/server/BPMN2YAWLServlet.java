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
import de.hpi.bpmn2yawl.BPMN2YAWLResourceMapper;
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
 * s
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
			//TODO correct mime type??
			res.setContentType("application/xhtml");

			String rdf = req.getParameter("data");
					
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
		String yawlXML, yawlOrgDataXML;
		String type = new StencilSetUtil().getStencilSet(document);
		
		BPMNDiagram diagram = getDiagram(document, type);

		//Syntax-Checking
		BPMN2YAWLSyntaxChecker checker = new BPMN2YAWLSyntaxChecker(diagram);
		boolean isOK = checker.checkSyntax();
		if(!isOK){
			writer.print(checker.getErrorsAsJson().toString());
			return;
		}
		
		//normalize the given diagram for easier mapping
		BPMN2YAWLNormalizer normalizerForBPMN = new BPMN2YAWLNormalizer(diagram);
		normalizerForBPMN.normalizeForYAWL();
		
		boolean noEmptyTasks = checker.checkForNonEmptyTasks(diagram);
		if(!noEmptyTasks){
			writer.print(checker.getErrorsAsJson().toString());
			return;
		}
		
		BPMN2YAWLResourceMapper resourcing = new BPMN2YAWLResourceMapper();
		yawlOrgDataXML = resourcing.translate(diagram);
		
		try{
			fileWriter = new FileWriter("OryxBPMNToYAWL.ybkp");
			fileWriter.write(yawlOrgDataXML);
			fileWriter.close();
		} catch (IOException e){
			System.out.println("Fehler beim Erstellen des YBKP-Files");
		}
		
		int numberOfPools = diagram.getProcesses().size();
		for(int i = 0; i < numberOfPools; i++){
			BPMN2YAWLConverter converter = new BPMN2YAWLConverter();
			yawlXML = converter.translate(diagram, i);
			
			try{
				fileWriter = new FileWriter("OryxBPMNToYAWL_" + i + ".yawl");
				fileWriter.write(yawlXML);
				fileWriter.close();
			} catch (IOException e){
				System.out.println("Fehler beim Erstellen des YAWL-Files");
			}
		}
	}

	/**
	 * @param document
	 * @param type
	 * @param diagram
	 * @return
	 */
	private BPMNDiagram getDiagram(Document document, String type) {
		if (type.equals("bpmn.json"))
			return new BPMNRDFImporter(document).loadBPMN();
		else if (type.equals("bpmn1.1.json"))
			return new BPMN11RDFImporter(document).loadBPMN();
		else
			return null;
	}
}