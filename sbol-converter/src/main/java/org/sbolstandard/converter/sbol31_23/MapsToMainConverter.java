package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.util.SBOLGraphException;

public class MapsToMainConverter {

    public static void convertForComponentDefinition(SBOLDocument sbol2Doc, Component sbol3Component)
            throws SBOLGraphException, SBOLValidationException {

                
            
                
    }
    
    public static void convertForModuleDefinition(SBOLDocument sbol2Doc, Component sbol3Component)
            throws SBOLGraphException, SBOLValidationException {
                ComponentReferenceToMapsToConverter compRefToMapsToConverter = new ComponentReferenceToMapsToConverter();
		        ConstraintToMapsToConverter constraintToMapsToConverter = new ConstraintToMapsToConverter();

                // TODO: How to find SBOL2 parent? 
                
                for (org.sbolstandard.core3.entity.ComponentReference componentReference : sbol3Component.getComponentReferences()) {
					// TODO: implement this
					// CHECK 
					compRefToMapsToConverter.convert(sbol2Doc, null, sbol3Component, componentReference);
				}
				for (org.sbolstandard.core3.entity.Constraint constraint : sbol3Component.getConstraints()) {
					if (Util.isMapstoConstraint(constraint)) {
						//TODO: implement this
						// CHECK
						constraintToMapsToConverter.convert(sbol2Doc, null, sbol3Component, constraint);

					}
				}
            
                
    }
    

}
