package org.sbolstandard.converter.sbol31_23;

import java.net.URI;
import java.util.List;

import org.sbolstandard.converter.ConverterVocabulary;
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

		moduleDef.setRoles(Util.convertSORoles3_to_2(component.getRoles()));

		SubComponentToFunctionalComponentConverter subCompToFunCompConverter = new SubComponentToFunctionalComponentConverter();
		SubComponentToModuleConverter subCompToModuleConverter = new SubComponentToModuleConverter();

		if (component.getSubComponents() != null) {
			for (SubComponent subComponent : component.getSubComponents()) {
				if (Util.isModuleDefinition(subComponent.getInstanceOf())) {
					subCompToModuleConverter.convert(sbol2Doc, moduleDef, component, subComponent);
				} 
				else {
					if (isModule(subComponent)){
						subCompToModuleConverter.convert(sbol2Doc, moduleDef, component, subComponent);
					}
					else{
						subCompToFunCompConverter.convert(sbol2Doc, moduleDef, component, subComponent);
					}
				}
			}
		}

		InteractionConverter interactionConverter = new InteractionConverter();

		if (component.getInteractions() != null) {
			for (org.sbolstandard.core3.entity.Interaction interaction : component.getInteractions()) {
				interactionConverter.convert(sbol2Doc, moduleDef, component, interaction);
			}
		}

		if (component.getModelURIs() != null) {
			for (URI modelURI : component.getModelURIs()) {
				moduleDef.addModel(Util.createSBOL2Uri(modelURI));
			}
		}

		return moduleDef;
	}

	/**
	 * Checks whether the given SubComponent is converted from an SBOL2 Module by checking a previous annotation.
	 * @param subComponent
	 * @return
	 * @throws SBOLGraphException
	 */
	private boolean isModule(SubComponent subComponent) throws SBOLGraphException {
		boolean isFromModule = false;
		List<Object> values = subComponent.getAnnotation(ConverterVocabulary.Two_to_Three.sbol2OriginatesFromModule);
		if (values != null && !values.isEmpty()) {
			String value = values.get(0).toString();
			if (value.equalsIgnoreCase("true")) {
				isFromModule = true;
			}
		}
		return isFromModule;
	}
	/*
	 * private boolean isModule(SubComponent subComponent) throws SBOLGraphException
	 * {
	 * if(subComponent.getInstanceOf()!= null &&
	 * subComponent.getInstanceOf().getInteractions() != null){
	 * return true;
	 * }
	 * else if (Util.isModuleDefinition(subComponent, subComponent.getInstanceOf()))
	 * {
	 * return true;
	 * }
	 * return false;
	 * }
	 */
}
