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
import org.sbolstandard.core3.entity.Identified;
//import org.sbolstandard.core3.entity.Sequence;
//import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.Orientation;

public class SubComponentToAnnotationConverter implements ChildEntityConverter<SubComponent, SequenceAnnotation> {

	@Override
	public SequenceAnnotation convert(SBOLDocument doc, org.sbolstandard.core2.Identified sbol2Parent, Identified inputParent,
			SubComponent sbol3SubComponent) throws SBOLGraphException, SBOLValidationException {

		// SBOL3 SequenceFeature will be mapped to SBOL2 SequenceAnnotation
		ComponentDefinition parentCompDef = (ComponentDefinition) sbol2Parent;



		// This will hold the SBOL2 SequenceAnnotation that we are constructing
		SequenceAnnotation sbol2SeqAnno = null;

		// Iterate over each Location
		if (sbol3SubComponent.getLocations()!=null)
		{
			for (Location loc : sbol3SubComponent.getLocations()) {

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
					if (sbol2SeqAnno == null) {
						// If this is the first location, create a new SequenceAnnotation with this Range
						sbol2SeqAnno = parentCompDef.createSequenceAnnotation(getSequenceAnnotationId(sbol3SubComponent, loc), loc.getDisplayId(), range.getStart().get(), range.getEnd().get(), orientationType);
						newLoc = sbol2SeqAnno.getLocation(loc.getDisplayId());
					} else {
						// If SequenceAnnotation exists, add an additional Range to it
						newLoc = sbol2SeqAnno.addRange(loc.getDisplayId(), range.getStart().get(),range.getEnd().get(), orientationType);
					}
				}
				// Handle Cut location (single position)
				else if (loc instanceof Cut) {
					Cut cut = (Cut) loc;
					if (sbol2SeqAnno == null) {
						// Create a new SequenceAnnotation with this Cut
						sbol2SeqAnno = parentCompDef.createSequenceAnnotation(getSequenceAnnotationId(sbol3SubComponent, loc), loc.getDisplayId(), cut.getAt().get(), orientationType);
						newLoc = sbol2SeqAnno.getLocation(loc.getDisplayId());
					} else {
						// Add an additional Cut to existing SequenceAnnotation
						newLoc = sbol2SeqAnno.addCut(loc.getDisplayId(), cut.getAt().get(), orientationType);
					}
				}
				// Handle EntireSequence location (refers to the whole sequence)
				else if (loc instanceof EntireSequence) {
					if (sbol2SeqAnno == null) {
						// Create SequenceAnnotation referring to entire sequence
						sbol2SeqAnno = parentCompDef.createSequenceAnnotation(getSequenceAnnotationId(sbol3SubComponent, loc), loc.getDisplayId(), orientationType);
						newLoc = sbol2SeqAnno.getLocation(loc.getDisplayId());
					} else {
						// Add a generic location for entire sequence
						newLoc = sbol2SeqAnno.addGenericLocation(loc.getDisplayId(), orientationType);
					}
				}

				if (sbol2SeqAnno != null) {
					// Copy location metadata and properties from SBOL3 Location to SBOL2 Location
					Util.copyIdentified(loc, newLoc, doc);
				}
			}
		}
		

		// If no valid locations were found, throw an exception
		if (sbol2SeqAnno == null) {
			throw new SBOLGraphException(
					"No valid locations found in SequenceFeature: " + sbol3SubComponent.getDisplayId());
		}

		// If the resulting SequenceAnnotation has no locations, this is invalid
		if (sbol2SeqAnno.getLocations().isEmpty()) {
			throw new SBOLGraphException("SequenceAnnotation must have at least one location.");
		}

		// Copy general Identified properties (like displayId, version, etc.)
		Util.copyIdentified(sbol3SubComponent, sbol2SeqAnno, doc);

		return sbol2SeqAnno;
	}

	private String getSequenceAnnotationId(SubComponent sbol3SubComponent, Location location) throws SBOLGraphException {
		// Generate a unique ID for the SequenceAnnotation based on the SubComponent
		return sbol3SubComponent.getDisplayId() + "_" + location.getDisplayId();
	
	}
}
