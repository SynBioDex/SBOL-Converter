package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Implementation;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ImplementationConverter implements EntityConverter<org.sbolstandard.core2.Implementation, Implementation>  {

    @Override
    public Implementation convert(SBOLDocument doc, org.sbolstandard.core2.Implementation input) throws SBOLGraphException { 
    	Implementation impl = doc.createImplementation(Util.createSBOL3Uri(input), Util.getNamespace(input));
		Util.copyIdentified(input, impl);
		if (input.isSetBuilt()) {
			impl.setComponent(Util.createSBOL3Uri(input.getBuiltURI()));
		}
		return impl;
	}
}
