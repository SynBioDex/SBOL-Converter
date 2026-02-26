package org.sbolstandard.converter.sbol23_31.location;

import java.net.URI;

import org.sbolstandard.converter.ConverterVocabulary;
import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.Parameters;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.FeatureWithLocation;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.util.SBOLGraphException;

public abstract class AbstractLocationConverter {

    org.sbolstandard.core3.entity.Sequence defaultSbol3Seq;
    FeatureWithLocation ownerFeature;
    SBOLDocument sbol3Doc;
    Component sbol3ParentComp;
    Parameters parameters;
    private Identified identifiedWithLocation;


    public AbstractLocationConverter(SBOLDocument document, Component parentComponent, org.sbolstandard.core2.ComponentDefinition sbol2ParentCompDef, Identified identifiedWithLocation,
            Parameters params) throws SBOLGraphException {
        // Prepare for location conversion
		// Resolve the "default" SBOL3 Sequence for this parent component definition.
		// If missing, we'll later fall back to creating/using an empty sequence.
        this.sbol3Doc = document;
        this.sbol3ParentComp = parentComponent;
        this.parameters = params;
        
		this.defaultSbol3Seq = Util.getSBOL3SequenceFromSBOl2Parent(sbol3Doc, sbol2ParentCompDef, parameters);

		// Resolve SBOL3 parent Component (converted from SBOL2 ComponentDefinition).
		//URI sbol3ComponentURI = Util.createSBOL3Uri(sbol2Cd);
		//Component sbol3ParentComp = sbol3Doc.getIdentified(sbol3ComponentURI, Component.class);

		if (defaultSbol3Seq == null) {
			// Ensure we always have a sequence object to attach locations to.
			defaultSbol3Seq = Util.getEmptySequence(sbol3ParentComp, sbol3Doc);
		}

		// The SBOL3 object that will actually own the locations.
		// (Both SubComponent and SequenceFeature implement FeatureWithLocation.)
		this.identifiedWithLocation = identifiedWithLocation;
        ownerFeature = getFeatureEntity(identifiedWithLocation);
        

    }

    public Location convert(org.sbolstandard.core2.Location sbol2Loc) throws SBOLGraphException {
        //ComponentDefinitionConverter cdConverter = new ComponentDefinitionConverter();

        boolean sbol2LocHadNullSequence = false;
        Sequence sbol3LocationSequence = null;

        // 1) Try to use the explicit sequence URI on the SBOL2 Location (if present).
        if (sbol2Loc.getSequenceURI() != null) {
            URI sbol3LocSeqURI = Util.createSBOL3Uri(sbol2Loc.getSequenceURI(), parameters);
            sbol3LocationSequence = sbol3Doc.getIdentified(sbol3LocSeqURI, org.sbolstandard.core3.entity.Sequence.class);
        } else {
            // If SBOL2 Location did not specify a sequence, record that fact.
            sbol2LocHadNullSequence = true;
        }
        // 2) Fallback to the parent's default sequence if mapping was missing.
        if (sbol3LocationSequence == null) {                
            //sbol3LocationSequence = fallbackSbol3LocationSequence(sbol2Loc, identifiedWithLocation);
            sbol3LocationSequence = defaultSbol3Seq;
            
        }
        // 3) If still null, attempt to resolve a child sequence from the referenced component definition.
        // (This is a defensive fallback for incomplete SBOL2 inputs.)
        // Defensive fallback: resolve a child sequence from the referenced component definition (if any).
        /*if (sbol3LocationSequence == null) {
            sbol3LocationSequence = getSBOL3SequenceFromChild(identifiedWithLocation);
            if (sbol3LocationSequence == null) {
                sbol3LocationSequence = Util.getEmptySequence(sbol3ParentComp, sbol3Doc);
            }
        }*/
        Util.getLogger().debug("SourceLocationConverter: using sequence " + sbol3LocationSequence.getDisplayId() + " for location " + sbol2Loc.getDisplayId());
        // Convert orientation SBOL2 -> SBOL3.
        org.sbolstandard.core3.vocabulary.Orientation sbol3Ori = Util.toSBOL3Orientation(sbol2Loc.getOrientation());

        // Create the correct SBOL3 Location based on the SBOL2 Location subtype.
        Location sbol3Loc = null;

        sbol3Loc = createLocations(sbol2Loc, sbol3Loc, sbol3LocationSequence);
        
        // Apply orientation and preserve metadata.
        if (sbol3Loc != null) {
            Util.getLogger().debug("SourceLocationConverter: converted location " + sbol2Loc.getIdentity() + " to " + sbol3Loc.getDisplayId());
            sbol3Loc.setOrientation(sbol3Ori);

            // Keep an annotation to indicate SBOL2 had null sequenceURI (useful for debugging round-trips).
            if (sbol2LocHadNullSequence) {
                sbol3Loc.addAnnotation(ConverterVocabulary.Two_to_Three.sbol2LocationSequenceNull, true);
            }

            // Copy common Identified metadata (name, description, annotations, etc.)
            Util.copyIdentified(sbol2Loc, sbol3Loc, parameters);
        }
        return sbol3Loc;
    }

    public abstract Location createLocations(org.sbolstandard.core2.Location sbol2Loc, Location sbol3Loc, org.sbolstandard.core3.entity.Sequence locSeq) throws SBOLGraphException;
    public abstract FeatureWithLocation getFeatureEntity(Identified identified) throws SBOLGraphException;
    public abstract org.sbolstandard.core3.entity.Sequence getSBOL3SequenceFromChild(Identified identifiedWithLocation) throws SBOLGraphException;;
    //public abstract Sequence fallbackSbol3LocationSequence(org.sbolstandard.core2.Location sbol2Loc, Identified identifiedWithLocation) throws SBOLGraphException;


}
