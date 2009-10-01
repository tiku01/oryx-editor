package de.hpi.bpmn2_0.factory;

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

import org.oryxeditor.server.diagram.Shape;

import de.hpi.bpmn2_0.annotations.StencilId;
import de.hpi.bpmn2_0.model.BaseElement;
import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.activity.Task;
import de.hpi.bpmn2_0.model.diagram.ActivityShape;

/**
 * Concrete class to create any kind of task objects from a {@link Shape} with 
 * the stencil id "http://b3mn.org/stencilset/bpmn2.0#Task"
 * 
 * @author Sven Wagner-Boysen
 *
 */
@StencilId("Task")
public class TaskFactory extends AbstractBpmnFactory {

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createDiagramElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected Object createDiagramElement(Shape shape) {
		ActivityShape actShape = new ActivityShape();
		actShape.setX(shape.getUpperLeft().getX());
		actShape.setY(shape.getUpperLeft().getY());
		actShape.setWidth(shape.getWidth());
		actShape.setHeight(shape.getHeight());
		actShape.setId(shape.getResourceId());
		return actShape;
		
	}

	/* (non-Javadoc)
	 * @see de.hpi.bpmn2_0.factory.AbstractBpmnFactory#createProcessElement(org.oryxeditor.server.diagram.Shape)
	 */
	@Override
	protected BaseElement createProcessElement(Shape shape) {
		Task task = new Task();
		task.setId(shape.getResourceId());
		task.setName(shape.getProperty("name"));
		return task;
	}

	@Override
	public BPMNElement createBpmnElement(Shape shape, BPMNElement parent) {
		BaseElement task = this.createProcessElement(shape);
		ActivityShape activity = (ActivityShape) this.createDiagramElement(shape);
		
		activity.setActivityRef((Activity) task);
		
		return new BPMNElement(activity, task, shape.getResourceId());
	}

}
