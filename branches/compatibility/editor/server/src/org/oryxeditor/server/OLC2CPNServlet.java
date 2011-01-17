package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.JSONBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.PTnet.serialization.PTNetRDFImporter;
import de.hpi.olc.CPNGenerator;

public class OLC2CPNServlet extends HttpServlet {
	private static final long serialVersionUID = -3215102566003538575L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String rdf = request.getParameter("data");

		response.setStatus(200);
		response.setContentType("application/json");
		
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new ByteArrayInputStream(rdf.getBytes()));
			convert(document, response.getWriter());
		} catch (ParserConfigurationException e) {
			response.setStatus(500);
			response.getWriter().print("import failed");
			e.printStackTrace(response.getWriter());
		} catch (SAXException e) {
			response.setStatus(500);
			response.getWriter().print("import failed");
			e.printStackTrace(response.getWriter());
		}

	}

	private void convert(Document document, PrintWriter writer) throws IOException {
		PTNetRDFImporter rdfImporter = new PTNetRDFImporter(document);
		CPNGenerator generator = new CPNGenerator();
		Diagram cpn = generator.generate(rdfImporter.loadPTNet());
		try {
			String result = JSONBuilder.parseModeltoString(cpn);
			writer.write(result);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
}
