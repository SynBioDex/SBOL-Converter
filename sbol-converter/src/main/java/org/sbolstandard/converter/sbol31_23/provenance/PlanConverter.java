package org.sbolstandard.converter.sbol31_23.provenance;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol31_23.EntityConverter;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.provenance.Plan;
import org.sbolstandard.core3.util.SBOLGraphException;

public class PlanConverter implements EntityConverter<Plan, org.sbolstandard.core2.Plan>  {

    @Override
    public org.sbolstandard.core2.Plan convert(SBOLDocument doc, Plan input) throws SBOLGraphException, SBOLValidationException {
    	org.sbolstandard.core2.Plan sbol2Plan;
    	sbol2Plan = doc.createPlan(Util.getURIPrefix(input),input.getDisplayId(),Util.getVersion(input));
    	Util.copyIdentified(input, sbol2Plan, doc);
        return sbol2Plan;
    }
}
