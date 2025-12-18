package org.sbolstandard.converter.sbol23_31;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
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

	//TODO
	public static void convertFromComponentDefinition(SBOLDocument document, ComponentDefinition sbol2ComponentDefinition, Parameters parameters)
			throws SBOLGraphException, SBOLValidationException {

		for(org.sbolstandard.core2.Component sbol2Component: sbol2ComponentDefinition.getComponents()) {
			for(MapsTo mapsTo: sbol2Component.getMapsTos()) {
				
				URI sbol3ParentComponentURI = Util.createSBOL3Uri(sbol2ComponentDefinition);
				Component sbol3ParentComponent=document.getIdentified(sbol3ParentComponentURI, Component.class);
				SubComponent sbol3SubComponentForModule = Util.getSBOL3Entity(sbol3ParentComponent.getSubComponents(), sbol2Component, parameters);
				
				ComponentReference sbol3CompRef = MapstoToComponentReferenceConverter.convertForComponent(document, sbol2Component, sbol3ParentComponent, mapsTo, sbol3SubComponentForModule, parameters);
				
				Constraint sbol3Constraint = MapstoToConstraintConverter.convert(sbol3ParentComponent, mapsTo, sbol3CompRef, parameters);
				
				if (sbol3Constraint == null) {
					// sbol3SubComponentForModule.addConstraint(sbol3Constraint);
					System.out.println("Constraint is null for " + mapsTo.getIdentity() + ". This should not happen.");
				}
				
			}
		}

	}
	
	
	public static void convertFromModuleDefinition(SBOLDocument document, ModuleDefinition sbol2ModuleDefinition, Parameters parameters)
			throws SBOLGraphException, SBOLValidationException {

		for(org.sbolstandard.core2.Module sbol2Module: sbol2ModuleDefinition.getModules()) {
			for(MapsTo mapsTo: sbol2Module.getMapsTos()) {
				
				URI sbol3ParentComponentURI = Util.createSBOL3Uri(sbol2ModuleDefinition);
				Component sbol3ParentComponent=document.getIdentified(sbol3ParentComponentURI, Component.class);
				
				SubComponent sbol3SubComponentForModule = Util.getSBOL3Entity(sbol3ParentComponent.getSubComponents(), sbol2Module, parameters);
				
				ComponentReference sbol3CompRef = MapstoToComponentReferenceConverter.convertForModule(document, sbol2Module, sbol3ParentComponent, mapsTo, sbol3SubComponentForModule, parameters);
				if (sbol3CompRef!=null){			
					Constraint sbol3Constraint = MapstoToConstraintConverter.convert(sbol3ParentComponent, mapsTo, sbol3CompRef, parameters);
				
					if (sbol3Constraint == null) {
						String message="***CONVERSION ERROR***: sbol2:MapsTo_sbol3:Constraint conversion could not be completed for MapsTo:"+ mapsTo.getIdentity() + ".";
						System.out.println(message);
					}
				}
				else{
					String message="***CONVERSION ERROR***: sbol2:MapsTo_sbol3:ComponentReference conversion could not be completed for MapsTo:"+ mapsTo.getIdentity() + ".";
					System.out.println(message);
				}
				
			}
		}

	}
	
}
