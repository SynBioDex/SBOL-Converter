package org.sbolstandard.converter.tests;

import java.io.File;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;

/**
 * Unit test for simple App.
 */
public class SBOL3_to_2_Test {
	@Test
	public void TestSBOL2AllFiles() throws Exception {
		Logger logger = TestUtil.getLogger("AllFilesTest.log");	
		File sbol3FileRDFSource=new File ("../../libSBOLj3/output/usecase/toggleswitch.xml");

		File sbol2File=new File ("output/tmp/toggleswitch.xml_sbol3_sbol2_from_sbol3.xml");
		File tmpSbol3FileRDF=new File ("output/tmp/toggleswitch.xml_sbol3.xml");
		File tmpSbol3FileTTL=new File ("output/tmp/toggleswitch.xml_sbol3.ttl");
		
		
		SBOLDocument sbol3Doc=SBOLIO.read(sbol3FileRDFSource);
		SBOLIO.write(sbol3Doc,tmpSbol3FileRDF,  SBOLFormat.RDFXML);
		SBOLIO.write(sbol3Doc,tmpSbol3FileTTL,  SBOLFormat.TURTLE);

		SBOLDocumentConverter converter = new SBOLDocumentConverter();
		org.sbolstandard.core2.SBOLDocument sbol2Doc = converter.convert(sbol3Doc);
		
		SBOLWriter.write(sbol2Doc, sbol2File);
	
	}
}
