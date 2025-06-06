package org.sbolstandard.converter.sbol31_23;

//import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.OrientationType;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SequenceAnnotation;
import org.sbolstandard.core3.entity.Location;
import org.sbolstandard.core3.entity.Range;
import org.sbolstandard.core3.entity.Cut;
import org.sbolstandard.core3.entity.EntireSequence;
import org.sbolstandard.core3.entity.Interaction;
//import org.sbolstandard.core3.entity.Sequence;
//import org.sbolstandard.core3.entity.SequenceFeature;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.Orientation;

public class InteractionConverter implements ChildEntityConverter<Interaction, org.sbolstandard.core2.Interaction> {

	@Override
	public org.sbolstandard.core2.Interaction convert(SBOLDocument doc, org.sbolstandard.core2.Identified sbol2Parent,
			Interaction sbol3Interaction) throws SBOLGraphException, SBOLValidationException {

		org.sbolstandard.core2.Interaction sbol2Interaction = null;
		
		//
		System.out.println("WARNING! 3to2 InteractionConverter is not fully implemented yet.");
		
		ModuleDefinition sbol2ParentModuleDef = (ModuleDefinition) sbol2Parent;
		
		// TODO: Check DISPLAY ID
		sbol2ParentModuleDef.createInteraction(sbol3Interaction.getDisplayId(), Util.convertToSBOL2_SBO_URIs(sbol3Interaction.getTypes()));
		

		return sbol2Interaction;
	}
}
