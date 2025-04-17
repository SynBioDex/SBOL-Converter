package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Collection;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;

public class CollectionConverter implements EntityConverter<Collection, org.sbolstandard.core2.Collection>  {

    @Override
    public org.sbolstandard.core2.Collection convert(SBOLDocument doc, Collection input) throws SBOLGraphException, SBOLValidationException {  
    	org.sbolstandard.core2.Collection col= doc.createCollection(Util.getURIPrefix(input),input.getDisplayId(),Util.getVersion(input));    	    			
     	col.setMembers(Util.toSet(input.getMembers()));        
    	Util.copyIdentified(input, col);
        return col;
    }
}
