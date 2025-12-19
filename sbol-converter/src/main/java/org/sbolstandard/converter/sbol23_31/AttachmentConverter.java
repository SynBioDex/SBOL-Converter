package org.sbolstandard.converter.sbol23_31;

import java.util.OptionalLong;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.entity.Attachment;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.HashAlgorithm;

public class AttachmentConverter implements EntityConverter<org.sbolstandard.core2.Attachment, Attachment> {

	@Override
	public Attachment convert(SBOLDocument doc, org.sbolstandard.core2.Attachment input, Parameters parameters) throws SBOLGraphException {
		Attachment att = doc.createAttachment(Util.createSBOL3Uri(input), Util.getNamespace(input),	Util.enforceURI(input.getSource(), parameters));
		
		Util.copyIdentified(input, att, parameters);
		if (input.getHash() != null) {
			att.setHash(input.getHash());			
			//TODO: If converting from an SBOL2 generated from SBOL3, then use a backport annotation to set the hash algorithm
			//Set the default hash algorithm to sha-256 for now. This will be correct in SBOL2->SBOL3->SBOL2 round trips.
			att.setHashAlgorithm(HashAlgorithm.sha_256);
		}
		if (input.getSize() != null) {
			att.setSize(OptionalLong.of(input.getSize()));
		}
		if (input.getFormat() != null) {
			att.setFormat(Util.convertEDAMUri_2_to_3(input.getFormat()));
		}
		return att;
	}
}

