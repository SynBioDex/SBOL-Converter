package org.sbolstandard.converter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.StmtIterator;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.EDAMOntology;
import org.sbolstandard.core2.RestrictionType;
import org.sbolstandard.core2.SBOLValidate;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SystemsBiologyOntology;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.Metadata;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.TopLevel;
import org.sbolstandard.core3.entity.TopLevelMetadata;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.util.SBOLUtil;
import org.sbolstandard.core3.util.URINameSpace;
import org.sbolstandard.core3.util.URINameSpace.SONameSpace;
import org.sbolstandard.core3.vocabulary.ComponentType;
import org.sbolstandard.core3.vocabulary.DataModel;
import org.sbolstandard.core3.vocabulary.Encoding;
import org.sbolstandard.core3.vocabulary.Role;
import org.sbolstandard.core3.vocabulary.RestrictionType.ConstraintRestriction;
import org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction;
import org.sbolstandard.core3.vocabulary.RestrictionType.OrientationRestriction;
import org.sbolstandard.core3.vocabulary.RestrictionType.SequentialRestriction;

import com.apicatalog.jsonld.StringUtils;

import org.sbolstandard.core3.api.SBOLAPI;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.core3.entity.Constraint;
import org.sbolstandard.core3.entity.Component;
import org.sbolstandard.converter.sbol23_31.Parameters;


public class Util {
	
	

	private static final String versionPattern = "[0-9]+[\\p{L}0-9_\\.-]*";

	private static final Pattern versionPatternPat = Pattern.compile(versionPattern);

	public static <T> List<T> toList(Set<T> set) {
		return new ArrayList<>(set);
	}

	public static <T> Set<T> toSet(List<T> list) {
		return new HashSet<>(list);
	}

	static boolean isVersionValid(String version) {
		if (version.equals(""))
			return true;
		Matcher m = versionPatternPat.matcher(version);
		return m.matches();
	}

	public static String extractDisplayIdSBOL3Uri(String uri) {
		int lastSlash = uri.lastIndexOf('/');
		if (lastSlash == -1)
			return "";
		String displayId = uri.substring(lastSlash + 1);
		// if (isDisplayIdValid(version))
		return displayId;
		// return "";
	}

	public static String extractVersionSBOL3Uri(String uri) {
		int lastSlash = uri.lastIndexOf('/');
		if (lastSlash == -1)
			return "";
		String firstSegment = uri.substring(0, lastSlash);
		// String lastSegment = uri.substring(lastSlash + 1);
		int secondLastSlash = firstSegment.lastIndexOf('/');
		if (secondLastSlash == -1)
			return "";
		String version = uri.substring(secondLastSlash + 1, lastSlash);
		if (isVersionValid(version))
			return version;
		return "";
	}

	// TODO: should configure whether you want minimal or maximal
	public static String extractNameSpaceSBOL3Uri(String uri) {
		String displayId = extractDisplayIdSBOL3Uri(uri);
		String version = extractVersionSBOL3Uri(uri);
		int lastSlash = uri.lastIndexOf('/');
		if (lastSlash == -1)
			return "";
		String namespace = uri.substring(0, lastSlash);
		if (!version.equals("")) {
			lastSlash = namespace.lastIndexOf('/');
			if (lastSlash == -1)
				return "";
			namespace = namespace.substring(0, lastSlash);
		}
		return namespace;
	}

	// TODO: should configure whether you want minimal or maximal
	/* GM Commented on 2025-6-10
	public static URI extractNameSpaceSBOL3Uri(URI uri) {
		try {
			java.net.URL parsedUrl = new java.net.URL(uri.toString());
			String protocol = parsedUrl.getProtocol();
			String namespace = uri.toString().replaceFirst(protocol + "://", "");

			int slashIndex = namespace.lastIndexOf('/');

			if (slashIndex == -1) {
				// TODO:
				return uri;
			}

			return URI.create(protocol + "://" + namespace.substring(0, slashIndex));

		} catch (Exception e) {
			// TODO:
			return null;
		}
	}
*/
	// TODO: what if the collection is a collection + version
	public static String extractCollectionSBOL3Uri(String uri, String namespace, String displayId) {
		String collection;
		collection = uri.replace(namespace + "/", "");
		int lastSlash = uri.lastIndexOf('/');
		if (lastSlash == -1)
			return "";
		String lastSegment = uri.substring(lastSlash + 1);
		if (!lastSegment.equals(displayId))
			return "";
		lastSlash = collection.lastIndexOf('/');
		if (lastSlash == -1)
			return "";
		collection = collection.substring(0, lastSlash);
		if (isVersionValid(collection))
			return "";
		if (!collection.equals(""))
			collection = "/" + collection;
		return collection;
	}

	public static String extractVersionSBOL3Uri(String uri, String namespace, String displayId) {
		String collection;
		collection = uri.replace(namespace + "/", "");
		int lastSlash = uri.lastIndexOf('/');
		if (lastSlash == -1)
			return "";
		String lastSegment = uri.substring(lastSlash + 1);
		if (!lastSegment.equals(displayId))
			return "";
		lastSlash = collection.lastIndexOf('/');
		if (lastSlash == -1)
			return "";
		collection = collection.substring(0, lastSlash);
		if (isVersionValid(collection))
			return collection;
		return "";
	}

	public static String extractVersionSBOL2Uri(String uri) {
		if (uri == null || uri.isEmpty()) {
			return null;
		} else if (uri.endsWith("/")) {
			return null;
		}

		int lastSlash = uri.lastIndexOf('/');
		if (lastSlash == -1)
			return null;

		String lastSegment = uri.substring(lastSlash + 1);
		if (isVersionValid(lastSegment)) {
			return lastSegment;
		} else {
			return "";
		}
	}

	public static String extractDisplayIdSBOL2Uri(String uri) {
		if (uri == null || uri.isEmpty()) {
			return null;
		} else if (uri.endsWith("/")) {
			return null;
		}

		int lastSlash = uri.lastIndexOf('/');
		if (lastSlash == -1)
			return null;

		String lastSegment = uri.substring(lastSlash + 1);
		boolean isNumber = isVersionValid(lastSegment);

		if (isNumber) {
			int secondLastSlash = uri.lastIndexOf('/', lastSlash - 1);
			if (secondLastSlash == -1)
				return uri;
			return uri.substring(secondLastSlash + 1, lastSlash);
		} else {
			return uri.substring(lastSlash + 1);
		}

	}

