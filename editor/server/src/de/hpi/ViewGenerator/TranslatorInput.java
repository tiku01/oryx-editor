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
import java.util.ArrayList;

public class TranslatorInput {
//	holds information in form of nodes and edges like specified in dot/neato graph notation languages (graphviz)
	private String layoutAlgorithm;
	private ArrayList<TranslatorInputNode> nodes;
	private ArrayList<TranslatorInputEdge> edges;
	
	public TranslatorInput(String layoutAlgorithm) {
		this.layoutAlgorithm = layoutAlgorithm;
		this.nodes = new ArrayList<TranslatorInputNode>();
		this.edges = new ArrayList<TranslatorInputEdge>();
	}
	
	public String getLayoutAlgorithm() {
		return layoutAlgorithm;
	}
	
	public void addEdge(TranslatorInputEdge edge) {
		edges.add(edge);
	}
	
	public void addNode(TranslatorInputNode node) {
		nodes.add(node);
	}

	public ArrayList<TranslatorInputNode> getNodes() {
		return nodes;
	}
	
	public ArrayList<TranslatorInputEdge> getEdges() {
		return edges;
	}
}
