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

package de.hpi.bpmn2_0.model.connector;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;

import de.hpi.bpmn2_0.model.FlowElement;
import de.hpi.bpmn2_0.model.FlowNode;

/**
 * Represents all types of edges in a BPMN 2.0 process.
 * 
 * @author Philipp Giese
 * @author Sven Wagner-Boysen
 *
 */
public class Edge extends FlowNode {
	
	@XmlAttribute(required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private FlowElement sourceRef;
	
	@XmlAttribute(required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	private FlowElement targetRef;
	
	
	/* Getters */
	
	/**
	 * @return the sourceRef
	 */
	public FlowElement getSourceRef() {
		return sourceRef;
	}
	
	/**
	 * @return the targetRef
	 */
	public FlowElement getTargetRef() {
		return targetRef;
	}
	
	/* Setters */
	
	/**
	 * @param sourceRef the sourceRef to set
	 */
	public void setSourceRef(FlowElement sourceRef) {
		this.sourceRef = sourceRef;
	}
	/**
	 * @param targetRef the targetRef to set
	 */
	public void setTargetRef(FlowElement targetRef) {
		this.targetRef = targetRef;
	}
}
