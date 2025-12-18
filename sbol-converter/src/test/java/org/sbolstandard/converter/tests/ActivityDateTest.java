package org.sbolstandard.converter.tests;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.jena.datatypes.xsd.XSDDateTime;
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

	public static SBOLDocument createComponentDefinitionOutput2() throws SBOLValidationException {
		
		DateTime now = DateTime.parse("2018-01-23T00:17:42Z");		
		System.out.println(now.getMillisOfSecond());
		System.out.println(now.toString());
		

		String prURI="http://partsregistry.org/";		
		String prPrefix="pr";	
		
		SBOLDocument document = new SBOLDocument();				
		document.setTypesInURIs(true);
		document.addNamespace(URI.create(prURI), prPrefix);
		document.setDefaultURIprefix(prURI);	

		//DateTime now = DateTime.now();
		//DateTime now = DateTime.parse("2018-01-23T00:17:42.711Z");
		Date date=now.toDate();
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		XSDDateTime xsdNow = new XSDDateTime(calendar);
		DateTime past = now.minusDays(10);
		String dateString=xsdNow.toString();

		


		
		Activity act = document.createActivity("TEST_ACTIVITY");
		act.setStartedAtTime(past);
		act.setEndedAtTime(now);
		return document;
	}
	
	@Test
	public void testmethod() throws Exception
    {

		DateTime now = DateTime.parse("2018-01-23T00:17:42Z");		
		System.out.println(now.getMillisOfSecond());
		System.out.println(now.toString());
		
		
		SBOLDocument document = createComponentDefinitionOutput();
		SBOLWriter.write(document,(System.out));

		List<String> errors1 = TestUtil.roundTripConvert(document, true, "ActivityTestOutput.txt", true);
		TestUtil.DisplayErrors(errors1);
		System.out.println("Done");

		SBOLDocument document2 = createComponentDefinitionOutput2();
		SBOLWriter.write(document2,(System.out));

		List<String> errors = TestUtil.roundTripConvert(document2, true, "ActivityTestOutput2.txt", true);
 		TestUtil.DisplayErrors(errors);
    }
}	