package org.sbolstandard.converter.sbol23_31;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.RoleIntegrationType;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Interface;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.RoleIntegration;

public class ComponentConverter implements ChildEntityConverter<org.sbolstandard.core2.Component, Feature> {

	@Override
	public Feature convert(SBOLDocument doc, Identified parent, org.sbolstandard.core2.Identified inputParent, org.sbolstandard.core2.Component input, Parameters parameters) throws SBOLGraphException {
		Component parentComponent = (Component) parent;
		Feature result = null;
		if (input.getMapsTos() != null && !input.getMapsTos().isEmpty()) {
			// TODO
			throw new SBOLGraphException("Component with mapsTo not supported yet");
		}
		else {	//Create an SBOL3 SubComponent
			URI sbol3InstanceOfUri = Util.createSBOL3Uri(input.getDefinitionURI());
			SubComponent resultSC=null;
			if (input.getDisplayId()!=null)
			{
				resultSC= parentComponent.createSubComponent(input.getDisplayId(), sbol3InstanceOfUri);
			}
			else
			{
				resultSC= parentComponent.createSubComponent(sbol3InstanceOfUri);
			}
			parameters.addMapping(input.getIdentity(), resultSC.getUri());
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

		if (input.getAccess()==AccessType.PUBLIC)
		{
			Interface sbol3Interface = parentComponent.getInterface();
			if(sbol3Interface == null) {
				sbol3Interface=parentComponent.createInterface();
			}

				List<Feature> features=sbol3Interface.getNonDirectionals();
				if (features==null)
				{
					features=new ArrayList<>();
				}
				features.add(result);
				sbol3Interface.setNonDirectionals(features);
		}
		return result;

	}
}
