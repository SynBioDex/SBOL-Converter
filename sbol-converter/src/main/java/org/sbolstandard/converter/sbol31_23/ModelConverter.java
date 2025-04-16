package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Model;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;

public class ModelConverter implements EntityConverter<Model, org.sbolstandard.core2.Model>  {

    @Override
    public org.sbolstandard.core2.Model convert(SBOLDocument doc, Model input) throws SBOLGraphException, SBOLValidationException {  
    	org.sbolstandard.core2.Model mod;
    	// TODO: need to convert framework/language URIs
		mod = doc.createModel(Util.getURIPrefix(input),input.getDisplayId(),Util.getVersion(input),
				input.getSource(),input.getLanguage(),input.getFramework());
    	Util.copyIdentified(input, mod);
        return mod;
    }
}
