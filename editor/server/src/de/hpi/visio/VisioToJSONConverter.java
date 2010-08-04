package de.hpi.visio;

import java.io.Reader;
import java.io.StringReader;

import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.JSONBuilder;
import org.xmappr.Xmappr;
import de.hpi.visio.data.VisioDocument;

/**
 * Main class of Visio import: Imports visio .vdx-models to orxy-json, that can
 * be imported through the json-import into the oryx-editor
 * 
 * @author Thamsen
 */
public class VisioToJSONConverter {

	private String contextPath;

	public VisioToJSONConverter(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * Main method to import Visio files
	 * 
	 * @return
	 */
	public String importVisioData(String xml, String stencilSet) {
		VisioXmlPreparator preparator = new VisioXmlPreparator();
		String preparedXml = preparator.prepareXML(xml);

		VisioDocument visioDocument = getVisioDocumentFromXML(stencilSet, preparedXml);

		VisioDataToDiagramTransformator transformator = new VisioDataToDiagramTransformator(contextPath, visioDocument.getStencilSet());
		Diagram diagram = transformator.createDiagram(visioDocument);

		return getJSONForDiagram(diagram);
	}

	private VisioDocument getVisioDocumentFromXML(String stencilSet, String preparedXml) {
		Reader reader = new StringReader(preparedXml);
		Xmappr xmappr = new Xmappr(VisioDocument.class);
		VisioDocument visioDocument = (VisioDocument) xmappr.fromXML(reader);
		visioDocument.setStencilSet(stencilSet);
		return visioDocument;
	}

	private String getJSONForDiagram(Diagram diagram) {
		try {
			return JSONBuilder.parseModeltoString(diagram);
		} catch (Exception e) {
			throw new IllegalStateException(
					"Wasn't possible to get a json representation " + "of the created diagram.", e);
		}
	}

}
