package org.sbolstandard.converter.sbol23_31;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.RoleIntegrationType;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.ComponentReference;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Interface;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.RoleIntegration;

public class ComponentConverter implements ChildEntityConverter<org.sbolstandard.core2.Component, Feature> {

	@Override
	public Feature convert(SBOLDocument document, Identified parent, org.sbolstandard.core2.Identified inputParent, org.sbolstandard.core2.Component input) throws SBOLGraphException {
		Component sbol3ParentComponent = (Component) parent;
		Feature result = null;
		
		//Create an SBOL3 SubComponent
		URI sbol3URI = Util.createSBOL3Uri(input.getIdentity());
		URI sbol3InstanceOfUri = Util.createSBOL3Uri(input.getDefinitionURI());
		SubComponent resultSC = sbol3ParentComponent.createSubComponent(sbol3URI, sbol3InstanceOfUri);
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
		
		Util.copyIdentified(input, result);

		if (input.getAccess()==AccessType.PUBLIC)
		{
			Interface sbol3Interface = sbol3ParentComponent.getInterface();
			if(sbol3Interface == null) {
				sbol3Interface=sbol3ParentComponent.createInterface();
			}

				List<Feature> features=sbol3Interface.getNonDirectionals();
				if (features==null)
				{
					features=new ArrayList<>();
				}
				features.add(result);
				sbol3Interface.setNonDirectionals(features);
		}
		if (input.getMapsTos() != null && !input.getMapsTos().isEmpty()) {
			// TODO
			//throw new SBOLGraphException("Component with mapsTo not supported yet");
			System.out.println("Component mapsTos not supported yet. This is a temporary message.");
//			SubComponent sbol3SubComponentForComponentInstance 
//			// TODO: CHECK HERE IT IS WRONG FOR COMPONENT CONVERTER
//			// TODO: MAKE IT GENERIC FOR ALL CONVERTERS
//			for (org.sbolstandard.core2.MapsTo mapsTo : input.getMapsTos()) {
//				// Convert MapsTo to ComponentReference
//				ComponentReference sbol3CompRef = MapstoToComponentReferenceConverter.convertForComponent(document, sbol2Module,
//						sbol3ParentComponent, mapsTo, sbol3SubComponentForComponentInstance);
//
//				Constraint sbol3Constraint = MapstoToConstraintConverter.convert(sbol3ParentComponent, mapsTo,
//						sbol3CompRef);
//
//				if (sbol3Constraint == null) {
//					// sbol3SubComponentForModule.addConstraint(sbol3Constraint);
//					System.out.println("Constraint is null for " + mapsTo.getIdentity() + ". This should not happen.");
//				}
//				
//			}
			
			
		}
		
		
		return result;

	}
}
