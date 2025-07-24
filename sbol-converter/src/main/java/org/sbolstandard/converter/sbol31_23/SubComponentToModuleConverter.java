package org.sbolstandard.converter.sbol31_23;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.DirectionType;
import org.sbolstandard.core2.FunctionalComponent;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Interface;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

// This converter maps an SBOL3 SubComponent to an SBOL2 FunctionalComponent
public class SubComponentToModuleConverter
		implements ChildEntityConverter<SubComponent, org.sbolstandard.core2.Module> {

	@Override
	public org.sbolstandard.core2.Module convert(SBOLDocument document,
			org.sbolstandard.core2.Identified parent, // Should be an SBOL2 ModuleDefinition
			Identified inputParent, // Should be the parent SBOL3 Component
			SubComponent sbol3SubComponent // The SBOL3 SubComponent to convert
	) throws SBOLGraphException, SBOLValidationException {

		
		org.sbolstandard.core2.ModuleDefinition sbol2ModuleDefinition = (org.sbolstandard.core2.ModuleDefinition) parent;


		Component sbol3ParentComponent = (Component) inputParent;

		URI sbol2ChildModuleDefUri = Util.createSBOL2Uri(sbol3SubComponent.getInstanceOf());

		// Assumed that DisplayId always exists for SubComponent

		if (sbol3SubComponent.getDisplayId() == null) {
			System.out.println("DISPLAY ID NULL");
		}
		org.sbolstandard.core2.Module module  = sbol2ModuleDefinition.createModule(sbol3SubComponent.getDisplayId(), sbol2ChildModuleDefUri);
		
		// Measures???

		return module; 
		
	}
}
