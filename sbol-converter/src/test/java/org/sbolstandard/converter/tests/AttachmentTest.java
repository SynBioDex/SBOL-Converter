package org.sbolstandard.converter.tests;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class AttachmentTest {


	
	@Test
	public void TestSBOL2Attachment() throws Exception {
		List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/attachment.xml"));
		TestUtil.DisplayErrors(errors);
		
		//TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/sbol2/attachment.xml"));				
	}

}
