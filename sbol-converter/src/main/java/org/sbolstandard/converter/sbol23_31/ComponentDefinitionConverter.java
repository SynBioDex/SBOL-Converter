package org.sbolstandard.converter.sbol23_31;


import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.Model;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core2.SequenceConstraint;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
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
    	    	
    	Component comp = doc.createComponent(Util.createSBOL3Uri(input),
    			Util.getNamespace(input),
    			Util.toSBOL3ComponentDefinitionTypes(Util.toList(input.getTypes())));
    	    	
        Util.copyIdentified(input, comp);
        comp.setRoles(Util.convertRoles2_to_3(input.getRoles()));
        
        // TODO: need method to set the list of Sequence URIs
        //comp.setSequences(Util.toList(input.getSequenceURIs()));
        
        ComponentConverter converter = new ComponentConverter();        
        for (org.sbolstandard.core2.Component c : input.getComponents()) {
        	converter.convert(doc, comp, input, c);
        }
        
        for (SequenceAnnotation sa : input.getSequenceAnnotations()) {
        	if (sa.isSetComponent()) {
        		// If the SequenceAnnotation is a subComponent, convert it to a SubComponent
        		SequenceAnnotationToSubComponentConverter satoscConverter = new SequenceAnnotationToSubComponentConverter();
        		satoscConverter.convert(doc, comp, input, sa);
        		
        	} else {
    			// If the SequenceAnnotation is not a subComponent, convert it to a Feature
        		SequenceAnnotationToFeatureConverter satosfConverter = new SequenceAnnotationToFeatureConverter();
        		satosfConverter.convert(doc, comp, input,sa);
        
        	}
        }
        
        SequenceConstraintConverter seqconstConverter = new SequenceConstraintConverter();
        
        for (SequenceConstraint sc : input.getSequenceConstraints()) {
        	seqconstConverter.convert(doc, comp, input, sc);
        }

        return comp;
    }
}
