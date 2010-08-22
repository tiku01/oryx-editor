package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import de.hpi.processLogGeneration.petrinetTimeAndPropability.PTNetRDFImporter;
import de.hpi.petrinet.PetriNet;
import de.hpi.processLogGeneration.CompletenessOption;
import de.hpi.processLogGeneration.ProcessLogGenerator;

/**
 * This servlet acts as the interface for generating ProcessLogs from Petrinets.
 * @author Thomas Milde
 * */
public class ProcessLogGeneratorServlet extends HttpServlet{
	private static final long serialVersionUID = -4916834400403412632L;
	
	protected void doPost(HttpServletRequest requsest, HttpServletResponse response)
			throws IOException {
		try {
			response.setContentType("application/xml");
			response.setCharacterEncoding("utf-8");
			JSONObject options =
				new JSONObject(requsest.getParameter("options"));
			processDocument(modelOf(requsest),//extracts the model from the request
					(String)options.get("completeness"),
					(Integer)options.get("noise"),
					(Integer)options.get("traceCount"),
					response.getWriter());
			
		} catch (ParserConfigurationException e) {
			reportError(e,response);
		} catch (SAXException e) {
			reportError(e,response);
		} catch (JSONException e) {
			reportError(e,response);
		} catch (ClassCastException e) {
			reportError(e,response);
		}  catch (IllegalArgumentException e) {
			reportError(e,response);
		}
	}
	
	/**
	 * This will print an exception's stacktrace to the content of the response
	 * and set the response-status to 400. The status is 400, because the
	 * exceptions, which are handled by this function only occur due to illegal
	 * requests.
	 * @throws IOException if writing to the Response-object fails.
	 * */
	private void reportError(Exception exception, HttpServletResponse response)
			throws IOException {
		exception.printStackTrace(response.getWriter());
		response.setStatus(400);
	}

	/**
	 * extracts the model from an HttpServletRequest.
	 * */
	private Document modelOf(HttpServletRequest req) throws SAXException,
			IOException, ParserConfigurationException {
		String rdf =req.getParameter("model");
		DocumentBuilder builder;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		byte[] bytes = rdf.getBytes();
		Document document = builder.parse(new ByteArrayInputStream(bytes));
		return document;
	}

	/**
	 * Uses the ProcessLogGenerator to generate a ProcessLog with the supplied
	 * options for the supplied model, which it first converts to a PetriNet using
	 * the PetriNetRDFImporter and writes the result to the output
	 * 
	 * @param model the Document, which contains the petrinet, from which a
	 * log should be generated
	 * @param completenessOption None, Trace- or Ordering-completeness
	 * @param noise the degree of noise (0 to 100)
	 * @param traceCount the desired number of traces
	 * @param output the PrintWriter on which to write the serialized log
	 * */
	private void processDocument(Document model, String completenessOption, 
			int noise, int traceCount, PrintWriter output)
			throws NumberFormatException, IllegalArgumentException{
		PetriNet net = new PTNetRDFImporter(model).loadPTNet();
		CompletenessOption completeness = CompletenessOption.fromString(completenessOption);
		ProcessLogGenerator generator = new ProcessLogGenerator(net, completeness, noise, traceCount);
		output.write(generator.getSerializedLog());
	}
}
