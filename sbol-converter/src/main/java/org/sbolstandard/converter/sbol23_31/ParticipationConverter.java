package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Interaction;
import org.sbolstandard.core3.entity.Participation;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.entity.Component;


import org.sbolstandard.converter.Util;

public class ParticipationConverter implements ChildEntityConverter<org.sbolstandard.core2.Participation, Participation> {

	@Override
	public Participation convert(SBOLDocument document, Identified sbol3Parent,
			org.sbolstandard.core2.Identified sbol2Parent, org.sbolstandard.core2.Participation sbol2Participation)
			throws SBOLGraphException, SBOLValidationException {

		
		return null; //TODO: PARTICIPATION CONVERSION
		
		/*
		 * Interaction sbol3ParentInteraction = (Interaction) sbol3Parent; Participation
		 * sbol3Participation = sbol3ParentInteraction.createParticipation(
		 * Util.convertToSBOL3Roles(sbol2Participation.getRoles()),
		 * Util.createSBOL3Uri(sbol2Participation.getParticipant().getIdentity())); //
		 * FUNCTIONAL COMPONENT FunctionalComponent sbol2Participation.getParticipant();
		 * sbol3Participation.setParticipant(sbol3ParticipantFeature); Participation
		 * sbol3Participation = null;
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * Util.copyIdentified(sbol2Participation, sbol3Participation);
		 * 
		 * return sbol3Participation;
		 */

	}

}
