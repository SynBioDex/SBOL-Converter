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
public class AppTest {

	//@Test
	public void SBOL3ComponentConversion() throws Exception {
		// https://uriprefix.com/foo/B0034
		URI base = URI.create("https://synbiohub.org/public/igem/");
		org.sbolstandard.core3.entity.SBOLDocument doc = new org.sbolstandard.core3.entity.SBOLDocument(base);
		// Create the RBS component
		Component rbs = doc.createComponent("B0034", Arrays.asList(ComponentType.DNA.getUri()));
		rbs.setName("B0034");
		rbs.setDescription("RBS (Elowitz 1999)");
		rbs.setRoles(Arrays.asList(Role.RBS));
		String output = SBOLIO.write(doc, SBOLFormat.TURTLE);
		System.out.println(output);

		org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter converter = new org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter();
		SBOLDocument sbol2Doc = converter.convert(doc);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		sbol2Doc.write(stream);
		String result = stream.toString("UTF-8");
		System.out.println(result);
		assertTrue(true);
	}

	// @Test
	public void SBOL2ComponentDefinitionConversion() throws Exception {
		// URI base=URI.create("https://synbiohub.org/public/igem/");
		SBOLDocument sbol2Doc = new SBOLDocument();
		String prURI = "https://synbiohub.org/public/igem/";
		String prPrefix = "pr";
		sbol2Doc.addNamespace(URI.create(prURI), prPrefix);
		sbol2Doc.setDefaultURIprefix(prURI);

		ComponentDefinition rbs = sbol2Doc.createComponentDefinition("B0034", ComponentType.DNA.getUri());
		rbs.setName("B0034");
		rbs.setDescription("RBS (Elowitz 1999)");
		rbs.setRoles(Set.of(SequenceOntology.RIBOSOME_ENTRY_SITE));

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		sbol2Doc.write(stream);
		String result = stream.toString("UTF-8");
		System.out.println(result);

		SBOLDocumentConverter converter = new SBOLDocumentConverter();
		org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = converter.convert(sbol2Doc);
		result = SBOLIO.write(sbol3Doc, SBOLFormat.TURTLE);
		System.out.println(result);
		assertTrue(true);
	}

	
	
	//@Test
	public void TestSBOL2Model() throws Exception {
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/sbol2/ModelOutput.xml"));		
	}
		
	//@Test
	public void TestSBOL2Experiment() throws Exception {
		TestUtil.runTestSuiteFile(new File("../SBOLTestSuite/sbol2/test_Experiment_ExperimentData.xml"));		
	}

	public static ArrayList<String[]> getTestCases()
	{
		String testfolder=".." + File.separator + "test_files" + File.separator;

		ArrayList<String[]> testCases = new ArrayList<>();
		
		testCases.add(new String[] {testfolder + "sbol2TestFile.xml", "-l", "SBOL3"});
		testCases.add(new String[] {testfolder + "sbol2TestFile.xml", "-l", "SBOL3", "-o", testfolder + "outputs" + File.separator + "convFromSBOL2toSBOL3File.ttl"});
		testCases.add(new String[] {testfolder + "sbol3TestFile.ttl", "-i"});
		testCases.add(new String[] {testfolder + "sbol3TestFile.ttl", "-no"});
		testCases.add(new String[] {testfolder + "invalid.ttl", "-b"});
		testCases.add(new String[] {testfolder + "sbol2TestFile.xml"});
		testCases.add(new String[] {testfolder + "genBankTestFile.gb", "-l", "SBOL2", "-p", "https://keele.ac.uk/scm", "-o", testfolder + "outputs" + File.separator + "convFromGenBanktoSBOL2File.xml"});
		testCases.add(new String[] {".." + File.separator + "SBOLTestSuite" + File.separator + "SBOL2" + File.separator + "Collection.xml", "-l", "SBOL3", "-o", testfolder + "Collection.sbol3.xml"});
		testCases.add(new String[] {testfolder + "toggle_switch.rdf", "-l", "SBOL2", "-o", testfolder + "toggle_switch.sbol2.xml"});
		testCases.add(new String[] {testfolder + "toggle_switch.sbol3", "-l", "SBOL2", "-o", testfolder + "toggle_switch.sbol2.xml"});
		testCases.add(new String[] {testfolder + "toggle_switch.sbol3.sbol", "-l", "SBOL2", "-o", testfolder + "toggle_switch.sbol2.xml"});		
		testCases.add(new String[] {testfolder + "toggle_switch.sbol3.sbol", "-o", testfolder + "toggle_switch.sbol2.rdf", "-l", "SBOL2", "-n", "-i",});
		return testCases;
	}

	@Test
	public void TestCommandLineInterface() throws Exception {
        ArrayList<String[]> testCases=AppTest.getTestCases();		 
		
			for (String[] args : testCases) {
				App.main(args);
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
