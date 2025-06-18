package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.MapsTo;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.ComponentReference;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class MapstoToComponentReferenceConverter {

	public static ComponentReference convertForModule(SBOLDocument document, org.sbolstandard.core2.Module sbol2Module,
			Component sbol3ParentComponent, MapsTo mapsTo, SubComponent sbol3SubComponentForModule)
			throws SBOLGraphException, SBOLValidationException {

		ModuleDefinition childModuleDefinition = sbol2Module.getDefinition();

		Component childComponent = Util.getSBOL3Entity(document.getComponents(), childModuleDefinition);
		SubComponent sbol3RemoteSubComponent=null;
		try
		{
		 sbol3RemoteSubComponent = Util.getSBOL3Entity(childComponent.getSubComponents(),
				mapsTo.getRemote());
		}
		catch (Exception e)
		{
			String str="khbk";
			str="xcgxg";
		}
		//System.out.println("sbol3RemoteSubComponent: " + sbol3RemoteSubComponent.getUri());
		//System.out.println("sbol3ParentComponent: " + sbol3ParentComponent.getUri());
		//System.out.println("sbol3SubComponentForModule: " + sbol3SubComponentForModule.getUri());
		ComponentReference sbol3CompRef = sbol3ParentComponent.createComponentReference(sbol3RemoteSubComponent,
				sbol3SubComponentForModule);

		return sbol3CompRef;

	}
	//TODO: SAME THING FOR COMPONENT CONVERTER
	public static ComponentReference convertForComponent(SBOLDocument document, org.sbolstandard.core2.ComponentInstance sbol2ComponentInstance,
			Component sbol3ParentComponent, MapsTo mapsTo, SubComponent sbol3SubComponentForModule)
			throws SBOLGraphException, SBOLValidationException {

		ComponentDefinition childModuleDefinition = sbol2ComponentInstance.getDefinition();

		Component childComponent = Util.getSBOL3Entity(document.getComponents(), childModuleDefinition);

		SubComponent sbol3RemoteSubComponent = Util.getSBOL3Entity(childComponent.getSubComponents(),
				mapsTo.getRemote());
		ComponentReference sbol3CompRef = sbol3ParentComponent.createComponentReference(sbol3RemoteSubComponent,
				sbol3SubComponentForModule);
		// TODO: CHECK sbol3SubComponentForModule is true
		
		System.out.println("sbol3RemoteSubComponent: " + sbol3RemoteSubComponent.getUri());
		System.out.println("sbol3ParentComponent: " + sbol3ParentComponent.getUri());
		System.out.println("sbol3SubComponentForModule: " + sbol3SubComponentForModule.getUri());

		return sbol3CompRef;

	}
	

}
