package org.sbolstandard.converter.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.sbolstandard.converter.sbol23_31.SBOLDocumentConverter;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidate;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core2.Sequence;
import org.sbolstandard.core2.SequenceOntology;
import org.sbolstandard.core3.entity.Component;
//import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.util.Configuration;
import org.sbolstandard.core3.vocabulary.ComponentType;
import org.sbolstandard.core3.vocabulary.Role;

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
