package org.sbolstandard.converter;

import java.io.IOException;
import java.net.URI;

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

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) throws SBOLValidationException, IOException, SBOLConversionException, SBOLGraphException {
        System.out.println("Hello World!");
        Configuration.getInstance().setValidateBeforeSaving(false);
        SBOLDocument doc3 = new SBOLDocument();
        doc3.setBaseURI(URI.create("http://dummy.org"));
        org.sbolstandard.core2.SBOLDocument doc = SBOLReader.read("/Users/myers/git/SBOLTestSuite/sbol2/RepressionModel.xml");
        
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
}
