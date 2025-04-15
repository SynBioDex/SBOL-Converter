package org.sbolstandard.converter.sbol23_31;



import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class SBOLDocumentConverter {

	public SBOLDocument convert(org.sbolstandard.core2.SBOLDocument sbol2Doc) throws SBOLGraphException {
        SBOLDocument sbol3Doc = new SBOLDocument();

        ComponentDefinitionConverter cdConverter = new ComponentDefinitionConverter();
        for (ComponentDefinition cd : sbol2Doc.getComponentDefinitions()) {
            cdConverter.convert(sbol3Doc, cd);            
        }
        return sbol3Doc;
	}
}
