package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ComponentToModuleDefinitionConverter implements EntityConverter<Component, ModuleDefinition> {

	@Override
	public ModuleDefinition convert(SBOLDocument sbol2Doc, Component component)
			throws SBOLGraphException, SBOLValidationException {

		ModuleDefinition moduleDef = sbol2Doc.createModuleDefinition(Util.getURIPrefix(component),
				component.getDisplayId(), Util.getVersion(component));

		Util.copyIdentified(component, moduleDef, sbol2Doc);

		// TODO
		// THINGS NEED TO BE CONVERTED
		// roles
		moduleDef.setRoles(Util.convertRoles3_to_2(component.getRoles()));

		// MODULES
		// HOW TO CONVERT MODULES?
		// TODO: SOME OF THE SUBCOMPONENTS SHOULD BE CONVERTED INTO MODULES
		// SUB COMPONENT TO FUNCTIONAL COMPONENTS
		SubComponentToFunctionalComponentConverter subCompToFunCompConverter = new SubComponentToFunctionalComponentConverter();
		SubComponentToModuleConverter subCompToModuleConverter = new SubComponentToModuleConverter();

		// TODO: CHECK HERE
		for (SubComponent subComponent : component.getSubComponents()) {
			if(subComponent.getInstanceOf()!= null && subComponent.getInstanceOf().getSubComponents() != null)
			{
				subCompToModuleConverter.convert(sbol2Doc, moduleDef, component, subComponent);
			}
			else
			{
				subCompToFunCompConverter.convert(sbol2Doc, moduleDef, component, subComponent);
			}
		}
		// INTERACTIONS
		InteractionConverter interactionConverter = new InteractionConverter();

		// Can Interactions be null?
		if (component.getInteractions() == null) {
			System.out.println("Component " + component.getUri() + " has no interactions.");
		}
		else{
			for (org.sbolstandard.core3.entity.Interaction interaction : component.getInteractions()) {
				interactionConverter.convert(sbol2Doc, moduleDef, component, interaction);
			}
		}
		
		return moduleDef;
	}

}
