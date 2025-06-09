package org.sbolstandard.converter.sbol23_31;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.AccessType;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.FunctionalComponent;
import org.sbolstandard.core2.ModuleDefinition;
import org.sbolstandard.core2.Participation;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Feature;
import org.sbolstandard.core3.entity.Interface;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.SubComponent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ModuleDefinitionToComponentConverter implements EntityConverter<ModuleDefinition, Component>  {

    @Override
    public Component convert(SBOLDocument doc, ModuleDefinition sbol2ModuleDefinition) throws SBOLGraphException, SBOLValidationException { 
    	//System.out.println("Component:"+Util.createSBOL3Uri(input));
    	List<URI> types = new ArrayList<URI>();
    	types.add(URI.create("https://identifiers.org/SBO:0000241"));
    	Component sbol3Component = doc.createComponent(Util.createSBOL3Uri(sbol2ModuleDefinition),Util.getNamespace(sbol2ModuleDefinition),types);
        Util.copyIdentified(sbol2ModuleDefinition, sbol3Component);
        sbol3Component.setRoles(Util.convertRoles2_to_3(sbol2ModuleDefinition.getRoles()));
        
        //TODO: Interaction, Participants etc
        for(org.sbolstandard.core2.Interaction sbol2Interaction: sbol2ModuleDefinition.getInteractions()) {
        	//System.out.println(sbol2Interaction);
        	
        	for (Participation sbol2Participation: sbol2Interaction.getParticipations())
        	{
    			FunctionalComponentToSubComponentConverter fcConverter = new FunctionalComponentToSubComponentConverter();
        		FunctionalComponent sbol2FuncCom = sbol2Participation.getParticipant();
        		SubComponent sbol3SubCom  = fcConverter.convert(doc, sbol3Component, sbol2ModuleDefinition, sbol2FuncCom);

        		
        		if(sbol2Participation.getParticipant().getDirection()!=org.sbolstandard.core2.DirectionType.NONE)
        		{
        			
        			Interface sbol3Interface = getInterface(sbol3Component);
        			
        			if(sbol2FuncCom.getDirection() == org.sbolstandard.core2.DirectionType.IN)
        			{
        				sbol3Interface.setInputs(toFeatureList(sbol3SubCom, sbol3Interface.getInputs()));
        			}
        			else if(sbol2FuncCom.getDirection() == org.sbolstandard.core2.DirectionType.OUT)
        			{
        				sbol3Interface.setOutputs(toFeatureList(sbol3SubCom, sbol3Interface.getOutputs()));
        			}
        			else if(sbol2FuncCom.getDirection() == org.sbolstandard.core2.DirectionType.INOUT)
        			{
        				sbol3Interface.setNonDirectionals(toFeatureList(sbol3SubCom, sbol3Interface.getNonDirectionals()));
        			}        			        			
        		}
        		else
        		{
        			if (sbol2FuncCom.getAccess()== AccessType.PUBLIC)
        			{
        				Interface sbol3Interface = getInterface(sbol3Component);
        				sbol3Interface.setNonDirectionals(toFeatureList(sbol3SubCom, sbol3Interface.getNonDirectionals()));
        			}
        		}
        		
        	}
        }
        
        // Interaction Converter
        for(org.sbolstandard.core2.Interaction sbol2Interaction: sbol2ModuleDefinition.getInteractions()) {
        	//sbol3Component.createInteraction(sbol2Interaction.getIdentity(), types)
        	System.out.println(sbol2Interaction);
        	InteractionConverter interactionConverter = new InteractionConverter();
        	interactionConverter.convert(doc, sbol3Component, sbol2ModuleDefinition, sbol2Interaction);
        }
        
        return sbol3Component;
    }

	private Interface getInterface(Component sbol3Component) throws SBOLGraphException {
		Interface sbol3Interface = sbol3Component.getInterface();
		if(sbol3Interface == null) {
			sbol3Interface=sbol3Component.createInterface();
		}
		return sbol3Interface;
	}

	private List<Feature> toFeatureList(SubComponent sbol3SubCom, List<Feature> features) {
		if (features==null)
		{
			features=new ArrayList<Feature>();
		}
		features.add(sbol3SubCom);
		return features;
	}
}
