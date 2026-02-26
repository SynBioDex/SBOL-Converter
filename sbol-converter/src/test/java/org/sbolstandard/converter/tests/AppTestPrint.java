package org.sbolstandard.converter.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.sbolstandard.converter.App;
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
public class AppTestPrint {

		@Test
	public void TestCommandLineInterface() throws Exception {
		ArrayList<String[]> testCases = AppTest.getTestCases();
	
		System.out.println("********************************************************");		
		System.out.println("List of commands with multiple lines:");
		
		String javaCommand= "java -jar target/sbol-converter-1.0.3-SNAPSHOT-jar-with-dependencies.jar";
		for (String[] args : testCases) {
			System.out.println(javaCommand + " \\");
			for (int i = 0; i < args.length; i++) {
				if (i < args.length - 1) {
					System.out.println(" " + args[i] + " \\");
				} else {
					System.out.println(" " + args[i]);
				}
			}
			System.out.println();
		}

		System.out.println("********************************************************");		
		System.out.println("List of commands with single lines:");		
		for (String[] args : testCases) {
			System.out.print(javaCommand);
			for (int i = 0; i < args.length; i++) {
				System.out.print(" " + args[i]);				
			}
			System.out.println();
		}


		
      

		
		// SBOL2 to GenBank test
        //args=new String[] {"test_files/invalid-out.xml", "-l", "GenBank", "-p", "keele", "-o", "test_files/outputs/cSbol2ToGenBank.gb"};

        // SBOL3 to GenBank test
        //args=new String[] {"test_files/invalid.ttl", "-l", "GenBank", "-o", "test_files/outputs/3ToGenBank.gb"};

        // GenBank to SBOL2 test
        //args=new String[] {"test_files/genBankTestFile.gb", "-l", "SBOL2", "-p", "keele", "-o", "test_files/outputs/gbtoSBOL2.xml"};

        // GenBank to SBOL3 test
        //args=new String[] {"test_files/genBankTestFile.gb", "-l", "SBOL3", "-p", "keele", "-o", "test_files/outputs/gbtoSBOL3.ttl"};

	
	}




}
