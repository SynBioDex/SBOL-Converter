package org.sbolstandard.converter.sbol31_23.provenance;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol31_23.ChildEntityConverter;
import org.sbolstandard.core2.Activity;
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

				URI sbol3AgentURI = sbol3Association.getAgentURI();
				URI sbol2AgentURI = Util.createSBOL2Uri(sbol3AgentURI);
								
				org.sbolstandard.core2.Association sbol2Association = sbol2ParentActivity.createAssociation(sbol3Association.getDisplayId(), sbol2AgentURI);

				URI sbol3PlanURI = sbol3Association.getPlanURI();
				if (sbol3PlanURI	!= null) {
					URI sbol2PlanURI = Util.createSBOL2Uri(sbol3PlanURI);
					sbol2Association.setPlan(sbol2PlanURI);
				}			

				if(sbol3Association.getRoles()!=null){
					sbol2Association.setRoles(Util.toSet(sbol3Association.getRoles()));
				}
			
				Util.copyIdentified(sbol3Association, sbol2Association, document);

				return sbol2Association;

	}

}
