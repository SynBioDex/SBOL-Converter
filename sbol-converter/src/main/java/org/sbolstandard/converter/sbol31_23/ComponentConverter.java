package org.sbolstandard.converter.sbol31_23;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ComponentConverter implements EntityConverter<Component, ComponentDefinition>  {

    @Override
    public ComponentDefinition convert(SBOLDocument doc, Component input) throws SBOLGraphException, SBOLValidationException { 
    	// TODO: no easy way to see if a URI is in the set of roles
    	if (input.getTypes()!=null) {
    		for (URI type : input.getTypes()) {
    			if (type.toString().equals("https://identifiers.org/SBO:0000241")) {
    				return null;
    			}
    		}
    	}
    	ComponentDefinition compDef;
    	compDef = doc.createComponentDefinition(Util.getURIPrefix(input),input.getDisplayId(),Util.getVersion(input),Util.toSet(input.getTypes()));
    	Util.copyIdentified(input, compDef);
//      	  comp.setRoles(Util.convertRoles(input.getRoles()));
        return compDef;
    }
}
