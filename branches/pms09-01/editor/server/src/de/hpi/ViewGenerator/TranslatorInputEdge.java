/***************************************
 * Copyright (c) 2010 
 * Martin Krüger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
****************************************/

package de.hpi.ViewGenerator;
import java.util.HashMap;
import java.util.Set;

public class TranslatorInputEdge {
//	represents one edge according to dot/neato notation (graphviz) with possible attributes
	private String sourceNodeId;
	private String targetNodeId;
	private HashMap<String,String> attributes;

	public TranslatorInputEdge(String sourceNodeId, String targetNodeId) {
		this.sourceNodeId = sourceNodeId;
		this.targetNodeId = targetNodeId;
		this.attributes = new HashMap<String,String>();
	}
	
	public String getAttribute(String attribute) {
		return attributes.get(attribute);
	}
	
	public void setAttribute(String attribute, String value) {
		attributes.put(attribute, value);
	}
	
	public boolean hasAttribute(String attribute) {
		return attributes.containsKey(attribute);
	}
	
	public Set<String> attributes() {
		return attributes.keySet();
	}
	
	public String getSourceNodeId() {
		return sourceNodeId;
	}
	
	public String getTargetNodeId() {
		return targetNodeId;
	}
}
