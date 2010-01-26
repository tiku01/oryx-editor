package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpStatus;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.bpmn.BPMNDiagram;
import de.hpi.bpmn.validation.BPMNSyntaxChecker;
import de.hpi.bpmn2xpdl.BPMN2XPDLConverter;
import de.hpi.diagram.verification.SyntaxChecker;
import de.hpi.ibpmn.IBPMNDiagram;
import de.hpi.ibpmn.converter.IBPMNConverter;
import de.hpi.ibpmn.rdf.IBPMNRDFImporter;
import de.hpi.interactionnet.InteractionNet;
import de.hpi.interactionnet.localmodelgeneration.DesynchronizabilityChecker;
import de.hpi.interactionnet.serialization.InteractionNetRDFImporter;
import de.hpi.petrinet.Transition;

/**
 * Copyright (c) 2010 Markus Goetz
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
public class BPMN2XPDLServlet extends HttpServlet {
	private static final long serialVersionUID = -8374877061121257562L;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("text/xml");
		String json = req.getParameter("data");
		
		
		String action = req.getParameter("action");

		if (action.equals("Export")) {
			BPMN2XPDLConverter converter = new BPMN2XPDLConverter();
			try {
				res.getWriter().print(converter.exportXPDL(json));
			} catch (Exception e) {
				res.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} else if (action.equals("Import")){
			// IMPORT
		} else {
			res.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
	}
}
