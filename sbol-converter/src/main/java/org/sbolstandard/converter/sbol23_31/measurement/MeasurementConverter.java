package org.sbolstandard.converter.sbol23_31.measurement;

import java.net.URI;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.ChildEntityConverter;
import org.sbolstandard.converter.sbol23_31.Parameters;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.api.SBOLAPI;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.measure.*;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.MeasureDataModel;;

public class MeasurementConverter implements ChildEntityConverter<org.sbolstandard.core2.Measure,Measure> {

	@Override
	public Measure convert(SBOLDocument document, Identified sbol3Parent, org.sbolstandard.core2.Identified sbol2Parent, org.sbolstandard.core2.Measure sbol2Measure, Parameters parameters) throws SBOLGraphException, SBOLValidationException {
        
		Measure sbol3Measure =null;
        Double dbl = sbol2Measure.getNumericalValue();        
        Float value = dbl != null ? dbl.floatValue() : null;
		URI unitURI=Util.createSBOL3Uri(sbol2Measure.getUnitURI(), parameters);
		if (sbol2Measure.getDisplayId()!=null){
			sbol3Measure= sbol3Parent.createMeasure(sbol2Measure.getDisplayId(), value, unitURI);
        }
		else{
            String name=SBOLAPI.createLocalName(MeasureDataModel.Measure.uri,sbol3Parent.getMeasures());
            sbol3Measure= sbol3Parent.createMeasure(name,value, unitURI);
			parameters.addMapping(sbol2Measure.getIdentity(), sbol3Measure.getUri());					
		} 
		
		if(sbol2Measure.getTypes()!=null) {
			List<URI> sbol3Types=Util.convertToSBOL3_SBO_URIs(sbol2Measure.getTypes());
			sbol3Measure.setTypes(sbol3Types);
		}

		Util.copyIdentified(sbol2Measure, sbol3Measure, parameters);
        return sbol3Measure;
    }
}