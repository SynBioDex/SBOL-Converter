package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;

public interface EntityConverter<InputEntity, OutputEntity> {
	OutputEntity convert(SBOLDocument document, InputEntity input) throws SBOLGraphException, SBOLValidationException, SBOLValidationException;
}