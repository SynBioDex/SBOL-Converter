package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol31_23.measurement.MeasurementConverter;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.entity.measure.Measure;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;

public class SubComponentToComponentConverter implements ChildEntityConverter<SubComponent, org.sbolstandard.core2.Component>  {

	@Override
	public org.sbolstandard.core2.Component convert(SBOLDocument document, org.sbolstandard.core2.Identified parent,  Identified inputParent, SubComponent input)
			throws SBOLGraphException, SBOLValidationException {
		
		ComponentDefinition sbol2CD = (ComponentDefinition) parent;
		org.sbolstandard.core2.Component sbol2Comp=sbol2CD.createComponent(input.getDisplayId(), AccessType.PUBLIC, Util.createSBOL2Uri(input.getInstanceOfURI()));
		
		sbol2Comp.setRoles(Util.convertSORoles3_to_2(input.getRoles()));

		//Measurement conversion
		if(input.getMeasures()!=null) {
			MeasurementConverter measurementConverter = new MeasurementConverter();
			for (Measure sbol3Measure : input.getMeasures()) {
				measurementConverter.convert(document, sbol2Comp, input, sbol3Measure);
			}
		}

		if(input.getSourceLocations()!=null) {
			SourceLocationConverter sourceLocationConverter = new SourceLocationConverter();
			for (org.sbolstandard.core3.entity.Location sbol3SourceLocation : input.getSourceLocations()) {
				sourceLocationConverter.convert(document, sbol2Comp, input, sbol3SourceLocation);
			}
		}

		Util.copyIdentified(input, sbol2Comp, document);
        return sbol2Comp;	
	}
}
