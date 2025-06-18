package org.sbolstandard.converter.sbol23_31;

import java.net.URI;
import org.sbolstandard.converter.Util;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.TopLevelMetadata;
import org.sbolstandard.core3.util.SBOLGraphException;

public class GenericTopLevelConverter implements EntityConverter<org.sbolstandard.core2.GenericTopLevel, TopLevelMetadata> {

	@Override
	public TopLevelMetadata convert(SBOLDocument doc, org.sbolstandard.core2.GenericTopLevel input, Parameters parameters) throws SBOLGraphException {
		URI dataType=URI.create(input.getRDFType().getNamespaceURI() + "/" + input.getRDFType().getLocalPart());
		TopLevelMetadata tlm = doc.createMetadata(Util.createSBOL3Uri(input), Util.getNamespace(input), dataType);
				//doc.createAttachment(Util.createSBOL3Uri(input), Util.getNamespace(input),	Util.createSBOL3Uri(input.getSource())); 
		if (input.getAnnotations()!=null){
			for (Annotation annotation: input.getAnnotations()){
				URI property=getAnnotationProperty(annotation);
				if (annotation.isURIValue()){
					tlm.addAnnotion(property, annotation.getURIValue());
				}
				else{
					Object value=getAnnotationValue(annotation);					
					tlm.addAnnotion(property, value.toString());
				}
				if (annotation.getAnnotations()!=null && annotation.getAnnotations().size()>0)
				{
					
					
				}
			}
		}
		
		Util.copyIdentified(input, tlm);
		
		return tlm;
	}
	
	private URI getAnnotationProperty(Annotation annotation)
	{
		String uri=annotation.getQName().getNamespaceURI() + "/" + annotation.getQName().getLocalPart();
		return URI.create(uri);		
	}
	
	private Object getAnnotationValue(Annotation annotation)
	{
		Object value=null;
		if (annotation.isBooleanValue())
		{
			value=annotation.getBooleanValue();
		}
		else if (annotation.isDoubleValue())
		{
			value=annotation.getDoubleValue();
		}
		else if (annotation.isIntegerValue())
		{
			value=annotation.getIntegerValue();
		}
		else if (annotation.isStringValue())
		{
			value=annotation.getStringValue();
		}		
		else if (annotation.isURIValue())
		{
			value=annotation.getURIValue();
		}
		return value;		
	}
	
}
