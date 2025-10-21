package org.sbolstandard.converter.tests;

import java.net.URI;
import org.joda.time.DateTime;
import org.sbolstandard.core2.Activity;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SBOLWriter;
import org.junit.jupiter.api.Test;
/**
 * This example shows creating complex {@link org.sbolstandard.core2.ComponentDefinition} entities. In the example, the BBa_F2620 PoPS Receiver device is 
 * represented with its sub components and their corresponding nucleotide sequences.
 *
 */
public class ActivityDateTest {
	
	public static SBOLDocument createComponentDefinitionOutput() throws SBOLValidationException {
				
		String prURI="http://partsregistry.org/";		
		String prPrefix="pr";	
		
		SBOLDocument document = new SBOLDocument();				
		document.setTypesInURIs(true);
		document.addNamespace(URI.create(prURI), prPrefix);
		document.setDefaultURIprefix(prURI);	

		DateTime now = DateTime.now();

		DateTime past = now.minusDays(10);

		
		Activity act = document.createActivity("TEST_ACTIVITY");
		act.setStartedAtTime(past);
		act.setEndedAtTime(now);
		return document;
	}
	
	@Test
	public void testmethod() throws Exception
    {
		SBOLDocument document = createComponentDefinitionOutput();
		SBOLWriter.write(document,(System.out));

		TestUtil.roundTripConvert(document, true, "ActivityTestOutput.txt", true);

		System.out.println("Done");
    }
}	