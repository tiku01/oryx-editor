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

package de.hpi.bpmn2_0.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import de.hpi.bpmn2_0.model.activity.Activity;
import de.hpi.bpmn2_0.model.connector.Association;
import de.hpi.bpmn2_0.model.connector.DataAssociation;
import de.hpi.bpmn2_0.model.connector.DataInputAssociation;
import de.hpi.bpmn2_0.model.connector.DataOutputAssociation;
import de.hpi.bpmn2_0.model.connector.Edge;
import de.hpi.bpmn2_0.model.connector.SequenceFlow;
import de.hpi.bpmn2_0.model.event.BoundaryEvent;
import de.hpi.bpmn2_0.model.event.Event;
import de.hpi.bpmn2_0.model.gateway.Gateway;


/**
 * <p>Java class for tFlowNode complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tFlowNode">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.omg.org/bpmn20}tFlowElement">
 *       &lt;sequence>
 *         &lt;element name="incoming" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="outgoing" type="{http://www.w3.org/2001/XMLSchema}QName" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tFlowNode", propOrder = {
    "incoming",
    "outgoing"
})
@XmlSeeAlso({
    Event.class,
//    TChoreographyActivity.class,
    Gateway.class,
    Activity.class
})
public abstract class FlowNode
    extends FlowElement
{
	
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	@XmlElement(name = "incoming", type = Edge.class)
    protected List<Edge> incoming;
	
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	@XmlElement(name = "outgoing", type = Edge.class)
    protected List<Edge> outgoing;
    
    /**
     * Convenience method to retrieve all incoming {@link SequenceFlow}
     * 
     * Changes to that list have no influence to the result other callers get.
     * 
     * @return The list of {@link SequenceFlow}
     */
    public List<SequenceFlow> getIncomingSequenceFlows() {
    	ArrayList<SequenceFlow> incomingSeq = new ArrayList<SequenceFlow>();
    	
    	for(FlowNode node : this.getIncoming()) {
    		/* Determine if type of sequence flow */
    		if(node instanceof SequenceFlow) {
    			incomingSeq.add((SequenceFlow) node);
    		}
    	}
    	
    	return incomingSeq;
    }
    
    /**
     * Convenience method to retrieve all outgoing {@link SequenceFlow}
     * 
     * Changes to that list have no influence to the result other callers get.
     * 
     * @return The list of {@link SequenceFlow}
     */
    public List<SequenceFlow> getOutgoingSequenceFlows() {
    	ArrayList<SequenceFlow> outgoingSeq = new ArrayList<SequenceFlow>();
    	
    	for(FlowNode node : this.getOutgoing()) {
    		/* Determine if type of sequence flow */
    		if(node instanceof SequenceFlow) {
    			outgoingSeq.add((SequenceFlow) node);
    		}
    	}
    	
    	return outgoingSeq;
    }
    
    /* Getters */
    
    /**
     * Gets the value of the incoming property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the incoming property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIncoming().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     * 
     * 
     */
    public List<Edge> getIncoming() {
        if (incoming == null) {
            incoming = new ArrayList<Edge>();
        }
        return this.incoming;
    }

    /**
     * Gets the value of the outgoing property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outgoing property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutgoing().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     * 
     * 
     */
    public List<Edge> getOutgoing() {
        if (outgoing == null) {
            outgoing = new ArrayList<Edge>();
        }
        return this.outgoing;
    }

}
