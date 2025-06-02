package org.sbolstandard.converter;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sbolstandard.core2.ComponentDefinition;
import org.sbolstandard.core2.RestrictionType;
import org.sbolstandard.core2.SBOLValidationException;

import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.SBOLDocument;
import org.sbolstandard.core3.entity.TopLevel;
import org.sbolstandard.core3.util.SBOLGraphException;
import org.sbolstandard.core3.vocabulary.ComponentType;
import org.sbolstandard.core3.vocabulary.RestrictionType.ConstraintRestriction;
import org.sbolstandard.core3.vocabulary.RestrictionType.IdentityRestriction;
import org.sbolstandard.core3.vocabulary.RestrictionType.OrientationRestriction;
import org.sbolstandard.core3.vocabulary.RestrictionType.SequentialRestriction;
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
		if (version.equals("")) return true;
		Matcher m = versionPatternPat.matcher(version);
		return m.matches();
	}

	public static String extractDisplayIdSBOL3Uri(String uri) {
	    int lastSlash = uri.lastIndexOf('/');
	    if (lastSlash == -1) return "";
	    String displayId = uri.substring(lastSlash + 1);
	    //if (isDisplayIdValid(version)) 
	    return displayId;
	    // return "";
	}

	public static String extractVersionSBOL3Uri(String uri) {
	    int lastSlash = uri.lastIndexOf('/');
	    if (lastSlash == -1) return "";
	    String firstSegment = uri.substring(0,lastSlash);
	    //String lastSegment = uri.substring(lastSlash + 1);
	    int secondLastSlash = firstSegment.lastIndexOf('/');
	    if (secondLastSlash == -1) return "";
	    String version = uri.substring(secondLastSlash+1,lastSlash);
	    if (isVersionValid(version)) return version;
	    return "";
	}
	
	// TODO: should configure whether you want minimal or maximal
	public static String extractNameSpaceSBOL3Uri(String uri) {
		String displayId = extractDisplayIdSBOL3Uri(uri);
		String version = extractVersionSBOL3Uri(uri);
	    int lastSlash = uri.lastIndexOf('/');
	    if (lastSlash == -1) return "";
	    String namespace = uri.substring(0,lastSlash);
	    if (!version.equals("")) {
		    lastSlash = namespace.lastIndexOf('/');
		    if (lastSlash == -1) return "";
	    	namespace = namespace.substring(0,lastSlash);
	    }
	    return namespace;
	}
		
	// TODO: should configure whether you want minimal or maximal
	public static URI extractNameSpaceSBOL3Uri(URI uri) {
		try {
			java.net.URL parsedUrl = new java.net.URL(uri.toString());
			String protocol = parsedUrl.getProtocol();
			String namespace = uri.toString().replaceFirst(protocol + "://", "");

			int slashIndex = namespace.lastIndexOf('/');

			if (slashIndex == -1) {
				//TODO:
				return uri;
			}

			return URI.create(protocol + "://" + namespace.substring(0, slashIndex));

		} catch (Exception e) {
			// TODO:
			return null;
		}
	}
		
	// TODO: what if the collection is a collection + version
	public static String extractCollectionSBOL3Uri(String uri, String namespace, String displayId) {
		String collection;
		collection = uri.replace(namespace+"/","");
	    int lastSlash = uri.lastIndexOf('/');
	    if (lastSlash == -1) return "";
	    String lastSegment = uri.substring(lastSlash + 1);
	    if (!lastSegment.equals(displayId)) return "";
	    lastSlash = collection.lastIndexOf('/');
	    if (lastSlash == -1) return "";
	    collection = collection.substring(0, lastSlash);
	    if (isVersionValid(collection)) return "";
	    if (!collection.equals("")) collection = "/" + collection;
	    return collection;
	}
	
	public static String extractVersionSBOL3Uri(String uri, String namespace, String displayId) {
		String collection;
		collection = uri.replace(namespace+"/","");
	    int lastSlash = uri.lastIndexOf('/');
	    if (lastSlash == -1) return "";
	    String lastSegment = uri.substring(lastSlash + 1);
	    if (!lastSegment.equals(displayId)) return "";
	    lastSlash = collection.lastIndexOf('/');
	    if (lastSlash == -1) return "";
	    collection = collection.substring(0, lastSlash);
	    if (isVersionValid(collection)) return collection;
	    return "";
	}
	
	public static String extractVersionSBOL2Uri(String uri) {
		if (uri == null || uri.isEmpty()) {
			return null;
		}
		else if (uri.endsWith("/")) {
	        return null;
	    }

	    int lastSlash = uri.lastIndexOf('/');
	    if (lastSlash == -1) return null;

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
		}
		else if (uri.endsWith("/")) {
	        return null;
	    }

	    int lastSlash = uri.lastIndexOf('/');
	    if (lastSlash == -1) return null;

	    String lastSegment = uri.substring(lastSlash + 1);
	    boolean isNumber = isVersionValid(lastSegment);

	    if (isNumber) {
	        int secondLastSlash = uri.lastIndexOf('/', lastSlash - 1);
	        if (secondLastSlash == -1) return uri;
	        return uri.substring(secondLastSlash+1,lastSlash);
	    } else {
	        return uri.substring(lastSlash+1);
	    }
		
	}
	
	public static String extractURIPrefixSBOL2Uri(String uri) {
		if (uri == null || uri.isEmpty()) {
			return null;
		}
		else if (uri.endsWith("/")) {
	        return null;
	    }

	    int lastSlash = uri.lastIndexOf('/');
	    if (lastSlash == -1) return null;

	    String lastSegment = uri.substring(lastSlash + 1);
	    boolean isNumber = isVersionValid(lastSegment);

	    // Remove the number and the display id
	    if (isNumber) {
	        int secondLastSlash = uri.lastIndexOf('/', lastSlash - 1);
	        if (secondLastSlash == -1) return uri;
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
		
		//In case of URI of type "dynamic_measurement.mdx"
		//TODO: better check
		if (sbol3Uri == null)
			return URI.create(inputUri.toString());
			
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
		
		sbol2Uri = extractNameSpaceSBOL3Uri(inputUri.toString()) + "/" //+ extractVersionCollectionSBOL3Uri(inputUri)
			+ extractDisplayIdSBOL3Uri(inputUri.toString());
		
		if (!version.equals("")) {
			sbol2Uri += "/" + version;
		}
		
		//In case of URI of type "dynamic_measurement.mdx"
		//TODO: better check
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
		return input.getNamespace().toString()
				+Util.extractCollectionSBOL3Uri(input.getUri().toString(), input.getNamespace().toString(), input.getDisplayId());
	}
	
	public static String getVersion(TopLevel input) throws SBOLGraphException {
		return extractVersionSBOL3Uri(input.getUri().toString(), input.getNamespace().toString(), input.getDisplayId());
	}

	public static URI getNamespace(org.sbolstandard.core2.Identified input) {
		return URI.create(Util.extractURIPrefixSBOL2Uri(input.getIdentity().toString()));
	}
	
	public static void copyIdentified(org.sbolstandard.core2.Identified input, Identified output) throws SBOLGraphException {
		output.setName(input.getName());
		output.setDescription(input.getDescription());
		output.setWasDerivedFrom(toList(input.getWasDerivedFroms()));
		// TODO: FIX ME
		// output.setWasGeneratedBy(toList(input.getWasGeneratedBys()));
		for (org.sbolstandard.core2.Annotation annotation : input.getAnnotations()) {
			// TODO: need to add other annotation types
			if (annotation.isStringValue()) {
				output.addAnnotion(URI.create(annotation.getQName().getNamespaceURI()+annotation.getQName().getLocalPart()), 
						annotation.getStringValue());
			}
		}
	}

	public static void copyIdentified(Identified input, org.sbolstandard.core2.Identified output) throws SBOLGraphException, SBOLValidationException {
		output.setName(input.getName());
		output.setDescription(input.getDescription());
		if (input.getWasDerivedFrom()!=null) {
			output.setWasDerivedFroms(toSet(input.getWasDerivedFrom()));
		}
		// TODO: FIX ME
		// output.setWasGeneratedBy(toSet(input.getWasGeneratedBys()));
	}
	
	public static List<URI> convertRoles2_to_3(Set<URI> roles) {
		List<URI> convertedRoles = new ArrayList<URI>();
		for (URI role : roles) {
			String roleString = role.toString().replace("so/", "");
			roleString = roleString.replace("http", "https");
			convertedRoles.add(URI.create(roleString));
		}
		return convertedRoles;
	}
	
	public static Set<URI> convertRoles3_to_2(List<URI> roles) {
		Set<URI> convertedRoles = new HashSet<URI>();
		for (URI role : roles) {
			String roleString = role.toString().replace("SO:", "so/SO:");
			roleString = roleString.replace("https", "http");
			convertedRoles.add(URI.create(roleString));
		}
		return convertedRoles;
	}
	
	public static ConstraintRestriction toSBOL3RestrictionType(URI sbol2RestrictionType) throws SBOLGraphException {
		if (sbol2RestrictionType.equals(getSBOL2RestrictionURI(RestrictionType.DIFFERENT_FROM.toString()))){
			return IdentityRestriction.differentFrom;
		} 
		else if (sbol2RestrictionType.equals(getSBOL2RestrictionURI(RestrictionType.OPPOSITE_ORIENTATION_AS.toString()))) {
			return OrientationRestriction.oppositeOrientationAs;
		} 
		else if (sbol2RestrictionType.equals(getSBOL2RestrictionURI(RestrictionType.SAME_ORIENTATION_AS.toString()))) {
			return OrientationRestriction.sameOrientationAs;
		} 
		else if (sbol2RestrictionType.equals(getSBOL2RestrictionURI(RestrictionType.PRECEDES.toString()))) {
			return SequentialRestriction.precedes;
		}		
		else		
		{
			throw new SBOLGraphException("Unknown SBOL2 RestrictionType: " + sbol2RestrictionType);
		}		
	}
	
	private static URI getSBOL2RestrictionURI(String RestrictionType) {
        return URI.create(org.sbolstandard.examples.Sbol2Terms.sbol2.getNamespaceURI() + RestrictionType);
    }
	
	public static RestrictionType toSBOL2RestrictionType(URI sbol3RestrictionType) {
		
		if (sbol3RestrictionType.equals(IdentityRestriction.differentFrom.getUri())) {
			return RestrictionType.DIFFERENT_FROM;
		} 
		else if (sbol3RestrictionType.equals(OrientationRestriction.oppositeOrientationAs.getUri())) {
			return RestrictionType.OPPOSITE_ORIENTATION_AS;
		} 
		else if (sbol3RestrictionType.equals(OrientationRestriction.sameOrientationAs.getUri())) {
			return RestrictionType.SAME_ORIENTATION_AS;
		} 
		else if (sbol3RestrictionType.equals(SequentialRestriction.precedes.getUri())) {
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
				
		if (sbol2Type.equals(ComponentDefinition.DNA_REGION)){
			return ComponentType.DNA.getUri();
		}
		else if (sbol2Type.equals(ComponentDefinition.DNA)){
			return ComponentType.DNA.getUri();
		}
		else if (sbol2Type.equals(ComponentDefinition.DNA_MOLECULE)){
			return ComponentType.DNA.getUri();
		}
		else if (sbol2Type.equals(ComponentDefinition.RNA_REGION)){
			return ComponentType.RNA.getUri();
		}
		else if (sbol2Type.equals(ComponentDefinition.RNA)){
			return ComponentType.RNA.getUri();
		}
		else if (sbol2Type.equals(ComponentDefinition.RNA_MOLECULE)){
			return ComponentType.RNA.getUri();
		}
		else if (sbol2Type.equals(ComponentDefinition.PROTEIN)){
			return ComponentType.Protein.getUri();
		}
		else if (sbol2Type.equals(ComponentDefinition.SMALL_MOLECULE)){
			return ComponentType.SimpleChemical.getUri();
		}
		else if (sbol2Type.equals(ComponentDefinition.EFFECTOR)){
			return ComponentType.SimpleChemical.getUri();
		}
		else if (sbol2Type.equals(ComponentDefinition.COMPLEX)){
			return ComponentType.NoncovalentComplex.getUri();
		}
		else
		{
			throw new SBOLGraphException("Unknown SBOL2 ComponentDefinition type: " + sbol2Type);
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
		
		if (sbol3Type.equals(ComponentType.DNA.getUri())){
			return ComponentDefinition.DNA_REGION;
		}
		else if (sbol3Type.equals(ComponentType.RNA.getUri())){
			return ComponentDefinition.RNA_REGION;
		}
		else if (sbol3Type.equals(ComponentType.Protein.getUri())){
			return ComponentDefinition.PROTEIN;
		}
		else if (sbol3Type.equals(ComponentType.SimpleChemical.getUri())){
			return ComponentDefinition.SMALL_MOLECULE;
		}
		else if (sbol3Type.equals(ComponentType.NoncovalentComplex.getUri())){
			return ComponentDefinition.COMPLEX;
		}
		else if (sbol3Type.equals(ComponentType.FunctionalEntity.getUri())){
			return ComponentDefinition.COMPLEX;
		}		
		else
		{
			throw new SBOLGraphException("Unknown SBOL3 Component type: " + sbol3Type);
		}
	}
	
	public static org.sbolstandard.core3.vocabulary.Orientation toSBOL3Orientation(org.sbolstandard.core2.OrientationType sbol2Orientation) throws SBOLGraphException{
		if (sbol2Orientation == org.sbolstandard.core2.OrientationType.INLINE) {
			return org.sbolstandard.core3.vocabulary.Orientation.inline;
		} else if (sbol2Orientation == org.sbolstandard.core2.OrientationType.REVERSECOMPLEMENT) {
			return org.sbolstandard.core3.vocabulary.Orientation.reverseComplement;
		} else {
			throw new SBOLGraphException("Unknown SBOL3 Component type: " + sbol2Orientation);
		}
	}
	
	public static org.sbolstandard.core2.OrientationType toSBOL2OrientationType(org.sbolstandard.core3.vocabulary.Orientation sbol3Orientation) throws SBOLGraphException{
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
			throw new SBOLGraphException("Unknown SBOL2 Sequence type for ComponentDefinition: " + sbol2CompDef.getIdentity());
		}
	}
	
	//TO DO: 
//		public static URI getSBOL3SequenceType(Component sbol3Component) throws SBOLGraphException {
//			if (sbol3Component.getTypes().contains(ComponentType.DNA)) {
//				return org.sbolstandard.core3.vocabulary.Encoding.NucleicAcid.getUri();
//			} else if (sbol3Component.getTypes().contains(ComponentType.RNA)) {
//				return org.sbolstandard.core3.vocabulary.Encoding.;
//			} else if (sbol3Component.getTypes().contains(ComponentType.Protein)) {
//				return org.sbolstandard.core3.vocabulary.Sequence.IUPAC_PROTEIN;
//			} else if (sbol3Component.getTypes().contains(ComponentType.SimpleChemical)) {
//				return org.sbolstandard.core3.vocabulary.Sequence.SMILES;
//			} else {
//				throw new SBOLGraphException("Unknown SBOL3 Sequence type for Component: " + sbol3Component.getUri());
//			}
//		}
}
