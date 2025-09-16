package org.sbolstandard.converter.tests;

import java.io.File;
import org.junit.jupiter.api.Test;


/**
 * Unit test for simple App.
 */
public class ActivityAssociatonUsageTest {
	@Test
	public void TestSBOL2ActivityAssociatonUsageTest() throws Exception {
		// Activity, Association, Agent, Usage
		//TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/Provenance_StrainDerivation.xml"));
		// Activity, Association
		//TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/Provenance_SpecifyCutOperation_Ag_Act.xml"));

		//
		//TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/ActivityTypeOutput.xml"));
		
		//
		//TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/act_example.xml"));

		//
		//TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/Provenance_StrainDerivation.xml"));

		//
		//TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/Provenance_CodonOptimization.xml"));

		//
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/design_usa_act_ann.xml"));

		//
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/Provenance_SpecifyJoinOperation_act_us.xml"));

	}

}
