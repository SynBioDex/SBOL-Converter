package org.sbolstandard.converter.sbol31_23;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceConstraint;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Constraint;

public class SequenceConstraintConverter implements EntityConverter<Constraint, org.sbolstandard.core2.SequenceConstraint>  {

	@Override
	public SequenceConstraint convert(SBOLDocument document, Constraint input)
			throws SBOLGraphException, SBOLValidationException, SBOLValidationException {
		
		// How to access SBOL3 parent component?
		// input.getComponent()? does not exist
		
		//input.
		

        // 
        //Util.copyIdentified(input, sc);

        //return sc;
		
		
		
		return null;
	}


}
