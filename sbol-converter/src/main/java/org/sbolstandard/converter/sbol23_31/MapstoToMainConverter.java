package org.sbolstandard.converter.sbol23_31;

import java.net.URI;

import org.sbolstandard.converter.ConverterVocabulary;
import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.ComponentInstance;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.MapsTo;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.ComponentReference;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class MapstoToMainConverter {

	
	public static void convertFromComponentDefinition(SBOLDocument document, ComponentDefinition sbol2ComponentDefinition, Parameters parameters) throws SBOLGraphException, SBOLValidationException {
		URI sbol3ParentComponentURI = Util.createSBOL3Uri(sbol2ComponentDefinition);
		Component sbol3ParentComponent=document.getIdentified(sbol3ParentComponentURI, Component.class);
			
		for(org.sbolstandard.core2.Component sbol2Component: sbol2ComponentDefinition.getComponents()) {
			SubComponent sbol3SubComponentForModule = Util.getSBOL3Entity(sbol3ParentComponent.getSubComponents(), sbol2Component, parameters);								
			for(MapsTo mapsTo: sbol2Component.getMapsTos()) {				
				convertFromComponentInstance(document, sbol2Component, sbol3ParentComponent, sbol3SubComponentForModule, mapsTo, parameters);
			}
		}
	}
	
	private static ComponentReference convertFromComponentInstance(SBOLDocument document, ComponentInstance sbol2Component, Component sbol3ParentComponent, SubComponent sbol3SubComponentForModule, MapsTo mapsTo, Parameters parameters) throws SBOLGraphException, SBOLValidationException{
		ComponentReference sbol3CompRef = MapstoToComponentReferenceConverter.convertForComponent(document, sbol2Component, sbol3ParentComponent, mapsTo, sbol3SubComponentForModule, parameters);				
		Constraint sbol3Constraint = MapstoToConstraintConverter.convert(sbol3ParentComponent, mapsTo, sbol3CompRef, parameters);				
		if (sbol3Constraint == null) {
			throw new SBOLGraphException("SBOL3 Constraint is null for " + mapsTo.getIdentity() + "!");
		}				
		return sbol3CompRef;
	}

	public static void convertFromModuleDefinition(SBOLDocument document, ModuleDefinition sbol2ModuleDefinition, Parameters parameters) throws SBOLGraphException, SBOLValidationException {
		URI sbol3ParentComponentURI = Util.createSBOL3Uri(sbol2ModuleDefinition);
		Component sbol3ParentComponent=document.getIdentified(sbol3ParentComponentURI, Component.class);				
			
		for(org.sbolstandard.core2.Module sbol2Module: sbol2ModuleDefinition.getModules()) {
			SubComponent sbol3SubComponentForModule = Util.getSBOL3Entity(sbol3ParentComponent.getSubComponents(), sbol2Module, parameters);								
			for(MapsTo mapsTo: sbol2Module.getMapsTos()) {				
				ComponentReference sbol3CompRef = MapstoToComponentReferenceConverter.convertForModule(document, sbol2Module, sbol3ParentComponent, mapsTo, sbol3SubComponentForModule, parameters);
				if (sbol3CompRef!=null){			
					Constraint sbol3Constraint = MapstoToConstraintConverter.convert(sbol3ParentComponent, mapsTo, sbol3CompRef, parameters);				
					if (sbol3Constraint == null) {
						String message="***CONVERSION ERROR***: sbol2:MapsTo_sbol3:Constraint conversion could not be completed for MapsTo:"+ mapsTo.getIdentity() + ".";
						Util.getLogger().error(message);
					}
				}
				else{
					String message="***CONVERSION ERROR***: sbol2:MapsTo_sbol3:ComponentReference conversion could not be completed for MapsTo:"+ mapsTo.getIdentity() + ".";
					Util.getLogger().error(message);
				}				
			}		
		}

		for(org.sbolstandard.core2.FunctionalComponent sbol2FunctionalComponent: sbol2ModuleDefinition.getFunctionalComponents()) {
			SubComponent sbol3SubComponentForModule = Util.getSBOL3Entity(sbol3ParentComponent.getSubComponents(), sbol2FunctionalComponent, parameters);								
			for(MapsTo mapsTo: sbol2FunctionalComponent.getMapsTos()) {				
				ComponentReference compRef= convertFromComponentInstance(document, sbol2FunctionalComponent, sbol3ParentComponent, sbol3SubComponentForModule, mapsTo, parameters);
				if (compRef!=null){
					compRef.addAnnotation(ConverterVocabulary.Two_to_Three.sbol2MapstoOriginInFC, true);
				}
			}
		}
	}
}
