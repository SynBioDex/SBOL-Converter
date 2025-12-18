package org.sbolstandard.converter.sbol23_31.location;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.Parameters;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.FeatureWithLocation;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class DefaultLocationConverter extends AbstractLocationConverter {

    public DefaultLocationConverter(SBOLDocument document, Component parentComponent, org.sbolstandard.core2.ComponentDefinition sbol2ParentCompDef, org.sbolstandard.core2.SequenceAnnotation seqAnno,
            Parameters params) throws SBOLGraphException {
        super(document, parentComponent, sbol2ParentCompDef, seqAnno, params);

    }

    @Override
    public Location createLocations(org.sbolstandard.core2.Location sbol2Loc, Location sbol3Loc, org.sbolstandard.core3.entity.Sequence locSeq) throws SBOLGraphException
    {
            if (sbol2Loc instanceof org.sbolstandard.core2.Range) {
                org.sbolstandard.core2.Range r = (org.sbolstandard.core2.Range) sbol2Loc;
                if (sbol2Loc.getDisplayId() != null) {
                    sbol3Loc = ownerFeature.createRange(sbol2Loc.getDisplayId(), r.getStart(), r.getEnd(), locSeq);
                } else {
                    sbol3Loc = ownerFeature.createRange(r.getStart(), r.getEnd(), locSeq);
                }
            } else if (sbol2Loc instanceof org.sbolstandard.core2.Cut) {
                org.sbolstandard.core2.Cut c = (org.sbolstandard.core2.Cut) sbol2Loc;
                if (sbol2Loc.getDisplayId() != null) {
                    sbol3Loc = ownerFeature.createCut(sbol2Loc.getDisplayId(), c.getAt(), locSeq);
                } else {
                    sbol3Loc = ownerFeature.createCut(c.getAt(), locSeq);
                }
            } else if (sbol2Loc instanceof org.sbolstandard.core2.GenericLocation) {
                if (sbol2Loc.getDisplayId() != null) {
                    sbol3Loc = ownerFeature.createEntireSequence(sbol2Loc.getDisplayId(), locSeq);
                } else {
                    sbol3Loc = ownerFeature.createEntireSequence(locSeq);
                }
            }
            return sbol3Loc;
    }

    @Override
    public FeatureWithLocation getFeatureEntity(Identified identified) throws SBOLGraphException {
    		SequenceAnnotation sbol2SeqAnno = (SequenceAnnotation) identified;
        FeatureWithLocation ownerFeature = null;
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
        return ownerFeature;
    }

    @Override
    public org.sbolstandard.core3.entity.Sequence getSBOL3SequenceFromChild(Identified identifiedWithLocation) throws SBOLGraphException {
        org.sbolstandard.core3.entity.Sequence sbol3LocationSequence = null;
        SequenceAnnotation sbol2SeqAnno = (SequenceAnnotation) identifiedWithLocation;
        if (sbol2SeqAnno != null && sbol2SeqAnno.isSetComponent()) {
                org.sbolstandard.core2.Component sbol2Comp = sbol2SeqAnno.getComponent();
                if (sbol2Comp != null && sbol2Comp.getDefinition() != null) {
                    org.sbolstandard.core2.ComponentDefinition sbol2ChildDef = sbol2Comp.getDefinition();
                    if (sbol2ChildDef.getSequences() != null && !sbol2ChildDef.getSequences().isEmpty()) {
                        org.sbolstandard.core2.Sequence sbol2ChildSeq = sbol2ChildDef.getSequences().iterator().next();
                        URI sbol3ChildSeqURI = Util.createSBOL3Uri(sbol2ChildSeq.getIdentity(), parameters);
                        sbol3LocationSequence = sbol3Doc.getIdentified(sbol3ChildSeqURI, org.sbolstandard.core3.entity.Sequence.class);
                    }
                }
            }
        return sbol3LocationSequence;
    }

    // @Override
    // public Sequence fallbackSbol3LocationSequence(org.sbolstandard.core2.Location sbol2Loc, Identified identifiedWithLocation) throws SBOLGraphException {
    //     Sequence localSbol3LocationSequence = null; 
    //     localSbol3LocationSequence = defaultSbol3Seq;
    //     // 3) If still null, attempt to resolve a child sequence from the referenced component definition.
    //     // (This is a defensive fallback for incomplete SBOL2 inputs.)
    //     // Defensive fallback: resolve a child sequence from the referenced component definition (if any).
    //     if (localSbol3LocationSequence == null) {
    //         localSbol3LocationSequence = getSBOL3Sequence(identifiedWithLocation);
    //         if (localSbol3LocationSequence == null) {
    //             localSbol3LocationSequence = Util.getEmptySequence(sbol3ParentComp, sbol3Doc);
    //         }
    //     }
    //     return localSbol3LocationSequence;

    // }
}
