package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.Location;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.LocalSubComponent;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.ComponentType;
import org.sbolstandard.core3.vocabulary.Role;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Sequence;
import java.net.URI;
import java.util.Arrays;

import org.apache.jena.sparql.pfunction.library.seq;
import org.sbolstandard.converter.Util;

public class SequenceAnnotationToLocalSubComponent implements ChildEntityConverter<org.sbolstandard.core2.SequenceAnnotation,LocalSubComponent>{

	@Override
	public LocalSubComponent convert(SBOLDocument document, Identified parent, org.sbolstandard.core2.Identified seqaParent, SequenceAnnotation seqa, Parameters parameters)
	        throws SBOLGraphException {
	    
	    // Cast the parent object to a Component (SBOL3).
	    Component parentComponent = (Component) parent;
	    
	    ComponentDefinition parentCompDef = (ComponentDefinition) seqaParent;
	    
	    return getLocalSubComponent(document, parentComponent, parentCompDef, seqa, parameters);
	}
	
	private LocalSubComponent getLocalSubComponent(org.sbolstandard.core3.entity.SBOLDocument document,
			Component sbol3ParentComp, ComponentDefinition sbol2ParentCompDef, SequenceAnnotation seqa, Parameters parameters)
			throws SBOLGraphException {
		
		LocalSubComponent localSubComp=null;
						
		if (seqa.getDisplayId()!=null){
			localSubComp =sbol3ParentComp.createLocalSubComponent(seqa.getDisplayId(), Util.toSBOL3ComponentDefinitionTypes(Util.toList(sbol2ParentCompDef.getTypes())));
		}
		else{
			localSubComp =sbol3ParentComp.createLocalSubComponent(Util.toSBOL3ComponentDefinitionTypes(Util.toList(sbol2ParentCompDef.getTypes())));
		}
		parameters.addMapping(seqa.getIdentity(), localSubComp.getUri());		
		localSubComp.setRoles(Util.convertSORoles2_to_3(seqa.getRoles()));
		if ((seqa.getRoles()==null || seqa.getRoles().isEmpty()) && !localSubComp.getTypes().containsAll(Arrays.asList(ComponentType.DNA, ComponentType.RNA))){
			localSubComp.addRole(Role.EngineeredRegion);	
		}
		Util.copyIdentified(seqa, localSubComp, parameters);
		return localSubComp;
	}

	// A SequenceAnnotation is considered to be a local sub-component if it has a component reference and does not have any locations (i.e., it is not a cut or range). It can have no or generic locations only.
	public static boolean isLocalSubComponent(SequenceAnnotation seqa){
		if (seqa.getComponentURI()!=null){
			return false;
		}
		else{
			if (seqa.getLocations()!=null){
				for (Location loc: seqa.getLocations()){
					if (loc instanceof org.sbolstandard.core2.Cut){
						return false;
					}
					else if (loc instanceof org.sbolstandard.core2.Range){
						return false;
					}

				}
			}
		}
		return true;
	}
}
