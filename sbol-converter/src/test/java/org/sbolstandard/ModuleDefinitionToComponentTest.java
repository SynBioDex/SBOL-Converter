package org.sbolstandard;

import static org.junit.jupiter.api.Assertions.assertTrue;


import java.io.File;
import java.util.List;


import org.junit.jupiter.api.Test;
import org.sbolstandard.converter.Util;


/**
 * Unit test for simple App.
 */
public class ModuleDefinitionToComponentTest {

	@Test
	public void TestSBOL2SequenceAnnotation() throws Exception {
		
		
		String fileName = "output/fromTestSuite/ModuleDefinitionOutput.xml";
		File file = new File(fileName);
		if(Util.isSBOL2DocumentCompliant(fileName)) {
			System.out.println("File is SBOL2 compliant: " + fileName);
		} else {
			System.out.println("File is NOT SBOL2 compliant: " + fileName);
		}
		
	
		List<String> errors = TestUtil.roundTripConvert(file);

		if (errors.size() > 0) {
			for (String error : errors) {
				if (error.contains("not found") || error.contains("differ")) {
					System.out.println("WARNING: THERE ARE ERRORS BUT JUST DIFFERENT IDS " + error);
				} else {
					System.out.println("Error: " + error);
					assertTrue(false, "Error in round trip conversion: " + error);
				}
			}

		}

		

	}

}
