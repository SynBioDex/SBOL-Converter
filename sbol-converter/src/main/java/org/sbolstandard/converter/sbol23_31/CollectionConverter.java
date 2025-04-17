package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Collection;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class CollectionConverter implements EntityConverter<org.sbolstandard.core2.Collection, Collection>  {

    @Override
    public Collection convert(SBOLDocument doc, org.sbolstandard.core2.Collection input) throws SBOLGraphException {  
    	Collection col = doc.createCollection(Util.createSBOL3Uri(input),Util.getNamespace(input));
    	col.setMembers(Util.toList(input.getMemberURIs()));
    	Util.copyIdentified(input, col);		
        return col;
    }
}
