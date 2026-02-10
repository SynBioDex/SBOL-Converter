package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.Component;
import org.sbolstandard.core2.ComponentDefinition;

public class SourceLocationConverter  implements ChildEntityConverter<org.sbolstandard.core2.Location,Location>{

	//@Override
	public Location convert(SBOLDocument document, Identified parent, org.sbolstandard.core2.Identified sbol2Parent, org.sbolstandard.core2.Location sbol2Loc, Parameters parameters)
	        throws SBOLGraphException {
	    
	    SubComponent sbol3SeqFeature = (SubComponent) parent;
		Component sbol2ParentComponent = (Component) sbol2Parent;
	    Location sbol3Location = null;
		org.sbolstandard.core3.vocabulary.Orientation sbol3Orientation = Util.toSBOL3Orientation(sbol2Loc.getOrientation());

		ComponentDefinition sbol2InstanceOf=sbol2ParentComponent.getDefinition();

		Sequence sbol3Sequence = Util.getSBOL3SequenceFromSBOl2Parent(document, sbol2InstanceOf, parameters);
	
			if (sbol2Loc instanceof org.sbolstandard.core2.Range) {
				org.sbolstandard.core2.Range sbol2range = (org.sbolstandard.core2.Range) sbol2Loc;
				if (sbol2Loc.getDisplayId()!=null) {
					sbol3Location = sbol3SeqFeature.createSourceRange(sbol2Loc.getDisplayId(), sbol2range.getStart(), sbol2range.getEnd(), sbol3Sequence);								
				}
				else{
					sbol3Location = sbol3SeqFeature.createSourceRange(sbol2range.getStart(), sbol2range.getEnd(), sbol3Sequence);													
				}
			}
			else if (sbol2Loc instanceof org.sbolstandard.core2.Cut) {
				org.sbolstandard.core2.Cut sbol2cut = (org.sbolstandard.core2.Cut) sbol2Loc;
				if (sbol2Loc.getDisplayId()!=null) {					
					sbol3Location = sbol3SeqFeature.createSourceCut(sbol2Loc.getDisplayId(), sbol2cut.getAt(), sbol3Sequence);
				}
				else{
					sbol3Location = sbol3SeqFeature.createSourceCut(sbol2cut.getAt(), sbol3Sequence);
				}
			}
			else if (sbol2Loc instanceof org.sbolstandard.core2.GenericLocation) {
				if (sbol2Loc.getDisplayId()!=null) {										
					sbol3Location = sbol3SeqFeature.createSourceEntireSequence(sbol2Loc.getDisplayId(),  sbol3Sequence);	
				}
				else{
					sbol3Location = sbol3SeqFeature.createSourceEntireSequence(sbol3Sequence);
				}									
			}
			// Set the orientation for the SequenceFeature after each location is processed.
			if (sbol3Location != null) {
				if (sbol3Orientation!=null){
					sbol3Location.setOrientation(sbol3Orientation);
				}
				Util.copyIdentified(sbol2Loc, sbol3Location, parameters);
				parameters.addMapping(sbol2Loc.getIdentity(), sbol3Location.getUri());		
			}
			return sbol3Location;
	}
	


}
