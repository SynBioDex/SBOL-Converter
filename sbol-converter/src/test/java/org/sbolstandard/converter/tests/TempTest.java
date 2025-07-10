package org.sbolstandard.converter.tests;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class TempTest {


	
	@Test
	public void TestSBOL2Attachment() throws Exception {
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/pICH43844.xml"));
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/RepressionModel.xml"));
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/RepressionModel.xml"), true,"output/RepressionModel", true);
		List<String> errors=TestUtil.roundTripConvert(new File("output/simple_attachment_plan_ann_v2.xml"));


	
		TestUtil.DisplayErrors(errors);
		
		//TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/sbol2/attachment.xml"));				
	}

}
