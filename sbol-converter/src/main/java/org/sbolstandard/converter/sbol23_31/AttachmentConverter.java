package org.sbolstandard.converter.sbol23_31;

import java.util.OptionalLong;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Attachment;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;

public class AttachmentConverter implements EntityConverter<org.sbolstandard.core2.Attachment, Attachment>  {

    @Override
    public Attachment convert(SBOLDocument doc, org.sbolstandard.core2.Attachment input) throws SBOLGraphException { 
    	Attachment att = doc.createAttachment(Util.createSBOL3Uri(input), Util.getNamespace(input), Util.createSBOL3Uri(input.getSource()));
		Util.copyIdentified(input, att);
		att.setHash(input.getHash());
		if (input.getSize() != null)
			att.setSize(OptionalLong.of(input.getSize()));
		if (input.getFormat() != null)
			att.setFormat(Util.createSBOL3Uri(input.getFormat()));
		return att;
	}
}
