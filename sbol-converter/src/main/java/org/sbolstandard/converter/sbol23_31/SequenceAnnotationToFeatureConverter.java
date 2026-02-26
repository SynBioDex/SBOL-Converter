package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Sequence;
import java.net.URI;
import org.sbolstandard.converter.Util;

public class SequenceAnnotationToFeatureConverter implements ChildEntityConverter<org.sbolstandard.core2.SequenceAnnotation,SequenceFeature>{

	@Override
	public SequenceFeature convert(SBOLDocument document, Identified parent, org.sbolstandard.core2.Identified seqaParent, SequenceAnnotation seqa, Parameters parameters)
	        throws SBOLGraphException {
	    
	    // Cast the parent object to a Component (SBOL3).
	    Component parentComponent = (Component) parent;
	    
	    ComponentDefinition parentCompDef = (ComponentDefinition) seqaParent;
	    
	    return getSequenceFeature(document, parentComponent, parentCompDef, seqa, parameters);
	}
	
	private SequenceFeature getSequenceFeature(org.sbolstandard.core3.entity.SBOLDocument document,
			Component sbol3ParentComp, ComponentDefinition sbol2ParentCompDef, SequenceAnnotation seqa, Parameters parameters)
			throws SBOLGraphException {
		
		// Get the SBOL2 Sequence associated with the ComponentDefinition of the SequenceAnnotation.
		URI seqType = Util.getSBOL2SequenceType(sbol2ParentCompDef);
		Sequence sbol3Sequence = Util.getSBOL3SequenceFromSBOl2Parent(document, sbol2ParentCompDef, parameters);
		
		SequenceFeature seqFeature=null;
		if (seqa.getDisplayId()!=null){
			seqFeature =sbol3ParentComp.createSequenceFeature(seqa.getDisplayId());
		}
		else{
			seqFeature =sbol3ParentComp.createSequenceFeature();
		}
		parameters.addMapping(seqa.getIdentity(), seqFeature.getUri());

		seqFeature.setRoles(Util.convertSORoles2_to_3(seqa.getRoles()));

	/* 
		if (sbol3Sequence == null) {
			sbol3Sequence= Util.getEmptySequence(sbol3ParentComp, document);
		}

		// Iterate over all locations defined in the SBOL2 SequenceAnnotation.
		for (org.sbolstandard.core2.Location sbol2Loc : seqa.getLocations()) {
			Location sbol3Location = null;
			// Convert the SBOL2 orientation to SBOL3 orientation vocabulary.
			// org.sbolstandard.core2.OrientationType sbol2Orientation =
			// sbol2Loc.getOrientation();
			org.sbolstandard.core3.vocabulary.Orientation sbol3Orientation = Util.toSBOL3Orientation(sbol2Loc.getOrientation());

			// Handle Range location type (start and end positions).
			if (sbol2Loc instanceof org.sbolstandard.core2.Range) {
				org.sbolstandard.core2.Range sbol2range = (org.sbolstandard.core2.Range) sbol2Loc;
				if (sbol2Loc.getDisplayId()!=null) {
					sbol3Location = seqFeature.createRange(sbol2Loc.getDisplayId(), sbol2range.getStart(), sbol2range.getEnd(), sbol3Sequence);								
				}
				else{
					sbol3Location = seqFeature.createRange(sbol2range.getStart(), sbol2range.getEnd(), sbol3Sequence);													
				}
			}
			// Handle Cut location type (single cut site).
			else if (sbol2Loc instanceof org.sbolstandard.core2.Cut) {
				org.sbolstandard.core2.Cut sbol2cut = (org.sbolstandard.core2.Cut) sbol2Loc;
				if (sbol2Loc.getDisplayId()!=null) {					
					sbol3Location = seqFeature.createCut(sbol2Loc.getDisplayId(), sbol2cut.getAt(), sbol3Sequence);
				}
				else{
					sbol3Location = seqFeature.createCut(sbol2cut.getAt(), sbol3Sequence);
				}
			}
			else if (sbol2Loc instanceof org.sbolstandard.core2.GenericLocation) {
				if (sbol2Loc.getDisplayId()!=null) {										
					sbol3Location = seqFeature.createEntireSequence(sbol2Loc.getDisplayId(),  sbol3Sequence);	
				}
				else{
					sbol3Location = seqFeature.createEntireSequence(sbol3Sequence);
				}									
			}
			// Set the orientation for the SequenceFeature after each location is processed.
			if (sbol3Location != null) {
				sbol3Location.setOrientation(sbol3Orientation);
				Util.copyIdentified(sbol2Loc, sbol3Location);

			}
		}

*/
		// Copy common identified properties (displayId, version, etc.) from the SBOL2
		// SequenceAnnotation to the SBOL3 SequenceFeature.
		Util.copyIdentified(seqa, seqFeature, parameters);

		// Return the created SBOL3 SequenceFeature object.
		return seqFeature;
	}

	

}
