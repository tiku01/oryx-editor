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

public class Translator {
//	responsible for translating a TranslatorInput Class into a dot/neato representation
//	of its described nodes and edges (graphviz)
	public static String translate(String graphLabel, TranslatorInput translatorInput, String translateAlgorithm) {
//		neato is used for structured representations, therefore only undirected edges exist
		if (translateAlgorithm.equals("neato")) {
			String translated = "graph " + graphLabel + "{\n";
			
			for (TranslatorInputNode node: translatorInput.getNodes()) {
				translated = translated + "node [";
				
				for (String attribute: node.attributes()) {
					translated = translated + attribute + " = " + node.getAttribute(attribute) + ", ";
				}	
				translated = translated + "] " + node.getNodeId() + ";\n";
			}
			
			for (TranslatorInputEdge edge: translatorInput.getEdges()) {
				translated = translated + edge.getSourceNodeId();
				translated = translated + " -- ";
				translated = translated + edge.getTargetNodeId();
				
				translated = translated + " [";
				for (String attribute: edge.attributes()) {
					translated = translated + attribute + " = " + edge.getAttribute(attribute) + ", ";
				}
				translated = translated + "];\n";
			}
			translated = translated + "}";
			return translated;
		}
//		dot is used for directed graphs
		if (translateAlgorithm.equals("dot")) {
			String translated = "digraph " + graphLabel + "{\n";
		
			for (TranslatorInputNode node: translatorInput.getNodes()) {
				translated = translated + "node [";
				
				for (String attribute: node.attributes()) {
					translated = translated + attribute + " = " + node.getAttribute(attribute) + ", ";
				}
				translated = translated + "] " + node.getNodeId() + ";\n";
			}
			
			for (TranslatorInputEdge edge: translatorInput.getEdges()) {
				translated = translated + edge.getSourceNodeId();
				translated = translated + " -> ";
				translated = translated + edge.getTargetNodeId();
				translated = translated + " [";
				for (String attribute: edge.attributes()) {
					translated = translated + attribute + " = " + edge.getAttribute(attribute) + ", ";
				}
				translated = translated + "];\n";
			}
			translated = translated + "}";
			return translated;
		}
		return "";
	}
}
