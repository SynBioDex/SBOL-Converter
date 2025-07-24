package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.ExperimentalData;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ExperimentalDataConverter implements EntityConverter<ExperimentalData, org.sbolstandard.core2.ExperimentalData>  {

    @Override
    public org.sbolstandard.core2.ExperimentalData convert(SBOLDocument doc, ExperimentalData input) throws SBOLGraphException, SBOLValidationException {    	
    	org.sbolstandard.core2.ExperimentalData exp = doc.createExperimentalData(Util.getURIPrefix(input), input.getDisplayId(), Util.getVersion(input));
		Util.copyIdentified(input, exp, doc);	
		return exp;
	}
}
