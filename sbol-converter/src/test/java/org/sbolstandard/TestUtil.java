package org.sbolstandard;

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
		Configuration.getInstance().setValidateBeforeSaving(true);

		org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(file);

		SBOLDocumentConverter converter = new SBOLDocumentConverter();
		org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = converter.convert(doc);
		System.out.println("--------");

		System.out.println("Converted from SBOL2 to SBOL3:");
		System.out.println(SBOLIO.write(sbol3Doc, SBOLFormat.TURTLE));
		SBOLIO.write(sbol3Doc, new File("output/currentSBOL3File.xml"), SBOLFormat.RDFXML);

		org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter converter3_2 = new org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter();
		org.sbolstandard.core2.SBOLDocument sbol2Doc = converter3_2.convert(sbol3Doc);

		System.out.println("");
		System.out.println("Converted from SBOL3 to SBOL2:");

		SBOLWriter.write(sbol2Doc, System.out);
		SBOLValidate.compareDocuments("SBOL2in", doc, "SBOL2out", sbol2Doc);
		
		return SBOLValidate.getErrors();		
	}

	public static void runTestSuiteFile2_to_3(File file) throws Exception {
		System.out.println("Testing" + file.getName());

		try {
			// Configuration.getInstance().setValidateBeforeSaving(false);

			org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(file);
			SBOLWriter.write(doc, System.out);

			SBOLDocumentConverter converter = new SBOLDocumentConverter();
			org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = converter.convert(doc);

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

}
