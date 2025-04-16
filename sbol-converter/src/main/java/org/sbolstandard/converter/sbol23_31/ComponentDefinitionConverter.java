package org.sbolstandard.converter.sbol23_31;


import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ComponentDefinitionConverter implements EntityConverter<ComponentDefinition, Component>  {

    @Override
    public Component convert(SBOLDocument doc, ComponentDefinition input) throws SBOLGraphException {  
        Component comp = doc.createComponent(Util.createSBOL3Uri(input),Util.getNamespace(input),Util.toList(input.getTypes()));
        Util.copyIdentified(input, comp);
        comp.setRoles(Util.convertRoles(input.getRoles()));
        return comp;
    }
}
