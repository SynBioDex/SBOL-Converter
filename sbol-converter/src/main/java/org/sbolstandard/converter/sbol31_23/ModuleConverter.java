package org.sbolstandard.converter.sbol31_23;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ModuleConverter implements EntityConverter<Component, ModuleDefinition>  {

    @Override
    public ModuleDefinition convert(SBOLDocument doc, Component input) throws SBOLGraphException, SBOLValidationException { 
    	// TODO: no easy way to see if a URI is in the set of roles
    	if (input.getTypes()!=null) {
    		boolean foundIt = false;
    		for (URI type : input.getTypes()) {
    			if (type.toString().equals("https://identifiers.org/SBO:0000241")) {
    				foundIt = true;
    				break;
    			}
    		}
    		if (!foundIt) return null;
    	}    		
    	ModuleDefinition modDef;
    	modDef = doc.createModuleDefinition(Util.getURIPrefix(input),input.getDisplayId(),Util.getVersion(input));
    	Util.copyIdentified(input, modDef);
//        comp.setRoles(Util.convertRoles(input.getRoles()));
        return modDef;
    }
}
