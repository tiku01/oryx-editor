package de.hpi.visio;

import java.io.Reader;
import java.io.StringReader;

import org.json.JSONException;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.JSONBuilder;
import org.xmappr.Xmappr;
import de.hpi.visio.data.Page;
               	
public class VisioToJSONConverter {
	
	private VisioDataTransformator transformator;
	
	public VisioToJSONConverter(String contextPath) {
		transformator = new VisioDataTransformator(contextPath);
	}

	public String importVisioData(String xml) {
		Reader reader = new StringReader(xml);
		Xmappr xmappr = new Xmappr(Page.class);
		Page visioPage = (Page) xmappr.fromXML(reader);
		Diagram diagram = transformator.createDiagramFromVisioData(visioPage);
		String diagramJSONString = convertDiagramToJSON(diagram);
		System.out.println(diagramJSONString);
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
