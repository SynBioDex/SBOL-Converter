package org.sbolstandard.converter.sbol31_23;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.DirectionType;
import org.sbolstandard.core2.FunctionalComponent;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Interface;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

// This converter maps an SBOL3 SubComponent to an SBOL2 FunctionalComponent
public class SubComponentToFunctionalComponentConverter
		implements ChildEntityConverter<SubComponent, org.sbolstandard.core2.FunctionalComponent> {

	@Override
	public org.sbolstandard.core2.FunctionalComponent convert(SBOLDocument document,
			org.sbolstandard.core2.Identified parent, // Should be an SBOL2 ModuleDefinition
			Identified inputParent, // Should be the parent SBOL3 Component
			SubComponent sbol3SubComponent // The SBOL3 SubComponent to convert
	) throws SBOLGraphException, SBOLValidationException {

		// Cast parent to SBOL2 ModuleDefinition (where FunctionalComponents live)
		org.sbolstandard.core2.ModuleDefinition sbol2ModuleDefinition = (org.sbolstandard.core2.ModuleDefinition) parent;

		// Cast inputParent to SBOL3 Component (holds the interface)
		Component sbol3ParentComponent = (Component) inputParent;
		Interface sbol3Interface = sbol3ParentComponent.getInterface();

		// Default direction and access in SBOL2 (NONE/PRIVATE)
		DirectionType sbol2DirectionType = DirectionType.NONE;
		AccessType sbol2AccessType = AccessType.PRIVATE;

		// Check if SubComponent is an output of the parent Component's interface
		if (sbol3Interface.getOutputs() != null
				&& Util.getByUri(sbol3Interface.getOutputs(), sbol3SubComponent.getUri()) != null) {
			sbol2DirectionType = DirectionType.OUT; // Map to OUT direction in SBOL2
			sbol2AccessType = AccessType.PUBLIC; // Make access PUBLIC
		}
		// Check if SubComponent is an input
		else if (sbol3Interface.getInputs() != null
				&& Util.getByUri(sbol3Interface.getInputs(), sbol3SubComponent.getUri()) != null) {
			sbol2DirectionType = DirectionType.IN; // Map to IN direction
			sbol2AccessType = AccessType.PUBLIC;
		}
		// Check if SubComponent is non-directional
		else if (sbol3Interface.getNonDirectionals() != null
				&& Util.getByUri(sbol3Interface.getNonDirectionals(), sbol3SubComponent.getUri()) != null) {
			sbol2DirectionType = DirectionType.INOUT; // Map to INOUT
			sbol2AccessType = AccessType.PUBLIC;
		}

		// The SBOL2 FunctionalComponent must reference a ComponentDefinition, which
		// comes from
		// the "instanceOf" property of the SubComponent in SBOL3 (converted to SBOL2
		// URI)
		URI sbol2ChildComponentDefinitionURI = Util.createSBOL2Uri(sbol3SubComponent.getInstanceOf().getUri());

		// Create the FunctionalComponent in the SBOL2 ModuleDefinition
		FunctionalComponent sbol2FuncComp = sbol2ModuleDefinition.createFunctionalComponent(
				sbol3SubComponent.getDisplayId(), // Display ID
				sbol2AccessType, // AccessType (PUBLIC/PRIVATE)
				sbol2ChildComponentDefinitionURI, // URI of the referenced ComponentDefinition
				sbol2DirectionType // DirectionType (IN, OUT, INOUT, NONE)
		);

		// Copy metadata (displayId, name, description, etc.) from SBOL3 SubComponent to
		// SBOL2 FunctionalComponent
		Util.copyIdentified(sbol3SubComponent, sbol2FuncComp);

		// Return the new FunctionalComponent
		return sbol2FuncComp;
	}
}
