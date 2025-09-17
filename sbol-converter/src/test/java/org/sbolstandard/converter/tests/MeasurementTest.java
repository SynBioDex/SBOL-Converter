package org.sbolstandard.converter.tests;

import java.io.File;
import org.junit.jupiter.api.Test;


/**
 * Unit test for simple App.
 */
public class MeasurementTest {
	@Test
	public void TestSBOL2Measurement() throws Exception {
		
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/Measure.xml"));
	}

}