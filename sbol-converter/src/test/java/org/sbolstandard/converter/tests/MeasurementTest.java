package org.sbolstandard.converter.tests;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.sbolstandard.core3.util.Configuration;


/**
 * Unit test for simple App.
 */
public class MeasurementTest {
	@Test
	public void TestSBOL2Measurement() throws Exception {
		boolean oldValue = Configuration.getInstance().isCompleteDocument();
		Configuration.getInstance().setCompleteDocument(false);		
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/Measure.xml"));		
		Configuration.getInstance().setCompleteDocument(oldValue);
	}

}