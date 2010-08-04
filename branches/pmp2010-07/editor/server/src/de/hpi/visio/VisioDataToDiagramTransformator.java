package de.hpi.visio;

import java.util.ArrayList;
import java.util.List;

import org.oryxeditor.server.diagram.Bounds;
import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.Point;
import org.oryxeditor.server.diagram.StencilSet;
import org.oryxeditor.server.diagram.StencilType;

import de.hpi.visio.data.Page;
import de.hpi.visio.data.Shape;
import de.hpi.visio.data.VisioDocument;
import de.hpi.visio.util.ImportConfigurationUtil;
import de.hpi.visio.util.VisioShapeBoundsUtil;
import de.hpi.visio.util.VisioShapeDistanceUtil;
import de.hpi.visio.util.VisioShapeDockerUtil;

/**
 * Transforms a given visio document (.vdx-xml file of a Visio diagram, that has
 * been parsed to java by xmappr) to a oryx-diagram that can be translated to
 * json automatically. The resulting json can be used to import it in the editor.
 * 
 * @author Thamsen
 */
public class VisioDataToDiagramTransformator {

	private ImportConfigurationUtil importUtil;
	private VisioShapeDistanceUtil shapeDistanceUtil;
	private VisioShapeBoundsUtil shapeBoundsUtil;
	private VisioShapeDockerUtil shapeDockerUtil;

	public VisioDataToDiagramTransformator(String contextPath, String type) {
		importUtil = new ImportConfigurationUtil(contextPath + "execution/", type);
		shapeDistanceUtil = new VisioShapeDistanceUtil(importUtil);
		shapeBoundsUtil = new VisioShapeBoundsUtil(importUtil);
		shapeDockerUtil = new VisioShapeDockerUtil(importUtil, shapeBoundsUtil);
	}

	public Diagram createDiagram(VisioDocument visioData) {
		VisioPageMerger merger = new VisioPageMerger();
		visioData = merger.mergeAllPages(visioData);
		
		VisioDataPreparator preparator = new VisioDataPreparator(importUtil, shapeDistanceUtil);
		Page preparedVisioPage = preparator.checkAndCleanVisioData(visioData);
		
		HeuristicVisioFreeTextInterpreter freeTextInterpreter = new HeuristicVisioFreeTextInterpreter(importUtil, shapeDistanceUtil);
		Page interpretedPage = freeTextInterpreter.interpretShapes(preparedVisioPage);
		
		interpretedPage = removeShapesWithoutStencilId(interpretedPage);
		
		HeuristicVisioHugeTaskInterpreter hugeTaskInterpreter = new HeuristicVisioHugeTaskInterpreter(importUtil);
		Page interpretedPageWithSubprocesses = hugeTaskInterpreter.interpreteHugeTasksAsSubprocesses(interpretedPage);
				
		HeuristicVisioEdgeInterpreter edgeInterpreter = new HeuristicVisioEdgeInterpreter(importUtil, shapeDistanceUtil);
		Page interpretedPageWithEdges = edgeInterpreter.interpretEdges(interpretedPageWithSubprocesses);
		
		interpretedPageWithEdges.setShapes(interpretedPageWithEdges.getShapes());
		assignShapeIds(interpretedPageWithEdges.getShapes());
		
		Diagram diagram = getNewDiagram();
		diagram.setBounds(shapeBoundsUtil.getCorrectOryxShapeBounds(interpretedPageWithEdges.getBounds()));		
		ArrayList<org.oryxeditor.server.diagram.Shape> childShapes = getOryxChildShapesFromVisioData(interpretedPageWithEdges);
		diagram.setChildShapes(childShapes);
		
		HeuristicOryxContainmentInterpreter containmentInterpreter = new HeuristicOryxContainmentInterpreter(importUtil);
		diagram = containmentInterpreter.interpretDiagram(diagram);
		
		return diagram;
	}

	private ArrayList<org.oryxeditor.server.diagram.Shape> getOryxChildShapesFromVisioData(Page visioPage) {
		ArrayList<org.oryxeditor.server.diagram.Shape> childShapes = new ArrayList<org.oryxeditor.server.diagram.Shape>();
		for (Shape visioShape : visioPage.getShapes()) {
			String stencilId = importUtil.getStencilIdForName(visioShape.getName());
			StencilType type = new StencilType(stencilId);
			org.oryxeditor.server.diagram.Shape oryxShape = 
				new org.oryxeditor.server.diagram.Shape(visioShape.getShapeId(), type);
			Bounds correctedBounds = shapeBoundsUtil.getCorrectOryxShapeBoundsWithResizing(visioShape, visioPage);
			oryxShape.setBounds(correctedBounds);
			ArrayList<Point> dockers = shapeDockerUtil.getCorrectedDockersForShape(visioShape, visioPage);
			oryxShape.setDockers(dockers);
			setLabelPropertyForShape(visioShape, oryxShape);
			for (String propertyKey : visioShape.getProperties().keySet()) {
				oryxShape.putProperty(propertyKey, visioShape.getPropertyValueByKey(propertyKey));
			}
			childShapes.add(oryxShape);
		}
		correctFlowOfShapes(childShapes, visioPage);
		return childShapes;
	}

