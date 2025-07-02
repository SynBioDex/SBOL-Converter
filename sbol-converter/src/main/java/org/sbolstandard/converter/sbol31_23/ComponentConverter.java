package org.sbolstandard.converter.sbol31_23;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.RestrictionType;

public class ComponentConverter implements EntityConverter<Component, ComponentDefinition> {

	@Override
	public ComponentDefinition convert(SBOLDocument sbol2Doc, Component component)
			throws SBOLGraphException, SBOLValidationException {

		// Skip conversion if the component is a "functional compartment" (SBO:0000241)
		// TODO: no easy way to see if a URI is in the set of roles
		if (component.getTypes() != null) {
			for (URI type : component.getTypes()) {
				if (type.toString().equals("https://identifiers.org/SBO:0000241")) {
					// Not relevant for ComponentDefinition in SBOL2
					return null;
				}
			}
		}

		// Create a new SBOL2 ComponentDefinition using details from the SBOL3 Component
		ComponentDefinition compDef = sbol2Doc.createComponentDefinition(Util.getURIPrefix(component), // URI prefix
				component.getDisplayId(), // Display ID
				Util.getVersion(component), // Version
				Util.toSet(Util.toSBOL2ComponentDefinitionTypes(component.getTypes())) // Types (as set)
		);

		// Copy over identifiers, names, etc.
		Util.copyIdentified(component, compDef);

		// Convert and set SBOL3 roles to SBOL2 roles
		compDef.setRoles(Util.convertRoles3_to_2(component.getRoles()));

		// Convert all SBOL3 SubComponents to SBOL2 format
		List<SubComponent> subComponents = component.getSubComponents();
		if (subComponents != null) {
			SubComponentToComponentConverter subCompConverter = new SubComponentToComponentConverter();
			for (SubComponent subComp : subComponents) {
				// Convert each SubComponent to SBOL2
				subCompConverter.convert(sbol2Doc, compDef, component, subComp);

				// If the SubComponent contains locations, try converting to SequenceAnnotation
				List<Location> locations = subComp.getLocations();
				if (locations != null && !locations.isEmpty()) {
					SubComponentToAnnotationConverter subcToAnnoConverter = new SubComponentToAnnotationConverter();
					subcToAnnoConverter.convert(sbol2Doc, compDef, component, subComp);
					//System.out.println("THIS IS NOT IMPLEMENTED YET");
				}
			}
		}

		// Convert all SBOL3 constraints to SBOL2 format
		List<Constraint> constraints = component.getConstraints();
		if (constraints != null) {
			ConstraintConverter converter = new ConstraintConverter();
			for (Constraint constraint : constraints) {
				// If it is not mapsto constraint, then convert it
				if(!Util.isMapstoConstraint(constraint)) {
					converter.convert(sbol2Doc, compDef, component, constraint);
				}
				
			}
		}

		// Convert all SBOL3 SequenceFeatures to SBOL2 SequenceAnnotations
		List<SequenceFeature> sequenceFeatures = component.getSequenceFeatures();
		if (sequenceFeatures != null && !sequenceFeatures.isEmpty()) {
			for (SequenceFeature sf : sequenceFeatures) {
				SequenceFeatureToAnnotationConverter sfConverter = new SequenceFeatureToAnnotationConverter();
				sfConverter.convert(sbol2Doc, compDef, component, sf);
			}
		}

		if (component.getSequences()!=null)
		{
			for (Sequence sequence: component.getSequences()) {
				// Convert SBOL3 Sequence to SBOL2 Sequence
	            URI sequenceURI = sequence.getUri();
	            URI sbol2URI=Util.createSBOL2Uri(sequenceURI);
	            compDef.addSequence(sbol2URI);
	        
			}	
		}
		// Return the final SBOL2 ComponentDefinition
		return compDef;
	}

	
}
