package org.sbolstandard.converter.sbol23_31;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.FunctionalComponent;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.Participation;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.Interface;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class DEL_ModuleDefinitionToComponentConverter implements EntityConverter<ModuleDefinition, Component> {

	@Override
	public Component convert(SBOLDocument doc, ModuleDefinition sbol2ModuleDefinition, Parameters parameters)
			throws SBOLGraphException, SBOLValidationException {
		// Create a list of types for the SBOL3 Component (here, always SBO:0000241 =
		// "functional component")
		List<URI> types = new ArrayList<URI>();
		types.add(URI.create("https://identifiers.org/SBO:0000241"));

		// Create the SBOL3 Component for this module, using the module's URI and
		// namespace
		Component sbol3Component = doc.createComponent(Util.createSBOL3Uri(sbol2ModuleDefinition), Util.getNamespace(sbol2ModuleDefinition), types);

		// Copy generic metadata (displayId, name, description, etc.)
		Util.copyIdentified(sbol2ModuleDefinition, sbol3Component, parameters);

		// Set SBOL2 roles on the SBOL3 Component, converting format as needed
		sbol3Component.setRoles(Util.convertSORoles2_to_3(sbol2ModuleDefinition.getRoles()));
		
		List<FunctionalComponent> sbol2ConvertedFuncComps = new ArrayList<FunctionalComponent>();

		// --- Participation/Interface handling ---
		// For every Interaction in the ModuleDefinition...
		for (org.sbolstandard.core2.Interaction sbol2Interaction : sbol2ModuleDefinition.getInteractions()) {
			// For every Participation in this Interaction...
			for (Participation sbol2Participation : sbol2Interaction.getParticipations()) {
				// Convert SBOL2 FunctionalComponent to SBOL3 SubComponent
				FunctionalComponentToSubComponentConverter fcConverter = new FunctionalComponentToSubComponentConverter();
				FunctionalComponent sbol2FuncCom = sbol2Participation.getParticipant();
				sbol2ConvertedFuncComps.add(sbol2FuncCom);				
				SubComponent sbol3SubCom = fcConverter.convert(doc, sbol3Component, sbol2ModuleDefinition, sbol2FuncCom, parameters);

				setUpInterface(sbol2FuncCom, sbol3Component, sbol3SubCom);
				
			}
		}
		
		// --- Interactions conversion ---
		// For each SBOL2 Interaction, convert to SBOL3 and attach to the new component
		for (org.sbolstandard.core2.Interaction sbol2Interaction : sbol2ModuleDefinition.getInteractions()) {
			// Print the interaction (for debugging/logging)
			//System.out.println(sbol2Interaction);
			// Convert the interaction to SBOL3 and add to the component
			InteractionConverter interactionConverter = new InteractionConverter();
			interactionConverter.convert(doc, sbol3Component, sbol2ModuleDefinition, sbol2Interaction, parameters);
		}
		// TODO: Handle all the functional components
		// --- Functional Components conversion ---
		for(FunctionalComponent sbol2FuncCom : sbol2ModuleDefinition.getFunctionalComponents()) {
			// Convert SBOL2 FunctionalComponent to SBOL3 SubComponent
			//System.out.println("FUNC COMP" + sbol2FuncCom.getIdentity());
			if(!sbol2ConvertedFuncComps.contains(sbol2FuncCom)) {
				FunctionalComponentToSubComponentConverter fcConverter = new FunctionalComponentToSubComponentConverter();
				SubComponent sbol3SubCom = fcConverter.convert(doc, sbol3Component, sbol2ModuleDefinition, sbol2FuncCom, parameters);

				setUpInterface(sbol2FuncCom, sbol3Component, sbol3SubCom);
				
			}
			
		}
		// MODULES SHOULD BE CONVERTED INTO SUBCOMPONENTS
				// --- Module to SubComponent conversion ---
		ModuleToSubComponentConverter moduleToSubComponentConverter = new ModuleToSubComponentConverter();
		for (org.sbolstandard.core2.Module sbol2Module: sbol2ModuleDefinition.getModules()) {
			moduleToSubComponentConverter.convert(doc, sbol3Component, sbol2ModuleDefinition, sbol2Module, parameters);
		}
		
		// Return the newly created SBOL3 Component representing the SBOL2
		// ModuleDefinition
		return sbol3Component;
	}
	
	// Ensures the Component has an Interface. If not, creates one.
	private Interface getInterface(Component sbol3Component) throws SBOLGraphException {
		Interface sbol3Interface = sbol3Component.getInterface();
		if (sbol3Interface == null) {
			sbol3Interface = sbol3Component.createInterface();
		}
		return sbol3Interface;
	}

	// Adds a SubComponent to a list of Features (creating the list if needed)
	private List<Feature> toFeatureList(SubComponent sbol3SubCom, List<Feature> features) {
		if (features == null) {
			features = new ArrayList<Feature>();
		}
		features.add(sbol3SubCom);
		return features;
	}

	private void setUpInterface(FunctionalComponent sbol2FuncCom, Component sbol3Component, SubComponent sbol3SubCom) throws SBOLGraphException {
		// Directional information determines SBOL3 interface placement (input, output,
		// or non-directional)
		if (sbol2FuncCom.getDirection() != org.sbolstandard.core2.DirectionType.NONE) {
			// Get or create an SBOL3 Interface for this Component
			Interface sbol3Interface = getInterface(sbol3Component);

			// Place the SubComponent in the appropriate list
			// (inputs/outputs/non-directional) based on direction
			if (sbol2FuncCom.getDirection() == org.sbolstandard.core2.DirectionType.IN) {
				sbol3Interface.setInputs(toFeatureList(sbol3SubCom, sbol3Interface.getInputs()));
			} 
			else if (sbol2FuncCom.getDirection() == org.sbolstandard.core2.DirectionType.OUT) {
				sbol3Interface.setOutputs(toFeatureList(sbol3SubCom, sbol3Interface.getOutputs()));
			} 
			else if (sbol2FuncCom.getDirection() == org.sbolstandard.core2.DirectionType.INOUT) {
				sbol3Interface.setNonDirectionals(toFeatureList(sbol3SubCom, sbol3Interface.getNonDirectionals()));
			}
		} else {
			// If direction is NONE, but access is PUBLIC, treat as non-directional
			// interface
			if (sbol2FuncCom.getAccess() == AccessType.PUBLIC) {
				Interface sbol3Interface = getInterface(sbol3Component);
				sbol3Interface.setNonDirectionals(toFeatureList(sbol3SubCom, sbol3Interface.getNonDirectionals()));
			}
		}
	}
}
