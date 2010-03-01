package de.hpi.ViewGenerator.Tests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.hpi.ViewGenerator.Translator;
import de.hpi.ViewGenerator.TranslatorInput;
import de.hpi.ViewGenerator.TranslatorInputEdge;
import de.hpi.ViewGenerator.TranslatorInputNode;

public class TranslatorTest {
	TranslatorInput dotTi;
	TranslatorInput neatoTi;
	TranslatorInputNode firstNode;
	TranslatorInputNode secondNode;
	TranslatorInputEdge firstToSecond;
	
	@Before public void setUp() throws Exception {
		dotTi = new TranslatorInput("dot");
		neatoTi = new TranslatorInput("neato");
		firstNode = new TranslatorInputNode("first");
		secondNode =  new TranslatorInputNode("second");
		firstToSecond = new TranslatorInputEdge("first","second");
	}
	
	@Test public void noInput(){
		String expectedDotTranslation = "digraph G{\n}";
		String expectedNeatoTranslation = "graph G{\n}";
		assertEquals(expectedDotTranslation,Translator.translate("G",dotTi, "dot"));
		assertEquals(expectedNeatoTranslation,Translator.translate("G", neatoTi, "neato"));
	}
	
	@Test public void firstNodeAsInput() {
		dotTi.addNode(firstNode);
		neatoTi.addNode(firstNode);
		String expectedDotTranslation = "digraph G{\nnode [] first;\n}";
		String expectedNeatoTranslation = "graph G{\nnode [] first;\n}";		
		assertEquals(expectedDotTranslation,Translator.translate("G",dotTi, "dot"));
		assertEquals(expectedNeatoTranslation,Translator.translate("G", neatoTi, "neato"));
	}
	
	@Test public void bothNodesAsInput() {
		dotTi.addNode(firstNode);
		dotTi.addNode(secondNode);
		neatoTi.addNode(firstNode);
		neatoTi.addNode(secondNode);
		String expectedDotTranslation = "digraph G{\nnode [] first;\nnode [] second;\n}";
		String expectedNeatoTranslation = "graph G{\nnode [] first;\nnode [] second;\n}";		
		assertEquals(expectedDotTranslation,Translator.translate("G",dotTi, "dot"));
		assertEquals(expectedNeatoTranslation,Translator.translate("G", neatoTi, "neato"));
	}
	
	@Test public void bothNodesAndEdgeAsInput() {
		dotTi.addNode(firstNode);
		dotTi.addNode(secondNode);
		dotTi.addEdge(firstToSecond);
		neatoTi.addNode(firstNode);
		neatoTi.addNode(secondNode);
		neatoTi.addEdge(firstToSecond);
		String expectedDotTranslation = "digraph G{\nnode [] first;\nnode [] second;\nfirst -> second [];\n}";
		String expectedNeatoTranslation = "graph G{\nnode [] first;\nnode [] second;\nfirst -- second [];\n}";		
		assertEquals(expectedDotTranslation,Translator.translate("G",dotTi, "dot"));
		assertEquals(expectedNeatoTranslation,Translator.translate("G", neatoTi, "neato"));
	}

}
