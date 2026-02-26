package org.sbolstandard.converter.sbol23_31.provenance;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.ChildEntityConverter;
import org.sbolstandard.converter.sbol23_31.Parameters;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.provenance.Activity;
import org.sbolstandard.core3.entity.provenance.Association;
import org.sbolstandard.core3.util.SBOLGraphException;

public class AssociationConverter implements ChildEntityConverter<org.sbolstandard.core2.Association, Association> {

	@Override
	public Association convert(SBOLDocument doc, Identified parent, org.sbolstandard.core2.Identified inputParent, org.sbolstandard.core2.Association sbol2Association, Parameters parameters) throws SBOLGraphException {
		
		Activity sbol3ParentActivity = (Activity) parent;

		URI sbol2AgentURI = sbol2Association.getAgentURI();
		URI sbol3AgentURI = Util.createSBOL3Uri(sbol2AgentURI, parameters);
		
		Association sbol3Association = sbol3ParentActivity.createAssociation(sbol2Association.getDisplayId(), sbol3AgentURI);
		
		if(sbol2Association.getPlanURI() != null) {
			URI sbol3PlanURI = Util.createSBOL3Uri(sbol2Association.getPlanURI(), parameters);
			sbol3Association.setPlan(sbol3PlanURI);
		}

		sbol3Association.setRoles(Util.toList(sbol2Association.getRoles()));

		Util.copyIdentified(sbol2Association, sbol3Association, parameters);

		return sbol3Association;
	}

}