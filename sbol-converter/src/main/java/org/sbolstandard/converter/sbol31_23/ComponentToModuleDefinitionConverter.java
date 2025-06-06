package org.sbolstandard.converter.sbol31_23;

import java.net.URI;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.SequenceAnnotationToFeatureConverter;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ComponentToModuleDefinitionConverter implements EntityConverter<Component, ModuleDefinition>  {

    @Override
    public ModuleDefinition convert(SBOLDocument sbol2Doc, Component component) throws SBOLGraphException, SBOLValidationException { 
    	
    	ModuleDefinition moduleDef = sbol2Doc.createModuleDefinition(Util.getURIPrefix(component), 
    			component.getDisplayId(),
    			Util.getVersion(component));
    	
    	Util.copyIdentified(component, moduleDef);
    	
    	// TODO
    	// THINGS NEED TO BE CONVERTED
    	// roles
    	moduleDef.setRoles(Util.convertRoles3_to_2(component.getRoles()));
    	
    	
    	// MODULES
    	// ??
    	// HOW TO CONVERT MODULES?
    	
    	
    	// INTERACTIONS
    	InteractionConverter interactionConverter = new InteractionConverter();
    	for (org.sbolstandard.core3.entity.Interaction interaction : component.getInteractions()) {
			interactionConverter.convert(sbol2Doc, moduleDef, interaction);
		
		}
    	// FUNCTIONAL COMPONENTS
    	
    	
    	
    	// TODO
    	
    	
    	
    
    	
 	
        return moduleDef;
    }
    
}
