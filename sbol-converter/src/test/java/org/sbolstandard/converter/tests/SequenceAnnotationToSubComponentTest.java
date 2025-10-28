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


/*
 * 
         <sbol:location>
          <sbol:Range rdf:about="http://partsregistry.org/cd/BBa_F2620/anno2/location2">
            <sbol:persistentIdentity rdf:resource="http://partsregistry.org/cd/BBa_F2620/anno2/location2"/>
            <sbol:displayId>location2</sbol:displayId>
            <sbol:start>55</sbol:start>
            <sbol:end>66</sbol:end>
            <sbol:orientation rdf:resource="http://sbols.org/v2#inline"/>
            <sbol:sequence rdf:resource="http://partsregistry.org/seq/BBa_F2620_seq"/>
          </sbol:Range>
        </sbol:location>

 		<sbol:location>
          <sbol:Range rdf:about="http://partsregistry.org/cd/BBa_F2620/anno2/location2">
            <sbol:persistentIdentity rdf:resource="http://partsregistry.org/cd/BBa_F2620/anno2/location2"/>
            <sbol:displayId>location2</sbol:displayId>
            <sbol:start>55</sbol:start>
            <sbol:end>66</sbol:end>
            <sbol:orientation rdf:resource="http://sbols.org/v2#inline"/>
          </sbol:Range>
        </sbol:location>
 */