package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.provenance.Activity;
import org.sbolstandard.core3.entity.provenance.Agent;
import org.sbolstandard.core3.entity.provenance.Association;
import org.sbolstandard.core3.util.SBOLGraphException;

public class AssociationConverter implements ChildEntityConverter<org.sbolstandard.core2.Association, Association> {

	@Override
	public Association convert(SBOLDocument doc, Identified parent, org.sbolstandard.core2.Identified inputParent, org.sbolstandard.core2.Association sbol2Association, Parameters parameters) throws SBOLGraphException {
		
		Activity sbol3ParentActivity = (Activity) parent;

		org.sbolstandard.core2.Agent sbol2Agent = sbol2Association.getAgent();
		Agent sbol3Agent = doc.getIdentified(Util.createSBOL3Uri(sbol2Agent), Agent.class);

		Association sbol3Association = sbol3ParentActivity.createAssociation(Util.createSBOL3Uri(sbol2Association),sbol3Agent);

		sbol3Association.setRoles(Util.toList(sbol2Association.getRoles()));

		Util.copyIdentified(sbol2Association, sbol3Association, parameters);

		return sbol3Association;
	}

}