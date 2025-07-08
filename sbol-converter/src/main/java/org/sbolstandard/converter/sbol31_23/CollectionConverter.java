package org.sbolstandard.converter.sbol31_23;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Collection;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;

public class CollectionConverter implements EntityConverter<Collection, org.sbolstandard.core2.Collection>  {

    @Override
    public org.sbolstandard.core2.Collection convert(SBOLDocument doc, Collection input) throws SBOLGraphException, SBOLValidationException {  
    	org.sbolstandard.core2.Collection col;
		col = doc.createCollection(Util.getURIPrefix(input),input.getDisplayId(),Util.getVersion(input));
    	Util.copyIdentified(input, col,doc);
		if (input.getMembers()!=null){
			for (URI uri : input.getMembers()) {
				col.addMember(Util.createSBOL2Uri(uri));
			}
		}
        return col;
    }
}
