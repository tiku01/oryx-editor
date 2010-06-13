package de.hpi.visio;

import java.io.Reader;
import java.io.StringReader;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.JSONBuilder;
import org.xmappr.Xmappr;
import de.hpi.visio.data.VisioDocument;
               	
public class VisioToJSONConverter {
	
	private VisioDataTransformator transformator;
	
	public VisioToJSONConverter(String contextPath) {
		transformator = new VisioDataTransformator(contextPath);
	}

	public String importVisioData(String xml) {
		VisioXmlPreparator preparator = new VisioXmlPreparator();
		String preparedXml = preparator.prepareXML(xml);
		Reader reader = new StringReader(preparedXml);
		Xmappr xmappr = new Xmappr(VisioDocument.class);
		VisioDocument visioDocument = (VisioDocument) xmappr.fromXML(reader);
		Diagram diagram = transformator.createDiagramFromVisioData(visioDocument);
		String diagramJSONString = convertDiagramToJSON(diagram);
		return diagramJSONString;
	}



	private String convertDiagramToJSON(Diagram diagram) {
		try {
			return JSONBuilder.parseModeltoString(diagram);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new IllegalStateException();
		}
	}

}
