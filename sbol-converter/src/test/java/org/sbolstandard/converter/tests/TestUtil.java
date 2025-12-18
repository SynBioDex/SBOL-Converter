	
package org.sbolstandard.converter.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;

import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFFormat;
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
import org.sbolstandard.core3.util.RDFUtil;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.util.SBOLUtil;
import org.sbolstandard.core3.validation.IdentifiedValidator;

public class TestUtil {

	public static void runTestSuiteFile(File file) throws Exception {

		runTestSuiteFile(file, 0);
	}

		public static void runTestSuiteFile(File file, int expectedErrorCount) throws Exception {

		int numOfErros = 0;
		List<String> errors=roundTripConvert(file);
		
		for (String error : errors) {
			System.out.println(error);
			numOfErros++;
		}
		assertTrue(numOfErros == expectedErrorCount, "Errors in conversion from" + file.getName());
		System.out.println("--------");
	}
	
	public static List<String> roundTripConvert(File file) throws Exception {		
		return roundTripConvert(file, true,null, true);		
	}

	public static List<String> roundTripConvert(File file, boolean sbol3Validate, String outputFile, boolean printToScreen) throws Exception {
		System.out.println("Converting the SBOL2 file (Method: Default): " + file.getName());
		
		org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(file);
		return roundTripConvert(doc, sbol3Validate, outputFile, printToScreen);		
	}
	public static List<String> roundTripConvertForNC(File file, boolean sbol3Validate, String outputFile, boolean printToScreen) throws Exception {
		System.out.println("Converting the SBOL2 file (Method for Non Compliant Files -  Validation " + (sbol3Validate ? "Enabled" : "Disabled") + "): " + file.getName());
		
		org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(file);
		return roundTripConvert(doc, sbol3Validate, outputFile, printToScreen);		
	}
	public static List<String> roundTripConvertNonBP(File file, boolean sbol3Validate, String outputFile, boolean printToScreen) throws Exception {
		System.out.println("Converting the SBOL2 file (Method: NonBP): " + file.getName());
		boolean previousSetting=Configuration.getInstance().isValidateRecommendedRules();
		Configuration.getInstance().setValidateRecommendedRules(false);		
		org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(file);
		List<String> result = roundTripConvert(doc, sbol3Validate, outputFile, printToScreen);	
		Configuration.getInstance().setValidateRecommendedRules(previousSetting);
		return result;
	}
	public static List<String> roundTripConvertForIC(File file, boolean sbol3Validate, String outputFile, boolean printToScreen) throws Exception {
		System.out.println("Converting the SBOL2 file (Method: IC): " + file.getName());
		boolean previousSetting = Configuration.getInstance().isCompleteDocument();		
		Configuration.getInstance().setCompleteDocument(false);
		org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(file);		
		List<String> result = roundTripConvert(doc, sbol3Validate, outputFile, printToScreen);
		Configuration.getInstance().setCompleteDocument(previousSetting);
		return result;
	}
	public static List<String> roundTripConvertReadFromString(File file, boolean sbol3Validate, String outputFile, boolean printToScreen) throws Exception {
		System.out.println("Converting the SBOL2 file: " + file.getName());
		
		org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(file);
		OutputStream os = new ByteArrayOutputStream();		
		SBOLWriter.write(doc, os);
		InputStream is = new ByteArrayInputStream(((ByteArrayOutputStream) os).toByteArray());
		SBOLDocument doc2 = SBOLReader.read(is);
		System.out.println("++++++++++++++Read SBOL2 from string:++++++++++++++");

		return roundTripConvert(doc2, sbol3Validate, outputFile, printToScreen);		
	}


    private static void createSortedNtriples(File input, File output) throws FileNotFoundException, IOException, SBOLGraphException {
                Model model = RDFUtil.read(input, RDFFormat.RDFXML);
                File fileTmpUnsortedSBOL2=new File(output.getParent(), input.getName() + "_unsorted.nt");
				RDFUtil.write(model, fileTmpUnsortedSBOL2, RDFFormat.NTRIPLES);
                SBOLUtil.sort(fileTmpUnsortedSBOL2, output, Charset.forName("ASCII"));
				fileTmpUnsortedSBOL2.delete();
        }

