package org.sbolstandard.converter.sbol23_31;


import java.util.Arrays;

import org.sbolstandard.converter.EntityConverter;
import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ComponentDefinitionConverter implements EntityConverter<ComponentDefinition, Component>  {

    @Override
    public Component convert(SBOLDocument doc, ComponentDefinition input) throws SBOLGraphException {  
        Component comp = doc.createComponent(input.getIdentity(), Util.getNameSpace(input.getIdentity()), Util.toList(input.getTypes()));
        comp.setName(input.getDisplayId());
        comp.setDescription(input.getDescription());
        comp.setRoles(Util.convertRoles(input.getRoles()));
        return comp;
    }
}