	public static String extractURIPrefixSBOL2Uri(String uri) {
		if (uri == null || uri.isEmpty()) {
			return null;
		} else if (uri.endsWith("/")) {
			return null;
		}

		int lastSlash = uri.lastIndexOf('/');
		if (lastSlash == -1)
			return null;

		String lastSegment = uri.substring(lastSlash + 1);
		boolean isNumber = isVersionValid(lastSegment);

		// Remove the number and the display id
		if (isNumber) {
			int secondLastSlash = uri.lastIndexOf('/', lastSlash - 1);
			if (secondLastSlash == -1)
				return uri;
			return uri.substring(0, secondLastSlash);
		} else {
			// Remove the display id
			return uri.substring(0, lastSlash);
		}
	}

	public static URI createSBOL3UriOLD(URI inputUri) {
		String sbol3Uri = "";
		sbol3Uri = Util.extractURIPrefixSBOL2Uri(inputUri.toString());
		String version = extractVersionSBOL2Uri(inputUri.toString());
		String displayId = extractDisplayIdSBOL2Uri(inputUri.toString());
		if (version != null && !version.equals("")) {
			sbol3Uri += "/" + version;
		}
		if (displayId != null && !displayId.equals("")) {
			sbol3Uri += "/" + displayId;
		}

		if (sbol3Uri == null){
			String inputUriString = inputUri.toString();
			if (!inputUriString.toLowerCase().startsWith("urn") && !inputUriString.contains("://")){				
				return SBOLAPI.append("https://sbolstandard.org/SBOL3-Converter", inputUriString);
			}
			else{
				return URI.create(inputUri.toString());
			}
		}
		return URI.create(sbol3Uri);
	}

	

	private static URI getLatestUri(URI inputUri, Model rdfModel) {
		Property persistentIdentity= rdfModel.getProperty("http://sbols.org/v2#persistentIdentity");
		RDFNode valueNode=rdfModel.getResource(inputUri.toString());
		//If a resource with "persistentIdentity - inputUri" property exists then inputUri is a persistent identity. Find the most uri with the most current version that the persistent identity refers to.
		ResIterator it= rdfModel.listSubjectsWithProperty(persistentIdentity, valueNode);
		Double version=null;
		if (it.hasNext()) {
			// If there is a persistent identity, find the most current version
			while (it.hasNext()) {
				Resource resource = it.next();
				if (version==null){
					inputUri= URI.create(resource.getURI());
				}
				Property versionProperty= rdfModel.getProperty("http://sbols.org/v2#version");
				StmtIterator stmtIt= resource.listProperties(versionProperty);
				if (stmtIt.hasNext()) {
					// If there is a version, use it
					Double currentVersion = stmtIt.next().getObject().asLiteral().getDouble();
					//if (version==null || (currentVersion != null && currentVersion> version)) {
					if (version==null) {						
						version=currentVersion;
						inputUri=URI.create(resource.getURI());
					}
					//Split into if else for debugging purposes!
					else if (currentVersion != null && currentVersion> version){
						version=currentVersion;
						inputUri=URI.create(resource.getURI());
					}
				}
			}			
		}
		
		return inputUri;		
	}

	public static URI createSBOL3Uri(URI inputUri, Parameters parameters) throws SBOLGraphException {
		Model rdfModel = parameters.getRdfModel();
		inputUri=getLatestUri(inputUri, rdfModel);
		String sbol3Uri = "";
		sbol3Uri = Util.extractURIPrefixSBOL2Uri(inputUri.toString());
		String version = extractVersionSBOL2Uri(inputUri.toString());
		String displayId = extractDisplayIdSBOL2Uri(inputUri.toString());
		if (version != null && !version.equals("")) {
			sbol3Uri += "/" + version;
		}
		if (displayId != null && !displayId.equals("")) {
			sbol3Uri += "/" + displayId;
		}

		if (sbol3Uri == null){
			String inputUriString = inputUri.toString();
			if (!inputUriString.toLowerCase().startsWith("urn") && !inputUriString.contains("://")){				
				return SBOLAPI.append("https://sbolstandard.org/SBOL3-Converter", inputUriString);
			}
			else{
				return URI.create(inputUri.toString());
			}
		}
		return URI.create(sbol3Uri);
	}


	public static URI createSBOL3Uri(org.sbolstandard.core2.Identified input) {
		String sbol3Uri = "";

		sbol3Uri = Util.extractURIPrefixSBOL2Uri(input.getIdentity().toString());
		if (input.isSetVersion()) {
			sbol3Uri += "/" + input.getVersion();
		}
		if (input.isSetDisplayId()) {
			sbol3Uri += "/" + input.getDisplayId();
		}
		return URI.create(sbol3Uri);
	}
	
	public static URI createSBOL2Uri(URI inputUri) throws SBOLGraphException {
		String sbol2Uri = "";

		String version = extractVersionSBOL3Uri(inputUri.toString());

		sbol2Uri = extractNameSpaceSBOL3Uri(inputUri.toString()) + "/" // + extractVersionCollectionSBOL3Uri(inputUri)
				+ extractDisplayIdSBOL3Uri(inputUri.toString());

		if (!version.equals("")) {
			sbol2Uri += "/" + version;
		}

		// In case of URI of type "dynamic_measurement.mdx"
		// TODO: better check
		if (sbol2Uri.equals("/"))
			return URI.create(inputUri.toString());

		return URI.create(sbol2Uri);
	}

	public static URI createSBOL2Uri(TopLevel input) throws SBOLGraphException {
		String sbol2Uri = "";

		String version = getVersion(input);

		sbol2Uri = getURIPrefix(input) + "/" + input.getDisplayId();

		if (!version.equals("")) {
			sbol2Uri += "/" + version;
		}

		return URI.create(sbol2Uri);
	}

	public static String getURIPrefix(TopLevel input) throws SBOLGraphException {
		return input.getNamespace().toString() + Util.extractCollectionSBOL3Uri(input.getUri().toString(),
				input.getNamespace().toString(), input.getDisplayId());
	}

	public static String getVersion(TopLevel input) throws SBOLGraphException {
		return extractVersionSBOL3Uri(input.getUri().toString(), input.getNamespace().toString(), input.getDisplayId());
	}

	public static URI getNamespace(org.sbolstandard.core2.Identified input) {
		return URI.create(Util.extractURIPrefixSBOL2Uri(input.getIdentity().toString()));
	}

