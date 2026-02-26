package org.sbolstandard.converter.tests;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.sbolstandard.converter.ConverterLogger;
import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.slf4j.LoggerFactory;

/**
 * Unit test for simple App.
 */
public class SBOL3_to_2_Test {
	@Test
	public void TestSBOL3AllFiles() throws Exception {				
		File outputFolder = new File("output/SBOLTestSuite_conversion/SBOL3/");
		File inputFolder = new File("../../libSBOLj3/libSBOLj3/output/");
		//convertFile(outputFolder, new File(inputFolder.getPath() + "/combine2020/combine2020.rdf"));
		// Process all .rdf files recursively
		List<String> exclusionFolders = Arrays.asList("invalid", "component_urn_uri");
		int[] counts = processRDFFiles(outputFolder, inputFolder, exclusionFolders);
		System.out.println("Successfully converted " + counts[0] + " file(s).");
		System.out.println("Failed to convert " + counts[1] + " file(s).");	
	}
	
	private int[] processRDFFiles(File outputFolder, File folder, List<String> exclusionFolders) throws Exception {
		if (!folder.exists() || !folder.isDirectory()) {
			return new int[]{0, 0};
		}
		// Check if folder name is in exclusion list
		for (String exclusion : exclusionFolders) {
			if (folder.getName().equalsIgnoreCase(exclusion)) {
				return new int[]{0, 0};
			}
		}
		File[] files = folder.listFiles();
		if (files == null) {
			return new int[]{0, 0};
		}
		
		int successCount = 0;
		int failureCount = 0;
		
		for (File file : files) {
			if (file.isDirectory()) {
				// Recursively process subdirectories
				int[] subCounts = processRDFFiles(outputFolder, file, exclusionFolders);
				successCount += subCounts[0];
				failureCount += subCounts[1];
			} else if (file.isFile() && file.getName().endsWith(".rdf")) {
				// Process .rdf files
				try {
					convertFile(outputFolder, file);
					successCount++;
				} catch (Exception e) {
					System.err.println("Failed to convert file: " + file.getPath());
					System.err.println("	Error: " + e.getMessage());					
					e.printStackTrace();
					System.err.println("***************************************************");
					System.err.println("");
					failureCount++;
				}
			}
		}
		
		return new int[]{successCount, failureCount};
	}

	private void convertFile(File outputFolder,File file) throws Exception
	{
		
		String dest=outputFolder.getPath() + File.separator +  file.getName();
		File sbol2File=new File (dest + "_sbol3_to_sbol2");
		File tmpSbol3FileRDF=new File (dest + "_sbol3.xml");
		File tmpSbol3FileTTL=new File (dest + "_sbol3.ttl");
		

		SBOLDocument sbol3Doc=SBOLIO.read(file);
		SBOLIO.write(sbol3Doc,tmpSbol3FileRDF,  SBOLFormat.RDFXML);
		SBOLIO.write(sbol3Doc,tmpSbol3FileTTL,  SBOLFormat.TURTLE);

		SBOLDocumentConverter converter = new SBOLDocumentConverter();
		org.sbolstandard.core2.SBOLDocument sbol2Doc = converter.convert(sbol3Doc);
		
		SBOLWriter.write(sbol2Doc, sbol2File);
	}
}
