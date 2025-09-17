package org.sbolstandard.converter.sbol23_31;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.measurement.MeasurementConverter;
import org.sbolstandard.core2.FunctionalComponent;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Interaction;
import org.sbolstandard.core3.entity.Participation;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

// Converts an SBOL2 Interaction to an SBOL3 Interaction
public class InteractionConverter implements ChildEntityConverter<org.sbolstandard.core2.Interaction, Interaction> {

	@Override
	public Interaction convert(
		SBOLDocument document,
		Identified sbol3Parent,
		org.sbolstandard.core2.Identified sbol2Parent,
		org.sbolstandard.core2.Interaction sbol2Interaction, Parameters parameters
	) throws SBOLGraphException, SBOLValidationException {

		// Cast the SBOL3 parent to a Component (the owner of the interaction)
		Component sbol3ParentComp = (Component) sbol3Parent;

		// Create a new SBOL3 Interaction with converted SBO types from the SBOL2 Interaction
		//Interaction sbol3Interaction = sbol3ParentComp.createInteraction(Util.convertToSBOL3_SBO_URIs(sbol2Interaction.getTypes()));
		Interaction sbol3Interaction =null;
		if (sbol2Interaction.getDisplayId()!=null){
			sbol3Interaction= sbol3ParentComp.createInteraction(sbol2Interaction.getDisplayId(), Util.convertToSBOL3_SBO_URIs(sbol2Interaction.getTypes()));			
		}
		else{
			sbol3Interaction= sbol3ParentComp.createInteraction(Util.convertToSBOL3_SBO_URIs(sbol2Interaction.getTypes()));	
			parameters.addMapping(sbol2Interaction.getIdentity(), sbol3Interaction.getUri());					
		}



		// Convert each SBOL2 Participation to SBOL3 Participation
		for (org.sbolstandard.core2.Participation sbol2Participation : sbol2Interaction.getParticipations()) {

			// Get the SBOL2 FunctionalComponent that participates in the interaction
			FunctionalComponent sbol2FuncCom = sbol2Participation.getParticipant();

			// Generate the SBOL3 URI for the corresponding Feature (participant)
			URI sbol3ParticipantFeatureUri = Util.createSBOL3Uri(sbol2FuncCom.getIdentity(), parameters);

			// Attempt to retrieve the Feature instance in the SBOL3 document by URI and Feature type
			Feature sbol3Feature = document.getIdentified(sbol3ParticipantFeatureUri, Feature.getSubClassTypes());

			// If not found by URI, try to find the Feature by its displayId among the parent's features
			sbol3Feature = Util.getByDisplayId(sbol3ParentComp.getFeatures(), sbol2FuncCom.getDisplayId());

			// If the Feature could not be found, throw an exception
			if (sbol3Feature == null) {
				throw new SBOLGraphException("Could not find Feature for FunctionalComponent: " + sbol2FuncCom.getIdentity());
			}

			// Create a Participation in the SBOL3 Interaction with the converted SBO roles and the Feature
			//Participation sbol3Participation=sbol3Interaction.createParticipation( Util.convertToSBOL3_SBO_URIs(sbol2Participation.getRoles()),sbol3Feature);
			Participation sbol3Participation=null;
			if (sbol2Participation.getDisplayId()!=null){				
				sbol3Participation = sbol3Interaction.createParticipation(sbol2Participation.getDisplayId(), Util.convertToSBOL3_SBO_URIs(sbol2Participation.getRoles()),sbol3Feature);
			}
			else{
				sbol3Participation = sbol3Interaction.createParticipation(Util.convertToSBOL3_SBO_URIs(sbol2Participation.getRoles()),sbol3Feature);
				parameters.addMapping(sbol2Participation.getIdentity(), sbol3Participation.getUri());
			}
			// Measurement conversion of Participation
			if(sbol2Participation.getMeasures()!=null) {
				MeasurementConverter measurementConverter = new MeasurementConverter();
				for (org.sbolstandard.core2.Measure sbol2Measure : sbol2Participation.getMeasures()) {
					measurementConverter.convert(document, sbol3Participation, sbol2Participation, sbol2Measure, parameters);
				}
			}

			Util.copyIdentified(sbol2Participation, sbol3Participation, parameters);
		}

		// Measurement conversion
		if(sbol2Interaction.getMeasures()!=null) {
			MeasurementConverter measurementConverter = new MeasurementConverter();
			for (org.sbolstandard.core2.Measure sbol2Measure : sbol2Interaction.getMeasures()) {
				measurementConverter.convert(document, sbol3Interaction, sbol2Interaction, sbol2Measure, parameters);
			}
		}

		// Copy common identified fields (displayId, version, etc.) from the SBOL2 Interaction to the SBOL3 Interaction
		Util.copyIdentified(sbol2Interaction, sbol3Interaction, parameters);

		// Return the constructed SBOL3 Interaction
		return sbol3Interaction;
	}

}

