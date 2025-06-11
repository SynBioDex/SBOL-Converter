package org.sbolstandard.converter;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.EDAMOntology;
import org.sbolstandard.core2.RestrictionType;
import org.sbolstandard.core2.SBOLValidate;
import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core2.SystemsBiologyOntology;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.Sequence;
import org.sbolstandard.core3.entity.TopLevel;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.ComponentType;
import org.sbolstandard.core3.vocabulary.Encoding;
import org.sbolstandard.core3.vocabulary.RestrictionType.ConstraintRestriction;
import org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction;
import org.sbolstandard.core3.vocabulary.RestrictionType.OrientationRestriction;
import org.sbolstandard.core3.vocabulary.RestrictionType.SequentialRestriction;
import org.sbolstandard.core3.api.SBOLAPI;
import org.sbolstandard.core3.entity.Component;

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

	public static URI createSBOL3Uri(URI inputUri) {
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

		// In case of URI of type "dynamic_measurement.mdx"
		// TODO: better check
		if (sbol3Uri == null)
		{
			String inputUriString = inputUri.toString();
			if (!inputUriString.toLowerCase().startsWith("urn") && !inputUriString.contains("://"))
			{
				
				return SBOLAPI.append("https://sbolstandard.org/SBOL3-Converter", inputUriString);
			}
			else
			{
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

	public static void copyIdentified(org.sbolstandard.core2.Identified input, Identified output)
			throws SBOLGraphException {
		output.setName(input.getName());
		output.setDescription(input.getDescription());
		// TODO: Fix me: 
		output.setWasDerivedFrom(toList(input.getWasDerivedFroms()));
		// TODO: FIX ME
		// output.setWasGeneratedBy(toList(input.getWasGeneratedBys()));
		for (org.sbolstandard.core2.Annotation annotation : input.getAnnotations()) {
			// TODO: need to add other annotation types
			if (annotation.isStringValue()) {
				output.addAnnotion(
						URI.create(annotation.getQName().getNamespaceURI() + annotation.getQName().getLocalPart()),
						annotation.getStringValue());
			}
		}
	}

	public static void copyIdentified(Identified input, org.sbolstandard.core2.Identified output)
			throws SBOLGraphException, SBOLValidationException {
		output.setName(input.getName());
		output.setDescription(input.getDescription());
		 if (input.getWasDerivedFrom() != null) {
			output.setWasDerivedFroms(toSet(input.getWasDerivedFrom()));
		}
		// TODO: FIX ME
		// output.setWasGeneratedBy(toSet(input.getWasGeneratedBys()));
	}

	private static URI convertSOUri_2_to_3(URI soUri)
	{
		String uriString = soUri.toString().toLowerCase().replace("so/", "").replace("so:", "SO:");
		uriString= uriString.replace("http", "https");
		return URI.create(uriString);
	}
	
	private static URI convertSOUri_3_to_2(URI soUri)
	{
		String soUriString = soUri.toString().toLowerCase().replace("so:", "so/SO:");
		soUriString= soUriString.replace("https", "http");
		return URI.create(soUriString);
	}
	
	public static List<URI> convertRoles2_to_3(Set<URI> roles) {
		List<URI> convertedRoles = new ArrayList<URI>();
		for (URI role : roles) {
			convertedRoles.add(convertSOUri_2_to_3(role));
		}
		return convertedRoles;
	}

	public static Set<URI> convertRoles3_to_2(List<URI> roles) {
		Set<URI> convertedRoles = new HashSet<URI>();
		for (URI role : roles) {
			convertedRoles.add(convertSOUri_3_to_2(role));
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
		return URI.create(org.sbolstandard.examples.Sbol2Terms.sbol2.getNamespaceURI() + RestrictionType);
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

	public static org.sbolstandard.core3.vocabulary.Orientation toSBOL3Orientation(
			org.sbolstandard.core2.OrientationType sbol2Orientation) throws SBOLGraphException {
		if (sbol2Orientation == org.sbolstandard.core2.OrientationType.INLINE) {
			return org.sbolstandard.core3.vocabulary.Orientation.inline;
		} else if (sbol2Orientation == org.sbolstandard.core2.OrientationType.REVERSECOMPLEMENT) {
			return org.sbolstandard.core3.vocabulary.Orientation.reverseComplement;
		} else {
			throw new SBOLGraphException("Unknown SBOL3 Component type: " + sbol2Orientation);
		}
	}

	public static org.sbolstandard.core2.OrientationType toSBOL2OrientationType(
			org.sbolstandard.core3.vocabulary.Orientation sbol3Orientation) throws SBOLGraphException {
		if (sbol3Orientation == org.sbolstandard.core3.vocabulary.Orientation.inline) {
			return org.sbolstandard.core2.OrientationType.INLINE;
		} else if (sbol3Orientation == org.sbolstandard.core3.vocabulary.Orientation.reverseComplement) {
			return org.sbolstandard.core2.OrientationType.REVERSECOMPLEMENT;
		} else {
			throw new SBOLGraphException("Unknown SBOL2 OrientationType: " + sbol3Orientation);
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
			ComponentDefinition sbol2ParentCompDef) throws SBOLGraphException {
		
		URI seqType = Util.getSBOL2SequenceType(sbol2ParentCompDef);
		org.sbolstandard.core2.Sequence sbol2seq = sbol2ParentCompDef.getSequenceByEncoding(seqType);
		Sequence sbol3seq=null;
		if (sbol2seq!=null)
		{
			URI SBOL3ObjectUri = Util.createSBOL3Uri(sbol2seq.getIdentity());
			sbol3seq = document.getIdentified(SBOL3ObjectUri, Sequence.class);
		}

		return sbol3seq;
	}

	
	public static List<URI> convertToSBOL3_SBO_URIs(Set<URI> types) {
		// TODO Auto-generated method stub
		List<URI> convertedTypes = new ArrayList<URI>();
		for (URI type : types) {
			String typeString = type.toString().toLowerCase().replace("/biomodels.sbo", "").replace("sbo:", "SBO:");
			
			// WARNING! BELOW IS SHOULD NOT BE NECESSARY ACCORDING TO SBOL3 SPECIFICATION
			typeString = typeString.replace("http", "https");
			convertedTypes.add(URI.create(typeString));
		}
		return convertedTypes;
	}
	public static Set<URI> convertToSBOL2_SBO_URIs(List<URI> types) throws SBOLGraphException {
		Set<URI> convertedTypes = new HashSet<URI>();
		for (URI type : types) {
			System.out.println(type.toString());
			if(type.equals(org.sbolstandard.core3.vocabulary.InteractionType.Inhibition.getUri())) {
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
		}
		return convertedTypes;
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
	public static boolean isModelDefinition(Component component) throws SBOLGraphException {
		if(component.getInteractions() != null && !component.getInteractions().isEmpty()) {
			return true;
		} else if (component.getTypes().contains(org.sbolstandard.core3.vocabulary.ComponentType.FunctionalEntity)) {
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

		//String outputStr= baosOutput.toString();
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

}
