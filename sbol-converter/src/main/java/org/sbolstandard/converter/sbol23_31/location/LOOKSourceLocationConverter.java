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
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class LOOKSourceLocationConverter extends AbstractLocationConverter {

    public LOOKSourceLocationConverter(SBOLDocument document, Component parentComponent, org.sbolstandard.core2.ComponentDefinition sbol2ParentCompDef, org.sbolstandard.core2.Identified identifiedWithLocation,
            Parameters params) throws SBOLGraphException {
         super(document, parentComponent, sbol2ParentCompDef, identifiedWithLocation, params);
    }

    @Override
    public Location createLocations(org.sbolstandard.core2.Location sbol2Loc, Location sbol3Loc, org.sbolstandard.core3.entity.Sequence locSeq) throws SBOLGraphException
    {
            SubComponent ownerSubComponent = (SubComponent) ownerFeature;
            if (sbol2Loc instanceof org.sbolstandard.core2.Range) {
                org.sbolstandard.core2.Range r = (org.sbolstandard.core2.Range) sbol2Loc;
                if (sbol2Loc.getDisplayId() != null) {
                    sbol3Loc = ownerSubComponent.createSourceRange(sbol2Loc.getDisplayId(), r.getStart(), r.getEnd(), locSeq);
                } else {
                    sbol3Loc = ownerSubComponent.createSourceRange(r.getStart(), r.getEnd(), locSeq);
                }
            } else if (sbol2Loc instanceof org.sbolstandard.core2.Cut) {
                org.sbolstandard.core2.Cut c = (org.sbolstandard.core2.Cut) sbol2Loc;
                if (sbol2Loc.getDisplayId() != null) {
                    sbol3Loc = ownerSubComponent.createSourceCut(sbol2Loc.getDisplayId(), c.getAt(), locSeq);
                } else {
                    sbol3Loc = ownerSubComponent.createSourceCut(c.getAt(), locSeq);
                }
            } else if (sbol2Loc instanceof org.sbolstandard.core2.GenericLocation) {
                if (sbol2Loc.getDisplayId() != null) {
                    sbol3Loc = ownerSubComponent.createSourceEntireSequence(sbol2Loc.getDisplayId(), locSeq);
                } else {
                    sbol3Loc = ownerSubComponent.createEntireSequence(locSeq);
                }
            }
            return sbol3Loc;
    }

    @Override
    public FeatureWithLocation getFeatureEntity(Identified identifiedWithLocation) throws SBOLGraphException {
    		//SequenceAnnotation sbol2SeqAnno = (SequenceAnnotation) identified;
        org.sbolstandard.core2.Component  sbol2Component = (org.sbolstandard.core2.Component) identifiedWithLocation; 
        FeatureWithLocation ownerFeature = null;
        // SBOL2 annotation references a Component => SBOL3 SubComponent.
        URI sbol3SubCompURI = parameters.getMapping(sbol2Component.getIdentity());
        if (sbol3SubCompURI == null) {
                throw new SBOLGraphException("Missing SBOL2->SBOL3 mapping for " + sbol2Component.getIdentity());
        }
        ownerFeature = sbol3Doc.getIdentified(sbol3SubCompURI, SubComponent.class);
        return ownerFeature;
    }

    @Override
    public org.sbolstandard.core3.entity.Sequence getSBOL3SequenceFromChild(Identified identifiedWithLocation) throws SBOLGraphException {
        org.sbolstandard.core3.entity.Sequence sbol3LocationSequence = null;
        org.sbolstandard.core2.Component sbol2Component = (org.sbolstandard.core2.Component) identifiedWithLocation;
        if (sbol2Component != null) {
                if (sbol2Component.getDefinition() != null) {
                    org.sbolstandard.core2.ComponentDefinition sbol2ChildDef = sbol2Component.getDefinition();
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
        
    //     if (localSbol3LocationSequence == null) {
    //         localSbol3LocationSequence = Util.getEmptySequence(sbol3ParentComp, sbol3Doc);
    //         if (localSbol3LocationSequence == null) {
    //             localSbol3LocationSequence = getSBOL3Sequence(identifiedWithLocation);
    //         }
    //     }
    //     return localSbol3LocationSequence;
    // }


}