	public static List<String> roundTripConvert(SBOLDocument sbol2InputDocument, boolean sbol3Validate, String outputFile, boolean printToScreen) throws Exception {
		Configuration.getInstance().setValidateBeforeSaving(sbol3Validate);
		Configuration.getInstance().setValidateAfterSettingProperties(sbol3Validate);
		
		if (printToScreen){
			SBOLWriter.write(sbol2InputDocument, System.out);
		}
		if (outputFile != null) {
			File sbol2File = new File(outputFile + "_sbol2.xml");			
			SBOLWriter.write(sbol2InputDocument, sbol2File);
			createSortedNtriples(sbol2File, new File(outputFile + "_sbol2_sorted.nt"));
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
			SBOLIO.write(sbol3Doc, new File(outputFile + "_sbol3.ttl"), SBOLFormat.TURTLE);	
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
			createSortedNtriples(new File(outputFile + "_sbol2_from_sbol3.xml"), new File(outputFile + "_sbol2_from_sbol3_sorted.nt"));
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

	public static Logger getLogger(String fileName) throws SecurityException, IOException {
		Logger logger= Logger.getLogger(AllFilesTest.class.getName());
		  // Disable default handlers
        logger.setUseParentHandlers(false);

		// Create FileHandler with pattern
        FileHandler fileHandler = new FileHandler("output/logs/" + fileName, false);  // true = append mode
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

	private void processFile()
	{

	}

	public static void testRecursiveFromDirectory(File folder, String outputFolder, Logger logger, boolean isNonBP, boolean isIC, boolean isNC) throws Exception{	
		logger.info("Testing folder " + folder.getAbsolutePath());

		File[] files=folder.listFiles();
		int successCounter=0;
		int successCounterWithNoErrors=0;
		List<String> allDiffFiles = new ArrayList<>();
		List<String> allDiffFilesOutputs = new ArrayList<>();
		List<String> diffFiles = new ArrayList<>();
		List<String> diffFilesWarnings = new ArrayList<>();
		
		int totalCounter=0;
		int withoutValidationCounter=0;
		List<String> withoutValidationFiles = new ArrayList<>();
		String diffFileName = getDiffFileName(isNonBP, isIC, isNC);
		for (int i=0;i<files.length;i++)
		{
			File file=files[i];
			if (file.isDirectory()){
				testRecursiveFromDirectory(folder, outputFolder, logger, isNonBP, isIC, isNC);
			}
			else{	
				String extension = getFileExtension(file.getName());
				if (extension==null || extension.isEmpty()){
					printError("File " + file.getName() + " has no extension, skipping", logger);
					continue;
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
					if (isNonBP) {
						errors = roundTripConvertNonBP(file,true,outputFile,false );
					} else if (isIC) {
						errors = roundTripConvertForIC(file,true,outputFile,false );
					} else if (isNC) {
						errors = roundTripConvertForNC(file, true, outputFile,false );
					} else {
						errors = roundTripConvert(file,true,outputFile,false );
					}
					successCounter++;
					if (errors==null || errors.size()==0){
						successCounterWithNoErrors++;
					} 
					else {
					 	diffFiles.add(file.getName());
					 	diffFilesWarnings.add(errors.toString());
						allDiffFiles.add(file.getName());
						allDiffFilesOutputs.add("DIFF, "+errors.toString());
					}
				}
				catch (SBOLGraphException e){
					e.printStackTrace();
					printError("Error testing " + file.getName() + ": " + e.getMessage(),logger);					
					allDiffFiles.add(file.getName());
					String sanitizedMsg = e.getMessage();
					if (sanitizedMsg != null) {
						sanitizedMsg = sanitizedMsg.replace("\t", "; ").replace(",", "; ").replace("\n", "; ");
					}
					allDiffFilesOutputs.add("VALERROR, "+sanitizedMsg);
					
					printError("Converting without SBOL3 validation", logger);
					try{
						if (isNonBP) {
							errors = roundTripConvertNonBP(file,false,outputFile,false );
						} else if (isIC) {
							errors = roundTripConvertForIC(file,false,outputFile,false );
						} else if (isNC) {
							errors = roundTripConvertForNC(file, false, outputFile,false );
						} else {
							errors = roundTripConvert(file,false,outputFile,false );
						}
						withoutValidationCounter++;
						withoutValidationFiles.add(file.getName());

					}
					catch (Exception e2){
						logger.severe("Error testing " + file.getName() + " without validation: " + e2.getMessage());
						e2.printStackTrace();						
						throw e2;
					}					
				}
				if (errors!=null && errors.size()>0){
					logger.warning(DisplayErrors(errors));
					print ("Completed converting " + file.getName(),logger);
					print ("////////////////////////////////////////////", logger);
					
				}
			}
		}
		logger.info("Testing folder " + folder.getAbsolutePath());
		String conversionSummary = String.format("Completed converting SBOL2 files in folder %s. %d/%d files were converted successfully. %d/%d were round tripped identically.", folder.getAbsolutePath(), successCounter, totalCounter, successCounterWithNoErrors, totalCounter);
		if (withoutValidationCounter>0){
			conversionSummary+=String.format(" %d additional files could be converted without SBOL3 validation.", withoutValidationCounter);
		}
		print(conversionSummary, logger);
		if (!diffFiles.isEmpty()) {
			print("****************************************", logger);
			print("Files that DIFFERED after round-tripping: \n" + String.join(", \n", diffFiles), logger);
			print("****************************************", logger);
		}
		if (!withoutValidationFiles.isEmpty()) {
			print("+++++++++++++++++++++++++++++++++++++++++", logger);
			print("Files that could be converted without SBOL3 VALIDATION: \n" + String.join(", \n", withoutValidationFiles), logger);
			print("+++++++++++++++++++++++++++++++++++++++++", logger);
		}

		writeDiffFilesWithWarnings(allDiffFiles, allDiffFilesOutputs, diffFileName);

	}

	/**
	 * Writes the diffFiles and diffFilesWarnings lists to a file, combining each filename and its corresponding warning in a comma-separated style.
	 * Each line in the output file will be: filename,warning
	 */
	private static void writeDiffFilesWithWarnings(List<String> fileNames, List<String> warnings, String outputPath) throws IOException {
		if (fileNames == null || warnings == null || fileNames.size() != warnings.size()) {
			throw new IllegalArgumentException("fileNames and warnings must be non-null and of equal size");
		}
		try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(outputPath))) {
			for (int i = 0; i < fileNames.size(); i++) {
				String line = String.valueOf(i+1) + ", " + fileNames.get(i) + ", " + warnings.get(i);
				writer.write(line);
				writer.newLine();
			}
		}
	}

	private static String getDiffFileName(boolean isNonBP, boolean isIC, boolean isNC) {
		if (isNonBP) {
			return "output/logs/AllDiffFilesNonBPOutputs.csv";
		} else if (isIC) {
			return "output/logs/AllDiffFilesICOutputs.csv";
		} else if (isNC) {
			return "output/logs/AllDiffFilesNCOutputs.csv";
		} else {
			return "output/logs/AllDiffFilesOutputs.csv";
		}
	}
	

	private static void print(String message, Logger logger) {
		System.out.println(message);
		logger.info(message);		
	}

	private static void printError(String message, Logger logger) {
		logger.severe(message);		
	}

}
