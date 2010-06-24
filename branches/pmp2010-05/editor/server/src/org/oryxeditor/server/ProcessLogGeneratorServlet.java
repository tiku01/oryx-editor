package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.petrinet.PetriNet;
import de.hpi.petrinet.serialization.erdf.PetriNeteRDFParser;
import de.hpi.processLogGeneration.CompletenessOption;
import de.hpi.processLogGeneration.ProcessLogGenerator;

public class ProcessLogGeneratorServlet extends HttpServlet{
	private static final long serialVersionUID = -4916834400403412632L;
	
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			res.setContentType("application/xml");
			JSONObject options = new JSONObject(req.getParameter("options"));
			processDocument(modelOf(req),
					(String)options.get("completeness"),
					(String)options.get("noise"),
					(Boolean)options.get("respectPropabilities"),
					res.getWriter());
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace(res.getWriter());
			res.setStatus(400);
		} catch (SAXException e) {
			e.printStackTrace(res.getWriter());
			res.setStatus(400);
		} catch (JSONException e) {
			e.printStackTrace(res.getWriter());
			res.setStatus(400);
		} catch (ClassCastException e) {
			e.printStackTrace(res.getWriter());
			res.setStatus(400);
		}
	}

	private Document modelOf(HttpServletRequest req) throws SAXException,
			IOException, ParserConfigurationException {
		return DocumentBuilderFactory.newInstance().newDocumentBuilder().
			parse(new ByteArrayInputStream(
					req.getParameter("model").getBytes("UTF-8")));
	}

	private void processDocument(Document model, String completenessOption, 
			String noiseOption, boolean respectProbalities, PrintWriter output) {
		PetriNet net = new PetriNeteRDFParser(model).parse();
		CompletenessOption completeness = CompletenessOption.fromString(completenessOption);
		int noise = Integer.parseInt(noiseOption);
		ProcessLogGenerator generator = new ProcessLogGenerator(net, completeness, noise, respectProbalities);
		output.write(generator.getSerializedLog());
	}
}
