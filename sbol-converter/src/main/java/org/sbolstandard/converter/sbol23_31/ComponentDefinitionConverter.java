package org.sbolstandard.converter.sbol23_31;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol31_23.LocalComponentToAnnotationConverter;
import org.sbolstandard.converter.sbol31_23.SubComponentToAnnotationConverter;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core2.SequenceConstraint;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ComponentDefinitionConverter implements EntityConverter<ComponentDefinition, Component> {

    @Override
    public Component convert(SBOLDocument doc, ComponentDefinition input, Parameters parameters) throws SBOLGraphException, SBOLValidationException { 
    	
		if (doc.getComponents()!=null) {
    		for (Component c : doc.getComponents()) {
    			if (c.getUri().toString().equals(Util.createSBOL3Uri(input).toString())) {
    				return null;
    			}
    		}
    	}
    	    	
    	Component comp = doc.createComponent(Util.createSBOL3Uri(input), Util.getNamespace(input), Util.toSBOL3ComponentDefinitionTypes(Util.toList(input.getTypes())));    	    	
        Util.copyIdentified(input, comp, parameters);
        comp.setRoles(Util.convertSORoles2_to_3(input.getRoles()));
        
        if (input.getSequenceURIs()!= null && input.getSequenceURIs().size() > 0) {
        	List<org.sbolstandard.core3.entity.Sequence> sequences= new ArrayList<>();
        	for (URI uri:input.getSequenceURIs()){
        		URI sbol3URI=Util.createSBOL3Uri(uri, parameters);
        		org.sbolstandard.core3.entity.Sequence sbol3Seq=doc.getIdentified(sbol3URI, org.sbolstandard.core3.entity.Sequence.class);
        		sequences.add(sbol3Seq);        		
        	}
        	comp.setSequences(sequences);        	 
        }
               
        ComponentConverter converter = new ComponentConverter();        
        for (org.sbolstandard.core2.Component c : input.getComponents()) {
        	converter.convert(doc, comp, input, c, parameters);
        }
        
        for (SequenceAnnotation sa : input.getSequenceAnnotations()) {
        	if (sa.isSetComponent()) {
        		// If the SequenceAnnotation is a subComponent, convert it to a SubComponent
        		SequenceAnnotationToSubComponentConverter satoscConverter = new SequenceAnnotationToSubComponentConverter();
        		satoscConverter.convert(doc, comp, input, sa, parameters);
        		
        	} 
			else if (SequenceAnnotationToLocalSubComponent.isLocalSubComponent(sa)){
				// If the SequenceAnnotation is a local sub-component, convert it to a SequenceAnnotation with generic location
				SequenceAnnotationToLocalSubComponent satolscConverter = new SequenceAnnotationToLocalSubComponent();
				satolscConverter.convert(doc, comp, input, sa, parameters);
			}
			else {
    			// If the SequenceAnnotation is not a subComponent, convert it to a Feature
        		SequenceAnnotationToFeatureConverter satosfConverter = new SequenceAnnotationToFeatureConverter();
        		satosfConverter.convert(doc, comp, input,sa, parameters);
        	}
        }
        
        SequenceConstraintConverter seqconstConverter = new SequenceConstraintConverter();
        for (SequenceConstraint sc : input.getSequenceConstraints()) {
        	seqconstConverter.convert(doc, comp, input, sc, parameters);
        }

		return comp;
	}
}