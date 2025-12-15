package org.sbolstandard.converter.tests;

import java.io.File;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AllFilesTestIC{


	
	@Test
	public void TestSBOL2AllFiles() throws Exception {
		//TestUtil.runTestSuiteFile2_to_3(new File("../SBOLTestSuite/SBOL2/SequenceConstraintOutput.xml"));
		Logger logger = TestUtil.getLogger("AllFilesTestIC.log");	
		TestUtil.testRecursiveFromDirectory(new File("../SBOLTestSuite/SBOL2_ic/"), "output/SBOLTestSuite_conversion/SBOL2_ic/", logger, false, true, false);		
		/* 
		print("Starting to convert SBOL2 files that are not compliant with best practices.", logger);
		testRecursive(new File("../SBOLTestSuite/SBOL2_bp/"), "output/SBOLTestSuite_conversion/SBOL2_bp/", logger);				
		print("All SBOL2_bp files tested successfully.", logger);
		*/

	}
}
