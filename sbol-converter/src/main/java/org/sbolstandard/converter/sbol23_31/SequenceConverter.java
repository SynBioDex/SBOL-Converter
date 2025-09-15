package org.sbolstandard.converter.sbol23_31;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class SequenceConverter implements EntityConverter<org.sbolstandard.core2.Sequence, Sequence>  {

    @Override
    public Sequence convert(SBOLDocument doc, org.sbolstandard.core2.Sequence input, Parameters parameters) throws SBOLGraphException {  
        Sequence seq = doc.createSequence(Util.createSBOL3Uri(input),Util.getNamespace(input));
        Util.copyIdentified(input, seq, parameters);
        String elements=" ";
		if (input.getElements() != null && !input.getElements().isEmpty()) {
			elements = input.getElements();
		} 
        seq.setElements(elements);
        seq.setEncoding(Util.getSBOL3SequenceEncodingType(input.getEncoding()));
        return seq;
    }
}
