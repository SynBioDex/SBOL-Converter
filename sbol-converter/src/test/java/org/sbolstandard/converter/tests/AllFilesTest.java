package org.sbolstandard.converter.tests;

import java.io.File;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AllFilesTest {
	@Test
	public void TestSBOL2AllFiles() throws Exception {
		Logger logger = TestUtil.getLogger("AllFilesTest.log");	
		TestUtil.testRecursiveFromDirectory(new File("../SBOLTestSuite/SBOL2/"), "output/SBOLTestSuite_conversion/SBOL2/", logger, false, false, false);	
	}
}
