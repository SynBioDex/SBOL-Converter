package org.sbolstandard.converter.sbol23_31.activity;


import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol23_31.EntityConverter;
import org.sbolstandard.converter.sbol23_31.Parameters;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.provenance.Agent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class AgentConverter implements EntityConverter<org.sbolstandard.core2.Agent, Agent> {

	@Override
	public Agent convert(SBOLDocument doc, org.sbolstandard.core2.Agent input, Parameters parameters) throws SBOLGraphException {
		Agent agent = doc.createAgent(Util.createSBOL3Uri(input), Util.getNamespace(input));		
		Util.copyIdentified(input, agent, parameters);	
		return agent;
	} 

}
