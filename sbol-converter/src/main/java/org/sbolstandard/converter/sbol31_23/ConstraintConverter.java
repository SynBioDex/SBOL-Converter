package org.sbolstandard.converter.sbol31_23;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.RestrictionType;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceConstraint;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Constraint;

public class ConstraintConverter implements ChildEntityConverter<Constraint, org.sbolstandard.core2.SequenceConstraint>  {

	@Override
	public SequenceConstraint convert(SBOLDocument document, Identified parent, org.sbolstandard.core3.entity.Identified constraintParent, Constraint input)
			throws SBOLGraphException, SBOLValidationException, SBOLValidationException {
				
		ComponentDefinition sbol2CD = (ComponentDefinition) parent;
		RestrictionType sbol2RestrictionType = Util.toSBOL2RestrictionType(input.getRestriction());
		org.sbolstandard.core2.SequenceConstraint sbol2Const=null;
		if (sbol2RestrictionType!=null){
			sbol2Const=sbol2CD.createSequenceConstraint(input.getDisplayId(), 
					sbol2RestrictionType, 
					Util.createSBOL2Uri(input.getSubject().getUri()), 
					Util.createSBOL2Uri(input.getObject().getUri()));
			
			Util.copyIdentified(input, sbol2Const,document);
		}
    	return sbol2Const;	
	}

}
