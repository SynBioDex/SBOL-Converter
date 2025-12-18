package org.sbolstandard.converter.sbol23_31.location;

import java.net.URI;

import org.sbolstandard.converter.ConverterVocabulary;
import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.Parameters;
import org.sbolstandard.core2.Range;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.FeatureWithLocation;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public abstract class TMPLocationConverter {

    org.sbolstandard.core3.entity.Sequence defaultSbol3Seq;
    FeatureWithLocation ownerFeature;
    SBOLDocument sbol3Doc;
    Component sbol3ParentComp;
    Parameters parameters;
    org.sbolstandard.core2.SequenceAnnotation sbol2SeqAnno;


    public TMPLocationConverter(SBOLDocument document, Component parentComponent, org.sbolstandard.core2.ComponentDefinition sbol2ParentCompDef, org.sbolstandard.core2.SequenceAnnotation seqAnno,
            Parameters params) throws SBOLGraphException {
        // Prepare for location conversion
		// Resolve the "default" SBOL3 Sequence for this parent component definition.
		// If missing, we'll later fall back to creating/using an empty sequence.
        this.sbol3Doc = document;
        this.sbol3ParentComp = parentComponent;
        this.parameters = params;
        this.sbol2SeqAnno = seqAnno;

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
		ownerFeature = null;

		if (sbol2SeqAnno.isSetComponent()) {
			// SBOL2 annotation references a Component => SBOL3 SubComponent.
			URI sbol3SubCompURI = parameters.getMapping(sbol2SeqAnno.getComponent().getIdentity());
            if (sbol3SubCompURI == null) {
                    throw new SBOLGraphException("Missing SBOL2->SBOL3 mapping for " + sbol2SeqAnno.getIdentity());
            }
			ownerFeature = sbol3Doc.getIdentified(sbol3SubCompURI, SubComponent.class);
		} else {
			// Otherwise => SBOL3 SequenceFeature.
			URI sbol3SeqFeatURI = parameters.getMapping(sbol2SeqAnno.getIdentity());
            if (sbol3SeqFeatURI == null) {
                    throw new SBOLGraphException("Missing SBOL2->SBOL3 mapping for " + sbol2SeqAnno.getIdentity());
            }
			ownerFeature = sbol3Doc.getIdentified(sbol3SeqFeatURI, SequenceFeature.class);
		}

    }

    public Location convert(org.sbolstandard.core2.Location sbol2Loc) throws SBOLGraphException {
        //ComponentDefinitionConverter cdConverter = new ComponentDefinitionConverter();

            boolean sbol2LocHadNullSequence = false;
            org.sbolstandard.core3.entity.Sequence locSeq = null;

            // 1) Try to use the explicit sequence URI on the SBOL2 Location (if present).
            if (sbol2Loc.getSequenceURI() != null) {
                URI sbol3LocSeqURI = Util.createSBOL3Uri(sbol2Loc.getSequenceURI(), parameters);
                locSeq = sbol3Doc.getIdentified(sbol3LocSeqURI, org.sbolstandard.core3.entity.Sequence.class);
            } else {
                // If SBOL2 Location did not specify a sequence, record that fact.
                sbol2LocHadNullSequence = true;
            }

            // 2) Fallback to the parent's default sequence if mapping was missing.
            if (locSeq == null) {
                locSeq = defaultSbol3Seq;
            }

            // 3) If still null, attempt to resolve a child sequence from the referenced component definition.
            // (This is a defensive fallback for incomplete SBOL2 inputs.)
            // if (locSeq == null) {
            //     org.sbolstandard.core2.Component sbol2Comp = sbol2Anno.getComponent();
            //     org.sbolstandard.core2.ComponentDefinition sbol2ChildDef = sbol2Comp.getDefinition();
            //     org.sbolstandard.core2.Sequence sbol2ChildSeq = sbol2ChildDef.getSequences().iterator().next();

            //     if (sbol2ChildSeq == null) {
            //         locSeq = Util.getEmptySequence(sbol3ParentComp, sbol3Doc);
            //     } else {
            //         URI sbol3ChildSeqURI = Util.createSBOL3Uri(sbol2ChildSeq.getIdentity(), parameters);
            //         locSeq = sbol3Doc.getIdentified(sbol3ChildSeqURI, org.sbolstandard.core3.entity.Sequence.class);
            //     }
            // }
            // OLD USAGE IS COMMENTED OUT ABOVE AND REPLACED WITH THE FOLLOWING:
            // 3) Defensive fallback: resolve a child sequence from the referenced component definition (if any).
            if (locSeq == null && sbol2SeqAnno != null && sbol2SeqAnno.isSetComponent()) {
                org.sbolstandard.core2.Component sbol2Comp = sbol2SeqAnno.getComponent();
                if (sbol2Comp != null && sbol2Comp.getDefinition() != null) {
                    org.sbolstandard.core2.ComponentDefinition sbol2ChildDef = sbol2Comp.getDefinition();
                    if (sbol2ChildDef.getSequences() != null && !sbol2ChildDef.getSequences().isEmpty()) {
                        org.sbolstandard.core2.Sequence sbol2ChildSeq = sbol2ChildDef.getSequences().iterator().next();
                        URI sbol3ChildSeqURI = Util.createSBOL3Uri(sbol2ChildSeq.getIdentity(), parameters);
                        locSeq = sbol3Doc.getIdentified(sbol3ChildSeqURI, org.sbolstandard.core3.entity.Sequence.class);
                    }
                }
            }
            if (locSeq == null) {
                locSeq = Util.getEmptySequence(sbol3ParentComp, sbol3Doc);
            }


            // Convert orientation SBOL2 -> SBOL3.
            org.sbolstandard.core3.vocabulary.Orientation sbol3Ori =
                    Util.toSBOL3Orientation(sbol2Loc.getOrientation());

            // Create the correct SBOL3 Location based on the SBOL2 Location subtype.
            Location sbol3Loc = null;

            sbol3Loc = createLocations(sbol2Loc, sbol3Loc, locSeq);

            // Apply orientation and preserve metadata.
            if (sbol3Loc != null) {
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


}
