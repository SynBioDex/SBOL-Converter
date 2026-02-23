package org.sbolstandard.converter.tests;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
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
		//Util.turnOffHibernateInfo();
		//LoggerFactory.getLogger(SBOLDocumentConverter.class);

		//Logger.getLogger("org.hibernate.validator").setLevel(Level.SEVERE);

		/*Logger hvLogger = Logger.getLogger("org.hibernate.validator");
        	hvLogger.setLevel(Level.WARNING);
		*/
        // Also raise levels of existing handlers
        /*Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            handler.setLevel(Level.SEVERE);
        }*/
		
		/* 
		LogManager.getLogManager().reset();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        Logger.getLogger("").addHandler(handler);

        Logger.getLogger("org.hibernate.validator").setLevel(Level.SEVERE);
		*/
		//Logger logger = TestUtil.getLogger("AllFilesTest.log");	
		//logger.info("GMGMGMGM");
		
		File outputFolder = new File("output/SBOLTestSuite_conversion/SBOL3/");
		File inputFolder = new File("../../libSBOLj3/libSBOLj3/output/");
		convertFile(outputFolder, new File(inputFolder.getPath() + "/combine2020/combine2020.rdf"));
		// Process all .rdf files recursively
		//processRDFFiles(outputFolder, inputFolder);
	}
	
	private void processRDFFiles(File outputFolder, File folder) throws Exception {
		if (!folder.exists() || !folder.isDirectory()) {
			return;
		}
		
		File[] files = folder.listFiles();
		if (files == null) {
			return;
		}
		
		for (File file : files) {
			if (file.isDirectory()) {
				// Recursively process subdirectories
				processRDFFiles(outputFolder, file);
			} else if (file.isFile() && file.getName().endsWith(".rdf")) {
				// Process .rdf files
				try {
					convertFile(outputFolder, file);
				} catch (Exception e) {
					System.err.println("Failed to convert file: " + file.getPath());
					e.printStackTrace();
				}
			}
		}
	}

	private void convertFile(File outputFolder,File file) throws Exception
	{
		
		String dest=outputFolder.getPath() + file.getName();
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
