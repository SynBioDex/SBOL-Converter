package org.sbolstandard.converter.sbol31_23;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Experiment;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;

public class ExperimentConverter implements EntityConverter<Experiment, org.sbolstandard.core2.Experiment>  {

    @Override
    public  org.sbolstandard.core2.Experiment convert(SBOLDocument doc, Experiment input) throws SBOLGraphException, SBOLValidationException {      	
    	 org.sbolstandard.core2.Experiment exp = doc.createExperiment( Util.getURIPrefix(input), input.getDisplayId(), Util.getVersion(input) );
		Util.copyIdentified(input, exp, doc);
		if (input.getMembers() != null) {
			for (URI uri : input.getMembers()) {
				exp.addExperimentalData(Util.createSBOL2Uri(uri));//TODO: update URIs
			}
		}
		return exp;
	}
}
