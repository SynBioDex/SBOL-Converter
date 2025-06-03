package org.sbolstandard.converter.sbol31_23;

import java.net.URI;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.SequenceAnnotationToFeatureConverter;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ComponentConverter implements EntityConverter<Component, ComponentDefinition>  {

    @Override
    public ComponentDefinition convert(SBOLDocument sbol2Doc, Component component) throws SBOLGraphException, SBOLValidationException { 
    	// TODO: no easy way to see if a URI is in the set of roles
    	if (component.getTypes()!=null) {
    		for (URI type : component.getTypes()) {
    			if (type.toString().equals("https://identifiers.org/SBO:0000241")) {
    				return null;
    			}
    		}
    	}
    	
    	ComponentDefinition compDef = sbol2Doc.createComponentDefinition(Util.getURIPrefix(component),
    			component.getDisplayId(),
    			Util.getVersion(component),
    			Util.toSet(Util.toSBOL2ComponentDefinitionTypes(component.getTypes())));
    	
    	Util.copyIdentified(component, compDef);
    	compDef.setRoles(Util.convertRoles3_to_2(component.getRoles()));
    	    	
    	List<SubComponent> subComponents= component.getSubComponents();
    	if (subComponents!=null) {
    		SubComponentToComponentConverter subCompConverter = new SubComponentToComponentConverter();
			for (SubComponent subComp : subComponents) {
				
				subCompConverter.convert(sbol2Doc, compDef, subComp);
				// If the SubComponent is a SequenceAnnotation, convert it to a SequenceAnnotation
    			List<Location> locations = subComp.getLocations();
    			if (locations != null && !locations.isEmpty()) {
    				//TODO: this is not implemented yet
    				
    				SubComponentToAnnotationConverter subcToAnnoConverter = new SubComponentToAnnotationConverter();
    				subcToAnnoConverter.convert(sbol2Doc, compDef, subComp);
    				System.out.println("THIS IS NOT IMPLEMENTED YET");
    			}
			}
    	}
    	
    	List<Constraint> constraints= component.getConstraints();
    	if (constraints!=null) {
    		ConstraintConverter converter = new ConstraintConverter();
			for (Constraint constraint: constraints) {
				converter.convert(sbol2Doc, compDef, constraint);
			}
    	}
    	
    	List<SequenceFeature> sequenceFeatures = component.getSequenceFeatures();
    	if (sequenceFeatures != null && !sequenceFeatures.isEmpty()) {
    		for (SequenceFeature sf : sequenceFeatures) {
        		SequenceFeatureToAnnotationConverter sfConverter = new SequenceFeatureToAnnotationConverter ();
        		sfConverter.convert(sbol2Doc, compDef, sf);
            }
    	
    	
    	
    	
    	}
    	
    	
        return compDef;
    }
}
