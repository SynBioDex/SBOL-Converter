package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.Activity;
import org.sbolstandard.core2.Agent;
import org.sbolstandard.core2.Identified;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.provenance.Association;

public class AssociationConverter implements ChildEntityConverter<Association, org.sbolstandard.core2.Association>  {

	@Override
	public org.sbolstandard.core2.Association convert(SBOLDocument document, Identified parent, org.sbolstandard.core3.entity.Identified sbol3Parent, Association sbol3Association)
			throws SBOLGraphException, SBOLValidationException, SBOLValidationException {
				
				Activity sbol2ParentActivity = (Activity) parent;

				org.sbolstandard.core3.entity.provenance.Agent sbol3Agent = sbol3Association.getAgent();
				
				Agent sbol2Agent = document.getAgent(Util.createSBOL2Uri(sbol3Agent));
				
				// TODO: SBOL3 Association DISPLAY ID CHECK
				org.sbolstandard.core2.Association sbol2Association = sbol2ParentActivity.createAssociation(sbol3Association.getDisplayId(), sbol2Agent.getIdentity());
				
				if(sbol3Association.getRoles()!=null){
					sbol2Association.setRoles(Util.toSet(sbol3Association.getRoles()));
				}
			
				Util.copyIdentified(sbol3Association, sbol2Association, document);

				return sbol2Association;

	}

}
