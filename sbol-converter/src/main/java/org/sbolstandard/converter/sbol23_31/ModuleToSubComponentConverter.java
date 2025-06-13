package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ModuleToSubComponentConverter
		implements ChildEntityConverter<org.sbolstandard.core2.Module, SubComponent> {

	@Override
	public SubComponent convert(SBOLDocument document, Identified sbol3Parent,
			org.sbolstandard.core2.Identified sbol2Parent, org.sbolstandard.core2.Module sbol2Module)
			throws SBOLGraphException, SBOLValidationException {

		ModuleDefinition sbol2ParentModuleDefinition = (ModuleDefinition) sbol2Parent;
		org.sbolstandard.core3.entity.Component sbol3ParentComponent = (org.sbolstandard.core3.entity.Component) sbol3Parent;
		
		
		
		// TODO:
		// MAPS TOS
		
		
		
		// MEASURES

		System.out.println("ModuleToSubComponentConverter.convert() NOT IMPLEMENTED YET");

		return null;

	}

}
