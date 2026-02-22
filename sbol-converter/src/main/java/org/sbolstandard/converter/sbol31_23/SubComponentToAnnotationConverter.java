package org.sbolstandard.converter.sbol31_23;

import java.net.URI;
import java.util.List;
import org.sbolstandard.converter.ConverterVocabulary;
import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.OrientationType;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.Metadata;
import org.sbolstandard.core3.entity.Range;
import org.sbolstandard.core3.entity.Cut;
import org.sbolstandard.core3.entity.EntireSequence;
import org.sbolstandard.core3.entity.FeatureWithLocation;
import org.sbolstandard.core3.entity.Identified;
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
		sbol2SeqAnno = handleLocations(sbol3SubComponent, sbol2SeqAnno, parentCompDef, doc);	

		// If no valid locations were found, throw an exception
		if (sbol2SeqAnno == null) {
			throw new SBOLGraphException( "No valid locations found in SubComponent: " + sbol3SubComponent.getUri());
		}
		else{
			sbol2SeqAnno.setComponent(sbol3SubComponent.getDisplayId());
		}

		// If the resulting SequenceAnnotation has no locations, this is invalid
		if (sbol2SeqAnno.getLocations().isEmpty()) {
			throw new SBOLGraphException("The resulting SBOL2 SequenceAnnotation must have at least one location." + sbol2SeqAnno.getIdentity());
		}

		// Copy general Identified properties (like displayId, version, etc.)
		Util.copyIdentified(sbol3SubComponent, sbol2SeqAnno, doc);

		return sbol2SeqAnno;
	}

	public static String getSequenceAnnotationId(Identified sbol3SubComponent, Location location) throws SBOLGraphException {
		// Generate a unique ID for the SequenceAnnotation based on the SubComponent
		return sbol3SubComponent.getDisplayId() + "_" + location.getDisplayId();	
	}

	public static String extractSBOL2AnnotationId(Identified sbol3SubComponent) throws SBOLGraphException{
		String sbol2AnnotationId= null;				
		List<Object> backPorts=sbol3SubComponent.getAnnotation(ConverterVocabulary.Two_to_Three.sbol2SequenceAnnotationURI);
		if (backPorts!=null && backPorts.size()>0){
			URI sbol2AnnotationURI= (URI) backPorts.get(0);
			sbol2AnnotationId= Util.extractDisplayIdSBOL2Uri(sbol2AnnotationURI.toString());	
		}
		return sbol2AnnotationId;
	}

	

	public static SequenceAnnotation handleLocations(FeatureWithLocation sbol3SubComponent,
			SequenceAnnotation sbol2SeqAnno, ComponentDefinition parentCompDef, SBOLDocument doc)
			throws SBOLGraphException, SBOLValidationException {
		{
			String sbol2AnnotationDisplayId = extractSBOL2AnnotationId(sbol3SubComponent);
			// Iterate over each Location
			if (sbol3SubComponent.getLocations() != null) {
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
							// If this is the first location, create a new SequenceAnnotation with this
							// Range
							if (sbol2AnnotationDisplayId == null) {
								sbol2AnnotationDisplayId = getSequenceAnnotationId(sbol3SubComponent, loc);
							}
							sbol2SeqAnno = parentCompDef.createSequenceAnnotation(sbol2AnnotationDisplayId,
									loc.getDisplayId(), range.getStart().get(), range.getEnd().get(), orientationType);
							newLoc = sbol2SeqAnno.getLocation(loc.getDisplayId());
						} else {
							// If SequenceAnnotation exists, add an additional Range to it
							newLoc = sbol2SeqAnno.addRange(loc.getDisplayId(), range.getStart().get(),
									range.getEnd().get(), orientationType);
						}
					}
					// Handle Cut location (single position)
					else if (loc instanceof Cut) {
						Cut cut = (Cut) loc;
						if (sbol2SeqAnno == null) {
							// Create a new SequenceAnnotation with this Cut
							if (sbol2AnnotationDisplayId == null) {
								sbol2AnnotationDisplayId = getSequenceAnnotationId(sbol3SubComponent, loc);
							}
							sbol2SeqAnno = parentCompDef.createSequenceAnnotation(sbol2AnnotationDisplayId,
									loc.getDisplayId(), cut.getAt().get(), orientationType);
							newLoc = sbol2SeqAnno.getLocation(loc.getDisplayId());
						} else {
							// Add an additional Cut to existing SequenceAnnotation
							newLoc = sbol2SeqAnno.addCut(loc.getDisplayId(), cut.getAt().get(), orientationType);
						}
					}
					// Handle EntireSequence location (refers to the whole sequence)
					else if (loc instanceof EntireSequence) {
						if (sbol2SeqAnno == null) { // Create SequenceAnnotation referring to entire sequence
							if (sbol2AnnotationDisplayId == null) {
								sbol2AnnotationDisplayId = getSequenceAnnotationId(sbol3SubComponent, loc);
							}
							sbol2SeqAnno = parentCompDef.createSequenceAnnotation(sbol2AnnotationDisplayId,
									loc.getDisplayId(), orientationType);
							newLoc = sbol2SeqAnno.getLocation(loc.getDisplayId());
						} else { // Add a generic location for entire sequence
							newLoc = sbol2SeqAnno.addGenericLocation(loc.getDisplayId(), orientationType);
						}
					}

					if (sbol2SeqAnno != null) {
						// Set the location's sequence if the SBOL2 location's sequence is not marked as
						// null in the SBOL3 location
						// During conversion from 2 to 3, empty sequences are created if there is no
						// sequence. If the parent sbol2.CompDef has a sequence, then it is used.
						// However, the sbol2.Location may not have any sequence attached to it
						// originally!
						boolean sbol2LocationSequenceNull = false;
						List<Object> locNullAnnotations = loc
								.getAnnotation(ConverterVocabulary.Two_to_Three.sbol2LocationSequenceNull);
						if (locNullAnnotations != null && locNullAnnotations.size() > 0) {
							sbol2LocationSequenceNull = (boolean) locNullAnnotations.get(0);
						}
						if (!sbol2LocationSequenceNull) {
							URI sbol2SequenceURI = Util.createSBOL2Uri(loc.getSequenceURI());
							newLoc.setSequence(sbol2SequenceURI);
						}

						// Copy location metadata and properties from SBOL3 Location to SBOL2 Location
						Util.copyIdentified(loc, newLoc, doc);
					}
				}
			} else {
				// During 2-to-3 conversion, if there is no location, we create a generic
				// location as metadata. Here, we check for such metadata and create generic
				// locations accordingly.
				
				List<Metadata> metadataEntities = sbol3SubComponent.getMetadataEntites();
				if (metadataEntities != null && metadataEntities.size() > 0) {
					for (Metadata metadata : metadataEntities) {
						if (metadata.getType().contains(URI.create("http://sbols.org/v2#GenericLocation"))) {
							String locationId = metadata.getDisplayId();
							//LocalSubComponent, otherwise the annotation is is taken from the metadata earlier
							if (sbol2AnnotationDisplayId == null) {								
								sbol2AnnotationDisplayId = sbol3SubComponent.getDisplayId();
							}
							OrientationType sbol2Orientation = null;
							String value = Util.extractSBOL2AnnotationURIValue(metadata, URI.create("http://sbols.org/v2#orientation"));
							if (value != null) {
								sbol2Orientation = Util.getSBOL2OrientationType(value);
							}

							if (sbol2SeqAnno == null) {
								sbol2SeqAnno = parentCompDef.createSequenceAnnotation(sbol2AnnotationDisplayId, locationId,  sbol2Orientation);
							} 
							else {
								sbol2SeqAnno.addGenericLocation(locationId, sbol2Orientation);
							}
						}
					}
				}

				// If there are no generic location metadata, but the SubComponent has an
				// orientation, we create a generic location with the orientation annotation.
				// Otherwise, there is no location to store the orientation in SBOL2.
				if (sbol2SeqAnno == null && sbol3SubComponent.getOrientation() != null) {
					// TODO: GMGM Add sbol3-2 annotation
					String locationId = "generic";
					sbol2SeqAnno = parentCompDef.createSequenceAnnotation(sbol2AnnotationDisplayId, locationId,
							Util.toSBOL2OrientationType(sbol3SubComponent.getOrientation()));
				}
			}

			return sbol2SeqAnno;
		}
	}

	public static boolean isSBOL2GenericLocation(FeatureWithLocation feature) throws SBOLGraphException
	{
		List<Metadata> metadataEntities = feature.getMetadataEntites();
				if (metadataEntities != null && metadataEntities.size() > 0) {
					for (Metadata metadata : metadataEntities) {
						if (metadata.getType().contains(URI.create("http://sbols.org/v2#GenericLocation"))) {							
							return true;
						}
					}
				}
				return false;
	}
}