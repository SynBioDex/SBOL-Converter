package org.sbolstandard.converter.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.sbolstandard.converter.sbol23_31.SBOLDocumentConverter;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidate;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.util.Configuration;
import org.sbolstandard.core3.validation.IdentifiedValidator;

public class TestUtil {

	public static void runTestSuiteFile(File file) throws Exception {

		int numOfErros = 0;
		List<String> errors=roundTripConvert(file);
		
		for (String error : errors) {
			System.out.println(error);
			numOfErros++;
		}
		assertTrue(numOfErros == 0, "Errors in conversion from" + file.getName());
		System.out.println("--------");

	}
	
	public static List<String> roundTripConvert(File file) throws Exception {		
		return roundTripConvert(file, true,null, true);		
	}

	public static List<String> roundTripConvert(File file, boolean sbol3Validate, String outputFile, boolean printToScreen) throws Exception {
		System.out.println("Converting the SBOL2 file:" + file.getName());
		
		org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(file);
		return roundTripConvert(doc, sbol3Validate, outputFile, printToScreen);		
	}

	public static List<String> roundTripConvert(SBOLDocument sbol2InputDocument, boolean sbol3Validate, String outputFile, boolean printToScreen) throws Exception {
		Configuration.getInstance().setValidateBeforeSaving(sbol3Validate);
		Configuration.getInstance().setValidateAfterSettingProperties(sbol3Validate);
		
		
		if (printToScreen){
			SBOLWriter.write(sbol2InputDocument, System.out);
		}
		if (outputFile != null) {			
			SBOLWriter.write(sbol2InputDocument, new File(outputFile + "_sbol2.xml"));
		}

		SBOLDocumentConverter converter = new SBOLDocumentConverter();
		org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = converter.convert(sbol2InputDocument);
		System.out.println("--------");

		System.out.println("Converted from SBOL2 to SBOL3:");
		if (printToScreen){
			System.out.println(SBOLIO.write(sbol3Doc, SBOLFormat.TURTLE));
		}
		if (outputFile != null) {			
			SBOLIO.write(sbol3Doc, new File(outputFile + "_sbol3.xml"), SBOLFormat.RDFXML);
			SBOLIO.write(sbol3Doc, new File(outputFile + "_sbol3.ttl"), SBOLFormat.RDFXML);			
		}
		
		org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter converter3_2 = new org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter();
		org.sbolstandard.core2.SBOLDocument sbol2Doc = converter3_2.convert(sbol3Doc);

		System.out.println("");
		System.out.println("Converted from SBOL3 to SBOL2:");

		if (printToScreen){		
			SBOLWriter.write(sbol2Doc, System.out);
		}
		if (outputFile != null) {			
			SBOLWriter.write(sbol2Doc, new File(outputFile + "_sbol2_from_sbol3.xml"));
		}
		SBOLValidate.compareDocuments("SBOL2in", sbol2InputDocument, "SBOL2out", sbol2Doc);
		
		return SBOLValidate.getErrors();		
	}

	public static List<String> roundTripConvert(SBOLDocument sbol2InputDocument) throws Exception {
		return roundTripConvert(sbol2InputDocument, true,null, true);
	}

	public static void runTestSuiteFile2_to_3(File file) throws Exception {
		System.out.println("Testing" + file.getName());

		try {
			// Configuration.getInstance().setValidateBeforeSaving(false);

			org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(file);
			SBOLWriter.write(doc, System.out);

			SBOLDocumentConverter converter = new SBOLDocumentConverter();
			org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = converter.convert(doc);
			
			//System.out.println("SBOL3-DOC "+sbol3Doc.getComponents().size());
			for (Component component : sbol3Doc.getComponents()) {
				List<String> messages = IdentifiedValidator.getValidator().validate(sbol3Doc.getComponents().get(0));
				printMessages(messages, "Component" + component.getDisplayId());

				List<SubComponent> subComponents = component.getSubComponents();
				if (subComponents != null && subComponents.size() > 0) {
					for (SubComponent subComponent : subComponents) {
						messages = IdentifiedValidator.getValidator().validate(subComponent);
						printMessages(messages, "SubComponent" + subComponent.getDisplayId());
						if (messages == null || messages.size() == 0) {
							Component subCompRef = subComponent.getInstanceOf();
							if (subCompRef != null) {
								System.out.println("SubComponent reference: " + subCompRef.getDisplayId());
							}

						}
					}
				}
			}

			System.out.println("--------");
			// System.out.println(sbol3Doc.getComponents().get(1).getSubComponents().get(0).getInstanceOf());

			System.out.println("Converted from SBOL2 to SBOL3:");
			System.out.println(SBOLIO.write(sbol3Doc, SBOLFormat.TURTLE));

			System.out.println("--------");

			org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter converter3_2 = new org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter();
			org.sbolstandard.core2.SBOLDocument sbol2Doc = converter3_2.convert(sbol3Doc);

			System.out.println("");
			System.out.println("Converted from SBOL3 to SBOL2:");

			SBOLWriter.write(sbol2Doc, System.out);

			SBOLValidate.compareDocuments("SBOL2in", doc, "SBOL2out", sbol2Doc);
			int numOfErros = 0;
			for (String error : SBOLValidate.getErrors()) {
				System.out.println(error);
				numOfErros++;
			}
			assertTrue(numOfErros == 0, "Errors in conversion from" + file.getName());
			System.out.println("--------");

		} catch (Exception e) {
			System.out.println("Error in conversion from" + file.getName());
			e.printStackTrace();
			throw e;
		}
	}

	private static String printMessages(List<String> messages, String type) {
		String output = "";
		if (messages != null && messages.size() > 0) {
			for (String message : messages) {
				output = output + type + " Validation test: " + message + System.lineSeparator();
			}
		} else {
			output = output + "\tNo errors. Type:" + type + System.lineSeparator();
		}
		output = output + "--------------------" + System.lineSeparator();
		System.out.print(output);
		return output;
	}
	
	public static String DisplayErrors(List<String> errors)
	{
		StringBuffer buffer = new StringBuffer();
		if (errors.size()>0) {
			for (String error : errors) {
				if (error.contains("not found") || error.contains("differ"))
				{
					String message=("WARNING: THERE ARE ERRORS BUT JUST DIFFERENT IDS " + error);
					System.out.println(message);
					buffer.append(message + System.lineSeparator());
				}else{
					System.out.println("Error: " + error);
					System.out.println(buffer);
					assertTrue(false, "Error in round trip conversion: " + error);
				}
			}
		}
		else {
			System.out.println("No errors in round trip conversion");
			buffer.append("No errors in round trip conversion" + System.lineSeparator());
		}
		return buffer.toString();
	}

}
