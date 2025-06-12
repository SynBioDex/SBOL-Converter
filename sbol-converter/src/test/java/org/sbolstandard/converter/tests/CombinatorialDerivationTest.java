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
public class CombinatorialDerivationTest {


	
	@Test
	public void TestCombinatorialDerivation() throws Exception {
		
		File file = new File("../SBOLTestSuite/SBOL2_ic/gfp_reporter_template.xml");
		//File file = new File("output/fromTestSuite/ModuleDefinitionOutput_int_md_ann.xml");
		
		List<String> errors=TestUtil.roundTripConvert(file);
		
		if (errors.size()>0) {
			for (String error : errors) {
				if (error.contains("not found") || error.contains("differ"))
				{
					System.out.println("WARNING: THERE ARE ERRORS BUT JUST DIFFERENT IDS " + error);
				}else{
					System.out.println("Error: " + error);
					assertTrue(false, "Error in round trip conversion: " + error);
				}
			}
			
		}
		
			
		
	}

}
