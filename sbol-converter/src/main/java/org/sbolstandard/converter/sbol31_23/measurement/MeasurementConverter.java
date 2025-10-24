package org.sbolstandard.converter.sbol31_23.measurement;

import java.net.URI;
import java.util.List;
import java.util.Set;

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
		//Double valueDouble = value != null ? value.doubleValue() : null;
		Double valueDouble=null;
		if (value!=null){
			valueDouble= value.doubleValue();//This causes issues due the prevision in float vs double
			//As a workaround, convert float to string and parse it back to double
			double temp=Double.parseDouble(value.toString());
			valueDouble=Double.valueOf(temp);
		}
		if (valueDouble!=null){
			if (sbol2Parent instanceof org.sbolstandard.core2.Measured) {
				org.sbolstandard.core2.Measured sbol2ParentMeasured = (org.sbolstandard.core2.Measured) sbol2Parent;
				URI sbol3UnitURI = sbol3Measure.getUnitURI();
				URI sbol2UnitURI= Util.createSBOL2Uri(sbol3UnitURI);
				sbol2measure = sbol2ParentMeasured.createMeasure(sbol3Measure.getDisplayId(),valueDouble, sbol2UnitURI);
			}
			if (sbol2measure!=null){
				if (sbol3Measure.getTypes()!=null){
					Set<URI> sbol2Types=Util.convertToSBOL2_SBO_URIs(sbol3Measure.getTypes());
					sbol2measure.setTypes(sbol2Types);
				}				
				Util.copyIdentified(sbol3Measure, sbol2measure, doc);
			}
		}
		//Double t=(double) value;


		return sbol2measure;
	}
}
