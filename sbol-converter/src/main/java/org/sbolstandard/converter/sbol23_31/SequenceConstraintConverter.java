package org.sbolstandard.converter.sbol23_31;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.Identified;

public class SequenceConstraintConverter implements ChildEntityConverter<org.sbolstandard.core2.SequenceConstraint,Constraint>  {

    @Override
    public Constraint convert(SBOLDocument doc, Identified parent, org.sbolstandard.core2.Identified inputParent, org.sbolstandard.core2.SequenceConstraint input, Parameters parameters) throws SBOLGraphException {
		// SBOL 2 SequenceConstraint maps to SBOL3 Constraint

    	// Trying to reach parent  component since there is no way to create a SequenceConstraint in SBOL2
    	org.sbolstandard.core2.ComponentDefinition sbol2Parent = input.getObjectDefinition();
    	
    	ComponentDefinitionConverter cdConverter = new ComponentDefinitionConverter();
    	
    	// SBOL2 parent componentdefinition converted to SBOL3 component
    	URI SBOL3SubjectUri= parameters.getMapping(input.getSubjectURI());    	
    	URI SBOL3ObjectUri=parameters.getMapping(input.getObjectURI());
    	
    	Feature subjectFeature=doc.getIdentified(SBOL3SubjectUri, Feature.getSubClassTypes());
    	Feature objectFeature=doc.getIdentified(SBOL3ObjectUri, Feature.getSubClassTypes());
        Component parentComponent = (Component) parent;
        URI sbol3RestrictionUri = Util.toSBOL3RestrictionType(input.getRestrictionURI()).getUri(); 
        Constraint constraint =null;
        if (input.getDisplayId()!=null){
        	constraint= parentComponent.createConstraint(input.getDisplayId(), sbol3RestrictionUri, subjectFeature, objectFeature);
        }
        else{
        	constraint= parentComponent.createConstraint(sbol3RestrictionUri, subjectFeature, objectFeature);
        }
        Util.copyIdentified(input, constraint);		
    	return constraint;
    	
    
    }
}
