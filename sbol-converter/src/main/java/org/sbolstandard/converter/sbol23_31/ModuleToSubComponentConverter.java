package org.sbolstandard.converter.sbol23_31;

import java.net.URI;



import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ModuleToSubComponentConverter
		implements ChildEntityConverter<org.sbolstandard.core2.Module, SubComponent> {

	@Override
	public SubComponent convert(SBOLDocument document, Identified sbol3Parent,
			org.sbolstandard.core2.Identified sbol2Parent, org.sbolstandard.core2.Module sbol2Module, Parameters parameters)
			throws SBOLGraphException, SBOLValidationException {

		
		org.sbolstandard.core3.entity.Component sbol3ParentComponent = (org.sbolstandard.core3.entity.Component) sbol3Parent;

		URI sbol3ChildComponentURI=Util.createSBOL3Uri(sbol2Module.getDefinition());
		//System.out.println("sbol3ChildComponentURI: " + sbol3ChildComponentURI + " sbol2Module.getDef.getUri: " + sbol2Module.getDefinition().getIdentity());

		SubComponent sbol3SubComponentForModule = null;
		if (sbol2Module.getDisplayId() != null) {
			sbol3SubComponentForModule = sbol3ParentComponent.createSubComponent(sbol2Module.getDisplayId(),
					sbol3ChildComponentURI);
		} else {
			sbol3SubComponentForModule = sbol3ParentComponent.createSubComponent(sbol3ChildComponentURI);
			parameters.addMapping(sbol2Module.getIdentity(), sbol3SubComponentForModule.getUri());
			//sbol3SubComponentForModule.addAnnotion(URI.create("http//sbolstandard.org/sbol-converter/23_to_31"),sbol2Module.getIdentity());
		}
		
		// MEASURES
		//System.out.println("ModuleToSubComponentConverter.convert() NOT IMPLEMENTED YET");

		return sbol3SubComponentForModule;

	}

}
