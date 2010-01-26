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
		
		if ("Export".equals(action)) {
			BPMN2XPDLConverter converter = new BPMN2XPDLConverter();
			try {
				res.getWriter().print(converter.exportXPDL(json));
			} catch (Exception e) {
				res.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		} else if ("Import".equals(action)){
			res.getWriter().print("{\"resourceId\":\"oryx-canvas123\",\"properties\":{\"id\":\"\",\"name\":\"\",\"documentation\":\"\",\"version\":\"\",\"author\":\"\",\"language\":\"English\",\"expressionlanguage\":\"\",\"querylanguage\":\"\",\"creationdate\":\"\",\"modificationdate\":\"\",\"pools\":\"\"},\"stencil\":{\"id\":\"BPMNDiagram\"},\"childShapes\":[{\"resourceId\":\"oryx_40C5D0E1-CA3D-4A12-A52A-AD04313265A2\",\"properties\":{\"poolid\":\"\",\"name\":\"\",\"poolcategories\":\"\",\"pooldocumentation\":\"\",\"participantref\":\"\",\"lanes\":\"\",\"boundaryvisible\":true,\"mainpool\":\"\",\"processref\":\"\",\"processname\":\"\",\"processtype\":\"None\",\"status\":\"None\",\"adhoc\":\"\",\"adhocordering\":\"Parallel\",\"adhoccompletioncondition\":\"\",\"suppressjoinfailure\":\"\",\"enableinstancecompensation\":\"\",\"processcategories\":\"\",\"processdocumentation\":\"\"},\"stencil\":{\"id\":\"Pool\"},\"childShapes\":[],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":600,\"y\":250},\"upperLeft\":{\"x\":0,\"y\":0}},\"dockers\":[]},{\"resourceId\":\"oryx_4765DFCC-F0D8-43FA-A69E-C65F8E0ADD2C\",\"properties\":{\"poolid\":\"\",\"name\":\"\",\"poolcategories\":\"\",\"pooldocumentation\":\"\",\"participantref\":\"\",\"lanes\":\"\",\"boundaryvisible\":true,\"mainpool\":\"\",\"processref\":\"\",\"processname\":\"\",\"processtype\":\"None\",\"status\":\"None\",\"adhoc\":\"\",\"adhocordering\":\"Parallel\",\"adhoccompletioncondition\":\"\",\"suppressjoinfailure\":\"\",\"enableinstancecompensation\":\"\",\"processcategories\":\"\",\"processdocumentation\":\"\"},\"stencil\":{\"id\":\"Pool\"},\"childShapes\":[{\"resourceId\":\"oryx_8DF2CC71-69AB-4887-ABF5-3370FA4B06EC\",\"properties\":{\"id\":\"\",\"name\":\"\",\"categories\":\"\",\"documentation\":\"\",\"parentpool\":\"\",\"parentlane\":\"\",\"showcaption\":\"true\"},\"stencil\":{\"id\":\"Lane\"},\"childShapes\":[{\"resourceId\":\"oryx_76A07C1C-6298-43E0-AB6E-BCF11E76FEF2\",\"properties\":{\"id\":\"\",\"name\":\"\",\"categories\":\"\",\"documentation\":\"\",\"parentpool\":\"\",\"parentlane\":\"\",\"showcaption\":\"true\"},\"stencil\":{\"id\":\"Lane\"},\"childShapes\":[{\"resourceId\":\"oryx_20625127-5431-40E6-8411-D1984FDDEBA1\",\"properties\":{\"id\":\"\",\"name\":\"\",\"categories\":\"\",\"documentation\":\"\",\"parentpool\":\"\",\"parentlane\":\"\",\"showcaption\":\"true\"},\"stencil\":{\"id\":\"Lane\"},\"childShapes\":[{\"resourceId\":\"oryx_D4F4BB0B-DFAF-4353-B567-A8A9A6C5883F\",\"properties\":{\"id\":\"\",\"name\":\"\",\"categories\":\"\",\"documentation\":\"\",\"parentpool\":\"\",\"parentlane\":\"\",\"showcaption\":\"true\"},\"stencil\":{\"id\":\"Lane\"},\"childShapes\":[],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":600,\"y\":250},\"upperLeft\":{\"x\":0,\"y\":0}},\"dockers\":[]}],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":600,\"y\":250},\"upperLeft\":{\"x\":0,\"y\":0}},\"dockers\":[]}],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":600,\"y\":320},\"upperLeft\":{\"x\":0,\"y\":70}},\"dockers\":[]}],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":600,\"y\":250},\"upperLeft\":{\"x\":0,\"y\":0}},\"dockers\":[]}],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":600,\"y\":501},\"upperLeft\":{\"x\":0,\"y\":251}},\"dockers\":[]},{\"resourceId\":\"oryx_84AA3D55-D1E6-4D11-8343-FFED6750D8CE\",\"properties\":{\"poolid\":\"\",\"name\":\"\",\"poolcategories\":\"\",\"pooldocumentation\":\"\",\"participantref\":\"\",\"lanes\":\"\",\"boundaryvisible\":true,\"mainpool\":\"\",\"processref\":\"\",\"processname\":\"\",\"processtype\":\"None\",\"status\":\"None\",\"adhoc\":\"\",\"adhocordering\":\"Parallel\",\"adhoccompletioncondition\":\"\",\"suppressjoinfailure\":\"\",\"enableinstancecompensation\":\"\",\"processcategories\":\"\",\"processdocumentation\":\"\",\"bgcolor\":\"#ffffff\"},\"stencil\":{\"id\":\"CollapsedPool\"},\"childShapes\":[],\"outgoing\":[],\"bounds\":{\"lowerRight\":{\"x\":546,\"y\":642},\"upperLeft\":{\"x\":46,\"y\":582}},\"dockers\":[]}],\"bounds\":{\"lowerRight\":{\"x\":1485,\"y\":1050},\"upperLeft\":{\"x\":0,\"y\":0}},\"stencilset\":{\"url\":\"/oryx//stencilsets/bpmn1.1/bpmn1.1.json\",\"namespace\":\"http://b3mn.org/stencilset/bpmn1.1#\"},\"ssextensions\":[]}");
		} else {
			res.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
	}
}
