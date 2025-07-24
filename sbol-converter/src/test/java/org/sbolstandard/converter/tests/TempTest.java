package org.sbolstandard.converter.tests;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit test for simple App.
 */
public class TempTest {


	
	@Test
	public void TestSBOL2Temp() throws Exception {
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
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/sequence4.xml"));
		
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/memberAnnotations.xml"));
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/partial_pIKE_right_casette.xml"));
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/partial_pTAK_left_cassette.xml"));
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/ComponentDefinitionOutput_gl_noRange.xml"));
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/partial_pIKE_left_cassette.xml"));		
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/partial_pIKE_right_cassette.xml"));
		//ERROR: due to the SBOL2 issues 
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/ComponentDefinitionOutput.xml"));
		
	    //TODO: Do another round trip using the final SBOL2 file. It contains a sequence URI but not the sequence itself now!
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/eukaryotic_transcriptional_cd_sa_gl.xml"));
		
		//ERROR: due to the SBOL2 issues
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/ModuleDefinitionOutput.xml"));
		
		//ERROR: due to the SBOL2 issues		
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/ComponentDefinitionOutput_gl.xml"));
		
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/ComponentDefinitionOutput_gl_cd_sa_comp.xml"));
		List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/partial_pTAK_right_cassette.xml"));
		
		//ERROR: due to the SBOL2 issues		
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/toggle.xml"));
				
/*
https://github.com/SynBioDex/SBOLTestSuite/blob/master/SBOL2/test_source_location.xml
cd_comp
	cd_base_1: 4-14 (source loc): ttgacagctagctcagtcctaggtataatgctagc : The first 11 bases							  
	cd_base2: 4-14 (source loc):  ttgacagctagctcagtcctaggtataatgctagttagcgc: The first 11 bases

seq_comp: 
1-11
12-22
acagctagctcacagctagctc

*/	
		TestUtil.DisplayErrors(errors);
			
	}

}
