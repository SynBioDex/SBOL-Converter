package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.util.SBOLGraphException;

public interface ChildEntityConverter<InputEntity,  OutputEntity> {
	OutputEntity convert(org.sbolstandard.core2.SBOLDocument document, org.sbolstandard.core2.Identified parent, InputEntity input) throws SBOLGraphException, SBOLValidationException;
}