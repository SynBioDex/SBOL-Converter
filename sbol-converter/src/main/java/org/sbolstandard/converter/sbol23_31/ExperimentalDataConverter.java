package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.ExperimentalData;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ExperimentalDataConverter implements EntityConverter<org.sbolstandard.core2.ExperimentalData, ExperimentalData>  {

    @Override
    public ExperimentalData convert(SBOLDocument doc, org.sbolstandard.core2.ExperimentalData input, Parameters parameters) throws SBOLGraphException {    	
    	ExperimentalData exp = doc.createExperimentalData(Util.createSBOL3Uri(input), Util.getNamespace(input));
		Util.copyIdentified(input, exp, parameters);	
		return exp;
	}
}
