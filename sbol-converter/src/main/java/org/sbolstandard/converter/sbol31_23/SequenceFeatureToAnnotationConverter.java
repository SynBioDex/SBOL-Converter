package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.entity.ExperimentalData;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.util.SBOLGraphException;


public class SequenceFeatureToAnnotationConverter implements ChildEntityConverter<SequenceFeature, SequenceAnnotation>{

    @Override
    public SequenceAnnotation convert(SBOLDocument doc, org.sbolstandard.core2.Identified parent, SequenceFeature seqf) throws SBOLGraphException, SBOLValidationException {    	
    	
    	// SBOL 3 SequenceFeature maps to SBOL2 SequenceAnnotation
    	ComponentDefinition parentCompDef = (ComponentDefinition) parent;
    	
    	//SequenceFeature feature = new SequenceFeature(document.getRDFModel(), seqa.getIdentity());
    	Sequence sbol2seq = seqf.getLocations().get(0).getSequence();
    	
    	// Location uzerinden ilerleyecegim
    	// Once locationi alip ondan sequence i bulacagim
    	
    	
    	
    	
    	
    	//Util.copyIdentified(input, exp);	
		return null;
	}
}
