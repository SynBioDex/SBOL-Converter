package org.sbolstandard.converter.tests;

import java.io.File;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class ImplementationTest {

	@Test
	public void TestSBOL2Implementation() throws Exception {
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/sbol2/implementation_example.xml"));		
	}
	
	

}
