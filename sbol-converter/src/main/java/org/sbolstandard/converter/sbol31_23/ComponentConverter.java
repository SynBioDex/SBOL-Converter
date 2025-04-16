package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ComponentConverter implements EntityConverter<Component, ComponentDefinition>  {

    @Override
    public ComponentDefinition convert(SBOLDocument doc, Component input) throws SBOLGraphException, SBOLValidationException {  
    	ComponentDefinition comp;
		comp = doc.createComponentDefinition(Util.getURIPrefix(input),input.getDisplayId(),Util.getVersion(input),Util.toSet(input.getTypes()));
    	Util.copyIdentified(input, comp);
//        comp.setRoles(Util.convertRoles(input.getRoles()));
        return comp;
    }
}
