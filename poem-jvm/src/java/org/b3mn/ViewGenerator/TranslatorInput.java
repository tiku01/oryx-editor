package org.b3mn.ViewGenerator;
import java.util.ArrayList;


public class TranslatorInput {
	
	private String layoutAlg;
	private ArrayList<TranslatorInputNode> nodes;
	private ArrayList<TranslatorInputEdge> edges;

	
	public TranslatorInput(String layoutAlgorithm) {
		layoutAlg = layoutAlgorithm;
		nodes = new ArrayList<TranslatorInputNode>();
		edges = new ArrayList<TranslatorInputEdge>();
	}
	
	public String getLayoutAlgorithm() {
		return layoutAlg;
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
