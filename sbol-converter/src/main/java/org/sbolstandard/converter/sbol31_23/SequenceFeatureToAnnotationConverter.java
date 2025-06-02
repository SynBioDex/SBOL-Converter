package org.sbolstandard.converter.sbol31_23;

//import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.OrientationType;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.Range;
import org.sbolstandard.core3.entity.Cut;
import org.sbolstandard.core3.entity.EntireSequence;
//import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.Orientation;


public class SequenceFeatureToAnnotationConverter implements ChildEntityConverter<SequenceFeature, SequenceAnnotation>{

    @Override
    public SequenceAnnotation convert(SBOLDocument doc, org.sbolstandard.core2.Identified parent, SequenceFeature seqf) throws SBOLGraphException, SBOLValidationException {    	
    	
    	// SBOL3 SequenceFeature will be mapped to SBOL2 SequenceAnnotation
    	ComponentDefinition parentCompDef = (ComponentDefinition) parent;
    	
    	// Get the SBOL3 Sequence from the first location in SequenceFeature
    	//Sequence sbol3seq = seqf.getLocations().get(0).getSequence();
    	
    	// Convert SBOL3 Sequence URI to SBOL2 URI (if needed)
    	//URI SBOL2ObjectUri = Util.createSBOL2Uri(sbol3seq.getUri());
    	
    	// Retrieve the corresponding SBOL2 Sequence from the document
    	//org.sbolstandard.core2.Sequence sbol2seq = doc.getSequence(SBOL2ObjectUri);
    	
    	// This will hold the SBOL2 SequenceAnnotation that we are constructing
    	SequenceAnnotation seqa = null;
    	
    	// Iterate over each Location in the SequenceFeature
    	for(Location loc : seqf.getLocations()) {
    		
    		// Get the orientation and convert to SBOL2 OrientationType
    		Orientation orientation = loc.getOrientation();
			OrientationType orientationType = null;
			if (orientation != null) {	
				orientationType = Util.toSBOL2OrientationType(orientation);
			} 
			
			org.sbolstandard.core2.Location newLoc = null;
			
			// Handle Range location (with start and end coordinates)
			if (loc instanceof Range) {
				Range range = (Range) loc;
				if (seqa == null) {
					// If this is the first location, create a new SequenceAnnotation with this Range
					seqa = parentCompDef.createSequenceAnnotation(
						seqf.getDisplayId(), 
						loc.getDisplayId(), 
						range.getStart().get(), 
						range.getEnd().get(), 
						orientationType
					);
					newLoc = seqa.getLocation(loc.getDisplayId());
				} else {
					// If SequenceAnnotation exists, add an additional Range to it
					newLoc = seqa.addRange(
						seqf.getDisplayId(),
						range.getStart().get(), 
						range.getEnd().get(), 
						orientationType
					);
				}
			}
			// Handle Cut location (single position)
			else if (loc instanceof Cut) {
				Cut cut = (Cut) loc;
				if (seqa == null) {
					// Create a new SequenceAnnotation with this Cut
					seqa = parentCompDef.createSequenceAnnotation(
						seqf.getDisplayId(), 
						loc.getDisplayId(), 
						cut.getAt().get(), 
						orientationType
					);
					newLoc = seqa.getLocation(loc.getDisplayId());
				} else {
					// Add an additional Cut to existing SequenceAnnotation
					newLoc = seqa.addCut(
						seqf.getDisplayId(),
						cut.getAt().get(), 
						orientationType
					);
				}
			}
			// Handle EntireSequence location (refers to the whole sequence)
			else if (loc instanceof EntireSequence) {
				if (seqa == null) {
					// Create SequenceAnnotation referring to entire sequence
					seqa = parentCompDef.createSequenceAnnotation(
						seqf.getDisplayId(), 
						loc.getDisplayId(), 
						orientationType
					);
					newLoc = seqa.getLocation(loc.getDisplayId());
				} else {
					// Add a generic location for entire sequence
					newLoc = seqa.addGenericLocation(
						seqf.getDisplayId(), 
						orientationType
					);
				}
			}
			
			if (seqa != null) {
				// Copy location metadata and properties from SBOL3 Location to SBOL2 Location
				Util.copyIdentified(loc, newLoc);
			}
		}
    	
    	// If no valid locations were found, throw an exception
		if (seqa == null) {
			throw new SBOLGraphException("No valid locations found in SequenceFeature: " + seqf.getDisplayId());
		}
		
		// If the resulting SequenceAnnotation has no locations, this is invalid
		if (seqa.getLocations().isEmpty()) {
			throw new SBOLGraphException("SequenceAnnotation must have at least one location.");		
		}
    	
    	// Copy general Identified properties (like displayId, version, etc.)
	    Util.copyIdentified(seqf, seqa);

		return seqa;
	}
}
