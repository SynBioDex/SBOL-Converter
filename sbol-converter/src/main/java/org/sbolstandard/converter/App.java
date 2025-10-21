package org.sbolstandard.converter;

import static org.sbolstandard.core.datatree.Datatree.NamespaceBinding;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.xml.namespace.QName;

import org.sbolstandard.converter.sbol23_31.SBOLDocumentConverter;
import org.sbolstandard.core.datatree.NamespaceBinding;
import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidate;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SBOLWriter;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.util.Configuration;
import org.sbolstandard.core3.util.SBOLGraphException;

/**
 * Hello world!
 */
public class App {
	static final NamespaceBinding sbol1 = NamespaceBinding("http://sbols.org/v1#", "");
	static final NamespaceBinding sbol2 = NamespaceBinding("http://sbols.org/v2#", "sbol");
	static final NamespaceBinding sbol3 = NamespaceBinding("http://sbols.org/v3#", "sbol");

	private static void usage() {
		System.err.println("Description: validates the contents of an SBOL "
				+ " document, can compare two documents,\n"
				+ "and can convert to/from SBOL 1, to/from SBOL2, to/from SBOL3, GenBank, FASTA, SnapGene, and GFF3 formats.");
		System.err.println();
		System.err.println("Usage:");
		System.err.println("\tjava --jar libSBOLj.jar [options] <inputFile> [-o <outputFile> -e <compareFile>]");
		System.err.println();
		System.err.println("Options:");
		System.err.println("\t-l  <language> specfies language (SBOL1/SBOL2/GenBank/FASTA/SnapGene/GFF3/CSV) for output (default=SBOL2)");
		System.err.println("\t-s  <topLevelURI> select only this object and those it references");
		System.err.println("\t-p  <URIprefix> used for converted objects");
		System.err.println("\t-c  change URI prefix to specified <URIprefix>");
		System.err.println("\t-v  <version> used for converted objects");
		System.err.println("\t-t  uses types in URIs");
		System.err.println("\t-n  allow non-compliant URIs");
		System.err.println("\t-i  allow SBOL document to be incomplete");
		System.err.println("\t-b  check best practices");
		System.err.println("\t-f  fail on first error");
		System.err.println("\t-d  display detailed error trace");
		System.err.println("\t-mf main SBOL file if file diff. option is selected");
		System.err.println("\t-cf second SBOL file if file diff. option is selected");
		System.err.println("\t-no indicate no output file to be generated from validation");
		System.err.println("\t-en enumerate CombinatorialDerivations");

		System.exit(1);
	}
	
