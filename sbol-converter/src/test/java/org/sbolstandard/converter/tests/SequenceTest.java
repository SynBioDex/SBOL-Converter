package org.sbolstandard.converter.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.util.Arrays;
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
import org.sbolstandard.core3.entity.Sequence;
//import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.util.Configuration;
import org.sbolstandard.core3.vocabulary.ComponentType;
import org.sbolstandard.core3.vocabulary.Encoding;
import org.sbolstandard.core3.vocabulary.Role;

/**
 * Unit test for simple App.
 */
public class SequenceTest {


	
	@Test
	public void TestSBOL3Sequence() throws Exception {
		URI base = URI.create("https://synbiohub.org/public/igem/");
		org.sbolstandard.core3.entity.SBOLDocument doc = new org.sbolstandard.core3.entity.SBOLDocument(base);
		// Create the RBS component
		Component rbs = doc.createComponent("B0034", Arrays.asList(ComponentType.DNA.getUri()));
		rbs.setName("B0034");
		rbs.setDescription("RBS (Elowitz 1999)");
		rbs.setRoles(Arrays.asList(Role.RBS));
		
		Sequence seq=doc.createSequence("B0034_seq");
		seq.setEncoding(Encoding.NucleicAcid);
		seq.setElements("AGGAGG");
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

}
