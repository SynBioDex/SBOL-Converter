package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.Model;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ModelConverter implements EntityConverter<org.sbolstandard.core2.Model, Model>  {

    @Override
    public Model convert(SBOLDocument doc, org.sbolstandard.core2.Model input) throws SBOLGraphException {  
    	// TODO: need to convert framework/language URIs
    	// TODO: should swap Language and Framework order to match libSBOLj
        Model mod = doc.createModel(Util.createSBOL3Uri(input),Util.getNamespace(input),input.getSource(),input.getFramework(),
        		input.getLanguage());
        Util.copyIdentified(input, mod);
        return mod;
    }
}
