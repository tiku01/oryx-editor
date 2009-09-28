package de.hpi.bpmn2_0;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.DiagramBuilder;

import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.transformation.Diagram2BpmnConverter;
import de.hpi.diagram.verification.SyntaxChecker;

public class BPMNSerializationTest {

	final static String path = "/Users/Phil/Documents/Studium/signavio/oryx BPMN 2.0/editor/server/src/de/hpi/bpmn2_0/";
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		toBpmn2_0();

	}
	
	public static void toBpmn2_0() throws Exception {
		File json = new File(path + "trivial_bpmn2.0_process.json");
		BufferedReader br = new BufferedReader(new FileReader(json));
		String bpmnJson = "";
		String line;
		while((line = br.readLine()) != null) {
			bpmnJson += line;
		}
		Diagram diagram = DiagramBuilder.parseJson(bpmnJson);
		
		Diagram2BpmnConverter converter = new Diagram2BpmnConverter(diagram);
		Definitions def = converter.getDefinitionsFormDiagram();
		
//		final XMLStreamWriter xmlStreamWriter = XMLOutputFactory
//		.newInstance().createXMLStreamWriter(System.out);
//		
//		xmlStreamWriter.setPrefix("bpmndi","http://bpmndi.org");
		
		SyntaxChecker checker = def.getSyntaxChecker();
		checker.checkSyntax();
		
		System.out.println(checker.getErrorsAsJson().toString());
	}

}