	public static <T extends Identified> String inferDisplayId(URI uri, URI childEntityDataType, List<T> childEntities) throws SBOLGraphException {
		String displayId=SBOLAPI.inferDisplayId(uri);
		if (StringUtils.isBlank(displayId)){
			displayId=SBOLAPI.createLocalName(childEntityDataType,childEntities);
		}
		return displayId;

	}
	private static String getAfterTheLast(String str, String after) {
		int index = str.lastIndexOf(after);
		if (index == -1) {
			return null;
		} 
		else {
			return str.substring(index + after.length());
		}
	}
	public static QName toQName(URI uri, org.sbolstandard.core2.SBOLDocument sbol2Document) throws SBOLGraphException
	{
		String namespace=null;
		String uriString=uri.toString();
		String local=null;
		
		if (SBOLUtil.isURL(uriString)){
			int indexHash = uriString.lastIndexOf("#");
			int indexSlash = uriString.lastIndexOf("/");
			if (indexHash> indexSlash){
				local=getAfterTheLast(uriString, "#");
			}
			else{			
				local=SBOLAPI.inferDisplayId(uri);
			}
		}
		else{
			local = getAfterTheLast(uriString, "/");
			if (local==null){
				//Create a random local name
				local = "displayid_auto_generated" + UUID.randomUUID().toString().replace("-", "");
			}
		}
		// Extract the namespace from the top-level type string
		if (local!=null){
			int index=uriString.lastIndexOf(local);
			namespace = uriString.substring(0,index);
		}
		/*if (namespace.endsWith("/")) {
			namespace = namespace.substring(0, namespace.length() - 1);
		}*/
		QName qName = null;
		QName qNameExists=sbol2Document.getNamespace(URI.create(namespace));
		if (qNameExists!=null){
			String prefix = qNameExists.getPrefix();
			if (prefix!=null && !prefix.isEmpty()) {
				// If the prefix is not empty, use it to create the QName
				qName = new QName(qNameExists.getNamespaceURI(), local, prefix);
			} 			
		}
		
		if (qName==null){
			// If the prefix is empty, use the namespace URI directly
			qName = new QName(namespace, local);
		}
		return qName;
	}
	
	public static void copyIdentified(Identified input, org.sbolstandard.core2.Identified output, org.sbolstandard.core2.SBOLDocument sbol2Document)
			throws SBOLGraphException, SBOLValidationException {
		output.setName(input.getName());
		output.setDescription(input.getDescription());
		if (input.getWasDerivedFrom() != null) {
			output.setWasDerivedFroms(toSet(input.getWasDerivedFrom()));
		}
		if (input.getWasGeneratedByURIs() != null) {
			output.setWasGeneratedBys(toSet(input.getWasGeneratedByURIs()));
		}
		
		convertIfTopLevel3to2(input, output);
		convertAnnotations3_to_2(input, output, sbol2Document);	
		/* 
		List<Metadata> metadataEntities = input.getMetadataEntites();
		if (metadataEntities != null){
			for (Metadata metadata : metadataEntities) {
				// Convert Metadata to SBOL2 Annotation
				Annotation annotation = new Annotation(metadata.getProperty(), metadata.getValue());
				Annotation nested1=gtl.createAnnotation(new QName(annoNS, "property4"), new QName(annoNS, "NestedType1"), "nestedid", annotations);
		
				output.addAnnotation(annotation);
			}
		}*/


		// TODO: FIX ME
		// output.setWasGeneratedBy(toSet(input.getWasGeneratedBys()));
	}

	private static void convertIfTopLevel3to2(Identified input, org.sbolstandard.core2.Identified output) throws SBOLGraphException, SBOLValidationException {
		if (input instanceof org.sbolstandard.core3.entity.TopLevel) {
			org.sbolstandard.core3.entity.TopLevel topLevelInput = (org.sbolstandard.core3.entity.TopLevel) input;
			if (output instanceof org.sbolstandard.core2.TopLevel) {
				org.sbolstandard.core2.TopLevel topLevelOutput = (org.sbolstandard.core2.TopLevel) output;
				if(topLevelInput.getAttachments()!=null) {
					Set<URI> sbol2AttachmentURIs = new HashSet<>();
					for(org.sbolstandard.core3.entity.Attachment sbol3Attachment : topLevelInput.getAttachments()) {
						URI sbol2Uri = Util.createSBOL2Uri(sbol3Attachment.getUri());
						sbol2AttachmentURIs.add(sbol2Uri);
					}
					// It does not have setAttachmentURIs method
					topLevelOutput.setAttachments(sbol2AttachmentURIs);
				}
			}
			// TODO: WRITE ME
			// if has measurement 
			// 	if output is module 
			// 	    copy hasMeasurement URIS
			// 	else if component instance 
			// 		copy hasMeasurement URIS
			
		}
	}

	/**
	 * Converts a SBOL3 Metadata object to a SBOL2 Annotation and adds it to the output.
	 * @param metadata The SBOL3 Metadata object
	 * @param output The SBOL2 Identified object to add the annotation to
	 * @param qName The QName for the annotation property
	 * @throws SBOLGraphException 
	 */
	private static void convertAnnotations3_to_2(Identified input, org.sbolstandard.core2.Identified output, org.sbolstandard.core2.SBOLDocument sbol2Document) throws SBOLGraphException, SBOLValidationException {
		List<Pair<URI, Object>> annotations = input.getAnnotations();
		if (annotations!=null){
			//String sbol2BackPortAnnotationPrefix=ConverterNameSpace.BackPort_2_3.getUri().toString() +  ConverterVocabulary.Two_to_Three.sbol2BackPortTermPrefix;		
			for (Pair<URI, Object> annotation : annotations) {
				// Convert SBOL3 Annotation to Metadata
				String property = annotation.getLeft().toString();
				//If the annotation was added during the conversion from v2 to v3, then remove it when converting it back to v2
				if (property.toLowerCase().startsWith(ConverterNameSpace.BackPort_2_3.getUri().toString().toLowerCase())){				
					continue;
				}
				Object value = annotation.getRight();
				QName qName=toQName(URI.create(property),sbol2Document);
				if (value instanceof URI) {
					output.createAnnotation(qName, (URI) value);
				} else if (value instanceof String) {
					output.createAnnotation(qName, (String) value);
				} else if (value instanceof Boolean) {
					output.createAnnotation(qName, (Boolean) value);
				} else if (value instanceof Double) {
					output.createAnnotation(qName, (Double) value);
				} else if (value instanceof Integer) {
					output.createAnnotation(qName, (Integer) value);
				}
				else if (value instanceof TopLevel) {
					output.createAnnotation(qName, ((TopLevel) value).getUri());
				}
				else if (value instanceof Metadata) {
					Metadata metadata = (Metadata) value;
					createNestedAnnotation(output, qName, metadata, sbol2Document);					
				}
			}
		}

	}

