package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.Model;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Component;

public class SBOLDocumentConverter {

	public org.sbolstandard.core2.SBOLDocument convert(SBOLDocument sbol3Doc) throws SBOLGraphException, SBOLValidationException {
        org.sbolstandard.core2.SBOLDocument sbol2Doc = new org.sbolstandard.core2.SBOLDocument();

        SequenceConverter seqConverter = new SequenceConverter();
        if (sbol3Doc.getSequences()!=null) {
        	for (Sequence seq : sbol3Doc.getSequences()) {
        		seqConverter.convert(sbol2Doc, seq);            
        	}
        }
        
        if (sbol3Doc.getComponents()!=null) {
        	ComponentConverter cConverter = new ComponentConverter();
        	for (Component c : sbol3Doc.getComponents()) {
        		cConverter.convert(sbol2Doc, c);            
        	}
        }
        
        if (sbol3Doc.getModels()!=null) {
        	ModelConverter mConverter = new ModelConverter();
        	for (Model m : sbol3Doc.getModels()) {
        		mConverter.convert(sbol2Doc, m);
        	}
        }
        return sbol2Doc;
	}
}
