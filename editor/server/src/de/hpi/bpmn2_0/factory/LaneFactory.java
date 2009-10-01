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

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.exceptions.BpmnConverterException;
import de.hpi.bpmn2_0.model.diagram.BpmnCompartment;
import de.hpi.bpmn2_0.model.diagram.LaneCompartment;
import de.hpi.bpmn2_0.model.diagram.PoolCompartment;
import de.hpi.bpmn2_0.model.participant.Lane;

/**
 * Factory to create lanes and pools
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 *
 */
@StencilId({
	"CollapsedPool",
	"Pool",
	"Lane"
})
public class LaneFactory extends AbstractBpmnFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createBpmnElement(org.oryxeditor.server.diagram.Shape, de.hpi.bpmn2_0.factory.BPMNElement)
	 */
	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent)
			throws BpmnConverterException {
		BpmnCompartment poolLaneShape = this.createDiagramElement(shape);
		Lane lane = this.createProcessElement(shape);
		
		/* Set references */
		if(poolLaneShape instanceof PoolCompartment) {
			return new BPMNElement(poolLaneShape, null, shape.getResourceId());
		}
		
		if(poolLaneShape instanceof LaneCompartment) {
			((LaneCompartment) poolLaneShape).setLaneRef(lane);
		}
		
		return new BPMNElement(poolLaneShape, lane, shape.getResourceId());
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected BpmnCompartment createDiagramElement(Shape shape) {
		/* Create a shape for a pool or Lane */
		if(shape.getStencilId().equals("Lane")) {
			LaneCompartment laneShape = new LaneCompartment();
			this.setVisualAttributes(laneShape, shape);
			laneShape.setIsVisible(true);
			
			return laneShape;
		} 
		
		PoolCompartment pool = new PoolCompartment();
		this.setVisualAttributes(pool, shape);
		pool.setIsVisible(true);
		
		return pool;
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected Lane createProcessElement(Shape shape)
			throws BpmnConverterException {
		if(!this.hasChildLanes(shape)) {
			return null;
		}
		Lane lane = new Lane();
		lane.setId(shape.getResourceId());
		lane.setName(shape.getProperty("name"));
		lane.setLane(lane);
		
		return lane;
	}
	
	private boolean hasChildLanes(Shape shape) {
		for(Shape childShape : shape.getChildShapes()) {
			if(childShape.getStencilId().equals("Lane")) {
				return true;
			}
		}
		return false;
	}
}
