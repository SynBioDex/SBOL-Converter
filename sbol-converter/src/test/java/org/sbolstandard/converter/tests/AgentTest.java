package org.sbolstandard.converter.tests;

import java.io.File;
import org.junit.jupiter.api.Test;


/**
 * Unit test for simple App.
 */
public class AgentTest {
	@Test
	public void TestSBOL2Agent() throws Exception {
		//TestUtil.runTestSuiteFile2_to_3(new File("../SBOLTestSuite/SBOL2/Provenance_CodonOptimization_Agent_Act_CD.xml"));
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/Provenance_CodonOptimization_Agent_Act_CD.xml"));
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/Provenance_StrainDerivation.xml"));
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/Provenance_SpecifyCutOperation_Ag.xml"));
		
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/Provenance_SpecifyCutOperation_ag_ann.xml"));

		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/SBOL2/Provenance_CodonOptimization.xml"));
	}

}