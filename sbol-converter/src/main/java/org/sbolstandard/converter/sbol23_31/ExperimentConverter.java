package org.sbolstandard.converter.sbol23_31;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Experiment;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ExperimentConverter implements EntityConverter<org.sbolstandard.core2.Experiment, Experiment>  {

    @Override
    public Experiment convert(SBOLDocument doc, org.sbolstandard.core2.Experiment input, Parameters parameters) throws SBOLGraphException {      	
		Experiment exp = doc.createExperiment(Util.createSBOL3Uri(input), Util.getNamespace(input));
		Util.copyIdentified(input, exp);
		List<URI> members = new ArrayList<URI>();
		if (input.getExperimentalData() != null) {
			for (URI expDataUri : input.getExperimentalDataURIs()) {
				members.add(Util.createSBOL3Uri(expDataUri, parameters));
			}
		}
		exp.setMembers(members);
		return exp;
	}
}
