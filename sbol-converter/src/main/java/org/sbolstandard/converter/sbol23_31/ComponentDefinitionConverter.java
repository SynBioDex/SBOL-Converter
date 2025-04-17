package org.sbolstandard.converter.sbol23_31;


import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core2.SequenceConstraint;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ComponentDefinitionConverter implements EntityConverter<ComponentDefinition, Component>  {

    @Override
    public Component convert(SBOLDocument doc, ComponentDefinition input) throws SBOLGraphException { 
    	//System.out.println("Component:"+Util.createSBOL3Uri(input));
    	// TODO: there are duplicates in the list
    	if (doc.getComponents()!=null) {
    		for (Component c : doc.getComponents()) {
    			//System.out.println("Check:"+c.getUri());
    			if (c.getUri().toString().equals(Util.createSBOL3Uri(input).toString())) {
    				return null;
    			}
    		}
    	}
    	
    	Component comp = doc.createComponent(Util.createSBOL3Uri(input),Util.getNamespace(input),Util.toList(input.getTypes()));
        Util.copyIdentified(input, comp);
        comp.setRoles(Util.convertRoles(input.getRoles()));
        
        // TODO: refactor later
        // TODO: need method to set the list of Sequence URIs

        //comp.setSequences(Util.toList(input.getSequenceURIs()));
        for (org.sbolstandard.core2.Component c : input.getComponents()) {
        	// TODO: need method to create subComponent with URI for instanceOf
        	//comp.createSubComponent(null, comp)
        	//System.out.println("Sub:"+Util.createSBOL3Uri(c).toString());
        	//System.out.println("Def:"+Util.createSBOL3Uri(c.getDefinition()).toString());
        	//comp.createSubComponent(Util.createSBOL3Uri(c), Util.createSBOL3Uri(c.getDefinition()));
        }
        for (SequenceConstraint sc : input.getSequenceConstraints()) {
        	// TODO: create constraints
        	//comp.createConstraint(Util.createSBOL3Uri(sc), sc.getRestrictionURI(), null, null);
        }
        for (SequenceAnnotation sa : input.getSequenceAnnotations()) {
        	if (sa.isSetComponent()) {
        		// TODO: add location to subComponent
        	} else {
        		// TODO: create a SequenceFeature
        	}
        }
        return comp;
    }
}
