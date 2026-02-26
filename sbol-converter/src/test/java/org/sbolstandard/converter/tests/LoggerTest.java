package org.sbolstandard.converter.tests;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.sbolstandard.converter.ConverterLogger;

import com.google.common.base.Converter;


/**
 * Unit test for simple App.
 */
public class LoggerTest {
	@Test
	public void TestSBOL2Agent() throws Exception {
		Logger hvLogger = Logger.getLogger("org.hibernate.validator");
        hvLogger.setLevel(Level.WARNING);
		System.setProperty("org.slf4j.simpleLogger.log.org.sbolstandard.converter", "warn");		
		
		executeUsingtheConverterLogger();

		executeUsingtheSimpleLogger();
		
		/*File outputFolder = new File("output/SBOLTestSuite_conversion/SBOL3/");
		File inputFolder = new File("../../libSBOLj3/libSBOLj3/output/");
		convertFile(outputFolder, new File(inputFolder.getPath() + "/combine2020/combine2020.rdf"));
		*/
	}

	private void executeUsingtheConverterLogger(){		
		System.out.println("Executing using the ConverterLogger...");
		ConverterLogger logger = new ConverterLogger();
		logger.setDebugEnabled(true);
		
		logger.debug("Processing file: " + "file1");		
		logger.info("Starting conversion...");
		logger.warn("Warning for file: " + "file1");
		
		try{
			int x=0;
			int y=1;
			int z=y/x;
		}
		catch(Exception e) {
			logger.error("Failed to convert:" + e.getMessage());

		}
		System.out.println("********************************");	
	}

	private void executeUsingtheSimpleLogger() {
		System.out.println("Executing using the simple logger...");
		org.slf4j.Logger logger2 = org.sbolstandard.converter.Util.getLogger();
		logger2.debug("Initial debugging ingformation...");		
		logger2.info("Starting conversion of all files...");
		//logger2.info("Successfully converted {} file(s)", 1);
		logger2.warn("Failed to convert {} file(s)", 2);
		try {
			int x = 0;
			int y = 1;
			int z = y / x;
		} 
		catch (Exception e) {
			logger2.error("Failed to convert:" + e.getMessage());
			// logger2.error("Failed to convert:" , e);
		}
		System.out.println("--------------------------------");
	}
}