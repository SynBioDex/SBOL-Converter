package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

import java.net.URI;

import org.sbolstandard.converter.Util;

public class FunctionalComponentToSubComponentConverter
		implements ChildEntityConverter<org.sbolstandard.core2.FunctionalComponent, SubComponent> {

	@Override
	public SubComponent convert(SBOLDocument document, Identified sbol3Parent,
			org.sbolstandard.core2.Identified sbol2Parent,
			org.sbolstandard.core2.FunctionalComponent sbol2FunctionalComponent, Parameters parameters)
			throws SBOLGraphException, SBOLValidationException {

		SubComponent sbol3SubComponent = null;

		Component sbol3ParentComponent = (Component) sbol3Parent;

		// ModuleDefinition sbol2ParentModuleDefinition = (ModuleDefinition)
		// sbol2Parent;

		URI sbol3ChildComponentURI = Util.createSBOL3Uri(sbol2FunctionalComponent.getDefinition().getIdentity());
		Component sbol3ChildComponent = document.getIdentified(sbol3ChildComponentURI, Component.class);
		if (sbol3ChildComponent == null) {
			throw new SBOLGraphException("Could not find Component for FunctionalComponent: "
					+ sbol2FunctionalComponent.getDefinition().getIdentity());
		}
		
		//sbol3SubComponent = sbol3ParentComponent.createSubComponent(sbol3ChildComponent);

		if (SBOLDocumentConverter.isCompliant()){
			sbol3SubComponent = sbol3ParentComponent.createSubComponent(sbol2FunctionalComponent.getIdentity(), sbol3ChildComponent);
		}
		else{
			sbol3SubComponent = sbol3ParentComponent.createSubComponent(sbol2FunctionalComponent.getDisplayId(), sbol3ChildComponent);
			
		}
		sbol3SubComponent.setDisplayId(sbol2FunctionalComponent.getDisplayId());

		Util.copyIdentified(sbol2FunctionalComponent, sbol3SubComponent);

		return sbol3SubComponent;

	}

}
