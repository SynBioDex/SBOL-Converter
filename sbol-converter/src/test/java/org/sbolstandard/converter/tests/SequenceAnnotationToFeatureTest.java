package org.sbolstandard.converter.tests;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core2.Sequence;


/**
 * Unit test for simple App.
 */
public class SequenceAnnotationToFeatureTest {


	
	@Test
	public void TestSBOL2SequenceAnnotation() throws Exception {
		
		
		org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(new File("output/fromTestSuite/CD_SA_Range_Example_modified.xml"));
		
		Sequence seq=doc.createSequence("http://partsregistry.org","test", "1",  "AAAAATTTTTTTTTTTTCCCCCCCCCCCCCCGGGGGGGGGGGGGGGG", Sequence.IUPAC_DNA);
		
		ComponentDefinition cd= doc.getComponentDefinition(URI.create("http://partsregistry.org/cd/BBa_J23119"));
		cd.addSequence(seq);
		File newFile= new File("output/SequenceAnnotationToFeatureTest.xml");
		SBOLWriter.write(doc, newFile);
		
		
		SBOLWriter.write(doc, System.out);

		TestUtil.runTestSuiteFile(newFile);
		
		/*
		List<String> errors=TestUtil.roundTripConvert(newFile);
		
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
			
		}*/
		
		
		List<String> errors=TestUtil.roundTripConvert(new File("../SBOLTestSuite/SBOL2/EF587312.xml"));
		TestUtil.DisplayErrors(errors);
		
	}
	

}
