package org.sbolstandard.converter.sbol31_23;


import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.MapsTo;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.ComponentReference;
import org.sbolstandard.core3.util.SBOLGraphException;


public class ComponentReferenceToMapsToConverter implements ChildEntityConverter<ComponentReference, org.sbolstandard.core2.MapsTo> {

    @Override
    public MapsTo convert(SBOLDocument document, Identified sbol2parent,
            org.sbolstandard.core3.entity.Identified sbol3Parent, ComponentReference componentReference)
            throws SBOLGraphException, SBOLValidationException {
        
                // TODO Auto-generated method stub
                
                
        
                throw new UnsupportedOperationException("Unimplemented method 'convert'");
    }



}
