package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Implementation;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;

public class ImplementationConverter implements EntityConverter<Implementation, org.sbolstandard.core2.Implementation>  {

    @Override
    public  org.sbolstandard.core2.Implementation convert(SBOLDocument doc, Implementation input) throws SBOLGraphException, SBOLValidationException {      	
    	org.sbolstandard.core2.Implementation impl = doc.createImplementation(Util.getURIPrefix(input), input.getDisplayId(), Util.getVersion(input));
		Util.copyIdentified(input, impl);
		if (input.getComponent() != null)
			impl.setBuilt(Util.createSBOL2Uri(input.getComponent()));
		return impl;
	}
}
