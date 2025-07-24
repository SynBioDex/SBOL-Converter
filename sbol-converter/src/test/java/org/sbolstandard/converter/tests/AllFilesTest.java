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
public class AllFilesTest {


	
	@Test
	public void TestSBOL2AllFiles() throws Exception {
		//TestUtil.runTestSuiteFile2_to_3(new File("../SBOLTestSuite/SBOL2/SequenceConstraintOutput.xml"));
		Logger logger=getLogger();	
		testRecursive(new File("../SBOLTestSuite/SBOL2/"), "output/SBOLTestSuite_conversion/SBOL2/", logger);		
		print("All SBOL2 files tested successfully.", logger);
	}
	
	private static Logger getLogger() throws SecurityException, IOException {
		Logger logger= Logger.getLogger(AllFilesTest.class.getName());
		  // Disable default handlers
        logger.setUseParentHandlers(false);

		// Create FileHandler with pattern
        FileHandler fileHandler = new FileHandler("output/AllFilesTest.log", false);  // true = append mode
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

	 public static String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";  // No extension or ends with dot
        }
        return fileName.substring(dotIndex + 1);
    }

	private static void testRecursive(File folder, String outputFolder, Logger logger) throws Exception{	
		logger.info("Testing folder " + folder.getAbsolutePath());

		File[] files=folder.listFiles();
		for (int i=0;i<files.length;i++)
		{
			File file=files[i];
			if (file.isDirectory()){
				testRecursive(folder, outputFolder, logger);
			}
			else{	
				String extension=getFileExtension(file.getName());
				if (extension==null || extension.isEmpty()){
					printError("File " + file.getName() + " has no extension, skipping", logger);
					continue;
				}

				print ("**********************************************", logger);
				print("Testing " + file.getName(), logger);
				List<String> errors=null;
				File folderToSave=new File (outputFolder);
				if (!folderToSave.exists()){
					folderToSave.mkdirs();
				}
				String outputFile= folderToSave + File.separator + file.getName();
				try{
					errors=TestUtil.roundTripConvert(file,true,outputFile,false );
				}
				catch (SBOLGraphException e){
					e.printStackTrace();
					printError("Error testing " + file.getName() + ": " + e.getMessage(),logger);
					//logger.throwing("AllFilesTest", "testRecursive:" + file.getName(), e);
					//Try without validation
					printError("Converting without SBOL3 validation", logger);
					try{
						errors=TestUtil.roundTripConvert(file,false,outputFile,false);
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
			}
		}
	}

	private static void print(String message, Logger logger) {
		//System.out.println(message);
		logger.info(message);		
	}

	private static void printError(String message, Logger logger) {
		//System.out.println(message);
		logger.severe(message);		
	}
}
