package org.sbolstandard.converter;

import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public interface EntityConverter<InputEntity, OutputEntity> {
	OutputEntity convert(SBOLDocument document, InputEntity input) throws SBOLGraphException;
}