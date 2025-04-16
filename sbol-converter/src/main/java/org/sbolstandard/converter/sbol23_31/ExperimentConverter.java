package org.sbolstandard.converter.sbol23_31;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ExperimentalData;
import org.sbolstandard.core3.entity.Experiment;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ExperimentConverter implements EntityConverter<org.sbolstandard.core2.Experiment, Experiment>  {

    @Override
    public Experiment convert(SBOLDocument doc, org.sbolstandard.core2.Experiment input) throws SBOLGraphException {      	
		Experiment exp = doc.createExperiment(Util.createSBOL3Uri(input), Util.getNamespace(input));
		Util.copyIdentified(input, exp);
		List<URI> members = new ArrayList<URI>();
		if (input.getExperimentalData() != null) {
			for (ExperimentalData expData : input.getExperimentalData()) {
				members.add(Util.createSBOL3Uri(expData));
			}
		}
		exp.setMembers(members);
		return exp;
	}
}
