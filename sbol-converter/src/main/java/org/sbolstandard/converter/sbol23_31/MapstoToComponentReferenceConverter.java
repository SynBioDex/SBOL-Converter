package org.sbolstandard.converter.sbol23_31;

import java.net.URI;
import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.MapsTo;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.ComponentReference;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class MapstoToComponentReferenceConverter {

	public static ComponentReference convertForModule(SBOLDocument document, org.sbolstandard.core2.Module sbol2Module,
			Component sbol3ParentComponent, MapsTo mapsTo, SubComponent sbol3SubComponentForModule, Parameters parameters)
			throws SBOLGraphException, SBOLValidationException {
		
		URI sbol3ChildComponentURI = Util.createSBOL3Uri(sbol2Module.getDefinitionURI(), parameters);		
		Component childComponent=document.getIdentified(sbol3ChildComponentURI, Component.class);						
		SubComponent sbol3RemoteSubComponent=null;
		ComponentReference sbol3CompRef =null;
		if (childComponent!=null){
			sbol3RemoteSubComponent = Util.getSBOL3Entity(childComponent.getSubComponents(), mapsTo.getRemoteURI(), parameters);	
			sbol3CompRef = sbol3ParentComponent.createComponentReference(mapsTo.getDisplayId(), sbol3RemoteSubComponent, sbol3SubComponentForModule);
			Util.copyIdentified(mapsTo, sbol3CompRef, parameters);
		}
		else{
			String message="***CONVERSION ERROR***: Not converting the sbol2:MapsTo into an sbol:ComponentReference. An sbol3:Feature as a child entity of a sbol:Component is required to create an sbol3:ComponentReference. However, there is no sbol3:Component for" +  sbol3ChildComponentURI + ". Can't refer to a child entity if it is not included.";
			System.out.println(message);
		}
		return sbol3CompRef;
	}

	public static ComponentReference convertForComponent(SBOLDocument document, org.sbolstandard.core2.ComponentInstance sbol2ComponentInstance,
			Component sbol3ParentComponent, MapsTo mapsTo, SubComponent sbol3SubComponentForModule, Parameters parameters)	
			throws SBOLGraphException, SBOLValidationException {

		ComponentDefinition childModuleDefinition = sbol2ComponentInstance.getDefinition();		
		URI childComponentURI = Util.createSBOL3Uri(childModuleDefinition);
		Component childComponent=document.getIdentified(childComponentURI, Component.class);					
		SubComponent sbol3RemoteSubComponent = Util.getSBOL3Entity(childComponent.getSubComponents(), mapsTo.getRemote(), parameters);
		ComponentReference sbol3CompRef = sbol3ParentComponent.createComponentReference(mapsTo.getDisplayId(), sbol3RemoteSubComponent,sbol3SubComponentForModule);		
		Util.copyIdentified(mapsTo, sbol3CompRef, parameters);
		return sbol3CompRef;
	}
	
}
