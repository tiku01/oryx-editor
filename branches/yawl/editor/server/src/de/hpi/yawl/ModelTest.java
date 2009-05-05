package de.hpi.yawl;

import static org.junit.Assert.*;

import org.junit.Test;

public class ModelTest {

	@Test
	public void testAddDecomposition() {
		Model model = new Model("test");
		Decomposition dec = new Decomposition("1", "true", "NetFactsType");
		model.addDecomposition("2", dec);
		assertTrue(model.getDecompositions().size() == 1);
		assertTrue(model.getDecomposition("2").equals(dec));
	}

}
