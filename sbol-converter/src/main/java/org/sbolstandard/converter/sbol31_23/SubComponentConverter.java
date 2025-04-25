package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Identified;

public class SubComponentConverter implements ChildEntityConverter<SubComponent, org.sbolstandard.core2.Component>  {

	@Override
	public org.sbolstandard.core2.Component convert(SBOLDocument document, org.sbolstandard.core2.Identified parent, SubComponent input)
			throws SBOLGraphException, SBOLValidationException {
		
		ComponentDefinition sbol2CD = (ComponentDefinition) parent;
		org.sbolstandard.core2.Component sbol2Comp=sbol2CD.createComponent(input.getDisplayId(), AccessType.PUBLIC, Util.createSBOL2Uri(input.getInstanceOf().getUri()));
    	Util.copyIdentified(input, sbol2Comp);
        return sbol2Comp;	
	}
}