	private void setLabelPropertyForShape(Shape visioShape, org.oryxeditor.server.diagram.Shape oryxShape) {
		if (visioShape.getLabel() != null && !visioShape.getLabel().equals("")) {
			String labelPropertyNameExceptionsString = importUtil.getStencilSetConfig("LabelPropertyException");
			if (labelPropertyNameExceptionsString != null && !"".equals(labelPropertyNameExceptionsString)) {
				String[] labelPropertyNameExceptions = labelPropertyNameExceptionsString.split(",");
				for (String exception : labelPropertyNameExceptions) {
					if (exception.equalsIgnoreCase(oryxShape.getStencilId()))
						oryxShape.putProperty(importUtil.getStencilSetConfig(exception + ".LabelProperty"), 
								visioShape.getLabel());
				}
			}
			if (oryxShape.getProperties().size() == 0) {
				oryxShape.putProperty(importUtil.getStencilSetConfig("DefaultLabelProperty"), visioShape.getLabel());
			}
		}
	}

	private Diagram getNewDiagram() {
		StencilSet stencilSet = 
			new StencilSet(importUtil.getStencilSetConfig("StencilSetUrl"), importUtil.getStencilSetConfig("StencilSetNameSpace"));
		StencilType type = new StencilType(importUtil.getStencilSetConfig("StencilType"));
		Diagram diagram = new Diagram("oryx-canvas123", type, stencilSet);
		diagram.putProperty("targetnamespace", importUtil.getStencilSetConfig("TargetNameSpace"));
		diagram.putProperty("typelanguage", importUtil.getStencilSetConfig("TypeLanguage"));
		return diagram;
	}

	private Page removeShapesWithoutStencilId(Page page) {
		List<Shape> shapesWithId = new ArrayList<Shape>();
		for (Shape shape : page.getShapes()) {
			String stencilID = importUtil.getStencilIdForName(shape.getName());
			if (stencilID != null && !"".equals(stencilID)) {
				// stencilId is required in oryx json
				shapesWithId.add(shape);
			}
		}
		page.setShapes(shapesWithId);
		return page;
	}

	private void assignShapeIds(List<Shape> shapes) {
		String shapeString = "imported_visio_";
		Integer i = 0;
		for (Shape shape : shapes) {
			shape.setShapeId(shapeString + i.toString());
			i += 1;
		}
	}

	private void correctFlowOfShapes(ArrayList<org.oryxeditor.server.diagram.Shape> childShapes, Page visioPage) {
		for (Shape visioShape : visioPage.getShapes()) {
			if (visioShape.getOutgoings().size() > 0) {
				org.oryxeditor.server.diagram.Shape oryxShape = getOryxShapeById(childShapes, visioShape.getShapeId());
				for (Shape outgoing : visioShape.getOutgoings()) {
					oryxShape.addOutgoing(getOryxShapeById(childShapes, outgoing.getShapeId()));
				}
			}
			if (visioShape.getIncomings().size() > 0) {
				org.oryxeditor.server.diagram.Shape oryxShape = getOryxShapeById(childShapes, visioShape.getShapeId());
				for (Shape incoming : visioShape.getIncomings()) {
					oryxShape.addIncoming(getOryxShapeById(childShapes, incoming.getShapeId()));
				}
			}
			if (visioShape.getTarget() != null) {
				org.oryxeditor.server.diagram.Shape oryxShape = getOryxShapeById(childShapes, visioShape.getShapeId());
				oryxShape.setTarget(getOryxShapeById(childShapes, visioShape.getTarget().getShapeId()));
			}
		}
	}

	private org.oryxeditor.server.diagram.Shape getOryxShapeById(
			ArrayList<org.oryxeditor.server.diagram.Shape> childShapes, String shapeId) {
		for (org.oryxeditor.server.diagram.Shape oryxShape : childShapes) {
			if (oryxShape.getResourceId().equals(shapeId))
				return oryxShape;
		}
		return null;
	}

}