	private static Annotation createNestedAnnotation(Object output, QName qnameProperty, Metadata metadata,org.sbolstandard.core2.SBOLDocument sbol2Document) throws SBOLGraphException, SBOLValidationException
	{
		URI metadataTypeURI=metadata.getType().get(0);
		QName nestedQName = toQName(metadataTypeURI,sbol2Document);
		
		List<Annotation> sbol2Annotations = new ArrayList<>();
		List<Pair<URI, Object>> childMetadataAnnotations=null;
		List<Pair<URI, Object>> annotations = metadata.getAnnotations();
		if (annotations!=null){
			for (Pair<URI, Object> annotation : annotations) {
				String property = annotation.getLeft().toString();
				Object value = annotation.getRight();
				QName qName=toQName(URI.create(property),sbol2Document);
				if (value instanceof URI) {
					sbol2Annotations.add(new Annotation(qName, (URI) value));
				} else if (value instanceof String) {
					sbol2Annotations.add(new Annotation(qName, (String) value));
				} else if (value instanceof Boolean) {
					sbol2Annotations.add(new Annotation(qName, (Boolean) value));
				} else if (value instanceof Double) {
					sbol2Annotations.add(new Annotation(qName, (Double) value));
				} else if (value instanceof Integer) {
					sbol2Annotations.add(new Annotation(qName, (Integer) value));
				}
				else if (value instanceof TopLevel) {
					sbol2Annotations.add(new Annotation(qName, ((TopLevel) value).getUri()));
				}
				else if (value instanceof Metadata) {
					if (childMetadataAnnotations==null)
					{
						childMetadataAnnotations=new ArrayList<>();
					}
					childMetadataAnnotations.add(annotation);
				}					
			}
		}

		Annotation nestedAnnotation=null;
		if (output instanceof org.sbolstandard.core2.Identified){
			org.sbolstandard.core2.Identified identifiedOutput = (org.sbolstandard.core2.Identified) output;
			nestedAnnotation = identifiedOutput.createAnnotation(qnameProperty, nestedQName, metadata.getDisplayId(), sbol2Annotations);
		}
		else if (output instanceof Annotation){
			Annotation annotationOutput = (Annotation) output;
			nestedAnnotation = annotationOutput.createAnnotation(qnameProperty, nestedQName, metadata.getDisplayId(), sbol2Annotations);
		}
				
		if (childMetadataAnnotations!=null){
			for (Pair<URI, Object> childMetadataAnnotation : childMetadataAnnotations){
				QName qNameChildMetadata=toQName(URI.create(childMetadataAnnotation.getLeft().toString()), sbol2Document);
				Metadata childMetadata= (Metadata)childMetadataAnnotation.getRight();
				//No need to add the created child annoation since it is added within the createNestedAnnotation method
				createNestedAnnotation(nestedAnnotation, qNameChildMetadata, childMetadata, sbol2Document);
			}
		}
		return nestedAnnotation;
	}

	public static void copyIdentified(org.sbolstandard.core2.Identified input, Identified output, Parameters parameters)
			throws SBOLGraphException {
		output.setName(input.getName());
		output.setDescription(input.getDescription());
		output.setWasDerivedFrom(toList(input.getWasDerivedFroms()));
		output.setWasGeneratedByURIs(toList(input.getWasGeneratedBys()));
		convertIfTopLevel(input, output,parameters);
		convertAnnotations_2_to3(input.getAnnotations(), output);

		
	}

	private static void convertIfTopLevel(org.sbolstandard.core2.Identified input, Identified output, Parameters parameters) throws SBOLGraphException {
		if (input instanceof org.sbolstandard.core2.TopLevel) {
			org.sbolstandard.core2.TopLevel topLevelInput = (org.sbolstandard.core2.TopLevel) input;
			if (input instanceof org.sbolstandard.core2.TopLevel) {
				org.sbolstandard.core3.entity.TopLevel topLevelOutput = (org.sbolstandard.core3.entity.TopLevel) output;
				List<URI> sbol3AttachmentURIs = new ArrayList<>();
				for(URI sbol2Uri : topLevelInput.getAttachmentURIs()) {
					URI sbol3Uri = Util.createSBOL3Uri(sbol2Uri, parameters);
					sbol3AttachmentURIs.add(sbol3Uri);
				}
				topLevelOutput.setAttachmentsByURIs(sbol3AttachmentURIs);
			}
		}
	}
	/**
	 * Recursively converts SBOL2 annotations (and nested annotations) to SBOL3 Metadata.
	 * @param sbol2Annotations List of SBOL2 annotations to convert
	 * @param parent The SBOL3 Identified object to which metadata will be added
	 * @throws SBOLGraphException
	 */
	public static void convertAnnotations_2_to3(List<Annotation> sbol2Annotations, Identified parent) throws SBOLGraphException {
		if (sbol2Annotations == null || sbol2Annotations.isEmpty()) return;
		for (Annotation annotation : sbol2Annotations) {
			
			URI property=getAnnotationProperty(annotation);
			if (annotation.isURIValue()){
				parent.addAnnotation(property, annotation.getURIValue());
			}
			else if (annotation.isNestedAnnotations()){			
				// If the annotation is a nested annotation, we need to create a Metadata object
				// and recursively convert its annotations
				URI annotationURI = annotation.getNestedIdentity();
				String annotationDataTypeString = annotation.getNestedQName().getNamespaceURI() + annotation.getNestedQName().getLocalPart();
				URI annotationDataType = URI.create(annotationDataTypeString);
				String displayId = inferDisplayId(annotationURI, URINameSpace.SBOL.local("Identified"), parent.getMetadataEntites());
				Metadata metadata = parent.createMetadata(displayId, annotationDataType, property);
				convertAnnotations_2_to3(annotation.getAnnotations(), metadata);
			}
			else if (annotation.isBooleanValue()){
				parent.addAnnotation(property, annotation.getBooleanValue());				
			}			
			else if (annotation.isDoubleValue()){
				parent.addAnnotation(property, annotation.getDoubleValue());				
			}
			else if (annotation.isIntegerValue()){
				parent.addAnnotation(property, annotation.getIntegerValue());				
			}
			else if (annotation.isStringValue()){
				parent.addAnnotation(property, annotation.getStringValue());				
			}
			else {
				throw new SBOLGraphException("Unknown annotation type: " + annotation.getClass().getName());
			}			
		}
	}


	private static URI getAnnotationProperty(Annotation annotation)
	{
		String namespaceURI = annotation.getQName().getNamespaceURI();
		String localString = annotation.getQName().getLocalPart();
		return SBOLAPI.append(namespaceURI, localString);
	}
	
	private static Object getAnnotationValue(Annotation annotation)
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

