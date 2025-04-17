package org.sbolstandard.converter.sbol23_31;

import java.util.Set;

import org.sbolstandard.core2.Collection;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.Experiment;
import org.sbolstandard.core2.ExperimentalData;
import org.sbolstandard.core2.Model;
import org.sbolstandard.core2.Sequence;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class SBOLDocumentConverter {

	public SBOLDocument convert(org.sbolstandard.core2.SBOLDocument sbol2Doc) throws SBOLGraphException {
        SBOLDocument sbol3Doc = new SBOLDocument();

        SequenceConverter seqConverter = new SequenceConverter();
        for (Sequence seq : sbol2Doc.getSequences()) {
        	seqConverter.convert(sbol3Doc, seq);
        }
        
        ModelConverter mConverter = new ModelConverter();
        for (Model mod : sbol2Doc.getModels()) {
        	mConverter.convert(sbol3Doc, mod);
        }
        
        ComponentDefinitionConverter cdConverter = new ComponentDefinitionConverter();
        for (ComponentDefinition cd : sbol2Doc.getComponentDefinitions()) {
            cdConverter.convert(sbol3Doc, cd);            
        }
        
        ExperimentalDataConverter edConverter = new ExperimentalDataConverter();
        for (ExperimentalData ed : sbol2Doc.getExperimentalData()) {
        	edConverter.convert(sbol3Doc, ed);            
        }
        
        ExperimentConverter eConverter = new ExperimentConverter();
        for (Experiment cd : sbol2Doc.getExperiments()) {
            eConverter.convert(sbol3Doc, cd);            
        }
        
        CollectionConverter collectionConverter = new CollectionConverter();        
        Set<Collection> collections= sbol2Doc.getCollections();
        if (collections != null) {
			for (Collection collection : collections) {
				collectionConverter.convert(sbol3Doc, collection);
			}
		}
        return sbol3Doc;
	}
}
