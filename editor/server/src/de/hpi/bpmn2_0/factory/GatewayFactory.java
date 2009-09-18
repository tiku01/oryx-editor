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

package de.hpi.bpmn2_0.factory;

import java.lang.reflect.InvocationTargetException;

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.factory.annotations.StencilId;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.ExclusiveGateway;
import de.hpi.bpmn2_0.model.Gateway;
import de.hpi.bpmn2_0.model.GatewayDirection;
import de.hpi.bpmn2_0.model.SequenceFlow;
import de.hpi.bpmn2_0.model.diagram.GatewayShape;

/**
 * The factory to create {@link Gateway} BPMN 2.0 elements
 * 
 * @author Sven Wagner-Boysen
 * 
 */
@StencilId( { "Exclusive_Databased_Gateway" })
public class GatewayFactory extends AbstractBpmnFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor
	 * .server.diagram.Shape)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape) throws BpmnConverterException {
		GatewayShape gatewayShape = (GatewayShape) this.createDiagramElement(shape);
		Gateway gateway = (Gateway) createProcessElement(shape);
		return new BPMNElement(gatewayShape, gateway, shape.getResourceId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected Object createDiagramElement(Shape shape) {
		GatewayShape gatewayShape = new GatewayShape();
		this.setVisualAttributes(gatewayShape, shape);
		return gatewayShape;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.
	 * oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected BaseElement createProcessElement(Shape shape)
			throws BpmnConverterException {
		try {
			Gateway gateway = (Gateway) this.invokeCreatorMethod(shape);
			this.identifyGatewayDirection(gateway, shape);
			return gateway;
		} catch (Exception e) {
			/* Wrap exceptions into specific BPMNConverterException */
			throw new BpmnConverterException(
					"Error while creating the process element of "
							+ shape.getStencilId(), e);
		}
	}

	/**
	 * Creator method for an exclusive databased Gateway.
	 * 
	 * @param shape
	 *            The resource shape
	 * @return The resulting {@link ExclusiveGateway}
	 */
	@StencilId("Exclusive_Databased_Gateway")
	protected ExclusiveGateway createExclusiveGateway(Shape shape) {
		ExclusiveGateway gateway = new ExclusiveGateway();
		gateway.setId(shape.getResourceId());
		gateway.setName(shape.getProperty("name"));
		return gateway;
	}

	/**
	 * Determines and sets the {@link GatewayDirection}
	 */
	private void identifyGatewayDirection(Gateway gateway, Shape shape) {

		/* Determine the direction of the Gateway */

		int numIncomming = shape.getIncomings().size();
		int numOutgoing = shape.getOutgoings().size();

		GatewayDirection direction = GatewayDirection.UNSPECIFIED;

		if (numIncomming > 1 && numOutgoing > 1)
			direction = GatewayDirection.MIXED;
		else if (numIncomming <= 1 && numOutgoing > 1)
			direction = GatewayDirection.DIVERGING;
		else if (numIncomming > 1 && numOutgoing <= 1)
			direction = GatewayDirection.CONVERGING;

		/* Set the gateway direction */
		gateway.setGatewayDirection(direction);
	}

}
