package org.sbolstandard.converter.sbol31_23;


import java.net.URI;

import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.MapsTo;
import org.sbolstandard.core2.RefinementType;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.util.SBOLGraphException;



public class ConstraintToRefinementTypeConverter implements ChildEntityConverter<Constraint, org.sbolstandard.core2.RefinementType> {

    @Override
    public RefinementType convert(SBOLDocument document, Identified sbol2Parent,
            org.sbolstandard.core3.entity.Identified sbol3Parent, Constraint constraint)
            throws SBOLGraphException, SBOLValidationException {
        
                URI uriRestrictionType = constraint.getRestriction();
                
                //Get subject and object
                org.sbolstandard.core3.entity.Identified subject = constraint.getSubject();
                org.sbolstandard.core3.entity.Identified object = constraint.getObject();


                RefinementType refinementType = RefinementType.USEREMOTE;
                if (uriRestrictionType.equals(org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.replaces.getUri())) {

                    // If subject is ComponentReference and object  is SubComponent then USEREMOTE
                    // If subject is SubComponent and object is ComponentReference then USELOCAL
                    if (subject instanceof org.sbolstandard.core3.entity.ComponentReference &&
                        object instanceof org.sbolstandard.core3.entity.SubComponent) {
                        refinementType = RefinementType.USEREMOTE;
                    } else if (subject instanceof org.sbolstandard.core3.entity.SubComponent &&
                               object instanceof org.sbolstandard.core3.entity.ComponentReference) {
                        refinementType = RefinementType.USELOCAL;
                    } 
                } else if (uriRestrictionType.equals(org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.verifyIdentical.getUri())) {
                    refinementType = RefinementType.VERIFYIDENTICAL;
                } else {
                    throw new SBOLGraphException("Unknown restriction type: " + uriRestrictionType);
                }

        
                return refinementType;
    }

    
    


}
