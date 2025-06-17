package org.sbolstandard.converter.sbol23_31;

import java.net.URI;

import org.sbolstandard.core2.MapsTo;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.ComponentReference;
import org.sbolstandard.core3.entity.Constraint;
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

		// ModuleDefinition sbol2ParentModuleDefinition = (ModuleDefinition)
		// sbol2Parent;
		org.sbolstandard.core3.entity.Component sbol3ParentComponent = (org.sbolstandard.core3.entity.Component) sbol3Parent;

		SubComponent sbol3SubComponentForModule = null;
		if (SBOLDocumentConverter.isCompliant()) {
			sbol3SubComponentForModule = sbol3ParentComponent.createSubComponent(sbol2Module.getIdentity(),
					sbol3ParentComponent);
		} else if (sbol2Module.getDisplayId() != null) {
			sbol3SubComponentForModule = sbol3ParentComponent.createSubComponent(sbol2Module.getDisplayId(),
					sbol3ParentComponent);
		} else {
			sbol3SubComponentForModule = sbol3ParentComponent.createSubComponent(sbol3ParentComponent);
			sbol3SubComponentForModule.addAnnotion(URI.create("http//sbolstandard.org/sbol-converter/23_to_31"),
					sbol2Module.getIdentity());
		}
		
		
		// MapsTo Conversion is lifted one level up
//		for (MapsTo mapsTo : sbol2Module.getMapsTos()) {
//
//			ComponentReference sbol3CompRef = MapstoToComponentReferenceConverter.convert(document, sbol2Module,
//					sbol3ParentComponent, mapsTo, sbol3SubComponentForModule);
//
//			Constraint sbol3Constraint = MapstoToConstraintConverter.convert(sbol3ParentComponent, mapsTo,
//					sbol3CompRef);
//
//			if (sbol3Constraint == null) {
//				// sbol3SubComponentForModule.addConstraint(sbol3Constraint);
//				System.out.println("Constraint is null for " + mapsTo.getIdentity() + ". This should not happen.");
//			}
//
//		}

		// MEASURES
		//System.out.println("ModuleToSubComponentConverter.convert() NOT IMPLEMENTED YET");

		return null;

	}

}
