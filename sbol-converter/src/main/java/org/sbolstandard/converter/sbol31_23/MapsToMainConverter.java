package org.sbolstandard.converter.sbol31_23;

import org.apache.jena.base.Sys;
import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class MapsToMainConverter {

    public static void convertForComponentDefinition(SBOLDocument sbol2Doc, Component sbol3Component, ComponentDefinition sbol2ComponentDefinition)
            throws SBOLGraphException, SBOLValidationException {

				ComponentReferenceToMapsToConverter compRefToMapsToConverter = new ComponentReferenceToMapsToConverter();
				System.out.println("MAPSTO CONVERT FOR COMPONENT DEFINITION: " + sbol3Component.getDisplayId());
				if (sbol3Component.getComponentReferences() == null){
					System.out.println("Component References NULL: " + sbol3Component.getUri());
				} else {
					
					for (org.sbolstandard.core3.entity.ComponentReference componentReference : sbol3Component.getComponentReferences()) {
					
						SubComponent subComponentInChildOf = componentReference.getInChildOf();
						
						org.sbolstandard.core2.Component componentSbol2Parent = sbol2ComponentDefinition.getComponent(subComponentInChildOf.getDisplayId());
						
						compRefToMapsToConverter.convert(sbol2Doc, componentSbol2Parent, sbol3Component, componentReference);
					}
				}            
    }
    
    public static void convertForModuleDefinition(SBOLDocument sbol2Doc, Component sbol3Component, ModuleDefinition sbol2ModuleDefinition)
            throws SBOLGraphException, SBOLValidationException {
                ComponentReferenceToMapsToConverter compRefToMapsToConverter = new ComponentReferenceToMapsToConverter();
		       
                System.out.println("MAPSTO CONVERT MODULE DEFINITION: " + sbol3Component.getDisplayId());
				if (sbol3Component.getComponentReferences() == null){
					System.out.println("Component References NULL: " + sbol3Component.getUri());
				} else {

					for (org.sbolstandard.core3.entity.ComponentReference componentReference : sbol3Component.getComponentReferences()) {
					
						SubComponent subComponentInChildOf = componentReference.getInChildOf();
						
						org.sbolstandard.core2.Module moduleSbol2Parent = sbol2ModuleDefinition.getModule(subComponentInChildOf.getDisplayId());
						
						compRefToMapsToConverter.convert(sbol2Doc, moduleSbol2Parent, sbol3Component, componentReference);
					}
				}
                
    }
    

}
