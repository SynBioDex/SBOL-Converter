package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol31_23.measurement.MeasurementConverter;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.Participation;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Interaction;

import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class InteractionConverter implements ChildEntityConverter<Interaction, org.sbolstandard.core2.Interaction> {

	@Override
	public org.sbolstandard.core2.Interaction convert(SBOLDocument doc, org.sbolstandard.core2.Identified sbol2Parent, Identified inputParent,
			Interaction sbol3Interaction) throws SBOLGraphException, SBOLValidationException {

		org.sbolstandard.core2.Interaction sbol2Interaction = null;		
		ModuleDefinition sbol2ParentModuleDef = (ModuleDefinition) sbol2Parent;
		sbol2Interaction = sbol2ParentModuleDef.createInteraction(sbol3Interaction.getDisplayId(), Util.convertToSBOL2_SBO_URIs(sbol3Interaction.getTypes()));
		

		if (sbol3Interaction.getParticipations()!=null) {
			for (org.sbolstandard.core3.entity.Participation sbol3Participation : sbol3Interaction.getParticipations()) {
				SubComponent sbol3SubComponent = (SubComponent) sbol3Participation.getParticipant();
				// Get the FunctionalComponent from the ModuleDefinition
				org.sbolstandard.core2.FunctionalComponent sbol2FuncCom = sbol2ParentModuleDef.getFunctionalComponent(sbol3SubComponent.getDisplayId());
				if (sbol2FuncCom == null) {
					throw new SBOLGraphException("Could not find FunctionalComponent for SubComponent: " + sbol3SubComponent.getUri());
				}
				Participation newSbol2Participation = sbol2Interaction.createParticipation(sbol3Participation.getDisplayId(), sbol2FuncCom.getIdentity(), Util.convertToSBOL2_SBO_URIs(sbol3Participation.getRoles()));			
				// Measurement conversion
				if(sbol3Participation.getMeasures()!=null) {
					MeasurementConverter measurementConverter = new MeasurementConverter();
					for (org.sbolstandard.core3.entity.measure.Measure sbol3Measure : sbol3Participation.getMeasures()) {
						measurementConverter.convert(doc, newSbol2Participation, sbol3Participation, sbol3Measure);
					}
				}
				Util.copyIdentified(sbol3Participation, newSbol2Participation, doc);			
			}
		}

		// Measuremet conversion
		if(sbol3Interaction.getMeasures()!=null) {
			MeasurementConverter measurementConverter = new MeasurementConverter();
			for (org.sbolstandard.core3.entity.measure.Measure sbol3Measure : sbol3Interaction.getMeasures()) {
				measurementConverter.convert(doc, sbol2Interaction, sbol3Interaction, sbol3Measure);
			}
		}

		Util.copyIdentified(sbol3Interaction, sbol2Interaction, doc);
		return sbol2Interaction;
	}
}