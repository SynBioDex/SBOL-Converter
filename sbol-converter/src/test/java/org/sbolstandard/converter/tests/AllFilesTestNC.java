package org.sbolstandard.converter.tests;

import java.io.File;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AllFilesTestNC {
	@Test
	public void TestSBOL2AllFiles() throws Exception {
		Logger logger = TestUtil.getLogger("AllFilesTestNC.log");	
		TestUtil.testRecursiveFromDirectory(new File("../SBOLTestSuite/SBOL2_nc/"), "output/SBOLTestSuite_conversion/SBOL2_nc/", logger, false, false, true);	
	}
}
