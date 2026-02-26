package org.sbolstandard.converter.sbol23_31;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.measurement.MeasurementConverter;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.Location;
import org.sbolstandard.core2.Measure;
import org.sbolstandard.core2.RoleIntegrationType;
import org.sbolstandard.core2.SBOLValidationException;
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
	public Feature convert(SBOLDocument doc, Identified parent, org.sbolstandard.core2.Identified inputParent, org.sbolstandard.core2.Component input, Parameters parameters) throws SBOLGraphException, SBOLValidationException {
		Component sbol3ParentComponent = (Component) parent;
		Feature result = null;
		URI sbol3InstanceOfUri = Util.createSBOL3Uri(input.getDefinitionURI(), parameters);
		SubComponent resultSC=null;
		if (input.getDisplayId()!=null){
			resultSC= sbol3ParentComponent.createSubComponent(input.getDisplayId(), sbol3InstanceOfUri);
		}
		else{
			resultSC= sbol3ParentComponent.createSubComponent(sbol3InstanceOfUri);
		}
		parameters.addMapping(input.getIdentity(), resultSC.getUri());
			
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

		// Measurement conversion
		if(input.getMeasures()!=null) {
			MeasurementConverter measurementConverter = new MeasurementConverter();
			for (Measure sbol2Measure : input.getMeasures()) {
				measurementConverter.convert(doc, result, input, sbol2Measure, parameters);
			}
		}
		
		Util.copyIdentified(input, result, parameters);

		if (input.getAccess()==AccessType.PUBLIC){
			Interface sbol3Interface = sbol3ParentComponent.getInterface();
			if(sbol3Interface == null) {
				sbol3Interface=sbol3ParentComponent.createInterface();
			}

			List<Feature> features=sbol3Interface.getNonDirectionals();
			if (features==null){
				features=new ArrayList<>();
			}
			features.add(result);
			sbol3Interface.setNonDirectionals(features);
		}		
	
		if (input.getSourceLocations()!=null){
			SourceLocationConverter locationConverter = new SourceLocationConverter();			
			for (Location loc : input.getSourceLocations()) {
				locationConverter.convert(doc, result, input, loc, parameters);
			}
		}
		return result;
	}

}