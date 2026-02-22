package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.ConverterVocabulary;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class MapsToMainConverter {

    public static void convertForComponentDefinition(SBOLDocument sbol2Doc, Component sbol3Component, ComponentDefinition sbol2ComponentDefinition) throws SBOLGraphException, SBOLValidationException {

		ComponentReferenceToMapsToConverter compRefToMapsToConverter = new ComponentReferenceToMapsToConverter();
		if (sbol3Component.getComponentReferences() != null) {
			for (org.sbolstandard.core3.entity.ComponentReference componentReference : sbol3Component.getComponentReferences()) {
				SubComponent subComponentInChildOf = componentReference.getInChildOf();
				org.sbolstandard.core2.Component componentSbol2Parent = sbol2ComponentDefinition.getComponent(subComponentInChildOf.getDisplayId());
				compRefToMapsToConverter.convert(sbol2Doc, componentSbol2Parent, sbol3Component, componentReference);
			}
		}            
    }
    
    public static void convertForModuleDefinition(SBOLDocument sbol2Doc, Component sbol3Component, ModuleDefinition sbol2ModuleDefinition) throws SBOLGraphException, SBOLValidationException {
        ComponentReferenceToMapsToConverter compRefToMapsToConverter = new ComponentReferenceToMapsToConverter();
		       
        if (sbol3Component.getComponentReferences() != null){
			for (org.sbolstandard.core3.entity.ComponentReference componentReference : sbol3Component.getComponentReferences()) {					
				SubComponent subComponentInChildOf = componentReference.getInChildOf();						
				boolean placeCompRefInFC=false;
				Object objectValue=org.sbolstandard.converter.Util.extractSBOL2AnnotationValue(componentReference, ConverterVocabulary.Two_to_Three.sbol2MapstoOriginInFC);
				if (objectValue!=null && objectValue.toString().equalsIgnoreCase("true")){
					placeCompRefInFC=true;
				}
				//Maps to can also be within FCs. If this was a conversion from SBOL2, follow the same steps and create an fc.MapsTo
				if (placeCompRefInFC)
				{
					org.sbolstandard.core2.FunctionalComponent fcSbol2Parent = sbol2ModuleDefinition.getFunctionalComponent(subComponentInChildOf.getDisplayId());
					compRefToMapsToConverter.convert(sbol2Doc, fcSbol2Parent, sbol3Component, componentReference);
				}
				//As default, convert CompRef to MapsTo.
				else{
					org.sbolstandard.core2.Module moduleSbol2Parent = sbol2ModuleDefinition.getModule(subComponentInChildOf.getDisplayId());						
					compRefToMapsToConverter.convert(sbol2Doc, moduleSbol2Parent, sbol3Component, componentReference);

				}
			}
		}                
    }
}