	public static void copyNamespacesFrom2_to_3(org.sbolstandard.core2.SBOLDocument inputSbol2Doc, SBOLDocument outputSbol3Doc) {
		Model model = outputSbol3Doc.getRDFModel();
		outputSbol3Doc.addNameSpacePrefixes(ConverterNameSpace.BackPort_2_3.getPrefix(), ConverterNameSpace.BackPort_2_3.getUri());

		for (int i = 0; i < inputSbol2Doc.getNamespaces().size(); i++) {
			String uri = inputSbol2Doc.getNamespaces().get(i).getNamespaceURI();
			String prefix = inputSbol2Doc.getNamespaces().get(i).getPrefix();

			if (model.getNsPrefixURI(prefix) == null && model.getNsPrefixURI(prefix.toLowerCase()) == null &&
					model.getNsPrefixURI(prefix) == null &&
					model.getNsURIPrefix(uri) == null &&
					model.getNsURIPrefix(uri.toLowerCase()) == null) {
				// if (!uri.toLowerCase().equals("http://sbols.org/v2#")) {
				outputSbol3Doc.addNameSpacePrefixes(prefix, URI.create(uri));
			}
		}
	}	
	public static void copyNamespacesFrom3_to_2(SBOLDocument inputSbol3Doc, org.sbolstandard.core2.SBOLDocument outputSbol2Doc) throws SBOLValidationException 
	{
		Model model=inputSbol3Doc.getRDFModel();
		for (Entry<String, String> ns:model.getNsPrefixMap().entrySet())
		{
			String prefixSBOL3=ns.getKey();
			String namespaceSBOL3=ns.getValue();
			if (namespaceSBOL3.equals(URINameSpace.CHEBI.getUri().toString()) ||
				namespaceSBOL3.equals(URINameSpace.EDAM.getUri().toString()) ||
				namespaceSBOL3.equals(URINameSpace.GO.getUri().toString()) ||
				namespaceSBOL3.equals(URINameSpace.OM.getUri().toString()) ||
				namespaceSBOL3.equals(URINameSpace.PROV.getUri().toString()) ||
				namespaceSBOL3.equals(URINameSpace.RDFS.getUri().toString()) ||
				namespaceSBOL3.equals(URINameSpace.SBO.getUri().toString()) ||
				namespaceSBOL3.equals(URINameSpace.SO.getUri().toString()) ||
				namespaceSBOL3.equals(URINameSpace.SBOL.getUri().toString())){					
				continue;	
			}
			else
			{	
				boolean found=false;
				for (int i = 0; i < outputSbol2Doc.getNamespaces().size(); i++) {
					String namespaceSBOL2 = outputSbol2Doc.getNamespaces().get(i).getNamespaceURI();
					String prefixSBOL2 = outputSbol2Doc.getNamespaces().get(i).getPrefix();
					
				    if (prefixSBOL2.toLowerCase().equals(prefixSBOL3)){
				    	found=true;
				    	break;
				    }
				    else if (namespaceSBOL2.toLowerCase().equals(namespaceSBOL3)){
				    	found=true;
				    	break;
				    }			    					
				}
				if (!found){
					//Only add if the annotation was about v2 to v3 when converting to v2
					//No need to include to v2->v3 namespace in a v2 document
					if (!namespaceSBOL3.equalsIgnoreCase(ConverterNameSpace.BackPort_2_3.getUri().toString())){						
						outputSbol2Doc.addNamespace(URI.create(namespaceSBOL3), prefixSBOL3);	
					}
				}
			}
		}
	}	 	

	public static URI convertSOUri_2_to_3(URI soUri)
	{
		String uriString = soUri.toString().replaceAll("(?i)so/", "").replace("(?i)so:", "SO:");
		uriString= uriString.replaceAll("(?i)http://", "https://");
		return URI.create(uriString);
	}
	
	private static URI convertSOUri_3_to_2(URI soUri)
	{
		String soUriString = soUri.toString().replaceAll("(?i)so:", "so/SO:");
		soUriString= soUriString.replaceAll("(?i)https://", "http://");		
		return URI.create(soUriString);
	}
	
	public static List<URI> convertSORoles2_to_3(Set<URI> roles) {
		List<URI> convertedRoles = new ArrayList<URI>();
		for (URI role : roles) {
			convertedRoles.add(convertSOUri_2_to_3(role));
		}
		return convertedRoles;
	}

	public static Set<URI> convertSORoles3_to_2(List<URI> roles) {
		Set<URI> convertedRoles = null;
		if (roles != null && !roles.isEmpty()) {	
			convertedRoles = new HashSet<URI>();		
			for (URI role : roles) {
				convertedRoles.add(convertSOUri_3_to_2(role));
			}
		}
		return convertedRoles;
	}

	public static ConstraintRestriction toSBOL3RestrictionType(URI sbol2RestrictionType) throws SBOLGraphException {
		if (sbol2RestrictionType.equals(getSBOL2RestrictionURI(RestrictionType.DIFFERENT_FROM.toString()))) {
			return IdentityRestriction.differentFrom;
		} else if (sbol2RestrictionType
				.equals(getSBOL2RestrictionURI(RestrictionType.OPPOSITE_ORIENTATION_AS.toString()))) {
			return OrientationRestriction.oppositeOrientationAs;
		} else if (sbol2RestrictionType
				.equals(getSBOL2RestrictionURI(RestrictionType.SAME_ORIENTATION_AS.toString()))) {
			return OrientationRestriction.sameOrientationAs;
		} else if (sbol2RestrictionType.equals(getSBOL2RestrictionURI(RestrictionType.PRECEDES.toString()))) {
			return SequentialRestriction.precedes;
		} else {
			throw new SBOLGraphException("Unknown SBOL2 RestrictionType: " + sbol2RestrictionType);
		}
	}

	private static URI getSBOL2RestrictionURI(String RestrictionType) {
		return URI.create("http://sbols.org/v2#" + RestrictionType);
	}

	public static RestrictionType toSBOL2RestrictionType(URI sbol3RestrictionType) {

		if (sbol3RestrictionType.equals(IdentityRestriction.differentFrom.getUri())) {
			return RestrictionType.DIFFERENT_FROM;
		} else if (sbol3RestrictionType.equals(OrientationRestriction.oppositeOrientationAs.getUri())) {
			return RestrictionType.OPPOSITE_ORIENTATION_AS;
		} else if (sbol3RestrictionType.equals(OrientationRestriction.sameOrientationAs.getUri())) {
			return RestrictionType.SAME_ORIENTATION_AS;
		} else if (sbol3RestrictionType.equals(SequentialRestriction.precedes.getUri())) {
			return RestrictionType.PRECEDES;
		}
		return null;
	}

	public static List<URI> toSBOL3ComponentDefinitionTypes(List<URI> sbol2Types) throws SBOLGraphException {
		List<URI> sbol3Types = new ArrayList<URI>();
		for (URI sbol2Type : sbol2Types) {
			sbol3Types.add(toSBOL3ComponentType(sbol2Type));
		}
		return sbol3Types;
	}

