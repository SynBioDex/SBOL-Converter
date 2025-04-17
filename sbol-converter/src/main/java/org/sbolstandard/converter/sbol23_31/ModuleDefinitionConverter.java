package org.sbolstandard.converter.sbol23_31;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ModuleDefinitionConverter implements EntityConverter<ModuleDefinition, Component>  {

    @Override
    public Component convert(SBOLDocument doc, ModuleDefinition input) throws SBOLGraphException { 
    	System.out.println("Component:"+Util.createSBOL3Uri(input));
    	List<URI> types = new ArrayList<URI>();
    	types.add(URI.create("https://identifiers.org/SBO:0000241"));
    	Component comp = doc.createComponent(Util.createSBOL3Uri(input),Util.getNamespace(input),types);
        Util.copyIdentified(input, comp);
        comp.setRoles(Util.convertRoles(input.getRoles()));
        return comp;
    }
}
