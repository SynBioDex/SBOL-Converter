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
public class AllFilesTest {


	
	@Test
	public void TestSBOL2SequenceConstraint() throws Exception {
		//TestUtil.runTestSuiteFile2_to_3(new File("../SBOLTestSuite/SBOL2/SequenceConstraintOutput.xml"));
		testRecursive(new File("../SBOLTestSuite/SBOL2/"));
		
	}
	
	private static void testRecursive(File folder) throws Exception
	{
		File[] files=folder.listFiles();
		for (int i=0;i<files.length;i++)
		{
			File file=files[i];
			if (file.isDirectory())
			{
				testRecursive(folder);
			}
			else
			{	
				System.out.println ("**********************************************");
				System.out.println ("Testing " + file.getName());
				
				List<String> errors=TestUtil.roundTripConvert(file);
				if (errors!=null && errors.size()>0)
				{
					TestUtil.DisplayErrors(errors);
					System.out.println ("Tested " + file.getName());
					System.out.println ("////////////////////////////////////////////");
				}
			}
		}
	}

}