	public static URI toSBOL3ComponentType(URI sbol2Type) throws SBOLGraphException {

		if (sbol2Type.equals(ComponentDefinition.DNA_REGION)) {
			return ComponentType.DNA.getUri();
		} else if (sbol2Type.equals(ComponentDefinition.DNA)) {
			return ComponentType.DNA.getUri();
		} else if (sbol2Type.equals(ComponentDefinition.DNA_MOLECULE)) {
			return ComponentType.DNA.getUri();
		} else if (sbol2Type.equals(ComponentDefinition.RNA_REGION)) {
			return ComponentType.RNA.getUri();
		} else if (sbol2Type.equals(ComponentDefinition.RNA)) {
			return ComponentType.RNA.getUri();
		} else if (sbol2Type.equals(ComponentDefinition.RNA_MOLECULE)) {
			return ComponentType.RNA.getUri();
		} else if (sbol2Type.equals(ComponentDefinition.PROTEIN)) {
			return ComponentType.Protein.getUri();
		} else if (sbol2Type.equals(ComponentDefinition.SMALL_MOLECULE)) {
			return ComponentType.SimpleChemical.getUri();
		} else if (sbol2Type.equals(ComponentDefinition.EFFECTOR)) {
			return ComponentType.SimpleChemical.getUri();
		} else if (sbol2Type.equals(ComponentDefinition.COMPLEX)) {
			return ComponentType.NoncovalentComplex.getUri();
		} else {
			if (sbol2Type.toString().toLowerCase().contains("so/so:")) {
				return convertSOUri_2_to_3(sbol2Type);
			}
			else
			{
				return sbol2Type; // return the original URI if it does not match any known type
			}
		}
	}

	public static List<URI> toSBOL2ComponentDefinitionTypes(List<URI> sbol3Types) throws SBOLGraphException {
		List<URI> sbol2Types = new ArrayList<URI>();
		for (URI type : sbol3Types) {
			sbol2Types.add(toSBOL2ComponentDefinitionType(type));
		}
		return sbol2Types;
	}

	public static URI toSBOL2ComponentDefinitionType(URI sbol3Type) throws SBOLGraphException {

		if (sbol3Type.equals(ComponentType.DNA.getUri())) {
			return ComponentDefinition.DNA_REGION;
		} else if (sbol3Type.equals(ComponentType.RNA.getUri())) {
			return ComponentDefinition.RNA_REGION;
		} else if (sbol3Type.equals(ComponentType.Protein.getUri())) {
			return ComponentDefinition.PROTEIN;
		} else if (sbol3Type.equals(ComponentType.SimpleChemical.getUri())) {
			return ComponentDefinition.SMALL_MOLECULE;
		} else if (sbol3Type.equals(ComponentType.NoncovalentComplex.getUri())) {
			return ComponentDefinition.COMPLEX;
		} else if (sbol3Type.equals(ComponentType.FunctionalEntity.getUri())) {
			return ComponentDefinition.COMPLEX;
		} 
		else {
			if (sbol3Type.toString().toLowerCase().contains("so:")) {
				return convertSOUri_3_to_2(sbol3Type);
			}
			else
			{
				return sbol3Type; // return the original URI if it does not match any known type
			}
		}
	}

	public static org.sbolstandard.core3.vocabulary.Orientation toSBOL3Orientation(org.sbolstandard.core2.OrientationType sbol2Orientation) throws SBOLGraphException {
		if (sbol2Orientation!=null)
		{
			if (sbol2Orientation == org.sbolstandard.core2.OrientationType.INLINE) {
				return org.sbolstandard.core3.vocabulary.Orientation.inline;
			} 
			else if (sbol2Orientation == org.sbolstandard.core2.OrientationType.REVERSECOMPLEMENT) {
				return org.sbolstandard.core3.vocabulary.Orientation.reverseComplement;
			} 
			else {
				throw new SBOLGraphException("Unknown SBOL2 OrientationType: " + sbol2Orientation);
			}
		}
		else
		{
			return null;
		}
	}

	public static org.sbolstandard.core2.OrientationType toSBOL2OrientationType( org.sbolstandard.core3.vocabulary.Orientation sbol3Orientation) throws SBOLGraphException {
		if (sbol3Orientation!=null){
			if (sbol3Orientation == org.sbolstandard.core3.vocabulary.Orientation.inline) {
				return org.sbolstandard.core2.OrientationType.INLINE;
			} 
			else if (sbol3Orientation == org.sbolstandard.core3.vocabulary.Orientation.reverseComplement) {
				return org.sbolstandard.core2.OrientationType.REVERSECOMPLEMENT;
			} 
			else {
				throw new SBOLGraphException("Unknown SBOL3 OrientationType: " + sbol3Orientation);
			}
		}
		else
		{
			return null;
		}
	}

	public static URI getSBOL2SequenceType(ComponentDefinition sbol2CompDef) throws SBOLGraphException {
		if (sbol2CompDef.getTypes().contains(ComponentDefinition.DNA_REGION)) {
			return org.sbolstandard.core2.Sequence.IUPAC_DNA;
		} else if (sbol2CompDef.getTypes().contains(ComponentDefinition.RNA_REGION)) {
			return org.sbolstandard.core2.Sequence.IUPAC_RNA;
		} else if (sbol2CompDef.getTypes().contains(ComponentDefinition.PROTEIN)) {
			return org.sbolstandard.core2.Sequence.IUPAC_PROTEIN;
		} else if (sbol2CompDef.getTypes().contains(ComponentDefinition.SMALL_MOLECULE)) {
			return org.sbolstandard.core2.Sequence.SMILES;
		} else {
			throw new SBOLGraphException(
					"Unknown SBOL2 Sequence type for ComponentDefinition: " + sbol2CompDef.getIdentity());
		}
	}

	public static Sequence getSBOL3SequenceFromSBOl2Parent(SBOLDocument document,
			ComponentDefinition sbol2ParentCompDef, Parameters parameters) throws SBOLGraphException {
		
		URI seqType = Util.getSBOL2SequenceType(sbol2ParentCompDef);
		org.sbolstandard.core2.Sequence sbol2seq = sbol2ParentCompDef.getSequenceByEncoding(seqType);
		Sequence sbol3seq=null;
		if (sbol2seq!=null)
		{
			URI SBOL3ObjectUri = Util.createSBOL3Uri(sbol2seq.getIdentity(), parameters);
			sbol3seq = document.getIdentified(SBOL3ObjectUri, Sequence.class);
		}

		return sbol3seq;
	}

	
	public static List<URI> convertToSBOL3_SBO_URIs(Set<URI> types) {
		List<URI> convertedTypes = new ArrayList<URI>();
		for (URI type : types) {
			String typeString = type.toString().replaceAll("(?i)/biomodels.sbo", "").replace("sbo:", "SBO:");			
			typeString = typeString.replaceAll("(?i)http://", "https://");
			convertedTypes.add(URI.create(typeString));
		}
		return convertedTypes;
	}

