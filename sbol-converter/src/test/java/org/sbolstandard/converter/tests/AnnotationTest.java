package org.sbolstandard.converter.tests;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;

//import javax.xml.namespace.QName;



import org.junit.jupiter.api.Test;
import org.sbolstandard.core2.Annotation;
import org.sbolstandard.core2.GenericTopLevel;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLWriter;

/**
 * Unit test for simple App.
 */
public class AnnotationTest {


	
	@Test
	public void TestSBOL2Attachment() throws Exception {
		String prURI="http://partsregistry.org/";	
		String annoNS="http://keele.ac.uk/";	
		
		String prPrefix="pr";	
		
		SBOLDocument document = new SBOLDocument();				
		document.setTypesInURIs(true);
		document.addNamespace(URI.create(prURI), prPrefix);
		document.setDefaultURIprefix(prURI);	
	
		GenericTopLevel gtl=document.createGenericTopLevel("Repository", new QName(annoNS, "SynBioRepository"));
		gtl.createAnnotation(new QName(annoNS, "property1"), "value1");
		gtl.createAnnotation(new QName(annoNS, "property2"), true);
		gtl.createAnnotation(new QName(annoNS, "property3"), 3);
		
		Annotation annotation1= new Annotation(new QName(annoNS, "property5"), "value5");		
		List<Annotation> annotations=List.of(annotation1);
		
		//gtl.createAnnotation(new QName(annoNS, "property4"), new QName(annoNS, "NestedType1"), "nestedid", annotations);
		
		SBOLWriter.write(document, System.out);
		
		
		String namespaceURI = "http://example.org/ns";
        String localPart = "elementName";

        // Load the QName class without importing it
        Class<?> qnameClass = Class.forName("javax.xml.namespace.QName");

        // Get the constructor that takes (String namespaceURI, String localPart)
        Constructor<?> constructor = qnameClass.getConstructor(String.class, String.class);

        // Create an instance
        Object qnameInstance = constructor.newInstance(namespaceURI, localPart);

        // Optional: print it or cast if needed
        System.out.println("QName via reflection: " + qnameInstance.toString());
        
	}

}
