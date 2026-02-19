package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.LocalSubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class LocalComponentToAnnotationConverter implements ChildEntityConverter<LocalSubComponent, SequenceAnnotation> {

	@Override
	public SequenceAnnotation convert(SBOLDocument doc, org.sbolstandard.core2.Identified sbol2Parent, Identified inputParent,
			LocalSubComponent sbol3LocalSubComponent) throws SBOLGraphException, SBOLValidationException {

		//Currently, only converted if it was originally created from SBOL2 SequenceAnnotation with generic location. 
		if (!SubComponentToAnnotationConverter.isSBOL2GenericLocation(sbol3LocalSubComponent)){
			return null;
		}

		// SBOL3 SequenceFeature will be mapped to SBOL2 SequenceAnnotation
		ComponentDefinition parentCompDef = (ComponentDefinition) sbol2Parent;

		// This will hold the SBOL2 SequenceAnnotation that we are constructing
		SequenceAnnotation sbol2SeqAnno = null;
		sbol2SeqAnno = SubComponentToAnnotationConverter.handleLocations(sbol3LocalSubComponent, sbol2SeqAnno, parentCompDef, doc);	

		// If no valid locations were found, throw an exception
		if (sbol2SeqAnno == null) {
			throw new SBOLGraphException( "No valid locations found in LocalSubComponent: " + sbol3LocalSubComponent.getUri());
		}
		

		// If the resulting SequenceAnnotation has no locations, this is invalid
		if (sbol2SeqAnno.getLocations().isEmpty()) {
			throw new SBOLGraphException("The resulting SBOL2 SequenceAnnotation must have at least one location." + sbol2SeqAnno.getIdentity());
		}

		// Copy general Identified properties (like displayId, version, etc.)
		Util.copyIdentified(sbol3LocalSubComponent, sbol2SeqAnno, doc);

		return sbol2SeqAnno;
	}

	
}
