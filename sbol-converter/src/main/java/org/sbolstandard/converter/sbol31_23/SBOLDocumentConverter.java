package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Component;

public class SBOLDocumentConverter {

	public org.sbolstandard.core2.SBOLDocument convert(SBOLDocument sbol3Doc) throws SBOLGraphException, SBOLValidationException {
        org.sbolstandard.core2.SBOLDocument sbol2Doc = new org.sbolstandard.core2.SBOLDocument();

        ComponentConverter cConverter = new ComponentConverter();
        for (Component c : sbol3Doc.getComponents()) {
            cConverter.convert(sbol2Doc, c);            
        }
        return sbol2Doc;
	}
}
