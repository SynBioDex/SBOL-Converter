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
		//List<String> errors=TestUtil.roundTripConvert(new File("output/simple_attachment_plan_ann_v2.xml"));
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/CreateAndRemoveModel.xml"));
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/AnnotationOutput.xml"));
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/singleModuleDefinition.xml"));
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/test_source_location.xml"));
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/ModuleDefinitionOutput.xml"), false,null,true);
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/memberAnnotations_interaction.xml"));
		List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/sequence4.xml"));
		
	
		
/*
cd_comp
cd_base_1: 4-14 (source loc): ttgacagctagctcagtcctaggtataatgctagc : The first 11 bases
cd_base2: 4-14 (source loc):  ttgacagctagctcagtcctaggtataatgctagttagcgc: The first 11 bases

seq_comp: 
1-11
12-22
acagctagctcacagctagctc

*/
		
		


	
		TestUtil.DisplayErrors(errors);
		
		//TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/sbol2/attachment.xml"));				
	}

}
