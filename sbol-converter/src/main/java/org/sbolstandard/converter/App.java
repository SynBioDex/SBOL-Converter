package org.sbolstandard.converter;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.ObjectInputFilter.Config;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;
import org.apache.jena.base.Sys;
import org.sbolstandard.converter.sbol23_31.SBOLDocumentConverter;
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
import org.sbolstandard.core3.validation.SBOLValidator;

import static org.sbolstandard.core.datatree.Datatree.NamespaceBinding;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.namespace.QName;
import org.sbolstandard.core.datatree.NamespaceBinding;
import org.sbolstandard.core.io.graphviz.Styler.document;




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
		System.err.println("\tjava --jar sbol-converter-<version>-.jar [options] <inputFile> [-o <outputFile> -e <compareFile>]");
		System.err.println();
		System.err.println("Options:");
		System.err.println("\t-l  <language> specfies language (SBOL1/SBOL2/SBOL3/GenBank/FASTA/SnapGene/GFF3/CSV) for output (default=SBOL2)");
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

        // SBOL3 to SBOL2 test
        //args=new String[] {"test_files/invalid.ttl", "-l", "SBOL2", "-o", "test_files/invalid-out.xml"};
        
        // SBOL2 to GenBank test
        //args=new String[] {"test_files/invalid-out.xml", "-l", "GenBank", "-p", "keele", "-o", "test_files/outputs/cSbol2ToGenBank.gb"};

        // SBOL3 to GenBank test
        //args=new String[] {"test_files/invalid.ttl", "-l", "GenBank", "-o", "test_files/outputs/3ToGenBank.gb"};

        // GenBank to SBOL2 test
        //args=new String[] {"test_files/genBankTestFile.gb", "-l", "SBOL2", "-p", "keele", "-o", "test_files/outputs/gbtoSBOL2.xml"};

        // GenBank to SBOL3 test
        //args=new String[] {"test_files/genBankTestFile.gb", "-l", "SBOL3", "-p", "keele", "-o", "test_files/outputs/gbtoSBOL3.ttl"};


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

        String documentAsString = "";

		try {
		    documentAsString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

            foundSBOL2 = documentAsString.contains(sbol2.getNamespaceURI());
            foundSBOL3 = documentAsString.contains(sbol3.getNamespaceURI());

		} catch (Exception e) {

            // TODO: CHECK THIS EXCEPTION HANDLING
			foundSBOL3 = true;
		}

        //OLD IF-ELSE STRUCTURE METHOD CALL FOR THE SAKE OF DEMONSTRATION
        //oldIfElseStructure(file, fileName, URIPrefix, complete, compliant, bestPractice, typesInURI, version, keepGoing, compareFile, compareFileName, mainFileName, topLevelURIStr, genBankOut, sbolV1out, fastaOut, snapGeneOut, gff3Out, csvOut, outputFile, showDetail, noOutput, changeURIprefix, enumerate, foundSBOL2, foundSBOL3, documentAsString, sbolV3out, sbolV2out);
        
        if (!foundSBOL3 && !sbolV3out) {
            // THERE IS NO SBOL3 CONTENT AND THERE IS NO SBOL3 OUTPUT REQUESTED,
            // INPUT FILE CAN BE SBOL2/GENBANK/FASTA etc.
            // SO PERFORM SBOL2 VALIDATION OR CONVERSION TO/FROM SBOL2
			boolean isDirectory = file.isDirectory();
			if (!isDirectory) {
                System.out.println("Either Validate or Convert to/from SBOL2");
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
                    
                            //SBOLValidator.getValidator().validate(null)
				}
			}
		} else { // SBOL3 CONTENT IS PRESENT OR SBOL3 OUTPUT IS REQUESTED // SO PERFORM SBOL3 VALIDATION OR CONVERSION TO/FROM SBOL3
	        if (foundSBOL2 && !foundSBOL3) {// THERE IS SBOL2 IN INPUT, NO SBOL3 IN INPUT - SBOL2 TO SBOL3 CONVERSION
                InputStream inputStream = new ByteArrayInputStream(documentAsString.getBytes(StandardCharsets.UTF_8));
	        	org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read(inputStream);
                //System.out.println("SBOL2 to SBOL3 document read.");
	        	if (sbolV3out) {
                    System.out.println("Converting SBOL2 to SBOL3...");
                    //CONVERT SBOL2 TO SBOL3
                    Configuration.getInstance().setCompleteDocument(complete);
	                Configuration.getInstance().setValidateRecommendedRules(bestPractice);
	        		SBOLDocumentConverter converter2to3 = new SBOLDocumentConverter();
	        		org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = converter2to3.convert(doc);
	        		if (!noOutput) {
                        // OUTPUT THE SBOL3 DOCUMENT
	        			if (outputFile.equals("")) {
                            // OUTPUT TO SYSTEM.OUT
	        				String result=SBOLIO.write(sbol3Doc, SBOLFormat.RDFXML);
	        				System.out.println(result);
	        			} else {
                            // OUTPUT TO SPECIFIED FILE
	        				File outFile = new File(outputFile);
	        				SBOLIO.write(sbol3Doc, outFile, SBOLFormat.RDFXML);
	        			}
	        		}
	        	}
	        } else if (foundSBOL3) { // THERE IS SBOL3 CONTENT
	        	if (sbolV3out) {
                    // SBOL3 FOUND AND SBOL3 OUTPUT REQUESTED
                    // ?? WHAT IS THIS ??
                    File inFile = new File(fileName);
	                Configuration.getInstance().setCompleteDocument(complete);
	                Configuration.getInstance().setValidateRecommendedRules(bestPractice);
	        	    org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = SBOLIO.read(inFile);
                    // OUTPUT THE SBOL3 DOCUMENT
        			if (outputFile.equals("")) {
                        // OUTPUT TO SYSTEM.OUT
        				String result=SBOLIO.write(sbol3Doc, SBOLFormat.RDFXML);
        				System.out.println(result);
        			} else {
                        // OUTPUT TO SPECIFIED FILE
        				File outFile = new File(outputFile);
        				SBOLIO.write(sbol3Doc, outFile, SBOLFormat.RDFXML);
        			}
	        	} else if (sbolV2out || genBankOut || fastaOut || snapGeneOut || gff3Out || csvOut) {
                    //CONVERT SBOL3 TO SBOL2 OR GENBANK/FASTA/SNAPGENE/GFF3/CSV
                    System.out.println("Converting SBOL3 to SBOL2...");
                    File inFile = new File(fileName);
	                Configuration.getInstance().setCompleteDocument(complete);
	                Configuration.getInstance().setValidateRecommendedRules(bestPractice);
	        	    org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = SBOLIO.read(inFile);
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
	        	} else {
                    System.out.println("Performing SBOL3 validation...");
                    
                    boolean isDirectory = file.isDirectory();
                    if (!isDirectory) {
                        //org.sbolstandard.core3.validation.SBOLValidator sbol3validator = SBOLValidator.getValidator();
                        int result = sbol3Validation(System.out,System.err,fileName, URIPrefix, "", complete, compliant, bestPractice, typesInURI, 
                                version, keepGoing, compareFile, compareFileName, mainFileName, 
                                topLevelURIStr, genBankOut, sbolV1out, fastaOut, snapGeneOut, gff3Out, csvOut, outputFile, 
                                showDetail, noOutput, changeURIprefix, enumerate);
                        System.exit(result);
                    } else {
                        for (File eachFile : file.listFiles()) {
                            // TODO: should allow compare to a directory of same named files
                            System.out.println(eachFile.getAbsolutePath());
                            sbol3Validation(System.out,System.err,eachFile.getAbsolutePath(), URIPrefix, "", complete, compliant, bestPractice, typesInURI, 
                                    version, keepGoing, compareFile, compareFileName, mainFileName, 
                                    topLevelURIStr, genBankOut, sbolV1out, fastaOut, snapGeneOut, gff3Out, csvOut, outputFile, 
                                    showDetail, noOutput, changeURIprefix, enumerate);
                            
                                    //SBOLValidator.getValidator().validate(null)
                        }
                    }
                }
	        } else {
                
                // INPUT FILE IS EITHER SBOL1 OR NON-SBOL
                System.out.println("Not Provided SBOL2 or SBOL3 content in the input file.");
                // FIRST CONVERT TO SBOL2 THEN TO SBOL3
                org.sbolstandard.core2.SBOLDocument sbol2Doc = SBOLValidate.validate(System.out,System.err,fileName, URIPrefix, "", complete, compliant, bestPractice, typesInURI, 
						version, keepGoing, compareFile, compareFileName, mainFileName, 
						topLevelURIStr, genBankOut, sbolV1out, fastaOut, snapGeneOut, gff3Out, csvOut, outputFile, 
						showDetail, noOutput, changeURIprefix, enumerate);
                List<String> errors = SBOLValidate.getErrors();
		    	if (errors == null || errors.size() > 0) {
		    		System.exit(1);
		    	}

                System.out.println("Converting SBOL2 to SBOL3...");
                Configuration.getInstance().setCompleteDocument(complete);
                Configuration.getInstance().setValidateRecommendedRules(bestPractice);
        		SBOLDocumentConverter converter2to3 = new SBOLDocumentConverter();
        		org.sbolstandard.core3.entity.SBOLDocument sbol3Doc = converter2to3.convert(sbol2Doc);
                if (!noOutput) {
                        // OUTPUT THE SBOL3 DOCUMENT
	        			if (outputFile.equals("")) {
                            // OUTPUT TO SYSTEM.OUT
	        				String result=SBOLIO.write(sbol3Doc, SBOLFormat.RDFXML);
	        				System.out.println(result);
	        			} else {
                            // OUTPUT TO SPECIFIED FILE
	        				File outFile = new File(outputFile);
	        				SBOLIO.write(sbol3Doc, outFile, SBOLFormat.RDFXML);
	        			}
	        		}
            }
		}
    
    }
    


    private static int sbol3Validation(PrintStream outputStream, PrintStream errorStream, String fileName,
			String URIPrefix, String defaultDisplayId, boolean complete, boolean compliant, boolean bestPractice, boolean typesInURI,
			String version, boolean keepGoing, String compareFile, String compareFileName, String mainFileName,
			String topLevelURIStr, boolean genBankOut, boolean sbolV1out, boolean fastaOut, boolean snapGeneOut, boolean gff3Out, boolean csvOut, String outputFile,
			boolean showDetail, boolean noOutput, boolean changeURIPrefix, boolean enumerate) throws SBOLGraphException, FileNotFoundException{


            // Not used parameters
            String uriPrefix_unused = URIPrefix;
            String defaultDisplayId_unused = defaultDisplayId;
            boolean compliant_unused = compliant;
            boolean typesInURI_unused = typesInURI;
            String compareFile_unused = compareFile;
            String compareFileName_unused = compareFileName;
            String mainFileName_unused = mainFileName;
            String topLevelURIStr_unused = topLevelURIStr;
            boolean genBankOut_unused = genBankOut;
            boolean sbolV1out_unused = sbolV1out;
            boolean fastaOut_unused = fastaOut;
            boolean snapGeneOut_unused = snapGeneOut;
            boolean gff3Out_unused = gff3Out;
            boolean csvOut_unused = csvOut;
            boolean changeURIPrefix_unused = changeURIPrefix;
            boolean enumerate_unused = enumerate;

            List<String> result;
            try {
                result = sbol3ValidationOnlyRequiredParameters(fileName, complete, bestPractice);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            } catch (SBOLGraphException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw e;
            }
            
            //System.out.println("Error List Size: " + result.size());
            if (result.size()>0) {
                outputStream.println("SBOL3 Validation failed.");
                errorStream.println(result);
                return 1;
            } else {
                outputStream.println("SBOL3 Validation successful, no errors.");
                return 0;
            }
    }

    private static List<String> sbol3ValidationOnlyRequiredParameters(String fileName, boolean complete,  boolean bestPractice) throws SBOLGraphException, FileNotFoundException { 

            
            File file = new File(fileName);
            List<String> errorList = new ArrayList<>();
            try {
                Configuration.getInstance().setValidateAfterReadingSBOLDocuments(false);
                Configuration.getInstance().setCompleteDocument(complete);
                Configuration.getInstance().setValidateRecommendedRules(bestPractice);
                SBOLDocument sbolDocument = SBOLIO.read(file);
            
                errorList = SBOLValidator.getValidator().validate(sbolDocument);
                //System.out.println("Validation errors: " + errorList.size());
                return errorList;
                
            } catch (FileNotFoundException | SBOLGraphException e) {
                
                e.printStackTrace();
                //return errorList;
                throw e;
            }

    }

    private static void  ifElseStructure(boolean foundSBOL2, boolean sbolV3out, boolean genBankOut, boolean fastaOut, boolean snapGeneOut, boolean gff3Out, boolean csvOut, boolean foundSBOL3, boolean sbolV2out) {
        // JUST TO HAVE ANOTHER METHOD FOR THE SAKE OF DEMONSTRATION
        if (foundSBOL2 && foundSBOL3){
            // Input Document is MIXED SBOL2 AND SBOL3
            // WHAT SHOULD BE DONE IN THIS CASE?

        } else if (foundSBOL2){
            // Input Document is SBOL2
            if(sbolV3out){
                // CONVERT SBOL2 TO SBOL3
                /*convertToSBOL2ToSBOL3(file, fileName, URIPrefix, complete, compliant, bestPractice, typesInURI,
                    version, keepGoing, compareFile, compareFileName, mainFileName,
                    topLevelURIStr, genBankOut, sbolV1out, fastaOut, snapGeneOut, gff3Out, csvOut, outputFile,
                    showDetail, noOutput, changeURIprefix, enumerate);

            } else if (genBankOut || fastaOut || snapGeneOut || gff3Out || csvOut){
                // CONVERT SBOL2 TO GENBANK/FASTA/SNAPGENE/GFF3/CSV

                /*convertSBOL2ToOtherFormats(file, fileName, URIPrefix, complete, compliant, bestPractice, typesInURI,
                    version, keepGoing, compareFile, compareFileName, mainFileName,
                    topLevelURIStr, genBankOut, sbolV1out, fastaOut, snapGeneOut, gff3Out, csvOut, outputFile,
                    showDetail, noOutput, changeURIprefix, enumerate);*/

            } else {
                // SBOL2 VALIDATION

                /*sbol2Validation(System.out, System.err, fileName, URIPrefix, complete, compliant, bestPractice, typesInURI,
                    version, keepGoing, compareFile, compareFileName, mainFileName,
                    topLevelURIStr, genBankOut, sbolV1out, fastaOut, snapGeneOut, gff3Out, csvOut, outputFile,
                    showDetail, noOutput, changeURIprefix, enumerate);*/
            }
        } else if (foundSBOL3){
            // Input Document is SBOL3
            if (sbolV2out){
                // CONVERT SBOL3 TO SBOL2


            } else if (genBankOut || fastaOut || snapGeneOut || gff3Out || csvOut){
                // CONVERT SBOL3 TO GENBANK/FASTA/SNAPGENE/GFF3/CSV
                // FOR THIS FIRST CONVERT SBOL3 TO SBOL2, THEN SBOL2 TO GENBANK/FASTA/SNAPGENE/GFF3/CSV

            } else {
                // SBOL3 VALIDATION

            }
        } else {
            // Input Document is NEITHER SBOL2 NOR SBOL3
            // POSSIBLY GENBANK/FASTA/SNAPGENE/GFF3/CSV
            if (sbolV2out){
                // CONVERT TO SBOL2

            } else if (sbolV3out){
                // CONVERT TO SBOL3

            } else {
                // VALIDATION OF NON-SBOL DOCUMENT

            }
        }


    }

    private static void convertSBOL2ToOtherFormats(File file, String fileName, String uRIPrefix, boolean complete,
            boolean compliant, boolean bestPractice, boolean typesInURI, String version, boolean keepGoing,
            String compareFile, String compareFileName, String mainFileName, String topLevelURIStr, boolean genBankOut,
            boolean sbolV1out, boolean fastaOut, boolean snapGeneOut, boolean gff3Out, boolean csvOut,
            String outputFile, boolean showDetail, boolean noOutput, boolean changeURIprefix, boolean enumerate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertSBOL2ToOtherFormats'");
    }


    private static void convertToSBOL2ToSBOL3(File file, String fileName, String uRIPrefix, boolean complete,
            boolean compliant, boolean bestPractice, boolean typesInURI, String version, boolean keepGoing,
            String compareFile, String compareFileName, String mainFileName, String topLevelURIStr, boolean genBankOut,
            boolean sbolV1out, boolean fastaOut, boolean snapGeneOut, boolean gff3Out, boolean csvOut,
            String outputFile, boolean showDetail, boolean noOutput, boolean changeURIprefix, boolean enumerate) {
        // TODO Auto-generated method stub
        

        // CONVERT FROM SBOL2 TO SBOL3




    }

    private static void oldIfElseStructure(File file, String fileName, String URIPrefix, boolean complete, boolean compliant, boolean bestPractice, boolean typesInURI,
            String version, boolean keepGoing, String compareFile, String compareFileName, String mainFileName,
            String topLevelURIStr, boolean genBankOut, boolean sbolV1out, boolean fastaOut, boolean snapGeneOut, boolean gff3Out, boolean csvOut, String outputFile,
            boolean showDetail, boolean noOutput, boolean changeURIprefix, boolean enumerate, boolean foundSBOL2, boolean foundSBOL3, String documentAsString, boolean sbolV3out, boolean sbolV2out) throws SBOLGraphException, IOException, SBOLValidationException, SBOLConversionException {
        
    } 
}


