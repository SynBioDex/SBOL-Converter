package org.sbolstandard.converter.sbol23_31;

import java.net.URI;
import java.util.Set;

import org.sbolstandard.converter.Util;
import org.sbolstandard.core3.api.SBOLAPI;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.TopLevelMetadata;
import org.sbolstandard.core3.util.SBOLGraphException;

public class GenericTopLevelConverter implements EntityConverter<org.sbolstandard.core2.GenericTopLevel, TopLevelMetadata> {

	@Override
	public TopLevelMetadata convert(SBOLDocument doc, org.sbolstandard.core2.GenericTopLevel input, Parameters parameters) throws SBOLGraphException {
		String namespaceURI = input.getRDFType().getNamespaceURI();
		String localPart = input.getRDFType().getLocalPart();
		URI dataType=SBOLAPI.append(namespaceURI, localPart);
		URI newURI=Util.createSBOL3Uri(input);
		TopLevelMetadata tlm = doc.createMetadata(newURI, Util.getNamespace(input), dataType);
		Set<URI> attachments=input.getAttachmentURIs();
		if (attachments!=null){
			tlm.setAttachmentsByURIs(Util.toList(attachments));
		}
		Util.copyIdentified(input, tlm);		
		return tlm;
	}
	
	
	
}
