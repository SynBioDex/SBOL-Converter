package org.sbolstandard.converter.sbol31_23;

import java.net.URI;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
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
    		SubComponentConverter subCompConverter = new SubComponentConverter();
			for (SubComponent subComp : subComponents) {
				subCompConverter.convert(sbol2Doc, compDef, subComp);
			}
    	}
    	
    	List<Constraint> constraints= component.getConstraints();
    	if (constraints!=null) {
    		ConstraintConverter converter = new ConstraintConverter();
			for (Constraint constraint: constraints) {
				converter.convert(sbol2Doc, compDef, constraint);
			}
    	}
    	
    	
        return compDef;
    }
}
