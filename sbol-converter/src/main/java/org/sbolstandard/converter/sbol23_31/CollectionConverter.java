package org.sbolstandard.converter.sbol23_31;

import java.net.URI;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Collection;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class CollectionConverter implements EntityConverter<org.sbolstandard.core2.Collection, Collection>  {

    @Override
    public Collection convert(SBOLDocument doc, org.sbolstandard.core2.Collection input) throws SBOLGraphException {  
        Collection col = doc.createCollection(Util.createSBOL3Uri(input),Util.getNamespace(input));
        Util.copyIdentified(input, col);
        for (URI memberUri : input.getMemberURIs()) {
        	System.out.println("Add member:"+memberUri.toString());
        	System.out.println("SBOL3Uri member:"+Util.createSBOL3Uri(memberUri));
        	col.addMember(Util.createSBOL3Uri(memberUri));
        }
        return col;
    }
}