	/**
	 * Command line method for reading an input file and producing an output file.
	 * <p>
	 * By default, validations on compliance and completeness are performed, and
	 * types for top-level objects are not used in URIs.
	 * <p>
	 * Options:
	 * <p>
	 * "-o" specifies an output filename
	 * <p>
	 * "-e" specifies a file to compare if equal to
	 * <p>
	 * "-l" indicates the language for output (default=SBOL2, other options SBOL1,
	 * GenBank, FASTA, SnapGene, CSV)
	 * <p>
	 * "-s" select only this topLevel object and those it references
	 * <p>
	 * "-p" specifies the default URI prefix for converted objects
	 * <p>
	 * "-c" change URI prefix to specified URIprefix
	 * <p>
	 * "-v" specifies version to use for converted objects
	 * <p>
	 * "-t" uses types in URIs
	 * <p>
	 * "-n" allow non-compliant URIs
	 * <p>
	 * "-i" allow SBOL document to be incomplete
	 * <p>
	 * "-b" check best practices
	 * <p>
	 * "-f" fail on first error
	 * <p>
	 * "-d" display detailed error trace
	 * <p>
	 * "-mf" main SBOL file if file diff. option is selected
	 * <p>
	 * "-cf" second SBOL file if file diff. option is selected
	 * <p>
	 * "-no" indicate no output file to be generated from validation
	 * <p>
	 * "-en" enumerate CombinatorialDerivations
	 *
	 * @param args
	 *            arguments supplied at command line
	 * @throws SBOLConversionException 
	 * @throws IOException 
	 * @throws SBOLValidationException 
	 * @throws SBOLGraphException 
	 */
	public static void main(String[] args) throws SBOLValidationException, IOException, SBOLConversionException, SBOLGraphException {
		String fileName = "";
		String outputFile = "";
		String compareFile = "";
		String mainFileName = "";
		String compareFileName = "";
		String topLevelURIStr = "";
		String URIPrefix = "";
		String version = null;
		boolean complete = true;
		boolean compliant = true;
		boolean typesInURI = false;
		boolean bestPractice = false;
		boolean keepGoing = true;
		boolean showDetail = false;
		boolean genBankOut = false;
		boolean fastaOut = false;
		boolean snapGeneOut = false;
		boolean gff3Out = false;
		boolean sbolV1out = false;
		boolean sbolV2out = false;
		boolean sbolV3out = false;
		boolean csvOut = false;
		boolean noOutput = false;
		boolean changeURIprefix = false;
		boolean enumerate = false;
		int i = 0;
		while (i < args.length) {
			if (args[i].equals("-i")) {
				complete = false;
			} else if (args[i].equals("-t")) {
				typesInURI = true;
			} else if (args[i].equals("-b")) {
				bestPractice = true;
			} else if (args[i].equals("-n")) {
				compliant = false;
			} else if (args[i].equals("-f")) {
				keepGoing = false;
			} else if (args[i].equals("-d")) {
				showDetail = true;
			} else if (args[i].equals("-c")) {
				changeURIprefix = true;
			} else if (args[i].equals("-en")) {
				enumerate = true;
			} else if (args[i].equals("-s")) { 	
				if (i+1 >= args.length) {
					usage();
				}
				topLevelURIStr = args[i + 1];
				i++;
			} else if (args[i].equals("-l")) {
				if (i + 1 >= args.length) {
					usage();
				}
				if (args[i + 1].equals("SBOL1")) {
					sbolV1out = true;
				} else if (args[i + 1].equals("GenBank")) {
					genBankOut = true;
				} else if (args[i + 1].equals("FASTA")) {
					fastaOut = true;
				}  else if (args[i + 1].equals("SnapGene")) {
					snapGeneOut = true;
				} else if (args[i + 1].equals("GFF3")) {
					gff3Out = true;
				} else if (args[i + 1].equals("CSV")) {
					csvOut = true;
				} else if (args[i + 1].equals("SBOL2")) {
					sbolV2out = true;
				} else if (args[i + 1].equals("SBOL3")) {
					sbolV3out = true;
				} else {
					usage();
				}
				i++;
			} else if (args[i].equals("-o")) {
				if (i + 1 >= args.length) {
					usage();
				}
				outputFile = args[i + 1];
				i++;
			} else if (args[i].equals("-no")) {
				noOutput = true;
			} else if (args[i].equals("-e")) {
				if (i + 1 >= args.length) {
					usage();
				}
				compareFile = args[i + 1];
				i++;
			} else if (args[i].equals("-mf")) {
				if (i + 1 >= args.length) {
					usage();
				}
				mainFileName = args[i + 1];
				i++;
			} else if (args[i].equals("-cf")) {
				if (i + 1 >= args.length) {
					usage();
				}
				compareFileName = args[i + 1];
				i++;
			} else if (args[i].equals("-p")) {
				if (i + 1 >= args.length) {
					usage();
				}
				URIPrefix = args[i + 1];
				i++;
			} else if (args[i].equals("-v")) {
				if (i + 1 >= args.length) {
					usage();
				}
				version = args[i + 1];
				i++;
			} else if (fileName.equals("")) {
				fileName = args[i];
			} else {
				usage();
			}
			i++;
		}
		if (fileName.equals(""))
			usage();

		File file = new File(fileName);
		if (!URIPrefix.equals("")) {
			SBOLReader.setURIPrefix(URIPrefix);
		}
		if (!compliant) {
			SBOLReader.setCompliant(false);
		}
		SBOLReader.setTypesInURI(typesInURI);
		SBOLReader.setVersion(version);
		SBOLReader.setKeepGoing(keepGoing);
		SBOLWriter.setKeepGoing(keepGoing);

		boolean foundSBOL2 = false;
		boolean foundSBOL3 = false;

		org.sbolstandard.core2.SBOLDocument document = SBOLReader.read(file);
		try {
			for (QName n : document.getNamespaces())
			{	
				if (n.getNamespaceURI().equals(sbol2.getNamespaceURI())) foundSBOL2 = true;
				if (n.getNamespaceURI().equals(sbol3.getNamespaceURI())) foundSBOL3 = true;
			}
		} catch (Exception e) {
			foundSBOL3 = true;
		}
		
		if (!foundSBOL3 && !sbolV3out) {
			boolean isDirectory = file.isDirectory();
			if (!isDirectory) {
				SBOLValidate.validate(System.out,System.err,fileName, URIPrefix, "", complete, compliant, bestPractice, typesInURI, 
						version, keepGoing, compareFile, compareFileName, mainFileName, 
						topLevelURIStr, genBankOut, sbolV1out, fastaOut, snapGeneOut, gff3Out, csvOut, outputFile, 
						showDetail, noOutput, changeURIprefix, enumerate);
		    	if (SBOLValidate.getErrors().size()>0) {
		    		System.exit(1);
		    	}
			} else {
				for (File eachFile : file.listFiles()) {
					// TODO: should allow compare to a directory of same named files
					System.out.println(eachFile.getAbsolutePath());
					SBOLValidate.validate(System.out,System.err,eachFile.getAbsolutePath(), URIPrefix, "", complete, compliant, bestPractice, typesInURI, 
							version, keepGoing, compareFile, compareFileName, mainFileName, 
							topLevelURIStr, genBankOut, sbolV1out, fastaOut, snapGeneOut, gff3Out, csvOut, outputFile, 
							showDetail, noOutput, changeURIprefix, enumerate);
				}
			}
		} else {
			// TODO: Currently only supports SBOL2 to SBOL3 conversion and vice versa
	        Configuration.getInstance().setValidateBeforeSaving(false);
	        SBOLDocument doc3 = new SBOLDocument();
	        doc3.setBaseURI(URI.create("http://dummy.org"));
	        
	        if (foundSBOL2 && !foundSBOL3) {
	        	org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(fileName);
	        	//SBOLWriter.write(doc, System.out);
	        	if (sbolV3out) {
	        		SBOLDocumentConverter converter = new SBOLDocumentConverter();
	        		org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = converter.convert(doc);
	        		if (!noOutput) {
	        			if (outputFile.equals("")) {
	        				String result=SBOLIO.write(sbol3Doc, SBOLFormat.RDFXML);
	        				System.out.println(result);
	        			} else {
	        				File outFile = new File(outputFile);
	        				SBOLIO.write(sbol3Doc, outFile, SBOLFormat.RDFXML);
	        			}
	        		}
	        	}
	        } else if (foundSBOL3) {
	    		File inFile = new File(fileName);
	            Configuration.getInstance().setCompleteDocument(complete);
	            Configuration.getInstance().setValidateRecommendedRules(bestPractice);
	        	org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = SBOLIO.read(inFile);
	        	if (sbolV3out) {
        			if (outputFile.equals("")) {
        				String result=SBOLIO.write(sbol3Doc, SBOLFormat.RDFXML);
        				System.out.println(result);
        			} else {
        				File outFile = new File(outputFile);
        				SBOLIO.write(sbol3Doc, outFile, SBOLFormat.RDFXML);
        			}
	        	} else if (sbolV2out) {
	        		org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter converter3_2 = new org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter();
	        		org.sbolstandard.core2.SBOLDocument sbol2Doc = converter3_2.convert(sbol3Doc);
	        		if (!noOutput) {
	        			if (outputFile.equals("")) {
	        				if (sbolV1out) {
		            	    	SBOLWriter.write(sbol2Doc, System.out, org.sbolstandard.core2.SBOLDocument.RDFV1);
	        				} else if (genBankOut) {
		            	    	SBOLWriter.write(sbol2Doc, System.out, org.sbolstandard.core2.SBOLDocument.GENBANK);
	        				} else if (fastaOut) {
		            	    	SBOLWriter.write(sbol2Doc, System.out, org.sbolstandard.core2.SBOLDocument.FASTAformat);
	        				} else if (snapGeneOut) {
		            	    	SBOLWriter.write(sbol2Doc, System.out, org.sbolstandard.core2.SBOLDocument.SNAPGENE);
	        				} else if (gff3Out) {
		            	    	SBOLWriter.write(sbol2Doc, System.out, org.sbolstandard.core2.SBOLDocument.GFF3format);
	        				} else if (csvOut) {
		            	    	SBOLWriter.write(sbol2Doc, System.out, org.sbolstandard.core2.SBOLDocument.CSV);
	        				} else {
		            	    	SBOLWriter.write(sbol2Doc, System.out);
	        				}
	        			} else {
	        				if (sbolV1out) {
		            	    	SBOLWriter.write(sbol2Doc, outputFile, org.sbolstandard.core2.SBOLDocument.RDFV1);
	        				} else if (genBankOut) {
		            	    	SBOLWriter.write(sbol2Doc, outputFile, org.sbolstandard.core2.SBOLDocument.GENBANK);
	        				} else if (fastaOut) {
		            	    	SBOLWriter.write(sbol2Doc, outputFile, org.sbolstandard.core2.SBOLDocument.FASTAformat);
	        				} else if (snapGeneOut) {
		            	    	SBOLWriter.write(sbol2Doc, outputFile, org.sbolstandard.core2.SBOLDocument.SNAPGENE);
	        				} else if (gff3Out) {
		            	    	SBOLWriter.write(sbol2Doc, outputFile, org.sbolstandard.core2.SBOLDocument.GFF3format);
	        				} else if (csvOut) {
		            	    	SBOLWriter.write(sbol2Doc, outputFile, org.sbolstandard.core2.SBOLDocument.CSV);
	        				} else {
		            	    	SBOLWriter.write(sbol2Doc, outputFile);
	        				}
	        			}
	        		}
	        	}
	        }	    	
		}
	}
	/*
    public static void main(String[] args) throws SBOLValidationException, IOException, SBOLConversionException, SBOLGraphException {
        System.out.println("Hello World!");
        Configuration.getInstance().setValidateBeforeSaving(false);
        SBOLDocument doc3 = new SBOLDocument();
        doc3.setBaseURI(URI.create("http://dummy.org"));
        org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read("../SBOLTestSuite/sbol2/RepressionModel.xml");
        
    	SBOLDocumentConverter converter = new SBOLDocumentConverter();
    	org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = converter.convert(doc);
    	
    	org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter converter3_2 = new org.sbolstandard.converter.sbol31_23.SBOLDocumentConverter();
    	org.sbolstandard.core2.SBOLDocument sbol2Doc = converter3_2.convert(sbol3Doc);

    	SBOLWriter.write(sbol2Doc, System.out);
    	
    	SBOLValidate.compareDocuments("SBOL2in", doc, "SBOL2out", sbol2Doc);
    	for (String error : SBOLValidate.getErrors()) {
    		System.out.println(error);
    	}
    	
        String result=SBOLIO.write(sbol3Doc, SBOLFormat.RDFXML);
        System.out.println(result);
    }
    */
}
