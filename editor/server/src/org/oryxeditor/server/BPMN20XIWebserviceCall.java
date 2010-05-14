package org.oryxeditor.server;
/**
 * Copyright (c) 2010
 * 
 * Philipp Berger
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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;
import org.oryxeditor.server.diagram.JSONBuilder;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

import de.hpi.bpmn2_0.ExportValidationEventCollector;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.BPMN2DiagramConverter;
import de.hpi.bpmn2_0.transformation.BPMNPrefixMapper;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import de.hpi.util.reflection.ClassFinder;

/**
 * @author Philipp Berger
 * this class sends a webservice call with the model as bpmnXI, the current selected element as semicolon separated list to a host,
 * which process it, and returns a bpmnXi, which gets transformed back to json and send to the editor
 */
public class BPMN20XIWebserviceCall extends HttpServlet{

	private static final long serialVersionUID = -682814702528834026L;
	private static final String SELECTED = "selection";
	private static final String INPUT = "input";
	private static final String URI = "http://example.webservice.com";

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
		String json = req.getParameter("data");
		String selection = StringUtils.join(req.getParameterValues("selection"), ";");
		try {
			/*
			 * get factory by reflection
			 */
			List<Class<? extends AbstractBpmnFactory>> factoryClasses = ClassFinder
			.getClassesByPackageName(AbstractBpmnFactory.class,
					"de.hpi.bpmn2_0.factory", this.getServletContext());

			/*
			 * perfom bpmnXi transformation
			 */
			StringWriter output = this.performTransformationToDi(json, factoryClasses);
			/*
			 * send bpmnXi to the webservice
			 */
			sendWebserviceCall(res, output.toString(), selection);

		} catch (Exception e) {
			writeExceptionToResponse(res, e);
		}




	}
	/**
	 * Sends a request to the Webservice and write the response to the {@link HttpServletResponse}
	 * @param res the response to write on
	 * @param tool the tool which should be called
	 * @param bpmnXi 
	 * @param selected 
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private void sendWebserviceCall(HttpServletResponse res, String bpmnXi, String selected)
	throws MalformedURLException, IOException,
	UnsupportedEncodingException {
		URL uri = new URL(URI);
		URLConnection con = uri.openConnection();
		con.setDoOutput(true);
		StringBuilder databld = new StringBuilder();
		databld.append(		   INPUT	+	"=" + URLEncoder.encode(bpmnXi, "UTF-8"));
		databld.append(	"&"	+ 	SELECTED		+	"=" + URLEncoder.encode(selected, "UTF-8"));
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


		String a =answer.toString();
		/*
		 * remove added pre container if there
		 */
		if(a.startsWith("<pre>")){
			a = a.replace("<pre>","");
			a = a.replace("</pre>", "");
		}
		/*
		 * convert back to json
		 */
		try {
			StringWriter output = this.getJsonFromBpmn20Xml(a);
			res.setContentType("application/json");
			res.setStatus(200);
			res.getWriter().print(output.toString());
		} catch (Exception e) {
			writeExceptionToResponse(res, e);
		}

	}

	/**
	 * Triggers the transformation from Diagram to BPMN model and writes the 
	 * resulting BPMN XML on success.
	 * 
	 * @param json
	 * 		The diagram in JSON format
	 * @param writer
	 * 		The HTTP-response writer
	 * @throws Exception
	 * 		Exception occurred while processing
	 */
	private StringWriter performTransformationToDi(String json,List<Class<? extends AbstractBpmnFactory>> factoryClasses) throws Exception {
		StringWriter writer = new StringWriter();

		/* Retrieve diagram model from JSON */

		Diagram diagram = DiagramBuilder.parseJson(json);

		/* Build up BPMN 2.0 model */
		Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram, factoryClasses);
		Definitions bpmnDefinitions = converter.getDefinitionsFromDiagram();

		/* Perform XML creation */
		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		NamespacePrefixMapper nsp = new BPMNPrefixMapper();
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", nsp);

		/* Set Schema validation properties */
		SchemaFactory sf = SchemaFactory
		.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

		String xsdPath = this.getServletContext().getRealPath("/WEB-INF/lib/bpmn20/BPMN20.xsd");

		Schema schema = sf.newSchema(new File(xsdPath));
		marshaller.setSchema(schema);

		ExportValidationEventCollector vec = new ExportValidationEventCollector();
		marshaller.setEventHandler(vec);

		/* Marshal BPMN 2.0 XML */
		marshaller.marshal(bpmnDefinitions, writer);

		return writer;
	}
	/**
	 * Converts given BPMN20XML to OryxEditor compatible json
	 * @param bpmn20Xml
	 * @return
	 * @throws JAXBException
	 * @throws JSONException
	 */
	private StringWriter getJsonFromBpmn20Xml(String bpmn20Xml) throws JAXBException, JSONException {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);

		StringReader reader = new StringReader(bpmn20Xml);

		JAXBContext context = JAXBContext.newInstance(Definitions.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		Definitions def = (Definitions) unmarshaller.unmarshal(reader);

		BPMN2DiagramConverter converter = new BPMN2DiagramConverter("/" + this.getServletContext().getServletContextName() + "/");
		List<Diagram> dia = converter.getDiagramFromBpmn20(def);

		out.print(JSONBuilder.parseModeltoString(dia.get(0)));

		return writer;
	}
	/**
	 * Helper method to write error to the Response Stream
	 * @param res
	 * @param e
	 */
	private void writeExceptionToResponse(HttpServletResponse res, Exception e) {
		try {
			e.printStackTrace();
			res.setStatus(500);
			res.setContentType("text/plain");
			if(e.getCause()!=null)
				res.getWriter().write(e.getCause().getMessage());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}

