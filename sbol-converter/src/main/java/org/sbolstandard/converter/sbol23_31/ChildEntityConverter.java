package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public interface ChildEntityConverter<InputEntity, OutputEntity> {
	 OutputEntity convert(SBOLDocument document, Identified parent, org.sbolstandard.core2.Identified inputParent, InputEntity input,Parameters parameters) throws SBOLGraphException, SBOLValidationException;
}