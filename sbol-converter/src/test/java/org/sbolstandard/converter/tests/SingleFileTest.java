package org.sbolstandard.converter.tests;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;
import org.sbolstandard.core3.util.SBOLGraphException;

/**
 * Unit test for simple App.
 */
public class SingleFileTest {

	@Test
	public void TestSBOL2SingleFile() throws Exception {
		//TestUtil.runTestSuiteFile2_to_3(new File("../SBOLTestSuite/SBOL2/SequenceConstraintOutput.xml"));
		Logger logger=getLogger();	
		testSingleSBOL2File("design-build-test-learn.xml", new File("../SBOLTestSuite/SBOL2_bp/"), "output/SBOLTestSuite_conversion/SBOL2/SingleFileTest", logger);		

	}
	
	private static Logger getLogger() throws SecurityException, IOException {
		Logger logger= Logger.getLogger(SingleFileTest.class.getName());
		  // Disable default handlers
        logger.setUseParentHandlers(false);

		// Create FileHandler with pattern
        FileHandler fileHandler = new FileHandler("output/logs/SingleFileTest.log", false);  // true = append mode
        //fileHandler.setFormatter(new SimpleFormatter());
		// Create minimal Formatter (only message, nothing else)
        java.util.logging.Formatter minimalFormatter = new java.util.logging.Formatter() {
            @Override
            public String format(LogRecord record) {
                return record.getMessage() + System.lineSeparator();
            }
        };

		// Custom Formatter: Only message, no date, no method, no level        
		fileHandler.setFormatter(minimalFormatter);
        fileHandler.setLevel(Level.ALL);
		// Add handler to the logger
        logger.addHandler(fileHandler);
        logger.setLevel(Level.ALL);
		return logger;
	}

	private static String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";  // No extension or ends with dot
        }
        return fileName.substring(dotIndex + 1);
    }

	private static void testSingleSBOL2File(String fileName, File folder, String outputFolder, Logger logger) throws Exception{	
		logger.info("Testing folder " + folder.getAbsolutePath());
		File file=new File(folder, fileName);
		if (!file.exists()){
			printError("File " + file.getAbsolutePath() + " does not exist.", logger);
			return;
		}
		//File[] files=folder.listFiles();
		int successCounter=0;
		int successCounterWithNoErrors=0;
		
		int totalCounter=0;
		int withoutValidationCounter=0;
			
		String extension=getFileExtension(file.getName());
		if (extension==null || extension.isEmpty()){
					printError("File " + file.getName() + " has no extension, skipping", logger);
					return;
		}
		totalCounter++;
		print ("**********************************************", logger);
		print("Testing " + file.getName(), logger);
		List<String> errors=null;
		File folderToSave=new File (outputFolder);
		if (!folderToSave.exists()){
			folderToSave.mkdirs();
		}
		String outputFile = folderToSave + File.separator + file.getName();
		try{
			errors=TestUtil.roundTripConvertReadFromString(file,true,outputFile,false );
			successCounter++;
			if (errors==null || errors.size()==0){
				successCounterWithNoErrors++;
			}
		}
		catch (SBOLGraphException e){
			e.printStackTrace();
			printError("Error testing " + file.getName() + ": " + e.getMessage(),logger);
			//logger.throwing("AllFilesTest", "testRecursive:" + file.getName(), e);
			//Try without validation
			printError("Converting without SBOL3 validation", logger);
			try{
				errors=TestUtil.roundTripConvert(file,false,outputFile,false);
				withoutValidationCounter++;
			}
			catch (Exception e2){
				logger.severe("Error testing " + file.getName() + " without validation: " + e2.getMessage());
				e2.printStackTrace();						
				throw e2;
			}					
		}
		if (errors!=null && errors.size()>0){
			logger.warning(TestUtil.DisplayErrors(errors));
			print ("Completed converting " + file.getName(),logger);
			print ("////////////////////////////////////////////", logger);
		}
		logger.info("Testing folder " + folder.getAbsolutePath());
		String conversionSummary=String.format("Completed converting SBOL2 files in folder %s. %d/%d files were converted successfully. %d/%d were round tripped identically.", folder.getAbsolutePath(), successCounter, totalCounter, successCounterWithNoErrors, totalCounter);
		if (withoutValidationCounter>0){
			conversionSummary+=String.format(" %d additional files could be converted without SBOL3 validation.", withoutValidationCounter);
		}
		print(conversionSummary, logger);

	}

	private static void print(String message, Logger logger) {
		System.out.println(message);
		logger.info(message);		
	}

	private static void printError(String message, Logger logger) {
		//System.out.println(message);
		logger.severe(message);		
	}
}
