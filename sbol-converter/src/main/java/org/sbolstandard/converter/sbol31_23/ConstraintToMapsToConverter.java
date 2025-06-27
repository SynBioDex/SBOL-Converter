package org.sbolstandard.converter.sbol31_23;


import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.MapsTo;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.ComponentReference;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.util.SBOLGraphException;


public class ConstraintToMapsToConverter implements ChildEntityConverter<Constraint, org.sbolstandard.core2.MapsTo> {

    @Override
    public MapsTo convert(SBOLDocument document, Identified sbol2Parent,
            org.sbolstandard.core3.entity.Identified sbol3Parent, Constraint constraint)
            throws SBOLGraphException, SBOLValidationException {
        
                // TODO Auto-generated method stub
        
                
        
                throw new UnsupportedOperationException("Unimplemented method 'convert'");
    }



}
