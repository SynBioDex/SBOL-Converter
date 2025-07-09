package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Sequence;

import java.net.URI;

import org.sbolstandard.converter.Util;

public class SequenceAnnotationToSubComponentConverter
		implements ChildEntityConverter<org.sbolstandard.core2.SequenceAnnotation, SubComponent> {

	@Override
	public SubComponent convert(SBOLDocument document, Identified sbol3Parent,
			org.sbolstandard.core2.Identified sbol2ParentSeqAnno, SequenceAnnotation sbol2SeqAnno, Parameters parameters)
			throws SBOLGraphException, SBOLValidationException {
	
		ComponentDefinition sbol2ParentCompDef = (ComponentDefinition) sbol2ParentSeqAnno;		
		URI sbol3ComponentURI=parameters.getMapping(sbol2SeqAnno.getComponent().getIdentity());
		SubComponent sbol3SubComponent = document.getIdentified(sbol3ComponentURI, SubComponent.class);
		
		//Roles also come from Component entities
		if (sbol3SubComponent.getRoles()==null){ 
			sbol3SubComponent.setRoles(Util.convertSORoles2_to_3(sbol2SeqAnno.getRoles()));
		}
		else{
			for (URI role: sbol2SeqAnno.getRoles()) {
				sbol3SubComponent.addRole(Util.convertSOUri_2_to_3(role));
			}		
		}
		Sequence sbol3Sequence = Util.getSBOL3SequenceFromSBOl2Parent(document, sbol2ParentCompDef, parameters);
		
		if (sbol2SeqAnno.getLocations()!=null){
			for (org.sbolstandard.core2.Location sbol2Location : sbol2SeqAnno.getLocations()) {
	
				Location sbol3Location = null;
				org.sbolstandard.core3.vocabulary.Orientation sbol3Orientation = Util.toSBOL3Orientation(sbol2Location.getOrientation());
	
				// Handle Range location type (start and end positions).
				if (sbol2Location instanceof org.sbolstandard.core2.Range) {
					org.sbolstandard.core2.Range sbol2Range = (org.sbolstandard.core2.Range) sbol2Location;
					sbol3Location = sbol3SubComponent.createRange(sbol2Location.getDisplayId(), sbol2Range.getStart(), sbol2Range.getEnd(), sbol3Sequence);
				} 
				else if (sbol2Location instanceof org.sbolstandard.core2.Cut) {
					org.sbolstandard.core2.Cut sbol2Cut = (org.sbolstandard.core2.Cut) sbol2Location;
					sbol3Location = sbol3SubComponent.createCut(sbol2Location.getDisplayId(), sbol2Cut.getAt(), sbol3Sequence);											
				} 
				else if (sbol2Location instanceof org.sbolstandard.core2.GenericLocation) {
					sbol3Location = sbol3SubComponent.createEntireSequence(sbol2Location.getDisplayId(), sbol3Sequence);												
				}
				
				sbol3Location.setOrientation(sbol3Orientation);				
				Util.copyIdentified(sbol2Location, sbol3Location);
			}
		}
		Util.copyIdentified(sbol2SeqAnno, sbol3SubComponent);
		return sbol3SubComponent;
	}
}