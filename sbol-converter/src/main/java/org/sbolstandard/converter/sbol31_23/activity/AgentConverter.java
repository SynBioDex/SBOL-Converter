package org.sbolstandard.converter.sbol31_23.activity;

import org.sbolstandard.converter.Util;
import org.sbolstandard.converter.sbol31_23.EntityConverter;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.provenance.Agent;
import org.sbolstandard.core3.util.SBOLGraphException;

public class AgentConverter implements EntityConverter<Agent, org.sbolstandard.core2.Agent>  {

    @Override
    public org.sbolstandard.core2.Agent convert(SBOLDocument doc, Agent input) throws SBOLGraphException, SBOLValidationException {

        
    	org.sbolstandard.core2.Agent sbol2Agent = doc.createAgent(Util.getURIPrefix(input),input.getDisplayId(),Util.getVersion(input));
    	Util.copyIdentified(input, sbol2Agent, doc);
        return sbol2Agent;
    }
}
