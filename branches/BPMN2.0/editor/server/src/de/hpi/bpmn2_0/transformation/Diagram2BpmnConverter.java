/**
 * Copyright (c) 2009
 * Philipp Giese, Sven Wagner-Boysen
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

package de.hpi.bpmn2_0.transformation;

import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.oryxeditor.server.diagram.Diagram;
import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.AbstractBpmnFactory;
import de.hpi.bpmn2_0.factory.BPMNElement;
import de.hpi.bpmn2_0.factory.IntermediateCatchEventFactory;
import de.hpi.bpmn2_0.factory.annotations.StencilId;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.Definitions;
import de.hpi.bpmn2_0.model.FlowNode;
import de.hpi.bpmn2_0.model.Process;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.DataAssociation;
import de.hpi.bpmn2_0.model.connector.DataInputAssociation;
import de.hpi.bpmn2_0.model.connector.DataOutputAssociation;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.diagram.AssociationConnector;
import de.hpi.bpmn2_0.model.diagram.BpmnConnector;
import de.hpi.bpmn2_0.model.diagram.DataAssociationConnector;
import de.hpi.bpmn2_0.model.diagram.LaneCompartment;
import de.hpi.bpmn2_0.model.diagram.MessageFlowConnector;
import de.hpi.bpmn2_0.model.diagram.ProcessDiagram;
import de.hpi.bpmn2_0.model.diagram.SequenceFlowConnector;
import de.hpi.bpmn2_0.model.gateway.ExclusiveGateway;
import de.hpi.util.reflection.ClassFinder;

/**
 * Converter class for Diagram to BPMN 2.0 transformation.
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 * 
 */
public class Diagram2BpmnConverter {
	/* Hash map of factories for BPMN 2.0 element to enable lazy initialization */
	private HashMap<String, AbstractBpmnFactory> factories;
	private HashMap<String, BPMNElement> bpmnElements;
	private Diagram diagram;
	private ProcessDiagram processDia;

	/* Define edge ids */
	private final static String[] edgeIdsArray = { "SequenceFlow",
			"Association_Undirected", "Association_Unidirectional",
			"Association_Bidirectional", "MessageFlow" };

	public final static HashSet<String> edgeIds = new HashSet<String>(Arrays
			.asList(edgeIdsArray));

	/* Define data related objects ids */
	private final static String[] dataObjectIdsArray = { "DataObject",
			"DataStore", "Message" };

	public final static HashSet<String> dataObjectIds = new HashSet<String>(
			Arrays.asList(dataObjectIdsArray));

	public Diagram2BpmnConverter(Diagram diagram) {
		this.factories = new HashMap<String, AbstractBpmnFactory>();
		this.bpmnElements = new HashMap<String, BPMNElement>();
		this.diagram = diagram;
	}

	/**
	 * Retrieves the stencil id related hashed factory.
	 * 
	 * @param stencilId
	 *            The stencil id
	 * @return The related factory
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private AbstractBpmnFactory getFactoryForStencilId(String stencilId)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		/* Create a new factory instance if necessary */
		if (!factories.containsKey(stencilId)) {
			this.factories.put(stencilId, createFactoryForStencilId(stencilId));
		}

