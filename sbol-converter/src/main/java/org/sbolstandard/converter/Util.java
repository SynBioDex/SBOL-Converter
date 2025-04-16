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
