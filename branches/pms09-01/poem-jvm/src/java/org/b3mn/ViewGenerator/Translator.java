package org.b3mn.ViewGenerator;


public class Translator {
	
	public String translate(String graphLabel, TranslatorInput translatorInput, String translateAlgorithm) {
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
			System.out.println(translated);

			return translated;
		}
		
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
			System.out.println(translated);

			return translated;
		}
		
		return "";
	}
}
