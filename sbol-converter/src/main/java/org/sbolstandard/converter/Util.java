package org.sbolstandard.converter;

import java.net.URI;
import java.util.*;

public class Util {
	public static <T> List<T> toList(Set<T> set) {
		return new ArrayList<>(set);
	}

	public static <T> Set<T> toSet(List<T> list) {
		return new HashSet<>(list);
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
	    boolean isNumber = lastSegment.matches("\\d+");

	    // Remove the number and the display id
	    if (isNumber) {
	        int secondLastSlash = uri.lastIndexOf('/', lastSlash - 1);
	        if (secondLastSlash == -1) return uri;
	        return uri.substring(0, secondLastSlash + 1);
	    } else {
	        // Remove the display id
	        return uri.substring(0, lastSlash + 1);
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
