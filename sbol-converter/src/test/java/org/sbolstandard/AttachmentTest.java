package org.sbolstandard;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.junit.jupiter.api.Test;
import org.sbolstandard.converter.sbol23_31.SBOLDocumentConverter;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidate;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core2.SequenceOntology;
import org.sbolstandard.core3.entity.Component;
//import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.util.Configuration;
import org.sbolstandard.core3.util.RDFUtil;
import org.sbolstandard.core3.vocabulary.ComponentType;
import org.sbolstandard.core3.vocabulary.Role;

/**
 * Unit test for simple App.
 */
public class AttachmentTest {


	
	@Test
	public void TestSBOL2Attachment() throws Exception {
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/sbol2/attachment.xml"));
		Model model = RDFDataMgr.loadModel("../SBOLTestSuite/sbol2/attachment.xml") ;
		RDFUtil.write(model,System.out, RDFFormat.RDFXML_ABBREV, null);
		
		//String str= RDFUtil.write(model, RDFFormat.NTRIPLES, null);
		//System.out.println(str);
		
	}
	
	
	

}
