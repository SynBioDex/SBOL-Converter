package org.sbolstandard;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.sbolstandard.converter.sbol23_31.SBOLDocumentConverter;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidate;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.util.Configuration;

public class TestUtil {

	public static void runTestSuiteFile(File file) throws Exception {
		Configuration.getInstance().setValidateBeforeSaving(false);

		org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(file);

		SBOLDocumentConverter converter = new SBOLDocumentConverter();
		org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = converter.convert(doc);
		System.out.println("--------");

		System.out.println("Converted from SBOL2 to SBOL3:");
		System.out.println(SBOLIO.write(sbol3Doc, SBOLFormat.TURTLE));
        
		org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter converter3_2 = new org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter();
		org.sbolstandard.core2.SBOLDocument sbol2Doc = converter3_2.convert(sbol3Doc);

		System.out.println("");
		System.out.println("Converted from SBOL3 to SBOL2:");

		SBOLWriter.write(sbol2Doc, System.out);

		SBOLValidate.compareDocuments("SBOL2in", doc, "SBOL2out", sbol2Doc);
		int numOfErros=0;
		for (String error : SBOLValidate.getErrors()) {
			System.out.println(error);
			numOfErros++;
		}
		assertTrue(numOfErros==0, "Errors in conversion from" + file.getName());
		System.out.println("--------");

	}
	
	public static void runTestSuiteFile2_to_3(File file) throws Exception {
		System.out.println("Testing" + file.getName());
		
		try {
		Configuration.getInstance().setValidateBeforeSaving(false);

		org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(file);

		SBOLDocumentConverter converter = new SBOLDocumentConverter();
		org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = converter.convert(doc);
		System.out.println("--------");

		System.out.println("Converted from SBOL2 to SBOL3:");
		System.out.println(SBOLIO.write(sbol3Doc, SBOLFormat.TURTLE));
        
		
		System.out.println("--------");
		}
		catch (Exception e) {
			System.out.println("Error in conversion from" + file.getName());
			e.printStackTrace();
			throw e;
		}

	}
	
}
