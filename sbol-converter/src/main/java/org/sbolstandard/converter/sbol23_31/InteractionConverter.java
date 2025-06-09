package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core2.FunctionalComponent;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Interaction;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Feature;

import java.net.URI;
import java.util.List;

import org.sbolstandard.converter.Util;

public class InteractionConverter implements ChildEntityConverter<org.sbolstandard.core2.Interaction, Interaction> {

	@Override
	public Interaction convert(SBOLDocument document, Identified sbol3Parent,
			org.sbolstandard.core2.Identified sbol2Parent, org.sbolstandard.core2.Interaction sbol2Interaction)
			throws SBOLGraphException, SBOLValidationException {

		
		
		Component sbol3ParentComp = (Component) sbol3Parent;
		Interaction sbol3Interaction = sbol3ParentComp.createInteraction(Util.convertToSBOL3_SBO_URIs(sbol2Interaction.getTypes()));
		
	
		// Participation conversion
		for(org.sbolstandard.core2.Participation sbol2Participation : sbol2Interaction.getParticipations()) {
			
			FunctionalComponent sbol2FuncCom=sbol2Participation.getParticipant();
			
			URI sbol3ParticipantFeatureUri = Util.createSBOL3Uri(sbol2FuncCom.getIdentity());
			Feature sbol3Feature=document.getIdentified(sbol3ParticipantFeatureUri, Feature.getSubClassTypes());
			
			sbol3Feature = getByDisplayId(sbol3ParentComp.getFeatures(), sbol2FuncCom.getDisplayId());
			
			
			if (sbol3Feature == null) {
				throw new SBOLGraphException("Could not find Feature for FunctionalComponent: " + sbol2FuncCom.getIdentity());
			}
			sbol3Interaction.createParticipation(Util.convertToSBOL3_SBO_URIs(sbol2Participation.getRoles()), sbol3Feature); //HOW TO GET FEATURES?
	
		}
		Util.copyIdentified(sbol2Interaction, sbol3Interaction);
		
	    

		return sbol3Interaction;

	}
	
	private <T extends Identified> T getByDisplayId(List<T> list, String displayId) throws SBOLGraphException {
		for (T item : list) {
			if (item.getDisplayId().equals(displayId)) {
				return item;
			}
		}
		return null;
	}
	

}