	private static URI convertSBOUri_3_to_2(URI uri)
	{
		String uriString = uri.toString().replaceAll("(?i)sbo:", "biomodels.sbo/SBO:");
		uriString= uriString.replaceAll("(?i)https://", "http://");
		return URI.create(uriString);
	}

	public static Set<URI> convertToSBOL2_SBO_URIs(List<URI> uris) throws SBOLGraphException {
		Set<URI> convertedUris = new HashSet<URI>();
		for (URI uri : uris) {
			convertedUris.add(convertSBOUri_3_to_2(uri));
			/*if(type.equals(org.sbolstandard.core3.vocabulary.InteractionType.Inhibition.getUri())) {
				convertedTypes.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000169"));
			} else if(type.equals(org.sbolstandard.core3.vocabulary.InteractionType.Stimulation.getUri())) {
				convertedTypes.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000170"));
			} else if(type.equals(org.sbolstandard.core3.vocabulary.InteractionType.BiochemicalReaction.getUri())) {
				convertedTypes.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000176"));
			} else if(type.equals(org.sbolstandard.core3.vocabulary.InteractionType.NonCovalentBinding.getUri())) {
				convertedTypes.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000177"));
			} else if(type.equals(org.sbolstandard.core3.vocabulary.InteractionType.Degradation.getUri())) {
				convertedTypes.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000179"));
			} else if(type.equals(org.sbolstandard.core3.vocabulary.InteractionType.GeneticProduction.getUri())) {
				convertedTypes.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000589"));
			} else if(type.equals(org.sbolstandard.core3.vocabulary.InteractionType.Control.getUri())) {
				convertedTypes.add(URI.create("http://identifiers.org/biomodels.sbo/SBO:0000168"));
			} else {
				throw new SBOLGraphException("Unknown SBOL3 Interaction type: " + type);				
			}
			*/
		}
		return convertedUris;
	}
	
	public static URI getSBOL3SequenceEncodingType(URI sbol2EncodingType) throws SBOLGraphException {
		if (sbol2EncodingType.equals(org.sbolstandard.core2.Sequence.IUPAC_DNA)) {
			return Encoding.NucleicAcid.getUri();
		} else if (sbol2EncodingType.equals(org.sbolstandard.core2.Sequence.IUPAC_RNA)) {
			return Encoding.NucleicAcid.getUri();
		} else if (sbol2EncodingType.equals(org.sbolstandard.core2.Sequence.IUPAC_PROTEIN)) {
			return Encoding.AminoAcid.getUri();
		} else if (sbol2EncodingType.equals(org.sbolstandard.core2.Sequence.SMILES)) {
			return Encoding.SMILES.getUri();
		} else {
			throw new SBOLGraphException("Unknown SBOL2 Sequence encoding type: " + sbol2EncodingType);
		}		
	}
	
	public static URI getSBOL2SequenceEncodingType(URI sbol3EncodingType) throws SBOLGraphException {
		if (sbol3EncodingType.equals(Encoding.NucleicAcid.getUri())) {
			return org.sbolstandard.core2.Sequence.IUPAC_DNA;
		} else if (sbol3EncodingType.equals(Encoding.AminoAcid.getUri())) {
			return org.sbolstandard.core2.Sequence.IUPAC_PROTEIN;
		} else if (sbol3EncodingType.equals(Encoding.SMILES.getUri())) {
			return org.sbolstandard.core2.Sequence.SMILES;
		} else {
			throw new SBOLGraphException("Unknown SBOL3 Sequence encoding type: " + sbol3EncodingType);
		}
	}
	public static URI getSBOL3ModelLanguage(URI sbol2Language) throws SBOLGraphException {
		if (sbol2Language.equals(EDAMOntology.SBML)) {
			return org.sbolstandard.core3.vocabulary.ModelLanguage.SBML;}
		else if (sbol2Language.equals(EDAMOntology.CELLML)) {
			return org.sbolstandard.core3.vocabulary.ModelLanguage.CellML;
		} else if (sbol2Language.equals(EDAMOntology.BIOPAX)) {
			return org.sbolstandard.core3.vocabulary.ModelLanguage.BioPAX;
		} else {
			throw new SBOLGraphException("Unknown SBOL2 Model language: " + sbol2Language);
		}
	}
	public static URI getSBOL2ModelLanguage(URI sbol3Language) throws SBOLGraphException {
		if (sbol3Language.equals(org.sbolstandard.core3.vocabulary.ModelLanguage.SBML)) {
			return EDAMOntology.SBML;
		} else if (sbol3Language.equals(org.sbolstandard.core3.vocabulary.ModelLanguage.CellML)) {
			return EDAMOntology.CELLML;
		} else if (sbol3Language.equals(org.sbolstandard.core3.vocabulary.ModelLanguage.BioPAX)) {
			return EDAMOntology.BIOPAX;
		} else {
			throw new SBOLGraphException("Unknown SBOL3 Model language: " + sbol3Language);
		}
	}
	public static URI getSBOL2ModelFramework(URI sbol3Framework) throws SBOLGraphException {
		if (sbol3Framework.equals(org.sbolstandard.core3.vocabulary.ModelFramework.Continuous)) {
			return SystemsBiologyOntology.CONTINUOUS_FRAMEWORK;
		} else if (sbol3Framework.equals(org.sbolstandard.core3.vocabulary.ModelFramework.Discrete)) {
			return SystemsBiologyOntology.DISCRETE_FRAMEWORK;
		} else {
			throw new SBOLGraphException("Unknown SBOL3 Model framework: " + sbol3Framework);
		}
	}
	public static URI getSBOL3ModelFramework(URI sbol2Framework) throws SBOLGraphException {
		if (sbol2Framework.equals(SystemsBiologyOntology.CONTINUOUS_FRAMEWORK)) {
			return org.sbolstandard.core3.vocabulary.ModelFramework.Continuous;
		} else if (sbol2Framework.equals(SystemsBiologyOntology.DISCRETE_FRAMEWORK)) {
			return org.sbolstandard.core3.vocabulary.ModelFramework.Discrete;
		} else {
			throw new SBOLGraphException("Unknown SBOL2 Model framework: " + sbol2Framework);
		}
	}
	public static boolean isModuleDefinition(Component component) throws SBOLGraphException {
		if(component!=null && component.getInteractions() != null && !component.getInteractions().isEmpty()) {
			return true;			
		} else if (component!=null && component.getTypes().contains(ComponentType.FunctionalEntity.getUri())) {
			return true;
		}
		return false;
	}
	

