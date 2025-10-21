package org.sbolstandard.converter.sbol31_23.measurement;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol31_23.ChildEntityConverter;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.measure.Measure;
import org.sbolstandard.core3.util.SBOLGraphException;

public class MeasurementConverter implements ChildEntityConverter<Measure, org.sbolstandard.core2.Measure> {

	@Override
	public org.sbolstandard.core2.Measure convert(SBOLDocument doc, org.sbolstandard.core2.Identified sbol2Parent, Identified inputParent,
			Measure sbol3Measure) throws SBOLGraphException, SBOLValidationException {


		org.sbolstandard.core2.Measure sbol2measure = null;
		Float value= sbol3Measure.getValue().get();
		Double valueDouble = value != null ? value.doubleValue() : null;
		if (valueDouble!=null){
			if (sbol2Parent instanceof org.sbolstandard.core2.Measured) {
				org.sbolstandard.core2.Measured sbol2ParentMeasured = (org.sbolstandard.core2.Measured) sbol2Parent;
				sbol2measure = sbol2ParentMeasured.createMeasure(sbol3Measure.getDisplayId(),valueDouble, sbol3Measure.getUnit().getUri());
			}
			if (sbol2measure!=null){
				if (sbol3Measure.getTypes()!=null){
					sbol2measure.setTypes(Util.toSet(sbol3Measure.getTypes()));
				}

				if(sbol3Measure.getUnit()!=null) {
					sbol2measure.setUnit(Util.createSBOL2Uri(sbol3Measure.getUnit().getUri()));
				}

				Util.copyIdentified(sbol3Measure, sbol2measure, doc);
			}
		}
		return sbol2measure;
	}
}
