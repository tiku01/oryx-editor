package de.hpi.visio.tests;

import org.junit.Test;

import de.hpi.visio.util.MappingConfigurationUtil;
import static org.junit.Assert.*;

public class MappingConfigurationUtilTest {
	
	@Test
	public void testNameUToStencilIdMapping() {
		// TODO Solve the MappingConfiguration path problem...so that tests can run on every machine.
		MappingConfigurationUtil util = new MappingConfigurationUtil("/Users/Thamsen/Workspaces/oryx/oryx/editor/data/execution/");
		assertEquals("StartTimerEvent",util.getStencilIdForNameU("Start Timer"));
		assertEquals("Association_Undirected",util.getStencilIdForNameU("Undirected Association"));
	}

}
