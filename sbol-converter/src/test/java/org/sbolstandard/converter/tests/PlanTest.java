package org.sbolstandard.converter.tests;

import java.io.File;
import org.junit.jupiter.api.Test;


/**
 * Unit test for simple App.
 */
public class PlanTest {
	@Test
	public void TestSBOL2Plan() throws Exception {
		//TestUtil.runTestSuiteFile2_to_3(new File("../SBOLTestSuite/SBOL2/simple_attachment_plan_ann.xml"));
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/simple_attachment_plan_ann.xml"));
		//TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/simple_attachment_ref.xml"));
		
	}

}
