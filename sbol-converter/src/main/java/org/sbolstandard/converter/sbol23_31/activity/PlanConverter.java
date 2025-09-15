package org.sbolstandard.converter.sbol23_31.activity;


import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.EntityConverter;
import org.sbolstandard.converter.sbol23_31.Parameters;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.provenance.Plan;
import org.sbolstandard.core3.util.SBOLGraphException;

public class PlanConverter implements EntityConverter<org.sbolstandard.core2.Plan, Plan> {

	@Override
	public Plan convert(SBOLDocument doc, org.sbolstandard.core2.Plan input, Parameters parameters) throws SBOLGraphException {
		Plan plan = doc.createPlan(Util.createSBOL3Uri(input), Util.getNamespace(input));
		
		Util.copyIdentified(input, plan, parameters);
		return plan;
	} 

}
