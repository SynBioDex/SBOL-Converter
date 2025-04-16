package org.sbolstandard.converter;

import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.sbolstandard.core2.SBOLValidationException;
import org.sbolstandard.core3.entity.Identified;
import org.sbolstandard.core3.entity.TopLevel;
import org.sbolstandard.core3.util.SBOLGraphException;

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
	
	// TODO: what if the collection is a collection + version
	public static String extractCollection(String uri, String namespace, String displayId) {
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
	
	public static String extractVersion(String uri, String namespace, String displayId) {
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
	
	public static String extractURIPrefix(String uri)
	{
		
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
	
	public static URI getNameSpace(URI uri) {
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
	
	public static URI createSBOL3Uri(org.sbolstandard.core2.Identified input) {
		String sbol3Uri = "";
		
		sbol3Uri = Util.extractURIPrefix(input.getIdentity().toString());
		if (input.isSetVersion()) {
			sbol3Uri += "/" + input.getVersion();
		}
		if (input.isSetDisplayId()) {
			sbol3Uri += "/" + input.getDisplayId();
		}
			
		return URI.create(sbol3Uri);
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
				+Util.extractCollection(input.getUri().toString(), input.getNamespace().toString(), input.getDisplayId());
	}
	
	public static String getVersion(TopLevel input) throws SBOLGraphException {
		return extractVersion(input.getUri().toString(), input.getNamespace().toString(), input.getDisplayId());
	}

	public static URI getNamespace(org.sbolstandard.core2.Identified input) {
		return URI.create(Util.extractURIPrefix(input.getIdentity().toString()));
	}
	
	public static void copyIdentified(org.sbolstandard.core2.Identified input, Identified output) throws SBOLGraphException {
		output.setName(input.getName());
		output.setDescription(input.getDescription());
		output.setWasDerivedFrom(toList(input.getWasDerivedFroms()));
		// TODO: FIX ME
		// output.setWasGeneratedBy(toList(input.getWasGeneratedBys()));
	}

	public static void copyIdentified(Identified input, org.sbolstandard.core2.Identified output) throws SBOLGraphException, SBOLValidationException {
		output.setName(input.getName());
		output.setDescription(input.getDescription());
		// TODO: Fix, should NEVER be NULL
		if (input.getWasDerivedFrom()!=null) {
			output.setWasDerivedFroms(toSet(input.getWasDerivedFrom()));
		}
		// TODO: FIX ME
		// output.setWasGeneratedBy(toSet(input.getWasGeneratedBys()));
	}
	
	public static List<URI> convertRoles(Set<URI> roles) {
		List<URI> convertedRoles = new ArrayList<URI>();
		for (URI role : roles) {
			String roleString = role.toString().replace("so/", "");
			roleString = roleString.replace("http", "https");
			convertedRoles.add(URI.create(roleString));
		}
		return convertedRoles;
	}

}
