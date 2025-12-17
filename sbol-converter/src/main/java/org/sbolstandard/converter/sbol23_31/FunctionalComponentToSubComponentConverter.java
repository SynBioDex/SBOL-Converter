package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.measurement.MeasurementConverter;

public class FunctionalComponentToSubComponentConverter
		implements ChildEntityConverter<org.sbolstandard.core2.FunctionalComponent, SubComponent> {

	@Override
	public SubComponent convert(SBOLDocument document, Identified sbol3Parent,
			org.sbolstandard.core2.Identified sbol2Parent,
			org.sbolstandard.core2.FunctionalComponent sbol2FunctionalComponent, Parameters parameters)
			throws SBOLGraphException, SBOLValidationException {

		SubComponent sbol3SubComponent = null;

		Component sbol3ParentComponent = (Component) sbol3Parent;
		
		URI sbol3ChildComponentURI = Util.createSBOL3Uri(sbol2FunctionalComponent.getDefinitionURI(), parameters);

		try{
			if (sbol2FunctionalComponent.getDisplayId() != null) {
				sbol3SubComponent = sbol3ParentComponent.createSubComponent(sbol2FunctionalComponent.getDisplayId(), sbol3ChildComponentURI);
			}
			else{
				sbol3SubComponent = sbol3ParentComponent.createSubComponent(sbol3ChildComponentURI);			
			}
			parameters.addMapping(sbol2FunctionalComponent.getIdentity(), sbol3SubComponent.getUri());
		}
		catch (Exception e) {
			throw new SBOLGraphException("Error creating SubComponent for FunctionalComponent: " + sbol2FunctionalComponent.getIdentity(), e);
		}

		// Measurement conversion
		if(sbol2FunctionalComponent.getMeasures()!=null) {
			MeasurementConverter measurementConverter = new MeasurementConverter();
			for (org.sbolstandard.core2.Measure sbol2Measure : sbol2FunctionalComponent.getMeasures()) {
				measurementConverter.convert(document, sbol3SubComponent, sbol2FunctionalComponent, sbol2Measure, parameters);
			}
		}

		Util.copyIdentified(sbol2FunctionalComponent, sbol3SubComponent, parameters);

		

		return sbol3SubComponent;

	}

}
