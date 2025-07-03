package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.util.SBOLGraphException;

public class SequenceConverter implements EntityConverter<Sequence, org.sbolstandard.core2.Sequence>  {

    @Override
    public org.sbolstandard.core2.Sequence convert(SBOLDocument doc, Sequence input) throws SBOLGraphException, SBOLValidationException {  
    	org.sbolstandard.core2.Sequence seq;
    	// TODO: FIX ME - elements / encoding optional in 3, but required in 2, so need defaults
		seq = doc.createSequence(Util.getURIPrefix(input),input.getDisplayId(),Util.getVersion(input),
				input.getElements(),Util.getSBOL2SequenceEncodingType(input.getEncoding()));
		
		
    	Util.copyIdentified(input, seq, doc);
        return seq;
    }
}