		return this.factories.get(stencilId);
	}

	/**
	 * Creates a new factory instance for a stencil id.
	 * 
	 * @param stencilId
	 *            The stencil id
	 * @return The created factory
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 */
	private AbstractBpmnFactory createFactoryForStencilId(String stencilId)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		List<Class<? extends AbstractBpmnFactory>> factoryClasses = ClassFinder
				.getClassesByPackageName(AbstractBpmnFactory.class,
						"de.hpi.bpmn2_0.factory");

		/* Find factory for stencil id */
		for (Class<? extends AbstractBpmnFactory> factoryClass : factoryClasses) {
			StencilId stencilIdA = (StencilId) factoryClass
					.getAnnotation(StencilId.class);
			if (stencilIdA == null)
				continue;

			/* Check if appropriate stencil id is contained */
			List<String> stencilIds = Arrays.asList(stencilIdA.value());
			if (stencilIds.contains(stencilId)) {
				return (AbstractBpmnFactory) factoryClass.newInstance();
			}
		}

		throw new ClassNotFoundException("Factory for stencil id: '"
				+ stencilId + "' not found!");
	}

	/**
	 * Secures uniqueness of an BPMN Element.
	 * 
	 * @param el
	 * @throws InvalidKeyException
	 */
	private void addBpmnElement(BPMNElement el) throws InvalidKeyException {
		if (this.bpmnElements.containsKey(el.getId())) {
			throw new InvalidKeyException(
					"Key already exists for BPMN element!");
		}

		this.bpmnElements.put(el.getId(), el);
	}

	/**
	 * Creates the BPMN 2.0 elements for the parent's child shapes recursively.
	 * 
	 * @param childShapes
	 *            The list of parent's child shapes
	 * @param parent
	 *            The parent {@link BPMNElement}
	 * 
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws BpmnConverterException
	 * @throws InvalidKeyException
	 */
	private void createBpmnElementsRecursively(List<Shape> childShapes,
			BPMNElement parent, Process process) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException,
			BpmnConverterException, InvalidKeyException {

		/* Terminate recursion */
		if (parent == null || childShapes == null)
			return;

		/* Create BPMN elements from shapes */
		for (Shape childShape : childShapes) {
			/* Get the appropriate factory and create the element */
			AbstractBpmnFactory factory = this
					.getFactoryForStencilId(childShape.getStencilId());
			BPMNElement bpmnElement = factory.createBpmnElement(childShape,
					null);
			
			bpmnElement.getNode().setProcessRef(process);
			
			/* Add element to flat list of all elements of the diagram */
			this.addBpmnElement(bpmnElement);
			
			/* Add child to parent BPMN element */
			parent.addChild(bpmnElement);

			Object shapeToAdd = bpmnElement.getShape();
			if (shapeToAdd instanceof SequenceFlowConnector) {
				this.processDia.getSequenceFlowConnector().add(
						(SequenceFlowConnector) shapeToAdd);
			
			} else if (shapeToAdd instanceof DataAssociationConnector) {
				this.processDia.getDataAssociationConnector().add(
						(DataAssociationConnector) shapeToAdd);
			
			} else if (shapeToAdd instanceof AssociationConnector) {
				this.processDia.getAssociationConnector().add((AssociationConnector) shapeToAdd);
			
			} else if(shapeToAdd instanceof MessageFlowConnector) {
				this.processDia.getMessageFlowConnector().add((MessageFlowConnector) shapeToAdd);
			}

			/* Handle child shape */
			this.createBpmnElementsRecursively(childShape.getChildShapes(),
					bpmnElement, process);
		}
	}

	/**
	 * Finds catching intermediate event that are attached to an activities
	 * boundary.
	 */
	private void detectBoundaryEvents(Process process) {
		for (Shape shape : this.diagram.getShapes()) {
			if (edgeIds.contains(shape.getStencilId())) {
				continue;
			}

			for (Shape outShape : shape.getOutgoings()) {
				if (edgeIds.contains(outShape.getStencilId()))
					continue;
				IntermediateCatchEventFactory.changeToBoundaryEvent(
						this.bpmnElements.get(shape.getResourceId()),
						this.bpmnElements.get(outShape.getResourceId()),
						process);
			}
		}
	}

	/**
	 * Retrieves the edges and updates the source and target references.
	 */
	private void detectConnectors() {
		for (Shape shape : this.diagram.getShapes()) {
			if (!edgeIds.contains(shape.getStencilId())) {
				continue;
			}

			/* Retrieve connector element */
			BPMNElement bpmnConnector = this.bpmnElements.get(shape
					.getResourceId());

			BPMNElement source = null;

			/*
			 * Find source of connector. It is assumed that the first none edge
			 * element is the source element.
			 */
			for (Shape incomingShape : shape.getIncomings()) {
				if (edgeIds.contains(incomingShape.getStencilId())) {
					continue;
				}

				source = this.bpmnElements.get(incomingShape.getResourceId());
				break;
			}

			BPMNElement target = (shape.getTarget() != null) ? this.bpmnElements
					.get(shape.getTarget().getResourceId())
					: null;

			/* Update source references */
			if (source != null) {
				FlowNode sourceNode = (FlowNode) source.getNode();
				sourceNode.getOutgoing().add((Edge) bpmnConnector.getNode());

				Edge edgeElement = (Edge) bpmnConnector.getNode();
				edgeElement.setSourceRef(sourceNode);

				BpmnConnector edgeShape = (BpmnConnector) bpmnConnector
						.getShape();
				edgeShape.setSourceRef(source.getShape());
			}

			/* Update target references */
			if (target != null) {
				FlowNode targetNode = (FlowNode) target.getNode();
				targetNode.getIncoming().add((Edge) bpmnConnector.getNode());

				Edge edgeElement = (Edge) bpmnConnector.getNode();
				edgeElement.setTargetRef(targetNode);

				BpmnConnector edgeShape = (BpmnConnector) bpmnConnector
						.getShape();
				edgeShape.setTargetRef(target.getShape());
			}
		}
	}

	/**
	 * A {@link DataAssociation} is a child element of an {@link Activity}. This
	 * method updates the references between activities and their data
	 * associations.
	 */
	private void updateDataAssociationsRefs() {
		/* Define edge ids */
		String[] associationIdsArray = { "Association_Undirected",
				"Association_Unidirectional", "Association_Bidirectional" };

		HashSet<String> associationIds = new HashSet<String>(Arrays
				.asList(associationIdsArray));

		for (Shape shape : this.diagram.getShapes()) {
			if (!associationIds.contains(shape.getStencilId())) {
				continue;
			}

			/* Retrieve connector element */
			BPMNElement bpmnConnector = this.bpmnElements.get(shape
					.getResourceId());

			/* Get related activity */
			Edge dataAssociation = (Edge) bpmnConnector.getNode();
			Activity relatedActivity = null;
			if (dataAssociation instanceof DataInputAssociation) {
				relatedActivity = (dataAssociation.getTargetRef() instanceof Activity ? (Activity) dataAssociation
						.getTargetRef()
						: null);
				if (relatedActivity != null)
					relatedActivity.getDataInputAssociation().add(
							(DataInputAssociation) dataAssociation);

			} else if (dataAssociation instanceof DataOutputAssociation) {
				relatedActivity = (dataAssociation.getSourceRef() instanceof Activity ? (Activity) dataAssociation
						.getSourceRef()
						: null);
				if (relatedActivity != null)
					relatedActivity.getDataOutputAssociation().add(
							(DataOutputAssociation) dataAssociation);
			}
		}
	}

	/**
	 * Identifies the default sequence flows after all sequence flows are set
	 * correctly.
	 */
	private void setDefaultSequenceFlowOfExclusiveGateway() {
		for (BPMNElement element : this.bpmnElements.values()) {
			BaseElement base = element.getNode();
			if (base instanceof ExclusiveGateway) {
				((ExclusiveGateway) base).findDefaultSequenceFlow();
			}
		}
	}

	/**
	 * Retrieves a BPMN 2.0 diagram and transforms it into the BPMN 2.0 model.
	 * 
	 * @param diagram
	 *            The BPMN 2.0 {@link Diagram} based on the ORYX JSON.
	 * @return The definitions root element of the BPMN 2.0 model.
	 * @throws BpmnConverterException
	 */
	public Definitions getDefinitionsFormDiagram()
			throws BpmnConverterException {
		/* Build-up standard definitions */
		Process process = new Process();
		process.setId("testProzess");
		LaneCompartment laneComp = new LaneCompartment();
		this.processDia = new ProcessDiagram();
		processDia.getLaneCompartment().add(laneComp);
		processDia.setProcessRef(process);

		Definitions definitions = new Definitions();
		definitions.getDiagram().add(processDia);
		definitions.getRootElement().add(process);

		/* Convert shapes to BPMN 2.0 elements */

		BPMNElement rootElement = new BPMNElement(laneComp, process, diagram
				.getResourceId());

		try {
			createBpmnElementsRecursively(diagram.getChildShapes(), rootElement, process);
		} catch (Exception e) {
			/* Pack exceptions in a BPMN converter exception */
			throw new BpmnConverterException(
					"Error while converting to BPMN model", e);
		}

		// for (Shape shape : this.diagram.getChildShapes()) {
		// try {
		// AbstractBpmnFactory factory = this.getFactoryForStencilId(shape
		// .getStencilId());
		// BPMNElement bpmnElement = factory
		// .createBpmnElement(shape, null);
		//
		// this.addBpmnElement(bpmnElement);
		//
		// process.getFlowElement().add(
		// (FlowElement) bpmnElement.getNode());
		//
		// Object shapeToAdd = bpmnElement.getShape();
		// if (shapeToAdd instanceof BpmnNode) {
		// laneComp.getBpmnShape().add((BpmnNode) shapeToAdd);
		// } else if (shapeToAdd instanceof SequenceFlowConnector) {
		// processDia.getSequenceFlowConnector().add(
		// (SequenceFlowConnector) shapeToAdd);
		// }
		//
		// } catch (Exception e) {
		// /* Pack exceptions in a BPMN converter exception */
		// throw new BpmnConverterException(
		// "Error while converting to BPMN model", e);
		// }
		// }

		this.detectBoundaryEvents(process);
		this.detectConnectors();
		this.updateDataAssociationsRefs();
		this.setDefaultSequenceFlowOfExclusiveGateway();

		return definitions;
	}

	/* Getter & Setter */

	/**
	 * @return The list of BPMN 2.0 's stencil set edgeIds
	 */
	public static HashSet<String> getEdgeIds() {
		return edgeIds;
	}

}
