package de.fraunhofer.fokus.jic.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fraunhofer.fokus.jic.JICException;
import de.fraunhofer.fokus.jic.framework.Framework;
import de.fraunhofer.fokus.jic.framework.impl.FrameworkImpl;
import de.fraunhofer.fokus.jic.identity.ClaimIdentity;

public class TestFrameworkImpl {

	private Framework framework = null;
	
	@Before
	public void setUp() throws Exception {
		this.framework = new FrameworkImpl();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetIDInputStream() {
		/*
		try {
			ClaimIdentity id = framework.getID("");
		} catch (JICException e) {
			e.printStackTrace();
			fail("exception occurred.");
		}
		*/
	}

}
