package org.sbolstandard.converter.tests;

import java.io.File;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AttachmentTest {


	
	@Test
	public void TestSBOL2Attachment() throws Exception {
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/sbol2/attachment.xml"));				
	}

}
