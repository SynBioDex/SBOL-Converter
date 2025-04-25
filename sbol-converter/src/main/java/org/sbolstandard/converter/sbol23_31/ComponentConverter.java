package org.sbolstandard.converter.sbol23_31;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.RoleIntegrationType;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.io.SBOLFormat;
import org.sbolstandard.core3.io.SBOLIO;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.RoleIntegration;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.Identified;

public class ComponentConverter implements ChildEntityConverter<org.sbolstandard.core2.Component, Feature> {

	@Override
	public Feature convert(SBOLDocument doc, Identified parent, org.sbolstandard.core2.Component input) throws SBOLGraphException {
		Component parentComponent = (Component) parent;
		Feature result = null;
		if (input.getMapsTos() != null && !input.getMapsTos().isEmpty()) {
			// TODO
			throw new SBOLGraphException("Component with mapsTo not supported yet");
		} 
		else {	//Create an SBOL3 SubComponent		
			URI sbol3URI = Util.createSBOL3Uri(input.getIdentity());
			URI sbol3InstanceOfUri = Util.createSBOL3Uri(input.getDefinitionURI());
			SubComponent resultSC = parentComponent.createSubComponent(sbol3URI, sbol3InstanceOfUri);
			// TODO: Check for the Access Type
			// TODO Add measures
			resultSC.setRoles(Util.toList(input.getRoles()));

			RoleIntegrationType ri = input.getRoleIntegration();
			if (ri != null) {
				RoleIntegration risbol3 = RoleIntegration.mergeRoles;

				if (ri.equals(RoleIntegrationType.MERGEROLES)) {
					risbol3 = RoleIntegration.mergeRoles;
				} else if (ri.equals(RoleIntegrationType.OVERRIDEROLES)) {
					risbol3 = RoleIntegration.overrideRoles;
				}
				resultSC.setRoleIntegration(risbol3);
			}
			result = resultSC;
		}
		Util.copyIdentified(input, result);
		return result;

	}
}
