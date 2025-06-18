package org.sbolstandard.converter.sbol31_23;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Attachment;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;

public class AttachmentConverter implements EntityConverter<Attachment, org.sbolstandard.core2.Attachment>  {

    @Override
    public  org.sbolstandard.core2.Attachment convert(SBOLDocument doc, Attachment input) throws SBOLGraphException, SBOLValidationException {      	
    	org.sbolstandard.core2.Attachment att = doc.createAttachment(Util.getURIPrefix(input), input.getDisplayId(), Util.getVersion(input), Util.createSBOL2Uri(input.getSource()));
		Util.copyIdentified(input, att);
		att.setHash(input.getHash());
		if (input.getSize() != null && input.getSize().isPresent()) {
			att.setSize(input.getSize().getAsLong());
		}
		if (input.getFormat() != null) {
			att.setFormat(input.getFormat());
		}
		return att;
	}
}
