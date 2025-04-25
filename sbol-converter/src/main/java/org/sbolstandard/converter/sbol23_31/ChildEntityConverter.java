package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public interface ChildEntityConverter<InputEntity, OutputEntity> {
	 OutputEntity convert(SBOLDocument document, Identified parent, InputEntity input) throws SBOLGraphException;
}