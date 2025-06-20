package org.sbolstandard.converter.sbol31_23.measurement;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol31_23.ChildEntityConverter;
import org.sbolstandard.core2.Participation;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.measure.Measure;
import org.sbolstandard.core3.util.SBOLGraphException;

public class MeasureConverter implements ChildEntityConverter<Measure, org.sbolstandard.core2.Measure> {

	@Override
	public org.sbolstandard.core2.Measure convert(SBOLDocument doc, org.sbolstandard.core2.Identified sbol2Parent, Identified inputParent,
			Measure sbol3Measure) throws SBOLGraphException, SBOLValidationException {


		org.sbolstandard.core2.Measure measure = null;
		Float value= sbol3Measure.getValue().get();
		Double valueDouble = value != null ? value.doubleValue() : null;
		if (valueDouble!=null){
			if (sbol2Parent instanceof org.sbolstandard.core2.Measured) {
				org.sbolstandard.core2.Measured sbol2ParentMeasured = (org.sbolstandard.core2.Measured) sbol2Parent;
				measure = sbol2ParentMeasured.createMeasure(sbol3Measure.getDisplayId(),valueDouble, sbol3Measure.getUnit().getUri());
			}
			if (measure!=null){
				Util.copyIdentified(sbol3Measure, measure);
			}
		}
		return measure;
	}
}
