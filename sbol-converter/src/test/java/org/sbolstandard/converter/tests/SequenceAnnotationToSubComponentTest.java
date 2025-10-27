package org.sbolstandard.converter.tests;

import java.io.File;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.sbolstandard.core2.SBOLWriter;
/**
 * Unit test for simple App.
 */
public class SequenceAnnotationToSubComponentTest {

	@Test
	public void TestSBOL2SequenceAnnotation() throws Exception {
		
		org.sbolstandard.core2.SBOLDocument doc = ComponentDefinitionOutput.createComponentDefinitionOutput();
		File newFile= new File("output/SequenceAnnotationToSubComponentTest.xml");
		SBOLWriter.write(doc, newFile);
		
		SBOLWriter.write(doc, System.out);
		
		List<String> errors=TestUtil.roundTripConvert(newFile);	
		TestUtil.DisplayErrors(errors);		
		//List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/ComponentDefinitionOutput_gl_cd_sa_comp.xml"));
		//TestUtil.DisplayErrors(errors);		
	}
}
