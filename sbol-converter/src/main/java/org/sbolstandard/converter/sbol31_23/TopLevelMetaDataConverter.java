package org.sbolstandard.converter.sbol31_23;

import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.api.SBOLAPI;
import org.sbolstandard.core3.entity.Attachment;
import org.sbolstandard.core3.entity.TopLevelMetadata;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.util.SBOLUtil;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLValidationException;

public class TopLevelMetaDataConverter implements EntityConverter<TopLevelMetadata, org.sbolstandard.core2.GenericTopLevel>  {

    @Override
    public  org.sbolstandard.core2.GenericTopLevel convert(SBOLDocument doc, TopLevelMetadata input) throws SBOLGraphException, SBOLValidationException {      	
    	List<URI> types=input.getType();
		URI topLevelType=types.isEmpty() ? null : types.get(0);
		
		QName qnameType = Util.toQName(topLevelType);
				
		org.sbolstandard.core2.GenericTopLevel gtl = doc.createGenericTopLevel(Util.getURIPrefix(input), input.getDisplayId(), Util.getVersion(input), qnameType);
		List<Attachment> attachments=input.getAttachments();
		
		if (attachments != null) {
			List<URI> attachmentURIs=SBOLUtil.getURIs(attachments);
			gtl.setAttachments(Util.toSet(attachmentURIs));
		}
		Util.copyIdentified(input, gtl);
		return gtl;
	}
}