	public static boolean isSBOL2DocumentCompliant(String fileName) {
		ByteArrayOutputStream baosOutput = new ByteArrayOutputStream();
		PrintStream outputStream = new PrintStream(baosOutput);

		ByteArrayOutputStream baosError = new ByteArrayOutputStream();
		PrintStream errorStream = new PrintStream(baosError);
		
		
		String URIPrefix = "";
		Boolean complete = false;
		Boolean compliant = true;
		Boolean bestPractice = true;
		Boolean typesInURI = false;
		String version = null;
		Boolean keepGoing = false;
		String compareFile = "";
		String compareFileName = null;
		String mainFileName = null;
		String topLevelURIStr = "";
		boolean genBankOut = false;
		boolean sbolV1out = false;
		boolean fastaOut = false;
		boolean snapGeneOut = false;
		boolean gff3Out = false;
		boolean csvOut = false;
		String outputFile = "output/OUTPUT.xml";
		boolean showDetail = false;
		boolean noOutput = false;
		boolean changeURIPrefix = false;
		boolean enumerate = false;
		
		System.out.println("VALIDATING SBOLDocument...");

		SBOLValidate.validate(outputStream, errorStream,
				fileName, URIPrefix, null, complete, compliant, bestPractice,
				typesInURI, version, keepGoing, compareFile, compareFileName, mainFileName, topLevelURIStr, genBankOut,
				sbolV1out, fastaOut, snapGeneOut, gff3Out, csvOut, outputFile, showDetail, noOutput, changeURIPrefix,
				enumerate);

		String errorStr = baosError.toString();
		if(errorStr.isEmpty()) {
			return true;
		}
		return false;
		
	}

	// Helper method to find an Identified object by its displayId in a list
	public static <T extends Identified> T getByDisplayId(List<T> list, String displayId) throws SBOLGraphException {
		for (T item : list) {
			if (item.getDisplayId().equals(displayId)) {
				return item;
			}
		}
		return null; // Return null if no item with the given displayId is found
	}
	
	// Helper method to find an Identified object by its displayId in a list
		public static <T extends Identified> T getByUri(List<T> list, URI uri) throws SBOLGraphException {
			for (T item : list) {
				if (item.getUri().equals(uri)) {
					return item;
				}
			}
			return null; // Return null if no item with the given displayId is found
		}
		
		public static <T extends Identified> T getSBOL3Entity(List<T> items, org.sbolstandard.core2.Identified sbol2Entity, Parameters parameters) throws SBOLGraphException
		{
			URI uri= parameters.getMapping(sbol2Entity.getIdentity());						
			if (items != null) {
				for (T item : items) {
					if (item.getUri().equals(sbol2Entity.getIdentity())) {
						return item;
					}
					else if (item.getDisplayId().equals(sbol2Entity.getDisplayId())) {
						return item;
					}
					else
					{
						if (uri!=null && item.getUri().equals(uri)) {
							return item;
						}
						/*List<Object> annoItems=item.getAnnotion(URI.create("http//sbolstandard.org/sbol-converter/23_to_31"));
						if (annoItems != null && !annoItems.isEmpty() && annoItems.get(0) instanceof URI) {
							URI uri = (URI) annoItems.get(0);
							if (uri.equals(sbol2Entity.getIdentity())) {
								return item;
							}
						}*/
					}
				}
			}
			return null;
		}

		public static boolean isMapstoConstraint(Constraint constraint) throws SBOLGraphException {
			// Check if the SBOL3 constraint is a SBOL2 mapsto type
			if(constraint.getRestriction().equals(org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.differentFrom.getUri())){
				return true;
			} 
			else if(constraint.getRestriction().equals(org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.verifyIdentical.getUri())){
				return true;
			}
			else if(constraint.getRestriction().equals(org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction.replaces.getUri())){
				return true;
			}
			return false;
		}

		public static Sequence getEmptySequence(Component sbol3ParentComp, SBOLDocument document) throws SBOLGraphException
		{
			Sequence sbol3Sequence = null;
			URI emptySeqUri= URI.create(sbol3ParentComp.getUri().toString() + "_seq");				
			if (sbol3ParentComp.getSequences()!=null && !sbol3ParentComp.getSequences().isEmpty()) {
				sbol3Sequence = Util.getByUri(sbol3ParentComp.getSequences(), emptySeqUri);
				//sbol3ParentComp.getSequences().add(sbol3Sequence);
				//sbol3Sequence = sbol3ParentComp.getSequences().get(0);				
			}
			else{ 
				sbol3Sequence = Util.createEmptySequence(sbol3ParentComp, document, emptySeqUri);
			}
			return sbol3Sequence;
		}

		public static Sequence createEmptySequence(Component sbol3Component, SBOLDocument document, URI sequenceURI) throws SBOLGraphException {
			Sequence sbol3Sequence = document.createSequence(sequenceURI, sbol3Component.getNamespace());
			List<Encoding> encodings = getEncodingsFromComponentType(sbol3Component);
			if (encodings != null && !encodings.isEmpty()) {
				sbol3Sequence.setEncoding(encodings.get(0));
			}
			sbol3Sequence.setEncoding(Encoding.NucleicAcid);
			sbol3Component.setSequences(Arrays.asList(sbol3Sequence));
			sbol3Component.addAnnotation(ConverterVocabulary.Two_to_Three.sbol3TempSequenceURI, sbol3Sequence.getUri());
			return sbol3Sequence;
		}

		private static List<Encoding> getEncodingsFromComponentType(Component sbol3Component) {
			List<Encoding> encodings = null;
			if (sbol3Component.getTypes() != null) {
				for (URI type : sbol3Component.getTypes()) {
					ComponentType compType = ComponentType.get(type);
					if (compType != null) {
						List<Encoding> currentEncodings = ComponentType.checkComponentTypeMatch(compType);
						if (currentEncodings != null && !currentEncodings.isEmpty()) {
							if (encodings == null) {
								encodings = currentEncodings;
							} else {
								encodings.addAll(currentEncodings);
							}
						}
					}

				}
			}
			return encodings;
		}


		public static boolean isTempSequence(Component component, URI sequenceURI) throws SBOLGraphException {
		List<Object> backPorts=component.getAnnotation(ConverterVocabulary.Two_to_Three.sbol3TempSequenceURI);
		if (backPorts!=null && backPorts.size()>0){
			for (Object o: backPorts){
				URI tempSeqUri=null;
				if (o instanceof URI){
					tempSeqUri=(URI) o;				
				}
				else if (o instanceof Metadata){
					tempSeqUri=((Metadata) o).getUri();		
				}
				else if (o instanceof TopLevelMetadata){
					tempSeqUri=((TopLevelMetadata) o).getUri();			
				}

				if (tempSeqUri.equals(sequenceURI)) {
					return true;
				}
			}
		}
		return false;
	}


}